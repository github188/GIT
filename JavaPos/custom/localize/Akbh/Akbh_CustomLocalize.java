package custom.localize.Akbh;

import com.efuture.javaPos.Logic.SaleBS;

import custom.localize.Cmls.Cmls_CustomLocalize;
//安康百货
public class Akbh_CustomLocalize extends Cmls_CustomLocalize
{
    public String getAssemblyVersion()
    {
    	return "9169 build 2012.08.17";
    }
    
    
    public SaleBS createSaleBS()
    {
		return new custom.localize.Akbh.Akbh_SaleBS();
    }
}
