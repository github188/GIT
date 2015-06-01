package custom.localize.Tygc;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Struct.OperUserDef;

public class Tygc_DisplaySaleTicketBS extends DisplaySaleTicketBS
{

	
	
    //************************************打印调用*******************************************************
//	OperUserDef user = null;
//	static String[] b = null;
    public void printSaleTicket()
    {
		try
        {
            // 检查发票是否打印完,打印完未设置新发票号则不能交易
            if (Printer.getDefault().getSaleFphmComplate())
            {
            	return ;
            }
            OperUserDef user = null;
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
        	
        	if(user!=null)salehead.str9 = user.gh;
        	salehead.printnum++;
    		AccessDayDB.getDefault().updatePrintNum(salehead.syjh, String.valueOf(salehead.fphm), String.valueOf(salehead.printnum));
    		
//    		if(!getReprint()) return ;
     		
    		ProgressBox pb = new ProgressBox();
    		pb.setText(Language.apply("现在正在打印小票,请等待....."));
    		
    		
    		
    		
    		printSaleTicket(salehead,salegoods,salepay,false);
    		
    		
    		
    		
//			if(salehead.printnum > 0)
//			{
//				String[] s = Tygc_DisplaySaleTicketBS.getResult();   //上传字段
//
//				s[7] = String.valueOf(Printer.getDefault().getCurrentSaleFphm() -1);
//				new Tygc_NetService().postReprint(s);
//			}
    		
    		
    		pb.close();
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }
	}
	
//	
//	protected boolean getReprint()
//	{
////		从本地查找重打原因
//		ResultSet rs = null;
//		ReprintDef print = null;
//		try{			
//			if ((rs = GlobalInfo.localDB.selectData("select IWID,IWMEMO,IWSTATUS from REPRINT")) != null)
//			{
//				if (!rs.next())
//				{
//					new MessageBox(Language.apply("没有查询到小票打印原因!"));
//					return false;
//				}
//
//				String code = "";
//				String text = "";
//				String log = "";
//				String printType = "2";              //发票打印类型  1正常销售 2重打
//				
//				String startfph = String.valueOf(Printer.getDefault().getCurrentSaleFphm());
//				String usedfphnum = "";
//				
//				Vector reprint = new Vector();
//				String[] title =  { "打印原因ID" ,"原因说明" ,"是否启用(Y/N)"};
//				int[] width = { 150, 250 ,150};
//
//				do{
//					print = new ReprintDef();
//					
//					if (!GlobalInfo.localDB.getResultSetToObject(print)) { return false; }
//					
//					reprint.add(new String[] {String.valueOf(print.IWID), print.IWMEMO, print.IWSTATUS});
//				}while(rs.next());
//
//				if(user.gh == null) user.gh = GlobalInfo.posLogin.gh;						
//				int choice = new MutiSelectForm().open("请选择打印原因", title, width, reprint, true);
//				if (choice == -1)
//				{
//					new MessageBox(Language.apply("没有选择打印原因"));
//					log = "收银机号:" + ConfigClass.CashRegisterCode + ",小票号:" + salehead.fphm + ",发票打印类型:" + printType + ",打印原因:" + "0" + "没有选择打印原因" + ",授权工号:"+ user.gh ;
//					return false;
//				}else {
//					String[] row = (String[]) (reprint.elementAt(choice));
//					code = row[0].toString();
//					text = row[2].toString();
//					log = "收银机号:" + ConfigClass.CashRegisterCode + ",小票号:" + salehead.fphm + ",发票打印类型:" + printType + ",打印原因:" + code + ",授权工号:" + user.gh;
//				}
//				AccessDayDB.getDefault().writeWorkLog(log,"1234");
//				
//				String[] s = {GlobalInfo.sysPara.mktcode, ConfigClass.CashRegisterCode ,String.valueOf(salehead.fphm) ,printType ,code ,user.gh ,startfph ,usedfphnum ,ManipulateDateTime.getDateTimeByClock(),"0"};
//				
//				b = s;
//				
//				return true;
//			}
//			else
//			{
//				new MessageBox(Language.apply("没有查询到小票打印原因!"), null, false);
//				return false;
//			}
//		}catch(Exception e)
//		{
//			e.printStackTrace();
//			new MessageBox(Language.apply("获取打印原因列表异常"));
//			return false;
//		}		
//	}
//	
//	public static String[] getResult()
//	{
//		return b;
//	}

}
