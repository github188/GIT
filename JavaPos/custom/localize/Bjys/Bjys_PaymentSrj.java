package custom.localize.Bjys;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Bjys_PaymentSrj extends Payment
{
	public Bjys_PaymentSrj()
	{
		super();
	}
	
	public Bjys_PaymentSrj(PayModeDef mode,SaleBS sale)
	{
		initPayment(mode,sale);
	}
	
	public Bjys_PaymentSrj(SalePayDef pay,SaleHeadDef head)
	{
		initPayment(pay,head);
	}
	
	protected boolean checkMoneyValid(String money,double ye)
	{	
		if ((Convert.toDouble(money)%50)!= 0)
		{
			new MessageBox(paymode.name + "付款金额必须是50元的倍数!");
			return false;
		}
		
		if (Convert.toDouble(money) - ye >= 50)
		{
			new MessageBox(paymode.name + "溢余金额不允许超过50元!");
			return false;
		}
		
		return super.checkMoneyValid(money, ye);
	}
}
