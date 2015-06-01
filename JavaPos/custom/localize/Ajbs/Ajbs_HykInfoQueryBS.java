package custom.localize.Ajbs;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustomerDef;

public class Ajbs_HykInfoQueryBS extends HykInfoQueryBS
{
	public CustomerDef findMemberCard(String track2)
	{
		ProgressBox progress = null;
		CustomerDef cust = null;
		try
		{
			new MessageBox("请将卡片插入读卡器,并按任意键进行读卡...");
			
			progress = new ProgressBox();
			progress.setText("正在查询会员卡信息，请稍等.....");

			// 查找会员卡
			cust = new CustomerDef();
			if (!DataService.getDefault().getCustomer(cust, track2)) { return null; }
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

}
