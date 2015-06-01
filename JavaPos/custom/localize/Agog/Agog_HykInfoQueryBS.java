package custom.localize.Agog;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustomerDef;

public class Agog_HykInfoQueryBS extends HykInfoQueryBS
{
	public CustomerDef findMemberCard(String track2)
	{
		ProgressBox progress = null;
		CustomerDef cust = null;
		try
		{
			progress = new ProgressBox();
			progress.setText("正在查询会员卡信息，请稍等.....");

			// 查找会员卡
			cust = new CustomerDef();

			if (!Agog_VipCaller.getDefault().queryVip(cust, track2,true))
				return null;

			if (cust.code == null || cust.code.trim().equals(""))
			{
				new MessageBox("查询的会员卡信息无效!");
				return null;
			}
		}
		finally
		{
			if (progress != null)
				progress.close();
		}

		return cust;
	}

	protected void hykDisplayInfo(CustomerDef cust)
	{
		StringBuffer info = new StringBuffer();

		info.append("卡    号: " + Convert.appendStringSize("", cust.code, 1, 16, 16, 0) + "\n");
		info.append("持 卡 人: " + Convert.appendStringSize("", cust.name, 1, 16, 16, 0) + "\n");
		info.append("持 类 型: " + Convert.appendStringSize("", cust.type, 1, 16, 16, 0) + "\n");
		info.append("卡 状 态: " + Convert.appendStringSize("", cust.status, 1, 16, 16, 0) + "\n");
		info.append("卡 积 分: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.valuememo), 1, 16, 16, 0) + "\n");
		info.append("卡 余 额: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.num1), 1, 16, 16, 0) + "\n");
		info.append("有 效 期: " + Convert.appendStringSize("", cust.maxdate, 1, 16, 16, 0) + "\n");

		new MessageBox(info.toString());
	}
}
