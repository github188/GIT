package custom.localize.Nnmk;

import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Logic.CouponQueryInfoBS;
import com.efuture.javaPos.Logic.FjkInfoQueryBS;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.MutiSelectBS;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.GiftBillMode;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.PrintTemplate.YyySaleBillMode;

import custom.localize.Cmls.Cmls_CustomLocalize;

public class Nnmk_CustomLocalize extends Cmls_CustomLocalize
{
	public MzkInfoQueryBS createMzkInfoQueryBS()
	{
		return new Nnmk_MzkInfoQueryBS();
	}
	
	public String getAssemblyVersion()
    {
    	return "11541 build 2013.04.10";
    }
	
	public GiftBillMode createGiftMode()
	{
		return new Nnmk_GiftBillMode();
	}
	
    public SaleBillMode createSaleBillMode()
	{
		return new Nnmk_SaleBillMode();
	}
    
    public DataService createDataService()
	{
		return new Nnmk_DataService();
	} 
    
	public SaleBS createSaleBS()
    {
		return new Nnmk_SaleBS();
    }
	
	public YyySaleBillMode createYyySaleBillMode()
	{
		return new Nnmk_YyySaleBillMode();
	}
	
	public MenuFuncBS createMenuFuncBS()
	{
		return new Nnmk_MenuFuncBS();
	}
    
	public MutiSelectBS createMutiSelectBS()
	{
		return new Nnmk_MutiSelectBS();
	}
	
    public LoadSysInfo createLoadSysInfo()
    {
        return new Nnmk_LoadSysInfo();
    }
    
    public FjkInfoQueryBS createFjkInfoQueryBS()
    {
        return new Nnmk_FjkInfoQueryBS();
    }
    
    public CouponQueryInfoBS createCouponQueryInfoBS()
    {
        return new Nnmk_CouponQueryInfoBS();
    }
    
	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new Nnmk_HykInfoQueryBS();
	}
}
