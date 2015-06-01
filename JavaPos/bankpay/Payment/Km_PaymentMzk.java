package bankpay.Payment;

import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;

public class Km_PaymentMzk extends PaymentMzk
{
	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{	
		return DataService.getDefault().sendDzqSale(req, ret);
		
	}
}
