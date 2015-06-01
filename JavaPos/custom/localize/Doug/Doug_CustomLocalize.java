package custom.localize.Doug;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bhls.Bhls_CustomLocalize;

public class Doug_CustomLocalize extends Bhls_CustomLocalize
{

    public String getAssemblyVersion()
    {
    	return "11135 build 2012.02.21";
    }
    
    public CreatePayment createCreatePayment()
    {
		return new custom.localize.Doug.Doug_CreatePayment();
    }
    
    public SaleBS createSaleBS()
    {
		return new custom.localize.Doug.Doug_SaleBS();
    }    
    
    public HykInfoQueryBS createHykInfoQueryBS()
    {
		return new custom.localize.Doug.Doug_HykInfoQueryBS();
    }
    
    public NetService createNetService()
    {
		return new custom.localize.Doug.Doug_NetService();
    }
    
    public AccessLocalDB createAccessLocalDB()
    {
		return new custom.localize.Doug.Doug_AccessLocalDB();
    }
    
    public DataService createDataService()
    {
		return new custom.localize.Doug.Doug_DataService();
    }
    
    public AccessBaseDB createAccessBaseDB()
    {
		return new custom.localize.Doug.Doug_AccessBaseDB();
    }    
    
    public SaleBillMode createSaleBillMode()
    {
		return new custom.localize.Doug.Doug_SaleBillMode();
    }  


}
