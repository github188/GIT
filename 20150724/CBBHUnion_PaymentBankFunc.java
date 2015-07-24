package bankpay.Bank;

import com.efuture.javaPos.Payment.PaymentBank;

public class CBBHUnion_PaymentBankFunc extends CBBHWallet_PaymentBankFunc {
	
	public String getTrans(int type)
	{
		String trans = null;
		
		switch(type)
		{
			case PaymentBank.XYKXF://消费
				trans = "76"  ;
				break;
			case PaymentBank.XYKQD://签到
				trans = "85"  ;
				break;
			case PaymentBank.XYKJZ://结账
				trans = "86"  ;
				break;
			case PaymentBank.XKQT1://重打结算单
				trans = "87"  ;
				break;
			case PaymentBank.XYKCD://重打印
				trans = "84"  ;
				break;
		}
		
		return trans;
	}

}
