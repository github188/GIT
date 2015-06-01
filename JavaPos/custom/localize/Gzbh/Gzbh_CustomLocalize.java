package custom.localize.Gzbh;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Logic.MzkSeqNoResetBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

public class Gzbh_CustomLocalize extends CustomLocalize
{
	// 广百百货
	public String getAssemblyVersion()
	{
		return "9925 build 2015.01.26";
	}

	public CreatePayment createCreatePayment()
	{
		return new custom.localize.Gzbh.Gzbh_CreatePayment();
	}

	public NetService createNetService()
	{
		return new custom.localize.Gzbh.Gzbh_NetService();
	}

	public SaleBS createSaleBS()
	{
		return new custom.localize.Gzbh.Gzbh_SaleBS();
	}

	public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Gzbh.Gzbh_SaleBillMode();
	}

	public MenuFuncBS createMenuFuncBS()
	{
		return new custom.localize.Gzbh.Gzbh_MenuFuncBS();
	}

	public PaymentMzk createPaymentMzk()
	{
		return new custom.localize.Gzbh.Gzbh_PaymentMzk();
	}

	public AccessBaseDB createAccessBaseDB()
	{
		return new custom.localize.Gzbh.Gzbh_AccessBaseDB();
	}

	public AccessLocalDB createAccessLocalDB()
	{
		return new custom.localize.Gzbh.Gzbh_AccessLocalDB();
	}

	public DataService createDataService()
	{
		return new custom.localize.Gzbh.Gzbh_DataService();
	}

	public TaskExecute createTaskExecute()
	{
		return new custom.localize.Gzbh.Gzbh_TaskExecute();
	}

	public MzkInfoQueryBS createMzkInfoQueryBS()
	{
		return new custom.localize.Gzbh.Gzbh_MzkInfoQueryBS();
	}
	
	public MzkSeqNoResetBS createMzkSeqNoResetBS()
	{
		return new custom.localize.Gzbh.Gzbh_MzkSeqNoResetBS();
	}
	
	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new Gzbh_HykInfoQueryBS();
	}
}
