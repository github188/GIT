package custom.localize.Xjly;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Cmls.Cmls_CustomLocalize;

// 邢台家乐园
public class Xjly_CustomLocalize extends Cmls_CustomLocalize
{
    public String getAssemblyVersion()
    {
    	return "11261 build 2015.01.13";
    }
    
    public SaleBS createSaleBS()
	{
		return new custom.localize.Xjly.Xjly_SaleBS();
	}
    
    public DataService createDataService()
	{
		return new custom.localize.Xjly.Xjly_DataService();
	}
    
    public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Xjly.Xjly_SaleBillMode();
	}
    
    public AccessDayDB createAccessDayDB()
    {
    	return new custom.localize.Xjly.Xjly_AccessDayDB();
    }
    
    public MenuFuncBS createMenuFuncBS()
    {
    	return new custom.localize.Xjly.Xjly_MenuFuncBS();
    }
    
    public AccessLocalDB createAccessLocalDB()
    {
    	return new custom.localize.Xjly.Xjly_AccessLocalDB();
    }
    
    public LoadSysInfo createLoadSysInfo()
    {
    	return new custom.localize.Xjly.Xjly_LoadSysInfo();
    }
    
    public NetService createNetService()
    {
    	return new custom.localize.Xjly.Xjly_NetService();
    }
}
