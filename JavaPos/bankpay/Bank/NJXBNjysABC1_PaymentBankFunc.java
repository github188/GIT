package bankpay.Bank;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

public class NJXBNjysABC1_PaymentBankFunc extends PaymentBankFunc
{
	String path = null;

	public String[] getFuncItem()
	{
		String[] func = new String[7];

		func[0] = "[0]消费";
		func[1] = "[1]消费撤销";
		func[2] = "[2]隔日退货";
		func[3] = "[5]查询余额";
		func[4] = "[4]交易结账";
		func[5] = "[6]重打票据";

		func[6] = "[7]交易一览";
		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		switch (type)
		{
			case 0:
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";

				break;
			case 1:
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";

				break;
			case 5:
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "余额查询";

				break;
			case 4:
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "结账";

				break;
			case 2:
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "退货";

				break;
			case 6:
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打上笔签购单";
				break;
			case 7:
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易一览";
			case 3:
		}

		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		switch (type)
		{
			case 0:
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;
			case 1:
				grpTextStr[0] = "";
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;
			case 2:
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;
			case 5:
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始余额查询";

				break;
			case 4:
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始结账";

				break;
			case 6:
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "开始签购单重打";

				break;
			case 7:
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易一览交易";
			case 3:
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != 0) && (type != 1) && (type != 2) && (type != 3) && (type != 4) && (type != 5) && (type != 6) && (type != 7))
			{
				this.errmsg = "银联接口不支持该交易";
				new MessageBox(this.errmsg);

				return false;
			}

			this.path = getBankPath(this.paycode);

			// 先删除上次交易数据文件
			if (PathFile.fileExist(this.path + "\\request.txt"))
			{
				PathFile.deletePath(this.path + "\\request.txt");

				if (PathFile.fileExist(this.path + "\\request.txt"))
				{
					errmsg = "交易请求文件request.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist(this.path + "\\result.txt"))
			{
				PathFile.deletePath(this.path + "\\result.txt");

				if (PathFile.fileExist(this.path + "\\result.txt"))
				{
					errmsg = "交易请求文件result.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist(this.path + "\\toprint.txt"))
			{
				PathFile.deletePath(this.path + "\\toprint.txt");

				if (PathFile.fileExist(this.path + "\\toprint.txt"))
				{
					errmsg = "打印文件print.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			XYKgetRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);

			String request = null;

			if (PathFile.fileExist(this.path + "\\javaposbank.exe"))
			{
				CommonMethod.waitForExec(this.path + "\\javaposbank.exe NJYSMZDABC", "javaposbank.exe");
			}
			else
			{
				new MessageBox("找不到金卡工程模块 javaposbank.exe");
				XYKSetError("XX", "找不到金卡工程模块 javaposbank.exe");
				return false;
			}

			BufferedReader br = null;
			if ((!PathFile.fileExist(this.path + "\\result.txt")) || ((br = CommonMethod.readFile(this.path + "\\result.txt")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}
			request = br.readLine();
			br.close();

			if (!XYKReadResult1(request, type))
				return false;

			XYKCheckRetCode();

			if (XYKNeedPrintDoc(type))
				XYKPrintDoc();

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			XYKSetError("XX", "金卡异常XX:" + ex.getMessage());
			new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);
		}
		return false;
	}

	protected boolean XYKNeedPrintDoc(int type)
	{
		if (type != PaymentBank.XYKXF && type != PaymentBank.XYKTH && type != PaymentBank.XYKCX && type != PaymentBank.XYKCD && type != PaymentBank.XYKJZ && type != PaymentBank.XKQT1) { return false; }
		return true;
	}

	public void XYKPrintDoc()
	{
		ProgressBox pb = null;

		try
		{
			String printName = path + "\\toprint.txt";

			if (!PathFile.fileExist(printName))
			{
				new MessageBox("找不到签购单打印文件!");
				return;
			}

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

					String line = null;
					while ((line = br.readLine()) != null)
					{
						if (line.trim().equals("CUTPAPER"))
						{
							Printer.getDefault().cutPaper_Journal();
							continue;
						}

						XYKPrintDoc_Print(line);
					}

				}
				catch (Exception ex)
				{
					new MessageBox(ex.getMessage());
				}
				finally
				{
					if (br != null)
						br.close();
					br = null;
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

	public boolean XYKCheckRetCode()
	{
		if (this.bld.retcode.trim().equals("00"))
		{
			this.bld.retbz = 'Y';
			this.bld.retmsg = "金卡工程调用成功";

			return true;
		}

		this.bld.retbz = 'N';

		return false;
	}

	public boolean checkBankSucceed()
	{
		if (this.bld.retbz == 'N') { return false; }

		this.errmsg = "交易成功";

		String path = "c:\\gmc\\answer.txt";
		if (PathFile.fileExist(path))
		{
			PathFile.deletePath(path);
			if (PathFile.fileExist(path))
			{
				new MessageBox(path + "已经被其他程序锁住，请联系电脑部解决");
			}
		}
		return true;
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

			switch (type)
			{
				case 0:
					type1 = 'C';
					break;
				case 1:
					type1 = 'D';
					break;
				case 2:
					type1 = 'R';
					break;
				case 5:
					type1 = 'I';
					break;
				case 6:
					type1 = '0';
					break;
				case 4:
					type1 = '0';
					break;
				case 7:
					type1 = '0';
					break;
				case 3:
				default:
					type1 = '0';
			}

			line = syjh + syyh + type1 + jestr;

			PrintWriter pw = null;
			try
			{
				pw = CommonMethod.writeFile("C:\\gmc\\request.txt");

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

			return line;
		}
		catch (Exception ex)
		{
			new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
			ex.printStackTrace();
		}
		return null;
	}

	public boolean XYKReadResult1(String result, int type)
	{
		try
		{
			String line = result;

			result = result.substring(2);
			this.bld.retcode = line.substring(0, 2);

			if (!this.bld.retcode.equals("00")) { return false; }

			this.bld.retmsg = "交易成功";

			if ((type != 0) && (type != 1) && (type != 2))
			{
				this.bld.retmsg = "银联交易成功";
				return true;
			}

			this.bld.cardno = Convert.newSubString(line, 2, 21);
			this.bld.crc = Convert.newSubString(line, 21, 22);
			String je = Convert.newSubString(line, 22, 34);
			double j = Convert.toDouble(je);
			j = ManipulatePrecision.mul(j, 0.01D);
			this.bld.je = j;

			return true;
		}
		catch (Exception ex)
		{
			XYKSetError("XX", "读取应答XX:" + ex.getMessage());
			new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
			ex.printStackTrace();
		}
		return false;
	}

	public String XYKReadBankName(String bankid)
	{
		String line = "";
		try
		{
			if (bankid.charAt(0) == '4')
				return "商行";

			if ((!PathFile.fileExist(GlobalVar.ConfigPath + File.separator + "BankInfo.ini")) || (!this.rtf.loadFile(GlobalVar.ConfigPath + File.separator + "BankInfo.ini")))
			{
				new MessageBox("找不到BankInfo.ini", null, false);

				return bankid;
			}

			while ((line = this.rtf.nextRecord()) != null)
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

				if (Convert.toInt(a[0]) == Convert.toInt(bankid.trim()))
					return a[1].trim();
			}

			this.rtf.close();

			return "未知银行";
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return bankid;
	}
}
