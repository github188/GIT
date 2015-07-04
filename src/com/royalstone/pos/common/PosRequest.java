/*
 * Created on 2004-6-15
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.common;

import java.io.Serializable;

import com.royalstone.pos.util.Day;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PosRequest  implements Serializable
{

	public PosRequest( PosContext con )
	{
		storeid = con.getStoreid();
		posid   = con.getPosid();
		placeno = con.getPlaceno();
		cashierid = con.getCashierid();
//修改    沧州富达 by fire  2005_5_11 		
//		work_date = con.getWorkDate();
//		work_turn = con.getShiftid();
		work_date = new Day();
		work_turn = 0;
		
	}
	
	public String getPosid()
	{
		return posid;
	}
	
	public String getCashierid()
	{
		return cashierid;
	}
	
	public String getPlaceno()
	{
		return placeno;
	}
	
	public Day getWorkDate()
	{
		return work_date;
	}
	
	public int getWorkTurn()
	{
		return work_turn;
	}
	
	public String getStoreid()
	{
		return storeid;
	}
	
	public String toString()
	{
		return ( "storeid:" + storeid + "; posid:" + posid 
			+ "; work_date:" + work_date.toString() + "; work_turn:" + work_turn ) ;
	}
	
	private String storeid;
	private String posid;
	private String placeno;
	private String cashierid;
	private Day work_date;
	private int work_turn;

}
