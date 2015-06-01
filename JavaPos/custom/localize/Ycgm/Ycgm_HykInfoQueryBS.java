package custom.localize.Ycgm;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustomerDef;

public class Ycgm_HykInfoQueryBS extends HykInfoQueryBS
{
	String str = "";
	
	//根据轨道信息，解析出卡号
	public String getCardnoFromTrack(String track)
	{
		String no = track;
		
		//轨道规则：  ;3453253535235=525345? ,在 “；”与“=”之间的为卡号
		if (!track.startsWith(";") && track.indexOf('=') > -1)
		{
			no = track.substring(0,track.indexOf('='));
		}
		else if (track.startsWith(";") && track.indexOf('=') > -1)
		{
			no = track.substring(1,track.indexOf('='));
		}
		
		return no;
	}
	
	
	public CustomerDef findMemberCard(String track2)
	{
		ProgressBox progress = null;
		CustomerDef cust = null;
		try
		{
			progress = new ProgressBox();
			progress.setText(Language.apply("正在查询会员卡信息，请等待....."));
			
			// 查找会员卡
			cust = new CustomerDef();
						
			//宜昌国贸要求，在JavaPos查询会员时要到同程CRM系统中效验卡号是否有效
			//同程CRM中磁道号是重写后的，与JavaPos不一致，因此JavaPos通过CRM中返回的卡号来查询会员
			
			if ("1".equals(Excute.validate) || "3".equals(Excute.validate))
			{
				if(!getCustomerTc(cust, track2))
				{
					new MessageBox(Language.apply("在CRM中查询的会员卡信息无效!\n请找后台人员"));
					return null;
				}
			}
			
			if ("1".equals(Excute.validate) || "2".equals(Excute.validate))
			{
				if ("Y".equals(Excute.deal))
					track2 = getCardnoFromTrack(track2);
			    //if (!DataService.getDefault().getCustomer(cust, getCardnoFromTrack(track2))) { return null; }
				if (!DataService.getDefault().getCustomer(cust, track2)) { return null; }
				if (cust.code == null || cust.code.trim().equals(""))
				{
					new MessageBox(Language.apply("查询的会员卡信息无效!\n请找后台人员"));
					return null;
				}
			}
			cust.str2 = str;

		}
		finally
		{
			if (progress != null)
				progress.close();
		}

		return cust;
	}
	
	//到同程CRM系统查询会员
	public boolean getCustomerTc(CustomerDef cust,String track2)
	{
		try
		{
			
			String rs = Excute.queryJfOrCzInfo(track2, " ", " ");
			if (null == rs || "".equals(rs))
			{
				
				return false;
			}
			
			//在解析CRM返回的信息时，返回字符长度必须大于114，否则会出现数组越界异常
			if (rs.length() < 114)
				return false;
			
			str = rs; //记录CRM中返回的卡信息
			
			cust.status = "Y";
			cust.code = rs.substring(93, 113).trim();
			cust.name = Convert.newSubString(rs,113,133).trim();
//		cust.maxdate = rs.substring(85, 93).trim();
//		cust.valuememo = Double.parseDouble(rs.substring(7,20));
//		//cust.zkl = Double.parseDouble(Convert.newSubString(rs,173,193).trim());
//		
			cust.track = track2;
			cust.str3 = ""; //密码
			
			return true;
		}
		catch(Exception e)
		{
			new MessageBox("解析查询返回信息异常:" + e.getMessage());
			return false;
		}
	}
	
	protected void getHykDisplayInfo(CustomerDef cust, StringBuffer info)
	{
		try
		{

			String rs = cust.str2;
			info.append(Language.apply("卡    号: ") + Convert.appendStringSize("", rs.substring(93, 113).trim(), 1, 16, 16, 0) + "\n");
			info.append(Language.apply("持 卡 人: ") + Convert.appendStringSize("", Convert.newSubString(rs,113,133).trim(), 1, 16, 16, 0) + "\n");
			info.append(Language.apply("卡 积 分: ") + Convert.appendStringSize("", Convert.toDouble(rs.substring(20,33).trim()) + "", 1, 16, 16, 0) + "\n");
			info.append(Language.apply("卡 积 点: ") + Convert.appendStringSize("", Convert.toDouble(rs.substring(72, 85).trim()) + "", 1, 16, 16, 0) + "\n");
			
			if (isLczcFunc(cust))
			{
				info.append(Language.apply("零钞转存: ") + Convert.appendStringSize("", getFuncText('Y'), 1, 16, 16, 0) + "\n");
				info.append(Language.apply("零钞余额: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.value1), 1, 16, 16, 0) + "\n");
				info.append(Language.apply("零钞上限: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.value2), 1, 16, 16, 0) + "\n");
			}
		}
		catch(Exception e)
		{
			new MessageBox("显示会员信息异常" + cust.str2 + "\n" + e.getMessage());
		}
	}
}
