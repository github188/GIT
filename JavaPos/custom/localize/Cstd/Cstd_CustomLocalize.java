package custom.localize.Cstd;

import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Logic.SaleBS;

public class Cstd_CustomLocalize extends CustomLocalize {
	
	public String getAssemblyVersion()
    {
        return "1.0.0 build 2011.08.03";
    }
	
	public SaleBS createSaleBS()
    {
		return new Cstd_SaleBS();
    }

}
