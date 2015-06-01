package custom.localize.Nmzd;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.MutiSelectBS;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.GiftBillMode;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.PrintTemplate.YyySaleBillMode;

import custom.localize.Cmls.Cmls_CustomLocalize;

public class Nmzd_CustomLocalize extends Cmls_CustomLocalize
{
	public MzkInfoQueryBS createMzkInfoQueryBS()
	{
		return new Nmzd_MzkInfoQueryBS();
	}
	
	public String getAssemblyVersion()
    {
    	return "1.0.0 bulid 2013.4.12";
    }
	
	public GiftBillMode createGiftMode()
	{
		return new Nmzd_GiftBillMode();
	}
	
    public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Nmzd.Nmzd_SaleBillMode();
	}
    
    public DataService createDataService()
	{
		return new custom.localize.Nmzd.Nmzd_DataService();
	} 
    
	public SaleBS createSaleBS()
    {
		return new Nmzd_SaleBS();
    }
	
	public YyySaleBillMode createYyySaleBillMode()
	{
		return new Nmzd_YyySaleBillMode();
	}
	
	public MenuFuncBS createMenuFuncBS()
	{
		return new Nmzd_MenuFuncBS();
	}
    
	public MutiSelectBS createMutiSelectBS()
	{
		return new Nmzd_MutiSelectBS();
	}
	
    public LoadSysInfo createLoadSysInfo()
    {
        return new Nmzd_LoadSysInfo();
    }
    
	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new Nmzd_HykInfoQueryBS();
	}
	
    public NetService createNetService()
	{
		return new Nmzd_NetService();
	}
}
