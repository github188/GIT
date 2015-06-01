package custom.localize.Wqbh;

import java.util.Vector;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.LineDisplay;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustFilterDef;
import com.efuture.javaPos.Struct.CustomerDef;

import device.ICCard.KTL512VWQ;

public class Wqbh_HykInfoQueryBS extends HykInfoQueryBS
{
	public Wqbh_HykInfoQueryBS()
	{
	}

	public void QueryHykInfo()
	{
		// 读会员卡
		String track2 = readMemberCard();
		if (track2 == null || track2.equals("")) return;

		// 解析出磁道和选择的类型
		String[] s = track2.split(",");
		track2 = s[0];

		// 查找会员卡
		CustomerDef cust = findMemberCard(track2);
		if (cust == null) return;

		// 在客显上显示卡号及余额
		LineDisplay.getDefault().displayAt(0, 1, cust.code);
		LineDisplay.getDefault().displayAt(1, 1, ManipulatePrecision.doubleToString(cust.valuememo));

		// 显示卡信息
		hykDisplayInfo(cust);
	}

	public String readMemberCard(boolean ispay)
	{
		StringBuffer cardno = new StringBuffer();

		// 获取自定义的解析规则
		Vector rulelist = showRule();
		if (rulelist != null && rulelist.size() <= 0) rulelist = null;

		// 先选择规则后刷会员卡
		boolean ch = false;
		if (GlobalInfo.sysPara.unionVIPMode == 'A')
		{
			if (rulelist != null && rulelist.size() > 1)
			{
				rulelist = chooseRule(rulelist, ispay);
				if (rulelist != null) ch = true;
			}
		}

		int type = getMemberInputMode();
		if (ch && rulelist != null && rulelist.size() > 0)
		{
			CustFilterDef rule = (CustFilterDef) rulelist.elementAt(0);
			if (rule.InputType != -2) type = rule.InputType;
		}

		// 输入顾客卡号
		TextBox txt = new TextBox();
		String track2 = "";
		if (type == 3)
		{
			ProgressBox pb = null;
			pb = new ProgressBox();
			pb.setText("正在输入卡号和密码,请等待...");
			track2 = new KTL512VWQ().findCard();
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}
		else
		{
			if (!txt.open("请刷会员卡或顾客打折卡", "会员号", "请将会员卡或顾客打折卡从刷卡槽刷入", cardno, 0, 0, false, type)) { return null; }
			track2 = txt.Track2;
		}

		// 调用客户化会员磁道解析程序
		String tr = getTrackByCustom(track2);

		// 检查磁道是否和规则相匹配
		if (rulelist != null && rulelist.size() > 0)
		{
			rulelist = chkTrack(txt.Track1, track2, txt.Track3, rulelist, ispay);
		}

		// 如果匹配的规则有多个,再次让客户选择(B模式先刷卡后选择)
		if (rulelist != null && rulelist.size() > 1)
		{
			rulelist = chooseRule(rulelist, ispay);
		}

		// 解析有效规则下的磁道号
		if (rulelist != null && rulelist.size() > 0)
		{
			tr = getTrackByDefine(txt.Track1, track2, txt.Track3, rulelist);
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
			custtrack = new String[] { txt.Track1, (tr == null) ? track2 : tr, txt.Track3 };
		}

		return tr;
	}
}
