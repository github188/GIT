
package com.royalstone.pos.io;
import  com.royalstone.pos.common.*;

public class PosInput 
{
	public PosInput( int k )
	{
		key = k;
	}

	public int key()
	{
		return key;
	}

	public int getKey()
	{
		return key;
	}

	public String toString()
	{
		return PosFunction.toString(key);
	}

	private int key ;
}
