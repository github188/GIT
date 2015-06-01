package com.efuture.javaPos.Device.Interface;

import java.util.Vector;

public interface Interface_ICCard 
{
    public boolean open();

    public boolean close();
    
    public String findCard();
    
    public String updateCardMoney(String cardno,String operator,double ye);
    
    public Vector getPara();
    
    public String getDiscription();
}
