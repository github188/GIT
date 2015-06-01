package custom.localize.Tcrc;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bhls.Bhls_DataService;

public class Tcrc_DataService extends Bhls_DataService {
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
	            
	            if ( saleHead.bcjf > 0 ||  saleHead.ljjf > 0)
	            {
	            	StringBuffer sb = new StringBuffer();
	            	sb.append("本笔交易有存在积分\n");
	            	sb.append("本次积分: " + Convert.appendStringSize("",String.valueOf(saleHead.bcjf),0,10,10,1)+"\n");
	            	sb.append("累计积分: " + Convert.appendStringSize("",String.valueOf(saleHead.ljjf),0,10,10,1)+"\n");
	            	sb.append(saleHead.str5);
	            	new MessageBox(sb.toString());
	            }
        	}
        	else
        	{
        		saleHead.bcjf = 0;
        		new MessageBox("计算本笔交易小票积分失败\n请到会员中心查询积分!");
        	}     
        }
    }
    
    //获取小票实时返券
    public void getSellRealFQ(SaleHeadDef saleHead)
    {
        String[] row = new String[3];

        if (SellType.ISSALE(saleHead.djlb))
        {
	        if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
	        {
	            if (NetService.getDefault().getSellRealFQ(row, GlobalInfo.sysPara.mktcode, saleHead.syjh, String.valueOf(saleHead.fphm)))
	            {
	            	saleHead.memo = row[0] + "," + row[1] + ","+row[2];
	
	                double faq = Convert.toDouble(row[0]);
	                double fbq = Convert.toDouble(row[1]);
	                AccessDayDB.getDefault().updateSaleJf(saleHead.fphm, 4, 0, 0,saleHead.memo);
	
	                // 提示
	                if ((Convert.toDouble(row[0]) != 0) || (Convert.toDouble(row[1]) != 0))
	                {
	                    StringBuffer sb = new StringBuffer();
	                    sb.append("本笔交易有活动返券\n");
	                    sb.append("返A券: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(faq), 0, 10, 10, 1) + "\n");
	                    sb.append("返B券: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(fbq), 0, 10, 10, 1));
	                    new MessageBox(sb.toString());
	                }
	            }
	            else
	            {
	                saleHead.memo = "-1,-1";
	                new MessageBox("计算本笔交易小票返券失败\n请到会员中心查询返券!");
	            }
	        }
        }
        else // TCRC需要在退货时检查扣除的返券金额并提示
        {
            if (NetService.getDefault().getSellRealFQ(row, GlobalInfo.sysPara.mktcode, saleHead.syjh, String.valueOf(saleHead.fphm)))
            {
                saleHead.memo = row[0] + "," + row[1] + ","+row[2];

                double faq = Convert.toDouble(row[0]);
                double fbq = Convert.toDouble(row[1]);
                AccessDayDB.getDefault().updateSaleJf(saleHead.fphm, 4, 0, 0,saleHead.memo);

                // 提示
                if ((Convert.toDouble(row[0]) != 0) || (Convert.toDouble(row[1]) != 0))
                {
                    StringBuffer sb = new StringBuffer();
                    sb.append("本笔交易存在扣券\n");
                    sb.append("返A券: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(faq), 0, 10, 10, 1) + "\n");
                    sb.append("返B券: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(fbq), 0, 10, 10, 1));
                    new MessageBox(sb.toString());
                }
            }
        }
    }
    
    public boolean FINDGRANT(String gh,String gz){
    	if (!GlobalInfo.isOnline) return false;
    	
    	Tcrc_NetService netservice = (Tcrc_NetService)(NetService.getDefault());
    	String[] iszk = netservice.FINDGRANT(gh, gz);
    	if (iszk!=null)
		{
			if(iszk[0].equals("Y")) return true;
			return false;
		}
		else
		{
			return false;
		}
    }
}
