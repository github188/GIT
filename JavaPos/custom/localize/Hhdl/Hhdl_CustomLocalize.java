package custom.localize.Hhdl;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bstd.Bstd_CustomLocalize;

/*
 * 杭州汇德隆
 */
public class Hhdl_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
	{
		// 主版本号 . 子版本号 [ 修正版本号 [. 编译版本号 ]]
		return "1.0.17 build 2014.08.21";
	}

	public DataService createDataService()
	{
		return new Hhdl_DataService();
	}

	public SaleBS createSaleBS()
	{
		return new Hhdl_SaleBS();
	}

	public AccessLocalDB createAccessLocalDB()
	{
		return new Hhdl_AccessLocalDB();
	}

	public SaleBillMode createSaleBillMode()
	{
		return new Hhdl_SaleBillMode();
	}

	public NetService createNetService()
	{
		return new Hhdl_NetService();
	}

	public CreatePayment createCreatePayment()
	{
		return new Hhdl_CreatePayment();
	}

	public LoadSysInfo createLoadSysInfo()
	{
		return new Hhdl_LoadSysInfo();
	}
}
