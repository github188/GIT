package custom.localize.Jjls;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Cmls.Cmls_CustomLocalize;
//九江联盛
public class Jjls_CustomLocalize extends Cmls_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "14555 build 2013.09.10";
	}

	public SaleBS createSaleBS()
	{
		return new custom.localize.Jjls.Jjls_SaleBS();
	}

	public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Jjls.Jjls_SaleBillMode();
	}

	public AccessLocalDB createAccessLocalDB()
	{
		return new custom.localize.Jjls.Jjls_AccessLocalDB();
	}

	public NetService createNetService()
	{
		return new custom.localize.Jjls.Jjls_NetService();
	}

	public MenuFuncBS createMenuFuncBS()
	{
		return new custom.localize.Jjls.Jjls_MenuFuncBS();
	}
}
