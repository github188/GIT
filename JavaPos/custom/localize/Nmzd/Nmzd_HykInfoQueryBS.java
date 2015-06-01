package custom.localize.Nmzd;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustFilterDef;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Nmzd_HykInfoQueryBS extends HykInfoQueryBS
{
	protected void getHykDisplayInfo(CustomerDef cust, StringBuffer info)
	{
		info.append("卡    号: " + Convert.appendStringSize("", cust.code, 1, 16, 16, 0) + "\n");
		info.append("持 卡 人: " + Convert.appendStringSize("", cust.name, 1, 16, 16, 0) + "\n");
		info.append("卡 状 态: " + Convert.appendStringSize("", cust.status, 1, 16, 16, 0) + "\n");
		info.append("卡 积 分: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.valuememo), 1, 16, 16, 0) + "\n");
		info.append("会员功能: " + Convert.appendStringSize("", getFuncText(cust.ishy), 1, 16, 16, 0) + "\n");
		info.append("积分功能: " + Convert.appendStringSize("", getFuncText(cust.isjf), 1, 16, 16, 0) + "\n");
		info.append("折扣功能: " + Convert.appendStringSize("", getFuncText(cust.iszk), 1, 16, 16, 0) + "\n");
		if (isLczcFunc(cust))
		{
			info.append("零钞转存: " + Convert.appendStringSize("", getFuncText('Y'), 1, 16, 16, 0) + "\n");
			info.append("零钞余额: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.value1), 1, 16, 16, 0) + "\n");
			info.append("零钞上限: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.value2), 1, 16, 16, 0) + "\n");
		}
		double zxje = 0;
		if (cust.memo !=null && cust.memo.split(",").length>=2)
		{
//			分解积分折现规则
			String[] num = cust.memo.split(",");
			
			//num1:积分数  num2:折现金额
			double num1 = Double.parseDouble(num[0]);
			double num2 = Double.parseDouble(num[1]);
			
			if (num2 > 0) zxje = (int)ManipulatePrecision.div(cust.valuememo,num1)*num2;
		}
			
		info.append("积分折现："+Convert.appendStringSize("", ManipulatePrecision.doubleToString(zxje), 1, 16, 16, 0) + "\n");
		
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
		boolean phone = false;
		if (ch && rulelist != null && rulelist.size() > 0)
		{
			CustFilterDef rule = (CustFilterDef) rulelist.elementAt(0);
			if (rule.InputType != -2)
				type = rule.InputType;
			
			if (rule.desc.indexOf("手机") >=0)
			{
				phone = true;
			}
		}
		
		
		// 输入顾客卡号
		TextBox txt = new TextBox();
		String tr = null;
		if (!phone)
		{
			if (!txt.open("请刷会员卡或顾客打折卡", "会员号", "请将会员卡或顾客打折卡从刷卡槽刷入", cardno, 0, 0, false, type)) { return null; }
			 tr= getTrackByCustom(txt.Track2);
		}
		else
		{
			if (!txt.open("请输入手机号", "手机号", "请输入手机号", cardno, 0, 0, false, type)) { return null; }
			
			if (txt.Track2 == null)
			{
				tr = cardno.toString();
				txt.Track2 = tr;
			}
		}
		// 调用客户化会员磁道解析程序
		
		

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
		
		//如果是手机，查询手机号对应的卡号
		if (phone)
		{
			Vector v = new Vector();
			if (((Nmzd_NetService)NetService.getDefault()).FINDMOBILE2CARD(v,txt.Track2))
			{
				for (int i = 0 ; i < v.size(); i++)
				{
					String[] info = (String[]) v.elementAt(i);
					String jf = ManipulatePrecision.doubleToString(Convert.toDouble(info[2]),1,1,false);
					String je = ManipulatePrecision.doubleToString(Convert.toDouble(info[3]),1,1,false);
					info[2] = jf;
					info[3] = je;
				}
				
				//String[] info = new String[]{"123456789012345678","老伯一二三四","9999999.9","9999999.9","卡类型卡类型卡类型卡类型"};
				//v.add(info);
				if (v.size() == 1)
				{
					String[] s = (String[]) v.elementAt(0);
					txt.Track2 = "#"+s[0];
					tr = "#"+s[0];
				}
				else
				{
					String[] title = {"卡号", "姓名","当前积分","当前消费" ,"卡类型"};
					int[] width = { 190,135,-110,-110,216 };
	
					int choice = new MutiSelectForm().open("请选择卡类型", title, width, v,false,800, 400,780, 275,false);
					if (choice == -1)
						return null;
	
					String[] s = (String[]) v.elementAt(choice);
					txt.Track2 = "#"+s[0];
					tr = "#"+s[0];
				}
			}
			
		}

		// 记录磁道供返立即查询券卡使用
		if (GlobalInfo.sysPara.findcustfjk == 'Y')
		{
			custtrack = new String[] { txt.Track1, (tr == null) ? txt.Track2 : tr, txt.Track3 };
		}

		return tr;
	}
}
