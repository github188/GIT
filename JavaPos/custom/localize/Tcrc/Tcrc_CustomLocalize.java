package custom.localize.Tcrc;

import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bhls.Bhls_CustomLocalize;


public class Tcrc_CustomLocalize extends Bhls_CustomLocalize
{
    public String getAssemblyVersion()
    {
        return "11657 build 2012.08.29";
    }
    
    
	public DataService createDataService()
	{
		return new custom.localize.Tcrc.Tcrc_DataService();
	}   

    public HykInfoQueryBS createHykInfoQueryBS()
    {
    	return new Tcrc_HykInfoQueryBS();
    }
    
    public SaleBillMode createSaleBillMode()
    {
        return new custom.localize.Tcrc.Tcrc_SaleBillMode();
    }
    
    public SaleBS createSaleBS()
    {
		return new custom.localize.Tcrc.Tcrc_SaleBS();
    }
}
