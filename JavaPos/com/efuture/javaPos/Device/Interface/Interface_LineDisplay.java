package com.efuture.javaPos.Device.Interface;

import java.util.Vector;

public interface Interface_LineDisplay
{
    public boolean open();

    public void close();
    
    public void setEnable(boolean enable);

    public void display(String message);

    public void displayAt(int row, int col, String message);

    public void clearText();
    
    public Vector getPara();
    
    public String getDiscription();
}
