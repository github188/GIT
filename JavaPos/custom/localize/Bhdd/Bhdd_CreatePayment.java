package custom.localize.Bhdd;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bhls.Bhls_CreatePayment;

public class Bhdd_CreatePayment extends Bhls_CreatePayment 
{
    //是否允许直接在金额款输入付款金额
    public boolean allowQuickInputMoney(PayModeDef pay)
    {
        //在礼券类 除电子券以外 其余
        if (pay.code.trim().equals("0508"))
        {
            return true;
        }
        else
        {
            return super.allowQuickInputMoney(pay);
        }
    }

    public Payment createLocalizePayment(boolean flag, PayModeDef mode, SaleBS sale, SalePayDef pay, SaleHeadDef head)
    {
        if (mode.code.equals("0508"))
        {
            if (flag)
            {
                return new Bhdd_PaymentJfzx(mode, sale);
            }
            else
            {
                return new Bhdd_PaymentJfzx(pay, head);
            }
        }
        else
        {
            return super.createLocalizePayment(flag, mode, sale, pay, head);
        }
    }
}
