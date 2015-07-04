/*
 * Created on 2004-9-22
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.gui.test;

import java.io.IOException;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RuntimeTest {

	public static void main(String[] args) {
		try{
			Process p = Runtime.getRuntime().exec("run.bat");
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
}
