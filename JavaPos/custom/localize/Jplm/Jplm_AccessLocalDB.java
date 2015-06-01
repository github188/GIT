package custom.localize.Jplm;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bstd.Bstd_AccessLocalDB;

public class Jplm_AccessLocalDB extends Bstd_AccessLocalDB
{
	public void paraInitDefault()
	{
		super.paraInitDefault();
		GlobalInfo.sysPara.bankreate = "";
	}

	public void paraConvertByCode(String code, String value)
	{
		super.paraConvertByCode(code, value);
		if (code.equals("BK") && CommonMethod.noEmpty(value))
		{
			GlobalInfo.sysPara.bankreate = value.trim();
			return;
		}
	}
}
