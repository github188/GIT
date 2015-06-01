package custom.localize.Ksss;

import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Logic.GoodsInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Cmls.Cmls_CustomLocalize;
//昆山商厦
public class Ksss_CustomLocalize extends Cmls_CustomLocalize
{
    public String getAssemblyVersion()
    {
    	return "12174 build 2012.10.24";
    }
    
    public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Ksss.Ksss_SaleBillMode();
	}
   
    public SaleBS createSaleBS()
	{
		return new custom.localize.Ksss.Ksss_SaleBS();
	}
    
    public AccessLocalDB createAccessLocalDB()
    {
		return new custom.localize.Ksss.Ksss_AccessLocalDB();
    }
    
    public GoodsInfoQueryBS createGoodsInfoQueryBS()
    {
		return new custom.localize.Ksss.Ksss_GoodsInfoQueryBS();
    }
}
