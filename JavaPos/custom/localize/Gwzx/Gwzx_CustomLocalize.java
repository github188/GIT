package custom.localize.Gwzx;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bcrm.Bcrm_CustomLocalize;

public class Gwzx_CustomLocalize extends Bcrm_CustomLocalize
{
    public String getAssemblyVersion()
    {
    	return "9169 build 2011.01.26";
    }
    
    public SaleBS createSaleBS()
    {
		return new Gwzx_SaleBS();
    }
    
    public DataService createDataService()
	{
		return new Gwzx_DataService();
	} 
    
    public NetService createNetService()
	{
		return new Gwzx_NetService();
	}
    
    public SaleBillMode createSaleBillMode()
	{
		return new Gwzx_SaleBillMode();
	}
    
    public HykInfoQueryBS createHykInfoQueryBS()
    {
		return new custom.localize.Gwzx.Gwzx_HykInfoQueryBS();
    } 
}
