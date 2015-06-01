package custom.localize.Fxhd;

import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.UI.MenuFuncEvent;

public class Fxhd_MenuFuncBS extends MenuFuncBS
{
	public boolean execExtendFuncMenu(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (Convert.toInt(mfd.code) == 399)
		{
			Fxhd_ExchangeCardForm a = new Fxhd_ExchangeCardForm();
			a.open();
			return true;
		}
		return false;
	}
}
