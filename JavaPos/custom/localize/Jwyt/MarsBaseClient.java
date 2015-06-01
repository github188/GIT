package custom.localize.Jwyt;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.efuture.javaPos.Global.ConfigClass;
import com.mar114.mars.encrypt.DESandMD5Encoder;

public class MarsBaseClient
{
	protected static final String VERSION = "1.0";
	// mars服务器地址
	protected static final String URL = "http://localhost:8080/mars/service/cmd";
	// 商家Id
	protected static final String merchantId = "138025433199920";
	// 商家通信密钥
	protected static final String merKey = "DXwMM21kFGY=";

	/**
	 * 发送HTTP请求到Mars
	 * 
	 * @param url
	 *            接口地址
	 * @param param
	 *            请求参数
	 * @param key
	 *            密钥
	 * @param merchantId
	 *            商家Id
	 * @return
	 */
	public MarsResponseEntity requestForHTTP(String url, String param, String key, String merchantId)
	{
		HttpClient httpclient = null;
		HttpPost post = null;
		String resp = null;
		List params = new ArrayList(); // NameValuePair
		HttpEntity entity = null;
		MarsResponseEntity resEntity = null;
		DESandMD5Encoder encoder = null;

		try
		{
			encoder = new DESandMD5Encoder(key);
			encoder.encodeBase64(param);

			params.add(new BasicNameValuePair("reqmsg", encoder.getCiphertext()));
			// System.out.println(encoder.getCiphertext());
			params.add(new BasicNameValuePair("sign", encoder.getSign()));
			// System.out.println(encoder.getSign());
			params.add(new BasicNameValuePair("merchantId", merchantId));
			httpclient = new DefaultHttpClient();

			HttpParams httpparams = httpclient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpparams, ConfigClass.ConnectTimeout);
			HttpConnectionParams.setSoTimeout(httpparams, ConfigClass.ReceiveTimeout);
			// HttpClientParams.setRedirecting(httpparams, true);
			// HttpClientParams.setAuthenticating(httpparams, false);

			post = new HttpPost(url);

			entity = new UrlEncodedFormEntity(params, "utf-8");

			post.setEntity(entity);
			HttpResponse response = httpclient.execute(post);
			int status = response.getStatusLine().getStatusCode();
			resp = EntityUtils.toString(response.getEntity());
			if (status == org.apache.http.HttpStatus.SC_ACCEPTED || status == org.apache.http.HttpStatus.SC_OK)
			{
				resEntity = new MarsResponseEntity(true, status, resp);
			}
			else
			{
				resEntity = new MarsResponseEntity(false, status, resp);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (httpclient != null)
			{
				httpclient.getConnectionManager().shutdown();
				httpclient = null;
			}
			if (post != null)
			{
				// 释放连接
				post.abort();
				post = null;
			}
		}
		return resEntity;
	}
}
