package custom.localize.Bzhx;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Logic.SaleBS;

import custom.localize.Cmls.Cmls_CustomLocalize;
//北京资和信百货
public class Bzhx_CustomLocalize extends Cmls_CustomLocalize
{
    public String getAssemblyVersion()
    {
    	return "17344 build 2014.07.21";
    }
    
    public SaleBS createSaleBS()
    {
      return new custom.localize.Bzhx.Bzhx_SaleBS();
    }
    
    public NetService createNetService()
    {
      return new custom.localize.Bzhx.Bzhx_NetService();
    }
   
}
