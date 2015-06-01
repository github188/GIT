package custom.localize.Ybsj;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class CrmUserLogOff
{
	private String app;
	private String session_id;

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

	public String toXml(String head)
	{
		String xml = null;
		try
		{
			XStream xstream = new XStream(new XppDriver(new XmlFriendlyReplacer("__", "_")));
			xstream.alias("bfcrm_req", CrmUserLogOff.class);
			xstream.useAttributeFor(CrmUserLogOff.class, "app");
			xstream.useAttributeFor(CrmUserLogOff.class,"session_id");

			String xmlString = xstream.toXML(this);
			int xmlLen = xmlString.length();
			String dataLen = "00000000"+String.valueOf(xmlLen);
			dataLen = dataLen.substring(dataLen.length()-8,dataLen.length());
			xml = head + dataLen + xmlString + "12345678";
			System.out.println(xml);

			return xml;
		}
		catch (Exception ex)
		{
			return null;
		}
	}
}
