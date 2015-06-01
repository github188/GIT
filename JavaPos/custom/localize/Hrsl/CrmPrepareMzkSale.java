package custom.localize.Hrsl;

import java.util.ArrayList;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class CrmPrepareMzkSale
{
	private String app;
	private String session_id;
	private String bill_id;
	private String cashier;
	private String date_account;
	private ArrayList card_list;

	public void setApp(String app)
	{
		this.app = app;
	}

	public void setSession_id(String session_id)
	{
		this.session_id = session_id;
	}

	public void setBill_id(String bill_id)
	{
		this.bill_id = bill_id;
	}

	public void setCashier(String cashier)
	{
		this.cashier = cashier;
	}

	public void setDate_account(String date_account)
	{
		this.date_account = date_account;
	}

	public void setCard_list(ArrayList card_list)
	{
		this.card_list = card_list;
	}

	public String toXml(String head)
	{
		String xml = null;
		try
		{
			XStream xstream = new XStream(new XppDriver(
					new XmlFriendlyReplacer("__", "_")));
			xstream.alias("bfcrm_req", CrmPrepareMzkSale.class);
			xstream.alias("card", CrmMzkCard.class);
		 	xstream.useAttributeFor(CrmPrepareMzkSale.class, "app");
			xstream.useAttributeFor(CrmPrepareMzkSale.class, "session_id");
			xstream.useAttributeFor(CrmMzkCard.class, "member_id");

			String xmlString = xstream.toXML(this);
			int xmlLen = xmlString.length();
			String dataLen = "00000000"+String.valueOf(xmlLen);
			dataLen = dataLen.substring(dataLen.length()-8,dataLen.length());
			xml = head +"0106"+ dataLen + xmlString;
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
		
		retXml = retXml.substring(52);
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
