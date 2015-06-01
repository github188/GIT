package custom.localize.Jjls;

import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.UI.MenuFuncEvent;

public class Jjls_MenuFuncBS extends MenuFuncBS
{
	public void execFuncMenu(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (Integer.parseInt(mfd.code) == StatusType.MN_MZKXX)
		{
			Jjls_CardCheckForm cardCheckForm = new Jjls_CardCheckForm();
			cardCheckForm.open();
		}
		else
		{
			super.execFuncMenu(mfd, mffe);
		}
	}
}
