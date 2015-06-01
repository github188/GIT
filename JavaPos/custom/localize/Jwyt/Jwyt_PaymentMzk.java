package custom.localize.Jwyt;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.SalePayDef;

public class Jwyt_PaymentMzk extends PaymentMzk
{
	protected String getDisplayStatusInfo()
	{
		Jwyt_SaleBS.autoMSR();
		return super.getDisplayAccountInfo();
	}

	public int getAccountInputMode()
	{
		Jwyt_SaleBS.autoMSR();
		return super.getAccountInputMode();
	}

	public boolean findMzk(String track1, String track2, String track3)
	{
		boolean flag = super.findMzk(track1, track2, track3);

		if (!flag)
			Jwyt_SaleBS.autoMSR();

		return flag;
	}

	protected boolean checkMoneyValid(String money, double ye)
	{
		try
		{
			if (saleBS.isSpecifyTicketBack() && GlobalInfo.sysPara.isctrlthpay == 'Y')
			{
				if (saleBS.backPayment != null && saleBS.backPayment.size() > 0 && !money.equals(""))
				{
					for (int i = 0; i < saleBS.backPayment.size(); i++)
					{
						SalePayDef tmpPay = (SalePayDef) saleBS.backPayment.get(i);
						if (super.mzkret.cardno.equals(tmpPay.payno) && ManipulatePrecision.doubleConvert(Double.parseDouble(money)) == ManipulatePrecision.doubleConvert(tmpPay.je))
							return super.checkMoneyValid(money, ye);
					}
				}
				new MessageBox("请核对当前[储值卡号/金额]是否与原始小票付款一致");
				return false;
			}
			return super.checkMoneyValid(money, ye);
		}
		catch (Exception ex)
		{
			new MessageBox(ex.getMessage());
			return false;
		}
	}
}
