package custom.localize.Zmsy;

import java.net.URL;

import org.codehaus.xfire.client.Client;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.WebServiceConfigClass;

public class WebServiceHG
{

	public static String getInformation(String strInValue)
	{
		try
		{
			String strUrl = "";//"http://172.17.8.2:8888/pip/SearchCusInfWS?wsdl";
			strUrl=WebServiceConfigClass.getDefault().getEndPoint();
			int timeout=Convert.toInt(WebServiceConfigClass.getDefault().getWebServicetimeout());
			if (strUrl==null || strUrl.trim().length()<=0 || timeout<=0)
			{
				PosLog.getLog(WebServiceHG.class.getSimpleName()).info("读取WW参数的海关平台地址,IEUrl(WW)=[" + String.valueOf(GlobalInfo.sysPara.gwkHGUrl).trim() + "]");
				
				//当本地配置文件不存在时，则读取参数
				String[] urlArr = GlobalInfo.sysPara.gwkHGUrl.split("\\|");
				if (urlArr.length>=2)
				{
					strUrl = urlArr[1].trim();
				}
				else
				{
					PosLog.getLog(WebServiceHG.class.getSimpleName()).info("海关平台地址获取失败");
					return null;
				}
				if (urlArr.length>=3)
				{
					timeout = Convert.toInt(urlArr[2]);
				}
				
			}			
			PosLog.getLog(WebServiceHG.class.getSimpleName()).info("strUrl=[" + strUrl + "]");

			if (timeout<=1) timeout=5;
			timeout = timeout*1000;
			PosLog.getLog(WebServiceHG.class.getSimpleName()).info("timeout=[" + timeout + "]");
			
			URL url = new URL(strUrl);
			//PosLog.getLog(WebServiceHG.class.getSimpleName()).info("url init end");
			Client c = new Client(url);
			//PosLog.getLog(WebServiceHG.class.getSimpleName()).info("client init end");			
			c.setTimeout(timeout);
			//PosLog.getLog(WebServiceHG.class.getSimpleName()).info("invoke start");
			Object[] results = c.invoke("getInformation", new Object[]{strInValue});//"02,12345"
			//PosLog.getLog(WebServiceHG.class.getSimpleName()).info("invoke end");
			if (results==null || results.length<=0) return null;
			return String.valueOf(results[0]);
		}
		catch(Exception ex)
		{
			PosLog.getLog(WebServiceHG.class.getSimpleName()).error(ex);
			return ex.getMessage();
		}
		//return null;
	}
}
