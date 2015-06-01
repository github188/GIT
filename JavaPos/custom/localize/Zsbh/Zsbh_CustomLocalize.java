/**
 * 
 */
package custom.localize.Zsbh;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Logic.BankLogQueryBS;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.MutiSelectBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Logic.WithdrawBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.PayinBillMode;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bcrm.Bcrm_CustomLocalize;

public class Zsbh_CustomLocalize extends Bcrm_CustomLocalize
{
	public String getAssemblyVersion() {
        return "1.1.13 bulid 2014.12.22";
    }
	
    public TaskExecute createTaskExecute()
    {
    	return new Zsbh_TaskExecute();
    }
	//业务
    public SaleBS createSaleBS() {
        return new Zsbh_SaleBS();
    }
    
    //缴款模板
    public PayinBillMode createPayinBillMode()
	{
		return new Zsbh_PayinBillMode();
	}

    //小票打印模板
    public SaleBillMode createSaleBillMode()
	{
    	if (ConfigClass.CustomItem5 != null)
    	{
    		if (ConfigClass.CustomItem5.split("\\|").length > 0)
    		{
    			if (ConfigClass.CustomItem5.split("\\|")[0].trim().equalsIgnoreCase("ShiYan"))
        		{
        			return new Zsbh_SaleBillMode_ShiYan();
        		}
        		else if (ConfigClass.CustomItem5.split("\\|")[0].trim().equalsIgnoreCase("SuiZhou"))
        		{
        			return new Zsbh_SaleBillMode_SuiZhou();
        		}
    		}
    		
    	}
    	
		return new Zsbh_SaleBillMode();
	}
    
    public CreatePayment createCreatePayment()
    {
		return new Zsbh_CreatePayment();
    }
    
    public Printer createPrinter(String name)
	{
		return new Zsbh_Printer(name);
	}
    
    public NetService createNetService()
    {
    	return new Zsbh_NetService();
    }
    
    public AccessDayDB createAccessDayDB()
    {
    	return new Zsbh_AccessDayDB();
    }  
    
	public WithdrawBS createWithdrawBS()
	{
		return new Zsbh_WithdrawBS();
	}	
	
	public DisplaySaleTicketBS createDisplaySaleTicketBS()
	{
		return new Zsbh_DisplaySaleTicketBS();
	}
	
	public BankLogQueryBS createBankCardQueryBS()
	{
		return new Zsbh_BankLogQueryBS();
	}
	
	public DataService createDataService()
	{
		return new Zsbh_DataService();
	}
	
	public MenuFuncBS createMenuFuncBS()
	{
		return new Zsbh_MenuFuncBS();
	}
	
	public AccessLocalDB createAccessLocalDB()
    {
		return new Zsbh_AccessLocalDB();
    }

    public MutiSelectBS createMutiSelectBS()
	{
		return new Zsbh_MutiSelectBS_ISHB();
	}
}
