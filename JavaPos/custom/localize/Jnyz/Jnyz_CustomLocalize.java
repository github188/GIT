package custom.localize.Jnyz;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.BankLogQueryBS;
import com.efuture.javaPos.Logic.CouponQueryInfoBS;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Logic.SyySaleStatBS;
import com.efuture.javaPos.PrintTemplate.HangBillMode;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.PrintTemplate.YyySaleBillMode;

import custom.localize.Cmls.Cmls_CustomLocalize;

public class Jnyz_CustomLocalize extends Cmls_CustomLocalize
{
    public String getAssemblyVersion()
    {
    	return "9707 bulid 2014.11.20";
    }
    
    public SaleBS createSaleBS()
    {
		return new custom.localize.Jnyz.Jnyz_SaleBS();
    }
    
    public NetService createNetService()
	{
		return new custom.localize.Jnyz.Jnyz_NetService();
	}
    
    public DataService createDataService()
	{
		return new Jnyz_DataService();
	} 
    
    public AccessLocalDB createAccessLocalDB()
    {
		return new custom.localize.Jnyz.Jnyz_AccessLocalDB();
    } 
    
    public HykInfoQueryBS createHykInfoQueryBS()
    {
		return new custom.localize.Jnyz.Jnyz_HykInfoQueryBS();
    } 
    
    public SaleBillMode createSaleBillMode()
    {
		return new custom.localize.Jnyz.Jnyz_SaleBillMode();
    } 
    
    public YyySaleBillMode createYyySaleBillMode()
    {
		return new custom.localize.Jnyz.Jnyz_YyySaleBillMode();
    } 
    
    public HangBillMode createHangBillMode()
    {
		return new custom.localize.Jnyz.Jnyz_HangBillMode();
    } 
    
    public MzkInfoQueryBS createMzkInfoQueryBS()
	{
		return new Jnyz_MzkInfoQueryBS();
	}
    
    public Printer createPrinter(String name)
	{
		return new Jnyz_Printer(name);
	}
	
    public CouponQueryInfoBS createCouponQueryInfoBS()
	{
    	return new Jnyz_CouponQueryInfoBS();
	}
    
    public DisplaySaleTicketBS createDisplaySaleTicketBS()
	{
    	return new Jnyz_DisplaySaleTicketBS();
	}
    
    public SyySaleStatBS createSyySaleStatBS()
	{
    	return new Jnyz_SyySaleStatBS();
	}
    
    public BankLogQueryBS createBankCardQueryBS()
    {
	
    	return new Jnyz_BankLogQueryBS();
	}
    
    public MenuFuncBS createMenuFuncBS()
    {
	
    	return new Jnyz_MenuFuncBS();
	}
    
}
