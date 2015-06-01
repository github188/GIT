package custom.localize.Jcgj;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentCustLczc;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;

public class Jcgj_PaymentCustLczc extends PaymentCustLczc
{
	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
		String cardSell = Convert.increaseChar(ConfigClass.Market + ConfigClass.CashRegisterCode, ' ', 10)
				+ Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 10)
				+ Convert.increaseChar(String.valueOf((long) ManipulatePrecision.doubleConvert(salepay.ybje * -100, 2, 1)), ' ', 12)
				+ Convert.increaseChar(salehead.hykh, ' ', 19)
				+ Convert.increaseChar(String.valueOf(salehead.fphm), ' ', 20);
		
		Jcgj_Svc svc = new Jcgj_Svc("svc_change", null, cardSell);
		if (svc.doYsCard(null))
		{
			svc = new Jcgj_Svc("svc_commit", null, "");
			if (!svc.doYsCard(null))
			{
				new MessageBox(svc.getMethordName() + "零钞转存记账失败");
				return false;
			}
			else return true;
		}
		else
		{
			new MessageBox(svc.getMethordName() + "零钞转存记账失败");
			return false;
		}
	}
	
	public boolean writeMzkCz()
	{
		return true;
	}
}
