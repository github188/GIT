package custom.localize.Bhcm;

import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.PrintTemplate.YyySaleBillMode;

import custom.localize.Bcrm.Bcrm_CustomLocalize;

public class Bhcm_CustomLocalize extends Bcrm_CustomLocalize 
{
    public String getAssemblyVersion()
    {
    	return "9719 build 2011.04.22";
    }
    
    public CreatePayment createCreatePayment()
    {
        return new custom.localize.Bhcm.Bhcm_CreatePayment();
    }
    
    public SaleBS createSaleBS()
    {
		return new custom.localize.Bhcm.Bhcm_SaleBS();
    }
    
	public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Bhcm.Bhcm_SaleBillMode();
	}
	
	public DataService createDataService()
	{
		return new custom.localize.Bhcm.Bhcm_DataService();
	} 
	
	public YyySaleBillMode createYyySaleBillMode()
	{
		return new custom.localize.Bhcm.Bhcm_YyySaleBillMode();
	} 
	
/*	 public HykInfoQueryBS createHykInfoQueryBS()
	 {
	        return new Bhcm_HykInfoQueryBS();
	 }*/
}
