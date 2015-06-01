package custom.localize.Ysal;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Cmls.Cmls_CustomLocalize;

public class Ysal_CustomLocalize extends Cmls_CustomLocalize
{
    public String getAssemblyVersion()
    {
    	return "16420 build 2013.12.17";
    }
    
    public SaleBS createSaleBS()
	{
		return new custom.localize.Ysal.Ysal_SaleBS();
	}
    
    public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Ysal.Ysal_SaleBillMode();
	}
    
    public CreatePayment createCreatePayment()
    {
    	return new custom.localize.Ysal.Ysal_CreatePayment();
    }
    
    public NetService createNetService()
	{
		return new custom.localize.Ysal.Ysal_NetService();
	}
    
    public AccessBaseDB createAccessBaseDB()
    {
    	return new custom.localize.Ysal.Ysal_AccessBaseDB();
    }
    
}
