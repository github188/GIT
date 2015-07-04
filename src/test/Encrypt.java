/*
 * Created on 2004-6-7
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package test;

import java.text.DecimalFormat;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Encrypt 
{

	public static void main(String[] args) {
		if( args.length == 2 ) System.out.println( Encrypt.encryptPin( args[0], args[1] ) );
	}
	
	public static String encryptPin( String id, String pin )
	{
		long k = 123456789;
		
		for( int i=0; i < id.length(); i++){
			long	a = ( (int)id.charAt(i) ) % 13 + 1;
			k = ( k * a ) % 9999999 + 1;
		}
		
		k = k % 98989898 + 99;
		for(int i=0; i<  pin.length(); i++){
			long	a = ( (int)pin.charAt(i) ) % 17 + 1;
			k = ( k % 9876543 + 1 ) * a;
		}
		
		DecimalFormat df = new DecimalFormat( "00000000" );
		return df.format( k % 100000000 );
	}
}
