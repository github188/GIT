package custom.localize.Tygc;

import java.util.ArrayList;

import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Logic.CouponQueryInfoBS;
import com.efuture.javaPos.Payment.CreatePayment;

public class Tygc_CouponQueryInfoBS extends CouponQueryInfoBS
{
	public boolean findHYK(String track1, String track2, String track3)
	{
		hyk = CustomLocalize.getDefault().createHykInfoQueryBS();
		
		String[] s = parseFjkTrack(track1, track2, track3);
		Tygc_HykInfoQueryBS thq = new Tygc_HykInfoQueryBS();
		String temp = s[1];
		s[1] = thq.getCardNo(s[1]);
		
		cust = hyk.findMemberCard(s[1]);

		if (cust == null) { return false; }

		coupon = CreatePayment.getDefault().getPaymentCoupon();

		if (coupon.findFjk(track1, temp, track3) && coupon.initList())
		{
			couponlist = coupon.couponList;
		}

		double zje = 0;
		if (couponlist != null)
		{
			for (int i = 0; i < couponlist.size(); i++)
			{
				String[] row = (String[]) couponlist.elementAt(i);
				zje += Convert.toDouble(row[2]);
			}
		}

		if (zje > 0)
		{
			ArrayList list = new ArrayList();

			if (coupon.findFjkInfo(track1, coupon.mzkret.cardno, track3, list))
			{
				if (list.size() > 0)
				{
					coupondetail = list;
				}
			}
		}

		return true;
	}
}
