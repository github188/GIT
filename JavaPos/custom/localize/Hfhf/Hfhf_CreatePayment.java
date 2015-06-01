package custom.localize.Hfhf;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Hfhf_CreatePayment extends CreatePayment
{

	public Payment createLocalizePayment(boolean flag, PayModeDef mode, SaleBS sale, SalePayDef pay, SaleHeadDef head)
	{
		if (mode.code.trim().equals("0402"))
		{
			if (flag)
			{
				return new Hfhf_PaymentElecMoney(mode, sale);
			}
			else
			{
				return new Hfhf_PaymentElecMoney(pay, head);
			}
		}
		else if (mode.code.trim().equals("0403"))
		{
			if (flag)
			{
				return new Hfhf_PaymentScore(mode, sale);
			}
			else
			{
				return new Hfhf_PaymentScore(pay, head);
			}
		}
		else if (mode.code.trim().equals("0404"))
		{
			if (flag)
			{
				return new Hfhf_PaymentCoinPurse(mode, sale);
			}
			else
			{
				return new Hfhf_PaymentCoinPurse(pay, head);
			}
		}

		return super.createLocalizePayment(flag, mode, sale, pay, head);
	}

}
