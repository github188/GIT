package custom.localize.Lyjb;

import com.efuture.javaPos.Logic.MenuFuncBS;

import custom.localize.Bhcm.Bhcm_CustomLocalize;

public class Lyjb_CustomLocalize extends Bhcm_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "1.0.00 bulid 2012.08.22";
	}
	
	public MenuFuncBS createMenuFuncBS()
	{
		return new Lyjb_MenuFuncBS();
	}
}
