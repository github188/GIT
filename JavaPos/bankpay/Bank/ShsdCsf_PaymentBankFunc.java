package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;

import org.eclipse.swt.widgets.Display;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class ShsdCsf_PaymentBankFunc extends PaymentBankFunc
{
	public String getbankfunc()
	{
		return "c:\\JavaPOS\\bank\\";
	}
	
	public String[] getFuncItem()
	{
		String[] func = new String[7];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结帐";
		func[5] = "[" + PaymentBank.XYKCD + "]" + "交易查询";
		func[6] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		return func;
	}

	Vector cardtype = null;

	public void XYKPrintDoc()
	{
		ProgressBox pb = null;

		try
		{
			String printName = "";

			int type = Integer.parseInt(bld.type.trim());

			if (type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKTH || type == PaymentBank.XYKJZ)
			{
				if (!PathFile.fileExist(getbankfunc() + "SandTicket.txt"))
				{
					new MessageBox("找不到签购单打印文件!");

					return;
				}
				else
				{
					printName = getbankfunc() + "SandTicket.txt";
				}
			}
			else
			{
				return;
			}

			pb = new ProgressBox();
			pb.setText("正在打印银联签购单,请等待...");

			for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++)
			{
				XYKPrintDoc_Start();

				BufferedReader br = null;

				try
				{
					br = CommonMethod.readFileGBK(printName);

					if (br == null)
					{
						new MessageBox("打开" + printName + "打印文件失败!");

						return;
					}

					String line = null;

					while ((line = br.readLine()) != null)
					{

						if (line.trim().equals("CUTPAPPER"))
						{
							break;
						}

						if (line.trim().length() > 0)
						{
							XYKPrintDoc_Print(line.trim());
						}
					}

					for (int j = 0; j < 4; j++)
					{
						XYKPrintDoc_Print("");
					}
				}
				catch (Exception ex)
				{
					new MessageBox(ex.getMessage());
				}
				finally
				{
					if (br != null)
					{
						br.close();
					}
				}

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

	public boolean XYKNeedPrintDoc()
	{
		if (!checkBankSucceed()) { return false; }

		int type = Integer.parseInt(bld.type.trim());

		// 消费，消费撤销，隔日退货，重打签购单
		if ((type == PaymentBank.XYKXF) || (type == PaymentBank.XYKCX) || (type == PaymentBank.XYKTH) || (type == PaymentBank.XYKJZ))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		if (cardtype != null)
			cardtype.clear();

		if (type != PaymentBank.XYKQD && type != PaymentBank.XYKJZ && type != PaymentBank.XYKCD)
		{
			// 不进行选择，直接交易银联卡
			if (cardtype == null)
				cardtype = new Vector();

			// 只用巍康卡
			cardtype.add(new String[] { "06", "巍康卡" });

			if (!(cardtype != null && cardtype.size() > 0))
			{
				String[] title = { "代码", "卡类型" };
				int[] width = { 60, 440 };
				Vector contents = new Vector();
				contents.add(new String[] { "01", "银行卡" });
				contents.add(new String[] { "02", "会员卡" });
				contents.add(new String[] { "03", "SMART卡" });
				contents.add(new String[] { "05", "联华OK卡" });
				contents.add(new String[] { "06", "巍康卡" });
				contents.add(new String[] { "07", "联华积点卡" });

				int choice = new MutiSelectForm().open("请选择交易卡类型", title, width, contents, true);
				if (choice == -1)
				{
					errmsg = "没有选择交易卡类型";
					return false;
				}
				else
				{
					cardtype = new Vector();
					String[] row = (String[]) (contents.elementAt(choice));
					cardtype.add(row[0]);
				}

				// 刷新界面
				while (Display.getCurrent().readAndDispatch());
			}
		}

		// 0-4对应FORM中的5个输入框
		// null表示该不用输入
		switch (type)
		{
			case PaymentBank.XYKXF:// 消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				if (cardtype != null && cardtype.size() > 0)
				{
					if (cardtype.get(0).toString().equals("05"))
					{
						grpLabelStr[0] = null;
						grpLabelStr[1] = null;
						grpLabelStr[2] = null;
						grpLabelStr[3] = "卡号";
						grpLabelStr[4] = "交易金额";
					}
				}
				break;
			case PaymentBank.XYKCX:// 消费撤销
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKTH:// 隔日退货
				grpLabelStr[0] = "原参考号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = "原交易日";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKQD:// 交易签到
				grpLabelStr[0] = null;
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
			case PaymentBank.XYKCD:// 余额查询
				grpLabelStr[0] = "原小票号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打签购单";
				break;
		}

		return true;
	}

	protected String getBankClassConfig(String attr)
	{
		for (int i = 0; bankcfgvector != null && i < bankcfgvector.size(); i++)
		{
			String[] s = (String[]) bankcfgvector.elementAt(i);
			if (attr.equalsIgnoreCase(s[0])) { return s[1]; }
		}

		if (attr.equals("REQCHECKDATETIME")) { return "YYYYMMDDHHMMSS"; }

		return null;
	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		// 0-4对应FORM中的5个输入框
		// null表示该需要用户输入,不为null用户不输入
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
				grpTextStr[4] = "";
				break;
			case PaymentBank.XYKTH:// 隔日退货
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "";
				break;
			case PaymentBank.XYKQD:// 交易签到
				grpTextStr[0] = null;
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
				grpTextStr[4] = "按回车键开始交易结帐";
				break;
			case PaymentBank.XYKYE:// 余额查询
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始余额查询";//银联不支持该功能
				break;
			case PaymentBank.XYKCD:// 签购单重打
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "签购单重打";
				break;
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if (type != PaymentBank.XYKQD && type != PaymentBank.XYKCD && type != PaymentBank.XYKJZ && type != PaymentBank.XYKXF && type != PaymentBank.XYKCX && type != PaymentBank.XYKTH && type != PaymentBank.XYKYE)
			{
				errmsg = "银联接口不支持该交易";
				new MessageBox(errmsg);
				return false;
			}

			// 先删除上次交易数据文件
			if (PathFile.fileExist(getbankfunc() + "request.txt"))
			{
				PathFile.deletePath(getbankfunc() + "request.txt");

				if (PathFile.fileExist(getbankfunc() + "request.txt"))
				{
					errmsg = "交易请求文件无法删除,请重试";
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist(getbankfunc() + "result.txt"))
			{
				PathFile.deletePath(getbankfunc() + "result.txt");

				if (PathFile.fileExist(getbankfunc() + "result.txt"))
				{
					errmsg = "交易应答文件无法删除,请重试";
					new MessageBox(errmsg);
					return false;
				}
			}

			// 选择卡类型
			// 17 SMART卡
			// 18 巍康卡
			// 19 畅购卡
			// 20 OK积点卡
			// 21 OK会员卡
			// 签到的结算不传卡类型

			// 写入请求数据
			if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, cardtype)) { return false; }

			// 调用接口模块
			if (PathFile.fileExist(getbankfunc() + "javaposbank.exe"))
			{
				CommonMethod.waitForExec(getbankfunc() + "javaposbank.exe SHSD");
			}
			else
			{
				new MessageBox("找不到金卡工程模块 javaposbank.exe");
				XYKSetError("XX", "找不到金卡工程模块 javaposbank.exe");
				return false;
			}

			// 读取应答数据
			if (!XYKReadResult()) { return false; }

			// 检查交易是否成功
			XYKCheckRetCode();

			// 打印签购单
			if (XYKNeedPrintDoc())
			{
				XYKPrintDoc();
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);

			return false;
		}
	}

	public boolean XYKWriteRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector cardtype)
	{
		StringBuffer sbstr = null;

		try
		{
			sbstr = new StringBuffer();
			String cardtypecode = "";

			if (type == PaymentBank.XYKCD)
			{
				sbstr.append("1" + ",");
				sbstr.append("B0" + ",");
				sbstr.append("  " + ",");

				if (cardtype != null && cardtype.size() > 0 && cardtype.elementAt(0).toString().trim().length() >= 2)
				{
					cardtypecode = cardtype.elementAt(0).toString();
					sbstr.append(cardtypecode.substring(cardtypecode.length() - 2) + ",");
				}
				sbstr.append(Convert.increaseChar(GlobalInfo.syjDef.syjh, '0', 6) + ",");
				sbstr.append(Convert.increaseLong(GlobalInfo.syjStatus.fphm, 6));
			}
			else
			{
				// 组织请求数据
				// 操作类型,交易类型,卡类型,收银机编号,操作员,金额,收银流水号,原交易流水号,预留字段
				sbstr.append("0" + ",");
				sbstr.append("A0" + ",");
				if (type == PaymentBank.XYKXF)
					sbstr.append("30" + ",");
				else if (type == PaymentBank.XYKCX)
					sbstr.append("40" + ",");
				else if (type == PaymentBank.XYKTH)
					sbstr.append("50" + ",");
				else if (type == PaymentBank.XYKQD)
					sbstr.append("91" + ",");
				else if (type == PaymentBank.XYKJZ)
					sbstr.append("92" + ",");
				else if (type == PaymentBank.XYKYE)
					sbstr.append("80" + ",");//wangyong add by 2013.9.12 for ZMSY
				else
				{
					throw new Exception("无效的交易类型!");
				}

				if (cardtype != null && cardtype.size() > 0 && cardtype.elementAt(0).toString().trim().length() >= 2)
				{
					cardtypecode = cardtype.elementAt(0).toString();
					sbstr.append(cardtypecode.substring(cardtypecode.length() - 2) + ",");
				}
				else
				{
					sbstr.append("  " + ",");
				}

				sbstr.append(Convert.increaseChar(GlobalInfo.syjDef.syjh, '0', 6) + ",");
				sbstr.append(Convert.increaseChar(GlobalInfo.posLogin.gh, '0', 6) + ",");
				sbstr.append(Convert.increaseCharForward(String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1)), '0', 12) + ",");
				sbstr.append(Convert.increaseLong(GlobalInfo.syjStatus.fphm, 6) + ",");
				// 撤销
				if (type == PaymentBank.XYKCX)
				{
					sbstr.append(Convert.increaseCharForward(oldseqno, '0', 6) + ",");
				}
				else
				{
					sbstr.append("000000" + ",");
				}

				// 退货
				if (cardtypecode.length() >= 2 && cardtypecode.substring(cardtypecode.length() - 2).equals("05"))
				{
					if (type == PaymentBank.XYKTH)
					{
						sbstr.append(Convert.increaseChar(oldseqno, 6) + ",");
						sbstr.append(Convert.increaseChar(oldauthno, 15));
					}
					else
					{
						sbstr.append("000000");
						sbstr.append("," + track2);
					}
				}
				else
				{
					if (type == PaymentBank.XYKTH)
					{
						sbstr.append(Convert.increaseChar(oldseqno, 6) + ",");
						sbstr.append(Convert.increaseChar(olddate, 10));
					}
				}
			}

			// 写入请求数据
			if (!rtf.writeFile(getbankfunc() + "request.txt", sbstr.toString()))
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
		BufferedReader br = null;

		try
		{
			if (!PathFile.fileExist(getbankfunc() + "result.txt") || ((br = CommonMethod.readFileGBK(getbankfunc() + "result.txt")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}

			String line = br.readLine();

			if (line == null || line.length() <= 0)
			{
				new MessageBox("没有读到银联返回码!", null, false);
				return false;
			}

			try
			{
				String[] strs = line.split(",");

				if (strs.length >= 4)
					bld.retcode = strs[3];

				if (strs.length >= 5)
					bld.retmsg = strs[4];

				if (strs.length >= 13)
					bld.cardno = strs[12];

				if (strs.length >= 15)
					bld.bankinfo = strs[14];

				if (strs.length >= 19)
					bld.authno = strs[18];

				if (strs.length >= 22 && strs[21] != null && strs[21].trim().length() > 0)
					bld.trace = strs[21].trim().length() > 0 ? Long.parseLong(strs[21].trim()) : 0;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				new MessageBox(ex.getMessage());
			}
			finally
			{
				if (br != null)
				{
					br.close();
					br = null;
				}
			}
		}
		catch (Exception ex)
		{
			XYKSetError("XX", "读取应答XX:" + ex.getMessage());
			new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
			ex.printStackTrace();

			return false;
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
					br = null;
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		return true;
	}
}
