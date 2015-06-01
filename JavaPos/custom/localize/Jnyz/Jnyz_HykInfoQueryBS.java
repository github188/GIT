package custom.localize.Jnyz;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustFilterDef;
import com.efuture.javaPos.Struct.CustomerDef;

public class Jnyz_HykInfoQueryBS extends HykInfoQueryBS{
	
	
	private String track3 = null;
	
	protected void getHykDisplayInfo(CustomerDef cust, StringBuffer info)
	{
		info.append("卡    号: " + Convert.appendStringSize("", cust.code, 1, 16, 16, 0) + "\n");
		info.append("持 卡 人: " + Convert.appendStringSize("", cust.name, 1, 16, 16, 0) + "\n");
		String cardType = cust.type;
		if((cardType !=null && cardType.length() > 0) && cardType.equals("01"))
		{
			cardType = "银鼎卡";
		}
		else if((cardType !=null && cardType.length() > 0) && cardType.equals("02")){
			cardType = "金鼎卡";
		}
		else if((cardType !=null && cardType.length() > 0) && cardType.equals("03")){
			cardType = "钻石卡";
		}
		info.append("卡类型: " + Convert.appendStringSize("",cardType, 1, 16, 16, 0) + "\n");
		info.append("是否折扣: " + Convert.appendStringSize("", getFuncText(cust.iszk), 1, 16, 16, 0) + "\n");
		info.append("本年积分余额: " + Convert.appendStringSize("",ManipulatePrecision.doubleToString(cust.valuememo), 1, 16, 16, 0) + "\n");
		info.append("去年积分余额: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.value2), 1, 16, 16, 0) + "\n");
		info.append("本次可兑换金额: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.value3), 1, 16, 16, 0) + "\n");
		info.append("今年累计积分: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.value4), 1, 16, 16, 0) + "\n");
//		if (isLczcFunc(cust))
//		{
//			info.append("零钞转存: " + Convert.appendStringSize("", getFuncText('Y'), 1, 16, 16, 0) + "\n");
//			info.append("零钞余额: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.valuememo), 1, 16, 16, 0) + "\n");
//			info.append("零钞上限: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.value2), 1, 16, 16, 0) + "\n");
//		}
	}

	
	public String readMemberCard(boolean ispay)
	{
		selectedRule = null;

		StringBuffer cardno = new StringBuffer();

		// 获取自定义的解析规则
		Vector rulelist = showRule();
		
		
		
		if (rulelist != null && rulelist.size() <= 0)
			rulelist = null;

		// 先选择规则后刷会员卡
		boolean ch = false;
		if (GlobalInfo.sysPara.unionVIPMode == 'A')
		{
			if (rulelist != null && rulelist.size() > 1)
			{
				rulelist = chooseRule(rulelist, ispay);
				if (rulelist != null)
					ch = true;
			}
			else if (rulelist != null && rulelist.size() == 1)
				ch = true;
		}

		int type = getMemberInputMode();
		if (ch && rulelist != null && rulelist.size() > 0)
		{
			CustFilterDef rule = (CustFilterDef) rulelist.elementAt(0);
			if (rule.InputType != -2)
				type = rule.InputType;
		}

		// 输入顾客卡号
		TextBox txt = new TextBox();
		if (!txt.open("请刷会员卡或顾客打折卡", "会员号", "请将会员卡或顾客打折卡从刷卡槽刷入", cardno, 0, 0, false, type)) { return null; }

		track3 = txt.Track3;//联名卡用到3磁道
		// 调用客户化会员磁道解析程序
		String tr = getTrackByCustom(txt.Track2);

		// 检查磁道是否和规则相匹配
		if (rulelist != null && rulelist.size() > 0)
		{
			rulelist = chkTrack(txt.Track1, txt.Track2, txt.Track3, rulelist, ispay);
		}

		// 如果匹配的规则有多个,再次让客户选择(B模式先刷卡后选择)
		if (rulelist != null && rulelist.size() > 1)
		{
			rulelist = chooseRule(rulelist, ispay);
		}

		// 解析有效规则下的磁道号
		if (rulelist != null && rulelist.size() > 0)
		{
			selectedRule = (CustFilterDef) rulelist.get(0);
			tr = getTrackByDefine(txt.Track1, txt.Track2, txt.Track3, rulelist);
		}
		else
		{
			if (ch)
			{
				new MessageBox("刷卡与联名卡规则不匹配，该卡无效");
				return null;
			}
		}

		// 记录磁道供返立即查询券卡使用
		if (GlobalInfo.sysPara.findcustfjk == 'Y')
		{
			custtrack = new String[] { txt.Track1, (tr == null) ? txt.Track2 : tr, txt.Track3 };
		}

		return tr;
	}
	
	public CustomerDef findMemberCard(String track2)
	{
		ProgressBox progress = null;
		CustomerDef cust = null;
		try
		{
			progress = new ProgressBox();
			progress.setText("正在查询会员卡信息，请等待.....");

			// 查找会员卡
			cust = new CustomerDef();
			cust.str2 = "2";//磁道查询
			cust.str3 = track3;
			if (!DataService.getDefault().getCustomer(cust, track2)) { return null; }
			if (cust.code == null || cust.code.trim().equals(""))
			{
				new MessageBox("查询的会员卡信息无效!\n请找后台人员");
				return null;
			}
		}
		finally
		{
			if (progress != null)
				progress.close();
		}

		return cust;
	}
}
