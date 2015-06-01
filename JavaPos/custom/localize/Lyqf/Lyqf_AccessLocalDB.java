package custom.localize.Lyqf;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bstd.Bstd_AccessLocalDB;

public class Lyqf_AccessLocalDB extends Bstd_AccessLocalDB
{
	public void paraInitDefault()
	{
		super.paraInitDefault();
		
		GlobalInfo.sysPara.scoreAmountLimit = 0;
	}

	public void paraConvertByCode(String code, String value)
	{
		super.paraConvertByCode(code, value);

		if (code.equals("BG") && CommonMethod.noEmpty(value))
		{
			GlobalInfo.sysPara.scoreAmountLimit = Convert.toDouble(value.trim());
			return;
		}
	}
}
