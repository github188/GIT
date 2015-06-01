package custom.localize.Jdhx;

import java.io.File;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Logic.SetSystemTimeBS;

public class Jdhx_SetSystemTimeBS extends SetSystemTimeBS
{

    public boolean modifyDateTime(String txtDate, String txtTime)
    {
        try
        {
            //提示
            MessageBox me = new MessageBox(Language.apply("你确定要重新设置系统时间\n\n并将系统切换到单机运行吗?"), null, true);

            if (me.verify() == GlobalVar.Key1)
            {               
        		// 江都宏信要求在设置日期时间时 检查设置的日期是否 大于记录流水的最大日期
        		File file = new File(ConfigClass.LocalDBPath + "Invoice");
        		String[] fileList = file.list();
        		if (fileList != null )
        		{
        			String dateMax = fileList[0];
        			for(int i = 0 ; i < fileList.length; i++)
        			{
        				if (dateMax.compareToIgnoreCase(fileList[i]) < 0)
        				{
        					dateMax = fileList[i];
        				}
        			}
        			dateMax = dateMax.substring(0,4) + "/" + dateMax.substring(4,6)+ "/" + dateMax.substring(6);
            		if (ManipulateDateTime.isValidDate(dateMax))
            		{
            			if (dateMax.compareToIgnoreCase(txtDate) > 0)
            			{
            				new MessageBox("设置的当前日期【"+ txtDate + "】比已记录的" +
            						     "\n小票流水的日期【" + dateMax + "】早!       \n\n请重新设置当前日期时间");
            				return false;
            			}
            		}
        			
        		}
        		
            	// 记录日志
            	if (!isOnlysettime())
            	{           		
	                // 记工作日志
	                AccessDayDB.getDefault().writeWorkLog("重新设置系统时间为 " + txtDate + " " + txtTime);
	
	                // 设置脱网
	                getDcnwb().setDisConnNet();
            	}
            	
                // 设置系统时间
                ManipulateDateTime mdt = new ManipulateDateTime(); 
                mdt.setDateTime(txtDate,txtTime);                
                
                // 切换当天数据源
                if (!isOnlysettime())
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
