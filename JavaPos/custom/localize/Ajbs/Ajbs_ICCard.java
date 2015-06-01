package custom.localize.Ajbs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

public class Ajbs_ICCard
{
	public static Ajbs_ICCard iccard = new Ajbs_ICCard();

	private double originalJF;

	public void clear()
	{
		originalJF = 0.0;
	}

	public double getOriginalJF()
	{
		return originalJF;
	}

	public static Ajbs_ICCard getDefault()
	{
		return iccard;
	}

	public Ajbs_ICCard()
	{

	}

	private void clearMidFile()
	{
		if (PathFile.fileExist("C:\\JavaPos\\ICCARD\\request.txt"))
			PathFile.deletePath("C:\\JavaPos\\ICCARD\\request.txt");

		if (PathFile.fileExist("C:\\JavaPos\\ICCARD\\result.txt"))
			PathFile.deletePath("C:\\JavaPos\\ICCARD\\result.txt");
	}

	// 查找会员
	public boolean getCustomer(CustomerDef cust)
	{
		try
		{
			if (!getCustomerWriteRequest())
				return false;

			// 调用接口模块
			if (PathFile.fileExist("c:\\JavaPOS\\ICCARD\\javaposIC.exe"))
			{
				CommonMethod.waitForExec("c:\\JavaPOS\\ICCARD\\javaposIC.exe AJIC");
			}
			else
			{
				new MessageBox("查找会员失败\r\n系统找不到会员查找模块!");
				return false;
			}

			// 读取应答数据
			if (!getCustomerReadResult(cust))
				return false;

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox(ex.getMessage());
			return false;
		}
	}

	private boolean getCustomerReadResult(CustomerDef cust)
	{
		BufferedReader br = null;

		try
		{
			if (!PathFile.fileExist("C:\\JavaPos\\ICCARD\\result.txt") || ((br = CommonMethod.readFileGBK("C:\\JavaPos\\ICCARD\\result.txt")) == null))
			{
				new MessageBox("查找会员失败\r\n读取会员返回信息失败!", null, false);
				return false;
			}

			// 读取请求数据
			String line = br.readLine();

			String[] strs = line.split(",");
			if (strs == null || strs.length < 10)
			{
				new MessageBox("查找会员失败\r\n会员信息返回格式有误!", null, false);
				return false;
			}

			// ACardNo Out(20) 卡号
			// ACardSbh Out(40) 识别号
			// ACardType Out(4) 卡类型编号
			// ACardTypeName Out(30) 卡类型名称
			// ACardGrade Out(4) 卡等级编号
			// ACardGradeName Out(60) 卡等级名称
			// ACardMoney Out 卡总余额
			// ACardMoneyShop Out 当前店可消费余额
			// AJf Out 卡总积分

			if (!"00".equals(strs[0])) { return false; }

			cust.code = strs[1];
			cust.name = "";
			cust.track = strs[2];
			cust.type = strs[3];
			cust.ishy = 'Y';
			cust.isjf = 'Y';
			cust.iszk = 'Y';
			cust.zkl = 1;
			cust.ispay = true;
			cust.maxdate = "";
			cust.status = "Y";
			cust.maxdate = ManipulateDateTime.getCurrentDateBySign();
			
			originalJF = ManipulatePrecision.doubleConvert(Convert.toDouble(strs[9]), 2, 1);
			
			if (cust.code.trim().length() <= 0)
				return false;

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("查找会员失败\r\n读取应答数据异常!" + ex.getMessage(), null, false);
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
					new MessageBox("删除临时文件失败!");
					e.printStackTrace();
				}
			}
		}
	}

	private boolean getCustomerWriteRequest()
	{
		try
		{
			String line = "4";
			PrintWriter pw = null;

			try
			{
				clearMidFile();

				pw = CommonMethod.writeFile("c:\\JavaPOS\\ICCARD\\request.txt");

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
					pw = null;
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
	}

	// 积分
	public boolean baoYuanJF(SaleHeadDef saleHead, double bcjf, double ljjf)
	{
		try
		{
			if (!getCustomerSellJfWriteRequest(saleHead, bcjf, ljjf))
			{
				new MessageBox("会员积分失败\r\n写入积分请求参数失败!");
				return false;
			}

			// 调用接口模块
			if (PathFile.fileExist("c:\\JavaPOS\\ICCARD\\javaposIC.exe"))
			{
				CommonMethod.waitForExec("c:\\JavaPOS\\ICCARD\\javaposIC.exe AJIC");
			}
			else
			{
				new MessageBox("会员积分失败\r\n找不到积分模块!");
				return false;
			}

			// 读取应答数据
			if (!getCustomerSellJfReadResult(saleHead))
			{
				new MessageBox("会员积分失败\r\n读取应答数据失败!", null, false);
				return false;
			}

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("会员积分失败\r\n获取积分发生异常");
			return false;
		}
	}

	private boolean getCustomerSellJfReadResult(SaleHeadDef saleHead)
	{
		BufferedReader br = null;

		try
		{
			if (!PathFile.fileExist("C:\\JavaPos\\ICCARD\\result.txt") || ((br = CommonMethod.readFileGBK("C:\\JavaPos\\ICCARD\\result.txt")) == null))
			{
				new MessageBox("会员积分失败\r\n读取会员返回信息失败!", null, false);
				return false;
			}

			// 读取请求数据
			String line = br.readLine();

			String[] strs = line.split(",");
			if (strs.length < 13)
			{
				new MessageBox("会员积分失败\r\n返回格式错误!\r\n", null, false);
				return false;
			}

			// ACardNo Out(20) 卡号
			// ACardSbh Out(40) 识别号
			// ACardType Out(4) 卡类型编号
			// ACardTypeName Out(30) 卡类型名称
			// ACardGrade Out(4) 卡等级编号
			// ACardGradeName Out(60) 卡等级名称
			// ACardMoney Out 卡总余额
			// ACardMoneyShop Out 当前店可消费余额
			// AJf Out 卡总积分

			if (!"00".equals(strs[0])) { return false; }

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("会员积分失败\r\n读取应答数据异常!" + ex.getMessage(), null, false);
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
					new MessageBox("删除临时文件异常");
					e.printStackTrace();
				}
			}
		}
	}

	private boolean getCustomerSellJfWriteRequest(SaleHeadDef saleHead, double bcjf, double ljjf)
	{
		try
		{
			/*
			 * //In 小票号 char APno[200]; memset(APno,0,200);
			 * strncpy(APno,szRequest[1],200); //In 工号 char
			 * AWorker[200];memset(AWorker,0,200);
			 * strncpy(AWorker,szRequest[2],200); //In 卡号 char
			 * ACardNo[200];memset(ACardNo,0,200); //Both　
			 * 传入刷卡金额(可折扣部分)，传出实际刷卡金额，比例折扣卡，刷卡５元，９７折，实刷4.85(5 * 0.97) double
			 * APayMoney = 0; //Both
			 * 传入刷卡金额(不可折扣部分)，传出实际刷卡金额，比例折扣卡，刷卡５元，９７折，实刷5(5 * 不打折) double
			 * APayMoneyNoZk = 0; //In 积分卡号： char
			 * AJFCardNo[200];memset(AJFCardNo,0,200);
			 * strncpy(AJFCardNo,ACardNo1,200); //In 积分金额： double AJFMoney = 0;
			 * //Out 原卡金额 double AOLdMoney = 0; //Out 现卡金额 double ANowMoney = 0;
			 * //Out 原卡积分 double AOldJF = 0; //Out 本次积分 double AADDJF = 0; //In
			 * 是否有卡积分，有卡积分时传入1,无卡时传入0，如果此参数未用到，则最好传入值1 char
			 * AJfCardValid[200];memset(AJfCardValid,0,200);
			 * strncpy(AJfCardValid,"1",1); //In 乐透券信息: 格式为
			 * DLTNO;DBARCODE;DMZ;DNUM;DMONEY;,限制：每单最多使用２０种乐透券 char
			 * ALtInfo[200];memset(ALtInfo,0,200); //In *未启用 char
			 * AJfGUID[200];memset(AJfGUID,0,200); //In *未启用 double AJFBL = 0;
			 * //Out *未启用 char AIfBack[200];memset(AIfBack,0,200);
			 */

			clearMidFile();

			String line = "";
			String type = "2";
			String fphm = saleHead.syjh + "-" + String.valueOf(saleHead.fphm);
			String syyh = saleHead.syyh;
			// String bcjf = String.valueOf(saleHead.bcjf);

			line = type + "," + fphm + "," + syyh + "," + String.valueOf(bcjf);
			PrintWriter pw = null;

			try
			{
				pw = CommonMethod.writeFile("c:\\JavaPOS\\ICCARD\\request.txt");

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
					pw = null;
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
	}

	// 充值
	public void cardRecharge()
	{
		ProgressBox progress = null;
		try
		{
			progress = new ProgressBox();
			progress.setText("正在对IC卡进行充值，请稍等.....");

			if (!writeCardRechargeRequest())
				return;

			// 调用接口模块
			if (PathFile.fileExist("c:\\JavaPOS\\ICCARD\\javaposIC.exe"))
			{
				CommonMethod.waitForExec("c:\\JavaPOS\\ICCARD\\javaposIC.exe AJIC");
			}
			else
			{
				new MessageBox("充值失败\r\n系统找不到IC卡充值模块!");
				return;
			}

			// 读取应答数据
			readRechargeResult();

		}
		catch (Exception ex)
		{
			ex.getMessage();
		}
		finally
		{
			if (progress != null)
				progress.close();
		}
		return;
	}

	private boolean writeCardRechargeRequest()
	{
		PrintWriter pw = null;
		String line = "3";

		try
		{
			clearMidFile();

			line += "," + GlobalInfo.posLogin.gh;

			pw = CommonMethod.writeFile("c:\\JavaPOS\\ICCARD\\request.txt");

			if (pw != null)
			{
				pw.println(line);
				pw.flush();
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			if (pw != null)
			{
				pw.close();
				pw = null;
			}
		}
	}

	public static void main(String[] args)
	{
		Ajbs_ICCard card = new Ajbs_ICCard();
		card.readRechargeResult();

	}

	private boolean readRechargeResult()
	{
		BufferedReader br = null;
		String printLine = null;

		try
		{
			if (!PathFile.fileExist("C:\\JavaPos\\ICCARD\\result.txt") || ((br = CommonMethod.readFileGBK("C:\\JavaPos\\ICCARD\\result.txt")) == null))
			{
				new MessageBox("充值失败\r\n读取充值返回信息失败!", null, false);
				return false;
			}

			printLine = br.readLine();

			if (printLine == null)
				return false;

			String[] strs = printLine.split(",");

			if (strs.length < 2 || !"00".equals(strs[0]))
			{
				new MessageBox(printLine, null, false);
				return false;
			}

			new MessageBox("充值成功");
			printLine = printLine.substring(3, printLine.length()) + "\n";
			String line = null;
			while ((line = br.readLine()) != null)
			{
				if (line.indexOf(",") != -1)
					line = line.replace(",", "");

				printLine += line + "\n";
			}

			if (writePrint(printLine))
				printBill();

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("充值失败\r\n读取应答数据异常!" + ex.getMessage(), null, false);
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
					new MessageBox("删除临时文件失败!");
					e.printStackTrace();
				}
			}
		}
	}

	// 轧帐
	public void cardRolling()
	{
		ProgressBox progress = null;
		try
		{
			progress = new ProgressBox();
			progress.setText("正在对IC卡进行轧账，请稍等.....");

			if (!writeCardRollingRequest())
				return;

			// 调用接口模块
			if (PathFile.fileExist("c:\\JavaPOS\\ICCARD\\javaposIC.exe"))
			{
				CommonMethod.waitForExec("c:\\JavaPOS\\ICCARD\\javaposIC.exe AJIC");
			}
			else
			{
				new MessageBox("轧账失败\r\n系统找不到IC卡轧账模块!");
				return;
			}

			// 读取应答数据
			readRollingResult();

			progress.close();
			progress = null;

		}
		catch (Exception ex)
		{
			ex.getMessage();
		}
		finally
		{
			if (progress != null)
				progress.close();
		}
		return;

	}

	private boolean writeCardRollingRequest()
	{
		PrintWriter pw = null;
		String line = "6";
		try
		{
			clearMidFile();

			line += "," + GlobalInfo.posLogin.gh;

			pw = CommonMethod.writeFile("c:\\JavaPOS\\ICCARD\\request.txt");

			if (pw != null)
			{
				pw.println(line);
				pw.flush();
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			if (pw != null)
			{
				pw.close();
				pw = null;
			}
		}
	}

	private boolean readRollingResult()
	{
		BufferedReader br = null;
		String printLine = null;

		try
		{
			if (!PathFile.fileExist("C:\\JavaPos\\ICCARD\\result.txt") || ((br = CommonMethod.readFileGBK("C:\\JavaPos\\ICCARD\\result.txt")) == null))
			{
				new MessageBox("轧帐失败\r\n读取轧帐返回信息失败!", null, false);
				return false;
			}

			printLine = br.readLine();

			if (printLine == null)
				return false;

			String[] strs = printLine.split(",");

			if (strs.length < 2 || !"00".equals(strs[0]))
			{
				new MessageBox(printLine, null, false);
				return false;
			}

			new MessageBox("轧帐成功");

			printLine = printLine.substring(3, printLine.length()) + "\n";
			String line = null;
			while ((line = br.readLine()) != null)
			{
				if (line.indexOf(",") != -1)
					line = line.replace(",", "");

				printLine += line + "\n";
			}

			if (writePrint(printLine))
				printBill();

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("充值失败\r\n读取应答数据异常!" + ex.getMessage(), null, false);
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
					new MessageBox("删除临时文件失败!");
					e.printStackTrace();
				}
			}
		}
	}

	// 零钞转存
	public boolean saveChangeMoney(double yje, String syyh, double money)
	{
		try
		{
			if (!writeChangeMoneyRequest(syyh, money))
				return false;

			// 调用接口模块
			if (PathFile.fileExist("c:\\JavaPOS\\ICCARD\\javaposIC.exe"))
			{
				CommonMethod.waitForExec("c:\\JavaPOS\\ICCARD\\javaposIC.exe AJIC");
			}
			else
			{
				new MessageBox("零超转存失败\r\n系统找不到IC卡模块!");
				return false;
			}

			return readChangeMoneyResult(yje);

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return true;
		}

	}

	private boolean writeChangeMoneyRequest(String syyh, double money)
	{
		PrintWriter pw = null;
		String line = "5";
		try
		{
			clearMidFile();

			line += "," + syyh + "," + String.valueOf(money);

			pw = CommonMethod.writeFile("c:\\JavaPOS\\ICCARD\\request.txt");

			if (pw != null)
			{
				pw.println(line);
				pw.flush();
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			if (pw != null)
			{
				pw.close();
				pw = null;
			}
		}

	}

	private boolean readChangeMoneyResult(double yje)
	{
		BufferedReader br = null;
		String oldMoney = "";
		String nowMoney = "";
		String curMoney = "";
		String leaveMoney = "";

		try
		{
			if (!PathFile.fileExist("C:\\JavaPos\\ICCARD\\result.txt") || ((br = CommonMethod.readFileGBK("C:\\JavaPos\\ICCARD\\result.txt")) == null))
			{
				new MessageBox("零钞转存失败\r\n读取零钞转存返回信息失败!", null, false);
				return false;
			}

			// 读取请求数据
			String line = br.readLine();

			String[] strs = line.split(",");

			if (strs == null)
			{
				new MessageBox("零钞转存失败");
				return false;
			}

			if (!"00".equals(strs[0]) || strs.length < 10)
			{
				new MessageBox("零钞转存失败");
				return false;
			}

			curMoney = String.valueOf(ManipulatePrecision.doubleConvert(Double.parseDouble(strs[7].trim()), 2, 2));
			oldMoney = String.valueOf(ManipulatePrecision.doubleConvert(Double.parseDouble(strs[8].trim()), 2, 2));
			nowMoney = String.valueOf(ManipulatePrecision.doubleConvert(Double.parseDouble(strs[9].trim()), 2, 2));
			leaveMoney = String.valueOf(ManipulatePrecision.doubleConvert(yje - Double.parseDouble(strs[7].trim()), 2, 2));
			/*
			 * curMoney = strs[7].trim().substring(0,strs[7].trim().length()-2);
			 * oldMoney = strs[8].trim().substring(0,strs[8].trim().length()-2);
			 * nowMoney = strs[9].trim().substring(0,strs[9].trim().length()-2);
			 */

			StringBuffer sb = new StringBuffer();
			sb.append("  总找零金额:" + Convert.appendStringSize("", String.valueOf(yje), 0, 10, 10, 2) + "\n\n");
			sb.append("    转存金额:" + Convert.appendStringSize("", curMoney, 0, 10, 10, 2) + "\n");
			sb.append("  转存前金额:" + Convert.appendStringSize("", oldMoney, 0, 10, 10, 2) + "\n");
			sb.append("  转存后金额:" + Convert.appendStringSize("", nowMoney, 0, 10, 10, 2) + "\n\n");
			sb.append("剩余找零金额:" + Convert.appendStringSize("", leaveMoney, 0, 10, 10, 2));

			new MessageBox(sb.toString());

			if (writePrint(strs[10]))
				printBill();

			return true;
		}
		catch (Exception ex)
		{
			return false;
		}
		finally
		{
			try
			{
				if (br != null)
					br.close();
				br = null;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				br = null;
			}
		}
	}

	// 打印
	public void printBill()
	{
		ProgressBox pb = null;
		BufferedReader br = null;

		try
		{
			String printName = "";

			if (!PathFile.fileExist("c:\\JavaPOS\\ICCard\\Print.txt"))
			{
				new MessageBox("找不到IC卡打印文件!");

				return;
			}
			else
			{
				printName = "c:\\JavaPOS\\ICCard\\Print.txt";
			}

			pb = new ProgressBox();
			pb.setText("正在打印IC卡凭证,请等待...");

			Printer.getDefault().startPrint_Journal();

			try
			{
				br = CommonMethod.readFileGB2312(printName);

				if (br == null)
				{
					new MessageBox("打开" + printName + "失败!");

					return;
				}

				String line = null;

				while ((line = br.readLine()) != null)
					Printer.getDefault().printLine_Journal(line);
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
			Printer.getDefault().cutPaper_Journal();

			pb.close();
			pb = null;
		}
		catch (Exception ex)
		{
			new MessageBox("打印IC卡凭证发生异常\n\n" + ex.getMessage());
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

	private boolean writePrint(String content)
	{
		PrintWriter pw = null;
		try
		{
			if (PathFile.fileExist("c:\\JavaPOS\\ICCard\\print.txt"))
			{
				PathFile.deletePath("c:\\JavaPOS\\ICCard\\print.txt");
			}

			pw = CommonMethod.writeFile("c:\\JavaPOS\\ICCard\\print.txt");
			if (pw != null)
			{
				pw.println(content);
				pw.flush();
			}
			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("写入打印数据异常!\n\n" + ex.getMessage(), null, false);
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (pw != null)
			{
				pw.close();
			}
		}
	}

}
