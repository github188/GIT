package custom.localize.Hmsl;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Hmsl_CreatePayment extends CreatePayment
{
	public Payment createLocalizePayment(boolean flag, PayModeDef mode, SaleBS sale, SalePayDef pay, SaleHeadDef head)
	{
		if (mode.code.trim().equals("04"))
		{
			if (flag)
			{
				return new Hmsl_PaymentMzk(mode, sale);
			}
			else
			{
				return new Hmsl_PaymentMzk(pay, head);
			}
		}

		return super.createLocalizePayment(flag, mode, sale, pay, head);
	}

	public PaymentMzk getPaymentMzk()
	{
		return new Hmsl_PaymentMzk();
	}
}
