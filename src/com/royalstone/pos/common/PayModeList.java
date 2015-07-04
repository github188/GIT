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
		
		PayMode pm = new PayMode("m", "΢��", 1, 0);
		pmlist.add(pm);
		
		pm = new PayMode("z", "֧����", 1, 0);
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
		return code + "����";
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
	
	/**	����Ʒ�۸��������XMLԪ��.
	 * @return	XMLԪ��,���б�������Ʒ�۸�������.
	 */
	public Element toElement() {
		Element elm_list = new Element("paymodelist");
		for (int i = 0; i < paymode_lst.size(); i++)
			elm_list.addContent(((PayMode) paymode_lst.get(i)).toElement());
		
		//this.maxVgno=((Goods) goodslist.get(goodslist.size()-1)).getVgno();
		
		return elm_list;
	}

	/**	����Ʒ�۸���XML�ĵ�Ԫ���н����۸�������.
	 * @param root	XML�ĵ��е���Ʒ�۸��Ԫ��.
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
			// TODO �˴�Ӧ�����⴦��.
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
