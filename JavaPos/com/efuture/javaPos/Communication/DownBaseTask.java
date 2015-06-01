package com.efuture.javaPos.Communication;

import java.util.TimerTask;

import com.efuture.DeBugTools.PosLog;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.StatusType;


public class DownBaseTask extends TimerTask
{
	static boolean oncerun = false;
	
    public void run()
    {
        try
        {
        	// 关机状态定时器不工作
        	if (GlobalInfo.syjStatus.status == StatusType.STATUS_SHUTDOWN) return;
        	
        	if (GlobalInfo.sysPara.downloadsaleupdate == 'N' && GlobalInfo.syjStatus.status == StatusType.STATUS_SALEING) return ;
/*        	
 * 
			// 采用第二线程方式下载更新，不影响主用户界面线程的方式，所以不用暂停下载线程
        	// 正在销售状态不下载
        	if (GlobalInfo.syjStatus.status == StatusType.STATUS_SALEING)
        	{
        		oncerun = true;
        		return;
        	}
        	oncerun = false;
*/        	
        	// 下载实时基本信息
	        if (GlobalInfo.isOnline)
	        {
	            UpdateBaseInfo.downloadBaseInfo(true);
	        }
        }
        catch (Exception e)
        {
        	PosLog.getLog(getClass()).error(e);
            e.printStackTrace();
        }
    }
    
    // 每笔交易完成时调用一次,检查是否有一次定时发生被放弃
    public static void onceRun()
    {
/*    	
		// 采用第二线程方式下载更新，不影响主用户界面线程的方式，
    	// 不能在用户界面线程中调用下载更新，因为下载更新中更新状态提示一次了用户界面线程异步调用
    	if (!oncerun) return;
    	
    	// 主线程执行,阻止定时器在此时执行
    	char oldstatus = GlobalInfo.syjStatus.status;
    	GlobalInfo.syjStatus.status = StatusType.STATUS_SALEING;
    	
    	// 下载实时基本信息
        if (GlobalInfo.isOnline)
        {
            UpdateBaseInfo.downloadBaseInfo(false);
        }
        
        // 恢复状态
        GlobalInfo.syjStatus.status = oldstatus;
        oncerun = false;
*/
    }
}
