package com.efuture.javaPos.Struct;

import java.io.Serializable;

public class GiftGoodsDef implements Cloneable, Serializable
{
    private static final long serialVersionUID = 0L;
    public static String[] ref = { "type", "code", "info", "sl", "je","startdate","enddate", "memo" };
    
    public String type;				//类型,1-会员返券 2-纸券 3-刷卡送券 99-券总金额，98-可返券余额
    								//BCRM(1-银行送/2-商场送/3-礼品/4-电子券/11-买券礼券/89-积分换停车券/90-停车券)
    public String code; 			//代码 
    public String info; 			//描述,BCRM(券种券名称:券金额,券种券名称:券金额)
    public int sl; 					//数量
    public double je; 				//金额
    public String startdate;		//券起始有效期
    public String enddate;			//券截止有效期
    public String memo; 			//备注

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
