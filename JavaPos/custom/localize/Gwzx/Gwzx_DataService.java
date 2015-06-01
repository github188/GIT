package custom.localize.Gwzx;

import java.util.Vector;

import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bcrm.Bcrm_AccessBaseDB;
import custom.localize.Bcrm.Bcrm_DataService;


public class Gwzx_DataService extends Bcrm_DataService
{	
	public int doRefundExtendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Vector retValue)
	{
		int ret = NetService.getDefault().sendSaleData(saleHead, saleGoods, salePayment, retValue, NetService.getDefault().getMemCardHttp(CmdDef.SENDCRMSELL), CmdDef.SENDCRMSELL);
		if (ret != 2 && ret != 0)
		{
			return ret;
		}
		
		if (GlobalInfo.sysPara.searchPosAndCUST.equals("Y"))
		{
			ret = NetService.getDefault().sendSaleData(saleHead, saleGoods, salePayment, retValue, NetService.getDefault().getMemCardHttp(CmdDef.SENDSELL), CmdDef.SENDSELL);
		}
		
		return ret;
	}
	
	//查找满减满增促销
    public boolean findPopRuleCRM(GoodsPopDef popDef,String code,String gz,String uid,String rulecode,String catid,String ppcode,String time,String cardno,String cardtype,String isfjk,String grouplist,String djlb)
    {
    	if (GlobalInfo.isOnline)
    	{
    		Gwzx_NetService netservice = (Gwzx_NetService)NetService.getDefault();
			boolean suc =  netservice.findPopRuleCRM(popDef, code, gz, uid, rulecode, catid, ppcode, time, cardno, cardtype,isfjk,grouplist,djlb,NetService.getDefault().getMemCardHttp(CmdDef.FINDCRMPOP),CmdDef.FINDCRMPOP);
			
	   		if (GlobalInfo.sysPara.searchPosAndCUST.equals("Y"))
    		{
	   			GoodsPopDef popDef1 = new GoodsPopDef();
	   			boolean suc1 = netservice.findPopRuleCRM(popDef1, code, gz, uid, rulecode, catid, ppcode, time, cardno, cardtype,isfjk,grouplist,djlb,NetService.getDefault().getMemCardHttp(CmdDef.FINDCRMPOP + 200),CmdDef.FINDCRMPOP + 200);
	   			popDef.type = popDef1.type;
	   			popDef.mode = popDef.mode+"|"+popDef1.mode;
	   			popDef.jsrq = popDef1.jsrq;
	   			
	   			suc = suc || suc1;
    		}
	   		
	   		return suc;
    	}
    	else
    	{
    		Bcrm_AccessBaseDB accessbasedb = (Bcrm_AccessBaseDB)AccessBaseDB.getDefault();
			return accessbasedb.findPopRuleCRM(popDef, code, gz, uid, rulecode, catid, ppcode, time,cardno,cardtype);
    	}	
    }
}
