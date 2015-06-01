package bankpay.Payment;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;


public class LNBE_PaymentMzk extends Shop_PaymentMzk
{
	public LNBE_PaymentMzk()
	{
		super();
	}

	public LNBE_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public LNBE_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	protected boolean getPasswdBeforeFindMzk(StringBuffer passwd)
	{
		TextBox txt = new TextBox();

		if (!txt.open("请输入密码", "PASSWORD", "请输入卡密码,没有密码请直接按回车键!", passwd, 0, 0, false, TextBox.AllInput))
			return false;

		return true;
	}

	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		if ((track1 == null || track1.trim().length() <= 0) && (track2 == null || track2.trim().length() <= 0) && (track3 == null || track3.trim().length() <= 0))
		{
			new MessageBox("磁道数据为空!");
			return false;
		}

		// 解析磁道
		String[] s = parseTrack(track1, track2, track3);
		if (s == null)
			return false;
		track1 = s[0];
		track2 = s[1];
		track3 = s[2];

		// 设置请求数据
		setRequestDataByFind(track1, track2, track3);
		if (!DataService.getDefault().getMzkInfo(mzkreq, mzkret))
			return false;

		if (!mzkret.str3.equals(""))
		{
			StringBuffer passwd = new StringBuffer();
			if (!getPasswdBeforeFindMzk(passwd))
				return false;

			if (!mzkret.str3.equals(passwd.toString()))
				return false;
		}
		
		return true;
	}

	public boolean findMzk(String track1, String track2, String track3)
	{
		if ((track1 == null || track1.trim().length() <= 0) && (track2 == null || track2.trim().length() <= 0) && (track3 == null || track3.trim().length() <= 0))
		{
			new MessageBox("磁道数据为空!");
			return false;
		}

		// 解析磁道
		String[] s = parseTrack(track1, track2, track3);
		if (s == null)
			return false;
		track1 = s[0];
		track2 = s[1];
		track3 = s[2];

		// 设置请求数据
		setRequestDataByFind(track1, track2, track3);
		if (!sendMzkSale(mzkreq, mzkret))
			return false;

		if (!mzkret.str3.equals(""))
		{
			StringBuffer passwd = new StringBuffer();
			if (!getPasswdBeforeFindMzk(passwd))
				return false;

			if (!mzkret.str3.equals(passwd.toString()))
				return false;
		}
		mzkret.str3="";
		
		return true;
	}
}
