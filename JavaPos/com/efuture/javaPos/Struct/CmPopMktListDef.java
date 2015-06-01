package com.efuture.javaPos.Struct;

import java.io.Serializable;

public class CmPopMktListDef implements Serializable
{
	private static final long serialVersionUID = 1L; 
	
	public static String[] ref = {"dqid","mkt","joinmode"};
	
	public static String[] key = {"dqid","mkt"};
	
	public String dqid;					//档期ID
	public String mkt;		          	//门店
	public String joinmode;				//参与方式,Y:参与 N:不参与

}
