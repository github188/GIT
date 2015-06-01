package custom.localize.Xjly;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bcrm.Bcrm_AccessLocalDB;

public class Xjly_AccessLocalDB  extends Bcrm_AccessLocalDB{
	public void paraInitDefault()
	{
		super.paraInitDefault();
		
		GlobalInfo.sysPara.isSalePrecision = 0;
	}
	
    public void paraConvertByCode(String code, String value)
    {
        super.paraConvertByCode(code, value);

        try
        {	
        	if (code.equals("E8") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isSalePrecision = Integer.parseInt(value.trim());
				return;
			}
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
