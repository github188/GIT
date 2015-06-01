package custom.localize.Sdyz;


public class Sdyz_AccessLocalDB extends com.efuture.javaPos.Global.AccessLocalDB
{
	public void paraConvertByCode(String code, String value)
	{
		//
		super.paraConvertByCode(code, value);
		
		//
		try
		{
			if (code.equals("4S"))
			{
				Sdyz_CustomGlobalInfo.getDefault().sysPara.isrebate = value.charAt(0);
				return;
			}
			
			if (code.equals("4V"))
			{
				Sdyz_CustomGlobalInfo.getDefault().sysPara.dpsswr = value.charAt(0);
				return;
			}
			
			if (code.equals("C1"))
			{
				Sdyz_CustomGlobalInfo.getDefault().sysPara.cardflag = value.charAt(0);
			}
		}
		catch (Exception ex)
		{
		    ex.printStackTrace();
		}
	}
}
