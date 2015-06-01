package custom.localize.Ajbs;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bstd.Bstd_DataService;

public class Ajbs_DataService extends Bstd_DataService
{
	public boolean getCustomer(CustomerDef cust, String track)
	{
		return Ajbs_ICCard.getDefault().getCustomer(cust);
	}

	// 获取小票实时积分
	public void getCustomerSellJf(SaleHeadDef saleHead)
	{
		String[] row = new String[4];

		if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
		{
			if (NetService.getDefault().getCustomerSellJf(row, GlobalInfo.sysPara.mktcode, saleHead.syjh, String.valueOf(saleHead.fphm), saleHead.hykh, saleHead.hytype))
			{
				if (Ajbs_ICCard.getDefault().baoYuanJF(saleHead, Double.parseDouble(row[0]), Double.parseDouble(row[1])))
				{
					saleHead.bcjf = Double.parseDouble(row[0]);
					saleHead.ljjf = Double.parseDouble(row[1]);
					saleHead.str5 = row[2];

					if ((Math.abs(saleHead.bcjf) > 0 || Math.abs(saleHead.ljjf) > 0) && GlobalInfo.sysPara.calcjfbyconnect == 'Y')
					{
						StringBuffer sb = new StringBuffer();
						sb.append("本笔交易存在积分\n");
						sb.append("本次积分: " + Convert.appendStringSize("", String.valueOf(saleHead.bcjf), 0, 10, 10, 1) + "\n");
						sb.append("累计积分: " + Convert.appendStringSize("", String.valueOf(saleHead.ljjf), 0, 10, 10, 1));

 						new MessageBox(sb.toString());
					}

					AccessDayDB.getDefault().updateSaleJf(saleHead.fphm, 1, saleHead.bcjf, saleHead.ljjf);
				}
				else
				{
					saleHead.bcjf = 0;
					new MessageBox("积分充值失败\n请拿小票到会员中心进行积分补录!");
				}
			}
			else
			{
				saleHead.bcjf = 0;
				new MessageBox("计算本笔交易小票积分失败\n请拿小票到会员中心进行积分补录!");
			}
		}
	}
}
