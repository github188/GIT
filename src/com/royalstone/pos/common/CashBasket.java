/*
 * Created on 2004-6-23
 *
 */
package com.royalstone.pos.common;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.royalstone.pos.util.ExchangeList;
import com.royalstone.pos.util.InvalidDataException;
import com.royalstone.pos.util.PosConfig;

/**	POS系统在工作过程中,要不断地监控收银抽屉内的资金情况.如果现金超过一定的数额,就需要作出款处理.
 * 为了实现该功能,POS程序引入了两个类: CashBasket, CashBox.
 * CashBasket 代表装钱的抽屉,其中包括多个CashBox. 每个CashBox 中装的"钱"属于同一类型同一币别.
 * 对于收银抽屉内的现金,系统要作两种限制:"软限制"和"硬限制". 
 * 如果现金超过"软限制",系统会给出提示,但仍可继续操作; 如果超过"硬限制",则只可以作出款操作.
 * CashBasket 提供以下成员函数用于判断现金是否超出限制: exceedCashLimit, exceedCashMaxLimit.
 * POS程序在工作过程中,用一个XML文件保存收银抽屉的信息.所以,CashBasket/CashBox 都提供了与XML互转换的方法.
 * @author Mengluoyi
 */
public class CashBasket 
{

	/**
	 * Constructor
	 */
	public CashBasket()
	{
		boxes = new Vector();	
	}
	
	/**
	 * @param elm
	 * @throws InvalidDataException
	 */
	public CashBasket( Element elm ) throws InvalidDataException
	{
		try{
			boxes = new Vector();	

			List list = elm.getChildren( "cashbox" );

			for (int i = 0; i < list.size(); i++)
			{
				Element item = (Element) list.get(i);
				boxes.add( new CashBox(item) );
			}
		
			Element elm_exch = elm.getChild( "exchange_list" );
			exch_lst = new ExchangeList( elm_exch );
			String limit_str = elm.getChildTextTrim( "cash_limit" );
			cash_limit = Integer.parseInt( limit_str );
		} catch ( Exception e ){
			throw new InvalidDataException();
		}
	}
	
	/**	此函数把收银抽屉信息保存到一个文件file 中.
	 * @param file	保存收银抽屉信息的文件
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void dump( String file ) throws FileNotFoundException, IOException
	{
		XMLOutputter outputter = new XMLOutputter( "  ", true, "GB2312" );
		outputter.output(new Document(this.toElement()),  new FileOutputStream( file ));
	}
	
	/**
	 * @param file
	 * @return
	 * @throws InvalidDataException
	 */
	public static CashBasket load( String file ) throws InvalidDataException
	{
		CashBasket basket = null;
		Document doc;
		try {
			doc = (new SAXBuilder()).build( file );
			Element   root   = doc.getRootElement();
			basket = new CashBasket( root );
		} catch (JDOMException e) {
			throw new InvalidDataException( e );
		}
		return basket;
	}
	
	/**	 此函数对现金抽屉作初始化处理.
	 * 只有现金盒子中需要装外币,支票,礼券待只需要考虑人民币.
	 */
	public void init()
	{
		put( Payment.CASH, 		"RMB", 0 );
		put( Payment.CASH, 		"HKD", 0 );
		put( Payment.CHEQUE, 	"RMB", 0 );
		put( Payment.VOUCHER, 	"RMB", 0 );
		put( Payment.CARDBANK, 	"RMB", 0 );
		put( Payment.CARDSHOP, 	"RMB", 0 );
		put( Payment.CARDLOAN, 	"RMB", 0 );
		put( Payment.FLEE, 		"RMB", 0 );
		put( Payment.SAMPLE, 	"RMB", 0 );
		put( Payment.OILTEST, 	"RMB", 0 );
	}
	
	/**
	 * 
	 */
	public void reset()
	{
		boxes = new Vector();	
	}
	
	/**
	 * @return
	 */
	public int size()
	{
		return boxes.size();
	}
	
	/**
	 * @param i
	 * @return
	 */
	public CashBox get(int i)
	{
		return (CashBox) boxes.get(i);
	}
	
	/**
	 * @param type
	 * @param curren_code
	 * @return
	 */
	public CashBox getBox( int type, String curren_code )
	{
		for( int i=0; i<boxes.size(); i++ ){
			CashBox box = ( CashBox ) boxes.get(i);
			if( type == box.getType() && curren_code.equals( box.getCurrenCode() ) ){
				return box;
			}
		}
		return null;
	}
	
	/**	收银抽屉由一系列的CashBox组成. 每个CashBox 中只可以装同一币别同一类型的"钱". 
	 * 如果向抽屉中放钱时, 找到了合适的盒子,就放在这个盒子中, 如果找不到合适的盒子,就在抽屉中增加一个盒子.
	 * @param type
	 * @param curren_code
	 * @param value
	 */
	public void put( int type, String curren_code, int value )
	{
		PosContext context=PosContext.getInstance();
		
		if(!context.isTraining()){

			for( int i=0; i<boxes.size(); i++ ){
				CashBox box = ( CashBox ) boxes.get(i);
				// 找到了合适的盒子,就把钱放在这个盒子中.
				if( type == box.getType() && curren_code.equals( box.getCurrenCode() ) ){
					box.addValue( value );
					return;
				}
			}
		
			//  如果找不到合适的盒子,就在抽屉中增加一个盒子.
			boxes.add( new CashBox( type, curren_code, value ) );
			
		}
		
	}
	
	/**
	 * @return
	 */
	public Element toElement()
	{
		Element elm = new Element( "cashbasket");
		for( int i=0; i<size(); i++ ){
			CashBox box = (CashBox) this.get(i);
			elm.addContent( box.toElement() );	
		}
		elm.addContent( exch_lst.toElement() );
		Element elm_limit = new Element( "cash_limit" ).addContent( "" + cash_limit );
		elm.addContent( elm_limit );
		return elm;
	}
	
	/**	设置汇率表
	 * @param list	汇率表
	 */
	public void setExchange( ExchangeList list )
	{
		exch_lst = list;
	}
	
	/**
	 * @param limit
	 */
	public void setCashLimit( int limit )
	{
		cash_limit = limit;
	}
	
	/**
	 * @return
	 * @throws InvalidDataException
	 */
	public int getCashTotal() throws InvalidDataException
	{
		int total = 0;
		for( int i=0; i< boxes.size(); i++ ){
			CashBox box = (CashBox) boxes.get(i);
			double rate = exch_lst.getRate( box.getCurrenCode() );
			if( box.getType() == Payment.CASH ) total += box.getValue() * rate;
		}
		return total;
	}
	
	/**
	 * @return
	 */
	public boolean exceedCashLimit()
	{
		try {
			return ( getCashTotal() > cash_limit );
		} catch (InvalidDataException e) {
			return false;
		}
	}
	
	/**
	 * @return
	 */
	public boolean exceedCashMaxLimit()
	{
		try {
			cash_maxlimit = Integer.parseInt(PosConfig.getInstance().getString("CASH_MAXLIMIT")) * 100;
			return ( getCashTotal() > cash_maxlimit );
		} catch (InvalidDataException e) {
			return false;
		}
	}
	

	/**
	 * @return
	 */
	public int CompareLimit(){
		try{
		cash_maxlimit = Integer.parseInt(PosConfig.getInstance().getString("CASH_MAXLIMIT")) * 100;
		if(cash_limit < cash_maxlimit){
			if(cash_limit < getCashTotal() && getCashTotal() < cash_maxlimit ){
				return 1;
			}else if(getCashTotal() > cash_maxlimit){
				return 2;
			}else{
				return 0;
			}
			
		} else{
			if(cash_limit < getCashTotal()){
				return 1;
			}else{
				return 0;
			}
			
		}
		
		}catch(InvalidDataException e){
			return 0;
		}catch(Exception e){
			return 0;
		}
	}
	
	/**
	 * <code>boxes</code>		此变量模拟装钱的"盒子".
	 */
	private Vector boxes = null;
	
	/**
	 * <code>exch_lst</code>	汇率表
	 */
	private ExchangeList exch_lst = null;
	
	/**
	 * <code>cash_limit</code>		建议性现金上限
	 */
	private int cash_limit = 0;
	
	/**
	 * <code>cash_maxlimit</code>	强制性现金上限
	 */
	private int cash_maxlimit = 0 ;
}
