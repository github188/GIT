package custom.localize.Gbyw;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bstd.Bstd_AccessLocalDB;

public class Gbyw_AccessLocalDB extends Bstd_AccessLocalDB
{
	public void paraInitDefault()
	{
		super.paraInitDefault();
		GlobalInfo.sysPara.isAutoLczc = 'N';
		GlobalInfo.sysPara.lczcmaxmoney = 0;
		GlobalInfo.sysPara.hflczcperje = 0;
		GlobalInfo.sysPara.hflczcacctuplimit = 0;
		GlobalInfo.sysPara.visiblepaycode = "0405,0406";
		GlobalInfo.sysPara.isEnableLHCard = 'Y';
	}

	public void paraConvertByCode(String code, String value)
	{
		super.paraConvertByCode(code, value);

		try
		{
			if (code.equals("WJ") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.cyCrmUrl = value.trim();
				return;
			}

			if (code.equals("WM") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isEnableLHCard = value.trim().charAt(0);
				return;
			}

			if (code.equals("LC") && CommonMethod.noEmpty(value))
			{
				String[] values = value.trim().split(",");

				if (values.length > 0)
				{
					GlobalInfo.sysPara.isAutoLczc = values[0].charAt(0);
				}

				if (values.length > 1)
				{
					GlobalInfo.sysPara.lczcmaxmoney = Convert.toDouble(values[1]);
				}

				if (values.length > 2)
				{
					GlobalInfo.sysPara.hflczcperje = Convert.toDouble(values[2]);
				}

				if (values.length > 3)
				{
					GlobalInfo.sysPara.hflczcacctuplimit = Convert.toDouble(values[3]);
				}

				return;
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
