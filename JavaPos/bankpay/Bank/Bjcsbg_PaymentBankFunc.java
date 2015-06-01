package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;

//北国先天下 民生银行接口(原接口 Bjcs_PaymentBankFunc)
public class Bjcsbg_PaymentBankFunc extends Bjcs_PaymentBankFunc
{
	String path = "";
	
	private SaleBS saleBS = null;
	
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && 
				(type != PaymentBank.XYKTH) && (type != PaymentBank.XYKQD) && 
				 (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD) && 
				 (type != PaymentBank.XKQT1) && (type != PaymentBank.XKQT2)) 
	            {
	                errmsg = "银联接口不支持该交易";
	                new MessageBox(errmsg);

	                return false;
	            }
			
//			获得金卡文件路径
			path = getBankPath(paycode);
			
			 // 先删除上次交易数据文件
            if (PathFile.fileExist(path + "\\request.txt"))
            {
                PathFile.deletePath(path + "\\request.txt");
                
                if (PathFile.fileExist(path + "\\request.txt"))
                {
            		errmsg = "交易请求文件request.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist(path + "\\result.txt"))
            {
                PathFile.deletePath(path + "\\result.txt");
                
                if (PathFile.fileExist(path + "\\result.txt"))
                {
            		errmsg = "交易请求文件result.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            //  写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
            {
                return false;
            }
            
            if (bld.retbz != 'Y')
            {
            	
                // 调用接口模块
                if (PathFile.fileExist(path + "\\javaposbank.exe"))
                {
                	CommonMethod.waitForExec(path + "\\javaposbank.exe BJCS");
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
            
            // 	打印签购单
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
			 String line = "";
			 
	         String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
	         
	         for (int i = jestr.length(); i < 12; i++)
             {
                 jestr = "0" + jestr;
             }
	         
	         if (memo.size() >=2 ) saleBS = (SaleBS)memo.elementAt(2);
	         
	         //	 根据不同的类型生成文本结构
	         switch (type)
	         {
	        	 case PaymentBank.XYKXF:
	        		 if (saleBS != null)
	        		 {
	        			 line 		= "0" + "|" + jestr + "|" + saleBS.saleHead.syyh + "|" + saleBS.saleHead.fphm +"||" + saleBS.saleHead.syjh + "|\0";
	        		 }
	        		 else
	        		 {
	        			 line 		= "0" + "|" + jestr + "||||" + GlobalInfo.syjDef.syjh +"|\0";
	        		 }
	        	 break;
	        	 case PaymentBank.XYKCX:
	        		 if (saleBS != null)
	        		 {
	        			 line = "5" + "|" + jestr + "|" + saleBS.saleHead.syyh + "|" + saleBS.saleHead.fphm + "|" + oldseqno + "|" + saleBS.saleHead.syjh + "|\0";
	        		 }
	        		 else
	        		 {
	        			 line = "5" + "|" + jestr + "||||" + GlobalInfo.syjDef.syjh + "|\0";
	        		 }
	        	 break;
	        	 case PaymentBank.XYKTH:
	        		 if (saleBS != null)
	        		 {
	        			 line = "4" + "|" + jestr + "|" + saleBS.saleHead.syyh + "|" + saleBS.saleHead.fphm + "|" + oldseqno + "|" + saleBS.saleHead.syjh + "|\0";
	        		 }
	        		 else
	        		 {
	        			 line = "4" + "|" + jestr + "||||" + GlobalInfo.syjDef.syjh + "|\0";
	        		 }
	        	 break;
	        	 case PaymentBank.XYKQD:
	        		 line = "L|||||" +GlobalInfo.syjDef.syjh + "|\0";
	        	 break;
	        	 case PaymentBank.XYKYE:
	        		 line = "7|||||" + GlobalInfo.syjDef.syjh + "|\0";
	        	 break;
	        	 case PaymentBank.XYKCD:
	        		 line = "D|||||" + GlobalInfo.syjDef.syjh + "|\0";
	        	 break;	 
	        	 case PaymentBank.XKQT1:
	        		 line = "8|||||"+ GlobalInfo.syjDef.syjh + "|\0";
	        	 break;
	        	 case PaymentBank.XKQT2:
	        		 line = "Z||||| "+ GlobalInfo.syjDef.syjh + "|\0";
	        	 break;	 
	         }
	         
	         PrintWriter pw = null;
	            
	         try
	         {
	            pw = CommonMethod.writeFile(path + "\\request.txt");
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
    	   if (!PathFile.fileExist(path + "\\result.txt") || ((br = CommonMethod.readFileGBK(path + "\\result.txt")) == null))
           {
           		XYKSetError("XX","读取金卡工程应答数据失败!");
           		new MessageBox("读取金卡工程应答数据失败!", null, false);

           		return false;
           }
    	   
    	   String line = br.readLine();

           if (line.length() <= 0)
           {
               return false;
           }
           
           String result[] = line.split("\\|");
           
           if (result == null) return false;
           

           bld.retcode 	= result[0].split(",")[1];
           
           if (result.length >= 8 && result[8] != null && !result[8].equals(""))
           {
        	   bld.retmsg 	= result[8];
           }
           //bld.cardno	= result[3];  瞿均以前的写法。因廊坊/秦皇岛金卡也是使用该银联接口,在测试过程中发现卡号取的是12位金额,于是改成:
           bld.cardno	= result[2];
           
           if (result.length >= 4 && result[4] != null && !result[4].equals(""))
           {
        	   bld.trace	= Long.parseLong(result[4]);
           }
           
    	   return true;
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
                   
                   if (PathFile.fileExist(path + "\\request.txt"))
		            {
		                PathFile.deletePath(path + "\\request.txt");
		            }
					
					if (PathFile.fileExist(path + "\\result.txt"))
		            {
		                PathFile.deletePath(path + "\\result.txt");
		            }
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
		 ProgressBox pb = null;
		 
		 try
		 {
			 String printName = "";
			 
			 int type = Integer.parseInt(bld.type.trim());
			 
			 if ((type == PaymentBank.XYKXF || type == PaymentBank.XYKCX  || type == PaymentBank.XYKTH ||  type == PaymentBank.XYKCD || type == PaymentBank.XKQT1 ))
			 {
				 if (!PathFile.fileExist(path + "\\Print.txt"))
	             {
	                  new MessageBox("找不到签购单打印文件!");

	                  return;
	             }
	             else
	             {
	                  printName = path + "\\Print.txt";
	             }
			 }
			 else
			 {
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
            
            //删除打印文件
            if (PathFile.fileExist(path + "\\Print.txt"))
            {
            	PathFile.deletePath(path + "\\Print.txt");
            }
		 }
	 }
}
