package custom.localize.Ksbl;

import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bhcm.Bhcm_CustomLocalize;
//昆山巴黎春天百货
public class Ksbl_CustomLocalize extends Bhcm_CustomLocalize
{
    public String getAssemblyVersion()
    {
    	return "13104 build 2012.12.21";
    }
    
    public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Ksbl.Ksbl_SaleBillMode();
	}
    
    public AccessDayDB createAccessDayDB()
	{
		return new custom.localize.Ksbl.Ksbl_AccessDayDB();
	}
	
}
