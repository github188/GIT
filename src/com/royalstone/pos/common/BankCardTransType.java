/*
 * Good Day;
 */
package com.royalstone.pos.common;

/**
 * @author wubingyan
 * Created by King_net  on 2005-7-19  16:11:25
 * 1. “OPN” -签到
2. “PCA” -消费
3.“PRA” -撤消
4.	“ATZ”-预授权
5.	“ARZ”-预授权撤销
6.	“PAA”-预授权完成
7.	“PSA”-预授权完成撤销
8.	“PST”-预授权结算
9.	“INQ”-余额查询

 */
public class BankCardTransType {
	public static void main(String[] args) {
	}
	public final  static String SignIn = "OPN";
	public final  static String Consume = "PCA";
	public final  static String Cancel = "PRA";
	public final  static String PreAuth = "ATZ";
	public final  static String PreAuthCancel = "ARZ";
	public final  static String PreAuthFinish = "PAA";
	public final  static String PreAuthFinishCancel = "PSA";
	public final  static String PreAuthBalance = "PST";
	public final  static String BalanceQuery = "INQ";
	public String getTransType(String key){
		if(key.equals("OPN")) 	return "签到";
		if(key.equals("PCA")) 	return "消费";
		if(key.equals("PRA")) 	return "撤消";
		if(key.equals("ATZ")) 	return "预授权";
		if(key.equals("ARZ")) 	return "预授权撤销";
		if(key.equals("PAA")) 	return "预授权完成";
		if(key.equals("PSA")) 	return "预授权完成撤销";
		if(key.equals("PST")) 	return "预授权结算";
		if(key.equals("INQ")) 	return "余额查询";
		
		return "未知处理";
	}
	
	}
