package com.royalstone.pos.common;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.royalstone.pos.invoke.realtime.RealTime;
import com.royalstone.pos.invoke.realtime.RealTimeException;
import com.royalstone.pos.notify.UnitOfWork;
import com.royalstone.pos.util.PosConfig;

/**	GoodsList 为商品价格表. 该表中存放有商品的编码,条码,价格,商品名称,规格,单位.
 * 可以根据商品的编码或条码查询商品资料与价格.
 * 早期版本用序列化方法实现商品价格表下载,现已改用XML文档. 
 * 原来的 load/unload 方法已删除. 
 * @version 1.0 2004.05.12
 * @author  Mengluoyi
 */

public class GoodsCutList {

	/**
	 * 构造一个空的商品价格表.
	 */
	public GoodsCutList() {
		map_vgno = new HashMap();
		map_barcode = new HashMap();
		goodslist = new Vector();
	}

	/**	向商品价格表中添加商品价格对象.
	 * @param g
	 */
	public void add(Goods g) {
		map_vgno.put(g.getVgno(), g);
		map_barcode.put(g.getBarcode(), g);
		goodslist.add(g);
	}

	/**	把商品价格表打包生成XML元素.
	 * @return	XML元素,其中保存有商品价格表的内容.
	 */
	public Element toElement() {
		Element elm_list = new Element("goodscutlist");
		for (int i = 0; i < goodslist.size(); i++)
			elm_list.addContent(((Goods) goodslist.get(i)).toElement());
		
		//this.maxVgno=((Goods) goodslist.get(goodslist.size()-1)).getVgno();
		
		return elm_list;
	}

	/**	从商品价格表的XML文档元素中解析价格表的内容.
	 * @param root	XML文档中的商品价格表元素.
	 */
	public void fromElement(Element root) {
		List list;
		try {
			System.out.println("node name: " + root.getName());
			list = root.getChildren("goods");

			for (int i = 0; i < list.size(); i++)
				this.add(new Goods((Element) list.get(i)));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update(String goodsNo, Goods g) {

		Goods goods = (Goods) map_vgno.get(goodsNo);
		if (goods == null) { //new
			if (g != null)
				add(g);
		} else {
			if (g == null) { //delete
				remove(goods);
			} else { //update
				remove(goods);
				add(g);
			}
		}

		UnitOfWork.getInstance().updateGoods(true);

	}

	public void remove(Goods g) {
		goodslist.remove(g);
		map_vgno.remove(g.getVgno());
		map_barcode.remove(g.getBarcode());
	}

	/**	从名为file 的文件中解析商品价格表的内容.
	 * @param file	存放XML商品价格表的文件.
	 */
	public void fromXMLFile(String file) {
		try {
			Document doc = (new SAXBuilder()).build(file);
			fromElement(doc.getRootElement());
		} catch (JDOMException e) {
			// TODO 此处应作特殊处理.
			e.printStackTrace();
		}
	}

	public void toXMLFile(String file) {
		try {
			XMLOutputter outputter = new XMLOutputter("  ", true, "GB2312");
			outputter.setTextTrim(true);
			FileOutputStream out = new FileOutputStream(file);
			outputter.output(new Document(toElement()), out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param  code 商品销售码. 可以是条码(通常为13位),也可以是商品编码(6位). <br/>
	 * @return a Goods obj which matches the code param, 
	 * null if no matches found.
	 */
	public Goods findCut(String code) throws RealTimeException {
		//TODO  沧州富达 by fire  2005_5_11 		
        Goods g=null;
         PosConfig config=PosConfig.getInstance();
        String isRealTime=config.getString("ISREALTIME");
		   String ifSupportOffLine=PosConfig.getInstance().getString("IFSUPPORTOFFLINE");

        if (PosContext.getInstance().isOnLine()&&"ON".equals(isRealTime)||"OFF".equals(ifSupportOffLine)) {
                g=RealTime.getInstance().findGoodsCut(code);
		    if (g != null)
				 return g;
        }else{

     	   g = (Goods) map_barcode.get(code);
		  if (g != null)
				return g;
        }

      return g;
	}



	/**
	 * @return	商品价格表内的单品数.
	 */
	public int size() {
		return map_vgno.size();
	}

	/**
	 * <code>map_vgno</code>
	 * <br/>map_vgno 内保存有vgno 与商品资料的对应关系; map_barcode 内保存有barcode 与商品资料的对应关系.
	 * 根据map_vgno/map_barcode, 可以分别实现按vgno/barcode查询商品价格的功能.
	 */
	private HashMap map_vgno, map_barcode;

	/**
	 * <code>goodslist</code>	保存商品资料对象,用于顺序地输出商品资料( 主要供 toElement 方法使用 ).
	 */
	private Vector goodslist;
	
    private String maxVgno="0";
    public String getMaxVgno(){
        return this.maxVgno;
    }

}
