/*
 * Created on 2004-6-16
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.util;

import java.io.Serializable;
import java.text.DecimalFormat;

import org.jdom.Element;

/**
 * Workturn 是业务上的"班次"在技术上的实现.
 * 其基本要素有二: workdate(营业日期) 和shiftid(班次号). shiftid 为营业员班次的编号, workdate 为营业日期. 
 * 按零售企业管理制度,营业日期可能与系统日期不一致. 
 * 以实例说明如下: 假如规定每日三班,第一班交接时间为早上7点,则在2004年9月1日早晨6:30,营业班次为2004-08-31#03,
 * 7点交班后班次为2004-09-01#01.
 *
 * @author Mengluoyi
 */
public class WorkTurn  implements Serializable
{

	public static void main(String[] args) 
	{
		WorkTurn t = new WorkTurn( new Day(), 2 );
		System.out.println( t.toString() );
	}
	
	
	/**
	 * 缺省的构造函数,取当前系统日期为营业日期.班次号为0. 
	 */
	public WorkTurn()
	{
		workdate = new Day();
		postime  = new PosTime();
		shiftid  = 0;
	}
	
	/**
	 * @param workdate 营业日期.
	 * @param shiftid  营业班次号.
	 */
	public WorkTurn( Day workdate, int shiftid )
	{
		this.workdate = workdate;
		this.shiftid = shiftid;
	}
	
	/**
	 * @param elm	以XML节点表示的班次信息.
	 * @throws InvalidDataException
	 */
	public WorkTurn( Element elm ) throws InvalidDataException
	{
		try {
			shiftid = Integer.parseInt( elm.getChild( "shiftid" ).getTextTrim() );
			workdate = new Day( elm.getChild( "workdate" ) );
		} catch (Exception e) {
			throw new InvalidDataException( "WorkTurn" );
		}
	}
	
	/**
	 * get an XML element which represents WorkTurn obj.
	 * @return	以XML节点表示的班次信息.
	 */
	public Element toElement()
	{
		Element elm = new Element( "workturn" );
		Element d = ( new Element( "workdate" ) ).addContent( workdate.toElement() );
		Element s = ( new Element( "shiftid" ) ).addContent( "" + shiftid );
		elm.addContent( d );
		elm.addContent( s );
		return elm;
	}
	
	/**	取班次信息中的营业日期.
	 * @return	营业日期.
	 */
	public Day getWorkDate()
	{
		return workdate;
	}
	
	/**
	 * @return
	 */
	public String getLWorkDate()
	{
		return workdate.LtoString();
	}
	
	/**
	 * @return
	 */
	public String getLPosTime()
	{
		return postime.LtoString();
	}
	
	/**
	 * 取班次号.
	 * @return	班次号.
	 */
	public int getShiftid()
	{
		return shiftid;
	}
	
	/**
	 * 判断两个班次是否相同.
	 * @param t	班次.
	 * @return	
	 * true		相同.<br/>
	 * false	不同.
	 */
	public boolean equals( WorkTurn t )
	{
		return ( this.workdate.equals(t.workdate) && this.shiftid == t.shiftid );
	}
	
	/**
	 * @param b
	 * @return
	 */
	public int compareTo( WorkTurn b )
	{
		int diff = this.workdate.daysBetween( b.workdate );
		int shift_diff = this.shiftid - b.shiftid;

		if( diff <0 ) return -1;
		if( diff >0 ) return  1;
		if( diff == 0 && shift_diff <0 ) return -1;
		if( diff == 0 && shift_diff >0 ) return  1;
		return 0;
	}
	
	/**
	 * @return	以字串表示的班次信息.如: 2004-09-28#01
	 */
	public String toString()
	{
		DecimalFormat df = new DecimalFormat( "00" );
		return workdate.toString() + "#" + df.format(shiftid); 
	}
	
	public String LtoString()
	{
		DecimalFormat df = new DecimalFormat( "00" );
	    return workdate.LtoString() ; 

	}
	
	private Day workdate;
	private PosTime postime;
	private int shiftid;
}
