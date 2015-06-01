package bankpay.Payment;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import net.sf.json.JSONObject;
import bankpay.alipay.service.AliPayOService;
import bankpay.alipay.tools.AliPrintMode;
import bankpay.alipay.tools.ParseIni;
import bankpay.alipay.tools.ParseXml;
import bankpay.alipay.tools.RandomNum;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.QrcodeDisplay;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
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

public class AlipayO_Payment extends Payment {
	private AliPayOService aliPayOService = new AliPayOService();
	private ParseXml parseXml = new ParseXml();
	private ParseIni parseIni = new ParseIni();
	private MessageBox codeMsg = null;
	private ProgressBox soundBox = null;
	// private static SonicWave sonicWave = new SonicWave();
	private String text = "请确认支付宝支付是否成功 \n" + "按数字键1查询，按数字键2退出";
	private String text2 = "请确认支付宝退货是否成功 \n" + "按数字键1查询，按数字键2退出";
	private String aliConfigUrl = "http://127.0.0.1:8080/JAVAPAYSERVER/AliPayOServlet";

	private RandomNum randomNum = new RandomNum();
	
	private AliPrintMode apm = new AliPrintMode();
	
	private String queryFlag ="";
	private JSONObject json = null;
	private int choice = -1;

	public AlipayO_Payment() {
	}

	public AlipayO_Payment(PayModeDef mode, SaleBS sale) {
		initPayment(mode, sale);
	}

	// 该构造函数用于红冲小票时,通过小票付款明细创建对象
	public AlipayO_Payment(SalePayDef pay, SaleHeadDef head) {
		initPayment(pay, head);
	}

	public void initPayment(SalePayDef pay, SaleHeadDef head) {
		super.initPayment(pay, head);
	}

	public SalePayDef inputPay(String money) {
		try {

			NetService netService = (NetService) NetService.getDefault();

			Map mapN = new HashMap();
			// 获取请求地址
			mapN = parseIni.Parse();
			String url = mapN.get("aliPayUrl").toString();
			//打印机个数判断标识
			String printFlag = mapN.get("printFlag").toString();
//			if (!GlobalInfo.isOnline) {
//				new MessageBox(Language.apply("请检查支付宝支付配置，并保证当前系统处于联网状态"));
//				return null;
//			}
			if (SellType.ISSALE(saleBS.saletype)) 
			{
				Vector v = new Vector();
				// 组装MutiSelectForm显示数据

				v.add(new String[] { "0", "条码支付" });
				v.add(new String[] { "1", "扫码支付" });
				v.add(new String[] { "2", "查询支付" });

				String strmsg = Language.apply("请输入序号");

				String rn = randomNum.generateString(4);
				// 商户外部订单号
				String outTradeNo = randomNum.getTime()
						+ GlobalInfo.posLogin.gh + rn;
				MutiSelectForm msf = new MutiSelectForm();
				choice = msf.open(strmsg, new String[] { Language.apply("序号"),
						Language.apply("交易方式") }, new int[] { 80, 550 }, v,
						true, 700, 400, 673, 285, false, false);
				if ("".equals(String.valueOf(money))
						|| null == String.valueOf(money)) {
					new MessageBox(Language.apply("金额不能为空"));
					return null;
				}
				if (choice == -1) {
					return null;
				}

				// 执行二维码支付
				else if (choice == 1 && SellType.ISSALE(saleBS.saletype)) {
					String content = aliPayOService.advancePay(
							saleBS.saleHead.syyh, outTradeNo,
							String.valueOf(saleBS.saleHead.fphm), GlobalInfo.sysPara.mktname,
							"二维码支付", money, "2d", saleBS.saleHead.mkt,
							saleBS.saleHead.syjh, url,saleBS.saleHead.mkt);

					// 获得阿里接口返回值
					String text = "请扫二维码";
					String msgString = "请确认二维码扫描是否完成?\n 按数字键1确认，按数字键2退出";
					int msg = -1;

					if (null != content && !("").equals(content)) {

						// 查询交易是否成功
						while (true) {
							// 获取二维码
							msg = QrcodeDisplay.display(content, text, 10,
									msgString);
							if (msg == GlobalVar.Key1) {
							    queryFlag = aliPayOService.query(
										outTradeNo, "", saleBS.saleHead.syyh,
										url,saleBS.saleHead.mkt);
								json = JSONObject.fromObject(queryFlag);
								if (!"TRADE_SUCCESS".equals(json.get("pay_status"))) {
									StringBuffer msgStr = new StringBuffer();
									msgStr.append("商户单号");
									msgStr.append(outTradeNo);
									msgStr.append("\n");
									msgStr.append("付款失败");
									msgStr.append("\n");
									msgStr.append("按数字键1确认，按数字键2退出");
									msgString = msgStr.toString();
									continue;
								}
								new MessageBox(Language.apply("用户向支付宝支付完成"));
								// 给小票头的商户订单号赋值
								if (!createSalePay(money))
									return null;
								// 商户订单号
								salepay.payno = outTradeNo;
								// 付款明细
								salepay.memo = json.getString("pay_channel");
								salepay.batch = "ALISALE";
								// 打印签购单
								if(!apm.aliPrint(json,salehead,saleBS.saleGoods,saleBS.salePayment,printFlag))
								{
									new MessageBox(Language.apply("打印签购单失败,交易完成后尝试重打印"));
									//return null;
								}
								return salepay;
							} else if (msg == GlobalVar.Key2) {
								return null;
							} 
							//0000代表二维码生成失败
							else if (msg == 0000) {
								new MessageBox(Language.apply("无法使用客显屏显示二维码，请联系管理员或者选着其他方式付款"));
								return null;
							}

						}

					} else 
					{
						new MessageBox(Language.apply("无法使用二维码，请联系管理员或者选着其他方式付款"));
						return null;
					}
					// 二维码支付end
				}
				// 条码支付begin
				else if (choice == 0 && SellType.ISSALE(saleBS.saletype)) 
				{
					StringBuffer req = new StringBuffer();
					boolean done = new TextBox().open(Language.apply("请输入条码"),
							Language.apply("条码"),
							Language.apply("请扫描客户手机条码或者输入条码号"), req, 0, 0,
							false, TextBox.AliPayInput);
					if (done) 
					{
						String tmCode = req.toString();
						String unifiedPayFlag = aliPayOService.unifiedPay(
								saleBS.saleHead.syyh, outTradeNo,
								String.valueOf(saleBS.saleHead.fphm), GlobalInfo.sysPara.mktname,
								"条码支付", req.toString(), money, "2d",
								saleBS.saleHead.mkt, saleBS.saleHead.syjh, url,saleBS.saleHead.mkt);
						// 获得阿里接口返回值
						if (!"SUCCESS".equals(unifiedPayFlag))
						{
							// 查询交易是否成功
							while (true) 
							{
								codeMsg = new MessageBox(text, null, true);

								if (codeMsg.verify() == GlobalVar.Key1) 
								{
									// 查询
									queryFlag = aliPayOService.query(outTradeNo, "",saleBS.saleHead.syyh, url,saleBS.saleHead.mkt);
									json = JSONObject.fromObject(queryFlag);
									if (!"TRADE_SUCCESS".equals(json.get("pay_status"))) 
									{
										StringBuffer msgStr = new StringBuffer();
										if (null == outTradeNo)
										{
											msgStr.append("无效商户单号");
											msgStr.append(tmCode);
											msgStr.append("\n");
										} else 
										{
											msgStr.append("商户单号");
											msgStr.append(outTradeNo);
											msgStr.append("\n");
											msgStr.append("付款失败");
											msgStr.append("\n");
										}
										msgStr.append("按数字键1确认，按数字键2退出");
										text = msgStr.toString();
										continue;
									}
									new MessageBox(Language.apply("用户向支付宝支付完成"));

									// 给小票头的商户订单号赋值
									if (!createSalePay(money))
										return null;
									salepay.payno = outTradeNo;
									salepay.memo = json.getString("pay_channel");
									salepay.batch = "ALISALE";

									// 打印签购单
									if(!apm.aliPrint(json,salehead,saleBS.saleGoods,saleBS.salePayment,printFlag))
									{
										new MessageBox(Language.apply("打印签购单失败,交易完成后尝试重打印"));
										//return null;
									}
									return salepay;
								} else if (codeMsg.verify() == GlobalVar.Key2) {
									return null;
								}

							}
						} else 
						{
							// 给小票头的商户订单号赋值
							if (!createSalePay(money))
								return null;
							queryFlag = aliPayOService.query(outTradeNo, "",saleBS.saleHead.syyh, url,saleBS.saleHead.mkt);
							json = JSONObject.fromObject(queryFlag);
							salepay.payno = outTradeNo;
							salepay.memo=json.getString("pay_channel");
							
							if(!apm.aliPrint(json,salehead,saleBS.saleGoods,saleBS.salePayment,printFlag))
							{
								new MessageBox(Language.apply("打印签购单失败,交易完成后尝试重打印"));
								//return null;
							}
							salepay.batch = "ALISALE";
							new MessageBox(Language.apply("用户向支付宝支付完成"));
							return salepay;
						}
							
					}
				} // 条码支付end
				// 查询支付或补单begin
				else if (choice == 2 && SellType.ISSALE(saleBS.saletype)) 
				{
					StringBuffer shdnh = new StringBuffer();
					boolean done = new TextBox().open(Language.apply("商户订单号"),
							Language.apply("商户订单号"), Language.apply("输入商户订单号"),
							shdnh, 0, 0, false, TextBox.IntegerInput);
					// 查询订单是否已存在
					boolean selectFlag = netService.selectOutId(shdnh.toString());

					if (done && selectFlag) 
					{
						// 访问阿里查询接口
						queryFlag = aliPayOService.query(shdnh.toString(), "",saleBS.saleHead.syyh, url,saleBS.saleHead.mkt);
						json = JSONObject.fromObject(queryFlag);
			
						//查询不成功或者非交易支付成功状态不让补单
						if (!"SUCCESS".equals(json.get("pay_flag"))||!"TRADE_SUCCESS".equals(json.get("pay_status"))) 
						{
							new MessageBox(Language.apply("该" + shdnh+ "商户单号" + "补单失败"));
						   return null;
						}
						else if((json.getDouble("total_fee")/100)!=Double.parseDouble(money))
						{
							new MessageBox(Language.apply("金额不一致"));
							return null;
						}
						else 
						{
							if (!createSalePay(money))
								return null;
							salepay.payno = shdnh.toString();
							salepay.memo = json.getString("pay_channel");
							new MessageBox(Language.apply("用户向支付宝支付完成"));
							// 打印签购单
							if(!apm.aliPrint(json,salehead,saleBS.saleGoods,saleBS.salePayment,printFlag))
							{
							   new MessageBox(Language.apply("打印签购单失败,交易完成后尝试重打印"));
								//return null;
							}
							salepay.batch = "ALISALE";
							
							return salepay;
						}
					} //查询支付或补单end
					else 
					{
						return null;
					}
				
				}

			}
			
			// 支付宝退货begin
			else if (SellType.ISBACK(saleBS.saletype)||SellType.ISHC(saleBS.saletype)) 
			{
				// 退货前线先查询交易单号的状态
				String outTradeNo = "";
				StringBuffer shdnh = new StringBuffer();
				boolean done = new TextBox().open(Language.apply("请输入商户订单号"),
						Language.apply("商户订单号"), Language.apply("请输入商户订单号"),
						shdnh, 0, 0, false, TextBox.IntegerInput);
				outTradeNo = shdnh.toString();
				
				if (done) 
				{
					// 访问阿里查询接口
					queryFlag = aliPayOService.query(shdnh.toString(),
							"", saleBS.saleHead.syyh, url,saleBS.saleHead.mkt);
					json = JSONObject.fromObject(queryFlag);
					String pay_channel = json.getString("pay_channel");
					
					// 交易单号处于交易成功状态才允许退货
					if ("TRADE_SUCCESS".equals(json.get("pay_status"))) 
					{
						//pay_channel = json.getString("pay_channel");
						String backrn = randomNum.generateString(4);
						String backoutTradeNo = randomNum.getTime()
								+ GlobalInfo.posLogin.gh + backrn;
						// 退货接口
						if (!aliPayOService.sellBack(shdnh.toString(), "",
								saleBS.saleHead.syyh, backoutTradeNo, money,url,saleBS.saleHead.mkt)) 
						{
							while (true) 
							{
								codeMsg = new MessageBox(text2, null, true);
								if (codeMsg.verify() == GlobalVar.Key1)
								{
									// 查询
									queryFlag = aliPayOService.query(outTradeNo, "", salehead.syyh, url,saleBS.saleHead.mkt);
									json = JSONObject.fromObject(queryFlag);
									if (!"TRADE_CLOSED".equals(json.get("pay_status"))&& !"TRADE_FINISH".equals(json.get("pay_status"))) 
									{
										StringBuffer msgStr = new StringBuffer();
										msgStr.append("商户单号");
										msgStr.append(outTradeNo);
										msgStr.append("\n");
										msgStr.append("支付宝退款失败");
										msgStr.append("\n");
										msgStr.append("按数字键1确认，按数字键2退出");
										text = msgStr.toString();
										continue;
									} 
									else 
									{
										new MessageBox(Language.apply("支付宝退款完成"));
										if (!createSalePay(money))return null;
										salepay.payno = outTradeNo;
										salepay.memo = pay_channel;
										//打印签购单
										if(!apm.aliPrint(json,salehead,saleBS.saleGoods,saleBS.salePayment,printFlag))
										{
											new MessageBox(Language.apply("打印签购单失败"));
											salepay.batch = "ALIPRINTFC";
											return null;
										}
										salepay.batch = "ALISALEBCK";
										return salepay;
									}
								}
								else
								{
									return null;
								}
							}
						}
						else 
						{
							new MessageBox(Language.apply("支付宝退款完成"));
							if (!createSalePay(money))return null;
							salepay.payno = outTradeNo;
							salepay.memo = pay_channel;
							//打印签购单
							if(!apm.aliPrint(json,salehead,saleBS.saleGoods,saleBS.salePayment,printFlag))
							{
								new MessageBox(Language.apply("打印签购单失败"));
								salepay.batch = "ALIPRINTFC";
								return null;
							}
							salepay.batch = "ALISALEBCK";
							
							return salepay;
						}
					}
					else if("ALIPRINTFC".equals(salepay.batch )) 
					{
						salepay.payno = outTradeNo;
						salepay.memo = pay_channel;
						if(!apm.aliPrint(json,salehead,saleBS.saleGoods,saleBS.salePayment,printFlag))
						{
							new MessageBox(Language.apply("打印签购单失败"));
							salepay.batch = "ALIPRINTFC";
							return null;
						}
						salepay.batch = "ALISALEBCK";
						
						return salepay;
					}
					else {
						new MessageBox(Language.apply("该" + outTradeNo + "商户订单号" + "并非交易成功状态无法退货请选择其他退货方式"));
						return null;
		 			}
				}
			}

		} catch (Exception e) {
			new MessageBox("支付宝交易异常");
			PosLog.getLog(getClass()).info("支付宝退款异常"+e);
			return null;
		}
		return null;

	}

	// 撤销
	public boolean cancelPay() {
		try 
		{
			Map mapN = new HashMap();
			// 获取请求地址
			mapN = parseIni.Parse();
			String url = mapN.get("aliPayUrl").toString();
			if (!GlobalInfo.isOnline) {
				new MessageBox(Language.apply("请检查支付宝支付配置，并保证当前系统处于联网状态"));
				return false;
			}
			// 访问阿里查询接口
			String outTradeNo = salepay.payno;
			String backrn = randomNum.generateString(4);
			String backoutTradeNo = randomNum.getTime()
					+ GlobalInfo.posLogin.gh + backrn;
			if (true) 
			{
				// 访问阿里查询接口
				queryFlag = aliPayOService.query(outTradeNo, "",
						saleBS.saleHead.syyh, url,saleBS.saleHead.mkt);
				json = JSONObject.fromObject(queryFlag);
				 if ("TRADE_SUCCESS".equals(json.get("pay_status"))) {
					// 撤销接口
//					if (!aliPayOService.sellcancel(outTradeNo,
//							json.getString("alipay_trade_no"),
//							saleBS.saleHead.syyh, url)) {
					if(!aliPayOService.sellBack(outTradeNo,"",
							saleBS.saleHead.syyh, backoutTradeNo, 
							String.valueOf(salepay.je), url,saleBS.saleHead.mkt))
					{
						queryFlag = aliPayOService.query(outTradeNo, "",saleBS.saleHead.syyh, url,saleBS.saleHead.mkt);
						json = JSONObject.fromObject(queryFlag);
						if (!"TRADE_CLOSED".equals(json.get("pay_status"))&& !"TRADE_FINISH".equals(json.get("pay_status"))) 
						{
							new MessageBox(Language.apply("该" + outTradeNo
									+ "商户订单号" + "支付宝退款异常，请选择 其他方式退款"));
							return false;
						}
						new MessageBox(Language.apply("支付宝撤销完成"));
						salepay.batch = "ALISALEBCK";
						return true;
						
					}
					new MessageBox(Language.apply("支付宝撤销完成"));
					salepay.batch = "ALISALEBCK";
					return true;
				} else {
					new MessageBox(Language.apply("该" + outTradeNo + "商户订单号"
							+ "并非交易成功状态无法退货请选择其他退货方式"));
					return false;
				}
			}
			return false;
		} catch (Exception e) {
			new MessageBox("支付宝撤销异常");
			PosLog.getLog(getClass()).info(e);
			return false;

		}
	}

	public boolean collectAccountPay() {
     try {
		Map mapN = new HashMap();
		// 获取请求地址
		mapN = parseIni.Parse();
		String url = mapN.get("aliPayUrl").toString();
		String printFlag = mapN.get("printFlag").toString();
		String outTradeNo = salepay.payno;

		String backrn = randomNum.generateString(4);
		String backoutTradeNo = randomNum.getTime() + GlobalInfo.posLogin.gh+ backrn;
		queryFlag = aliPayOService.query(outTradeNo, "",salehead.syyh, url,salehead.mkt);
		json = JSONObject.fromObject(queryFlag);
		
		//ALISALEBCK代表已退货   ALISALE代表交易成功// 退货前先查询交易单号的状态//交易状态为成功时才允许退货 //2红冲  4 退货
		if ((SellType.ISBACK(salehead.djlb)||SellType.ISHC(salehead.djlb))&& 
				("ALISALE".equals(salepay.batch)||"TRADE_SUCCESS".equals(json.get("pay_status")))) 
		{
			//退货
			if (!aliPayOService.sellBack(outTradeNo, "", salehead.syyh,
					backoutTradeNo, String.valueOf(salepay.je), url,salehead.mkt)) 
			{
			 	while (true) 
				{
					codeMsg = new MessageBox(text2, null, true);
					if (codeMsg.verify() == GlobalVar.Key1) 
					{
						// 查询
						queryFlag = aliPayOService.query(outTradeNo, "",salehead.syyh, url,salehead.mkt);
						json = JSONObject.fromObject(queryFlag);
						if (!"TRADE_CLOSED".equals(json.get("pay_status"))) 
						{
							StringBuffer msgStr = new StringBuffer();
							msgStr.append("商户单号");
							msgStr.append(outTradeNo);
							msgStr.append("\n");
							msgStr.append("支付宝退货失败");
							msgStr.append("\n");
							msgStr.append("按数字键1确认，按数字键2退出");
							text = msgStr.toString();
							continue;
						} 
						else 
						{
							new MessageBox(Language.apply("支付宝退货成功"));
							//打印签购单
							if(!apm.aliPrint(json,salehead,null,null,printFlag))
							{
								new MessageBox(Language.apply("打印签购单失败"));
								salepay.batch = "ALIPRINTF";
								return false;
							}
							salepay.batch = "ALISALEBCK";
							return true;
						}
					} else 
					{
						new MessageBox(Language.apply("支付宝退货失败"));
						return false;
					}
				}
			} else 
			{
				new MessageBox(Language.apply("支付宝退货成功"));
				queryFlag = aliPayOService.query(salepay.payno, "",salehead.syyh, url,salehead.mkt);
				json = JSONObject.fromObject(queryFlag);
				//打印签购单
				if(!apm.aliPrint(json,salehead,null,null,printFlag))
				{
					new MessageBox(Language.apply("打印签购单失败"));
					salepay.batch = "ALIPRINTF";
					return false;
				}
				salepay.batch = "ALISALEBCK";
				return true;
			}
		}
		//处理退货成功 签购单没打印的情况
		else if ("ALIPRINTF".equals(salepay.batch)&&"TRADE_CLOSED".equals(json.get("pay_status")))
		{
			if(!apm.aliPrint(json,salehead,null,null,printFlag))
			{
				new MessageBox(Language.apply("打印签购单失败"));
				salepay.batch = "ALIPRINTF";
				return false;
			}
			else
			{
				salepay.batch = "ALISALEBCK";
				return true;
			}
		}
			return true;
		} 
		catch (Exception e) 
		{
			new MessageBox("支付宝退款异常");
			PosLog.getLog(getClass()).info(e);
			return false;
		}
	}

	public boolean collectAccountClear() {
		// 删除相应的冲正记录
		return true;
	}

	

}
