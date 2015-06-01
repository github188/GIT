package custom.localize.Bjys;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.SaleHeadDef;

public class Bjys_DataService extends DataService
{	
	//获得POS积分规则
    /*public boolean getPosJfRule()
    {   	
    	if (GlobalInfo.isOnline)
        {
    		((Bjys_NetService)NetService.getDefault()).getPosJfRule();
        }
    	
    	return true;
    }*/
    
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
	            saleHead.num1= Double.parseDouble(row[2]);
	            
	            ((Bjys_AccessDayDB)AccessDayDB.getDefault()).updateSaleJf(saleHead.fphm, 1, saleHead.bcjf,saleHead.ljjf,saleHead.num1);
        	}
        	else
        	{
        		saleHead.bcjf = 0;
        		new MessageBox("计算本笔交易小票积分失败\n请到会员中心查询积分!");
        	}     
        }
    }
}
