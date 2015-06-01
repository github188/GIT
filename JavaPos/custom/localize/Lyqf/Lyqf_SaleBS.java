package custom.localize.Lyqf;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bstd.Bstd_SaleBS;

public class Lyqf_SaleBS extends Bstd_SaleBS
{
	protected double leave = 0.0;// ManipulatePrecision.doubleConvert(GlobalInfo.sysPara.scoreAmountLimit
									// - curCustomer.num3, 2, 1);

	public void doSaleFinshed(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		if (curCustomer != null)
		{
			double money = ManipulatePrecision.doubleConvert(curCustomer.value3 + saleHead.ysje, 2, 1);
			if (!((Lyqf_NetService) (NetService.getDefault())).sendCustTotalAmount(saleHead, money))
			{
				AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票,金额:" + saleHead.ysje + ",同步会员累计金额失败", StatusType.WORK_SENDERROR);
				new MessageBox("同步会员累计金额失败!");
			}
		}
	}

	public void afterInitPay()
	{
		if (curCustomer != null)
		{
			leave = ManipulatePrecision.doubleConvert(GlobalInfo.sysPara.scoreAmountLimit - curCustomer.value3, 2, 1);
			new MessageBox("该卡当月已累计消费" + curCustomer.value3 + "\n\n剩余可消费金额:" + String.valueOf(leave));
		}

		super.afterInitPay();
	}

	public boolean payAccount(PayModeDef mode, String money)
	{
		if (curCustomer != null)
		{
			if (GlobalInfo.sysPara.scoreAmountLimit <= 0)
			{
				new MessageBox("后台参数未定义该卡当月可用金额!");
				return false;
			}

			if (Convert.toDouble(money) > leave)
			{
				new MessageBox("当前金额已超过该卡剩余可消费金额!");
				return false;
			}

			if (getCurrentTotalPay() + Convert.toDouble(money) > leave)
			{
				new MessageBox("当前已付款总额超过该卡剩余可消费金额,请撤销后重新交易!");
				return false;
			}
		}

		return super.payAccount(mode, money);
	}

	public double getCurrentTotalPay()
	{
		double money = 0.0;

		if (salePayment.size() == 0)
			return money;

		for (int i = 0; i < salePayment.size(); i++)
		{
			SalePayDef spd = (SalePayDef) salePayment.get(i);
			money = money + spd.je - spd.num1;
		}

		return money;
	}
}
