package custom.localize.Sxtx;



import custom.localize.Bstd.Bstd_AccessLocalDB;

public class Sxtx_AccessLocalDB extends Bstd_AccessLocalDB {

	public void paraInitDefault()
	{
		super.paraInitDefault();
		
		Sxtx_CustomGlobalInfo.getDefault().sysPara.isprintflk = 'N';
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
				Sxtx_CustomGlobalInfo.getDefault().sysPara.isprintflk = value.charAt(0);
				return;
			}
			
		}
		catch (Exception ex)
		{
		    ex.printStackTrace();
		}
	}
}
