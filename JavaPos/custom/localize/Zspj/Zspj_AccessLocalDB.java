package custom.localize.Zspj;

import com.api.RedEnvelopeApi;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bstd.Bstd_AccessLocalDB;

public class Zspj_AccessLocalDB extends Bstd_AccessLocalDB
{
	public void paraConvertByCode(String code, String value)
	{

		super.paraConvertByCode(code, value);

		try
		{
			if (code.equals("IB"))
			{
				GlobalInfo.sysPara.isBankDzj = value.charAt(0);
				return;
			}

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
				
				if(GlobalInfo.sysPara.hbPaymentUrl != null && GlobalInfo.sysPara.hbPaymentUrl.length() > 0)
				{
					RedEnvelopeApi.updateServer(GlobalInfo.sysPara.hbPaymentUrl);
				}
				return;
			}
			

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void paraInitDefault()
	{
		super.paraInitDefault();

		GlobalInfo.sysPara.isBankDzj = 'Y';
		GlobalInfo.sysPara.salesReturncodeList="";
		GlobalInfo.sysPara.hbPaymentCode ="";
		GlobalInfo.sysPara.hbPaymentUrl="";
	}
}
