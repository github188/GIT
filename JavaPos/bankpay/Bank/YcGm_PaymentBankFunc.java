package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

/**
宜昌国贸
*/

public class YcGm_PaymentBankFunc extends PaymentBankFunc
{
	private SaleBS saleBS = null;
	
	public String[] getFuncItem()
    {
        String[] func = new String[9];

        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
        func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
        func[4] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
        func[5] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
        func[6] = "[" + PaymentBank.XKQT1 + "]" + "按凭证号打印";
        func[7] = "[" + PaymentBank.XKQT2 + "]" + "查 流 水";
        func[8] = "[" + PaymentBank.XYKJZ + "]" + "结算";

        return func;
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
                grpLabelStr[0] = "参 考 号";
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易金额";
            break;
        	case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = "参 考 号";
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
        	case PaymentBank.XYKYE: //余额查询    
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "余额查询";
            break;
        	case PaymentBank.XYKCD: //签购单重打
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打上笔签购单";
            break;
        	case PaymentBank.XKQT1:		// 按凭证号打印
        		grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "按凭证号打印";
            break;    
        	case PaymentBank.XKQT2:		// 查流水
        		grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "查 流 水";
        	break;	
        	case PaymentBank.XYKJZ:		// 结算
        		grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易结算";
        	break;	
        }
		
		return true;
    }
	
	public boolean getFuncText(int type, String[] grpTextStr)
    {
		switch (type)
		{
		 	case PaymentBank.XYKXF: 	// 消费
		        grpTextStr[0] = null;
		        grpTextStr[1] = null;
		        grpTextStr[2] = null;
		        grpTextStr[3] = null;
		        grpTextStr[4] = null;
		    break;
		 	case PaymentBank.XYKCX: 	// 消费撤销
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = null;
            break;
		 	case PaymentBank.XYKTH:		//隔日退货   
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
			break;
		 	case PaymentBank.XYKQD: 	//交易签到
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始交易签到";
            break;
		 	case PaymentBank.XYKYE: 	//余额查询    
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始余额查询";
            break;
		 	case PaymentBank.XYKCD: 	//签购单重打
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键签购单重打";
            break;
		 	case PaymentBank.XKQT1:		// 按凭证号打印
		 		grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键凭证号打印";
		 	break;	
		 	case PaymentBank.XKQT2:		// 查流水
		 		grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按查流水";
		 	break;	
		 	case PaymentBank.XYKJZ:		// 结算
		 		grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键结算";
		 	break;	
		}
		
		return true;
    }
	
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		try
		{
			/*
			String conf[] = ConfigClass.Bankfunc.split("\\|"); 
        	if (conf.length != 1)
        	{
        		new MessageBox("该门店只支持一个信用卡接口！");
        		return false;
        	}
			*/
			if ((type != PaymentBank.XYKJZ) && (type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && 
				(type != PaymentBank.XYKTH) && (type != PaymentBank.XYKQD) && 
				 (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD) && 
				 (type != PaymentBank.XKQT1) && (type != PaymentBank.XKQT2)) 
	            {
	                errmsg = "银联接口不支持该交易";
	                new MessageBox(errmsg);

	                return false;
	            }
			
			 // 先删除上次交易数据文件
            if (PathFile.fileExist(ConfigClass.BankPath+"\\request.txt"))
            {
                PathFile.deletePath(ConfigClass.BankPath+"\\request.txt");
                
                if (PathFile.fileExist(ConfigClass.BankPath+"\\request.txt"))
                {
            		errmsg = "交易请求文件request.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist(ConfigClass.BankPath+"\\result.txt"))
            {
                PathFile.deletePath(ConfigClass.BankPath+"\\result.txt");
                
                if (PathFile.fileExist(ConfigClass.BankPath+"\\result.txt"))
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
                if (PathFile.fileExist(ConfigClass.BankPath+"\\javaposbank.exe"))
                {
                	CommonMethod.waitForExec(ConfigClass.BankPath+"\\javaposbank.exe BJCS", "javaposbank.exe");
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
            if(XYKNeedPrintDoc()){
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
	
	public boolean XYKNeedPrintDoc()
	{
       if (!checkBankSucceed())
       {
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
	        	 case PaymentBank.XYKJZ:
//	        		 line = "9||||| "+ GlobalInfo.syjDef.syjh + "|\0";
	        		 line = "9||" + GlobalInfo.posLogin.gh + "||| "+ GlobalInfo.syjDef.syjh + "|\0";
//	        		 
	        	 break;	 
	         }
	         
	         PrintWriter pw = null;
	            
	         try
	         {
	            pw = CommonMethod.writeFile(ConfigClass.BankPath+"\\request.txt");
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
    	   if (!PathFile.fileExist(ConfigClass.BankPath+"\\result.txt") || ((br = CommonMethod.readFileGBK(ConfigClass.BankPath+"\\result.txt")) == null))
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
                   
                   if (PathFile.fileExist(ConfigClass.BankPath+"\\request.txt"))
		            {
		                PathFile.deletePath(ConfigClass.BankPath+"\\request.txt");
		            }
					
					if (PathFile.fileExist(ConfigClass.BankPath+"\\result.txt"))
		            {
		                PathFile.deletePath(ConfigClass.BankPath+"\\result.txt");
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
			 
			 if ((type == PaymentBank.XYKXF || type == PaymentBank.XYKCX  || type == PaymentBank.XYKTH ||  type == PaymentBank.XYKCD || type == PaymentBank.XKQT1))
			 {
                  printName = ConfigClass.BankPath+"\\Print.txt";
			 }
			 else if (type == PaymentBank.XYKJZ)
			 {
				 printName = ConfigClass.BankPath+"\\Settle.txt";
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
