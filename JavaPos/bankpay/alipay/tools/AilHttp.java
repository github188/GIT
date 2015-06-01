package bankpay.alipay.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.efuture.DeBugTools.PosLog;
/**
 * url为posserver访问地址
 * urlvalue 为组装好的ailpay参数
 * rev 为ailpay返回的参数
 * @author Administrator
 *
 */
public class AilHttp 
{
	//客户端调用代码
	public String HttpPostData(String url,String urlvalue) 
	{
		String rev = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
    	String time = df.format(new Date()).toString();
		try 
		{
			HttpClient httpclient = new DefaultHttpClient();
			PosLog.getLog(getClass()).info("["+time+"  ]"+"aliurl=====>"+url);
			PosLog.getLog(getClass()).info("["+time+"  ]"+"aliurlvalue=====>"+urlvalue);
			//String url ="http://127.0.0.1:8080/JAVAPOSSERVER/AliPayService";
			HttpPost httppost = new HttpPost(url);
			// 添加http头信息
			httppost.addHeader("Authorization", "your token"); // 认证token
			//httppost.addHeader("Content-Type", "charset=UTF-8");
			httppost.addHeader("User-Agent", "imgfornote");
			httppost.addHeader("Content-Type", "application/json;charset=UTF-8");
			httppost.setEntity(new StringEntity(urlvalue,"UTF-8"));
			HttpResponse response;
			response = httpclient.execute(httppost);
			// 检验状态码，如果成功接收数据
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) 
			{
				rev = EntityUtils.toString(response.getEntity(),"UTF-8");
			}
			PosLog.getLog(getClass()).info("["+time+"  ]"+"alireturn=====>"+rev);
			return rev;
		} 
		catch (ClientProtocolException e) 
		{
			PosLog.getLog(getClass()).info("["+time+"  ]"+"ClientProtocolException"+e);
			e.printStackTrace();
			return null;
		} 
		catch (IOException e) 
		{
			PosLog.getLog(getClass()).info("["+time+"  ]"+"IOException"+e);
			e.printStackTrace();
			return null;
		} 
		catch (Exception e) 
		{
			PosLog.getLog(getClass()).info("["+time+"  ]"+"Exception"+e);
			e.printStackTrace();
			return null;
		}
	}
	
	//HttpGet
	@SuppressWarnings("finally")
	public String executeGet(String url) throws Exception 
	{  
		BufferedReader in = null;  
		   
		String content = null; 
		PosLog.getLog(getClass()).info("SUNING==========>"+url);
			try 
		   	{  
		       URL urlp = new URL(url);
		       URI uri = new URI(urlp.getProtocol(), urlp.getHost(), urlp.getPath(), urlp.getQuery(), null);
		       // 定义HttpClient  
		       HttpClient client = new DefaultHttpClient();  
		       // 实例化HTTP方法  
		       HttpGet request = new HttpGet();  
		       request.setURI(uri);  
		       HttpResponse response = client.execute(request);  
		    
		       in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"));  
		       StringBuffer sb = new StringBuffer("");  
		       String line = "";  
		       //String NL = System.getProperty("line.separator");  
		       while ((line = in.readLine()) != null) 
		       {  
		          sb.append(line);  
		       }
		       in.close();  
		       content = sb.toString();  
		       PosLog.getLog(getClass()).info("SUNINGRETURN==========>"+content);
		       } 
		         catch (Exception e) 
               {  
                   e.printStackTrace();  
               }  
		         finally 
		         {  
		         if (in != null)
		              {  
		                  try 
		                  {  
		                      in.close();// 最后要关闭BufferedReader  
		                  } 
		                  catch (final Exception e) 
		                  {  
		                      e.printStackTrace();  
		                  }  
		              }  
		             return content;  
		          }  
		  }  
	
	public static void main(String [] args)
	{
		AilHttp ah = new AilHttp();
		String url ="http://sitpay.cnsuning.com/epp-open/openService/gate-way.action?_input_charset=UTF-8&out_order_no=2389838112&partner=XBGW&service=query_trade_order&sign=7ca62ead5d2d7a10376a1a8cf045699e&sign_type=md5";
		try {
			System.out.println(ah.executeGet(url));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
