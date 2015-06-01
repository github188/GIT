package bankpay.Payment;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;

public class Nxdz_PaymentMzk extends Ycsb_PaymentMzk
{
	protected boolean inputPass(StringBuffer passwd)
	{
		if (GlobalInfo.sysPara.defaultmzkpass.length() > 0 && passwd.toString().equals(GlobalInfo.sysPara.defaultmzkpass))
		{
			new MessageBox("不允许消费,请到服务台修改卡密码!");
			return false;
		}

		return true;
	}

}
