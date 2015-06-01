package custom.localize.Lrls;

import com.efuture.javaPos.Payment.PaymentCoupon;
import com.efuture.javaPos.Payment.PaymentCouponForm;
import com.efuture.javaPos.Struct.SalePayDef;

public class Lrls_PaymentCoupon extends PaymentCoupon
{
	public SalePayDef inputPay(String money)
	{
		try
		{
			// 退货小票不能使用,退货扣回按销售算
			/*if (checkMzkIsBackMoney() && (GlobalInfo.sysPara.thmzk != 'Y'))
			{
				new MessageBox(Language.apply("退货时不能使用") + paymode.name, null, false);

				return null;
			}
*/
			// 先检查是否有冲正未发送
			if (!sendAccountCz()) { return null; }

			// 打开明细输入窗口
			new PaymentCouponForm().open(this, saleBS);

			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;
	}
}
