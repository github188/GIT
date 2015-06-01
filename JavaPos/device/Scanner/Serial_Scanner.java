package device.Scanner;

import gnu.io.CommPortIdentifier;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_Scanner;
import com.efuture.javaPos.Device.SerialPort.SerialConnection;
import com.efuture.javaPos.Device.SerialPort.SerialConnectionException;
import com.efuture.javaPos.Device.SerialPort.SerialInputEvent;
import com.efuture.javaPos.Device.SerialPort.SerialParameters;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.PublicMethod;


public class Serial_Scanner implements Interface_Scanner
{
	private SerialParameters para = null;
	private SerialConnection port = null;
	private SerialInputEvent inevent = null;
	private StringBuffer dataStr = null;
	
	public boolean open() 
	{
		dataStr = new StringBuffer();
		
		//
		String[] arg = DeviceName.deviceScanner.split(",");
		para = new SerialParameters();
		
		if (arg.length >= 1)
		{
			para.setPortName(arg[0]);
			if (arg.length > 1)
			{
				para.setBaudRate(arg[1]);
				para.setParity(arg[2]);
				para.setDatabits(arg[3]);
				para.setStopbits(arg[4]);
			}
		}
		
		port = new SerialConnection(para);
		
		//
		inevent = new SerialInputEvent()
		{
		    public void inputData(int data)
		    {
		    	SerialInput(data);
		    }
		};
		port.inputevent = inevent;
		
		// 
		try
		{	
			port.openConnection();
			
			return true;
		}
		catch(SerialConnectionException ex)
		{
			ex.printStackTrace();
		}
		
		return false;
	}
	
	public void close() 
	{
		if (port != null) port.closeConnection();
	}

	public void setEnable(boolean enable)
	{
	}

	public void SerialInput(final int data)
	{
		
		if ('\r' != (char)data) 
		{
			// 过滤非法字符
			if (data >= 32 && data <= 127)
			{
				dataStr.append((char)data);
			}
		}
		else
		{
	        Display.getDefault().syncExec(new Runnable()
	        {
	            public void run()
	            {
	            	if (Display.getCurrent() != null)
	            	{
		                Control control = Display.getCurrent().getFocusControl();
	
		                if (control != null && control.getClass() != null &&control.getClass().getName().equals("org.eclipse.swt.widgets.Text"))
		                {
		                	dataStr.append('\r');
	                        for (int j = 0; j < dataStr.length(); j++)
	                        {
	                            Event event = new Event();
	                            event.widget    = control;
	                            event.keyCode   = dataStr.charAt(j);
	                            event.character = dataStr.charAt(j);

	                            if (j == 0) PublicMethod.DEBUG_MSG("Start");
	                            PublicMethod.DEBUG_MSG(String.valueOf(j) + "KEYCODE:" + String.valueOf(event.keyCode) + "|KEYCHAR:" + event.character);
	                            if (j >= dataStr.length()-1) PublicMethod.DEBUG_MSG("End");
	                            
	                            event.type = SWT.KeyDown;
	                            Display.getCurrent().post(event);

	                            event.type = SWT.KeyUp;
	                            Display.getCurrent().post(event);
	                        }
		                }
	            	}
	            }
	        });
	        
	        System.out.println(dataStr.toString());
	        //
	        dataStr.delete(0,dataStr.length());
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
		return v;
	}

	public String getDiscription() 
	{
		return Language.apply("标准串口扫描仪");
	}
}
