package bankpay.Payment;


import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Payment.PaymentMzk;

public class TsjWjMzk_PaymentMzk extends PaymentMzk
{
	// 唐山家万佳储值卡

	public boolean findMzk(String track1, String track2, String track3)
	{
		if ((track1 == null || track1.trim().length() <= 0) && 
			(track2 == null || track2.trim().length() <= 0) && 
			(track3 == null || track3.trim().length() <=0))
		{
			new MessageBox("磁道数据为空!");
			return false;
		}
		
		// 解析磁道
		String[] s = parseTrack(track1,track2,track3);
		if (s == null) return false;
		track1 = s[0];
		track2 = s[1];
		track3 = s[2];
		
		// 设置请求数据
		setRequestDataByFind(track1,track2,track3);
		
//		if(track2.indexOf('6') == 0 && track2.indexOf('0') ==1){
			// 设置用户输入密码
			StringBuffer passwd = new StringBuffer();
			if (!getPasswdBeforeFindMzk(passwd))
			{
				return false;
			}
			else
			{
				mzkreq.passwd = passwd.toString();
			}
//		}
		
		//
		return sendMzkSale(mzkreq,mzkret); 
	}
	
	
	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		if ((track1 == null || track1.trim().length() <= 0) && 
			(track2 == null || track2.trim().length() <= 0) && 
			(track3 == null || track3.trim().length() <=0))
		{
			new MessageBox("磁道数据为空!");
			return false;
		}
		
		// 解析磁道
		String[] s = parseTrack(track1,track2,track3);
		if (s == null) return false;
		track1 = s[0];
		track2 = s[1];
		track3 = s[2];
		
		// 设置请求数据
		setRequestDataByFind(track1,track2,track3);

//		if(track2.indexOf('6') == 0 && track2.indexOf('0') ==1){
			// 设置用户输入密码
			StringBuffer passwd = new StringBuffer();
			if (!getPasswdBeforeFindMzk(passwd))
			{
				return false;
			}
			else
			{
				mzkreq.passwd = passwd.toString();
			}
//		}
		//
		return DataService.getDefault().getMzkInfo(mzkreq, mzkret);
	}
	
	public boolean isPasswdInput()
	{
		
			return false;
	}
	
}
