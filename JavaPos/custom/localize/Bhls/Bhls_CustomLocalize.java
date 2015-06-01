package custom.localize.Bhls;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

public class Bhls_CustomLocalize extends CustomLocalize 
{
    public String getAssemblyVersion()
    {
    	return "1.2.5 bulid 2008.01.17";
    }
    
    public SaleBS createSaleBS()
    {
		return new custom.localize.Bhls.Bhls_SaleBS();
    }     
    
    public CreatePayment createCreatePayment()
    {
		return new custom.localize.Bhls.Bhls_CreatePayment();
    } 
    
    public AccessBaseDB createAccessBaseDB()
    {
    	return new custom.localize.Bhls.Bhls_AccessBaseDB();
    }  
    
    public AccessLocalDB createAccessLocalDB()
    {
    	return new custom.localize.Bhls.Bhls_AccessLocalDB();
    }
    
	public DataService createDataService()
	{
		return new custom.localize.Bhls.Bhls_DataService();
	}    
	
	public NetService createNetService()
	{
		return new custom.localize.Bhls.Bhls_NetService();
	}  	
    
	public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Bhls.Bhls_SaleBillMode();
	}
	
	public TaskExecute createTaskExecute()
	{
		return new custom.localize.Bhls.Bhls_TaskExecute();
	}
}
