package custom.localize.Jcgj;

import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Payment.PaymentCoupon;
import com.efuture.javaPos.Payment.PaymentCouponEvent;

public class Jcgj_PaymentCoupon extends PaymentCoupon
{
	public void specialDeal(PaymentCouponEvent event)
	{
		try
		{
			ProgressBox pb = null;
			pb = new ProgressBox();
			pb.setText("正在刷卡,请等待...");
			event.msrRead(null, "", "", "");
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			event.shell.close();
			event.shell.dispose();
			event.shell = null;
		}
	}
	 
	public boolean findFjk(String track1, String track2, String track3)
	{
		return findFjk(track1, track2, track3, false);
	}
	
	public boolean findFjk(String track1, String track2, String track3, boolean isQureyFjk)
	{
		if (isQureyFjk)
		{
			return super.findFjk(track1, track2, track3);
		}
		else
		{
			Jcgj_Svc svc;
			svc = new Jcgj_Svc("svc_inq_vip", null, "");
			Jcgj_YsCardDef card = new Jcgj_YsCardDef();
			if (svc.doYsCard(card))
			{
				// 设置查询条件
				return super.findFjk(track1, card.cardNo, track3);
			}
			else return false;
		}
	}

	public String[] parseFjkTrack(String track1, String track2, String track3)
	{
		String[] s = new String[3];
		s[0] = track1;
		s[1] = track2;
		s[2] = track3;
		return s;
	}
	
	public int choicFjkType()
	{
		return -1;
	}
}
