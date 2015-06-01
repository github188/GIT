package custom.localize.Nxmx;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;

import  custom.localize.Bszm.Bszm_CustomLocalize;

public class Nxmx_CustomLocalize extends Bszm_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "1001 build 2011.10.24";
	}
	
    public CreatePayment createCreatePayment()
    {
    	return new Nxmx_CreatePayment();
    }
    
	public SaleBS createSaleBS()
	{
		return new Nxmx_SaleBS();
	}
}
