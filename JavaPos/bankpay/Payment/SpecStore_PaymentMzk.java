package bankpay.Payment;

import com.efuture.javaPos.Device.BankTracker;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class SpecStore_PaymentMzk extends PaymentMzk
{
	public SpecStore_PaymentMzk()
	{

	}

	public SpecStore_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public SpecStore_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public int getAccountInputMode()
	{
		BankTracker.autoMSR();
		return super.getAccountInputMode();
	}
}
