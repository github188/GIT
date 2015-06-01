package custom.localize.Hycs;


import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bstd.Bstd_DataService;

public class Hycs_DataService extends Bstd_DataService
{
	public boolean getCustomer(CustomerDef cust, String track)
	{
		return Hycs_common.getDefault().getCustomer(cust,track);
	}

	public void getCustomerSellJf(SaleHeadDef saleHead)
	{
		String[] row = new String[4];

		if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
		{
			if (NetService.getDefault().getCustomerSellJf(row, GlobalInfo.sysPara.mktcode, saleHead.syjh, String.valueOf(saleHead.fphm), saleHead.hykh, saleHead.hytype))
			{
				//积分为0时，不上传
				if(Convert.toDouble(row[0]) == 0.0)
				{
					return ;
				}
				if (Hycs_common.getDefault().UpdateVipCent(saleHead, Convert.toDouble(row[0])))
				{

					if (GlobalInfo.sysPara.sendhyjf == 'Y')
					{
						if (!sendHykJf(saleHead))
						{
							new MessageBox("本笔积分同步失败无法获得累计积分\n请到会员中心查询累计积分!");
						}
					}

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
					new MessageBox("Crm同步本笔小票积分失败\n请到会员中心查询积分!");
				}
			}
			else
			{
				saleHead.bcjf = 0;
				new MessageBox("计算本笔交易小票积分失败\n请到会员中心查询积分!");
			}
		}
	}
	
}
