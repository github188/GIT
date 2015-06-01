package bankpay.alipay.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Arrays;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import bankpay.alipay.tools.Md5Tools;






public class TestClient extends HttpPost{
	public static void main(String[]arg)
	{
		TestClient testClient = new TestClient();
		testClient.HttpPostData();
//		//生成签名
//		String [] signs =
//			{"service=alipay.acquire.query",
//				"partner=2088201565141845",
//				"_input_charset=UTF-8",
//				"out_trade_no=1111111111"};
//		Arrays.sort(signs);
//		StringBuffer sb = new StringBuffer();
//		for(int j=0;j<signs.length;j++)
//		{
//			if(j==0)
//			{
//				sb.append(signs[j]);
//			}
//			else
//			{
//				sb.append("&");
//				sb.append(signs[j]);
//			}
//			
//		}
//		
//		
//		System.out.println(sb.toString()+"ai1ce2jkwkmd3bddy97z0xnz3lxqk731");
//		Md5Tools getMD5 = new Md5Tools();
//		String sign = getMD5.GetMD5Code(sb.toString()+"ai1ce2jkwkmd3bddy97z0xnz3lxqk731");
//		String aliurl ="http://mapi.alipay.com/gateway.do?"
//				+ "service=alipay.acquire.query"
//				+ "&partner=2088201565141845"
//				+ "&_input_charset=UTF-8"
//				+ "&sign_type=MD5"
//				+ "&sign="+sign
//				+ "&out_trade_no=1111111111";
//		try 
//		{
//			System.out.println(aliurl);
//			System.out.println(testClient. executeGet(aliurl));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	//客户端调用代码
	private void HttpPostData() {
		try {
			
			//生成签名
			String [] signs ={"service=alipay.acquire.query",
					"partner=2088201565141845",
					"_input_charset=UTF-8",
					"out_trade_no=1111111111"};
			Arrays.sort(signs);
			StringBuffer sb = new StringBuffer();
			for(int j=0;j<signs.length;j++)
			{
				if(j==0)
				{
					sb.append(signs[j]);
				}
				else
				{
					sb.append("&");
					sb.append(signs[j]);
				}
				
			}
			
			System.out.println(sb.toString()+"ai1ce2jkwkmd3bddy97z0xnz3lxqk731");
			Md5Tools getMD5 = new Md5Tools();
			String sign = getMD5.GetMD5Code(sb.toString()+"ai1ce2jkwkmd3bddy97z0xnz3lxqk731");
			
			@SuppressWarnings({ "deprecation", "resource" })
			HttpClient httpclient = new DefaultHttpClient();
			//httpclient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"UTF-8");
			String urlvalue ="service=alipay.acquire.query"
					+ "&partner=2088201565141845"
					+ "&_input_charset=UTF-8"
					+ "&sign_type=MD5"
					+ "&sign="+sign
					+ "&out_trade_no=1111111111";
			String url ="http://127.0.0.1:8080/JAVAPOSSERVER/AliPayService";
			HttpPost httppost = new HttpPost(url);
			// 添加http头信息
			httppost.addHeader("Authorization", "your token"); // 认证token
			httppost.addHeader("Content-Type", "charset=UTF-8");
			httppost.addHeader("User-Agent", "imgfornote");
			//httppost.setHeader("Content-Type", "application/json;charset=UTF-8");
			
//			JSONObject obj = new JSONObject();
//			
//			obj.put("urlvalue",urlvalue);

			httppost.setEntity(new StringEntity(urlvalue,"UTF-8"));
			HttpResponse response;
			response = httpclient.execute(httppost);
			// 检验状态码，如果成功接收数据
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String rev = EntityUtils.toString(response.getEntity());
				System.out.println("client====="+rev);
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (Exception e) {
		}
	}
	
	  public String executeGet(String url) throws Exception 
	  {  
		          BufferedReader in = null;  
		   
		          String content = null;  
		         try 
		         {  
		              // 定义HttpClient  
		              HttpClient client = new DefaultHttpClient();  
		              // 实例化HTTP方法  
		              HttpGet request = new HttpGet();  
		              request.setURI(new URI(url));  
		              HttpResponse response = client.execute(request);  
		    
		              in = new BufferedReader(new InputStreamReader(response.getEntity()  
		                      .getContent()));  
		              StringBuffer sb = new StringBuffer("");  
		              String line = "";  
		              String NL = System.getProperty("line.separator");  
		              while ((line = in.readLine()) != null) 
		              {  
		                  sb.append(line + NL);  
		              }  
		              in.close();  
		              content = sb.toString();  
		          } 
		         	finally 
		          {  
		              if (in != null)
		              {  
		                  try 
		                  {  
		                      in.close();// 最后要关闭BufferedReader  
		                  } 
		                  catch (Exception e) 
		                  {  
		                      e.printStackTrace();  
		                  }  
		              }  
		             return content;  
		          }  
		  }  
	
}
