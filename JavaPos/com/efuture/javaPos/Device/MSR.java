package com.efuture.javaPos.Device;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Interface.Interface_MSR;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.Language;


public class MSR
{
	private static MSR deviceMSR = null;
	 
	private Interface_MSR msr = null;
	private String msrname = null;
	
	private boolean connect = false;
	private boolean enable  = false;
    
    public static String MSRTrack1 = "";
    public static String MSRTrack2 = "";
    public static String MSRTrack3 = "";
    public static boolean havaMSR = false;
    
    public static MSR getDefault()
    {
        if (MSR.deviceMSR == null)
        {
        	MSR.deviceMSR = new MSR(ConfigClass.Msr1);
        }
        
        return MSR.deviceMSR;
    }
    
    public MSR(String name)
    {
        try
        {
        	if (name != null && name.trim().length() > 0)
        	{          	
        		msrname = name;
        		
	            Class cl = Class.forName(name);
	
	            msr = (Interface_MSR) cl.newInstance();
        	}
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            PosLog.getLog(getClass()).debug(ex);
            msr = null;
            
//            new MessageBox("[" + name + "]\n\n刷卡设备对象不存在");
            new MessageBox(Language.apply("[{0}]\n\n刷卡设备对象不存在", new Object[]{name}));
        }
    }

    public boolean isValid()
    {
    	if (msr == null) return false;
    	else return true;
    }
    
    public boolean open()
    {
        if (msr == null)
        {
            return false;
        }

        connect = msr.open();
        
        return connect;
    }

    public void close()
    {
        if (connect)
        {
        	setEnable(false);
        	
            msr.close();
            
            connect = false;
        }
    }

    public void setEnable(boolean enable)
    {
        if (connect)
        {
    		if (this.enable != enable) msr.setEnable(enable);

    		this.enable = enable;
        }
    }
    
    public boolean parseTrack(StringBuffer trackbuffer,StringBuffer track1,StringBuffer track2,StringBuffer track3)
    {
        if (connect)
        {
    		return msr.parseTrack(trackbuffer, track1, track2, track3);
        }
        else
        {
        	return false;
        }
    }
    
    public boolean postKeyReleased(boolean trackhaveprefix)
    {
        if (connect)
        {
            if (msrname != null && msrname.indexOf("IBM4614KeyBoard_MSR") >= 0 && trackhaveprefix)
            {
            	return true;
            }
            else
            {
            	return false;
            }
        }
        else
        {
        	return false;
        }
    }
}
