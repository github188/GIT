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
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

//上海依恋接口
public class ShylMisPos_PaymentBankFunc extends PaymentBankFunc
{
	protected String bankpath = ConfigClass.BankPath;

	public String[] getFuncItem()
	{
		String[] func = new String[3];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XKQT1 + "]" + "辅助功能";

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
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XKQT1: // 辅助功能
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "辅助功能";
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
			case PaymentBank.XKQT1: // 辅助功能
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "辅助功能";
				break;

		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1,
			String track2, String track3, String oldseqno, String oldauthno,
			String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX)
					&& (type != PaymentBank.XKQT1))
			{
				errmsg = "银联接口不支持该交易";
				new MessageBox(errmsg);

				return false;
			}

			// 先删除上次交易数据文件
			if (PathFile.fileExist(bankpath + "\\request.txt"))
			{
				PathFile.deletePath(bankpath + "\\request.txt");

				if (PathFile.fileExist(bankpath + "\\request.txt"))
				{
					errmsg = "交易请求文件request.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist(bankpath + "\\Response.txt"))
			{
				PathFile.deletePath(bankpath + "\\Response.txt");

				if (PathFile.fileExist(bankpath + "\\Response.txt"))
				{
					errmsg = "交易请求文件Response.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			// 写入请求数据
			if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno,
					oldauthno, olddate, memo))
			{
				return false;
			}

			if (bld.retbz != 'Y')
			{

				// 调用接口模块
				if (PathFile.fileExist(bankpath + "\\javaposbank.exe"))
				{
					CommonMethod.waitForExec(bankpath
							+ "\\javaposbank.exe mispos", "javaposbank.exe");
				} else
				{
					new MessageBox("找不到金卡工程模块 javaposbank.exe");
					XYKSetError("XX", "找不到金卡工程模块 javaposbank.exe");
					return false;
				}

				// 读取应答数据
				if (!XYKReadResult())
				{
					return false;
				}

				// 检查交易是否成功
				if (!XYKCheckRetCode())
					return false;
			}

			new MessageBox("消费成功");

			return true;
		} catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean XYKCheckRetCode()
	{
		if (bld.retcode.trim().equals("00"))
		{
			bld.retbz = 'Y';

			return true;
		} else
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
		} else
		{
			errmsg = "交易成功";

			return true;
		}
	}

	public boolean XYKWriteRequest(int type, double money, String track1,
			String track2, String track3, String oldseqno, String oldauthno,
			String olddate, Vector memo)
	{
		try
		{
			String line = "";

			String jestr = String.valueOf(money);

			// 根据不同的类型生成文本结构
			switch (type)
			{
				case PaymentBank.XYKXF:
					line = GlobalInfo.syjDef.syjh + ","
							+ GlobalInfo.sysPara.mktcode + ","
							+ GlobalInfo.posLogin.gh + "," + "01" + "," + "2"
							+ "," + jestr + ",," + GlobalInfo.syjStatus.fphm
							+ ",";
					break;
				case PaymentBank.XYKCX:
					line = GlobalInfo.syjDef.syjh + ","
							+ GlobalInfo.sysPara.mktcode + ","
							+ GlobalInfo.posLogin.gh + "," + "01" + "," + "3"
							+ "," + jestr + "," + oldseqno + ","
							+ GlobalInfo.syjStatus.fphm + ",";
					break;
				case PaymentBank.XKQT1:
					line = GlobalInfo.syjDef.syjh + ","
							+ GlobalInfo.sysPara.mktcode + ","
							+ GlobalInfo.posLogin.gh + "," + "01" + "," + "99"
							+ "," + "0" + ",,,";
					break;

			}

			PrintWriter pw = null;

			try
			{
				pw = CommonMethod.writeFile(bankpath + "\\request.txt");

				if (pw != null)
				{
					pw.println(line);
					pw.flush();
				}
			} finally
			{
				if (pw != null)
				{
					pw.close();
				}
			}

			return true;
		} catch (Exception ex)
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
			if (!PathFile.fileExist(bankpath + "\\Response.txt")|| ((br = CommonMethod.readFileGBK(bankpath
							+ "\\Response.txt")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}

			String line = br.readLine();

			if (line == null || line.length() <= 0)
			{
				new MessageBox("返回数据为空");
				return false;
			}

			String[] result = line.split(",");

			// 0,00,交易成功,800,1001,9999,01,银行卡,2,.01,0,0,0,000039,,****,000424,
			// 303310054110159,纽可超市,03039049,141346416219,059626,0003010000,交通银行,
			// 622260*********6409,20120724,141346,0,245942
			if (result == null)
				return false;

			if (result.length > 0)
			{
				if (!result[0].trim().equals("0"))
				{
					bld.retmsg = "调用金卡函数发生异常!";
					return false;
				}
			}

			if (result.length > 1)
				bld.retcode = result[1].trim();

			if (bld.retcode.equals("00"))
			{
				if (result.length > 8)
					bld.type = result[8];
				if (result.length > 9)
					bld.je = ManipulatePrecision.doubleConvert(
							Convert.toDouble(result[9].trim()), 2, 1);
				if (result.length > 13)
					bld.trace = Convert.toLong(result[13]);
				if (result.length > 14)
					bld.oldtrace = Convert.toLong(result[14]);
				if (result.length > 20)
					bld.authno = result[20];
				if (result.length > 23)
					bld.bankinfo = result[23];
				if (result.length > 24)
					bld.cardno = result[24];
				
				return true;
			}
			else
			{
				if(result.length>2)
					new MessageBox(result[2]);
				return false;
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();

			XYKSetError("XX", "读取应答XX:" + ex.getMessage());
			new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);

			return false;
		} finally
		{
			if (br != null)
			{
				try
				{
					br.close();
					br =null;

					/*
					 * if (PathFile.fileExist(bankpath + "\\request.txt")) {
					 * PathFile.deletePath(bankpath + "\\request.txt"); }
					 * 
					 * if (PathFile.fileExist(bankpath + "\\Response.txt")) {
					 * PathFile.deletePath(bankpath + "\\Response.txt"); }
					 */
				} catch (IOException e)
				{
					e.printStackTrace();
					br = null;
				}
			}
		}
	}
}
