package custom.localize.Sfks;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bhls.Bhls_CustomLocalize;

public class Sfks_CustomLocalize extends Bhls_CustomLocalize 
{
    public String getAssemblyVersion()
    {
    	return "1.5.2 bulid 2008.10.27";
    }
    
    public CreatePayment createCreatePayment()
    {
		return new custom.localize.Sfks.Sfks_CreatePayment();
    }
    
    public SaleBS createSaleBS()
    {
		return new custom.localize.Sfks.Sfks_SaleBS();
    }    
    
    public HykInfoQueryBS createHykInfoQueryBS()
    {
		return new custom.localize.Sfks.Sfks_HykInfoQueryBS();
    }
    
    public NetService createNetService()
    {
		return new custom.localize.Sfks.Sfks_NetService();
    }
    
    public AccessLocalDB createAccessLocalDB()
    {
		return new custom.localize.Sfks.Sfks_AccessLocalDB();
    }
    
    public DataService createDataService()
    {
		return new custom.localize.Sfks.Sfks_DataService();
    }
    
    public AccessBaseDB createAccessBaseDB()
    {
		return new custom.localize.Sfks.Sfks_AccessBaseDB();
    }    
    
    public SaleBillMode createSaleBillMode()
    {
		return new custom.localize.Sfks.Sfks_SaleBillMode();
    }  
}
