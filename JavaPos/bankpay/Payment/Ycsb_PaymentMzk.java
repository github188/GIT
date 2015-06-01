package bankpay.Payment;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.DataService;

public class Ycsb_PaymentMzk extends Shop_PaymentMzk
{
	protected boolean getPasswdBeforeFindMzk(StringBuffer passwd)
	{
		return true;
	}

	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		if (super.findMzkInfo(track1, track2, track3))
		{
			if (this.mzkret.ispw == 'Y')
			{
				String[] str = track2.split("=");
				track2 = str[0];

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

				// 设置用户输入密码
				StringBuffer passwd = new StringBuffer();
				if (!super.getPasswdBeforeFindMzk(passwd))
				{
					return false;
				}
				else
				{
					mzkreq.passwd = passwd.toString();
				}
				return DataService.getDefault().getMzkInfo(mzkreq, mzkret);
			}
			return true;
		}
		return false;

	}

	public boolean findMzk(String track1, String track2, String track3)
	{
		if (super.findMzk(track1, track2, track3))
		{
			if (this.mzkret.ispw == 'Y')
			{
				String[] str = track2.split("=");
				track2 = str[0];

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

				// 设置用户输入密码
				StringBuffer passwd = new StringBuffer();
				if (!super.getPasswdBeforeFindMzk(passwd))
				{
					return false;
				}
				else
				{
					mzkreq.passwd = passwd.toString();
				}

				return sendMzkSale(mzkreq, mzkret);
			}
			return true;
		}
		return false;
	}
}
