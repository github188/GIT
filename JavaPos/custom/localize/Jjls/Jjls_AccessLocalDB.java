package custom.localize.Jjls;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bcrm.Bcrm_AccessLocalDB;

public class Jjls_AccessLocalDB extends Bcrm_AccessLocalDB
{
	public void paraInitDefault()
	{
		super.paraInitDefault();
		GlobalInfo.sysPara.promotionDiscountPayCode = "";
		GlobalInfo.sysPara.cardDiscountRate = 0;
		GlobalInfo.sysPara.cardDiscountPayCode = "";
		GlobalInfo.sysPara.cardRebatePayCode = "";
		GlobalInfo.sysPara.ICCardPayment = "";
		
	}

	public void paraConvertByCode(String code, String value)
	{
		super.paraConvertByCode(code, value);

		try
		{
			if (code.equals("WN") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.promotionDiscountPayCode = value.trim();
				return;
			}
			if (code.equals("WO") && CommonMethod.noEmpty(value))
			{
				String[] cardDiscount = value.trim().split("\\|");
				if (cardDiscount.length == 2)
				{
					GlobalInfo.sysPara.cardDiscountRate = Double.parseDouble(cardDiscount[0]);
					GlobalInfo.sysPara.cardDiscountPayCode = cardDiscount[1];
				}
				//GlobalInfo.sysPara.cardDiscountRate = value.trim()
				return;
			}
			if (code.equals("WW") && CommonMethod.noEmpty(value))
			{	
				GlobalInfo.sysPara.cardDiscountPayCode1 = value.trim();
				return;
			}
			if (code.equals("WP") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.cardRebatePayCode = value.trim();
				return;
			}
			if (code.equals("WQ") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.ICCardPayment = value.trim();
				return;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
