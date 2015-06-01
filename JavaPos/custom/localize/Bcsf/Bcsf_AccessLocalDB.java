package custom.localize.Bcsf;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bstd.Bstd_AccessLocalDB;

public class Bcsf_AccessLocalDB extends Bstd_AccessLocalDB
{
	public void paraInitDefault()
	{
		super.paraInitDefault();

		GlobalInfo.sysPara.isxxcj = 'N';
		GlobalInfo.sysPara.xfcardmerchantno = "";
		GlobalInfo.sysPara.xfcardmpwd = "";
		GlobalInfo.sysPara.xfcardsrvurl = "";
		GlobalInfo.sysPara.scoreAmountLimit = 500;
		GlobalInfo.sysPara.limitpaytype = "";
		GlobalInfo.sysPara.isenableyhps = 'N';
	}

	public void paraConvertByCode(String code, String value)
	{
		super.paraConvertByCode(code, value);

		try
		{
			if (code.equals("WD") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isxxcj = value.trim().charAt(0);
				return;
			}

			if (code.equals("BC") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.xfcardmerchantno = value.trim();
				return;
			}

			if (code.equals("BS") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.xfcardmpwd = value.trim();
				return;
			}

			if (code.equals("BF") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.xfcardsrvurl = value.trim();
				return;
			}

			if (code.equals("BG") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.scoreAmountLimit = Convert.toDouble(value.trim());
				return;
			}
			if (code.equals("CS") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.limitpaytype = value.trim();
				return;
			}
			if (code.equals("CF") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isenableyhps = value.trim().charAt(0);
				return;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
