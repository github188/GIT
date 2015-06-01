package custom.localize.Zmjc;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentChange;
import com.efuture.javaPos.PrintTemplate.HangBillMode;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bcrm.Bcrm_CustomLocalize;

/**
 * 中免机场店
 * @author wy
 *
 */
public class Zmjc_CustomLocalize extends Bcrm_CustomLocalize
{

	//客户化版本号
	public String getAssemblyVersion()
    {
        return "1.24.83 build 2015.03.16";        
    }
	
	//业务逻辑
	public SaleBS createSaleBS()
    {
		return new custom.localize.Zmjc.Zmjc_SaleBS();
    }
	
	//数据连接逻辑
	public DataService createDataService()
	{
		return new custom.localize.Zmjc.Zmjc_DataService();
	}
	
	//数据连接实现
	public NetService createNetService()
	{
		return new custom.localize.Zmjc.Zmjc_NetService();
	}
	
	//小票打印
	public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Zmjc.Zmjc_SaleBillMode();
	}
	
	//脱网数据访问
	public AccessBaseDB createAccessBaseDB()
	{
		return new custom.localize.Zmjc.Zmjc_AccessBaseDB();
	}
    
	//基础数据访问
    public AccessLocalDB createAccessLocalDB()
    {
    	return new custom.localize.Zmjc.Zmjc_AccessLocalDB();
    }
    
    public AccessDayDB createAccessDayDB()
    {
    	return new custom.localize.Zmjc.Zmjc_AccessDayDB();
    }
    
    public TaskExecute createTaskExecute()
    {
    	return new custom.localize.Zmjc.Zmjc_TaskExecute();
    }
    
    public PaymentChange getPaymentChange(SaleBS sale)
    {
        return new Zmjc_PaymentChange(sale);
    }
    	
    public CreatePayment createCreatePayment()
	{
		return new Zmjc_CreatePayment();
	}
    
    public DisplaySaleTicketBS createDisplaySaleTicketBS()
	{
		return new Zmjc_DisplaySaleTicketBS();
	}
    public LoadSysInfo createLoadSysInfo()
    {
        return new Zmjc_LoadSysInfo();
    }
    
    public HangBillMode createHangBillMode()
	{
		return new Zmjc_HangBillMode();
	}
    
    public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new Zmjc_HykInfoQueryBS();
	}
}
