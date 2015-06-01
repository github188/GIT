package custom.localize.Tjdc;

import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bhcm.Bhcm_CustomLocalize;

//天津太仓市金鼎城
public class Tjdc_CustomLocalize extends Bhcm_CustomLocalize
{
    public String getAssemblyVersion()
    {
        return "11170 build 2012.03.06";
    }
  
    public SaleBillMode createSaleBillMode()
    {
        return new custom.localize.Tjdc.Tjdc_SaleBillMode();
    }
}
