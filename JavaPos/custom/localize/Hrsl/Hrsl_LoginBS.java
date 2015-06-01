package custom.localize.Hrsl;

import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.LoginBS;

public class Hrsl_LoginBS extends LoginBS
{
	public boolean loginDone()
	{
		if (super.loginDone())
		{
			Hrsl_ServiceCrmModule.getDefault().initCrmConnection();
			Hrsl_ServiceCrmModule.getDefault().userLogin(GlobalInfo.sysPara.mktcode, GlobalInfo.posLogin.gh, GlobalInfo.syjDef.syjh);
			return true;
		}
		return false;
	}
}
