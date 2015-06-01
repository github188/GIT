package custom.localize.Hycs;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.CustomerDef;

import custom.localize.Bstd.Bstd_HykInfoQueryBS;

public class Hycs_HykInfoQueryBS extends Bstd_HykInfoQueryBS
{
	protected void hykDisplayInfo(CustomerDef cust)
	{
		StringBuffer info = new StringBuffer();
		
		info.append(Language.apply("卡    号: ") + Convert.appendStringSize("", cust.code, 1, 16, 16, 0) + "\n");
		info.append(Language.apply("持 卡 人: ") + Convert.appendStringSize("", cust.name, 1, 16, 16, 0) + "\n");
		info.append(Language.apply("卡 状 态: ") + Convert.appendStringSize("", cust.status, 1, 16, 16, 0) + "\n");
		info.append(Language.apply("卡 级 别: ") + Convert.appendStringSize("", cust.type, 1, 16, 16, 0) + "\n");
		info.append(Language.apply("卡 积 分: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.valuememo), 1, 16, 16, 0) + "\n");
		info.append(Language.apply("会员功能: ") + Convert.appendStringSize("", getFuncText(cust.ishy), 1, 16, 16, 0) + "\n");
		info.append(Language.apply("积分功能: ") + Convert.appendStringSize("", getFuncText(cust.isjf), 1, 16, 16, 0) + "\n");
		info.append(Language.apply("折扣功能: ") + Convert.appendStringSize("", getFuncText(cust.iszk), 1, 16, 16, 0) + "\n");
		
//		info.append("会员类型: " + Convert.appendStringSize("", cust.str1, 1, 16, 16, 0) + "\n");
//		
//		info.append("储值功能: " + Convert.appendStringSize("", getFuncText(cust.str2.charAt(0)), 1, 16, 16, 0) + "\n");
//		info.append("券 功 能: " + Convert.appendStringSize("", getFuncText(cust.str3.charAt(0)), 1, 16, 16, 0) + "\n");
//		
//		info.append("可用积分: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.num1), 1, 16, 16, 0) + "\n");
//		info.append("储值余额: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.num2), 1, 16, 16, 0) + "\n");
//		info.append("券 余 额: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.num3), 1, 16, 16, 0) + "\n");

		new MessageBox(info.toString());
	}
	
}
