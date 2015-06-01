package custom.localize.Szxw;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentChange;
import com.efuture.javaPos.Struct.SalePayDef;

public class Szxw_PaymentChange extends PaymentChange{
	public Szxw_PaymentChange(SaleBS sale) {
		super(sale);
		// TODO 自动生成构造函数存根
	}

	public boolean checkpay(SalePayDef sp) {
		if (sp.paycode.equals("DJQF")) return true;
		return false;
	}
}
