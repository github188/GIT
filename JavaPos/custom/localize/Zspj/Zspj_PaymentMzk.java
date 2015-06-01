package custom.localize.Zspj;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Zsbh.Zsbh_PaymentMzk;

public class Zspj_PaymentMzk extends Zsbh_PaymentMzk
{
    public Zspj_PaymentMzk()
    {
	super();
    }
    
    public Zspj_PaymentMzk(PayModeDef mode, SaleBS sale)
    {
	super(mode, sale);
    }
    
    public Zspj_PaymentMzk(SalePayDef pay, SaleHeadDef head)
    {
	super(pay, head);
    }
}
