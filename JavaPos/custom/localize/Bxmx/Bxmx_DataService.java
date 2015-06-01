package custom.localize.Bxmx;

import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bstd.Bstd_DataService;

public class Bxmx_DataService extends Bstd_DataService
{
	public boolean sendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Sqldb sql)
	{
		if (!GlobalInfo.isOnline)
			return false;

		boolean again;

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
			if (SellType.ISPREPARETAKE(saleHead.djlb))
			{
				AccessDayDB.getDefault().updateSaleBz(GlobalInfo.syjStatus.fphm-1, 1, 'Y');
			}
			else
			{
				AccessDayDB.getDefault().updateSaleBz(saleHead.fphm, 1, 'Y');
			}
		}
		else
		{
			// 重发未送网小票时，不能用sql的execute(sqltext)方法
			// 和前面selectData换一个对象执行,否则冲突
			// 更新小票送网标志
			sql.setSql("update SALEHEAD set netbz = 'Y' where syjh = '" + saleHead.syjh + "' and fphm = " + String.valueOf(saleHead.fphm));
			sql.executeSql();
		}

		// 预收订金，则直接返回
		if (SellType.ISEARNEST(saleHead.djlb))
			return true;

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

		// 需要联网实时计算积分
		if (GlobalInfo.sysPara.calcjfbyconnect == 'Y' || GlobalInfo.sysPara.calcjfbyconnect == 'A')
		{
			getCustomerSellJf(saleHead);
		}

		return true;
	}
}
