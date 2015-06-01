package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

public class BLCOMPOS_PaymentBankFunc extends PaymentBankFunc
{
	public String[] getFuncItem()
    {
        String[] func = new String[3];

        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
        func[2] = "[" + PaymentBank.XYKTH + "]" + "交易退货";
     
        return func;
    }

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示该不用输入
		switch (type)
		{
			case PaymentBank.XYKXF://消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKCX://消费撤销
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = "原参考号";
				grpLabelStr[1] = "原授权号";
				grpLabelStr[2] = "原交易日";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
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
			case PaymentBank.XYKTH://隔日退货   
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
		}

		return true;
	}

	
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && 
				 (type != PaymentBank.XYKQD) && (type != PaymentBank.XYKJZ) && 
				 (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD) )
	            {
	                errmsg = "银联接口不支持该交易";
	                new MessageBox(errmsg);

	                return false;
	            }
			
			 // 先删除上次交易数据文件
            if (PathFile.fileExist("c:\\compos\\request.txt"))
            {
                PathFile.deletePath("c:\\compos\\request.txt");
                
                if (PathFile.fileExist("c:\\compos\\request.txt"))
                {
            		errmsg = "交易请求文件request.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist("c:\\compos\\result.txt"))
            {
                PathFile.deletePath("c:\\compos\\result.txt");
                
                if (PathFile.fileExist("c:\\compos\\result.txt"))
                {
            		errmsg = "交易请求文件result.txt无法删除,请重试";
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
                if (PathFile.fileExist("c:\\compos\\javaposbank.exe"))
                {
                	CommonMethod.waitForExec("c:\\compos\\javaposbank.exe BLYL");
                }
                else
                {
                    new MessageBox("找不到金卡工程模块 javaposbank.exe");
                    XYKSetError("XX","找不到金卡工程模块 javaposbank.exe");
                    return false;
                }
                
                // 读取应答数据
                if (!XYKReadResult(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
                {
                    return false;
                }
                
                // 检查交易是否成功
                if (!XYKCheckRetCode()) return false;
            }
            
            
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
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
	         
	         String com = "0";
	         	 
	         String typecode = "";
	         //	根据不同的类型生成文本结构
	         switch (type)
	         {
	         	case PaymentBank.XYKXF:
	         		typecode = "01";
	         	break;
	         	case PaymentBank.XYKCX:
	         		typecode = "03";
	         	break;
	         	case PaymentBank.XYKQD:
	         		typecode = "05";
	         	break;
	         	case 97:
	         		typecode = "02";
	         	break;
	         	case 98:
	         		typecode = "04";
	         	break;
	         	case 99:
	         		typecode = "06";
	         	break;	
	         	
	         }
	         
	         String info = Convert.increaseCharForward(String.valueOf(bld.fphm), 13)+" "+Convert.increaseCharForward(GlobalInfo.sysPara.mktcode,6);
	         
	         String memo1 = Convert.increaseChar("", 80);
	         
	         String trace1 = Convert.increaseCharForward(oldseqno,'0',6);
	         
	         String oldauth1 = Convert.increaseCharForward(oldauthno, '0',12);
	         PrintWriter pw = null;
	            
	         line = com + typecode+ jestr +info+memo1+trace1+olddate+oldauth1;
	         try
	         {
	            pw = CommonMethod.writeFile("c:\\compos\\request.txt");
	            
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
	
	public boolean checkDate(Text date)
	{
		String date1 = date.getText();
		if (date1.length() > 4)
		{
			new MessageBox("请输入日期\n日期格式《MMDD》");
			return false;
		}
		return true;
	}
	
	public boolean XYKReadResult(int type, double money, String track1,String track2, String track3,String oldseqno, String oldauthno,String olddate, Vector memo)
	{
       BufferedReader br = null;
       
       try
       {
    	   if (!PathFile.fileExist("c:\\compos\\result.txt") || ((br = CommonMethod.readFileGBK("c:\\compos\\result.txt")) == null))
           {
           		XYKSetError("XX","读取金卡工程应答数据失败!");
           		new MessageBox("读取金卡工程应答数据失败!", null, false);

           		return false;
           }
    	   
    	   String line = br.readLine();

    	   br.close();
           if (line.length() <= 0)
           {
               return false;
           }
           
           String code = line.substring(0,line.indexOf(","));
           if (!code.equals("0"))
           {
        	   if (new MessageBox("交易获取失败，是否需要重取上笔交易信息",null,true).verify() == GlobalVar.Key1)
        	   {
                   if (PathFile.fileExist("c:\\compos\\request.txt"))
		           {
		                PathFile.deletePath("c:\\compos\\request.txt");
		           }
					
				   if (PathFile.fileExist("c:\\compos\\result.txt"))
		           {
		                PathFile.deletePath("c:\\compos\\result.txt");
		           }
				   
        		   switch (type)
      	         	{
      	         	case PaymentBank.XYKXF:
      	         		XYKWriteRequest(97, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);
      	         	break;
      	         	case PaymentBank.XYKCX:
      	         		XYKWriteRequest(98, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);
      	         	break;
      	         	case PaymentBank.XYKTH:
      	         		XYKWriteRequest(99, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);
      	         	break;
      	         	}
        		   
//        		 调用接口模块
                   if (PathFile.fileExist("c:\\compos\\javaposbank.exe"))
                   {
                   		CommonMethod.waitForExec("c:\\compos\\javaposbank.exe BLYL");
                   }
                   else
                   {
                       new MessageBox("找不到金卡工程模块 javaposbank.exe");
                       XYKSetError("XX","找不到金卡工程模块 javaposbank.exe");
                       return false;
                   }
                   
                   if (!PathFile.fileExist("c:\\compos\\result.txt") || ((br = CommonMethod.readFileGBK("c:\\compos\\result.txt")) == null))
                   {
                   		XYKSetError("XX","读取金卡工程应答数据失败!");
                   		new MessageBox("读取金卡工程应答数据失败!", null, false);

                   		return false;
                   }
            	   
            	   line = br.readLine();
                   
                   
        	   }
           }
           
           line = line.substring(line.indexOf(",")+1);
           
           line = line.substring(2);
           String je = line.substring(0,12);
           line = line.substring(12);
           String retcode = line.substring(0,2);
           if (retcode.equals("00"))
           {
        	   return false;
           }
           
           line = line.substring(2);
           bld.cardno = line.substring(0,19);
           
           line = line.substring(19);
           line = line.substring(8);
           line = line.substring(6);
           bld.bankinfo = line.substring(0,6);
           line = line.substring(8);
           line = line.substring(6);
           line = line.substring(12);
           line = line.substring(20);
           line = line.substring(80);
           bld.trace = Convert.toLong(line);
           
    	   return true;
       }
       catch (Exception ex)
       {
    	   ex.printStackTrace();
    	   
    	   XYKSetError("XX","读取应答XX:"+ex.getMessage());
           new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
           
    	   
    	   return false;
       }
       finally
       {
           if (br != null)
           {
               try
               {
                   br.close();
                   
                   
                   if (PathFile.fileExist("c:\\compos\\request.txt"))
		           {
		                PathFile.deletePath("c:\\compos\\request.txt");
		           }
					
				   if (PathFile.fileExist("c:\\compos\\result.txt"))
		           {
		                PathFile.deletePath("c:\\compos\\result.txt");
		           }
               }
               catch (IOException e)
               {
                   e.printStackTrace();
               }
           }
       }
	 }

}
