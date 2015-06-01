package custom.localize.Bjcx;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bcrm.Bcrm_AccessLocalDB;

public class Bjcx_AccessLocalDB extends Bcrm_AccessLocalDB
{
	public void paraConvertByCode(String code, String value)
    {
        super.paraConvertByCode(code, value);

        try
        {	
            if (code.equals("WC") && CommonMethod.noEmpty(value))
            {
                GlobalInfo.sysPara.isGroupJSLB = value.trim().charAt(0);
                return;
            }
            
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
