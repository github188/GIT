package com.royalstone.pos.core;

import java.io.Serializable;
import java.util.Vector;

import com.royalstone.pos.common.Payment;


/**
   @version 1.0 2004.05.13
   @author  Mengluoyi, Royalstone Co., Ltd.
 */

public class PaymentList implements Serializable
{
	/**
	 * 
	 */
	public PaymentList()
	{
		lst = new Vector();
	}

	/**
	 * @param p
	 */
	public void add( Payment p )
	{
		lst.add(p);
	}

	/**
	   @param r payment reason.
	   @param t payment type.
	   @param curren currency code.
	   @param v value in the currency paid.
	   @param cardno card number, cheque number.
	 */
//	public void add( int r, int t, String curren, int v, int equiv, String no )
//	{
//		Payment p = new Payment( r, t, curren, v, equiv, no );
//		lst.add(p);
//	}

	/**
	 * @return
	 */
	public int size()
	{
		return lst.size();
	}

	/**
	 * @param i
	 * @return
	 */
	public Payment get( int i )
	{
		return (Payment) lst.get(i);
	}

	/**
	 * @return
	 */
	public int getValueSum()
	{
		int v = 0;
		for ( int i=0; i < lst.size(); i++ ){
			Payment p = (Payment) lst.get(i);
			v += p.getValueEquiv();
		}
		return v;
	}

	/**
	 * @return
	 */
	public int getValuePaid()
	{
		int v = 0;
		for ( int i=0; i < lst.size(); i++ ){
			Payment p = (Payment) lst.get(i);
			if( p.getReason() == Payment.PAY ) v += p.getValueEquiv();
		}
		return v;
	}

	/**
	 * @return
	 */
	public int getCashPaid()
	{
		int v = 0;
		for ( int i=0; i < lst.size(); i++ ){
			Payment p = (Payment) lst.get(i);
			if( p.getReason() == Payment.PAY && p.getType() == Payment.CASH ) 
				v += p.getValueEquiv();
		}
		return v;
	}

	/**
	 * Comment for <code>lst</code>
	 */
	private Vector lst;
}
