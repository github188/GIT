package bankpay.Payment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import bankpay.alipay.tools.Md5Tools;
import bankpay.alipay.tools.RandomNum;
import bankpay.alipay.tools.TcpUtil;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class MYAlipay_Payment extends Payment {
	private MessageBox codeMsg = null;
	private String text = "请确认支付宝支付是否成功 \n" + "按数字键1查询，按数字键2退出";
	private TcpUtil tcpUtil = new TcpUtil();
	// 生成随机数
	private RandomNum randomNum = new RandomNum();
	
	public MYAlipay_Payment() {
	}

	public MYAlipay_Payment(PayModeDef mode, SaleBS sale) {
		initPayment(mode, sale);
	}

	// 该构造函数用于红冲小票时,通过小票付款明细创建对象
	public MYAlipay_Payment(SalePayDef pay, SaleHeadDef head) {
		initPayment(pay, head);
	}

	public void initPayment(SalePayDef pay, SaleHeadDef head) {
		super.initPayment(pay, head);
	}

	public SalePayDef inputPay(String money)
	{	
	try 
	{
		if(!GlobalInfo.isOnline)
		{
			new MessageBox(Language.apply("请检查支付宝支付配置，并保证当前系统处于联网状态"));
			return null;
		}
		
		//System.out.println(saleBS.getPayBalanceLabel());
		//计算剩余金额
		money = saleBS.getPayBalanceLabel();
		
		Map mapN = new HashMap();
		Map<String, String> params = new HashMap();
		//获取请求地址
		mapN=ParseIni();
		String url = mapN.get("payUrl").toString();
		Integer port = Integer.parseInt(mapN.get("port").toString());
		String userid =mapN.get("userid").toString();
		String saasid =mapN.get("saasid").toString();
		String marketid =mapN.get("marketid").toString();
		//"0018866"
		String key = mapN.get("key").toString();
		String rn = randomNum.generateString(4);
		String outTradeNo = randomNum.getTime()+GlobalInfo.posLogin.gh+rn;
		

		if (SellType.ISSALE(saleBS.saletype))
		{
			int choice = -1;
			Vector v = new Vector();
			//组装MutiSelectForm显示数据
			
			v.add(new String[]{"0","条码支付"});
			//v.add(new String[]{"1","扫码支付"});
			v.add(new String[]{"1","查询支付"});
			
			String strmsg = Language.apply("请输入序号");
			MutiSelectForm msf = new MutiSelectForm();
			choice = msf.open(strmsg, new String[] { Language.apply("序号"), Language.apply("交易方式")}, new int[] { 80, 550}, v, true, 700, 400, 673, 285, false, false);
			//new MessageBox(Language.apply("金额"+money));
			if("".equals(String.valueOf(money))||null == String.valueOf(money))
			{
				new MessageBox(Language.apply("金额不能为空"));
				return null;
			}
			if(choice == -1)
			{
				return null;
			}
			
			//执行二维码支付
//			else if(choice == 1&&SellType.ISSALE(saleBS.saletype))
//			{
//				//传参
//				params.put("userid","001");
//				params.put("saasid", "miya");
//				params.put("marketid", "1");
//				params.put("paymentplatform", "2");
//				params.put("serveicetype", "1");
//				params.put("version", "1.0");
//				params.put("receipt_no", String.valueOf(salehead.fphm));
//				params.put("operator_id", salehead.syyh);
//				params.put("pay_toc", "2h");
//				params.put("total_fee", money);
//				params.put("subject", "扫码支付");
//				params.put("mall_code", "code1");
//				params.put("out_id", outTradeNo);
//				//组装签名
//				String signkey = getSign(params, key);
//				
//				//组装传入Xml
//				String xml = requestXml(params, signkey);
//				//获得返回xml
//				String returnXml = ailHttp.HttpPostData(url, xml);
//				
////				String bearXml = aliPayService.advancePay(partnerNo,partnerKey,""
////						,String.valueOf(money),url,extendParams.toString());
//				//获得阿里接口返回值
//				HashMap<String,String> map =parseXml(returnXml);
//				String content = map.get("qr_code").toString(); 
//				String out_trade_no = outTradeNo;
//				String text ="请扫二维码";
//				String msgString ="请确认二维码扫描是否完成?\n 按数字键1确认，按数字键2退出";
//				int msg = -1;
//				
//				if(null !=content&&!("").equals(content))
//				{
//
//					//查询交易是否成功
//					while(true)
//					{
//						//获取二维码
//						msg = QrcodeDisplay.display(content,text,10,msgString);
//						
//						if(msg == GlobalVar.Key1)
//						{
//							HashMap queryMap = parseXml.domParseXml(aliPayService.query(partnerNo,partnerKey,out_trade_no, "",url));
//							if(!"TRADE_SUCCESS".equals(queryMap.get("trade_status")))
//							{
//								StringBuffer msgStr = new StringBuffer();
//								msgStr.append("商户单号");
//								msgStr.append(out_trade_no);
//								msgStr.append("\n");
//								msgStr.append("并非交易成功状态");
//								msgStr.append("\n");
//								msgStr.append("按数字键1确认，按数字键2退出");
//								msgString = msgStr.toString();
//								continue;
//							}
//							new MessageBox(Language.apply("用户向支付宝支付完成"));
//							//给小票头的商户订单号赋值
//							if (!createSalePay(money)) return null;
//							salepay.payno = out_trade_no;
//							return salepay;
//						} 
//						else if(msg == GlobalVar.Key2)
//						{
//							return null;
//						}
//						else if(msg == 999)
//						{
//							new MessageBox(Language.apply("无法使用客显屏显示二维码，请联系管理员或者选着其他方式付款"));
//							return null;
//						}
//						
//					}
//					
//				}
//				else
//				{
//					new MessageBox(Language.apply("无法使用二维码，请联系管理员或者选着其他方式付款"));
//					return null;
//				}
				//二维码支付end
//			}
			//条码支付begin
			else if(choice == 0  && SellType.ISSALE(saleBS.saletype))
			{
				StringBuffer req = new StringBuffer();
				boolean done = new TextBox().open(Language.apply("请输入条码"), Language.apply("条码"), Language.apply("请扫描客户手机条码或者输入条码号"), req, 0, 0, false,
													TextBox.IntegerInput);
				if(done)
				{
					String tmCode = req.toString();
					//获得阿里接口返回值
					HashMap map =BarCodePay(userid, saasid, marketid, "3", "1", "1.0", outTradeNo, "条码支付", "BARCODE_PAY_OFFLINE", 
						(long)(ManipulatePrecision.doubleConvert(Double.parseDouble(money) * 100)), "barcode",tmCode, key, url,port);
					if(!"PAYSUCCESS".equals(map.get("trad_status")))
					{
						//查询交易是否成功
						while(true)
						{
							codeMsg = new MessageBox(text, null, true);
							
							if(codeMsg.verify() == GlobalVar.Key1)
							{
								HashMap queryMap = queryMap(userid, saasid, marketid, "3", "4", "1.0", outTradeNo, key, url,port);
								if(!"PAYSUCCESS".equals(queryMap.get("trad_status")))
								{
									StringBuffer msgStr = new StringBuffer();
									if(null == outTradeNo)
									{
										msgStr.append("无效商户单号");
										msgStr.append(outTradeNo);
										msgStr.append("\n");
									}
									else
									{
										msgStr.append("商户单号");
										msgStr.append(outTradeNo);
										msgStr.append("\n");
										msgStr.append("并非交易成功状态");
										msgStr.append("\n");
									}
									msgStr.append("按数字键1确认，按数字键2退出");
									text = msgStr.toString();
									continue;
								}
								new MessageBox(Language.apply("用户向支付宝支付完成"));
								//给小票头的商户订单号赋值
								if (!createSalePay(money)) return null;
								salepay.payno = outTradeNo;
								return salepay;
							}
							else if(codeMsg.verify() == GlobalVar.Key2)
							{
								return null;
							}
							
						}
					}
					else
					{
						//给小票头的商户订单号赋值
						if (!createSalePay(money)) return null;
						salepay.payno = outTradeNo;
						new MessageBox(Language.apply("用户向支付宝支付完成"));
						return salepay;
					}
				}
				else
				{
					return null;
				}
				//条码支付end
			}
			
			//查询支付或补单begin
			else if(choice == 1  && SellType.ISSALE(saleBS.saletype))
			{
				StringBuffer shdnh = new StringBuffer();
				boolean done = new TextBox().open(Language.apply("商户订单号"), Language.apply("商户订单号"), Language.apply("输入商户订单号"), shdnh, 0, 0, false,
													TextBox.IntegerInput);
				if(done)
				{
					//访问阿里查询接口
					HashMap queryMap = queryMap(userid, saasid, marketid, "3", "4", "1.0", shdnh.toString(), key, url,port);
					if(!"PAYSUCCESS".equals(queryMap.get("trad_status")))
					{
						if(null!=queryMap.get("query_desc"))
						{
							new MessageBox(Language.apply("该"+shdnh+"商户订单号"+queryMap.get("query_desc")));
							return null;
						}
						else
						{
							new MessageBox(Language.apply("该"+shdnh+"商户订单号"+"并非交易成功状态"));
							return null;
						}
					}
					else
					{
						if (!createSalePay(money)) return null;
						salepay.payno = shdnh.toString();
						new MessageBox(Language.apply("用户向支付宝支付完成"));
						return salepay;
					}
				}
				else
				{
					return null;
				}
			}
			//查询支付或补单end
		}
		//支付宝退货begin
		else if(SellType.ISBACK(saleBS.saletype))
		{ 
			//退货前线先查询交易单号的状态
			StringBuffer shdnh = new StringBuffer();
			boolean done = new TextBox().open(Language.apply("请输入商户订单号"), Language.apply("商户订单号"), Language.apply("请输入商户订单号"), shdnh, 0, 0, false,
												TextBox.IntegerInput);
			//访问阿里查询接口
			outTradeNo = shdnh.toString();
			if(done)
			{
				//访问阿里查询接口
				HashMap queryMap = queryMap(userid, saasid, marketid, "3", "4", "1.0", outTradeNo, key, url,port);
				//交易单号处于交易成功状态才允许退货
				if("PAYSUCCESS".equals(queryMap.get("trad_status")))
				{
					
					//退货接口
					HashMap map = BackPay(userid, saasid, marketid, "3", "2", "1.0", outTradeNo,(long)(ManipulatePrecision.doubleConvert(Double.parseDouble(money) * 100)), key, url,port);
					if(!"REFUNDSUCCESS".equals(map.get("trad_status")))
					{
						new MessageBox(Language.apply("该"+outTradeNo+"商户订单号"+"支付宝退款异常，请选择 其他方式退款"));
						return null;
					}
					else
					{
						new MessageBox(Language.apply("支付宝退款完成"));
						if (!createSalePay(money)) return null;
						salepay.payno = shdnh.toString();
						return salepay;
					}
				}
				else
				{
					new MessageBox(Language.apply("该"+outTradeNo+"商户订单号"+"并非交易成功状态无法退货请选择其他退货方式"));
					return null;
				}
			}
		}
		
		} 
		catch (Exception e) 
		{
			new MessageBox("支付宝交易异常");
			PosLog.getLog(getClass()).info(e);
			return null;
		}
		return null;

	}

	// 撤销
	public boolean cancelPay() {
		try {
			Map mapN = new HashMap();
			// 获取请求地址
			mapN=ParseIni();
			String url = mapN.get("payUrl").toString();
			Integer port = Integer.parseInt(mapN.get("port").toString());
			String userid =mapN.get("userid").toString();
			String saasid =mapN.get("saasid").toString();
			String marketid =mapN.get("marketid").toString();
			//"0018866"
			String key = mapN.get("key").toString();
		
			if (!GlobalInfo.isOnline) {
				new MessageBox(Language.apply("请检查支付宝支付配置，并保证当前系统处于联网状态"));
				return false;
			}
			// 访问阿里查询接口
			String outTradeNo = salepay.payno;
			if (true) {
				HashMap queryMap = queryMap(userid, saasid, marketid, "3", "4", "1.0", outTradeNo, key, url,port);
				// 交易单号处于交易成功状态才允许退货
				if ("PAYSUCCESS".equals(queryMap.get("trad_status"))) {
					HashMap map =  queryMap(userid, saasid, marketid, "3", "3", "1.0", outTradeNo, key, url,port);
					// 撤销接口
					if (!"CANCELSUCCESS".equals(map.get("trad_status"))) 
					{
						new MessageBox(Language.apply("该" + outTradeNo
								+ "商户订单号" + "支付宝退款异常，请选择 其他方式退款"));
						return false;
					}
					new MessageBox(Language.apply("支付宝撤销完成"));
					return true;
				} else {
					new MessageBox(Language.apply("该" + outTradeNo + "商户订单号"
							+ "并非交易成功状态无法退货请选择其他退货方式"));
					return false;
				}
			}
			return false;
		} catch (Exception e) {
			new MessageBox("支付宝退款异常");
			PosLog.getLog(getClass()).info(e);
			return false;

		}
	}

	public boolean collectAccountPay() {

		return true;
	}

	public boolean collectAccountClear() {
		// 删除相应的冲正记录
		return true;
	}

	// 组装传入XML
	public  String requestXml(Map<String, String> params, String signkey) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<xml>");
		sb.append("<request>");
		if(!"".equals(params.get("userid"))&&null!=params.get("userid"))
		{
			sb.append("<userid>");
			sb.append(params.get("userid").toString());
			sb.append("</userid>");
		}
		if(!"".equals(signkey)&&null!=signkey)
		{
			sb.append("<password>");
			sb.append(signkey.toUpperCase());
			sb.append("</password>");
		}
		if(!"".equals(params.get("saasid"))&&null!=params.get("saasid"))
		{
			sb.append("<saasid>");
			sb.append(params.get("saasid").toString());
			sb.append("</saasid>");
		}
		if(!"".equals(params.get("marketid"))&&null!=params.get("marketid"))
		{
			sb.append("<marketid>");
			sb.append(params.get("marketid").toString());
			sb.append("</marketid>");
		}
		if(!"".equals(params.get("paymentplatform"))&&null!=params.get("paymentplatform"))
		{
			sb.append("<paymentplatform>");
			sb.append(params.get("paymentplatform").toString());
			sb.append("</paymentplatform>");
		}
		if(!"".equals(params.get("serveicetype"))&&null!=params.get("serveicetype"))
		{
			sb.append("<serveicetype>");
			sb.append(params.get("serveicetype").toString());
			sb.append("</serveicetype>");
		}
		if(!"".equals(params.get("version"))&&null!=params.get("version"))
		{
			sb.append("<version>");
			sb.append(params.get("version").toString());
			sb.append("</version>");
		}
		
		sb.append("</request>");
		sb.append("<data>");
	
		if(!"".equals(params.get("out_trade_no"))&&null!=params.get("out_trade_no"))
		{
			sb.append("<out_trade_no>");
			sb.append(params.get("out_trade_no").toString());
			sb.append("</out_trade_no>");
		}
		if(!"".equals(params.get("total_fee"))&&null!=params.get("total_fee"))
		{
			sb.append("<total_fee>");
			sb.append(params.get("total_fee").toString());
			sb.append("</total_fee>");
		}
		if(!"".equals(params.get("subject"))&&null!=params.get("subject"))
		{
			sb.append("<subject>");
			sb.append(params.get("subject").toString());
			sb.append("</subject>");
		}
		
		if(!"".equals(params.get("dynamic_id_type"))&&null!=params.get("dynamic_id_type"))
		{
			sb.append("<dynamic_id_type>");
			sb.append(params.get("dynamic_id_type").toString());
			sb.append("</dynamic_id_type>");
		}
		
		if(!"".equals(params.get("dynamic_id"))&&null!=params.get("dynamic_id"))
		{
			sb.append("<dynamic_id>");
			sb.append(params.get("dynamic_id").toString());
			sb.append("</dynamic_id>");
		}
		
		if(!"".equals(params.get("product_code"))&&null!=params.get("product_code"))
		{
			sb.append("<product_code>");
			sb.append(params.get("product_code").toString());
			sb.append("</product_code>");
		}
		if(!"".equals(params.get("refund_amount"))&&null!=params.get("refund_amount"))
		{
			sb.append("<refund_amount>");
			sb.append(params.get("refund_amount").toString());
			sb.append("</refund_amount>");
		}
		
		sb.append("</data>");
		sb.append("</xml>");
		return sb.toString();
	}
	
	//组装签名
	public String getSign(Map<String, String> params, String signkey) {
		String[] keyArray = (String[]) params.keySet().toArray(new String[0]);
		StringBuffer targetSign = new StringBuffer();
		targetSign.append("&");
		Arrays.sort(keyArray);
		for (int i = 0; i < keyArray.length; ++i) {
			String key = keyArray[i];
			targetSign.append(key);
			targetSign.append("=");
			targetSign.append((String) params.get(key));
			if (i != keyArray.length - 1)
				targetSign.append("&");
		}
		targetSign.append("&key=" + signkey);
		try {
			Md5Tools md5 = new Md5Tools();
			//byte[] bytes = targetSign.toString().getBytes("utf-8");
			return md5.GetMD5Code(targetSign.toString());
		} catch (Exception ex) {
		}
		return "";
	}
	
	//解析返回xml
	public HashMap<String,String> parseXml(String xml)
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
		DocumentBuilder builder = null;  
		Document document = null;
		Element element = null;
		InputSource source = null;
		StringReader read = null;
		Map <String,String>map = new HashMap<String,String>();
		try {
			builder = factory.newDocumentBuilder();
			read = new StringReader(xml);
            source = new InputSource(read);
			document = builder.parse(source);
			element = document.getDocumentElement();
			
			NodeList AilErrNodes = element.getChildNodes();
			for(int i =0 ; i<AilErrNodes.getLength();i++)
			{
				map.put(AilErrNodes.item(i).getNodeName(), AilErrNodes.item(i).getTextContent());
			}
			return (HashMap<String,String>) map;
		}catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	   return null;
	}
	
	public HashMap ParseIni()
	{
		BufferedReader br = null;
		String configName = GlobalVar.ConfigPath + "//MyPayConfig.ini";
		String line = null;
		Map map = new HashMap();
		br = CommonMethod.readFile(configName);
		
		try 
		{
			while ((line = br.readLine()) != null)
			{
				String[] row = line.split("=");
				if(row[0].equals("payUrl"))
				{
					map.put("payUrl", row[1]);
				}
				if(row[0].equals("port"))
				{
					map.put("port", row[1]);
				}
				if(row[0].equals("userid"))
				{
					map.put("userid", row[1]);
				}
				if(row[0].equals("saasid"))
				{
					map.put("saasid", row[1]);
				}
				if(row[0].equals("marketid"))
				{
					map.put("marketid", row[1]);
				}
				if(row[0].equals("key"))
				{
					map.put("key", row[1]);
				}
			}
			return (HashMap) map;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	
    /**
     * 查询接口和撤销
     * @param userid
     * @param saasid
     * @param marketid
     * @param paymentplatform
     * @param serveicetype
     * @param version
     * @param out_trade_no
     * @param key
     * @param url
     * @return
     */
	public HashMap<String, String> queryMap(String userid,String saasid,String marketid,
			String paymentplatform,String serveicetype,String version,String out_trade_no
			,String key,String url,Integer port)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("userid",userid);
		params.put("saasid", saasid);
		params.put("marketid", marketid);
		params.put("paymentplatform",paymentplatform);
		params.put("serveicetype", serveicetype);
		params.put("version", version);
		params.put("out_trade_no", out_trade_no);
		
		String signkey = getSign(params, key);
		
		String xml = requestXml(params, signkey);
		
		String returnXml = null;
		try {
			returnXml = new String(tcpUtil.send(url, port, xml.getBytes("gbk")),"gbk");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//String returnXml = ailHttp.HttpPostData(url, xml);
		
		return parseXml(returnXml);
	}
	
	/**
	 * 统一下单并支付
	 * @param userid
	 * @param saasid
	 * @param marketid
	 * @param paymentplatform
	 * @param serveicetype
	 * @param version
	 * @param operator_id
	 * @param out_trade_no
	 * @param subject
	 * @param product_code
	 * @param total_fee
	 * @param dynamic_id_type
	 * @param dynamic_id
	 * @param key
	 * @param url
	 * @return
	 */
	public HashMap<String, String>  BarCodePay(String userid,String saasid,String marketid,String paymentplatform
			,String serveicetype,String version,String out_trade_no,String subject,
			String product_code,Long total_fee,String dynamic_id_type,String dynamic_id
			,String key,String url,Integer port)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("userid",userid);
		params.put("saasid", saasid);
		params.put("marketid", marketid);
		params.put("paymentplatform",paymentplatform);
		params.put("serveicetype", serveicetype);
		params.put("version", version);
		
		params.put("out_trade_no", out_trade_no);
		params.put("subject", subject);
		params.put("product_code", product_code);
		params.put("total_fee", total_fee.toString());
		params.put("dynamic_id_type", dynamic_id_type);
		params.put("dynamic_id", dynamic_id);
		
		String signkey = getSign(params, key);
		
		String xml = requestXml(params, signkey);
		String returnXml = null;
		try {
			returnXml = new String(tcpUtil.send(url, port, xml.getBytes("gbk")),"gbk");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		//String returnXml = ailHttp.HttpPostData(url, xml);
		
		return parseXml(returnXml);
	}
	
	/**退款
	 * 
	 * @param userid
	 * @param saasid
	 * @param marketid
	 * @param paymentplatform
	 * @param serveicetype
	 * @param version
	 * @param operator_id
	 * @param out_trade_no
	 * @param refund_amount
	 * @param key
	 * @param url
	 * @return
	 */
	public HashMap<String, String> BackPay(String userid,String saasid,String marketid,String paymentplatform
			,String serveicetype,String version,String out_trade_no,Long refund_amount,String key,String url,Integer port)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("userid",userid);
		params.put("saasid", saasid);
		params.put("marketid", marketid);
		params.put("paymentplatform",paymentplatform);
		params.put("serveicetype", serveicetype);
		params.put("version", version);
		
		params.put("out_trade_no", out_trade_no);
		params.put("refund_amount", refund_amount.toString());
	
		
		String signkey = getSign(params, key);
		
		String xml = requestXml(params, signkey);
		
		String returnXml = null;
		try {
			returnXml = new String(tcpUtil.send(url, port, xml.getBytes("gbk")),"gbk");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//String returnXml = ailHttp.HttpPostData(url, xml);
		
		return parseXml(returnXml);
	}
	
	
	public static void main(String [] args)
	{
		
	}

}
