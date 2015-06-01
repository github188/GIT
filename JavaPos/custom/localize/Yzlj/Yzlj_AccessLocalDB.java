package custom.localize.Yzlj;

import custom.localize.Bstd.Bstd_AccessLocalDB;


public class Yzlj_AccessLocalDB extends Bstd_AccessLocalDB
{
	public void paraConvertByCode(String code, String value)
	{
		//
		super.paraConvertByCode(code, value);
		
		//
		try
		{
			if (code.equals("GI"))
			{
				Yzlj_CustomGlobalInfo.getDefault().sysPara.ggkUrl = value;
				return;
			}
		}
		catch (Exception ex)
		{
		    ex.printStackTrace();
		}
	}
}
