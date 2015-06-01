package custom.localize.Hfhf;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

public class Hfhf_Customer
{
	private AbacusCustomer customer;

	public String getQueryRequestXml(String cardno)
	{
		try
		{
			Document document = DocumentHelper.createDocument();
			document.setXMLEncoding("GBK");

			Element inputParameterElement = document.addElement("InputParameter");
			inputParameterElement.addElement("CardNo").setText(cardno);
			inputParameterElement.addElement("StoreNo").setText(GlobalInfo.sysPara.mktcode);
			inputParameterElement.addElement("PosNo").setText(GlobalInfo.syjStatus.syjh);

			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			StringWriter content = new StringWriter();

			XMLWriter xmlWriter = new XMLWriter(content, format);
			xmlWriter.write(document);
			xmlWriter.close();

			return content.toString();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}

	}

	public boolean parseQuery(String xml)
	{
		try
		{
			customer = new AbacusCustomer();

			Document retDoc = DocumentHelper.parseText(xml);
			Element root = retDoc.getRootElement();
			Iterator iter = root.elementIterator();

			while (iter.hasNext())
			{
				Element e = (Element) iter.next();
				if (e.getName().equals("CardNo"))
					customer.CardNo = e.getTextTrim();
				if (e.getName().equals("Status"))
					customer.Status = e.getTextTrim();
				if (e.getName().equals("Address"))
					customer.Address = e.getTextTrim();
				if (e.getName().equals("Email"))
					customer.Email = e.getTextTrim();
				if (e.getName().equals("ExpiryDate"))
					customer.ExpiryDate = e.getTextTrim();
				if (e.getName().equals("Idnumber"))
					customer.Idnumber = e.getTextTrim();
				if (e.getName().equals("IssueDate"))
					customer.IssueDate = e.getTextTrim();
				if (e.getName().equals("MemberClsId"))
					customer.MemberClsId = e.getTextTrim();
				if (e.getName().equals("MemberClsName"))
					customer.MemberClsName = e.getTextTrim();
				if (e.getName().equals("Mtel"))
					customer.Mtel = e.getTextTrim();
				if (e.getName().equals("Name"))
					customer.Name = e.getTextTrim();
				if (e.getName().equals("Birthday"))
					customer.Birthday = e.getTextTrim();
				if (e.getName().equals("PostalCode"))
					customer.PostalCode = e.getTextTrim();
				if (e.getName().equals("Sex"))
					customer.Sex = e.getTextTrim();
				if (e.getName().equals("Tel"))
					customer.Tel = e.getTextTrim();
				if (e.getName().equals("Points"))
					customer.Points = Convert.toDouble(e.getTextTrim());
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public String getSavePointXml(SaleHeadDef salehead, Vector salegoods)
	{
		try
		{
			Document document = DocumentHelper.createDocument();
			document.setXMLEncoding("GBK");

			Element inputParameterElement = document.addElement("InputParameter");

			inputParameterElement.addElement("CardNo").setText(salehead.hykh.indexOf("szd") > -1 ? salehead.hykh.replace("szd", "") : salehead.hykh);
			inputParameterElement.addElement("MemberRankId").setText(salehead.hytype == null ? "" : salehead.hytype);
			inputParameterElement.addElement("SaleDate").setText(salehead.rqsj.replace("/", "-"));
			inputParameterElement.addElement("Remarks").setText("销售积分");
			inputParameterElement.addElement("Points").setText(String.valueOf(salehead.bcjf));
			inputParameterElement.addElement("Expense").setText(String.valueOf(SellType.ISBACK(salehead.djlb) ? -1 * salehead.ysje : salehead.ysje));
			inputParameterElement.addElement("StoreNo").setText(salehead.mkt);
			inputParameterElement.addElement("PosNo").setText(GlobalInfo.syjStatus.syjh);
			inputParameterElement.addElement("Employee").setText(GlobalInfo.syjStatus.syyh);
			inputParameterElement.addElement("BillType").setText("Invoice");
			inputParameterElement.addElement("SrcId").setText(String.valueOf(salehead.fphm));

			Element detailsElement = inputParameterElement.addElement("Details");

			for (int i = 0; i < salegoods.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) salegoods.get(i);
				if (sgd == null)
					continue;

				Element itemElement = detailsElement.addElement("Detail");
				itemElement.addElement("SaleDeptId").setText(sgd.gz);
				itemElement.addElement("ProductId").setText(sgd.code);
				itemElement.addElement("CategoryId").setText(sgd.catid);
				itemElement.addElement("Expense").setText(ManipulatePrecision.doubleToString(SellType.ISBACK(salehead.djlb) ? -1 * (sgd.hjje - sgd.hjzk) : (sgd.hjje - sgd.hjzk), 2, 1));
				itemElement.addElement("Quantity").setText(String.valueOf(sgd.sl));
				itemElement.addElement("Point").setText("");
				itemElement.addElement("Remarks").setText(sgd.name);
				itemElement.addElement("PointsRule").setText("");
			}

			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			StringWriter content = new StringWriter();

			XMLWriter xmlWriter = new XMLWriter(content, format);
			xmlWriter.write(document);
			xmlWriter.close();

			return content.toString();
			// return new String(document.asXML().getBytes(), "UTF-8");

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public AbacusCustomer getAbacusCustomer()
	{
		return this.customer;
	}

	public void setCustomerInvalid()
	{
		this.customer = null;
	}

	class AbacusCustomer
	{
		public String CardNo;
		public String Status;
		public String Address;
		public String Email;
		public String ExpiryDate;
		public String Idnumber;
		public String IssueDate;
		public String MemberClsId;
		public String MemberClsName;
		public String Mtel;
		public String Name;
		public String Birthday;
		public String PostalCode;
		public String Sex;
		public String Tel;
		public double Points;

	}

}
