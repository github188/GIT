package custom.localize.Cbcp;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Logic.SyySaleStatBS;
import com.efuture.javaPos.PrintTemplate.CardSaleBillMode;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.PrintTemplate.YyySaleBillMode;

import custom.localize.Bcrm.Bcrm_CustomLocalize;

/**
 * 重百百货
 * @author wangy
 *
 */
public class Cbcp_CustomLocalize extends Bcrm_CustomLocalize
{

	public String getAssemblyVersion()
    {
        return "1.7.26 build 2015.05.15";
    }
	
	public AccessDayDB createAccessDayDB()
	{
		return new Cbcp_AccessDayDB();
	}
	
	public SaleBillMode createSaleBillMode()
	{
		return new Cbcp_SaleBillMode();
	}
	
	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new Cbcp_HykInfoQueryBS();
	}
	
	public MzkInfoQueryBS createMzkInfoQueryBS()
	{
		return new Cbcp_MzkInfoQueryBS();
	}
	
	public AccessLocalDB createAccessLocalDB()
	{
		return new Cbcp_AccessLocalDB();
	}
	
	public AccessBaseDB createAccessBaseDB()
	{
		return new Cbcp_AccessBaseDB();
	}
	
	public SaleBS createSaleBS()
    {
        return new Cbcp_SaleBS();
    }
	
	public NetService createNetService()
    {
    	return new Cbcp_NetService();
    }
	
	public DataService createDataService()
    {
    	return new Cbcp_DataService();
    }


	public CardSaleBillMode createCardSaleBillMode()
	{
		return new custom.localize.Cbbh.Cbbh_CardSaleBillMode();
	}
	
	public SyySaleStatBS createSyySaleStatBS()
	{
	    	return new Cbcp_SyySaleStatBS();
	}
	
	public YyySaleBillMode createYyySaleBillMode()
	{
	    	return new Cbcp_YyySaleBillMode();
	}
	
	public LoadSysInfo createLoadSysInfo()
    {
        return new Cbcp_LoadSysInfo();
    }
}
