package custom.localize.Wqbh;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.RefundMoneyDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bcrm.Bcrm_NetService;
import custom.localize.Wqbh.Wqbh_SaleBillMode.DQTaxDef;

public class Wqbh_NetService extends Bcrm_NetService
{
	public int sendExtendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Vector retValue)
	{
		if (saleHead.bc == '#') { return sendSaleData(saleHead, saleGoods, salePayment, retValue, null, 38); }

		return sendSaleData(saleHead, saleGoods, salePayment, retValue, getMemCardHttp(45), 45);
	}
	
	public boolean getMemoInfo()
	{
		return true;
	}
	
	public boolean sendTaxLog(String[] faxref ,DQTaxDef dtd)
	{
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			if (!GlobalInfo.isOnline) { return false; }

			 String[] values = {dtd.mkt,dtd.syjh,String.valueOf(dtd.fphm),dtd.machineid,dtd.invoicecode,dtd.fiscalcode,String.valueOf(dtd.invoiceserialnumber),String.valueOf(dtd.invoicedate),String.valueOf(dtd.invoicetime),String.valueOf(dtd.invoicetype),String.valueOf(dtd.totalsum),dtd.infoid,dtd.reserve};

			cmdHead = new CmdHead(CmdDef.SENDFAXINFO);
			line.append(cmdHead.headToString() + Transition.SimpleXML(values, faxref));

			result = HttpCall(line, "税控信息上传失败!");

			if (result == 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(getClass()).error(ex);
			ex.printStackTrace();
			return false;
		}
	}
	
	public boolean getBackSaleInfo(String syjh, String fphm, SaleHeadDef shd, Vector saleDetailList, Vector payDetail)
	{
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { syjh, fphm };
		String[] args = { "syjh", "code" };

		try
		{
			// 查询退货小票头
			head = new CmdHead(CmdDef.GETBACKSALEHEAD);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox("退货小票头查询失败!");
				return false;
			}

			Vector v = new XmlParse(line.toString()).parseMeth(0, SaleHeadDef.ref);

			if (v.size() < 1)
			{
				new MessageBox("没有查询到退货小票头,退货小票不存在或已确认!");
				return false;
			}

			String[] row = (String[]) v.elementAt(0);
            //判断此单积分是否足够扣回
			if(row[38].trim().equals("#")){
				shd = null;
				new MessageBox("卡内积分不足扣回不允许退货，请重新做退货单！");
				return false;
			}
			
			if (!Transition.ConvertToObject(shd, row))
			{
				shd = null;
				new MessageBox("退货小票头转换失败!");
				return false;
			}

			line.delete(0, line.length());
			v.clear();
			row = null;
			result = -1;

			// 查询退货小票明细
			head = new CmdHead(CmdDef.GETBACKSALEDETAIL);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox("退货小票明细查询失败!");
				return false;
			}

			v = new XmlParse(line.toString()).parseMeth(0, SaleGoodsDef.ref);

			if (v.size() < 1)
			{
				new MessageBox("没有查询到退货小票明细,退货小票不存在或已确认!");
				return false;
			}

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				SaleGoodsDef sgd = new SaleGoodsDef();

				if (Transition.ConvertToObject(sgd, row))
				{
					saleDetailList.add(sgd);
				}
				else
				{
					saleDetailList.clear();
					saleDetailList = null;
					return false;
				}
			}

			line.delete(0, line.length());
			v.clear();
			row = null;
			result = -1;

			// 查询小票付款明细
			head = new CmdHead(CmdDef.GETBACKPAYSALEDETAIL);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox("付款明细查询失败!");
				return false;
			}

			v = new XmlParse(line.toString()).parseMeth(0, SalePayDef.ref);

			if (v.size() < 1)
			{
				new MessageBox("没有查询到付款小票明细,退货小票不存在或已确认!");
				return false;
			}

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);
				SalePayDef spd = new SalePayDef();

				if (Transition.ConvertToObject(spd, row))
				{
					payDetail.add(spd);
				}
				else
				{
					payDetail.clear();
					payDetail = null;
					return false;
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			shd = null;

			if (saleDetailList != null)
			{
				saleDetailList.clear();
				saleDetailList = null;
			}
			ex.printStackTrace();
			return false;
		}
		finally
		{
			head = null;
			line = null;
		}
	}
	public boolean sendMemberInfo(CustomerDef cust,String name, String track2, String cardID, String bonus, String validBonus, String certificateType, String certificate, String sex, String phone, String memberLevel, String address, String email)
	{	
		return sendMemberInfo(getMemCardHttp(CmdDef.FINDCUSTOMER), cust, name,track2,cardID,bonus,validBonus,certificateType,certificate,sex,phone,memberLevel,address,email);
	}
	public boolean sendMemberInfo(Http h, CustomerDef cust,String name, String track2, String cardID, String bonus, String validBonus, String certificateType, String certificate, String sex, String phone, String memberLevel, String address, String email)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode, track2,name,cardID,bonus,validBonus,certificateType,certificate,sex,phone,memberLevel,address,email,""};
		String[] args = { "mktcode", "track", "name","cardID","bonus","validBonus","certificateType","certificate","sex","phone","memberLevel","address","email","memo"};

		try
		{
			head = new CmdHead(133);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(h, line, "找不到该顾客卡信息!");

			if (result == 0)
			{

				Vector v = new XmlParse(line.toString()).parseMeth(0, CustomerDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(cust, row)) { return true; }
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		return false;
	
	}
	
	public boolean getDHYRefundMoney(String mkt, String syjh, long fphm,double dhyjf, RefundMoneyDef rmd)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { mkt, GlobalInfo.sysPara.jygs, syjh, String.valueOf(fphm), String.valueOf(dhyjf)};
		String[] args = { "mkt", "jygs", "syjh", "fphm" ,"validBonus"};

		try
		{
			head = new CmdHead(134);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(getMemCardHttp(134), line, "联网计算退货扣回金额失败!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, RefundMoneyDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(rmd, row)) { return true; }
				}
			}
		}
		catch (Exception er)
		{
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();
		}

		return false;
	}
	
    public boolean getCustomerSellJf(String[] row, String mktcode, String syjh, String fphm, String hykh, String hytype, Http http)
	{
		if (!GlobalInfo.isOnline) { return false; }

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		String[] values = { mktcode, syjh, fphm, GlobalInfo.sysPara.jygs, hykh, hytype };
		String[] args = { "mktcode", "syjh", "fphm", "jygs", "hykh", "hytype" };

		try
		{
			head = new CmdHead(CmdDef.GETCUSTSELLJF);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(http, line, "计算本笔交易小票积分失败\n请到会员中心查询积分!");

			if (result == 0)
			{
				Vector vi = new XmlParse(line.toString()).parseMeth(0, new String[] { "curJf", "Jf", "memo", "num1","jftz" });

				if (vi.size() > 0)
				{
					String[] row1 = (String[]) vi.elementAt(0);

					row[0] = row1[0];
					row[1] = row1[1];
					row[2] = row1[2];

					if (row.length > 3 && row1.length > 3)
					{
						row[3] = row1[3];
					}
					if (row.length > 4 && row1.length > 4)
					{
						row[4] = row1[4];
					}
				}

				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(getClass()).error(ex);
			return false;
		}
	}

}
