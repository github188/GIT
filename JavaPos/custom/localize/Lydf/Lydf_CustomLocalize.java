package custom.localize.Lydf;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bstd.Bstd_CustomLocalize;

/*
 * 临沂东方
*/
public class Lydf_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
	{
		// 主版本号 . 子版本号 [ 修正版本号 [. 编译版本号 ]]
		return "1.0.12 build 2013.08.07";
	}

	public SaleBillMode createSaleBillMode()
	{
		return new Lydf_SaleBillMode();
	}

	public AccessDayDB createAccessDayDB()
	{
		return new Lydf_AccessDayDB();
	}

	public AccessLocalDB createAccessLocalDB()
	{
		return new Lydf_AccessLocalDB();
	}

	public SaleBS createSaleBS()
	{
		return new Lydf_SaleBS();
	}

	public NetService createNetService()
	{
		return new Lydf_NetService();
	}
}
