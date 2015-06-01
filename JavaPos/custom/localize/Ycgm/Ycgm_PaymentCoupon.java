package custom.localize.Ycgm;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.PaymentCoupon;

public class Ycgm_PaymentCoupon extends PaymentCoupon 
{

	//根据轨道信息，解析出卡号
	public String getCardnoFromTrack(String track)
	{
		String no = track;
		
		//轨道规则：  ;3453253535235=525345? ,在 “；”与“=”之间的为卡号
		if (!track.startsWith(";") && track.indexOf('=') > -1)
		{
			no = track.substring(0,track.indexOf('='));
		}
		else if (track.startsWith(";") && track.indexOf('=') > -1)
		{
			no = track.substring(1,track.indexOf('='));
		}
		
		return no;
	}
	
	//宜昌国贸要求使用电子券时，先到同程CRM会员系统中验证卡号是否有效
	public boolean findFjk(String track1, String track2, String track3)
	{
		if ((track1.trim().length() <= 0) && (track2.trim().length() <= 0) && (track3.trim().length() <= 0)) { return false; }

		// 解析磁道
		String[] s = parseFjkTrack(track1, track2, track3);

		if (s == null) { return false; }

		track1 = s[0];
		track2 = s[1];
		track3 = s[2];
		
		//到同程CRM中查询会员是否有效
		if ("Y".equals(Excute.validate))
		{
			String rs = Excute.queryJfOrCzInfo(track2, " ", "");
			if (null == rs || "".equals(rs))
			{
				new MessageBox("会员卡信息无效");
				return false;
			}
		}	

		if ("Y".equals(Excute.deal))
			track2 = getCardnoFromTrack(track2);
		
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
				if (!txt.open(Language.apply("请刷原小票里的会员卡或顾客打折卡"), Language.apply("会员号"), Language.apply("请将会员卡或顾客打折卡从刷卡槽刷入"), cardno, 0, 0, false, getAccountInputMode())) { return false; }
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
