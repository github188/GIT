package custom.localize.Hycs;

import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;

import custom.localize.Bstd.Bstd_CustomLocalize;

public class Hycs_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
	{
		// 主版本号 . 子版本号 [ 修正版本号 [. 编译版本号 ]]
		return "20000 build 2014.11.17";
	}

	public AccessLocalDB createAccessLocalDB()
	{
		return new Hycs_AccessLocalDB();
	}

	public SaleBS createSaleBS()
	{
		return new Hycs_SaleBS();
	}
//
//	public AccessLocalDB createAccessLocalDB()
//	{
//		return new Hhdl_AccessLocalDB();
//	}
//
//	public SaleBillMode createSaleBillMode()
//	{
//		return new Hhdl_SaleBillMode();
//	}
//
	public DataService createDataService()
	{
		return new Hycs_DataService();
	}
//
//	public CreatePayment createCreatePayment()
//	{
//		return new Hhdl_CreatePayment();
//	}
//
	public Hycs_HykInfoQueryBS createHykInfoQueryBS()
	{
		return new Hycs_HykInfoQueryBS();
	}
	
//    public CreatePayment createCreatePayment()
//    {
//		return new Webs_CreatePayment();
//    }
    
	public MzkInfoQueryBS createMzkInfoQueryBS()
	{
		return new Hycs_MzkInfoQueryBS();
	}
}
