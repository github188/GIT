/*
 * Created on 2004-6-29
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
public class TouchDigital {

	public static void main(String[] args) {
		for( int i='0'; i <= '9'; i++ )
		System.out.println( "insert into function_lst values ( " + i + ", 'Fun_" + (char)i + "', '" + (char)i + "' ); " );

		int i = '.';
		System.out.println( "insert into function_lst values ( " + i + ", 'Fun_PERIOD'" +  ", '" + (char)i + "' ); " );
	}
}
