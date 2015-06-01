package custom.localize.Hbgy;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bstd.Bstd_AccessLocalDB;

public class Hbgy_AccessLocalDB extends Bstd_AccessLocalDB
{

	public void paraInitDefault()
	{
		super.paraInitDefault();

		GlobalInfo.sysPara.ishmkzsz = 'N';
		GlobalInfo.sysPara.hmklevel = "";
		GlobalInfo.sysPara.hmkrebate = 1;
	}

	public void paraConvertByCode(String code, String value)
	{
		super.paraConvertByCode(code, value);

		try
		{

			if (code.equals("GH") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.ishmkzsz = value.trim().charAt(0);
				return;
			}

			if (code.equals("GB") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.hmklevel = value.trim();
				return;
			}

			if (code.equals("GY") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.hmkrebate = Convert.toDouble(value.trim());
				return;
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
