package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.eclipse.swt.widgets.Display;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
//
public class ICBCXY_PaymentBankFunc extends ICBCKPC_PaymentBankFunc{

	
	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			String type1 = "";
			// 调用接口模块
			if (PathFile.fileExist(ConfigClass.BankPath+"\\javaposbank.exe"))
			{
				if (PathFile.fileExist(ConfigClass.BankPath+"\\ICBCPRTTKT.txt"))
				{
					PathFile.deletePath(ConfigClass.BankPath+"\\ICBCPRTTKT.txt");

					if (PathFile.fileExist(ConfigClass.BankPath+"\\ICBCPRTTKT.txt"))
					{
						errmsg = "交易请求文件ICBCPRTTKT.txt无法删除,请重试";
						XYKSetError("XX", errmsg);
						new MessageBox(errmsg);
						return false;
					}
				}
				
				if (PathFile.fileExist(ConfigClass.BankPath+"\\request.txt"))
				{
					PathFile.copyPath(ConfigClass.BankPath+"\\request.txt", ConfigClass.BankPath+"\\LastRequest.txt");
					PathFile.deletePath(ConfigClass.BankPath+"\\request.txt");

					if (PathFile.fileExist(ConfigClass.BankPath+"\\request.txt"))
					{
						errmsg = "交易请求文件request.TXT无法删除,请重试";
						XYKSetError("XX", errmsg);
						new MessageBox(errmsg);
						return false;
					}
				}

				if (PathFile.fileExist(ConfigClass.BankPath+"\\result.txt"))
				{
					PathFile.copyPath(ConfigClass.BankPath+"\\result.txt", ConfigClass.BankPath+"\\LastResult.txt");
					PathFile.deletePath(ConfigClass.BankPath+"\\result.txt");

					if (PathFile.fileExist(ConfigClass.BankPath+"\\result.txt"))
					{
						errmsg = "交易请求文件result.txt无法删除,请重试";
						XYKSetError("XX", errmsg);
						new MessageBox(errmsg);
						return false;
					}
				}
				String xftype = "";
				if (type == PaymentBank.XYKXF)
				{
						String[] title = { "代码", "交易类型" };
						int[] width = { 60, 440 };
						Vector contents = new Vector();
						contents.add(new String[] { "05", "普通消费" });
						contents.add(new String[] { "21", "快速消费" });
						
						int choice = new MutiSelectForm().open("请选择交易类型", title, width, contents, true);
						if (choice == -1)
						{
							errmsg = "没有选择消费交易类型";
							return false;
						}
						else
						{
							xftype = ((String[])contents.elementAt(choice))[0];
						}
		
						// 刷新界面
						while (Display.getCurrent().readAndDispatch())
							;
					
				}

				String line = "";

				
		
				switch (type)
				{
					case PaymentBank.XYKQD:
						type1 = "09";
						break;
					case PaymentBank.XYKXF:
						type1 = xftype;
						break;
					case PaymentBank.XYKCX:
						type1 = "04";
						break;
					case PaymentBank.XYKTH:
						type1 = "04";
						break;
					case PaymentBank.XYKYE:		// 查询余额
						type1 = "10";
						break;
					case PaymentBank.XYKCD:		// 重打上笔票据
						type1 = "13";
						break;
					case PaymentBank.XYKJZ:		// 交易结账
						type1 = "14";
						break;
					case PaymentBank.XKQT1:		// 重打指定票据
						type1 = "13";
						break;
					default:
						return false;
				}
				
				
				// 交易卡号【19】
				String cardno = Convert.increaseChar("", 19);
				// 交易金额【12】
				String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));
				jestr = Convert.increaseCharForward(jestr, '0', 12);
				// 消费金额【12】
				String Tip = Convert.increaseChar("", 12);
				// MIS批次号【6】
				String MisBatchNo = Convert.increaseChar("", 6);
				// MIS流水号【6】
				String MisTraceNo = Convert.increaseChar("", 6);
				// 交易时间【6】
				String TransTime = Convert.increaseChar("", 6);
				// 交易日期【8】
				String TransDate = Convert.increaseChar(olddate , 8);
				// 卡片有效期【4】
				String ExpDate = Convert.increaseChar("", 4);
				// 二磁道信息【37】
				String Track2 = Convert.increaseChar("", 37);
				// 三磁道信息【104】
				String Track3 = Convert.increaseChar("", 104);
				// 系统检索号【8】
				String ReferNo = Convert.increaseChar(oldseqno,'0' , 8);
				// MISPOS系统返回【6】
				String AuthNo = Convert.increaseChar("", 6);
				// 返回
				String retmsg = "";
				if(type == PaymentBank.XYKCX ){
					retmsg = ","+Convert.increaseChar("", 2)+
					","+Convert.increaseChar(oldauthno,' ', 15)+
					","+Convert.increaseChar("", 12)+
					","+Convert.increaseChar("", 15)+
					","+Convert.increaseChar("", 2)+
					","+Convert.increaseChar("",130)+
					","+TransDate+//当日撤销专用
					"," +Convert.increaseChar("", 50)+
					","+Convert.increaseChar("", 40)+
					","+Convert.increaseChar("", 6)+
					","+Convert.increaseChar("", 6)+
					","+Convert.increaseChar("", 4)+
					","+Convert.increaseChar("", 20)+
					","+Convert.increaseChar("", 20)+
					","+Convert.increaseChar("", 800)+
					","+Convert.increaseChar("", 1)+
					","+Convert.increaseChar("", 100)+
					","+Convert.increaseChar("", 300)+
					","+Convert.increaseChar("", 24)+
					","+Convert.increaseChar(ConfigClass.CashRegisterCode, 20)+
					","+Convert.increaseChar(GlobalInfo.posLogin.gh, 20);
				}
				else{
					retmsg = ","+Convert.increaseChar("", 2)+
								","+Convert.increaseChar(oldauthno,' ', 15)+
								","+Convert.increaseChar("", 12)+
								","+Convert.increaseChar("",15)+
								","+Convert.increaseChar("",2)+
								","+Convert.increaseChar("",130)+
								","+TransDate+
								"," +Convert.increaseChar("", 50)+
								","+Convert.increaseChar("", 40)+
								","+Convert.increaseChar("", 6)+
								","+Convert.increaseChar("", 6)+
								","+Convert.increaseChar("", 4)+
								","+Convert.increaseChar("", 20)+
								","+Convert.increaseChar("", 20)+
								","+Convert.increaseChar("", 800)+
								","+Convert.increaseChar("", 1)+
								","+Convert.increaseChar("", 100)+
								","+Convert.increaseChar("", 300)+
								","+Convert.increaseChar("", 24)+
								","+Convert.increaseChar(ConfigClass.CashRegisterCode, 20)+
								","+Convert.increaseChar(GlobalInfo.posLogin.gh, 20);
				}
				//bld.type = type1;
				line = type1 +","+ cardno +","+ jestr +","+ Tip +","+ MisBatchNo +","+ MisTraceNo +","+ TransTime +","+ Convert.increaseChar("" , 8) +","+ ExpDate +","+ Track2 +","+ Track3 +","+ ReferNo +","+ AuthNo+retmsg;

				PrintWriter pw = null;

				try
				{
					pw = CommonMethod.writeFile(ConfigClass.BankPath + "\\request.txt");

					if (pw != null)
					{
						pw.print(line);
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

				CommonMethod.waitForExec(ConfigClass.BankPath+"\\javaposbank.exe ICBCKPCLIENT4","javaposbank.exe");
			}
			else
			{
				new MessageBox("找不到金卡工程模块 javaposbank.exe");
				XYKSetError("XX", "找不到金卡工程模块 javaposbank.exe");
				return false;
			}

			// 读取应答数据
			if (!XYKReadResult(type1)) { return false; }

			// 检查交易是否成功
			XYKCheckRetCode();

			
			//System.err.println("head");
			// 打印签购单
			if (XYKNeedPrintDoc(type))
			{
				//System.err.println("head IN");
				XYKPrintDoc();
			}

			return true;
		}
		catch (Exception ex)
		{
			XYKSetError("XX", "金卡异常XX:" + ex.getMessage());
			new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);

			return false;
		}
	}
	
	public boolean XYKReadResult(String type)
	{
		BufferedReader br = null;

		try
		{
			if (!PathFile.fileExist(ConfigClass.BankPath+"\\result.txt") || ((br = CommonMethod.readFileGBK(ConfigClass.BankPath+"\\result.txt")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}

			// 读取请求数据
			String line1 = null;
			String line = null;
			
			 while((line1 = br.readLine())!=null){
				line +=line1;
			}

			String[] ret = line.split(","); 
			bld.retcode = ret[13];
			
			
			
			if (bld.retcode.trim().equals("00"))
			{
				if (type.equals("14")||type.equals("09")||type.equals("10")) return true;
				if(ret.length>25)bld.bankinfo = ret[25];
				if(ret.length>1)bld.cardno =ret[1];
				if(ret.length>11)bld.trace = Long.parseLong((ret[11]));
			}
			else
			{
				if (ret.length > 28) bld.retmsg = ret[29].trim();
			}

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
					new MessageBox("PFACE.TXT 关闭失败\n重试后如果仍然失败，请联系信息部");
					e.printStackTrace();
				}
			}
		}
	}
	
	public void XYKPrintDoc_Start()
	{
			Printer.getDefault().startPrint_Normal();
	}
	
	public void XYKPrintDoc_Print(String printStr)
	{
			Printer.getDefault().printLine_Normal(printStr);
	}
	
	public void XYKPrintDoc_End()
	{
			Printer.getDefault().cutPaper_Normal();
	}
	
}

	