/*
 * Created on 2004-6-5
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package test;

import java.io.*;
import java.util.Properties;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PosInit {

	/**
	 * 
	 */
	public PosInit() {
	}

	public static void main(String[] args) {
		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream("pos.ini"));
			String posid = prop.getProperty("posid");
			System.out.println( "POSID: " + posid );
			System.out.println( "SERVER: " + prop.getProperty("server") );
			System.out.println( "PORT: " + prop.getProperty("port") );

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} 

	}
}
