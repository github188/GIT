package custom.localize.Lrtd;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bstd.Bstd_DataService;

public class Lrtd_DataService extends Bstd_DataService
{
	public void getCustomerSellJf(SaleHeadDef saleHead)
	{
		String[] row = new String[4];

		if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
		{
			if (NetService.getDefault().getCustomerSellJf(row, GlobalInfo.sysPara.mktcode, saleHead.syjh, String.valueOf(saleHead.fphm), saleHead.hykh, saleHead.hytype))
			{
				saleHead.bcjf = Double.parseDouble(row[0]);
				saleHead.ljjf = Double.parseDouble(row[1]);
				saleHead.str5 = row[2];

				if (GlobalInfo.sysPara.sendhyjf == 'Y')
				{
					if (!sendHykJf(saleHead))
					{
						new MessageBox(Language.apply("本笔积分同步失败无法获得累计积分\n请到会员中心查询累计积分!"));
					}
				}

				if ((Math.abs(saleHead.bcjf) > 0 || Math.abs(saleHead.ljjf) > 0) && GlobalInfo.sysPara.calcjfbyconnect == 'Y')
				{
					StringBuffer sb = new StringBuffer();
					sb.append(Language.apply("本笔交易存在积分\n"));
					sb.append(Language.apply("本次积分: ") + Convert.appendStringSize("", String.valueOf(saleHead.bcjf+saleHead.num10), 0, 10, 10, 1) + "\n");
					sb.append(Language.apply("累计积分: ") + Convert.appendStringSize("", String.valueOf(saleHead.ljjf), 0, 10, 10, 1));
					
					new MessageBox(sb.toString());
				}

				AccessDayDB.getDefault().updateSaleJf(saleHead.fphm, 1, saleHead.bcjf, saleHead.ljjf);
			}
			else
			{
				saleHead.bcjf = 0;
				new MessageBox(Language.apply("计算本笔交易小票积分失败\n请到会员中心查询积分!"));
			}
		}
	}

}
