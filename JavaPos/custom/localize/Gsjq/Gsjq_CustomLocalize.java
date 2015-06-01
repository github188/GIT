package custom.localize.Gsjq;

import com.efuture.javaPos.Logic.SaleBS;

import custom.localize.Bhcm.Bhcm_CustomLocalize;

public class Gsjq_CustomLocalize extends Bhcm_CustomLocalize 
{
	public String getAssemblyVersion()
    {
    	return "9719 build 2014.12.29";
    }
	public SaleBS createSaleBS()
    {
		return new Gsjq_SaleBS();
    }
}
