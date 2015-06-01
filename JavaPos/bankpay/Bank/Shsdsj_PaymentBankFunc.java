package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import org.eclipse.swt.widgets.Display;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Shsdsj_PaymentBankFunc extends PaymentBankFunc
{
	class ResponseData
	{
		public String OperateType; //操作类型
		public String TransType; //交易类型
		public String CardType; //卡类型
		public String ResponseCode; //返回码
		public String ResponseMsg; //返回信息
		public String CashRegNo; //收银机编号
		public String CasherNo; //操作员
		public String Amount; //金额
		public String SettleNum; //批次号
		public String MerchantID; //商户号
		public String MerchantName; //商户名称
		public String TerminalID; //终端号
		public String CardNo; //卡号
		public String Exp_Date; //有效期
		public String BankNo; //发卡行标识
		public String TransDate; //交易日期
		public String TransTime; //交易时间
		public String Auth_Code; //授权号
		public String SysRefNo; //系统参照号
		public String CashTraceNo; //收银流水号
		public String OriginTrace; //撤销交易流水号
		public String SysTraceNo; //系统流水号
		public String OriginSysTrace; //系统流水号
		public String MemoItem; //保留字段
	}

	ResponseData response = new ResponseData();

	public String[] getFuncItem()
	{
		String[] func = new String[3];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";

		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示该不用输入
		switch (type)
		{
			case PaymentBank.XYKXF://消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKCX://消费撤销
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = "原参考号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = "原交易日";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKQD://交易签到
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";
				break;
			case PaymentBank.XYKJZ://交易结账
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易结账";
				break;
			case PaymentBank.XYKYE://余额查询    
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "余额查询";
				break;
			case PaymentBank.XYKCD://签购单重打
				grpLabelStr[0] = null;
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
		//0-4对应FORM中的5个输入框
		//null表示该需要用户输入,不为null用户不输入
		switch (type)
		{
			case PaymentBank.XYKXF://消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKCX://消费撤销
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "";
				break;
			case PaymentBank.XYKTH://隔日退货   
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "";
				break;
			case PaymentBank.XYKQD://交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "银联不支持该功能";
				break;
			case PaymentBank.XYKJZ://交易结账
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "银联不支持该功能";
				break;
			case PaymentBank.XYKYE://余额查询    
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "银联不支持该功能";
				break;
			case PaymentBank.XYKCD://签购单重打
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "银联不支持该功能";
				break;
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if (type != PaymentBank.XYKXF && type != PaymentBank.XYKCX && type != PaymentBank.XYKTH)
			{
				errmsg = "银联接口不支持该交易";
				new MessageBox(errmsg);
				return false;
			}

			// 先删除上次交易数据文件
			if (PathFile.fileExist("c:\\libSand\\request.dat"))
			{
				PathFile.deletePath("c:\\libSand\\request.dat");

				if (PathFile.fileExist("c:\\libSand\\request.dat"))
				{
					errmsg = "交易请求文件无法删除,请重试";
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist("c:\\libSand\\result.dat"))
			{
				PathFile.deletePath("c:\\libSand\\result.dat");

				if (PathFile.fileExist("c:\\libSand\\result.dat"))
				{
					errmsg = "交易应答文件无法删除,请重试";
					new MessageBox(errmsg);
					return false;
				}
			}

			if (!(memo != null && memo.size() > 0))
			{
				Vector payModeList = new Vector();
				PayModeDef payMode = null;
				
				for (int i = 0; i < GlobalInfo.payMode.size(); i ++)
				{
					payMode = (PayModeDef)GlobalInfo.payMode.get(i);
					
					if (payMode.isbank == 'Y')
					{
						String payCode = "";
						
						if(payMode.code.length() > 1)
						{
							payCode = payMode.code.substring(payMode.code.length() - 2);
						}
						
						payModeList.add(new String[]{payCode, payMode.name});
					}
				}
				
				String[] title = { "代码", "卡类型" };
				int[] width = { 60, 440 };
				Vector contents = new Vector();
//				contents.add(new String[] { "00", "银行卡" });
//				contents.add(new String[] { "17", "SMART卡" });
//				contents.add(new String[] { "18", "巍康卡" });
//				contents.add(new String[] { "19", "畅购卡" });
//				contents.add(new String[] { "20", "OK积点卡" });
//				contents.add(new String[] { "21", "OK会员卡" });
				for (int i = 0; i < payModeList.size(); i++)
				{
					contents.add((String[])payModeList.get(i));
				}
				
				int choice = new MutiSelectForm().open("请选择交易卡类型", title, width, contents, true);
				if (choice == -1)
				{
					errmsg = "没有选择交易卡类型";
					return false;
				}
				else
				{
					memo = new Vector();
					String[] row = (String[]) (contents.elementAt(choice));
					memo.add(row[0]);
				}

				// 刷新界面
				while (Display.getCurrent().readAndDispatch())
					;
			}

			// 写入请求数据
			if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo)) { return false; }

			// 调用接口模块
			if (PathFile.fileExist("c:\\libSand\\libSand.exe"))
			{
				Process p = Runtime.getRuntime().exec("c:\\libSand\\libSand.exe");

				if (p != null)
				{
					StreamLibSand errorStream = new StreamLibSand(p.getErrorStream());
					StreamLibSand outputStream = new StreamLibSand(p.getInputStream());
					errorStream.start();
					outputStream.start();

					p.waitFor();
				}
			}
			else
			{
				new MessageBox("找不到金卡工程模块 libSand.exe");

				return false;
			}

			// 读取应答数据
			if (!XYKReadResult()) { return false; }

			// 检查交易是否成功
			XYKCheckRetCode();

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);

			return false;
		}
	}

	public boolean XYKWriteRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		StringBuffer sbstr = null;

		try
		{
			sbstr = new StringBuffer();

			// 组织请求数据
			// 操作类型,交易类型,卡类型,收银机编号,操作员,金额,收银流水号,原交易流水号,预留字段
			sbstr.append("A0" + ",");
			if (type == PaymentBank.XYKXF) sbstr.append("30" + ",");
			else if (type == PaymentBank.XYKCX) sbstr.append("40" + ",");
			else if (type == PaymentBank.XYKTH) sbstr.append("50" + ",");
			else
			{
				throw new Exception("无效的交易类型!");
			}
			String paycode = "";
			if (memo != null && memo.size() > 0)
			{
				paycode = memo.elementAt(0).toString();
				sbstr.append(paycode.substring(paycode.length() - 2) + ",");
			}
			else
			{
				throw new Exception("无效的卡类型!");
			}
			sbstr.append(Convert.increaseChar(GlobalInfo.syjDef.syjh, '0', 6) + ",");
			sbstr.append(Convert.increaseChar(GlobalInfo.posLogin.gh, '0', 6) + ",");
			sbstr.append(Convert.increaseCharForward(String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1)), '0', 12) + ",");
			sbstr.append(Convert.increaseLong(GlobalInfo.syjStatus.fphm, 6) + ",");
			if (paycode.endsWith("05") && type == PaymentBank.XYKCX)
			{
				sbstr.append(Convert.increaseChar("", 6) + ",");
			}
			else
			{
				sbstr.append(Convert.increaseCharForward(oldseqno, '0', 6) + ",");
			}
			if (type == PaymentBank.XYKTH)
			{
				sbstr.append(Convert.increaseChar(oldseqno, 12) + Convert.increaseChar(olddate, 4));
			}
			else
			{
				sbstr.append(" ");
			}

			// 写入请求数据
			if (!rtf.writeFile("c:\\libSand\\request.dat", sbstr.toString()))
			{
				new MessageBox("写入金卡工程请求数据失败!", null, false);

				return false;
			}

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
			ex.printStackTrace();

			return false;
		}
		finally
		{
			if (sbstr != null)
			{
				sbstr.delete(0, sbstr.length());
				sbstr = null;
			}
		}
	}

	public boolean XYKReadResult()
	{
		try
		{
			if (!PathFile.fileExist("c:\\libSand\\result.dat") || !rtf.loadFileByGBK("c:\\libSand\\result.dat"))
			{
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}

			// 读取请求数据
			String line = rtf.nextRecord();
			rtf.close();
			String[] linelist = line.split(",");

			if (linelist.length != 24)
			{
				new MessageBox("金卡工程应答数据格式错误", null, false);

				return false;
			}

			response.OperateType = linelist[0].trim();
			response.TransType = linelist[1].trim();
			response.CardType = linelist[2].trim();
			response.ResponseCode = linelist[3].trim();
			response.ResponseMsg = linelist[4].trim();
			response.CashRegNo = linelist[5].trim();
			response.CasherNo = linelist[6].trim();
			response.Amount = linelist[7].trim();
			response.SettleNum = linelist[8].trim();
			response.MerchantID = linelist[9].trim();
			response.MerchantName = linelist[10].trim();
			response.TerminalID = linelist[11].trim();
			response.CardNo = linelist[12].trim();
			response.Exp_Date = linelist[13].trim();
			response.BankNo = linelist[14].trim();
			response.TransDate = linelist[15].trim();
			response.TransTime = linelist[16].trim();
			response.Auth_Code = linelist[17].trim();
			response.SysRefNo = linelist[18].trim();
			response.CashTraceNo = linelist[19].trim();
			response.OriginTrace = linelist[20].trim();
			response.SysTraceNo = linelist[21].trim();
			response.OriginSysTrace = linelist[22].trim();
			response.MemoItem = linelist[23].trim();

			//
			bld.retcode = response.ResponseCode;
			bld.retmsg = response.ResponseMsg;
			bld.cardno = response.CardNo;
			bld.trace = Convert.toLong(response.CashTraceNo);
			if (response.BankNo.length() >= 2)
			{
				String bankid;
				if (response.BankNo.substring(0, 2).equals("00"))
				{
					bankid = response.BankNo.substring(2, 6);
				}
				else
				{
					bankid = "WK" + response.BankNo.substring(0, 2);
				}

				bld.bankinfo = bankid + XYKReadBankName(bankid);
			}
			double j = Double.parseDouble(response.Amount);
			j = ManipulatePrecision.mul(j, 0.01);
			bld.je = j;

			//
			errmsg = bld.retmsg;

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
			ex.printStackTrace();

			return false;
		}
	}

	class StreamLibSand extends Thread
	{
		InputStream is;

		StreamLibSand(InputStream is)
		{
			this.is = is;
		}

		public void run()
		{
			try
			{
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				while (br.readLine() != null)
					;
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
	}

}
