package custom.localize.Bjkl;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentMzk;

public class Bjkl_MzkInfoQueryBS extends MzkInfoQueryBS
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
			text = Language.apply("面值卡");
		}
		else
		{
			mzk = pay;
			if (text == null || text.equals(""))
				text = mzk.paymode.name;
		}

		// 刷面值卡
		TextBox txt = new TextBox();
		if (!txt.open(Language.apply("请刷") + text, text, Language.apply("请将{0}从刷卡槽刷入", new Object[] { text }), cardno, 0, 0, false, mzk.getAccountInputMode()))
			return;

		ProgressBox progress = null;

		try
		{
			progress = new ProgressBox();
			progress.setText(Language.apply("正在查询{0}信息，请等待.....", new Object[] { text }));

			// 得到磁道信息
			track1 = txt.Track1;
			track2 = txt.Track2;
			track3 = txt.Track3;

			// 再查询
			if (!mzk.findMzkInfo(track1, track2, track3))
				return;

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
			if (progress != null)
				progress.close();

			text = null;
		}
	}

	protected void mzkDisplayInfo(PaymentMzk mzk)
	{
		StringBuffer info = new StringBuffer();

		// 组织提示信息
		info.append(Language.apply("卡  号: ") + Convert.appendStringSize("", mzk.getDisplayCardno(), 1, 20, 20, 0) + "\n");
		info.append(Language.apply("持卡人: ") + Convert.appendStringSize("", mzk.mzkret.cardname, 1, 20, 20, 0) + "\n");
		info.append(Language.apply("卡状态: ") + Convert.appendStringSize("", mzk.mzkret.status, 1, 20, 20, 0) + "\n");
		info.append(Language.apply("面  值: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzk.mzkret.money), 1, 20, 20, 0) + "\n");
		info.append(Language.apply("余  额: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzk.mzkret.ye), 1, 20, 20, 0) + "\n");

		if (mzk.mzkret.str1 != null && mzk.mzkret.str1.trim().length() > 0)
		{
			info.append(Language.apply("有效期: ") + Convert.appendStringSize("", mzk.mzkret.str1, 1, 20, 20, 0) + "\n");
		}

		if (mzk.isRecycleType(mzk.mzkret.func))
		{
			info.append(Language.apply("工本费: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzk.mzkret.value3), 1, 20, 20, 0) + "\n");
			info.append(Language.apply("有效额: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzk.mzkret.ye - mzk.mzkret.value3), 1, 20, 20, 0) + "\n");
		}

		// 弹出显示
		new MessageBox(info.toString());
	}
}
