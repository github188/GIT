package custom.localize.Cmjb;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Logic.LoginBS;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Cmls.Cmls_CustomLocalize;

public class Cmjb_CustomLocalize extends Cmls_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "1.0.7 bulid 2014.12.31";
	}

	public LoginBS createLoginBS()
	{
		return new Cmjb_LoginBS();
	}

	public DisplaySaleTicketBS createDisplaySaleTicketBS()
	{
		return new Cmjb_DisplaySaleTicketBS();
	}

	public Color getStatusBarColor()
	{
		return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	}

	public LoadSysInfo createLoadSysInfo()
	{
		return new Cmjb_LoadSysInfo();
	}

	public SaleBS createSaleBS()
	{
		return new Cmjb_SaleBS();
	}

	public SaleBillMode createSaleBillMode()
	{
		return new Cmjb_SaleBillMode();
	}

	/**
	 * public CreatePayment createCreatePayment() { return new
	 * Cmjb_CreatePayment(); }
	 */

	public MzkInfoQueryBS createMzkInfoQueryBS()
	{
		return new Cmjb_MzkInfoQueryBS();
	}

	public MenuFuncBS createMenuFuncBS()
	{
		return new Cmjb_MenuFuncBS();
	}

	public AccessDayDB createAccessDayDB()
	{
		return new Cmjb_AccessDayDB();
	}

	public AccessLocalDB createAccessLocalDB()
	{
		return new Cmjb_AccessLocalDB();
	}

	public DataService createDataService()
	{
		return new Cmjb_DataService();

	}
}
