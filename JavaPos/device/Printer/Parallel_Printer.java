package device.Printer;

import gnu.io.CommPortIdentifier;

import java.util.Enumeration;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_Printer;
import com.efuture.javaPos.Device.SerialPort.ParallelConnection;
import com.efuture.javaPos.Device.SerialPort.ParallelConnectionException;
import com.efuture.javaPos.Global.Language;

import device.DeviceInfo;

public class Parallel_Printer implements Interface_Printer
{
	protected ParallelConnection port = null;
	protected boolean cutMsg = false;
	protected int cutLine = 0;
    protected String cutCmd = null;
    protected String initCmd = null;
    protected String passCmd = null;
    
    public boolean open()
    {
    	if (DeviceName.devicePrinter.length() <= 0) return false;
    	
        try
        {    	
        	String[] s = DeviceName.devicePrinter.split(",");
        	
        	port = new ParallelConnection(s[0]);
        	
	        port.openConnection();
	        
	        if (s.length > 1) cutMsg = (s[1].equalsIgnoreCase("Y") ? true : false);
	        if (s.length > 2) cutLine = Convert.toInt(s[2]);
	        if (s.length > 3) cutCmd = s[3];
	        if (s.length > 4) initCmd = s[4];
	        if (s.length > 5) passCmd = s[5];
	        
            return true;
        }
        catch (ParallelConnectionException ex)
        {
            ex.printStackTrace();
            new MessageBox(Language.apply("打开并口打印机异常:\n") + ex.getMessage());
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        	new MessageBox(Language.apply("打开并口打印机异常:\n") + ex.getMessage());
        }
        
        return false;
    }
    
    public void close()
    {
        if (port != null)
        {
            port.closeConnection();
        }
    }

    public void setEnable(boolean enable)
    {
    	// 发送初始化命令
    	if (enable && initCmd != null && initCmd.trim().length() > 0)
    	{
    		char[] c = DeviceInfo.convertCmdStringToCmdChar(initCmd);
    		for (int i=0;i<c.length;i++)
    		{
    			port.sendChar(c[i]);
    		}
    	}
    }

    public void cutPaper_Normal()
    {
    	for (int i = 0;i < cutLine;i++)
    	{
    		printLine_Normal("\n");
    	}
    	
    	// 发送切纸命令
    	if (cutCmd != null && cutCmd.trim().length() > 0)
    	{
    		char[] c = DeviceInfo.convertCmdStringToCmdChar(cutCmd);
    		for (int i=0;i<c.length;i++)
    		{
    			port.sendChar(c[i]);
    		}
    	}
    	
    	//
    	if (cutMsg)
    	{
    		new MessageBox(Language.apply("请从打印机撕下已打印的单据"));
    	}
    }
    
    public void cutPaper_Journal()
    {
        cutPaper_Normal();
    }
    
    public void cutPaper_Slip()
    {
        cutPaper_Normal();
    }

    public void printLine_Normal(String printStr)
    {
    	port.sendString(printStr);
    }
    
    public void printLine_Journal(String printStr)
    {
        printLine_Normal(printStr);
    }

    public void printLine_Slip(String printStr)
    {
        printLine_Normal(printStr);
    }
    
	public boolean passPage_Normal() 
	{
    	// 发送分页走纸命令
    	if (passCmd != null && passCmd.trim().length() > 0)
    	{
    		char[] c = DeviceInfo.convertCmdStringToCmdChar(passCmd);
    		for (int i=0;i<c.length;i++)
    		{
    			port.sendChar(c[i]);
    		}
    		return true;
    	}
    	
		return false;
	}

	public boolean passPage_Journal()
	{
		return passPage_Normal();
	}
	
	public boolean passPage_Slip()
	{
		return passPage_Normal();
	}

	public void enableRealPrintMode(boolean flag)
	{
	}

	public Vector getPara()
	{
		Vector v = new Vector();
		String comlist = Language.apply("端口号");
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
    	v.add(new String[]{Language.apply("是否显示切纸提示"),"N","Y"});
    	v.add(new String[]{Language.apply("切纸前走纸的行数"),"0"});
    	v.add(new String[]{Language.apply("切纸命令")});
    	v.add(new String[]{Language.apply("初始化命令")});
    	v.add(new String[]{Language.apply("分页走纸命令")});
    	
    	return v;
	}

	public String getDiscription() 
	{
		return Language.apply("标准并口打印机");
	}

	public void setEmptyMsg_Slip(String msg)
	{
	}
}