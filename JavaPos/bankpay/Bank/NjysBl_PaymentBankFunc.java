package bankpay.Bank;


import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Payment.PaymentBank;
//昆山巴黎春天  不打印签购单
public class NjysBl_PaymentBankFunc extends NjysKs_PaymentBankFunc
{

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
					errmsg = "交易结果文件result.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist(ConfigClass.BankPath + "\\toprint.txt"))
			{
				PathFile.deletePath(ConfigClass.BankPath + "\\toprint.txt");

				if (PathFile.fileExist(ConfigClass.BankPath + "\\toprint.txt"))
				{
					errmsg = "交易结果文件result.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			// 写入请求数据
			String line = XYKgetRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);

			PrintWriter pw = CommonMethod.writeFile(ConfigClass.BankPath + "\\request.txt");

			if (pw != null)
			{
				pw.println(line);
				pw.flush();
				pw.close();
			}

			// 调用接口模块
			if (PathFile.fileExist(ConfigClass.BankPath + "\\javaposbank.exe"))
			{
				CommonMethod.waitForExec(ConfigClass.BankPath + "\\javaposbank.exe XMYS", "javaposbank.exe");
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
			//if (XYKNeedPrintDoc(type))
			//{
				//XYKPrintDoc();
		//	}
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

	
}
