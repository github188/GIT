package com.royalstone.pos.common;

import com.royalstone.pos.favor.BulkFavor;
import com.royalstone.pos.favor.DiscBulk;
import com.royalstone.pos.favor.DiscPrice;
import com.royalstone.pos.favor.DiscRate;
import com.royalstone.pos.favor.Discount;
import com.royalstone.pos.util.*;

import java.io.*;
import java.text.DecimalFormat;

/**
   @version 1.0 2004.05.14
   @author  Mengluoyi, Royalstone Co., Ltd.
 */

public class Sale implements Serializable {
	/**
	 * @param g		商品
	 * @param q		数量
	 */
	public Sale(Goods g, int q) {
		goods = g;
		qty = q;
		type = SALE;
		quickcorrecttype = NO;
		disc_type = Discount.NONE;
		orgcode = "";
		waiter = "";
		authorizer = "";
		placeno = "";
		subsheetid = 1;

		value_std = g.getPrice() * q / g.getX();
		price_fact = g.getPrice();
		value_fact = value_std;
		value_disc = 0;
		sysdate = new Day();
		systime = new PosTime();
		price_std=g.getPrice();
	}

	/**
	 * @param type
	 * @param g
	 * @param value
	 */
	public Sale(int type, Goods g, int value) {
		this.type = type;
		goods = g;
		value_std = value;
		sysdate = new Day();
		systime = new PosTime();
		price_std=g.getPrice();
	}

	/**
	 * @param goods
	 * @param qty
	 * @param type
	 */
	public Sale(Goods goods, int qty, int type) {
		this.goods = goods;
		this.qty = qty;
		this.type = type;
		this.disc_type = Discount.NONE;

		this.value_std = goods.getPrice() * qty / goods.getX();
		this.price_fact = goods.getPrice();
		this.value_fact = value_std;
		this.value_disc = 0;
		this.sysdate = new Day();
		this.systime = new PosTime();
		this.price_std=goods.getPrice();
	}

	//针对挂帐卡优惠
	/**
	 * @param type
	 * @param qty
	 * @param goods
	 * @param loancarddisc
	 */
	public Sale(int type, int qty, Goods goods, int loancarddisc) {
		this.goods = goods;
		this.qty = qty;
		this.type = type;
		this.disc_type = Discount.LOANDISC;

		this.value_std = goods.getPrice() * qty / goods.getX();
		this.price_fact = goods.getPrice();
		this.value_fact = value_std - loancarddisc * qty / goods.getX();
		this.value_disc = loancarddisc * qty / goods.getX();
		this.sysdate = new Day();
		this.systime = new PosTime();
		this.price_std=goods.getPrice();
	}

	/**
	 * @param goods
	 * @param orgcode
	 * @param qty
	 * @param type
	 * @param disc_type
	 * @param waiter
	 * @param authorizer
	 * @param placeno
	 * @param colorsize
	 * @param itemvalue
	 * @param discvalue
	 * @param factvalue
	 * @param trainflag
	 * @param sysdate
	 * @param systime
	 */
	public Sale(
		Goods goods,
		String orgcode,
		int qty,
		int type,
		int disc_type,
		String waiter,
		String authorizer,
		String placeno,
		String colorsize,
		int itemvalue,
		int discvalue,
		int factvalue,
		int trainflag,
		Day sysdate,
		PosTime systime) {
		this.goods = goods;
		this.orgcode = orgcode;
		this.qty = qty;
		this.type = type;
		this.disc_type = disc_type;
		this.waiter = waiter;
		this.authorizer = authorizer;
		this.placeno = placeno;
		this.colorsize = colorsize;
		this.value_std = itemvalue;
		this.value_disc = discvalue;
		this.value_fact = factvalue;
		this.trainflag = trainflag;
		this.sysdate = sysdate;
		this.systime = systime;
		this.price_std=goods.getPrice();

	}

	public long getPrice(){
		return price_std;
	}


	/**
	 * @return
	 */
	public Day getSysDate() {
		return sysdate;
	}

	/**
	 * @return
	 */
	public PosTime getSysTime() {
		return systime;
	}

	/**
	 * @return	Sale 对象的类型代码(售出,更正,即更,退货).
	 */
	public int getType() {
		return type;
	}

	/**	此函数用于生成Sale 对象的XML节点, 返回值类型为String.
	 * @return	Sale 对象的类型代码(售出,更正,即更,退货).
	 */
	public String getTypeCode() {
		return "" + (char) type;
	}

	/**	取Sale对象的折扣类型代码.
	 * @return	商品折扣类型码
	 */
	public int getDiscType() {
		return disc_type;
	}

	/**	此函数用于生成Sale 对象的XML节点, 返回值类型为String.
	 * @return	商品折扣类型码
	 */
	public String getDiscCode() {
		return "" + (char) disc_type;
	}

	/**	从Sale 记录中取出商品对象.
	 * @return	Sale 对象中的商品对象.
	 */
	public Goods getGoods() {
		return goods;
	}

	/**	待明确.
	 * @param g
	 * @return
	 */
	public long getGoodsPrice(Goods g) {
		long goodsprice = (value_std * g.getX()) / qty;
		return goodsprice;
	}

	/**
	 * @return	商品数量
	 */
	public int getQty() {
		return qty;
	}
	
	public void setQty(int Qty){
		this.qty =  Qty;
		}

	/**
	 * @return	享受折扣的商品数量
	 */
	public int getQtyDisc() {
		return qty_disc;
	}

	/**	POS需要支持称重商品和非称重商品. 称重商品的数量需要显示小数点后三位,非称重商品的数量则应以整数形式显示.
	 * 此函数即为生成便于显示的数量字串而设计.
	 * @return	便于显示的数量字串.
	 */
	public String getQtyStr() {
		int x = goods.getX();
		return (x == 1)
			? Integer.toString(qty)
			: df_qty.format((double) qty / x);
	}

	/**
	 * @return	商品编码
	 */
	public String getVgno() {
		return goods.getVgno();
	}

	/**
	 * @return	商品条码
	 */
	public String getBarcode() {
		return goods.getBarcode();
	}

	/**
	 * @return	商品原始的扫描码
	 */
	public String getOrgCode() {
		return orgcode;
	}

	/**
	 * @return	商品色码. 目前,油品的油岛号用色码表示.
	 */
	public String getColorSize() {
		return colorsize;
	}

	/**
	 * @return	营业员
	 */
	public String getWaiter() {
		return waiter;
	}

	/**
	 * @return	授权人员
	 */
	public String getAuthorizer() {
		return authorizer;
	}

	/**
	 * @return	柜组号
	 */
	public String getPlaceno() {
		return placeno;
	}

	/**
	 * @return	商品规格
	 */
	public String getSpec() {
		return goods.getSpec();
	}

	/**
	 * @return	商品销售单位
	 */
	public String getUnit() {
		return goods.getUnit();
	}

	/**
	 * @return	商品子类号
	 */
	public String getDeptid() {
		return goods.getDeptid();
	}

	/**
	 * @return	商品名称
	 */
	public String getName() {
		return goods.getName();
	}

	/**
	 * @param changename
	 */
	public void setName(String changename) {
		this.goods.setName(changename);
	}

	/**
	 * @return	商品标准价
	 */
	public int getStdPrice() {
		return goods.getPrice();
	}

	/**
	 * @param baseprice
	 */
	public void setStdPrice(int baseprice) {
		this.goods.setPrice(baseprice);
	}

	/**
	 * @return	商品的实际有效金额.
	 */
	public long getFactValue() {
		return value_fact;
	}

	/**
	 * @param value	商品的实际有效金额.
	 */
	public void setFactValue(long value) {
		value_fact = value;
	}

	/**
	 * @param value	商品的标准金额(按标价计算的金额).
	 */
	public void setStdValue(long value) {
		value_std = value;
	}

	/**
	 * @param value	商品的实际有效金额.
	 */
	public void setValue(int value) {
		value_fact = value;
	}

	/**
	 * @return	商品的标准金额(按标价计算的金额).
	 */
	public long getStdValue() {
		return value_std;
	}

	/**
	 * @return	商品的折扣金额.
	 */
	public long getDiscValue() {
		return value_disc;
	}

	/**
	 * @param value	商品的折扣金额.
	 */
	public void setDiscValue(long value) {
		value_disc = value;
	}
	
	/**
	 * @return	商品的批次号.
	 */
	public String getBatchno() {
		return this.goods.getBatchno();
	}
	
	public String getmanufact() {
		return this.goods.getmanufact();
	}

	/**
	 * @param dtype		discount type. can be DISC_PROMOTION, DISC_MEMBER.
	 * @param price		new effective price for the sold goods.
	 */
	public void setFactPrice(int dtype, int price) {
		disc_type = dtype;
		price_fact = price;
		value_fact = price * qty / goods.getX();
		value_disc = value_std - value_fact;
	}
	
	public void setFactPrice(long factprice){
		this.price_fact=factprice;
	}

	/**
	 * @param disc
	 */
	public void setDiscount(DiscRate disc) {
		disc_type = disc.getType();
		qty_disc = qty;
		value_disc = (int) Math.rint(value_std * disc.getPoint() * 1.0 / 100);
		value_fact = value_std - value_disc;
		price_fact = value_fact / qty;
	}

	/**
	 * @param disc
	 */
	public void setDiscount(DiscPrice disc) {
		disc_type = disc.getType();
		qty_disc = qty;
		price_fact = disc.getPrice();
		value_fact = (int) Math.rint(price_fact * qty / goods.getX());
		value_disc = value_std - value_fact;
	}

	/**
	 * @param disc
	 */
	public void setDiscValue(DiscPrice disc) {
		disc_type = disc.getType();
		qty_disc = qty;
		price_fact = (int) Math.rint(disc.getPrice() * 1.0 / qty / goods.getX());
		value_fact = (int) (disc.getPrice());
		//disc.getPrice() / goods.getX() ;
		value_disc = value_std - value_fact;
	}

	/**
	 * @param disc
	 */
	public void setDiscPrice(DiscPrice disc) {
		disc_type = disc.getType();
		qty_disc = qty;
		price_fact = disc.getPrice();
		value_fact = (int) Math.rint(price_fact * qty / goods.getX());
		value_disc = value_std - value_fact;
	}

	/**
	 * @param disc
	 * @param qty_bulk
	 */
	public void consumeBulkPrice(DiscBulk disc, int qty_bulk) {
		disc_type = Discount.BULK;
		qty_disc += qty_bulk;
		value_disc += qty_bulk
			* (goods.getPrice() - disc.getPrice())
			/ goods.getX();
		value_fact = value_std - value_disc;
	}

	/**
	 * @param favor
	 */
	public void consumeBulkFavor(BulkFavor favor) {
		if (favor == null)
			return;
		disc_type = Discount.BULK;
		qty_disc += favor.getQty();
		value_disc += favor.getValue();
		value_fact = value_std - value_disc;
	}

	/**	设置Sale 对象的色码
	 * @param colorsize	色码
	 */
	public void setColorSize(String colorsize) {
		this.colorsize = colorsize;
	}

	/**	设置Sale 对象的柜组号
	 * @param placeno	柜组号
	 */
	public void setPlaceno(String placeno) {
		this.placeno = placeno;
	}

	/**
	 * @param waiterid	营业员
	 */
	public void setWaiter(String waiterid) {
		this.waiter = waiterid;
	}

	/**
	 * @param authorizer	授权人员
	 */
	public void setAuthorizer(String authorizer) {
		this.authorizer = authorizer;
	}

	/**	设置Sale对象的原始销售码.
	 * @param code	原始的输入/扫描码.
	 */
	public void setOriginalCode(String code) {
		this.orgcode = code;
	}

	/**	POS可以在一张小票内支持来自不同柜组的多张"子单". 
	 * 为了区分不同柜组的商品,在Sale对象中有 subsheetid 字段.
	 * 此函数用于设置Sale 对象的subsheetid.
	 * @param id	POS小票的子单号.
	 */
	public void setSubSheetid(int id) {
		this.subsheetid = id;
	}

	/**	此函数用于组合促销的折扣分摊计算. 
	 * 组合促销的计算如下:首先把POS收银工作单内的商品按单品作汇总,在组合促销表中求出可享受的折扣,
	 * 再把所有可享受折扣分摊到具体的商品. 在最后的销售流水中,折扣要记录在具体的商品名下.
	 * 此函数供 SaleList.consumeFavor 调用.
	 * @param qty_favor		享受促销价的商品的数量
	 * @param value_favor	享受促销价的商品的金额
	 * @param favor_name	组合促销方案名称
	 */
	public void consumeFavor(
		int qty_favor,
		int value_favor,
		String favor_name) {
		// Exception should be thrown here.
		if (qty_favor > qty)
			return;

		disc_type = Discount.COMPLEX;
		this.favor_name = favor_name;
		qty_disc = qty_favor;

		/**
		 * 在一个Sale 内的商品,有可能部分打折,部分不打折.
		 * 商品总金额 = 打折部分金额 + 不打折部分金额
		 */
		value_fact =
			value_favor + (qty - qty_favor) * getPrice() / goods.getX();
		value_disc = value_std - value_fact;
		price_fact = value_fact / qty;
	}

	public long caculateFavor(
		int qty_favor,
		int value_favor,
		String favor_name) {
		// Exception should be thrown here.
		if (qty_favor > qty)
			return 0;

		int qty_disc = qty_favor;

		long value_fact =
			value_favor + (qty - qty_favor) * getPrice() / goods.getX();
		long value_disc = value_std - value_fact;

		return value_disc;

	}

	/**
	 * 清除商品的组合促销折扣(对普通折扣无影响).
	 */
	public void clearFavor() {
		// only clears DiscComplex.
		if (disc_type == Discount.COMPLEX) {
			disc_type = Discount.NONE;
			this.favor_name = "";
			qty_disc = 0;
			value_disc = 0;
			value_fact = value_std;
			price_fact = goods.getPrice();
		}
	}

	/**
	 * 作用待明确.
	 */
	public void setLastFavor() {
		lastfavor = Discount.COMPLEX;
	}

	/**	作用待明确.
	 * @return
	 */
	public int getlastfavor() {
		return lastfavor;
	}

	/**
	 * 清除所有折扣.
	 */
	public void clearDiscount() {
		disc_type = Discount.NONE;
		price_fact = goods.getPrice();
		value_fact = value_std;
		value_disc = 0;
		qty_disc = 0;
	}

	/**
	 * 将Sale 对象置为"培训记录".
	 */
	public void setAsTraining() {
		trainflag = 1;
	}

	/**
	 * 将Sale 记录设置为"已删除".
	 */
	public void setAsDeleted() {
		trainflag = 2;
	}

	/**	设置Sale 对象的标识标志
	 * @param flag	培训标志
	 */
	public void setTrainFlag(int flag) {
		trainflag = flag;
	}

	/**	此函数在生成XML 文档时使用.
	 * @return	培训标志.
	 */
	public int getTrainFlag() {
		return trainflag;
	}

	/**	作用待明确.
	 * 
	 */
	public void setquickcorrect() {
		this.quickcorrecttype = YES;
	}

	/**	作用待明确.
	 * @return
	 */
	public int getquickcorrect() {
		return this.quickcorrecttype;
	}

	/**	测试商品销售码与Sale对象中的商品是否匹配.
	 * @param code	商品销售码
	 * @return	true		匹配<br/>
	 * false				不匹配
	 */
	public boolean matches(String code) {
		return this.goods.matches(code);
	}

	/**	测试Sale 对象中的商品对象与传入的参数对象是否匹配.
	 * @param g	Goods 对象
	 * @return	true		匹配<br/>
	 * false				不匹配
	 */
	public boolean matches(Goods g) {
		return this.goods.matches(g);
	}

	/**	此函数的作用有待明确.
	 * @param qty_favor
	 * @param value_favor
	 * @param favor_name
	 */
	public void consumeFavorAfter(
		int qty_favor,
		int value_favor,
		String favor_name) {
		// Exception should be thrown here.
		if (qty_favor > qty - qty_disc)
			return;

		disc_type = Discount.COMPLEX;
		this.favor_name = favor_name;
		value_disc
			+= (qty_favor * getPrice() / goods.getX() - value_favor);
		qty_disc += qty_favor;
		value_fact = value_std - value_disc;
		price_fact = value_fact / qty;
	}

	public long caculateFavorAfter(
		int qty_favor,
		int value_favor,
		String favor_name) {
		// Exception should be thrown here.
		if (qty_favor > qty - qty_disc)
			return 0;

		return	(qty_favor * getPrice() / goods.getX() - value_favor);
	}

	/**
	 * @return	促销方案名称
	 */
	public String getFavorName() {
		return favor_name;
	}
	
	public long getPriceFact(){
		return price_fact;
	}

	/**
	 * for debug use.
	 * @return	abstraction of Sale info.
	 */
	public String toString() {
		return goods.toString() + " * " + qty + " " + sysdate + " " + systime;
	}
	/*
	promtype       	0	普通           
	promtype       	1	特价           
	promtype       	2	惠赠           
	v_type         	0	普通           
	v_type         	2	称重           
	v_type         	3	金额           
	v_type         	8	基本           
	v_type         	9	代收银         
	*/

	/**
	 * <code>SALE</code>销售
	 */
	final public static int SALE = 's';
	/**
	 * <code>CORRECT</code>更正
	 */
	final public static int CORRECT = 'v';
	/**
	 * <code>QUICKCORRECT</code>即更
	 */
	final public static int QUICKCORRECT = 'q';
	/**
	 * <code>WITHDRAW</code>退货
	 */
	final public static int WITHDRAW = 'r';

	/**
	 * <code>TOTAL</code>
	 */
	final public static int TOTAL = 't';
	/**
	 * <code>Drug</code>剂药
	 */
	final public static int Drug = 'D';
	/**
	 * <code>AlTPRICE</code>改价折扣
	 */
	final public static int AlTPRICE = 'a';
	/**
	 * Comment for <code>SINGLEDISC</code>
	 */
	final public static int SINGLEDISC = 'g';
	/**
	 * Comment for <code>TOTALDISC</code>
	 */
	final public static int TOTALDISC = 'l';
	/**
	 * Comment for <code>MONEYDISC</code>
	 */
	final public static int MONEYDISC = 'm';
	/**
	 * <code>AUTODISC</code>自动折扣
	 */
	final public static int AUTODISC = 'c';
	/**
	 * Comment for <code>LOANCARD</code>
	 */
	final public static int LOANCARD = 'd';
	/**
	 * <code>LOANDISC</code>挂帐卡折扣
	 */
	final public static int LOANDISC = 'k';

	/**
	 * <code>df_qty</code>此对象用于对数量作格式化处理.
	 */
	final private static DecimalFormat df_qty = new DecimalFormat("#,###.000");

	/**
	 * <code>YES</code>
	 */
	final public static int YES = 'y';
	/**
	 * <code>NO</code>
	 */
	final public static int NO = 'n';

	/**
	 * <code>goods</code>商品
	 */
	private Goods goods;
	/**
	 * <code>trainflag</code>培训标志：0表示正常销售，1表示培训
	 */
	private int trainflag = 0;

	/**
	 * <code>type</code>类型. Sale对象所表达的业务行为可能是:销售,更正,即更,退货. 
	 * Sale 对象的具体含义就根据type区分.
	 */
	private int type = SALE;

	/**
	 * <code>quickcorrecttype</code>此变量似乎不必要存在.
	 */
	private int quickcorrecttype = NO;

	/**
	 * <code>disc_type</code>折扣类型. 缺省值为NONE,表示没有享受折扣.
	 */
	private int disc_type = Discount.NONE;

	/**
	 * <code>lastfavor</code>用途待明确.
	 */
	private int lastfavor = Discount.NONE;

	/**
	 * <code>favor_name</code>促销方案名称
	 */
	private String favor_name = "";
	/**
	 * <code>qty</code>商品数量
	 */
	private int qty;
	/**
	 * <code>qty_disc</code>享受折扣或促销的商品数量
	 */
	private int qty_disc = 0;

	/**
	 * <code>price_fact</code>实际有效商品售价
	 */
	private long price_fact = 0;

	/**
	 * <code>value_std</code>商品的标准金额(按标价计算)
	 */
	private long value_std = 0;

	/**
	 * <code>value_fact</code>实际有效金额
	 */
	private long value_fact = 0;

	/**
	 * <code>value_disc</code>折扣金额
	 */
	private long value_disc = 0;

	/**
	 * <code>orgcode</code>原始的输入码/扫描码.
	 */
	private String orgcode = "";

	/**
	 * <code>waiter</code>营业员
	 */
	private String waiter = "";

	/**
	 * <code>authorizer</code>授权人员
	 */
	private String authorizer = "";

	/**
	 * <code>placeno</code>柜组号
	 */
	private String placeno = "";

	/**
	 * <code>colorsize</code>色码
	 */
	private String colorsize = "";

	/**
	 * <code>subsheetid</code>收银小票上的子单号
	 */
	private int subsheetid = 1;

	/**
	 * <code>systime</code>操作发生时的系统时间
	 */
	private PosTime systime;

	/**
	 * <code>sysdate</code>操作发生时的系统日期
	 */
	private Day sysdate;

	private long price_std;
}
