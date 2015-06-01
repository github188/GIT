package custom.localize.Jcgj;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bcrm.Bcrm_AccessLocalDB;

public class Jcgj_AccessLocalDB extends Bcrm_AccessLocalDB
{
	public void paraInitDefault()
	{
		super.paraInitDefault();
		
		GlobalInfo.sysPara.limitDisRate = 0;
		GlobalInfo.sysPara.isMustCustCoupon = 'Y';
		GlobalInfo.sysPara.maxPriceInCardSale = 9999999;
		GlobalInfo.sysPara.grantRole = "";
	}
	
	public void paraConvertByCode(String code, String value)
    {
        super.paraConvertByCode(code, value);

        try
        {	
        	if (code.equals("WE") && CommonMethod.noEmpty(value))
			{
         		GlobalInfo.sysPara.limitDisRate = Convert.toDouble(value.toString().trim());
				return;
			}
        	if (code.equals("WF") && CommonMethod.noEmpty(value))
			{
         		GlobalInfo.sysPara.isMustCustCoupon = value.trim().charAt(0);
				return;
			}
         	if (code.equals("WG") && CommonMethod.noEmpty(value))
			{
         		GlobalInfo.sysPara.maxPriceInCardSale = Double.parseDouble(value.trim());
         		return;
			}
         	if (code.equals("WH") && CommonMethod.noEmpty(value))
			{
         		GlobalInfo.sysPara.grantRole = value.toString().trim();
         		return;
			}
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
