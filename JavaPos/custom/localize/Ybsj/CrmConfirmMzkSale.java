package custom.localize.Ybsj;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class CrmConfirmMzkSale
{
	private String app;
	private String session_id;
	private String step;
	private String server_transaction_id;
	private String total_amount; 

	public String getApp()
	{
		return app;
	}

	public void setApp(String app)
	{
		this.app = app;
	}

	public String getSession_id()
	{
		return session_id;
	}

	public void setSession_id(String session_id)
	{
		this.session_id = session_id;
	}

	public String getStep()
	{
		return step;
	}

	public void setStep(String step)
	{
		this.step = step;
	}

	public String getServer_transaction_id()
	{
		return server_transaction_id;
	}

	public void setServer_transaction_id(String server_transaction_id)
	{
		this.server_transaction_id = server_transaction_id;
	}

	public String getTotal_amount()
	{
		return total_amount;
	}

	public void setTotal_amount(String total_amount)
	{
		this.total_amount = total_amount;
	}

	public String toXml(String head)
	{
		String xml = null;
		try
		{
			XStream xstream = new XStream(new XppDriver(
					new XmlFriendlyReplacer("__", "_")));
			xstream.alias("bfcrm_req", CrmConfirmMzkSale.class);
			xstream.useAttributeFor(CrmConfirmMzkSale.class, "app");
			xstream.useAttributeFor(CrmConfirmMzkSale.class, "session_id");

			String xmlString = xstream.toXML(this);
			int xmlLen = xmlString.length();
			String dataLen = "00000000"+String.valueOf(xmlLen);
			dataLen = dataLen.substring(dataLen.length()-8,dataLen.length());
			xml = head + dataLen + xmlString + "12345678";
			System.out.println(xml);

			return xml;
		} catch (Exception ex)
		{
			return null;
		}
	}

	public CrmMzkSaleResult fromXml(String retXml)
	{
		if(retXml == null)
			return null;
		
		retXml = retXml.substring(18, retXml.length() - 8);
		System.out.println(retXml);

		try
		{
			XStream stream = new XStream(new DomDriver());

			stream.alias("bfcrm_resp", CrmMzkSaleResult.class);
			// 获取属性
			stream.useAttributeFor("success", String.class);

			CrmMzkSaleResult result = (CrmMzkSaleResult) stream.fromXML(retXml);

			return result;

		} catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
}
