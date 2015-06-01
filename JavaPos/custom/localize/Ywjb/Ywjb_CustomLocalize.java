package custom.localize.Ywjb;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Logic.FjkInfoQueryBS;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bcrm.Bcrm_CustomLocalize;

public class Ywjb_CustomLocalize extends Bcrm_CustomLocalize
{
	public FjkInfoQueryBS createFjkInfoQueryBS()
	{
		return new Ywjb_FjkInfoQueryBS();
	}
	
	public String getAssemblyVersion()
    {
    	return "1.2.1 bulid 2009.01.06";
    }
	
    public Color getStatusBarColor() 
    {
    	return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
    }
    
    public LoadSysInfo createLoadSysInfo()
    {
        return new Ywjb_LoadSysInfo();
    }
    
	public NetService createNetService()
	{
		return new custom.localize.Ywjb.Ywjb_NetService();
	}  	
	
    public SaleBS createSaleBS()
    {
		return new custom.localize.Ywjb.Ywjb_SaleBS();
    }
    
	public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Ywjb.Ywjb_SaleBillMode();
	}
	
	public CreatePayment createCreatePayment()
    {
		return new custom.localize.Ywjb.Ywjb_CreatePayment();
    }
	
	public MzkInfoQueryBS createMzkInfoQueryBS()
	{
		return new Ywjb_MzkInfoQueryBS();
	}
	
	public MenuFuncBS createMenuFuncBS()
	{
		return new Ywjb_MenuFuncBS();
	}
	
    public AccessLocalDB createAccessLocalDB()
    {
		return new custom.localize.Ywjb.Ywjb_AccessLocalDB();
    }
    
    public AccessDayDB createAccessDayDB()
    {
		return new custom.localize.Ywjb.Ywjb_AccessDayDB();
    }
    
	public DataService createDataService()
	{
		return new custom.localize.Ywjb.Ywjb_DataService();

	}
}
