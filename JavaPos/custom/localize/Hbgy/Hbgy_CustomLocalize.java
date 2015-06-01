package custom.localize.Hbgy;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bstd.Bstd_CustomLocalize;

/**
 * 
 * 河北秦皇岛广缘超市
 * 
 */
public class Hbgy_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "1.0.7 build 2015.01.26";
	}

	public CreatePayment createCreatePayment()
	{
		return new Hbgy_CreatePayment();
	}

	public AccessBaseDB createAccessBaseDB()
	{
		return new Hbgy_AccessBaseDB();
	}

	public DataService createDataService()
	{
		return new Hbgy_DataService();
	}
	
	public SaleBS createSaleBS()
	{
		return new Hbgy_SaleBS();
	}

	public TaskExecute createTaskExecute()
	{
		return new Hbgy_TaskExecute();
	}

	public AccessLocalDB createAccessLocalDB()
	{
		return new Hbgy_AccessLocalDB();
	}

	public NetService createNetService()
	{
		return new Hbgy_NetService();
	}

	public SaleBillMode createSaleBillMode()
	{
		return new Hhgy_SaleBillMode();
	}
}
