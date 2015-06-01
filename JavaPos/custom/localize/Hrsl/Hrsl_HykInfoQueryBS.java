package custom.localize.Hrsl;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Struct.CustomerDef;

import custom.localize.Bstd.Bstd_HykInfoQueryBS;

public class Hrsl_HykInfoQueryBS extends Bstd_HykInfoQueryBS
{
	protected void hykDisplayInfo(CustomerDef cust)
	{
		StringBuffer info = new StringBuffer();

		info.append("卡    号: " + Convert.appendStringSize("", cust.code, 1, 16, 16, 0) + "\n");
		info.append("持 卡 人: " + Convert.appendStringSize("", cust.name, 1, 16, 16, 0) + "\n");
		info.append("卡 状 态: " + Convert.appendStringSize("", cust.status, 1, 16, 16, 0) + "\n");
		info.append("卡 类 型: " + Convert.appendStringSize("", cust.str1, 1, 16, 16, 0) + "\n");
		info.append("有 效 期: " + Convert.appendStringSize("", cust.maxdate, 1, 16, 16, 0) + "\n");

		// info.append("会员类型: " + Convert.appendStringSize("", cust.str1, 1, 16,
		// 16, 0) + "\n");

		info.append("积分功能: " + Convert.appendStringSize("", getFuncText(cust.isjf), 1, 16, 16, 0) + "\n");
		info.append("储值功能: " + Convert.appendStringSize("", getFuncText(cust.str2.charAt(0)), 1, 16, 16, 0) + "\n");
		info.append("券 功 能: " + Convert.appendStringSize("", getFuncText(cust.str3.charAt(0)), 1, 16, 16, 0) + "\n");

		info.append("可用积分: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.num1), 1, 16, 16, 0) + "\n");
		info.append("储值余额: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.num2), 1, 16, 16, 0) + "\n");
		info.append("券 余 额: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.num3), 1, 16, 16, 0) + "\n");

		new MessageBox(info.toString());
	}

}
