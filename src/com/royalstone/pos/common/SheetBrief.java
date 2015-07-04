package com.royalstone.pos.common;

import com.royalstone.pos.core.PosSheet;
import com.royalstone.pos.util.PosConfig;
import com.royalstone.pos.util.PosTime;

/**
   @version 1.0 2004.05.24
   @author  Mengluoyi, Royalstone Co., Ltd.
 */

/**
 * POS 程序可以支持多张挂单. 在作解挂操作时,为了便于收银员选择正确的挂单,系统应把挂起的单的主要特征显示出来.
 * SheetBrief 就是为了显示已挂起 POS 工作单的主要特征而引入.
 * 作为特征的要素有三: 1)时间, 2)总金额, 3)油品.
 * @author Mengluoyi
 */
public class SheetBrief
{

	/**
	 * Constructs a SheetBrief instance by an instance of PosSheet.
	 * @param sheet		POS工作单.
	 */
	public SheetBrief ( PosSheet sheet )
	{
		if( sheet != null ){
			sale_len = sheet.getSaleLen();
			if( !sheet.isEmpty() ){
				value_total = sheet.getValue().getValueTotal();
				sheet_time  = sheet.getSale(0).getSysTime();
			}
			//TODO  沧州富达 by fire  2005_5_11 
			//getIndicators( sheet );
		}
	}
	
	/**	从PosSheet 中取出标志性商品(油品). 
	 * 是否标志性商品,需要根据配置文件中的参数确定.
	 * NOTE: 无论在sheet 中是否存在标志性商品, indicators 都将得到初始化. 
	 * 如果sheet 为空,或是没有标志性商品,则数组 indicators 长度为0.
	 * @param sheet
	 */
	private void getIndicators( PosSheet sheet )
	{
		PosConfig config = PosConfig.getInstance();
		int count = 0;
		if( !sheet.isEmpty() ) for( int i=0; i<sheet.getSaleLen(); i++ ){
			Sale sale = sheet.getSale( i );
			if( config.isIndicatorDept( sale.getGoods().getDeptid() ) ) count++;
		}
		
		indicators = new Sale[ count ];
		for( int i=0, j=0; j<count && i < sheet.getSaleLen(); i++ ){
			Sale sale = sheet.getSale( i );
			if( config.isIndicatorDept( sale.getGoods().getDeptid() ) ) indicators[j++] = sale;
		}
	}


	/**	该方法返回一个代表POS工作单特征的字串. 
	 * 按业务需求,如果售出商品中有多个油品,则返回三个'*',
	 * 如果有一个油品,则返回该油品的名称,如果没有油品,则返回一个空白字串.
	 * @return	代表POS工作单特殊的字串.
	 */
	public String toString()
	{
		if( indicators == null ) return " ";
		else if ( indicators.length == 0 ) return " ";
		else if ( indicators.length == 1 ) return (indicators[0].getName() );
		else  return " * * * ";
	}

	/**
	 * @return	POS工作单的总金额.
	 */
	public int getValueTotal()
	{
		return value_total;
	}
	
	/**
	 * @return	POS工作单的时间.
	 */
	public PosTime getTime()
	{
		return sheet_time; 
	}

	/**
	 * @return	true	POS工作单为空<br/>
	 * false	POS工作单非空<br/>
	 */
	public boolean isEmpty()
	{
		return ( sale_len == 0 );
	}

	/**
	 * <code>sheet_time</code>POS工作单的时间.
	 */
	private PosTime sheet_time  = new PosTime();
	
	/**
	 * <code>value_total</code>POS工作单上的商品总金额.
	 */
	private int   	value_total = 0;

	/**
	 * <code>indicators</code>POS工作单上的特征商品(油品).
	 */
	private Sale[]  indicators  = null;
	
	/**
	 * <code>sale_len</code>POS工作单内售出商品清单的长度.
	 */
	private int		sale_len    = 0;
}
