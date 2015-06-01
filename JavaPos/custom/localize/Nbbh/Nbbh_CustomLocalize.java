package custom.localize.Nbbh;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Cmls.Cmls_CustomLocalize;

/**
 * 宁波（第二）百货
 *
 */
public class Nbbh_CustomLocalize extends Cmls_CustomLocalize
{

	public String getAssemblyVersion()
	{
		return "1.0.16 bulid 2014.12.15";
	}
	
	public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Nbbh.Nbbh_SaleBillMode();
	}
    
	//基础数据访问
    public AccessLocalDB createAccessLocalDB()
    {
    	return new custom.localize.Nbbh.Nbbh_AccessLocalDB();
    }
    
  //数据连接逻辑
	public DataService createDataService()
	{
		return new custom.localize.Nbbh.Nbbh_DataService();
	}
	
	//数据连接实现
	public NetService createNetService()
	{
		return new custom.localize.Nbbh.Nbbh_NetService();
	}
	
	public SaleBS createSaleBS()
	{
		return new Nbbh_SaleBS();
	}
	public DisplaySaleTicketBS createDisplaySaleTicketBS()
	{
		return new Nbbh_DisplaySaleTicketBS();
	}
}
