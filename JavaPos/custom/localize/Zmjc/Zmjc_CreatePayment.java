package custom.localize.Zmjc;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.PaymentChange;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bhls.Bhls_CreatePayment;

public class Zmjc_CreatePayment extends Bhls_CreatePayment
{

	public PaymentChange getPaymentChange(SaleBS sale)
	{
		return new Zmjc_PaymentChange(sale);
	}

	// flag:true代表以销售窗口创建对象;false红冲
	public Payment createPaymentAll(boolean flag, PayModeDef mode, SaleBS sale, SalePayDef pay, SaleHeadDef head)
	{
		// 客户化付款对象
		Payment p = createLocalizePayment(flag, mode, sale, pay, head);
		if (p != null) return p;

		// 允许直接输入付款金额
		if (allowQuickInputMoney(mode))
		{
			if (flag)
			{
				return new Payment(mode, sale);
			}
			else
			{
				return new Payment(pay, head);
			}
		}
		else
		{
			// 金卡工程,生成相应的金卡工程付款对象
			if (mode.isbank == 'Y')
			{
				if (flag)
				{
					return new PaymentBank(mode, sale);
				}
				else
				{
					return new PaymentBank(pay, head);
				}
			}
			else if (mode.isbank == 'M')
			{
				if (flag)
				{
					return new PaymentMzk(mode, sale);
				}
				else
				{
					return new PaymentMzk(pay, head);
				}
			}
			else
			{
				// 根据不同的付款方式,创建相应的付款对象
				switch (mode.type)
				{
					case '4': // 面值卡付款
					{
						if (flag)
						{
							return new PaymentMzk(mode, sale);
						}
						else
						{
							return new PaymentMzk(pay, head);
						}
					}

					default: // 其他付款方式
					{
						if (flag)
						{
							return new Zmjc_PaymentDetail(mode, sale);
						}
						else
						{
							return new Zmjc_PaymentDetail(pay, head);
						}
					}
				}
			}
		}
	}
}
