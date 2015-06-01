package custom.localize.Cjmx;

import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.UI.MenuFuncEvent;
import com.efuture.javaPos.UI.Design.GoodsStockQueryForm;

public class Cjmx_MenuFuncBS extends MenuFuncBS
{
	public boolean execExtendFuncMenu(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (Integer.parseInt(mfd.code) == 310)
		{
			openStockQuery(mfd, mffe);
			return true;
		}
		return false;
	}
	
//	打开库存查询界面
	private void openStockQuery(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		new GoodsStockQueryForm(null,null);
	}
}
