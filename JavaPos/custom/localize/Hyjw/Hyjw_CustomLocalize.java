package custom.localize.Hyjw;

import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;

import custom.localize.Bstd.Bstd_CustomLocalize;

/*
 * 河北宜家旺
 */
public class Hyjw_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
	{
		// 主版本号 . 子版本号 [ 修正版本号 [. 编译版本号 ]]
		return "1.0.1 build 2014.11.05";
	}

	public AccessLocalDB createAccessLocalDB()
	{
		return new Hyjw_AccessLocalDB();
	}

	public DataService createDataService()
	{
		return new Hyjw_DataService();
	}

	public SaleBS createSaleBS()
	{
		return new Hyjw_SaleBS();
	}

	public MzkInfoQueryBS createMzkInfoQueryBS()
	{
		return new Hyjw_MzkInfoQueryBS();
	}

	public CreatePayment createCreatePayment()
	{
		return new Hyjw_CreatePayment();
	}
}
