package com.efuture.javaPos.Device;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Interface.Interface_Scanner;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.Language;

public class Scanner 
{
	private static Scanner deviceScanner = null;
	
	private Interface_Scanner scanner = null;
	
	private boolean connect = false;
	private boolean enable  = false;
	
    public static Scanner getDefault()
    {
        if (Scanner.deviceScanner == null)
        {
        	Scanner.deviceScanner = new Scanner(ConfigClass.Scanner1);
        }
        
        return Scanner.deviceScanner;
    }
    
    public Scanner(String name)
    {
        try
        {
        	if (name != null && name.trim().length() > 0)
        	{        	
	            Class cl = Class.forName(name);
	
	            scanner = (Interface_Scanner) cl.newInstance();
        	}
        }
        catch (Exception ex)
        {
        	PosLog.getLog(getClass()).debug(ex);
            ex.printStackTrace();
            scanner = null;
            
//            new MessageBox("[" + name + "]\n\n扫描设备对象不存在");
            new MessageBox(Language.apply("[{0}]\n\n扫描设备对象不存在", new Object[]{name}));
        }
    }
    
    public boolean isValid()
    {
    	if (scanner == null) return false;
    	else return true;
    }
    
    public boolean open()
    {
    	if (scanner == null) return false;
    	
        connect = scanner.open();
        
        return connect;
    }

    public void close()
    {
    	if (connect)
    	{
    		setEnable(false);
    		
    		scanner.close();
    		
    		connect = false;
    	}
    }

    public void setEnable(boolean enable)
    {
    	if (connect)
    	{
    		if (this.enable != enable) scanner.setEnable(enable);

    		this.enable = enable;
    	}
    }
}
