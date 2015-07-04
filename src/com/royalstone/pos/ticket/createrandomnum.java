/*
 * Created on 2004-9-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.royalstone.pos.ticket;
import java.util.*;
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class createrandomnum {

	private static int c;
	public int getrandomnum(){
		Random R=new Random(); 
		c=R.nextInt(10); //½çÓÚ0-9 
		return c;
	}
}
