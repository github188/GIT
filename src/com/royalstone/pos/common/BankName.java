/*
 * Good Day;
 */
package com.royalstone.pos.common;



/**
 * @author wubingyan
 * Created by King_net  on 2005-7-19  14:57:06
 */
public class BankName {
	public static void main(String[] args) {
		BankName b=new BankName();
		System.out.println(b.getBankName("10"));
	}
	
	//public  Hashtable bankName =new Hashtable();
	public BankName(){
//		bankName.put("01","中国工商银行");
//		bankName.put("02","中国农业银行");
//		bankName.put("03","中国银行");
//		bankName.put("04","中国建设银行");
//		bankName.put("05","中国交通银行");
//		bankName.put("06","邮政储蓄");
//		bankName.put("07","上海浦东发展银行");
//		bankName.put("08","招商银行");
//		bankName.put("09","深圳发展银行");
//		bankName.put("10","广东发展银行");
//		bankName.put("11","商业银行");
//		bankName.put("12","光大银行");
//		bankName.put("13","福建兴业银行");
//		bankName.put("14","中信实业银行");
//		bankName.put("15","华夏银行");
//		bankName.put("16","民生银行");
//		bankName.put("17","农村信用联社");
//		bankName.put("20","异地银行");
//		bankName.put("55","未定义银行");
	}
	public String getBankName(String key){
		if(key.equals("01")) 	return "中国工商银行";
		if(key.equals("02")) 	return "中国农业银行";
		if(key.equals("03")) 	return "中国银行";
		if(key.equals("04")) 	return "中国建设银行";
		if(key.equals("05")) 	return "中国交通银行";
		if(key.equals("06")) 	return "邮政储蓄";
		if(key.equals("07")) 	return "上海浦东发展银行";
		if(key.equals("08")) 	return "招商银行";
		if(key.equals("09")) 	return "深圳发展银行";
		if(key.equals("10")) 	return "广东发展银行";
		if(key.equals("11")) 	return "商业银行";
		if(key.equals("12")) 	return "光大银行";
		if(key.equals("13")) 	return "福建兴业银行";
		if(key.equals("14")) 	return "中信实业银行";
		if(key.equals("15")) 	return "华夏银行";
		if(key.equals("16")) 	return "民生银行";
		if(key.equals("17")) 	return "农村信用联社";
		if(key.equals("20")) 	return "异地银行";
		if(key.equals("55")) 	return "未定义银行";
		
		return "银行代码错误!";
		
	}
	
}
