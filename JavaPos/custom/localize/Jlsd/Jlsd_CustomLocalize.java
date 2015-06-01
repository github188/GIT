package custom.localize.Jlsd;



import com.efuture.javaPos.Logic.SaleBS;

import custom.localize.Bhcm.Bhcm_CustomLocalize;


public class Jlsd_CustomLocalize extends Bhcm_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "9924 build 2011.10.13";
	}

	public SaleBS createSaleBS()
	{
		return new custom.localize.Jlsd.Jlsd_SaleBS();
	}
	
//	  public Bhcm_SaleBillMode createSaleBillMode()
//		{
//			return new custom.localize.Jlsd.Jlsd_SaleBillMode();
//		}

}
