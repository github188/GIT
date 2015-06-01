package custom.localize.Zsbh;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import custom.localize.Zsbh.Zsbh_NetService;
import custom.localize.Bcrm.Bcrm_DataService;

public class Zsbh_DataService extends Bcrm_DataService
{
	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
		return NetService.getDefault().sendMzkSale(req, ret);
	}

	public int doRefundExtendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Vector retValue)
	{
		return ((Zsbh_NetService) NetService.getDefault()).sendRefundSaleData(saleHead, saleGoods, salePayment, retValue);
	}

	// 发送小票到CRM
	public void sendSaleDataToMemberDB(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, boolean again)
	{
		boolean sendok = true;

		int result = NetService.getDefault().sendExtendSaleData(saleHead, saleGoods, salePayment, null);

		// 非重发如果返回不为0，表示小票发送失败
		if (!again && result != 0)
			sendok = false;

		// 重发小票，如果返回为2表示小票已存在，0表示成功，其他为送网失败
		if (again && result != 0 && result != 2)
			sendok = false;

		// 送网失败，记录小票未发送到CRM数据源的命令任务
		if (!sendok)
		{
			if (again)
			{
				if (((Zsbh_AccessDayDB) AccessDayDB.getDefault()).getCrmResendFlag(saleHead.fphm))
					return;
			}

			AccessDayDB.getDefault().updateSaleBz(saleHead.fphm, 4, 'N');
			AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDINVTOEXTEND, GlobalInfo.balanceDate + "," + saleHead.fphm);

			// 提示
			if (!NetService.getDefault().isStopService())
				new MessageBox("上传小票到会员服务器失败\n请去会员中心查询!");

		}
		else
			AccessDayDB.getDefault().updateSaleBz(saleHead.fphm, 4, 'Y');
	}

	public void getCustomerSellJf(SaleHeadDef saleHead)
	{
		String[] row = new String[4];

		if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
		{
			if (NetService.getDefault().getCustomerSellJf(row, GlobalInfo.sysPara.mktcode, saleHead.syjh, String.valueOf(saleHead.fphm), saleHead.hykh, saleHead.hytype))
			{
				saleHead.bcjf = Double.parseDouble(row[0]);
				saleHead.ljjf = Double.parseDouble(row[1]);
				saleHead.num4 = Convert.toDouble(row[3]);
				saleHead.str5 = row[2];

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
					sb.append("倍享积分: " + Convert.appendStringSize("", String.valueOf(saleHead.num4), 0, 10, 10, 1) + "\n");
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

	public boolean getNetSysPara()
	{
		if (GlobalInfo.isOnline)
		{
			if (!NetService.getDefault().getSysPara())
				return false;
		}

		// 读取获得的参数为获取CRM参数作准备
		if (!AccessLocalDB.getDefault().readSysPara(false))
			return false;

		if (GlobalInfo.isOnline)
		{
			// 获取CRM参数
			Zsbh_NetService netService = (Zsbh_NetService) NetService.getDefault();
			if (!netService.getCrmSysPara(NetService.getDefault().getMemCardHttp(CmdDef.GETSYSPARAFORZSBH), CmdDef.GETSYSPARAFORZSBH))
				return false;
		}

		return AccessLocalDB.getDefault().readSysPara();
	}
}
