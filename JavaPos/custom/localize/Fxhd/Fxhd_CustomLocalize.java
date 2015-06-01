package custom.localize.Fxhd;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Logic.MenuFuncBS;

import custom.localize.Cmls.Cmls_CustomLocalize;

// 新华都
public class Fxhd_CustomLocalize extends Cmls_CustomLocalize
{
	public String getAssemblyVersion()
    {
    	return "11541 build 2013.07.16";
    }
	
	public NetService createNetService()
	{
		return new Fxhd_NetService();
	}

    public MenuFuncBS createMenuFuncBS()
    {
    	return new Fxhd_MenuFuncBS();
    }
}
