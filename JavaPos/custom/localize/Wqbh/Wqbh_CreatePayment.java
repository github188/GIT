package custom.localize.Wqbh;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentCustJfSale;
import com.efuture.javaPos.Payment.PaymentCustLczc;
import com.efuture.javaPos.Payment.PaymentFjk;
import com.efuture.javaPos.Payment.Bank.Bjyd_PaymentBankFunc;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bcrm.Bcrm_CreatePayment;

public class Wqbh_CreatePayment extends Bcrm_CreatePayment
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
		return new Wqbh_PaymentFjk();
	}
	
	public Payment createLocalizePayment(boolean flag,PayModeDef mode,SaleBS sale,SalePayDef pay,SaleHeadDef head)
    {	
		if (mode.type == '4')
		{
			if (flag)
			{
				return new Wqbh_PaymentMzk(mode,sale);
			}
			else
			{
				return new Wqbh_PaymentMzk(pay,head);
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
        		return new PaymentCustJfSale(mode,sale);
        	}
        	else
        	{
        		return new PaymentCustJfSale(pay,head);
        	}
		}
		else
		{
			return super.createLocalizePayment(flag, mode, sale, pay, head);
		}
    }
}
