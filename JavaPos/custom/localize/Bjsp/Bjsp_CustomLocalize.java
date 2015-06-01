package custom.localize.Bjsp;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.SaleBS;


import custom.localize.Cmls.Cmls_CustomLocalize;

//北京上品折扣
public class Bjsp_CustomLocalize extends Cmls_CustomLocalize
{
	public String getAssemblyVersion()
    {
        return "15870 build 2015.03.30";
    }
	
	public DataService createDataService()
    {
    	return new Bjsp_DataService();
    }
	
	public NetService createNetService()
	{
		return new Bjsp_NetService();
	}
	
	public SaleBS createSaleBS()
	{
		return new Bjsp_SaleBS();
	}
}
