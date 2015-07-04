package com.royalstone.pos.common;

import java.io.*;

final public class PosMode implements Serializable
{
	PosMode()
	{
		mode = ONLINE;
	}
	
	PosMode( int m )
	{
		mode = m;
	}

	public int getMode()
	{
		return mode;
	}

	public void setMode(int m)
	{
		mode = m;
	}

	public boolean equals( int m )
	{
		return ( mode == m );
	}

	public boolean equals( PosMode m)
	{
		return ( mode == m.getMode() );
	}
	
	public void lock()
	{
		locked = true;
	}

	public void unlock()
	{
		locked = false;
	}
	
	public void setTraining( boolean training )
	{
		this.trainflag = training;
	}

	public boolean isLocked()
	{
		return locked;
	}

	public boolean isTraining()
	{
		return trainflag;
	}

	public boolean isOnline()
	{
		return ( mode == ONLINE );
	}

	public boolean isOffline()
	{
		return ( mode == OFFLINE );
	}
	
	public void setOnline()
	{
		mode = ONLINE;
	}
	
	public void setOffline()
	{
		mode = OFFLINE;
	}

	public String code()
	{
		if ( mode == ONLINE )   return "ONLINE";
		if ( mode == OFFLINE )  return "OFFLINE";
		if ( mode == TRAINING ) return "TRAINING";
		return "INVALID";
	}

	public String toString()
	{
		if ( mode == ONLINE )   return "Áª»ú";
		if ( mode == OFFLINE )  return "ÍÑ»ú";
		if ( trainflag ) return "ÅàÑµ";
		return "INVALID";
	}

	final public static int	ONLINE   = 'N';
	final public static int	OFFLINE  = 'F';
	final public static int	TRAINING = 'T';
	private int	mode			= ONLINE ;
	private boolean locked 		= false;
	private boolean trainflag 	= false;
}
