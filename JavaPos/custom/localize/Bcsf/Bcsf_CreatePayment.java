package custom.localize.Bcsf;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Bcsf_CreatePayment extends CreatePayment
{
	public Payment createLocalizePayment(boolean flag, PayModeDef mode, SaleBS sale, SalePayDef pay, SaleHeadDef head)
	{
		if (mode.code.trim().equals("0108"))
		{
			if (flag)
			{
				return new Bcsf_PaymentStamp(mode, sale);
			}
			else
			{
				return new Bcsf_PaymentStamp(pay, head);
			}
		}
		return super.createLocalizePayment(flag, mode, sale, pay, head);
	}

}
