package custom.localize.Zmjc;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentDetail;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Zmjc_PaymentDetail extends PaymentDetail
{
	public Zmjc_PaymentDetail(PayModeDef mode,SaleBS sale)
	{
		initPayment(mode,sale);
	}
	
	public Zmjc_PaymentDetail(SalePayDef pay,SaleHeadDef head)
	{
		initPayment(pay,head);
	}

	public SalePayDef inputPay(String money)
	{
		try
		{
			// 打开明细输入窗口
			new Zmjc_PaymentDetailForm().open(this,saleBS);
			
			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
        }
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return null;		
	}
}
