package bankpay.Bank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

public class Zsjt_PaymentBankFunc extends PaymentBankFunc
{
	public String[] getFuncItem()
	{
		String[] func = new String[8];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[3] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
		func[4] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		//func[5] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[5] = "[" + PaymentBank.XYKCD + "]" + "重打上一笔签购单";
		func[6] = "[" + PaymentBank.XKQT1 + "]" + "重印小票交易账单";
		func[7] = "[" + PaymentBank.XKQT2 + "]" + "重印上笔结账单";

		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		// 0-4对应FORM中的5个输入框
		// null表示该不用输入
		switch (type)
		{
			case PaymentBank.XYKXF: // 消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = "请 刷 卡";
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKCX: // 消费撤销
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = "请 刷 卡";
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKQD: // 交易签到
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";

				break;

			case PaymentBank.XYKYE: // 余额查询
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = "请 刷 卡";
				grpLabelStr[4] = "余额查询";

				break;

			case PaymentBank.XYKJZ: // 当班结算
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易结算";

				break;

			case PaymentBank.XYKTH: // 隔日退货，暂不支持
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = null;

				break;

			case PaymentBank.XYKCD: // 签购单重打
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打上笔签购单";

				break;
				
			case PaymentBank.XKQT1://重印小票交易账单
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重印签购单";
				
				break;
				
			case PaymentBank.XKQT2://重印上笔结账单
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重印结账单";
				
				break;
				
		}

		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		// 0-4对应FORM中的5个输入框
		// null表示该需要用户输入,不为null用户不输入
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

			case PaymentBank.XYKYE: // 余额查询
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始余额查询";

				break;

			case PaymentBank.XYKQD: // 交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易签到";

				break;

			case PaymentBank.XYKTH: // 隔日退货，暂不支持
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;

			case PaymentBank.XYKJZ: // 结 算
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始结算";

				break;

			case PaymentBank.XYKCD: // 签购单重打
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车重打上一笔签购单";

				break;
				
			case PaymentBank.XKQT1://重印小票交易账单
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车开始重印小票交易账单";
				
				break;
				
			case PaymentBank.XKQT2://重印上笔结账单
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车开始重印上笔结账单";
				
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
			writeBankFileLog("XYKExecute开始...");
			// 判断交易类型是否签到，消费，撤消，查余，结算，重打签购单，不支持退货
			if (type == PaymentBank.XYKQD || type == PaymentBank.XYKXF
					|| type == PaymentBank.XYKCX || type == PaymentBank.XYKYE
					|| type == PaymentBank.XYKJZ || type == PaymentBank.XYKCD)
			{

				writeBankFileLog("删除request.txt文件");
				// 先删除上次交易数据文件
				if (PathFile.fileExist("c:\\javapos\\request.txt"))
				{
					PathFile.deletePath("c:\\javapos\\request.txt");

					if (PathFile.fileExist("c:\\javapos\\request.txt"))
					{
						errmsg = "交易请求文件request.txt无法删除,请重试";
						XYKSetError("XX", errmsg);
						new MessageBox(errmsg);
						return false;
					}
				}

				writeBankFileLog("删除result.txt文件");
				if (PathFile.fileExist("c:\\javapos\\result.txt"))
				{
					PathFile.deletePath("c:\\javapos\\result.txt");

					if (PathFile.fileExist("c:\\javapos\\result.txt"))
					{
						errmsg = "交易请求文件result.txt无法删除,请重试";
						XYKSetError("XX", errmsg);
						new MessageBox(errmsg);
						return false;
					}
				}

				writeBankFileLog("写入请求数据");
				// 写入请求数据
				if (!XYKWriteRequest(type, money, track1, track2, track3,
						oldseqno, oldauthno, olddate, memo))
				{
					return false;
				}

				writeBankFileLog("开始调用接口模块");
				// 调用接口模块
				if (PathFile.fileExist("c:\\javapos\\javaposbank.exe"))
				{
					CommonMethod
							.waitForExec("c:\\javapos\\javaposbank.exe YLSWZS");
					writeBankFileLog("调用接口模块结束");
				}
				else
				{
					writeBankFileLog("找不到金卡工程模块 javaposbank.exe");
					new MessageBox("找不到金卡工程模块 javaposbank.exe");
					XYKSetError("XX", "找不到金卡工程模块 javaposbank.exe");
					return false;
				}
				writeBankFileLog("读取应答数据");
				// 读取应答数据
				if (!XYKReadResult())
				{
					return false;
				}

				writeBankFileLog("检查交易是否成功");
				// 检查交易是否成功
				XYKCheckRetCode();

				writeBankFileLog("生成签购单");
				// 打印签购单
				if (XYKNeedPrintDoc())
				{
					XYKPrintDoc();
				}
			}
			else if (type == PaymentBank.XKQT1)
			{
				writeBankFileLog("重印指定小票签购单");
				//重印指定小票签购单
				c_printOtherDoc();
			}
			else if (type == PaymentBank.XKQT2)
			{
				writeBankFileLog("重印上笔结账单");
				//重印上笔结账单
				if (c_printDoc("c:\\javapos\\temp_prn_JS.txt"))
				{
					bld.retcode = "";
					bld.retmsg = "操作成功";
				}
				else
				{
					bld.retcode = "EF";
					bld.retmsg = "操作失败";
				}
			}
			return true;
		}
		catch (Exception ex)
		{
			writeBankFileLog("XYKExecute异常:" + ex.toString());
			ex.printStackTrace();
			return false;
		}
		finally
		{
			writeBankFileLog("XYKExecute结束\n");
		}
	}
	
	//重印指定小票的所有BANK签购单
	private boolean c_printOtherDoc()
	{
		try
		{
			//重打印指定小票的签购单
			final StringBuffer fphm = new StringBuffer();
			if (new TextBox().open("请输入需要的:", "重打印的小票号", "小票号", fphm, -1, -1,false, TextBox.AllInput))
        	{
				if (fphm.toString().trim().length() > 0)
				{					
					//ConfigClass.LocalDBPath + "//Invoice//"
					//"Bankdoc_" + salehead.syjh + "_" + salehead.fphm + "_" + pay.batch + ".txt");
					File files = new File(ConfigClass.LocalDBPath + "//Invoice");
					String[] filelist = files.list();

					if (filelist != null)
					{

						FilenameFilter filter = new FilenameFilter() 
						{

						    public boolean accept(File dir, String name) 
							{
						    	try
						    	{
							    	return name.startsWith("Bankdoc_" + bld.syjh + "_" + fphm.toString() + "_");
						    	}
						    	catch(Exception ex)
						    	{
						    		ex.printStackTrace();
						    		return true;
						    	}
						    	
							}

						};
							
						filelist = files.list(filter);
						return c_printOtherDocToFile(filelist);
					}
					else
					{
						bld.retcode = "EF";
						bld.retmsg = "失败,未找到签购单信息";
					}
				}
				else
				{
					bld.retcode = "EF";
					bld.retmsg = "失败,不合法的小票号";
				}				
									
        	}
			else
			{
				bld.retcode = "EF";
				bld.retmsg = "失败,操作被取消";
			}
			
		}
		catch(Exception ex)
		{
			bld.retcode = "EF";
			bld.retmsg = "失败,打印异常:" + ex.getMessage();
			ex.printStackTrace();
		}
		return false;
	}
	
	//直接打印:指定小票的签购单
	public boolean c_printOtherDocToFile(String[] printfilenamelist)
	{
		try
		{			
			XYKPrintDoc_Start();

			BufferedReader br = null;

			try
			{
				for (int i=0;i<printfilenamelist.length;i++)
				{

					br = CommonMethod.readFileGBK(ConfigClass.LocalDBPath + "//Invoice//" + printfilenamelist[i]);

					if (br == null)
					{
						bld.retcode = "EF";
						bld.retmsg = "失败,未找到签购单文件:" + printfilenamelist[i];
						new MessageBox("打开" + printfilenamelist[i] + "打印文件失败!");
						return false;
					}

					String line = null;

					while ((line = br.readLine()) != null)
					{
						XYKPrintDoc_Print(line);
					}
					
					if (br != null)
					{
						br.close();
					}
				}
			}
			catch (Exception e)
			{
				bld.retcode = "EF";
				bld.retmsg = "失败,打印异常:" + e.getMessage();
				e.printStackTrace();
				new MessageBox(e.getMessage());
				return false;
			}
			finally
			{
				if (br != null)
				{
					br.close();
				}
			}
			
			XYKPrintDoc_End();
			bld.retcode = "";
			bld.retmsg = "操作成功";
			return true;
			
		}
		catch (Exception ex)
		{
			bld.retcode = "EF";
			bld.retmsg = "失败,打印异常:" + ex.getMessage();
			ex.printStackTrace();
			new MessageBox("打印签购单发生异常\n\n" + ex.getMessage());
			return false;
		}
		
	}

	public boolean XYKNeedPrintDoc()
	{
		// 交易成功后方可打印
		if (!checkBankSucceed())
		{
			return false;
		}

		int type = Integer.parseInt(bld.type.trim());

		// 消费，消费撤销,结算,签购单重打
		if ((type == PaymentBank.XYKXF) || (type == PaymentBank.XYKCX)
				|| (type == PaymentBank.XYKJZ) || (type == PaymentBank.XYKCD))
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
		// 根据返回值置返回标志
		if (bld.retcode.equals("00"))
		{
			bld.retbz = 'Y';
			bld.retmsg = "金卡工程调用成功";

			return true;
		}
		else
		{
			bld.retbz = 'N';
			if (bld.type.equals(String.valueOf(PaymentBank.XYKCD)))
				bld.retmsg = "系统生成签购单失败";
			else
				bld.retmsg = XYKReadRetMsg(bld.retcode);// 根据返回码来查找错误信息

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
			errmsg = bld.retmsg;

			return true;
		}
	}

	public boolean XYKWriteRequest(int type, double money, String track1,
			String track2, String track3, String oldseqno, String oldauthno,
			String olddate, Vector memo)
	{
		try
		{
			String reqStr = "";

			if (type == PaymentBank.XYKCD)
			{
				reqStr = "1" + "," + "c:\\javapos\\";
			}
			else
			{
				String tratype = ""; // 交易类型

				switch (type)
				{
					case PaymentBank.XYKQD:
						tratype = String.valueOf("0");						
						break;
					case PaymentBank.XYKXF:
						tratype = String.valueOf("1");
						break;
					case PaymentBank.XYKCX:
						tratype = String.valueOf("2");
						break;
					case PaymentBank.XYKYE:
						tratype = String.valueOf("3");
						break;
					case PaymentBank.XYKJZ:
						tratype = String.valueOf("4");
						break;
					case PaymentBank.XYKTH: // 不被支持的业务
						break;
					default:
						break;

				}
				bld.type = String.valueOf(type);

				// 将交易所需数据全部写入文件
				String pin = String.valueOf("1"); // 是否需要密码 1-Y,0-N
				
				if (type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKYE)
				{
					//查余额/消费/撤消时,需要提示是否有密码
					int ret = new MessageBox("该卡是否有密码?",null,true).verify();
					if (ret == GlobalVar.Key1 || ret==GlobalVar.Enter)
					{
						pin = String.valueOf("1");
					}
					else
					{
						pin = String.valueOf("0");
					}
					
				}				
				
				String rspcode = ""; // 交易返回代码
				String trk2len = String.valueOf(track2.length()); // 2轨长度
				String trk3len = String.valueOf(track3.length()); // 3轨长度
				String trk2 = track2; // 2轨信息
				String trk3 = track3; // 3轨信息
				String amount = String.valueOf((long) ManipulatePrecision
						.doubleConvert(money * 100, 2, 1)); // 将金额转换为字符串
				amount = Convert.increaseCharForward(amount, '0', 12); // 填充至12位字符

				String casher = GlobalInfo.posLogin.gh; // 收银员号
				String counter = GlobalInfo.syjDef.syjh; // 收银机号
				String orgon = oldseqno; // 原交易凭证号(撤消用)
				String balance = ""; // 余额
				String old_reference = ""; // 原交易参考号(退货用)
				String old_date = ""; // 原交易日期(退货用)
				String old_batch = ""; // 原交易批次(退货用)
				String old_trace = ""; // 原交易凭证号(退货用)
				String cardno = ""; // 卡号
				String seq = ""; // 流水号
				String bankcode = ""; // 发卡行代号
				String bankname = ""; // 发卡行名称

				// 各字段间用','(逗号)分隔开
				reqStr = "0" + "," + "c:\\javapos\\" + "," + tratype + ","
						+ pin + "," + rspcode + "," + trk2len + "," + trk3len
						+ "," + trk2 + "," + trk3 + "," + amount + "," + casher
						+ "," + counter + "," + orgon + "," + balance + ","
						+ old_reference + "," + old_date + "," + old_batch
						+ "," + old_trace + "," + cardno + "," + seq + ","
						+ bankcode + "," + bankname;
			}

			PrintWriter pw = null;

			try
			{
				pw = CommonMethod.writeFile("c:\\javapos\\request.txt");
				if (pw != null)
				{
					pw.println(reqStr);
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
		// 判断是否重打印，重打印不生成请求和响应文件
		BufferedReader br = null;

		try
		{
			//new MessageBox("读取金卡工程应答数据开始...!", null, false);//test
			String[] ret;
			if (!PathFile.fileExist("c:\\javapos\\result.txt")
					|| ((br = CommonMethod.readFileGBK("c:\\javapos\\result.txt")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}
			
			//new MessageBox("读取金卡工程应答数据开始!", null, false);//test
			
			String line = br.readLine();

			if (line == null || line.length() <= 0)
			{
				return false;
			}

			ret = line.split(",", -1);
			
			if (ret.length >= 3)
			{
				bld.retcode = ret[3]; // 返回码
				bld.retmsg = XYKReadRetMsg(bld.retcode); // 返回信息
			}
			
			
			if (!ret[0].toString().equals("0"))//函数返回值为0时表示函数执行成功
			{		
				if (bld.type.equals(String.valueOf(PaymentBank.XYKCD)))
				{
					bld.retmsg = ret[0] + "|失败,未知错误.";
				}
				return false;
			}
			
			if (bld.type.equals(String.valueOf(PaymentBank.XYKCD)))
			{
				//签购单重打,当返回0时表示成功,否则视为失败
				bld.retcode = "00"; // 返回码
				bld.retmsg = XYKReadRetMsg("00"); // 返回信息
				return true;
			}
			
			if (!ret[3].toString().equals("00"))//交易返回值为00时表示交易成功
			{				
				return false;
			}
			
					

			/*if (bld.type.equals(String.valueOf(PaymentBank.XYKCD)))
			{
				bld.retcode = ret[3];
			}
			else
			{				
				///bld.cardno = ret[17].trim(); // 卡 号

				///bld.trace = Convert.toInt(ret[18].trim()); // 流水号
				
				if (!ret[19].equals("")) // 发卡行代号
					bld.bankinfo = XYKReadBankName(ret[19]);
				///bld.bankinfo = ret[20].trim();//发卡行名称
			}*/
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			XYKSetError("ex", "读取应答异常:" + ex.getMessage());
			new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
			br = null;
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
					e.printStackTrace();
					br = null;
				}
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

	public void XYKPrintDoc_Start()
	{
		if (onceprint)
		{
			
			Printer.getDefault().startPrint_Normal();
		}
		else
		{
			//printdoc = CommonMethod.writeFileUTF("bankdoc_" + String.valueOf(bld.trace) + ".txt");
		}
	}
	
	public void XYKPrintDoc_End()
	{
		if (onceprint)
		{
			//Printer.getDefault().cutPaper_Normal();
			switch(ConfigClass.RepPrintTrack)
			{
				case 1:
					Printer.getDefault().cutPaper_Normal();
					break;
				case 2:
					Printer.getDefault().cutPaper_Journal();
					break;
				case 3:
					Printer.getDefault().cutPaper_Slip();
					break;
				default:
					Printer.getDefault().cutPaper_Normal();
					break;
			}
		}
		else
		{
			printdoc.flush();
			printdoc.close();
			printdoc = null;
		}
	}

	public String XYKReadBankName(String bankcode)
	{

		String line = "";
		String bankname = "中国银联";
		BufferedReader br = null;

		try
		{
			if (!PathFile.fileExist("c:\\javapos\\cardbank.ini")
					|| (br = CommonMethod
							.readFileGBK("c:\\javapos\\cardbank.ini")) == null)
			{
				new MessageBox("未发现cardbank.ini", null, false);

				return bankname;
			}

			//
			while ((line = br.readLine()) != null)
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

				if (a[0].trim().equals(bankcode.trim()))
				{
					bankname = a[1].trim();
				}
			}
			return bankname;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return bankname;
		}
		finally
		{
			try
			{
				if (br != null)
					br.close();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				br = null;
			}
		}
	}

	public String XYKReadRetMsg(String retmsg)
	{
		String line = "";
		String msg = "未知错误";
		BufferedReader br = null;
		try
		{
			if (!PathFile.fileExist("c:\\javapos\\Rsp.ini")
					|| (br = CommonMethod.readFileGBK("c:\\javapos\\Rsp.ini")) == null)
			{
				new MessageBox("找不到rsp.ini", null, false);
				return msg;
			}

			while ((line = br.readLine()) != null)
			{
				if (line.length() <= 0)
				{
					continue;
				}
				if (line.startsWith("["))
				{
					continue;
				}

				String[] a = line.split("=");

				if (a.length < 2)
				{
					continue;
				}

				if (a[0].trim().equals(retmsg))
				{
					msg = a[1].trim();
					break;
				}
			}
			
			if (!retmsg.equals("00"))
			{
				msg = "交易失败:" + retmsg + "|" + msg;
			}
			return msg;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			br = null;
			return msg;
		}
		finally
		{
			try
			{
				if (br != null)
					br.close();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
				br = null;
			}
		}
	}


	//生成签购单文件
	private boolean c_createBankPrintInfo(String printfilename)
	{
		try
		{
			//银行签购单
			//第一行
			String strType = "交易类型:";
			String strJe = "交易金额:" + String.valueOf(bld.je) + "元";					
			String strCardno = "卡号:";
			
			//第二行
			String strBankinfo = "发卡行:";
			String strTrace = "流水:";
			String strMemo = "授权:";
			String strDatetime = "日期:";
			
			//第三行
			String strMCHT = "商户号:";
			String strTermno = "终端号:";
			String strText = "顾客签字:________";
			
			//XYKPrintDoc_Start();

			BufferedReader br = null;

			try
			{

				br = CommonMethod.readFileGBK(printfilename);

				if (br == null)
				{
					new MessageBox("打开" + printfilename + "打印文件失败!");

					return false;
				}

				String line = null;

				while ((line = br.readLine()) != null)
				{
					/*if (line.trim().equals("CUT"))
					{
						break;
					}

					XYKPrintDoc_Print(line);*/
					
					if (line.indexOf("商户号") >= 0 || line.indexOf("商 户 号") >= 0)
					{
						strMCHT = "商户号:";
						strMCHT += Convert.newSubString(line, 9, 24).trim();
					}
					else if (line.indexOf("终端号") >= 0 || line.indexOf("终 端 号") >= 0)
					{
						
						strTermno = "终端号:";
						strTermno += Convert.newSubString(line, 9, 17).trim();
					}
					else if (line.indexOf("发卡行") >= 0 || line.indexOf("发 卡 行") >= 0)
					{
						bld.bankinfo = Convert.newSubString(line, 9, 19).trim();
						strBankinfo = "发卡行:";
						strBankinfo += bld.bankinfo;
					}
					else if (line.indexOf("凭证号") >= 0)
					{
						if (Convert.countLength(strTrace) > 5)
						{
							continue;
						}
						bld.trace = Convert.toInt(Convert.newSubString(line, line.indexOf("凭证号")+10, Convert.countLength(line)).trim());
						strTrace = "流水:";
						strTrace += Convert.increaseCharForward(String.valueOf(bld.trace),'0', 6);
					}
					else if (line.indexOf("授权码") >= 0 || line.indexOf("授 权 码") >= 0)
					{				
						bld.memo = Convert.newSubString(line, 9, 15).trim();
						if (bld.memo.length() < 1 || bld.memo.equals("000000"))
						{
							bld.memo = "";
							strMemo = "";
						}
						else
						{
							strMemo = "授权:";
							strMemo += bld.memo;
						}
						
					}
					else if (line.indexOf("卡    号") >= 0)
					{						
						bld.cardno = Convert.newSubString(line, 9, Convert.countLength(line)).trim();	
						strCardno = "卡号:";
						strCardno += bld.cardno;
					}
					else if (line.indexOf("卡号") >= 0)
					{				
						bld.cardno = Convert.newSubString(line, 9, line.indexOf("有效期") + 1).trim();
						strCardno = "卡号:";
						strCardno += bld.cardno;
					}
					else if (line.indexOf("日期时间") >= 0)
					{				
						//bld.rqsj = Convert.newSubString(line, 9, Convert.countLength(line)).trim();
						strDatetime = "日期:";
						strDatetime += Convert.newSubString(line, 9, Convert.countLength(line)).trim();
						//bld.rqsj = bld.rqsj.replace('-', '/');
					}
				
				}
				
				
				if (bld.type.equals(String.valueOf(PaymentBank.XYKXF)))
				{
					strType = strType + "消费";
				}
				else if (bld.type.equals(String.valueOf(PaymentBank.XYKCX)))
				{
					strType = strType + "消费撤销";
				}
				else
				{
					strType = strType + "未知（" + bld.type.toString() + ")";
				}
						
				String strPrint = "";
				
				if (ConfigClass.CustomItem5.split("\\|").length > 0 && 
						(ConfigClass.CustomItem5.split("\\|")[0].trim().equalsIgnoreCase("ShiYan") 
						|| ConfigClass.CustomItem5.split("\\|")[0].trim().equalsIgnoreCase("SuiZhou")))
				{
					strPrint = strType + strJe + strCardno + "\n" + 
	                  strBankinfo + strTrace + strMemo + strDatetime + "\n" + 
	                  strMCHT + strTermno + strText;
				}
				else
				{
					//新打印机格式
					strPrint = strMCHT + strTermno + "\n" + 
					"款机:" + bld.syjh + strType + " " + strDatetime.split(" ")[0] + "\n" + 
					strCardno + strBankinfo.replace("发卡行:", "") + "\n" + 
					strMemo + strTrace + strJe + "\n" + 
					//strDatetime + "\n" + 
					"持卡人签名:________\n" +  
					"本人确认以上交易,并同意记入该帐户";
				}
				
				
				//bld.memo = strPrint;
									
				//XYKPrintDoc_Print(strPrint);
				c_writePrintDoc(strPrint);
				
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
			//XYKPrintDoc_End();
			return true;
		}
		catch(Exception ex)
		{
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

			if ((bld.type.equals(String.valueOf(PaymentBank.XYKXF))
					|| bld.type.equals(String.valueOf(PaymentBank.XYKCD))
					|| bld.type.equals(String.valueOf(PaymentBank.XYKJZ)) || bld.type
					.equals(String.valueOf(PaymentBank.XYKCX))))
			{
				if (!PathFile.fileExist("c:\\javapos\\temp_prn.txt"))
				{
					new MessageBox("未发现签购单打印文件!");
					// 若银联函数返回成功，但未生成签购单文件
					bld.retmsg = "银联扣款成功--但未生成签购单";
					return;
				}
				else
				{
					printName = "c:\\javapos\\temp_prn.txt";
				}
			}

			//区分:签购单/重印上一笔/结算单打印/
			pb = new ProgressBox();
			pb.setText("正在生成银联签购单,请等待...");

			if (bld.type.equals(String.valueOf(PaymentBank.XYKXF)) || bld.type.equals(String.valueOf(PaymentBank.XYKCX)))
			{
				try
				{
					//备份消费打印文件
					if (PathFile.fileExist("c:\\javapos\\temp_prn_PAY.txt"))
					{
						PathFile.deletePath("c:\\javapos\\temp_prn_PAY.txt");						
					}
					PathFile.copyPath("c:\\javapos\\temp_prn.txt", "c:\\javapos\\temp_prn_PAY.txt");
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
				
				//签购单:消费/撤消
				//只生成打印文件
				c_createBankPrintInfo(printName);
			}
			else if (bld.type.equals(String.valueOf(PaymentBank.XYKJZ)) || bld.type.equals(String.valueOf(PaymentBank.XYKCD)))
			{
				try
				{
					//备份结算打印文件
					if (PathFile.fileExist("c:\\javapos\\temp_prn_JS.txt"))
					{
						PathFile.deletePath("c:\\javapos\\temp_prn_JS.txt");
					}
					PathFile.copyPath("c:\\javapos\\temp_prn.txt", "c:\\javapos\\temp_prn_JS.txt");
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
								
				//结算/重印上一笔
				//直接打印
				c_printDoc(printName);
			}
			else
			{
				return;
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
	
	private boolean c_writePrintDoc(String strPrintBuffer)
	{
		try
		{
			String strDocDir = ConfigClass.LocalDBPath + "//Invoice";
			String strDocFile = strDocDir + "//Bankdoc_" + bld.syjh + "_" + bld.fphm + "_" + Convert.increaseCharForward(String.valueOf(bld.trace),'0', 6) + ".txt";
			try
			{
				/*if (!PathFile.isPathExists(strDocDir))
				{
					PathFile.createDir(strDocDir)
				}*/
				if (PathFile.fileExist(strDocFile))
				{
					PathFile.deletePath(strDocFile); 
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			
			PrintWriter pw = CommonMethod.writeFileAppendGBK(strDocFile);
			pw.print(strPrintBuffer.toString());
	        pw.flush();
	        pw.close();
			return true;
		}
		catch(Exception ex)
		{
			return false;
		}
	}
	
	//直接打印:重印/结算
	public boolean c_printDoc(String printfilename)
	{
		ProgressBox pb = null;

		try
		{
			if (!PathFile.fileExist(printfilename))
			{
				return false;
			}
			
			pb = new ProgressBox();
			pb.setText("正在打印银联单据,请等待...");
			
			XYKPrintDoc_Start();

			BufferedReader br = null;

			try
			{
				br = CommonMethod.readFileGBK(printfilename);

				if (br == null)
				{
					new MessageBox("打开" + printfilename + "打印文件失败!");

					return false;
				}

				String line = null;

				while ((line = br.readLine()) != null)
				{
					XYKPrintDoc_Print(line);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				new MessageBox(e.getMessage());
				return false;
			}
			finally
			{
				if (br != null)
				{
					br.close();
				}
			}
			
			XYKPrintDoc_End();
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("打印银联单据发生异常\n\n" + ex.getMessage());
			return false;
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

	private void writeBankFileLog(String content)
	{		
		writeBankFileLog(ConfigClass.LocalDBPath + "\\Invoice\\" + new ManipulateDateTime().getDateByEmpty() + "\\BankLog_" + new ManipulateDateTime().getDateByEmpty() + ".log", content);
	}
	
	//记录日志（追加）
	private void writeBankFileLog(String fileName, String content)
	{
		FileWriter writer = null;		
		try
		{			
			writer = new FileWriter(fileName, true);
			writer.write("[" + ManipulateDateTime.getCurrentTime() + "] " + content + "\n"); 
			writer.close(); 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if (writer != null)
				{
					writer.close();
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			
		}
	}
	
}
