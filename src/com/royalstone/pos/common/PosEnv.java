/*
 * Created on 2004-6-13
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.common;

import java.io.Serializable;

import com.royalstone.pos.util.Day;
import com.royalstone.pos.util.WorkTurn;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PosEnv implements Serializable
{
	public PosEnv( Day work_date, int work_turn, int sheetid_start )
	{
		this.turn = new WorkTurn( work_date, work_turn );
		this.sheetid_start = sheetid_start;
	}
	
	public PosEnv( WorkTurn turn, int sheetid_start )
	{
		this.turn = turn;
		this.sheetid_start = sheetid_start;
	}
	
	public PosEnv(WorkTurn turn, int sheetid_start,boolean isNewDay) {
			this.turn = turn;
			this.sheetid_start = sheetid_start;
			this.isNewDay=isNewDay;
		}
	
	public Day getWorkDate()
	{
		return turn.getWorkDate();
	}
	
	public int getShiftid()
	{
		return turn.getShiftid();
	}
	
	public WorkTurn getWorkTurn()
	{
		return turn;
	}
	
	public int getSheetidStart()
	{
		return sheetid_start;
	}
	
	public String toString()
	{
		return "work_date: " + turn.getWorkDate() + "; shiftid: " + turn.getShiftid()
			+ "; sheetid: " + sheetid_start + "; ";
	}

	public boolean isNewDay() {
		return isNewDay;
	}

	public void setNewDay(boolean value) {
		isNewDay = value;
	}

	private WorkTurn turn;
	private int sheetid_start;
	private boolean isNewDay = false;
}
