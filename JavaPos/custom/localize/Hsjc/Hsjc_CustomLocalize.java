package custom.localize.Hsjc;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bjkl.Bjkl_SaleBillMode;
import custom.localize.Cmls.Cmls_CustomLocalize;

//杭州水晶城
public class Hsjc_CustomLocalize extends Cmls_CustomLocalize {
	
	public String getAssemblyVersion()
    {
    	return "16339 build 2015.3.20";
    }
	public SaleBillMode createSaleBillMode()
	{
		return new Hsjc_SaleBillMode();
	}

}
