package custom.localize.Bjys;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Bjys_PaymentBank extends PaymentBank 
{
	public Bjys_PaymentBank()
	{
		super();
	}
	
	public Bjys_PaymentBank(PayModeDef mode,SaleBS sale)
	{
		initPayment(mode,sale);
	}
	
	public Bjys_PaymentBank(SalePayDef pay,SaleHeadDef head)
	{
		initPayment(pay,head);
	}
	
	public static void printXYKDoc(String batch)
	{
        BufferedReader br = null;
        PrintWriter pw = null;
        
        String line = null;
        String filename = "bankdoc_" + batch + ".txt";
        
        try
        {
            if (!PathFile.fileExist(filename))
            {
                new MessageBox("找不到流水号[" + batch + "]的签购单打印文件!");

                return;
            }
            
            int num = 0;
            
            if (GlobalInfo.syjDef.issryyy != 'Y')
            {
            	num = 2;
            }
            else
            {
            	num = 1;
            }
            
            for (int i = 0 ;i < num ;i++)
    		{
	            br = CommonMethod.readFile(filename);
	            if (br == null)
	            {
	                new MessageBox("打开流水号[" + batch + "]的签购单打印文件失败!");
	
	                return;
	            }
	
	    		//
	    		Printer.getDefault().startPrint_Normal();
    		
	            while ((line = br.readLine()) != null)
	            {
	                if (line.length() <= 0)
	                {
	                	Printer.getDefault().printLine_Normal("\n");
	                }
	                else
	                {
	                	Printer.getDefault().printLine_Normal(line);
	                }
	            }
            
	            // 
	            Printer.getDefault().cutPaper_Normal();
    		}
            
            // 关闭并删除打印文件
            br.close();
            br = null;
            
            pw = CommonMethod.writeFileUTF(filename);
            pw.println("");
            pw.close();
            pw.flush();
            pw = null;
            
        	File f = new File(filename);
        	f.delete();
        	f = null;
        }
        catch (Exception e)
        {
        	e.printStackTrace();
            new MessageBox("打印签购单异常:\n\n" + e.getMessage());
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
				}
            }
            
            if (pw != null)
            {
            	 pw.close();
                 pw.flush();
                 pw = null;
            }
         
        }
	}
}
