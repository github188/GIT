
package com.royalstone.pos.io;
import  com.royalstone.pos.common.*;

public class PosInputNewPin extends PosInput
{
	public PosInputNewPin ( String id, String pin, String newpin )
	{
		super( PosFunction.LOGON );
		Identity = id;
		Passwd   = pin;
		NewPass  = newpin;
	}

	public String getID()
	{
		return Identity;
	}

	public String getPIN()
	{
		return Passwd;
	}
	
	public String getNewPin()
	{
		return NewPass;
	}

	private String Identity  = "";
	private String Passwd    = "";
	private String NewPass   = "";
}
