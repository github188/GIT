package custom.localize.Nhls;

import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bhcm.Bhcm_CustomLocalize;

public class Nhls_CustomLocalize extends Bhcm_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "11693 build 2012.09.11";
	}

	public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Nhls.Nhls_SaleBillMode();
	}
}
