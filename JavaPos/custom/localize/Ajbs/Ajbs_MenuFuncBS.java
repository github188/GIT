package custom.localize.Ajbs;

import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.UI.MenuFuncEvent;

public class Ajbs_MenuFuncBS extends MenuFuncBS
{
	public void execFuncMenu(MenuFuncDef mfd, MenuFuncEvent mffe)
	{

		if (Integer.parseInt(mfd.code) == StatusType.MN_BYICCARDRECHARGE)
		{
			Ajbs_ICCard.getDefault().cardRecharge();
		}
		else if (Integer.parseInt(mfd.code) == StatusType.MN_BYICCARDROLLING)
		{
			Ajbs_ICCard.getDefault().cardRolling();
		}
		else
		{
			super.execFuncMenu(mfd, mffe);
		}
	}
}
