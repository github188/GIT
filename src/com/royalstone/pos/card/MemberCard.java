/*
 * Created on 2004-5-30
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.card;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Mengluoyi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MemberCard implements Serializable
{
	public MemberCard(){
		
	}
	public MemberCard( String cardno )
	{
		this.cardNo = cardno;
	}
	
	public MemberCard( String cardno, int memberlevel ,BigDecimal totalPoint)
	{
		this.cardNo = cardno;
        this.memberLevel = memberlevel;
        this.totalPoint=totalPoint;
	}

	public String getCardNo()
	{
		return cardNo;
	}
	public void setCardNo(String cardNo){
		this.cardNo=cardNo;
	}
	
	


	
	public int getMemberLevel()
	{
		return memberLevel;
	}
	
	public void setMemberLevel( int memberLevel )
	{
		this.memberLevel = memberLevel; 
	}
	public String getExceptionInfo(){
		return this.exceptionInfo;
	}
	public void setExceptionInfo(String exception){
		this.exceptionInfo=exception;
	}

	/**
	 * @return Returns the currentPoint.
	 */
	public BigDecimal getCurrentPoint() {
		return currentPoint;
	}
	/**
	 * @param currentPoint The currentPoint to set.
	 */
	public void setCurrentPoint(BigDecimal currentPoint) {
		this.currentPoint = currentPoint;
	}
	/**
	 * @return Returns the totalPoint.
	 */
	public BigDecimal getTotalPoint() {
		return totalPoint;
	}
	/**
	 * @param totalPoint The totalPoint to set.
	 */
	public void setTotalPoint(BigDecimal totalPoint) {
		this.totalPoint = totalPoint;
	}


	private String cardNo     = "";
    private int memberLevel   = 0;
	private String exceptionInfo=null;
	private BigDecimal totalPoint=new BigDecimal((double)0);
	private BigDecimal currentPoint=new BigDecimal((double)0);
}
