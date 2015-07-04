/*
 * Created on 2004-9-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.royalstone.pos.card.test;

import com.royalstone.pos.ticket.createrandomnum;
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class randomtest {

	
	public static void main(String[] args) {
		createrandomnum randomnum = new createrandomnum();
		int num=randomnum.getrandomnum();
		System.out.println("num="+num);
		
	}
}
