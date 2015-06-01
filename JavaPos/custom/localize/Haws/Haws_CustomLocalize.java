package custom.localize.Haws;

import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bhcm.Bhcm_CustomLocalize;
//哈尔滨奥维斯
public class Haws_CustomLocalize extends Bhcm_CustomLocalize
{
    public String getAssemblyVersion()
    {
        return "15854 build 2013.10.30";
    }
  
    public SaleBillMode createSaleBillMode()
    {
        return new custom.localize.Haws.Haws_SaleBillMode();
    }
    
    public AccessLocalDB createAccessLocalDB()
    {
		return new custom.localize.Haws.Haws_AccessLocalDB();
    }
  
}
