package com.efuture.commonKit;

public class DBParaDef {
	private String paraname;
	private String paratype;
	private Object paravalue;
	public void setParaname(String paraname) {
		this.paraname = paraname;
	}
	public String getParaname() {
		return paraname;
	}
	public void setParatype(String paratype) {
		this.paratype = paratype;
	}
	public String getParatype() {
		return paratype;
	}
	public void setParavalue(Object paravalue) {
		this.paravalue = paravalue;
	}
	public Object getParavalue() {
		return paravalue;
	}
	
	public Object clone()
	{
		DBParaDef para = new DBParaDef();
		para.setParaname(this.getParaname());
		para.setParatype(this.getParatype());
		para.setParavalue(this.getParavalue());
		return para;
	}
}
