package custom.localize.Bjys;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Bjys_PaymentZp extends Payment
{
	public Bjys_PaymentZp()
	{
		super();
	}
	
	public Bjys_PaymentZp(PayModeDef mode,SaleBS sale)
	{
		initPayment(mode,sale);
	}
	
	public Bjys_PaymentZp(SalePayDef pay,SaleHeadDef head)
	{
		initPayment(pay,head);
	}
	
	public SalePayDef inputPay(String money)
	{
		try
		{
			// 打开明细输入窗口
			new Bjys_PaymentZpForm().open(this,saleBS,true,false);
			
			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
        }
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return null;		
	}
	
	public boolean createSalePay(String money,String payno,String idno,String date)
	{
		try
		{
			if (super.createSalePay(money))
			{
				salepay.payno = payno;
				salepay.idno = idno;
				salepay.memo = date;
				
				return true;
			}
		}
		catch(Exception ex)
		{
			new MessageBox("生成交易付款对象失败\n\n" + ex.getMessage());
			ex.printStackTrace();
		}
		
		//
		salepay = null;
		return false;
	}
}
