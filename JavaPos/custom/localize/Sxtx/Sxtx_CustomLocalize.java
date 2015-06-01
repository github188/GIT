package custom.localize.Sxtx;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bstd.Bstd_CustomLocalize;

public class Sxtx_CustomLocalize  extends Bstd_CustomLocalize 
{
    public String getAssemblyVersion()
    {
    	return "1.0.0 bulid 2014.05.23";
    }
    
	public static boolean crmMode()
	{
		return true;
	}
	
    public SaleBS createSaleBS()
    {
		return new custom.localize.Sxtx.Sxtx_SaleBS();
    }
    
	public SaleBillMode createSaleBillMode()
	{
		return new Sxtx_SaleBillMode();
	}
	
	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new Sxtx_HykInfoQueryBS();
	}
	
	public NetService createNetService()
	{
		return new Sxtx_NetService();
	}
	
	public DataService createDataService()
	{
		return new Sxtx_DataService();
	}
	
	public AccessLocalDB createAccessLocalDB()
	{
		return new Sxtx_AccessLocalDB();
	}
	
}
