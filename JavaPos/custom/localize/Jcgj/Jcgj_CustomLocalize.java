package custom.localize.Jcgj;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.CouponQueryInfoBS;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.PrintTemplate.GiftBillMode;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Cmls.Cmls_CustomLocalize;
// 深圳晶城国际
public class Jcgj_CustomLocalize extends Cmls_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "1.2.1 bulid 2012.09.10";
	}

	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new custom.localize.Jcgj.Jcgj_HykInfoQueryBS();
	}

	public SaleBS createSaleBS()
	{
		return new custom.localize.Jcgj.Jcgj_SaleBS();
	}

	public CouponQueryInfoBS createCouponQueryInfoBS()
	{
		return new custom.localize.Jcgj.Jcgj_CouponQueryInfoBS();
	}

	public DataService createDataService()
	{
		return new custom.localize.Jcgj.Jcgj_DataService();
	}

	public PaymentMzk createPaymentMzk()
	{
		return new custom.localize.Jcgj.Jcgj_PaymentMzk();
	}
	
	public CreatePayment createCreatePayment()
	{
		return new custom.localize.Jcgj.Jcgj_CreatePayment();
	}
	
	public DisplaySaleTicketBS createDisplaySaleTicketBS()
	{
		return new custom.localize.Jcgj.Jcgj_DisplaySaleTicketBS();
	}
	
	public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Jcgj.Jcgj_SaleBillMode();
	}
	
	public MzkInfoQueryBS createMzkInfoQueryBS()
	{
		return new custom.localize.Jcgj.Jcgj_MzkInfoQueryBS();
	}
	
	public AccessLocalDB createAccessLocalDB()
	{
		return new custom.localize.Jcgj.Jcgj_AccessLocalDB();
	}
    public NetService createNetService()
	{
		return new custom.localize.Jcgj.Jcgj_NetService();
	}
    
    public GiftBillMode createGiftMode()
	{
        return new custom.localize.Jcgj.Jcgj_GiftBillMode();
	} 
}
