package custom.localize.Jwyt;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustFilterDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Jwyt_HykInfoQueryBS extends HykInfoQueryBS
{
	public int getMemberInputMode()
	{
		if (!Jwyt_SaleBS.isOpenSelectForm)
			Jwyt_SaleBS.autoMSR();

		Jwyt_SaleBS.isOpenSelectForm = false;
		Jwyt_SaleBS.hykOprType = false;
		return super.getMemberInputMode();
	}

	public Vector chooseRule(Vector rulelist, boolean ispay)
	{

		Vector con = new Vector();
		String titleInfo = "会员卡类型";

		for (int i = 0; i < rulelist.size(); i++)
		{
			CustFilterDef filterDef = (CustFilterDef) rulelist.elementAt(i);

			if (filterDef.ispay == 1 && ispay)
				continue;

			con.add(new String[] { filterDef.desc });
		}

		if (Jwyt_SaleBS.hykOprType)
			titleInfo = "【会员卡授权】";
		else
			titleInfo = "【会员卡信息查询】";

		String[] title = { "请选择会员卡类型" };
		int[] width = { 500 };

		int choice = new MutiSelectForm().open(titleInfo, title, width, con);
		if (choice == -1)
			return null;

		CustFilterDef rule = ((CustFilterDef) rulelist.elementAt(choice));
		rulelist.removeAllElements();
		rulelist.add(rule);

		return rulelist;
	}

	public String readMemberCard(boolean ispay)
	{
		StringBuffer cardno = new StringBuffer();
		Vector rulelist = null;
		boolean ch = false;
		String tipInfo1 = "请刷会员卡或顾客打折卡";
		String tipInfo2 = "请刷会员卡或顾客打折卡\n直接将卡片从刷卡槽刷过即可";

		if (Jwyt_SaleBS.isOpenSelectForm)
		{
			rulelist = showRule(); // 获取自定义的解析规则
			tipInfo2 = "请输入顾客手机号码或身份证号码\n直接输入数字即可";
		}

		if (Jwyt_SaleBS.hykOprType)
			tipInfo1 = "【会员卡授权】";
		else
			tipInfo1 = "【会员卡信息查询】";

		if (rulelist != null && rulelist.size() <= 0)
			rulelist = null;

		// 先选择规则后刷会员卡
		if (GlobalInfo.sysPara.unionVIPMode == 'A')
		{
			if (rulelist != null && rulelist.size() > 1)
			{
				rulelist = chooseRule(rulelist, ispay);
				if (rulelist != null)
					ch = true;
				else
				{
					Jwyt_SaleBS.isOpenSelectForm = false;
					return null;
				}
			}
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
		if (!txt.open(tipInfo1, "会员号", tipInfo2, cardno, 0, 0, false, type)) { return null; }

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

		Jwyt_SaleBS.isOpenSelectForm = false;
		return tr;
	}

	public Vector showRule()
	{
		if (!Jwyt_SaleBS.isOpenSelectForm)
			return null;

		return super.showRule();
	}
}
