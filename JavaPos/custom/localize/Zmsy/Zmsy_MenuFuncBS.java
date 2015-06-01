package custom.localize.Zmsy;

import com.efuture.DeBugTools.PosLog;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.UI.MenuFuncEvent;

public class Zmsy_MenuFuncBS extends MenuFuncBS
{
	public void execFuncMenu(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		try
		{
			if (Integer.parseInt(mfd.code) == Zmsy_StatusType.ZMSY_MN_RTPKC)
			{
				RptDPKCForm kc = new RptDPKCForm();
				kc.open();
			}
			else if (Integer.parseInt(mfd.code) == Zmsy_StatusType.ZMSY_MN_RTPKC_LIST)
			{
				RptSPKCForm kclist = new RptSPKCForm();
				kclist.open();
			}
			else if (Integer.parseInt(mfd.code) == Zmsy_StatusType.MN_YYYTJ)
			{
				RptYYYForm yyy = new RptYYYForm();
				yyy.open();
			}
			else
			{
				super.execFuncMenu(mfd, mffe);
			}
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
	}

}
