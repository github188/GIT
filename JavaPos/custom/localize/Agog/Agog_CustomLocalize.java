package custom.localize.Agog;

import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;

import custom.localize.Bstd.Bstd_CustomLocalize;

//AGOGO KTV
public class Agog_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "1.0.6 bulid 2013.09.23";
	}

	public SaleBS createSaleBS()
	{
		return new Agog_SaleBS();
	}

	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new Agog_HykInfoQueryBS();
	}

	public CreatePayment createCreatePayment()
	{
		return new Agog_CreatePayment();
	}

	public AccessLocalDB createAccessLocalDB()
	{
		return new Agog_AccessLocalDB();
	}

	public DataService createDataService()
	{
		return new Agog_DataService();
	}

}
