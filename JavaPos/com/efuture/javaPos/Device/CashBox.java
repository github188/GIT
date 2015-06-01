package com.efuture.javaPos.Device;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Interface.Interface_CashBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.Language;


public class CashBox
{
    private static CashBox deviceCashBox = null;
    
    private Interface_CashBox cashBox = null;
    
    private boolean connect = false;
    private boolean enable = false;

    public static CashBox getDefault()
    {
        if (CashBox.deviceCashBox == null)
        {
        	CashBox.deviceCashBox = new CashBox(ConfigClass.CashBox1);
        }
        
        return CashBox.deviceCashBox;
    }
    
    public CashBox(String name)
    {
        try
        {
            if ((name != null) && (name.trim().length() > 0))
            {
                Class cl = Class.forName(name);

                cashBox = (Interface_CashBox) cl.newInstance();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            PosLog.getLog(getClass()).debug(ex);
            cashBox = null;
            
            //new MessageBox("[" + name + "]\n\n钱箱设备对象不存在");
            new MessageBox(Language.apply("[{0}]\n\n钱箱设备对象不存在", new Object[]{name}));
        }
    }

    public boolean isValid()
    {
        if (cashBox == null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean open()
    {
        if (cashBox == null)
        {
            return false;
        }

        connect = cashBox.open();

        return connect;
    }

    public void close()
    {
        if (connect)
        {
            setEnable(false);

            cashBox.close();

            connect = false;
        }
    }

    public void setEnable(boolean enable)
    {
        if (connect)
        {
            if (this.enable != enable)
            {
                cashBox.setEnable(enable);
            }

            this.enable = enable;
        }
    }

    public void openCashBox()
    {
        if (connect && enable)
        {
            if (!getOpenStatus())
            {
                cashBox.openCashBox();
            }
        }
    }

    public boolean getOpenStatus()
    {
        if (connect && enable)
        {
            return cashBox.getOpenStatus();
        }
        else
        {
            return false;
        }
    }
    
    public boolean canCheckStatus()
    {
        if (connect && enable)
        {
            return cashBox.canCheckStatus();
        }
        else
        {
            return false;
        }
    }
}
