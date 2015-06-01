package com.efuture.javaPos.Device;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Interface.Interface_LineDisplay;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.Language;


public class LineDisplay
{
	private static LineDisplay deviceLineDisplay = null;
	
	private Interface_LineDisplay display = null;
	
	private boolean connect = false;
	private boolean enable  = false;
    
    public static LineDisplay getDefault()
    {
        if (LineDisplay.deviceLineDisplay == null)
        {
        	LineDisplay.deviceLineDisplay = new LineDisplay(ConfigClass.LineDispaly1);
        }
        
        return LineDisplay.deviceLineDisplay;
    }
    
    public LineDisplay(String name)
    {
        try
        {
        	if (name != null && name.trim().length() > 0)
        	{        	
	            Class cl = Class.forName(name);
	
	            display = (Interface_LineDisplay) cl.newInstance();
        	}
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            PosLog.getLog(getClass()).debug(ex);
            display = null;
            
//            new MessageBox("[" + name + "]\n\n客显设备对象不存在");
            new MessageBox(Language.apply("[{0}]\n\n客显设备对象不存在", new Object[]{name}));
        }
    }

    public boolean isValid()
    {
    	if (display == null) return false;
    	else return true;
    }
    
    public boolean open()
    {
    	if (display == null) return false;
    	
    	connect = display.open();
    	
    	return connect;
    }

    public void close()
    {
    	if (connect)
    	{
    		setEnable(false);
    		
    		display.close();
    		
    		connect = false;
    	}
    }

    public void setEnable(boolean enable)
    {
    	if (connect)
    	{
    		if (this.enable != enable) display.setEnable(enable);

    		this.enable = enable;
    	}
    }
    
    public void display(String message)
    {
    	if (connect && enable)
    	{
    		display.display(message);
    	}
    }

    public void displayAt(int row, int col, String message)
    {
    	if (connect && enable)
    	{
    		display.displayAt(row, col, message);
    	}
    }

    public void clearText()
    {
    	if (connect && enable)
    	{
    		display.clearText();
    	}
    }
}
