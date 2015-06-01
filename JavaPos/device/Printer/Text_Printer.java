package device.Printer;

import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Device.Interface.Interface_Printer;
import com.efuture.javaPos.Global.Language;


public class Text_Printer implements Interface_Printer
{
	public boolean open()
    {
        return true;
    }

    public void close()
    {
    }

    public void setEnable(boolean enable)
    {
    }

    public void cutPaper_Journal()
    {
    	String cut = "-------------Journal"+Language.apply("切纸")+"---------------\n\n";
    	System.out.println(cut);
    	printLine_Journal(cut);
    }

    public void cutPaper_Normal()
    {
    	String cut = "-------------Normal"+Language.apply("切纸")+"----------------\n\n";
    	System.out.println(cut);
    	printLine_Normal(cut);
    }

    public void cutPaper_Slip()
    {
    	String cut = "-------------Slip"+Language.apply("切纸")+"------------------\n\n";
    	System.out.println(cut);
    	printLine_Slip(cut);
    }

    public void printLine_Journal(String printStr)
    {
        System.out.print(printStr);

        PrintWriter pw = null;
        if (System.getProperties().getProperty("os.name").substring(0, 5).equals("Linux"))
        {
        	pw = CommonMethod.writeFileAppend("Journal_Print_file.ini");
        }
        else
        {
        	pw = CommonMethod.writeFileAppendGBK("Journal_Print_file.ini");
        	printStr = printStr.replaceAll("\n","\r\n");
        }
        
        pw.print(printStr);
        pw.flush();
        pw.close();
    }

    public void printLine_Normal(String printStr)
    {
    	System.out.print(printStr);

        PrintWriter pw = null;
        if (System.getProperties().getProperty("os.name").substring(0, 5).equals("Linux"))
        {
        	pw = CommonMethod.writeFileAppend("Normal_Print_file.ini");
        }
        else
        {
        	pw = CommonMethod.writeFileAppendGBK("Normal_Print_file.ini");
        	printStr = printStr.replaceAll("\n","\r\n");
        }
        
        pw.print(printStr);
        pw.flush();
        pw.close();
    }

    public void printLine_Slip(String printStr)
    {
    	System.out.print(printStr);

        PrintWriter pw = null;
        if (System.getProperties().getProperty("os.name").substring(0, 5).equals("Linux"))
        {
        	pw = CommonMethod.writeFileAppend("Slip_Print_file.ini");
        }
        else
        {
        	pw = CommonMethod.writeFileAppendGBK("Slip_Print_file.ini");
        	printStr = printStr.replaceAll("\n","\r\n");
        }
        
        pw.print(printStr);
        pw.flush();
        pw.close();
    }
    
    public boolean passPage_Journal()
    {
    	return false;
    }

    public boolean passPage_Normal()
    {
    	return false;
    }

    public boolean passPage_Slip()
    {
    	System.out.println("-------------Slip PASS------------------");
    	return true;
    }

	public void enableRealPrintMode(boolean flag)
	{
	}

	public String getDiscription() 
	{
		return Language.apply("文本文件打印机");
	}

	public Vector getPara() {
		return null;
	}

	public void setEmptyMsg_Slip(String msg)
	{
	}
}
