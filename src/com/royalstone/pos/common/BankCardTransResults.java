/*
 * Good Day;
 */
package com.royalstone.pos.common;


/**
 * @author wubingyan
 * Created by King_net  on 2005-7-19  15:27:54
 */
public class BankCardTransResults {
	public static void main(String[] args) {
	}
	
	public String getBankTransResults(String key){
		if(key.equals("000000")) 	return "���׳ɹ�";
		if(key.equals("001111")) 	return "�����ļ������������ļ�������";
		if(key.equals("001001")) 	return "�Ƿ�����,�ӿڲ�֧�ִ˽���RESERVE";
		if(key.equals("001002")) 	return "�Ƿ����׽��";
		if(key.equals("001003")) 	return "�Ƿ���ˮ��";
		if(key.equals("001004")) 	return "�Ƿ����κ�RESERVE";
		if(key.equals("001005")) 	return "ȡˢ����ʽ����,INI���ô���";
		if(key.equals("001006")) 	return "ˢ������";
		if(key.equals("001007")) 	return "�ŵ���Ϣ����";
		if(key.equals("001008")) 	return "ȡ�������";
		if(key.equals("001009")) 	return "ȡ���ô���";
		if(key.equals("002000")) 	return "ϵͳ��";
		if(key.equals("002001")) 	return "���ͽ��װ�����";
		if(key.equals("002002")) 	return "������Ӧ����ʱ";
		if(key.equals("010000")) 	return "�鷢����";
		if(key.equals("020000")) 	return "�鷢��������������";
		if(key.equals("030000")) 	return "��Ч�̻�";
		if(key.equals("040000")) 	return "û�տ�";
		if(key.equals("050000")) 	return "����ж�";
		if(key.equals("060000")) 	return "����";
		if(key.equals("070000")) 	return "����������û�տ�";
		if(key.equals("090000")) 	return "�������ڴ�����";
		if(key.equals("120000")) 	return "��Ч����";
		if(key.equals("130000")) 	return "��Ч���";
		if(key.equals("140000")) 	return "��Ч����";
		if(key.equals("150000")) 	return "�޴˷�����";
		if(key.equals("190000")) 	return "�������뽻��";
		if(key.equals("200000")) 	return "��ЧӦ��";
		if(key.equals("210000")) 	return "�����κδ���";
		if(key.equals("220000")) 	return "���ɲ�������";
		if(key.equals("230000")) 	return "���ɽ��ܵĽ��׷�";
		if(key.equals("250000")) 	return "δ���ҵ��ļ��ϼ�¼";
		if(key.equals("300000")) 	return "��ʽ����";
		if(key.equals("310000")) 	return "�������Ĳ�֧�ֵ�����";
		if(key.equals("330000")) 	return "���ڵĿ���û�տ���";
		if(key.equals("340000")) 	return "���������ɣ�û�տ���";
		if(key.equals("350000")) 	return "�ܿ�����������ȫ���ܲ��ţ�û�տ���";
		if(key.equals("360000")) 	return "�����ƵĿ���û�տ���";
		if(key.equals("370000")) 	return "�ܿ�����������ȫ���ܲ��ţ�û�տ���";
		if(key.equals("380000")) 	return "������������������루û�տ���";
		if(key.equals("390000")) 	return "�޴����ÿ��ʻ�";
		if(key.equals("400000")) 	return "����Ĺ����в�֧��";
		if(key.equals("410000")) 	return "��ʧ����û�տ���";
		if(key.equals("420000")) 	return "�޴��ʻ�";
		if(key.equals("430000")) 	return "���Կ���û�տ���";
		if(key.equals("440000")) 	return "�޴�Ͷ���ʻ�";
		if(key.equals("510000")) 	return "���㹻�Ĵ��";
		if(key.equals("520000")) 	return "�޴�֧Ʊ�ʻ�";
		if(key.equals("530000")) 	return "�޴˴���ʻ�";
		if(key.equals("540000")) 	return "���ڵĿ�";
		if(key.equals("550000")) 	return "����ȷ������";
		if(key.equals("560000")) 	return "�޴˿���¼";
		if(key.equals("570000")) 	return "������ֿ��˽��еĽ���";
		if(key.equals("580000")) 	return "�������ն˽��еĽ���";
		if(key.equals("590000")) 	return "����������";
		if(key.equals("600000")) 	return "�ܿ����밲ȫ���ܲ�����ϵ";
		if(key.equals("610000")) 	return "����ȡ��������";
		if(key.equals("620000")) 	return "�����ƵĿ�";
		if(key.equals("630000")) 	return "Υ����ȫ���ܹ涨";
		if(key.equals("640000")) 	return "ԭʼ����ȷ";
		if(key.equals("650000")) 	return "����ȡ���������";
		if(key.equals("660000")) 	return "�ܿ�����������ȫ���ܲ���";
		if(key.equals("670000")) 	return "��׽��û�տ���";
		if(key.equals("680000")) 	return "�յ��Ļش�̫��";
		if(key.equals("750000")) 	return "��������������������";
		if(key.equals("770000")) 	return "POS�������������Ĳ�һ��";
		if(key.equals("780000")) 	return "����������Ҫ��POS�ն���������";
		if(key.equals("790000")) 	return "POS�ն��ϴ����ѻ����ݶ��ʲ�ƽ";
		if(key.equals("900000")) 	return "�����л����ڴ���";
		if(key.equals("910000")) 	return "�������򽻻����Ĳ��ܲ���";
		if(key.equals("920000")) 	return "���ڻ����޷��ﵽ";
		if(key.equals("930000")) 	return "����Υ�����������";
		if(key.equals("940000")) 	return "�ظ�����";
		if(key.equals("950000")) 	return "���ڿ��ƴ�";
		if(key.equals("960000")) 	return "ϵͳ�쳣��ʧЧ";
		if(key.equals("970000")) 	return "ATM/POS�ն˺��Ҳ���";
		if(key.equals("980000")) 	return "���������ղ���������Ӧ��";
		if(key.equals("990000")) 	return "PIN ��ʽ��";
		if(key.equals("A00000")) 	return "MAC����ʧ��";
		if(key.equals("Z00000")) 	return "�ٵ�����׼Ӧ��";
		if(key.equals("Z10000")) 	return "MAC����ʧ�������ĳ���";
		if(key.equals("Z20000")) 	return "MISϵͳ���ܰ���׼Ӧ���ͽ��ն�";
		if(key.equals("Z30000")) 	return "MISϵͳ���ܰ���׼Ӧ���ͽ��ն�";
		if(key.equals("Z40000")) 	return "���Ĵ�Ϊ�����ĳ���";
		if(key.equals("Z50000")) 	return "�ղ����������ĵ�Ӧ��";
		if(key.equals("Z60000")) 	return "�ֿ��˱�����������";
		if(key.equals("Z70000")) 	return "�ǳֿ��˱�����������";
		if(key.equals("UM0000")) 	return "ǰ��̨MACKEY��һ��";
		if(key.equals("B00000")) 	return "����ǰ̨�ط����׳ɹ�";
		if(key.equals("B10000")) 	return "��̨ϵͳ��";
		if(key.equals("B20000")) 	return "�������г�ʱ";
		if(key.equals("B30000")) 	return "���ĸ�ʽ��";
		if(key.equals("B40000")) 	return "ǰ̨����MAC��";
		if(key.equals("B50000")) 	return "���Ų���ʶ��";
		if(key.equals("B60000")) 	return "����δ��ͨ";
		if(key.equals("B70000")) 	return "�޴�ԭ����";
		if(key.equals("B80000")) 	return "�˿��޴˹���";
		if(key.equals("B90000")) 	return "��̨��������";
		if(key.equals("BA0000")) 	return "ǰ̨�����̻��Ŵ�";
		if(key.equals("BB0000")) 	return "ǰ̨�����ն˺Ŵ�";
		if(key.equals("BC0000")) 	return "����Ա�����";
		if(key.equals("BD0000")) 	return "�̻�δ��ͨ";
		if(key.equals("BE0000")) 	return "�޴�����Ա����";
		if(key.equals("BF0000")) 	return "����Ա�����";
		if(key.equals("C00000")) 	return "����Ա״̬��";
		if(key.equals("C10000")) 	return "�̻��Ų�����";
		if(key.equals("C20000")) 	return "����������";
		if(key.equals("C30000")) 	return "�Ƿ����׽��";
		if(key.equals("S10000")) 	return "��Ч���״���";
		if(key.equals("S20000")) 	return "��Ч��8583��";
		if(key.equals("S30000")) 	return "���ü��ܽ��̴�";
		if(key.equals("S40000")) 	return "���ذ������벻һ��";
		if(key.equals("S50000")) 	return "���ذ����׽���";
		if(key.equals("S60000")) 	return "���ذ��ն˺Ų���";
		if(key.equals("S70000")) 	return "����KEY��Ϣ����";
		if(key.equals("S70000")) 	return "����KEY��Ϣ����";
		if(key.equals("S80000")) 	return "�޴�ԭ����";
		if(key.equals("S90000")) 	return "�̻�δ��ͨ";
		if(key.equals("SA0000")) 	return "�̻��ѽ���,������ǩ��";
		if(key.equals("SB0000")) 	return "�Ƿ�����";
		if(key.equals("SC0000")) 	return "��ˮ���ظ�";
		if(key.equals("SD0000")) 	return "��Ч���׽��";
		if(key.equals("SE0000")) 	return "ϵͳ��";
		if(key.equals("SF0000")) 	return "��Ч��Ӧ��";
		if(key.equals("R00000")) 	return "�����ɹ�";
		return key+"--δ�������!";
	}
	
	
	
	
}
