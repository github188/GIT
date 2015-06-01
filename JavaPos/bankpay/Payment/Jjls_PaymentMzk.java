package bankpay.Payment;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.ICCard;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Jjls_PaymentMzk extends PaymentMzk
{
	public Jjls_PaymentMzk()
	{
		super();
	}

	public Jjls_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Jjls_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		track2 = ICCard.getDefault().getICCardNo();

		if (track2 == null || track2.trim().length() <= 0)
		{
			new MessageBox("没有读到IC卡信息!");
			return false;
		}

		track1 = "CARDYE";
		track3 = String.valueOf(ICCard.getDefault().getICCardMoney());

		return super.findMzkInfo(track1, track2, track3);
	}

	public boolean findMzk(String track1, String track2, String track3)
	{
		track2 = ICCard.getDefault().getICCardNo();

		if (track2 == null || track2.trim().length() <= 0)
		{
			new MessageBox("没有读到IC卡信息!");
			return false;
		}

		track1 = "CARDYE";
		track3 = String.valueOf(ICCard.getDefault().getICCardMoney());

		return super.findMzk(track1, track2, track3);
	}

	public boolean updateICKData()
	{
		String payno = ICCard.getDefault().getICCardNo();

		if (salepay != null && salepay.payno != null && !payno.equals(salepay.payno))
		{
			new MessageBox("当前卡号不符");
			return false;
		}

		// System.err.println("write:" + payno + ";" + salepay.je);

		if (!ICCard.getDefault().updateCardMoney(payno, "UPDATE", ICCard.getDefault().getICCardMoney() - salepay.je))
		{
			new MessageBox("IC卡更新余额失败!");
			return false;
		}

		return true;
	}

	public boolean realAccountPay()
	{
		if (ICCard.getDefault().getICCardMoney() > mzkret.ye)
		{
			new MessageBox("卡中余额大于网上余额,不允许交易");
			return false;
		}

		// 付款即时记账
		if (mzkAccount(true))
		{
			deleteMzkCz();

			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean collectAccountPay()
	{
		// 实时记账,直接返回
		return true;
	}

	public boolean cancelPay()
	{
		// 余额即时写入IC卡,总是立即记账
		if (mzkAccount(false))
		{
			deleteMzkCz();

			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean mzkAccount(boolean isAccount)
	{
		if (!updateICKData()) { return false; }

		if (super.mzkAccount(isAccount))
		{

			return true;
		}
		else
		{
			return false;
		}
	}
}
