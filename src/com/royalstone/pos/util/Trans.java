/*
 * Good Day;
 */
package com.royalstone.pos.util;

/**
 * @author wubingyan
 * Created by King_net  on 2005-7-17  16:06:54
 */
public class Trans {
	public static void main(String[] args) {
	}
	static{
		System.loadLibrary("TransDll");
	}
	
	public native static String trans(String parms);
	
	 
	
}
