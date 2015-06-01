package com.efuture.javaPos.Payment;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class PaymentDetail extends Payment
{
	public PaymentDetail()
	{
	}
	
	public PaymentDetail(PayModeDef mode,SaleBS sale)
	{
		initPayment(mode,sale);
	}
	
	public PaymentDetail(SalePayDef pay,SaleHeadDef head)
	{
		initPayment(pay,head);
	}
	
	public SalePayDef inputPay(String money)
	{
		try
		{
			// 打开明细输入窗口
			new PaymentDetailForm().open(this,saleBS);
			
			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
        }
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return null;		
	}
	
	public boolean createSalePay(String money,String payno,String idno)
	{
		try
		{
			if (super.createSalePay(money))
			{
				salepay.payno = payno;
				salepay.idno = idno;
				
				return true;
			}
		}
		catch(Exception ex)
		{
			new MessageBox(Language.apply("生成交易付款对象失败\n\n") + ex.getMessage());
			ex.printStackTrace();
		}
		
		//
		salepay = null;
		return false;
	}
	
	public int getAccountInputMode()
	{
		return TextBox.MsrKeyInput;
	}
}
