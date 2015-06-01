package custom.localize.Jybh;

import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bhdd.Bhdd_CustomLocalize;


public class Jybh_CustomLocalize extends Bhdd_CustomLocalize
{
    public String getAssemblyVersion()
    {
        return "1.0.1 bulid 2008.12.17";
    }

    public SaleBillMode createSaleBillMode()
    {
        return new custom.localize.Jybh.Jybh_SaleBillMode();
    }
}
