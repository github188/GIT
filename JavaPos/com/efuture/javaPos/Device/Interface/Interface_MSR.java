package com.efuture.javaPos.Device.Interface;

import java.util.Vector;

public interface Interface_MSR
{
    public boolean open();

    public void close();
    
    public void setEnable(boolean enable);
    
    public boolean parseTrack(StringBuffer trackbuffer,StringBuffer track1,StringBuffer track2,StringBuffer track3);

    public Vector getPara();
    
    public String getDiscription();
}
