package custom.localize.Cctd;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bstd.Bstd_CustomLocalize;

//卓展超市
public class Cctd_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
	{
		// 主版本号 . 子版本号 [ 修正版本号 [. 编译版本号 ]]
		return "18224 build 2015.03.11";
	}

	public SellType createSellType()
	{
		return new Cctd_SellType();
	}

	public DataService createDataService()
	{
		return new custom.localize.Cctd.Cctd_DataService();
	}

	public AccessLocalDB createAccessLocalDB()
	{
		return new Cctd_AccessLocalDB();
	}

	public SaleBS createSaleBS()
	{
		return new Cctd_SaleBS();
	}

	public NetService createNetService()
	{
		return new Cctd_NetService();
	}

	public SaleBillMode createSaleBillMode()
	{
		return new Cctd_SaleBillMode();
	}

	public MenuFuncBS createMenuFuncBS()
	{
		return new Cctd_MenuFuncBS();
	}

	public AccessDayDB createAccessDayDB()
	{
		return new Cctd_AccessDayDB();
	}

	public DisplaySaleTicketBS createDisplaySaleTicketBS()
	{
		return new Cctd_DisplaySaleTicketBS();
	}
}
