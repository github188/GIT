package com.royalstone.pos.data;

public class BatchnoData {
	private String batchon;/*���κ�*/
	private String batchdate;/*��������*/
	private int flag;
	private int saleflag;//�Ƿ����1=����0=��ֹ���� ���ڹ���ǰһ������Ʒ��������״̬ά����������ʱ

	
	
	public void setBatchon(String batchon) {
		this.batchon = batchon;
	}
	public String getBatchon() {
		return batchon;
	}
	public void setBatchdate(String batchdate) {
		this.batchdate = batchdate;
	}
	public String getBatchdate() {
		return batchdate;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public int getFlag() {
		return flag;
	}
	public void setSaleflag(int saleflag) {
		this.saleflag = saleflag;
	}
	public int getSaleflag() {
		return saleflag;
	}

}
