package custom.localize.Dxyc;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.CustomerDef;

import custom.localize.Bstd.Bstd_DataService;

public class Dxyc_DataService extends Bstd_DataService
{
	public boolean getCustomer(CustomerDef cust, String track)
	{
		// 联网本地优先查询(Y-联网优先本地查询再查网上/Z-联网只查询本地)
		if (GlobalInfo.isOnline && GlobalInfo.sysPara.localfind != 'N' && GlobalInfo.sysPara.customerbyconnect != 'Y')
		{
			// 联网且优先找本地时，当参数为A时先找网上的会员 /美佳美
			if (GlobalInfo.sysPara.customerbyconnect == 'A' && NetService.getDefault().getCustomer(cust, track))
				return true;

			if (!AccessBaseDB.getDefault().getCustomer(cust, track))
			{
				// Z-联网只查询本地,失败不再查询网上
				if (GlobalInfo.sysPara.localfind == 'Z')
				{
					new MessageBox("无此顾客卡信息!", null, false);

					return false;
				}
			}
			else
			{
				return true;
			}
		}

		if (GlobalInfo.isOnline)
		{
			if (!NetService.getDefault().getCustomer(cust, track)) { return false; }
		}
		else
		{
			// 会员卡必须联网使用
			if (GlobalInfo.sysPara.customerbyconnect == 'Y')
			{
				new MessageBox("顾客卡必须联网使用!");

				return false;
			}

			if (!AccessBaseDB.getDefault().getCustomer(cust, track))
			{
				new MessageBox("无此顾客卡信息!", null, false);

				return false;
			}
		}

		return true;
	}
}
