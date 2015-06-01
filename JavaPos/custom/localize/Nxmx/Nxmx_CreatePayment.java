package custom.localize.Nxmx;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Payment.CreatePayment;

public class Nxmx_CreatePayment extends CreatePayment
{
	public Payment createLocalizePayment(boolean flag, PayModeDef mode, SaleBS sale, SalePayDef pay, SaleHeadDef head)
	{
		if (mode.code.trim().equals("0302") || mode.code.trim().equals("0303"))
		{
			if (flag)
			{
				return new Nxmx_PaymentCoupon(mode, sale);
			}
			else
			{
				return new Nxmx_PaymentCoupon(pay, head);
			}
		}
		else
		{
			return super.createLocalizePayment(flag, mode, sale, pay, head);
		}
	}
}
