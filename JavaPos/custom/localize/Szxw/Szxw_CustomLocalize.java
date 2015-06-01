package custom.localize.Szxw;

import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bhls.Bhls_CustomLocalize;

// 深圳西武
public class Szxw_CustomLocalize extends Bhls_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "9750 build 2011.09.20";
	}

	public SaleBS createSaleBS()
	{
		return new custom.localize.Szxw.Szxw_SaleBS();
	}

	public SellType createSellType()
	{
		return new custom.localize.Szxw.Szxw_SellType();
	}

	public DisplaySaleTicketBS createDisplaySaleTicketBS()
	{
		return new custom.localize.Szxw.Szxw_DisplaySaleTicketBS();
	}

	public CreatePayment createCreatePayment()
	{
		return new custom.localize.Szxw.Szxw_CreatePayment();
	}

	public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Szxw.Szxw_SaleBillMode();
	}

	public AccessLocalDB createAccessLocalDB()
	{
		return new custom.localize.Szxw.Szxw_AccessLocalDB();
	}

	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new custom.localize.Szxw.Szxw_HykInfoQueryBS();
	}

	public MenuFuncBS createMenuFuncBS()
	{
		return new custom.localize.Szxw.Szxw_MenuFuncBS();
	}

	public MzkInfoQueryBS createMzkInfoQueryBS()
	{
		return new custom.localize.Szxw.Szxw_MzkInfoQueryBS();
	}

	public AccessDayDB createAccessDayDB()
	{
		return new custom.localize.Szxw.Szxw_AccessDayDB();
	}
}
