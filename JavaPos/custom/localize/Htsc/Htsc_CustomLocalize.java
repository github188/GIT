package custom.localize.Htsc;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.MutiSelectBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Cbbh.Cbbh_AccessDayDB;
import custom.localize.Cmjb.Cmjb_SaleBillMode;
import custom.localize.Cmls.Cmls_CustomLocalize;
import custom.localize.Lrls.Lrls_MutiSelectBS;

public class Htsc_CustomLocalize extends Cmls_CustomLocalize 
{
    public String getAssemblyVersion()
    {
    	return "1.2.5 bulid 2014.04.10";
    }
    
    public SaleBS createSaleBS()
    {
		return new custom.localize.Htsc.Htsc_SaleBS();
    }
	
	public NetService createNetService()
	{
		return new custom.localize.Htsc.Htsc_NetService();
	}  
	
	public MutiSelectBS createMutiSelectBS()
	{
		return  new custom.localize.Htsc.Htsc_MutiSelectBS();
	}
	
	public DataService createDataService()
	{
		return new custom.localize.Htsc.Htsc_DataService();
	} 
	
	public SaleBillMode createSaleBillMode()
	{
		return new  Htsc_SaleBillMode();
	}
	
	public AccessLocalDB createAccessLocalDB()
	{
		return new Htsc_AccessLocalDB();
	}
}
