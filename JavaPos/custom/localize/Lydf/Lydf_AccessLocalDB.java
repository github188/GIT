package custom.localize.Lydf;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bstd.Bstd_AccessLocalDB;

public class Lydf_AccessLocalDB extends Bstd_AccessLocalDB
{
	public void paraInitDefault()
	{
		super.paraInitDefault();
		GlobalInfo.sysPara.taxcompanyname = "";
		GlobalInfo.sysPara.taxcompanyid = "";
		GlobalInfo.sysPara.taxlimitpay = "";
		GlobalInfo.sysPara.taxfailedtip = "";
		GlobalInfo.sysPara.isMultiMkt = 'N';
	}

	public void paraConvertByCode(String code, String value)
	{
		super.paraConvertByCode(code, value);

		try
		{
			if (code.equals("LL") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.taxfailedtip = value.trim();
				return;
			}

			if (code.equals("LY") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.taxcompanyname = value.trim();
				return;
			}

			if (code.equals("LD") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.taxcompanyid = value.trim();
				return;
			}

			if (code.equals("LF") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.taxlimitpay = value.trim();
				return;
			}
			if (code.equals("LW") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isMultiMkt = value.trim().charAt(0);
				return;
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
