package bankpay.Payment;

import java.util.HashMap;
import java.util.Vector;
import bankpay.alipay.service.SuNingPayService;
import bankpay.alipay.tools.RandomNum;
import bankpay.alipay.tools.SuNingPrintMode;
import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
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

public class SuNingPay_Payment extends Payment {
	private MessageBox codeMsg = null;
	private SuNingPayService sn = new SuNingPayService();
	// private static SonicWave sonicWave = new SonicWave();
	private String text = "请确认易付宝支付是否成功 \n" + "按数字键1查询，按数字键2退出";
	private String text2 = "请确认易付宝退货是否成功 \n" + "按数字键1查询，按数字键2退出";

	private RandomNum randomNum = new RandomNum();

	private SuNingPrintMode spm = new SuNingPrintMode();

	private HashMap<String, String> queryFlag;
	private int choice = -1;

	public SuNingPay_Payment() {
	}

	public SuNingPay_Payment(PayModeDef mode, SaleBS sale) {
		initPayment(mode, sale);
	}

	// 该构造函数用于红冲小票时,通过小票付款明细创建对象
	public SuNingPay_Payment(SalePayDef pay, SaleHeadDef head) {
		initPayment(pay, head);
	}

	public void initPayment(SalePayDef pay, SaleHeadDef head) {
		super.initPayment(pay, head);
	}

	public SalePayDef inputPay(String money) {
		try {

			NetService netService = (NetService) NetService.getDefault();

			HashMap<String, String> mapN = new HashMap<String, String>();
			// 获取请求地址
			mapN = sn.parseIni();
			// 打印机个数判断标识
			String printFlag = mapN.get("printFlag").toString();
			if (!GlobalInfo.isOnline) {
				new MessageBox(Language.apply("请检查易付宝支付配置，并保证当前系统处于联网状态"));
				return null;
			}
			if (SellType.ISSALE(saleBS.saletype)) {
				Vector v = new Vector();
				// 组装MutiSelectForm显示数据

				// v.add(new String[] { "0", "条码支付" });
				v.add(new String[] { "0", "扫码支付" });
				v.add(new String[] { "1", "查询支付" });

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
				else if (choice == 0 && SellType.ISSALE(saleBS.saletype)) {
					String content = sn.assemblyTdc(outTradeNo, money,
							GlobalInfo.sysPara.mktname, mapN);
					System.out.println("二维码URL======》" + content);

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
								queryFlag = sn.query(outTradeNo, mapN);
								if (!"01".equals(queryFlag.get("status_code")))
								{
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
								new MessageBox(Language.apply("用户向易付宝支付完成"));
								// 给小票头的商户订单号赋值
								if (!createSalePay(money))
									return null;
								// 商户订单号
								salepay.payno = outTradeNo;
								// 付款明细
								salepay.memo = queryFlag.get("pay_channel");
								salepay.batch = "SUNINGSALE";
								// 打印签购单
								if (!spm.suNingPrint(queryFlag, salehead,
										saleBS.saleGoods, saleBS.salePayment,
										printFlag)) {
									new MessageBox(
											Language.apply("打印签购单失败,交易完成后尝试重打印"));
									// return null;
								}
								return salepay;
							} else if (msg == GlobalVar.Key2) {
								return null;
							}
							// 0000代表二维码生成失败
							else if (msg == 0000) {
								new MessageBox(
										Language.apply("无法使用客显屏显示二维码，请联系管理员或者选着其他方式付款"));
								return null;
							}

						}

					} else {
						new MessageBox(
								Language.apply("无法使用二维码，请联系管理员或者选着其他方式付款"));
						return null;
					}
					// 二维码支付end
				}
				// 查询支付或补单begin
				else if (choice == 1 && SellType.ISSALE(saleBS.saletype)) {
					StringBuffer shdnh = new StringBuffer();
					boolean done = new TextBox().open(Language.apply("商户订单号"),
							Language.apply("商户订单号"), Language.apply("输入商户订单号"),
							shdnh, 0, 0, false, TextBox.IntegerInput);
					// 查询订单是否已存在
					boolean selectFlag = netService.selectOutId(shdnh
							.toString());

					if (done && selectFlag) {
						// 访问阿里查询接口
						queryFlag = sn.query(shdnh.toString(), mapN);

						// 查询不成功或者非交易支付成功状态不让补单
						if (!"01".equals(queryFlag.get("status_code"))) 
						{
							new MessageBox(Language.apply("该" + shdnh + "商户单号"
									+ "补单失败"));
							return null;
						} 
						else if (!"00".equals(queryFlag.get("return_status_code"))) {
							new MessageBox(Language.apply("该" + shdnh + "商户单号"
									+ "补单失败"));
							return null;
						}
						else if (ManipulatePrecision.doubleConvert(Double
								.parseDouble(queryFlag.get("total_fee")) / 100) != Double
								.parseDouble(money)) {
							new MessageBox(Language.apply("金额不一致"));
							return null;
						} else {
							if (!createSalePay(money))
								return null;
							salepay.payno = shdnh.toString();
							salepay.memo = queryFlag.get("pay_channel");
							new MessageBox(Language.apply("用户向易付宝支付完成"));
							// 打印签购单
							if (!spm.suNingPrint(queryFlag, salehead,
									saleBS.saleGoods, saleBS.salePayment,
									printFlag)) {
								new MessageBox(
										Language.apply("打印签购单失败,交易完成后尝试重打印"));
								// return null;
							}
							salepay.batch = "SUNINGSALE";

							return salepay;
						}
					} else {
						return null;
					}
				}// 查询支付或补单end
			}

			// 易付宝退货begin
			else if (SellType.ISBACK(saleBS.saletype)|| SellType.ISHC(saleBS.saletype)) 
			{
				String outTradeNo = "";
				// 退货前线先查询交易单号的状态
				StringBuffer shdnh = new StringBuffer();
				boolean done = new TextBox().open(Language.apply("请输入商户订单号"),
						Language.apply("商户订单号"), Language.apply("请输入商户订单号"),
						shdnh, 0, 0, false, TextBox.IntegerInput);
				outTradeNo = shdnh.toString();

				if (done) {
					// 访问阿里查询接口
					queryFlag = sn.query(outTradeNo, mapN);
					String pay_channel = queryFlag.get("pay_channel");

					// 交易单号处于交易成功状态才允许退货 01表示交易成功已付款
					if ("01".equals(queryFlag.get("status_code"))&& "00".equals(queryFlag.get("return_status_code")))
					{
						// 退货单号
						String backrn = randomNum.generateString(4);
						String backoutTradeNo = randomNum.getTime()
								+ GlobalInfo.posLogin.gh + backrn;
						// 访问退货接口
						HashMap backMap = sn.backPay(outTradeNo, outTradeNo,
								money, salehead.syyh, backoutTradeNo,
								backoutTradeNo, backoutTradeNo, mapN);

						if ("T".equals(backMap.get("is_success"))) {
							while (true) {
								codeMsg = new MessageBox(text2, null, true);
								if (codeMsg.verify() == GlobalVar.Key1) {
									// 退款
									backMap = sn.backPay(outTradeNo,
											outTradeNo, money, salehead.syyh,
											backoutTradeNo, backoutTradeNo,
											backoutTradeNo, mapN);
									if (!"T".equals(backMap.get("is_success"))) {
										StringBuffer msgStr = new StringBuffer();
										msgStr.append("商户单号");
										msgStr.append(outTradeNo);
										msgStr.append("\n");
										msgStr.append("易付宝退款失败");
										msgStr.append("\n");
										msgStr.append("按数字键1确认，按数字键2退出");
										text = msgStr.toString();
										continue;
									} else {
										new MessageBox(
												Language.apply("易付宝退款完成"));
										if (!createSalePay(money))
											return null;
										salepay.payno = outTradeNo;
										salepay.memo = pay_channel;
										// 打印签购单
										if (!spm.suNingPrint(queryFlag,
												salehead, saleBS.saleGoods,
												saleBS.salePayment, printFlag)) {
											new MessageBox(
													Language.apply("打印签购单失败,交易完成后尝试重打印"));
											salepay.batch = "SUNINGPRINTFC";
											// return null;
										}
										salepay.batch = "SUNINGSALEBCKB";
										return salepay;
									}
								} else {
									return null;
								}
							}
						} else {
							new MessageBox(Language.apply("易付宝退款完成"));
							if (!createSalePay(money))
								return null;
							salepay.payno = outTradeNo;
							salepay.memo = pay_channel;
							// 打印签购单
							if (!spm.suNingPrint(queryFlag, salehead,
									saleBS.saleGoods, saleBS.salePayment,
									printFlag)) {
								new MessageBox(
										Language.apply("打印签购单失败,交易完成后尝试重打印"));
								salepay.batch = "SUNINGPRINTFC";
								// return null;
							}
							salepay.batch = "SUNINGSALEBCKB";

							return salepay;
						}
					}
					// else if("SUNINGPRINTFC".equals(salepay.batch ))
					// {
					// salepay.payno = outTradeNo;
					// salepay.memo = pay_channel;
					// if(!spm.suNingPrint(mapN,salehead,saleBS.saleGoods,saleBS.salePayment,printFlag))
					// {
					// new MessageBox(Language.apply("打印签购单失败,交易完成后尝试重打印"));
					// salepay.batch = "SUNINGPRINTFC";
					// //return null;
					// }
					// salepay.batch = "SUNINGSALEBCK";
					//
					// return salepay;
					// }
					else {
						new MessageBox(Language.apply("该" + outTradeNo
								+ "商户订单号" + "并非交易成功状态无法退货请选择其他退货方式"));
						return null;
					}
				}
			}

		} catch (Exception e) {
			new MessageBox("易付宝交易异常");
			PosLog.getLog(getClass()).info("易付宝异常" + e);
			return null;
		}
		return null;

	}

	// 撤销
	public boolean cancelPay() {
		try {
			HashMap mapN = new HashMap();
			// 获取请求地址
			mapN = sn.parseIni();
			// String url = mapN.get("aliPayUrl").toString();
			if (!GlobalInfo.isOnline) {
				new MessageBox(Language.apply("请检查易付宝支付配置，并保证当前系统处于联网状态"));
				return false;
			}
			String outTradeNo = salepay.payno;
			String backrn = randomNum.generateString(4);
			String backoutTradeNo = randomNum.getTime()
					+ GlobalInfo.posLogin.gh + backrn;
			if (true) {
				// 访问查询接口
				queryFlag = sn.query(outTradeNo, mapN);

				// if ("01".equals(queryFlag.get("status_code")))
				if ("01".equals(queryFlag.get("status_code"))
						&& "00".equals(queryFlag.get("return_status_code"))) {
					HashMap backMap = sn.backPay(outTradeNo, outTradeNo,
							Double.toString(salepay.je), salehead.syyh,
							backoutTradeNo, backoutTradeNo, backoutTradeNo,
							mapN);
					if (!"T".equals(backMap.get("is_success"))) {
						new MessageBox(Language.apply("该" + outTradeNo
								+ "商户订单号" + "易付宝退款异常，请选择 其他方式退款"));
						return false;
					}
					new MessageBox(Language.apply("易付宝撤销完成"));
					salepay.batch = "SUNINGSALEBCKB";
					return true;
				} else {
					new MessageBox(Language.apply("该" + outTradeNo + "商户订单号"
							+ "并非交易成功状态无法退货请选择其他退货方式"));
					return false;
				}
			}
			return false;
		} catch (Exception e) {
			new MessageBox("易付宝撤销异常");
			PosLog.getLog(getClass()).info(e);
			return false;

		}
	}

	public boolean collectAccountPay() {
		try {
			HashMap mapN = new HashMap();
			// 获取请求地址
			mapN = sn.parseIni();
			String printFlag = mapN.get("printFlag").toString();
			String outTradeNo = salepay.payno;

			String backrn = randomNum.generateString(4);
			String backoutTradeNo = randomNum.getTime()
					+ GlobalInfo.posLogin.gh + backrn;
			queryFlag = sn.query(outTradeNo, mapN);
			if(!"SUNINGSALEBCKB".equals(salepay.batch)&&!"00".equals(queryFlag.get("return_status_code")))
			{
				return false;
			}
			// ALISALEBCK代表已退货 ALISALE代表交易成功// 退货前先查询交易单号的状态//交易状态为成功时才允许退货
			// //2红冲 4 退货
			if ((SellType.ISBACK(salehead.djlb) || SellType.ISHC(salehead.djlb))
					&& ("SUNINGSALE".equals(salepay.batch) || ("01".equals(queryFlag.get("status_code")) && "00".equals(queryFlag.get("return_status_code"))))) 
			{
				// 退款
				HashMap backMap = sn.backPay(outTradeNo, outTradeNo,
						String.valueOf(salepay.je), salehead.syyh,
						backoutTradeNo, backoutTradeNo, backoutTradeNo, mapN);
				if (!"T".equals(backMap.get("is_success"))) {
					while (true) {
						codeMsg = new MessageBox(text2, null, true);
						if (codeMsg.verify() == GlobalVar.Key1) {
							// 查询
							backMap = sn.backPay(outTradeNo, outTradeNo,
									String.valueOf(salepay.je), salehead.syyh,
									backoutTradeNo, backoutTradeNo,
									backoutTradeNo, mapN);
							if (!"T".equals(backMap.get("is_success"))) {
								StringBuffer msgStr = new StringBuffer();
								msgStr.append("商户单号");
								msgStr.append(outTradeNo);
								msgStr.append("\n");
								msgStr.append("易付宝退货失败");
								msgStr.append("\n");
								msgStr.append("按数字键1确认，按数字键2退出");
								text = msgStr.toString();
								continue;
							} else {
								new MessageBox(Language.apply("易付宝退货成功"));
								// 打印签购单
								if (!spm.suNingPrint(queryFlag, salehead, null,
										null, printFlag)) {
									new MessageBox(
											Language.apply("打印签购单失败,交易完成后尝试重打印"));
									salepay.batch = "SUNINGPRINTF";
									return false;
								}
								salepay.batch = "SUNINGSALEBCK";
								return true;
							}
						} else {
							new MessageBox(Language.apply("易付宝退货失败"));
							return false;
						}
					}
				} else {
					new MessageBox(Language.apply("易付宝退货成功"));
					// 打印签购单
					if (!spm.suNingPrint(queryFlag, salehead, null, null,
							printFlag)) {
						new MessageBox(Language.apply("打印签购单失败"));
						salepay.batch = "SUNINGPRINTF";
						return false;
					}

					salepay.batch = "SUNINGSALEBCK";
					return true;
				}
			}
			// 处理退货成功 签购单没打印的情况
			// else if ("ALIPRINTF".equals(salepay.batch)
			// && "TRADE_CLOSED".equals(json.get("pay_status")))
			// {
			// if (!apm.aliPrint(json, salehead, null, null, printFlag)) {
			// new MessageBox(Language.apply("打印签购单失败"));
			// salepay.batch = "ALIPRINTF";
			// return false;
			// } else {
			// salepay.batch = "ALISALEBCK";
			// return true;
			// }
			// }
			return true;
		} catch (Exception e) {
			new MessageBox("易付宝异常");
			PosLog.getLog(getClass()).info(e);
			return false;
		}
	}

	public boolean collectAccountClear() {
		// 删除相应的冲正记录
		return true;
	}

}
