package bankpay.Bank;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;

/**
 * 中免三亚 建行接口2(银石)
 * @author yw
 * 2013.12.17
 */
public class SnYlZMJH_PaymentBankFunc extends NJXBNjysABC1_PaymentBankFunc
{

	public String getBankPath(String paycode)
	{
		//接口路径
		return "C:\\CCB2";
	}
	

	public String[] getFuncItem()
	{
		String[] func = new String[7+1];

		func[0] = "[0]消费";
		func[1] = "[1]消费撤销";
		func[2] = "[2]隔日退货";
		func[3] = "[5]查询余额";
		func[4] = "[4]交易结账";
		func[5] = "[6]重打票据";

		func[6] = "[7]交易一览";
		func[7] = "[3]签到";
		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		if (type==PaymentBank.XYKQD)
		{
			grpLabelStr[0] = null;
			grpLabelStr[1] = null;
			grpLabelStr[2] = null;
			grpLabelStr[3] = null;
			grpLabelStr[4] = "签到";
			return true;
		}
		else
		{
			return super.getFuncLabel(type, grpLabelStr);
		}

	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		if (type==PaymentBank.XYKQD)
		{
			grpTextStr[0] = null;
			grpTextStr[1] = null;
			grpTextStr[2] = null;
			grpTextStr[3] = null;
			grpTextStr[4] = "按回车键开始签到";
			return true;
		}
		else
		{
			return super.getFuncText(type, grpTextStr);
		}	

	}
	
	protected boolean XYKNeedPrintDoc(int type)
	{
		//不打印	
		return false;
	}
	
	public void XYKPrintDoc()
	{
		//不打印
	}

	public boolean checkBankSucceed()
	{
		if (this.bld.retbz == 'N') { return false; }

		this.errmsg = "交易成功";

		String path1 = path + "\\answer.txt";
		if (PathFile.fileExist(path1))
		{
			PathFile.deletePath(path1);
			if (PathFile.fileExist(path1))
			{
				new MessageBox(path1 + "已经被其他程序锁住，请联系电脑部解决");
			}
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

			/*if (PathFile.fileExist(this.path + "\\toprint.txt"))
			{
				PathFile.deletePath(this.path + "\\toprint.txt");

				if (PathFile.fileExist(this.path + "\\toprint.txt"))
				{
					errmsg = "打印文件print.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}*/

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
			try
			{
				if ((!PathFile.fileExist(this.path + "\\result.txt")) || ((br = CommonMethod.readFile(this.path + "\\result.txt")) == null))
				{
					XYKSetError("XX", "读取金卡工程应答数据失败!");
					new MessageBox("读取金卡工程应答数据失败!", null, false);

					return false;
				}
				request = br.readLine();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if (br!=null) br.close();
			}
					

			if (!XYKReadResult1(request, type))
				return false;

			XYKCheckRetCode();

			/*if (XYKNeedPrintDoc(type))
				XYKPrintDoc();*/

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
				case 3://签到
					type1 = 'N';
					break;
				default:
					type1 = '0';
			}

			line = syjh + syyh + type1 + jestr;

			PrintWriter pw = null;
			try
			{
				pw = CommonMethod.writeFile(path + "\\request.txt");

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

        	PosLog.getLog(this.getClass().getSimpleName()).info("XYKReadResult_JH2 result=[" + result + "].");
			result = result.substring(2);
			this.bld.retcode = line.substring(0, 2);

			if (!this.bld.retcode.equals("00")) { return false; }

			this.bld.retmsg = "交易成功";

			if ((type != 0) && (type != 1) && (type != 2))
			{
				this.bld.retmsg = "银联交易成功";
				return true;
			}

			this.bld.cardno = Convert.newSubString(line, 2, 21).trim();//卡号
			//this.bld.crc = Convert.newSubString(line, 21, 22);//交易类型标志
			String je = Convert.newSubString(line, 22, 34);//刷卡总金额
			double j = Convert.toDouble(je);
			j = ManipulatePrecision.mul(j, 0.01D);
			this.bld.je = j;
			
			//add wangyong by 2013.12.19
			this.bld.trace=Convert.toLong(Convert.newSubString(line, 34, 40));//流水号
			//this.bld.bankinfo=Convert.newSubString(line, 40, 43);//发卡银行代码*
			
			bld.authno = Convert.newSubString(line, 43, 43+12).trim();//参考号
			String strSQH = Convert.newSubString(line, 55, 55+6).trim();//授权号
	        String batchNo = Convert.newSubString(line, 61, 61+6).trim();//批次号
	        String strDate = Convert.newSubString(line, 67, 67+4).trim();//交易日期
		    String strInfo=Convert.newSubString(line, 71, 71+24).trim();//交易信息,旧接口里没有,则为空
		    strInfo = ManipulateStr.getStrToHex(strInfo);//由于特殊字符的问题,所以转换成16进制
			
			this.bld.memo2=Convert.newSubString(line, 95, 95+12);//实际刷卡金额=刷卡总金额-折扣金额
			j=Convert.toDouble(Convert.newSubString(line, 107, 107+12));//折扣金额
			this.bld.ylzk=ManipulatePrecision.mul(j, 0.01D);
						

			//原流水号|原参考号|原授权号|原批次号|原交易日期|交易信息
            bld.tempstr = bld.trace + "|" + bld.authno + "|" + strSQH + "|" + batchNo + "|" + strDate + "|" + strInfo;
			PosLog.getLog(this.getClass().getSimpleName()).info("JH_log=[" + bld.tempstr + "].");

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
		//String line = "";
		try
		{
			/*if (bankid.charAt(0) == '4')
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

			return "未知银行";*/
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return bankid;
	}
	
}
