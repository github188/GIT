package custom.localize.Ybsj;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class CrmUserLogin
{
	private String app;
	private String store;
	private String operator;
	private String machine;
	private String bfcrm_user;
	private String password;
	private String system;

	public String getApp()
	{
		return app;
	}

	public void setApp(String app)
	{
		this.app = app;
	}

	public String getStore()
	{
		return store;
	}

	public void setStore(String store)
	{
		this.store = store;
	}

	public String getOperator()
	{
		return operator;
	}

	public void setOperator(String operator)
	{
		this.operator = operator;
	}

	public String getMachine()
	{
		return machine;
	}

	public void setMachine(String machine)
	{
		this.machine = machine;
	}

	public String getBfcrm_user()
	{
		return bfcrm_user;
	}

	public void setBfcrm_user(String bfcrm_user)
	{
		this.bfcrm_user = bfcrm_user;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getSystem()
	{
		return system;
	}

	public void setSystem(String system)
	{
		this.system = system;
	}

	public String PadLeft(String str, int length, char padchar)
	{
		int count = length - str.length();

		for (int i = 0; i < count; i++)
		{
			str = padchar + str;
		}

		return str;
	}

	public String toXml(String head)
	{
		String xml = null;
		try
		{
			XStream xstream = new XStream(new XppDriver(new XmlFriendlyReplacer("__", "_")));
			xstream.alias("bfcrm_req", CrmUserLogin.class);
			xstream.useAttributeFor(CrmUserLogin.class, "app");

			String xmlString = xstream.toXML(this);
			int xmlLen = xmlString.length();
			String dataLen = "00000000" + String.valueOf(xmlLen);
			dataLen = dataLen.substring(dataLen.length() - 8, dataLen.length());
			xml = head + dataLen + xmlString + "12345678";

			System.out.println(xml);

			return xml;
		}
		catch (Exception ex)
		{
			return null;
		}
	}

	public CrmUserLoginResult fromXml(String retXml)
	{
		if (retXml == null)
			return null;

		retXml = retXml.substring(18, retXml.length() - 8);
		System.out.println(retXml);

		try
		{
			XStream stream = new XStream(new DomDriver());

			stream.alias("bfcrm_resp", CrmUserLoginResult.class);
			// 获取属性
			stream.useAttributeFor("success", String.class);

			CrmUserLoginResult result = (CrmUserLoginResult) stream.fromXML(retXml);

			return result;

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public class CrmUserLoginResult
	{
		private String success;
		private String session_id;
		private String exist_grant_voucher_promotion;
		private String fail_type;
		private String message;

		public String getSuccess()
		{
			return success;
		}

		public void setSuccess(String success)
		{
			this.success = success;
		}

		public String getSession_id()
		{
			return session_id;
		}

		public void setSession_id(String session_id)
		{
			this.session_id = session_id;
		}

		public String getExist_grant_voucher_promotion()
		{
			return exist_grant_voucher_promotion;
		}

		public void setExist_grant_voucher_promotion(String exist_grant_voucher_promotion)
		{
			this.exist_grant_voucher_promotion = exist_grant_voucher_promotion;
		}

		public String getFail_type()
		{
			return fail_type;
		}

		public void setFail_type(String fail_type)
		{
			this.fail_type = fail_type;
		}

		public String getMessage()
		{
			return message;
		}

		public void setMessage(String message)
		{
			this.message = message;
		}
	}
}
