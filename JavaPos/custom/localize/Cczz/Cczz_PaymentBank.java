package custom.localize.Cczz;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Cczz_PaymentBank extends PaymentBank {
	public Cczz_PaymentBank()
	{
	}
	
	public Cczz_PaymentBank(PayModeDef mode,SaleBS sale)
	{
		initPayment(mode,sale);
	}
	
	public Cczz_PaymentBank(SalePayDef pay,SaleHeadDef head)
	{
		initPayment(pay,head);
	}
	
	protected boolean cancelPayBack()
	{
		// 退货交易
		boolean ret = false;
		
		try
		{ 
			// 扣回消费
			if (salepay != null && salepay.je < 0)
			{
				ret = CreatePayment.getDefault().getPaymentBankForm().open(this,PaymentBank.XYKXF);
				return ret;
			}
			
			String msg = getBackPayHint();
			if (msg != null && msg.length() > 0)			
			{
				if (new MessageBox("你要"+msg+"\n\n1-就在本机撤销 / 2-其他途径撤销",null,false).verify() == GlobalVar.Key2)				
				{
					String line = "你已经通过其他途径进行银联交易退货\n\n所以不在本机" + msg;
					if (new MessageBox(line,null,true).verify() == GlobalVar.Key1)				
					{
						return true;
					}
				}
			}
			
			// 退货交易
			ret = CreatePayment.getDefault().getPaymentBankForm().open(this,PaymentBank.XYKCX);
			return ret;
		}
		catch(Exception er)
		{
			new MessageBox(er.getMessage());
			return false;
		}
	}
}
