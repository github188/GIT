package custom.localize.Dxyc;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bstd.Bstd_CustomLocalize;

public class Dxyc_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
    {
        return "1003 build 2012.05.11";
    }
	
    public DataService createDataService()
    {
    	return new Dxyc_DataService();
    }

	 public SaleBillMode createSaleBillMode()
		{
			return new Dxyc_SaleBillMode();
		}
	 
	 public SaleBS createSaleBS()
	    {
			return new Dxyc_SaleBS();
	    }
	 
	   public NetService createNetService()
	    {
	    	return new Dxyc_NetService();
	    }
}
