package custom.localize.Ycgm;

import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.CouponQueryInfoBS;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Cmls.Cmls_CustomLocalize;
//宜昌国贸
public class Ycgm_CustomLocalize extends Cmls_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "17274 build 2014.07.01";
	}
	
	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new custom.localize.Ycgm.Ycgm_HykInfoQueryBS();
	}
	
	public SaleBS createSaleBS()
	{
		return new custom.localize.Ycgm.Ycgm_SaleBS();
	}
	
	public DataService createDataService()
	{
		return new custom.localize.Ycgm.Ycgm_DataService();
	}
	
	public MzkInfoQueryBS createMzkInfoQueryBS()
	{
		return new custom.localize.Ycgm.Ycgm_MzkInfoQueryBS();
	}
	
	public CouponQueryInfoBS createCouponQueryInfoBS()
	{
		return new custom.localize.Ycgm.Ycgm_CouponQueryInfoBS();
	}
	
	public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Ycgm.Ycgm_SaleBillMode();
	}
}
