package custom.localize.Hhdl;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Hhdl_CreatePayment extends CreatePayment
{
	public Payment createLocalizePayment(boolean flag, PayModeDef mode, SaleBS sale, SalePayDef pay, SaleHeadDef head)
	{
		if (mode.code.trim().equals("0508") || mode.code.trim().equals("0555"))
		{
			if (flag)
			{
				return new Hhdl_PaymentCoupon(mode, sale);
			}
			else
			{
				return new Hhdl_PaymentCoupon(pay, head);
			}
		}

		return super.createLocalizePayment(flag, mode, sale, pay, head);
	}
}
