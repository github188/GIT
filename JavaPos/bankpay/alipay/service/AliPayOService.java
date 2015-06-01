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


public class AliPayOService {
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
	public String unifiedPay(String operator_id, String out_id, String fphm,
			String subject,String desc,String bar_code,String total_fee,String pay_toc,String mall_code,String shop_code,String url,String mkt) 
	{
		try 
		{
			//String rn = randomNum.generateString(4);
			//商户外部订单号
			JSONObject json = new JSONObject();
			json.put("method", "unifiedPay");
			json.put("operator_id", operator_id);
			json.put("out_id", out_id);
			json.put("receipt_no", fphm);
			json.put("subject", subject);
			json.put("desc", "二维码支付");
			json.put("bar_code", bar_code);
			json.put("total_fee", total_fee);
			json.put("pay_toc", pay_toc);
			json.put("mall_code", mall_code);
			json.put("shop_code", shop_code);
			json.put("mkt", mkt);
			
			String urlvalue = json.toString();
			
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
	public String advancePay(String operator_id, String out_id, String receipt_no,
			String subject,String desc,String total_fee,String pay_toc,String mall_code,String shop_code,String url,String mkt) 
	{
		try 
		{
			
			JSONObject json = new JSONObject();
			json.put("method", "advancePay");
			json.put("operator_id", operator_id);
			json.put("out_id", out_id);
			json.put("receipt_no", receipt_no);
			json.put("subject", subject);
			json.put("desc", desc);
			json.put("total_fee", total_fee);
			json.put("pay_toc", pay_toc);
			json.put("mall_code", mall_code);
			json.put("shop_code", shop_code);
			json.put("mkt", mkt);
			
			String urlvalue = json.toString();

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
	public String query(String out_id, String alipay_order_id,String operator_id,String url,String mkt) 
	{
		try 
		{
			if("".equals(alipay_order_id))
			{
				alipay_order_id ="000000000000";
			}
			JSONObject json = new JSONObject();
			json.put("method", "query");
			json.put("out_id", out_id);
			json.put("alipay_order_id", alipay_order_id);
			json.put("operator_id", operator_id);
			json.put("mkt", mkt);
			
			String urlvalue = json.toString();
			return ailHttp.HttpPostData(url, urlvalue);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;

	}
	
	
	public String queryA(String out_id, String alipay_order_id,String operator_id,String url) 
	{
		try 
		{
			JSONObject json = new JSONObject();
			json.put("method", "queryA");
			//json.put("out_id", out_id);
			json.put("alipay_order_id", alipay_order_id);
			json.put("operator_id", operator_id);
			
			String urlvalue = json.toString();
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
	public boolean sellBack(String out_id, String alipay_order_id,
			String operator_id, String out_refund_req_no,String refund_amount,String url,String mkt) 
	{ 
		try 
		{
			if("".equals(alipay_order_id))
			{
				alipay_order_id ="000000000000";
			}
			JSONObject json = new JSONObject();
			json.put("method", "sellBack");
			json.put("out_id", out_id);
			json.put("alipay_order_id", alipay_order_id);
			json.put("operator_id", operator_id);
			json.put("out_refund_req_no", out_refund_req_no);
			json.put("refund_amount", Double.parseDouble(refund_amount));
			json.put("mkt", mkt);
			
			String urlvalue = json.toString();
			
			String value = ailHttp.HttpPostData(url, urlvalue);
			JSONObject backjson = JSONObject.fromObject(value);
			if(backjson.getString("backFlag").equals("SUCCESS"))
			{
				return true;
			}
			
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
	public boolean sellcancel(String out_id, String alipay_order_id,
			String operator_id,String url,String mkt) {
		try 
		{
			if("".equals(alipay_order_id))
			{
				alipay_order_id ="000000000000";
			}
			JSONObject json = new JSONObject();
			json.put("method", "sellcancel");
			json.put("out_id", out_id);
			json.put("alipay_order_id", alipay_order_id);
			json.put("operator_id", operator_id);
			json.put("mkt", mkt);
			
			String urlvalue = json.toString();
			
			String value = ailHttp.HttpPostData(url, urlvalue);
			JSONObject js =JSONObject.fromObject(value);
			if("SUCCESS".equals(js.get("sellCancel")))
			{
				return true;
			}
		
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) 
	{
		AliPayOService AS= new AliPayOService();
		//System.out.println(AS.aliConfig(url));
	}

}
