package custom.localize.Bstd;

import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.UI.MenuFuncEvent;

public class Bstd_MenuBS extends MenuFuncBS
{
	protected void openMzkChgPass(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		((Bstd_MzkInfoQueryBS) mzkbs).isChgPass(true);
		mzkbs.QueryMzkInfo();
	}
}
