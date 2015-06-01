package bankpay.Payment;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.ICCard;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class ICKSyn_PaymentMzk extends PaymentMzk
{

	public ICKSyn_PaymentMzk()
	{
		super();
	}

	public ICKSyn_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public ICKSyn_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		String cardno = ICCard.getDefault().getICCardNo();
		if (cardno == null || cardno.trim().length() <= 0)
		{
			new MessageBox("没有读到IC卡信息!");
			return false;
		}

		// 余额返回为0时，提示人工处理,之后若余额为0时或<2时将第100位直接更新成1
		if ((ICCard.getDefault().getICCardMoney() > 0 && ICCard.getDefault().getICCardMoney() <= 2) || ICCard.getDefault().getICCardMoney() == 0)
		{
			// new MessageBox("该卡余额为0,请转人工处理");
			if (updateICKData())
			{
				super.findMzkInfo(track1, track2, track3);
				return true;
			}
			return false;
		}
		else if (ICCard.getDefault().getICCardMoney() > 2)
		{
			// 余额大于0时，需要同步金额
			return this.findMzk(track1, track2, track3);
		}
		else
		{
			return super.findMzkInfo(track1, track2, track3);
		}
	}

	public boolean findMzk(String track1, String track2, String track3)
	{
		String cardno = ICCard.getDefault().getICCardNo();

		if (cardno == null || cardno.trim().length() <= 0)
		{
			new MessageBox("没有读到IC卡信息!");
			return false;
		}

		if (!track2.equals(cardno))
		{
			new MessageBox("传入卡号和读出卡号不匹配,请重新刷卡!");
			return false;
		}

		if ((ICCard.getDefault().getICCardMoney() > 0 && ICCard.getDefault().getICCardMoney() <= 2) || ICCard.getDefault().getICCardMoney() == 0)
		{
			if (updateICKData())
			{
				super.findMzk(track1, track2, track3);
				return true;
			}
			return false;
		}
		else
		{
			// setRequestDataByFind设置需同步金额
			boolean bool = super.findMzk(track1, track2, track3);

			// 数据库同步成功,将ICK余额更新为-1,下次无须再次同步
			if (ICCard.getDefault().getICCardMoney() > 2 && mzkreq.track1.equals("CARDYE"))
			{
				// 清空同步标识数据
				mzkreq.track1 = "";
				mzkreq.track3 = "";

				// 更新ICK
				if (bool && !updateICKData())
					return false;
			}

			return bool;
		}
	}

	public void setRequestDataByFind(String track1, String track2, String track3)
	{
		super.setRequestDataByFind(track1, track2, track3);

		mzkreq.track2 = ICCard.getDefault().getICCardNo();

		// 告诉后台查询过程磁道信息是存放的是卡号和卡余额,同步数据库余额主档
		if (ICCard.getDefault().getICCardMoney() > 2)
		{
			mzkreq.track1 = "CARDYE";

			mzkreq.track3 = ManipulatePrecision.doubleToString(ICCard.getDefault().getICCardMoney());
		}
	}

	public boolean updateICKData()
	{
		if (ICCard.getDefault() == null)
		{
			new MessageBox("没有定义IC卡读卡设备,不能更新IC卡余额");
			return false;
		}

		String payno = ICCard.getDefault().getICCardNo();

		if (salepay != null)
			payno = salepay.payno;

		if (!ICCard.getDefault().updateCardMoney(payno, "UPDATE", -1))
		{
			new MessageBox("IC卡写卡失败!");
			return false;
		}

		return true;
	}

}
