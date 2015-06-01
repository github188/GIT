package com.efuture.javaPos.Payment;

import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;


public class PaymentCredit extends PaymentMzk
{
    public PaymentCredit()
    {
    }

    public PaymentCredit(PayModeDef mode, SaleBS sale)
    {
        initPayment(mode, sale);
    }

    // 该构造函数用于红冲小票时,通过小票付款明细创建对象
    public PaymentCredit(SalePayDef pay, SaleHeadDef head)
    {
        initPayment(pay, head);
    }

    public String getDisplayCardno()
    {
        return "["+mzkret.cardno + "]" + mzkret.cardname;
    }
    
	public int getAccountInputMode()
	{
		return TextBox.AllInput;
	}
	
	protected String getDisplayAccountInfo()
	{
		return Language.apply("客户账号");
	}
}
