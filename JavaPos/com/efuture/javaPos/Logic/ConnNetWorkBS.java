package com.efuture.javaPos.Logic;


import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Global.TaskExecute;


public class ConnNetWorkBS
{
    public ConnNetWorkBS()
    {
    }

    public boolean setConnNet()
    {
        ProgressBox pb = null;

        try
        {
        	pb = new ProgressBox();
        	
            pb.setText(Language.apply("正在进行联网操作....."));

            // 清空暂停访问的POSSERER URL 
            if (GlobalInfo.httpStatus != null) GlobalInfo.httpStatus.removeAllElements();
            
            // 重新创建联网HTTP对象
            NetService.getDefault().createHttpConnection();
            
            // 连接网络成功,恢复收银机状态标志
            if (DataService.getDefault().getServerTime(false))
            {
                GlobalInfo.syjStatus.netstatus = 'Y';
                
                // 重新设置时间以后要重新启用后台定时器
                LoadSysInfo.getDefault().startBackgroundTimer();                
            }
            else
            {
            	GlobalInfo.syjStatus.netstatus = 'N';
            }            

            //加载JSTORE数据库连接
            pb.setText(Language.apply("正在连接JSTORE数据库......"));
            LoadSysInfo.getDefault().loadRemoteDB(pb.getLabel());
            
            // 刷新状态栏网络状态
            GlobalInfo.statusBar.setNetStatus();
            
            // 刷新收银机状态
            if (GlobalInfo.isOnline)
            {
	            GlobalInfo.syjStatus.status = StatusType.STATUS_LOGIN ;
	            DataService.getDefault().sendSyjStatus();            
            }
            
            // 下载数据
            pb.setText(Language.apply("正在下载系统基本信息......"));
            LoadSysInfo.getDefault().getNetNewData(pb.getLabel());
            
            // 检查数据
            pb.setText(Language.apply("正在检查网上的新数据..."));
            TaskExecute.getDefault().executeTimeTask(false);
            
            // 强制写入小票和缴款的送网任务
            AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDINVOICE,
                    TaskExecute.getKeyTextByBalanceDate());
            AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDPAYJK,
                    TaskExecute.getKeyTextByBalanceDate());
            
            pb.setText(Language.apply("正在执行尚未完成的任务....."));
            DataService.getDefault().execHistoryTask();

            //
            pb.setText(Language.apply("正在检查销售小票号......."));
            DataService.getDefault().checkInvoiceNo();                 
            
            // 记日志
            if (GlobalInfo.isOnline) AccessDayDB.getDefault().writeWorkLog("连接网络成功,收银机进入联网运行");
            else AccessDayDB.getDefault().writeWorkLog("连接网络失败,收银机进入脱网运行");
//            if (GlobalInfo.isOnline) AccessDayDB.getDefault().writeWorkLog(Language.apply("连接网络成功,收银机进入联网运行"));
//            else AccessDayDB.getDefault().writeWorkLog(Language.apply("连接网络失败,收银机进入脱网运行"));
            
            return true;
        }
        catch (Exception ex)
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
