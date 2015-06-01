package custom.localize.Jnyz;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.BankLogQueryBS;
import com.efuture.javaPos.UI.BankLogQueryEvent;

public class Jnyz_BankLogQueryBS extends BankLogQueryBS {
	
	public void printAgainBankCardInfo(String rowcode,BankLogQueryEvent  bcqe)
	{
		if (GlobalInfo.posLogin.type == '1')
		{
			new MessageBox(" 收银员不允许重打印！");
			return;
		}
		
		super.printAgainBankCardInfo(rowcode, bcqe);
	}

}
