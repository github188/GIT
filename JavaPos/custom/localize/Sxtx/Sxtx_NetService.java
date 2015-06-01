package custom.localize.Sxtx;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;

import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.RefundMoneyDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SpareInfoDef;


import custom.localize.Bstd.Bstd_NetService;

public class Sxtx_NetService extends Bstd_NetService
{
	public static int FINDCRMCUST = 210;
	public static int FINDCRMJF = 236;
	//为了区分会员卡，查询CRM会员卡命令号改为210
	
	public String getMkt()
	{
		return "3006";
	}
	
	public boolean getGoodsIsJFXF(SaleHeadDef salehead,GoodsDef goods, SpareInfoDef info, Http http)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
        String[] values = 
        {
            GlobalInfo.sysPara.mktcode,GlobalInfo.sysPara.jygs ,goods.code, goods.gz, goods.uid,
            "0", goods.catid, goods.ppcode,salehead.rqsj, "", ""
        };
String[] args = 
      {
          "mktcode","jygs", "code", "gz", "uid", "rule", "catid",
          "ppcode", "rqsj", "cardno", "cardtype"
      };
		try
		{
			head = new CmdHead(131);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			//不显示错误信息
			result = HttpCall(http, line, "查询本商品积分消费规则失败");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] {"isjf","couponrule","memo","str1","str2","str3","num1","num2"});

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);
					info.char3 = (row[0].charAt(0));
					goods.str4 = row[1];
					return true;
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return false;
	}
	
	 public boolean findHYZK(GoodsPopDef popDef,String code,String custtype,String gz,String catid,String ppcode,String specialInfo,Http http)
	    {
	        if (!GlobalInfo.isOnline)
	        {
	            return false;
	        }

	        CmdHead head = null;
	        StringBuffer line = new StringBuffer();
	        int result = -1;
	        String[] values = 
	                          {
	                              code,custtype,getMkt(),gz,catid,ppcode, specialInfo, GlobalInfo.sysPara.jygs
	                          };
	        String[] args = 
	                        {
	                            "code", "custtype","mktcode", "gz", "catid", "ppcode","specinfo","jygs"
	                        };

	        try
	        {
	            head = new CmdHead(CmdDef.GETCRMVIPZK);
	            line.append(head.headToString() +
	                        Transition.SimpleXML(values, args));

	            //不显示错误信息
	            result = HttpCall(http, line, "");

	            if (result == 0)
	            {
	                Vector v = new XmlParse(line.toString()).parseMeth(0,
	                                                                   new String[]{"zk", "zkmk", "num1", "num2", "str1", "str2", "memo"});

	                if (v.size() > 0)
	                {	
	                    String[] row = (String[]) v.elementAt(0);
	                    
	                    popDef.pophyj = Double.parseDouble(row[0]);
	                    
	                    popDef.num1 = Double.parseDouble(row[1]);
	                    
	                    popDef.num2 = Double.parseDouble(row[2]);
	                    
	                    popDef.num3 = Double.parseDouble(row[3]);
	                    
	                    popDef.num4 = Double.parseDouble(row[4]);
	                    
	                    popDef.str2 = row[5];
	                    
	                    popDef.memo = row[6];
	                    
	                    return true;
	                }
	            }
	        }
	        catch (Exception ex)
	        {
	            ex.printStackTrace();
	        }

	        return false;
	    }
	 
	public boolean getCRMCust(CustomerDef cust, String track)
    {
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { getMkt(), track, GlobalInfo.sysPara.jygs };
		String[] args = { "mktcode", "track", "jygs" };

		try
		{
			head = new CmdHead(FINDCRMCUST);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, "找不到该顾客卡信息!");

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
	
	public boolean getCRMSellJf(String[] row, String mktcode, String syjh, String fphm, String hykh, String hytype)
	{
		if (!GlobalInfo.isOnline) { return false; }

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		String[] values = { getMkt(), syjh, fphm, GlobalInfo.sysPara.jygs, hykh, hytype };
		String[] args = { "mktcode", "syjh", "fphm", "jygs", "hykh", "hytype" };

		try
		{
			head = new CmdHead(FINDCRMJF);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, "计算本笔交易小票积分失败\n请到会员中心查询积分!");

			if (result == 0)
			{
				Vector vi = new XmlParse(line.toString()).parseMeth(0, new String[] { "curJf", "Jf", "memo", "num1" });

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
	
	public boolean javaGetCustXF(String[] row, String cardno, String syjh, String string)
	{
		if (!GlobalInfo.isOnline) { return false; }

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		String[] values = { cardno};
		String[] args = { "cardno" };

		try
		{
			head = new CmdHead(CmdDef.GETCUSTXF);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(getMemCardHttp(CmdDef.GETCUSTXF), line, "查询会员信息\n请到会员中心查询积分!");

			if (result == 0)
			{
				Vector vi = new XmlParse(line.toString()).parseMeth(0, new String[] { "yearxf", "monthxf", "str1", "str2", "str3", "str4", "num1", "num2", "num3", "num4" });

				if (vi.size() > 0)
				{
					String[] row1 = (String[]) vi.elementAt(0);

					row[0] = row1[0];
					row[1] = row1[1];
					row[2] = row1[2];
					row[3] = row1[3];
					row[4] = row1[4];
					row[5] = row1[5];
					row[6] = row1[6];
					row[7] = row1[7];
					row[8] = row1[8];
					row[9] = row1[9];
					
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
			new MessageBox(ex.getMessage());
			PosLog.getLog(getClass()).error(ex);
			return false;
		}
	}
	
	public boolean getRefundMoney(String mkt, String syjh, long fphm, RefundMoneyDef rmd)
	{
		boolean done = super.getRefundMoney(mkt, syjh, fphm, rmd, CmdDef.GETREFUNDMONEY);
		System.out.println(done + " " + GlobalInfo.sysPara.searchPosAndCUST);
		if (done && GlobalInfo.sysPara.searchPosAndCUST.equals("Y"))
		{
			RefundMoneyDef rmd1 = new RefundMoneyDef();
			boolean done1 = super.getRefundMoney(mkt, syjh, fphm, rmd1, CmdDef.GETREFUNDMONEY + 200);
			if (done1)
			{
				rmd.jfkhje = ManipulatePrecision.doubleConvert(rmd.jfkhje + rmd1.jfkhje);
				rmd.jfdesc = rmd.jfdesc + rmd1.jfdesc;
				rmd.fqkhje = ManipulatePrecision.doubleConvert(rmd.fqkhje + rmd1.fqkhje);
				rmd.fqdesc = rmd.fqdesc + rmd1.fqdesc;
				rmd.qtkhje = ManipulatePrecision.doubleConvert(rmd.qtkhje + rmd1.qtkhje);
				rmd.qtdesc = rmd.qtdesc + rmd1.qtdesc;
				rmd.jdxxfkje = ManipulatePrecision.doubleConvert(rmd.jdxxfkje + rmd1.jdxxfkje);
				rmd.jdxxfkdesc = rmd.jdxxfkdesc + rmd1.jdxxfkdesc;
			}
			done = done || done1;
		}
		return done;
	}
}
