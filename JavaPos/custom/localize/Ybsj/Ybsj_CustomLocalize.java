package custom.localize.Ybsj;

import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.LoginBS;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;

import custom.localize.Bstd.Bstd_CustomLocalize;

public class Ybsj_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
	{
		//主版本号 . 子版本号 [ 修正版本号 [. 编译版本号 ]]
		return "1.0.2 build 2012.07.30";
	}
	

	public SaleBS createSaleBS()
	{
		return new Ybsj_SaleBS();
	}
	
	public AccessLocalDB createAccessLocalDB()
	{
		return new Ybsj_AccessLocalDB();
	}


	public LoadSysInfo createLoadSysInfo()
	{
		return new Ybsj_LoadSysInfo();
	}

	public LoginBS createLoginBS()
	{
		return new Ybsj_LoginBS();
	}

	public CreatePayment createCreatePayment()
	{
		return new Ybsj_CreatePayment();
	}
	
/*	public DataService createDataService()
	{
		return new Ybsj_DataService();
	}
	
	public MzkInfoQueryBS createMzkInfoQueryBS()
	{
		return new Ybsj_MzkInfoQueryBS();
	}
	
	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new Ybsj_HykInfoQueryBS();
	}*/

}
