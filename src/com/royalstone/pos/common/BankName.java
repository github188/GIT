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
//		bankName.put("01","�й���������");
//		bankName.put("02","�й�ũҵ����");
//		bankName.put("03","�й�����");
//		bankName.put("04","�й���������");
//		bankName.put("05","�й���ͨ����");
//		bankName.put("06","��������");
//		bankName.put("07","�Ϻ��ֶ���չ����");
//		bankName.put("08","��������");
//		bankName.put("09","���ڷ�չ����");
//		bankName.put("10","�㶫��չ����");
//		bankName.put("11","��ҵ����");
//		bankName.put("12","�������");
//		bankName.put("13","������ҵ����");
//		bankName.put("14","����ʵҵ����");
//		bankName.put("15","��������");
//		bankName.put("16","��������");
//		bankName.put("17","ũ����������");
//		bankName.put("20","�������");
//		bankName.put("55","δ��������");
	}
	public String getBankName(String key){
		if(key.equals("01")) 	return "�й���������";
		if(key.equals("02")) 	return "�й�ũҵ����";
		if(key.equals("03")) 	return "�й�����";
		if(key.equals("04")) 	return "�й���������";
		if(key.equals("05")) 	return "�й���ͨ����";
		if(key.equals("06")) 	return "��������";
		if(key.equals("07")) 	return "�Ϻ��ֶ���չ����";
		if(key.equals("08")) 	return "��������";
		if(key.equals("09")) 	return "���ڷ�չ����";
		if(key.equals("10")) 	return "�㶫��չ����";
		if(key.equals("11")) 	return "��ҵ����";
		if(key.equals("12")) 	return "�������";
		if(key.equals("13")) 	return "������ҵ����";
		if(key.equals("14")) 	return "����ʵҵ����";
		if(key.equals("15")) 	return "��������";
		if(key.equals("16")) 	return "��������";
		if(key.equals("17")) 	return "ũ����������";
		if(key.equals("20")) 	return "�������";
		if(key.equals("55")) 	return "δ��������";
		
		return "���д������!";
		
	}
	
}
