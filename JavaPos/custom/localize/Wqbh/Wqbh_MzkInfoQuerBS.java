package custom.localize.Wqbh;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentMzk;

public class Wqbh_MzkInfoQuerBS extends MzkInfoQueryBS
{

	public void QueryMzkInfo(PaymentMzk pay)
	{
		StringBuffer cardno = new StringBuffer();
		String track1, track2, track3;

		// 创建面值卡付款对象
		
		PaymentMzk mzk = null;
		if (pay == null )
		{
			mzk = CreatePayment.getDefault().getPaymentMzk();
			text = "面值卡";
		}
		else
		{
			mzk = pay;
			if (text == null || text.equals(""))
			{
				if (mzk.paymode.name.equals("大会员支付"))
					text = "面值卡";
				else
					text = mzk.paymode.name;
			}
		}
		
		if (!mzk.allowMzkOffline() && !GlobalInfo.isOnline)
		{
			new MessageBox("此功能必须联网使用");
			return;
		}
		
		// 刷面值卡
		TextBox txt = new TextBox();
		if (!txt.open("请刷" + text, text, "请将" + text + "从刷卡槽刷入", cardno, 0, 0, false, mzk.getAccountInputMode())) { return; }

		ProgressBox progress = null;

		try
		{
			progress = new ProgressBox();
			progress.setText("正在查询" + text + "信息，请等待.....");

			// 得到磁道信息
			track1 = txt.Track1;
			track2 = txt.Track2;
			track3 = txt.Track3;

			// 先发送冲正
			if (!mzk.sendAccountCz()) return;

			// 再查询
			if (!mzk.findMzkInfo(track1, track2, track3)) { return; }

			// 在客显上显示面值卡号及余额
			// LineDisplay.getDefault().displayAt(0, 1,
			// mzk.getDisplayCardno());
			// LineDisplay.getDefault().displayAt(1, 1,
			// ManipulatePrecision.doubleToString(mzk.mzkret.ye));

			//
			progress.close();
			progress = null;

			// 显示卡信息
			mzkDisplayInfo(mzk);

		}
		catch (Exception er)
		{
			er.printStackTrace();
			new MessageBox(er.getMessage());
		}
		finally
		{
			if (progress != null) progress.close();
			
			text = null;
		}
	}
}
