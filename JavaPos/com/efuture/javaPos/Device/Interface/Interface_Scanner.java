package com.efuture.javaPos.Device.Interface;

import java.util.Vector;

public interface Interface_Scanner {
    public boolean open();

    public void close();
    
    public void setEnable(boolean enable);
    
    public Vector getPara();
    
    public String getDiscription();
}
