package custom.localize.Cdkg;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bstd.Bstd_CustomLocalize;

/*
 * 承德宽广
 */
public class Cdkg_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "1.0.4 build 2013.02.28";
	}
	
	public SaleBS createSaleBS()
	{
		return new Cdkg_SaleBS();
	}
	
	public SaleBillMode createSaleBillMode()
	{
		return new Cdkg_SaleBillMode();
	}
}
