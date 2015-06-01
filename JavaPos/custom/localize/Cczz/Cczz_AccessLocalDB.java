package custom.localize.Cczz;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bcrm.Bcrm_AccessLocalDB;

public class Cczz_AccessLocalDB extends Bcrm_AccessLocalDB
{

	public void paraInitDefault()
	{
		// TODO 自动生成方法存根
		super.paraInitDefault();
		GlobalInfo.sysPara.printyyygrouptype = '2';
		GlobalInfo.sysPara.cupCardPwd = "";
		GlobalInfo.sysPara.isDel = "Y";
		GlobalInfo.sysPara.isPrintCPR = 'N';
		GlobalInfo.sysPara.isprintgkl = 'N';
	}

	public void paraConvertByCode(String code, String value)
	{
		try
		{
			if (code.equals("ZZ") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.printMode = value.trim().charAt(0);
				return;
			}
			if (code.equals("MK") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.cupCardPwd = value.trim();
				return;
			}
			if (code.equals("ID") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isDel = value.trim();
				return;
			}
			if (code.equals("IE") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isPrintCPR = value.trim().charAt(0);
				return;
			}
			if (code.equals("Y6") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isprintgkl = value.trim().charAt(0);
				return;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		super.paraConvertByCode(code, value);
	}
}
