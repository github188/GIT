package custom.localize.Bcrm;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bhls.Bhls_AccessLocalDB;


public class Bcrm_AccessLocalDB extends Bhls_AccessLocalDB
{
	public void paraInitDefault()
	{
		super.paraInitDefault();
		
        GlobalInfo.sysPara.iscrmtjprice = 'Y';
    	GlobalInfo.sysPara.ispregetmsinfo = 'N';
    	GlobalInfo.sysPara.vipPromotionCrm = "2";
    	GlobalInfo.sysPara.vipPayExcp = 'N';
    	GlobalInfo.sysPara.vipCalcType = "1";
    	GlobalInfo.sysPara.ismj = 'N';
    	GlobalInfo.sysPara.mjtype = 'N';
    	GlobalInfo.sysPara.mjloop = 'N';
	}
	
    public void paraConvertByCode(String code, String value)
    {
        super.paraConvertByCode(code, value);

        try
        {	
            if (code.equals("F1") && CommonMethod.noEmpty(value))
            {
                GlobalInfo.sysPara.iscrmtjprice = value.trim().charAt(0);
                return;
            }
            
            if (code.equals("PQ") && CommonMethod.noEmpty(value))
            {
            	GlobalInfo.sysPara.ispregetmsinfo = value.trim().charAt(0);
            	return;
            }

            if (code.equals("E3") && CommonMethod.noEmpty(value))
            {
            	String[] s = value.trim().split(",");
            	
            	if (s.length > 0) GlobalInfo.sysPara.vipPromotionCrm = s[0].trim();
            	if (s.length > 1) GlobalInfo.sysPara.vipPayExcp = s[1].trim().charAt(0);
            	if (s.length > 2) GlobalInfo.sysPara.vipCalcType = s[2].trim();
            	return;
            }
            
            if (code.equals("T3") && CommonMethod.noEmpty(value))
            {
            	GlobalInfo.sysPara.ismj = value.charAt(0);
            	return;
            }
            
            if (code.equals("E6") && CommonMethod.noEmpty(value))
            {
            	GlobalInfo.sysPara.mjtype = value.charAt(0);
            	return;
            }
            
            if (code.equals("E5") && CommonMethod.noEmpty(value))
            {
            	GlobalInfo.sysPara.mjloop = value.charAt(0);
            	return;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
