package custom.localize.Dxzy;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.SaleBS;

import custom.localize.Bhcm.Bhcm_CustomLocalize;

//新朝阳——单品管理POS
public class Dxzy_CustomLocalize extends Bhcm_CustomLocalize
{
    public String getAssemblyVersion()
    {
    	return "10002 build 2014.09.14";
    }
    	 
	/*public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Dxzy.Dxzy_SaleBillMode();
	}*/
    
    public DataService createDataService()
	{
		return new custom.localize.Dxzy.Dxzy_DataService();
	} 
	
	public SaleBS createSaleBS()
	{
		return new custom.localize.Dxzy.Dxzy_SaleBS();
	}
	
	public NetService createNetService()
	{
		return new custom.localize.Dxzy.Dxzy_NetService();
	}  	
}
