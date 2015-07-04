/*
 * 创建日期 2004-6-25
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package com.royalstone.pos.common;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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

/**
 * @author liangxinbiao
 *
 * 更改所生成类型注释的模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
public class GoodsExtList {

	private Vector vGoodsExtlist;

	private HashMap map_barcode;

	public GoodsExtList() {
		map_barcode = new HashMap();
		vGoodsExtlist = new Vector();
	}

	public Element toElement() {
		Element elm_list = new Element("goodsExtList");
		for (int i = 0; i < vGoodsExtlist.size(); i++)
			elm_list.addContent(((GoodsExt) vGoodsExtlist.get(i)).toElement());

        elm_list.addContent(new Element("MaxCode").addContent(((Goods) vGoodsExtlist.get(vGoodsExtlist.size()-1)).getVgno()));
		return elm_list;
	}

	public void fromElement(Element root) {
		List list;
		try {
			list = root.getChildren("goods");
			for (int i = 0; i < list.size(); i++)
				this.add(new GoodsExt((Element) list.get(i)));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void fromXMLFile(String file) {

		try {
			Document doc = (new SAXBuilder()).build(file);
			fromElement(doc.getRootElement());
		} catch (JDOMException e) {
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

	public void update(String goodsNo, ArrayList goodsExtList) {

		ArrayList oldGoodsExtList = new ArrayList();

		for (int i = 0; i < vGoodsExtlist.size(); i++) {
			if(((GoodsExt)vGoodsExtlist.get(i)).getVgno().equals(goodsNo)){
				oldGoodsExtList.add(vGoodsExtlist.get(i));
			}
		}

		if (oldGoodsExtList.size() == 0) { //new
			if (goodsExtList != null) {
				for (int j = 0; j < goodsExtList.size(); j++) {
					add((GoodsExt) goodsExtList.get(j));
				}
			}
		} else { //delete||update
			for (int i = 0; i < oldGoodsExtList.size(); i++) {
				remove((GoodsExt) oldGoodsExtList.get(i));
			}
			if (goodsExtList != null) {
				for (int j = 0; j < goodsExtList.size(); j++) {
					add((GoodsExt) goodsExtList.get(j));
				}
			}
		}

		if (!(oldGoodsExtList.size() == 0
			&& (goodsExtList == null || goodsExtList.size() == 0))) {
			UnitOfWork.getInstance().updateGoodsExt(true);
		}

	}

	public void remove(GoodsExt g) {
		map_barcode.remove(g.getBarcode());
		vGoodsExtlist.remove(g);
	}

	public GoodsExt find(String code) throws RealTimeException {
		//TODO
	    GoodsExt g;
         PosConfig config=PosConfig.getInstance();
        String isRealTime=config.getString("ISREALTIME");
         String ifSupportOffLine=PosConfig.getInstance().getString("IFSUPPORTOFFLINE");

        if (PosContext.getInstance().isOnLine()&&"ON".equals(isRealTime)||"OFF".equals(ifSupportOffLine)) {
			g = RealTime.getInstance().findGoodsExt(code);
		} else {
			g = (GoodsExt) map_barcode.get(code);
		}

		return g;
	}

	public void add(GoodsExt g) {
		map_barcode.put(g.getBarcode(), g);
		vGoodsExtlist.add(g);
	}

}
