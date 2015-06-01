package custom.localize.Bxmx;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Bxmx_CreatePayment extends CreatePayment
{
	public Payment createLocalizePayment(boolean flag, PayModeDef mode, SaleBS sale, SalePayDef pay, SaleHeadDef head)
	{
		if (mode.code.trim().equals("00"))
		{
			if (flag)
			{
				return new Bxmx_VirtualPayment(mode, sale);
			}
			else
			{
				return new Bxmx_VirtualPayment(pay, head);
			}
		}
		else if (mode.code.trim().equals("0402"))
		{
			if (flag)
			{
				return new Bxmx_GoodsOrCouponPaymentMzk(mode, sale);
			}
			else
			{
				return new Bxmx_GoodsOrCouponPaymentMzk(pay, head);
			}
		}

		return super.createLocalizePayment(flag, mode, sale, pay, head);
	}
}
