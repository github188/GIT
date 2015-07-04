/*
 * 创建日期 2004-5-28
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package com.royalstone.pos.util;

import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * @author liangxinbiao
 *
 * 更改所生成类型注释的模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
public class XMLObjectHelper {

	public String str1 = "haha";
	private int n;
	private double d = 445;
	private Date date = new Date();
	private Integer adfd = new Integer(12);

	public void Object2XML(Object source, String target) {
		try {
			FileWriter fw = new FileWriter(target);
			Class clazz = source.getClass();
			Field[] fields = clazz.getDeclaredFields();
			Document doc = new Document();
			Element root = new Element("Class");
			root.setAttribute("name", source.getClass().getName());
			doc.setRootElement(root);

			for (int i = 0; i < fields.length; i++) {
				Element fieldElement = new Element("Field");
				List attrList = new ArrayList();
				Attribute type =
					new Attribute("type", fields[i].getType().getName());
				Attribute name = new Attribute("name", fields[i].getName());

				attrList.add(name);
				attrList.add(type);

				fieldElement.setAttributes(attrList);
				fieldElement.setText(fields[i].get(source).toString());
				doc.getRootElement().addContent(fieldElement);
			}

			String indent = "    ";
			boolean newLines = true;

			XMLOutputter outp = new XMLOutputter(indent, newLines, "GBK");
			outp.output(doc, fw);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public Object XML2Object(String fileName) {
		try {
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(fileName);
			List fields = doc.getRootElement().getChildren("Field");
			Iterator iter = fields.iterator();
			while (iter.hasNext()) {
				Element element = (Element) iter.next();
				System.out.println(
					((Attribute) element.getAttribute("name")).getValue());
				System.out.println(element.getText());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public static void main(String[] args) {
		XMLObjectHelper xh = new XMLObjectHelper();
		//xh.Object2XML(xh, "abcd.xml");
		xh.XML2Object("abcd.xml");
	}
}
