package custom.localize.Bjkl;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.MemoInfoDef;

import custom.localize.Bstd.Bstd_AccessLocalDB;

public class Bjkl_AccessLocalDB extends Bstd_AccessLocalDB
{

	public void paraInitDefault()
	{
		super.paraInitDefault();
		GlobalInfo.sysPara.limitpaytype = "";
		GlobalInfo.sysPara.bankreate = "";
		GlobalInfo.sysPara.backPaycode = "";
		GlobalInfo.sysPara.quantityChange = "N";
		GlobalInfo.sysPara.isSuperMarketPop = 'Y';
		GlobalInfo.sysPara.salepayDisplayRate = 'N';
	}

	public void paraConvertByCode(String code, String value)
	{
		super.paraConvertByCode(code, value);

		if (code.equals("BJ") && CommonMethod.noEmpty(value))
		{
			GlobalInfo.sysPara.limitpaytype = value.trim();
			return;
		}
		if (code.equals("BK") && CommonMethod.noEmpty(value))
		{
			GlobalInfo.sysPara.bankreate = value.trim();
			return;
		}
		if (code.equals("HM") && CommonMethod.noEmpty(value))
		{
			GlobalInfo.sysPara.backPaycode = value.trim();
			return;
		}
	}

	public MemoInfoDef checkMobileCharge(String code)
	{
		return null;
	}
}
