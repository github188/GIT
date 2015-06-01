package custom.localize.Nbbh;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Nbbh_DisplaySaleTicketBS extends DisplaySaleTicketBS
{
	
	
    //************************************打印调用*******************************************************
	OperUserDef user = null;
	static String[] b = null;
    public void printSaleTicket()
    {
    	   	
//		多种支付方式，选择
		String code = "";
		String[] title = { "代码", "打印小票类型" };
		int[] width = { 60, 440 };
		Vector contents = new Vector();
		contents.add(new String[] { "1", "普通交易小票" });
		contents.add(new String[] { "2", "储值卡小票" });
		
		int choice = new MutiSelectForm().open("选择打印小票类型", title, width, contents, true);
		if (choice == -1)
		{
			new MessageBox("没有选择打印小票类型") ;
			return ;
		}else {
			String[] row = (String[]) (contents.elementAt(choice));
			code = row[0];
			if( Integer.parseInt(code) == 1)
			{
//				new MessageBox("打印销售小票") ;
				reprintsale();
			}
			else
			{
//				new MessageBox("打印面值卡小票") ;
				reprintmzk();
			}
		}   	       
    }
	
    public void reprintsale()
	{
		try
        {
            // 检查发票是否打印完,打印完未设置新发票号则不能交易
            if (Printer.getDefault().getSaleFphmComplate())
            {
            	return ;
            }
            
        	if (GlobalInfo.posLogin.privdy != 'Y')
        	{
        		
        		if ((user =DataService.getDefault().personGrant(Language.apply("授权重打印小票")))!=null)
        		{
        			if (user.privdy == 'Y')
        			{
        				String log = Language.apply("授权重打印小票: 小票号") + salehead.fphm + Language.apply(",授权:") + user.gh;
        				AccessDayDB.getDefault().writeWorkLog(log);
        			}
        			else
        			{
        				new MessageBox(Language.apply("操作失败,该工号没有重打印权限!"), null, false);
        				return ;
        			}
        		}
        		else
        		{
        			return;
        		}
        	}
        	else
        	{
        		user = new OperUserDef();
        	}
        	
        	salehead.printnum++;
    		AccessDayDB.getDefault().updatePrintNum(salehead.syjh, String.valueOf(salehead.fphm), String.valueOf(salehead.printnum));
    		
    		if(!getReprint()) return ;
     		
    		ProgressBox pb = new ProgressBox();
    		pb.setText(Language.apply("现在正在打印小票,请等待....."));
    		
    		
    		
    		
    		printSaleTicket(salehead,salegoods,salepay,false);
    		
    		
    		
    		
			if(salehead.printnum > 0)
			{
				String[] s = Nbbh_DisplaySaleTicketBS.getResult();   //上传字段

				s[7] = String.valueOf(Printer.getDefault().getCurrentSaleFphm() -1);
				new Nbbh_NetService().postReprint(s);
			}
    		
    		
    		
    		
    		pb.close();
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }
	}
	


    public void reprintmzk()
	{
//		 打印面值卡联
    	try
    	{
    		if (salehead != null && salegoods != null && salepay != null)
            {
//    			if(!getReprint(salehead.syyh)) return ;
    			
    			SaleBillMode.getDefault().setTemplateObject(salehead, salegoods, salepay);
  			
    			SaleBillMode.getDefault().printMZKBill(1);
    			
//				String[] s = Nbbh_DisplaySaleTicketBS.getResult();   //上传字段
//				s[6]=s[7] = String.valueOf(Printer.getDefault().getCurrentSaleFphm()-1);
//				new Nbbh_NetService().postReprint(s);
    			
    			
    			
            }
    		else
    		{
    			new MessageBox(Language.apply("未发现小票对象，不能打印\n或\n打印模版读取失败"));
    		}
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
	}
	
	
	protected boolean getReprint()
	{
//		从本地查找重打原因
		ResultSet rs = null;
		ReprintDef print = null;
		try{			
			if ((rs = GlobalInfo.localDB.selectData("select IWID,IWMEMO,IWSTATUS from REPRINT")) != null)
			{
				if (!rs.next())
				{
					new MessageBox(Language.apply("没有查询到小票打印原因!"));
					return false;
				}

				String code = "";
				String text = "";
				String log = "";
				String printType = "2";              //发票打印类型  1正常销售 2重打
				
				String startfph = String.valueOf(Printer.getDefault().getCurrentSaleFphm());
				String usedfphnum = "";
				
				Vector reprint = new Vector();
				String[] title =  { "打印原因ID" ,"原因说明" ,"是否启用(Y/N)"};
				int[] width = { 150, 250 ,150};

				do{
					print = new ReprintDef();
					
					if (!GlobalInfo.localDB.getResultSetToObject(print)) { return false; }
					
					reprint.add(new String[] {String.valueOf(print.IWID), print.IWMEMO, print.IWSTATUS});
				}while(rs.next());

				if(user.gh == null) user.gh = GlobalInfo.posLogin.gh;						
				int choice = new MutiSelectForm().open("请选择打印原因", title, width, reprint, true);
				if (choice == -1)
				{
					new MessageBox(Language.apply("没有选择打印原因"));
					log = "收银机号:" + ConfigClass.CashRegisterCode + ",小票号:" + salehead.fphm + ",发票打印类型:" + printType + ",打印原因:" + "0" + "没有选择打印原因" + ",授权工号:"+ user.gh ;
					return false;
				}else {
					String[] row = (String[]) (reprint.elementAt(choice));
					code = row[0].toString();
					text = row[2].toString();
					log = "收银机号:" + ConfigClass.CashRegisterCode + ",小票号:" + salehead.fphm + ",发票打印类型:" + printType + ",打印原因:" + code + ",授权工号:" + user.gh;
				}
				AccessDayDB.getDefault().writeWorkLog(log,"1234");
				
				String[] s = {GlobalInfo.sysPara.mktcode, ConfigClass.CashRegisterCode ,String.valueOf(salehead.fphm) ,printType ,code ,user.gh ,startfph ,usedfphnum ,ManipulateDateTime.getDateTimeByClock()};
				
				b = s;
				
				return true;
			}
			else
			{
				new MessageBox(Language.apply("没有查询到小票打印原因!"), null, false);
				return false;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			new MessageBox(Language.apply("获取打印原因列表异常"));
			return false;
		}		
	}
	
	public static String[] getResult()
	{
		return b;
	}
}
