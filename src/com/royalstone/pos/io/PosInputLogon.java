
package com.royalstone.pos.io;
import  com.royalstone.pos.common.*;

public class PosInputLogon extends PosInput
{
	public PosInputLogon ( String id, String pin )
	{
		super( PosFunction.LOGON );
		Identity	= id;
		Passwd		= pin;
	}
//TODO  沧州富达 by fire  2005_5_11 
//	public PosInputLogon ( String id, String pin, int shiftid )
//	{
//		super( PosFunction.LOGON );
//		this.Identity 	= id;
//		this.Passwd   	= pin;
//		this.Shiftid    = shiftid;
//	}

	public String getID()
	{
		return Identity;
	}

	public String getPIN()
	{
		return Passwd;
	}
//TODO  沧州富达 by fire  2005_5_11 	
//	public int getShiftid()
//	{
//		return Shiftid;
//	}
	
	private String Identity  = "";
	private String Passwd    = "";
//TODO  沧州富达 by fire  2005_5_11 	
//	private int    Shiftid   = 0;
}
