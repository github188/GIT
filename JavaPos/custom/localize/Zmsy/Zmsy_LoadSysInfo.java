package custom.localize.Zmsy;

import org.eclipse.swt.widgets.Label;

import com.efuture.DeBugTools.PosLog;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.TaskExecute;

import custom.localize.Zmjc.Zmjc_LoadSysInfo;

public class Zmsy_LoadSysInfo extends Zmjc_LoadSysInfo
{

	public boolean getNetNewData(Label lbl_message)
	{
		try
		{
			return super.getNetNewData(lbl_message);
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			return false;
		}
		finally
		{
			PosLog.getLog(this.getClass().getSimpleName()).info("getNetNewData() WW=[" + GlobalInfo.sysPara.gwkHGUrl.trim() + "].");
			AccessDayDB.getDefault().writeWorkLog(GlobalInfo.sysPara.gwkHGUrl.trim(), Zmsy_StatusType.ZMSY_WORK_GETGWKGHURL);
			TaskExecute.getDefault().sendAllWorkLog(TaskExecute.getKeyTextByBalanceDate());
		}
	}
}
