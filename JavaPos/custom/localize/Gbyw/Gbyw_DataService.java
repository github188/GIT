package custom.localize.Gbyw;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bstd.Bstd_DataService;
import custom.localize.Gbyw.Gbyw_MzkModule.RetInfoDef;

public class Gbyw_DataService extends Bstd_DataService
{
	public void getCustomerSellJf(SaleHeadDef saleHead, Vector saleGoods, Vector salePay)
	{
		String[] row = new String[4];

		if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
		{
			if (NetService.getDefault().getCustomerSellJf(row, GlobalInfo.sysPara.mktcode, saleHead.syjh, String.valueOf(saleHead.fphm), saleHead.hykh, saleHead.hytype))
			{
				saleHead.bcjf = Double.parseDouble(row[0]);
				saleHead.ljjf += Double.parseDouble(row[1]);
				saleHead.str5 = row[2];

				if (!sendHykJf(saleHead, saleGoods, salePay))
					new MessageBox("本笔积分同步失败");

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
				new MessageBox("计算本笔交易小票积分失败\n请到会员中心查询积分!");
			}
		}

	}

	public boolean sendHykJf(SaleHeadDef saleHead)
	{
		if (saleHead.bcjf == 0)
			return true;

		if (saleHead.num1 == -1)
		{
			String line = "05," + saleHead.str2 + "," + saleHead.ysje + "," + saleHead.bcjf + "," + saleHead.syjh + "," + ManipulateStr.PadLeft(String.valueOf(saleHead.fphm), 12, '0') + "," + GlobalInfo.sysPara.commMerchantId + "," + saleHead.zl;

			if (Gbyw_MzkVipModule.getDefault().initConnection())
			{
				line = Gbyw_MzkVipModule.getDefault().sendData(line);

				if (line != null && line.equals("0"))
					return true;
			}
			AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDHYKJF, GlobalInfo.balanceDate + "," + saleHead.fphm);
			return false;
		}
		else if (saleHead.num1 == 1)
		{
			RetInfoDef info = Gbyw_MzkModule.getDefault().sendScore(saleHead.djlb, saleHead.bcjf < 0 ? saleHead.bcjf * -1 : saleHead.bcjf);
			if (info == null)
			{
				AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDHYKJF, GlobalInfo.balanceDate + "," + saleHead.fphm);
				return false;
			}
			saleHead.ljjf = info.scoreYe;
		}
		return true;
	}
}
