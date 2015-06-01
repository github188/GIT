package bankpay.Bank;

//晶城
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
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

public class NjysJC_PaymentBankFunc extends PaymentBankFunc
{
	public String[] getFuncItem()
	{
		String[] func = new String[8];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
		func[5] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		func[6] = "[" + PaymentBank.XKQT1 + "]" + "功能菜单";
		func[7] = "[" + PaymentBank.XYKCD + "]" + "签购单重打印";
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
				grpLabelStr[4] = "金额";

				break;

			case PaymentBank.XYKCD: //签购单重打
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打印签购单";
				break;
				
			case PaymentBank.XKQT1: //消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "功能菜单";

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
				grpTextStr[0] = null;
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
				
			case PaymentBank.XKQT1: //签购单重打
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "功能菜单";

				break;
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKTH) && (type != PaymentBank.XYKCX) && (type != PaymentBank.XYKQD)
					&& (type != PaymentBank.XYKJZ) && (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD) && (type != PaymentBank.XKQT1)
					&& (type != PaymentBank.XKQT2) && (type != PaymentBank.XKQT3))
			{
				errmsg = "银联接口不支持该交易";
				new MessageBox(errmsg);

				return false;
			}

			//	先删除上次交易数据文件
			if (PathFile.fileExist("C:\\gmc\\request.txt"))
			{
				PathFile.deletePath("C:\\gmc\\request.txt");

				if (PathFile.fileExist("C:\\gmc\\request.txt"))
				{
					errmsg = "交易请求文件request.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist("C:\\gmc\\result.txt"))
			{
				PathFile.deletePath("C:\\gmc\\result.txt");

				if (PathFile.fileExist("C:\\gmc\\result.txt"))
				{
					errmsg = "交易结果文件result.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist("C:\\gmc\\toprint.txt"))
			{
				PathFile.deletePath("C:\\gmc\\toprint.txt");

				if (PathFile.fileExist("C:\\gmc\\toprint.txt"))
				{
					errmsg = "交易结果文件result.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			// 写入请求数据
			String line = XYKgetRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);

			PrintWriter pw = CommonMethod.writeFile("C:\\gmc\\request.txt");

			if (pw != null)
			{
				pw.println(line);
				pw.flush();
				pw.close();
			}

			// 调用接口模块
			if (PathFile.fileExist("C:\\gmc\\javaposbank.exe"))
			{
				CommonMethod.waitForExec("C:\\gmc\\javaposbank.exe XMYS", "javaposbank.exe");
			}
			else
			{
				new MessageBox("找不到金卡工程模块 javaposbank.exe");
				XYKSetError("XX", "找不到金卡工程模块 javaposbank.exe");
				return false;
			}
			
			// 读取应答数据
			if (type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKTH)
			{
				if (!XYKReadResult(type)) return false;
			}
			else
			{
				bld.retcode = "00";
			}
			// 检查交易是否成功
			if (!XYKCheckRetCode()) return false;

			//无论是否成功，都检查打印
			if (XYKNeedPrintDoc(type))
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
			String memo1 = "      ";

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
					type1 = 'I';
					break;
				default:
					type1 = '0';
					break;
			}

			line = syjh + syyh + type1 + jestr + memo1;
			return line;
		}
		catch (Exception ex)
		{
			new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
			ex.printStackTrace();

			return null;
		}
	}

	public boolean XYKReadResult(int type)
	{
		BufferedReader br = null;
		try
		{
			if (!PathFile.fileExist("C:\\gmc\\result.txt") || ((br = CommonMethod.readFileGBK("C:\\gmc\\result.txt")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}
			String line = br.readLine();

			if (line.indexOf(",") > -1)
			{
				line = line.substring(line.indexOf(",") + 1, line.length());
			}

			bld.retcode = line.substring(0, 2);
			
			if (!bld.retcode.equals("00"))
			{
				return false;
			}
			
			bld.cardno = Convert.newSubString(line, 2, 21);
			String je = Convert.newSubString(line, 22, 34);
			double j = Double.parseDouble(je);
			j = ManipulatePrecision.mul(j, 0.01);
			bld.je = j;
			bld.trace = Long.parseLong(Convert.newSubString(line, 34, 40));
			if (line.length() >= 43)
			{
				bld.bankinfo = Convert.newSubString(line, 40, 43);
				String bankname = XYKReadBankName(bld.bankinfo);
				bld.bankinfo = bld.bankinfo + bankname;
//				bld.bankinfo = new String (bld.bankinfo.getBytes("gbk"),"UTF-8");
			}
			errmsg = bld.retmsg;
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

					if (PathFile.fileExist("C:\\gmc\\request.txt"))
					{
						PathFile.deletePath("C:\\gmc\\request.txt");
					}

					if (PathFile.fileExist("C:\\gmc\\result.txt"))
					{
						PathFile.deletePath("C:\\gmc\\result.txt");
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	protected boolean XYKNeedPrintDoc(int type)
	{
		if (type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKTH || type == PaymentBank.XYKCD || type == PaymentBank.XYKJZ)
		{
			return true;
		}
		return false;
	}

	public void XYKPrintDoc()
	{
		ProgressBox pb = null;
		String printName = "";
		printName = "C:\\gmc\\toprint.txt";	
		
		try
		{
			if (!PathFile.fileExist(printName))
			{
				new MessageBox("找不到签购单打印文件!");
				return;
			}

			pb = new ProgressBox();
			pb.setText("正在打印银联签购单,请等待...");

			for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++)
			{
				BufferedReader br = null;

				//
				Printer.getDefault().startPrint_Slip();

				try
				{
					//由于发现在windows环境下,用GBK读取文件会产生BUG,改为GB2310
					br = CommonMethod.readFileGB2312(printName);

					if (br == null)
					{
						new MessageBox("打开签购单打印文件失败!");

						return;
					}

					//
					String line = null;

					while ((line = br.readLine()) != null)
					{
						if (line.trim().equals("CUTPAPER")) {
							Printer.getDefault().printLine_Slip("\n");
							Printer.getDefault().printLine_Slip("\n");
							Printer.getDefault().printLine_Slip("\n");
							Printer.getDefault().printLine_Slip("\n");
							new MessageBox("请手工撕下银联签购单");
							continue;
						}
						Printer.getDefault().printLine_Slip(line + "\n");
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

				// 切纸
				Printer.getDefault().cutPaper_Slip();
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
}
