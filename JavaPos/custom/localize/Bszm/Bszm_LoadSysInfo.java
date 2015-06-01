package custom.localize.Bszm;

import com.efuture.commonKit.Convert;
import com.efuture.javaPos.AssemblyInfo;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Global.TaskExecute;

public class Bszm_LoadSysInfo extends LoadSysInfo
{

	public int getSaleCountByNoSendJSTORE()
	{		
		Object obj = null;

		try
		{
			obj = GlobalInfo.localDB.selectOneData("select count(*) from TASKS where type = 'N'");

			if (obj == null) { return 0; }

			return Convert.toInt(obj);
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return -1;
		}
		finally
		{
			GlobalInfo.localDB.resultSetClose();
		}
	}
	
	public int getSaleCountByNoSendPOSDB()
	{		
		Object obj = null;

		try
		{
			obj = GlobalInfo.dayDB.selectOneData("select count(*) from salehead where netbz <> 'Y'");

			if (obj == null) { return 0; }

			return Convert.toInt(obj);
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return -1;
		}
		finally
		{
			GlobalInfo.dayDB.resultSetClose();
		}
	}
	
	public boolean checkVersion()
	{
		//记录当前的版本信息
		AccessDayDB.getDefault().writeWorkLog("POS当前系统版本号为[" + AssemblyInfo.AssemblyVersion.trim() + "] | 当前客户化版本为[" + CustomLocalize.getDefault().getAssemblyVersion().trim() + "]", StatusType.WORK_POSVERSION);		
		return super.checkVersion();
	}
	
	public void ExitSystem(String msg)
	{
		try
		{

			//小票是否全部上传JSTORE
			int iCount = getSaleCountByNoSendJSTORE();
			String strLog = "";
			if (GlobalInfo.RemoteDB == null || GlobalInfo.RemoteDB.getIsDisConnection()==true)
			{				
				strLog = "POS收银机与JSTORE连接断开,检测当有[" + iCount + "]笔小票未上传到JSTORE | ";
			}
			else
			{				
				strLog = "POS收银机与JSTORE连接正常,检测当有[" + iCount + "]笔小票未上传到JSTORE | ";
			}
			
 			//小票是否全部上传POSDB
			iCount = getSaleCountByNoSendPOSDB();
			if (GlobalInfo.isOnline)
			{
				strLog = strLog + "收银机安全关机,检测当有[" + iCount + "]笔小票未上传到POSDB";
				AccessDayDB.getDefault().writeWorkLog(strLog, StatusType.WORK_SHUTDOWN);
				TaskExecute.getDefault().sendAllWorkLog(TaskExecute.getKeyTextByBalanceDate());
			}
			else
			{
				strLog = strLog + "收银机脱网关机,请检查,检测当有[" + iCount + "]笔小票未上传到POSDB";
				AccessDayDB.getDefault().writeWorkLog(strLog, StatusType.WORK_SHUTDOWN);
			}
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		super.ExitSystem(msg);
	}
}
