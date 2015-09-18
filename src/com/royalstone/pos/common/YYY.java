package com.royalstone.pos.common;

import java.io.Serializable;

import org.jdom.Element;

public class YYY implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String iD;
	private String name;
	public YYY(String iD, String name) {
		super();
		this.iD = iD;
		this.name = name;
	}

	public String getiD() {
		return iD;
	}
	public void setiD(String iD) {
		this.iD = iD;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public YYY(Element elm)
	{
		try {
			this.iD = elm.getChildTextTrim("id");
			this.name = elm.getChildTextTrim("name");
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}
	
	public Element toElement() 
	{
		Element ele = new Element("yyy");
		ele.addContent(new Element("id").addContent(this.iD));
		ele.addContent(new Element("name").addContent(this.name));
		return ele;
	}

}
