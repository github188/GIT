package custom.localize.Hmsl;

import bankpay.Payment.Shop_PaymentMzk;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Hmsl_PaymentMzk extends Shop_PaymentMzk
{
	public Hmsl_PaymentMzk()
	{
		super();
	}

	public Hmsl_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Hmsl_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		if(track2==null || track2.trim().equals(""))
		{
			new MessageBox("卡磁道数据不正确!");
			return false;
		}
		
		if (track2.indexOf("=") > -1) 
			return super.findMzkInfo(track1, track2, track3);

		String password = "";
		if (track2.length() == 19)
			password = track2.substring(track2.length() - 6);

		// 设置请求数据
		setRequestDataByFind(track1, track2, track3);
		if (!DataService.getDefault().getMzkInfo(mzkreq, mzkret)) return false;

		if (track2.length() == 19)
		{
			if (mzkret.cardpwd.equals(""))
			{
				new MessageBox("无法判断该卡有效性!");
				return false;
			}

			if (!mzkret.cardpwd.equals(password))
			{
				new MessageBox("卡校验失败,不允许交易!");
				return false;
			}
		}

		return true;
	}

	public boolean findMzk(String track1, String track2, String track3)
	{
		if(track2==null || track2.trim().equals(""))
		{
			new MessageBox("卡磁道数据不正确!");
			return false;
		}
		if (track2.indexOf("=") > -1) 
			return super.findMzk(track1, track2, track3);


		String password = "";
		if (track2.length() == 19) 
			password = track2.substring(track2.length() - 6);

		// 设置请求数据
		setRequestDataByFind(track1, track2, track3);
		if (!sendMzkSale(mzkreq, mzkret)) return false;

		if (mzkreq.type.equals("05"))
		{
			if (track2.length() == 19)
			{
				if (mzkret.cardpwd.equals(""))
				{
					new MessageBox("无法判断该卡有效性!");
					return false;
				}

				if (!mzkret.cardpwd.equals(password))
				{
					new MessageBox("卡校验失败,不允许交易!");
					return false;
				}
			}
		}

		return true;
	}
}
