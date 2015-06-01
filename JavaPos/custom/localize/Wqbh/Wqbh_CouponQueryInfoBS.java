package custom.localize.Wqbh;

import java.util.ArrayList;

import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Logic.CouponQueryInfoBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Struct.CustFilterDef;
import com.efuture.javaPos.UI.CouponQueryInfoEvent;

import device.ICCard.KTL512VWQ;

public class Wqbh_CouponQueryInfoBS extends CouponQueryInfoBS
{
	public boolean findHYK(String track1, String track2, String track3)
	{
		hyk = CustomLocalize.getDefault().createHykInfoQueryBS();
		String[] s = parseFjkTrack(track1, track2, track3);
		cust = hyk.findMemberCard(s[1]);

		if (cust == null) { return false; }
		coupon = CreatePayment.getDefault().getPaymentCoupon();

		if (coupon.findFjk(track1, s[1], track3) && coupon.initList())
		{
			couponlist = coupon.couponList;
		}

		ArrayList list = new ArrayList();

		if (coupon.findFjkInfo(track1, coupon.mzkret.cardno, track3, list))
		{
			if (list.size() > 0)
			{
				coupondetail = list;
			}
		}
		return true;
	}
	
	public void specialDeal(CouponQueryInfoEvent event)
	{
		try 
		{
			if (rulelist != null && rulelist.size() == 1)
			{
				CustFilterDef rule = ((CustFilterDef) rulelist.elementAt(0));
				if (rule.InputType == 3)
				{
					String track2 = "";
					ProgressBox pb = null;
					pb = new ProgressBox();
					pb.setText("正在输入卡号和密码,请等待...");
					track2 = new KTL512VWQ().findCard();
					if (pb != null)
					{
						pb.close();
						pb = null;
					}
					
					event.msrRead(null, "", track2, "");
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			event.shell.close();
			event.shell.dispose();
		}

	}
	
}
