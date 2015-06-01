package custom.localize.Bcsf;

import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.UI.MenuFuncEvent;


public class Bcsf_MenuFuncBS  extends MenuFuncBS
{
	public void execFuncMenu(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (Integer.parseInt(mfd.code) == StatusType.MN_DAILYFEE)
		{
			new Bcsf_DailyFeeForm().open();
		}
		else
		{
			super.execFuncMenu(mfd, mffe);
		}
	}
}
