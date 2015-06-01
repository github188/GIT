package custom.localize.Hzjb;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.LoginBS;

public class Hzjb_LoginBS  extends LoginBS
{
	public boolean loginDone()
	{
		if (super.loginDone())
		{
			if (!PathFile.fileExist("lh.ini"))
			{
				if(Hzjb_ICCardCaller.getDefault().login(GlobalInfo.posLogin.gh)!=0)
					new MessageBox(Hzjb_ICCardCaller.getDefault().getLastError());
			}
			return true;
		}
		return false;
	}
	
	public boolean logoutDone()
	{
		if (GlobalInfo.posLogin != null && GlobalInfo.posLogin.gh != null && GlobalInfo.posLogin.gh.length() > 0)
			if(!PathFile.fileExist("lh.ini") && Hzjb_ICCardCaller.getDefault().logout()!=0)
				new MessageBox(Hzjb_ICCardCaller.getDefault().getLastError());

		return super.logoutDone();
	}


}
