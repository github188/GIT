package custom.localize.Njxb;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bcrm.Bcrm_AccessLocalDB;

public class Njxb_AccessLocalDB extends Bcrm_AccessLocalDB
{
	public void paraInitDefault()
	{
		super.paraInitDefault();

		GlobalInfo.sysPara.feeRate = 0;
		GlobalInfo.sysPara.feeCode = "";
		GlobalInfo.sysPara.feePayment = "";
	}

	public void paraConvertByCode(String code, String value)
	{
		super.paraConvertByCode(code, value);

		try
		{
			if (code.equals("WR") && CommonMethod.noEmpty(value))
			{
				String[] fee = value.split("\\|");
				if (fee.length == 3)
				{
					GlobalInfo.sysPara.feeCode = fee[0].trim();
					GlobalInfo.sysPara.feeRate = Convert.toDouble(fee[1].trim());
					GlobalInfo.sysPara.feePayment = fee[2].trim();
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
