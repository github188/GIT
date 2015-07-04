package com.royalstone.pos.common;

import java.io.*;

import com.royalstone.pos.util.Value;
import com.royalstone.pos.util.PosConfig;

/**
   @version 1.0 2004.05.20
   @author  Mengluoyi, Royalstone Co., Ltd.
 */

/**
 * 销售过程中,POS程序需要根据商品价值与顾客支付情况不断计算应收/应找赎金额. 
 * 为了解决这类计算问题而引入SheetValue.
 * 零售业中,对于支付金额与找赎的计算有较为复杂的规定:
 * 找赎金额不得大于已支付的现金( 礼券不设找赎 ).
 * 
 * @author Mengluoyi
 */
public class SheetValue implements Serializable
{
	/**
	 * constructs an instance of SheetValue, all value set to 0.
	 */
	public SheetValue()
	{
		value_total  = 0;
		value_paid   = 0;
		cash_paid    = 0;
		value_unpaid = 0;
		disc_old     = 0;
		disc_current = 0;
		qty=0;
	}

	/**
	 * @param v		an instance of SheetValue. 由一个 SheetValue 实例构造另一个实例.
	 */
	public SheetValue( SheetValue v )
	{
		value_total  = v.value_total;
		value_paid   = v.value_paid;
		cash_paid    = v.cash_paid;
		value_unpaid = v.value_unpaid;
		disc_old     = v.disc_old;
		disc_current = v.disc_current;
		qty=v.qty;
	}

	/**	设置金额.
	 * @param total		总金额.
	 * @param paid		已支付.
	 * @param cash		已付现金.
	 * @param disc		折扣金额.
	 */
	public void setValue( int total, int paid, int cash, int disc,int qty )
	{
        String talRound=PosConfig.getInstance().getString("TALROUND_SCHEMA");
        if("ON".equals(talRound))
             total=(int)Math.floor((double)total/10)*10;
        
        String round =PosConfig.getInstance().getString("ROUND_SCHEMA");
        if("ON".equals(round))
        	total = (int)Math.round((double)total/10)*10;
        
		value_total  = total;
		value_paid   = paid;
		cash_paid    = cash;
		value_unpaid = total - paid;
		disc_old     = disc_current;
		disc_current = disc;
		this.qty=qty;
	}
	
	/**
	 * @param disc	设定折扣金额.
	 */
	public void setDisc(int disc){
		disc_old = 0;
		disc_current = disc;
	}

	/**
	 * @return	商品金额总和(售出商品-更正商品-退货商品).
	 */
	public int getValueTotal()
	{
		return value_total;
	}

	/**
	 * @return	已支付金额.
	 */
	public int getValuePaid()
	{
		return value_paid;
	}

	/**
	 * @return	未支付金额.
	 */
	public int getValueUnPaid()
	{
		return value_unpaid;
	}

	/**	计算 "待支付金额".
	 * 首先查看 value_unpaid, 如果为正,表明顾客支付金额不足,则 value_topay = value_unpaid.
	 * 如果value_unpaid 为负, 表明顾客支付金额已超过应支付金额, 根据"找赎不得超过已付现金"的规定计算找赎金额.
	 * @return	待支付金额. 如果该值为正,其绝对值为顾客还需要再支付的金额,如果该值为负,其绝对值为应找回给顾客的金额.
	 */
	public int getValueToPay()
	{
		if( value_unpaid >= 0 ) return value_unpaid;
		else if( value_total >0 ) {
			int	value_change = 0 - value_unpaid;
			if( cash_paid < value_change ) value_change = cash_paid;
			return (0-value_change);
		}
		else{
			return (value_total - cash_paid);
		}
	}

	/**
	 * @return	折扣总额.
	 */
	public int getDiscTotal()
	{
		return disc_current;
	}

	/**
	 * @return	折扣金额的变化值(可为正,也可为负). 
	 * POS程序在销售过程中要不断地计算应为顾客提供的折扣. 商品售出则可能使折扣增加,而更正和退货可能使折扣收回.
	 * 所以,折扣变化可能为正,也可能为负.
	 */
	public int getDiscDelta()
	{
		return (disc_current-disc_old);
	}
	
	/**
	 * @return 整单销售商品的数量
	 */
	public int getQty(){
		return qty;
	}

	/**
	 * @return	a string summarizing SheetValue info, for debug use.
	 */
	public String toString()
	{
		return ( "Total: "    + new Value( getValueTotal() )
			+ " Paid: "   + new Value( getValuePaid()  )
			+ " UnPaid: " + new Value( getValueUnPaid() )
			+ " ToBePaid: " + new Value( getValueToPay() )
			+ "DiscDelta: " + new Value( getDiscDelta()) );
	}

	/**
	 * <code>value_total</code>	商品的总金额.
	 */
	private int value_total;
	/**
	 * <code>value_paid</code>	已支付金额.
	 */
	private int value_paid;
	/**
	 * <code>value_unpaid</code>	未支付金额.
	 */
	private int value_unpaid;
	/**
	 * <code>disc_current</code>	当前折扣金额.
	 */
	private int disc_current;
	/**
	 * <code>disc_old</code>	原折扣金额.
	 */
	private int disc_old;
	/**
	 * <code>cash_paid</code>	已支付的现金(以分为单位).
	 */
	private int cash_paid;
	
	/**
	 * 整单销售商品的数量
	 */
	private int qty; 
	

}
