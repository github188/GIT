package custom.localize.Bhls;

import java.util.Vector;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.GoodsAmountDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;

public class Bhls_DataService extends DataService 
{
    // 查找满减满增促销
    public boolean findPopRule(GoodsPopDef popDef,String code,String gz,String uid,String rulecode,String catid,String ppcode,String time, String custType, String custNo)
    {
    	if (GlobalInfo.isOnline)
    	{
    		Bhls_NetService netservice = (Bhls_NetService)NetService.getDefault();
    		
    		if (GlobalInfo.sysPara.rulepop == 'F' || GlobalInfo.sysPara.rulepop == 'A' || GlobalInfo.sysPara.rulepop == 'S')
    			return netservice.findPopRuleNew(popDef, code, gz, uid, rulecode, catid, ppcode, time);
    		else
    			return netservice.findPopRule(popDef, code, gz, uid, rulecode, catid, ppcode, time, custType, custNo);
    	}
    	else
    	{
    		Bhls_AccessBaseDB accessbasedb = (Bhls_AccessBaseDB)AccessBaseDB.getDefault();
    		
    		if (GlobalInfo.sysPara.rulepop == 'F' || GlobalInfo.sysPara.rulepop == 'A' || GlobalInfo.sysPara.rulepop == 'S')
    			return accessbasedb.findPopRuleNew(popDef, code, gz, uid, rulecode, catid, ppcode, time);
    		else
    			return accessbasedb.findPopRule(popDef, code, gz, uid, rulecode, catid, ppcode, time, custType, custNo);
    	}
    }
    
    // 查找前台满赠促销
    public boolean findGiftRule(GoodsPopDef popDef,String code,String gz,String uid,String rulecode,String catid,String ppcode,String time,String cardno,String cardtype)
    {
    	if (GlobalInfo.isOnline)
    	{
    		Bhls_NetService netservice = (Bhls_NetService)NetService.getDefault();
    		
			return netservice.findGiftRuleNew(popDef, code, gz, uid, rulecode, catid, ppcode, time,cardno,cardtype);
    	}
    	else
    	{
    		Bhls_AccessBaseDB accessbasedb = (Bhls_AccessBaseDB)AccessBaseDB.getDefault();
    		
			return accessbasedb.findGiftRuleNew(popDef, code, gz, uid, rulecode, catid, ppcode, time,cardno,cardtype);
    	}
    }
    
    // 查找编码商品赠品促销
    public boolean findRulePopGift(Vector giftGoods,String djbh)
    {
    	if (GlobalInfo.isOnline)
    	{
    		Bhls_NetService netservice = (Bhls_NetService)NetService.getDefault();
    		return netservice.findRulePopGift(giftGoods, djbh);	
    	}
    	else
    	{
    		Bhls_AccessBaseDB accessbasedb = (Bhls_AccessBaseDB)AccessBaseDB.getDefault();
    		return accessbasedb.findRulePopGift(giftGoods, djbh);
    	}
    }
	
    // 查找VIP限量折扣定义
    public boolean findLimitVIPZK(String vipno,GoodsAmountDef limitzk,SaleGoodsDef saleGoodsDef)
	{
		if (!GlobalInfo.isOnline) return false;
		
		Bhls_NetService netservice = (Bhls_NetService)(NetService.getDefault());
		if (netservice.findLimitVIPZK(limitzk, vipno, saleGoodsDef.code, saleGoodsDef.gz, saleGoodsDef.uid))
		{
			return true;
		}
		else
		{
			return false;
		}
	}    
}
