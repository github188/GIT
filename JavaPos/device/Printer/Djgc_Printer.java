package device.Printer;

import gnu.io.CommPortIdentifier;

import java.io.File;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;

import javax.print.PrintService;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Global.Language;

//德基广场购物中心   通过银联的exe程序调用银联的打印机打印储值卡签购单、报表、缴款单等  
public class Djgc_Printer extends Text_Printer
{
	PrintService printservice = null;
	protected String EXEpath = "";
	protected String EXEname = "";
	protected String Printfile = "";
	
	public boolean open()
    {
    	if (DeviceName.devicePrinter.length() <= 0) 
    		return false;
    	
        try
        {   		
	        String[] arg = DeviceName.devicePrinter.split(",");

	        if (arg.length > 0)
	        {
	        	EXEpath = arg[0].trim();
	        	if ((arg.length > 1) && (arg[1].length() > 0))
		        {
	        		EXEname = arg[1].trim();
		        }
		        if ((arg.length > 2) && (arg[2].length() > 0))
		        {
		        	Printfile = arg[2].trim();
		        }
	        }
            return true;
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        	new MessageBox(Language.apply("打开串口打印机异常:\n") + ex.getMessage());
        }
        
        return false;
    }
	
    public void cutPaper_Journal()
    {
    	cutPaper_Normal();
    }
    public void cutPaper_Slip()
    {
    	cutPaper_Normal();
    }
	
    public void cutPaper_Normal()
    {
    	String cut = "-------------Normal"+Language.apply("切纸")+"----------------\n\n";
    	try
    	{
        	if(new File(EXEpath+"\\"+EXEname).exists())
        	{
        		CommonMethod.waitForExec(EXEpath+"\\"+EXEname);
        	}else {
        		 new MessageBox(Language.apply("银联{0}不存在，不能调用打印机！",new Object[]{EXEname}));
        		 return;
        	}
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}

    	printLine_Normal(cut);
		
    	if (PathFile.fileExist(Printfile))
        {
            PathFile.deletePath(Printfile);
        }
    }

    public void printLine_Journal(String printStr)
    {
        PrintWriter pw = null;
        if (System.getProperties().getProperty("os.name").substring(0, 5).equals("Linux"))
        {
        	pw = CommonMethod.writeFileAppend(Printfile);
        }
        else
        {
        	pw = CommonMethod.writeFileAppendGBK(Printfile);
        	printStr = printStr.replaceAll("\n","\r\n");
        }
        
        pw.print(printStr);
        pw.flush();
        pw.close();
    }

    public void printLine_Normal(String printStr)
    {
        PrintWriter pw = null;
        if (System.getProperties().getProperty("os.name").substring(0, 5).equals("Linux"))
        {
        	pw = CommonMethod.writeFileAppend(Printfile);
        }
        else
        {
        	pw = CommonMethod.writeFileAppendGBK(Printfile);
        	printStr = printStr.replaceAll("\n","\r\n");
        }
        
        pw.print(printStr);
        pw.flush();
        pw.close();
    }

    public void printLine_Slip(String printStr)
    {
        PrintWriter pw = null;
        if (System.getProperties().getProperty("os.name").substring(0, 5).equals("Linux"))
        {
        	pw = CommonMethod.writeFileAppend(Printfile);
        }
        else
        {
        	pw = CommonMethod.writeFileAppendGBK(Printfile);
        	printStr = printStr.replaceAll("\n","\r\n");
        }
        
        pw.print(printStr);
        pw.flush();
        pw.close();
    }

    
	public Vector getPara() 
	{
		Vector v = new Vector();
		String comlist = Language.apply("EXE绝对路径");
		Enumeration portList = CommPortIdentifier.getPortIdentifiers();
    	while(portList != null)
    	{
    		CommPortIdentifier p = (CommPortIdentifier)portList.nextElement();
    		if (p == null) break;
    		else
    		{
    			comlist +=","+p.getName();
    		}
    	}
    	
		v.add(comlist.split(","));
    	v.add(new String[]{Language.apply("EXE文件名"),""});
    	v.add(new String[]{Language.apply("打印文件绝对路径"),""});
    	
		return v;
	}
    
	public String getDiscription() 
	{
		return Language.apply("调用银联exe调银联打印机打印报表等");
	}

}
