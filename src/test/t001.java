/*
 * Created on 2004-6-1
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package test;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class t001 {

	public static void main(String[] args) {
		t001 t = new t001(9);
		
		System.out.println( "MAX= " + t.getMax() );		
	}
	
	public t001( int max )
	{
		MAX_SHEETS = max;
	}
	
	public int getMax()
	{
		return MAX_SHEETS;
	}
	
	final private int MAX_SHEETS;
}
