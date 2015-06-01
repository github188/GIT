package custom.localize.Sbjh;

import com.efuture.javaPos.Logic.SaleBS;

import custom.localize.Gwzx.Gwzx_CustomLocalize;

public class Sbjh_CustomLocalize extends Gwzx_CustomLocalize
{
    public String getAssemblyVersion()
    {
    	return "16339 build 2013.12.12";
    }
    
    public SaleBS createSaleBS()
    {
		return new Sbjh_SaleBS();
    }
}
