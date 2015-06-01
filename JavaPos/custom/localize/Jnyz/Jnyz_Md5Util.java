package custom.localize.Jnyz;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import bankpay.alipay.tools.Md5Tools;

public class Jnyz_Md5Util {
	public final static String MD5(String s,String key,String charset) {
	        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};       

	        try {
	        	s = s+key;
	            byte[] btInput = s.getBytes(charset);
	            // 获得MD5摘要算法的 MessageDigest 对象
	            MessageDigest mdInst = MessageDigest.getInstance("MD5");
	            // 使用指定的字节更新摘要
	            mdInst.update(btInput);
	            // 获得密文
	            byte[] md = mdInst.digest();
	            // 把密文转换成十六进制的字符串形式
	            int j = md.length;
	            char str[] = new char[j * 2];
	            int k = 0;
	            for (int i = 0; i < j; i++) {
	                byte byte0 = md[i];
	                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
	                str[k++] = hexDigits[byte0 & 0xf];
	            }
	            return new String(str);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
    }

	public static void main(String[] args) {
        System.out.println(Jnyz_Md5Util.MD5("_input_charset=utf-8&datestr=20140823&dynamic_id=289029002122950949&dynamic_id_type=barcode&invno=986173&market=0001&opertype=PAY&posid=0608&saleseqno=40823268&subcompany=01&total_fee=0.01","1e825bc215af7f49943190e0f8d4eb4b","utf-8"));
        
        StringBuffer sb = new StringBuffer();
//      sb.append("_input_charset=utf-8&datestr=20140823&dynamic_id=289029002122950949&dynamic_id_type=barcode&invno=986173&market=0001&opertype=PAY&posid=0608&saleseqno=40823268&subcompany=01&total_fee=0.01");
        sb.append("_input_charset=utf-8&datestr=20140828&dynamic_id=0100010608201408283&dynamic_id_type=barcode&invno=0&market=0001&opertype=REFUND&posid=0608&saleseqno=0100010608201408283&subcompany=01&total_fee=0.1");
		Md5Tools getMD5 = new Md5Tools();
		String sign = getMD5.GetMD5Code(sb.toString()+"1e825bc215af7f49943190e0f8d4eb4b");
        HttpPost httppost =new HttpPost("http://10.0.128.161:8001/O2O");
		String aliurl =sb.toString()+"&sign="+sign+ "&sign_type=MD5";
		System.out.println(sign);
		System.out.println("urlvalue====="+aliurl);
		
		StringEntity entity;
		String bearXml = "";
		try {
			entity = new StringEntity(aliurl);
			
			HttpClient httpclient = new DefaultHttpClient();
			entity.setContentType("application/x-www-form-urlencoded");
			httppost.setEntity(entity);

			HttpResponse response;
			response = httpclient.execute(httppost);
			
			// 检验状态码，如果成功接收数据
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				bearXml = EntityUtils.toString(response.getEntity());
				System.out.println("resultXml====="+bearXml);
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
//		System.out.println(Jnyz_Md5Util.MD5("_input_charset=utf-8&datestr=20140822&dynamic_id=284091807938704871&dynamic_id_type=barcode&invno=1&market=0001&opertype=PAY&posid=0608&saleseqno=635443113411566588&subcompany=01&total_fee=0.021e825bc215af7f49943190e0f8d4eb4b", "1e825bc215af7f49943190e0f8d4eb4b", "utf-8"));
        
//        Md5Tools getMD5 = new Md5Tools();
//        String sign = getMD5.GetMD5Code("_input_charset=utf-8&datestr=20140822&dynamic_id=284091807938704871&dynamic_id_type=barcode&invno=1&market=0001&opertype=PAY&posid=0608&saleseqno=635443113411566588&subcompany=01&total_fee=0.02");
//        System.out.println(sign);
    }
	
	 public static String padLeft(String s, int length)
	    {
	        byte[] bs = new byte[length];
	        byte[] ss = s.getBytes();
	        Arrays.fill(bs, (byte) (48 & 0xff));
	        System.arraycopy(ss, 0, bs,length - ss.length, ss.length);
	        return new String(bs);
	    }

	 public static String GetX(byte[] t) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < t.length; i++) {
				int v = t[i] & 0xFF;
				String hv = Integer.toHexString(v);
				if (hv.length() < 2) {
					sb.append(0);
				}
				sb.append(hv);
			}
			return sb.toString();
		}
}
