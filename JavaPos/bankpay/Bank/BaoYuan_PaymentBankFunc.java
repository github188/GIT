package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.SalePayDef;

public class BaoYuan_PaymentBankFunc extends PaymentBankFunc
{
	public static boolean isOneBill = false;
	public static int printcount = 0;

	private SaleBS saleBS = null;

	public String[] getFuncItem()
	{
		String[] func = new String[2];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKYE + "]" + "查询";

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
			case PaymentBank.XYKYE: // 查询
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "查询";
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
			case PaymentBank.XYKYE: // 查询
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键进行查询";
				break;
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKYE) && (type != PaymentBank.XKQT2))
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

			// 先删除上次交易数据文件
			if (PathFile.fileExist("c:\\JavaPOS\\ICCard\\request.txt"))
			{
				PathFile.deletePath("c:\\JavaPOS\\ICCard\\request.txt");

				if (PathFile.fileExist("c:\\JavaPOS\\ICCard\\request.txt"))
				{
					errmsg = "交易请求文件request.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist("c:\\JavaPOS\\ICCard\\result.txt"))
			{
				PathFile.deletePath("c:\\JavaPOS\\ICCard\\result.txt");

				if (PathFile.fileExist("c:\\JavaPOS\\ICCard\\result.txt"))
				{
					errmsg = "交易请求文件result.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			// 新交易
			if (!isOneBill && PathFile.fileExist("c:\\JavaPOS\\ICCard\\tzxprint.txt"))
			{
				PathFile.deletePath("c:\\JavaPOS\\ICCard\\tzxprint.txt");

				if (PathFile.fileExist("c:\\JavaPOS\\ICCard\\tzxprint.txt"))
				{
					errmsg = "上笔天中行打印文件tzxprint.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			// 消费时才判断
			if (type == PaymentBank.XYKXF && PathFile.fileExist("c:\\JavaPOS\\ICCard\\icbak"))
			{
				new MessageBox("系统发现上笔IC卡交易异常,稍后将发起扣款校验");
				PrintWriter pw = null;
				BufferedReader br = null;

				try
				{
					br = CommonMethod.readFileGBK("c:\\JavaPOS\\ICCard\\icbak");
					String data = br.readLine();

					new MessageBox("check data:" + data);
					
					if (data == null)
					{
						new MessageBox("扣款校验数据异常,请联系服务台");
						return false;
					}

					pw = CommonMethod.writeFile("c:\\JavaPOS\\ICCard\\request.txt");
					if (pw != null)
					{
						pw.println(data);
						pw.flush();
					}
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
				finally
				{
					if (br != null)
					{
						br.close();
						br = null;
					}

					if (pw != null)
					{
						pw.close();
						pw = null;
					}
				}
			}
			else
			{
				new MessageBox("警  告\n程序若无响应,请耐心等待10秒,切勿重启POS");
				
				if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
					return false;
			}

			if (bld.retbz != 'Y')
			{

				// 调用接口模块
				if (PathFile.fileExist("c:\\JavaPOS\\ICCARD\\javaposIC.exe"))
				{
					CommonMethod.waitForExec("c:\\JavaPOS\\ICCARD\\javaposIC.exe AJIC");
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

				if (type == PaymentBank.XYKYE)
					return checkBankSucceed();
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
		printcount++;

		if (GlobalInfo.syjDef.printfs == '1')
		{
			isOneBill = true;
			super.setOnceXYKPrintDoc(false);
			return false;
		}
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
				case PaymentBank.XYKYE:
					line = "0";
					break;
			}

			PrintWriter pw = null;
			PrintWriter bak = null;
			try
			{
				pw = CommonMethod.writeFile("c:\\JavaPOS\\ICCard\\request.txt");
				if (pw != null)
				{
					pw.println(line);
					pw.flush();
				}

				// 消费时才写备份
				if (type == PaymentBank.XYKXF)
				{
					bak = CommonMethod.writeFile("c:\\JavaPOS\\ICCard\\icbak");
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
			String printName = "c:\\JavaPOS\\ICCard\\tzxprint.txt";
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
			if (!PathFile.fileExist("c:\\JavaPOS\\ICCard\\result.txt") || ((br = CommonMethod.readFileGBK("c:\\JavaPOS\\ICCard\\result.txt")) == null))
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

				if (result.length >= 9 && result[9] != null && !result[9].equals(""))
				{
					bld.je = ManipulatePrecision.doubleConvert(Double.parseDouble(result[9]), 2, 1);
				}

				if (result.length >= 11 && result[11] != null && !result[11].equals(""))
				{
					bld.kye = ManipulatePrecision.doubleConvert(Double.parseDouble(result[11]), 2, 1);
				}

				if (PathFile.fileExist("c:\\JavaPOS\\ICCard\\icbak"))
				{
					PathFile.deletePath("c:\\JavaPOS\\ICCard\\icbak");
				}

			}

			if (type == PaymentBank.XYKYE)
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
			}
			return true;
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
					 * (PathFile.fileExist("c:\\JavaPOS\\ICCard\\request.txt"))
					 * {
					 * PathFile.deletePath("c:\\JavaPOS\\ICCard\\request.txt");
					 * }
					 * 
					 * if
					 * (PathFile.fileExist("c:\\JavaPOS\\ICCard\\result.txt")) {
					 * PathFile.deletePath("c:\\JavaPOS\\ICCard\\result.txt"); }
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
		String printName = "c:\\JavaPOS\\ICCard\\tzxprint.txt";

		try
		{
			if ((printcount > 0 && !PathFile.fileExist(printName)) || printcount == 0)
				return;

			pb = new ProgressBox();
			pb.setText("正在打印消费凭证,请等待...");

			XYKPrintDoc_Start();

			br = CommonMethod.readFileGBK(printName);

			if (br == null && printcount == 0)
			{
				new MessageBox("打开打印文件失败");
				return;
			}

			String line = null;

			while ((line = br.readLine()) != null)
				XYKPrintDoc_Print(line);

			XYKPrintDoc_End();

			printcount--;
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

			if (isOneBill)
			{
				isOneBill = false;
				if (PathFile.fileExist(printName))
				{
					PathFile.deletePath(printName);
					if (PathFile.fileExist(printName))
						new MessageBox("天中行打印文件tzxprint.txt无法删除,请手工删除");
				}
			}
		}
	}
}
