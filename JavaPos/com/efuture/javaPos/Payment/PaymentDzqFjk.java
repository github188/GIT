package com.efuture.javaPos.Payment;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;

public class PaymentDzqFjk extends PaymentDzq
{
	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		if ((track1 == null || track1.trim().length() <= 0) && 
			(track2 == null || track2.trim().length() <= 0) && 
			(track3 == null || track3.trim().length() <=0))
		{
			new MessageBox(Language.apply("磁道数据为空!"));
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
		
		//
		return DataService.getDefault().getDzqInfo(mzkreq, mzkret);
	}
	
	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{		
		return DataService.getDefault().sendDzqSale(req,ret);
		
	}
}
