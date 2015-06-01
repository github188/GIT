package custom.localize.Bjkl;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustomerDef;

public class Bjkl_HykInfoQueryBS extends HykInfoQueryBS
{
	//京客隆卡支持 刷卡，手输入手机号，卡号，因此是是刷卡，输入模式
	public int getMemberInputMode()
	{
		return TextBox.MsrKeyInput;
	}
	
	public CustomerDef findMemberCard(String track2)
	{

		CustomerDef cust = null;

		// 查找会员卡
		cust = CardModule.getDefault().getCustomer(track2);
		if (null == cust || cust.code == null || cust.code.trim().equals(""))
			return null;

		return cust;
	}

	protected void getHykDisplayInfo(CustomerDef cust, StringBuffer info)
	{
		String msg = "";
		if ("0".equals(cust.valstr10))
		{
			msg = "询问状态";
		}
		else if ("1".equals(cust.valstr10))
		{
			msg = "1 元以下零存";
		}
		else if ("2".equals(cust.valstr10))
		{
			msg = "10 元以下零存";
		}
		else if ("4".equals(cust.valstr10))
		{
			msg = "不启用零钱包";
		}
		info.append(Language.apply("卡    号: ") + Convert.appendStringSize("", cust.code, 1, 17, 17, 0) + "\n");
		info.append(Language.apply("卡 余 额: ") + Convert.appendStringSize("", cust.value1 + "", 1, 17, 17, 0) + "\n");
		info.append(Language.apply("卡 积 分: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.valuememo), 1, 17, 17, 0) + "\n");
		info.append(Language.apply("折扣余额: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.valnum1), 1, 17, 17, 0) + "\n");
		info.append(Language.apply("零 钱 包: ") + Convert.appendStringSize("", msg, 1, 17, 17, 0) + "\n");
		info.append(Language.apply("有 效 期: ") + Convert.appendStringSize("", cust.maxdate, 1, 17, 17, 0) + "\n");
	}
}
