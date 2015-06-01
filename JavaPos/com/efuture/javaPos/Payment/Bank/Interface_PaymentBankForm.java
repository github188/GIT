package com.efuture.javaPos.Payment.Bank;

import com.efuture.javaPos.Payment.PaymentBank;

public interface Interface_PaymentBankForm
{
    public boolean open(PaymentBank pay,int type);

    public boolean open(int type);
    
    public boolean open(String paycode,int type);
}
