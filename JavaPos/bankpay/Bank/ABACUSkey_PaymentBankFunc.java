package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

//红府项目，增加key参数，确保读取的返回数据是否是本次交易的
public class ABACUSkey_PaymentBankFunc extends PaymentBankFunc
{
	String key = "";

	public String[] getFuncItem()
	{
		String[] func = new String[2];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		// func[0] = "[" + PaymentBank.XYKTH + "]" + "退货";
		func[1] = "[" + PaymentBank.XYKYE + "]" + "余额查询";

		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		switch (type)
		{
			case PaymentBank.XYKXF: // 消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			/*
			 * case PaymentBank.XYKTH: // 退货 grpLabelStr[0] = null; grpLabelStr[1] =
			 * null; grpLabelStr[2] = null; grpLabelStr[3] = null; grpLabelStr[4] =
			 * "交易金额"; break;
			 */
			case PaymentBank.XYKYE: // 余额查询
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "余额查询";
				break;
		}

		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		switch (type)
		{
			case PaymentBank.XYKXF: // 消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			/*
			 * case PaymentBank.XYKTH: // 退货 grpTextStr[0] = null; grpTextStr[1] =
			 * null; grpTextStr[2] = null; grpTextStr[3] = null; grpTextStr[4] =
			 * null; break;
			 */
			case PaymentBank.XYKYE: // 余额查询
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始余额查询";
				break;
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKYE))
			{
				errmsg = "会员证接口不支持该交易";
				new MessageBox(errmsg);

				return false;
			}

			// 先删除上次交易数据文件
			if (PathFile.fileExist("c:\\JavaPOS\\request.txt"))
			{
				PathFile.deletePath("c:\\JavaPOS\\request.txt");

				if (PathFile.fileExist("c:\\JavaPOS\\request.txt"))
				{
					errmsg = "交易请求文件request.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist("c:\\JavaPOS\\response.txt"))
			{
				PathFile.deletePath("c:\\JavaPOS\\response.txt");

				if (PathFile.fileExist("c:\\JavaPOS\\response.txt"))
				{
					errmsg = "交易请求文件result.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			// 写入请求数据
			if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo)) { return false; }

			if (bld.retbz != 'Y')
			{

				// 调用接口模块
				if (PathFile.fileExist("c:\\JavaPOS\\nohookczksal.exe"))
				{
					CommonMethod.waitForExec("c:\\JavaPOS\\nohookczksal.exe");
				}
				else
				{
					new MessageBox("找不到金卡工程模块 nohookczksal.exe");
					XYKSetError("XX", "找不到金卡工程模块 nohookczksal.exe");
					return false;
				}

				// 读取应答数据
				if (!XYKReadResult()) { return false; }

				// 检查交易是否成功
				XYKCheckRetCode();
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean XYKNeedPrintDoc()
	{
		if (!checkBankSucceed()) { return false; }

		return true;
	}

	public boolean XYKCheckRetCode()
	{
		if (bld.type.equals(String.valueOf(PaymentBank.XYKYE))) { return true; }

		if (bld.retcode.trim().equals("00"))
		{
			bld.retbz = 'Y';

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
			errmsg = bld.retmsg;

			return false;
		}
		else
		{
			errmsg = "交易成功";

			return true;
		}
	}

	public boolean XYKWriteRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		String line = "";
		String logLine = "";
		String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));

		String saletype = "";
		try
		{
			switch (type)
			{
				case PaymentBank.XYKXF:
					saletype = "S";
					break;
				case PaymentBank.XYKYE:
					saletype = "Q";
					break;
				/*
				 * case PaymentBank.XYKTH: saletype = "V"; break;
				 */
			}
			ManipulateDateTime mdt = new ManipulateDateTime();
			key = Convert.increaseChar(ConfigClass.CashRegisterCode + mdt.getDateByEmpty() + mdt.getTimeByEmpty(), '0', 20);
			line = "00" + "\0" + Convert.increaseChar("", ' ', 200) + saletype + "\0" + Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 6) + "\0"
					+ Convert.increaseChar(ConfigClass.CashRegisterCode, ' ', 6) + "\0" + Convert.increaseCharForward(jestr, ' ', 12) + "\0"
					+ "000000" + "\0" + key + "\0\0\0\0\0\0\0\0";
			logLine = "响应码:00" + "\0" + "文本信息:" + Convert.increaseChar("", ' ', 200) + "状态:" + saletype + "\0" + "收银员:"
					+ Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 6) + "\0" + "收银台:"
					+ Convert.increaseChar(ConfigClass.CashRegisterCode, ' ', 6) + "\0" + "消费金额:" + Convert.increaseCharForward(jestr, ' ', 12)
					+ "\0" + "备用:000000" + "\0" + "KEY:" + key + "\0\0\0\0\0\0\0\0";
			PrintWriter pw = null;

			try
			{
				pw = CommonMethod.writeFile("c:\\JavaPOS\\request.txt");
				WritePrintDoc(key + "   request:" + logLine);
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
					pw = null;
				}
			}

			// new MessageBox("请拷备request.txt文件!");
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
	}

	public static void main(String[] args)
	{
		System.out.println(Convert.newSubString("1234567890", 0, 2));
	}

	public boolean XYKReadResult()
	{
		if (bld.type.equals(String.valueOf(PaymentBank.XYKYE))) { return true; }

		BufferedReader br = null;

		try
		{
			if (!PathFile.fileExist("c:\\JavaPOS\\response.txt") || ((br = CommonMethod.readFileGBK("c:\\JavaPOS\\response.txt")) == null))
			{
				XYKSetError("XX", "读取会员证工程应答数据失败!");
				new MessageBox("读取会员证应答数据失败!", null, false);

				return false;
			}

			//String line = br.readLine();
			String line = "";
			String temp = null;
			while ((temp = br.readLine()) != null)
			{
				line = line + temp;
			}
			if (line.length() <= 0)
			{
				WritePrintDoc(key + "    response:" + line);
				WritePrintDoc(key + "    response:响应码:" + Convert.newSubString(line, 0, 3).trim() + " 文本信息:"
						+ Convert.newSubString(line, 3, 203).trim() + " 状态:" + Convert.newSubString(line, 203, 205).trim() + " 收银员:"
						+ Convert.newSubString(line, 205, 212).trim() + " 收银台:" + Convert.newSubString(line, 212, 219).trim() + " 商户号:"
						+ Convert.newSubString(line, 219, 224).trim() + " 会员证卡面卡号:" + Convert.newSubString(line, 224, 245).trim() + " 会员证卡主:"
						+ Convert.newSubString(line, 245, 266).trim() + " 会员证卡类别:" + Convert.newSubString(line, 266, -271).trim() + " 会员证消费金额:"
						+ Convert.newSubString(line, 271, 284).trim() + " 会员证当前余额:" + Convert.newSubString(line,  284,297).trim()
						+ " 流水号:" + Convert.newSubString(line, 297,314).trim()
						+ " 备用:" + Convert.newSubString(line,  314,321).trim()
						+ " 交易时间:" + Convert.newSubString(line, 321,336).trim()
						+ " KEY:"+ Convert.newSubString(line, 336).trim());
				return false;
			}
			WritePrintDoc(key + " response:" + line);

			bld.retcode = Convert.newSubString(line, 0, 3).trim();
			if (!bld.retcode.equals("00"))
			{
				bld.retbz = 'N';
				bld.retmsg = Convert.newSubString(line, 3, 203).trim();

				return false;
			}
			if (line.length() >= 356 && !Convert.newSubString(line, 336, 357).trim().equals(key))
			{
				bld.retbz = 'N';
				bld.retmsg = "验证key与请求时不同，交易失败！";
				return false;
			}

			bld.retbz = 'Y';
			bld.retmsg = "会员证支付处理成功";

			// 会员证卡主(卡面卡号)
			// bld.cardno = Convert.newSubString(line,224,245).trim();
			bld.cardno = Convert.newSubString(line, 245, 266).trim();

			// 内卡号
			// bld.tempstr = Convert.newSubString(line,245,266).trim();
			bld.tempstr = Convert.newSubString(line, 224, 245).trim();

			String kje = Convert.newSubString(line, 271, 284).trim();
			bld.je = ManipulatePrecision.doubleConvert(Double.parseDouble(kje) / 100, 2, 1);

			String kye = Convert.newSubString(line, 284, 297).trim();
			bld.kye = ManipulatePrecision.doubleConvert(Double.parseDouble(kye) / 100, 2, 1);

			bld.trace = Long.parseLong(Convert.newSubString(line, 297, 314).trim());

			/*
			 * bld.retcode = "00"; bld.retbz = 'Y'; bld.retmsg = "奥博克支付处理成功";
			 * bld.cardno = "123"; bld.tempStr = "小王"; bld.kye = 90; bld.trace =
			 * 9876;
			 */
			return true;
		}
		catch (Exception ex)
		{
			XYKSetError("XX", "读取应答XX:" + ex.getMessage());
			new MessageBox("读取会员证应答数据异常!" + ex.getMessage(), null, false);
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

					if (PathFile.fileExist("c:\\JavaPOS\\request.txt"))
					{
						PathFile.deletePath("c:\\JavaPOS\\request.txt");
					}
					if (PathFile.fileExist("c:\\JavaPOS\\Lastresponse.txt"))
					{
						PathFile.deletePath("c:\\JavaPOS\\Lastresponse.txt");
					}

					if (PathFile.fileExist("c:\\JavaPOS\\response.txt"))
					{
						PathFile.copyPath("c:\\JavaPOS\\response.txt", "c:\\JavaPOS\\Lastresponse.txt");

						PathFile.deletePath("c:\\JavaPOS\\response.txt");
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void XYKPrintDoc()
	{
		ProgressBox pb = null;

		try
		{
			String printName = "";

			int type = Integer.parseInt(bld.type.trim());

			if (type == PaymentBank.XYKXF)
			{
				if (!PathFile.fileExist("c:\\javapos\\savelist.dat"))
				{
					new MessageBox("找不到签购单打印文件!");

					return;
				}
				else
				{
					printName = "c:\\javapos\\savelist.dat";
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
						if (line.trim().equals("|"))
						{
							break;
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

	private String WritePrintDoc(String strPrintBuffer)
	{
		try
		{
			String date = GlobalInfo.balanceDate.replaceAll("/", "");
			String strDocDir = ConfigClass.LocalDBPath + "//Invoice//" + date;
			String strDocFile = strDocDir + "//BankLog" + ".txt";

			PrintWriter pw = CommonMethod.writeFileAppendGBK(strDocFile);
			pw.println(strPrintBuffer.toString());
			pw.flush();
			pw.close();

			return strDocFile;
		}
		catch (Exception ex)
		{
			return null;
		}
	}

}
