package com.efuture.javaPos.Device.Interface;

import java.util.Vector;

public interface Interface_Printer
{
    public boolean open();

    public void close();
    
    public void setEnable(boolean enable);

    public void cutPaper_Normal();
    
    public void cutPaper_Journal();
    
    public void cutPaper_Slip();

    public void printLine_Normal(String printStr);

    public void printLine_Journal(String printStr);

    public void printLine_Slip(String printStr);
    
    public void setEmptyMsg_Slip(String msg);
    
    public boolean passPage_Normal();    
    
    public boolean passPage_Journal();
    
    public boolean passPage_Slip();
    
    public void enableRealPrintMode(boolean flag);
    
    public Vector getPara();
    
    public String getDiscription();
}
