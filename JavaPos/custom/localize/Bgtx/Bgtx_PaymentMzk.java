package custom.localize.Bgtx;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Payment.PaymentMzk;

public class Bgtx_PaymentMzk extends PaymentMzk
{
	protected boolean getPasswdBeforeFindMzk(StringBuffer passwd)
	{
		if (GlobalInfo.sysPara.cardpasswd.equals("Y"))
		{
	    	TextBox txt = new TextBox();
	    	
	        if (!txt.open("请输入钢印号", "PASSWORD", "需要先输入卡钢印号以后才能查询卡资料", passwd, 0, 0,false, TextBox.AllInput))
	        {
	            return false;
	        }
		}
	    return true;
	}
}
