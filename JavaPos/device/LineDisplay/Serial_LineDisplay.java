package device.LineDisplay;

import gnu.io.CommPortIdentifier;

import java.util.Enumeration;
import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_LineDisplay;
import com.efuture.javaPos.Device.SerialPort.SerialConnection;
import com.efuture.javaPos.Device.SerialPort.SerialConnectionException;
import com.efuture.javaPos.Device.SerialPort.SerialParameters;
import com.efuture.javaPos.Global.Language;

import device.DeviceInfo;


public class Serial_LineDisplay implements Interface_LineDisplay
{
    protected SerialParameters para = null;
    protected SerialConnection port = null;
    protected String initCmd = null;
    protected String clearCmd = null;
    protected String dispCmd = null;
    
    public void clearText()
    {
    	// 发送清屏命令
    	if (clearCmd != null && clearCmd.trim().length() > 0)
    	{
    		char[] c = DeviceInfo.convertCmdStringToCmdChar(clearCmd);
    		for (int i=0;i<c.length;i++)
    		{
    			port.sendChar(c[i]);
    		}
    	}
    }

    public void close()
    {
        if (port != null)
        {
            port.closeConnection();
        }
    }

    public void display(String message)
    {
        displayAt(0, 0, message);
    }

    public void displayAt(int row, int col, String message)
    {
		boolean havemsg = false;
		
    	if (dispCmd != null && dispCmd.trim().length() > 0)
    	{
    			String rows[] = dispCmd.toLowerCase().split("\\|");
    		
    			String cmd = null;
    			
    			if (row < rows.length) 
    			{
    				cmd = rows[row];
    			}
    			else 
    			{
    				cmd = rows[rows.length - 1];
    			}
    			
    			String[] s = cmd.toLowerCase().replaceAll("0x","").split("&");   
    			
    			for (int i=0;i<s.length;i++)
    			{
    				if (s[i].equalsIgnoreCase("%MSG%"))
    				{
    					havemsg = true;
    					port.sendString(message);
    				}
    				else if (s[i].toUpperCase().startsWith("%ROW%"))
    				{
    					if (s[i].indexOf(":") < 0) 
    					{
    						port.sendChar((char)(row+1));
    					}
    					else
    					{
    						String[] str = s[i].split(":");
    						port.sendChar((char)(Integer.parseInt(str[1],16)+row));
    					}
    				}
    				else if (s[i].toUpperCase().startsWith("%COL%"))
    				{
    					if (s[i].indexOf(":") < 0) 
    					{
    						port.sendChar((char)(col+1));
    					}
    					else
    					{
    						String[] str = s[i].split(":");
    						port.sendChar((char)(Integer.parseInt(str[1],16)+col));
    					}
    				}
    				else
    				{
    					char c = (char)Integer.parseInt(String.valueOf(s[i]), 16);
    					port.sendChar(c);
    				}
    			}
    		}
    	
    	if (!havemsg) port.sendString(message);
    }

    public boolean open()
    {
        if (DeviceName.deviceLineDisplay.length() <= 0)
        {
            return false;
        }

        try
        {
            String[] arg = DeviceName.deviceLineDisplay.split(",");
            para = new SerialParameters();

            if (arg.length > 0)
            {
                para.setPortName(arg[0]);

                if (arg.length > 1)
                {
                    para.setBaudRate(arg[1]);
                }

                if (arg.length > 2)
                {
                    para.setParity(arg[2]);
                }

                if (arg.length > 3)
                {
                    para.setDatabits(arg[3]);
                }

                if (arg.length > 4)
                {
                    para.setStopbits(arg[4]);
                }
                
		        if (arg.length > 5)
		        {
		        	initCmd = arg[5];
		        }
		        
		        if (arg.length > 6)
		        {
		        	clearCmd = arg[6];
		        }
		        
		        if (arg.length > 7)
		        {
		        	dispCmd = arg[7];
		        }	
            }

            port = new SerialConnection(para);

            port.openConnection();

            return true;
        }
        catch (SerialConnectionException ex)
        {
            ex.printStackTrace();
            new MessageBox(Language.apply("打开串口客显异常:\n") + ex.getMessage());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            new MessageBox(Language.apply("打开串口客显异常:\n") + ex.getMessage());
        }

        return false;
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

    public Vector getPara()
    {
        Vector v = new Vector();
        String comlist = Language.apply("端口号");
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();

        while (portList != null)
        {
            CommPortIdentifier p = (CommPortIdentifier) portList.nextElement();

            if (p == null)
            {
                break;
            }
            else
            {
                comlist += ("," + p.getName());
            }
        }

        v.add(comlist.split(","));
        v.add(new String[] { Language.apply("波特率"), "9600", "110", "300", "600", "1200", "2400", "4800", "19200" });
        v.add(new String[] { Language.apply("奇偶效验位"), "None", "Odd", "Even" });
        v.add(new String[] { Language.apply("数据位"), "8", "7", "6", "5", "4" });
        v.add(new String[] { Language.apply("停止位"), "1", "1.5", "2" });
    	v.add(new String[] { Language.apply("初始化命令") });
    	v.add(new String[] { Language.apply("清屏命令") });
    	v.add(new String[] { Language.apply("显示命令") });

        return v;
    }

    public String getDiscription()
    {
        return Language.apply("标准串口顾客显示牌");
    }
}
