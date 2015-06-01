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
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

public class YlswCq_PaymentBankFunc extends PaymentBankFunc
{

	public String[] getFuncItem()
	{
		String[] func = new String[5];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[3] = "[" + PaymentBank.XYKCD + "]" + "签购单重打";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";

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

			case PaymentBank.XYKQD: //签到    
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "签到";

				break;

			case PaymentBank.XYKJZ: //结账
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "结账";

				break;

			case PaymentBank.XYKCD: //签购单重打
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打上笔签购单";
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

			//			case PaymentBank.XYKYE: //余额查询    
			//				grpTextStr[0] = null;
			//				grpTextStr[1] = null;
			//				grpTextStr[2] = null;
			//				grpTextStr[3] = null;
			//				grpTextStr[4] = "按回车键开始余额查询";
			//
			//				break;

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
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && (type != PaymentBank.XYKQD) && (type != PaymentBank.XYKJZ)
					&& (type != PaymentBank.XYKCD))
			{
				errmsg = "银联接口不支持该交易";
				new MessageBox(errmsg);

				return false;
			}

			// 先删除上次交易数据文件
			if (PathFile.fileExist("C:\\GMC\\request.TXT"))
			{
				PathFile.deletePath("C:\\GMC\\request.TXT");
				if (PathFile.fileExist("C:\\GMC\\request.TXT"))
				{
					errmsg = "交易请求文件request.TXT无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			// 写入请求数据
			String line = XYKgetRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);

			PrintWriter pw = CommonMethod.writeFile(ConfigClass.BankPath + "\\request.TXT");

			if (pw != null)
			{
				pw.println(line);
				pw.flush();
				pw.close();
			}

			// 调用接口模块
			if (PathFile.fileExist(ConfigClass.BankPath + "\\GMC.EXE"))
			{
				CommonMethod.waitForExec(ConfigClass.BankPath + "\\GMC.EXE", "gmc.exe");
			}
			else
			{
				new MessageBox("找不到金卡工程模块 GMC.EXE");
				XYKSetError("XX", "找不到金卡工程模块 GMC.EXE");
				return false;
			}

			// 读取应答数据
			if (!XYKReadResult1(type)) { return false; }

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

			// 收银机号
			String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode, ' ', 15);
			// 收银员号
			String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 15);
			// 交易类型
			char type1 = ' ';
			switch (type)
			{
				case PaymentBank.XYKXF:
					type1 = 'C';
					break;
				case PaymentBank.XYKCX:
				case PaymentBank.XYKTH:
					type1 = 'D';
					break;
				//				case PaymentBank.XYKQD:
				//				case PaymentBank.XYKCD:
				//				case PaymentBank.XYKJZ:
				default:
					type1 = 'E';
					break;

			}

			//交易金额
			String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));
			jestr = Convert.increaseCharForward(jestr, '0', 12);

			// 凭证号
			String authno = "000000";

			bld.crc = XYKGetCRC();

			// 根据要求拼接传入串
			line = syjh + syyh + type1 + jestr + authno + bld.crc;
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
			if (!PathFile.fileExist("C:\\GMC\\response.TXT") || ((br = CommonMethod.readFileGBK("C:\\GMC\\response.TXT")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}
			String newLine = br.readLine();

			if (newLine.length() != 124)
			{
				XYKSetError("XX", "金卡工程应答数据长度有误!");
				new MessageBox("金卡工程应答数据长度有误!", null, false);
				return false;
			}
			// 返回码
			bld.retcode = newLine.substring(0, 2);
			if (type != PaymentBank.XYKXF && type != PaymentBank.XYKCX && type != PaymentBank.XYKTH)
			{
				bld.retcode = "00";
				bld.retmsg = "银联交易成功";
				errmsg = bld.retmsg;
				return true;
			}
			else
			{
				bld.retmsg = "交易成功";
				errmsg = bld.retmsg;
			}
			// 金额
			String je = newLine.substring(2, 14);
			double j = Double.parseDouble(je);
			j = ManipulatePrecision.mul(j, 0.01);
			bld.je = j;
			// 卡号
			bld.cardno = newLine.substring(14, 33);
			// 备用
			bld.memo = newLine.substring(33, 52);
			// 卡类型标志
//			String cardType = newLine.substring(52, 56);
			// 卡名称
			bld.bankinfo = newLine.substring(56, 64);
			// LRC
			String LRC = newLine.substring(64, 67);
			if (!LRC.equals(bld.crc))
			{
				errmsg = "返回效验码" + LRC + "同原始效验码" + bld.crc + "不一致";
				XYKSetError("XX", errmsg);
				new MessageBox(errmsg);
				return false;
			}
			// 商户号
//			String bankNo = newLine.substring(67, 82);
//			// 终端号
//			String terminalNo = newLine.substring(82, 90);
//			// 批次号
//			String batchNo = newLine.substring(90, 96);
//			// 流水号
//			bld.trace = Long.parseLong(newLine.substring(96, 102));
//			// 系统参考号
//			String referenceNo = newLine.substring(102, 114);
//			// 日期
//			String date = newLine.substring(114, 118);
//			// 时间
//			String time = newLine.substring(118, 124);
//			System.out.println("金卡返回字符串:");
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
