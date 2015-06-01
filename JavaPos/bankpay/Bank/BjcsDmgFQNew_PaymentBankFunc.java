package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Payment.PaymentBank;

//西安大明宫连锁项目JAVAPOS金卡工程修改 原来的基础上记录：交易日期和时间  add2014.08.25
//原接口BjcsDmgFQ_PaymentBankFunc
//ChaseInterface.dll  BJCS int Abmcs(void *strIn, void *strOut)  字符串
public class BjcsDmgFQNew_PaymentBankFunc extends BjcsDmgFQ_PaymentBankFunc
{
	public boolean XYKReadResult()
	{
       BufferedReader br = null;
       
       try
       {
    	   if (!PathFile.fileExist("c:\\JavaPOS\\result.txt") || ((br = CommonMethod.readFileGBK("c:\\JavaPOS\\result.txt")) == null))
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
           
           if (result.length > 3 && result[3] != null && !result[3].equals("") && bld.type.equals(String.valueOf(PaymentBank.XYKCX)))
           {
        	   bld.je = ManipulatePrecision.doubleConvert(Double.parseDouble(result[3]) / 100, 2, 1);
           }
           
           if (result.length > 8 && result[8] != null && !result[8].equals(""))
           {
        	   bld.retmsg 	= result[8];
           }
           bld.cardno	= result[2];
           
           if (result.length > 4 && result[4] != null && !result[4].equals(""))
           {
        	   bld.trace	= Long.parseLong(result[4]);
           }
           
           //交易日期
           if (result.length >= 6 && result[6] != null && !result[6].equals(""))
           {
        	   bld.memo	= result[6].trim();
           }
           //交易时间
           if (result.length >= 7 && result[7] != null && !result[7].equals(""))
           {
        	   bld.memo1	= result[7].trim();
           }
           
//           if (result.length > 11 && result[11] != null && !result[11].equals(""))
//           {
//        	   String bank	= result[11];
//        	   bld.bankinfo = bank+ "-"+XYKReadBankName(bank);
//           }
           if (result.length > 11 && result[11] != null && !result[11].equals(""))
           {
        	  
        	   bld.tempstr = result[11].trim();
           }
           if (result.length > 11 && result[12] != null && !result[12].equals(""))
           {
        	  
        	   bld.tempstr1 =  result[12];
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
                   
                   if (PathFile.fileExist("c:\\JavaPOS\\request.txt"))
		            {
		                PathFile.deletePath("c:\\JavaPOS\\request.txt");
		            }
					
					if (PathFile.fileExist("c:\\JavaPOS\\result.txt"))
		            {
		                PathFile.deletePath("c:\\JavaPOS\\result.txt");
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
