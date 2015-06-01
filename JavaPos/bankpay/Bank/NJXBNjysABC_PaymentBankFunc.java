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
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.SalePayDef;

//彭州置信
public class NJXBNjysABC_PaymentBankFunc extends PaymentBankFunc
{
	String shh = null;
	String zdh = null;

	public String[] getFuncItem()
	{
		String[] func = new String[8];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
		func[5] = "[" + PaymentBank.XYKCD + "]" + "重打票据";
		func[6] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[7] = "[" + PaymentBank.XKQT1 + "]" + "交易一览";
		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示该不用输入
		switch (type)
		{
			case PaymentBank.XYKXF: //消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKCX: //消费撤销
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKYE: //余额查询    
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "余额查询";

				break;

			case PaymentBank.XYKJZ: //结账
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "结账";

				break;

			case PaymentBank.XYKTH: //隔日退货   
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "退货";

				break;

			case PaymentBank.XYKCD: //签购单重打
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打上笔签购单";
				break;
			case PaymentBank.XYKQD: //签到
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";
				break;
			case PaymentBank.XKQT1: //交易一览
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
		//0-4对应FORM中的5个输入框
		//null表示该需要用户输入,不为null用户不输入
		switch (type)
		{
			case PaymentBank.XYKXF: //消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;

			case PaymentBank.XYKCX: //消费撤销
				grpTextStr[0] = "";
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;

			case PaymentBank.XYKTH: //退货
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;

			case PaymentBank.XYKYE: //余额查询    
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始余额查询";

				break;

			case PaymentBank.XYKQD: //交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易签到";

				break;

			case PaymentBank.XYKJZ: //内卡结账
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始结账";

				break;

			case PaymentBank.XYKCD: //签购单重打
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "开始签购单重打";

				break;

			case PaymentBank.XKQT1: //交易一览
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易一览交易";

				break;
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && (type != PaymentBank.XYKTH) && (type != PaymentBank.XYKQD)
					&& (type != PaymentBank.XYKJZ) && (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD) && (type != PaymentBank.XKQT1))
			{
				errmsg = "银联接口不支持该交易";
				new MessageBox(errmsg);

				return false;
			}

			// 先删除上次交易数据文件
			if (PathFile.fileExist("c:\\gmc\\toprint.txt"))
			{
				if (PathFile.fileExist("c:\\gmc\\toprint.txt"))
				{
					errmsg = "交易请求文件toprint.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			// 写入请求数据
			XYKgetRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);

			String request = null;
			// 调用接口模块
			if (PathFile.fileExist(ConfigClass.BankPath + "\\javaposbank.exe"))
			{
				CommonMethod.waitForExec(ConfigClass.BankPath + "\\javaposbank.exe NJYSMZDABC", "javaposbank.exe");
			}
			else
			{
				new MessageBox("找不到金卡工程模块 javaposbank.exe");
				XYKSetError("XX", "找不到金卡工程模块 javaposbank.exe");
				return false;
			}

			//String request = null;
			BufferedReader br = null;
			if (!PathFile.fileExist(ConfigClass.BankPath + "\\result.txt")
					|| ((br = CommonMethod.readFile(ConfigClass.BankPath + "\\result.txt")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}
			request = br.readLine();
			br.close();

			// 读取应答数据
			if (!XYKReadResult1(request, type)) { return false; }

			// 检查交易是否成功
			XYKCheckRetCode();

			//无论是否成功，都检查打印
			XYKPrintDoc();

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			XYKSetError("XX", "金卡异常XX:" + ex.getMessage());
			new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);

			return false;
		}

	}

	public boolean XYKNeedPrintDoc()
	{
		return false;
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

	public boolean checkBankSucceed()
	{
		if (bld.retbz == 'N')
		{
			return false;
		}
		else
		{
			errmsg = "交易成功";

			//加入解百特殊金卡工程断电保护
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

			//根据不同的类型生成文本结构
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
					type1 = '0';
					break;
				case PaymentBank.XYKCD:
					type1 = '0';
					break;
				case PaymentBank.XYKQD:
					type1 = '0';
					break;
				case PaymentBank.XYKJZ:
					type1 = '0';
					break;
				case PaymentBank.XKQT1:
					type1 = '0';
					break;
				default:
					type1 = '0';
					break;
			}

			String fphm = Convert.increaseChar(String.valueOf(GlobalInfo.syjStatus.fphm), ' ', 18);

			String cardtype = "0";
			String payName;
			if (memo.size() > 3)
			{
				payName = ((SalePayDef)memo.get(3)).payname;
				if (payName.indexOf("外卡") > -1)
				{
					cardtype = "1";
				}
			}
			
			line = syjh + syyh + type1 + jestr + fphm + cardtype + Convert.increaseChar("", 20);

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

			return null;
		}
	}

	public boolean XYKReadResult1(String result, int type)
	{
		try
		{
			String line = result;

			//去掉前面的前缀
			result = result.substring(2);
			bld.retcode = line.substring(0, 2);

			if (type != PaymentBank.XYKXF && type != PaymentBank.XYKCX && type != PaymentBank.XYKTH)
			{
				bld.retcode = "00";
				bld.retmsg = "银联交易成功";
				return true;
			}

			if (!bld.retcode.equals("00"))
			{
				return false;
			}
			else
			{
				bld.retmsg = "交易成功";

			}

			bld.cardno = Convert.newSubString(line, 2, 21);
			bld.crc = Convert.newSubString(line, 21, 22);
			String je = Convert.newSubString(line, 22, 34);
			double j = Double.parseDouble(je);
			j = ManipulatePrecision.mul(j, 0.01);
			bld.je = j;

			return true;
		}
		catch (Exception ex)
		{
			XYKSetError("XX", "读取应答XX:" + ex.getMessage());
			new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
			ex.printStackTrace();

			return false;
		}
	}

	public String XYKReadBankName(String bankid)
	{
		String line = "";

		try
		{
			if (bankid.charAt(0) == '4') { return "商行"; }

			if (!PathFile.fileExist(GlobalVar.ConfigPath + File.separator + "BankInfo.ini")
					|| !rtf.loadFile(GlobalVar.ConfigPath + File.separator + "BankInfo.ini"))
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
