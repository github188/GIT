package custom.localize.Jwyt;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Jwyt_CreatePayment extends CreatePayment
{
	public PaymentMzk getPaymentMzk()
	{
		return new Jwyt_PaymentMzk();
	}

	public Payment createLocalizePayment(boolean flag, PayModeDef mode, SaleBS sale, SalePayDef pay, SaleHeadDef head)
	{
		if (mode.code.trim().equals("08"))
		{
			if (flag)
			{
				return new Jwyt_PaymentMars(mode, sale);
			}
			else
			{
				return new Jwyt_PaymentMars(pay, head);
			}
		}

		else if (mode.code.trim().equals("0707"))
		{
			if (flag)
			{
				return new Jwyt_PaymentDeduct(mode, sale);
			}
			else
			{
				return new Jwyt_PaymentDeduct(pay, head);
			}
		}

		return super.createLocalizePayment(flag, mode, sale, pay, head);
	}
}
