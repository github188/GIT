package custom.localize.Shhl;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.CustomerDef;

import custom.localize.Bstd.Bstd_HykInfoQueryBS;

public class Shhl_HykInfoQueryBS extends Bstd_HykInfoQueryBS
{

	public CustomerDef findMemberCard(String track2)
	{
		CustomerDef cust = super.findMemberCard(track2);

		if (cust == null)
			return null;

		if (cust.valstr1 != null && !cust.valstr1.equals(""))
		{
			String now = new ManipulateDateTime().getDateBySign();
			if (new ManipulateDateTime().compareDate(cust.valstr1, now) == 0)
			{
				new MessageBox("生日提醒\n\n今天是会员【" + cust.code + "】的生日!");
			}
		}
		return cust;
	}

	protected void getHykDisplayInfo(CustomerDef cust, StringBuffer info)
	{
		info.append(Language.apply("卡    号: ") + Convert.appendStringSize("", cust.code, 1, 20, 20, 0) + "\n");
		info.append(Language.apply("持 卡 人: ") + Convert.appendStringSize("", cust.name, 1, 20, 20, 0) + "\n");
		info.append(Language.apply("卡 状 态: ") + Convert.appendStringSize("", cust.status, 1, 20, 20, 0) + "\n");
		info.append(Language.apply("卡 积 分: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.valuememo), 1, 20, 20, 0) + "\n");
		info.append(Language.apply("会员功能: ") + Convert.appendStringSize("", getFuncText(cust.ishy), 1, 20, 20, 0) + "\n");
		info.append(Language.apply("积分功能: ") + Convert.appendStringSize("", getFuncText(cust.isjf), 1, 20, 20, 0) + "\n");
		info.append(Language.apply("折扣功能: ") + Convert.appendStringSize("", getFuncText(cust.iszk), 1, 20, 20, 0) + "\n");
		if (isLczcFunc(cust))
		{
			info.append(Language.apply("零钞转存: ") + Convert.appendStringSize("", getFuncText('Y'), 1, 20, 20, 0) + "\n");
			info.append(Language.apply("零钞余额: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.value1), 1, 20, 20, 0) + "\n");
			info.append(Language.apply("零钞上限: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.value2), 1, 20, 20, 0) + "\n");
		}
	}
}
