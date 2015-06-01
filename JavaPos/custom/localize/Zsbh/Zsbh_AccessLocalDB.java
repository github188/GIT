package custom.localize.Zsbh;

import com.api.RedEnvelopeApi;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bcrm.Bcrm_AccessLocalDB;

public class Zsbh_AccessLocalDB extends Bcrm_AccessLocalDB
{
	public void paraInitDefault()
	{
		super.paraInitDefault();

		GlobalInfo.sysPara.salesReturncodeList = "";
		GlobalInfo.sysPara.hbPaymentCode ="";
		GlobalInfo.sysPara.hbPaymentUrl="";
	}

	public void paraConvertByCode(String code, String value)
	{
		super.paraConvertByCode(code, value);
		try
		{
			if (code.equals("YT"))
			{
				GlobalInfo.sysPara.salesReturncodeList = value.trim();
				return;
			}
			
			if (code.equals("YU"))
			{
				GlobalInfo.sysPara.hbPaymentCode = value.trim();
				return;
			}
			
			if (code.equals("YN"))
			{
				GlobalInfo.sysPara.hbPaymentUrl = value.trim();
				if(GlobalInfo.sysPara.hbPaymentUrl != null && GlobalInfo.sysPara.hbPaymentUrl.trim().length() > 0)
				{
					RedEnvelopeApi.updateServer(GlobalInfo.sysPara.hbPaymentUrl.trim());
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
