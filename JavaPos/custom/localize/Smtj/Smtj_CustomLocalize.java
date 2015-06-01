package custom.localize.Smtj;

import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Logic.SaleBS3Modify;
import com.efuture.javaPos.Logic.SaleBS5Pay;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;


import custom.localize.Cmls.Cmls_CustomLocalize;

public class Smtj_CustomLocalize extends Cmls_CustomLocalize
{
    public String getAssemblyVersion()
    {
    	return "11762 build 2012.09.26";
    }
    
    public SaleBS createSaleBS()
	{
		return new custom.localize.Smtj.Smtj_SaleBS();
	}
    
    public MenuFuncBS createMenuFuncBS()
	{
		return new custom.localize.Smtj.Smtj_MenuFuncBS();
	}
    
    public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Smtj.Smtj_SaleBillMode();
	}
}
