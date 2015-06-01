package custom.localize.Lnbe;

import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.UI.MenuFuncEvent;

public class Lnbe_MenuFuncBS extends MenuFuncBS
{
	public void execFuncMenu(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (Integer.parseInt(mfd.code) == StatusType.MN_DAILYFEE)
		{
			new Lnbe_WriteGrantForm().open();
		}
		else
		{
			super.execFuncMenu(mfd, mffe);
		}
	}

}
