package custom.localize.Zmsy;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.PayinBillMode;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Zmjc.Zmjc_CustomLocalize;

/**
 * 中免三亚店
 * @author yw
 *
 */
public class Zmsy_CustomLocalize extends Zmjc_CustomLocalize
{

	//客户化版本号
	public String getAssemblyVersion()
    {
        return "1.30.104 build 2015.03.24";        
    }
	
	//业务逻辑
	public SaleBS createSaleBS()
    {
		return new custom.localize.Zmsy.Zmsy_SaleBS();
    }
	
	//数据连接逻辑
	public DataService createDataService()
	{
		return new custom.localize.Zmsy.Zmsy_DataService();
	}
	
	//数据连接实现
	public NetService createNetService()
	{
		return new custom.localize.Zmsy.Zmsy_NetService();
	}
	
	//小票打印
	public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Zmsy.Zmsy_SaleBillMode();
	}
	
	public AccessLocalDB createAccessLocalDB()
    {
    	return new custom.localize.Zmsy.Zmsy_AccessLocalDB();
    }
    
    public AccessDayDB createAccessDayDB()
    {
    	return new custom.localize.Zmsy.Zmsy_AccessDayDB();
    }
    
    public AccessBaseDB createAccessBaseDB()
	{
		return new custom.localize.Zmsy.Zmsy_AccessBaseDB();
	}
    
    public MenuFuncBS createMenuFuncBS()
	{
		return new Zmsy_MenuFuncBS();
	}
    
    public DisplaySaleTicketBS createDisplaySaleTicketBS()
    {
    	return new Zmsy_DisplaySaleTicketBS();
    }
    
    public LoadSysInfo createLoadSysInfo()
    {
        return new Zmsy_LoadSysInfo();
    }
    
    public PayinBillMode createPayinBillMode()
	{
		return new Zmsy_PayinBillMode();
	}
    
}
