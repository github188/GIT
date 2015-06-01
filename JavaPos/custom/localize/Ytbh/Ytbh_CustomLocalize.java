package custom.localize.Ytbh;

import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.LoginBS;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Cmls.Cmls_CustomLocalize;
//永泰百货
public class Ytbh_CustomLocalize extends Cmls_CustomLocalize {
	public String getAssemblyVersion()
	{
		return "17335 build 2015.03.03";
	}
	
	public SaleBS createSaleBS()
	{
		return new custom.localize.Ytbh.Ytbh_SaleBS();
	}
	
	public DataService createDataService()
	{
		return new custom.localize.Ytbh.Ytbh_DataService();
	}

	public MenuFuncBS createMenuFuncBS()
	{
		return new custom.localize.Ytbh.Ytbh_MenuFuncBS();
	}
	
	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new custom.localize.Ytbh.Ytbh_HykInfoQueryBS();
	}
	
	public AccessLocalDB createAccessLocalDB()
	{
		return new custom.localize.Ytbh.Ytbh_AccessLocalDB();
	}
	
	public LoginBS createLoginBS()
	{
		return new custom.localize.Ytbh.Ytbh_LoginBS();
	}
	
	public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Ytbh.Ytbh_SaleBillMode();
	}
	
}
