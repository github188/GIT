package custom.localize.Ybsj;

import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.LoadSysInfo;

public class Ybsj_LoadSysInfo extends LoadSysInfo
{
	public boolean startBroken(boolean b)
	{
		if (super.startBroken(b))
		{
			Ybsj_SocketCrmModule.getDefault().initCrmConnection();
			Ybsj_SocketCrmModule.getDefault().userLogin(GlobalInfo.sysPara.mktcode, GlobalInfo.posLogin.gh, GlobalInfo.syjDef.syjh);
			return true;
		}
		return false;
	}

	public void ExitSystem(String msg)
	{
		super.ExitSystem(msg);
		Ybsj_SocketCrmModule.getDefault().userLogoff();
	}
}
