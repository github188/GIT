package custom.localize.Bjkl;

import java.util.ArrayList;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.ShopPreSaleDef;
import com.efuture.javaPos.Struct.SuperMarketPopRuleDef;

import custom.localize.Bstd.Bstd_NetService;

public class Bjkl_NetService extends Bstd_NetService
{
	public boolean getShopPreSaleGoods(ArrayList listgoods, String billid)
	{
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();

		try
		{
			if (!GlobalInfo.isOnline)
				return false;

			int result = -1;

			cmdHead = new CmdHead(CmdDef.GETSHOPPRESALE);

			String[] value = { GlobalInfo.sysPara.mktcode, billid };
			String[] arg = { "mktcode", "billid" };

			line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

			result = HttpCall(line, Language.apply("未找到此单据"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[]{"barcode","sl"});

				if (v.size() > 0)
				{
					for (int i = 0; i < v.size(); i++)
					{
						String[] row = (String[]) v.elementAt(i);

						ShopPreSaleDef gd = new ShopPreSaleDef();

						if (Transition.ConvertToObject(gd, row,new String[]{"barcode","sl"}))
						{
							listgoods.add(gd);
						}
						else
						{
							return false;
						}
					}
				}
				else
				{
					return false;
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
			PosLog.getLog(getClass()).error(ex);
			ex.printStackTrace();
			return false;
		}
		finally
		{
			cmdHead = null;
			line = null;
		}
	}
	
	// 根据商品查找超市促销规则单号
	public boolean findSuperMarketPopBillNo(SuperMarketPopRuleDef ruleDef, String code, String gz, String catid, String ppcode, String spec, String time, String yhtime, String cardno, Http http, int cmdcode)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode, code, gz, catid, ppcode, spec, time, yhtime, cardno };
		String[] args = { "mktcode", "code", "gz", "catid", "ppcode", "spec", "time", "yhtime", "cardno" };

		try
		{
			head = new CmdHead(cmdcode);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, "查找超市促销规则单号失败!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, SuperMarketPopRuleDef.ref);

				if (v.size() > 0)
				{
					String[] lines = (String[]) v.elementAt(0);
					if (Transition.ConvertToObject(ruleDef, lines)) { return true; }
				}
				else
				{
					return false;
				}

				//
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
			PosLog.getLog(getClass()).error(er);
			return false;
		}
	}

	// 查找超市促销规则
	public boolean findSuperMarketPopRule(Vector ruleReqList, Vector rulePopList, SuperMarketPopRuleDef popRule, Http http, int cmdcode)
	{
		if (!GlobalInfo.isOnline) { return false; }

		//M:CmdHead类用来管理包头
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
	
		String[] values = { GlobalInfo.sysPara.mktcode, GlobalInfo.sysPara.jygs, popRule.djbh };//xml中 的value
		String[] args = { "mktcode", "jygs", "billno" };//xml中 的key

		try
		{
			head = new CmdHead(cmdcode);

			line.append(head.headToString() + Transition.SimpleXML(values, args));
			
	
			result = HttpCall(line, "查找超市促销规则失败!");

			if (result == 0)
			{
				
				Vector v = new XmlParse(line.toString()).parseMeth(0, SuperMarketPopRuleDef.ref);

				if (v.size() > 0)
				{
					for (int i = 0; i < v.size(); i++)
					{
						String[] row = (String[]) v.elementAt(i);
						SuperMarketPopRuleDef retRule = new SuperMarketPopRuleDef();

						if (Transition.ConvertToObject(retRule, row))
						{
							if (retRule.yhdjlb == '8')
							{
								// 规则条件
								ruleReqList.add(retRule);
							}
							else
							{
								// 规则结果
								rulePopList.add(retRule);
							}
						}
						else
						{
							return false;
						}
					}
				}
				else
				{
					return false;
				}

				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
			PosLog.getLog(getClass()).error(er);
			return false;
		}
	}
	
	//京客隆消费卡消费后，提交交易数据     
	public boolean sendBjklSubmitSale(StringBuffer sb)	    
	{
		if (!GlobalInfo.isOnline) return false;

	    CmdHead cmdHead = null;
	    StringBuffer line = new StringBuffer();
	    int result = -1;
	    try
	    {
	      cmdHead = new CmdHead(138);
	
	      //String[] value = { ConfigClass.CashRegisterCode, String.valueOf(saleHead.fphm), saleHead.djlb, GlobalInfo.sysPara.mktcode, String.valueOf(GlobalInfo.syjStatus.bc), saleHead.rqsj, saleHead.syyh, saleHead.hykh, "", s[2], "", "", "", "", s[1], "", "", "", "" };
	      String[] value = sb.toString().split("\\|")[0].split(",");
	      String[] arg = { "trace", "syjh", "syyh", "fphm", "transtime", "amount", "totalmoney","transtype","setdate","status", "changemoney", "distype", "disinmoney", "disusemoney" };
	      line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));
	
	      result = HttpCall(line, "发送销售信息错误");
	
			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[]{"ret"});

				for (int i = 0; i < v.size(); i++)
				{
					String[] row = (String[]) v.elementAt(0);

					if ((row.length <= 0) || (row[0] == null)) { return false; }

					if (row[0].equals("0")) { return true; }
				}
			}
	    }
	    catch (Exception ex)
	    {
	      ex.printStackTrace();
	    }
	    finally
	    {
	      cmdHead = null;
	      line = null;
	    }
	    return false;
	  }
	
	//京客隆消费卡消费后，提交交易数据     
	public Vector getSalepaysummary(String syyh, String bc, String date, Vector sum)	    
	{
		if (!GlobalInfo.isOnline) return null;

	    CmdHead cmdHead = null;
	    StringBuffer line = new StringBuffer();
	    int result = -1;
	    try
	    {
	      cmdHead = new CmdHead(139);
	
	      String[] value = {date, syyh, bc};
	      String[] arg = {"date","syyh", "bc"};
	      line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));
	
	      result = HttpCall(line, "发送销售信息错误");
	
			if (result == 0)
			{
				sum = new XmlParse(line.toString()).parseMeth(0, new String[]{"payincode", "bs", "je"});
				
				return sum;
			}
	    }
	    catch (Exception ex)
	    {
	      ex.printStackTrace();
	    }
	    finally
	    {
	      cmdHead = null;
	      line = null;
	    }
	    return null;
	  }

}
