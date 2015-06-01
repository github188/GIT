package custom.localize.Wqls;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentFjk;
import com.efuture.javaPos.Payment.Bank.Bjyd_PaymentBankFunc;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bhls.Bhls_CreatePayment;

public class Wqls_CreatePayment extends Bhls_CreatePayment 
{
	public PaymentBankFunc getPaymentBankFunc()
	{
		PaymentBankFunc bank = getConfigBankFunc();
		
    	if (bank != null)
    	{      
    		return bank;
    	}
    	else
    	{
    		return new Bjyd_PaymentBankFunc();
    	}		
	}
	
    public PaymentFjk getPaymentFjk()
    {
        return new Wqls_PaymentFjk();
    }
    
    public Payment createLocalizePayment(boolean flag,PayModeDef mode,SaleBS sale,SalePayDef pay,SaleHeadDef head)
    {
		if (CreatePayment.getDefault().isPaymentFjk(mode.code))
		{
			if (flag)
			{
				return new Wqls_PaymentFjk(mode,sale);
			}
			else
			{
				return new Wqls_PaymentFjk(pay,head);
			}
		}
		else
		{
			return super.createLocalizePayment(flag, mode, sale, pay, head);
		}
    }
}
