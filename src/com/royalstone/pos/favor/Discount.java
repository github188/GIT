package com.royalstone.pos.favor;

/**
   @version 1.0 2004.05.25
   @author  Mengluoyi, Royalstone Co., Ltd.
 */

/**		POS可以支持多种折扣. Discount 为各种具体折扣的父类.
 * NOTE: 以下代码的含义有待明确 : 
 * @author Mengluoyi
 */
public class Discount
{
	/**
	 * @param t		折扣类型代码
	 */
	public Discount( int t )
	{
		type = t;
	}

	/**
	 * @return		折扣类型代码
	 */
	public int getType()
	{
		return type;
	}
	
	/**
	 * @return		折扣类型名称
	 */
	public String getTypeName()
	{
		if( type == NONE ) 			return "折扣更正";
		if( type == QUANTITY ) 		return "量贩折扣";
		if( type == DEPT ) 			return "小类折扣";
		if( type == PERIOD ) 		return "时点折扣";
		if( type == PROMOTION ) 	return "促销";
		if( type == MEMBERPRICE ) 	return "会员促销";
		if( type == MEMBERDISC ) 	return "会员折扣";
		if( type == MANUAL ) 		return "手工折扣";
		if( type == ALTPRICE ) 		return "变价";
		if( type == COMPLEX ) 		return "组合促销";
		if( type == BULK ) 			return "量贩促销";
		if( type == SINGLE ) 		return "单项折扣";
		if( type == TOTAL ) 		return "总额折扣";
		if( type == MONEY ) 		return "金额折扣";
		if( type == LOANDISC )      return "挂帐卡折扣";
        if( type == MEMBERDEPT )    return "会员整类折扣";
		return "不详";
	}

	/**
	 * <code>NONE</code>	无折扣
	 */
	final public static int NONE       = 'n';
	/**
	 * <code>QUANTITY</code>	量贩折扣
	 */
	final public static int QUANTITY   = 'v';
	/**
	 * <code>DEPT</code>		小类折扣
	 */
	final public static int DEPT       = 'd';
	/**
	 * <code>PERIOD</code>		时点折扣
	 */
	final public static int PERIOD     = 't';
	/**
	 * <code>PROMOTION</code>	促销
	 */
	final public static int PROMOTION  = 'p';
	/**
	 * <code>MEMBERPRICE</code>	会员促销
	 */
	final public static int MEMBERPRICE= 'h';
	/**
	 * <code>MEMBERDISC</code>	会员折扣
	 */
	final public static int MEMBERDISC = 'k';
	/**
	 * <code>MANUAL</code>		手工折扣
	 */
	final public static int MANUAL     = 'Z';
	/**
	 * <code>ALTPRICE</code>	变价
	 */
	final public static int ALTPRICE   = 'c';
	/**
	 * <code>COMPLEX</code>		组合促销
	 */
	final public static int COMPLEX    = 's';
	/**
	 * <code>BULK</code>		量贩促销
	 */
	final public static int BULK       = 'B';
	/**
	 * <code>SINGLE</code>		单项折扣
	 */
	final public static int SINGLE     = 'i';
	/**
	 * <code>TOTAL</code>		总额折扣
	 */
	final public static int TOTAL      = 'T';
	/**
	 * <code>MONEY</code>		金额折扣
	 */
	final public static int MONEY      = 'm';
    /**
     * <code>LOANDISC</code>	挂帐卡折扣
     */
    final public static int LOANDISC   = 'o';
    //会员整类折扣
    final public static int MEMBERDEPT   = 'k';

	/**
	 * <code>type</code>折扣类型
	 */
	private int type = NONE;
}
