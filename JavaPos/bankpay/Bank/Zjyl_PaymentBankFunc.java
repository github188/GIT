package bankpay.Bank;

import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

public class Zjyl_PaymentBankFunc extends PaymentBankFunc
{
	 
    public void readBankClassConfig(String classname)
    {
    	super.readBankClassConfig("PaymentBankFunc_ZJYL");
    }
    
    protected boolean XYKReadResult()
    {
    	if (super.XYKReadResult())
    	{
    		bld.memo = bld.tempstr + "," + bld.tempstr1 + "," + paycode;
    		return true;
    	}
    	
    	return false;
    }
    
}
