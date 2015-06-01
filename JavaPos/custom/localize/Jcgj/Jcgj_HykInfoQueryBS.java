package custom.localize.Jcgj;

import java.util.Vector;

import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustFilterDef;

public class Jcgj_HykInfoQueryBS extends HykInfoQueryBS
{
	public String readMemberCard(boolean ispay)
	{
		selectedRule = null;

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

		if (ch && rulelist != null && rulelist.size() > 0)
		{
			CustFilterDef rule = (CustFilterDef) rulelist.elementAt(0);
			Jcgj_Svc.inputType = rule.InputType;
		}
		return "\n";
	}
}
