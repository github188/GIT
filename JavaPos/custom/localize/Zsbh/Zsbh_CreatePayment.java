/**
 * 
 */
package custom.localize.Zsbh;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentBankCMCC;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import custom.localize.Bcrm.Bcrm_CreatePayment;

public class Zsbh_CreatePayment extends Bcrm_CreatePayment
{
	public Payment createLocalizePayment(boolean flag, PayModeDef mode, SaleBS sale, SalePayDef pay, SaleHeadDef head)
	{
		if (mode.code.trim().equals("0317"))
		{
			if (flag)
			{
				return new Zsbh_PaymentBank(mode, sale);
			}
			else
			{
				return new Zsbh_PaymentBank(pay, head);
			}
		}
		else if (mode.code.trim().equals("04"))
		{
			if (flag)
			{
				return new Zsbh_PaymentMzk(mode, sale);
			}
			else
			{
				return new Zsbh_PaymentMzk(pay, head);
			}
		}
		else if (mode.code.trim().equals("0401"))
		{
			if (flag)
			{
				return new Zsbh_WebServicePaymentMzk(mode, sale);
			}
			else
			{
				return new Zsbh_WebServicePaymentMzk(pay, head);
			}

		}
		else if (mode.code.trim().equals("0721") || mode.code.trim().equals("0722"))// 移动积分/找零充值
		{
			if (flag)
			{
				return new PaymentBankCMCC(mode, sale);
			}
			else
			{
				return new PaymentBankCMCC(pay, head);
			}
		}
		else if (mode.code.trim().equals("0500"))
		{
			if (flag)
			{
				return new Zsbh_PaymentCoupon(mode, sale);
			}
			else
			{
				return new Zsbh_PaymentCoupon(pay, head);
			}
		}
		else
		{
			return super.createLocalizePayment(flag, mode, sale, pay, head);
		}
	}

	public PaymentMzk getPaymentMzk()
	{
		return new Zsbh_PaymentMzk();
	}
}
