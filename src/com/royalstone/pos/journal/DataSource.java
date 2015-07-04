/*
 * Created on 2004-6-4
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.journal;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DataSource 
{
	public static void main(String[] args) {
	}

	public DataSource( String hostip, int port, String dbname ) 
	{
		dbURL 	= "jdbc:microsoft:sqlserver://" + hostip + ":" + port + ";" + "DatabaseName=" + dbname
			+ ";SelectMethod=Cursor;";
	} 
	
	public Connection open( String user, String passwd )
	{
		try{
			Class.forName ( "com.microsoft.jdbc.sqlserver.SQLServerDriver" );
			connection = DriverManager.getConnection( dbURL, user, passwd );
			return connection;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public Connection connection (){ return connection; }

	public Connection connection = null;
	private String dbURL = "";
}
