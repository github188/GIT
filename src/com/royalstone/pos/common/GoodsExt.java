/*
 * 创建日期 2004-6-25
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package com.royalstone.pos.common;

import org.jdom.Element;

/**
 * @author liangxinbiao
 *
 * 更改所生成类型注释的模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
public class GoodsExt extends Goods {

	private int pknum;

	public GoodsExt(
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
		int pknum,
		String batchno) {
		super(
			Vgno,
			Barcode,
			GoodsName,
			Deptid,
			Spec,
			UnitName,
			GoodsPrice,
			gtype,
			x4Price,
			ptype,
			batchno);
		this.pknum = pknum;
	}

	public GoodsExt(Element elm) {
		super(elm);
		try {
			pknum = Integer.parseInt(elm.getChildTextTrim("pknum"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public Element toElement() {
		Element e = super.toElement();
		e.addContent(new Element("pknum").addContent("" + pknum));
		return e;
	}

	public int getPknum() {
		return pknum;
	}

	public void setPknum(int i) {
		pknum = i;
	}

}
