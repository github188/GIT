package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.SalePayDef;

public class Gslh_PaymentBankFunc extends PaymentBankFunc
{
	// public static boolean isOneBill = false;
	// public static int printcount = 0;

	private SaleBS saleBS = null;

	public String[] getFuncItem()
	{
		String[] func = new String[1];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";

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
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XKQT2))
			{
				errmsg = "不支持该交易";
				new MessageBox(errmsg);

				return false;
			}

			if (memo.size() >= 2)
				saleBS = (SaleBS) memo.elementAt(2);
			if (saleBS == null && type == PaymentBank.XYKXF)
			{
				errmsg = "该交易必须在付款的时候使用";
				XYKSetError("XX", errmsg);
				new MessageBox(errmsg);
				return false;
			}

			// c:\\JavaPOS\\mzkcard\\javaposic.exe"
			// 先删除上次交易数据文件
			if (PathFile.fileExist("c:\\JavaPOS\\mzkcard\\request.txt"))
			{
				PathFile.deletePath("c:\\JavaPOS\\mzkcard\\request.txt");

				if (PathFile.fileExist("c:\\JavaPOS\\mzkcard\\request.txt"))
				{
					errmsg = "交易请求文件request.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist("c:\\JavaPOS\\mzkcard\\result.txt"))
			{
				PathFile.deletePath("c:\\JavaPOS\\mzkcard\\result.txt");

				if (PathFile.fileExist("c:\\JavaPOS\\mzkcard\\result.txt"))
				{
					errmsg = "交易请求文件result.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			new MessageBox("警  告\n程序若无响应,请耐心等待10秒,切勿重启POS");

			if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
				return false;

			if (bld.retbz != 'Y')
			{

				// 调用接口模块
				if (PathFile.fileExist("c:\\JavaPOS\\mzkcard\\javaposic.exe"))
				{
					CommonMethod.waitForExec("c:\\JavaPOS\\mzkcard\\javaposic.exe GSLH");
				}
				else
				{
					new MessageBox("找不到付款模块 JavaPosIC.exe");
					XYKSetError("XX", "找不到付款模块 JavaPosIC.exe");
					return false;
				}

				// 读取应答数据
				if (!XYKReadResult())
					return false;

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
			XYKSetError("XX", "面值卡XX:" + ex.getMessage());
			new MessageBox("调用面值卡处理模块异常!\n\n" + ex.getMessage(), null, false);
			return false;
		}
	}

	public boolean XYKNeedPrintDoc()
	{
		if (!checkBankSucceed())
			return false;

		XYKWritePrint();

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

	public boolean XYKWriteRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			String line = "";

			String jestr = String.valueOf(ManipulatePrecision.doubleConvert(money, 2, 1));

			if (memo.size() >= 2)
				saleBS = (SaleBS) memo.elementAt(2);

			// 根据不同的类型生成文本结构
			switch (type)
			{
				case PaymentBank.XYKXF:
					String stryscardno = "";
					for (int i = 0; i < saleBS.salePayment.size(); i++)
					{
						SalePayDef pay = (SalePayDef) saleBS.salePayment.elementAt(i);
						if (this.paycode.equals(pay.paycode))
						{
							stryscardno = stryscardno + pay.payno + "#";
						}
					}

					if (stryscardno.length() > 0)
						stryscardno = stryscardno.substring(0, stryscardno.length() - 1);

					line = "1," + saleBS.saleHead.syyh + "," + saleBS.saleHead.syjh + "-" + String.valueOf(saleBS.saleHead.fphm) + "," + stryscardno + "," + jestr;
					break;
			}

			PrintWriter pw = null;
			PrintWriter bak = null;
			try
			{
				pw = CommonMethod.writeFile("c:\\JavaPOS\\mzkcard\\request.txt");
				if (pw != null)
				{
					pw.println(line);
					pw.flush();
				}

				// 消费时才写备份
				if (type == PaymentBank.XYKXF)
				{
					bak = CommonMethod.writeFile("c:\\JavaPOS\\mzkcard\\icbak");
					if (bak != null)
					{
						bak.println(line);
						bak.flush();
					}
				}
			}
			finally
			{
				if (pw != null)
				{
					pw.close();
					pw = null;
				}

				if (bak != null)
				{
					bak.close();
					bak = null;
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("写入面值卡请求数据异常!\n\n" + ex.getMessage(), null, false);
			ex.printStackTrace();

			return false;
		}
	}

	private String billContent()
	{
		StringBuffer br = new StringBuffer();
		try
		{
			br.append(" 商户存根   请保存票据" + "\n");
			br.append("===  ===  ===  ===  ===" + "\n");
			br.append("票号：" + GlobalInfo.syjStatus.fphm + "\n");
			br.append("商户：" + GlobalInfo.sysPara.mktcode + "  " + GlobalInfo.sysPara.mktname + "\n");
			br.append("收银机：" + GlobalInfo.syjDef.syjh + "\n");
			br.append("操作员：" + GlobalInfo.posLogin.gh + "\n");
			br.append("卡号：" + bld.cardno + "\n");
			br.append("交易：" + bld.je + "  余额：" + bld.kye + "\n");
			br.append("日期：" + ManipulateDateTime.getCurrentDate() + "  " + ManipulateDateTime.getCurrentTime() + "\n");
			br.append("签名：" + "\n\n\n\n\n\n");

			return br.toString();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "";
		}
	}

	public boolean XYKWritePrint()
	{
		try
		{
			PrintWriter pw = null;
			String printName = "c:\\JavaPOS\\mzkcard\\tzxprint.txt";
			try
			{
				pw = CommonMethod.writeFileAppendGBK(printName);

				if (pw != null)
				{
					pw.println(billContent());
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
			new MessageBox("写入打印数据异常!\n\n" + ex.getMessage(), null, false);
			ex.printStackTrace();

			return false;
		}
	}

	public boolean XYKReadResult()
	{
		BufferedReader br = null;

		try
		{
			if (!PathFile.fileExist("c:\\JavaPOS\\mzkcard\\result.txt") || ((br = CommonMethod.readFileGBK("c:\\JavaPOS\\mzkcard\\result.txt")) == null))
			{
				XYKSetError("XX", "读取应答数据失败!");
				new MessageBox("读取应答数据失败!", null, false);

				return false;
			}

			String line = br.readLine();

			if (line.length() <= 0)
				return false;

			int type = Integer.parseInt(bld.type.trim());
			if (type == PaymentBank.XYKXF)
			{
				String result[] = line.split(",");
				if (result == null)
					return false;

				bld.retcode = result[0];

				if (result.length == 2)
				{
					bld.retmsg = result[1];
					return true;
				}

				if (result.length >= 1 && result[1] != null && !result[1].equals(""))
				{
					bld.cardno = result[1];
				}

				if (result.length >= 10 && result[10] != null && !result[10].equals(""))
				{
					bld.je = ManipulatePrecision.doubleConvert(Double.parseDouble(result[10]), 2, 1);
				}

				if (result.length >= 12 && result[12] != null && !result[12].equals(""))
				{
					bld.kye = ManipulatePrecision.doubleConvert(Double.parseDouble(result[12]), 2, 1);
				}

				return true;
			}
			return false;
		}
		catch (Exception ex)
		{
			XYKSetError("XX", "读取应答XX:" + ex.getMessage());
			new MessageBox("读取应答数据异常!" + ex.getMessage(), null, false);
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

					/*
					 * if
					 * (PathFile.fileExist("c:\\JavaPOS\\mzkcard\\request.txt"))
					 * {
					 * PathFile.deletePath("c:\\JavaPOS\\mzkcard\\request.txt");
					 * }
					 * 
					 * if
					 * (PathFile.fileExist("c:\\JavaPOS\\mzkcard\\result.txt"))
					 * {
					 * PathFile.deletePath("c:\\JavaPOS\\mzkcard\\result.txt");
					 * }
					 */

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
		if (GlobalInfo.sysPara.isprinticandmzk != 'Y')
			return;

		ProgressBox pb = null;
		BufferedReader br = null;
		String printName = "c:\\JavaPOS\\mzkcard\\tzxprint.txt";

		try
		{

			pb = new ProgressBox();
			pb.setText("正在打印消费凭证,请等待...");

			XYKPrintDoc_Start();

			br = CommonMethod.readFileGBK(printName);

			if (br == null)
			{
				new MessageBox("打开打印文件失败");
				return;
			}

			String line = null;

			while ((line = br.readLine()) != null)
				XYKPrintDoc_Print(line);

			XYKPrintDoc_End();

		}
		catch (Exception ex)
		{
			new MessageBox("打印签购单发生异常\n\n" + ex.getMessage());
			ex.printStackTrace();
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
					br = null;
				}
				catch (IOException e)
				{
					e.printStackTrace();
					br = null;
				}
			}

			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}
	}
}