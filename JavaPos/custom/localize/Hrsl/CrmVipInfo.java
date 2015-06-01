package custom.localize.Hrsl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class CrmVipInfo
{
	private String app;
	private String track_data;
	private String verify_code;
	private String session_id;
	private String flag;
	
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
			xstream.alias("bfcrm_req", CrmVipInfo.class);
			xstream.useAttributeFor(CrmVipInfo.class, "app");
			xstream.useAttributeFor(CrmVipInfo.class, "session_id");

			String xmlString = xstream.toXML(this);
			xmlString = "<?xml version='1.0' encoding='GBK'?>\n"+xmlString;
			
			int xmlLen = xmlString.length();
			String dataLen = "00000000"+String.valueOf(xmlLen);
			dataLen = dataLen.substring(dataLen.length()-8,dataLen.length());
			xml = head + "0101" + dataLen + xmlString;
			System.out.println(xml);

			return xml;
		} catch (Exception ex)
		{
			return null;
		}
	}

	public CrmVipInfoResult fromXml(String retXml)
	{
		if(retXml == null)
			return null;
		
		retXml = retXml.substring(52);
		System.out.println(retXml);

		try
		{
			XStream stream = new XStream(new DomDriver());

			stream.alias("bfcrm_resp", CrmVipInfoResult.class);
			// 获取属性
			stream.useAttributeFor("success", String.class);
			stream.useAttributeFor("id", String.class);

			CrmVipInfoResult result = (CrmVipInfoResult) stream.fromXML(retXml);

			return result;

		} catch (Exception ex)
		{
			ex.printStackTrace();
			System.out.println(ex);
			return null;
		}
	}

	public class CrmVipInfoResult
	{
		private String success;
		private String fail_type;
		private String message;
		private Member member;

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

		public Member getMember()
		{
			return member;
		}

		public void setMember(Member member)
		{
			this.member = member;
		}
	}

	public class Member
	{
		private String id;				//会员ID
		private String code;			//会员卡号
		private String name;			//会员名称
		private String type_code;		//会员类型
		private String type_name;		//会员类型名称
		private String date_valid;		//会员卡有效期
		private String discount_method;    //折扣方式
		private String lastdate_shopping;  //最后购物时间
		private String save_shopping_detail; //?
		private String permit_discount;		//是否允许商品折扣
		private String permit_voucher;     //是否开通优惠券帐户
		private String permit_valuedcard;  //是否开通储值帐户
		private String cumulate_cent;     //是否开通积分帐户
		private String cent_available;    //会员当前可用积分值
		private String cent_period;      //?
		private String cent_bn;			//?
		private String czkye;			//储值卡余额
		private String yhqye;			//优惠卡余额
		private String hello;			//问候语
		private String permit_th;
		private String phone;
		private String master_code;
		public String getMaster_code()
		{
			return master_code;
		}
		public void setMaster_code(String master_code)
		{
			this.master_code = master_code;
		}
		public String getPhone()
		{
			return phone;
		}
		public void setPhone(String phone)
		{
			this.phone = phone;
		}
		public String getPermit_discount()
		{
			return permit_discount;
		}
		public void setPermit_discount(String permit_discount)
		{
			this.permit_discount = permit_discount;
		}
		public String getPermit_th()
		{
			return permit_th;
		}
		public void setPermit_th(String permit_th)
		{
			this.permit_th = permit_th;
		}
		public String getId()
		{
			return id;
		}
		public void setId(String id)
		{
			this.id = id;
		} 
		public String getCode()
		{
			return code;
		}
		public void setCode(String code)
		{
			this.code = code;
		}
		public String getName()
		{
			return name;
		}
		public void setName(String name)
		{
			this.name = name;
		}
		public String getType_code()
		{
			return type_code;
		}
		public void setType_code(String type_code)
		{
			this.type_code = type_code;
		}
		public String getType_name()
		{
			return type_name;
		}
		public void setType_name(String type_name)
		{
			this.type_name = type_name;
		}
		public String getDate_valid()
		{
			return date_valid;
		}
		public void setDate_valid(String date_valid)
		{
			this.date_valid = date_valid;
		}
		public String getDiscount_method()
		{
			return discount_method;
		}
		public void setDiscount_method(String discount_method)
		{
			this.discount_method = discount_method;
		}
		public String getLastdate_shopping()
		{
			return lastdate_shopping;
		}
		public void setLastdate_shopping(String lastdate_shopping)
		{
			this.lastdate_shopping = lastdate_shopping;
		}
		public String getSave_shopping_detail()
		{
			return save_shopping_detail;
		}
		public void setSave_shopping_detail(String save_shopping_detail)
		{
			this.save_shopping_detail = save_shopping_detail;
		}
		public String getPermit_voucher()
		{
			return permit_voucher;
		}
		public void setPermit_voucher(String permit_voucher)
		{
			this.permit_voucher = permit_voucher;
		}
		public String getPermit_valuedcard()
		{
			return permit_valuedcard;
		}
		public void setPermit_valuedcard(String permit_valuedcard)
		{
			this.permit_valuedcard = permit_valuedcard;
		}
		public String getCumulate_cent()
		{
			return cumulate_cent;
		}
		public void setCumulate_cent(String cumulate_cent)
		{
			this.cumulate_cent = cumulate_cent;
		}
		public String getCent_available()
		{
			return cent_available;
		}
		public void setCent_available(String cent_available)
		{
			this.cent_available = cent_available;
		}
		public String getCent_period()
		{
			return cent_period;
		}
		public void setCent_period(String cent_period)
		{
			this.cent_period = cent_period;
		}
		public String getCent_bn()
		{
			return cent_bn;
		}
		public void setCent_bn(String cent_bn)
		{
			this.cent_bn = cent_bn;
		}
		public String getCzkye()
		{
			return czkye;
		}
		public void setCzkye(String czkye)
		{
			this.czkye = czkye;
		}
		public String getYhqye()
		{
			return yhqye;
		}
		public void setYhqye(String yhqye)
		{
			this.yhqye = yhqye;
		}
		public String getHello()
		{
			return hello;
		}
		public void setHello(String hello)
		{
			this.hello = hello;
		}
	}
}
