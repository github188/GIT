package custom.localize.Hzjb;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Logic.FjkInfoQueryBS;
import com.efuture.javaPos.Logic.LoginBS;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bcrm.Bcrm_CustomLocalize;

public class Hzjb_CustomLocalize extends Bcrm_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "1.3.11 bulid 2012.07.12";
	}
	
    public LoginBS createLoginBS()
    {
        return new Hzjb_LoginBS();
    }	
    
	public DisplaySaleTicketBS createDisplaySaleTicketBS()
	{
		return new Hzjb_DisplaySaleTicketBS();
	}

	public FjkInfoQueryBS createFjkInfoQueryBS()
	{
		return new Hzjb_FjkInfoQueryBS();
	}

	public Color getStatusBarColor()
	{
		return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	}

	public LoadSysInfo createLoadSysInfo()
	{
		return new Hzjb_LoadSysInfo();
	}

	public NetService createNetService()
	{
		return new custom.localize.Hzjb.Hzjb_NetService();
	}

	public SaleBS createSaleBS()
	{
		return new custom.localize.Hzjb.Hzjb_SaleBS();
	}

	public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Hzjb.Hzjb_SaleBillMode();
	}

	public CreatePayment createCreatePayment()
	{
		return new custom.localize.Hzjb.Hzjb_CreatePayment();
	}

	public MzkInfoQueryBS createMzkInfoQueryBS()
	{
		return new Hzjb_MzkInfoQueryBS();
	}

	public MenuFuncBS createMenuFuncBS()
	{
		return new Hzjb_MenuFuncBS();
	}

	public AccessLocalDB createAccessLocalDB()
	{
		return new custom.localize.Hzjb.Hzjb_AccessLocalDB();
	}

	public AccessDayDB createAccessDayDB()
	{
		return new custom.localize.Hzjb.Hzjb_AccessDayDB();
	}

	public DataService createDataService()
	{
		return new custom.localize.Hzjb.Hzjb_DataService();

	}
}
