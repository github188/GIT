package custom.localize.Bcsf;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bstd.Bstd_CustomLocalize;

public class Bcsf_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "1.1.1 build 2014.11.05";
	}

	public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Bcsf.Bcsf_SaleBillMode();
	}

	public AccessLocalDB createAccessLocalDB()
	{
		return new Bcsf_AccessLocalDB();
	}

	public DataService createDataService()
	{
		return new Bcsf_DataService();
	}

	public NetService createNetService()
	{
		return new custom.localize.Bcsf.Bcsf_NetService();
	}

	public CreatePayment createCreatePayment()
	{
		return new Bcsf_CreatePayment();
	}

	public SaleBS createSaleBS()
	{
		return new custom.localize.Bcsf.Bcsf_SaleBS();
	}

	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new custom.localize.Bcsf.Bcsf_HykInfoQueryBS();
	}

	public TaskExecute createTaskExecute()
	{
		return new Bcsf_TaskExecute();
	}

	public Printer createPrinter(String name)
	{
		return new Bcsf_Printer(name);
	}

	public MenuFuncBS createMenuFuncBS()
	{
		return new Bcsf_MenuFuncBS();
	}
}
