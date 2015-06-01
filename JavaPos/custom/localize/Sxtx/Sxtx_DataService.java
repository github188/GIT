package custom.localize.Sxtx;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.SaleHeadDef;


import custom.localize.Bstd.Bstd_DataService;


public class Sxtx_DataService extends Bstd_DataService
{
    public boolean findHYZK(GoodsPopDef popDef,String code,String custtype,String gz,String catid,String ppcode,String specialInfo)
    {
    	if (GlobalInfo.isOnline)
    	{
    		Sxtx_NetService netservice = (Sxtx_NetService)NetService.getDefault();
			return netservice.findHYZK(popDef, code, custtype, gz, catid, ppcode, specialInfo, NetService.getDefault().getMemCardHttp(CmdDef.GETCRMVIPZK));
    	}
    	else
    	{
    		return false;
    	}
    }
    
//	 获取小票实时积分
	public void getCustomerSellJf(SaleHeadDef saleHead)
	{
		if (!Sxtx_CustomLocalize.crmMode())
		{
			super.getCustomerSellJf(saleHead);
			return ;
		}
		
		String[] row = new String[4];

	        if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
	        {
	            if (((Sxtx_NetService)NetService.getDefault()).getCRMSellJf(row, saleHead.mkt, saleHead.syjh, String.valueOf(saleHead.fphm),"",""))
	            {
	                saleHead.bcjf = Convert.toDouble(row[0]);
	                saleHead.ljjf = Convert.toDouble(row[1]);
	                saleHead.num4 = Convert.toDouble(row[3]);
	                saleHead.str5 = row[2];

	                AccessDayDB.getDefault().updateSaleJf(saleHead.fphm, 1, ManipulatePrecision.doubleConvert(saleHead.bcjf+saleHead.num4), saleHead.ljjf);
	                AccessDayDB.getDefault().updateSaleHeadStr(saleHead.fphm,"str5",saleHead.str5);
	                if (saleHead.ljjf > 0)
	                {
	                    StringBuffer sb = new StringBuffer();
	                    sb.append("累计积分: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(saleHead.ljjf), 0, 10, 10, 1) + "\n");
	                    sb.append("本次积分: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(saleHead.bcjf), 0, 10, 10, 1) + "\n");
	                    sb.append("倍享积分: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(saleHead.num4), 0, 10, 10, 1) + "\n");
	                    new MessageBox(sb.toString());
	                }
	            }
	            else
	            {
	                saleHead.bcjf = 0;
	                new MessageBox("计算本笔交易小票积分失败\n请到会员中心查询积分!");
	            }
	            
	            // 打印会员信息
	            String[] row1 = new String[10];
	            if (((Sxtx_NetService)NetService.getDefault()).javaGetCustXF(row1, saleHead.hykh, saleHead.syjh, String.valueOf(saleHead.fphm)))
	            {
	            	new MessageBox("月消费："+row1[1]+"\n年消费："+row1[0]);
	            	
	            	saleHead.str5 = saleHead.str5 + "\n" + "月消费："+row1[1]+"\n年消费："+row1[0];
	            	AccessDayDB.getDefault().updateSaleHeadStr(saleHead.fphm,"str5",saleHead.str5);
	            }
	        }
	}
	
	
	public int doRefundExtendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Vector retValue)
	{
		int ret = NetService.getDefault().sendSaleData(saleHead, saleGoods, salePayment, retValue,
														NetService.getDefault().getMemCardHttp(CmdDef.SENDCRMSELL), CmdDef.SENDCRMSELL);
		if (ret != 2 && ret != 0) { return ret; }

		if (GlobalInfo.sysPara.searchPosAndCUST.equals("Y"))
		{
			ret = NetService.getDefault().sendSaleData(saleHead, saleGoods, salePayment, retValue,
														NetService.getDefault().getMemCardHttp(CmdDef.SENDSELL), CmdDef.SENDSELL);
		}

		return ret;
	}
	
}
