package custom.localize.Cczz;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentFjk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bcrm.Bcrm_CreatePayment;


public class Cczz_CreatePayment extends Bcrm_CreatePayment
{
    public PaymentFjk getPaymentFjk()
    {
        return new Cczz_PaymentFjk();
    }

    public PaymentFjk getPaymentFjk(PayModeDef mode, SaleBS sale)
    {
        return new Cczz_PaymentFjk(mode, sale);
    }

    public PaymentFjk getPaymentFjk(SalePayDef pay, SaleHeadDef head)
    {
        return new Cczz_PaymentFjk(pay, head);
    }
    
    public Payment createLocalizePayment(boolean flag,PayModeDef mode,SaleBS sale,SalePayDef pay,SaleHeadDef head)
    {
		if (mode.type == '4')
		{
			if (flag)
			{
				return new Cczz_PaymentMzk(mode,sale);
			}
			else
			{
				return new Cczz_PaymentMzk(pay,head);
			}
		}
		else if (mode.isbank == 'Y') // 金卡工程,生成相应的金卡工程付款对象
        {
            if (flag)
            {
                return new Cczz_PaymentBank(mode, sale);
            }
            else
            {
                return new Cczz_PaymentBank(pay, head);
            }
        }
		else
		{
			return super.createLocalizePayment(flag, mode, sale, pay, head);
		}
    }
    
    public boolean allowQuickInputMoney(PayModeDef pay)
    {
		if (pay.code.trim().equals("0802"))
		{
			return false;
		}
		else
		{
			return super.allowQuickInputMoney(pay);
		}
    }
}
