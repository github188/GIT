package custom.localize.Jcgj;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentCustJfSale;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Jcgj_PaymentCustJfSale extends PaymentCustJfSale
{
	public Jcgj_PaymentCustJfSale()
	{
	}

	public Jcgj_PaymentCustJfSale(PayModeDef mode, SaleBS sale)
	{
		super.initPayment(mode, sale);
	}

	public Jcgj_PaymentCustJfSale(SalePayDef pay, SaleHeadDef head)
	{
		super.initPayment(pay, head);
	}

	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
		if (salepay.paycode.equals("0509"))
		{
			String cardSell = Convert.increaseChar(ConfigClass.Market + ConfigClass.CashRegisterCode, ' ', 10)
					+ Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 10)
					+ Convert.increaseChar(String.valueOf(salehead.fphm), ' ', 20)
					+ Convert.increaseChar(salehead.hykh, ' ', 19)
					+ Convert.increaseChar(String.valueOf((long) ManipulatePrecision.doubleConvert(req.je * 100, 2, 1)), ' ', 12)
					+ "1";

			Jcgj_Svc svc = new Jcgj_Svc("svc_use_score", null, cardSell);
			if (svc.doYsCard(null))
			{
				svc = new Jcgj_Svc("svc_commit", null, "");
				if (!svc.doYsCard(null))
				{
					new MessageBox(svc.getMethordName() + "积分消费记账失败");
					return false;
				}
				else return true;
			}
			else
			{
				new MessageBox(svc.getMethordName() + "积分消费记账失败");
				return false;
			}
		}
		else return super.sendMzkSale(req, ret);
	}
	
	public boolean writeMzkCz()
	{
		return true;
	}
}
