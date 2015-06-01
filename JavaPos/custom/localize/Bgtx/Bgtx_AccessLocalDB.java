package custom.localize.Bgtx;

import custom.localize.Bcrm.Bcrm_AccessLocalDB;

public class Bgtx_AccessLocalDB extends Bcrm_AccessLocalDB
{
	public void paraInitDefault()
	{
		super.paraInitDefault();
		
		Bgtx_CustomGlobalInfo.getDefault().sysPara.isprintflk = 'N';
	}
	public void paraConvertByCode(String code, String value)
	{
		//
		super.paraConvertByCode(code, value);
		
		//
		try
		{
			if (code.equals("SC"))
			{
				Bgtx_CustomGlobalInfo.getDefault().sysPara.isprintflk = value.charAt(0);
				return;
			}
			
		}
		catch (Exception ex)
		{
		    ex.printStackTrace();
		}
	}
}
