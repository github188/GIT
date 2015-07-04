/*
 * Created on 2004-7-3
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
public class TouchString {

	public static void main(String[] args) {
		String name = "ebaker";
		Touch touch = new Touch( name );
		System.out.println ( touch.name() );
		name = "mengly";
		System.out.println ( touch.name() );

		String str = touch.name();
		str = "MengLuoyi";
		System.out.println ( touch.name() );
		
	}
	
}

class Touch
{
	public Touch( String name )
	{
		this.name = name;
	}
	public String name(){ return name; }
	private String name = "nameless";	
}
