package custom.localize.Ccsj;

import com.efuture.javaPos.Logic.SaleBS;

import custom.localize.Bstd.Bstd_CustomLocalize;

public class Ccsj_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
    {
        return "11099 build 2012.02.03";
    }
	
	public SaleBS createSaleBS()
    {
		return new Ccsj_SaleBS();
    }
}
