package custom.localize.Agog;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bstd.Bstd_AccessLocalDB;

public class Agog_AccessLocalDB extends Bstd_AccessLocalDB
{
	public void paraInitDefault()
	{
		super.paraInitDefault();
		GlobalInfo.sysPara.cyCrmUrl = "";
		GlobalInfo.sysPara.isEnableLHCard = 'N';
	}

	public void paraConvertByCode(String code, String value)
	{
		super.paraConvertByCode(code, value);

		try
		{
			if (code.equals("WJ") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.cyCrmUrl = value.trim();
				return;
			}

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
