package com.efuture.javaPos.Global;

public class PopTypeModeInfo 
{
	public static String getCodeMode(char mode)
	{
		switch (mode)
		{
			case '0':
			return Language.apply("全场");
			case '1':
			return Language.apply("单品");
			case '2':
			return Language.apply("柜组");
			case '3':
			return Language.apply("品牌");
			case '4':
			return Language.apply("品类");
			case '5':
			return Language.apply("供应商");
			case '6':
			return Language.apply("款式");
			case '7':
			return Language.apply("尺码");
			case '8':
			return Language.apply("属性1");
			case '9':
			return Language.apply("属性2");	
			default:
				return Language.apply("无此方式");
		}
	}
	
	public static String getPopMode(char mode)
	{
		switch (mode)
		{
			case '1':
			return Language.apply("促销价格");
			case '2':
			return Language.apply("减价比率");
			case '3':
			return Language.apply("减价金额");
			default:
				return Language.apply("无此方式");
		}
	}
}
