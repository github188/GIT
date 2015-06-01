package bankpay.alipay.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import bankpay.alipay.tools.AilHttp;
import bankpay.alipay.tools.Md5Tools;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.GlobalVar;

public class SuNingPayService {
	private AilHttp aliHttp = new AilHttp();

	/**
	 * 组装二维码
	 * 
	 * @param saleNo
	 * @param amount
	 * @param title
	 * @param map
	 * @return 二维码里面的信息
	 */
	public String assemblyTdc(String saleNo, String amount, String title,
			HashMap<String, String> map) {
		String[] args = {
				"partner=" + map.get("partner"),
				"saleNo=" + saleNo,
				"amount="+ Long.toString((long)(ManipulatePrecision.doubleConvert(Double.parseDouble(amount) * 100))),
				"title=" + title, "shopid=" + map.get("shopid") };
		// 组装传入参数
		String data = assemblyData(args);
		// 二维码展示值
		String str = map.get("fiappurl") + "?" +"service=qrpay"+
				"&plat=o"+"&"+data;

		return str;
	}

	/**
	 * 查询
	 * 
	 * @param out_order_no
	 * @param map
	 * @return
	 */
	public HashMap<String, String> query(String out_order_no,
			HashMap<String, String> map) {
		String returnxml = "";
		try {
			String[] args = { "service=query_trade_order",
					"_input_charset=utf-8", "partner=" + map.get("partner"),
					"out_order_no=" + out_order_no };
			// 组装传入参数
			String data = assemblyData(args);
			// 组装签名
			Md5Tools getMD5 = new Md5Tools();
			String sign = getMD5.GetMD5Code(data+map.get("partnerkey"));
			// 拼接url
			String str = map.get("payurl") + "?" + data + "&sign=" + sign
					+ "&sign_type=md5";
			// 发起请求获得返回值
			returnxml = aliHttp.executeGet(str);
			return parseXml(returnxml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 退款
	 * @param out_order_no
	 * @param out_item_no
	 * @param return_amount
	 * @param operator
	 * @param return_order_no
	 * @param pos_order_no
	 * @param offset_order_no
	 * @param map
	 * @return
	 */
	public HashMap<String, String> backPay(String out_order_no,
			String out_item_no, String return_amount, String operator,
			String return_order_no, String pos_order_no,
			String offset_order_no, HashMap<String, String> map) {
		String returnxml = "";
		try {
			String[] args = {
					"service=return_trade_new",
					"partner=" + map.get("partner"),
					"_input_charset=utf-8",
					"out_order_no=" + out_order_no,
					"out_item_no=" + out_item_no,
					"return_amount="+ Long.toString((long)(ManipulatePrecision.doubleConvert(Double.parseDouble(return_amount) * 100))),
					"operator=" + operator,
					"return_order_no=" + return_order_no,
					"pos_order_no=" + pos_order_no,
					"offset_order_no=" + offset_order_no };
			// 组装传入参数
			String data = assemblyData(args);
			// 组装签名
			Md5Tools getMD5 = new Md5Tools();
			String sign = getMD5.GetMD5Code(data+map.get("partnerkey"));
			// 拼接url
			String str = map.get("payurl") + "?" + data + "&sign=" + sign
					+ "&sign_type=md5";
			// 发起请求获得返回值
			returnxml = aliHttp.executeGet(str);
			return parseXml(returnxml);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 循环排序
	 * 
	 * @param strs
	 * @return
	 */
	public String assemblyData(String[] strs) {
		Arrays.sort(strs);

		StringBuffer sb = new StringBuffer();
		for (int j = 0; j < strs.length; j++) {
			// System.out.println(ref[j]);
			if (j == 0) {
				sb.append(strs[j]);
			} else {
				sb.append("&");
				sb.append(strs[j]);
			}

		}
		return sb.toString();
	}

	public HashMap parseIni() {
		BufferedReader br = null;
		String configName = GlobalVar.ConfigPath + "//SuNingPayConfig.ini";
		String line = null;
		Map map = new HashMap();
		br = CommonMethod.readFile(configName);

		try {
			while ((line = br.readLine()) != null) {
				String[] row = line.split("=");
				if (row[0].equals("fiappurl")) {
					map.put("fiappurl", row[1]);
				} else if (row[0].equals("payurl")) {
					map.put("payurl", row[1]);
				} else if (row[0].equals("shopid")) {
					map.put("shopid", row[1]);
				} else if (row[0].equals("partner")) {
					map.put("partner", row[1]);
				} else if (row[0].equals("printFlag")) {
					map.put("printFlag", row[1]);
				} else if (row[0].equals("partnerkey")) {
					map.put("partnerkey", row[1]);
				}	
				
			}
			return (HashMap) map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 解析返回xml
	public static HashMap<String, String> parseXml(String xml) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document document = null;
		Element element = null;
		InputSource source = null;
		StringReader read = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			builder = factory.newDocumentBuilder();
			read = new StringReader(xml);
			source = new InputSource(read);
			document = builder.parse(source);
			element = document.getDocumentElement();

			NodeList nodes = element.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				map.put(nodes.item(i).getNodeName(), nodes.item(i)
						.getTextContent());
				// 获得response节点数据
				if ("response".equals(nodes.item(i).getNodeName())) {
					// 删除response节点数据
					map.remove("response");
					NodeList responsenodes = element
							.getElementsByTagName("response");
					if (null != responsenodes.item(0)) {
						Element responseElement = (Element) responsenodes
								.item(0);
						NodeList response = responseElement.getChildNodes();

						for (int r = 0; r < response.getLength(); r++) {
							map.put(response.item(r).getNodeName(), response
									.item(r).getTextContent());
							if ("items".equals(response.item(r).getNodeName())) {
								// 获得itmes节点下数据
								map.remove("items");
							}

						}
					}

				}
			}
			return (HashMap<String, String>) map;
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		String xml = "<suning>" + "<is_success>T</is_success>" + "<response>"
				+ "<out_order_no>BSP2014112700000046</out_order_no>"
				+ "<epp_order_no>2014112702911494</epp_order_no>"
				+ "<total_fee>2200</total_fee>"
				+ "<return_amount_allowed>2200</return_amount_allowed>"
				+ "<account_no>0000000000001117729</account_no>"
				+ "<consumer_name>杨慧</consumer_name>"
				+ "<pay_amount>2200</pay_amount>" + "<pay_channel/>"
				+ "<pay_channel_amount>0</pay_channel_amount>"
				+ "<status_code>01</status_code>" + "<status>支付完成</status>"
				+ "<return_status_code>00</return_status_code>"
				+ "<return_status>未退货</return_status>"
				+ "<create_time>2014-11-27 17:08:33</create_time>"
				+ "<last_update_time>2014-11-27 17:09:03</last_update_time>"
				+ "<pay_time>2014-11-27 17:09:03</pay_time>" + "<payment/>"
				+ "<payment_desc>易付宝支付</payment_desc>" + "<items>" + "<item>"
				+ "<out_item_no>20141127000000461</out_item_no>"
				+ "<amount>2200</amount>"
				+ "<company_code>768546214</company_code>"
				+ "<item_return_status>00</item_return_status>"
				+ "<item_return_allowed>2200</item_return_allowed>" + "</item>"
				+ "</items>" + "</response>" + "</suning>";

		System.out.println(parseXml(xml));
	}
}
