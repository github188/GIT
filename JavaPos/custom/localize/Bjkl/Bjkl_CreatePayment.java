package custom.localize.Bjkl;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentDetail;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Bjkl_CreatePayment extends CreatePayment
{
	// flag:true代表以销售窗口创建对象;false红冲
	public Payment createPaymentAll(boolean flag, PayModeDef mode, SaleBS sale, SalePayDef pay, SaleHeadDef head)
	{
		// 客户化付款对象
		Payment p = createLocalizePayment(flag, mode, sale, pay, head);
		if (p != null)
			return p;

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
					return new Bjkl_PaymentBank(mode, sale);
				}
				else
				{
					return new Bjkl_PaymentBank(pay, head);
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
							return new PaymentDetail(mode, sale);
						}
						else
						{
							return new PaymentDetail(pay, head);
						}
					}
				}
			}
		}
	}

	public Payment createLocalizePayment(boolean flag, PayModeDef mode, SaleBS sale, SalePayDef pay, SaleHeadDef head)
	{
		if (mode.code.trim().equals("0108"))
		{
			if (flag)
			{
				return new Bjkl_PaymentBankRebate(mode, sale);
			}
			else
			{
				return new Bjkl_PaymentBankRebate(pay, head);
			}
		}
		return super.createLocalizePayment(flag, mode, sale, pay, head);
	}

	public boolean isPaymentLczc(SalePayDef sp)
	{
		String[] pay = getCustomPaymentDefine("PaymentCustMzk");
		if (pay != null)
		{
			for (int i = 1; i < pay.length; i++)
			{
				if (sp.paycode.equals(pay[i]) && sp.memo.trim().equals("3"))
					return true;
			}
		}

		if (sp.paycode.equals("0112") && sp.memo.trim().equals("3"))
			return true;
		else
			return false;
	}

	public PaymentMzk getPaymentMzk()
	{
		// 检索自定义付款对象列表中是否有会员卡付款对象,如果有则以该付款对象为会员卡查询的对象
		String[] pay = getCustomPaymentDefine("PaymentCustMzk");
		if (pay != null)
		{
			try
			{
				Class cl = payClassName(pay[0]);
				if (cl != null)
					return (PaymentMzk) cl.newInstance();

				new MessageBox(Language.apply("付款对象 {0} 不存在\n\n将生成默认付款对象进行交易处理", new Object[] { pay[0] }));
			}
			catch (Exception e)
			{
				e.printStackTrace();

				new MessageBox(Language.apply("付款对象 {0} 创建失败\n\n{1}\n\n将生成默认付款对象进行交易处理", new Object[] { pay[0], e.getMessage() }));
			}
		}

		// 生成默认对象
		return new Bjkl_PaymentCustMzk();
	}

}
