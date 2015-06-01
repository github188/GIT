package custom.localize.Lyqf;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Logic.SaleBS;

import custom.localize.Bstd.Bstd_CustomLocalize;

/**
 * 
 * 洛阳群丰
 * 
 */
public class Lyqf_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "1.0.2 bulid 2014.08.04";
	}

	public SaleBS createSaleBS()
	{
		return new Lyqf_SaleBS();
	}

	public AccessLocalDB createAccessLocalDB()
	{
		return new Lyqf_AccessLocalDB();
	}
	
	public NetService createNetService()
	{
		return new Lyqf_NetService();
	}
}
