package com.efuture.javaPos.Logic;

import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.StatusType;

public class DisConnNetWorkBS 
{
	public DisConnNetWorkBS()
	{
		
	}
	
	public boolean setDisConnNet()
	{
		ProgressBox pb = null; 
		
		try
		{
			pb = new ProgressBox();
			
			// 设置主动脱网
			pb.setText(Language.apply("正在进行脱网操作......."));
			GlobalInfo.syjStatus.netstatus = 'Z';
			GlobalInfo.syjStatus.status = StatusType.STATUS_DISCONNET;

			// 发送网络状态
			pb.setText(Language.apply("正在发送脱网状态......."));
			DataService.getDefault().sendSyjStatus();
			
			// 切换到脱网状态
			pb.setText(Language.apply("正在切换到脱网状态......."));
			GlobalInfo.isOnline = false;
			
			// 刷新状态栏网络状态
	        GlobalInfo.statusBar.setNetStatus();
            
            // 记日志
            if (!GlobalInfo.isOnline) AccessDayDB.getDefault().writeWorkLog(Language.apply("断开网络成功,收银机进入脱网运行"));
            else AccessDayDB.getDefault().writeWorkLog(Language.apply("断开网络失败,收银机保持联网运行"));
	        
	        return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			
			return false;
		}
		finally
		{
			if (pb != null) pb.close();
		}
	}
}
