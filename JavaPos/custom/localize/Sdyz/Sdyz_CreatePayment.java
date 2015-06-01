package custom.localize.Sdyz;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Sdyz_CreatePayment extends CreatePayment
{
    public PaymentMzk getPaymentMzk()
    {
    	return new Sdyz_PaymentMzk();
    }
    
    public Payment createLocalizePayment(boolean flag,PayModeDef mode,SaleBS sale,SalePayDef pay,SaleHeadDef head)
    {	
        if (mode.type == '4')
        {
        	if (flag)
        	{
        		return new Sdyz_PaymentMzk(mode,sale);
        	}
        	else
        	{
        		return new Sdyz_PaymentMzk(pay,head);
        	}
        }
        else
        {
        	return super.createLocalizePayment(flag, mode, sale, pay, head);
        }
    }
}
