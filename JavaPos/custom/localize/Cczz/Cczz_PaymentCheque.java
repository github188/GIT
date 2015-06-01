package custom.localize.Cczz;

import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Cczz_PaymentCheque extends PaymentMzk {
	public Cczz_PaymentCheque()
	{
	}
	
	public Cczz_PaymentCheque(PayModeDef mode,SaleBS sale)
	{
		initPayment(mode,sale);
	}

	// 该构造函数用于红冲小票时,通过小票付款明细创建对象
	public Cczz_PaymentCheque(SalePayDef pay,SaleHeadDef head)
	{
		initPayment(pay,head);
	}
	
	protected boolean getPasswdBeforeFindMzk(StringBuffer passwd)
	{

    	TextBox txt = new TextBox();
    	
        if (!txt.open("请输入身份证号", "", "请输入身份证号", passwd, 0, 0,false, TextBox.AllInput))
        {
            return false;
        }
		
	    return true;
	}
}
