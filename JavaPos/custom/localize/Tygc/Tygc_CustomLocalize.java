package custom.localize.Tygc;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Logic.CouponQueryInfoBS;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.GiftBillMode;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Cmls.Cmls_CustomLocalize;
//宁波天一广场
public class Tygc_CustomLocalize extends Cmls_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "18264 build 2015.04.08";
	}

	
	public GiftBillMode createGiftMode()
	{
		return new custom.localize.Tygc.Tygc_GiftBillMode();
	}
	
	public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Tygc.Tygc_SaleBillMode();
	}
	
	public SaleBS createSaleBS(){
		return new custom.localize.Tygc.Tygc_SaleBS();
	}
	
	public NetService createNetService(){
		return new custom.localize.Tygc.Tygc_NetService();
	}
	
	public HykInfoQueryBS createHykInfoQueryBS(){
		return new custom.localize.Tygc.Tygc_HykInfoQueryBS();
	}

	public CouponQueryInfoBS createCouponQueryInfoBS()
	{
		return new custom.localize.Tygc.Tygc_CouponQueryInfoBS();
	}
	
	public AccessBaseDB createAccessBaseDB(){
		return new custom.localize.Tygc.Tygc_AccessBaseDB();
	}
	
	public AccessLocalDB createAccessLocalDB(){
		return new custom.localize.Tygc.Tygc_AccessLocalDB();
	}
	
	public DisplaySaleTicketBS createDisplaySaleTicketBS()
	{
		return new custom.localize.Tygc.Tygc_DisplaySaleTicketBS();
	}
}
