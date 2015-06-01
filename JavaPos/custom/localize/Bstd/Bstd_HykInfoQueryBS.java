package custom.localize.Bstd;

import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.BankTracker;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustomerDef;

public class Bstd_HykInfoQueryBS extends HykInfoQueryBS
{
	public int getMemberInputMode()
	{
		BankTracker.autoMSR();

		if (GlobalInfo.sysPara.hykinputmode == 2)
			return TextBox.MsrInput;
		if (GlobalInfo.sysPara.hykinputmode == 3)
			return TextBox.MsrKeyInput;

		return TextBox.MsrInput;
	}

	public CustomerDef findMemberCard(String track2)
	{
		CustomerDef cust = super.findMemberCard(track2);

		if (cust != null && GlobalInfo.sysPara.isusecoinbag == 'N')
		{
			// 控制手工输入
			if (GlobalInfo.sysPara.hykhandinputflag.indexOf("|" + track2.charAt(0) + "|") != -1)
				cust.isHandInput = true;
		}

		return cust;
	}
}
