package custom.localize.Djgc;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentCustLczc;
import com.efuture.javaPos.Payment.PaymentMzkForm;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Djgc_PaymentCustLczc extends PaymentCustLczc
{

	public Djgc_PaymentCustLczc()
	{
	}

	public Djgc_PaymentCustLczc(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Djgc_PaymentCustLczc(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}
	
	protected boolean getPasswdBeforeFindMzk(StringBuffer passwd)
	{
		return true;
	}
	public SalePayDef inputPay(String money)
	{
		try
		{
			/*// 退货小票不能使用,退货扣回按销售算
			if (checkMzkIsBackMoney() && GlobalInfo.sysPara.thmzk != 'Y')
			{
//				new MessageBox("退货时不能使用" + paymode.name);
				new MessageBox(Language.apply("退货时不能使用{0}" ,new Object[]{paymode.name}));
				return null;
			}*/

			// 先检查是否有冲正未发送
			if (!sendAccountCz())
				return null;
			
			//是否通过外部设备读取卡号
			if(!autoFindCard()) return null;

			// 打开明细输入窗口
			new PaymentMzkForm().open(this, saleBS);

			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;
	}
	
	/*public boolean checkMzkIsBackMoney()
	{
		return false;
	}*/
	
}
