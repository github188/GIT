/*
 * Created on 2004-6-23
 */
package com.royalstone.pos.common;

import org.jdom.Element;

import com.royalstone.pos.util.InvalidDataException;
import com.royalstone.pos.util.Value;


/**	POS系统在工作过程中,要不断地监控收银抽屉内的资金情况.如果现金超过一定的数额,就需要作出款处理.
 * 为了实现该功能,POS程序引入了两个类: CashBasket, CashBox.
 * CashBasket 代表装钱的抽屉,其中包括多个CashBox. 每个CashBox 中装的"钱"属于同一类型同一币别.
 * 对于收银抽屉内的现金,系统要作两种限制:"软限制"和"硬限制". 
 * 如果现金超过"软限制",系统会给出提示,但仍可继续操作; 如果超过"硬限制",则只可以作出款操作.
 * CashBasket 提供以下成员函数用于判断现金是否超出限制: exceedCashLimit, exceedCashMaxLimit.
 * POS程序在工作过程中,用一个XML文件保存收银抽屉的信息.所以,CashBasket/CashBox 都提供了与XML互转换的方法.
 * @author Mengluoyi
 */
public class CashBox {

	/**
	 * @param type			"钱"的类型
	 * @param curren_code	币别
	 * @param value			金额(以分为单位)
	 */
	public CashBox( int type, String curren_code, int value )
	{
		this.type 			= type;
		this.curren_code 	= curren_code;
		this.value 			= value;
	}
	
	/**
	 * @param elm	保存"钱盒子"信息的XML节点
	 * @throws InvalidDataException
	 */
	public CashBox( Element elm ) throws InvalidDataException
	{
		try{
			this.type 			= elm.getChild( "type" ).getTextTrim().charAt(0);
			this.curren_code 	= elm.getChild( "curren_code" ).getTextTrim();
			this.value 			= Integer.parseInt( elm.getChild( "value" ).getTextTrim() ); 
		} catch ( NullPointerException e ){
			throw new InvalidDataException( "CashBox" );
		}
	}
	
	/**
	 * @return	"钱"的类型
	 */
	public int getType()
	{
		return type;
	}
	
	/**	此函数主要在生成XML节点时使用.
	 * @return	"钱"的类型码
	 */
	public String getTypeCode()
	{
		return "" + (char) type;
	}
	
	/**	
	 * @return	金额
	 */
	public int getValue()
	{
		return value;
	}
	
	/**
	 * @return	币种代码: RMB,HKD,USD.
	 */
	public String getCurrenCode()
	{
		return curren_code;
	}
	
	/**	此函数往"钱盒"内增加金额为value 的钱.
	 * @param value	增加的金额
	 */
	public void addValue( int value )
	{
		this.value += value;
	}
	
	/**
	 * for debug use.
	 */
	public String toString()
	{
		return "{ " + Payment.getTypeName(type) + ": " + curren_code + " " + ( new Value (value) ).toString() + " }";	
	}
	
	/**
	 * @return	XML节点.
	 */
	public Element toElement()
	{
		Element elm = new Element( "cashbox" );
		elm.addContent( ( new Element( "type" ).addContent( getTypeCode() )));
		elm.addContent( ( new Element( "curren_code" ).addContent( curren_code )));
		elm.addContent( ( new Element( "value" ).addContent( "" + value )));
		return elm;
	}
	
	/**
	 * <code>type</code>	此变量表示钱盒内所装的"钱"的类型: 现金,礼券,储值卡.
	 */
	private int type;
	
	/**
	 * <code>curren_code</code>	币别: RMB, HKD, USD.
	 */
	private String curren_code;
	
	/**
	 * <code>value</code>	金额(以分为单位).
	 */
	private int value;
}
