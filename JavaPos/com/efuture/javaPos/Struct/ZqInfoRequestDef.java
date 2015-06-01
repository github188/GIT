package com.efuture.javaPos.Struct;

import java.io.Serializable;

public class ZqInfoRequestDef implements Cloneable, Serializable
{
    private static final long serialVersionUID = 0L;
    public static String[] ref = { "mktcode","syjh", "fphm", "cardno", "zqinfo", "memo", "str1", "str2", "str3" };
    
    public String mktcode;          //门店号
    public String syjh;				//收银机号
    public long fphm; 				//代码 
    public String cardno;			//卡号
    public String zqinfo; 			//赠券信息
    public String memo; 			//备注
    public String str1;             //备注1
    public String str2;             //备注2
    public String str3;             //备注3

    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();

            return this;
        }
    }
}
