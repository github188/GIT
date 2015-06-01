package custom.localize.Bhls;

import com.efuture.javaPos.Global.TaskExecute;

public class Bhls_TaskExecute extends TaskExecute 
{
	public boolean openDrawGrant()
	{
		// 百货无钱箱权限控制，总是允许
		return true;
	}
}
