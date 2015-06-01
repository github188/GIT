package custom.localize.Bcrm;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.CmPopGiftsDef;
import com.efuture.javaPos.Struct.CmPopGoodsDef;
import com.efuture.javaPos.Struct.CmPopRuleDef;
import com.efuture.javaPos.Struct.CmPopRuleLadderDef;
import com.efuture.javaPos.Struct.CmPopTitleDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.JfSaleRuleDef;
import com.efuture.javaPos.Struct.SuperMarketPopRuleDef;

import custom.localize.Bhls.Bhls_NetService;


public class Bcrm_NetService extends Bhls_NetService
{
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
    
    public boolean findPopRuleCRM(GoodsPopDef popDef, String code, String gz,
                                  String uid, String rulecode, String catid,
                                  String ppcode, String time, String cardno,
                                  String cardtype,Http http)
    {
        if (!GlobalInfo.isOnline)
        {
            return false;
        }

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
        String[] values = 
                          {
                              GlobalInfo.sysPara.mktcode,GlobalInfo.sysPara.jygs ,code, gz, uid,
                              rulecode, catid, ppcode, time, cardno, cardtype
                          };
        String[] args = 
                        {
                            "mktcode","jygs", "code", "gz", "uid", "rule", "catid",
                            "ppcode", "rqsj", "cardno", "cardtype"
                        };

        try
        {
            head = new CmdHead(CmdDef.FINDCRMPOP);
            line.append(head.headToString() +
                        Transition.SimpleXML(values, args));

            //不显示错误信息
            result = HttpCall(http, line, "");

            if (result == 0)
            {
                Vector v = new XmlParse(line.toString()).parseMeth(0,
                                                                   GoodsPopDef.ref);

                if (v.size() > 0)
                {
                    String[] row = (String[]) v.elementAt(0);

                    if (Transition.ConvertToObject(popDef, row))
                    {
                        return true;
                    }
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return false;
    }
    
    //查找商品是否存在换购规则
    public boolean getJfExchangeGoods(JfSaleRuleDef jsrd,String code,String gz,String cuscode,String type)
    {
    	if (!GlobalInfo.isOnline) return false;
    	
    	CmdHead cmdHead = null;
        StringBuffer line = new StringBuffer();
        int result = -1;
        
        try
        {
        	cmdHead = new CmdHead(CmdDef.GETGOODSEXCHANGE);
        	
        	String[] value = 
            {
        		GlobalInfo.sysPara.mktcode,cuscode,type,code,GlobalInfo.sysPara.jygs,gz
            };
        	
        	String[] arg = 
        	{
        		"mktcode","track","type","code","jygs","gz"
        	};
        	
        	line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));
        	
        	result = HttpCall(getMemCardHttp(CmdDef.GETGOODSEXCHANGE), line, "");
        	
        	if (result == 0)
            {
        		Vector v = new XmlParse(line.toString()).parseMeth(0,JfSaleRuleDef.ref);
        		
        		if (v.size() > 0)
                {
        			String[] lines = (String[]) v.elementAt(0);
        			
        			if (Transition.ConvertToObject(jsrd, lines))
                    {
                        return true;
                    }
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
            line    = null;
        }
        
    	return false;
    }
    
    // CMPOP促销 start
    public Vector findCMPOPGoods(String rqsj,GoodsDef goods,String cardno,String cardtype)
    {
    	CmdHead cmdHead = null;
        StringBuffer line = new StringBuffer();
        int result = -1;
        Vector popvec = null;
        
        cmdHead = new CmdHead(CmdDef.FINDGOODSCMPOP);
        
        String[] value = {GlobalInfo.sysPara.mktcode,GlobalInfo.sysPara.jygs,cardno,cardtype,goods.code,goods.gz,goods.uid,goods.catid,goods.ppcode,goods.managemode == '\0'?"":String.valueOf(goods.managemode),rqsj};
        String[] arg = {"mktcode","jygs","cardno","cardtype","code","gz","uid","catid","pp","ruletype","yhsj"};
        
        line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));
        
        result = HttpCall(getMemCardHttp(CmdDef.FINDGOODSCMPOP),line, "");
        
        if (result == 0)
        {
        	Vector v = new XmlParse(line.toString()).parseMeth(0,CmPopGoodsDef.ref);
        	
        	if (v.size() > 0)
            {
        		popvec = new Vector();
        		
                for (int i = 0; i < v.size(); i++)
                {
                    String[] row = (String[]) v.elementAt(i);

                    CmPopGoodsDef cg = new CmPopGoodsDef();

                    if (Transition.ConvertToObject(cg,row))
                    {
                    	// 分解活动档期信息
                    	cg.dqinfo = new CmPopTitleDef();
                    	String []strdqinfo = cg.strdqinfo.split(",");
                    	if (!Transition.ConvertToObject(cg.dqinfo,strdqinfo)) return null;
                    	
                    	// 分解档期规则信息
                    	cg.ruleinfo = new CmPopRuleDef();
                    	
                    	if (cg.strruleinfo.trim().equals("")) return null;
                    	String []strruleinfo = cg.strruleinfo.split(",");
                    	if (!Transition.ConvertToObject(cg.ruleinfo,strruleinfo)) return null;
                    	
                    	// 分解促销规则阶梯
                    	String []strruleladders = cg.strruleladder.split(";");
                    	if (!cg.strruleladder.equals(""))
                    	{
                    		cg.ruleladder = new Vector();
                    		for (int j = 0;j < strruleladders.length;j++)
                    		{
                    			String strruleladder[] = strruleladders[j].split(",");
                    			CmPopRuleLadderDef cprld = new CmPopRuleLadderDef();
                    			if (!Transition.ConvertToObject(cprld,strruleladder)) return null;
                    			
                    			cg.ruleladder.add(cprld);
                    		}	
                    	}
                    	
                    	popvec.add(cg);
                    }
                }
                                
                return popvec;
            }
        	
        	return null;
        }
        
        return null;
    }
    
    
    public Vector findCMPOPGroup(String dqid,String ruleid,int group)
    {
    	CmdHead cmdHead = null;
        StringBuffer line = new StringBuffer();
        int result = -1;
        Vector popvec = null;
        
        cmdHead = new CmdHead(CmdDef.FINDPOPGROUP);
        
        String[] value = {dqid,ruleid,String.valueOf(group)};
        String[] arg = {"dqid","ruleid","group"};
        
        line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));
        
        result = HttpCall(getMemCardHttp(CmdDef.FINDPOPGROUP),line, "");
        
        if (result == 0)
        {
        	// 与本地结构相同
        	Vector v = new XmlParse(line.toString()).parseMeth(0,CmPopGoodsDef.refLocal) ;
        	
        	if (v.size() > 0)
            {
        		popvec = new Vector();
        		
        		for (int i = 0; i < v.size(); i++)
                {
        			String[] row = (String[]) v.elementAt(i);

                    CmPopGoodsDef cg = new CmPopGoodsDef();
                    
                    if (Transition.ConvertToObject(cg,row,CmPopGoodsDef.refLocal))
                    {
                    	popvec.add(cg);
                    }
                }
        		
        		return popvec;
            }
        }
        
    	return null;
    }
    
    public Vector findCMPOPGift(String dqid,String ruleid,String ladderid)
    {
    	CmdHead cmdHead = null;
        StringBuffer line = new StringBuffer();
        int result = -1;
        Vector popvec = null;
        
        cmdHead = new CmdHead(CmdDef.FINDPOPGIFT);
        
        String[] value = {dqid,ruleid,ladderid};
        String[] arg = {"dqid","ruleid","ladderid"};
        
        line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));
        
        result = HttpCall(getMemCardHttp(CmdDef.FINDPOPGIFT),line, "");
        
        if (result == 0)
        {
        	Vector v = new XmlParse(line.toString()).parseMeth(0,CmPopGiftsDef.ref) ;
        	
        	if (v.size() > 0)
            {
	        	popvec = new Vector();
	        	
	        	for (int i = 0; i < v.size(); i++)
	            {
	        		String[] row = (String[]) v.elementAt(i);
	        		
	        		CmPopGiftsDef cg = new CmPopGiftsDef();
	        		
	        		if (Transition.ConvertToObject(cg,row))
	                {
	                 	popvec.add(cg);
	                }
	            }
	        	
	        	return popvec;
            }
        }
        
    	return null;
    }
    // CMPOP促销 end
    
	// 根据商品查找超市促销规则单号
	public boolean findSuperMarketPopBillNo(SuperMarketPopRuleDef ruleDef, String code, String gz, String catid, String ppcode, String spec, String time, String yhtime, String cardno, Http http, int cmdcode)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode, GlobalInfo.sysPara.jygs, code, gz, catid, ppcode, spec, time, yhtime, cardno };
		String[] args = { "mktcode", "jygs", "code", "gz", "catid", "ppcode", "spec", "time", "yhtime", "cardno" };

		try
		{
			head = new CmdHead(cmdcode);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, Language.apply("查找超市促销规则单号失败!"));

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

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode, GlobalInfo.sysPara.jygs, popRule.djbh };
		String[] args = { "mktcode", "jygs", "billno" };

		try
		{
			head = new CmdHead(cmdcode);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, Language.apply("查找超市促销规则失败!"));

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
}
