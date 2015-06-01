package custom.localize.Wqbh;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.MessageBox;

//import custom.localize.Wqbh.HttpsUtils;


public class Wqbh_DHYInterface {

	public String getRZ(String method) {//获取认证信息
//		String token = "f216270aa8a7c513fd1a2c287c58f036";
		String appid = "baihuo_pos";
//		long ts = new Date().getTime() / 1000;
		
//		String sign = GetMD5Code(token + ts + appid);
//		String Result = "&appid=" + appid + "&ts=" + ts + "&sign=" + sign;
		ApiKeySecret aps=new ApiKeySecret();
		int ts=(int) (System.currentTimeMillis() / 1000);
//		String key ="d3b181e0d7eef22286930ab390711e00";
//		String secret="b079f52a7717169e6df9d6d57ca4fb6c";
		String key ="abcdebda94b5407df87d6281f0bae01a";
		String secret="10a8f6eb6875d37cb85fcd3ca0f02ca4";
		String sign =aps.genSign(key, secret, method, ts);
		
		String Result="&appid=" + appid + "&ts=" + ts + "&sign=" + sign
		+"&app_key="+key+"&method="+ method;//
		return Result;
	}
	
	

	//	会员登录
	public String MemberLogin(String urlvalue) {
		String rev = null;
		try {
			urlvalue = urlvalue + getRZ("POST");
			HttpClient httpclient = new DefaultHttpClient();
			httpclient= WebClientDevWrapper.wrapClient(httpclient);
			//String url = "https://sandbox.api.wanhui.cn/ucenter/v2/loginTokens";//测试库
			String url = "https://api.wanhui.cn/ucenter/v2/loginTokens";//正式库
			HttpPost httppost = new HttpPost(url);
			httppost.addHeader("User-Agent", "imgfornote");
			httppost.addHeader("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			httppost.setEntity(new StringEntity(urlvalue, "UTF-8"));//默认的编码不是utf-8

			//HttpPost httpPost = new HttpPost(reqURL);
			HttpResponse response;
			response = httpclient.execute(httppost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				rev = EntityUtils.toString(response.getEntity(), "utf-8");
			}
			return rev;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	//验证支付密码
	public String CheckPayPwd(String uid, String urlvalue) {
		String rev = null;
		try {
			urlvalue = urlvalue + getRZ("POST");
			HttpClient httpclient = new DefaultHttpClient();
			httpclient= WebClientDevWrapper.wrapClient(httpclient);
			//String url = "https://sandbox.api.wanhui.cn/ucenter/v2/users/"+uid+"/payTokens";
			String url = "https://api.wanhui.cn/ucenter/v2/users/"+uid+"/payTokens";
			HttpPost httppost = new HttpPost(url);
			httppost.addHeader("User-Agent", "imgfornote");
			httppost.addHeader("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			httppost.setEntity(new StringEntity(urlvalue, "UTF-8"));//默认的编码不是utf-8
			HttpResponse response;
			response = httpclient.execute(httppost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				rev = EntityUtils.toString(response.getEntity(), "utf-8");
			}
			return rev;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	//增加积分
	public String AddDHYJF(String urlvalue) {
		String rev = null;
		try {

			urlvalue = urlvalue + getRZ("POST");
			HttpClient httpclient = new DefaultHttpClient();
			httpclient= WebClientDevWrapper.wrapClient(httpclient);
			//String url = "https://sandbox.api.wanhui.cn/trade/orders/addPoint";//测试库
			String url = "https://api.wanhui.cn/trade/orders/addPoint";//正式库
			HttpPost httppost = new HttpPost(url);
			httppost.addHeader("User-Agent", "imgfornote");
			httppost.addHeader("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			httppost.setEntity(new StringEntity(urlvalue, "UTF-8"));
			HttpResponse response;
			response = httpclient.execute(httppost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				rev = EntityUtils.toString(response.getEntity(), "utf-8");
			}else{
				PosLog.getLog(getClass()).info("当前增加积分接口请求返回:"+code+",请求数据:"+urlvalue);
				new MessageBox("当前增加积分接口请求失败返回"+code);
			}
			return rev;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	//扣减积分
	public String MinusDHYJF(String urlvalue) {
		String rev = null;
		try {
			urlvalue = urlvalue + getRZ("POST");
			HttpClient httpclient = new DefaultHttpClient();
			httpclient= WebClientDevWrapper.wrapClient(httpclient);
			//String url = "https://sandbox.api.wanhui.cn/trade/orders/reducePoint";
			String url = "https://api.wanhui.cn/trade/orders/reducePoint";
			HttpPost httppost = new HttpPost(url);
			httppost.addHeader("User-Agent", "imgfornote");
			httppost.addHeader("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			httppost.setEntity(new StringEntity(urlvalue, "UTF-8"));
			HttpResponse response;
			response = httpclient.execute(httppost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				rev = EntityUtils.toString(response.getEntity(), "utf-8");
			}else{
				PosLog.getLog(getClass()).info("当前扣减积分接口请求返回:"+code+",请求数据:"+urlvalue);
				new MessageBox("当前扣减积分接口请求失败返回"+code);
			}
			return rev;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	//取消积分
	public String CancelJF(String urlvalue) {

		String rev = null;
		try {
			urlvalue = urlvalue + getRZ("POST");
			HttpClient httpclient = new DefaultHttpClient();
			httpclient= WebClientDevWrapper.wrapClient(httpclient);
			//String url = "https://sandbox.api.wanhui.cn/trade/orders/cancelPointActive";
			String url = "https://api.wanhui.cn/trade/orders/cancelPointActive";
			HttpPost httppost = new HttpPost(url);
			httppost.addHeader("User-Agent", "imgfornote");
			httppost.addHeader("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			httppost.setEntity(new StringEntity(urlvalue, "UTF-8"));
			HttpResponse response;
			response = httpclient.execute(httppost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				rev = EntityUtils.toString(response.getEntity(), "utf-8");
			}
			return rev;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	//取消交易
	public String CancelJY(String urlvalue) {
		String rev = null;
		try {
			urlvalue = urlvalue + getRZ("POST");
			HttpClient httpclient = new DefaultHttpClient();
			httpclient= WebClientDevWrapper.wrapClient(httpclient);
			//String url = "https://sandbox.api.wanhui.cn/trade/orders/cancel";
			String url = "https://api.wanhui.cn/trade/orders/cancel";
			HttpPost httppost = new HttpPost(url);
			httppost.addHeader("User-Agent", "imgfornote");
			httppost.addHeader("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			httppost.setEntity(new StringEntity(urlvalue, "UTF-8"));
			HttpResponse response;
			response = httpclient.execute(httppost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				rev = EntityUtils.toString(response.getEntity(), "utf-8");
			}
			return rev;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	//发送短信验证码
	public String PostMsgChk(String urlvalue) {

		String rev = null;
		try {
			urlvalue = urlvalue + getRZ("POST");
			HttpClient httpclient = new DefaultHttpClient();
			httpclient= WebClientDevWrapper.wrapClient(httpclient);
			//String url = "https://sandbox.api.wanhui.cn/ucenter/v2/verifyCodes";
			String url = "https://api.wanhui.cn/ucenter/v2/verifyCodes";
			HttpPost httppost = new HttpPost(url);
			httppost.addHeader("User-Agent", "imgfornote");
			httppost.addHeader("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			httppost.setEntity(new StringEntity(urlvalue, "UTF-8"));
			HttpResponse response;
			response = httpclient.execute(httppost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				rev = EntityUtils.toString(response.getEntity(), "utf-8");
			}
			return rev;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	//同步交易信息
	public String SyncSaleMsg(String urlvalue) {
		String rev = null;
		try {
			urlvalue = urlvalue + getRZ("POST");
			HttpClient httpclient = new DefaultHttpClient();
			httpclient= WebClientDevWrapper.wrapClient(httpclient);
			//String url = "https://sandbox.api.wanhui.cn/trade/orders";
			String url = "https://api.wanhui.cn/trade/orders";
			HttpPost httppost = new HttpPost(url);
			httppost.addHeader("User-Agent", "imgfornote");
			httppost.addHeader("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			httppost.setEntity(new StringEntity(urlvalue, "UTF-8"));
			HttpResponse response;
			response = httpclient.execute(httppost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				rev = EntityUtils.toString(response.getEntity(), "utf-8");
			}
			return rev;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	//同步退款信息
	public String SyncBackMsg(String urlvalue) {
		String rev = null;
		try {
			urlvalue = urlvalue + getRZ("POST");
			HttpClient httpclient = new DefaultHttpClient();
			httpclient= WebClientDevWrapper.wrapClient(httpclient);
			//String url = "https://sandbox.api.wanhui.cn/trade/refunds";
			String url = "https://api.wanhui.cn/trade/refunds";
			HttpPost httppost = new HttpPost(url);
			httppost.addHeader("User-Agent", "imgfornote");
			httppost.addHeader("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			httppost.setEntity(new StringEntity(urlvalue, "UTF-8"));
			HttpResponse response;
			response = httpclient.execute(httppost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				rev = EntityUtils.toString(response.getEntity(), "utf-8");
			}
			return rev;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	//	会员注册
	public String MemberRegist(String urlvalue) {
		String rev = null;
		try {
			urlvalue = urlvalue + getRZ("POST");
			HttpClient httpclient = new DefaultHttpClient();
			httpclient= WebClientDevWrapper.wrapClient(httpclient);
			//String url = "http://openapi.wanhui.cn/pos/test/ucenter/users";
			String url = "http://openapi.wanhui.cn/ucenter/v2/users";
			HttpPost httppost = new HttpPost(url);
			httppost.addHeader("User-Agent", "imgfornote");
			httppost.addHeader("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			httppost.setEntity(new StringEntity(urlvalue, "UTF-8"));
			HttpResponse response;
			response = httpclient.execute(httppost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				rev = EntityUtils.toString(response.getEntity(), "utf-8");
			}
			return rev;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
