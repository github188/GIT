package custom.localize.Bgtx;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentDzq;
import com.efuture.javaPos.Payment.PaymentDzqFjk;
import com.efuture.javaPos.PrintTemplate.CardSaleBillMode;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Cmls.Cmls_CustomLocalize;

public class Bgtx_CustomLocalize extends Cmls_CustomLocalize
{
	public String getAssemblyVersion()
	{
		//北国集团  连锁版
	    return "16339 build 2015.04.17";
	}
	
	public static boolean crmMode()
	{
		return true;
	}
	public SaleBS createSaleBS()
	{
		return new Bgtx_SaleBS();
	}
	    
	public SaleBillMode createSaleBillMode()
	{
		return new Bgtx_SaleBillMode();
	 }
		
	 public HykInfoQueryBS createHykInfoQueryBS()
	 {
		return new Bgtx_HykInfoQueryBS();
	}
		
	public NetService createNetService()
	{
		return new Bgtx_NetService();
	}
		
	public DataService createDataService()
	{
		return new Bgtx_DataService();
	}
		
	public AccessLocalDB createAccessLocalDB()
	{
		return new Bgtx_AccessLocalDB();
	}
	public MzkInfoQueryBS createMzkInfoQueryBS()
	{
		return new Bgtx_MzkInfoQueryBS();
	}
//	public CardSaleBillMode createCardSaleBillMode()
//	{
//		return new Bgtx_CardSaleBillMode();
//	}
	
}
