package custom.localize.Cbbh;

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
public class Cbbh_CustomLocalize extends Bcrm_CustomLocalize
{

	public String getAssemblyVersion()
    {
        return "1.7.104 build 2015.06.01";
    }
	
	public AccessDayDB createAccessDayDB()
	{
		return new Cbbh_AccessDayDB();
	}
	
	public SaleBillMode createSaleBillMode()
	{
		return new Cbbh_SaleBillMode();
	}
	
	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new Cbbh_HykInfoQueryBS();
	}
	
	public MzkInfoQueryBS createMzkInfoQueryBS()
	{
		return new Cbbh_MzkInfoQueryBS();
	}
	
	public AccessLocalDB createAccessLocalDB()
	{
		return new Cbbh_AccessLocalDB();
	}
	
	public AccessBaseDB createAccessBaseDB()
	{
		return new Cbbh_AccessBaseDB();
	}
	
	public SaleBS createSaleBS()
    {
        return new Cbbh_SaleBS();
    }
	
	public NetService createNetService()
    {
    	return new Cbbh_NetService();
    }
	
	public DataService createDataService()
    {
    	return new Cbbh_DataService();
    }


	public CardSaleBillMode createCardSaleBillMode()
	{
		return new custom.localize.Cbbh.Cbbh_CardSaleBillMode();
	}
	
	public SyySaleStatBS createSyySaleStatBS()
	{
	    	return new Cbbh_SyySaleStatBS();
	}
	
	public YyySaleBillMode createYyySaleBillMode()
	{
	    	return new Cbbh_YyySaleBillMode();
	}
	
	public LoadSysInfo createLoadSysInfo()
    {
        return new Cbbh_LoadSysInfo();
    }
}
