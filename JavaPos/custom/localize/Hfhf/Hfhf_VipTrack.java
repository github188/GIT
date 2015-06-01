package custom.localize.Hfhf;

import java.io.StringWriter;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class Hfhf_VipTrack
{
	private String CardFaceNo;

	public String getRequestXml(String track)
	{
		try
		{
			Document document = DocumentHelper.createDocument();
			document.setXMLEncoding("GBK");

			Element inputParameterElement = document.addElement("InputParameter");
			inputParameterElement.addElement("CardMedNo").setText(track);

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

	public boolean parse(String xml)
	{
		try
		{
			Document retDoc = DocumentHelper.parseText(xml);
			Element root = retDoc.getRootElement();
			Iterator iter = root.elementIterator();

			while (iter.hasNext())
			{
				Element e = (Element) iter.next();
				if (e.getName().equals("CardFaceNo"))
					CardFaceNo = e.getTextTrim();
			}

			if (CardFaceNo != null && !CardFaceNo.equals(""))
				return true;

			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public String getCardFaceNo()
	{
		return this.CardFaceNo;
	}
}
