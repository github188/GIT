package custom.localize.Sdyz;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.LineDisplay;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustomerDef;

public class Sdyz_HykInfoQueryBS extends HykInfoQueryBS
{
    public void QueryHykInfo()
    {
    	StringBuffer cardno = new StringBuffer();
    	String track2,track3;
    	boolean iszshy = false;
    			
    	//
    	if (!GlobalInfo.isOnline)
    	{
    		new MessageBox("顾客会员卡必须联网使用");
    		return;
    	}
    	
    	// 输入顾客卡号
    	TextBox txt = new TextBox();
        if (!txt.open("请刷顾客会员卡", "会员号", "请将顾客会员卡从刷卡槽刷入", cardno, 0, 0,false, TextBox.MsrInput))
        {
            return ;
        }
        
        ProgressBox progress = null;
        
        try
        {
        	progress = new ProgressBox();

        	progress.setText("正在查询会员卡信息，请等待.....");
        	
	        // 得到顾客卡磁道信息
	        track2 = txt.Track2;
	        track3 = txt.Track3;
	    	        
	    	// 山东银座会员卡要截取
	    	int l = track2.length();
	    	if (l >= 13)
	    	{
	    		if (track2.charAt(0) < '0' || track2.charAt(0) > '9')
	    		{
	    			track2 = track2.substring(1);
	    		}
	    		
	    		StringBuffer s = new StringBuffer();
	    		s.append(track2.charAt(0));
	    		s.append(track2.charAt(2));
	    		s.append(track2.charAt(3));
	    		s.append(track2.charAt(5));
	    		s.append(track2.charAt(6));
	    		s.append(track2.charAt(8));
	    		s.append(track2.charAt(9));
	    		s.append(track2.charAt(11));
	    		s.append(track2.charAt(12));
	    		track2 = s.toString();
	    	}
	        
	    	// 招行联名卡
			if (track3.length() > 70)
			{
				track2 = track3.substring(69,78);
				iszshy = true;
			}
	    	
	        // 查找会员卡
	        CustomerDef cust = new CustomerDef();
	        if (!DataService.getDefault().getCustomer(cust, track2))
	        {
	        	return;
	        } 
	        
			// 在客显上显示卡号及积分
	        LineDisplay.getDefault().displayAt(0, 1, cust.code);
	        LineDisplay.getDefault().displayAt(1, 1, ManipulatePrecision.doubleToString(cust.value3));
			
			//
			progress.close();
			progress = null;
			
	        // 显示卡信息
	        StringBuffer info = new StringBuffer();
	        info.append("卡    号: " + Convert.appendStringSize("",cust.code,1,16,16,0) + "\n");
	        info.append("持 卡 人: " + Convert.appendStringSize("",cust.name,1,16,16,0) + "\n");
	        info.append("卡 类 型: " + Convert.appendStringSize("",(iszshy?"招商联名会员卡":"顾客会员卡"),1,16,16,0) + "\n");
	        info.append("卡 积 分: " + Convert.appendStringSize("",ManipulatePrecision.doubleToString(cust.value3),1,16,16,0) + "\n");
	        info.append("积分折扣: " + Convert.appendStringSize("",ManipulatePrecision.doubleToString(cust.value2),1,16,16,0) + "\n");
	        info.append("会员功能: " + Convert.appendStringSize("",getFuncText(cust.ishy),1,16,16,0) + "\n");
	        info.append("积分功能: " + Convert.appendStringSize("",getFuncText(cust.isjf),1,16,16,0) + "\n");
	        info.append("折扣功能: " + Convert.appendStringSize("",getFuncText(cust.iszk),1,16,16,0) + "\n");
	        
	        new MessageBox(info.toString());
        }
        finally
        {
        	if (progress != null) progress.close();
        } 	        
    }

}
