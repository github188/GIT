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

public class YYYList implements Serializable {

	public YYYList() {
		// TODO Auto-generated constructor stub
		yyy_lst = new Vector<YYY>();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	private Vector<YYY> yyy_lst = null;

	public Vector<YYY> getYyy_lst() {
		return yyy_lst;
	}

	public void setYyy_lst(Vector<YYY> yyy_lst) {
		this.yyy_lst = yyy_lst;
	}

	public void add(YYY op) {
		// TODO Auto-generated method stub
		yyy_lst.add(op);
	}

	public void dump(String file){
		try{
			ObjectOutputStream out = new ObjectOutputStream( new FileOutputStream( file ) );
			for( int i=0; i < yyy_lst.size(); i++ ) out.writeObject( yyy_lst.get(i) );
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
		Element elm_list = new Element("yyylist");
		for (int i = 0; i < yyy_lst.size(); i++)
			elm_list.addContent(((YYY) yyy_lst.get(i)).toElement());
				
		return elm_list;
	}

	/**	从商品价格表的XML文档元素中解析价格表的内容.
	 * @param root	XML文档中的商品价格表元素.
	 */
	public void fromElement(Element root) {
		List list;
		try {
			System.out.println("node name: " + root.getName());
			
			list = root.getChildren("yyy");

			for (int i = 0; i < list.size(); i++)
				this.add(new YYY((Element) list.get(i)));
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
	
	public boolean verifyYYY(String id)
	{
		for(YYY yyy :yyy_lst)
			if(yyy.getiD().equals(id)) return true;
		
		return false;
	}
	
}
