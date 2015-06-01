package custom.localize.Xjly;

import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.UI.MenuFuncEvent;

public class Xjly_MenuFuncBS extends MenuFuncBS
{
    public final static int MN_DMCHECK	 = 109;							//大码商品盘点
    
	public void execFuncMenu(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (Integer.parseInt(mfd.code) == StatusType.MN_CHECK)
		{
			if (mffe != null) mffe.dispose();

			GlobalInfo.saleform.setSaleType(SellType.CHECK_INPUT);
			
			((Xjly_SaleBS)GlobalInfo.saleform.sale.saleBS).checkType = 'X';
			
		}
		else if (Integer.parseInt(mfd.code) == MN_DMCHECK)
		{
			if (mffe != null) mffe.dispose();

			GlobalInfo.saleform.setSaleType(SellType.CHECK_INPUT);
			
			((Xjly_SaleBS)GlobalInfo.saleform.sale.saleBS).checkType = 'D';
		}
		else
		{
			super.execFuncMenu(mfd, mffe);
		}
	}
}
