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
	
	/**	����һ��"��Ʒ"����,������Ʒ�������������Ͼ�Ϊδ֪.
	 * @param GoodsName	��Ʒ����
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
	 * @param Vgno		��Ʒ����
	 * @param Barcode	��Ʒ����
	 * @param GoodsName	��Ʒ����
	 * @param Deptid	��ƷС��
	 * @param Spec		���
	 * @param UnitName	���۵�λ
	 * @param GoodsPrice	��Ʒ�ۼ�
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
	 * @param Vgno		��Ʒ����
	 * @param Barcode	��Ʒ����
	 * @param GoodsName	��Ʒ����
	 * @param Deptid	��ƷС��
	 * @param Spec		���
	 * @param UnitName	���۵�λ
	 * @param GoodsPrice	��Ʒ�ۼ�
	 * @param gtype			��Ʒ����
	 * @param x4Price		�۸�����
	 * @param ptype			�ۿ�/��������
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

	/**	�˺�������һ��Goods ������һ���µ�Goods ����.
	 * @param g	Goods ����
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

	/**	����ɨ����code ����Ʒ�Ƿ�ƥ��. code ��������Ʒ����(ͨ��Ϊ6λ), Ҳ��������Ʒ��������(ͨ��Ϊ13λ).
	 * @param code
	 * @return
	 */
	public boolean matches(String code) 
	{
		return (code.equals(vgno) || code.equals(barcode));
	}

	/**	��������Goods �����Ƿ�ƥ��.
	 * @param goods		Goods ����
	 * @return			true	ƥ��;<br/> false	��ƥ��.
	 */
	public boolean matches(Goods goods) 
	{
		return (vgno.equals(goods.getVgno()));
	}

	/**
	 * @return	��Ʒ����
	 */
	public String getVgno() 
	{
		return new String(vgno);
	}

	/**
	 * @return	��Ʒ��������
	 */
	public String getBarcode() 
	{
		return new String(barcode);
	}

	/**
	 * @return	��Ʒ���
	 */
	public String getSpec() 
	{
		return new String(spec);
	}

	/**
	 * @return	��Ʒ���۵�λ
	 */
	public String getUnit() {
		return new String(unit);
	}

	/**
	 * @return	��ƷС���
	 */
	public String getDeptid() {
		return new String(deptid);
	}
	
	/**
	 * @return	��־
	 */
	public int getCflag() 
	{
		return cflag;
	}
	
	/**
	 * @return	��־
	 */
	public String getmanufact() 
	{
		return manufact;
	}
	
	/**
	 * @param deptid ��ƷС���
	 * @return void 
	 * */
	public void setDeptid(String deptid){
		this.deptid = deptid;
	}

	/**
	 * @return	��Ʒ����
	 */
	public String getName() {
		return new String(name);
	}
	
	/**
	 * NOTE: �˷�����;����. �Ƿ��б�Ҫ����?
	 * @param changename
	 */
	public void setName(String changename)
	{
		name = changename;
	}

	/**
	 * @param baseprice		��������Ʒ�ļ۸�
	 */
	public void setPrice(int baseprice){
		price=baseprice;
	}
	
	/*�޹�*/
	public void setBatchno(String batch)
	{
		batchno=batch;
	}
	
	/**
	 * @return		��Ʒ�ۼ�(�Է�Ϊ��λ)
	 */
	public int getPrice() {
		return price;
	}
	
	/**		�˺�����Ҫ���¿���
	 * @param oildisc
	 */
	public void setOilDisc(int oildisc){
		oildisccount = oildisc;
	}
	
	/**		�˺�����Ҫ���¿���
	 * @return
	 */
	public int getOilDisc(){
		return oildisccount;
	}

	/**	������Ʒ����������Ʒ�ļ�ֵ.���������,�۸������Ѿ���������.
	 * @param qty	��Ʒ����. ��������"���ӵ�".���ڳ�����Ʒ,�Կ�Ϊ��λ,������Ʒ,�Ժ���Ϊ��λ.
	 * @return		��Ʒ�ļ�ֵ, �Է�Ϊ��λ.
	 */
	public int getValue(int qty) {
		return qty * price / x;
	}

	/**
	 * @return	��Ʒ�۸�����
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * @return	��Ʒ�۸�����
	 */
	public void setX(int x) {
		this.x=x;
	}

	/**
	 * @return	��Ʒ����
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return	��Ʒ�ۿ�/��������
	 */
	public String getPType() {
		return ptype;
	}
	
	/**
	 * @return ��Ʒ���κ�
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
	 * @return ���ɰ�����Ʒ��Ϣ��XML �ڵ�
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
		// zhouzhou add ��Ʒ���κ�
		g.addContent(new Element("batchno").addContent(batchno));
		g.addContent(new Element("manufact").addContent(manufact));
		return g;
	}

	/**
	 * @param elm	������Ʒ��Ϣ��XML �ڵ�
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
			// zhouzhou add ��Ʒ���κ�
			batchno = elm.getChildTextTrim("batchno");
			manufact = elm.getChildTextTrim("manufact");
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	/**	���Goods ����ĸ���Ա�Ƿ�Ϸ�.
	 * @return	true	�Ϸ�; <br/>false	�Ƿ�.
	 */
	private boolean isValid() {
		return !(x == 0);
	}

	/**
	 * <code>GENERAL</code>	��ͨ��Ʒ
	 */
	final public static int GENERAL = 0;
	/**
	 * <code>WEIGHT</code>	������Ʒ/�ذ�
	 */
	final public static int WEIGHT = 1;
	/**
	 * <code>VALUE</code>	�������Ʒ
	 */
	final public static int VALUE = 2;
	/**
	 * <code>WEIGHT_VALUE</code>	���ؽ������Ʒ
	 */
	final public static int WEIGHT_VALUE = 3;
	
	/**
	 * <code>LOADOMETER</code>  �ذ�
	 * */
	final public static String LOADOMETER = "040201";

	/**
	 * <code>vgno</code>	��Ʒ����
	 */
	private String vgno;
	/**
	 * <code>barcode</code>	��Ʒ����
	 */
	private String barcode;
	/**
	 * <code>name</code>	��Ʒ����
	 */
	private String name;
	/**
	 * <code>deptid</code>	��ƷС��
	 */
	private String deptid;
	/**
	 * <code>spec</code>	��Ʒ���
	 */
	private String spec;
	/**
	 * <code>unit</code>	���۵�λ
	 */
	private String unit;
	/**
	 * <code>price</code>	�ۼ�(�Է�Ϊ��λ)
	 */
	private int price;
	/**
	 * <code>oildisccount</code>	�˱�����Ҫ���¿���.
	 */
	private int oildisccount;
	
	/**
	 * <code>type</code>	��Ӧ�� price_lst.v_type
	 */
	private int type;
	
	/**
	 * <code>x</code>	��Ʒ�ļ۸�����.
	 */
	private int x = 1;
	
	/**
	 * <code>ptype</code>��Ʒ�Ĵ���/�ۿ�����.�����ݿ��� price_lst.p_type ���Ӧ. 
	 * �ڼ�����Ʒ�ۿ�ʱ,��Ҫ���ݴ˱�����ֵ���������ĸ����е��ۿ۱�׼.
	 * ȱʡֵΪn, ��ʾû���ۿۻ����.
	 */
	private String ptype = "n";
	
	/**
	 * ��Ʒ���κ�
	 * */
	private String batchno = "20070101";
	
	/*
	 * ����ҩ��ʶ
	 * 1 Ϊ����ҩ
	 * 0 Ϊ�Ǵ���ҩ
	 * */
	
	private int cflag=0;
	
	private String manufact="";
	
}
