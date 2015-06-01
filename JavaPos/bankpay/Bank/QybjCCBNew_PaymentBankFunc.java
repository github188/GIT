package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;

public class QybjCCBNew_PaymentBankFunc extends Bjcs_PaymentBankFunc
{
	private SaleBS saleBS = null;

	public String[] getFuncItem()
	{
		String[] func = new String[12];

		func[0] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[1] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
		func[2] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[3] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[4] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[5] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		func[6] = "[" + PaymentBank.XYKCD + "]" + "重打上一笔签购单";
		func[7] = "[" + PaymentBank.XKQT1 + "]" + "非接卡消费";
		func[8] = "[" + PaymentBank.XKQT2 + "]" + "非接卡余额查询";
		func[9] = "[" + PaymentBank.XKQT3 + "]" + "重打印任意一笔";
		func[10] = "[" + PaymentBank.XKQT4 + "]" + "EMV参数下载";
		func[11] = "[" + PaymentBank.XKQT5 + "]" + "EMV公钥下载";

		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		switch (type)
		{
			case PaymentBank.XYKQD:
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";
				break;
			case PaymentBank.XYKJZ:
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易结账";
				break;
			case PaymentBank.XYKXF:
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;

			case PaymentBank.XYKYE:
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "余额查询";
				break;
				
			case PaymentBank.XYKCX:
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKTH:
				grpLabelStr[0] = null;
				grpLabelStr[1] = "原参考号";
				grpLabelStr[2] = "原交易日期";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKCD:
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重印上笔";
				break;
			case PaymentBank.XKQT1:
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "金  额";
				break;
			case PaymentBank.XKQT2:
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "余额查询";
				break;

			case PaymentBank.XKQT3:
				grpLabelStr[0] = "流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打印";
				break;

			case PaymentBank.XKQT4:
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "EMV参数";
				break;

			case PaymentBank.XKQT5:
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "EMV公钥";
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
			case PaymentBank.XYKQD: // 交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易签到";
				break;
			case PaymentBank.XYKYE: // 余额查询
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始余额查询";
				break;
			case PaymentBank.XYKJZ: //
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始结账";
				break;

			case PaymentBank.XKQT1: //
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;

			case PaymentBank.XYKCD: //
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键重印上一笔";
				break;

			case PaymentBank.XKQT2: //
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始余额查询";
				break;

			case PaymentBank.XKQT3: //
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键重印任意一笔";
				break;
			case PaymentBank.XKQT4: //
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键下载EMV参数";
				break;
			case PaymentBank.XKQT5: //
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键下载EMV公钥";
				break;
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKJZ) && (type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && (type != PaymentBank.XYKTH) && (type != PaymentBank.XYKQD) && (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD) && (type != PaymentBank.XKQT1) && (type != PaymentBank.XKQT2) && (type != PaymentBank.XKQT3) && (type != PaymentBank.XKQT4) && (type != PaymentBank.XKQT5))
			{
				errmsg = "银联接口不支持该交易";
				new MessageBox(errmsg);

				return false;
			}

			// 先删除上次交易数据文件
			if (PathFile.fileExist("c:\\ccb\\request.txt"))
			{
				PathFile.deletePath("c:\\ccb\\request.txt");

				if (PathFile.fileExist("c:\\ccb\\request.txt"))
				{
					errmsg = "交易请求文件request.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist("c:\\ccb\\result.txt"))
			{
				PathFile.deletePath("c:\\ccb\\result.txt");

				if (PathFile.fileExist("c:\\ccb\\result.txt"))
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
				if (PathFile.fileExist("c:\\ccb\\javaposbank.exe"))
				{
					CommonMethod.waitForExec("c:\\ccb\\javaposbank.exe BJCCBNEW", "javaposbank.exe");
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

	public boolean XYKWriteRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			String line = "";

			String date = new ManipulateDateTime().getDateByEmpty();
			date = date.substring(4, date.length());

			if (memo.size() >= 2)
				saleBS = (SaleBS) memo.elementAt(2);

			// 根据不同的类型生成文本结构
			switch (type)
			{
				case PaymentBank.XYKQD:
					line = "00," + ManipulateStr.PadLeft("", 12, '0') + "," + date + "," + ManipulateStr.PadLeft("", 6, '0') + "," + ManipulateStr.PadLeft("", 12, '0');
					break;

				case PaymentBank.XYKJZ:
					line = "02," + ManipulateStr.PadLeft("", 12, '0') + "," + date + "," + ManipulateStr.PadLeft("", 6, '0') + "," + ManipulateStr.PadLeft("", 12, '0');

					break;
				case PaymentBank.XYKXF:
					if (saleBS != null)
					{
						long tmpmoney = (long) ManipulatePrecision.doubleConvert(money * 100, 2, 1);
						line = "03," + ManipulateStr.PadLeft(String.valueOf(tmpmoney), 12, '0') + "," + date + "," + ManipulateStr.PadLeft("", 6, '0') + "," + ManipulateStr.PadLeft("", 12, '0');
					}
					break;
				case PaymentBank.XYKCX:
					if (saleBS != null)
					{
						line = "04," + ManipulateStr.PadLeft(String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1)), 12, '0') + "," + olddate + "," + ManipulateStr.PadLeft(oldseqno, 6, '0') + "," + ManipulateStr.PadLeft("", 6, '0');
					}

					break;
				case PaymentBank.XYKTH:
					if (saleBS != null)
					{
						olddate = olddate.substring(0, 4);
						line = "05," + ManipulateStr.PadLeft(String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1)), 12, '0') + "," + olddate + "," + ManipulateStr.PadLeft("", 6, '0') + "," + ManipulateStr.PadLeft(oldauthno, 6, '0');
					}
					break;

				case PaymentBank.XYKYE:
					line = "06," + ManipulateStr.PadLeft("", 12, '0') + "," + date + "," + ManipulateStr.PadLeft("", 6, '0') + "," + ManipulateStr.PadLeft("", 12, '0');
					break;

				case PaymentBank.XKQT1:
					if (saleBS != null)
						line = "07," + ManipulateStr.PadLeft(String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1)), 12, '0') + "," + date + "," + ManipulateStr.PadLeft("", 6, '0') + "," + ManipulateStr.PadLeft("", 12, '0');
					break;

				case PaymentBank.XKQT2:
					line = "08," + ManipulateStr.PadLeft("", 12, '0') + "," + date + "," + ManipulateStr.PadLeft("", 6, '0') + "," + ManipulateStr.PadLeft("", 12, '0');
				case PaymentBank.XYKCD:
					line = "20," + ManipulateStr.PadLeft("", 12, '0') + "," + date + "," + ManipulateStr.PadLeft("", 6, '0') + "," + ManipulateStr.PadLeft("", 12, '0');
					break;
				case PaymentBank.XKQT3:
					line = "21," + ManipulateStr.PadLeft("", 12, '0') + "," + date + "," + ManipulateStr.PadLeft(oldseqno, 6, '0') + "," + ManipulateStr.PadLeft("", 12, '0');
					break;
				case PaymentBank.XKQT4:
					line = "91," + ManipulateStr.PadLeft("", 12, '0') + "," + date + "," + ManipulateStr.PadLeft(oldseqno, 6, '0') + "," + ManipulateStr.PadLeft("", 12, '0');
					break;
				case PaymentBank.XKQT5:
					line = "92," + ManipulateStr.PadLeft("", 12, '0') + "," + date + "," + ManipulateStr.PadLeft(oldseqno, 6, '0') + "," + ManipulateStr.PadLeft("", 12, '0');
					break;
			}

			PrintWriter pw = null;

			try
			{
				pw = CommonMethod.writeFile("c:\\ccb\\request.txt");
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

	public void XYKPrintDoc()
	{
		ProgressBox pb = null;

		try
		{
			String printName = "";

			int type = Integer.parseInt(bld.type.trim());

			if ((type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKTH || type == PaymentBank.XYKCD || type == PaymentBank.XKQT3))
			{
				printName = "C:\\CCB\\TRANS.PRN";
			}
			else
			{
				return;
			}

			if (!PathFile.fileExist(printName))
			{
				new MessageBox("找不到签购单打印文件!");

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

						if (line.trim().equals("CUTPAPPER"))
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

	public boolean XYKNeedPrintDoc()
	{
		if (!checkBankSucceed()) { return false; }

		return true;
	}

	public boolean XYKCheckRetCode()
	{
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

	public boolean XYKReadResult()
	{
		BufferedReader br = null;

		try
		{
			if (!PathFile.fileExist("c:\\ccb\\result.txt") || ((br = CommonMethod.readFileGBK("c:\\ccb\\result.txt")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}

			String line = br.readLine();

			br.close();

			if (line.length() <= 0)
				return false;

			String result[] = line.split(",");

			if (result == null)
				return false;

			if (result.length > 0 && result[0] != null)
			{
				if (!result[0].trim().equals("0"))
				{
					if (result.length > 1 && result[1] != null)
						bld.retcode = result[1].equals("") ? "99" : result[1];
					if (result.length > 2 && result[2] != null)
						bld.retmsg = result[2].equals("") ? "金卡交易返回失败" : result[2];
					return false;
				}
			}

			if (result.length > 1 && result[1] != null)
			{
				bld.retcode = result[1];
			}
			if (result.length > 2 && result[2] != null)
			{
				bld.retmsg = result[2];
			}

			if (result.length > 4 && result[4] != null)
			{
				bld.cardno = result[4].trim();
			}

			if (result.length > 5 && result[5] != null)
			{
				bld.je = ManipulatePrecision.doubleConvert(Convert.toDouble(result[5].trim()) / 100, 2, 1);
			}

			if (result.length > 10 && result[10] != null)
			{
				bld.trace = Convert.toLong(result[10].trim());
			}
			if (result.length > 11 && result[11] != null)
			{
				bld.authno = result[11];
			}

			if (result.length > 12 && result[12] != null)
			{
				bld.bankinfo = result[12];
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

					if (PathFile.fileExist("c:\\ccb\\request.txt"))
					{
						PathFile.deletePath("c:\\ccb\\request.txt");
					}

					if (PathFile.fileExist("c:\\ccb\\result.txt"))
					{
						PathFile.deletePath("c:\\ccb\\result.txt");
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

			}
		}
	}

}
