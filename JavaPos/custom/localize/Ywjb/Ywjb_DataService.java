package custom.localize.Ywjb;

import java.util.ArrayList;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bcrm.Bcrm_AccessBaseDB;
import custom.localize.Bcrm.Bcrm_DataService;
import custom.localize.Bcrm.Bcrm_NetService;

public class Ywjb_DataService extends Bcrm_DataService {
    // 获取小票实时积分
    public void getCustomerSellJf(SaleHeadDef saleHead)
    {
        String[] row = new String[3];
        
        if (saleHead.hykh != null && saleHead.hykh.length() > 0)
        {
        	if (NetService.getDefault().getCustomerSellJf(row, GlobalInfo.sysPara.mktcode, saleHead.syjh,String.valueOf(saleHead.fphm)))
        	{
	            saleHead.bcjf = Double.parseDouble(row[0]);
	            saleHead.ljjf = Double.parseDouble(row[1]);
	            saleHead.str5 = row[2];
	            
	            AccessDayDB.getDefault().updateSaleJf(saleHead.fphm, 1, saleHead.bcjf,saleHead.ljjf);

        	}
        	else
        	{
        		saleHead.bcjf = 0;
        		new MessageBox("计算本笔交易小票积分失败\n请到会员中心查询积分!");
        	}     
        }
    }
    
    // 查询返卡规则信息
    public boolean getFjkRuleInfo(MzkRequestDef req, ArrayList fjklist)
    {
        if (GlobalInfo.isOnline)
        {
        	 
        	if ( req.paycode!= null && (req.paycode.equals("0501") || req.paycode.equals("0502") || req.paycode.equals("0503")))
            return NetService.getDefault().getFjkRuleInfo(NetService.getDefault().getMemCardHttp(CmdDef.FINDCRMPOP),req, fjklist);
        	else
        		return NetService.getDefault().getFjkRuleInfo(req,fjklist);
        }
        else
        {
            new MessageBox("返券卡规则查询必须联网使用!");
        }

        return false;
    }
    
    //查找满减满增促销
    public boolean findPopRuleCRM(GoodsPopDef popDef,String code,String gz,String uid,String rulecode,String catid,String ppcode,String time,String cardno,String cardtype,String isfjk,String grouplist)
    {
    	if (GlobalInfo.isOnline)
    	{
    		Bcrm_NetService netservice = (Bcrm_NetService)NetService.getDefault();
			boolean suc =  netservice.findPopRuleCRM(popDef, code, gz, uid, rulecode, catid, ppcode, time, cardno, cardtype,NetService.getDefault().getMemCardHttp(CmdDef.FINDCRMPOP));


	   			GoodsPopDef popDef1 = new GoodsPopDef();
	   			boolean suc1 = netservice.findPopRuleCRM(popDef1, code, gz, uid, rulecode, catid, ppcode, time, cardno, cardtype,NetService.getDefault().getMemCardHttp(CmdDef.FINDCRMPOP + 200));
	   			popDef.type = popDef1.type;
	   			
	   			suc = suc || suc1;
	   		
	   		return suc;
    	}
    	else
    	{
    		Bcrm_AccessBaseDB accessbasedb = (Bcrm_AccessBaseDB)AccessBaseDB.getDefault();
			return accessbasedb.findPopRuleCRM(popDef, code, gz, uid, rulecode, catid, ppcode, time,cardno,cardtype);
    	}	
    }
}
