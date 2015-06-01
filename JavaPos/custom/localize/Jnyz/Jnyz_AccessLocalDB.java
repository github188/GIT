package custom.localize.Jnyz;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bcrm.Bcrm_AccessLocalDB;


public class Jnyz_AccessLocalDB extends Bcrm_AccessLocalDB
{
	public void paraConvertByCode(String code, String value)
	{
		//
		super.paraConvertByCode(code, value);
		
		//
		try
		{
			if (code.equals("4S"))
			{
				Jnyz_CustomGlobalInfo.getDefault().sysPara.isrebate = value.charAt(0);
				return;
			}
			
			if (code.equals("4V"))
			{
				Jnyz_CustomGlobalInfo.getDefault().sysPara.dpsswr = value.charAt(0);
				return;
			}
			
			if (code.equals("C1"))
			{
				Jnyz_CustomGlobalInfo.getDefault().sysPara.cardflag = value.charAt(0);
				return;
			}
			if (code.equals("44") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.inputydoc = 'A';
				return;
			}
			if (code.equals("FP"))
			{
				Jnyz_CustomGlobalInfo.getDefault().sysPara.fpje = value;
				return;
			}
			if (code.equals("TH"))
			{
				Jnyz_CustomGlobalInfo.getDefault().sysPara.isth = value.charAt(0);
				return;
			}
			if (code.equals("HC"))
			{
				Jnyz_CustomGlobalInfo.getDefault().sysPara.ishc = value.charAt(0);
				return;
			}
			if (code.equals("GI"))
			{
				Jnyz_CustomGlobalInfo.getDefault().sysPara.ggkUrl = value;
				return;
			}
			if (code.equals("IM") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.istcl = value.trim().charAt(0);
				return;
			}
		}
		catch (Exception ex)
		{
		    ex.printStackTrace();
		}
	}
}
