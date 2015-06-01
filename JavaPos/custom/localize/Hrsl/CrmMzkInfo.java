package custom.localize.Hrsl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class CrmMzkInfo
{
	private String app;
	private String session_id;
	private String track_data;
	private String verify_code;
	private String member_password;

	public String getApp()
	{
		return app;
	}

	public void setApp(String app)
	{
		this.app = app;
	}

	public String getTrack_data()
	{
		return track_data;
	}

	public void setTrack_data(String track_data)
	{
		this.track_data = track_data;
	}

	public String getVerify_code()
	{
		return verify_code;
	}

	public void setVerify_code(String verify_code)
	{
		this.verify_code = verify_code;
	}

	public String getSession_id()
	{
		return session_id;
	}

	public void setSession_id(String session_id)
	{
		this.session_id = session_id;
	}

	public String getMember_password()
	{
		return member_password;
	}

	public void setMember_password(String member_password)
	{
		this.member_password = member_password;
	}

	public String toXml(String head)
	{
		String xml = null;
		try
		{
			XStream xstream = new XStream(new XppDriver(
					new XmlFriendlyReplacer("__", "_")));
			xstream.alias("bfcrm_req", CrmMzkInfo.class);
			xstream.useAttributeFor(CrmMzkInfo.class, "app");
			xstream.useAttributeFor(CrmMzkInfo.class, "session_id");

			String xmlString = xstream.toXML(this);
			int xmlLen = xmlString.length();
			String dataLen = "00000000"+String.valueOf(xmlLen);
			dataLen = dataLen.substring(dataLen.length()-8,dataLen.length());
			xml = head + "0105" + dataLen + xmlString ;

			System.out.println(xml);

			return xml;
		} catch (Exception ex)
		{
			return null;
		}
	}

	public CrmMzkInfoResult fromXml(String retXml)
	{
		if(retXml == null)
			return null;
		
		retXml = retXml.substring(52);
		System.out.println(retXml);

		try
		{
			XStream stream = new XStream(new DomDriver());

			stream.alias("bfcrm_resp", CrmMzkInfoResult.class);
			// 获取属性
			stream.useAttributeFor("success", String.class);
		
			CrmMzkInfoResult result = (CrmMzkInfoResult) stream.fromXML(retXml);

			return result;

		} catch (Exception ex)
		{
			ex.printStackTrace();
			System.out.println(ex);
			return null;
		}
	}

	public class CrmMzkInfoResult
	{
		private String success;
		private String fail_type;
		private String message;
		
		private String member_id;
		private String member_code;
		private String bottom;
		private String balance;
		private String date_valid;
		private String hyktype;
		private String permit_th;
		
		public String getHyktype()
		{
			return hyktype;
		}

		public void setHyktype(String hyktype)
		{
			this.hyktype = hyktype;
		}

		public String getPermit_th()
		{
			return permit_th;
		}

		public void setPermit_th(String permit_th)
		{
			this.permit_th = permit_th;
		}

		public String getSuccess()
		{
			return success;
		}

		public void setSuccess(String success)
		{
			this.success = success;
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

		public String getMember_id()
		{
			return member_id;
		}

		public void setMember_id(String member_id)
		{
			this.member_id = member_id;
		}

		public String getMember_code()
		{
			return member_code;
		}

		public void setMember_code(String member_code)
		{
			this.member_code = member_code;
		}

		public String getBottom()
		{
			return bottom;
		}

		public void setBottom(String bottom)
		{
			this.bottom = bottom;
		}

		public String getBalance()
		{
			return balance;
		}

		public void setBalance(String balance)
		{
			this.balance = balance;
		}

		public String getDate_valid()
		{
			return date_valid;
		}

		public void setDate_valid(String date_valid)
		{
			this.date_valid = date_valid;
		}
	}
}
