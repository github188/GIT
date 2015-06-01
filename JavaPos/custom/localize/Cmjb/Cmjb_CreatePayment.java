package custom.localize.Cmjb;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentCustJfSale;
import com.efuture.javaPos.Payment.PaymentCustLczc;
import com.efuture.javaPos.Payment.PaymentMzkPaper;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bcrm.Bcrm_CreatePayment;
import custom.localize.Bcrm.Bcrm_PaymentMzk;


public class Cmjb_CreatePayment extends Bcrm_CreatePayment
{
	public Payment createLocalizePayment(boolean flag,PayModeDef mode,SaleBS sale,SalePayDef pay,SaleHeadDef head)
    {	
		Payment payment = createCustomPayment(flag, mode, sale, pay, head);
		if (payment != null)
		{
			return payment;
		}
		else if (mode.type == '4')
		{
			if (flag)
			{
				return new Bcrm_PaymentMzk(mode,sale);
			}
			else
			{
				return new Bcrm_PaymentMzk(pay,head);
			}
		}
		else if (mode.code.trim().equals("0111"))
		{
			if (flag)
        	{
        		return new PaymentCustLczc(mode,sale);
        	}
        	else
        	{
        		return new PaymentCustLczc(pay,head);
        	}
		}
		else if (mode.code.trim().equals("0508"))
		{
			if (flag)
        	{
        		return new Cmjb_PaymentCustJfSale(mode,sale);
        	}
        	else
        	{
        		return new Cmjb_PaymentCustJfSale(pay,head);
        	}
		}
		else
		{
			return super.createLocalizePayment(flag, mode, sale, pay, head);
		}
    }
}
