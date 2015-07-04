package com.royalstone.pos.data;

public class BatchnoData {
	private String batchon;/*批次号*/
	private String batchdate;/*批次日期*/
	private int flag;
	private int saleflag;//是否可售1=可售0=禁止销售 用于过期前一个月商品或者批号状态维护不能销售时

	
	
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
