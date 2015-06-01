package custom.localize.Ajbs;

import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import custom.localize.Bstd.Bstd_CustomLocalize;

//驻马店爱家
public class Ajbs_CustomLocalize extends Bstd_CustomLocalize 
{
    public String getAssemblyVersion()
    {
    	return "1.0.13 bulid 2012.07.17";
    }
    
    public SaleBS createSaleBS()
    {
		return new Ajbs_SaleBS();
    } 
    
    public DataService createDataService()
    {
    	return new Ajbs_DataService();
    }
    
	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new Ajbs_HykInfoQueryBS();
	}	
	
	public MenuFuncBS createMenuFuncBS()
	{
		return new Ajbs_MenuFuncBS();
	}  
	
    public LoadSysInfo createLoadSysInfo()
    {
        return new Ajbs_LoadSysInfo();
    }
    
	public SaleBillMode createSaleBillMode()
	{
		return new Ajbs_SaleBillMode();
	}
}
