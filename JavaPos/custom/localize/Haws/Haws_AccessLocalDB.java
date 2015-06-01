package custom.localize.Haws;


import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bcrm.Bcrm_AccessLocalDB;

public class Haws_AccessLocalDB extends Bcrm_AccessLocalDB
{
	public void paraInitDefault()
	{
		super.paraInitDefault();
		
        GlobalInfo.sysPara.isprintdjq = "N";
    	
	}
	public void paraConvertByCode(String code, String value)
	{
		super.paraConvertByCode(code, value);
		
		try
		{
			if (code.equals("DJ") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isprintdjq = value.trim();
				return;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
