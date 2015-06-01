package custom.localize.Ksss;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bcrm.Bcrm_AccessLocalDB;

public class Ksss_AccessLocalDB extends  Bcrm_AccessLocalDB
{
	public void paraInitDefault()
	{
		super.paraInitDefault();
		
        GlobalInfo.sysPara.mdcode = "";
    	
	}
	public void paraConvertByCode(String code, String value)
	{
		super.paraConvertByCode(code, value);
		
		try
		{
			if (code.equals("MD") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.mdcode = value.trim();
				return;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
