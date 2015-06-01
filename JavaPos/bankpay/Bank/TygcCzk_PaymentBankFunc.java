package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

public class TygcCzk_PaymentBankFunc extends PaymentBankFunc
{

	public String[] getFuncItem()
	{
		String[] func = new String[3];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "退货交易";
	

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

			case PaymentBank.XYKTH: //签到    
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "签到";

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

		
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && (type != PaymentBank.XYKTH))
			{
				errmsg = "银联接口不支持该交易";
				new MessageBox(errmsg);

				return false;
			}

			// 先删除上次交易数据文件
			if (PathFile.fileExist("D:\\POSCRM\\POCRM.TXT"))
			{
				PathFile.deletePath("D:\\POSCRM\\POCRM.TXT");
				if (PathFile.fileExist("D:\\POSCRM\\POCRM.TXT"))
				{
					errmsg = "交易请求文件POCRM.TXT无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			// 写入请求数据
			String line = XYKgetRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);

			PrintWriter pw = CommonMethod.writeFile("D:\\POSCRM\\POCRM.TXT");

			if (pw != null)
			{
				pw.println(line);
				pw.flush();
				pw.close();
			}

			// 调用接口模块
			if (PathFile.fileExist("D:\\POSCRM\\POSCALL.EXE"))
			{
				CommonMethod.waitForExec("D:\\POSCRM\\POSCALL.EXE");
			}
			else
			{
				new MessageBox("找不到金卡工程模块 POSCALL.EXE");
				XYKSetError("XX", "找不到金卡工程模块 POSCALL.EXE");
				return false;
			}

			// 读取应答数据
			if (!XYKReadResult1(type)) { return false; }

			// 检查交易是否成功
			XYKCheckRetCode();

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
		int type = Integer.parseInt(bld.type.trim());

		// 消费，消费撤销，重打签购单
		if ((type == PaymentBank.XYKXF) || (type == PaymentBank.XYKCX) || (type == PaymentBank.XYKTH) || (type == PaymentBank.XYKCD)
				|| (type == PaymentBank.XYKJZ) || (type == PaymentBank.XKQT1))
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
		if (bld.retcode.trim().equals("1"))
		{
			bld.retbz = 'Y';
			bld.retmsg = "储值卡调用成功";

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

			// 收银机号
			String syjh = ConfigClass.CashRegisterCode;
			// 收银员号
			String syyh = GlobalInfo.posLogin.gh+"["+GlobalInfo.posLogin.name+"]";
			String paycode = ((memo != null && memo.size() > 0)?(String)memo.elementAt(0):"");
			String fphm = "";
			if ((memo != null && memo.size() > 2))
			{
				if ((SaleBS)memo.elementAt(2)!=null && ((SaleBS)memo.elementAt(2)).saleHead != null)
				{
					fphm = String.valueOf(((SaleBS)memo.elementAt(2)).saleHead.fphm);
				}
			}
			
			// 交易类型
			char type1 = ' ';
			switch (type)
			{
				case PaymentBank.XYKXF:
					type1 = '1';
					break;
				case PaymentBank.XYKCX:
				case PaymentBank.XYKTH:
					type1 = '2';
					break;
				
				default:
					break;

			}

			//交易金额
			//String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));
			//jestr = Convert.increaseCharForward(jestr, '0', 12);
			String jestr = String.valueOf(money);
			
			// 根据要求拼接传入串
			line = paycode +"|"+ syjh +"|"+ fphm +"|"+type1+"|"+ jestr +"|"+ syyh ;
			return line;
		}
		catch (Exception ex)
		{
			new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
			ex.printStackTrace();

			return null;
		}
	}

	public boolean XYKReadResult1(int type)
	{
		BufferedReader br = null;
		try
		{
			if (!PathFile.fileExist("D:\\POSCRM\\POCRM.TXT") || ((br = CommonMethod.readFileGBK("D:\\POSCRM\\POCRM.TXT")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}
			String newLine = br.readLine();

			if (newLine.trim().length() <1)
			{
				XYKSetError("XX", "金卡工程应答数据长度有误!");
				new MessageBox("金卡工程应答数据长度有误!", null, false);
				return false;
			}
			// 返回码
			bld.retcode = newLine.split("\\|")[0].trim();
			// 金额
			//String je = newLine.substring(2, 14);
			//double j = Double.parseDouble(je);
			//j = ManipulatePrecision.mul(j, 0.01);
			if(bld.retcode.equals("1")) {
				bld.je = Double.parseDouble(newLine.split("\\|")[1].trim());
			}else{
				bld.retmsg = newLine.split("\\|")[2].trim();
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

					if (PathFile.fileExist(ConfigClass.BankPath + "\\request.TXT"))
					{
						PathFile.deletePath(ConfigClass.BankPath + "\\request.TXT");
					}

					if (PathFile.fileExist(ConfigClass.BankPath + "\\response.TXT"))
					{
						PathFile.deletePath(ConfigClass.BankPath + "\\response.TXT");
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public String getErrorInfo(String retcode)
	{
		String line = "";

		try
		{

			if (!PathFile.fileExist(GlobalVar.ConfigFile + "\\bankError.TXT") || !rtf.loadFile(GlobalVar.ConfigFile + "\\bankError.TXT"))
			{
				new MessageBox("找不到bankError.TXT", null, false);

				return retcode;
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

				if (a[0].trim().equals(retcode.trim())) { return a[1].trim(); }
			}

			rtf.close();

			return retcode;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return retcode;
		}
	}

	public void XYKPrintDoc()
	{

		ProgressBox pb = null;

		try
		{
			if (!PathFile.fileExist(ConfigClass.BankPath + "\\receipt.TXT"))
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
					br = CommonMethod.readFileGB2312(ConfigClass.BankPath + "\\receipt.TXT");

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

	public void XYKPrintDoc_Print(String printStr)
	{
		if (onceprint)
		{
			Printer.getDefault().printLine_Journal(printStr);
		}
		else
		{
			printdoc.println(printStr);
		}
	}

	public void XYKPrintDoc_End()
	{
		if (onceprint)
		{
			Printer.getDefault().cutPaper_Normal();
		}
		else
		{
			printdoc.flush();
			printdoc.close();
			printdoc = null;
		}
	}
}
