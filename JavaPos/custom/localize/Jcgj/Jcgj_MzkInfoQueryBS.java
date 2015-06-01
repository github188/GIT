package custom.localize.Jcgj;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentMzk;

public class Jcgj_MzkInfoQueryBS extends MzkInfoQueryBS
{
	public void QueryMzkInfo(PaymentMzk pay)
	{
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
			if (text == null || text.equals("")) text = mzk.paymode.name;
		}
		
		if (!mzk.allowMzkOffline() && !GlobalInfo.isOnline)
		{
			new MessageBox("此功能必须联网使用");
			return;
		}
		
		try
		{
			// 再查询
			if (!mzk.findMzkInfo("", "", "")) { return; }
			// 显示卡信息
			mzkDisplayInfo(mzk);
		}
		catch (Exception er)
		{
			er.printStackTrace();
			new MessageBox(er.getMessage());
		}
	}
	
	protected void mzkDisplayInfo(PaymentMzk mzk)
	{
		StringBuffer info = new StringBuffer();

		// 组织提示信息
		info.append("卡  号: " + Convert.appendStringSize("", mzk.getDisplayCardno(), 1, 20, 20, 0) + "\n");
		info.append("持卡人: " + Convert.appendStringSize("", mzk.mzkret.cardname, 1, 20, 20, 0) + "\n");
		info.append("卡状态: " + Convert.appendStringSize("", mzk.mzkret.status, 1, 20, 20, 0) + "\n");
		info.append("余  额: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzk.mzkret.ye), 1, 20, 20, 0) + "\n");

		if (mzk.mzkret.str1 != null && mzk.mzkret.str1.trim().length() > 0)
		{
			info.append("有效期: " + Convert.appendStringSize("", mzk.mzkret.str1, 1, 20, 20, 0) + "\n");
		}

		if (mzk.isRecycleType(mzk.mzkret.func))
		{
			info.append("工本费: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzk.mzkret.value3), 1, 20, 20, 0) + "\n");
			info.append("有效额: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzk.mzkret.ye - mzk.mzkret.value3), 1, 20, 20, 0)
					+ "\n");
		}

		// 弹出显示
		new MessageBox(info.toString());
	}
}
