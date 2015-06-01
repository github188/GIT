package custom.localize.Hmsl;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;

import custom.localize.Bstd.Bstd_DataService;

public class Hmsl_DataService extends Bstd_DataService
{
	public boolean getCustomer(CustomerDef cust, String track)
	{
		String password = "";
		if (track == null || track.trim().equals(""))
		{
			new MessageBox("磁道数据错误!");
			return false;
		}

		if (track.length() == 19)
			password = track.substring(track.length() - 6);

		if (super.getCustomer(cust, track))
		{
			if (track.length() == 19)
			{
				if (cust.valstr1 == null || cust.valstr1.trim().equals(""))
				{
					new MessageBox("无法判断该卡有效性!");
					return false;
				}

				if (!cust.valstr1.equals(password))
				{
					new MessageBox("卡校验失败,不允许交易!");
					return false;
				}
			}
			return true;
		}

		return false;
	}
	
}
