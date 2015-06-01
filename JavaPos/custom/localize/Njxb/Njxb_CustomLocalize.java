package custom.localize.Njxb;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.CouponQueryInfoBS;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.CardSaleBillMode;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Cmls.Cmls_CustomLocalize;

// 南京新百
public class Njxb_CustomLocalize extends Cmls_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "12174 build 2014.01.14";
	}

	public SaleBS createSaleBS()
	{
		return new custom.localize.Njxb.Njxb_SaleBS();
	}

    public DataService createDataService()
	{
		return new custom.localize.Njxb.Njxb_DataService();
	} 
    
	public CreatePayment createCreatePayment()
	{
		return new custom.localize.Njxb.Njxb_CreatePayment();
	}

	public DisplaySaleTicketBS createDisplaySaleTicketBS()
	{
		return new custom.localize.Njxb.Njxb_DisplaySaleTicketBS();
	}

	public CardSaleBillMode createCardSaleBillMode()
	{
		return new custom.localize.Njxb.Njxb_CardSaleBillMode();
	}

	public NetService createNetService()
	{
		return new custom.localize.Njxb.Njxb_NetService();
	}

	public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Njxb.Njxb_SaleBillMode();
	}
	
	public AccessLocalDB createAccessLocalDB()
	{
		return new custom.localize.Njxb.Njxb_AccessLocalDB();
	}
	
	public CouponQueryInfoBS createCouponQueryInfoBS()
	{
		return new custom.localize.Njxb.Njxb_CouponQueryInfoBS();
	}
}
