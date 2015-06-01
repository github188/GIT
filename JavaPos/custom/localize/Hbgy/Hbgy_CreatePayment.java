package custom.localize.Hbgy;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Hbgy_CreatePayment extends CreatePayment
{
	public Payment createLocalizePayment(boolean flag, PayModeDef mode, SaleBS sale, SalePayDef pay, SaleHeadDef head)
	{
		if (mode.code.trim().equals("0402"))
		{
			if (flag)
			{
				return new Hbgy_HMKPayment(mode, sale);
			}
			else
			{
				return new Hbgy_HMKPayment(pay, head);
			}
		}
		return super.createLocalizePayment(flag, mode, sale, pay, head);
	}

}
