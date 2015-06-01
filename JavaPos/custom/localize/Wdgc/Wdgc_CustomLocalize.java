package custom.localize.Wdgc;

import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bhcm.Bhcm_CustomLocalize;

//万达广场
public class Wdgc_CustomLocalize extends Bhcm_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "16659 build 2014.02.20";
	}

	public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Wdgc.Wdgc_SaleBillMode();
	}

	public LoadSysInfo createLoadSysInfo()
	{
		return new custom.localize.Wdgc.Wdgc_LoadSysInfo();
	}

	public AccessDayDB createAccessDayDB()
	{
		return new custom.localize.Wdgc.Wdgc_AccessDayDB();
	}

	public SaleBS createSaleBS()
	{
		return new custom.localize.Wdgc.Wdgc_SaleBS();
	}

	public DataService createDataService()
	{
		return new custom.localize.Wdgc.Wdgc_DataService();
	}

	public MenuFuncBS createMenuFuncBS()
	{
		return new custom.localize.Wdgc.Wdgc_MenuFuncBS();
	}

}
