package custom.localize.Bszm;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bstd.Bstd_CustomLocalize;

public class Bszm_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "1.0.21 build 2012.06.26.1";
	}

	public DataService createDataService()
	{
		return new Bszm_DataService();
	}

	public AccessBaseDB createAccessBaseDB()
	{
		return new Bszm_AccessBaseDB();
	}

	public AccessLocalDB createAccessLocalDB()
	{
		return new Bszm_AccessLocalDB();
	}

	public AccessDayDB createAccessDayDB()
	{
		return new Bszm_AccessDayDB();
	}
	
	public NetService createNetService()
	{
		return new Bszm_NetService();
	}
	
	public SaleBillMode createSaleBillMode()
	{
		return new Bszm_SaleBillMode();
	}
	
	public SaleBS createSaleBS()
	{
		return new Bszm_SaleBS();
	}	
	
	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new Bszm_HykInfoQueryBS();
	}	
	
	public TaskExecute createTaskExecute()
	{
		return new Bszm_TaskExecute();
	}
	
	public LoadSysInfo createLoadSysInfo()
	{
		return new Bszm_LoadSysInfo();
	}
		
}
