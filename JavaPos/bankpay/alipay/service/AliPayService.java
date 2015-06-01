package bankpay.alipay.service;

import java.util.Arrays;
import java.util.HashMap;

import net.sf.json.JSONObject;

import com.efuture.DeBugTools.PosLog;
import com.efuture.javaPos.Global.GlobalInfo;
import bankpay.alipay.tools.AilHttp;
import bankpay.alipay.tools.Md5Tools;
import bankpay.alipay.tools.ParseIni;
import bankpay.alipay.tools.ParseXml;
import bankpay.alipay.tools.RandomNum;


public class AliPayService {
	// 商户号
	//private String ctNo = "2088201565141845";
	// 商户密钥
	//private String ctKeyNo = "ai1ce2jkwkmd3bddy97z0xnz3lxqk731";
	// 实例化客户端Http
	private AilHttp ailHttp = new AilHttp();
	// 生成随机数
	private RandomNum randomNum = new RandomNum();
	// javaPos地址
	private static String url = "http://127.0.0.1:8080/JAVAPAYSERVER/AliConfigService";

	private ParseXml parseXml = new ParseXml();

	private ParseIni parseIni = new ParseIni();
 
	/**
	 * 获取ALiPAY商户号与商户密钥
	 * @param aliConfigUrl 
	 * 				webservice访问地址
	 * @return
	 */
	public HashMap aliConfig(String aliConfigUrl)
	{
		try 
		{
			HashMap map = new HashMap();
			String url = aliConfigUrl;
			String config = ailHttp.HttpPostData(url, "config");
			if(null!=config&&!"".equals(config))
			{
				String [] values = config.split("-");
				map.put("partnerNo", values[0]);
				map.put("partnerKey", values[1]);
				map.put("agentId", values[2]); 
				return map;
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
		
	}
	
	
	/**
	 * 声波支付
	 * 
	 * @param partner
	 *            商户号
	 * @param partnerKey
	 *            商户密钥
	 * @param subject
	 *            订单描述
	 * @param tmCode
	 *            条码号
	 * @param money
	 *            总金额
	 * @param extend_params 
	 * 			      扩展参数
	 * @return
	 */
	public String soundWavePay(String partner, String partnerKey, String subject,
			String tmCode, String money,String url,String extend_params) 
	{
		try 
		{
			String rn = randomNum.generateString(4);
			String outTradeNo = randomNum.getTime()+GlobalInfo.posLogin.gh+rn;
			// 组装签名
			String[] ref = { "service=alipay.acquire.createandpay",
					"partner=" + partner, "_input_charset=utf-8",
					"out_trade_no=" + outTradeNo, "subject=声波支付-" + subject,
					"dynamic_id_type=wave_code", "dynamic_id=" + tmCode,"it_b_pay=20m",
					"product_code=SOUNDWAVE_PAY_OFFLINE", "total_fee=" + money,
					"extend_params="+extend_params};

			Arrays.sort(ref);

			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < ref.length; j++) 
			{
				//System.out.println(ref[j]);
				if (j == 0) 
				{
					sb.append(ref[j]);
				} 
				else 
				{
					sb.append("&");
					sb.append(ref[j]);
				}

			}
			Md5Tools getMD5 = new Md5Tools();
			String sign = getMD5.GetMD5Code(sb.toString() + partnerKey.trim());
			String urlvalue = sb.toString() + "&sign=" + sign;
			return ailHttp.HttpPostData(url, urlvalue);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;

	}
	
	/**
	 * 统一下单并支付请求
	 * 
	 * @param partner
	 *            商户号
	 * @param partnerKey
	 *            商户密钥
	 * @param subject
	 *            订单描述
	 * @param tmCode
	 *            条码号
	 * @param money
	 *            总金额
	 * @param extend_params 
	 * 			      扩展参数
	 * @return
	 */
	public String unifiedPay(String partner, String partnerKey, String subject,
			String tmCode, String money,String url,String extend_params) 
	{
		try 
		{
			String rn = randomNum.generateString(4);
			String outTradeNo = randomNum.getTime()+GlobalInfo.posLogin.gh+rn;
			// 组装签名
			String[] ref = { "service=alipay.acquire.createandpay",
					"partner=" + partner, "_input_charset=utf-8",
					"out_trade_no=" + outTradeNo, "subject=条码支付-" + subject,
					"dynamic_id_type=bar_code", "dynamic_id=" + tmCode,"it_b_pay=20m",
					"product_code=BARCODE_PAY_OFFLINE", "total_fee=" + money,"extend_params="+extend_params};

			Arrays.sort(ref);

			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < ref.length; j++) 
			{
				//System.out.println(ref[j]);
				if (j == 0) 
				{
					sb.append(ref[j]);
				} 
				else 
				{
					sb.append("&");
					sb.append(ref[j]);
				}

			}
			Md5Tools getMD5 = new Md5Tools();
			String sign = getMD5.GetMD5Code(sb.toString() + partnerKey.trim());
			String urlvalue = sb.toString() + "&sign=" + sign;
			return ailHttp.HttpPostData(url, urlvalue);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 统一下单预支付请求
	 * 
	 * @param partner
	 *            商户号
	 * @param partnerKey
	 *            商户密钥
	 * @param subject
	 *            订单描述
	 * @param money
	 *            总金额
	 * @param extend_params 
	 * 			      扩展参数
	 * @return
	 */
	public String advancePay(String partner, String partnerKey, String subject,
			String money,String url,String extend_params) 
	{
		try 
		{
			String rn = randomNum.generateString(4);
			String outTradeNo = randomNum.getTime()+GlobalInfo.posLogin.gh+rn;
			// 组装签名
			String[] ref = {"service=alipay.acquire.precreate",
					"partner=" + partner, "_input_charset=utf-8",
					"out_trade_no=" + outTradeNo, "subject=二维码支付-" + subject,"it_b_pay=20m",
					"product_code=QR_CODE_OFFLINE", "total_fee=" + money,"extend_params="+extend_params};
			Arrays.sort(ref);

			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < ref.length; j++) 
			{
				//System.out.println(ref[j]);
				if (j == 0) 
				{
					sb.append(ref[j]);
				} 
				else 
				{
					sb.append("&");
					sb.append(ref[j]);
				}

			}
			Md5Tools getMD5 = new Md5Tools();
			PosLog.getLog(getClass()).info(sb.toString()+partnerKey.trim());
			String sign = getMD5.GetMD5Code(sb.toString() + partnerKey.trim());
			String urlvalue = sb.toString() + "&sign_type=MD5" + "&sign="
					+ sign;

			return ailHttp.HttpPostData(url, urlvalue);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 查询
	 * 
	 * @param partner
	 *            商户号
	 * @param partnerKey
	 *            商户密钥
	 * @param outTradeNo
	 *            商户订单号
	 * @param tradeNo
	 *            交易订单号 （为空）
	 * @return
	 */
	public String query(String partner, String partnerKey, String outTradeNo,
			String tradeNo,String url) 
	{
		try 
		{
			// 组装签名
			String[] ref = { "service=alipay.acquire.query", "partner=" + partner,
					"_input_charset=utf-8", "out_trade_no=" + outTradeNo
			// "trade_no="+tradeNo
			};
			Arrays.sort(ref);
			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < ref.length; j++) {
				if (j == 0) {
					sb.append(ref[j]);
				} else {
					sb.append("&");
					sb.append(ref[j]);
				}

			}
			Md5Tools getMD5 = new Md5Tools();
			String sign = getMD5.GetMD5Code(sb.toString() + partnerKey.trim());
			String urlvalue = sb.toString() + "&sign=" + sign;

			return ailHttp.HttpPostData(url, urlvalue);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 收单退款
	 * 
	 * @param partner
	 *            商户号
	 * @param partnerKey
	 *            商户密钥
	 * @param outTradeNo
	 *            商户订单号
	 * @param money
	 *            总金额
	 * @return
	 */
	public boolean sellBack(String partner, String partnerKey,
			String outTradeNo, String money,String url) 
	{
		// String outTradeNo = randomNum.generateString(16);
		   String outRequestNo = randomNum.generateString(8);
		try 
		{
			// 组装签名
			String[] ref = { "service=alipay.acquire.refund", "partner=" + partner,
					"_input_charset=utf-8", "out_trade_no=" + outTradeNo,
					"refund_amount=" + money,
			// "trade_no="+tradeNo,
					"out_request_no="+outRequestNo
			};
			Arrays.sort(ref);
			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < ref.length; j++) 
			{
				if (j == 0) 
				{
					sb.append(ref[j]);
				} 
				else 
				{
					sb.append("&");
					sb.append(ref[j]);
				}

			}
			Md5Tools getMD5 = new Md5Tools();
			String sign = getMD5.GetMD5Code(sb.toString() + partnerKey.trim());
			String urlvalue = sb.toString() + "&sign=" + sign;
			String bearXml = ailHttp.HttpPostData(url, urlvalue);
			HashMap map = parseXml.domParseXml(bearXml);
			if (!"SUCCESS".equals(map.get("result_code"))) 
			{
				return false;
			}
			return true;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 收单撤销
	 * 
	 * @param partner
	 *            商户号
	 * @param partnerKey
	 *            商户密钥
	 * @param outTradeNo
	 *            商户订单号
	 * @param tradeNo
	 *            交易号(为空)
	 * @return
	 */
	public boolean sellcancel(String partner, String partnerKey,
			String outTradeNo, String tradeNo,String url) {
		// String outTradeNo = randomNum.generateString(16);
		try 
		{
			//String outRequestNo = randomNum.generateString(16);
			// 组装签名
			String[] ref = { "service=alipay.acquire.cancel", "partner=" + partner,
					"_input_charset=utf-8", "out_trade_no=" + outTradeNo
					};
			Arrays.sort(ref);
			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < ref.length; j++) 
			{
				if (j == 0) 
				{
					sb.append(ref[j]);
				} else 
				{
					sb.append("&");
					sb.append(ref[j]);
				}

			}
			Md5Tools getMD5 = new Md5Tools();
			String sign = getMD5.GetMD5Code(sb.toString() + partnerKey);
			String urlvalue = sb.toString() + "&sign=" + sign;

			// 获得返回数据
			String bearXml = ailHttp.HttpPostData(url, urlvalue);
			// 解析返回数据
			HashMap map = parseXml.domParseXml(bearXml);

			if (!"SUCCESS".equals(map.get("result_code"))) 
			{
				return false;
			}
			return true;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) 
	{
		AliPayService AS= new AliPayService();
		System.out.println(AS.aliConfig(url));
	}

}
