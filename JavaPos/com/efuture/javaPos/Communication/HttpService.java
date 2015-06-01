package com.efuture.javaPos.Communication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.params.CoreConnectionPNames;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.Language;

public class HttpService
{

	public String httpGet(String url)
	{
		System.out.println("请求:" + url);
		String response = null; // 返回信息
		// 构造HttpClient的实例
		HttpClient httpClient = new HttpClient();
		// 创建GET方法的实例
		GetMethod httpGet = new GetMethod(url);

		// 设置超时时间
		httpGet.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, new Integer(ConfigClass.ReceiveTimeout));

		try
		{
			int statusCode = httpClient.executeMethod(httpGet);
			// SC_OK = 200
			if (statusCode == HttpStatus.SC_OK)
			{
				InputStream inputStream = httpGet.getResponseBodyAsStream(); // 获取输出流，流中包含服务器返回信息
				response = getData(inputStream);// 获取返回信息

				System.out.println("响应:" + response);
			}
			else
			{
				new MessageBox(Language.apply("通讯故障:") + statusCode);
				System.out.print("Get Method Statuscode : " + statusCode);
				return null;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			httpGet.releaseConnection();
			httpClient = null;
		}
		return response;
	}

	public String httpPost(String url, List params, int timeout) throws Exception
	{
		String response = null;
		HttpClient httpClient = new HttpClient();

		PostMethod httpPost = new CharsetPostMethod(url);

		// Post方式我们需要配置参数
		httpPost.addParameter("Connection", "Keep-Alive");
		httpPost.addParameter("Content-Type", "application/x-www-form-urlencoded");

		// httpPost.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, new
		// Integer(timeout));

		// 连接超时
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);

		// 读取超时
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);

		if (null != params & params.size() != 0)
			// 设置需要传递的参数，NameValuePair[]
			httpPost.setRequestBody(buildNameValuePair(params));

		try
		{
			int statusCode = httpClient.executeMethod(httpPost);

			if (statusCode == HttpStatus.SC_OK)
			{
				InputStream inputStream = httpPost.getResponseBodyAsStream();
				response = getData(inputStream);
			}
			else
			{
				new MessageBox(Language.apply("通讯故障:") + statusCode);
				System.out.print("Post Method Statuscode : " + statusCode);
				return null;
			}
		}
		catch (Exception ex)
		{
			throw new Exception(ex);
		}
		finally
		{
			httpPost.releaseConnection();
			httpClient = null;
		}
		return response;
	}

	private NameValuePair[] buildNameValuePair(List params)
	{
		int size = params.size();
		NameValuePair[] pair = new NameValuePair[size];
		for (int i = 0; i < size; i++)
		{
			HttpParameter param = (HttpParameter) params.get(i);
			pair[i] = new NameValuePair(param.getName(), param.getValue());
		}
		return pair;
	}

	private String getData(InputStream inputStream) throws Exception
	{
		String data = "";
		// 内存缓冲区
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		int len = -1;
		byte[] buff = new byte[1024];
		try
		{
			while ((len = inputStream.read(buff)) != -1)
			{
				outputStream.write(buff, 0, len);
			}
			byte[] bytes = outputStream.toByteArray();
			data = new String(bytes, "UTF-8");
		}
		catch (IOException e)
		{
			throw new Exception(e.getMessage(), e);
		}
		finally
		{
			outputStream.close();
		}
		return data;
	}

	public HttpParameter obtainParameter(String name, String value)
	{
		return new HttpParameter(name, value);
	}

	public class CharsetPostMethod extends PostMethod
	{
		private String charset = "UTF-8";

		public CharsetPostMethod(String url)
		{
			super(url);
		}

		public CharsetPostMethod(String url, String charset)
		{
			super(url);
			this.charset = charset;
		}

		public String getRequestCharSet()
		{
			return charset;
		}
	}

	class HttpParameter
	{
		private String name;// 参数名
		private String value;// 参数值

		public HttpParameter(String name, String value)
		{
			this.name = name;
			this.value = value;
		}

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public String getValue()
		{
			return value;
		}

		public void setValue(String value)
		{
			this.value = value;
		}
	}
}
