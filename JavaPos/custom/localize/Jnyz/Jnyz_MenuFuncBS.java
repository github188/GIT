package custom.localize.Jnyz;

import com.efuture.DeBugTools.PosLog;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.UI.MenuFuncEvent;


public class Jnyz_MenuFuncBS extends MenuFuncBS
{
	 public final static int PRINT_RECEIPT = 417;//打印网上小票	
		
		public void execFuncMenu(MenuFuncDef mfd, MenuFuncEvent mffe)
		{
			try
			{
				if (Integer.parseInt(mfd.code) == 417)
				{
					printReceipt();
				}
				else
				{
					super.execFuncMenu(mfd, mffe);
				}
			}
			catch(Exception ex)
			{
				PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			}
		}
		
		
		//打印网上小票
	private void printReceipt()
	{
		new Jnyz_SaleBS().getReceipt();
	}

}
