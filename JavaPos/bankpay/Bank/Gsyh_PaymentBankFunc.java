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
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;


public class Gsyh_PaymentBankFunc extends PaymentBankFunc
{
	public String[] getFuncItem()
    {
        String[] func = new String[7];

        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
        func[3] = "[" + PaymentBank.XYKJZ + "]" + "银联结账";
        func[4] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
        func[5] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
        func[6] = "[" + PaymentBank.XKQT1 + "]" + "按凭证号打印";
        
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
                grpLabelStr[0] = "原凭证号";
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易金额";
            break;
        	case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = "原检索号";
				grpLabelStr[1] = "原终端号";
				grpLabelStr[2] = "原交易日";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
			break;
        	case PaymentBank.XYKJZ: //银联内卡结账
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "银联结账";
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
        		grpLabelStr[0] = "原凭证号";
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "按凭证号打印";
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
		 	case PaymentBank.XYKJZ: 	//银联结账
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始内卡银联结账";
            break;
		 	case PaymentBank.XYKYE: 	//余额查询    
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始余额查询";
            break;
		 	case PaymentBank.XKQT1:		// 按凭证号打印
		 		grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键凭证号打印";
		 	break;	
		 	case PaymentBank.XKQT2:		// 银联外卡结账
		 		grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始外卡银联结账";
		 	break;	
		}
		
		return true;
    }
	
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && 
					(type != PaymentBank.XYKTH)  && (type != PaymentBank.XYKJZ) && 
					(type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD) && 
					(type != PaymentBank.XKQT1))
	            {
	                errmsg = "银联接口不支持该交易";
	                new MessageBox(errmsg);

	                return false;
	            }
			
			 // 先删除上次交易数据文件
            if (PathFile.fileExist("C:\\JavaPos\\xyktr.in"))
            {
                PathFile.deletePath("C:\\JavaPos\\xyktr.in");
                
                if (PathFile.fileExist("C:\\JavaPos\\xyktr.in"))
                {
            		errmsg = "交易请求文件xyktr.in无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist("C:\\JavaPos\\xyktr.Out"))
            {
                PathFile.deletePath("C:\\JavaPos\\xyktr.Out");
                
                if (PathFile.fileExist("C:\\JavaPos\\xyktr.Out"))
                {
            		errmsg = "交易请求文件xyktr.Out无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist("C:\\JavaPos\\XYKPR.TMP"))
            {
                PathFile.deletePath("C:\\JavaPos\\XYKPR.TMP");
                
                if (PathFile.fileExist("C:\\JavaPos\\XYKPR.TMP"))
                {
            		errmsg = "交易请求文件XYKPR.TMP无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            // 写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
            {
                return false;
            }
            
            if (bld.retbz != 'Y')
            {
            	 // 调用接口模块
                if (PathFile.fileExist("C:\\JavaPos\\posxyk.exe"))
                {
                	CommonMethod.waitForExec("C:\\JavaPos\\posxyk.exe");
                }
                else
                {
                    new MessageBox("找不到金卡工程模块 posxyk.exe");
                    XYKSetError("XX","找不到金卡工程模块 posxyk.exe");
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
		if (Integer.parseInt(bld.retcode) == 0)
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
			 String jestr = ManipulatePrecision.doubleToString(money);
			 
            //根据不同的类型生成文本结构
            switch (type)
            {
                case PaymentBank.XYKXF:
                	bld.crc = "0001 0000";
                	line  = "0001 0000 " + Convert.increaseCharForward(jestr,' ',12) + "           " + Convert.increaseChar(track2,' ',37) + " " +Convert.increaseChar(track3,' ',104);
                break;
                case PaymentBank.XYKCX:
                	bld.crc = "0001 0001";
                	line  = "0001 0001 " + Convert.increaseCharForward(jestr,' ',12) + "    " + Convert.increaseCharForward(oldseqno,' ',6) + "        " +
                	Convert.increaseChar(track2,' ',37) + " " +Convert.increaseChar(track3,' ',104);
                break;
                case PaymentBank.XYKTH:
                	bld.crc = "0001 0002";
                	line  = "0001 0002 " + Convert.increaseCharForward(jestr,' ',12) + "    " + Convert.increaseCharForward(oldseqno,' ',8) + " " +
                	Convert.increaseCharForward(olddate,' ',8) + " " + Convert.increaseCharForward(oldauthno,' ',8) + "        "+ 
                	Convert.increaseChar(track2,' ',37) + " " +Convert.increaseChar(track3,' ',104);
                break;
                case PaymentBank.XYKYE:
                	bld.crc = "0002 0021";
                	line  = "0002 0021    " + Convert.increaseChar(track2,' ',37) + " " +Convert.increaseChar(track3,' ',104);
                break;
                case PaymentBank.XYKJZ:
                	bld.crc = "0002 0058";
                	line  = "0002 0058";
                break;
                case PaymentBank.XYKCD:
                	bld.crc = "0002 0011";
                	line  = "0002 0011";
                break;	
                case PaymentBank.XKQT1:
                	bld.crc = "0002 0012";
                	line  = "0002 0012    " + Convert.increaseCharForward(oldseqno,' ',6);
                break;	
                default:
                    bld.retbz = 'Y';

                    return true;
            }

            PrintWriter pw = null;
            
            try
            {
	            pw = CommonMethod.writeFile("C:\\JavaPos\\xyktr.in");
	            
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
        	if (!PathFile.fileExist("C:\\JavaPos\\xyktr.Out") || ((br = CommonMethod.readFileGBK("C:\\JavaPos\\xyktr.Out")) == null))
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
            
            if (!line.trim().equals(bld.crc.trim()))
       	 	{
       		 	new MessageBox("CRC不相等,返回交易有误[oldcrc]"+ bld.crc.trim() + " " + "[newcrc]" + line.trim() + "!", null, false);
       		 	return false;
       	 	}
            
            int i = 1;
            
            while ((line = br.readLine()) != null)
            {
            	 String retcode = line.substring(0,4);
            	 
            	 if (i == 1)
            	 {
            		 bld.retcode = retcode.trim();
            		 bld.retmsg  = line.substring(4).trim();	
            		 
            	 }
            	 else if (retcode.trim().equals("0002"))
            	 {
            		 bld.cardno = line.substring(4).trim();	 
            	 }
            	 else if(retcode.trim().equals("0003"))
            	 {
            		 bld.trace	= Long.parseLong(line.substring(4).trim());
            	 }
            	 else if (retcode.trim().equals("0007"))
            	 {
            		 bld.bankinfo	= line.substring(4).trim();	
            	 }
            	 
            	 i =  i + 1;
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
                    
                    if (PathFile.fileExist("C:\\JavaPos\\xyktr.in"))
		            {
		                PathFile.deletePath("C:\\JavaPos\\xyktr.in");
		            }
					
					if (PathFile.fileExist("C:\\JavaPos\\xyktr.Out"))
		            {
		                PathFile.deletePath("C:\\JavaPos\\xyktr.Out");
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
			 int type = Integer.parseInt(bld.type.trim());
			 
			 if ((type == PaymentBank.XYKXF || type == PaymentBank.XYKCX  || type == PaymentBank.XYKTH ||type == PaymentBank.XYKCD || type == PaymentBank.XYKJZ  || type == PaymentBank.XKQT1 ))
			 {
				 if (!PathFile.fileExist("C:\\JavaPos\\xykpr.tmp")) 
				 {
					 new MessageBox("找不到当前打印文件!");
					 return ;
				 }
	              
			 }
			 else
			 {
	             return;
			 }
			 
			 pb = new ProgressBox();
	         pb.setText("正在打印银联签购单,请等待...");
	         
	         int bankprint = 0;
	         
	         if (type == PaymentBank.XYKJZ)
	         {
	        	 bankprint = 1;
	         }
	         else
	         {
	        	 bankprint = GlobalInfo.sysPara.bankprint;
	         }
	         
	         for (int i = 0; i < bankprint; i++)
	         {
	        	 XYKPrintDoc_Start();

	             BufferedReader br = null;
	             
	             try
	             {
	            	 br = CommonMethod.readFileGBK("C:\\JavaPos\\xykpr.tmp");

	            	 if (br == null)
	            	 {
                        new MessageBox("打开xykpr.tmp打印文件失败!");

                        return;
	            	 }
	            	   	 
	            	 String line = null;
	                   
	            	 while ((line = br.readLine()) != null)
	            	 {
                        if (line.trim().equals("|"))
                        {
                            break;
                        }
                       
                        XYKPrintDoc_Print(line);
	            	 }
	            	 
	            	 for (int j = 0;j < 5;j++)
	            	 {
	            		 XYKPrintDoc_Print("");
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
