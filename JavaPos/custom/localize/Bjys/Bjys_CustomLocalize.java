package custom.localize.Bjys;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;


public class Bjys_CustomLocalize extends CustomLocalize
{
    public String getAssemblyVersion()
    {
        return "18207 build 2015.02.10";
    }

    public SaleBS createSaleBS()
    {
        return new custom.localize.Bjys.Bjys_SaleBS();
    }

    public CreatePayment createCreatePayment()
    {
        return new custom.localize.Bjys.Bjys_CreatePayment();
    }

    public SaleBillMode createSaleBillMode()
    {
        return new custom.localize.Bjys.Bjys_SaleBillMode();
    }

    public HykInfoQueryBS createHykInfoQueryBS()
    {
        return new Bjys_HykInfoQueryBS();
    }

    protected boolean checkAllowUseBank(int seqno)
    {
        // 全部注册码都允许使用金卡工程   
        return true;
    }
    
    public LoadSysInfo createLoadSysInfo()
    {
        return new Bjys_LoadSysInfo();
    }
    
    public NetService createNetService()
    {
    	return new Bjys_NetService();
    }
    
    public DataService createDataService()
    {
    	return new Bjys_DataService();
    }
    
    public AccessBaseDB createAccessBaseDB()
    {
    	return new Bjys_AccessBaseDB();
    }
    
    public AccessLocalDB createAccessLocalDB()
    {
    	return new Bjys_AccessLocalDB();
    }
    
    public AccessDayDB createAccessDayDB()
    {
    	return new Bjys_AccessDayDB();
    }
    
    public MenuFuncBS createMenuFuncBS()
	{
    	return new Bjys_MenuFuncBS();
	}
    
    public DisplaySaleTicketBS createDisplaySaleTicketBS()
	{
		return new Bjys_DisplaySaleTicketBS();
	}	
}
