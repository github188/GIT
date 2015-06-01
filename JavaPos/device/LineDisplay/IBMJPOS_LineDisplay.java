package device.LineDisplay;

import java.util.Vector;

import jpos.JposException;
import jpos.LineDisplay;
import jpos.LineDisplayConst;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_LineDisplay;
import com.efuture.javaPos.Global.Language;

public class IBMJPOS_LineDisplay implements Interface_LineDisplay
{
    LineDisplay display = null;
    private int charset = 0;
    private int screenMode = -1;

    public boolean open()
    {
    	if (DeviceName.deviceLineDisplay.length() <= 0) return false;
    	
        display = new LineDisplay();

        String[] arg = DeviceName.deviceLineDisplay.split(",");

        if (arg.length > 1)
        {
            charset = Convert.toInt(arg[1]);
        }
        
        if (arg.length > 2)
        {
        	screenMode = Convert.toInt(arg[2]);
        }
        
        try
        {
            display.open(arg[0]);
            
            return true;
        }
        catch (JposException e)
        {
            e.printStackTrace();
            new MessageBox(Language.apply("打开JPOS顾客牌异常:") + "\n" + e.getMessage());
        }
        
        return false;
    }

    public void close()
    {
        try
        {
        	if (display != null) display.close();
        }
        catch (JposException e)
        {
            e.printStackTrace();
        }
    }

    public void setEnable(boolean enable)
    {
        try
        {
        	if (enable)
        	{
        		if (!display.getClaimed())
        		{   
        			display.claim(1000);
        			
        			if (screenMode >= 0)
        			{
        				display.setScreenMode(screenMode);
        			}

                    display.setDeviceEnabled(true);
                    
                    if (charset > 0)
                    {
                    	display.setCharacterSet(charset);
                    }
        		}
        	}
        	else
        	{
        		if (display.getClaimed())
        		{          		
	        		display.setDeviceEnabled(false);
	        		display.release();
        		}
        	}
        }
        catch (JposException e)
        {
            e.printStackTrace();
        }
    }    
    
    public void display(String message)
    {
        try
        {
            display.displayText(message, LineDisplayConst.DISP_DT_NORMAL);
        }
        catch (JposException e)
        {
            e.printStackTrace();
        }
    }

    public void displayAt(int row, int col, String message)
    {
    	
        try
        {
            display.displayTextAt(row, col, message,
                                  LineDisplayConst.DISP_DT_NORMAL);
        }
        catch (JposException e)
        {
            e.printStackTrace();
        }
    }

    public void clearText()
    {
        try
        {
            display.clearText();
        }
        catch (JposException e)
        {
            e.printStackTrace();
        }
    }

	public Vector getPara() {
		Vector v = new Vector();
		v.add(new String[]{Language.apply("JPOS逻辑名"),"LineDisplay1"});
		v.add(new String[]{Language.apply("字库编码")});
		v.add(new String[]{Language.apply("屏幕模式")});
		
		return v;
	}

	public String getDiscription() 
	{
		return Language.apply("IBM的JPOS驱动方式客显示牌");
	}
}
