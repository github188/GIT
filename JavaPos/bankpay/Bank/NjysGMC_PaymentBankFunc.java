package bankpay.Bank;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.SalePayDef;

/**
 * gmc（南京银石）
 * @author maxun
 *
 */
public class NjysGMC_PaymentBankFunc extends PaymentBankFunc
{

	private char iscardtype = 'N';//是否内卡
	private SalePayDef salePay;

	public String getbankfunc()
	{
		return ConfigClass.BankPath;
	}

	public String[] getFuncItem()
	{
		String[] func = new String[8];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "其他";
		func[5] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[6] = "[" + PaymentBank.XYKCD + "]" + "重印";
		func[7] = "[" + PaymentBank.XKQT1 + "]" + "交易一览";
		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		// 0-4对应FORM中的5个输入框
		// null表示该不用输入
		switch (type)
		{
			case PaymentBank.XYKXF: // 消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKCX: // 消费撤销
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKYE: // 余额查询
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "余额查询";

				break;

			case PaymentBank.XYKJZ: // 结账
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "结账";

				break;

			case PaymentBank.XYKTH: // 隔日退货
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "金额";

				break;

			case PaymentBank.XYKCD: // 签购单重打
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打上笔签购单";
				break;
			case PaymentBank.XYKQD:// 交易签到
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";
				break;

			case PaymentBank.XKQT1: // 交易一览
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易一览";

				break;
		}

		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		// 0-4对应FORM中的5个输入框
		// null表示该需要用户输入,不为null用户不输入
		switch (type)
		{
			case PaymentBank.XYKXF: // 消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;

			case PaymentBank.XYKCX: // 消费撤销
				grpTextStr[0] = "";
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;

			case PaymentBank.XYKTH: // 退货
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;

			case PaymentBank.XYKYE: // 余额查询
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始余额查询";

				break;

			case PaymentBank.XYKQD: // 交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易签到";

				break;

			case PaymentBank.XYKJZ: // 内卡结账
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始结账";

				break;

			case PaymentBank.XYKCD: // 签购单重打
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "开始签购单重打";

				break;

			case PaymentBank.XKQT1: // 交易一览
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键交易一览";

				break;
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			PosLog.getLog(this.getClass().getSimpleName()).info("XYKExecute start(" + getbankfunc() + "):");

			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && (type != PaymentBank.XYKTH) && (type != PaymentBank.XYKQD)
					&& (type != PaymentBank.XYKJZ) && (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD) && (type != PaymentBank.XKQT1))
			{
				errmsg = "银联接口不支持该交易";
				PosLog.getLog(this.getClass().getSimpleName()).info("XYKExecute error:" + errmsg + type);
				new MessageBox(errmsg);

				return false;
			}

			// 先删除上次交易数据文件
			if (PathFile.fileExist(getbankfunc() + "request.txt"))
			{
				PathFile.deletePath(getbankfunc() + "request.txt");
				if (PathFile.fileExist(getbankfunc() + "request.txt"))
				{
					errmsg = "交易请求文件" + getbankfunc() + "request.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}

			}

			if (PathFile.fileExist(getbankfunc() + "response.txt"))
			{
				PathFile.deletePath(getbankfunc() + "response.txt");
				if (PathFile.fileExist(getbankfunc() + "response.txt"))
				{
					errmsg = "交易请求文件" + getbankfunc() + "response.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}

			}

			if (PathFile.fileExist(getbankfunc() + "receipt.txt"))
			{
				PathFile.deletePath(getbankfunc() + "receipt.txt");
				if (PathFile.fileExist(getbankfunc() + "receipt.txt"))
				{
					errmsg = "交易请求文件" + getbankfunc() + "receipt.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}

			}

			if (memo.size() >= 3)
			{
				//				saleBs = (SaleBS) memo.elementAt(2);
				salePay = (SalePayDef) memo.elementAt(3);
			}

			// 写入请求数据
			XYKgetRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);
			/*
			 // 调用接口模块
			 if (PathFile.fileExist(getbankfunc() + "javaposbank.exe")) 
			 {
				 CommonMethod.waitForExec(getbankfunc() + "javaposbank.exe NJYSMZDABC");
			 } 
			 else {
				 PosLog.getLog(this.getClass().getSimpleName()).info("XYKExecute :" + "找不到金卡工程模块 " + getbankfunc() + "javaposbank.exe");
				 new MessageBox("找不到金卡工程模块 " + getbankfunc() + "javaposbank.exe");
				 XYKSetError("XX", "找不到金卡工程模块 " + getbankfunc() + "javaposbank.exe");
				 return false;
			 }*/
			 
				// 调用接口模块
				if (PathFile.fileExist(getbankfunc() + "\\gmc.exe"))
				{
					CommonMethod.waitForExec(getbankfunc() + "\\gmc.exe");
				}
				else
				{
					new MessageBox("找不到金卡工程模块 gmc.exe");
					XYKSetError("XX", "找不到金卡工程模块 gmc.exe");
					return false;
				}
			 
			PosLog.getLog(this.getClass().getSimpleName()).info("XYKExecute end.");
			
			//			 读取应答数据
			if (!XYKReadResult1(type)) { return false; }
			
			if (!XYKCheckRetCode()) { return false; }

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			XYKSetError("XX", "金卡异常XX:" + ex.getMessage());
			PosLog.getLog(this.getClass().getSimpleName()).info("XYKExecute ex:" + ex.toString());
			PosLog.getLog(this.getClass().getSimpleName()).error("XYKExecute ex:" + ex.toString());
			new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);

			return false;
		}
	}

	public String XYKgetRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			String line = "";

			String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode, ' ', 10);
			String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 10);
			char type1 = ' ';

			String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));
			jestr = Convert.increaseCharForward(jestr, '0', 12);

			// 根据不同的类型生成文本结构
			switch (type)
			{
				case PaymentBank.XYKXF:
					type1 = 'C';
					break;

				case PaymentBank.XYKCX:
					type1 = 'D';
					break;
				case PaymentBank.XYKTH:
					type1 = 'R';
					break;
				case PaymentBank.XYKYE:
					type1 = 'I';
					break;
				default:
					type1 = '0';
					break;
			}

			line = syjh + syyh + type1 + jestr + "      ";
			// System.out.println(line);

			PrintWriter pw = null;

			try
			{
				pw = CommonMethod.writeFile(getbankfunc() + "request.txt");

				if (pw != null)
				{
					pw.println(line);
					pw.flush();
				}
			}
			finally
			{
				if (pw != null)
				{
					pw.close();
				}
			}

			PosLog.getLog(this.getClass().getSimpleName()).info("XYKgetRequest line=[" + line + "].请求数据写入完成。");
			return line;
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).info("XYKgetRequest ex:" + ex.toString());
			PosLog.getLog(this.getClass().getSimpleName()).error("XYKgetRequest ex:" + ex.toString());
			new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
			ex.printStackTrace();

			return null;
		}
	}

	public boolean XYKReadResult1(int type)
	{
		try
		{

			String result = null;
			BufferedReader br = null;
			try
			{
				if (!PathFile.fileExist(getbankfunc() + "response.txt") || ((br = CommonMethod.readFile(getbankfunc() + "response.txt")) == null))
				{
					XYKSetError("XX", "读取金卡工程应答数据失败!");

					PosLog.getLog(this.getClass().getSimpleName()).info("XYKExecute 读取金卡工程应答数据失败!");
					new MessageBox("读取金卡工程应答数据失败!", null, false);

					return false;
				}
				result = br.readLine();
				if(result==null) return false;
			}
			catch (Exception ex)
			{
				XYKSetError("XX", "金卡异常XX:" + ex.getMessage());
				PosLog.getLog(this.getClass().getSimpleName()).info("XYKExecute ex1:" + ex.toString());
				PosLog.getLog(this.getClass().getSimpleName()).error("XYKExecute ex1:" + ex.toString());
				new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);

				return false;
			}
			finally
			{
				if (br != null)
				{
					br.close();
				}
			}

			PosLog.getLog(this.getClass().getSimpleName()).info("XYKReadResult1 result=[" + result + "].");
			// System.out.println(Convert.countLength(line));
			bld.retcode = result.substring(0, 2);

			if (type != PaymentBank.XYKXF && type != PaymentBank.XYKCX && type != PaymentBank.XYKTH)
			{
				bld.retcode = "00";
				bld.retmsg = "银联交易成功";
				return true;
			}

			if (!bld.retcode.equals("00"))
			{
				PosLog.getLog(this.getClass().getSimpleName()).info("本笔交易未成功!");
				// bld.retmsg = getErrorInfo(bld.retcode);
				return false;
			}
			else
			{
				bld.retmsg = "交易成功";

			}

			bld.cardno = Convert.newSubString(result, 2, 21);
			bld.typename = Convert.newSubString(result, 21, 22);
			String je = Convert.newSubString(result, 22, 34);
			double j = Double.parseDouble(je);
			j = ManipulatePrecision.mul(j, 0.01);
			bld.je = j;

			String trace = Convert.newSubString(result, 34, 40);
			if (trace == null)
			{
				bld.trace = Long.parseLong("000000");
			}
			else
			{
				bld.trace = Long.parseLong(Convert.newSubString(result, 34, 40));
			}

			if (result.length() > 40)
			{
				bld.bankinfo = XYKReadBankName(Convert.newSubString(result, 40, 43));

				iscardtype = Convert.newSubString(result, 43, 44).charAt(0);

				if (salePay != null)
				{
					salePay.str2 = bld.bankinfo + "," + iscardtype;//记录所属银行+是否内卡（0=内卡；1=外卡）
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).info("XYKReadResult1Exception ex:" + ex.toString());
			PosLog.getLog(this.getClass().getSimpleName()).error("XYKReadResult1Exception ex:" + ex.toString());
			XYKSetError("XX", "读取应答XX:" + ex.getMessage());
			new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
			ex.printStackTrace();

			return false;
		}
	}

	public boolean XYKNeedPrintDoc()
	{
		if (!XYKCheckRetCode()) { return false; }

		int type = Integer.parseInt(bld.type.trim());

		// 消费，消费撤销，重打签购单
		if ((type == PaymentBank.XYKXF) || (type == PaymentBank.XYKCX) || (type == PaymentBank.XYKTH) || (type == PaymentBank.XYKCD)
				|| (type == PaymentBank.XYKJZ) || (type == PaymentBank.XYKYE))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean XYKCheckRetCode()
	{
		if (bld.retcode.trim().equals("00"))
		{
			bld.retbz = 'Y';
			bld.retmsg = "金卡工程调用成功";

			return true;
		}
		else
		{
			bld.retbz = 'N';

			return false;
		}
	}

	public void XYKPrintDoc()
	{
		if (1 == 1) return;//要求不打印

		ProgressBox pb = null;

		try
		{
			String printName = getbankfunc() + "reprint.txt";
			if (!PathFile.fileExist(printName)) { return; }

			pb = new ProgressBox();
			pb.setText("正在打印银联签购单,请等待...");

			for (int i = 0; i < 1; i++)
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

					//
					String line = null;

					while ((line = br.readLine()) != null)
					{
						if (line.trim().equals("CUTPAPER"))
						{
							Printer.getDefault().cutPaper_Normal();
							continue;
						}

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

				XYKPrintDoc_End();

			}

			PathFile.deletePath(printName);
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

	public String XYKReadBankName(String bankid)
	{
		String line = "";

		try
		{
			if (!PathFile.fileExist(getbankfunc() + "cardbank.ini") || !rtf.loadFile(getbankfunc() + "cardbank.ini"))
			{
				new MessageBox("找不到cardbank.ini", null, false);

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

				if (Convert.toInt(a[0]) == Convert.toInt(bankid.trim())) { return a[1].trim(); }
			}

			rtf.close();

			return "未知银行";
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return bankid;
		}
	}
}
