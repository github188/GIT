package com.royalstone.pos.common;

import java.io.*;

import org.jdom.Element;

/**
 * @version 1.0 2004.05.12
 * @author  Mengluoyi
 */

/**
 * @author Mengluoyi
 *
 */
public class Goods implements Serializable 
{
	
	/**	构造一个"商品"对象,除了商品名称外其他资料均为未知.
	 * @param GoodsName	商品名称
	 */
	public Goods(String GoodsName)
	{
		vgno = "";
		barcode = "";
		spec = "";
		deptid = "";
		unit = "";
		name = GoodsName;
	}
	
	/**
	 * @param Vgno		商品编码
	 * @param Barcode	商品条码
	 * @param GoodsName	商品名称
	 * @param Deptid	商品小类
	 * @param Spec		规格
	 * @param UnitName	销售单位
	 * @param GoodsPrice	商品售价
	 */
	public Goods(
		String Vgno,
		String Barcode,
		String GoodsName,
		String Deptid,
		String Spec,
		String UnitName,
		int GoodsPrice) 
	{
		vgno = new String(Vgno);
		barcode = new String(Barcode);
		name = new String(GoodsName);
		deptid = new String(Deptid);
		spec = new String(Spec);
		unit = new String(UnitName);
		price = GoodsPrice;
		type = GENERAL;
		x = 1;
	}

	/**
	 * @param Vgno		商品编码
	 * @param Barcode	商品条码
	 * @param GoodsName	商品名称
	 * @param Deptid	商品小类
	 * @param Spec		规格
	 * @param UnitName	销售单位
	 * @param GoodsPrice	商品售价
	 * @param gtype			商品类型
	 * @param x4Price		价格因子
	 * @param ptype			折扣/促销类型
	 */
	public Goods(
		String Vgno,
		String Barcode,
		String GoodsName,
		String Deptid,
		String Spec,
		String UnitName,
		int GoodsPrice,
		int gtype,
		int x4Price,
		String ptype,
		String batchno) 
	{
		vgno = new String(Vgno);
		barcode = new String(Barcode);
		name = new String(GoodsName);
		deptid = new String(Deptid);
		spec = new String(Spec);
		unit = new String(UnitName);
		price = GoodsPrice;
		type = gtype;
		x = x4Price;
		this.ptype = ptype;
		this.batchno = batchno;
	}
	
	public Goods(
			String Vgno,
			String Barcode,
			String GoodsName,
			String Deptid,
			String Spec,
			String UnitName,
			int GoodsPrice,
			int gtype,
			int x4Price,
			String ptype,
			String batchno,
			int cflag,
			String manufact) 
		{
			vgno = new String(Vgno);
			barcode = new String(Barcode);
			name = new String(GoodsName);
			deptid = new String(Deptid);
			spec = new String(Spec);
			unit = new String(UnitName);
			price = GoodsPrice;
			type = gtype;
			x = x4Price;
			this.ptype = ptype;
			this.batchno = batchno;
			this.cflag=cflag;
			this.manufact=manufact;
		}
	
	public Goods(
			String Vgno,
			String Barcode,
			String GoodsName,
			String Deptid,
			String Spec,
			String UnitName,
			int GoodsPrice,
			int gtype,
			int x4Price,
			String ptype,
			String batchno,
			int cflag) 
		{
			vgno = new String(Vgno);
			barcode = new String(Barcode);
			name = new String(GoodsName);
			deptid = new String(Deptid);
			spec = new String(Spec);
			unit = new String(UnitName);
			price = GoodsPrice;
			type = gtype;
			x = x4Price;
			this.ptype = ptype;
			this.batchno = batchno;
			this.cflag=cflag;
		}

	/**	此函数根据一个Goods 对象构造一个新的Goods 对象.
	 * @param g	Goods 对象
	 */
	public Goods(Goods g) 
	{
		vgno = new String(g.vgno);
		barcode = new String(g.barcode);
		name = new String(g.name);
		deptid = new String(g.deptid);
		spec = new String(g.spec);
		unit = new String(g.unit);
		price = g.price;
		type = g.type;
		x = g.x;
	}

	/**	测试扫描码code 与商品是否匹配. code 可以是商品编码(通常为6位), 也可以是商品销售条码(通常为13位).
	 * @param code
	 * @return
	 */
	public boolean matches(String code) 
	{
		return (code.equals(vgno) || code.equals(barcode));
	}

	/**	测试两个Goods 对象是否匹配.
	 * @param goods		Goods 对象
	 * @return			true	匹配;<br/> false	不匹配.
	 */
	public boolean matches(Goods goods) 
	{
		return (vgno.equals(goods.getVgno()));
	}

	/**
	 * @return	商品编码
	 */
	public String getVgno() 
	{
		return new String(vgno);
	}

	/**
	 * @return	商品销售条码
	 */
	public String getBarcode() 
	{
		return new String(barcode);
	}

	/**
	 * @return	商品规格
	 */
	public String getSpec() 
	{
		return new String(spec);
	}

	/**
	 * @return	商品销售单位
	 */
	public String getUnit() {
		return new String(unit);
	}

	/**
	 * @return	商品小类号
	 */
	public String getDeptid() {
		return new String(deptid);
	}
	
	/**
	 * @return	标志
	 */
	public int getCflag() 
	{
		return cflag;
	}
	
	/**
	 * @return	标志
	 */
	public String getmanufact() 
	{
		return manufact;
	}
	
	/**
	 * @param deptid 商品小类号
	 * @return void 
	 * */
	public void setDeptid(String deptid){
		this.deptid = deptid;
	}

	/**
	 * @return	商品名称
	 */
	public String getName() {
		return new String(name);
	}
	
	/**
	 * NOTE: 此方法用途不详. 是否有必要存在?
	 * @param changename
	 */
	public void setName(String changename)
	{
		name = changename;
	}

	/**
	 * @param baseprice		基本码商品的价格
	 */
	public void setPrice(int baseprice){
		price=baseprice;
	}
	
	/*限购*/
	public void setBatchno(String batch)
	{
		batchno=batch;
	}
	
	/**
	 * @return		商品售价(以分为单位)
	 */
	public int getPrice() {
		return price;
	}
	
	/**		此函数需要重新考虑
	 * @param oildisc
	 */
	public void setOilDisc(int oildisc){
		oildisccount = oildisc;
	}
	
	/**		此函数需要重新考虑
	 * @return
	 */
	public int getOilDisc(){
		return oildisccount;
	}

	/**	根据商品数量计算商品的价值.计算过程中,价格因子已经考虑在内.
	 * @param qty	商品数量. 该数量是"量子的".对于称重商品,以克为单位,对于油品,以毫升为单位.
	 * @return		商品的价值, 以分为单位.
	 */
	public int getValue(int qty) {
		return qty * price / x;
	}

	/**
	 * @return	商品价格因子
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * @return	商品价格因子
	 */
	public void setX(int x) {
		this.x=x;
	}

	/**
	 * @return	商品类型
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return	商品折扣/促销类型
	 */
	public String getPType() {
		return ptype;
	}
	
	/**
	 * @return 商品批次号
	 * */
	public String getBatchno(){
		return batchno;
		}

	/**
	 * This method is for debug.
	 */
	public String toString() {
		return "{ " + vgno + ", " + barcode + " } { " + name + ", " + deptid
				+ " } " + " { " + spec + ", " + unit + ", @" + price + "/" + x + " } ";
	}

	/**
	 * @return 生成包含商品信息的XML 节点
	 */
	public Element toElement() {
		Element g = new Element("goods");
		g.addContent(new Element("vgno").addContent(vgno));
		g.addContent(new Element("barcode").addContent(barcode));
		g.addContent(new Element("name").addContent(name));
		g.addContent(new Element("deptid").addContent(deptid));
		g.addContent(new Element("spec").addContent(spec));
		g.addContent(new Element("unit").addContent(unit));
		g.addContent(new Element("price").addContent("" + price));
		g.addContent(new Element("type").addContent("" + type));
		g.addContent(new Element("x").addContent("" + x));
		g.addContent(new Element("ptype").addContent(ptype));
		// zhouzhou add 商品批次号
		g.addContent(new Element("batchno").addContent(batchno));
		g.addContent(new Element("manufact").addContent(manufact));
		return g;
	}

	/**
	 * @param elm	包含商品信息的XML 节点
	 */
	public Goods(Element elm) 
	{
		try {
			vgno = elm.getChildTextTrim("vgno");
			barcode = elm.getChildTextTrim("barcode");
			name = elm.getChildTextTrim("name");
			deptid = elm.getChildTextTrim("deptid");
			spec = elm.getChildTextTrim("spec");
			unit = elm.getChildTextTrim("unit");
			price = Integer.parseInt(elm.getChildTextTrim("price"));
			type = Integer.parseInt(elm.getChildTextTrim("type"));
			x = Integer.parseInt(elm.getChildTextTrim("x"));
			ptype = elm.getChildTextTrim("ptype");
			// zhouzhou add 商品批次号
			batchno = elm.getChildTextTrim("batchno");
			manufact = elm.getChildTextTrim("manufact");
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	/**	检查Goods 对象的各成员是否合法.
	 * @return	true	合法; <br/>false	非法.
	 */
	private boolean isValid() {
		return !(x == 0);
	}

	/**
	 * <code>GENERAL</code>	普通商品
	 */
	final public static int GENERAL = 0;
	/**
	 * <code>WEIGHT</code>	称重商品/地磅
	 */
	final public static int WEIGHT = 1;
	/**
	 * <code>VALUE</code>	金额码商品
	 */
	final public static int VALUE = 2;
	/**
	 * <code>WEIGHT_VALUE</code>	称重金额码商品
	 */
	final public static int WEIGHT_VALUE = 3;
	
	/**
	 * <code>LOADOMETER</code>  地磅
	 * */
	final public static String LOADOMETER = "040201";

	/**
	 * <code>vgno</code>	商品编码
	 */
	private String vgno;
	/**
	 * <code>barcode</code>	商品条码
	 */
	private String barcode;
	/**
	 * <code>name</code>	商品名称
	 */
	private String name;
	/**
	 * <code>deptid</code>	商品小类
	 */
	private String deptid;
	/**
	 * <code>spec</code>	商品规格
	 */
	private String spec;
	/**
	 * <code>unit</code>	销售单位
	 */
	private String unit;
	/**
	 * <code>price</code>	售价(以分为单位)
	 */
	private int price;
	/**
	 * <code>oildisccount</code>	此变量需要重新考虑.
	 */
	private int oildisccount;
	
	/**
	 * <code>type</code>	对应于 price_lst.v_type
	 */
	private int type;
	
	/**
	 * <code>x</code>	商品的价格因子.
	 */
	private int x = 1;
	
	/**
	 * <code>ptype</code>商品的促销/折扣类型.与数据库中 price_lst.p_type 相对应. 
	 * 在计算商品折扣时,需要根据此变量的值决定适用哪个表中的折扣标准.
	 * 缺省值为n, 表示没有折扣或促销.
	 */
	private String ptype = "n";
	
	/**
	 * 商品批次号
	 * */
	private String batchno = "20070101";
	
	/*
	 * 处方药标识
	 * 1 为处方药
	 * 0 为非处方药
	 * */
	
	private int cflag=0;
	
	private String manufact="";
	
}
