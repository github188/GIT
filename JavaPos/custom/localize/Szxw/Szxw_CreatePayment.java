package custom.localize.Szxw;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentChange;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bhls.Bhls_CreatePayment;

public class Szxw_CreatePayment extends Bhls_CreatePayment
{
	public PaymentMzk getPaymentMzk()
	{
		return new Szxw_PaymentMzk();
	}

	public Payment createLocalizePayment(boolean flag, PayModeDef mode, SaleBS sale, SalePayDef pay, SaleHeadDef head)
	{
		if (mode.type == '4')
		{
			if (flag)
			{
				return new Szxw_PaymentMzk(mode, sale);
			}
			else
			{
				return new Szxw_PaymentMzk(pay, head);
			}
		}
		else
		{
			return super.createLocalizePayment(flag, mode, sale, pay, head);
		}
	}
	 public PaymentChange getPaymentChange(SaleBS sale)
	    {
	        return new Szxw_PaymentChange(sale);
	    }
}
