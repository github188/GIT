package custom.localize.Czdn;

import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Payment.PaymentCoupon;

import custom.localize.Cmls.Cmls_CustomLocalize;

/**
 * 常州迪诺水镇
 * 来自V3标准版（CMLS）
 *
 */
public class Czdn_CustomLocalize extends Cmls_CustomLocalize
{
	public String getAssemblyVersion()
    {
    	return "16340 build 2013.04.30";
    }	
	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new Czdn_HykInfoQueryBS();
	}
		
}
