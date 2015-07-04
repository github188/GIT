package com.royalstone.pos.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * @author liangxinbiao
 */

public class DbConfig {

	private boolean hasServer = true;
	private String driver = "";
	private String url = "";
	private String user = "";
	private String password = "";

	private int maxActive = 2;
	private int maxIdel = 2;
	private int maxWaitTime = 10000;
	
	private String dataSrc="";


	public DbConfig(String filename) {

		File file = new File(filename);

		if (file != null && file.exists()) {

			Properties prop = new Properties();
			try {
				prop.load(new FileInputStream(filename));
				String hasServer = prop.getProperty("HasServer", "1");

				if (hasServer.equals("0")) {

					this.hasServer = false;
					
				} else {
					
					this.hasServer = true;
					
				}

				this.driver =
					prop.getProperty(
						"Driver",
						"com.microsoft.jdbc.sqlserver.SQLServerDriver");
				
				this.url =
					prop.getProperty(
						"Url",
						"jdbc:microsoft:sqlserver://localhost:1433;SelectMethod=Cursor;databasename=MyshopPOS_CZ");
				this.user = prop.getProperty("User", "sa");
				this.password = prop.getProperty("Password", "sa");
				
				try{
					
					this.maxActive=Integer.parseInt(prop.getProperty("MaxActive", "2"));
					this.maxIdel=Integer.parseInt(prop.getProperty("MaxIdel", "2"));
					this.maxWaitTime=Integer.parseInt(prop.getProperty("MaxWaitTime", "1000"));
				
				}catch(NumberFormatException e){
					e.printStackTrace();
				}
				
				

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public String getDriver() {
		return driver;
	}

	public boolean hasServer() {
		return hasServer;
	}

	public String getPassword() {
		return password;
	}

	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

	public void setDriver(String string) {
		driver = string;
	}

	public void setHasServer(boolean b) {
		hasServer = b;
	}

	public void setPassword(String string) {
		password = string;
	}

	public void setUrl(String string) {
		url = string;
	}

	public void setUser(String string) {
		user = string;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public int getMaxIdel() {
		return maxIdel;
	}

	public int getMaxWaitTime() {
		return maxWaitTime;
	}

	public void setMaxActive(int i) {
		maxActive = i;
	}

	public void setMaxIdel(int i) {
		maxIdel = i;
	}

	public void setMaxWaitTime(int i) {
		maxWaitTime = i;
	}

	public String getDataSrc() {
		return dataSrc;
	}

	public void setDataSrc(String string) {
		dataSrc = string;
	}

}
