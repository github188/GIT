package custom.localize.Bcrm;


import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.JfSaleRuleDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SuperMarketPopRuleDef;

import custom.localize.Bhls.Bhls_DataService;

public class Bcrm_DataService extends Bhls_DataService 
{
    public boolean getCustomer(CustomerDef cust, String track)
    {
        if (GlobalInfo.isOnline)
        {
            if (!NetService.getDefault().getCustomer(cust,track))
            {
                return false;
            }
        }
        else
        {	
            if (!AccessBaseDB.getDefault().getCustomer(cust,track))
            {
            	new MessageBox(Language.apply("无此顾客卡信息!"), null, false);
            	
                return false;
            }
        }
        
        return true;
    }
    
	// 获取私有参数
	public boolean getNetSysPara()
	{
		try
		{
			if (GlobalInfo.isOnline)
	        {
	            if (!NetService.getDefault().getSysPara()) return false;
	        }

	        // 读取CRM参数信息
			if (GlobalInfo.isOnline)
			{
				// 读取POS参数但不做paraFinish处理,以避免重复处理
				if (!AccessLocalDB.getDefault().readSysPara(false)) return false;
				
				if (NetService.getDefault().getMemCardHttp(CmdDef.GETCRMPARA) != GlobalInfo.localHttp)
				{
					if (!NetService.getDefault().getSysPara(null,false,CmdDef.GETCRMPARA)) return false;
				}
			}
			return true;
		}
		catch(Exception er)
		{
			er.printStackTrace();
			return false;
		}
		finally
		{
			AccessLocalDB.getDefault().readSysPara();
		}
	}

    // 查找满减满增促销
    public boolean findPopRuleCRM(GoodsPopDef popDef,String code,String gz,String uid,String rulecode,String catid,String ppcode,String time,String cardno,String cardtype,String djlb)
    {
    	if (GlobalInfo.isOnline)
    	{
    		Bcrm_NetService netservice = (Bcrm_NetService)NetService.getDefault();
			return netservice.findPopRuleCRM(popDef, code, gz, uid, rulecode, catid, ppcode, time, cardno, cardtype,NetService.getDefault().getMemCardHttp(CmdDef.FINDCRMPOP));
    	}
    	else
    	{
    		Bcrm_AccessBaseDB accessbasedb = (Bcrm_AccessBaseDB)AccessBaseDB.getDefault();
			return accessbasedb.findPopRuleCRM(popDef, code, gz, uid, rulecode, catid, ppcode, time,cardno,cardtype);
    	}	
    }
    
    public boolean findHYZK(GoodsPopDef popDef,String code,String custtype,String gz,String catid,String ppcode,String specialInfo)
    {
    	if (GlobalInfo.isOnline)
    	{
    		Bcrm_NetService netservice = (Bcrm_NetService)NetService.getDefault();
			return netservice.findHYZK(popDef, code, custtype, gz, catid, ppcode, specialInfo, NetService.getDefault().getMemCardHttp(CmdDef.GETCRMVIPZK));
    	}
    	else
    	{
    		Bcrm_AccessBaseDB accessbasedb = (Bcrm_AccessBaseDB)AccessBaseDB.getDefault();
			return accessbasedb.findHYZK(popDef, code, custtype, gz, catid, ppcode, specialInfo);
    	}
    }
    
    //查找商品是否存在换购规则
    public boolean getJfExchangeGoods(JfSaleRuleDef jsrd,String barcode,String gz,String custcode,String type)
    {
    	if (!GlobalInfo.isOnline) return false;
    	
		if (!((Bcrm_NetService)NetService.getDefault()).getJfExchangeGoods(jsrd,barcode,gz,custcode,type)) return false;
		
		return true;
    }    
   
    
	// 获取小票实时积分
    public void getCustomerSellJf(SaleHeadDef saleHead)
    {
        String[] row = new String[4];

        if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
        {
            if (NetService.getDefault().getCustomerSellJf(row, saleHead.mkt, saleHead.syjh, String.valueOf(saleHead.fphm)))
            {
                saleHead.bcjf = Convert.toDouble(row[0]);
                saleHead.ljjf = Convert.toDouble(row[1]);
                saleHead.str5 = row[2];
                saleHead.num4 = Convert.toDouble(row[3]);
                
                if (GlobalInfo.sysPara.sendhyjf == 'Y')
                {
                	if (!sendHykJf(saleHead))
                	{
                		 new MessageBox(Language.apply("本笔积分同步失败无法获得累计积分\n请到会员中心查询累计积分!"));
                	}
                }
                
                if (saleHead.ljjf != 0)
                {
                    StringBuffer sb = new StringBuffer();
                    sb.append(Language.apply("累计积分: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(saleHead.ljjf), 0, 10, 10, 1) + "\n");
                    if (saleHead.bcjf != 0) sb.append(Language.apply("本次积分: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(saleHead.bcjf), 0, 10, 10, 1) + "\n");
                    if (saleHead.num4 != 0) sb.append(Language.apply("倍享积分: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(saleHead.num4), 0, 10, 10, 1) + "\n");
                    new MessageBox(sb.toString());
                }
                
                AccessDayDB.getDefault().updateSaleJf(saleHead.fphm, 1, saleHead.bcjf, saleHead.ljjf,String.valueOf(saleHead.num4));
            }
            else
            {
                saleHead.bcjf = 0;
                new MessageBox(Language.apply("计算本笔交易小票积分失败\n请到会员中心查询积分!"));
            }
        }
    }
    
    // CMPOP促销 start
    public Vector findCMPOPGoods(String rqsj,GoodsDef goods,String cardno,String cardtype)
    {
    	// 联网本地优先查询(Y-联网优先本地查询再查网上/Z-联网只查询本地)
    	if (GlobalInfo.isOnline && GlobalInfo.sysPara.localfind != 'N')
    	{
    		Vector v = ((Bcrm_AccessBaseDB)AccessBaseDB.getDefault()).findCMPOPGoods(rqsj,goods,cardno,cardtype);
    		if (v == null || v.size() <= 0)
            {
    			// Z-联网只查询本地,失败不再查询网上
    			if (GlobalInfo.sysPara.localfind == 'Z')
    			{
	                return v;
    			}
            }
    		else 
    		{
    			return v;
    		}
    	}
    	
		if (GlobalInfo.isOnline)
		{
			return ((Bcrm_NetService)NetService.getDefault()).findCMPOPGoods(rqsj, goods, cardno, cardtype);
		}
		else
		{
			return ((Bcrm_AccessBaseDB)AccessBaseDB.getDefault()).findCMPOPGoods(rqsj,goods,cardno,cardtype);
		}
    }
    
    public Vector findCMPOPGroup(String dqid,String ruleid,int group)
    {
    	// 联网本地优先查询(Y-联网优先本地查询再查网上/Z-联网只查询本地)
    	if (GlobalInfo.isOnline && GlobalInfo.sysPara.localfind != 'N')
    	{
    		Vector v = ((Bcrm_AccessBaseDB)AccessBaseDB.getDefault()).findCMPOPGroup(dqid,ruleid,group);
    		if (v == null || v.size() <= 0)
            {
    			// Z-联网只查询本地,失败不再查询网上
    			if (GlobalInfo.sysPara.localfind == 'Z')
    			{
	                return v;
    			}
            }
    		else 
    		{
    			return v;
    		}
    	}
    	
		if (GlobalInfo.isOnline)
		{
			return ((Bcrm_NetService)NetService.getDefault()).findCMPOPGroup(dqid, ruleid, group);
		}
		else
		{
			return ((Bcrm_AccessBaseDB)AccessBaseDB.getDefault()).findCMPOPGroup(dqid,ruleid,group);
		}
    }
    
    public Vector findCMPOPGift(String dqid,String ruleid,String ladderid)
    {
    	// 联网本地优先查询(Y-联网优先本地查询再查网上/Z-联网只查询本地)
    	if (GlobalInfo.isOnline && GlobalInfo.sysPara.localfind != 'N')
    	{
    		Vector v = ((Bcrm_AccessBaseDB)AccessBaseDB.getDefault()).findCMPOPGift(dqid,ruleid,ladderid);
    		if (v == null || v.size() <= 0)
            {
    			// Z-联网只查询本地,失败不再查询网上
    			if (GlobalInfo.sysPara.localfind == 'Z')
    			{
	                return v;
    			}
            }
    		else 
    		{
    			return v;
    		}
    	}
    	
		if (GlobalInfo.isOnline)
		{
			return ((Bcrm_NetService)NetService.getDefault()).findCMPOPGift(dqid, ruleid, ladderid);
		}
		else
		{
			return ((Bcrm_AccessBaseDB)AccessBaseDB.getDefault()).findCMPOPGift(dqid,ruleid,ladderid);
		}
    }
    
	// 根据商品查找超市促销规则单号
	public boolean findSuperMarketPopBillNo(SuperMarketPopRuleDef ruleDef, String code, String gz, String catid, String ppcode, String spec, String time, String yhtime, String cardno)
	{
		if (GlobalInfo.isOnline)
		{
			Bcrm_NetService netservice = ((Bcrm_NetService)NetService.getDefault());
			boolean suc = netservice.findSuperMarketPopBillNo(ruleDef, code, gz, catid, ppcode, spec, time, yhtime, cardno,
																NetService.getDefault().getMemCardHttp(CmdDef.GETSMPOPBILLNO), CmdDef.GETSMPOPBILLNO);
			if (suc)
			{
				if (ruleDef.djbh.length() > 0) return true;
				else return false;
			}
			return suc;
		}
		return true;
	}

	// 根据规则单号查询超市促销规则
	public boolean findSuperMarketPopRule(Vector ruleReqList, Vector rulePopList, SuperMarketPopRuleDef ruleDef)
	{
		if (GlobalInfo.isOnline)
		{
			Bcrm_NetService netservice = ((Bcrm_NetService)NetService.getDefault());
			boolean suc = netservice.findSuperMarketPopRule(ruleReqList, rulePopList, ruleDef, NetService.getDefault()
																											.getMemCardHttp(CmdDef.GETSMPOPRULE),
															CmdDef.GETSMPOPRULE);
			return suc;
		}
		return true;
	}
}
