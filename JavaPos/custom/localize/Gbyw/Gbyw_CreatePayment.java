package custom.localize.Gbyw;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Gbyw_CreatePayment extends CreatePayment
{
	public Payment createLocalizePayment(boolean flag, PayModeDef mode, SaleBS sale, SalePayDef pay, SaleHeadDef head)
	{
		if (mode.code.trim().equals("0402"))
		{
			if (flag)
			{
				return new Gbyw_PaymentMzk(mode, sale);
			}
			else
			{
				return new Gbyw_PaymentMzk(pay, head);
			}
		}
		if (mode.code.trim().equals("0408"))
		{
			if (flag)
			{
				return new Gbyw_NewPaymentMzk(mode, sale);
			}
			else
			{
				return new Gbyw_NewPaymentMzk(pay, head);
			}
		}
		else if (mode.code.trim().equals("0403"))
		{
			if (flag)
			{
				return new Gbyw_PaymentCoin(mode, sale);
			}
			else
			{
				return new Gbyw_PaymentCoin(pay, head);
			}
		}
		else if (mode.code.trim().equals("0409")||mode.code.trim().equals("0407"))
		{
			if (flag)
			{
				return new Gbyw_PaymentBankMzk(mode, sale);
			}
			else
			{
				return new Gbyw_PaymentBankMzk(pay, head);
			}
		}
		else
		{
			return super.createLocalizePayment(flag, mode, sale, pay, head);
		}
	}
}
