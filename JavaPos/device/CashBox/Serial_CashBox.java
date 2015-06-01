package device.CashBox;

import gnu.io.CommPortIdentifier;

import java.util.Enumeration;
import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_CashBox;
import com.efuture.javaPos.Device.SerialPort.SerialConnection;
import com.efuture.javaPos.Device.SerialPort.SerialConnectionException;
import com.efuture.javaPos.Device.SerialPort.SerialParameters;
import com.efuture.javaPos.Global.Language;

import device.DeviceInfo;

public class Serial_CashBox implements Interface_CashBox
{
    protected SerialParameters para = null;
    protected SerialConnection port = null;
    protected String initCmd = null;
    protected String openCmd = null;
    
    public boolean canCheckStatus()
    {
        return false;
    }
    
    public boolean getOpenStatus()
    {
    	return false;
    }
	
    public boolean open()
    {
    	if (DeviceName.deviceCashBox.length() <= 0) return false;
    	
        try
        {    	
	        String[] arg = DeviceName.deviceCashBox.split(",");
	        para = new SerialParameters();
	
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
		        	openCmd = arg[5];
		        }	
		        
		        if (arg.length > 6)
		        {
		        	initCmd = arg[6];
		        }	
	        }
	
	        port = new SerialConnection(para);

            port.openConnection();

            return true;
        }
        catch (SerialConnectionException ex)
        {
            ex.printStackTrace();
            new MessageBox(Language.apply("打开串口钱箱异常:") + "\n" + ex.getMessage());
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        	new MessageBox(Language.apply("打开串口钱箱异常:") + "\n" + ex.getMessage());
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
    
    public void openCashBox()
    {
    	// 发送命令
    	if (openCmd != null && openCmd.trim().length() > 0)
    	{
    		char[] c = DeviceInfo.convertCmdStringToCmdChar(openCmd);
    		for (int i=0;i<c.length;i++)
    		{
    			port.sendChar(c[i]);
    		}
    	}
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
    	v.add(new String[]{Language.apply("开钱箱命令")});
    	v.add(new String[]{Language.apply("初始化命令")});
    	
		return v;
	}

	public String getDiscription() 
	{
		return Language.apply("接串口的钱箱");
	}
}
