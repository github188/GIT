package custom.localize.Jplm;

import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Logic.SaleBS;

import custom.localize.Bstd.Bstd_CustomLocalize;

/*
 * 九江派拉蒙
 */
public class Jplm_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "1.0.1 build 2014.08.20";
	}

	public SaleBS createSaleBS()
	{
		return new Jplm_SaleBS();
	}

	public AccessLocalDB createAccessLocalDB()
	{
		return new Jplm_AccessLocalDB();
	}
}
