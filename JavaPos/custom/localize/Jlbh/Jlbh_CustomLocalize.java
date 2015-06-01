package custom.localize.Jlbh;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Cmls.Cmls_CustomLocalize;

public class Jlbh_CustomLocalize extends Cmls_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "1.2.1 bulid 2012.09.18";
	}
	
    public SaleBS createSaleBS()
    {
		return new custom.localize.Jlbh.Jlbh_SaleBS();
    }
    
    public NetService createNetService()
	{
		return new custom.localize.Jlbh.Jlbh_NetService();
	}
    
    public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Jlbh.Jlbh_SaleBillMode();
	}
    
}
