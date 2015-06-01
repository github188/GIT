package custom.localize.Bjys;

import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.UI.MenuFuncEvent;
import com.efuture.javaPos.UI.Design.ArkGroupSaleStatForm;
import com.efuture.javaPos.UI.Design.BusinessPersonnelStatForm;
import com.efuture.javaPos.UI.Design.SyySaleStatForm;

public class Bjys_MenuFuncBS extends MenuFuncBS
{
	public void openQtxStj(MenuFuncDef mfd, MenuFuncEvent mffe)
    {
		new SyySaleStatForm();
    }
	
	public void openYyyTj(MenuFuncDef mfd, MenuFuncEvent mffe)
    {
		new BusinessPersonnelStatForm();
    }
	
	public void openGzXsTj(MenuFuncDef mfd, MenuFuncEvent mffe)
    {
		new ArkGroupSaleStatForm();
    }
}
