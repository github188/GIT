package com.efuture.javaPos.Struct;

public class SaleAppendDef 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static String[] ref={"syjh","fphm","rowno","netbz","str1","str2","str3","str4","str5","str6","str7","str8","str9","str10","str11","str12","str13","str14","str15","str16","str17","str18","str19","str20"};
	
	public static String[] refUpdate={"str1","str2","str3","str4","str5","str6","str7","str8","str9","str10","str11","str12","str13","str14","str15","str16","str17","str18","str19","str20"};
	
	public String syjh;				// 收银机号,主键
	public long fphm;				// 小票号,主键
	public int rowno;               // 商品行号,主键
	public char netbz;				// 送网标志											
	public String str1;				// 备用
	public String str2;				// 备用
	public String str3;				// 备用
	public String str4;				// 备用
	public String str5;				// 备用
	public String str6;				// 备用
	public String str7;				// 备用
	public String str8;				// 备用
	public String str9;				// 备用
	public String str10;				// 备用
	public String str11;				// 备用
	public String str12;				// 备用
	public String str13;				// 备用
	public String str14;				// 备用
	public String str15;				// 备用
	public String str16;				// 备用
	public String str17;				// 备用
	public String str18;				// 备用
	public String str19;				// 备用
	public String str20;				// 备用
	
	public Object clone()
	{
		try 
		{
			return super.clone();
		} catch (CloneNotSupportedException e) 
		{
			e.printStackTrace();
			return this;
		}
	}
}
