package custom.localize.Wjjy;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.LineDisplay;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustomerDef;

public class Wjjy_HykInfoQueryBS extends HykInfoQueryBS {
	 public void QueryHykInfo()
	    {
	    	StringBuffer cardno = new StringBuffer();
	    	String track2;
	    	
	    	// 输入顾客卡号
	    	TextBox txt = new TextBox();
	        if (!txt.open("请刷顾客会员卡", "会员号", "请将顾客会员卡从刷卡槽刷入", cardno, 0, 0,false, TextBox.MsrInput))
	        {
	            return;
	        }    	
	        
	        ProgressBox progress = null;
	        
	        try
	        {
	        	progress = new ProgressBox();

	        	progress.setText("正在查询会员卡信息，请等待.....");
	        	
		        // 得到顾客卡磁道信息
		        track2 = txt.Track2;
		        
		        // 查找会员卡
		        CustomerDef cust = new CustomerDef();
		        if (!DataService.getDefault().getCustomer(cust, track2))
		        {
		        	return;
		        }        
		        
				// 在客显上显示卡号及余额
		        LineDisplay.getDefault().displayAt(0, 1, cust.code);
		        LineDisplay.getDefault().displayAt(1, 1, ManipulatePrecision.doubleToString(cust.valuememo));
				
				//
				progress.close();
				progress = null;
				
				double ye = 0;
				try{
					ye = Double.parseDouble(cust.memo);
				}
				catch(Exception er)
				{
					er.printStackTrace();
				}
		        // 显示卡信息
		        StringBuffer info = new StringBuffer();
		        info.append("卡    号: " + Convert.appendStringSize("",cust.code,1,16,16,0) + "\n");
		        info.append("持 卡 人: " + Convert.appendStringSize("",cust.name,1,16,16,0) + "\n");
		        info.append("卡 状 态: " + Convert.appendStringSize("",cust.status,1,16,16,0) + "\n");
		        info.append("卡 积 分: " + Convert.appendStringSize("",ManipulatePrecision.doubleToString(cust.valuememo),1,16,16,0) + "\n");
		        info.append("卡 余 额: " + Convert.appendStringSize("",ManipulatePrecision.doubleToString(ye),1,16,16,0) + "\n");
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
