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

public class BjjwNmhl_PaymentBankFunc extends PaymentBankFunc
{
	public String[] getFuncItem()
	{
		String[] func = new String[8];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
		func[5] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		func[6] = "[" + PaymentBank.XYKCD + "]" + "重打上笔签购单";
		func[7] = "[" + PaymentBank.XKQT1 + "]" + "按流水号重打";
		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示该不用输入
		switch (type)
		{
			case PaymentBank.XYKXF: //消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKCX: //消费撤销
				grpLabelStr[0] = "原参考号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = "原交易日";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKYE: //余额查询    
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "余额查询";

				break;

			case PaymentBank.XYKJZ: //结账
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "结账";

				break;

			case PaymentBank.XYKTH: //隔日退货   
				grpLabelStr[0] = "原参考号";
				grpLabelStr[1] = "原终端号";
				grpLabelStr[2] = "原交易日";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKCD: //签购单重打
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打上笔签购单";
				break;
				
			case PaymentBank.XKQT1: //签购单重打
				grpLabelStr[0] = "原凭证号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打笔签购单";
				break;
		}

		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示该需要用户输入,不为null用户不输入
		switch (type)
		{
			case PaymentBank.XYKXF: //消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;

			case PaymentBank.XYKCX: //消费撤销
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;

			case PaymentBank.XYKTH: //退货
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;

			case PaymentBank.XYKYE: //余额查询    
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始余额查询";

				break;

			case PaymentBank.XYKQD: //交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易签到";

				break;

			case PaymentBank.XYKJZ: //内卡结账
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始结账";

				break;

			case PaymentBank.XYKCD: //签购单重打上笔
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "开始重打上笔签购单";

				break;
				
			case PaymentBank.XKQT1: //签购单重打指定
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "开始签购单重打";

				break;
		}

		return true;
	}
	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
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

				String line = "";

				String type1 = "";
		
				switch (type)
				{
					case PaymentBank.XYKQD:
						type1 = "09";
						break;
					case PaymentBank.XYKXF:
						type1 = "05";
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
				
				/**
typedef struct
{
   	char TransType[2];		//交易类型（收银机MIS系统传入），消费:"05"  消费撤销:"04" 查询余额"10"
	char CardNo[19];		//交易卡号：MISPOS系统返回需记录到数据库中作为对账内容之一
	char Amount[12];		//交易金额（收银机MIS系统传入，需记录到数据库中作为对账内容之一）
	char Tip[12];			//小费金额（暂时不用，空格补齐）
	char MisBatchNo[6];		//MIS批次号（暂时不用，空格补齐）
	char MisTraceNo[6];		//MIS流水号（暂时不用，空格补齐）
	char TransTime[6];		//交易时间（MISPOS系统返回）
	char TransDate[8];		//交易日期（MISPOS系统返回）
    	char ExpDate[4];		//卡片有效期（MISPOS系统返回）
	char Track2[37];		//二磁道信息（预留，暂时不用）
	char Track3[104];		//三磁道信息（预留，暂时不用）
	char ReferNo[8];		//系统检索号（MISPOS系统返回，收银机MIS系统在撤销交易时需传给MISPOS系统，需记录到数据库中作为对账内容之一）
	char AuthNo[6];			//MISPOS系统返回 暂时不用，空格补齐
	char ReturnCode[2];		//返回码（MISPOS系统返回，返回码为“00”表示交易成功，否则表示交易失败）
	char TerminalId[15];		//MISPOS系统返回交易终端号
	char MerchantId[12];		//MISPOS系统返回商户号
	char InstallmentTimes[2];	//MISPOS系统返回 暂时不用，空格补齐
	char TC[16];			//MISPOS系统返回 暂时不用，空格补齐
	char OldAuthDate[8];		//原交易日期，撤销交易时候传送给MISPOS系统
	char MerchantNameEng[50];	//商户名称（英文）MISPOS系统返回
	char MerchantNameChs[40];	//商户中文名称MISPOS系统返回
	char TerminalTraceNo[6];	//终端流水号MISPOS系统返回
	char TerminalBatchNo[6];	//终端批次号MISPOS系统返回
	char IcCardId[4];		//MISPOS系统返回，暂时无需处理
	char CardType[20];		//MISPOS系统返回
	char TransName[20];		//MISPOS交易中文名称
	char DeviceInitFlag[1];		//MISPOS系统返回，暂时无需处理
	char Message[100];		//交易失败时，MISPOS系统返回中文错误描述信息
	char Remark[300];		//MISPOS系统返回，暂时无需处理
	char ForeignCardTraceNo[24];	//MISPOS系统返回，暂时无需处理
	char Ttotal[800];		//交易信息汇总，为交易总账信息
	char PlatId[20];		//收银台号
	char OperId[20];		//操作员号
} ST_ICBC_MIS;
				 */
				// 交易卡号【19】
				String cardno = Convert.increaseLong(0, 19);
				// 交易金额【12】
				String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));
				jestr = Convert.increaseCharForward(jestr, '0', 12);
				// 消费金额【12】
				String Tip = Convert.increaseLong(0, 12);
				// MIS批次号【6】
				String MisBatchNo = Convert.increaseLong(0, 6);
				// MIS流水号【6】
				String MisTraceNo = Convert.increaseLong(0, 6);
				// 交易时间【6】
				String TransTime = Convert.increaseLong(0, 6);
				// 交易日期【8】
				String TransDate = Convert.increaseChar(olddate , 8);
				// 卡片有效期【4】
				String ExpDate = Convert.increaseLong(0, 4);
				// 二磁道信息【37】
				String Track2 = Convert.increaseLong(0, 37);
				// 三磁道信息【104】
				String Track3 = Convert.increaseLong(0, 104);
				// 系统检索号【8】
				String ReferNo = Convert.increaseChar(oldseqno , 8);
				// MISPOS系统返回【6】
				String AuthNo = Convert.increaseLong(0, 6);
				// 返回
				String retmsg = ","+Convert.increaseChar("", 2)+
								","+Convert.increaseCharForward(oldauthno,'0', 15)+
								","+Convert.increaseChar("", 12)+
								","+Convert.increaseChar("", 2)+
								","+Convert.increaseChar("",16)+
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

				CommonMethod.waitForExec(ConfigClass.BankPath+"\\javaposbank.exe BJJWNMHL","javaposbank.exe");
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

	public boolean XYKReadResult()
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
			String line = br.readLine();

			String[] ret = line.split(","); 
			bld.retcode = ret[13];

			if (bld.retcode.equals("00"))
			{
				bld.bankinfo = ret[26];
				bld.cardno =ret[1];
			}
			else
			{
				if (ret.length > 29) bld.retmsg = ret[30].trim();
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


	public void XYKPrintDoc()
	{
		ProgressBox pb = null;

		try
		{
			String printName = ConfigClass.BankPath+"\\ICBCPRTTKT.txt";
			
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
                       if (line.trim().equals("CUTPAPER"))
                       {
                    	   Printer.getDefault().cutPaper_Normal();
                    	   continue;
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
	
	protected boolean XYKNeedPrintDoc(int type)
	{
		if (type != PaymentBank.XYKXF && type != PaymentBank.XYKTH && type != PaymentBank.XYKCX && type != PaymentBank.XYKCD && type != PaymentBank.XYKJZ && type != PaymentBank.XKQT1)
		{
			return false;
		}
		return true;
	}
}
