package custom.localize.Hhdl;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bstd.Bstd_AccessLocalDB;

public class Hhdl_AccessLocalDB extends Bstd_AccessLocalDB
{
	public void paraInitDefault()
	{
		super.paraInitDefault();
		GlobalInfo.sysPara.saletimelimit = 0;
		GlobalInfo.sysPara.isEnable17code = '0';
	}

	public void paraConvertByCode(String code, String value)
	{
		super.paraConvertByCode(code, value);

		try
		{
			if (code.equals("WV") && CommonMethod.noEmpty(value))
			{
				String[] values = value.trim().split(",");

				if (values.length > 0)
					GlobalInfo.sysPara.saletimelimit = Convert.toInt(values[0]);

				if (values.length > 1)
					GlobalInfo.sysPara.isEnable17code = values[1].trim().charAt(0);

				return;

			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
