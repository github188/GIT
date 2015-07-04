package com.royalstone.pos.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.royalstone.pos.common.Goods;

/**
 * 条码转换表
 * 为了处理某些特殊商品,POS系统引入了几类特殊条码:称重码,金额码,称重金额码.这类条码的一部分是商品代码,一部分则表示商品的数量或金额.
 * POS程序读入商品条码后,首先需要进行分析,看是否特殊条码.
 * 如果是特殊条码,则根据相应的规则从条码中取出商品的数量或金额,再查询商品价格和名称等信息.
 * POS程序使用BarcodeFormat 模拟一个特殊条码"变换器", 用 BarcodeMap 实现对条码的检查和变换. 
 * BarcodeMap 中包含有多个 BarcodeFormat.
 * 上层模块主要使用BarcodeMap的两个方法完成变换: matches, convert. 
 * @version 1.0 2004.05.11
 * @author  Mengluoyi, Royalstone Co, Ltd.
 */

public class BarcodeMap 
{
	/**
	 * constructor.
	 */
	public BarcodeMap ( )
	{
		map = new Vector();
	}

	/**	此方法从一个文本文件中装载特殊条码格式定义.
	 * @param 特殊条码格式定义文件.
	 */
	public void load ( String mapfile )
	{
		System.err.println( "Load BarcodeMap from " + mapfile + " ... " );
		BufferedReader in;
		String s;

		try {
			in = new BufferedReader ( new FileReader ( mapfile ) );
			while ( ( s = in.readLine() ) != null )	add ( s );
			in.close();
		} catch (FileNotFoundException e) {
			System.err.println( "Load failed. File not found." );
		} catch (IOException e) {
			System.err.println( "Load failed. IOException." );
		}
		System.err.println( "BarcodeMap Loaded." );
	}

	/**	根据XML配置文件对条码变换表进行初始化.
	 * @param file	 保存了特殊条码定义信息的POS配置文件.
	 */
	public void fromXML( String file )
	{
		try{
			Document  doc    = (new SAXBuilder()).build(new FileInputStream( file ) );
			Element   root   = doc.getRootElement();
			Element   config = root.getChild( "barcodemap" );

			List parms = config.getChildren( "barcodeformat" );

			for (int i = 0; i < parms.size(); i++)
			{
				Element parm_item = (Element) parms.get(i);
				String  bformat   = parm_item.getTextTrim();
System.out.println ( bformat );
				this.add( bformat );
			}
		}
		catch ( Exception e ){
			e.printStackTrace();
		}
	}

	/**	向条码变换表中添加一个特殊条码定义.
	 * @param sformat	特殊条码定义字串.
	 */
	public void add ( String sformat )
	{
		map.add( new BarcodeFormat( sformat ) );
	}

	/**	向条码变换表中添加一个特殊条码定义.
	 * @param bformat	特殊条码变换器.
	 */
	public void add ( BarcodeFormat bformat )
	{
		map.add ( bformat );
	}

	/**	检查商品码code 是否与特殊条码定义相匹配.
	 * @param code
	 * @return	true	code是特殊条码;<br/> false	code不是特殊条码;
	 */
	public boolean matches ( String code )
	{
		for( int i=0; i < map.size(); i++ ){
			BarcodeFormat bformat = ( BarcodeFormat ) map.get(i);
			if ( bformat.matches( code ) ) return true;
		}
		return false;
	}

	/**	此方法对原始的输入条码进行解析和变换,生成规范化的条码(带校验位)及商品数量或金额.
	 * @param code	原始的输入码.
	 * @return	带校验位的规范条码,商品数量或金额.
	 * @see	PosInputGoods
	 */
	public PosInputGoods convert ( String code )
	{
		for( int i=0; i < map.size(); i++ ){
			BarcodeFormat bformat = ( BarcodeFormat ) map.get(i);
			if ( bformat.matches( code ) ) return bformat.convert( code );
		}
		return new PosInputGoods( code, 1 );
	}

	/**	取特殊条码格式表的长度.
	 * @return	特殊条码格式表的长度.
	 */
	public int size ()
	{
		return map.size();
	}

	/**
	 * <code>map</code><br/>此变量中存放特殊条码格式定义.
	 */
	private Vector map;
}

/**	BarcodeFormat 用于模拟"特殊条码变换器". 其方法convert可完成对原始输入码的解析与变换.
 * POS系统中,特殊条码格式定义采用类似下面的格式: 22xxxxxwwwwwc. 
 * 其中,最前面的数字部分(22)为定义头部,是原始输入码匹配格式定义的依据; 
 * 第二部分(xxxxx)为商品掩码,第三部分(wwwww)为数量掩码, 第四部分为校验位. 
 * @author Mengluoyi
 */
class BarcodeFormat
{
	/**
	 * @param fmt	特殊商品码的定义字串. 如:22xxxxxwwwwwc.
	 */
	public BarcodeFormat ( String fmt )
	{
		format = fmt;
		head   = getHead( fmt );
	}

	/**	判断原始的商品码是否与格式定义匹配.
	 * 判断依据有二: 输入码长度是否与格式定义相等; 输入码头部是否与格式定义头部字串匹配.
	 * @param code
	 * @return
	 */
	public boolean matches( String code )
	{
		return  ( code.length() == format.length() )
			&& code.startsWith( head ) ;
	}

	/**	对原始的输入商品码作变换.
	 * @param code	原始的商品输入码.
	 * @return		经过解析和变换后的商品条码,商品数量或商品金额.
	 */
	public PosInputGoods convert ( String code )
	{
		PosInputGoods g;
		String stdcode  = "";
		String sw = "";
		String sm = "";
		int    weight, money;
		int    gt;

		if ( !matches( code ) ) g =  new PosInputGoods ( code );
		else {
			for ( int i=0; i<format.length(); i++ ){
				char cfmt, c;
				cfmt = format.charAt(i);
				c  = code.charAt(i);
				if( ( cfmt >= '0' && cfmt <= '9' ) || cfmt == 'x' ) stdcode += c;
				if( cfmt == 'w' || cfmt == 'm' ) stdcode += '0';
				if( cfmt == 'w' ) sw += c;
				if( cfmt == 'm' ) sm += c;
			}

			stdcode += getChkChar( stdcode );
			try
			{
				weight = Integer.parseInt ( sw );
			}
			catch ( NumberFormatException e)
			{
				weight = 0;
			}

			try
			{
				money = Integer.parseInt ( sm );
			}
			catch ( NumberFormatException e)
			{
				money = 0;
			}

			if( weight > 0 && money == 0 ) gt = Goods.WEIGHT;
			else if( weight == 0 && money > 0 ) gt = Goods.VALUE;
			else gt = Goods.WEIGHT_VALUE;
			g = new PosInputGoods ( stdcode, weight, money, gt, code );

		}
		return g;
	}

	/**	计算检验位.
	 * @param rawcode	无检验的商品条码.
	 * @return			检验位.
	 */
	public static char getChkChar ( String rawcode )
	{
		int len = rawcode.length();
		int sum_odd  = 0;
		int sum_even = 0;
		int sum_last;
		int chk;

		for ( int i = len - 2; i >= 0; i -= 2 ) sum_even += rawcode.charAt( i ) - '0';
		for ( int i = len - 1; i >= 0; i -= 2 ) sum_odd  += rawcode.charAt( i ) - '0';

		sum_last = ( sum_even + sum_odd * 3 ) % 10;
		if ( sum_last > 0 ) chk = 10 - sum_last;
		else chk = sum_last;
		return (char) ( chk + '0' );
	}

	/**	解析条码格式定义的头部字串.
	 * @param fmt	条码格式定义.
	 * @return		格式定义的头部字串.
	 */
	private String getHead ( String fmt )
	{
		String h = "";

		for( int i=0; i< fmt.length() ; i++ ){
		char ch;
			ch = fmt.charAt( i );
			if ( ch < '0' || ch > '9' )break;
			h += ch;
		}
		return h;
	}

	/**
	 * Comment for <code>head</code><br/>
	 * 称重码/金额码的头部. 例如, 22xxxxxwwwwwc 的头部为22.
	 */
	private String head;
	
	/**
	 * <code>format</code><br/>
	 * 完整的称重码/金额码的定义字串. 如, 22xxxxxwwwwwc.
	 */
	private String format;
}
