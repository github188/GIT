package com.efuture.javaPos.Payment.Bank;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ExpressionDeal;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.ReadTextFile;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.PrintTemplate.PrintTemplate;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class PaymentBankFunc
{
	public String paycode = null;
	protected String errmsg = "交易失败!";
	protected BankLogDef bld = null;
	protected ReadTextFile rtf = new ReadTextFile();

	protected PrintWriter printdoc = null;
	protected boolean onceprint = true;
	protected boolean errmsgshow = false;
	protected boolean replacebankname = true;
	protected boolean salebyself = false;
	protected boolean banktypebymenu = true;
	protected boolean saveprintagain = false;

	public Vector bankcfgvector = null;
	public String bankcfgname = null;
	public Runnable msgcallback = null;

	public void readBankClassConfig(String classname)
	{
		bankcfgvector = null;

		String file = null;
		if (classname != null && classname.trim().length() > 0)
			file = classname.trim();
		else
			file = this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1);
		bankcfgname = file;
		file = GlobalVar.ConfigPath + "\\" + file + ".ini";
		if (!PathFile.fileExist(file))
			return;

		bankcfgvector = CommonMethod.readFileByVector(file);

		// 设置属性
		String str;
		str = getBankClassConfig("ONCEPRINT");
		if (str != null && str.length() > 0)
			setOnceXYKPrintDoc(str.equalsIgnoreCase("Y"));

		str = getBankClassConfig("SAVEPRINTAGAIN");
		if (str != null && str.length() > 0)
			setSavePrintAgain(str.equalsIgnoreCase("Y"));

		str = getBankClassConfig("SHOWMSG");
		if (str != null && str.length() > 0)
			setErrorMsgShowMode(str.equalsIgnoreCase("Y"));

		str = getBankClassConfig("REPLACEPAYNAME");
		if (str != null && str.length() > 0)
			setReplaceBankNameMode(str.equalsIgnoreCase("Y"));

		str = getBankClassConfig("SALEBYSELF");
		if (str != null && str.length() > 0)
			allowSaleBySelf(str.equalsIgnoreCase("Y"));

		str = getBankClassConfig("BANKMENU");
		if (str != null && str.length() > 0)
			setBankTypeByMenu(str.equalsIgnoreCase("Y"));
	}

	protected String getBankClassConfig(String attr)
	{
		for (int i = 0; bankcfgvector != null && i < bankcfgvector.size(); i++)
		{
			String[] s = (String[]) bankcfgvector.elementAt(i);
			if (attr.equalsIgnoreCase(s[0])) { return s[1]; }
		}
		return null;
	}

	public String getChangeType(String[] banktypelist, String type)
	{
		String strtype = null;

		if (banktypelist != null)
		{
			for (int i = 0; i < banktypelist.length; i++)
			{
				if (Convert.codeInString(banktypelist[i].trim(), '[').equals(type))
				{
					strtype = banktypelist[i];
					return strtype.substring(strtype.indexOf("]") + 1);
				}
			}
		}

		return "[" + type + "]未知交易类型";
	}

	public String[] getBankClassConfig(String mode, int type)
	{
		if (bankcfgvector == null)
			return null;

		Vector v = new Vector();
		for (int i = 0; i < bankcfgvector.size(); i++)
		{
			String[] s = (String[]) bankcfgvector.elementAt(i);
			if (mode.equalsIgnoreCase("getFuncItem") && s.length > 1 && s[0] != null && s[0].toUpperCase().startsWith("TYPE_"))
			{
				v.add("[" + s[0].substring("TYPE_".length()) + "]" + s[1]);
			}
			else if ((mode.equalsIgnoreCase("getFuncLabel") || mode.equalsIgnoreCase("getFuncText")) && s.length > 0 && s[0] != null && s[0].toUpperCase().startsWith("TYPE" + type + "_LB"))
			{
				if (v.size() <= 0)
					for (int j = 0; j < 5; j++)
						v.add(null);
				int idx = Convert.toInt(s[0].substring(("TYPE" + type + "_LB").length()));
				if (idx < 5 && s.length > 1)
				{
					String label = null;
					String text = null;
					int p = s[1].indexOf(",");
					if (p >= 0)
					{
						label = s[1].substring(0, p);
						text = s[1].substring(p + 1);
					}
					else
						label = s[1];
					if (mode.equalsIgnoreCase("getFuncLabel"))
						v.set(idx, label);
					if (mode.equalsIgnoreCase("getFuncText"))
						v.set(idx, text);
				}
			}
		}

		if (v.size() <= 0)
		{
			new MessageBox("银联接口对象配置文件不正确!");
			return null;
		}

		String[] s = new String[v.size()];
		for (int i = 0; i < s.length; i++)
			s[i] = (String) v.elementAt(i);
		return s;
	}

	public String[] getFuncItem()
	{
		String[] func = new String[7];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
		func[5] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		func[6] = "[" + PaymentBank.XYKCD + "]" + "签购单重打";

		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		// 0-4对应FORM中的5个输入框
		// null表示该不用输入
		switch (type)
		{
			case PaymentBank.XYKXF:// 消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = "请 刷 卡";
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKCX:// 消费撤销
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = "请 刷 卡";
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKTH:// 隔日退货
				grpLabelStr[0] = "原参考号";
				grpLabelStr[1] = "原授权号";
				grpLabelStr[2] = "原交易日";
				grpLabelStr[3] = "请 刷 卡";
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKQD:// 交易签到
				grpLabelStr[0] = null;// "calc|,02|测试输入,01";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";
				break;
			case PaymentBank.XYKJZ:// 交易结账
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易结账";
				break;
			case PaymentBank.XYKYE:// 余额查询
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = "请 刷 卡";
				grpLabelStr[4] = "余额查询";
				break;
			case PaymentBank.XYKCD:// 签购单重打
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打签单";
				break;
		}

		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		// 0-4对应FORM中的5个输入框
		// null表示必须用户输入,不为null表示缺省显示无需改变
		switch (type)
		{
			case PaymentBank.XYKXF:// 消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKCX:// 消费撤销
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKTH:// 隔日退货
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKQD:// 交易签到
				grpTextStr[0] = null;// "allowempty";
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易签到";
				break;
			case PaymentBank.XYKJZ:// 交易结账
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易结账";
				break;
			case PaymentBank.XYKYE:// 余额查询
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始余额查询";
				break;
			case PaymentBank.XYKCD:// 签购单重打
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始签购单重打";
				break;
		}

		return true;
	}

	public String getFuncItemDesc(String type, boolean onlydesc)
	{
		String[] s = getBankClassConfig("getFuncItem", 0);
		if (s == null)
			s = getFuncItem();

		for (int i = 0; s != null && i < s.length; i++)
		{
			if (Convert.codeInString(s[i].trim(), '[').equals(type))
			{
				String strtype = s[i].trim();
				if (onlydesc)
					return strtype.substring(strtype.indexOf("]") + 1);
				else
					return strtype;
			}
		}

		return "[" + type + "]未知交易类型";
	}

	public boolean checkDate(Text date)
	{
		String date1 = null;
		String time = null;  
		String reqcheckdatetime = getBankClassConfig("REQCHECKDATETIME");

		if (reqcheckdatetime == null || reqcheckdatetime.equals(""))
			reqcheckdatetime = "YYYYMMDD";

		if (reqcheckdatetime.equals("N"))
			return true;

		if (date.getText().length() == 4 && reqcheckdatetime.equals("YYYYMMDD"))
		{
			date1 = ManipulateDateTime.getCurrentDate();
			String date2[] = date1.split("/");

			int year = Convert.toInt(date2[0]);
			int month = Convert.toInt(date2[1]);

			String nowdate = date.getText();

			int month1 = Convert.toInt(nowdate.substring(0, 2));
			int day1=Convert.toInt(nowdate.substring(2, 4));
			if ((month1 == 0)||(day1==0))
			{
				new MessageBox("日期的输入格式必须为:\n20080203（YYYYMMDD）\n或\n0203(MMDD)");
				return false;
			}
			if (month1 > month)
			{
				year--;
			}
			date1 =ManipulateDateTime.getConversionDate(year + nowdate);
			if (!ManipulateDateTime.isValidDate(date1)){
            	new MessageBox("日期的输入错误");
				return false;
            }
			date.setText(year + nowdate);
			return true;
		}
		else if (date.getText().length() == 8 && reqcheckdatetime.equals("YYYYMMDD"))
		{
			date1 = ManipulateDateTime.getConversionDate(date.getText());
			if (!ManipulateDateTime.isValidDate(date1)){
	        	new MessageBox("日期的输入错误");
				return false;
	        }
			if (ManipulateDateTime.checkDate(date1))
			{
				return true;
			}
			else
			{
				new MessageBox("日期的输入格式必须为:\n20080203（YYYYMMDD）\n或\n0203(MMDD)");
				return false;
			}
		}
		else if (reqcheckdatetime.equals("YYYYMMDDHHMMSS"))
		{
			if (date.getText().length() != 14)
			{
				new MessageBox("日期的输入格式必须为:\n20080203010203（YYYYMMDDHHMMSS)");
				return false;
			}

			date1 = ManipulateDateTime.getConversionDate(date.getText().substring(0, 8));
			time = ManipulateDateTime.getConversionTime(date.getText().substring(8));

			if (!ManipulateDateTime.checkDate(date1) || !ManipulateDateTime.checkTime(time))
			{
				new MessageBox("日期的输入格式必须为:\n20080203010203（YYYYMMDDHHMMSS)");
				return false;
			}
			return true;
		}
		else
		{
			new MessageBox("日期的输入格式必须为:\n20080203（YYYYMMDD）\n或\n0203(MMDD)");
			return false;
		}
	}

	public boolean checkBankOperType(int operType, SaleBS saleBS, PaymentBank payObj)
	{
		boolean ok = true;
		if (saleBS != null)
		{
			if (
			// 销售交易或者扣回时,只允许选择0(消费)
			// 退货交易且非扣回时,只允许选择1(撤销),2(退货)
			((SellType.ISSALE(saleBS.saletype) || saleBS.isRefundStatus()) && operType != PaymentBank.XYKXF) || ((SellType.ISBACK(saleBS.saletype) && !saleBS.isRefundStatus()) && operType != PaymentBank.XYKCX && operType != PaymentBank.XYKTH))
			{
				ok = false;
			}
		}
		else
		{
			if (
			// 删除付款时只允许选择1(撤销),2(退货)
			// 交易红冲时只允许选择1(撤销)
			// 后台退货时只允许选择1(撤销),2(退货)
			// 非小票交易不允许选择0(消费)
			(payObj != null && operType != PaymentBank.XYKCX && operType != PaymentBank.XYKTH) || (payObj != null && SellType.ISHC(payObj.salehead.djlb) && operType != PaymentBank.XYKCX) || (payObj != null && SellType.ISBACK(payObj.salehead.djlb) && operType != PaymentBank.XYKCX && operType != PaymentBank.XYKTH) || (operType == PaymentBank.XYKXF && !salebyself))
			{
				ok = false;
			}
		}
		if (!ok)
		{
			new MessageBox("不允许进行该银联操作,请重新选择");
			return false;
		}

		return true;
	}

	public boolean allowSaleBySelf(boolean f)
	{
		salebyself = f;

		return salebyself;
	}

	public boolean setBankTypeByMenu(boolean f)
	{
		banktypebymenu = f;

		return banktypebymenu;
	}

	public boolean getBankTypeByMenu()
	{
		return banktypebymenu;
	}

	public BankLogDef getBankLog()
	{
		return bld;
	}

	public String getErrorMsg()
	{
		return errmsg;
	}

	public boolean checkBankSucceed()
	{
		if (bld.retbz == 'N')
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	public void XYKSetError(String errCode, String errInfo)
	{
		this.errmsg = errInfo;

		bld.retcode = errCode;
		if (errInfo.length() > 40)
			errInfo = errInfo.substring(0, 40);
		bld.retmsg = errInfo;
	}

	public void setReplaceBankNameMode(boolean b)
	{
		replacebankname = b;
	}

	public boolean getReplaceBankNameMode()
	{
		return replacebankname;
	}

	public void setErrorMsgShowMode(boolean b)
	{
		errmsgshow = b;
	}

	public boolean getErrorMsgShowMode()
	{
		return errmsgshow;
	}

	public void setWaitCallBack(Runnable cbmsg)
	{
		msgcallback = cbmsg;
	}

	public String getBankPath(String paycode)
	{
		String defaultPath = null;
		if ((ConfigClass.BankPath == null) || (ConfigClass.BankPath.equals("")))
		{
			defaultPath = "C:\\JAVAPOS";
		}
		else
		{
			defaultPath = ConfigClass.BankPath;
		}
		try
		{
			if ((paycode == null) || (paycode.length() <= 0))
				return defaultPath;
			if ((paycode == null) || (paycode.trim().equals("")))
				return defaultPath;
			if ((ConfigClass.BankPath == null) || (ConfigClass.BankPath.trim().equals("")))
				return defaultPath;
			if (("," + ConfigClass.BankPath).indexOf("," + paycode) < 0)
				return defaultPath;
			String[] bankPath = ConfigClass.BankPath.split("\\|");

			if ((bankPath == null) || (bankPath.length < 1))
				return defaultPath;
			for (int i = 0; i < bankPath.length; i++)
			{
				if (bankPath[i] == null)
				{
					continue;
				}
				String[] item = bankPath[i].split(",");
				if ((item == null) || (item.length < 2))
				{
					continue;
				}
				if (item[1].equals(paycode)) { return item[0]; }
			}
			return defaultPath;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return defaultPath;
	}

	public boolean callBankFunc(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		boolean doClosePrint = false;

		try
		{
			// 银联接口需要自己进行打印则释放打印机
			if (GlobalInfo.sysPara.issetprinter == 'Y' && GlobalInfo.syjDef.isprint == 'Y' && Printer.getDefault() != null && Printer.getDefault().getStatus())
			{
				Printer.getDefault().close();
				doClosePrint = true;
			}

			// 规范数据
			if (track1 == null)
				track1 = "";
			if (track2 == null)
				track2 = "";
			if (track3 == null)
				track3 = "";
			if (oldseqno == null)
				oldseqno = "";
			if (oldauthno == null)
				oldauthno = "";
			if (olddate == null)
				olddate = "";

			// 写入请求数据日志
			if (!this.WriteRequestLog(type, money, oldseqno, oldauthno, olddate)) { return false; }

			// 调用金卡模块处理
			this.XYKExecute(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);

			// 写入应答数据日志
			this.WriteResultLog();

			// 写入单独进行的银联消费数据
			this.WriteSelfSaleData(memo);

			// 将交易日志发送网上
			this.BankLogSend();

			// 判断交易是否成功
			return checkBankSucceed();
		}
		catch (Exception ex)
		{
			new MessageBox("执行第三方支付接口异常!\n\n" + ex.getMessage());
			ex.printStackTrace();
			return false;
		}
		finally
		{
			// 银联接口执行完重新连接打印机
			if (GlobalInfo.sysPara.issetprinter == 'Y' && GlobalInfo.syjDef.isprint == 'Y' && Printer.getDefault() != null && !Printer.getDefault().getStatus() && doClosePrint)
			{
				Printer.getDefault().open();
				Printer.getDefault().setEnable(true);
			}
		}
	}

	public String getMemo(int type, double money, String oldseqno, String oldauthno, String olddate)
	{
		return "";
	}

	public boolean WriteRequestLog(int type, double money, String oldseqno, String oldauthno, String olddate)
	{
		try
		{
			bld = new BankLogDef();

			Object obj = GlobalInfo.dayDB.selectOneData("select max(rowcode) from BANKLOG");

			if (obj == null)
			{
				bld.rowcode = 1;
			}
			else
			{
				bld.rowcode = Integer.parseInt(String.valueOf(obj)) + 1;
			}

			bld.rqsj = ManipulateDateTime.getCurrentDateTime();
			bld.syjh = GlobalInfo.syjDef.syjh;
			bld.fphm = GlobalInfo.syjStatus.fphm;
			bld.syyh = (GlobalInfo.posLogin != null ? GlobalInfo.posLogin.gh : "");
			bld.type = String.valueOf(type);
			bld.je = money;
			bld.oldrq = olddate;
			bld.typename = getChangeType(getFuncItem(), bld.type);
			bld.classname = (bankcfgname != null ? bankcfgname : this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1));

			if ((oldseqno != null) && !oldseqno.trim().equals(""))
			{
				bld.oldtrace = Long.parseLong(oldseqno);
			}
			else
			{
				bld.oldtrace = 0;
			}

			bld.cardno = "";
			bld.trace = 0;
			bld.authno = "";
			bld.bankinfo = "";
			bld.crc = "";
			bld.retcode = "";
			bld.retmsg = "";
			bld.retbz = 'N';
			bld.net_bz = 'N';
			bld.allotje = 0;
			bld.memo = getMemo(type, money, oldseqno, oldauthno, olddate);
			bld.memo1 = "";
			bld.memo2 = "";
			bld.tempstr = "";
			bld.tempstr1 = "";

			//
			if (!AccessDayDB.getDefault().writeBankLog(bld)) { return false; }
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			new MessageBox("写入请求数据交易日志失败\n\n" + ex.getMessage(), null, false);
			bld = null;

			return false;
		}

		return true;
	}

	protected boolean WriteResultLog()
	{
		try
		{
			// 更新
			if (bld.retmsg != null && !bld.retmsg.equals(""))
				this.errmsg = bld.retmsg;
			if (!AccessDayDB.getDefault().updateBankLogbyFirst(bld))
			{
				new MessageBox("写入应答数据交易日志失败!", null, false);
				return false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("写入应答数据交易日志异常\n\n" + ex.getMessage(), null, false);
			return false;
		}

		return true;
	}

	protected boolean WriteSelfSaleData(Vector memo)
	{
		// 不启用单独的银联消费交易
		if (!salebyself)
			return false;

		// 小票交易付款时memo不为空，不记录可分配金额
		if (memo != null && memo.size() > 0)
			return false;

		// 只有成功的单独银联消费交易才记录该交易的可分配金额,供小票付款时选择
		if (bld.retbz == 'N')
			return false;
		if (!bld.type.equals(String.valueOf(PaymentBank.XYKXF)))
			return false;

		// 可分配金额初始等于交易金额
		bld.allotje = bld.je;

		// 更新交易日志
		try
		{
			if (!AccessDayDB.getDefault().updateBankLog(bld, true))
			{
				new MessageBox("写入分配金额交易日志失败!", null, false);
				return false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("写入分配金额交易日志异常\n\n" + ex.getMessage(), null, false);
			return false;
		}

		return true;
	}

	public boolean BankLogSend()
	{
		ProgressBox pb = null;

		try
		{
			pb = new ProgressBox();

			pb.setText("正在发送第三方支付交易日志,请等待...");

			//
			if (NetService.getDefault().sendBankLog(bld))
			{
				//
				bld.net_bz = 'Y';

				//
				if (!AccessDayDB.getDefault().updateBankLog(bld)) { return false; }
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
			}
		}
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			String callexec = getBankClassConfig("CALLEXEC");
			if (callexec == null || callexec.trim().length() <= 0)
			{
				// yinliang test
				bld.retcode = "00";
				bld.retmsg = "模拟第三方支付交易成功!";
				bld.cardno = track2;
				bld.trace = Math.round(Math.random() * 1000000);
				bld.bankinfo = "0000测试银行";
				bld.memo = "100"; // 卡内余额
				this.errmsg = bld.retmsg;
				XYKCheckRetCode();
				// new MessageBox(bld.retmsg);
				return true;
			}
			else
			{
				// 支持保存重印文件模式,则直接在磁盘查找重印文件进行打印,不调用银联接口返回重印文件
				boolean call = true;
				if (saveprintagain && type == PaymentBank.XYKCD)
				{
					String tracefile = null;
					if (oldseqno != null && oldseqno.length() > 0)
						tracefile = "BankDoc\\" + "bankdoc_" + oldseqno + ".txt";
					else
						tracefile = "BankDoc\\" + "bankdoc_last.txt";
					if (PathFile.fileExist(tracefile))
					{
						call = false;

						// 无需调用接口直接生成成功应答
						String retcode = getBankClassConfig("RETCODE");
						if (retcode == null || retcode.length() <= 0)
							retcode = "00";
						if (retcode != null && retcode.length() > 0)
							retcode = retcode.split(",")[0];
						bld.retcode = retcode;
						bld.retmsg = "找到签购单重印文件";

						// 把重印文件拷贝为当前签购单文件
						String file = getBankClassConfig("PRINTFILE");
						if (file != null && file.length() > 0)
							PathFile.copyPath(tracefile, file);
					}
					else
					{
						XYKSetError("XX", "找不到原签购单重印文件");
						XYKCheckRetCode();
						new MessageBox(getErrorMsg());
						return true;
					}
				}

				if (call)
				{
					// 写入请求数据
					StringBuffer cmdline = new StringBuffer();
					if (!XYKWriteRequest(cmdline, type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo)) { return false; }

					// new MessageBox(cmdline.toString());
					// 调用接口模块
					if (!XYKExecuteModule(cmdline.toString())) { return false; }

					// 读取应答数据
					if (!XYKReadResult()) { return false; }
				}

				//检查银联返回
				if(!XYKCheckReJe(memo)) return false;
				
				// 检查交易是否成功
				XYKCheckRetCode();

				// 打印签购单
				if (XYKNeedPrintDoc())
				{
					XYKPrintDoc();
				}

				return true;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			XYKSetError("XX", "支付异常XX:" + ex.getMessage());
			new MessageBox("调用第三方支付处理模块异常!\n\n" + ex.getMessage(), null, false);

			return false;
		}
	}

	private boolean XYKCheckReJe(Vector memo)
	{
		if( GlobalInfo.sysPara.isCheckReJe == 'Y'){
			SaleBS saleBS = (SaleBS) memo.elementAt(2);
			double ye = saleBS.calcPayBalance();
			if(bld.je>ye) {
				while(new MessageBox(Language.apply("金卡接口返回数据中金额大于剩余付款金额!\n请联系管理员取消本笔金卡交易\n1-退出"), null, false).verify()!= GlobalVar.Key1){
					
				}
				bld.retmsg = "金卡接口返回金额大于剩余付款金额!";
				return false;
			}
		}
		return true;
	}

	protected boolean XYKExecuteModule(String cmdline)
	{
		String exec = "";
		try
		{
			exec = getBankClassConfig("CALLEXEC");
			exec = ExpressionDeal.replace(exec, "%PARAM%", cmdline, true);
			// new MessageBox(exec);
			String file = getBankClassConfig("CALLFILE");
			if (file == null || file.length() <= 0)
				CommonMethod.waitForExec(exec);
			else
				CommonMethod.waitForExec(exec, file, null, msgcallback);
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			XYKSetError("XX", "加载第三方支付执行模块失败:" + ex.getMessage());
			new MessageBox("加载第三方支付执行模块失败!\n\n" + exec + "\n\n" + ex.getMessage(), null, false);

			return false;
		}
	}

	protected boolean XYKWriteRequest(StringBuffer arg, int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{

			// 先删除上次交易数据文件
			String reqfile = getBankClassConfig("REQFILE");
			if (reqfile != null && reqfile.length() > 0 && PathFile.fileExist(reqfile))
			{
				PathFile.deletePath(reqfile);
				if (PathFile.fileExist(reqfile))
				{
					XYKSetError("XX", "交易请求文件 " + reqfile + " 无法删除,请重试");
					new MessageBox(this.getErrorMsg());
					return false;
				}
			}

			String file = getBankClassConfig("RETFILE");
			if (file != null && file.length() > 0 && PathFile.fileExist(file))
			{
				PathFile.deletePath(file);
				if (PathFile.fileExist(file))
				{
					XYKSetError("XX", "交易应答文件 " + file + " 无法删除,请重试");
					new MessageBox(this.getErrorMsg());
					return false;
				}
			}

			file = getBankClassConfig("PRINTFILE");
			if (file != null && file.length() > 0 && PathFile.fileExist(file))
			{
				PathFile.deletePath(file);
				if (PathFile.fileExist(file))
				{
					XYKSetError("XX", "交易签购单文件 " + file + " 无法删除,请重试");
					new MessageBox(this.getErrorMsg());
					return false;
				}
			}

			// 组织请求数据
			if (arg == null)
				arg = new StringBuffer();
			String reqsplit = getBankClassConfig("REQSPLT");
			String reqparastr = getBankClassConfig("TYPE" + type + "_REQPARA");
			String reqtypestr = getBankClassConfig("TYPE" + type + "_REQTYPE");
			if (reqparastr == null)
				reqparastr = getBankClassConfig("REQPARA");
			if (reqtypestr == null)
				reqtypestr = getBankClassConfig("REQTYPE");
			String[] reqpara = (reqparastr == null ? null : reqparastr.split(","));
			String[] reqtype = (reqtypestr == null ? null : reqtypestr.split(" "));

			for (int i = 0; reqpara != null && i < reqpara.length; i++)
			{
				String param = "";

				// 转换参数
				if ("%SYJH%".equalsIgnoreCase(reqpara[i]))
					param = GlobalInfo.syjDef.syjh;
				else if ("%SYYH%".equalsIgnoreCase(reqpara[i]))
					param = GlobalInfo.posLogin.gh;
				else if ("%TYPE%".equalsIgnoreCase(reqpara[i]))
					param = String.valueOf(type);
				else if ("%MONEY%".equalsIgnoreCase(reqpara[i]))
					param = String.valueOf(money);
				else if ("%MKTCODE%".equalsIgnoreCase(reqpara[i]))
					param = GlobalInfo.sysPara.mktcode;
				else if ("%TRACK1%".equalsIgnoreCase(reqpara[i]))
					param = track1;
				else if ("%TRACK2%".equalsIgnoreCase(reqpara[i]))
					param = track2;
				else if ("%TRACK3%".equalsIgnoreCase(reqpara[i]))
					param = track3;
				else if ("%OLDSEQNO%".equalsIgnoreCase(reqpara[i]))
					param = oldseqno;
				else if ("%OLDAUTHNO%".equalsIgnoreCase(reqpara[i]))
					param = oldauthno;
				else if ("%OLDDATE%".equalsIgnoreCase(reqpara[i]))
					param = olddate;
				else if ("%CRC%".equalsIgnoreCase(reqpara[i]))
					param = XYKGetCRC();
				else if ("%PAYCODE%".equalsIgnoreCase(reqpara[i]))
					param = ((memo != null && memo.size() > 0) ? (String) memo.elementAt(0) : "");
				else if ("%FPHM%".equalsIgnoreCase(reqpara[i]))
				{
					if ((memo != null && memo.size() > 2))
					{
						if ((SaleBS) memo.elementAt(2) != null && ((SaleBS) memo.elementAt(2)).saleHead != null)
						{
							param = String.valueOf(((SaleBS) memo.elementAt(2)).saleHead.fphm);
						}
					}
					else
					{
						param = "";
					}
				}
				else if ("%SALEHEAD%".equalsIgnoreCase(reqpara[i]))
				{
					SaleHeadDef sh = ((memo != null && memo.size() > 2) ? ((SaleBS) memo.elementAt(2)).saleHead : null);
					String parastr = getBankClassConfig("HEADPARA");
					String typestr = getBankClassConfig("HEADTYPE");
					String split = getBankClassConfig("HEADSPLT");
					String[] spara = (parastr == null ? null : parastr.split(","));
					String[] stype = (typestr == null ? null : typestr.split(" "));
					StringBuffer sb = new StringBuffer();
					if (sh != null)
					{
						for (int k = 0; spara != null && k < spara.length; k++)
						{
							Object curObj = PrintTemplate.findObjectValue(sh, spara[k], -1);
							if (curObj != null)
							{
								String pa = "";
								if (k < stype.length)
									pa = ExpressionDeal.replace(formatParam(stype[k], curObj), split, "");
								else
									pa = ExpressionDeal.replace(String.valueOf(curObj), split, "");
								pa = ExpressionDeal.replace(pa, reqsplit, "");
								sb.append(pa);
							}
							else
								sb.append(" ");
							if (split != null && split.length() > 0 && k < spara.length - 1)
								sb.append(split);
						}
					}
					param = sb.toString();
				}
				else if ("%SALEGOODS%".equalsIgnoreCase(reqpara[i]))
				{
					Vector sgs = ((memo != null && memo.size() > 2) ? ((SaleBS) memo.elementAt(2)).saleGoods : null);
					String parastr = getBankClassConfig("GOODSPARA");
					String typestr = getBankClassConfig("GOODSTYPE");
					String split = getBankClassConfig("GOODSSPLT");
					String rows = getBankClassConfig("GOODSROWS");
					String[] spara = (parastr == null ? null : parastr.split(","));
					String[] stype = (typestr == null ? null : typestr.split(" "));
					StringBuffer sb = new StringBuffer();
					for (int j = 0; sgs != null && j < sgs.size(); j++)
					{
						SaleGoodsDef sg = (SaleGoodsDef) sgs.elementAt(j);
						for (int k = 0; spara != null && k < spara.length; k++)
						{
							Object curObj = null;
							if ("cjj".equalsIgnoreCase(spara[k]))
								curObj = new Double(sg.hjje - sg.hjzk);
							else
								curObj = PrintTemplate.findObjectValue(sg, spara[k], -1);
							if (curObj != null)
							{
								String pa = "";
								if (k < stype.length)
									pa = ExpressionDeal.replace(formatParam(stype[k], curObj), split, "");
								else
									pa = ExpressionDeal.replace(String.valueOf(curObj), split, "");
								pa = ExpressionDeal.replace(pa, rows, "");
								pa = ExpressionDeal.replace(pa, reqsplit, "");
								sb.append(pa);
							}
							else
								sb.append(" ");
							if (split != null && split.length() > 0 && k < spara.length - 1)
								sb.append(split);
						}
						if (rows != null && rows.length() > 0 && j < sgs.size() - 1)
							sb.append(rows);
					}
					param = sb.toString();
				}
				else if ("%SALEPAY%".equalsIgnoreCase(reqpara[i]))
				{
					Vector sps = ((memo != null && memo.size() > 2) ? ((SaleBS) memo.elementAt(2)).salePayment : null);
					String parastr = getBankClassConfig("PAYSPARA");
					String typestr = getBankClassConfig("PAYSTYPE");
					String split = getBankClassConfig("PAYSSPLT");
					String rows = getBankClassConfig("PAYSROWS");
					String[] spara = (parastr == null ? null : parastr.split(","));
					String[] stype = (typestr == null ? null : typestr.split(" "));
					StringBuffer sb = new StringBuffer();
					for (int j = 0; sps != null && j < sps.size(); j++)
					{
						SalePayDef sp = (SalePayDef) sps.elementAt(j);
						for (int k = 0; spara != null && k < spara.length; k++)
						{
							Object curObj = PrintTemplate.findObjectValue(sp, spara[k], -1);
							if (curObj != null)
							{
								String pa = "";
								if (k < stype.length)
									pa = ExpressionDeal.replace(formatParam(stype[k], curObj), split, "");
								else
									pa = ExpressionDeal.replace(String.valueOf(curObj), split, "");
								pa = ExpressionDeal.replace(pa, rows, "");
								pa = ExpressionDeal.replace(pa, reqsplit, "");
								sb.append(pa);
							}
							else
								sb.append(" ");
							if (split != null && split.length() > 0 && k < spara.length - 1)
								sb.append(split);
						}
						if (rows != null && rows.length() > 0 && j < sps.size() - 1)
							sb.append(rows);
					}
					param = sb.toString();
				}
				else
					param = reqpara[i];

				// 格式化
				if (i < reqtype.length)
					param = formatParam(reqtype[i], param);
				if (!("%SALEHEAD%".equalsIgnoreCase(reqpara[i]) || "%SALEGOODS%".equalsIgnoreCase(reqpara[i]) || "%SALEPAY%".equalsIgnoreCase(reqpara[i])))
				{
					param = ExpressionDeal.replace(param, reqsplit, "");
				}
				if ("%CRC%".equalsIgnoreCase(reqpara[i]))
					bld.crc = param;

				// 组织请求数据
				arg.append(param);
				if (reqsplit != null && reqsplit.length() > 0 && i < reqpara.length - 1)
					arg.append(reqsplit);
			}

			// 写入请求数据
			if (reqfile != null && reqfile.length() > 0 && !rtf.writeFile(reqfile, arg.toString(), getBankClassConfig("REQENCODE")))
			{
				XYKSetError("XX", "写入交易请求数据失败!");
				new MessageBox(getErrorMsg());
				return false;
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("写入交易请求数据异常!\n\n" + ex.getMessage(), null, false);

			return false;
		}
	}

	private String formatParam(String fmt, Object val)
	{
		try
		{
			String fmtval = "";
			if (fmt == null || fmt.trim().length() <= 0)
				fmt = "%s";
			if (fmt.indexOf('%') < 0)
				fmt = "%" + fmt;
			int len = fmt.length();
			int i = fmt.indexOf('%') + 1;
			int j = i;
			for (; i < len; i++)
				if ((fmt.charAt(i) >= 'a' && fmt.charAt(i) <= 'z') || (fmt.charAt(i) >= 'A' && fmt.charAt(i) <= 'Z'))
					break;
			int fmtlen = Convert.toInt(fmt.substring(j, i));
			int fmtdec = (int) Math.abs(ManipulatePrecision.doubleConvert(Convert.toDouble(fmt.substring(j, i)) - Convert.toInt(fmt.substring(j, i))) * 10);
			if (fmt.charAt(i) == 'd')
			{
				int value = Convert.toInt(val);
				if (fmtdec > 0)
				{
					// %.2d表示以无小数点2位小数表示
					int dec10 = 1;
					for (int n = 0; n < fmtdec; n++)
						dec10 *= 10;
					value = (int) ManipulatePrecision.doubleConvert(Convert.toDouble(val) * dec10);
					if (fmt.indexOf('.') > -1)
						fmt = fmt.substring(0, j) + (fmtlen > 0 ? fmt.substring(j, fmt.indexOf('.')) : "") + fmt.substring(i);
					else
						fmt = fmt.substring(0, j) + (fmtlen > 0 ? String.valueOf(fmtlen) : "") + fmt.substring(i);
				}
				fmtval = String.format(fmt, new Object[] { new Integer(value) });
			}
			else if (fmt.charAt(i) == 'f')
				fmtval = String.format(fmt, new Object[] { new Double(Convert.toDouble(val)) });
			else
				fmtval = String.format(fmt, new Object[] { String.valueOf(val) });

			// 超出长度部分进行截取
			if (fmtlen != 0 && fmtval.length() > Math.abs(fmtlen))
			{
				if (fmtlen >= 0)
					fmtval = fmtval.substring(fmtval.length() - fmtlen);
				else
					fmtval = fmtval.substring(0, Math.abs(fmtlen));
			}

			return fmtval;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return String.valueOf(val);
		}
	}

	protected boolean XYKReadResult()
	{
		try
		{
			// 打开应答文件
			String file = getBankClassConfig("RETFILE");
			if (file == null || file.length() <= 0 || !PathFile.fileExist(file) || !rtf.loadFile(file, getBankClassConfig("RETENCODE")))
			{
				new MessageBox("读取交易应答数据失败!", null, false);
				return false;
			}

			// 读取应答配置
			String retsplit = getBankClassConfig("RETSPLT");
			String retparastr = getBankClassConfig("TYPE" + bld.type + "_RETPARA");
			String rettypestr = getBankClassConfig("TYPE" + bld.type + "_RETTYPE");
			if (retparastr == null)
				retparastr = getBankClassConfig("RETPARA");
			if (rettypestr == null)
				rettypestr = getBankClassConfig("RETTYPE");
			String[] retpara = (retparastr == null ? null : retparastr.split(","));
			String[] rettype = (rettypestr == null ? null : rettypestr.split(" "));

			// 读取应答数据
			String line = rtf.nextRecord();
			rtf.close();

			if (line == null || line.trim().length() <= 0)
			{
				new MessageBox("读取交易应答数据为空!", null, false);
				return false;
			}

			// 分解应答数据
			Vector linevec = new Vector();
			String[] linelist = null;
			if (retsplit != null && retsplit.trim().length() > 0)
				linelist = (line == null ? null : line.split(retsplit));
			else
				linelist = new String[] { line };
			int curline = 0, curlinepos = 0;
			for (int i = 0; retpara != null && i < retpara.length && linelist != null && curline < linelist.length; i++)
			{
				if (i < rettype.length)
				{
					String fmt = rettype[i];
					int j = 0;
					for (; j < fmt.length(); j++)
						if ((fmt.charAt(j) >= 'a' && fmt.charAt(j) <= 'z') || (fmt.charAt(j) >= 'A' && fmt.charAt(j) <= 'Z'))
							break;
					int fmtlen = Convert.toInt(fmt.substring(0, j));
					if (fmtlen > 0 && curlinepos + fmtlen < Convert.countLength(linelist[curline]))
						fmt = Convert.newSubString(linelist[curline], curlinepos, curlinepos + fmtlen);
					else
						fmt = Convert.newSubString(linelist[curline], curlinepos, Convert.countLength(linelist[curline]));
					curlinepos += Convert.countLength(fmt);
					if (curlinepos >= Convert.countLength(linelist[curline]))
					{
						curline++;
						curlinepos = 0;
					}
					linevec.add(fmt);
				}
				else
				{
					linevec.add(linelist[curline]);
					curline++;
					curlinepos = 0;
				}
			}

			String bankid = "", bankname = "";
			for (int i = 0; retpara != null && i < retpara.length && linevec != null && i < linevec.size(); i++)
			{
				String param = (String) linevec.elementAt(i);

				// 配置%NONE%参数标识要放弃的返回项目
				if ("%CRC%".equalsIgnoreCase(retpara[i]) && !param.equals(bld.crc))
				{
					new MessageBox("交易应答校验码不匹配:param=" + param + " crc=" + bld.crc, null, false);
					return false;
				}
				else if ("%KYE%".equalsIgnoreCase(retpara[i]))
					bld.kye = ManipulatePrecision.div(Convert.toDouble(param.trim()), 100);
				else if ("%JE%".equalsIgnoreCase(retpara[i]))
					bld.je = ManipulatePrecision.div(Convert.toDouble(param.trim()), 100);
				else if ("%RETCODE%".equalsIgnoreCase(retpara[i]))
					bld.retcode = param.trim();
				else if ("%RETMSG%".equalsIgnoreCase(retpara[i]))
					bld.retmsg = param.trim();
				else if ("%CARDNO%".equalsIgnoreCase(retpara[i]))
					bld.cardno = param.trim();
				else if ("%SEQNO%".equalsIgnoreCase(retpara[i]))
					bld.trace = Convert.toLong(param);
				else if ("%BANKID%".equalsIgnoreCase(retpara[i]))
					bankid = param.trim();
				else if ("%AUTHNO%".equalsIgnoreCase(retpara[i]))
					bld.authno = param.trim();
				else if ("%BANKNAME%".equalsIgnoreCase(retpara[i]))
					bankname = param.trim();
				else if ("%TEMPSTR%".equalsIgnoreCase(retpara[i]))
					bld.tempstr = param.trim();
				else if ("%TEMPSTR1%".equalsIgnoreCase(retpara[i]))
					bld.tempstr1 = param.trim();
				else if ("%MEMO1%".equalsIgnoreCase(retpara[i]))
					bld.memo1 = param.trim();
				else if ("%MEMO2%".equalsIgnoreCase(retpara[i]))
					bld.memo2 = param.trim();
			}

			//
			if (bld.trace <= 0)
				bld.trace = bld.rowcode;

			// 如果没有配置bankid不赋值bankinfo
			if (bankid.length() > 0 && bankname.length() <= 0)
				bankname = XYKReadBankName(bankid);
			if (bankid.length() > 0)
				bld.bankinfo = bankid + "-" + bankname;
			if (bld.retcode.length() > 0 && bld.retmsg.length() <= 0)
				bld.retmsg = XYKReadRetMsg(bld.retcode);

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("读取交易应答数据异常!" + ex.getMessage(), null, false);
			ex.printStackTrace();

			return false;
		}
		finally
		{
			rtf.close();
		}
	}

	public boolean XYKCheckRetCode()
	{
		String retcode = null;

		retcode = getBankClassConfig("TYPE" + bld.type + "_RETCODE");

		if (retcode == null)
			retcode = getBankClassConfig("RETCODE");

		if (retcode == null || retcode.length() <= 0)
			retcode = "00";
		if (retcode != null && retcode.length() > 0 && ManipulateStr.textInString(bld.retcode, retcode, ",", true))
		{
			bld.retbz = 'Y';
			if (bld.retmsg.trim().length() <= 0)
				bld.retmsg = "第三方支付处理成功";

			return true;
		}
		else
		{
			bld.retbz = 'N';

			return false;
		}
	}

	protected boolean XYKNeedPrintDoc()
	{
		// 交易未成功不打印
		if (!checkBankSucceed()) { return false; }

		// 判断当前交易类型是否需要打印
		String printtype = getBankClassConfig("PRINTTYPE");
		String pirntretcode = getBankClassConfig("PRINTRETCODE");

		if ((printtype == null || printtype.length() <= 0) && (pirntretcode == null || pirntretcode.length() <= 0))
		{
			printtype = PaymentBank.XYKXF + "," + PaymentBank.XYKCX + "," + PaymentBank.XYKTH + "," + PaymentBank.XYKCD;
		}

		if (pirntretcode != null && pirntretcode.length() > 0 && ManipulateStr.textInString(bld.retcode, pirntretcode, ",", true))
		{
			return true;
		}
		else if (printtype != null && printtype.length() > 0 && ManipulateStr.textInString(bld.type, printtype, ",", true))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public void setOnceXYKPrintDoc(boolean b)
	{
		onceprint = b;

		// 非及时打印签购单则标记小票打印完后需要打印签购单
		if (!onceprint)
			PaymentBank.haveXYKDoc = true;
		else
			PaymentBank.haveXYKDoc = false;
	}

	public boolean getOnceXYKPrintDoc()
	{
		return onceprint;
	}

	public void setSavePrintAgain(boolean b)
	{
		saveprintagain = b;
	}

	public boolean getSavePrintAgain()
	{
		return saveprintagain;
	}

	public void XYKPrintDoc_Start()
	{
		if (onceprint)
		{
			int pagesize = ConfigClass.BankPageSize;

			String port = getBankClassConfig("PRINTPORT");
			if (port == null || port.length() <= 0)
			{
				Printer.getDefault().startPrint_Journal();
				if (pagesize > 0)
					Printer.getDefault().setPagePrint_Journal(true, pagesize);
			}
			else if (port.trim().equals("1"))
			{
				Printer.getDefault().startPrint_Normal();
				if (pagesize > 0)
					Printer.getDefault().setPagePrint_Normal(true, pagesize);
			}
			else if (port.trim().equals("2"))
			{
				Printer.getDefault().startPrint_Journal();
				if (pagesize > 0)
					Printer.getDefault().setPagePrint_Journal(true, pagesize);
			}
			else if (port.trim().equals("3"))
			{
				Printer.getDefault().startPrint_Slip();
				if (pagesize > 0)
					Printer.getDefault().setPagePrint_Slip(true, pagesize);
			}
		}
		else
		{

			// 此地改为增加模式，防止在多个金卡工程同时存在时，可能序号相同
			printdoc = CommonMethod.writeFileAppend("bankdoc_" + String.valueOf(bld.trace) + ".txt");
		}
	}

	public void XYKPrintDoc_Print(String printStr)
	{
		if (onceprint)
		{
			String port = getBankClassConfig("PRINTPORT");
			if (port == null || port.length() <= 0)
				Printer.getDefault().printLine_Journal(printStr);
			else if (port.trim().equals("1"))
			{
				Printer.getDefault().printLine_Normal(printStr);
			}
			else if (port.trim().equals("2"))
			{
				Printer.getDefault().printLine_Journal(printStr);
			}
			else if (port.trim().equals("3"))
			{
				Printer.getDefault().printLine_Slip(printStr);
			}
		}
		else
		{
			printdoc.println(printStr);
		}
	}

	public void XYKPrintDoc_End()
	{
		if (onceprint)
		{
			String port = getBankClassConfig("PRINTPORT");
			if (port == null || port.length() <= 0)
				Printer.getDefault().cutPaper_Journal();
			else if (port.trim().equals("1"))
			{
				Printer.getDefault().cutPaper_Normal();
			}
			else if (port.trim().equals("2"))
			{
				Printer.getDefault().cutPaper_Journal();
			}
			else if (port.trim().equals("3"))
			{
				Printer.getDefault().cutPaper_Slip();
			}
		}
		else
		{
			if (printdoc == null)
				return;

			printdoc.flush();
			printdoc.close();
			printdoc = null;
		}
	}

	public void XYKPrintDoc()
	{
		ProgressBox pb = null;

		try
		{
			String file = getBankClassConfig("PRINTFILE");
			String printnocheck = getBankClassConfig("PRINTNOCHECK");

			if (file == null || file.length() <= 0)
				return;
			if (!PathFile.fileExist(file))
			{
				boolean bool = false;

				if (printnocheck != null && !printnocheck.equals(""))
				{
					String printnochecks[] = printnocheck.split(",");

					for (int i = 0; i < printnocheck.length(); i++)
					{
						if (printnochecks[i].trim().equals(bld.type))
						{
							bool = true;
						}
					}
				}

				if (!bool)
					new MessageBox("找不到签购单打印文件" + file + "!");

				return;
			}

			// 保存重打文件以便重打印
			if (saveprintagain)
			{
				// 按流水号备份重印文件
				String tracefile = "BankDoc\\" + "bankdoc_" + bld.trace + ".txt";
				if (!PathFile.fileExist(tracefile))
					PathFile.copyPath(file, tracefile);

				// 上一笔交易重印文件
				PathFile.copyPath(file, "BankDoc\\" + "bankdoc_last.txt");
			}

			pb = new ProgressBox();
			pb.setText("正在打印银联签购单,请等待...");

			int printnum = GlobalInfo.sysPara.bankprint;
			String encode = getBankClassConfig("PRINTENCODE");
			String strnum = getBankClassConfig("PRINTCOUNT");
			String CutFlg = getBankClassConfig("PRINTCUT");
			if (strnum != null && strnum.length() > 0)
				printnum = Convert.toInt(strnum);
			for (int i = 0; i < printnum; i++)
			{
				// 开始打印
				XYKPrintDoc_Start();

				BufferedReader br = null;

				try
				{
					br = CommonMethod.readFile(file, encode);

					if (br == null)
					{
						new MessageBox("打开签购单打印文件失败" + file + "!");

						return;
					}

					//
					boolean isstart = false;
					String line = null;
					while ((line = br.readLine()) != null)
					{
						// 打印文件中如果有空格是否省略
						if (line.length() < 1 && !PathFile.fileExist("BankSpaceLine"))
							continue;

						if (isstart)
						{
							isstart = false;
							XYKPrintDoc_Start();
						}

						if (line.length() > 0 && CutFlg != null && (line.trim().equalsIgnoreCase(CutFlg) || line.charAt(0) == (char) ManipulateStr.getHexStrToNum(CutFlg)) && onceprint)
						{
							XYKPrintDoc_End();
							isstart = true;
							continue;
						}

						System.out.println(line);
						XYKPrintDoc_Print(line);
					}
				}
				catch (Exception e)
				{
					new MessageBox(e.getMessage());
				}
				finally
				{
					if (br != null)
					{
						br.close();
					}
				}

				// 结束打印
				XYKPrintDoc_End();
			}
		}
		catch (Exception ex)
		{
			new MessageBox("打印签购单发生异常\n\n" + ex.getMessage());
			ex.printStackTrace();
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}
	}

	public String XYKGetCRC()
	{
		return ManipulatePrecision.getRandom();
	}

	public String XYKReadBankName(String bankid)
	{
		String line = "";

		try
		{
			if (!PathFile.fileExist(GlobalVar.ConfigPath + File.separator + "BankInfo.ini") || !rtf.loadFile(GlobalVar.ConfigPath + File.separator + "BankInfo.ini"))
			{
				new MessageBox("找不到BankInfo.ini", null, false);

				return bankid;
			}

			//
			while ((line = rtf.nextRecord()) != null)
			{
				if (line.length() <= 0)
				{
					continue;
				}

				String[] a = line.split("=");

				if (a.length < 2)
				{
					continue;
				}

				if (a[0].trim().equals(bankid.trim())) { return a[1].trim(); }
			}

			rtf.close();

			return bankid;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return bankid;
		}
	}

	public String XYKReadRetMsg(String retcode)
	{
		String line = "";
		String nomsg = "找不到返回码[" + retcode + "]对应的错误信息";

		try
		{
			if (!PathFile.fileExist(GlobalVar.ConfigPath + File.separator + "BankMsg.ini") || !rtf.loadFile(GlobalVar.ConfigPath + File.separator + "BankMsg.ini"))
			{
				new MessageBox("找不到bankmsg.ini", null, false);

				return nomsg;
			}

			//
			while ((line = rtf.nextRecord()) != null)
			{
				if (line.length() <= 0)
				{
					continue;
				}

				String[] a = line.split("=");

				if (a.length < 2)
				{
					continue;
				}

				if (a[0].trim().equals(retcode.trim())) { return a[1].trim(); }
			}

			rtf.close();

			return nomsg;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return nomsg;
		}
	}

	public boolean isCardType(BankLogDef bankLogDef, PayModeDef payModeDef)
	{
		return true;
	}

	public void printXYKDoc(String batch)
	{
		BufferedReader br = null;
		String line = null;

		String filename = "bankdoc_" + batch + ".txt";

		if (!PaymentBank.haveXYKDoc)
			return;

		boolean old_onceprint = onceprint;
		try
		{
			if (!PathFile.fileExist(filename))
			{
				// 不弹出提示，由于存在重打印，所以这里无法找到已经参数的签购单文件
				// new MessageBox("找不到流水号[" + batch + "]的签购单打印文件!");

				return;
			}

			br = CommonMethod.readFile(filename);
			if (br == null)
			{
				new MessageBox("打开流水号[" + batch + "]的签购单打印文件失败!");

				return;
			}

			onceprint = true;

			//
			// Printer.getDefault().startPrint_Journal();

			boolean isstart = true;

			String CutFlg = getBankClassConfig("PRINTCUT");

			boolean needCut = true;
			while ((line = br.readLine()) != null)
			{
				if (isstart)
				{
					isstart = false;
					// Printer.getDefault().startPrint_Journal();
					XYKPrintDoc_Start();
				}

				if (CutFlg != null && (line.trim().equalsIgnoreCase(CutFlg) || line.charAt(0) == (char) ManipulateStr.getHexStrToNum(CutFlg)))
				{

					// Printer.getDefault().cutPaper_Journal();
					XYKPrintDoc_End();
					needCut = false;
					isstart = true;

					continue;
				}

				if (line.trim().length() <= 0)
				{
					// Printer.getDefault().printLine_Journal("\n");
					XYKPrintDoc_Print("\n");
				}
				else
				{
					// Printer.getDefault().printLine_Journal(line);
					XYKPrintDoc_Print(line);
					needCut = true;
				}
			}

			if (needCut)
				XYKPrintDoc_End();

			// 关闭并删除打印文件
			br.close();
			br = null;
			File f = new File(filename);
			f.delete();
		}
		catch (Exception e)
		{
			e.printStackTrace();

			new MessageBox("打印签购单异常:\n\n" + e.getMessage());
		}
		finally
		{
			onceprint = old_onceprint;
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public boolean checkSeqno(Text seq)
	{

		return true;
	}

	/*
	 * 暂时不用 public String getCardTypeCode(Vector memo) { String flg = ""; if
	 * (memo != null && memo.size() > 0) { flg = (String) memo.elementAt(0); }
	 * else { Vector v = new Vector(); // 查询是否定义了银联付款方式 for (int k = 0; k <
	 * GlobalInfo.payMode.size(); k++) { PayModeDef mdf = (PayModeDef)
	 * GlobalInfo.payMode.elementAt(k); if (mdf.isbank == 'Y') { v.add(new
	 * String[] { mdf.code, mdf.name }); } }
	 * 
	 * if (v.size() > 1) { String[] title = { "付款代码", "付款名称" }; int[] width = {
	 * 100, 400 }; int choice = new MutiSelectForm().open("请选择交易方式", title,
	 * width, v); if (choice == -1) { flg = ((String[]) v.elementAt(0))[0]; }
	 * else { flg = ((String[]) v.elementAt(choice))[0]; } } else { flg =
	 * ((String[]) v.elementAt(0))[0]; } }
	 * 
	 * return flg; }
	 */
}
