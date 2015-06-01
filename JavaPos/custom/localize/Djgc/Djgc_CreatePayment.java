package custom.localize.Djgc;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bcrm.Bcrm_CreatePayment;

public class Djgc_CreatePayment extends Bcrm_CreatePayment
{
	public Payment createLocalizePayment(boolean flag,PayModeDef mode,SaleBS sale,SalePayDef pay,SaleHeadDef head)
    {	
		if (mode.code.trim().equals("0111"))
		{
			if (flag)
        	{
        		return new Djgc_PaymentCustLczc(mode,sale);
        	}
        	else
        	{
        		return new Djgc_PaymentCustLczc(pay,head);
        	}
		}
		else
		{
			return super.createLocalizePayment(flag, mode, sale, pay, head);
		}
    }
}
