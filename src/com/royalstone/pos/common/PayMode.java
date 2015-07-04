package com.royalstone.pos.common;

import java.io.Serializable;

import org.jdom.Element;

public class PayMode implements Serializable {
	
	public PayMode(String paycode, String payname, double payhl, int payiszl) {
		super();
		this.paycode = paycode;
		this.payname = payname;
		this.payhl = payhl;
		this.payiszl = payiszl;
	}
	
	public PayMode(Element elm)
	{
		try {
			this.paycode = elm.getChildTextTrim("code");
			this.payname = elm.getChildTextTrim("name");
			this.payhl = Double.valueOf(elm.getChildTextTrim("hl")).doubleValue();
			this.payiszl = Integer.valueOf(elm.getChildTextTrim("iszl")).intValue();
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}
	
	public Element toElement() 
	{
		Element ele = new Element("paymode");
		ele.addContent(new Element("code").addContent(this.paycode));
		ele.addContent(new Element("name").addContent(this.payname));
		ele.addContent(new Element("hl").addContent("" + this.payhl));
		ele.addContent(new Element("iszl").addContent("" + this.payiszl));
		return ele;
	}
	
	public String getPaycode() {
		return paycode;
	}
	public void setPaycode(String paycode) {
		this.paycode = paycode;
	}
	public String getPayname() {
		return payname;
	}
	public void setPayname(String payname) {
		this.payname = payname;
	}
	public double getPayhl() {
		return payhl;
	}
	public void setPayhl(double payhl) {
		this.payhl = payhl;
	}
	public int getPayiszl() {
		return payiszl;
	}
	public void setPayiszl(int payiszl) {
		this.payiszl = payiszl;
	}
	
	
	private String paycode;
	private String payname;
	private double payhl;
	private int payiszl;
}
