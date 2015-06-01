package com.efuture.javaPos.Struct;


// 备用金类
public class PrepareMoneyDef
{
    static public String[] ref = { "syjh", "syyh", "paycode", "je" };
    
    public String syjh; 			// 收银机号,主键
    public String syyh; 			// 收银员,主键				
    public String paycode; 			// 币种,主键
    public double je; 				// 金额
}
