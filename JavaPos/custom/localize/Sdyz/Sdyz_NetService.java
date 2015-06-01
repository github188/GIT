package custom.localize.Sdyz;

import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.CustomerDef;

public class Sdyz_NetService extends com.efuture.javaPos.Communication.NetService
{
	public boolean getCustomer(CustomerDef cust, String track)
	{
		if (GlobalInfo.sysPara.cardsvrurl == null || GlobalInfo.sysPara.cardsvrurl.trim().length() <= 0)
		{
			return getCustomer(GlobalInfo.localHttp,cust,track);
		}
		else
		{
			// 发送请求到独立卡服务器
	        if (GlobalInfo.cardHttp == null)
	        {
	            GlobalInfo.cardHttp = new Http(GlobalInfo.sysPara.cardsvrurl);
	            GlobalInfo.cardHttp.init();
	            GlobalInfo.cardHttp.setConncetTimeout(ConfigClass.ConnectTimeout);	//连接超时
	            GlobalInfo.cardHttp.setReadTimeout(ConfigClass.ReceiveTimeout); 	//处理超时        		
	        }
	        
			return getCustomer(GlobalInfo.cardHttp,cust,track);
		}
	}
}
