package custom.localize.Wjjy;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bhls.Bhls_CreatePayment;

public class Wjjy_CreatePayment extends Bhls_CreatePayment 
{
	public PaymentMzk getPaymentMzk()
	{
		return new Wjjy_PaymentMzk();
	}
	
	//是否允许直接在金额款输入付款金额
	public boolean allowQuickInputMoney(PayModeDef pay)
	{
		if (pay.code.trim().equals("0508"))
		{
			return false;
		}
		else
		{
			return super.allowQuickInputMoney(pay);
		}
	}
	
	public Payment createLocalizePayment(boolean flag,PayModeDef mode,SaleBS sale,SalePayDef pay,SaleHeadDef head)
    {	
		if (mode.code.equals("0508")) // 0508 调用面值卡界面
		{
			if (flag)
			{
				return new Wjjy_PaymentMzk(mode,sale);
			}
			else
			{
				return new Wjjy_PaymentMzk(pay,head);
			}
		}
		else if (mode.type == '4')
		{
			if (flag)
			{
				return new Wjjy_PaymentMzk(mode,sale);
			}
			else
			{
				return new Wjjy_PaymentMzk(pay,head);
			}
		}
		else 
		{
			return super.createLocalizePayment(flag, mode, sale, pay, head);
		}
    }
}
