package custom.localize.Ybsj;

import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.LoginBS;

public class Ybsj_LoginBS extends LoginBS
{
	public boolean loginDone()
	{
		if (super.loginDone())
		{
			Ybsj_SocketCrmModule.getDefault().initCrmConnection();
			Ybsj_SocketCrmModule.getDefault().userLogin(GlobalInfo.sysPara.mktcode, GlobalInfo.posLogin.gh, GlobalInfo.syjDef.syjh);
			return true;
		}
		return false;
	}
}
