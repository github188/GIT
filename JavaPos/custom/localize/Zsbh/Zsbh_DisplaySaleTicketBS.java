package custom.localize.Zsbh;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;

public class Zsbh_DisplaySaleTicketBS extends DisplaySaleTicketBS
{
	 public void printSaleTicket()
	    {
	        try
	        {

	        	int ret = new MessageBox("是否要重打印该小票?      \n按 1 或 回车键进行打印\n按其它键返回          ",null,false).verify();
				if (ret == GlobalVar.Key1 || ret==GlobalVar.Enter)
				{
					super.printSaleTicket();
				}
				else
				{
					return;
				}
				
	           /* // 检查发票是否打印完,打印完未设置新发票号则不能交易
	            if (Printer.getDefault().getSaleFphmComplate())
	            {
	            	return ;
	            }
	            
	        	if (GlobalInfo.posLogin.privdy != 'Y')
	        	{
	        		OperUserDef user = null;
	        		if ((user =DataService.getDefault().personGrant("授权重打印小票"))!=null)
	        		{
	        			if (user.privdy == 'Y')
	        			{
	        				String log = "授权重打印小票: 小票号" + salehead.fphm + ",授权:" + user.gh;
	        				AccessDayDB.getDefault().writeWorkLog(log);
	        			}
	        			else
	        			{
	        				new MessageBox("操作失败,该工号重打印权限!", null, false);
	        				return;
	        			}
	        		}
	        		else
	        		{
	        			return;
	        		}
	        	}
	        	
	        	salehead.printnum++;
	    		AccessDayDB.getDefault().updatePrintNum(salehead.syjh, String.valueOf(salehead.fphm), String.valueOf(salehead.printnum));
	    		ProgressBox pb = new ProgressBox();
	    		pb.setText("现在正在重打印小票,请等待.....");
	    		printSaleTicket(salehead,salegoods,salepay,false);
	    		pb.close();*/
	        }
	        catch (Exception er)
	        {
	            er.printStackTrace();
	        }
	    }

}
