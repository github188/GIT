package custom.localize.Ytbh;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;

import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustomerDef;

public class Ytbh_HykInfoQueryBS extends HykInfoQueryBS {
	public CustomerDef findMemberCard(String track2) {
		ProgressBox progress = null;
		CustomerDef cust = null;
		try {
			progress = new ProgressBox();
			progress.setText(Language.apply("正在查询会员卡信息，请等待....."));

			// 查找会员卡
			cust = new CustomerDef();
			if (!DataService.getDefault().getCustomer(cust, track2)) {return null;}
			if (cust.code == null || cust.code.trim().equals("")) {
				//new MessageBox(Language.apply("查询的会员卡信息无效!\n请找后台人员"));
				return null;
			}
		} finally {
			if (progress != null)
				progress.close();
		}

		return cust;
	}
	
	public void hykDisplayInfo(CustomerDef cust)
	{
		StringBuffer info = new StringBuffer();

		getHykDisplayInfo(cust, info);

		new MessageBox(info.toString());

		// 查询卡上的返券资料
		if (GlobalInfo.sysPara.findcustfjk == 'Y')
		{
			if (new MessageBox(Language.apply("是否立即查询卡中电子券的余额?"), null, true).verify() == GlobalVar.Key1)
			{
				CustomLocalize.getDefault().createFjkInfoQueryBS().QueryFjkInfo(custtrack);
			}
		}
	}
}
