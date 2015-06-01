package com.efuture.javaPos.Device.Interface;

import java.util.Vector;

public interface Interface_BankTracker
{
    public boolean open();

    public boolean close();
    
    public String getTracker();
    
    public Vector getPara();
    
    public String getDiscription();

}
