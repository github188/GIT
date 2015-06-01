package custom.localize.Gzbh;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Gzbh_CreatePayment extends CreatePayment
{
	// 是否允许直接在金额款输入付款金额
	public boolean allowQuickInputMoney(PayModeDef pay)
	{
		if (pay.code.trim().equals("0022"))
		{
			return false;
		}
		else
		{
			return super.allowQuickInputMoney(pay);
		}
	}

	public Payment createLocalizePayment(boolean flag, PayModeDef mode, SaleBS sale, SalePayDef pay, SaleHeadDef head)
	{
		if (mode.code.trim().equals("0022"))
		{
			if (flag)
			{
				return new Gzbh_PaymentDzq(mode, sale);
			}
			else
			{
				return new Gzbh_PaymentDzq(pay, head);
			}
		}
		if (mode.type == '4')
		{
			if (flag)
			{
				return new Gzbh_PaymentMzk(mode, sale);
			}
			else
			{
				return new Gzbh_PaymentMzk(pay, head);
			}
		}
		else
		{
			return super.createLocalizePayment(flag, mode, sale, pay, head);
		}
	}

	public boolean sendAllPaymentCz()
	{
		if (!super.sendAllPaymentCz()) return false;

		// 发送电子券记账冲正
		if (!new Gzbh_PaymentDzq().sendAccountCz()) { return false; }

		return true;
	}

	public PaymentMzk getPaymentMzk()
	{
		return new Gzbh_PaymentMzk();
	}
}
