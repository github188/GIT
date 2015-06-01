package custom.localize.Xdmg;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Cmls.Cmls_CustomLocalize;

// 西安大明宫
public class Xdmg_CustomLocalize extends Cmls_CustomLocalize
{
    public String getAssemblyVersion()
    {
    	return "16803 build 2014.03.20";
    }
    
    public SaleBS createSaleBS()
    {
    	return new custom.localize.Xdmg.Xdmg_SaleBS();
    }
    
    public SaleBillMode createSaleBillMode()
    {
    	return new custom.localize.Xdmg.Xdmg_SaleBillMode();
    }
}
