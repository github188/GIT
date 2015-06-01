package custom.localize.Njxb;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Cmls.Cmls_DataService;

public class Njxb_DataService extends Cmls_DataService
{
    // 获取小票实时积分
    public void getCustomerSellJf(SaleHeadDef saleHead)
    {
        String[] row = new String[5];

        if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
        {
            if (NetService.getDefault().getCustomerSellJf(row, saleHead.mkt, saleHead.syjh, String.valueOf(saleHead.fphm)))
            {
                saleHead.bcjf = Convert.toDouble(row[0]);
                saleHead.ljjf = Convert.toDouble(row[1]);
                saleHead.num4 = Convert.toDouble(row[3]);
                saleHead.memo = row[4];
                saleHead.str5 = row[2];

                AccessDayDB.getDefault().updateSaleJf(saleHead.fphm, 1, saleHead.bcjf, saleHead.ljjf,String.valueOf(saleHead.num4));
                AccessDayDB.getDefault().updateSaleHeadStr(saleHead.fphm,"str5",saleHead.str5);
                AccessDayDB.getDefault().updateSaleHeadStr(saleHead.fphm,"memo",saleHead.memo);
                
                StringBuffer sb = new StringBuffer();
                sb.append("累计积分: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(saleHead.ljjf), 0, 10, 10, 1) + "\n");
                sb.append("本次积分: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(saleHead.bcjf), 0, 10, 10, 1) + "\n");
                sb.append("倍享积分: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(saleHead.num4), 0, 10, 10, 1) + "\n");
                sb.append(saleHead.memo);
                new MessageBox(sb.toString());
                
            }
            else
            {
                saleHead.bcjf = 0;
                new MessageBox("计算本笔交易小票积分失败\n请到会员中心查询积分!");
            }
          
        }
    }
}
