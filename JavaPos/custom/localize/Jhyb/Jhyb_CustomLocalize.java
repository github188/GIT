package custom.localize.Jhyb;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Cmls.Cmls_CustomLocalize;
//金华一百
public class Jhyb_CustomLocalize extends Cmls_CustomLocalize
{
    public String getAssemblyVersion()
    {
    	return "11517 build 2012.06.30";
    }
    
    public SaleBS createSaleBS()
    {
      return new custom.localize.Jhyb.Jhyb_SaleBS();
    }
    
    public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Jhyb.Jhyb_SaleBillMode();
	}
}
