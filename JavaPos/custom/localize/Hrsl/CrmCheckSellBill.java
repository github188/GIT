package custom.localize.Hrsl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class CrmCheckSellBill
{
	private String app;
	private String session_id;
	private String cashier;
	private String bill_id_begin;
	private String bill_id_end;
	private String detail;
	
	public void setApp(String app)
	{
		this.app = app;
	}
	public void setSession_id(String session_id)
	{
		this.session_id = session_id;
	}
	public void setCashier(String cashier)
	{
		this.cashier = cashier;
	}
	public void setBill_id_begin(String bill_id_begin)
	{
		this.bill_id_begin = bill_id_begin;
	}
	public void setBill_id_end(String bill_id_end)
	{
		this.bill_id_end = bill_id_end;
	}
	public void setDetail(String detail)
	{
		this.detail = detail;
	}
	
	public String toXml(String head)
	{
		String xml = null;
		try
		{
			XStream xstream = new XStream(new XppDriver(
					new XmlFriendlyReplacer("__", "_")));
			xstream.alias("bfcrm_req", CrmCheckSellBill.class);
			xstream.useAttributeFor(CrmCheckSellBill.class, "app");
			xstream.useAttributeFor(CrmCheckSellBill.class, "session_id");

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
	
	public class CrmChkSellBillResult
	{
		private String success;
		
	}
	
}
