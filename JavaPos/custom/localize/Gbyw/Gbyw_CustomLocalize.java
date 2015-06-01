package custom.localize.Gbyw;

import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.LoginBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;

import custom.localize.Bstd.Bstd_CustomLocalize;

public class Gbyw_CustomLocalize extends Bstd_CustomLocalize
{
	// 宜佳旺
	public String getAssemblyVersion()
	{
		return "1.0.6 build 2015.02.09";
	}

	public AccessDayDB createAccessDayDB()
	{
		return new Gbyw_AccessDayDB();
	}

	public SaleBS createSaleBS()
	{
		return new Gbyw_SaleBS();
	}

	public DataService createDataService()
	{
		return new Gbyw_DataService();
	}

	public CreatePayment createCreatePayment()
	{
		return new Gbyw_CreatePayment();
	}

	public AccessLocalDB createAccessLocalDB()
	{
		return new Gbyw_AccessLocalDB();
	}

	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new Gbyw_HykInfoQueryBS();
	}

	public LoginBS createLoginBS()
	{
		return new Gbyw_LoginBS();
	}

}
