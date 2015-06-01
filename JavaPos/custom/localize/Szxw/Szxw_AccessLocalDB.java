package custom.localize.Szxw;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bhls.Bhls_AccessLocalDB;

public class Szxw_AccessLocalDB extends Bhls_AccessLocalDB
{
	public void paraConvertByCode(String code, String value)
	{
		super.paraConvertByCode(code, value);

		try
		{
			if (code.equals("Z1") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.slipPrinter_area = value.trim();
				return;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
