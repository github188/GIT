package custom.localize.Jdhx;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Logic.SetSystemTimeBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bstd.Bstd_CustomLocalize;

/**
 * 
 * 江都红信
 *
 */
public class Jdhx_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
	{
		// 主版本号 . 子版本号 [ 修正版本号 [. 编译版本号 ]]
		return "18179 build 2015.02.04";
	}
	public SaleBS createSaleBS()
	{
		return new  Jdhx_SaleBS();
	}
	
	public AccessBaseDB createAccessBaseDB()
	{
		return new Jdhx_AccessBaseDB();
	}
	
	public AccessDayDB createAccessDayDB()
	{
		return new Jdhx_AccessDayDB();
	}
	
	public AccessLocalDB createAccessLocalDB()
	{
		return new Jdhx_AccessLocalDB();
	}
	
	public NetService createNetService()
	{
		return new Jdhx_NetService();
	}
	public DataService createDataService()
	{
		return new Jdhx_DataService();
	}
	
	public DisplaySaleTicketBS createDisplaySaleTicketBS()
	{
		return new Jdhx_DisplaySaleTicketBS();
	}
	
	public SaleBillMode createSaleBillMode()
	{
		return new Jdhx_SaleBillMode();
	}

	public Printer createPrinter(String name)
	{
		return new Jdhx_Printer(name);
	}
	
	public SetSystemTimeBS createSetSystemTimeBS()
	{
		return new Jdhx_SetSystemTimeBS();
	}
	
	public MenuFuncBS createMenuFuncBS()
	{
		return new Jdhx_MenuFuncBS();
	}

	public TaskExecute createTaskExecute()
	{
		return new Jdhx_TaskExecute();
	}
}
