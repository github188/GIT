 package custom.localize.Bjkl;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Logic.GoodsInfoQueryBS;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Logic.WithdrawBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.PayinBillMode;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bstd.Bstd_CustomLocalize;

/**
 * 
 * 北京京客隆
 * 
 */
public class Bjkl_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "1.1.0 build 2014.11.07";
	}

	public SaleBS createSaleBS()
	{
		return new Bjkl_SaleBS();
	}

	public DataService createDataService()
	{
		return new Bjkl_DataService();
	}

	public NetService createNetService()
	{
		return new Bjkl_NetService();
	}

	public AccessBaseDB createAccessBaseDB()
	{
		return new Bjkl_AccessBaseDB();
	}

	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new Bjkl_HykInfoQueryBS();
	}

	public MenuFuncBS createMenuFuncBS()
	{
		return new Bjkl_MenuFuncBS();
	}

	public SaleBillMode createSaleBillMode()
	{
		return new Bjkl_SaleBillMode();
	}

	public AccessLocalDB createAccessLocalDB()
	{
		return new Bjkl_AccessLocalDB();
	}

	public MzkInfoQueryBS createMzkInfoQueryBS()
	{
		return new Bjkl_MzkInfoQueryBS();
	}

	public CreatePayment createCreatePayment()
	{
		return new Bjkl_CreatePayment();
	}

	public GoodsInfoQueryBS createGoodsInfoQueryBS()
	{
		return new Bjkl_GoodsInfoQueryBS();
	}
	
	public WithdrawBS createWithdrawBS()
	{
		return new Bjkl_WithdrawBS();
	}
	
	public PayinBillMode createPayinBillMode()
	{
		return new Bjkl_PayinBillMode();
	}	
	
	public TaskExecute createTaskExecute()
	{
		return new Bjkl_TaskExecute();
	}
}
