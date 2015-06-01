package custom.localize.Hrsl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class CrmVipPoint
{
	private String app;
	private String session_id;
	private String member_id;
	private String point;
	private String sale;
	private String operator;
	private String billid;
	private String date_account;
	private String time_shopping;
	private String system;

	public void setApp(String app)
	{
		this.app = app;
	}

	public void setSession_id(String session_id)
	{
		this.session_id = session_id;
	}

	public void setMember_id(String member_id)
	{
		this.member_id = member_id;
	}

	public void setPoint(String point)
	{
		this.point = point;
	}

	public void setSale(String sale)
	{
		this.sale = sale;
	}

	public void setOperator(String operator)
	{
		this.operator = operator;
	}

	public void setBillid(String billid)
	{
		this.billid = billid;
	}

	public void setDate_account(String date_account)
	{
		this.date_account = date_account;
	}

	public void setTime_shopping(String time_shopping)
	{
		this.time_shopping = time_shopping;
	}

	public void setSystem(String system)
	{
		this.system = system;
	}

	public String toXml(String head)
	{
		String xml = null;
		try
		{
			XStream xstream = new XStream(new XppDriver(
					new XmlFriendlyReplacer("__", "_")));
			xstream.alias("bfcrm_req", CrmVipPoint.class);
			xstream.useAttributeFor(CrmVipPoint.class, "app");
			xstream.useAttributeFor(CrmVipPoint.class, "session_id");

			String xmlString = xstream.toXML(this);
			xmlString = "<?xml version='1.0' encoding='GBK'?>\n"+xmlString;
			
			int xmlLen = xmlString.length();
			String dataLen = "00000000" + String.valueOf(xmlLen);
			dataLen = dataLen.substring(dataLen.length() - 8, dataLen.length());
			xml = head +"0141"+ dataLen + xmlString;
			System.out.println(xml);

			return xml;
		} catch (Exception ex)
		{
			return null;
		}
	}
	
	public String toXmlSale(String head)
	{
		String xml = null;
		try
		{
			XStream xstream = new XStream(new XppDriver(
					new XmlFriendlyReplacer("__", "_")));
			xstream.alias("bfcrm_req", CrmVipPoint.class);
			xstream.useAttributeFor(CrmVipPoint.class, "app");
			xstream.useAttributeFor(CrmVipPoint.class, "session_id");

			String xmlString = xstream.toXML(this);
			xmlString = "<?xml version='1.0' encoding='GBK'?>\n"+xmlString;
			
			int xmlLen = xmlString.length();
			String dataLen = "00000000" + String.valueOf(xmlLen);
			dataLen = dataLen.substring(dataLen.length() - 8, dataLen.length());
			xml = head +"0142"+ dataLen + xmlString;
			System.out.println(xml);

			return xml;
		} catch (Exception ex)
		{
			return null;
		}
	}

	public CrmVipPointResult fromXml(String retXml)
	{
		if (retXml == null)
			return null;

		retXml = retXml.substring(52);
		System.out.println(retXml);

		try
		{
			XStream stream = new XStream(new DomDriver());

			stream.alias("bfcrm_resp", CrmVipPointResult.class);
			// 获取属性
			stream.useAttributeFor("success", String.class);

			CrmVipPointResult result = (CrmVipPointResult) stream
					.fromXML(retXml);

			return result;

		} catch (Exception ex)
		{
			ex.printStackTrace();
			System.out.println(ex);
			return null;
		}
	}
	
	public CrmVipPointSaleResult fromXmlSale(String retXml)
	{
		if (retXml == null)
			return null;

		retXml = retXml.substring(52);
		System.out.println(retXml);

		try
		{
			XStream stream = new XStream(new DomDriver());

			stream.alias("bfcrm_resp", CrmVipPointSaleResult.class);
			// 获取属性
			stream.useAttributeFor("success", String.class);

			CrmVipPointSaleResult result = (CrmVipPointSaleResult) stream.fromXML(retXml);

			return result;

		} catch (Exception ex)
		{
			ex.printStackTrace();
			System.out.println(ex);
			return null;
		}
	}

	public class CrmVipPointResult
	{
		private String success;
		private String fail_type;
		private String message;

		public String getSuccess()
		{
			return success;
		}

		public String getFail_type()
		{
			return fail_type;
		}

		public String getMessage()
		{
			return message;
		}
	}
	
	public class CrmVipPointSaleResult
	{
		private String success;
		private String fail_type;
		private String message;
		private String billid;//积分返券的流水号
		private String coupon;//本次积分返券金额

		public String getBillid()
		{
			return billid;
		}

		public void setBillid(String billid)
		{
			this.billid = billid;
		}

		public String getCoupon()
		{
			return coupon;
		}

		public void setCoupon(String coupon)
		{
			this.coupon = coupon;
		}

		public void setFail_type(String fail_type)
		{
			this.fail_type = fail_type;
		}

		public void setMessage(String message)
		{
			this.message = message;
		}

		public void setSuccess(String success)
		{
			this.success = success;
		}

		public String getSuccess()
		{
			return success;
		}

		public String getFail_type()
		{
			return fail_type;
		}

		public String getMessage()
		{
			return message;
		}
	}
}
