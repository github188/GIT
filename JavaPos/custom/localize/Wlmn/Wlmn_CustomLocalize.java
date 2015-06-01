package custom.localize.Wlmn;

import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bhcm.Bhcm_CustomLocalize;
//温岭米尼项目
public class Wlmn_CustomLocalize extends Bhcm_CustomLocalize
{
    public String getAssemblyVersion()
    {
    	return "15248 build 2013.08.16";
    }
    
    public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Wlmn.Wlmn_SaleBillMode();
	}
    
}
