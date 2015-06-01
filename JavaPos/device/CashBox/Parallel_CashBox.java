package device.CashBox;

import gnu.io.CommPortIdentifier;

import java.util.Enumeration;
import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_CashBox;
import com.efuture.javaPos.Device.SerialPort.ParallelConnection;
import com.efuture.javaPos.Device.SerialPort.ParallelConnectionException;
import com.efuture.javaPos.Global.Language;

import device.DeviceInfo;

public class Parallel_CashBox implements Interface_CashBox
{
	protected ParallelConnection port = null;
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
			 
			 port = new ParallelConnection(arg[0]);
			 
			 if (arg.length > 0)
			 {
				 if (arg.length > 1)
		         {
					 openCmd =	arg[1];	
		         }
		            
		         if (arg.length > 2)
		         {
		        	 initCmd = arg[2];
		         }
			 }
			 
			 port.openConnection();
			 
			 return true;
		}
		catch (ParallelConnectionException ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("打开并口钱箱异常:") + "\n" + ex.getMessage());
			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("打开并口钱箱异常:") + "\n" + ex.getMessage());
			return false;
		}
	}
	
	public void close()
	{
		if (port != null)
        {
			port.closeConnection();
        }
	}

	public void openCashBox()
	{
		//  发送命令
    	if (openCmd != null && openCmd.trim().length() > 0)
    	{
    		char[] c = DeviceInfo.convertCmdStringToCmdChar(openCmd);
    		for (int i = 0;i < c.length;i++)
    		{
    			port.sendChar(c[i]);
    		}
    	}
	}

	public void setEnable(boolean enable)
	{
		//	发送初始化命令
    	if (enable && initCmd != null && initCmd.trim().length() > 0)
    	{
    		char[] c = DeviceInfo.convertCmdStringToCmdChar(initCmd);
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
    	v.add(new String[]{Language.apply("开钱箱命令")});
    	v.add(new String[]{Language.apply("初始化命令")});
    	
    	return v;
	}
	
	public String getDiscription()
	{
		return Language.apply("接并口的钱箱");
	}

}
