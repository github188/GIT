package custom.localize.Zspj;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.MutiSelectBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bstd.Bstd_CustomLocalize;

public class Zspj_CustomLocalize extends Bstd_CustomLocalize
{
    public String getAssemblyVersion()
    {
    	return "1.0.33 build 2014.12.22";
    }
    
    public TaskExecute createTaskExecute()
    {
    	return new Zspj_TaskExecute();
    }
    
    public AccessDayDB createAccessDayDB()
    {
    	return new Zspj_AccessDayDB();
    }
    public SaleBillMode createSaleBillMode()
    {
	return new custom.localize.Zspj.Zspj_SaleBillMode();
    }
    
    public SaleBS createSaleBS()
    {
	return new custom.localize.Zspj.Zspj_SaleBS();
    }
    
    public CreatePayment createCreatePayment()
    {
	return new custom.localize.Zspj.Zspj_CreatePayment();
    }
    
    public NetService createNetService()
    {
	return new custom.localize.Zspj.Zspj_NetService();
    }
    
    public DataService createDataService()
    {
	return new custom.localize.Zspj.Zspj_DataService();
    }
    
    public AccessLocalDB createAccessLocalDB()
    {
		return new custom.localize.Zspj.Zspj_AccessLocalDB();
    }
    
    public MenuFuncBS createMenuFuncBS()
	{
		return new Zspj_MenuFuncBS();
	}
    
    public MutiSelectBS createMutiSelectBS()
	{
		return new Zspj_MutiSelectBS_ISHB();
	}

}
