package custom.localize.Hrsl;

import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Logic.CouponQueryInfoBS;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.LoginBS;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bstd.Bstd_CustomLocalize;
//哈尔滨松雷超市
public class Hrsl_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
	{
		//主版本号 . 子版本号 [ 修正版本号 [. 编译版本号 ]]
		return "18200 build 2015.02.10";
	}
	
	public DataService createDataService()
	{
		return new Hrsl_DataService();
	}

	public SaleBS createSaleBS()
	{
		return new Hrsl_SaleBS();
	}
	
	public AccessLocalDB createAccessLocalDB()
	{
		return new Hrsl_AccessLocalDB();
	}

	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new Hrsl_HykInfoQueryBS();
	}

	public LoadSysInfo createLoadSysInfo()
	{
		return new Hrsl_LoadSysInfo();
	}

	public LoginBS createLoginBS()
	{
		return new Hrsl_LoginBS();
	}

	public CreatePayment createCreatePayment()
	{
		return new Hrsl_CreatePayment();
	}

	public MzkInfoQueryBS createMzkInfoQueryBS()
	{
		return new Hrsl_MzkInfoQueryBS();
	}
	
	public SaleBillMode createSaleBillMode()
	{
		return new Hrsl_SaleBillMode();
	}
	
	public CouponQueryInfoBS createCouponQueryInfoBS(){
		return new Hrsl_CouponQueryInfoBS();
	}
}
