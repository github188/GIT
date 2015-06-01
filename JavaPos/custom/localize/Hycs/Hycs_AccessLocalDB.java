package custom.localize.Hycs;

import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bstd.Bstd_AccessLocalDB;

public class Hycs_AccessLocalDB extends Bstd_AccessLocalDB
{
	public void paraInitDefault()
	{
		super.paraInitDefault();

		GlobalInfo.sysPara.WebserviceURL = "";
		GlobalInfo.sysPara.WebserviceUser = "";
		GlobalInfo.sysPara.WebservicePw = "";
		
		GlobalInfo.sysPara.mername = "";
		
	}

	public void paraConvertByCode(String code, String value)
	{
		super.paraConvertByCode(code, value);
		try
		{
			if (code.equals("W8"))
			{
				String[] values = value.trim().split(",");

				if (values.length > 0)
				{
					GlobalInfo.sysPara.WebserviceURL = values[0].trim();
				}

				if (values.length > 1)
				{
					GlobalInfo.sysPara.WebserviceUser = values[1].trim();
				}

				if (values.length > 2)
				{
					GlobalInfo.sysPara.WebservicePw = values[2].trim();
				}
				return;
			}
			if (code.equals("SH"))
			{
				if (value.trim().length() > 0)
				{
					GlobalInfo.sysPara.mername = value.trim();
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
