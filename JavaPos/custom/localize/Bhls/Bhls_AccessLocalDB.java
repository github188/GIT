package custom.localize.Bhls;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.GlobalInfo;


public class Bhls_AccessLocalDB extends AccessLocalDB 
{
	public void paraConvertByCode(String code, String value)
	{
		super.paraConvertByCode(code, value);
		
		try
		{
			if (code.equals("PP") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.printpopbill = value.trim().charAt(0);
				return;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
