package custom.localize.Bcrm;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bhls.Bhls_CustomLocalize;

public class Bcrm_CustomLocalize extends Bhls_CustomLocalize 
{
    public String getAssemblyVersion()
    {
    	return "1.2.5 bulid 2012.09.26";
    }
    
    public SaleBS createSaleBS()
    {
		return new custom.localize.Bcrm.Bcrm_SaleBS();
    }
    
	public CreatePayment createCreatePayment()
    {
		return new custom.localize.Bcrm.Bcrm_CreatePayment();
    }
	
    public AccessBaseDB createAccessBaseDB()
    {
    	return new custom.localize.Bcrm.Bcrm_AccessBaseDB();
    }
    
    public AccessLocalDB createAccessLocalDB()
    {
    	return new custom.localize.Bcrm.Bcrm_AccessLocalDB();
    }
    
	public DataService createDataService()
	{
		return new custom.localize.Bcrm.Bcrm_DataService();
	}    
	
	public NetService createNetService()
	{
		return new custom.localize.Bcrm.Bcrm_NetService();
	}  	
    
	public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Bcrm.Bcrm_SaleBillMode();
	}
	
    public LoadSysInfo createLoadSysInfo()
    {
        return new Bcrm_LoadSysInfo();
    }
    
    public TaskExecute createTaskExecute()
    {
    	return new Bcrm_TaskExecute();
    }
}
