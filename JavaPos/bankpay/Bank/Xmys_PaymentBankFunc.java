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
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

//厦门银石（厦门奥特莱斯）
public class Xmys_PaymentBankFunc extends PaymentBankFunc
{
	public String bankpath = "";

	public String[] getFuncItem()
	{
		String[] func = new String[7];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
//		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[2] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[3] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
		func[4] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		func[5] = "[" + PaymentBank.XKQT1 + "]" + "功能菜单";
		func[6] = "[" + PaymentBank.XYKCD + "]" + "签购单重打印";
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
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && (type != PaymentBank.XYKQD)
					&& (type != PaymentBank.XYKJZ) && (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD) && (type != PaymentBank.XKQT1)
					&& (type != PaymentBank.XKQT2) && (type != PaymentBank.XKQT3))
			{
				errmsg = "银联接口不支持该交易";
				new MessageBox(errmsg);

				return false;
			}

			// 选择卡类型
			
			String paycode = "";
			if (memo != null && memo.size() > 0)
			{
				paycode = (String) memo.elementAt(0);
			}
			else
			{
				Vector v = new Vector();
				// 查询是否定义了银联付款方式
				for (int k = 0; k < GlobalInfo.payMode.size(); k++)
				{
					PayModeDef mdf = (PayModeDef) GlobalInfo.payMode.elementAt(k);
					if (mdf.isbank == 'Y')
					{
						v.add(new String[] { mdf.code, mdf.name });
					}
				}

				if (v.size() > 1)
				{
					String[] title = { "付款代码", "付款名称" };
					int[] width = { 100, 400 };
					int choice = new MutiSelectForm().open("请选择交易方式", title, width, v);
					if (choice == -1)
					{
						paycode = ((String[]) v.elementAt(0))[0];
					}
					else
					{
						paycode = ((String[]) v.elementAt(choice))[0];
					}
				}
				else
				{
					paycode = ((String[]) v.elementAt(0))[0];
				}
			}

			if (paycode.equals("0300"))
			{
				bankpath = "c:\\gmc\\";
			}

			else if (paycode.equals("0302") || paycode.equals("0305") || paycode.equals("0306"))
			{
				bankpath = "c:\\gmc_SL\\";
			}

			else if (paycode.equals("0303"))
			{
				bankpath = "c:\\gmc_BOC\\";
			}

			//	先删除上次交易数据文件
			if (PathFile.fileExist(bankpath + "request.txt"))
			{
				PathFile.deletePath(bankpath + "request.txt");

				if (PathFile.fileExist(bankpath + "request.txt"))
				{
					errmsg = "交易请求文件request.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist(bankpath + "result.txt"))
			{
				PathFile.deletePath(bankpath + "result.txt");

				if (PathFile.fileExist(bankpath + "result.txt"))
				{
					errmsg = "交易结果文件result.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist(bankpath + "gmc_data\\receipt.txt"))
			{
				PathFile.deletePath(bankpath + "gmc_data\\receipt.txt");

				if (PathFile.fileExist(bankpath + "gmc_data\\receipt.txt"))
				{
					errmsg = "交易结果文件result.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			// 写入请求数据
			String line = XYKgetRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);

			PrintWriter pw = CommonMethod.writeFile(bankpath + "request.txt");

			if (pw != null)
			{
				pw.println(line);
				pw.flush();
				pw.close();
			}

			// 调用接口模块
			if (PathFile.fileExist(bankpath + "javaposbank.exe"))
			{
				CommonMethod.waitForExec(bankpath + "javaposbank.exe XMYS");
			}
			else
			{
				new MessageBox("找不到金卡工程模块 javaposbank.exe");
				XYKSetError("XX", "找不到金卡工程模块 javaposbank.exe");
				return false;
			}

			if (type == PaymentBank.XYKXF || type == PaymentBank.XYKCX)
			{
				// 读取应答数据
				if (!XYKReadResult()) { return false; }
			}
			else
			{
				errmsg = "调用金卡工程接口成功";
				bld.retcode = "00";
			}

			// 检查交易是否成功
			XYKCheckRetCode();

			//无论是否成功，都检查打印
			XYKPrintDoc(type);

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

			//根据不同的类型生成文本结构
			switch (type)
			{
				case PaymentBank.XYKXF:
					type1 = 'C';
					break;
				case PaymentBank.XYKCX:
					type1 = 'D';
					break;
//				case PaymentBank.XYKTH:
//					type1 = 'R';
//					break;
				case PaymentBank.XYKCD:
					type1 = 'P';
					break;
				default:
					type1 = '0';
					break;
			}

			line = syjh + syyh + type1 + jestr;
			return line;
		}
		catch (Exception ex)
		{
			new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
			ex.printStackTrace();

			return null;
		}
	}

	public boolean XYKReadResult()
	{
		BufferedReader br = null;
		try
		{
			if (!PathFile.fileExist(bankpath + "result.txt") || ((br = CommonMethod.readFileGBK(bankpath + "result.txt")) == null))
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
			bld.cardno = Convert.newSubString(line, 2, 21);
			String je = Convert.newSubString(line, 22, 34);
			double j = Double.parseDouble(je);
			j = ManipulatePrecision.mul(j, 0.01);
			bld.je = j;
			bld.trace = Long.parseLong(Convert.newSubString(line, 34, 40));

			if (!bld.retcode.equals("00"))
			{
				bld.retmsg = "交易失败";
				return false;
			}
			else
			{
				bld.retmsg = "交易成功";
			}
			//			String bankname = XYKReadBankName(bld.bankinfo);
			//			bld.bankinfo = bld.bankinfo + bankname;
			//bld.bankinfo = new String (bld.bankinfo.getBytes("gbk"),"UTF-8");
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

					if (PathFile.fileExist(bankpath + "request.txt"))
					{
						PathFile.deletePath(bankpath + "request.txt");
					}

					if (PathFile.fileExist(bankpath + "result.txt"))
					{
						PathFile.deletePath(bankpath + "result.txt");
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void XYKPrintDoc(int type)
	{
		ProgressBox pb = null;
		String printName = "";
		if (type == PaymentBank.XYKCD)
		{
			printName = bankpath + "\\gmc_data\\rereceipt.txt";	
		}
		else
		{
			printName = bankpath + "\\gmc_data\\receipt.txt";	
//			printName = bankpath + "\\gmc_data\\rereceipt.txt";	
		}
		
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
				Printer.getDefault().startPrint_Journal();

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
						if (line.length() <= 0)
						{
							continue;
						}

						Printer.getDefault().printLine_Journal(line + "\n");
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
				Printer.getDefault().cutPaper_Journal();
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

}
