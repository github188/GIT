package custom.localize.Bcrm;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bhls.Bhls_PaymentMzk;

public class Bcrm_PaymentMzk extends Bhls_PaymentMzk {
	public Bcrm_PaymentMzk()
	{
		super();
	}
	
	public Bcrm_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		super(mode,sale);
	}
	
	public Bcrm_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		super(pay,head);
	}
	
	public String getDisplayCardno()
	{
		return mzkret.cardno;
	}
}
