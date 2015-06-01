package com.efuture.javaPos.Logic;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.LoadSysInfo;



//这个类用来设置系统时间
public class SetSystemTimeBS
{
    private DisConnNetWorkBS dcnwb = null;
	private boolean onlysettime = false;

    public DisConnNetWorkBS getDcnwb()
	{
		return dcnwb;
	}

	public void setDcnwb(DisConnNetWorkBS dcnwb)
	{
		this.dcnwb = dcnwb;
	}
	
    public boolean isOnlysettime()
	{
		return onlysettime;
	}

	public void setOnlysettime(boolean onlysettime)
	{
		this.onlysettime = onlysettime;
	}

	public SetSystemTimeBS()
    {
        dcnwb = CustomLocalize.getDefault().createDisConnNetWorkBS();
    }

    public void onlySetTime(boolean onlysettime)
    {
    	this.onlysettime = onlysettime;
    }
    
    public boolean modifyDateTime(String txtDate, String txtTime)
    {
        try
        {
            //提示
            MessageBox me = new MessageBox(Language.apply("你确定要重新设置系统时间\n\n并将系统切换到单机运行吗?"), null, true);

            if (me.verify() == GlobalVar.Key1)
            {
            	// 记录日志
            	if (!onlysettime)
            	{
	                // 记工作日志
	                AccessDayDB.getDefault().writeWorkLog("重新设置系统时间为 " + txtDate + " " + txtTime);
	
	                // 设置脱网
	                dcnwb.setDisConnNet();
            	}
            	
                // 设置系统时间
                ManipulateDateTime mdt = new ManipulateDateTime();
                mdt.setDateTime(txtDate,txtTime);                
                
                // 切换当天数据源
                if (!onlysettime)
                {
                    // 设置时间以后要重新启用后台定时器
                    LoadSysInfo.getDefault().startBackgroundTimer();
                    
                    new MessageBox(Language.apply("设置系统时间成功,系统已进入脱网运行!"), null, false);
                    
                    return true;
                }
                
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return false;
        }
    }
}
