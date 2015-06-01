package custom.localize.Bxmx;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Logic.MzkRechargeBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bstd.Bstd_CustomLocalize;

//宁波新美心
public class Bxmx_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "1.0.3 build 2013.09.26";
	}

	public SaleBS createSaleBS()
	{
		return new Bxmx_SaleBS();
	}
	
	public DisplaySaleTicketBS createDisplaySaleTicketBS()
	{
		return new Bxmx_DisplaySaleTicketBS();
	}
	

	public LoadSysInfo createLoadSysInfo()
	{
		return new Bxmx_LoadSysInfo();
	}
	
	public CreatePayment createCreatePayment()
	{
		return new Bxmx_CreatePayment();
	}

	public SaleBillMode createSaleBillMode()
	{
		return new Bxmx_SaleBillMode();
	}

	public DataService createDataService()
	{
		return new Bxmx_DataService();
	}

	public NetService createNetService()
	{
		return new Bxmx_NetService();
	}
	
	public MzkRechargeBS createMzkRechargeBS()
	{
		return new Bxmx_MzkRechargeBS();
	}
}
