package custom.localize.Lrtd;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bstd.Bstd_AccessLocalDB;

public class Lrtd_AccessLocalDB extends  Bstd_AccessLocalDB
{
	public void paraInitDefault()
	{
		super.paraInitDefault();
		
        GlobalInfo.sysPara.isShowCatid = "";
    	
	}
	public void paraConvertByCode(String code, String value)
	{
		super.paraConvertByCode(code, value);
		
		try
		{
			if (code.equals("HO") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isShowCatid = value.trim();
				return;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
