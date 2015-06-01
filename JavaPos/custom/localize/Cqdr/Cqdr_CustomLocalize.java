package custom.localize.Cqdr;

import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bhcm.Bhcm_CustomLocalize;

public class Cqdr_CustomLocalize extends Bhcm_CustomLocalize
{
    public String getAssemblyVersion()
    {
    	return "10332 build 2011.11.29";
    }
    
	public MenuFuncBS createMenuFuncBS()
	{
		return new Cqdr_MenuFuncBS();
	}
	
	public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Cqdr.Cqdr_SaleBillMode();
	}
	
	public SaleBS createSaleBS()
	{
		return new custom.localize.Cqdr.Cqdr_SaleBS();
	}
}
