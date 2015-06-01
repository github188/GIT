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

////兰州银行POSMIS接口  嘉峪关东方百盛银行接口
public class Lzyh_PaymentBankFunc extends PaymentBankFunc
{
	public String[] getFuncItem()
	{
		String[] func = new String[8];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "银联结算";
		func[5] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		func[6] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
		func[7] = "[" + PaymentBank.XKQT1 + "]" + "按流水号打印";
		;

		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		switch (type)
		{
			case PaymentBank.XYKXF: //	消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKCX: //消费撤销
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = "原交易日期";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKQD: //交易签到
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";
				break;
			case PaymentBank.XYKJZ: //银联结账
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易结算";
				break;
			case PaymentBank.XYKYE: //余额查询    
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "余额查询";
				break;
			case PaymentBank.XYKCD: //签购单重打
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打上笔签购单";
				break;
			case PaymentBank.XKQT1: // 按流水号打印
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "按凭证号打印";
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
			case PaymentBank.XYKTH: //隔日退货   
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKQD: //交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "银联接口不支持该交易";
				break;
			case PaymentBank.XYKJZ: //银联结账
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始银联结账";
				break;
			case PaymentBank.XYKYE: //余额查询    
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始余额查询";
				break;
			case PaymentBank.XYKCD: //签购单重打
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键签购单重打";
				break;
			case PaymentBank.XKQT1: // 按凭流水号打印
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键凭流水号打印";
				break;
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && (type != PaymentBank.XYKTH) && (type != PaymentBank.XYKJZ)
					&& (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD) && (type != PaymentBank.XKQT1))
			{
				errmsg = "银联接口不支持该交易";
				new MessageBox(errmsg);

				return false;
			}

			// 先删除上次交易数据文件
			if (PathFile.fileExist(ConfigClass.BankPath + "\\request.txt"))
			{
				PathFile.deletePath(ConfigClass.BankPath + "\\request.txt");

				if (PathFile.fileExist(ConfigClass.BankPath + "\\request.txt"))
				{
					errmsg = "交易请求文件request.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist(ConfigClass.BankPath + "\\result.txt"))
			{
				PathFile.deletePath(ConfigClass.BankPath + "\\result.txt");

				if (PathFile.fileExist(ConfigClass.BankPath + "\\result.txt"))
				{
					errmsg = "交易应答文件result.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist(ConfigClass.BankPath + "\\lccb\\receipt.txt"))
			{
				PathFile.deletePath(ConfigClass.BankPath + "\\lccb\\receipt.txt");

				if (PathFile.fileExist(ConfigClass.BankPath + "\\lccb\\receipt.txt"))
				{
					errmsg = "交易请求文件receipt.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			//  写入请求数据
			if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo)) { return false; }

			if (bld.retbz != 'Y')
			{

				// 调用接口模块 c:\bank 目录
				if (PathFile.fileExist(ConfigClass.BankPath + "\\javaposbank.exe"))
				{
					CommonMethod.waitForExec(ConfigClass.BankPath + "\\javaposbank.exe LZBANK");

				}
				else
				{
					new MessageBox("找不到金卡工程模块 javaposbank.exe");
					XYKSetError("XX", "找不到金卡工程模块javaposbank.exe");
					return false;
				}

				// 读取应答数据
				if (!XYKReadResult()) { return false; }

				// 检查交易是否成功
				XYKCheckRetCode();
			}

			// 	打印签购单
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

		return true;
	}

	public boolean XYKCheckRetCode()
	{
		if (bld.retcode.trim().equals("000000"))
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
		try
		{
			String line = "";
			bld.type = String.valueOf(type);
			String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));
			jestr = Convert.increaseChar(jestr, ' ', 12);
			//交易类别  消费金额  收银员号  收银机号  原交易流水号  原交易检索参考号 原交易日期  原终端号
			String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 10);
			String syjh = Convert.increaseChar(GlobalInfo.syjDef.syjh, ' ', 10);
			String seqno = Convert.increaseChar("", ' ', 6); //pos流水号
			String oldrefno = Convert.increaseChar("", ' ', 15); //原交易检索参考号
			String authno = Convert.increaseChar("", ' ', 6); //授权号
			String odate = Convert.increaseChar("", ' ', 8); //原交易日期
			String cardtype = "";
			track1 = Convert.increaseChar("", ' ', 76);
			track2 = Convert.increaseChar("", ' ', 37);
			track3 = Convert.increaseChar("", ' ', 104);

			//	根据不同的类型生成文本结构
			switch (type)
			{
				case PaymentBank.XYKXF:
					line = "01" + jestr + seqno + syyh + syjh + oldrefno + authno + odate + cardtype + track1 + track2 + track3;

					break;
				case PaymentBank.XYKCX:
					seqno = Convert.increaseChar(oldseqno, ' ', 6);
					line = "02" + jestr + seqno + syyh + syjh + oldrefno + authno + odate + cardtype + track1 + track2 + track3;

					break;
				case PaymentBank.XYKTH:
					seqno = Convert.increaseChar(oldseqno, ' ', 6);
					oldrefno = Convert.increaseChar(olddate.substring(8, 14), ' ', 15);
					odate = Convert.increaseChar(olddate.substring(0, 8), ' ', 8);
					line = "09" + jestr + seqno + syyh + syjh + oldrefno + authno + odate + cardtype + track1 + track2 + track3;

					break;
				case PaymentBank.XYKJZ:
					line = "14" + jestr + seqno + syyh + syjh + oldrefno + authno + odate + cardtype + track1 + track2 + track3;

					break;
				case PaymentBank.XYKYE:
					line = "03" + jestr + seqno + syyh + syjh + oldrefno + authno + odate + cardtype + track1 + track2 + track3;

					break;
				case PaymentBank.XYKCD:
					seqno = "000000";
					line = "12" + jestr + seqno + syyh + syjh + oldrefno + authno + odate + cardtype + track1 + track2 + track3;

					break;
				case PaymentBank.XKQT1:
					seqno = Convert.increaseChar(oldseqno, ' ', 6);
					line = "02" + jestr + seqno + syyh + syjh + oldrefno + authno + odate + cardtype + track1 + track2 + track3;

					break;

			}

			PrintWriter pw = null;

			try
			{
				pw = CommonMethod.writeFile(ConfigClass.BankPath + "\\request.txt");
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
			if (!PathFile.fileExist(ConfigClass.BankPath + "\\result.txt")
					|| ((br = CommonMethod.readFileGBK(ConfigClass.BankPath + "\\result.txt")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}

			// 读取请求数据
			String line = br.readLine();
			bld.retcode = line.substring(2, 8);
			if (!bld.retcode.equals("000000"))
			{
				bld.retmsg = line.substring(8, 48);
				return false;
			}
			bld.retmsg = line.substring(8, 48);

			bld.trace = Long.parseLong(line.substring(48, 54));

			bld.cardno = line.substring(66, 85);
			bld.bankinfo = line.substring(89, 91);
			bld.authno = line.substring(91, 103);
			bld.je = ManipulatePrecision.doubleConvert(Integer.parseInt(line.substring(133, 145)) / 100, 2, 1);
			errmsg = bld.retmsg;
			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
			XYKSetError("XX", "读取应答XX:" + ex.getMessage());
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
				}
				catch (IOException e)
				{
					// TODO 自动生成 catch 块
					new MessageBox("result.TXT 关闭失败\n重试后如果仍然失败，请联系信息部");
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

			if ((type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKTH || type == PaymentBank.XYKCD
					|| type == PaymentBank.XKQT1 || type == PaymentBank.XYKJZ))
			{
				if (!PathFile.fileExist(ConfigClass.BankPath + "\\lccb\\receipt.txt"))
				{
					new MessageBox("找不到签购单打印文件!");

					return;
				}
				else
				{
					printName = ConfigClass.BankPath + "\\lccb\\receipt.txt";
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

	public void XYKPrintDoc_Print(String printStr)
	{
		if (onceprint)
		{
			Printer.getDefault().printLine_Normal(printStr);
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
			if (printdoc == null) return;

			printdoc.flush();
			printdoc.close();
			printdoc = null;
		}
	}

}
