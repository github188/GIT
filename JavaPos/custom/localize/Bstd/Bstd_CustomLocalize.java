package custom.localize.Bstd;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Logic.GoodsInfoQueryBS;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

public class Bstd_CustomLocalize extends CustomLocalize
{
	public String getAssemblyVersion()
	{
		// 主版本号 . 子版本号 [ 修正版本号 [. 编译版本号 ]]
		return "1.1.97 build 2014.08.25";
	}

	public boolean checkAllowUseBank()
	{
		return true;
	}

	public DataService createDataService()
	{
		return new Bstd_DataService();
	}

	public AccessBaseDB createAccessBaseDB()
	{
		return new Bstd_AccessBaseDB();
	}

	public MzkInfoQueryBS createMzkInfoQueryBS()
	{
		return new Bstd_MzkInfoQueryBS();
	}

	public MenuFuncBS createMenuFuncBS()
	{
		return new Bstd_MenuBS();
	}

	public AccessLocalDB createAccessLocalDB()
	{
		return new Bstd_AccessLocalDB();
	}

	public NetService createNetService()
	{
		return new Bstd_NetService();
	}

	public SaleBS createSaleBS()
	{
		if (GlobalInfo.sysPara.isSuperMarketPop == 'Y')
			return new Bstd_SaleBSU51();

		return new Bstd_SaleBS();
	}

	public GoodsInfoQueryBS createGoodsInfoQueryBS()
	{
		return new Bstd_GoodsInfoQueryBS();
	}

	public DisplaySaleTicketBS createDisplaySaleTicketBS()
	{
		return new Bstd_DisplaySaleTicketBS();
	}

	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new Bstd_HykInfoQueryBS();
	}

	public SaleBillMode createSaleBillMode()
	{
		return new Bstd_SaleBillMode();
	}
}
