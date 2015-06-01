package custom.localize.Jwyt;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bstd.Bstd_AccessLocalDB;

public class Jwyt_AccessLocalDB extends Bstd_AccessLocalDB
{
	public void paraInitDefault()
	{
		super.paraInitDefault();
		
		GlobalInfo.sysPara.disablepaycodewhenrebate = "";
		GlobalInfo.sysPara.isctrlthpay = 'N';
		GlobalInfo.sysPara.marsmerchantId = "";
		GlobalInfo.sysPara.marsKey = "";
		GlobalInfo.sysPara.marsurl = "";
		GlobalInfo.sysPara.marsVer = "1.0";
	}

	public void paraConvertByCode(String code, String value)
	{
		super.paraConvertByCode(code, value);

		if (code.equals("WA") && CommonMethod.noEmpty(value))
		{
			GlobalInfo.sysPara.disablepaycodewhenrebate = value.trim();
			return;
		}

		if (code.equals("WB") && CommonMethod.noEmpty(value))
		{
			GlobalInfo.sysPara.isctrlthpay = value.trim().charAt(0);
			return;
		}

		if (code.equals("WC") && CommonMethod.noEmpty(value))
		{
			GlobalInfo.sysPara.marsmerchantId = value.trim();
			return;
		}
		if (code.equals("WD") && CommonMethod.noEmpty(value))
		{
			GlobalInfo.sysPara.marsKey = value.trim();
			return;
		}
		if (code.equals("WE") && CommonMethod.noEmpty(value))
		{
			GlobalInfo.sysPara.marsurl = value.trim();
			return;
		}
		if (code.equals("WF") && CommonMethod.noEmpty(value))
		{
			GlobalInfo.sysPara.marsVer = value.trim();
			return;
		}
	}
}
