package device.LineDisplay;

import java.util.Vector;

import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Device.Interface.Interface_LineDisplay;
import com.efuture.javaPos.Device.Interface.Interface_Printer;
import com.efuture.javaPos.Global.Language;

import device.DeviceInfo;


public class Printer_LineDisplay implements Interface_LineDisplay
{
	Interface_Printer printer = null;
	String printtype = null;
	String dispstr = null;
	char[] clearcmd = null;
	
	public void clearText() 
	{
		if (printer == null)
        {
			Printer.getDefault().printer.printLine_Normal(String.valueOf(clearcmd));
        }
        else
        {
            printer.printLine_Normal(String.valueOf(clearcmd));
        }    	
	}

	public void displayAt(int row, int col, String message) 
	{
		char[] dispcmd = null;
		
		if (printtype.equalsIgnoreCase("EPSON320"))
    	{
			dispcmd = new char[] { 0x1b, 0x70, 0x00, 0x10, 0x20, 0x0d };
			
			String line = "[" + (row + 1) + ";" + col + "H" + message;
			if (printer == null)
			{
				Printer.getDefault().printer.printLine_Normal(String.valueOf(dispcmd));
				Printer.getDefault().printer.printLine_Normal(line);
			}
			else
			{
				printer.printLine_Normal(String.valueOf(dispcmd));
				printer.printLine_Normal(line);
			}
    	}	
		else
		if (printtype.equalsIgnoreCase("EPSONM156X"))
		{
			dispcmd = new char[] { 0x1b, 0x3d, 0x02 };
			
			if (printer == null)
			{
				Printer.getDefault().printer.printLine_Normal(String.valueOf(dispcmd));
				Printer.getDefault().printer.printLine_Normal(message);
			}
			else
			{
				printer.printLine_Normal(String.valueOf(dispcmd));
				printer.printLine_Normal(message);
			}
		}
		else
		{
	    	if (dispstr != null && dispstr.trim().length() > 0)
	    	{
	    		StringBuffer msgbuf = new StringBuffer();
	        	String[] s = dispstr.toLowerCase().replaceAll("0x","").split("&");
	        	for (int i=0;i<s.length;i++)
	        	{
	        		if (s[i].equalsIgnoreCase("%MSG%"))
	        		{
	        			msgbuf.append(message);
	        		}
	        		else
	        		if (s[i].toUpperCase().startsWith("%ROW%"))
	        		{
	        			if (s[i].indexOf(":") < 0) msgbuf.append((char)(row+1));
	        			else
	        			{
	        				String[] str = s[i].split(":");
	        				msgbuf.append((char)(Integer.parseInt(str[1],16)+row));
	        			}
	        		}
	        		else
	        		if (s[i].toUpperCase().startsWith("%COL%"))
	        		{
	        			if (s[i].indexOf(":") < 0) msgbuf.append((char)(col+1));
	        			else
	        			{
	        				String[] str = s[i].split(":");
	        				msgbuf.append((char)(Integer.parseInt(str[1],16)+col));
	        			}
	        		}
	        		else
	        		{
		        		char c = (char)Integer.parseInt(String.valueOf(s[i]), 16);
		        		msgbuf.append(c);
	        		}
	        	}
				if (printer == null)
				{
					Printer.getDefault().printer.printLine_Normal(msgbuf.toString());
				}
				else
				{
					printer.printLine_Normal(msgbuf.toString());
				}
	    	}
		}
	}

	public Vector getPara()
	{
		Vector v = new Vector();
		v.add(new String[] { Language.apply("打印机类型"),"EPSON320","EPSONM156X","Else"});
        v.add(new String[] { Language.apply("清屏命令")});
        v.add(new String[] { Language.apply("显示命令")});
        
		return v;
	}

	public String getDiscription() 
	{
		return Language.apply("各类接打印机的顾客显示牌");
	}
	
	public boolean open() 
	{
		String clearstr = null;
		
		//
		printtype = "";
    	if (DeviceName.deviceLineDisplay != null)
	    {
    		String[] arg = DeviceName.deviceLineDisplay.split(",");
    		printtype = arg[0];
    		if (arg.length > 1) clearstr = arg[1];
    		if (arg.length > 2)  dispstr = arg[2];
	    }

    	// clear cmd
    	if (printtype.equalsIgnoreCase("EPSON320") || printtype.equalsIgnoreCase("EPSONM156X"))
    	{
    		clearcmd = new char[] { 0x1b,0x40 };
    	}
    	else
    	{
			if (clearstr != null && clearstr.trim().length() > 0)
			{
				clearcmd = DeviceInfo.convertCmdStringToCmdChar(clearstr);
			}
    	}
		    	
		if (printer == null) return Printer.getDefault().getStatus();
    	else return true;
	}
	
	public void display(String message) 
	{
		displayAt(0, 0, message);
	}
	
	public void close() 
	{
	}

	public void setEnable(boolean enable)
	{
	}
}
