package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

//上海海亮银联接口
//调用动态库（模块名：ICBC1；(SHHL)动态库(dll文件）：MposCore.dll；函数：BSTR DoICBCZJMisTranSTD(LPCTSTR id,LPCTSTR preinput, LPCTSTR rsv1,LPCTSTR rsv2,LPCTSTR rsv3);）
public class Shhl_PaymentBankFunc extends PaymentBankFunc
{
	String path = null;

	public String[] getFuncItem()
	{
		String[] func = new String[7];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
		func[4] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
		func[5] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[6] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";

		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		// 0-4对应FORM中的5个输入框
		// null表示该不用输入
		switch (type)
		{
			case PaymentBank.XYKXF:// 消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKCX:// 消费撤销
				grpLabelStr[0] = "系统检索号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "消费撤销";
				break;
			case PaymentBank.XYKTH:// 隔日退货
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKYE:// 余额查询
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "余额查询";
				break;
			case PaymentBank.XYKJZ:// 余额查询
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易结账";
				break;
			case PaymentBank.XYKQD:// 交易签到
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";
				break;
			case PaymentBank.XYKCD:// 重打签购单
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打签购单";
				break;

		}

		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		// 0-4对应FORM中的5个输入框
		// null表示必须用户输入,不为null表示缺省显示无需改变
		switch (type)
		{
			case PaymentBank.XYKXF:// 消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKCX:// 消费撤销
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKTH:// 隔日退货
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKQD:// 交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易签到";
				break;
			case PaymentBank.XYKJZ:// 交易结账
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易结账";
				break;
			case PaymentBank.XYKYE:// 余额查询
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始余额查询";
				break;
			case PaymentBank.XYKCD:// 余额查询
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始重打签购单";
				break;
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if (!(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKTH || type == PaymentBank.XYKQD || type == PaymentBank.XYKJZ || type == PaymentBank.XYKYE || type == PaymentBank.XYKCD))
			{
				new MessageBox("银联接口不支持此交易类型!");

				return false;
			}

			// 获得金卡文件路径
			path = ConfigClass.BankPath;

			if (PathFile.fileExist(path + "\\request.txt"))
			{
				PathFile.deletePath(path + "\\request.txt");
				if (PathFile.fileExist(path + "\\request.txt"))
				{
					errmsg = "交易“request.txt”文件删除失败，请重试!";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);

					return false;
				}
			}
			if (PathFile.fileExist(path + "\\result.txt"))
			{
				PathFile.deletePath(path + "\\result.txt");
				if (PathFile.fileExist(path + "\\result.txt"))
				{
					errmsg = "交易“result.txt”文件删除失败，请重试!";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);

					return false;
				}
			}

			// 写入请求数据
			if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo)) { return false; }
			// 调用接口模块
			if (PathFile.fileExist(path + "\\javaposbank.exe"))
			{
				CommonMethod.waitForExec(path + "\\javaposbank.exe ICBC1", "javaposbank");
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

			if (!checkBankSucceed())
				return false;

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
		String line = "";
		// String syyid = Convert.increaseChar(GlobalInfo.posLogin.gh, 6);

		String id = "";
		String input = "";
		String seqno;
		String date;
		String zdh;

		try
		{
			// 交易类型
			switch (type)
			{
				case PaymentBank.XYKXF: // 消费
					id = "1001";
					String je = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));
					je = Convert.increaseCharForward(je, '0', 12);
					input = "AMT1=" + je;
					break;

				case PaymentBank.XYKCX: // 消费撤销
					id = "1101";
					seqno = Convert.increaseChar(oldseqno, '0', 8);
					input = "I1=" + seqno;
					break;

				case PaymentBank.XYKTH: //
					id = "1102";
					seqno = Convert.increaseChar(oldseqno, '0', 8);
					zdh = Convert.increaseChar(oldauthno, '0', 3);
					date = Convert.increaseChar(olddate, '0', 8);
					input = "I1=" + date + ",I2=" + seqno + ",I3=" + zdh;
					break;
				case PaymentBank.XYKYE: // 余额查询
					id = "2002";
					break;
				case PaymentBank.XYKQD: // 交易签到
					id = "4001";
					break;
				case PaymentBank.XYKJZ: // 交易结账
					id = "4002";
					break;
				case PaymentBank.XYKCD: // 重打签购单
					id = "4005";
					break;
			}
			line = id + "," + input + ", " + "," + "," + ",";
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		PrintWriter pw = CommonMethod.writeFile(path + "\\request.txt");

		if (pw != null)
		{
			pw.println(line);
			pw.flush();
			pw.close();
		}

		return true;
	}

	// 读取result文件
	public boolean XYKReadResult()
	{
		BufferedReader br = null;
		try
		{
			if (!PathFile.fileExist(path + "\\result.txt") || ((br = CommonMethod.readFileGBK(path + "\\result.txt")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}

			int type = Convert.toInt(bld.type.trim());

			String newLine = br.readLine();

			String line = newLine;

			if (line == null || line.length() <= 0)
				return false;

			bld.retcode = line.substring(0, 2);
			if (!bld.retcode.equals("00"))
			{
				bld.retmsg = "交易失败";
				errmsg = bld.retmsg;
				return false;
			}

			bld.retmsg = "交易成功";
			errmsg = bld.retmsg;

			if (type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKTH)
			{
				bld.cardno = Convert.newSubString(line, 2, 21).trim();
				bld.trace = Long.parseLong(Convert.newSubString(line, 35, 41).trim()); // 刷卡流水号，记录到pos流水中
				bld.crc = Convert.newSubString(line, 53, 61);
				double je= ManipulatePrecision.doubleConvert(Convert.toDouble(Convert.newSubString(line, 65, 77)) / 100, 2, 1);
				if (bld.je!=je){
					new MessageBox("银行返回的交易金额："+je+" 与收银系统发起的交易金额:"+bld.je+" 不匹配!");
				}
				bld.je = je;
				bld.memo = Convert.newSubString(line, 77, 92).trim(); // 刷卡机号
				bld.memo = bld.crc + "|" + bld.memo;
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
					// if (PathFile.fileExist(path + "\\request.txt"))
					// {
					// PathFile.deletePath(path + "\\request.txt");
					// }
					//
					// if (PathFile.fileExist(path + "\\result.txt"))
					// {
					// PathFile.deletePath(path + "\\result.txt");
					// }
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
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
			errmsg = "交易失败";

			return false;
		}
		else
		{
			errmsg = "交易成功";

			return true;
		}
	}

	public boolean checkDate(Text date)
	{
		String d = date.getText();
		if (d.length() > 8)
		{
			new MessageBox("日期格式错误\n日期格式《YYYYMMDD》");
			return false;
		}

		return true;
	}

}
