package custom.localize.Hrsl;

import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.LoadSysInfo;

public class Hrsl_LoadSysInfo extends LoadSysInfo
{
	public boolean startBroken(boolean b)
	{
		if (super.startBroken(b))
		{
			Hrsl_ServiceCrmModule.getDefault().initCrmConnection();
			Hrsl_ServiceCrmModule.getDefault().userLogin(GlobalInfo.sysPara.mktcode, GlobalInfo.posLogin.gh, GlobalInfo.syjDef.syjh);
			return true;
		}
		return false;
	}

	public void ExitSystem(String msg)
	{
		super.ExitSystem(msg);
		Hrsl_ServiceCrmModule.getDefault().userLogoff();
	}
}
