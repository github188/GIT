package custom.localize.Nnmk;

import java.util.ArrayList;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Logic.CouponQueryInfoBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Struct.MzkResultDef;

public class Nnmk_CouponQueryInfoBS extends CouponQueryInfoBS
{
	public static MzkResultDef mzkretZQ  = new MzkResultDef();
	public Vector getCouponValue(){
		return couponlist;
	}
	
	public ArrayList getCouponValue1(){
		return coupondetail;
	}
	
	//纸券查询时 不查询会员信息
	public boolean findHYKZJ(String track1, String track2, String track3)
	{
		hyk = CustomLocalize.getDefault().createHykInfoQueryBS();
		String[] s = parseFjkTrack(track1, track2, track3);
		//cust = hyk.findMemberCard(s[1]);

		//if (cust == null) { return false; }

		coupon = CreatePayment.getDefault().getPaymentCoupon();

		if (coupon.findFjk(track1, s[1], track3) && coupon.initList())
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
		}else{
			new MessageBox("该券余额为0!");
		}
		mzkretZQ = coupon.mzkret;
		return true;
	}
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
		}else{
			new MessageBox("该券余额为0!");
		}

		return true;
	}
}
