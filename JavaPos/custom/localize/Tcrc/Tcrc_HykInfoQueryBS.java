package custom.localize.Tcrc;

import java.util.Vector;

import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Tcrc_HykInfoQueryBS extends HykInfoQueryBS 
{
	public Tcrc_HykInfoQueryBS() 
	{
	}
	
	public String readMemberCard() 
	{
        StringBuffer cardno = new StringBuffer();
        String track2;
		
        String[] title = { "会员卡类型" };
        int[] width = { 500 };
        Vector contents = new Vector();
        contents.add(new String[] { "VIP会员卡" });
        contents.add(new String[] { "VIP手机号" });
        
        int choice = new MutiSelectForm().open("请选择卡类型", title, width, contents);
		if (choice == -1) return null;
		String hint = ((String[])contents.elementAt(choice))[0];
		
		//M:---------------------------------------------------------------
		//M:脱网时无法刷会员卡，此处进行修改
        
		if (GlobalInfo.isOnline){
			
			//输入顾客卡号
			if (choice == 0)
			{
		    	TextBox txt = new TextBox();
		        if (!txt.open("请刷" + hint, "会员号", "请将" + hint + "从刷卡槽刷入", cardno, 0, 0,false, TextBox.MsrInput))
		        {
		            return null;
		        }
		
		        //得到顾客卡磁道信息
		        track2 = "1,"+txt.Track2;
			}
			else
			{
		    	TextBox txt = new TextBox();
		        if (!txt.open("请输入" + hint, "手机号", "请直接输入手机号来查询VIP会员", cardno, 0, 0,false, TextBox.AllInput))
		        {
		            return null;
		        }
		
		        //得到顾客卡磁道信息
		        track2 = "2,"+cardno.toString();
			}
			
		}else{
			//输入顾客卡号
			if (choice == 0)
			{
		    	TextBox txt = new TextBox();
		        if (!txt.open("请刷" + hint, "会员号", "请将" + hint + "从刷卡槽刷入", cardno, 0, 0,false, TextBox.MsrInput))
		        {
		            return null;
		        }
		
		        //得到顾客卡磁道信息
		        track2 = txt.Track2;
			}
			else
			{
		    	TextBox txt = new TextBox();
		        if (!txt.open("请输入" + hint, "手机号", "请直接输入手机号来查询VIP会员", cardno, 0, 0,false, TextBox.AllInput))
		        {
		            return null;
		        }
		
		        //得到顾客卡磁道信息
		        track2 = cardno.toString();
			}
		}
		
		//M:-----------------------------------------------------修改前原代码
		
		
//        //输入顾客卡号
//		if (choice == 0)
//		{
//	    	TextBox txt = new TextBox();
//	        if (!txt.open("请刷" + hint, "会员号", "请将" + hint + "从刷卡槽刷入", cardno, 0, 0,false, TextBox.MsrInput))
//	        {
//	            return null;
//	        }
//	
//	        //得到顾客卡磁道信息
//	        track2 = "1,"+txt.Track2;
//		}
//		else
//		{
//	    	TextBox txt = new TextBox();
//	        if (!txt.open("请输入" + hint, "手机号", "请直接输入手机号来查询VIP会员", cardno, 0, 0,false, TextBox.AllInput))
//	        {
//	            return null;
//	        }
//	
//	        //得到顾客卡磁道信息
//	        track2 = "2,"+cardno.toString();
//		}
		
        return track2;
	}
}
