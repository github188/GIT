package custom.localize.Hfhf;

import java.io.StringWriter;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.javaPos.Global.GlobalInfo;

public class Hfhf_CoinPurse
{
	private VipCoinPurse vipCoin;
	private VipCoinPayResult vipCoinPayRet;

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
			inputParameterElement.addElement("Employee").setText(GlobalInfo.syjStatus.syyh);

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

	public String getPayOrRechangeRequestXml(String djlb, String cardno, double money)
	{
		try
		{
			Document document = DocumentHelper.createDocument();
			document.setXMLEncoding("GBK");

			Element inputParameterElement = document.addElement("InputParameter");
			inputParameterElement.addElement("Amount").setText(String.valueOf(money));
			inputParameterElement.addElement("BillType").setText("invoice");//SellType.getDefault().typeExchange(djlb, 'N', null));
			inputParameterElement.addElement("CardNo").setText(cardno);
			inputParameterElement.addElement("Employee").setText(GlobalInfo.syjStatus.syyh);
			inputParameterElement.addElement("SaleDate").setText(ManipulateDateTime.getCurrentDateTimeBySign());
			inputParameterElement.addElement("StoreNo").setText(GlobalInfo.sysPara.mktcode);
			inputParameterElement.addElement("Remarks ").setText("");
			inputParameterElement.addElement("SrcId ").setText(String.valueOf(GlobalInfo.syjStatus.fphm));
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
	
	public String getConfirmPayRequestXml(String referno)
	{
		try
		{
			Document document = DocumentHelper.createDocument();
			document.setXMLEncoding("GBK");

			Element inputParameterElement = document.addElement("InputParameter");
			inputParameterElement.addElement("ReferNo").setText(referno);

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
	
	public String cancelPayRequestXml(String referno)
	{
		try
		{
			Document document = DocumentHelper.createDocument();
			document.setXMLEncoding("GBK");

			Element inputParameterElement = document.addElement("InputParameter");
			inputParameterElement.addElement("ReferNo").setText(referno);
			
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
			Document retDoc = DocumentHelper.parseText(xml);
			Element root = retDoc.getRootElement();
			System.out.println(root.getName());

			Iterator iter = root.elementIterator();
			vipCoin = new VipCoinPurse();

			while (iter.hasNext())
			{
				Element item = (Element) iter.next();

				if (item.getName().equals("Name"))
					vipCoin.Name = item.getTextTrim();
				if (item.getName().equals("Balance"))
					vipCoin.Balance = Convert.toDouble(item.getTextTrim());
				if (item.getName().equals("OpenDate "))
					vipCoin.OpenDate = item.getTextTrim();
				if (item.getName().equals("UpdateDate "))
					vipCoin.UpdateDate = item.getTextTrim();
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean parsePayOrRechange(String xml)
	{
		try
		{
			Document retDoc = DocumentHelper.parseText(xml);
			Element root = retDoc.getRootElement();
			System.out.println(root.getName());

			Iterator iter = root.elementIterator();
			vipCoinPayRet = new VipCoinPayResult();

			while (iter.hasNext())
			{
				Element item = (Element) iter.next();

				if (item.getName().equals("Balance"))
					vipCoinPayRet.Balance = Convert.toDouble(item.getTextTrim());
				if (item.getName().equals("ReferNo"))
					vipCoinPayRet.ReferNo = item.getTextTrim();
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}

	}

	
	public VipCoinPurse getVipCoin()
	{
		return vipCoin;
	}

	public VipCoinPayResult getVipCoinPayRet()
	{
		return vipCoinPayRet;
	}


	class VipCoinPurse
	{
		public String Name;
		public double Balance;
		public String OpenDate;
		public String UpdateDate;
	}

	public class VipCoinPayResult
	{
		public double Balance;
		public String ReferNo;
	}
}
