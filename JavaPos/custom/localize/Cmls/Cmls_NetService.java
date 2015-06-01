package custom.localize.Cmls;

import java.util.Vector;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.RefundMoneyDef;

import custom.localize.Bcrm.Bcrm_NetService;

public class Cmls_NetService extends Bcrm_NetService
{
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
			done = done && done1;
		}
		return done;
	}

	public boolean findPopRuleCRM(GoodsPopDef popDef, String code, String gz, String uid, String rulecode, String catid, String ppcode, String time, String cardno, String cardtype, String isfjk, String grouplist, String djlb, Http http, int cmdcode)
	{
		if (!GlobalInfo.isOnline) { return false; }

		if (cardno == null)
		{
			cardno = " ";
		}

		if (cardtype == null)
		{
			cardtype = " ";
		}

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = {
							GlobalInfo.sysPara.mktcode,
							GlobalInfo.sysPara.jygs,
							code,
							gz,
							uid,
							rulecode,
							catid,
							ppcode,
							time,
							cardno,
							cardtype,
							isfjk,
							grouplist,
							djlb };
		String[] args = {
							"mktcode",
							"jygs",
							"code",
							"gz",
							"uid",
							"rule",
							"catid",
							"ppcode",
							"rqsj",
							"cardno",
							"cardtype",
							"isfjk",
							"grouplist",
							"djlb" };

		try
		{
			head = new CmdHead(cmdcode);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			//不显示错误信息
			result = HttpCall(http, line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, GoodsPopDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(popDef, row)) { return true; }
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
	                              code,custtype,GlobalInfo.sysPara.mktcode,gz,catid,ppcode, specialInfo, GlobalInfo.sysPara.jygs
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
	                                                                   new String[]{"zk", "zkmk", "num1", "num2", "str1", "str2", "memo","qdzkl","zszzkl","zkfd","zkbill"});

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
	                    
	                    if(row.length>7 && !row[7].trim().equals(""))popDef.num6 = Double.parseDouble(row[7]); //起点折扣率
	                    if(row.length>8 && !row[8].trim().equals(""))popDef.num7 = Double.parseDouble(row[8]); //折上折折扣率
	                    if(row.length>9 && !row[9].trim().equals(""))popDef.num8 = Double.parseDouble(row[9]); //折扣分担
	                    if(row.length>10 && !row[10].trim().equals(""))popDef.str6 = row[10];                    //折扣单号
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
}
