/*
 * Good Day;
 */
package com.royalstone.pos.common;

/**
 * @author wubingyan
 * Created by King_net  on 2005-7-19  16:11:25
 * 1. ��OPN�� -ǩ��
2. ��PCA�� -����
3.��PRA�� -����
4.	��ATZ��-Ԥ��Ȩ
5.	��ARZ��-Ԥ��Ȩ����
6.	��PAA��-Ԥ��Ȩ���
7.	��PSA��-Ԥ��Ȩ��ɳ���
8.	��PST��-Ԥ��Ȩ����
9.	��INQ��-����ѯ

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
		if(key.equals("OPN")) 	return "ǩ��";
		if(key.equals("PCA")) 	return "����";
		if(key.equals("PRA")) 	return "����";
		if(key.equals("ATZ")) 	return "Ԥ��Ȩ";
		if(key.equals("ARZ")) 	return "Ԥ��Ȩ����";
		if(key.equals("PAA")) 	return "Ԥ��Ȩ���";
		if(key.equals("PSA")) 	return "Ԥ��Ȩ��ɳ���";
		if(key.equals("PST")) 	return "Ԥ��Ȩ����";
		if(key.equals("INQ")) 	return "����ѯ";
		
		return "δ֪����";
	}
	
	}
