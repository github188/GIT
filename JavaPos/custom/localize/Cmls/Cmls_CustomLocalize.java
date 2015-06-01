package custom.localize.Cmls;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bcrm.Bcrm_CustomLocalize;

// 新连锁 V3版本
public class Cmls_CustomLocalize extends Bcrm_CustomLocalize
{
    public String getAssemblyVersion()
    {
    	return "16340 build 2015.01.16";
    }
    
    public SaleBS createSaleBS()
    {
		return new custom.localize.Cmls.Cmls_SaleBS();
    }
    
    public DataService createDataService()
	{
		return new custom.localize.Cmls.Cmls_DataService();
	} 
    
    public NetService createNetService()
	{
		return new custom.localize.Cmls.Cmls_NetService();
	}
    
    public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Cmls.Cmls_SaleBillMode();
	}
}
