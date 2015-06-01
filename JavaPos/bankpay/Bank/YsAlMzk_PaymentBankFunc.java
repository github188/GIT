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
/**
*北京燕莎奥莱
*/
public class YsAlMzk_PaymentBankFunc extends PaymentBankFunc
{
	public String[] getFuncItem()
	{
		String[] func = new String[7];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
		func[5] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		func[6] = "[" + PaymentBank.XKQT2+ "]" + "改卡密码";
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
			case PaymentBank.XKQT2: //改卡密码
				grpLabelStr[0] = "原凭证号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "改卡密码";
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
				
			case PaymentBank.XKQT2: //改卡密码
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "改卡密码";

				break;
		}

		return true;
	}
	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD) && (type != PaymentBank.XYKQD)
					&& (type != PaymentBank.XYKTH)  && (type != PaymentBank.XYKJZ)&& (type != PaymentBank.XKQT2))
			{
				errmsg = "银联接口不支持该交易";
				new MessageBox(errmsg);

				return false;
			}
			
			//先删除上次交易数据文件
			if (PathFile.fileExist("C:\\JavaPos\\request.txt"))
			{
				PathFile.deletePath("C:\\JavaPos\\request.txt");

				if (PathFile.fileExist("C:\\JavaPos\\request.txt"))
				{
					errmsg = "交易请求文件request.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}
			
			if (PathFile.fileExist("C:\\JavaPos\\result.txt"))
			{
				PathFile.deletePath("C:\\JavaPos\\result.txt");

				if (PathFile.fileExist("C:\\JavaPos\\result.txt"))
				{
					errmsg = "交易请求文件result.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}
			
			//写入请求数据
			if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo)) { return false; }

				if (bld.retbz != 'Y')
				{
					//调用接口模块
					if (PathFile.fileExist("C:\\JavaPos\\javaposbank.exe"))
					{
						CommonMethod.waitForExec("C:\\JavaPos\\javaposbank.exe YSAL");
					}
					else
					{
						new MessageBox("找不到金卡工程模块 javaposbank.exe");
						XYKSetError("XX", "找不到金卡工程模块 javaposbank.exe");
						return false;
					}

					//读取应答数据
					if (!XYKReadResult()) { return false; }

					// 检查交易是否成功
					XYKCheckRetCode();
				}

				//打印签购单
				if (XYKNeedPrintDoc())
				{
					if ((type == PaymentBank.XYKXF) && (type == PaymentBank.XYKCX) 
							&& (type == PaymentBank.XYKTH) && (type == PaymentBank.XYKJZ))
					{
						XYKPrintDoc();
					}
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
	
	public boolean XYKWriteRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		
		try
		{
			String strtypecode = "";
			String strje = "";
			String strseqno = "";
			String stroldterm = "";

			String strauto = "";
			String strolddate = "";

			String strtrack2 = "";
			String strtrack3 = "";
			
			switch (type)
			{
				case PaymentBank.XYKQD://交易签到
					strtypecode = "3";
					break;
				case PaymentBank.XYKXF://消费
					strtypecode = "1";
					strtrack2 = track2;
					strtrack3 = track3;
					// 交易金额
					strje = Convert.increaseCharForward(String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1)), '0', 12);
					break;
				case PaymentBank.XYKCX://消费撤销
					strtypecode = "7";
					strtrack2 = track2;
					strtrack3 = track3;
					// 交易金额
					strje = Convert.increaseCharForward(String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1)), '0', 12);
					//终端号
					if (oldauthno != null)
					{
						stroldterm = Convert.increaseChar(oldauthno, 12);
					}
					else
					{
						stroldterm = Convert.increaseChar("", 12);
					}

					strauto = Convert.increaseChar("", 12);
					
					//流水号
					if (oldseqno != null)
					{
						strseqno = Convert.increaseChar(oldseqno, 12);
					}
					else
					{
						stroldterm = Convert.increaseChar("", 12);
					}
					break;
				case PaymentBank.XYKTH://隔日退货
					strtypecode = "2";
					strtrack2 = track2;
					strtrack3 = track3;
					// 交易金额
					strje = Convert.increaseCharForward(String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1)), '0', 12);
					//终端号
					if (oldauthno != null)
					{
						stroldterm = Convert.increaseChar(oldauthno, 12);
					}
					else
					{
						stroldterm = Convert.increaseChar("", 12);
					}

					strauto = Convert.increaseChar("", 12);
					

					// 原交易日期
					if (olddate != null)
					{
						strolddate = Convert.increaseChar(olddate, 8);
					}
					else
					{
						strolddate = Convert.increaseChar("", 8);
					}
					break;
				case PaymentBank.XYKYE:		// 查询余额
					strtypecode = "5";
					strtrack2 = track2;
					strtrack3 = track3;
					break;
				case PaymentBank.XYKJZ:		// 交易结账
					strtypecode = "4";
					break;
				case PaymentBank.XKQT2:		// 改卡密码
					strtypecode = "9";
					strtrack2 = track2;
					strtrack3 = track3;
					break;
				default:
					return false;
			}
			
			// 传入串
			String line = strtypecode + strje + strseqno   + stroldterm + strauto + strolddate   + strtrack2
					+ strtrack3;

			PrintWriter pw = null;

			try
			{
				pw = CommonMethod.writeFile("C:\\JavaPos\\request.txt");

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
			ex.printStackTrace();
			new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);

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
				return false;
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
			String printName ="C:\\mpay\\dat\\print.txt";
			
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
	             
	             if (i == 0) Thread.sleep(2000);
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
