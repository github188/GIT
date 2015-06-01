package custom.localize.Bgtx;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustomerDef;

public class Bgtx_HykInfoQueryBS  extends HykInfoQueryBS
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
		info.append("有 效 期: " + Convert.appendStringSize("", cust.maxdate, 1, 16, 16, 0) + "\n");
		if (isLczcFunc(cust))
		{
			info.append("零钞转存: " + Convert.appendStringSize("", getFuncText('Y'), 1, 16, 16, 0) + "\n");
			info.append("零钞余额: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.value1), 1, 16, 16, 0) + "\n");
			info.append("零钞上限: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.value2), 1, 16, 16, 0) + "\n");
		}
	}
	//手输入和刷卡都支持
	public int getMemberInputMode()
	{
		return TextBox.MsrKeyInput;
	}
	
	public void QueryHykInfo()
	{
		// 读会员卡

		String track2 = readMemberCard();
		if (track2 == null || track2.equals(""))
			return;
		
		if(track2.startsWith(";"))
		{
			track2 = track2.substring(1);
		}
		if(track2.endsWith("?"))
		{
			track2 = track2.substring(0,track2.length()-1);
		}

		// 查找会员卡
		CustomerDef cust = findMemberCard(track2);

		if (cust == null)
			return;

		// 在客显上显示卡号及余额
		// LineDisplay.getDefault().clearText();
		// LineDisplay.getDefault().displayAt(0, 1, cust.code);
		// LineDisplay.getDefault().displayAt(1, 1,
		// ManipulatePrecision.doubleToString(cust.valuememo));

		// 显示卡信息
		hykDisplayInfo(cust);
	}
}
