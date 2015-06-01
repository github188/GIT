package custom.localize.Gzbh;

import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.GlobalInfo;

public class Gzbh_AccessLocalDB extends AccessLocalDB
{
	public void paraConvertByCode(String code, String value)
	{
		try
		{
			if (code.equals("O6"))
			{
				GlobalInfo.sysPara.printInfo1 = value.trim();
				return;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		super.paraConvertByCode(code, value);
	}
}
