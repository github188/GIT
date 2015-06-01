package custom.localize.Djgc;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Cmls.Cmls_CustomLocalize;

// 德基广场
public class Djgc_CustomLocalize extends Cmls_CustomLocalize
{
    public String getAssemblyVersion()
    {
    	return "16342 build 2015.03.27";
    }
	public DisplaySaleTicketBS createDisplaySaleTicketBS()
	{
		return new Djgc_DisplaySaleTicketBS();
	}
	public SaleBS createSaleBS()
	{
		return new Djgc_SaleBS();
	}
	public NetService createNetService()
	{
		return new Djgc_NetService();
	}
	public AccessLocalDB createAccessLocalDB()
	{
		return new Djgc_AccessLocalDB();
	}
	public SaleBillMode createSaleBillMode()
	{
		return new Djgc_SaleBillMode();
	}
	
	public PaymentMzk createPaymentMzk()
	{
		return new Djgc_PaymentMzk();
	}
	
	public CreatePayment createCreatePayment()
    {
		return new Djgc_CreatePayment();
    }
}
