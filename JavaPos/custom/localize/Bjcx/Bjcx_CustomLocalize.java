package custom.localize.Bjcx;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Logic.CouponQueryInfoBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.CardSaleBillMode;
import com.efuture.javaPos.PrintTemplate.GiftBillMode;
import com.efuture.javaPos.PrintTemplate.HangBillMode;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.PrintTemplate.YyySaleBillMode;

import custom.localize.Cmls.Cmls_CustomLocalize;

/***
 * 
 * 北京城乡客户化
 * @author wy
 *
 */
public class Bjcx_CustomLocalize extends Cmls_CustomLocalize {
	
	public String getAssemblyVersion()
    {
        return "2.0.22 build 2014.02.12";
        /**
         * 2.0.5 build 2011.08.31 为上线版本
         */
    }
	
	public SaleBS createSaleBS()
    {
		return new custom.localize.Bjcx.Bjcx_SaleBS();
    }
	
	public DataService createDataService()
	{
		return new custom.localize.Bjcx.Bjcx_DataService();
	}
	
	public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Bjcx.Bjcx_SaleBillMode();
	}
	
	public AccessBaseDB createAccessBaseDB()
	{
		return new custom.localize.Bjcx.Bjcx_AccessBaseDB();
	}
	
	//营业员联打印
	public YyySaleBillMode createYyySaleBillMode()
	{
		return new custom.localize.Bjcx.Bjcx_YyySaleBillMode();
	}
	
	//卡单打印
	public CardSaleBillMode createCardSaleBillMode()
	{
		return new custom.localize.Bjcx.Bjcx_CardSaleBillMode();
	}
	
	public TaskExecute createTaskExecute()
	{
		return new custom.localize.Bjcx.Bjcx_TaskExecute();
	}
	
	public GiftBillMode createGiftMode()
	{
		return new custom.localize.Bjcx.Bjcx_GiftBillMode();
	}
	
	public CouponQueryInfoBS createCouponQueryInfoBS()
	{
		return new custom.localize.Bjcx.Bjcx_CouponQueryInfoBS();
	}
	
	public HangBillMode createHangBillMode()
	{
		return new custom.localize.Bjcx.Bjcx_HangBillMode();
	}
	
    public NetService createNetService()
	{
		return new custom.localize.Bjcx.Bjcx_NetService();
	}

    
    public AccessLocalDB createAccessLocalDB()
    {
    	return new custom.localize.Bjcx.Bjcx_AccessLocalDB();
    }
}
