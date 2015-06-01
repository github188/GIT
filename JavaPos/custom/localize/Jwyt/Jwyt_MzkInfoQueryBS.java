package custom.localize.Jwyt;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentMzk;

public class Jwyt_MzkInfoQueryBS extends MzkInfoQueryBS
{
	public void QueryMzkInfo(PaymentMzk pay)
	{
		StringBuffer cardno = new StringBuffer();
		String track1, track2, track3;

		// 创建面值卡付款对象

		PaymentMzk mzk = null;
		if (pay == null)
		{
			mzk = CreatePayment.getDefault().getPaymentMzk();
			text = "面值卡";
		}
		else
		{
			mzk = pay;
			if (text == null || text.equals(""))
				text = mzk.paymode.name;
		}

		if (!mzk.allowMzkOffline() && !GlobalInfo.isOnline)
		{
			new MessageBox("面值卡查询必须联网使用");
			return;
		}

		// 刷面值卡
		TextBox txt = new TextBox();
		ProgressBox progress = null;

		try
		{
			do
			{
				if (!txt.open("请刷" + text, text, "请将" + text + "从刷卡槽刷入", cardno, 0, 0, false, mzk.getAccountInputMode()))
				{
					if (progress != null)
						progress.close();
					progress = null;
					break;
				}				
				progress = new ProgressBox();
				progress.setText("正在查询" + text + "信息，请等待.....");

				// 得到磁道信息
				track1 = txt.Track1;
				track2 = txt.Track2;
				track3 = txt.Track3;
				// 先发送冲正
				if (!mzk.sendAccountCz())
				{
					new MessageBox("冲正失败\n请转移c:\\javapos\\javapos.database中以.cz结尾的文件后重试");
					progress.close();
					progress = null;
					break;
				}

				// 再查询
				if (!mzk.findMzkInfo(track1, track2, track3))
				{
					progress.close();
					progress = null;
					continue;
				}
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

			} while (true);

		}
		catch (Exception er)
		{
			er.printStackTrace();
			new MessageBox(er.getMessage());
		}
		finally
		{
			if (progress != null)
				progress.close();

			text = null;
		}
	}
}
