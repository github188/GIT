package custom.localize.Zmjc;

import org.eclipse.swt.widgets.Label;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;

import custom.localize.Bcrm.Bcrm_LoadSysInfo;

public class Zmjc_LoadSysInfo extends Bcrm_LoadSysInfo
{
	protected void checkDeviceMessage()
	{		
		// 提示打印机是否有效
		if (!ConfigClass.DebugMode && !Printer.getDefault().getStatus())
		{
			AccessDayDB.getDefault().writeWorkLog(Language.apply("打印机未连接,打印操作将无法执行"));

			//new MessageBox("打印机未连接，打印操作将无法执行");//中免要求不提示
		}

		// 调试模式下提示客显,打印机是否已启用
		if (ConfigClass.DebugMode && ConfigClass.LineDispaly1 != null && ConfigClass.LineDispaly1.length() > 12 && GlobalInfo.syjDef.isdisp == 'N')
		{
			//new MessageBox("收银机定义中未启用客显");//中免要求不提示
		}

		if (ConfigClass.DebugMode && ConfigClass.Printer1 != null && ConfigClass.Printer1.length() > 8 && GlobalInfo.syjDef.isprint == 'N')
		{
			//new MessageBox("收银机定义中未启用打印机");//中免要求不提示
		}
	}
	
	public boolean startLoadInfo(Label lbl_message)
	{
		if (super.startLoadInfo(lbl_message))
		{
			GlobalInfo.statusBar.setHangCount();
			runPlugExe();
			return true;
		}
		return false;
	}
	
	//调用外挂EXE
	protected void runPlugExe()
	{
		try
		{
			String exePath = "Caps.exe";//转大写外挂
			if (PathFile.fileExist(exePath))
			{
				PosLog.getLog(this.getClass().getSimpleName()).info("run " + exePath);
				CommonMethod.waitForExec(exePath);
			}
			else
			{
				PosLog.getLog(this.getClass().getSimpleName()).info("no find " + exePath);
			}
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
	}

}
