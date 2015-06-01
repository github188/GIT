package custom.localize.Hzjb;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentMzkPaper;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bcrm.Bcrm_CreatePayment;


public class Hzjb_CreatePayment extends Bcrm_CreatePayment
{
	public boolean allowQuickInputMoney(PayModeDef pay)
	{
		if (pay.level > 1)
			return false;
		return super.allowQuickInputMoney(pay);
	}
	
	public boolean isPaymentFjk(String code)
	{
		if (code.trim().equals("0501") || code.trim().equals("0502") || code.trim().equals("0503") || code.trim().equals("0504") || code.trim().equals("0520"))
		{
			return true;
		}
		else
		{
			return super.isPaymentFjk(code);
		}
	}
		
    public Payment createLocalizePayment(boolean flag, PayModeDef mode, SaleBS sale, SalePayDef pay, SaleHeadDef head)
    {	
        if (mode.code.trim().equals("0501") || mode.code.trim().equals("0502") || mode.code.trim().equals("0503") || mode.code.trim().equals("0520"))
        {
            Hzjb_PaymentFjkPaper p = null;

            if (flag)
            {
                p = new Hzjb_PaymentFjkPaper(mode, sale);
            }
            else
            {
                p = new Hzjb_PaymentFjkPaper(pay, head);
            }
            return p;
        }
        else if (mode.code.trim().equals("0504"))
        {
            Hzjb_PaymentZZQ p = null;

            if (flag)
            {
                p = new Hzjb_PaymentZZQ(mode, sale);
            }
            else
            {
                p = new Hzjb_PaymentZZQ(pay, head);
            }
            return p;
        }
        else if (mode.isbank == 'Y') // 金卡工程,生成相应的金卡工程付款对象
        {
            if (flag)
            {
                return new Hzjb_PaymentBank(mode, sale);
            }
            else
            {
                return new Hzjb_PaymentBank(pay, head);
            }
        }
        else if (mode.code.equals("0401"))
        {
            if (flag)
            {
                return new PaymentMzkPaper(mode, sale);
            }
            else
            {
                return new PaymentMzkPaper(pay, head);
            }
        }
       /* else if (mode.code.equals("0402"))
        {
            if (flag)
            {
                return new Hzjb_LHPaymentMzk(mode, sale);
            }
            else
            {
                return new Hzjb_LHPaymentMzk(pay, head);
            }
        }*/
        else if (mode.code.equals("0402"))
        {
            if (flag)
            {
                return new Hzjb_JniICPaymentMzk(mode, sale);
            }
            else
            {
                return new Hzjb_JniICPaymentMzk(pay, head);
            }
        }
        else if (mode.type == '4')
        {
            if (flag)
            {
                return new Hzjb_PaymentMzk(mode, sale);
            }
            else
            {
                return new Hzjb_PaymentMzk(pay, head);
            }
        }
        else
        {
            return super.createLocalizePayment(flag, mode, sale, pay, head);
        }
    }

    public boolean isPaymentZZQ(String code)
    {
        if (code.trim().equals("0504"))
        {
            return true;
        }

        return false;
    }
}
