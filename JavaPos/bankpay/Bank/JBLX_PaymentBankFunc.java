package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

public class JBLX_PaymentBankFunc extends PaymentBankFunc
{

	public String[] getFuncItem()
	{
		String[] func = new String[9];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
		func[4] = "[" + PaymentBank.XYKCD + "]" + "重打上笔";
		func[5] = "[" + PaymentBank.XKQT1 + "]" + "重打任意笔";
		func[6] = "[" + PaymentBank.XKQT2 + "]" + "业务菜单";
		func[7] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
		func[8] = "[" + PaymentBank.XYKQD + "]" + "交易签到";

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
			case PaymentBank.XYKCX: // 消费撤销
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKTH:// 隔日退货
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
			case PaymentBank.XYKCD: // 其它交易
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打上笔";
				break;
			case PaymentBank.XKQT1: // 其它交易
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打任意笔";
			case PaymentBank.XKQT2: // 其它交易
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "业务菜单";
				break;
			case PaymentBank.XYKJZ://交易结账
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易结账";
				break;
			case PaymentBank.XYKQD: // 交易签到
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";
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
			case PaymentBank.XYKCX: // 消费撤销
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKTH: // 隔日退货
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
			case PaymentBank.XYKCD: // 重打上一笔
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始重打上笔";
				break;
			case PaymentBank.XKQT1: // 其它交易
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始重打任意笔";
				break;
			case PaymentBank.XKQT2: // 其它交易
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始调用银联业务菜单";
				break;
			case PaymentBank.XYKJZ://交易结账
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易结账";
				break;
				
			case PaymentBank.XYKQD: // 交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易签到";
				break;

		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && (type != PaymentBank.XYKTH) && (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD) && (type != PaymentBank.XKQT1) && (type != PaymentBank.XKQT2)&& (type != PaymentBank.XYKJZ) && (type != PaymentBank.XYKQD))
			{
				errmsg = "银联接口不支持该交易";
				new MessageBox(errmsg);

				return false;
			}

			// 先删除上次交易数据文件
			if (PathFile.fileExist("C:\\JavaPos\\request.txt"))
			{
				PathFile.deletePath("C:\\JavaPos\\request.txt");

				if (PathFile.fileExist("C:\\JavaPos\\request.txt"))
				{
					errmsg = "交易请求文件request.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist("C:\\JavaPos\\result.txt"))
			{
				PathFile.deletePath("C:\\JavaPos\\result.txt");

				if (PathFile.fileExist("C:\\JavaPos\\result.txt"))
				{
					errmsg = "交易请求文件result.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist("C:\\JavaPos\\toprint.txt"))
			{
				PathFile.deletePath("C:\\JavaPos\\toprint.txt");
			}

			// 写入请求数据
			if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo)) { return false; }

			if (bld.retbz != 'Y')
			{
				// 调用接口模块
				if (PathFile.fileExist("C:\\JavaPos\\javaposbank.exe"))
				{
					CommonMethod.waitForExec("C:\\JavaPos\\javaposbank.exe JHYB", "javaposbank.exe");
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
			}

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
			XYKSetError("XX", "金卡异常XX:" + ex.getMessage());
			new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);

			return false;
		}
	}

	public boolean XYKNeedPrintDoc()
	{

		if (!checkBankSucceed()) { return false; }

		int type = Integer.parseInt(bld.type.trim());

		if ((type == PaymentBank.XYKXF) || (type == PaymentBank.XYKCX) || (type == PaymentBank.XYKTH) || (type == PaymentBank.XYKYE) || (type == PaymentBank.XYKCD) || (type == PaymentBank.XKQT1) || (type == PaymentBank.XKQT2)|| (type == PaymentBank.XYKJZ))
		{
			return true;
		}
		else
		{
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
		String type1 = "";
		PrintWriter pw = null;

		try
		{
			switch (type)
			{
				case PaymentBank.XYKXF: // 消费
					type1 = "S01";
					break;
				case PaymentBank.XYKCX: // 消费撤销
					type1 = "V01";
					break;
				case PaymentBank.XYKTH: // 隔日退货
					type1 = "R01";
					break;
				case PaymentBank.XYKYE: // 余额查询
					type1 = "B01";
					break;
				case PaymentBank.XYKCD: // 重印上笔
					type1 = "P01";
					break;
				case PaymentBank.XKQT1: // 重印任意笔
					type1 = "P02";
					break;
				case PaymentBank.XKQT2: // 其它交易
					type1 = "X01";
					break;
				case PaymentBank.XYKJZ: // 交易结帐
					type1 = "ST1";
					break;
				case PaymentBank.XYKQD: // 交易签到
					type1 = "C02";
					break;
			}

			String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));
			jestr = Convert.increaseCharForward(jestr, '0', 12);
			String extrainfo = Convert.increaseChar("", ' ', 18);;
			String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode, ' ', 10);
			String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 6);

			line = type1 + jestr + extrainfo + syjh + syyh;

			try
			{
				pw = CommonMethod.writeFile("C:\\JavaPos\\request.txt");
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

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
			ex.printStackTrace();
			return false;
		}
	}

	public boolean XYKReadResult()
	{
		BufferedReader br = null;

		try
		{
			if (!PathFile.fileExist("C:\\JavaPos\\result.txt") || ((br = CommonMethod.readFileGBK("C:\\JavaPos\\result.txt")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}

			String line = br.readLine();

			if (line == null || line.length() <= 0) { return false; }

			String result[] = line.split(",");
			if (result == null) { return false; }

			if (result.length >= 2)
			{
				bld.retcode = Convert.newSubString(result[1], 0, 2);
				bld.retmsg = Convert.newSubString(result[1], 2, 42).trim();
				bld.cardno = Convert.newSubString(result[1], 57, 76).trim();
				String je = Convert.newSubString(result[1], 76, 88).trim();
				double j = Convert.toDouble(je);
				j = ManipulatePrecision.div(j, 100.00);
				bld.je = j;

			}

			return true;
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

					if (PathFile.fileExist("C:\\JavaPos\\request.txt"))
					{
						PathFile.deletePath("C:\\JavaPos\\request.txt");
					}

					if (PathFile.fileExist("C:\\JavaPos\\result.txt"))
					{
						PathFile.deletePath("C:\\JavaPos\\result.txt");
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

			if (!PathFile.fileExist("C:\\JavaPos\\toprint.txt"))
			{
				if (type == PaymentBank.XKQT1)
					return;

				new MessageBox("签购单文本不存在无法打印!", null, false);
				return;
			}
			else
			{
				printName = "C:\\JavaPos\\toprint.txt";
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
					int num = 0;

					while ((line = br.readLine()) != null)
					{
						if (line.length() <= 0)
						{
							continue;
						}

						if (line.indexOf("?") >= 0)
							continue;

						if (line.trim().equals("CUT"))
						{
							if (num >= 1)
								break;
							
							XYKPrintDoc_End();			
							num = num + 1;
							
							continue;
						}

						XYKPrintDoc_Print(line.trim());
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

			if (PathFile.fileExist("C:\\JavaPos\\toprint.txt"))
			{
				PathFile.deletePath("C:\\JavaPos\\toprint.txt");
			}
		}
	}

}
