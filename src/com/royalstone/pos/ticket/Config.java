/*
 *POS Version 4 Product
 *融通系统集成有限公司
 *@author HuangXuean
 *@version 1.0
 */
package com.royalstone.pos.ticket;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Huangxuean
 */
public class Config {	
	private static Config instance = null;
	private String remark,posid,cashier,head1;
	
	private Config(String file){
		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream( file ));
			posid = prop.getProperty("posid").toUpperCase();
			cashier = prop.getProperty("cashier");
			remark = prop.getProperty("remark");
			head1 = prop.getProperty("head1");
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} 
	}
	
	public static Config getInstance(){
		if(instance==null) instance = new Config("POSInvoice.properties");
		return instance;
	}

	public String getPOS(){
		return posid;
	}
	
	public String getCashier(){
		return cashier;
	}
	
	public String getRemark(){
		return remark;
	}
	public String getHead1(){
		return head1;
	}
		
	public static void main(String[] args) {
		
	}
}
