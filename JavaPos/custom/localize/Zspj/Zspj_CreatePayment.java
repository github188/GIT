package custom.localize.Zspj;


import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Zsbh.Zsbh_WebServicePaymentMzk;
import custom.localize.Zspj.Zspj_PaymentMzk;

public class Zspj_CreatePayment extends CreatePayment
{

	public Payment createLocalizePayment(boolean flag, PayModeDef mode, SaleBS sale, SalePayDef pay, SaleHeadDef head)
	{
		if (mode.code.trim().equals("04"))
		{
			if (flag)
			{
				return new Zspj_PaymentMzk(mode, sale);
			}
			else
			{
				return new Zspj_PaymentMzk(pay, head);
			}
		}
		else if (mode.code.trim().equals("0401"))
		{
			if (flag)
			{
				return new Zspj_WebServicePaymentMzk(mode, sale);
			}
			else
			{
				return new Zspj_WebServicePaymentMzk(pay, head);
			}

		}
		else if (mode.code.trim().equals("0728"))
		{
			if (flag)
			{
				return new Zspj_PaymentCoupon(mode, sale);
			}
			else
			{
				return new Zspj_PaymentCoupon(pay, head);
			}
		}

		return super.createLocalizePayment(flag, mode, sale, pay, head);
	}

	public PaymentMzk getPaymentMzk()
	{
		return new Zspj_PaymentMzk();
	}
}
