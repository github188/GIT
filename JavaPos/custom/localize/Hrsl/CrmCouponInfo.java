package custom.localize.Hrsl;

import java.util.ArrayList;

import com.efuture.DeBugTools.PosLog;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class CrmCouponInfo
{
	private String app;
	private String track_data;
	private String require_valid_date;
	private String session_id;
	private String flag;
	private String member_password;
	
	public String getMember_password()
    {
    	return member_password;
    }

	public void setMember_password(String member_password)
    {
    	this.member_password = member_password;
    }

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

	public String getRequire_valid_date()
	{
		return require_valid_date;
	}

	public void setRequire_valid_date(String require_valid_date)
	{
		this.require_valid_date = require_valid_date;
	}

	public String getSession_id()
	{
		return session_id;
	}

	public void setSession_id(String session_id)
	{
		this.session_id = session_id;
	}

	
	public String getFlag()
    {
    	return flag;
    }

	public void setFlag(String flag)
    {
    	this.flag = flag;
    }

	public String toXml(String head)
	{
		String xml = null;
		try
		{
			XStream xstream = new XStream(new XppDriver(
					new XmlFriendlyReplacer("__", "_")));
			xstream.alias("bfcrm_req", CrmCouponInfo.class);
			xstream.useAttributeFor(CrmCouponInfo.class, "app");
			xstream.useAttributeFor(CrmCouponInfo.class, "session_id");

			String xmlString = xstream.toXML(this);
			xmlString = "<?xml version='1.0' encoding='GBK'?>\n"+xmlString;
			
			int xmlLen = xmlString.length();
			String dataLen = "00000000"+String.valueOf(xmlLen);
			dataLen = dataLen.substring(dataLen.length()-8,dataLen.length());
			xml = head + "0107" + dataLen + xmlString;
			System.out.println(xml);
			return xml;
		} catch (Exception ex)
		{
			return null;
		}
	}

	public CrmCouponInfoResult fromXml(String retXml)
	{
		if(retXml == null)
			return null;
		//retXml = "BFCRMXML00000353<?xml version='1.0' encoding='GBK'?><bfcrm_resp success='Y'><member_id>100000129</member_id><member_code>88800058</member_code><voucher_list><voucher date_valid='2013-08-12' id='0'><name>积分返券</name><balance>71.00</balance></voucher><voucher date_valid='2013-08-31' id='1'><name>积分返券</name><balance>1000.00</balance></voucher></voucher_list></bfcrm_resp>";
		retXml = retXml.replace("date_valid", "date");
		retXml = retXml.substring(52);
		System.out.println(retXml);

		try
		{
			XStream stream = new XStream(new DomDriver());
			stream.alias("bfcrm_resp", CrmCouponInfoResult.class);

			stream.alias("voucher", voucher.class);
			stream.aliasField("voucher_list",CrmCouponInfoResult.class, "voucher_list");
			// 获取属性
			stream.useAttributeFor("success", String.class);
			stream.useAttributeFor("date", String.class);
			
			stream.useAttributeFor("id", String.class);
			
			CrmCouponInfoResult result = (CrmCouponInfoResult) stream.fromXML(retXml);

			return result;

		} catch (Exception ex)
		{
			ex.printStackTrace();
			System.out.println(ex);
			return null;
		}
	}

	public class CrmCouponInfoResult
	{
		private String success;
		private String member_id;
		private String member_code;
		private ArrayList voucher_list;
		private String message;
		private String fail_type;
		
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

		public String getSuccess()
		{
			return success;
		}

		public void setSuccess(String success)
		{
			this.success = success;
		}

		public ArrayList getVoucher_list()
		{
			return voucher_list;
		}

		public void setVoucher_list(ArrayList voucher_list)
		{
			this.voucher_list = voucher_list;
		}

		public String getMember_code()
		{
			return member_code;
		}

		public void setMember_code(String member_code)
		{
			this.member_code = member_code;
		}

		public String getMember_id()
		{
			return member_id;
		}

		public void setMember_id(String member_id)
		{
			this.member_id = member_id;
		}


	}
	public class voucher
	{
		private String id;				//优惠券ID
		private String name;			//优惠券名称
		private String date_valid;		//优惠券有效期
		private String balance;         //优惠券余额
		private String date; 
		
		public String getBalance()
		{
			return balance;
		}
		public void setBalance(String balance)
		{
			this.balance = balance;
		}
		public String getId()
		{
			return id;
		}
		public void setId(String id)
		{
			this.id = id;
		} 
		
		public String getName()
		{
			return name;
		}
		public void setName(String name)
		{
			this.name = name;
		}
		public String getDate_valid()
		{
			return date_valid;
		}
		public void setDate_valid(String date_valid)
		{
			this.date_valid = date_valid;
		}
		
		public String getDate()
		{
			return date;
		}
		public void setDate(String date)
		{
			this.date = date;
		}
		
	}
	
	public static void main(String args[]) { 
		CrmCouponInfo cc = new CrmCouponInfo();
		cc.fromXml("");
	} 
}
