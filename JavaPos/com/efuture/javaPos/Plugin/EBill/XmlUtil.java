package com.efuture.javaPos.Plugin.EBill;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;

public class XmlUtil
{
	DocumentBuilderFactory dbf = null;
	DocumentBuilder db = null;
	Document doc = null;

	public XmlUtil()
	{

	}

	public XmlUtil(String arg)
	{
		try
		{
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();

			doc = db.parse(new InputSource(new StringReader(arg)));
		}
		catch (ParserConfigurationException e)
		{
			PosLog.getLog(getClass()).error(e);
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			PosLog.getLog(getClass()).error(e);
			e.printStackTrace();
		}
		catch (IOException e)
		{
			PosLog.getLog(getClass()).error(e);
			e.printStackTrace();
		}
	}

	public Vector fromXml(String root, String node, int index, String[] arg)
	{
		Vector v = new Vector();
		NodeList nList = doc.getElementsByTagName(root);
		String[] row = null;

/*		if ((Element) nList.item((index < 0 ? nList.getLength() - 1 : index)) == null)
			return null;*/

		NodeList nll = ((Element) nList.item((index < 0 ? nList.getLength() - 1 : index))).getElementsByTagName(node);

		for (int j = 0; j < nll.getLength(); j++)
		{
			row = new String[arg.length];

			for (int i = 0; i < arg.length; i++)
			{
				NodeList t = ((Element) nll.item(j)).getElementsByTagName(arg[i]);

				if ((t == null) || (t.item(0) == null))
				{
					row[i] = "";
				}
				else
				{
					Text t1 = (Text) t.item(0).getFirstChild();

					if (t1 != null)
					{
						row[i] = t1.getNodeValue();
					}
					else
					{
						row[i] = "";
					}
				}
			}

			v.add(row);
		}

		return v;

	}

	public String toXml(String root, String[] values, String[] arg)
	{
		if (values.length != arg.length) { return null; }

		StringBuffer sbXML = new StringBuffer();
		sbXML.append("<" + root + ">");

		for (int i = 0; i < arg.length; i++)
		{
			try
			{
				sbXML.append("<" + arg[i] + ">");
				if (values[i] == null)
					sbXML.append("");
				else
					sbXML.append(values[i]);
				sbXML.append("</" + arg[i] + ">");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				PosLog.getLog("Tansition").error(e);
				return null;
			}
		}
		sbXML.append("</" + root + ">");
		return sbXML.toString();
	}

	public static void main(String[] args)
	{
		BufferedReader br = CommonMethod.readFile("c:\\test.txt");

		String line = null;
		String content = "";
		try
		{
			while ((line = br.readLine()) != null)
			{
				content += line;
			}
			br.close();
			br = null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		XmlUtil xml = new XmlUtil(content);
		String[] arg = { "BILLNO", "STATUS", "MKT", "SYJID", "INVNO", "DJLB", "DATE", "CHECKER", "CUSTNO", "CUSTMOBILE", "OUGHTPAY", "FACTPAY", "GCHANGE", "POPZK" };

		Vector v = xml.fromXml("NewDataSet", "SaleInvoices", 0, arg);

		for (int i = 0; i < v.size(); i++)
		{
			String[] row = (String[]) v.elementAt(i);

			for (int j = 0; j < row.length; j++)
			{
				if (row[j].length() > 0)
					System.out.print(row[j] + "      ");
				else
					System.out.print("null         ");
			}

			System.out.println("");
		}
	}

}
