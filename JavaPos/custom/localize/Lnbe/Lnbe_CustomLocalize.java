package custom.localize.Lnbe;

import com.efuture.javaPos.Logic.MenuFuncBS;

import custom.localize.Bstd.Bstd_CustomLocalize;
 
/*
 * 南宁百易
 */
public class Lnbe_CustomLocalize extends Bstd_CustomLocalize 
{
	public String getAssemblyVersion()
	{
		//主版本号 . 子版本号 [ 修正版本号 [. 编译版本号 ]]
		return "1.0.1 build 2012.07.04";
	}
	
	public MenuFuncBS createMenuFuncBS()
	{
		return new Lnbe_MenuFuncBS();
	}
}
