/*
 * Created on 2004-6-6
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package test;

import java.io.EOFException;
import java.io.File;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TouchFile {

	public static void main(String[] args) 
	{
		listJournal();
	}
	
	public static void mkdir()
	{
		boolean ok;
		
		touch();
	}
	
	public static boolean prepareDir( String dirname )
	{
		File dir = new File( dirname );
		if( dir.exists() && dir.isDirectory() )  return true;		
		if( dir.exists() && !dir.isDirectory() ) return false;	
		return dir.mkdir();	
	}
	
	public static void touch()
	{
		File file = new File( "work" + File.separator + "basket.xml" );
		System.out.println( "work" + File.separator + "basket.xml" + " exists? " + file.exists() );
	}
	
	public static void listJournal()
	{
		File jdir = new File ( "journal" );
		File[] files = jdir.listFiles();
		for ( int i=0; i<files.length; i++ ){
			if( files[i].isFile() && files[i].getName().endsWith( ".xml" ) )
			System.out.println( files[i].getName() );
		}
	}
	
}


class UserExitException extends EOFException
{
		public UserExitException()
		{
		}
		
		public UserExitException( String s )
		{
			super(s);
		}
}



