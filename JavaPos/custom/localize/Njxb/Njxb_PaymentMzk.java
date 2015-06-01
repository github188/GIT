package custom.localize.Njxb;

import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Payment.PaymentMzkEvent;

public class Njxb_PaymentMzk extends PaymentMzk
{
	public boolean findMzk(String track1, String track2, String track3)
	{
		GecrmFunc gecrm;
		if (paymode.code.equals("0401"))
		{
			gecrm = new GecrmFunc(GecrmFunc.CHECKOLD, salehead, null);
		}
		else if (paymode.code.equals("0402"))
		{
			gecrm = new GecrmFunc(GecrmFunc.CHECKNEW, salehead, null);
		}
		else
		{
			return super.findMzk(track1, track2, track3);
		}

		GecrmCard card = new GecrmCard();
		if (gecrm.doGecrm(card))
		{
			mzkret.cardno = card.card_no; // 卡号
			mzkret.ye = card.balance; // 余额
			mzkret.money = card.make_sum; // 面值
			mzkret.str1 = card.valid_date; // 有效期
			return true;
		}
		else
		{
			return false;
		}
	}

	public void specialDeal(PaymentMzkEvent event)
	{
		ProgressBox pb = null;
		try
		{
			pb = new ProgressBox();
			pb.setText("正在刷卡,请等待...");
			event.msrRead(null, "", "", "");
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			event.shell.close();
			event.shell.dispose();
			event.shell = null;
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}
	}

	public void doAfterFail(PaymentMzkEvent mzkEvent)
	{
		super.doAfterFail(mzkEvent);
		NewKeyListener.sendKey(GlobalVar.Exit);
	}

	protected boolean saveFindMzkResultToSalePay()
	{
		salepay.str2 = mzkret.str1;
		salepay.batch = "";
		salepay.payno = mzkret.cardno;
		salepay.kye = mzkret.ye;

		return true;
	}

	public boolean AutoCalcMoney()
	{
		return true;
	}

	public boolean writeMzkCz()
	{
		return true;
	}

	public boolean sendAccountCz()
	{
		return new GecrmFunc().sendRush();
	}

	public boolean deleteMzkCz(String fname)
	{
		return new GecrmFunc().deleteRushFile();
	}
	
	public boolean createSalePay(String money)
	{
		double ye = saleBS.calcPayBalance();
		String money1 = String.valueOf(Math.min(mzkret.ye,ye));
		return super.createSalePay(money1);
	}
}
