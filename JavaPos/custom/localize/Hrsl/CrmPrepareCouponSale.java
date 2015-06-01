package custom.localize.Hrsl;

import java.util.ArrayList;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class CrmPrepareCouponSale
{
	private String app;
	private String session_id;
	private String bill_id;
	private String type;
	private String cashier;
	private String store_code;
	private String pos_id;
	private String date_account;
	private ArrayList card_list;
	public String getApp()
	{
		return app;
	}
	public void setApp(String app)
	{
		this.app = app;
	}
	public String getBill_id()
	{
		return bill_id;
	}
	public void setBill_id(String bill_id)
	{
		this.bill_id = bill_id;
	}
	public ArrayList getCard_list()
	{
		return card_list;
	}
	public void setCard_list(ArrayList card_list)
	{
		this.card_list = card_list;
	}
	public String getCashier()
	{
		return cashier;
	}
	public void setCashier(String cashier)
	{
		this.cashier = cashier;
	}
	public String getDate_account()
	{
		return date_account;
	}
	public void setDate_account(String date_account)
	{
		this.date_account = date_account;
	}
	public String getSession_id()
	{
		return session_id;
	}
	public void setSession_id(String session_id)
	{
		this.session_id = session_id;
	}
	public String getPos_id()
	{
		return pos_id;
	}
	public void setPos_id(String pos_id)
	{
		this.pos_id = pos_id;
	}
	public String getStore_code()
	{
		return store_code;
	}
	public void setStore_code(String store_code)
	{
		this.store_code = store_code;
	}
	public String getType()
	{
		return type;
	}
	public void setType(String type)
	{
		this.type = type;
	}
	
	public String toXml(String head)
	{
		String xml = null;
		try
		{
			XStream xstream = new XStream(new XppDriver(
					new XmlFriendlyReplacer("__", "_")));
			xstream.alias("bfcrm_req", CrmPrepareCouponSale.class);
			xstream.alias("card", CrmCouponCard.class);
			xstream.alias("voucher", PrepareVoucher.class);
			xstream.aliasField("card_list",CrmPrepareCouponSale.class, "card_list");
			xstream.aliasField("voucher_list",CrmCouponCard.class, "voucher_list");
			
		 	xstream.useAttributeFor(CrmPrepareCouponSale.class, "app");
			xstream.useAttributeFor(CrmPrepareCouponSale.class, "session_id");
			xstream.useAttributeFor(CrmCouponCard.class, "member_id");
			xstream.useAttributeFor(PrepareVoucher.class, "id");
			String xmlString = xstream.toXML(this);
			int xmlLen = xmlString.length();
			String dataLen = "00000000"+String.valueOf(xmlLen);
			dataLen = dataLen.substring(dataLen.length()-8,dataLen.length());
			xml = head +"0108"+ dataLen + xmlString;
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
	public static class CrmCouponCard
	{
		private String member_id;
		private ArrayList voucher_list;
		public String getMember_id()
		{
			return member_id;
		}
		public void setMember_id(String member_id)
		{
			this.member_id = member_id;
		}
		public ArrayList getVoucher_list()
		{
			return voucher_list;
		}
		public void setVoucher_list(ArrayList voucher_list)
		{
			this.voucher_list = voucher_list;
		}
		
	}
	public static class PrepareVoucher
	{
		private String id;
		private String amount;
		
		public String getAmount()
		{
			return amount;
		}
		public void setAmount(String amount)
		{
			this.amount = amount;
		}
		public String getId()
		{
			return id;
		}
		public void setId(String id)
		{
			this.id = id;
		}
		
	}
	
	public class CrmCouponPrepareResult
	{
		
	}
	
}
