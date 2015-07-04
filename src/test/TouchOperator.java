/*
 * Created on 2004-6-14
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package test;

import com.royalstone.pos.common.Operator;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TouchOperator {

	public static void main(String[] args) 
	{
		int[] rights = { 1,2,3 };
		Operator op = new Operator( "0001", "24049578", "nameless", rights );
		System.out.println( op );
		System.out.println( op.encryptPin( "0001", "0001" ) );
	}
}
