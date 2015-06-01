package custom.localize.Hfhf;

import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bstd.Bstd_DataService;

public class Hfhf_DataService extends Bstd_DataService
{
	public boolean sendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Sqldb sql)
	{
		if (!GlobalInfo.isOnline)
			return false;

		boolean again = false;

		// 送网小票返回数据
		Vector retValue = new Vector();

		// sql对象为空,非重发小票
		if (sql == null)
		{
			again = false;
		}
		else
		{
			again = true;
		}

		// 发送小票
		int result = 0;
		result = NetService.getDefault().sendSaleData(saleHead, saleGoods, salePayment, retValue);

		// 非重发如果返回不为0，表示小票发送失败
		if (!again && result != 0)
			return false;

		// 重发小票，如果返回为2表示小票已存在，0表示成功，其他为送网失败
		if (again && result != 0 && result != 2)
			return false;

		// 得到返回数据,可对返回数据进行处理
		if (retValue.size() > 0)
		{
			String memo = retValue.elementAt(0).toString();
			double value = Double.parseDouble(CommonMethod.isNull(retValue.elementAt(1).toString(), "0"));

			updateSendSaleData(saleHead, memo, value, sql);
		}

		// 发送小票成功后更新小票送网标志
		if (sql == null)
		{
			// 更新小票送网标志
			AccessDayDB.getDefault().updateSaleBz(saleHead.fphm, 1, 'Y');
		}
		else
		{
			// 重发未送网小票时，不能用sql的execute(sqltext)方法
			// 和前面selectData换一个对象执行,否则冲突
			// 更新小票送网标志
			sql.setSql("update SALEHEAD set netbz = 'Y' where syjh = '" + saleHead.syjh + "' and fphm = " + String.valueOf(saleHead.fphm));
			sql.executeSql();
		}

		// 需要将小票发送到独立会员服务器
		if (GlobalInfo.sysPara.sendsaletocrm == 'Y')
		{
			sendSaleDataToMemberDB(saleHead, saleGoods, salePayment, again);
		}

		// 需要联网实时计算返券
		if (GlobalInfo.sysPara.calcfqbyreal == 'Y')
		{
			getSellRealFQ(saleHead);
		}

		getCustomerSellJf(saleHead, saleGoods, salePayment, again);

		return true;

	}

	public void getCustomerSellJf(SaleHeadDef saleHead, Vector saleGoods, Vector salePay, boolean agin)
	{
		String[] row = new String[4];

		if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
		{
			// 白卡不积分
			if (saleHead.hytype != null && saleHead.hytype.equals("blank"))
				return;

			if (NetService.getDefault().getCustomerSellJf(row, GlobalInfo.sysPara.mktcode, saleHead.syjh, String.valueOf(saleHead.fphm), saleHead.hykh, saleHead.hytype))
			{
				saleHead.bcjf = Double.parseDouble(row[0]);
				saleHead.ljjf = Double.parseDouble(row[1]);
				saleHead.str5 = row[2];

				if (!sendHykJf(saleHead, saleGoods, salePay, agin))
					new MessageBox("本笔积分同步到CRM失败");

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

	public boolean sendHykJf(SaleHeadDef saleHead, Vector saleGoods, Vector salePay, boolean again)
	{
		if (saleHead.hykh.length() > 3 && saleHead.hykh.startsWith("szd"))
		{
			if (!Hfhf_CrmModule.getDefault().saveVipPoint(saleHead, saleGoods, again))
			{
				AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDHYKJF, GlobalInfo.balanceDate + "," + saleHead.fphm);
				return false;
			}
		}
		return true;
	}
}
