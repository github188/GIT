package custom.localize.Ycgm;

import org.eclipse.swt.custom.StyledText;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.CouponQueryInfoBS;

public class Ycgm_CouponQueryInfoBS extends CouponQueryInfoBS
{

	public void displayBaseInfo(StyledText text)
	{
		StringBuffer info = new StringBuffer();
		info.append(Language.apply("卡    号: ")+ Convert.appendStringSize("", cust.code, 1, 16, 16, 0) + "\n");
		info.append(Language.apply("持 卡 人: ") + Convert.appendStringSize("", cust.name, 1, 16, 16, 0) + "\n");

		if (isLczcFunc(cust))
		{
			info.append(Language.apply("零钞转存: ") + Convert.appendStringSize("", getFuncText('Y'), 1, 16, 16, 0) + "\n");
			info.append(Language.apply("零钞余额: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.value1), 1, 16, 16, 0) + "\n");
			info.append(Language.apply("零钞上限: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.value2), 1, 16, 16, 0) + "\n");
		}

		text.setText(info.toString());
	}
}
