package custom.localize.Jcgj;


import java.util.ArrayList;

import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Logic.CouponQueryInfoBS;
import com.efuture.javaPos.Struct.CustFilterDef;
import com.efuture.javaPos.UI.CouponQueryInfoEvent;

public class Jcgj_CouponQueryInfoBS extends CouponQueryInfoBS
{
	protected Jcgj_PaymentCoupon coupon1 = null;
	
	public void specialDeal(CouponQueryInfoEvent event)
	{
		event.msrRead(null, "", "", "");
	}
	
	  public boolean findHYK(String track1, String track2, String track3)
	  {
	    this.hyk = CustomLocalize.getDefault().createHykInfoQueryBS();

	    this.cust = this.hyk.findMemberCard(track2);

	    if (this.cust == null) return false;

	    this.coupon1 = new Jcgj_PaymentCoupon();

	    if ((this.coupon1.findFjk(track1, this.cust.track, track3, true)) && (this.coupon1.initList()))
	    {
	      this.couponlist = this.coupon1.couponList;
	    }

	    double zje = 0.0D;
	    if (this.couponlist != null)
	    {
	      for (int i = 0; i < this.couponlist.size(); ++i)
	      {
	        String[] row = (String[])this.couponlist.elementAt(i);
	        zje += Convert.toDouble(row[2]);
	      }
	    }

	    if (zje > 0.0D)
	    {
	      ArrayList list = new ArrayList();

	      if ((this.coupon1.findFjkInfo(track1, this.coupon1.mzkret.cardno, track3, list)) && 
	        (list.size() > 0))
	      {
	        this.coupondetail = list;
	      }
	    }

	    return true;
	  }
	
	public int choicFjkType()
	{
		int i = super.choicFjkType();
		if (rulelist != null && rulelist.size() > 0)
		{
			CustFilterDef rule = ((CustFilterDef) rulelist.elementAt(0));
			Jcgj_Svc.inputType = rule.InputType;
		}
		return i;
	}
}
