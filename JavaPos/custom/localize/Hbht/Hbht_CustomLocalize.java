package custom.localize.Hbht;

import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Cmls.Cmls_CustomLocalize;

/*
 * 河北怀特
 */
public class Hbht_CustomLocalize extends Cmls_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "1.0.4 build 2014.04.24";
	}

	public SaleBillMode createSaleBillMode()
	{
		return new Hbht_SaleBillMode();
	}

	public LoadSysInfo createLoadSysInfo()
	{
		return new Hbht_LoadSysInfo();
	}
}
