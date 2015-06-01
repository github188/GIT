package custom.localize.Tygc;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.PaymentCoupon;

public class Tygc_PaymentCoupon extends PaymentCoupon
{
	public boolean findFjk(String track1, String track2, String track3)
	{
		if ((track1.trim().length() <= 0) && (track2.trim().length() <= 0) && (track3.trim().length() <= 0)) { return false; }

		// 解析磁道
		String[] s = parseFjkTrack(track1, track2, track3);

		if (s == null) { return false; }

		track1 = s[0];
		track2 = s[1];
		track3 = s[2];
		Tygc_HykInfoQueryBS thq = new Tygc_HykInfoQueryBS();
		track2 = thq.getCardNo(track2);
		// 设置查询条件
		setRequestDataByFind(track1, track2, track3);
		
		// 查询时memo存放活动规则
		mzkreq.memo = fjkrulecode;

		if (mzkreq.invdjlb != null && SellType.ISBACK(mzkreq.invdjlb) && !saleBS.isRefundStatus())
		{
			if (GlobalInfo.sysPara.oldqpaydet != 'N' && track2.equals("0000"))
			{
				StringBuffer cardno = new StringBuffer();
				TextBox txt = new TextBox();
				if (!txt.open("请刷原小票里的会员卡或顾客打折卡", "会员号", "请将会员卡或顾客打折卡从刷卡槽刷入", cardno, 0, 0, false, getAccountInputMode())) { return false; }
				String tr = txt.Track2;

				String[] retinfo = NetService.getDefault().findoldqpaydet(salehead.ysyjh, salehead.yfphm, paymode.code, tr, "", "", "", "", "", GlobalInfo.localHttp);
				if (retinfo == null) { return false; }
				yyje = Convert.toDouble(retinfo[1]);
				sjje = Convert.toDouble(retinfo[0]);
			}

			// 传入原收银机号和原小票号
			mzkreq.track3 = saleBS.saleHead.ysyjh + "," + saleBS.saleHead.yfphm;
		}

		// 发送查询交易
		boolean done = sendMzkSale(mzkreq, mzkret);

		return done;
	}
}
