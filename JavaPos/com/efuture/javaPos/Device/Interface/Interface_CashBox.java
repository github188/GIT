package com.efuture.javaPos.Device.Interface;

import java.util.Vector;

public interface Interface_CashBox
{
    public boolean open();

    public void close();
    
    public void setEnable(boolean enable);

    public void openCashBox();

    public boolean getOpenStatus();
    
    public boolean canCheckStatus();
    
    public Vector getPara();
    
    public String getDiscription();
}
