package custom.localize.Nbbh;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentDetail;
import com.efuture.javaPos.Payment.PaymentDetailForm;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Nbbh_PaymentDetail extends PaymentDetail
{

	public Nbbh_PaymentDetail()
	{
		super();
	}
	
	public Nbbh_PaymentDetail(PayModeDef mode,SaleBS sale)
	{
		initPayment(mode,sale);
	}
	
	public Nbbh_PaymentDetail(SalePayDef pay,SaleHeadDef head)
	{
		initPayment(pay,head);
	}
	
	public SalePayDef inputPay(String money)
	{
		try
		{
			// 打开明细输入窗口:卡号为必输项
			new PaymentDetailForm().open(this,saleBS,true,false);
			
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
