package device.Printer;

import java.util.Enumeration;
import java.util.Vector;

import javax.comm.CommPortIdentifier;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_Printer;
import com.efuture.javaPos.Device.SerialPort.Javax_SerialConnection;
import com.efuture.javaPos.Device.SerialPort.Javax_SerialParameters;
import com.efuture.javaPos.Device.SerialPort.SerialConnectionException;
import com.efuture.javaPos.Global.Language;

import device.DeviceInfo;


public class SerialJavax_Printer implements Interface_Printer
{
    protected Javax_SerialParameters para = null;
    protected Javax_SerialConnection port = null;
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
	        String[] arg = DeviceName.devicePrinter.split(",");
	        para = new Javax_SerialParameters();
	
	        if (arg.length > 0)
	        {
	        	//端口名
	            para.setPortName(arg[0]);
	
	            if (arg.length > 1)
	            {
	            	//波特率
	                para.setBaudRate(arg[1]);
	            }
	            
	            if (arg.length > 2)
	            {
	            	//奇偶效验位
	                para.setParity(arg[2]);
	            }
	            
	            if (arg.length > 3)
	            {
	            	//数据位
	                para.setDatabits(arg[3]);
	            }
	            
	            if (arg.length > 4)
	            {
	            	//停止位
	                para.setStopbits(arg[4]);
	            }
	            
		        if (arg.length > 5)
		        {
		        	//是否弹出消息框
		        	cutMsg = (arg[5].equalsIgnoreCase("Y") ? true : false);
		        }

		        if (arg.length > 6)
		        {
		        	cutLine = Convert.toInt(arg[6]);	            
		        }
		        
		        if (arg.length > 7)
		        {
		        	cutCmd = arg[7];
		        }
		        
		        if (arg.length > 8)
		        {
		        	initCmd = arg[8];
		        }
		        
		        if (arg.length > 9)
		        {
		        	passCmd = arg[9];
		        }
	        }
	
	        port = new Javax_SerialConnection(para);

            port.openConnection();

            return true;
        }
        catch (SerialConnectionException ex)
        {
            ex.printStackTrace();
            new MessageBox(Language.apply("打开串口打印机异常:\n") + ex.getMessage());
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        	new MessageBox(Language.apply("打开串口打印机异常:\n") + ex.getMessage());
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
    
    public void cutPaper_Journal()
    {
        cutPaper_Normal();
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
		v.add(new String[]{Language.apply("波特率"),"9600","110","300","600","1200","2400","4800","19200"});
		v.add(new String[]{Language.apply("奇偶效验位"),"None","Odd","Even"});
		v.add(new String[]{Language.apply("数据位"),"8","7","6","5","4"});
		v.add(new String[]{Language.apply("停止位"),"1","1.5","2"});
    	v.add(new String[]{Language.apply("是否显示切纸提示"),"N","Y"});
    	v.add(new String[]{Language.apply("切纸前走纸的行数"),"0"});
    	v.add(new String[]{Language.apply("切纸命令")});
    	v.add(new String[]{Language.apply("初始化命令")});
    	v.add(new String[]{Language.apply("分页走纸命令")});
    	
		return v;
	}

	public String getDiscription() 
	{
		return Language.apply("JAVAX串口打印机");
	}

	public void setEmptyMsg_Slip(String msg)
	{
	}
}