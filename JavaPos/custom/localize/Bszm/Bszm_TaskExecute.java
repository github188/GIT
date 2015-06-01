package custom.localize.Bszm;

import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.AccessRemoteDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Struct.TasksDef;

public class Bszm_TaskExecute extends TaskExecute
{

	public void executeTimeTask(boolean isTimer)
	{
		try
		{
			AccessRemoteDB.getDefault().isConnection(true);//连接JSTORE
			super.executeTimeTask(isTimer);
			 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		try
		{			
			TasksDef task = null;
	        long seqno = 0;
	        boolean ret = false;
	        // 强制写入小票及缴款发送任务
	        AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDINVOICE, TaskExecute.getKeyTextByBalanceDate());
	        AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDPAYJK, TaskExecute.getKeyTextByBalanceDate());
	        // 执行任务表未完成任务
	        while ((task = AccessLocalDB.getDefault().readTask(seqno)) != null)
	        {
	        	ret=false;
	        	
	            // 读取下一个seqno任务
	            seqno = task.seqno;
	            
	            // 执行上传小票 到JSTORE 和 到POSDB及缴款单的任务
	            if (task.type == StatusType.TASK_SENDSALETOJSTORE ||
	            		task.type == StatusType.TASK_SENDINVOICE ||
	            		task.type == StatusType.TASK_SENDPAYJK) ret = TaskExecute.getDefault().executeTask(task);	           	            
	            
	            if (ret)
	            {
	                AccessLocalDB.getDefault().deleteTask(task.seqno);
	            }
	        }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public boolean executeTask(TasksDef task)
	{
		if (task.type==StatusType.TASK_SENDSALETOJSTORE)
		{
			if (GlobalInfo.RemoteDB.getIsDisConnection()==true) 
			{
				System.out.println("小票上传到JSTORE任务失败:连接已断开.");
				return false;
			}
		}
		return super.executeTask(task);
	}
	
}
