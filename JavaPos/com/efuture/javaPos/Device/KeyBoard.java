package com.efuture.javaPos.Device;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Interface.Interface_KeyBoard;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.Language;


public class KeyBoard
{
	private static KeyBoard deviceKeyBoard = null;
	
	private Interface_KeyBoard keyboard = null;
	
	private boolean connect = false;
	private boolean enable  = false;
    
    public static KeyBoard getDefault()
    {
        if (KeyBoard.deviceKeyBoard == null)
        {
        	KeyBoard.deviceKeyBoard = new KeyBoard(ConfigClass.KeyBoard1);
        }
        
        return KeyBoard.deviceKeyBoard;
    }    
    
    public KeyBoard(String name)
    {
        try
        {
        	if (name != null && name.trim().length() > 0)
        	{        	
	            Class cl = Class.forName(name);
	
	            keyboard = (Interface_KeyBoard) cl.newInstance();
        	}
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            PosLog.getLog(getClass()).debug(ex);
            keyboard = null;
            
//            new MessageBox("[" + name + "]\n\n键盘设备对象不存在");
            new MessageBox(Language.apply("[{0}]\n\n键盘设备对象不存在", new Object[]{name}));
        }
    }

    public boolean isValid()
    {
    	if (keyboard == null) return false;
    	else return true;
    }
    
    public boolean open()
    {
    	if (keyboard == null) return false;
    	
        connect = keyboard.open();
        
        return connect;
    }

    public void close()
    {
    	if (connect)
    	{
    		setEnable(false);
    		
    		keyboard.close();
    		
    		connect = false;
    	}
    }

    public void setEnable(boolean enable)
    {
    	if (connect)
    	{
    		if (this.enable != enable) keyboard.setEnable(enable);

    		this.enable = enable;
    	}
    }
}
