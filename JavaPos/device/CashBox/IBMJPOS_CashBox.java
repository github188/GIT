package device.CashBox;

import java.util.Vector;

import jpos.CashDrawer;
import jpos.CashDrawerConst;
import jpos.JposException;
import jpos.events.StatusUpdateEvent;
import jpos.events.StatusUpdateListener;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_CashBox;
import com.efuture.javaPos.Global.Language;


public class IBMJPOS_CashBox implements Interface_CashBox
{
    private CashDrawer cashBox;
    
    private EventListener event = null;
    private boolean openstatus = false;
    
    public boolean open()
    {
    	if (DeviceName.deviceCashBox.length() <= 0) return false;
    	
        cashBox = new CashDrawer();

        event = new EventListener();
        
        try
        {
            cashBox.open(DeviceName.deviceCashBox);
            
            return true;
        }
        catch (JposException e)
        {
            e.printStackTrace();
            new MessageBox(Language.apply("打开JPOS钱箱异常:") + "\n" + e.getMessage());
        }
        
        return false;
    }

    public void close()
    {
        try
        {
        	if (cashBox != null) cashBox.close();
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
        		if (!cashBox.getClaimed())
        		{
	        		cashBox.claim(1000);
	        		cashBox.setDeviceEnabled(true);
	        		cashBox.addStatusUpdateListener(event);
        		}
        	}
        	else
        	{
        		if (cashBox.getClaimed())
        		{        		
        			cashBox.removeStatusUpdateListener(event);
	        		cashBox.setDeviceEnabled(false);
	        		cashBox.release();
        		}
        	}
        }
        catch (JposException e)
        {
            e.printStackTrace();
        }
    }
    
    public void openCashBox()
    {
        try
        {
            cashBox.openDrawer();
        }
        catch (JposException e)
        {
            e.printStackTrace();
        }
    }
    
    public boolean canCheckStatus()
    {
    	return true;
    }
    
    public boolean getOpenStatus()
    {
    	return openstatus;
    }

    private class EventListener implements StatusUpdateListener
    {
        public void statusUpdateOccurred(StatusUpdateEvent sue)
        {
            try
            {
            	if (sue.getStatus() == CashDrawerConst.CASH_SUE_DRAWEROPEN)
            		openstatus = true;
            	else 
            		openstatus = false;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                
                openstatus = false;
            }
        }
    }

	public Vector getPara() {
		Vector v = new Vector();
		v.add(new String[]{ Language.apply("JPOS逻辑名") ,"POSCashBox1"});
		return v;
	}

	public String getDiscription() {
		return Language.apply("IBM的JPOS驱动方式钱箱");
	}
   
}
