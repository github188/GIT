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
	 * @param g		��Ʒ
	 * @param q		����
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

	//��Թ��ʿ��Ż�
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
	 * @return	Sale ��������ʹ���(�۳�,����,����,�˻�).
	 */
	public int getType() {
		return type;
	}

	/**	�˺�����������Sale �����XML�ڵ�, ����ֵ����ΪString.
	 * @return	Sale ��������ʹ���(�۳�,����,����,�˻�).
	 */
	public String getTypeCode() {
		return "" + (char) type;
	}

	/**	ȡSale������ۿ����ʹ���.
	 * @return	��Ʒ�ۿ�������
	 */
	public int getDiscType() {
		return disc_type;
	}

	/**	�˺�����������Sale �����XML�ڵ�, ����ֵ����ΪString.
	 * @return	��Ʒ�ۿ�������
	 */
	public String getDiscCode() {
		return "" + (char) disc_type;
	}

	/**	��Sale ��¼��ȡ����Ʒ����.
	 * @return	Sale �����е���Ʒ����.
	 */
	public Goods getGoods() {
		return goods;
	}

	/**	����ȷ.
	 * @param g
	 * @return
	 */
	public long getGoodsPrice(Goods g) {
		long goodsprice = (value_std * g.getX()) / qty;
		return goodsprice;
	}

	/**
	 * @return	��Ʒ����
	 */
	public int getQty() {
		return qty;
	}
	
	public void setQty(int Qty){
		this.qty =  Qty;
		}

	/**
	 * @return	�����ۿ۵���Ʒ����
	 */
	public int getQtyDisc() {
		return qty_disc;
	}

	/**	POS��Ҫ֧�ֳ�����Ʒ�ͷǳ�����Ʒ. ������Ʒ��������Ҫ��ʾС�������λ,�ǳ�����Ʒ��������Ӧ��������ʽ��ʾ.
	 * �˺�����Ϊ���ɱ�����ʾ�������ִ������.
	 * @return	������ʾ�������ִ�.
	 */
	public String getQtyStr() {
		int x = goods.getX();
		return (x == 1)
			? Integer.toString(qty)
			: df_qty.format((double) qty / x);
	}

	/**
	 * @return	��Ʒ����
	 */
	public String getVgno() {
		return goods.getVgno();
	}

	/**
	 * @return	��Ʒ����
	 */
	public String getBarcode() {
		return goods.getBarcode();
	}

	/**
	 * @return	��Ʒԭʼ��ɨ����
	 */
	public String getOrgCode() {
		return orgcode;
	}

	/**
	 * @return	��Ʒɫ��. Ŀǰ,��Ʒ���͵�����ɫ���ʾ.
	 */
	public String getColorSize() {
		return colorsize;
	}

	/**
	 * @return	ӪҵԱ
	 */
	public String getWaiter() {
		return waiter;
	}

	/**
	 * @return	��Ȩ��Ա
	 */
	public String getAuthorizer() {
		return authorizer;
	}

	/**
	 * @return	�����
	 */
	public String getPlaceno() {
		return placeno;
	}

	/**
	 * @return	��Ʒ���
	 */
	public String getSpec() {
		return goods.getSpec();
	}

	/**
	 * @return	��Ʒ���۵�λ
	 */
	public String getUnit() {
		return goods.getUnit();
	}

	/**
	 * @return	��Ʒ�����
	 */
	public String getDeptid() {
		return goods.getDeptid();
	}

	/**
	 * @return	��Ʒ����
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
	 * @return	��Ʒ��׼��
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
	 * @return	��Ʒ��ʵ����Ч���.
	 */
	public long getFactValue() {
		return value_fact;
	}

	/**
	 * @param value	��Ʒ��ʵ����Ч���.
	 */
	public void setFactValue(long value) {
		value_fact = value;
	}

	/**
	 * @param value	��Ʒ�ı�׼���(����ۼ���Ľ��).
	 */
	public void setStdValue(long value) {
		value_std = value;
	}

	/**
	 * @param value	��Ʒ��ʵ����Ч���.
	 */
	public void setValue(int value) {
		value_fact = value;
	}

	/**
	 * @return	��Ʒ�ı�׼���(����ۼ���Ľ��).
	 */
	public long getStdValue() {
		return value_std;
	}

	/**
	 * @return	��Ʒ���ۿ۽��.
	 */
	public long getDiscValue() {
		return value_disc;
	}

	/**
	 * @param value	��Ʒ���ۿ۽��.
	 */
	public void setDiscValue(long value) {
		value_disc = value;
	}
	
	/**
	 * @return	��Ʒ�����κ�.
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

	/**	����Sale �����ɫ��
	 * @param colorsize	ɫ��
	 */
	public void setColorSize(String colorsize) {
		this.colorsize = colorsize;
	}

	/**	����Sale ����Ĺ����
	 * @param placeno	�����
	 */
	public void setPlaceno(String placeno) {
		this.placeno = placeno;
	}

	/**
	 * @param waiterid	ӪҵԱ
	 */
	public void setWaiter(String waiterid) {
		this.waiter = waiterid;
	}

	/**
	 * @param authorizer	��Ȩ��Ա
	 */
	public void setAuthorizer(String authorizer) {
		this.authorizer = authorizer;
	}

	/**	����Sale�����ԭʼ������.
	 * @param code	ԭʼ������/ɨ����.
	 */
	public void setOriginalCode(String code) {
		this.orgcode = code;
	}

	/**	POS������һ��СƱ��֧�����Բ�ͬ����Ķ���"�ӵ�". 
	 * Ϊ�����ֲ�ͬ�������Ʒ,��Sale�������� subsheetid �ֶ�.
	 * �˺�����������Sale �����subsheetid.
	 * @param id	POSСƱ���ӵ���.
	 */
	public void setSubSheetid(int id) {
		this.subsheetid = id;
	}

	/**	�˺���������ϴ������ۿ۷�̯����. 
	 * ��ϴ����ļ�������:���Ȱ�POS�����������ڵ���Ʒ����Ʒ������,����ϴ���������������ܵ��ۿ�,
	 * �ٰ����п������ۿ۷�̯���������Ʒ. ������������ˮ��,�ۿ�Ҫ��¼�ھ������Ʒ����.
	 * �˺����� SaleList.consumeFavor ����.
	 * @param qty_favor		���ܴ����۵���Ʒ������
	 * @param value_favor	���ܴ����۵���Ʒ�Ľ��
	 * @param favor_name	��ϴ�����������
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
		 * ��һ��Sale �ڵ���Ʒ,�п��ܲ��ִ���,���ֲ�����.
		 * ��Ʒ�ܽ�� = ���۲��ֽ�� + �����۲��ֽ��
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
	 * �����Ʒ����ϴ����ۿ�(����ͨ�ۿ���Ӱ��).
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
	 * ���ô���ȷ.
	 */
	public void setLastFavor() {
		lastfavor = Discount.COMPLEX;
	}

	/**	���ô���ȷ.
	 * @return
	 */
	public int getlastfavor() {
		return lastfavor;
	}

	/**
	 * ��������ۿ�.
	 */
	public void clearDiscount() {
		disc_type = Discount.NONE;
		price_fact = goods.getPrice();
		value_fact = value_std;
		value_disc = 0;
		qty_disc = 0;
	}

	/**
	 * ��Sale ������Ϊ"��ѵ��¼".
	 */
	public void setAsTraining() {
		trainflag = 1;
	}

	/**
	 * ��Sale ��¼����Ϊ"��ɾ��".
	 */
	public void setAsDeleted() {
		trainflag = 2;
	}

	/**	����Sale ����ı�ʶ��־
	 * @param flag	��ѵ��־
	 */
	public void setTrainFlag(int flag) {
		trainflag = flag;
	}

	/**	�˺���������XML �ĵ�ʱʹ��.
	 * @return	��ѵ��־.
	 */
	public int getTrainFlag() {
		return trainflag;
	}

	/**	���ô���ȷ.
	 * 
	 */
	public void setquickcorrect() {
		this.quickcorrecttype = YES;
	}

	/**	���ô���ȷ.
	 * @return
	 */
	public int getquickcorrect() {
		return this.quickcorrecttype;
	}

	/**	������Ʒ��������Sale�����е���Ʒ�Ƿ�ƥ��.
	 * @param code	��Ʒ������
	 * @return	true		ƥ��<br/>
	 * false				��ƥ��
	 */
	public boolean matches(String code) {
		return this.goods.matches(code);
	}

	/**	����Sale �����е���Ʒ�����봫��Ĳ��������Ƿ�ƥ��.
	 * @param g	Goods ����
	 * @return	true		ƥ��<br/>
	 * false				��ƥ��
	 */
	public boolean matches(Goods g) {
		return this.goods.matches(g);
	}

	/**	�˺����������д���ȷ.
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
	 * @return	������������
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
	promtype       	0	��ͨ           
	promtype       	1	�ؼ�           
	promtype       	2	����           
	v_type         	0	��ͨ           
	v_type         	2	����           
	v_type         	3	���           
	v_type         	8	����           
	v_type         	9	������         
	*/

	/**
	 * <code>SALE</code>����
	 */
	final public static int SALE = 's';
	/**
	 * <code>CORRECT</code>����
	 */
	final public static int CORRECT = 'v';
	/**
	 * <code>QUICKCORRECT</code>����
	 */
	final public static int QUICKCORRECT = 'q';
	/**
	 * <code>WITHDRAW</code>�˻�
	 */
	final public static int WITHDRAW = 'r';

	/**
	 * <code>TOTAL</code>
	 */
	final public static int TOTAL = 't';
	/**
	 * <code>Drug</code>��ҩ
	 */
	final public static int Drug = 'D';
	/**
	 * <code>AlTPRICE</code>�ļ��ۿ�
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
	 * <code>AUTODISC</code>�Զ��ۿ�
	 */
	final public static int AUTODISC = 'c';
	/**
	 * Comment for <code>LOANCARD</code>
	 */
	final public static int LOANCARD = 'd';
	/**
	 * <code>LOANDISC</code>���ʿ��ۿ�
	 */
	final public static int LOANDISC = 'k';

	/**
	 * <code>df_qty</code>�˶������ڶ���������ʽ������.
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
	 * <code>goods</code>��Ʒ
	 */
	private Goods goods;
	/**
	 * <code>trainflag</code>��ѵ��־��0��ʾ�������ۣ�1��ʾ��ѵ
	 */
	private int trainflag = 0;

	/**
	 * <code>type</code>����. Sale����������ҵ����Ϊ������:����,����,����,�˻�. 
	 * Sale ����ľ��庬��͸���type����.
	 */
	private int type = SALE;

	/**
	 * <code>quickcorrecttype</code>�˱����ƺ�����Ҫ����.
	 */
	private int quickcorrecttype = NO;

	/**
	 * <code>disc_type</code>�ۿ�����. ȱʡֵΪNONE,��ʾû�������ۿ�.
	 */
	private int disc_type = Discount.NONE;

	/**
	 * <code>lastfavor</code>��;����ȷ.
	 */
	private int lastfavor = Discount.NONE;

	/**
	 * <code>favor_name</code>������������
	 */
	private String favor_name = "";
	/**
	 * <code>qty</code>��Ʒ����
	 */
	private int qty;
	/**
	 * <code>qty_disc</code>�����ۿۻ��������Ʒ����
	 */
	private int qty_disc = 0;

	/**
	 * <code>price_fact</code>ʵ����Ч��Ʒ�ۼ�
	 */
	private long price_fact = 0;

	/**
	 * <code>value_std</code>��Ʒ�ı�׼���(����ۼ���)
	 */
	private long value_std = 0;

	/**
	 * <code>value_fact</code>ʵ����Ч���
	 */
	private long value_fact = 0;

	/**
	 * <code>value_disc</code>�ۿ۽��
	 */
	private long value_disc = 0;

	/**
	 * <code>orgcode</code>ԭʼ��������/ɨ����.
	 */
	private String orgcode = "";

	/**
	 * <code>waiter</code>ӪҵԱ
	 */
	private String waiter = "";

	/**
	 * <code>authorizer</code>��Ȩ��Ա
	 */
	private String authorizer = "";

	/**
	 * <code>placeno</code>�����
	 */
	private String placeno = "";

	/**
	 * <code>colorsize</code>ɫ��
	 */
	private String colorsize = "";

	/**
	 * <code>subsheetid</code>����СƱ�ϵ��ӵ���
	 */
	private int subsheetid = 1;

	/**
	 * <code>systime</code>��������ʱ��ϵͳʱ��
	 */
	private PosTime systime;

	/**
	 * <code>sysdate</code>��������ʱ��ϵͳ����
	 */
	private Day sysdate;

	private long price_std;
}
