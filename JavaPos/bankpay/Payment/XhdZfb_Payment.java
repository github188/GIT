package bankpay.Payment;

import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import net.sf.json.JSONObject;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.QrcodeDisplay;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.stuart.nest.client.NestClient;
import com.stuart.nest.client.WsClientException;
import com.stuart.nest.client.WsRequest;
import com.stuart.nest.client.WsResponse;

/**
 * 新华都支付宝接口
 * @author Maxun
 *
 */
public class XhdZfb_Payment extends PaymentMzk {
	
	private String ip = "127.0.0.1";
	private int port = 8080;
	
	public XhdZfb_Payment()
	{
	}

	public XhdZfb_Payment(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	// 该构造函数用于红冲小票时,通过小票付款明细创建对象
	public XhdZfb_Payment(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public void initPayment(SalePayDef pay, SaleHeadDef head)
	{
		super.initPayment(pay, head);
	}

	public SalePayDef inputPay(String money)
	{
		//加载配置信息
		initer();
		
		if (SellType.ISSALE(saleBS.saletype))
		{
			int choice = -1;
			Vector v = new Vector();
			//组装MutiSelectForm显示数据
			
			v.add(new String[]{"0","二维码支付"});
			v.add(new String[]{"1","条码支付"});
			v.add(new String[]{"2","查询支付"});
			
			String strmsg = Language.apply("请输入序号");
			MutiSelectForm msf = new MutiSelectForm();
			choice = msf.open(strmsg, new String[] { Language.apply("序号"), Language.apply("交易方式")}, new int[] { 80, 550}, v, true, 700, 400, 673, 285, false, false);
			if(choice == -1)
			{
				return null;
			}
			//执行二维码支付
			else if(choice == 0&&SellType.ISSALE(saleBS.saletype))
			{
				//获得阿里接口返回值
				Map map =precreate(money);
				if(map == null) return null;
				String qrcode = map.get("qrcode").toString(); 
				String out_id = map.get("out_id").toString();
				
				String text ="请扫二维码";
				String msgString ="请确认二维码扫描是否完成?\n 按数字键1确认，按数字键3退出";
				int msg = -1;
				
				if(null !=qrcode && !("").equals(qrcode))
				{

					//查询交易是否成功
					while(true)
					{
						//获取二维码
						msg = QrcodeDisplay.display(qrcode,text,10,msgString);
						
						if(msg == GlobalVar.Key1)
						{
							Map queryMap = query(out_id,saleBS.saleHead.syyh);
							
							if(queryMap == null || !"TRADE_SUCCESS".equals(queryMap.get("pay_status")))
							{
								StringBuffer msgStr = new StringBuffer();
								msgStr.append("商家订单号");
								msgStr.append(out_id);
								msgStr.append("\n");
								msgStr.append("并非交易成功状态");
								msgStr.append("\n");
								msgStr.append("按数字键1确认，按数字键3退出");
								msgString = msgStr.toString();
								continue;
							}
							new MessageBox(Language.apply("用户向支付宝支付完成"));
							
							mzkret.ye = Convert.toDouble(queryMap.get("total_fee")) / 100;
							
//							String payno=(String) queryMap.get("out_id");
//							String batch=(String) queryMap.get("pay_status");
							//salepay.payno = payno;//商户订单号
							//salepay.batch = batch;//交易状态
							
							
							//给小票头的商户订单号赋值
							if (!createSalePay(money)) return null;
							salepay.payno = out_id;
							ArrayList channels = (ArrayList)queryMap.get("pay_channels");
							salepay.memo = alipaymemo(channels);//付款明细
							printSellBill(salehead,out_id,"",channels);
							return salepay;
						} 
						else if(msg == GlobalVar.Key2)
						{
							return null;
						}
						else if(msg == 999)
						{
							new MessageBox(Language.apply("无法使用客显屏显示二维码，请联系管理员或者选着其他方式付款"));
							return null;
						}
						
					}
					
				}
				else
				{
					new MessageBox(Language.apply("无法使用二维码，请联系管理员或者选着其他方式付款"));
					return null;
				}
				//二维码支付end
			}
			//条码支付begin
			else if(choice == 1  && SellType.ISSALE(saleBS.saletype))
			{
				StringBuffer req = new StringBuffer();
				boolean done = new TextBox().open(Language.apply("请输入条码"), Language.apply("条码"), Language.apply("请扫描客户手机条码或者输入条码号"), req, 0, 0, false,
													TextBox.IntegerInput);
				if(done)
				{
					String tmCode = req.toString();
					MessageBox codeMsg = null;
					String text = "请确认支付宝支付是否成功 \n" +
								  "按数字键1查询，按数字键3退出";
					//支付接口调用
					Map map = createandpay(money,tmCode);
					String out_id = "";
					if(map != null)
					{
						out_id = map.get("out_id").toString();
						//查询交易是否成功
						while(true)
						{
							//获取二维码
							codeMsg = new MessageBox(text, null, true);
							
							if(codeMsg.verify() == GlobalVar.Key1)
							{
								//调用查询支付结果
								Map queryMap = query(out_id,saleBS.saleHead.syyh);
								if(queryMap != null)
								{
									if(!"TRADE_SUCCESS".equals(queryMap.get("pay_status")))
									{
										StringBuffer msgStr = new StringBuffer();
										if(null == out_id)
										{
											msgStr.append("无效商家订单号");
											msgStr.append(tmCode);
											msgStr.append("\n");
										}
										else
										{
											msgStr.append("商家订单号");
											msgStr.append(out_id);
											msgStr.append("\n");
											msgStr.append("并非交易成功状态");
											msgStr.append("\n");
										}
										msgStr.append("按数字键1确认，按数字键3退出");
										text = msgStr.toString();
										continue;
									}
									new MessageBox(Language.apply("用户向支付宝支付完成"));
									
									mzkret.ye = Convert.toDouble(queryMap.get("total_fee")) / 100;
									
									String payno=(String) queryMap.get("out_id");
									String batch=(String) queryMap.get("pay_status");
									//HashMap channels = (HashMap)queryMap.get("pay_channels");
									ArrayList channels = (ArrayList)queryMap.get("pay_channels");
									String memo ="";
									//给小票头的商户订单号赋值
									if (!createSalePay(money)) return null;
									salepay.payno = out_id;
									saleBS.saleHead.str5 = out_id;
									salepay.batch = "ALISALE";//交易状态
									salepay.memo = alipaymemo(channels);//付款明细
									printSellBill(salehead, out_id,memo,channels);
									return salepay;
								}
							}
							else if(codeMsg.verify() == GlobalVar.Key2)
							{
								return null;
							}
							
						}
					}
				}
				else
				{
					return null;
				}
			}
			//条码支付end
			// 查询支付或补单begin
			else if (choice == 2 && SellType.ISSALE(saleBS.saletype)) 
			{
				StringBuffer shdnh = new StringBuffer();
				boolean done = new TextBox().open(Language.apply("商户订单号"),
						Language.apply("商户订单号"), Language.apply("输入商户订单号"),
						shdnh, 0, 0, false, TextBox.IntegerInput);
				String out_id = shdnh.toString();
				NetService netService = (NetService) NetService.getDefault();
				// 查询订单是否已存在
				boolean selectFlag = netService.selectOutId(out_id);

				if (done && selectFlag) 
				{
					//调用查询支付结果
					Map queryMap = query(out_id,saleBS.saleHead.syyh);
		
					//查询不成功或者非交易支付成功状态不让补单
					if (!"TRADE_SUCCESS".equals(queryMap.get("pay_status"))) 
					{
						new MessageBox(Language.apply("该" + shdnh+ "商户单号" + "补单失败"));
					    return null;
					}
					else if(Convert.toDouble(queryMap.get("total_fee")) / 100!=Double.parseDouble(money))
					{
						new MessageBox(Language.apply("金额不一致"));
						return null;
					} 
					else 
					{
						if (!createSalePay(money))
							return null;
						salepay.payno = shdnh.toString();
						ArrayList channels = (ArrayList)queryMap.get("pay_channels");
						salepay.memo = alipaymemo(channels);//付款明细
						new MessageBox(Language.apply("用户向支付宝支付完成"));
						// 打印签购单
						printSellBill(salehead, out_id,"",channels);
						//salepay.batch = "ALISALE";
						
						return salepay;
					}
				} 
				else 
				{
					return null;
				}
			}
			// 查询支付或补单end
			
		}
		
		//支付宝退货begin
		else if(SellType.ISBACK(saleBS.saletype))
		{ 
			StringBuffer shdnh = new StringBuffer();
			
			boolean done = new TextBox().open(Language.apply("请输入商户订单号"), Language.apply("商户订单号"), Language.apply("请输入商户订单号"), shdnh, 0, 0, false,
												TextBox.IntegerInput);
			if(done)
			{
				//调用退货接口
				Map queryMap = saleBank(shdnh.toString(),saleBS.saleHead.syyh,String.valueOf(saleBS.saleHead.fphm),money);
				if(queryMap != null)
				{
					//交易单号处于交易成功状态才允许退货
					if("true".equals(queryMap.get("is_success")))
					{
						new MessageBox(Language.apply("支付宝退款完成"));
						if (!createSalePay(money)) return null;
						salepay.payno = queryMap.get("out_id").toString();
						saleBS.saleHead.str5 = queryMap.get("out_id").toString();
						salepay.batch = "ALIBACK";
						//printSellBill(salehead, salepay.payno,"");
						Map queryMap2 = query(queryMap.get("out_id").toString(),saleBS.saleHead.syyh);
						ArrayList channels = (ArrayList)queryMap2.get("pay_channels");
						salepay.memo = alipaymemo(channels);//付款明细
						printSellBill(salehead,queryMap.get("out_id").toString(),"",channels);
						return salepay;
					}
					else
					{
						new MessageBox(Language.apply("该"+shdnh+"商户订单号"+"退货失败！"));
						return null;
					}
				}
			}
		}
		
		return null;

	}

	//撤销
	public boolean cancelPay()
	{
		
		WsRequest request = new WsRequest();
		//具体服务
		request.setService("taobao.trade.acquire.closeorder");

		//请求数据
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("out_id", salehead.str5);
		data.put("operator_id",salehead.syyh);
		
		request.setData(data);
				
		NestClient client = new NestClient(ip, port, "", "");
		//执行调用
		WsResponse response;
		Map<String,Object> resultMap = null;
		try {
			response = client.execute(request);
		
		//error=0则成功，取返回业务数据
		if(response.getError() == 0){
			resultMap = response.getData();
			if(resultMap.get("is_success").equals("true"))
				return true;
			else
				return false;
		}else{
			//失败取原因码及错误信息
			System.out.println( response.getError() );
			System.out.println( response.getMessage() );
		}
		
		} catch (WsClientException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
		
		return false;
	}

	public boolean collectAccountPay()
	{
		
		return true;
	}

	public boolean collectAccountClear()
	{
		// 删除相应的冲正记录
		return true;
	}
	
	//订单支付结果查询
	private Map query(String out_id,String operator_id)
	{
		WsRequest request = new WsRequest();
		//具体服务
		request.setService("taobao.trade.acquire.order.query");

		//请求数据
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("out_id", out_id);
		data.put("operator_id",operator_id);
		
		request.setData(data);
				
		NestClient client = new NestClient(ip, port, "", "");
		//执行调用
		WsResponse response;
		Map<String,Object> resultMap = null;
		try {
			
			StringBuffer msglog = new StringBuffer();
			msglog.append("【查单Zfb_Request=<taobao.trade.acquire.order.query>[out_id="+data.get("out_id")+",operator_id="+data.get("operator_id")+"]】");
			PosLog.getLog(this.getClass()).error(msglog.toString());
			
			response = client.execute(request);
		
			//error=0则成功，取返回业务数据
		if(response.getError() == 0){
			resultMap = response.getData();
			
			msglog = new StringBuffer();
			msglog.append("【查单Zfb_Result=<taobao.trade.acquire.order.query>[pay_status="+resultMap.get("pay_status")+",actual_fee="+resultMap.get("actual_fee")+",total_fee="+resultMap.get("total_fee")+"]】");
			PosLog.getLog(this.getClass()).error(msglog.toString());
			
		}else{
//			失败取原因码及错误信息			
			msglog = new StringBuffer();
			msglog.append("【查单失败：<taobao.trade.acquire.order.query>"+response.getError()+","+response.getMessage());
			PosLog.getLog(this.getClass()).error(msglog.toString());
			
			new MessageBox("支付宝调用失败："+response.getError()+","+response.getMessage());
		}
		
		} catch (WsClientException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
			new MessageBox(""+e.getMessage());
			PosLog.getLog(this.getClass()).error(new StringBuffer(e.getMessage()).toString());
		}
		
		return resultMap;
	}
	
	//预下单(二维码)taobao.trade.acquire.createandpay
	private Map precreate(String money){
		
		WsRequest request = new WsRequest();
//		具体服务
		request.setService("taobao.trade.acquire.precreate");


//		请求数据
		Map<String,Object> data = new HashMap<String,Object>();
		/*data.put("operator_id", "001"); 
		data.put("out_id", "20131101009");
		data.put("receipt_no", "myticket_no001");
		data.put("subject", "银泰购物");
		data.put("desc", "购买九匹狼10件");
		data.put("total_fee", "200001");
		data.put("pay_toc", "2d");
		data.put("mall_code", "yintaimall_code");
		data.put("shop_code", "yintai_shop_001");*/
		
		data.put("operator_id",saleBS.saleHead.syyh); 
		data.put("out_id", saleBS.saleHead.mkt+saleBS.saleHead.syjh+saleBS.saleHead.syyh+GlobalInfo.balanceDate.replace("/", "").replace("-", "")+getMzkSeqno());
		data.put("receipt_no", saleBS.saleHead.fphm);
		data.put("subject", ((SaleGoodsDef)saleBS.saleGoods.get(0)).name);
		data.put("total_fee", String.valueOf((long)ManipulatePrecision.doubleConvert(Convert.toDouble(money)*100, 2, 1)));
		data.put("pay_toc", "2d");
		data.put("mall_code", "yintaimall_code");
		data.put("shop_code", saleBS.saleHead.mkt);
		request.setData(data);
		NestClient client = new NestClient(ip, port, "", "");
//		执行调用
		WsResponse response;
		Map<String,Object> resultMap = null;
		try {
			StringBuffer msglog = new StringBuffer();
			msglog.append("【二维码下单Zfb_Request=<taobao.trade.acquire.precreate>[operator_id="+data.get("operator_id")+",out_id="+data.get("out_id")+",receipt_no="+data.get("receipt_no")+",subject="+data.get("subject")+",subject="+data.get("subject")+",total_fee="+data.get("total_fee")+",pay_toc="+data.get("pay_toc")+",mall_code="+data.get("mall_code")+"]】");
			PosLog.getLog(this.getClass()).error(msglog.toString());
			
			response = client.execute(request);
			
			//error=0则成功，取返回业务数据
		if(response.getError() == 0){
			resultMap = response.getData();
			
			msglog = new StringBuffer();
			msglog.append("【二维码下单Zfb_Result=<taobao.trade.acquire.precreate>[seller_id="+data.get("seller_id")+",out_id="+data.get("out_id")+"]】");
			PosLog.getLog(this.getClass()).error(msglog.toString());

		}else{
			//失败取原因码及错误信息
			msglog = new StringBuffer();
			msglog.append("【二维码下单失败：<taobao.trade.acquire.precreate>"+response.getError()+","+response.getMessage());
			PosLog.getLog(this.getClass()).error(msglog.toString());
			
			new MessageBox("支付宝调用失败："+response.getError()+","+response.getMessage());
		}

		} catch (WsClientException e) {
			e.printStackTrace();
			new MessageBox(""+e.getMessage());
			PosLog.getLog(this.getClass()).error(new StringBuffer(e.getMessage()).toString());
		}
		
		return resultMap;
	}
	
//	下单(条形码&声波)
	private Map createandpay(String money,String code){
		
		WsRequest request = new WsRequest();
//		具体服务
		request.setService("taobao.trade.acquire.createandpay");

//		请求数据
		Map<String,Object> data = new HashMap<String,Object>();
		
//		data.put("operator_id", "001");
//		data.put("out_id", "0007410133099");
//		data.put("bar_code", code);
//		data.put("receipt_no", "2099");
//		data.put("subject", "新华都测试sku1");
//		data.put("total_fee", "1");
//		data.put("pay_toc", "60m");
//		data.put("mall_code", "yintaimall_code");
		
		data.put("operator_id", saleBS.saleHead.syyh);
		data.put("out_id", saleBS.saleHead.mkt+saleBS.saleHead.syjh+saleBS.saleHead.syyh+GlobalInfo.balanceDate.replace("/", "").replace("-", "")+getMzkSeqno());
		data.put("bar_code", code);
		data.put("receipt_no", saleBS.saleHead.fphm);
		data.put("subject", ((SaleGoodsDef)saleBS.saleGoods.get(0)).name);
		data.put("total_fee", String.valueOf((long)ManipulatePrecision.doubleConvert(Convert.toDouble(money)*100, 2, 1)));
		data.put("pay_toc", "80m");
		data.put("mall_code", "yintaimall_code");

		request.setData(data);
		
		NestClient client = new NestClient(ip, port, "", "");
		
		//执行调用
		WsResponse response;
		Map<String,Object> resultMap = null;
		try {
			
			StringBuffer msglog = new StringBuffer();
			msglog.append("【条码&声波下单Zfb_Request=<taobao.trade.acquire.createandpay>[operator_id="+data.get("operator_id")+",out_id="+data.get("out_id")+",bar_code="+data.get("bar_code")+",receipt_no="+data.get("receipt_no")+",subject="+data.get("subject")+",total_fee="+data.get("total_fee")+",pay_toc="+data.get("pay_toc")+",mall_code="+data.get("mall_code")+"]】");
			PosLog.getLog(this.getClass()).error(msglog.toString());
			
			response = client.execute(request);
			
			//error=0则成功，取返回业务数据
		if(response.getError() == 0){
			
			resultMap = response.getData();
			
			msglog = new StringBuffer();
			msglog.append("【条码&声波下单Zfb_Result=<taobao.trade.acquire.createandpay>[seller_id="+data.get("seller_id")+",out_id="+data.get("out_id")+"]】");
			PosLog.getLog(this.getClass()).error(msglog.toString());
			
		}else{
			//失败取原因码及错误信息			
			msglog = new StringBuffer();
			msglog.append("【条码&声波下单失败：<taobao.trade.acquire.createandpay>"+response.getError()+","+response.getMessage());
			PosLog.getLog(this.getClass()).error(msglog.toString());
			
			new MessageBox("支付宝调用失败："+response.getError()+","+response.getMessage());
		}

		} catch (WsClientException e) {
			e.printStackTrace();
			new MessageBox(""+e.getMessage());
			PosLog.getLog(this.getClass()).error(new StringBuffer(e.getMessage()).toString());
		}
		
		return resultMap;
	}
	
	//订单退货
	private Map saleBank(String out_id,String syyh,String req_no,String money){
		
		WsRequest request = new WsRequest();
//		具体服务
		request.setService("taobao.trade.acquire.refund");

//		请求数据
		Map<String,Object> data = new HashMap<String,Object>();
		
		data.put("out_id", out_id); 
		data.put("operator_id", syyh);
		data.put("out_refund_req_no", req_no);
		data.put("refund_amount", String.valueOf((long)ManipulatePrecision.doubleConvert(Convert.toDouble(money)*100, 2, 1)));
		request.setData(data);
		
		NestClient client = new NestClient(ip, port, "", "");
		
//		执行调用
		WsResponse response;
		Map<String,Object> resultMap = null;
		try {
			
			StringBuffer msglog = new StringBuffer();
			msglog.append("【退单Zfb_Request=<taobao.trade.acquire.refund>[out_id="+data.get("out_id")+",operator_id="+data.get("operator_id")+",out_refund_req_no="+data.get("out_refund_req_no")+",is_success="+data.get("is_success")+"]】");
			PosLog.getLog(this.getClass()).error(msglog.toString());
			
			response = client.execute(request);
			
//		error=0则成功，取返回业务数据
		if(response.getError() == 0){
			resultMap = response.getData();
			
			msglog = new StringBuffer();
			msglog.append("【退单Zfb_Result=<taobao.trade.acquire.refund>[out_id="+data.get("out_id")+",fund_is_changed="+data.get("fund_is_changed")+"]】");
			PosLog.getLog(this.getClass()).error(msglog.toString());
			

		}else{
//		失败取原因码及错误信息
			msglog = new StringBuffer();
			msglog.append("【退单失败：<taobao.trade.acquire.createandpay>"+response.getError()+","+response.getMessage());
			PosLog.getLog(this.getClass()).error(msglog.toString());
			
			new MessageBox("支付宝调用失败："+response.getError()+","+response.getMessage());
		}

		} catch (WsClientException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
			new MessageBox(""+e.getMessage());
			PosLog.getLog(this.getClass()).error(new StringBuffer(e.getMessage()).toString());
		}
		
		return resultMap;
	}

	//加载支付宝配置
	public void initer()
	{
		String configName = GlobalVar.ConfigPath + "//ZfbConfig.ini";
		String line = null;
		
		BufferedReader br = CommonMethod.readFile(configName);
		
		try 
		{
			while ((line = br.readLine()) != null)
			{
				String[] row = line.split("=");
				if(row[0].trim().equals("IP"))
				{
					ip = row[1].trim();
				}
				else if(row[0].trim().equals("PORT"))
				{
					port = Convert.toInt(row[1].trim());
				}
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	//显示余额信息
	public void showAccountYeMsg()
	{
		
	}
	
	//打印签购单
	@SuppressWarnings("rawtypes")
	public void printSellBill(SaleHeadDef saleHead,String outid,String memo,ArrayList channels)
	{
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH/mm:ss");//设置日期格式
    	String time = df.format(new Date()).toString();
		// 如果没有连接打印机则连接
		if (GlobalInfo.sysPara.issetprinter == 'Y'
			&& GlobalInfo.syjDef.isprint == 'Y'
			&& Printer.getDefault() != null
			&& !Printer.getDefault().getStatus()) 
		{
			Printer.getDefault().open();
			Printer.getDefault().setEnable(true);
		}
		
		Printer.getDefault().printLine_Normal("     支 付 宝 签 购 单          ");
		Printer.getDefault().printLine_Normal("--商户存根-----MERCHANT COPY--------");
		Printer.getDefault().printLine_Normal("商户名称(MER NAME):"+GlobalInfo.sysPara.mktname);
		Printer.getDefault().printLine_Normal("商户编号(MER   NO):"+GlobalInfo.sysPara.mktcode);
		Printer.getDefault().printLine_Normal("操作员号(OPER  NO):"+saleHead.syyh);
		Printer.getDefault().printLine_Normal("收银机号(POS   NO):"+saleHead.syjh);
		Printer.getDefault().printLine_Normal("交易号:"+saleHead.fphm);
		Printer.getDefault().printLine_Normal("-----------------------------------");
		if("1".equals(saleHead.djlb))
		{
			Printer.getDefault().printLine_Normal("交易类型(TRANTYPE):"+"销售");
		}
		if("4".equals(saleHead.djlb))
		{
			Printer.getDefault().printLine_Normal("交易类型(TRANTYPE):"+"退货");
		}
		
		Printer.getDefault().printLine_Normal("商户交易号(MER NO):"+outid);
		Printer.getDefault().printLine_Normal("日期时间(DATE/TIME):"+saleHead.rqsj);
		Printer.getDefault().printLine_Normal("金    额(RMB):"+saleHead.ysje);
		Printer.getDefault().printLine_Normal("其中:");
		for(int i=0; i<channels.size(); i++)
		{
			Map map = (Map) channels.get(i);
			if ("ALIPAYACCOUNT".equals(map.get("pay_channel").toString())) 
			{
				Printer.getDefault().printLine_Normal("  支付宝:"+ManipulatePrecision.doubleConvert(Double.parseDouble(map.get("pay_fee").toString()) / 100));
				
			}
			if ("JINYUANBAO".equals(map.get("pay_channel").toString())) 
			{
				Printer.getDefault().printLine_Normal("  元宝:"+ManipulatePrecision.doubleConvert(Double.parseDouble(map.get("pay_fee").toString()) / 100));
			}
			if ("MCARD ".equals(map.get("pay_channel").toString())) 
			{
				Printer.getDefault().printLine_Normal("  储值卡:"+ManipulatePrecision.doubleConvert(Double.parseDouble(map.get("pay_fee").toString()) / 100));
			}
			if ("COUPON".equals(map.get("pay_channel").toString())) 
			{
				Printer.getDefault().printLine_Normal("  支付红包:"+ManipulatePrecision.doubleConvert(Double.parseDouble(map.get("pay_fee").toString()) / 100));
			}
			if ("PROMOTION ".equals(map.get("pay_channel").toString())) 
			{
				Printer.getDefault().printLine_Normal("  优惠劵:"+ManipulatePrecision.doubleConvert(Double.parseDouble(map.get("pay_fee").toString()) / 100));
			}
			Printer.getDefault().printLine_Normal(map.get("pay_channel").toString()+":"+map.get("pay_fee").toString());
		}
		
		Printer.getDefault().printLine_Normal("----------------------------------");
		Printer.getDefault().printLine_Normal("谢谢惠顾，欢迎再次光临");
		Printer.getDefault().printLine_Normal("打印时间(DATE/TIME):"+time);
		Printer.getDefault().printLine_Normal("持卡人签名:______________________");
		Printer.getDefault().printLine_Normal("本人确认以上交易   同意记入此卡帐户");
		Printer.getDefault().printLine_Normal("----------------------------------");
		Printer.getDefault().printLine_Normal("     ");
		Printer.getDefault().printLine_Normal("     ");
		Printer.getDefault().printLine_Normal("     ");
		Printer.getDefault().printLine_Normal("     ");
		
		// 需要在付款时释放打印机时
		if (GlobalInfo.sysPara.issetprinter == 'Y'
					&& GlobalInfo.syjDef.isprint == 'Y'
					&& Printer.getDefault() != null
					&& Printer.getDefault().getStatus()) 
		{
				Printer.getDefault().close();
		}
	
				
	}
	
	public String alipaymemo(ArrayList list)
	{
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<list.size(); i++)
		{
			Map map = (Map) list.get(i);
			if ("ALIPAYACCOUNT".equals(map.get("pay_channel").toString())) 
			{
				sb.append(map.get("pay_channel").toString());
				sb.append(":");
				sb.append(map.get("pay_fee").toString());
				sb.append("&");
				
			}
			if ("JINYUANBAO".equals(map.get("pay_channel").toString())) 
			{
				sb.append(map.get("pay_channel").toString());
				sb.append(":");
				sb.append(map.get("pay_fee").toString());
				sb.append("&");
			}
			if ("MCARD ".equals(map.get("pay_channel").toString())) 
			{
				sb.append(map.get("pay_channel").toString());
				sb.append(":");
				sb.append(map.get("pay_fee").toString());
				sb.append("&");
			}
			if ("COUPON".equals(map.get("pay_channel").toString())) 
			{
				sb.append(map.get("pay_channel").toString());
				sb.append(":");
				sb.append(map.get("pay_fee").toString());
				sb.append("&");
			}
				
			if ("PROMOTION ".equals(map.get("pay_channel").toString())) 
			{
				sb.append(map.get("pay_channel").toString());
				sb.append(":");
				sb.append(map.get("pay_fee").toString());
				sb.append("&");
			}
			
		}
		
		return sb.toString();
	}
	
	
}

