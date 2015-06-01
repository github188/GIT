package custom.localize.Ytpj;

import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.LoginBS;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bstd.Bstd_CustomLocalize;
//永泰百货
public class Ytpj_CustomLocalize extends Bstd_CustomLocalize {
	public String getAssemblyVersion()
	{
		return "17334 build 2014.10.13";
	}
	
	public SaleBS createSaleBS()
	{
		return new custom.localize.Ytpj.Ytpj_SaleBS();
	}
	
	public DataService createDataService()
	{
		return new custom.localize.Ytpj.Ytpj_DataService();
	}

	public MenuFuncBS createMenuFuncBS()
	{
		return new custom.localize.Ytpj.Ytpj_MenuFuncBS();
	}
	
	public LoginBS createLoginBS()
	{
		return new custom.localize.Ytpj.Ytpj_LoginBS();
	}
	
	public AccessLocalDB createAccessLocalDB()
	{
		return new custom.localize.Ytpj.Ytpj_AccessLocalDB();
	}
	
	public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Ytpj.Ytpj_SaleBillMode();
	}
	
}
