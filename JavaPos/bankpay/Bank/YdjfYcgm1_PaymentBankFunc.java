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
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;


public class YdjfYcgm1_PaymentBankFunc extends PaymentBankFunc
{
	public String[] getFuncItem()
	{
		String[] func = new String[6];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[3] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
		func[4] = "[" + PaymentBank.XKQT1 + "]" + "查询某一笔交易";
		func[5] = "[" + PaymentBank.XYKCD + "]" + "重打印";

		return func;
	}
	
	public boolean checkBankSucceed()
	{
		if (Integer.parseInt(bld.type) != PaymentBank.XYKCD && bld.retbz == 'N')
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public boolean getFuncLabel(int type, String[] grpLabelStr)
    {
		switch (type)
        {
        	case PaymentBank.XYKXF: //	消费
	            grpLabelStr[0] = null;
	            grpLabelStr[1] = null;
	            grpLabelStr[2] = null;
	            grpLabelStr[3] = null;
	            grpLabelStr[4] = "交易金额";
            break;
        	case PaymentBank.XYKCX: //消费撤销
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易金额";

            break;
        	case PaymentBank.XYKQD: //交易签到
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易签到";
            break;
        	case PaymentBank.XYKJZ: //结账
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "结算交易";
            break;
        	case PaymentBank.XKQT1: //查询某一笔交易
                grpLabelStr[0] = "原流水号";
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "查询某一笔交易";
            break;
        	case PaymentBank.XYKCD: //签购单重打
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打上笔签购单";
            break;
        }
		
		return true;
    }

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示必须用户输入,不为null表示缺省显示无需改变
		switch (type)
		{
			case PaymentBank.XYKXF://消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKCX://消费撤销
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKQD://交易签到
				grpTextStr[0] = null;//"allowempty";
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易签到";
				break;
			case PaymentBank.XYKJZ://交易结账
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易结账";
				break;
			case PaymentBank.XYKYE://余额查询    
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "查询某一笔交易";
				break;
		 	case PaymentBank.XYKCD: 	//签购单重打
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键签购单重打";
            break;
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		 try
		 {  
			 if (!(type == PaymentBank.XYKCD))
			 {
				 //	先删除上次交易数据文件
				 if (PathFile.fileExist("c:\\JavaPos\\request.txt"))
				 {
	                PathFile.deletePath("c:\\JavaPos\\request.txt");
	                
	                if (PathFile.fileExist("c:\\JavaPos\\request.txt"))
	                {
	            		errmsg = "交易请求文件request.txt无法删除,请重试";
	            		XYKSetError("XX",errmsg);
	            		new MessageBox(errmsg);
	            		return false;   	
	                }
				 }
		         
				 if (PathFile.fileExist("c:\\JavaPos\\result.txt"))
				 {
	                PathFile.deletePath("c:\\JavaPos\\result.txt");
	                
	                if (PathFile.fileExist("c:\\JavaPos\\result.txt"))
	                {
	            		errmsg = "交易结果文件result.txt无法删除,请重试";
	            		XYKSetError("XX",errmsg);
	            		new MessageBox(errmsg);
	            		return false;   	
	                }
				 }
				 
				 if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo)) return false;
				 
				 if (bld.retbz != 'Y')
				 {
	                // 调用接口模块
	                if (PathFile.fileExist("c:\\JavaPos\\javaposbank.exe"))
	                {
	                	CommonMethod.waitForExec("c:\\JavaPos\\javaposbank.exe YDJFYCGM");
	                }
	                else
	                {
	                    new MessageBox("找不到金卡工程模块 javaposbank.exe");
	                    XYKSetError("XX","找不到金卡工程模块 javaposbank.exe");
	                    return false;
	                }
	
	                // 读取应答数据
	                if (!XYKReadResult())
	                {
	                    return false;
	                }
	
	                // 检查交易是否成功
	                XYKCheckRetCode();
				 }
			 }
			 
			 //	打印签购单
			 if (XYKNeedPrintDoc())
			 {
                XYKPrintDoc();
			 }
			 
			 return true;
		 }
		 catch (Exception ex)
		 {
			 ex.printStackTrace();
			 XYKSetError("XX","金卡异常XX:"+ex.getMessage());
			 
			 new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);
			 
			 return false;
		 }
	}
	
	public boolean XYKWriteRequest(int type, double money, String track1,String track2, String track3,String oldseqno, String oldauthno,String olddate, Vector memo)
	{
		 try
		 {
			 String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));
			 jestr = Convert.increaseCharForward(jestr, '0', 12);
				
			 String line = "";

			 switch (type)
	         {
	         	case PaymentBank.XYKXF:				// 消费
	         		line = "5" + "," + bld.fphm + "," + jestr;
	         		break;
	         	case PaymentBank.XYKCX:				// 撤销	
	         		line = "6" + "," + bld.fphm + "," + jestr;
	         		break;
	         	case PaymentBank.XYKQD:				// 签到
	         		line = "3" + "," + bld.fphm ;
	         		break;
	         	case PaymentBank.XYKJZ:				// 结算
	         		line = "4" + "," + bld.fphm;
	         		break;
	         	case PaymentBank.XKQT1:				// 查询
	         		line = "7" + "," + bld.fphm + "," + jestr + "," + oldseqno;
	         		break;
	         }
	         
	         PrintWriter pw = null;
	         try
	         {
	            pw = CommonMethod.writeFile("c:\\JavaPos\\request.txt");
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
			 bld.retcode = "XX";
			 
			 if (!PathFile.fileExist("c:\\JavaPos\\result.txt") || ((br = CommonMethod.readFileGBK("c:\\JavaPos\\result.txt")) == null))
			 {
				 XYKSetError("XX","读取金卡工程应答数据失败!");
				 new MessageBox("读取金卡工程应答数据失败!", null, false);

				 return false;
			 }
			 
			 String line = br.readLine();
			 
			 if (line.length() <= 0)
			 {
				 new MessageBox("没有读到返回码!", null, false);
				 return false;
			 }
			 
             bld.retcode = line.trim();
             
             br.close();
             br = null;
			  
			 if (!PathFile.fileExist("c:\\JavaPos\\answer.txt") || ((br = CommonMethod.readFileGBK("c:\\JavaPos\\answer.txt")) == null))
			 {
				 XYKSetError("XX","读取金卡工程应答数据失败!");
				 new MessageBox("读取金卡工程应答数据失败!", null, false);

				 return false;
			 }
             
			line = br.readLine();
			
			if (line.trim().length() >= 2)
			{
				bld.retcode = line.substring(0,2);
				
				if (line.trim().length() > 2)
				{
					bld.retmsg = line.substring(2);
				}
			}		
		 }
		 catch (Exception ex)
		 {
			 XYKSetError("XX","读取应答XX:"+ex.getMessage());
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
                   br = null;
               }
               catch (IOException e)
               {
                   e.printStackTrace();
               }
			 }
		 }

		 return true;
	 }
	
	public void XYKPrintDoc()
	 {
		 ProgressBox pb = null;
		 
		 try
		 {
			 String printName = "";
			 
			 int type = Integer.parseInt(bld.type.trim());
			 
			 if ((type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKJZ || type == PaymentBank.XYKCD))
			 {
                 printName = "c:\\JavaPOS\\toprint.txt";
			 }
			 else
			 {
	             return;
			 }
			 
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
	              	 if (Integer.parseInt(bld.type) == PaymentBank.XYKCD)
	            	 {     
	            		 XYKPrintDoc_Print("          重印          ");
	            	 }
	              	 
	            	 br = CommonMethod.readFileGBK(printName);

	            	 if (br == null)
	            	 {
                       new MessageBox("打开" + printName + "打印文件失败!");

                       return;
	            	 }
	            	   	 
	            	 String line = null;
	                   
	            	 while ((line = br.readLine()) != null)
	            	 {
	            		
                       if (line.trim().equals("CUTPAPPER"))
                       {
                           break;
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
}
