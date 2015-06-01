package custom.localize.Lrls;

import com.efuture.javaPos.Logic.MutiSelectBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Cmls.Cmls_CustomLocalize;

//丽日百货
public class Lrls_CustomLocalize extends Cmls_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "18054 build 2014.12.17";
	}
	
	
	public SaleBS createSaleBS()
	{
		return new Lrls_SaleBS();
	}
	
	public MutiSelectBS createMutiSelectBS()
	{
		return new Lrls_MutiSelectBS();
	}
	
    public SaleBillMode createSaleBillMode()
    {
    	return new custom.localize.Lrls.Lrls_SaleBillMode();
    }
}
