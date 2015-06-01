package custom.localize.Shhl;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bstd.Bstd_CustomLocalize;

/**
 * 
 * 上海海亮
 * 
 */
public class Shhl_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "1.0.6 bulid 2014.12.22";
	}

	public SaleBS createSaleBS()
	{
		return new Shhl_SaleBS();
	}

	public NetService createNetService()
	{
		return new Shhl_NetService();
	}

	public CreatePayment createCreatePayment()
	{
		return new Shhl_CreatePayment();
	}

	public SaleBillMode createSaleBillMode()
	{
		return new Shhl_SaleBillMode();
	}
	
	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new Shhl_HykInfoQueryBS();
	}
}
