package com.royalstone.pos.common;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class PayModeList implements Serializable {


	public PayModeList() {
		super();
		paymode_lst = new Vector<PayMode>();
	}

	public static void main(String[] args) {
		PayModeList pmlist = new PayModeList();
		
		PayMode pm = new PayMode("m", "微信", 1, 0);
		pmlist.add(pm);
		
		pm = new PayMode("z", "支付宝", 1, 0);
		pmlist.add(pm);
		
		pmlist.toXMLFile("paymode.NEW.xml");
	}
	
	public PayMode getPayModeByCode(String code)
	{
		for(PayMode pm : paymode_lst)
			if(pm.getPaycode().equals(code)) return pm;
		return null;
	}
	
	public String getPayName(String code)
	{
		for(PayMode pm : paymode_lst)
			if(pm.getPaycode().equals(code)) return pm.getPayname();
		return code + "付款";
	}
	
	public String getPayCode(int index)
	{
		return ((PayMode)paymode_lst.elementAt(index)).getPaycode();
	}
	
	public boolean payModeISZL(int index)
	{
		return ((PayMode)paymode_lst.elementAt(index)).getPayiszl() == 0 ? false : true;
	}
	
	private Vector<PayMode> paymode_lst = null;

	public Vector<PayMode> getPaymode_lst() {
		return paymode_lst;
	}

	public void add(PayMode op) {
		// TODO Auto-generated method stub
		paymode_lst.add(op);
	}

	public void dump(String file){
		try{
			ObjectOutputStream out = new ObjectOutputStream( new FileOutputStream( file ) );
			for( int i=0; i < paymode_lst.size(); i++ ) out.writeObject( paymode_lst.get(i) );
			out.close();
		}
		catch( Exception e ){
			e.printStackTrace();
		}
	}
	
	/**	把商品价格表打包生成XML元素.
	 * @return	XML元素,其中保存有商品价格表的内容.
	 */
	public Element toElement() {
		Element elm_list = new Element("paymodelist");
		for (int i = 0; i < paymode_lst.size(); i++)
			elm_list.addContent(((PayMode) paymode_lst.get(i)).toElement());
		
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
			list = root.getChildren("paymode");

			for (int i = 0; i < list.size(); i++)
				this.add(new PayMode((Element) list.get(i)));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void fromXML(String file) {
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
}
