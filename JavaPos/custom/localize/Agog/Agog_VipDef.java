package custom.localize.Agog;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

public class Agog_VipDef
{
	public String getQueryRequestJson(String card, String chkcode)
	{
		JSONObject content = new JSONObject();
		content.put("CardNum", card);
		content.put("CheckCode", chkcode);

		return content.toString();
	}

	public String getCheckCodeRequestJson(String card)
	{
		JSONObject content = new JSONObject();
		content.put("CardNum", card);

		return content.toString();
	}

	public String getRoomVipRequestJson(String roomCode)
	{
		try
		{
			JSONObject content = new JSONObject();
			content.put("RoomCode", roomCode);
			return content.toString();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public String getSubmitScoreRequestJson(SaleHeadDef head)
	{
		try
		{
			JSONObject content = new JSONObject();
			content.put("CardNum", head.hykh);
			content.put("BillNo", head.syjh + String.valueOf(head.fphm));
			content.put("CheckCode", "");
			content.put("ConsumeDate", ManipulateDateTime.getCurrentDateTimeBySign());
			content.put("ConsumeMoney", String.valueOf(head.ysje));
			content.put("MemberPayMoney", "0.00");
			content.put("HappeIntegral", String.valueOf(head.bcjf));
			content.put("SourceCode", "1");
			content.put("DetailInfo", new JSONArray());
			content.put("ShopNo", GlobalInfo.sysPara.mktcode);

			return content.toString();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public String getSubmitBillRequestJson(MzkRequestDef req, String checkcode)
	{
		try
		{
			JSONObject content = new JSONObject();
			content.put("CardNum", req.track2);
			content.put("BillNo", req.syjh + String.valueOf(req.fphm));
			content.put("CheckCode", checkcode);
			content.put("ConsumeDate", ManipulateDateTime.getCurrentDateTimeBySign());
			content.put("ConsumeMoney", String.valueOf(req.num2));
			content.put("MemberPayMoney", String.valueOf(req.je));
			content.put("HappeIntegral", "0");
			content.put("SourceCode", "1");

			content.put("DetailInfo", new JSONArray());
			content.put("ShopNo", GlobalInfo.sysPara.mktcode);

			/*
			 * JSONArray jsonarray = new JSONArray();
			 * 
			 * for (int i = 0; i < saleGoods.size(); i++) { SaleGoodsDef sgd =
			 * (SaleGoodsDef) saleGoods.get(i); JSONObject goods = new
			 * JSONObject(); goods.put("SortMane", sgd.name);
			 * goods.put("WineMane", sgd.name); goods.put("OrderNumber",
			 * String.valueOf(sgd.sl));
			 * 
			 * jsonarray.add(goods); }
			 * 
			 * content.put("DetailInfo", jsonarray);
			 */
			return content.toString();

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public QueryVipRet parseQueryJson(String retJson)
	{
		try
		{
			QueryVipRet vipQueryRet = new QueryVipRet();

			JSONObject content = JSONObject.fromObject(retJson);

			vipQueryRet.CardNum = content.getString("CardNum");
			vipQueryRet.MemberName = content.getString("MemberName");
			vipQueryRet.GradeCode = content.getString("GradeCode");
			vipQueryRet.GradeName = content.getString("GradeName");
			vipQueryRet.Password = content.getString("Password");
			vipQueryRet.Sex = content.getString("Sex");
			vipQueryRet.TelePhoneNum = content.getString("TelePhoneNum");
			vipQueryRet.ShopNo = content.getString("ShopNo");
			vipQueryRet.ShopName = content.getString("ShopName");
			vipQueryRet.StatusCode = content.getString("StatusCode");
			vipQueryRet.StatusName = content.getString("StatusName");
			vipQueryRet.CardIntegral = content.getString("CardIntegral");
			vipQueryRet.Balance = content.getString("Balance");
			vipQueryRet.CashBalance = content.getString("CashBalance");
			vipQueryRet.PresentBalance = content.getString("PresentBalance");
			vipQueryRet.PapersType = content.getString("PapersType");
			vipQueryRet.PapersNum = content.getString("PapersNum");
			vipQueryRet.CreateDate = content.getString("CreateDate");
			vipQueryRet.CardUseLimitDate = content.getString("CardUseLimitDate");
			vipQueryRet.Birthday = content.getString("Birthday");
			vipQueryRet.QQ = content.getString("QQ");
			vipQueryRet.MSN = content.getString("MSN");
			vipQueryRet.E_Mail = content.getString("E-Mail");
			vipQueryRet.Address = content.getString("Address");
			vipQueryRet.Remark = content.getString("Remark");
			vipQueryRet.ReturnCode = content.getString("ReturnCode");
			vipQueryRet.ReturnMessage = content.getString("ReturnMessage");
			vipQueryRet.IsHandInput = content.getString("IsHandInput");

			if (vipQueryRet.ReturnCode.equals("0"))
				return vipQueryRet;

			new MessageBox(vipQueryRet.ReturnMessage);
			return null;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public String parseCheckCodeJson(String retJson)
	{
		try
		{
			ResponseRet chkCode = new ResponseRet();

			JSONObject content = JSONObject.fromObject(retJson);
			chkCode.CheckCode = content.getString("CheckCode");
			chkCode.ReturnCode = content.getString("ReturnCode");
			chkCode.ReturnMessage = content.getString("ReturnMessage");

			if (chkCode.ReturnCode.equals("0"))
				return chkCode.CheckCode;

			new MessageBox(chkCode.ReturnMessage);
			return null;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args)
	{
		String retJson = "{\"MemberInfo\": [{\"Address\": \"\" }], \"ReturnCode\": \"0\",\"ReturnMessage\":\"0\"}";
		Agog_VipDef vip = new Agog_VipDef();
		vip.parseRoomVipJson(retJson);

	}

	public QueryVipRet parseRoomVipJson(String retJson)
	{
		try
		{
			QueryVipRet vipQueryRet = new QueryVipRet();

			JSONObject content = JSONObject.fromObject(retJson);
			JSONArray vip = content.getJSONArray("MemberInfo");
			JSONObject array = vip.getJSONObject(0);

			if (array != null)
			{
				vipQueryRet.CardNum = array.getString("CardNum");
				vipQueryRet.MemberName = array.getString("MemberName");
				vipQueryRet.GradeCode = array.getString("GradeCode");
				vipQueryRet.GradeName = array.getString("GradeName");
				vipQueryRet.Password = array.getString("Password");
				vipQueryRet.Sex = array.getString("Sex");
				vipQueryRet.TelePhoneNum = array.getString("TelePhoneNum");
				vipQueryRet.ShopNo = array.getString("ShopNo");
				vipQueryRet.ShopName = array.getString("ShopName");
				vipQueryRet.StatusCode = array.getString("StatusCode");
				vipQueryRet.StatusName = array.getString("StatusName");
				vipQueryRet.CardIntegral = array.getString("CardIntegral");
				vipQueryRet.Balance = array.getString("Balance");
				vipQueryRet.CashBalance = array.getString("CashBalance");
				vipQueryRet.PresentBalance = array.getString("PresentBalance");
				vipQueryRet.PapersType = array.getString("PapersType");
				vipQueryRet.PapersNum = array.getString("PapersNum");
				vipQueryRet.CreateDate = array.getString("CreateDate");
				vipQueryRet.CardUseLimitDate = array.getString("CardUseLimitDate");
				vipQueryRet.Birthday = array.getString("Birthday");
				vipQueryRet.QQ = array.getString("QQ");
				vipQueryRet.MSN = array.getString("MSN");
				vipQueryRet.E_Mail = array.getString("E-Mail");
				vipQueryRet.Address = array.getString("Address");
				vipQueryRet.Remark = array.getString("Remark");
				vipQueryRet.IsHandInput = array.getString("IsHandInput");
			}

			vipQueryRet.ReturnCode = content.getString("ReturnCode");
			vipQueryRet.ReturnMessage = content.getString("ReturnMessage");

			if (vipQueryRet.ReturnCode.equals("0"))
				return vipQueryRet;

			new MessageBox(vipQueryRet.ReturnMessage);
			return null;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public boolean parseSubmitBillJson(String retJson)
	{
		try
		{
			ResponseRet submit = new ResponseRet();
			JSONObject content = JSONObject.fromObject(retJson);

			submit.ReturnCode = content.getString("ReturnCode");
			submit.ReturnMessage = content.getString("ReturnMessage");

			if (submit.ReturnCode.equals("0"))
				return true;

			new MessageBox(submit.ReturnMessage);
			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	class ResponseRet
	{
		public String CheckCode;
		public String ReturnCode;
		public String ReturnMessage;
	}

	class QueryVipRet
	{
		public String CardNum;
		public String MemberName;
		public String GradeCode;
		public String GradeName;
		public String Password;
		public String Sex;
		public String TelePhoneNum;
		public String ShopNo;
		public String ShopName;
		public String StatusCode;
		public String StatusName;
		public String CardIntegral;
		public String Balance;
		public String CashBalance;
		public String PresentBalance;
		public String PapersType;
		public String PapersNum;
		public String CreateDate;
		public String CardUseLimitDate;
		public String Birthday;
		public String QQ;
		public String MSN;
		public String E_Mail;
		public String Address;
		public String Remark;
		public String ReturnCode;
		public String ReturnMessage;
		public String IsHandInput;
	}

}
