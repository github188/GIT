package com.efuture.javaPos.Struct;

import java.io.Serializable;

public class CustFilterDef implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static String[] ref = {"desc","chkTrackno","chkLength","chkKeypos","chkkeylen","chkKeyBeginValue","chkKeyEndValue","Trackno",
		"Trackpos","Tracklen","TrackFlag","zkl","area","str1","str2","str3","str4","str5","num1","num2","num3","num4","num5"
		};

	/**
	;描述
	;效验磁道(1,2,3)
	;校验磁道长度
	;效验码起始位
	;效验码的长度（如果在[]表示为特殊字符结尾，找到特殊字符为止，如果没有找到，提示本卡无效）
	;效验码起始值
	;效验码结束值（如果相等，起始值和结束值一样即可）
	;卡号磁道(1,2,3)
	;卡号起始位
	;卡号的长度（如果在[]表示为特殊字符结尾，找到特殊字符为止，如果没有找到，提示本卡无效）
	;卡号的前缀
	;折扣率
	;折扣商品范围shangp(0001:1,0002:)
	*/
	public String desc;
	public int chkTrackno;
	public String chkLength;
	public int chkKeypos;
	public String chkkeylen;
	public String chkKeyBeginValue;
	public String chkKeyEndValue;
	public int Trackno;
	public int Trackpos;
	public String Tracklen;
	public String TrackFlag;
	public double zkl;
	public String area;
	
	public int InputType = -2; // 2- MsrInput , 3-MsrKeyInput ,-2为原始值，代表没有配置
	
	public int ispay = 0;	  //会员卡信息是否用于付款 （0-Y，1-N）
	
	public String str1;				// 备用字段
									//GWZX 是否选择会员卡刷卡方式 （Y/N）
	public String str2;				// 备用字段	
	public String str3;				// 备用字段
	public String str4;				// 备用字段
	public String str5;				// 备用字段
	public double num1;				// 备用字段						
	public double num2;				// 备用字段						
	public double num3;				// 备用字段
	public double num4;				// 备用字段
	public double num5;				// 备用字段
	
}
