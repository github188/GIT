package custom.localize.Bjcx;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.OperUserDef;

import custom.localize.Bhls.Bhls_TaskExecute;

public class Bjcx_TaskExecute extends Bhls_TaskExecute
{

	public boolean openDrawGrant()
	{
		if (GlobalInfo.posLogin.priv.length() > 5 && GlobalInfo.posLogin.priv.charAt(5) != 'Y')
		{
			OperUserDef staff = DataService.getDefault().personGrant("收银开钱箱授权");

			if (staff != null)
			{
				if (staff.priv.length() > 5 && staff.priv.charAt(5) != 'Y')
				{
					new MessageBox("当前工号没有开钱箱权限!");
					return false;
				}

				String log = "授权临时打开钱箱,授权工号:" + staff.gh;
				AccessDayDB.getDefault().writeWorkLog(log);
			}
			else
			{
				return false;
			}
		}

		return true;
	}
}
