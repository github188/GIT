package custom.localize.Smtj;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.UI.MenuFuncEvent;

public class Smtj_MenuFuncBS extends MenuFuncBS
{
	public static String code = "";

	public boolean execExtendFuncMenu(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (Integer.parseInt(mfd.code) == 209)
		{
			code = mfd.code;
			openQtxStj_Today(mfd, mffe);
			return true;
		}
		
		if (Integer.parseInt(mfd.code) == 210)
		{
			code = mfd.code;
			openYyyTj_Today(mfd, mffe);
			return true;
		}
		if (Integer.parseInt(mfd.code) == 211)
		{
			code = mfd.code;
			openGzXsTj_Today(mfd, mffe);
			return true;
		}
		/*
		if (Integer.parseInt(mfd.code) == 212)
		{
			code = mfd.code;
			openXsCx_Today(mfd, mffe);
			return true;
		}
		*/
		if (Integer.parseInt(mfd.code) == 213)
		{
			code = mfd.code;
			openXsList_Today(mfd, mffe);
			return true;
		}
		
		return false;
	}
//	 打开收银员销售统计
	public void openQtxStj_Today(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if ((GlobalInfo.posLogin.priv.length() > 4) && (GlobalInfo.posLogin.priv.charAt(4) != 'Y'))
		{
			OperUserDef staff = DataService.getDefault().personGrant("授权查看报表");

			if (staff == null) { return; }

			if (staff.priv.charAt(4) != 'Y')
			{
				new MessageBox("该授权人员无查看报表权限");

				return;
			}
		}

		new Smtj_SyySaleStatForm();
	}
	
//	打开营业员销售统计
	public void openYyyTj_Today(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if ((GlobalInfo.posLogin.priv.length() > 4) && (GlobalInfo.posLogin.priv.charAt(4) != 'Y'))
		{
			OperUserDef staff = DataService.getDefault().personGrant("授权查看报表");

			if (staff == null) { return; }

			if (staff.priv.charAt(4) != 'Y')
			{
				new MessageBox("该授权人员无查看报表权限");

				return;
			}
		}

		new Smtj_BusinessPersonnelStatForm();
	}
	
//	打开柜组销售统计
	public void openGzXsTj_Today(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if ((GlobalInfo.posLogin.priv.length() > 4) && (GlobalInfo.posLogin.priv.charAt(4) != 'Y'))
		{
			OperUserDef staff = DataService.getDefault().personGrant("授权查看报表");

			if (staff == null) { return; }

			if (staff.priv.charAt(4) != 'Y')
			{
				new MessageBox("该授权人员无查看报表权限");

				return;
			}
		}

		new Smtj_ArkGroupSaleStatForm();
	}

//	打开销售小票查询
	protected void openXsCx_Today(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		//传入false代表查询小票
		new Smtj_DisplaySaleTicketForm(StatusType.MN_XSCX);
	}
	
//	打开当日小票列表
	public void openXsList_Today(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		new Smtj_SaleTicketListForm();
	}
}