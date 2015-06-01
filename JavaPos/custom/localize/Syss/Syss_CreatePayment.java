package custom.localize.Syss;

import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Payment.Bank.Ylswdll_PaymentBankFunc;

import custom.localize.Bhls.Bhls_CreatePayment;


public class Syss_CreatePayment extends Bhls_CreatePayment
{
    public PaymentBankFunc getPaymentBankFunc()
    {
        return new Ylswdll_PaymentBankFunc();
    }
}
