package custom.localize.Yzlj;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessLocalDB;

import custom.localize.Bstd.Bstd_CustomLocalize;


public class Yzlj_CustomLocalize extends Bstd_CustomLocalize
{
    public String getAssemblyVersion()
    {
    	return "9700 bulid 2014.07.11";
    }
    
    public AccessLocalDB createAccessLocalDB()
    {
		return new custom.localize.Yzlj.Yzlj_AccessLocalDB();
    } 
    
    public NetService createNetService()
	{
		return new custom.localize.Yzlj.Yzlj_NetService();
	}
 
}
