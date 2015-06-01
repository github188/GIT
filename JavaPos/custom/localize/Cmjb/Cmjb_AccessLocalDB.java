package custom.localize.Cmjb;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bcrm.Bcrm_AccessLocalDB;

public class Cmjb_AccessLocalDB extends Bcrm_AccessLocalDB
{
	public void paraInitDefault()
	{
		super.paraInitDefault();

		GlobalInfo.sysPara.isEnableLHCard = 'Y';
	}

	public void paraConvertByCode(String code, String value)
	{
		super.paraConvertByCode(code, value);

		try
		{
			if (code.equals("WM") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isEnableLHCard = value.trim().charAt(0);
				return;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
