package custom.localize.Agog;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bstd.Bstd_DataService;

public class Agog_DataService extends Bstd_DataService
{
	public void getCustomerSellJf(SaleHeadDef saleHead)
	{
		String[] row = new String[4];

		if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
		{
			if (NetService.getDefault().getCustomerSellJf(row, GlobalInfo.sysPara.mktcode, saleHead.syjh, String.valueOf(saleHead.fphm), saleHead.hykh, saleHead.hytype))
			{
				saleHead.bcjf = Convert.toDouble(row[0]);	
				saleHead.ljjf = ManipulatePrecision.doubleConvert(saleHead.ljjf+saleHead.bcjf, 2, 1);
				saleHead.str5 = row[2];

				if (GlobalInfo.sysPara.sendhyjf == 'Y')
				{
					if (!sendHykJf(saleHead))
					{
						new MessageBox("本笔积分同步失败无法获得累计积分\n请到会员中心查询累计积分!");
					}
				}

				if (Math.abs(saleHead.bcjf) > 0 || Math.abs(saleHead.ljjf) > 0)
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
				new MessageBox("计算本笔交易小票积分失败\n请到会员中心查询积分!");
			}
		}
	}

	public boolean sendHykJf(SaleHeadDef saleHead)
	{
		if (saleHead != null)
		{
			if (Agog_VipCaller.getDefault().sendVipScore(saleHead))
				return true;

			AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDHYKJF, GlobalInfo.balanceDate + "," + saleHead.fphm);

			return false;
		}

		return true;
	}
}
