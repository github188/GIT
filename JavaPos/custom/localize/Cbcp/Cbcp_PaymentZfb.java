package custom.localize.Cbcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.swt.events.KeyEvent;

import bankpay.alipay.tools.Md5Tools;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

import custom.localize.Bcrm.Bcrm_PaymentMzk;

import java.net.URLEncoder;

public class Cbcp_PaymentZfb extends Bcrm_PaymentMzk {

	private Cbcp_PaymentZfbForm form = null;	

	private Vector<Cbcp_WCCRuleDef> wcc_rules = null;
	private Cbcp_WCCRuleDef wcc_rule = null;
	private String wcc_billno = null;
	private String wcc_cardno = null;
	private String wcc_couponno = null;
	private double wcc_value = 0;
	
	BankLogDef bld = null;
	
	boolean flag = true;
	
	public SalePayDef inputPay(String money) {
		try {
			mzkreq.je = Convert.toDouble(money);
			saleBS.saleHead.num3 = Convert.toDouble(money);
			// 退货小票不能使用,退货扣回按销售算
			if (checkMzkIsBackMoney() && GlobalInfo.sysPara.thmzk != 'Y') {
				new MessageBox("退货时不能使用" + paymode.name);
				return null;
			}
			
			//初始化商品的微信券标志
			if(!isPaymentExist()) setCommodFree();
			
			// 先检查是否有冲正未发送
			if (!sendAccountCz())
				return null;
			
			// 打开明细输入窗口
			form = new Cbcp_PaymentZfbForm();
			
			form.open(this, saleBS);
			
			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}
	
	
	public boolean isPaymentExist()
	{
		int i;
		for(i = 0; i < saleBS.payAssistant.size(); ++ i)
		{
			if(saleBS.payAssistant.elementAt(i).getClass().getName().equals(this.getClass().getName()))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean cancelPay()
	{
		if (SellType.ISSALE(salehead.djlb))
		{
			//消费的时候撤销付款方式
			if(mzkAccount(false))
			{
				cancelMarks();
				return true;			
			}
		}
		else
		{	
			new MessageBox("此付款方式退货不允许取消或者删除 ！");
		}
		
		return false;
	}
	
	public boolean collectAccountPay()
	{
		return true;
	}
	
	public boolean realAccountPay() {
//		if (GlobalInfo.sysPara.cardrealpay == 'Y') {
			// 付款即时记账
			if (mzkAccount(true)) {
				deleteMzkCz();
				saveFindMzkResultToSalePay();
				return true;
			} else {
				return false;
			}
//		} else {
//			// 不即时记账
//			return true;
//		}
	}

	public boolean findMzk(String track1, String track2, String track3) {

		// 设置请求数据
		setRequestDataByFind(track1, track2, track3);
		if (!SellType.ISSALE(saleBS.saletype) && mzkreq.type.length()>0)
		{
			return mzkAccount(true);
		}
		return true;
		// return true;//(mzkreq,mzkret);
	}

	public boolean mzkAccount(boolean isAccount) {
		
		do {
			
			// 退货交易卡号为空时提示刷卡
			paynoMsrflag = false;
			if (!paynoMSR())
				return false;

			// 设置交易类型,isAccount=true是记账,false是撤销
			if (isAccount) {
				if (SellType.ISSALE(saleBS.saletype))
					mzkreq.type = "PAY"; // 消费,减
				else
					mzkreq.type = "REFUND"; // 退货,加
			}
			else
			{
				//取消或者删除付款方式
				mzkreq.type = "CANCEL";
			}
			
			// 保存交易数据进行交易
			if (!setRequestDataByAccount()) 
			{
				if (paynoMsrflag) {
					salepay.payno = "";
					continue;
				}
				
				return false;
			}

			// 记录面值卡交易日志
			// bld = mzkAccountLog(false, null, mzkreq, mzkret);

			/*
			 * mzkreq.type = "PAY"; mzkreq.seqno =
			 * Convert.toLong("635443116578347718"); mzkreq.termno =
			 * ConfigClass.CashRegisterCode; mzkreq.mktcode = "0001";
			 * mzkreq.syyh = GlobalInfo.posLogin.gh; mzkreq.syjh = "0608";
			 * mzkreq.fphm = 3; mzkreq.invdjlb = ((salehead != null) ?
			 * salehead.djlb : ""); mzkreq.paycode = ((paymode != null) ?
			 * paymode.code : ""); mzkreq.je = 0.02; mzkreq.track2 =
			 * "284091807938704871"; mzkreq.str1 =
			 * ManipulateDateTime.getCurrentDate().replaceAll("/","");
			 */
			
			// 发送交易请求
			if (!sendMzkSale(mzkreq, mzkret)) {
				if (paynoMsrflag) {
					salepay.payno = "";
					continue;
				}
				return false;
			}
			
			/*// 先写冲正文件
			if (!writeMzkCz()) {
				if (paynoMsrflag) {
					salepay.payno = "";
					continue;
				}
				return false;
			}*/

			// 记录应答信息, batch标记本付款方式已记账,这很重要
			saveAccountMzkResultToSalePay();

			// 记账完成操作,可用于记录记账日志或其他操作
			//return mzkAccountFinish(isAccount, bld);
			
			return true;
		} while (true);
	}

	public void setRequestDataByFind(String track1, String track2, String track3) {

		// 得到消费序号
		long seqno = getMzkSeqno();
		if (seqno <= 0)
			return;
		// 根据磁道生成查询请求包

		mzkreq.type = "";
		mzkreq.seqno = seqno;
		mzkreq.termno = ConfigClass.CashRegisterCode;
		mzkreq.mktcode = GlobalInfo.sysPara.mktcode;
		mzkreq.syyh = GlobalInfo.posLogin.gh;
		mzkreq.syjh = ConfigClass.CashRegisterCode;
		mzkreq.fphm = GlobalInfo.syjStatus.fphm;
		mzkreq.invdjlb = ((salehead != null) ? salehead.djlb : "");
		mzkreq.paycode = ((paymode != null) ? paymode.code : "");
		// mzkreq.je = 0;
		mzkreq.track1 = track1;
		mzkreq.track2 = track2;
		mzkreq.track3 = track3;
		mzkreq.passwd = "";
		mzkreq.memo = "";
		mzkreq.str1 = ManipulateDateTime.getCurrentDate().replaceAll("/", "");
	}

	// 保存交易数据进行交易
	protected boolean setRequestDataByAccount() {
		// 得到消费序号
		long seqno = getMzkSeqno();
		if (seqno <= 0) return false;

		//冲正不需要取seqno
		if(!mzkreq.type.equals("CANCEL"))
		{
			// 打消费交易包
			mzkreq.seqno = seqno;
		}
		else
		{
			//冲正的时候取原交易的seqno
			if(salepay != null && salepay.payno != null && salepay.payno.length() > 0 )
			{
				mzkreq.seqno = Convert.toLong(salepay.payno);
			}
		}
		
		// mzkreq.je = salepay.ybje;
		mzkreq.syjh = ConfigClass.CashRegisterCode;
		
		//mzkreq.mktcode = GlobalInfo.sysPara.mktcode;
		
		String mkt = GlobalInfo.sysPara.mktcode;
		
		//转换门店号
		if(GlobalInfo.sysPara.isnewmktcode.equals("Y"))
		{
			String a = mkt.substring(0,1);
			
			if(a.equals("6"))
			{
				a = "D";
			}
			else
			{
				 a = ManipulateStr.numToLetter(a).toUpperCase();
			}
			
			mzkreq.mktcode = a + mkt.substring(1,mkt.length());
		}
		else
		{
			mzkreq.mktcode = mkt;
		}
		
		if (!SellType.ISSALE(salehead.djlb)) {
			mzkreq.fphm = Convert.toLong(salehead.yfphm);
			mzkreq.syyh = salehead.str7;
		} else {
			mzkreq.fphm = GlobalInfo.syjStatus.fphm;
			mzkreq.syyh = GlobalInfo.posLogin.gh;
		}
		// mzkreq.paycode = salepay.paycode;
		mzkreq.invdjlb = ((salehead != null) ? salehead.djlb : "");

		// 告诉后台过程磁道信息是存放的是卡号,只采用卡号记账方式,不使用磁道记账方式
		mzkreq.track1 = "CARDNO";
		// mzkreq.track2 = salepay.payno;
		mzkreq.str1 = ManipulateDateTime.getCurrentDate().replaceAll("/", "");
		
		return true;
	}

	protected void saveAccountMzkResultToSalePay()
	{
		salepay.str1 = mzkret.str1;
		salepay.str2 = mzkret.str2;
		salepay.str3 = mzkret.str3;
		salepay.str4 = mzkret.str4;
		
		if (SellType.ISSALE(saleBS.saletype))
			salepay.payno = String.valueOf(mzkreq.seqno);	  		//记录当前交易号
		else
			salepay.payno = mzkreq.track2;	  		//记录当前交易号

		salepay.memo = mzkreq.track2;				  			//记录当前的券号		
	}
	
	public String generateUrlParameters(MzkRequestDef req, MzkResultDef ret,String key,String id,String extend)
	{
		String payname = null;
		
		try {
			
			payname = URLEncoder.encode(paymode.name, "utf-8");
			
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			new MessageBox(e1.getMessage());
			payname = "%E7%A7%BB%E5%8A%A8%E6%94%AF%E4%BB%98";//移动支付
		}
		
		String[] urlpara = { "_input_charset=utf-8", "datestr=" + req.str1,
				"dynamic_id=" + req.track2, "dynamic_id_type=" + payname, "extend=" + extend,
				"invno=" + req.fphm, "market=" + req.mktcode,
				"opertype=" + req.type, "posid=" + req.syjh,
				"saleseqno=" + id,
				"subcompany=" + GlobalInfo.sysPara.jygs, "total_fee=" + req.je
		};
		
		String[] signs = { "_input_charset=utf-8", "datestr=" + req.str1,
				"dynamic_id=" + req.track2, "dynamic_id_type=" + paymode.name, "extend=" + extend,
				"invno=" + req.fphm, "market=" + req.mktcode,
				"opertype=" + req.type, "posid=" + req.syjh,
				"saleseqno=" + id,
				"subcompany=" + GlobalInfo.sysPara.jygs, "total_fee=" + req.je
		};

		StringBuffer sb = new StringBuffer();
		for (int j = 0; j < signs.length; j++) {
			if (j == 0) {
				sb.append(signs[j]);
			} else {
				sb.append("&");
				sb.append(signs[j]);
			}
		}
		PosLog.getLog(this.getClass()).info(ManipulateDateTime.getCurrentTime() + "[para]="+ sb.toString());
		String sign = Md5Tools.GetMD5Code(sb.toString() + key);
		PosLog.getLog(this.getClass()).info(ManipulateDateTime.getCurrentTime() + "[key]=" + key);
		PosLog.getLog(this.getClass()).info(ManipulateDateTime.getCurrentTime() + "[sign]="+ sign);
		System.out.println("[key]="+key);
		System.out.println("[sign]="+sign);
		
		sb = new StringBuffer();
		for (int j = 0; j < urlpara.length; j++) {
			if (j == 0) {
				sb.append(urlpara[j]);
			} else {
				sb.append("&");
				sb.append(urlpara[j]);
			}
		}
		
		return sb.toString() + "&sign=" + sign + "&sign_type=MD5";
	}

	public boolean openUrl(String url,StringBuffer para)
	{
		HttpPost httppost = new HttpPost(url);
						
		PosLog.getLog(this.getClass()).info(
				ManipulateDateTime.getCurrentTime() + ":urlvalue【" + para.toString()
						+ "】");
		
		StringEntity entity = null;
		
		try {
			
			entity = new StringEntity(para.toString());

			HttpClient httpclient = new DefaultHttpClient();
			entity.setContentType("application/x-www-form-urlencoded");
			httppost.setEntity(entity);

			HttpResponse response;
			response = httpclient.execute(httppost);

			// 检验状态码，如果成功接收数据
			int code = response.getStatusLine().getStatusCode();
			para.delete(0, para.length());
			if (code == 200) 
			{
				para.append(EntityUtils.toString(response.getEntity()));
			}
			
		} catch (UnsupportedEncodingException e) {
			para.delete(0, para.length());
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			para.delete(0, para.length());
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
		
	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret) {

		// SEARCH：查询
		// PAY：下单
		// CANCEL：撤消/冲正
		// REFUND : 退款
				
		String id = String.valueOf(req.seqno);//这里先把序号转换为string类型。退货时如果输入交易号退货前面为0赋值给long类型会有问题。
		String extend = paymode.code;//销售的EXTEND传入支付编码,退货传入流水号;
		
		if(req.type.equals("REFUND"))
		{
			extend = id;
			id = req.track2;
//			req.track2 = "0";
			if((req.str2 !=null && req.str2.length() > 0) && (req.str3 != null && req.str3.length() > 0))
			{
				req.str1 = req.str3;
				req.syjh = req.str2;
			}
		}
		else if(req.type.equals("PAY")  || req.type.equals("RULEPAY"))
		{
			//记录冲正,微信券实际下账的type为RULEPAY,第一次PAY请求不构成下账
			if (!writeMzkCz()) 
			{	
				return false;
			}
		}
		
		HashMap mapN = readZfbFile();
		
		String key = mapN.get("key").toString();

		String url = mapN.get("url").toString();

		String aliurl = null;
		
		StringBuffer bearXml = null;
		
		String xmldata = null;
		
		boolean retcode = false;
		boolean isContinue = false;
		int continue_count = 0;
		//循环执行
		while(true)
		{
			isContinue = false;
			
			//记录每次请求的日志
			bld = mzkAccountLog(false, null, req, ret);

			aliurl = generateUrlParameters(req, ret, key, id, extend);
			
			if(CommonMethod.isNull(aliurl)) return false;
			
			bearXml = new StringBuffer(aliurl);
			
			if(openUrl(url, bearXml))
			{
				PosLog.getLog(this.getClass()).info( 
						ManipulateDateTime.getCurrentTime() + ":resultXml【"
								+ bearXml + "】");
				
				if (!CommonMethod.isNull(bearXml.toString())) 
				{
					// 获得阿里接口返回值
					xmldata = ResultData(bearXml.toString());
					
					if (xmldata != null) 
					{
						String splits[] = xmldata.split("\\|");
						String xmldata1[] = splits[0].split(";");
						System.out.println(xmldata);
						if (xmldata1[0].equals("F")) 
						{	
							//删除当前的冲正
							deleteMzkCz();
							
							new MessageBox(xmldata1[1]);
							
							bld.retcode = "99";
							bld.retmsg = Language.apply("交易失败" + xmldata1[1]);

							if(!CommonMethod.isNull(req.type) && req.type.equals("RULEPAY"))
							{
								//取消用券的商品
								markCommods(wcc_rule,Cbcp_WCCRuleDef.WCC_CALC_MARK_MATCH + wcc_couponno,Cbcp_WCCRuleDef.WCC_CALC_MARK_FREE);								
							}
							
							retcode = false;
						} 
						else if (xmldata1[0].equals("T")) 
						{
							if (xmldata1[1].equals("SUCCESS")) 
							{								
								ret.cardno = String.valueOf(req.seqno);

								bld.retcode = "00";
								bld.retmsg = Language.apply("交易成功");

//								if(!CommonMethod.isNull(mzkreq.type) && mzkreq.type.equals("RULEPAY"))
//								{									
//									mzkret.str2 = splits[1].toString();//payrules 
//									mzkret.str3 = splits[2].toString();//paychannels 
//									mzkret.str4 = getPaycodeFromChannels(mzkret.str3);									
//								}
								if(splits.length > 1) mzkret.str2 = splits[1].toString();//payrules 
								if(splits.length > 2)
								{	
									mzkret.str3 = splits[2].toString();//payrules 
									mzkret.str4 = getPaycodeFromChannels(mzkret.str3);									
								}

								
								retcode = true;								
							}
							else if (xmldata1[1].equals("WAITING")) 
							{
								if(SellType.ISBACK(saleBS.saletype))
									req.type = "REFUNDSEARCH";
								else
									req.type = "SEARCH";
								
								bld.retcode = "01";
								bld.retmsg = Language.apply("等待顾客付款");
								
								isContinue = true;
							}
							else if (xmldata1[1].equals("TRADE_NOT_ENABLE")) 
							{
								if(SellType.ISBACK(saleBS.saletype))
									req.type = "REFUNDSEARCH";
								else
									req.type = "SEARCH";
								
								bld.retcode = "02";
								bld.retmsg = Language.apply("等待2秒在查询");

								isContinue = true;
							}
							else if (xmldata1[1].equals("RETRULE") && req.type.equals("PAY"))
							{
								//删除当前的"PAY"冲正
								//RULEPAY没有记录冲正,所以不能删除
								//deleteMzkCz();
								
								if(splits[1].toString().length() > 0)
								{
									//splits[1] = payrule
									if(parseRetRule(splits[1]))
									{
										if(findWCCRules(wcc_billno))
										{
											//当前券号
											wcc_couponno = mzkreq.track2;
											
											if(rulesISMatchNew())
											{
												req.type = "RULEPAY";
												
												//如果当前券的余额小于输入的金额,那么请求金额为券余额
												if(wcc_value < req.je)
												{	
													req.je = wcc_value;
												}
												else
												{
													 wcc_value = req.je;
												}
												
												bld.retcode = "03";
												bld.retmsg = Language.apply("满足规则,进行RULEPAY");
												
												isContinue = true;
											}
											else
											{
												new MessageBox("商品不满足规则单【"+wcc_billno+"】,不能使用该券！");
												
												bld.retcode = "03";
												bld.retmsg = Language.apply("商品不满足规则单【"+wcc_billno+"】,不能使用该券");
												
												retcode = false;
											}
										}
										else
										{
											new MessageBox("查询规则单【"+wcc_billno+"】规则明细失败！");

											bld.retcode = "03";
											bld.retmsg = Language.apply("查询规则单【"+wcc_billno+"】规则明细失败");
											
											retcode = false;
										}
									}
									else
									{
										new MessageBox("返回的规则单【"+wcc_billno+"】解析失败！");
										
										bld.retcode = "03";
										bld.retmsg = Language.apply("返回的规则单【"+wcc_billno+"】解析失败");
										
										retcode = false;
									}
								}
								else
								{
									new MessageBox("服务端没有返回规则单号！");
									
									bld.retcode = "03";
									bld.retmsg = Language.apply("服务端没有返回规则单号");
									
									retcode = false;
								}								
							}
							else
							{	
								bld.retcode = "04";
								bld.retmsg = Language.apply("服务端返回未知的状态");
								
								retcode = false;
							}
						}
						else
						{
							bld.retcode = "05";
							bld.retmsg = Language.apply("服务端返回不为T和F");
	
							retcode = false;
						}
					} 
					else 
					{
						new MessageBox("解析返回数据失败,当前交易失败！");

						bld.retcode = "06";
						bld.retmsg = Language.apply("解析返回数据失败,当前交易失败");
						
						retcode = false;
					}
				} 
				else 
				{
					//如果退货,发送REUNDSERCH
					if(SellType.ISBACK(saleBS.saletype))
					{
						mzkreq.type = "REFUNDSEARCH";
						
						bld.retcode = "07";
						bld.retmsg = Language.apply("发送请求成功，但是没有返回数据,退货发起REFUNDSEARCH");
						
						isContinue = true;
					}
					else
					{
						
						bld.retcode = "08";
						bld.retmsg = Language.apply("发送请求成功，但是没有返回数据");
						
						retcode = false;
					}
				}
			}
			else
			{
				new MessageBox("向服务端发起请求失败,当前交易失败！");
				
				bld.retcode = "XX";
				bld.retmsg = Language.apply("向服务端发起请求失败,当前交易失败");

				retcode = false;
			}
			
			//更新日志
			bld = mzkAccountLog(true, bld, req, ret);
			
			if(isContinue)
			{
				continue_count ++;
					
				form.payName.setText("[" + paymode.code + "]" + paymode.name + "  第{"+ continue_count +"}次请求");
				
				if(continue_count > 5)
				{
					return false;
				}
				
				try {
					
					Thread.sleep(2000);//等待两秒钟
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				continue;
			}

			return retcode;
		}
	}

	public boolean sendMzkSale1(MzkRequestDef req, MzkResultDef ret) {

		// SEARCH：查询
		// PAY：下单
		// CANCEL：撤消/冲正
		// REFUND : 退款
		boolean retcode = true;
		
		// 记录面值卡交易日志
		bld = mzkAccountLog(false, null, req, ret);
		
		String id = String.valueOf(req.seqno);//这里先把序号转换为string类型。退货时如果输入交易号退货前面为0赋值给long类型会有问题。
		String extend = paymode.code;//销售的EXTEND传入支付编码,退货传入流水号;
		if(req.type.equals("REFUND"))
		{
			extend = id;
			id = req.track2;
			req.track2 = "0";
			if((req.str2 !=null &&req.str2.length() > 0) && (req.str3 != null &&req.str3.length() > 0))
			{
				req.str1 = req.str3;
				req.syjh = req.str2;
			}
		}
		else if(req.type.equals("PAY")  || req.type.equals("RULEPAY"))
		{
			//记录冲正,微信券实际下账的type为RULEPAY,第一次PAY请求不构成下账
			if (!writeMzkCz()) 
			{	
				return false;
			}
		
		}
		
		/*
		String payname = null;
		
		try {
			
			payname = URLEncoder.encode(paymode.name, "utf-8");
			
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			new MessageBox(e1.getMessage());
			payname = "%E7%A7%BB%E5%8A%A8%E6%94%AF%E4%BB%98";//移动支付
		}
		
		String[] urlpara = { "_input_charset=utf-8", "datestr=" + req.str1,
				"dynamic_id=" + req.track2, "dynamic_id_type=" + payname, "extend=" + extend,
				"invno=" + req.fphm, "market=" + req.mktcode,
				"opertype=" + req.type, "posid=" + req.syjh,
				"saleseqno=" + id,
				"subcompany=" + GlobalInfo.sysPara.jygs, "total_fee=" + req.je
		};
		
		String[] signs = { "_input_charset=utf-8", "datestr=" + req.str1,
				"dynamic_id=" + req.track2, "dynamic_id_type=" + paymode.name, "extend=" + extend,
				"invno=" + req.fphm, "market=" + req.mktcode,
				"opertype=" + req.type, "posid=" + req.syjh,
				"saleseqno=" + id,
				"subcompany=" + GlobalInfo.sysPara.jygs, "total_fee=" + req.je
		};

		StringBuffer sb = new StringBuffer();
		for (int j = 0; j < signs.length; j++) {
			if (j == 0) {
				sb.append(signs[j]);
			} else {
				sb.append("&");
				sb.append(signs[j]);
			}
		}

		HashMap mapN = readZfbFile();
		String key = mapN.get("key").toString();
//		Md5Tools getMD5 = new Md5Tools();
		String sign = Md5Tools.GetMD5Code(sb.toString() + key);

		
		sb = new StringBuffer();
		for (int j = 0; j < urlpara.length; j++) {
			if (j == 0) {
				sb.append(urlpara[j]);
			} else {
				sb.append("&");
				sb.append(urlpara[j]);
			}
		}
		
		String aliurl = sb.toString() + "&sign=" + sign + "&sign_type=MD5";
		 */
		
		HashMap mapN = readZfbFile();
		String key = mapN.get("key").toString();

		String url = mapN.get("url").toString();
		HttpPost httppost = new HttpPost(url);
		
		String aliurl = generateUrlParameters(req, ret, key, id, extend);
		
		if(CommonMethod.isNull(aliurl)) return false;
		
		PosLog.getLog(this.getClass()).info(
				ManipulateDateTime.getCurrentTime() + ":urlvalue【" + aliurl
						+ "】");
		
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
			}

			PosLog.getLog(this.getClass()).info( 
					ManipulateDateTime.getCurrentTime() + ":resultXml【"
							+ bearXml + "】");

			if (bearXml != null && bearXml.length() > 0) {
				// 获得阿里接口返回值
				String xmldata = ResultData(bearXml);
				
				if (xmldata != null) 
				{
					String splits[] = xmldata.split("\\|");
					String xmldata1[] = splits[0].split(";");
					System.out.println(xmldata);
					if (xmldata1[0].equals("F")) 
					{
						
						new MessageBox(xmldata1[1]);
						
						return false;
					} 
					else if (xmldata1[0].equals("T")) 
					{
						if (xmldata1[1].equals("SUCCESS")) {

							if(req.type != null && req.type.equals("CANCEL"))
							{
								return true;
							}
							else
							{
								/*
	//							成功先写冲正文件
								if (!writeMzkCz()) {
									return false;
								}
								*/
							}
							
							//如果是退货,卡号记录seqno;
							if(req.type != null && req.type.equals("REFUND"))
							{
								ret.cardno = String.valueOf(req.seqno);
								new MessageBox(xmldata1[2]);
								
								return true;
							}
							
						} else if (xmldata1[1].equals("WAITING")) {
							while (true) {

								/*try {
									Thread.sleep(2000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}*/
								new MessageBox("等待顾客付款！");
								if(SellType.ISBACK(saleBS.saletype))
									req.type = "REFUNDSEARCH";
								else
									req.type = "SEARCH";
								
								return sendMzkSale(mzkreq, mzkret);
							}
						}
						else if (xmldata1[1].equals("TRADE_NOT_ENABLE")) {
							while (true) {

							/*	try {
									Thread.sleep(2000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}*/
								new MessageBox("等待2秒在查询！");
								if(SellType.ISBACK(saleBS.saletype))
									req.type = "REFUNDSEARCH";
								else
									req.type = "SEARCH";
								
								return sendMzkSale(mzkreq, mzkret);
							}
						}
						else if (req.type.equals("PAY") && xmldata1[1].equals("RETRULE"))
						{
							//删除当前的"PAY"冲正
							deleteMzkCz();
							
							if(splits[1].toString().length() > 0)
							{
								//splits[1] = payrule
								if(parseRetRule(splits[1]))
								{
									if(findWCCRules(wcc_billno))
									{
										//当前券号
										wcc_couponno = mzkreq.track2;
										
										if(rulesISMatch())
										{
											req.type = "RULEPAY";
											
											//如果当前券的余额小于输入的金额,那么请求金额为券余额
											if(wcc_value < req.je)
											{	
												req.je = wcc_value;
											}

											if(sendMzkSale(req, ret))
											{
												//把参与的商品打上已经使用标记
												markCommods(Cbcp_WCCRuleDef.WCC_CALC_MARK_MATCH + wcc_couponno,Cbcp_WCCRuleDef.WCC_CALC_MARK_USED + wcc_couponno);
												
												//记录断点
												this.saleBS.writeBrokenData();
												
												salepay.ybje = Double.parseDouble(saleBS.getPayMoneyByPrecision(wcc_value, paymode));
												salepay.je = ManipulatePrecision.doubleConvert(salepay.ybje * salepay.hl, 2, 1);
												salepay.payno = String.valueOf(req.seqno);	  //记录当前交易号
												salepay.idno = String.valueOf(wcc_rule.seqno);//记录当前规则流水
												salepay.memo = mzkreq.track2;				  //记录当前的券号
												
												mzkret.str2 = splits[1].toString();//payrules 
												mzkret.str3 = splits[2].toString();//paychannels 

												if(saleBS.saleHead.hykh == null || saleBS.saleHead.hykh.isEmpty())
												{	
													saleBS.saleHead.hykh = wcc_cardno;
													saleBS.saleEvent.setVIPInfo(wcc_billno);
												}
												
												return true;
											}
											else
											{	
												markCommods(Cbcp_WCCRuleDef.WCC_CALC_MARK_MATCH + wcc_couponno,Cbcp_WCCRuleDef.WCC_CALC_MARK_FREE);
												return false;
											}
										}
										else
										{
											new MessageBox("商品不满足规则单【"+wcc_billno+"】,不能使用该券！");
											return false;
										}
									}
									else
									{
										new MessageBox("查询规则单【"+wcc_billno+"】规则明细失败！");
										return false;
									}
							
								}
							}
							else
							{
								new MessageBox("服务器没有返回规则单号！");
								return false;
							}
						}
					}

					mzkret.cardno = String.valueOf(req.seqno);	
					mzkret.str1 = splits[0].toString();//payrules 

					return true;
					
				} else {
					new MessageBox("没有接收到交易返回数据,当前交易失败！");
					return false;
				}
			} 
			else 
			{
				//如果退货,发送REUNDSERCH
				if(SellType.ISBACK(saleBS.saletype))
				{
					mzkreq.type = "REFUNDSEARCH";
					return sendMzkSale(mzkreq, mzkret);
				}
				else
					return false;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		bld = mzkAccountLog(true, null, req, ret);
		
		return false;
	}

	public BankLogDef mzkAccountLog(boolean success, BankLogDef bld, MzkRequestDef req, MzkResultDef ret)
	{
		try
		{
			//if (GlobalInfo.sysPara.usemzklog != 'Y')
			//	return null;

			if (!success)
			{
				// 记录开始交易日志
				BankLogDef newbld = new BankLogDef();
				Object obj = GlobalInfo.dayDB.selectOneData("select max(rowcode) from BANKLOG");
				if (obj == null)
					newbld.rowcode = 1;
				else
					newbld.rowcode = Integer.parseInt(String.valueOf(obj)) + 1;
				newbld.rqsj = ManipulateDateTime.getCurrentDateTime();
				newbld.syjh = req.syjh;
				newbld.fphm = req.fphm;
				newbld.syyh = req.syyh;
				newbld.type = req.type;
				newbld.je = req.je;
				if (req.type.equals("PAY"))
					newbld.typename = Language.apply("消费");
				else if (req.type.equals("SEARCH"))
					newbld.typename = Language.apply("消费查询");
				else if (req.type.equals("CANCEL"))
					newbld.typename = Language.apply("消费冲正");
				else if (req.type.equals("REFUND"))
					newbld.typename = Language.apply("退货");
				else if (req.type.equals("REFUNDSEARCH"))
					newbld.typename = Language.apply("退货查询");
				else if (req.type.equals("RULEPAY"))
					newbld.typename = Language.apply("券付款");
				else
					newbld.typename = Language.apply("未知");
				newbld.classname = this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1);
				newbld.trace = req.seqno;
				newbld.oldrq = req.mktcode + "|" + req.invdjlb;
				newbld.bankinfo = req.paycode;
				newbld.cardno = req.track2;
				newbld.memo = req.memo;
				newbld.oldtrace = 0;

				newbld.crc = "";
				newbld.retcode = "";
				newbld.retmsg = "";
				newbld.retbz = 'N';
				newbld.net_bz = 'N';
				newbld.allotje = 0;

				if (!AccessDayDB.getDefault().writeBankLog(newbld))
				{
					new MessageBox(Language.apply("记录储值卡交易日志失败!"));
					return null;
				}

				return newbld;
			}
			else
			{
				if (bld == null)
					return null;

				// 更新交易应答数据
				if (ret != null && !CommonMethod.isNull(ret.cardno))
				{	
					bld.je = req.je;
					bld.memo = ret.str1;
					bld.memo1 = ret.str2;
					bld.memo2 = ret.str3;					
					bld.authno = ret.cardno;
				}
				
				bld.retbz = 'Y';
				bld.net_bz = 'N';
				
				if (NetService.getDefault().sendBankLog(bld)) bld.net_bz = 'Y';
				
				AccessDayDB.getDefault().updateBankLog(bld);
				
				return bld;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	
	public HashMap readZfbFile() {
		BufferedReader br = null;
		String configName = GlobalVar.ConfigPath + "//CbbhZfb.ini";
		String line = null;
		Map map = new HashMap();
		br = CommonMethod.readFile(configName);

		try {
			while ((line = br.readLine()) != null) {
				String[] row = line.split("=");
				if (row[0].trim().equals("key")) {
					map.put("key", row[1].trim());
				} else if (row[0].trim().equals("url")) {
					map.put("url", row[1].trim());
				}
			}
			return (HashMap) map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 解析返回xml字符串
	public String ResultData(String xmlValue) {
		// xmlValue="<?xml version='1.0'
		// encoding='utf-8'?><pay><is_success>T</is_success><result_code>WAITING</result_code><result_des>下单并支付成功</result_des><buyer_user_id>2088302340390455</buyer_user_id><trade_no>2014012311001004450019518182</trade_no><sign>ab37a7c88e51f966422162c9fabcff43</sign><sign_type>MD5</sign_type></pay>";
		// xmlValue="<?xml version='1.0'
		// encoding='utf-8'?><pay><is_success>T</is_success><result_code>SUCCESS</result_code><result_des>total:0.01,actual:0.01,coupon:0,channels_count:1,channels:[{id:0,name:'ALIPAYACCOUNT',fee:0.01}]</result_des><buyer_user_id>783075601,maxun518</buyer_user_id><trade_no>783070412670156</trade_no><sign>689f102fa3f33a925e6ae565e97eec9d</sign><sign_type>MD5</sign_type></pay>";
		String xmlstr = "";
		Document document = null;
		Element element = null;
		try {
			document = DocumentHelper.parseText(xmlValue);
		} catch (DocumentException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
		element = document.getRootElement();

		System.out.println("根节点：" + element.getName());

		String is_success = element.elementTextTrim("is_success");
		String result_code = element.elementTextTrim("result_code");
		if (is_success.equals("T")) {
			String result_des = element.elementTextTrim("result_des");
			if (result_code.equals("FAIL"))
				return "F;" + result_des;
			String buyer_user_id = element.elementTextTrim("buyer_user_id");
			String trade_no = element.elementTextTrim("trade_no");
			String sign = element.elementTextTrim("sign");
			String sign_type = element.elementTextTrim("sign_type");
			System.out.println(is_success + ";" + result_code + ";"
					+ result_des + ";" + buyer_user_id + ";" + trade_no + ";"
					+ sign + ";" + sign_type);
			xmlstr = is_success + ";" + result_code + ";" + result_des + ";"
					+ buyer_user_id + ";" + trade_no + ";" + sign + ";"
					+ sign_type + "|";
			
			//xmlstr = ;;;;;|payrule|paychannels
			//payrule = ;;;;
			//paychannel = ;;;;:::,:::,:::
			
			//解析payrule
			String text = null;
			Element p_element = element.element("payrule");
			if(p_element != null)
			{
				text = p_element.elementText("ye");
				xmlstr += (text == null ? "":text) + ";";
				text = p_element.elementText("mz");
				xmlstr += (text == null ? "":text) + ";";
				text = p_element.elementText("rules");
				xmlstr += (text == null ? "":text) + ";";
				text = p_element.elementText("cardno");
				xmlstr += (text == null ? "":text) + "|";
			}
			else
			{
				xmlstr += " ; ; ; |";
			}
				
			//解析paychannels
			p_element = element.element("paychannels");
			
			if(p_element != null)
			{
				text = p_element.elementText("total");
				xmlstr += (text == null ? "":text) + ";";
				text = p_element.elementText("actual");
				xmlstr += (text == null ? "":text) + ";";
				text = p_element.elementText("coupon");
				xmlstr += (text == null ? "":text) + ";";
				text = p_element.elementText("channels_count");
				xmlstr += (text == null ? "":text) + ";";
			
				p_element = p_element.element("channels");
				
				if(p_element != null)
				{	
					List channels = p_element.elements("channel");
					for(Iterator it = channels.iterator();it.hasNext();)
					{
						p_element = (Element)it.next();
						text = p_element.elementText("name");
						xmlstr += (text == null ? "":text) + ":";
						text = p_element.elementText("mode");
						xmlstr += (text == null ? "":text) + ":";
						text = p_element.elementText("no");
						xmlstr += (text == null ? "":text) + ":";
						text = p_element.elementText("fee");
						xmlstr += (text == null ? "":text) + ",";
					}
				}
				else
				{	
					xmlstr += " :::,";
				}
			}
			else
			{
				xmlstr += " ; ; ; ;";
			}
			
			xmlstr = xmlstr.substring(0, xmlstr.length() - 1);

		} else {
			String error = element.elementTextTrim("error");
			xmlstr = is_success + ";" + error;
		}

		return xmlstr;
	}

	
	public String getPaycodeFromChannels(String channels)
	{
		String text = null;
		String[] values = channels.split(";"); 
		
		if(values.length > 4) 
		{	
			text = values[4].toString();
			if(text != null && text.length() > 0)
			{
				values = text.split(":");
				if(values.length > 3) 
				{
					//mode
					text = values[1].toString();
					if(text != null && text.length() > 0) return text;
				}
			}
		}

		return "";
	}
	
	public String getDisplayStatusInfo() {
		// yinliang test
		// mzkret.func = "Y01Y";
		// mzkret.value3 = 100;

		try {
			String line = "";
			if (!checkMzkIsBackMoney()) {
				// 如果卡有回收功能,显示回收提示
				double ye = -1;
				if (isRecycleType()) {
					// 定义了回收功能键模式
					if (NewKeyListener.searchKeyCode(GlobalVar.MzkRecycle) > 0) {
						if (recycleStatus) {
							ye = mzkret.ye;
							line = "有效金额:"
									+ ManipulatePrecision.doubleToString(ye)
									+ " 元\n\n可用金额: "
									+ ManipulatePrecision
											.doubleToString(mzkret.ye) + " 元";
						} else {
							ye = mzkret.ye - mzkret.value3;
							line = "有效金额:"
									+ ManipulatePrecision.doubleToString(ye)
									+ " 元\n\n可用金额: "
									+ ManipulatePrecision
											.doubleToString(mzkret.ye) + " 元";
						}
					} else {
						ye = mzkret.ye;
						line = "有效金额:" + ManipulatePrecision.doubleToString(ye)
								+ " 元\n\n可用金额: "
								+ ManipulatePrecision.doubleToString(mzkret.ye)
								+ " 元";
					}
				}

				/*
				 * // 计算并显示付款限制 if (!this.allowpayjealready) this.allowpayje =
				 * ManipulatePrecision.doubleConvert(calcPayRuleMaxMoney() /
				 * paymode.hl); if (this.allowpayje >= 0 && ye >= 0)
				 * this.allowpayje = Math.min(allowpayje,ye); else if (ye >= 0)
				 * this.allowpayje = ye; if (this.allowpayje >= 0) {
				 * this.allowpayje = Math.max(this.allowpayje,
				 * saleBS.getDetailOverFlow(this.allowpayje)); String allowstr =
				 * "付款限制:" + ManipulatePrecision.doubleToString(allowpayje) + "
				 * 元"; if (line.length() > 0) line += "\n\n" + allowstr; else
				 * line += allowstr; }
				 */
			} else {
				if (mzkret.money > 0) {
					line = "面值为:"
							+ ManipulatePrecision.doubleToString(mzkret.money)
							+ " 元\n\n退款后卡余额不能大于面值";
				} else {
					line = "";
				}
			}

			// 显示面值卡返回的提示信息
			if (mzkret.str3 != null && mzkret.str3.length() > 0) {
				if (line.length() > 0)
					line += "\n" + mzkret.str3;
				else
					line += mzkret.str3;
			}

			return line;
		} catch (Exception er) {
			er.printStackTrace();
			return "";
		}
	}

	protected String getDisplayAccountInfo() {
		return "请 刷 卡";
	}

	protected boolean needFindAccount() {
		return true;
	}


	public void specialDeal(Cbcp_PaymentZfbEvent event) {
	}

	public void setMoneyVisible(Cbcp_PaymentZfbEvent paymentMzkEvent) {

	}

	public void setPwdAndYe(Cbcp_PaymentZfbEvent event, KeyEvent e) {
		if (!SellType.ISSALE(saleBS.saletype)) {
			if (!flag) {
				// 输入金额
				if (e != null)
					e.data = "focus";
				event.moneyTxt.setFocus();
				event.moneyTxt.selectAll();
				return;
			}
			if (event.yeTips.getText().equals("原收银机")) {
				event.yeTips.setText("原日期号");
				event.yeTxt.setVisible(false);
				event.pwdTxt.setVisible(true);
				if (saleBS.saleHead.rqsj.length() <= 0) {
					event.pwdTxt.setText(String.valueOf(ManipulateDateTime.getCurrentDate().replaceAll("/", "")));
				} else {
					event.pwdTxt.setText(saleBS.saleHead.rqsj.replaceAll("/","").substring(0,8));
				}

				mzkreq.str3 = event.pwdTxt.getText();
				if (e != null)
					e.data = "focus";
				event.pwdTxt.setFocus();
				event.pwdTxt.selectAll();
				flag = false;
			} else {

				// 显示密码
				event.yeTips.setText("原收银机");
				event.yeTxt.setVisible(false);
				event.pwdTxt.setVisible(true);
				if (saleBS.saleHead.ysyjh.length() <= 0) {
					event.pwdTxt.setText(salehead.syjh);
				} else {
					event.pwdTxt.setText(salehead.ysyjh);
				}

				mzkreq.str2 = event.pwdTxt.getText();
				
				if (e != null)
					e.data = "focus";
				event.pwdTxt.setFocus();
				event.pwdTxt.selectAll();
			}
		} else {
			// 显示余额
			event.yeTips.setText("账户余额");
			event.yeTxt.setVisible(true);
			event.pwdTxt.setVisible(false);
			event.yeTxt.setText(ManipulatePrecision
					.doubleToString(getAccountYe()));

			// 输入金额
			if (e != null)
				e.data = "focus";
			event.moneyTxt.setFocus();
			event.moneyTxt.selectAll();
		}
	}

	public boolean checkMzkMoneyValid() {
		try {
			// 退货扣回付款时付款算消费
			if (checkMzkIsBackMoney()) {
				// 检查退款后余额是否大于卡面值
				if (mzkret.money > 0 && mzkret.ye > mzkret.money) {
					new MessageBox(Language.apply("退款余额不能超过面值!"));

					return false;
				}
			} else {
				/*
				 * // 检查金额是否超过卡余额 if
				 * (ManipulatePrecision.doubleCompare(salepay.ybje, mzkret.ye,
				 * 2) > 0) { new MessageBox(Language.apply("卡内余额不足!"));
				 * 
				 * return false; }
				 */

				// 检查金额是否超过限制金额
				if (this.allowpayje >= 0
						&& ManipulatePrecision.doubleCompare(salepay.ybje,
								this.allowpayje, 2) > 0) {
					new MessageBox(Language.apply("输入金额超过允许支付限额!"));

					return false;
				}

				// 判断是否是可回收的卡类型
				if (!recycle()) {
					return false;
				}
			}

			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public String GetMzkCzFile() {
		if(mzkreq.type.equals("REFUND") || mzkreq.type.equals("CANCEL"))mzkreq.seqno = Convert.toLong(mzkreq.track2);
		return ConfigClass.LocalDBPath + "/Zfb_" + mzkreq.seqno + ".cz";
	}

	public boolean sendAccountCzData(MzkRequestDef req, String czfile,
			String czname) {
		// 根据冲正文件的原交易类型转换冲正数据包
 		if (req.type.equals("PAY") /*|| req.type.equals("REFUND") ||*/ || req.type.equals("RULEPAY")) {
			req.type = "CANCEL"; // 消费冲正,加
		} else {
			new MessageBox(Language.apply("冲正文件的交易类型无效，请检查冲正文件"));
			return false;
		}

		// 冲正记录
		// String czmsg = "发起[" + czname + "]冲正:" + req.type + "," + req.fphm +
		// "," + req.track2 + "," + ManipulatePrecision.doubleToString(req.je) +
		// ",返回:";
		String czmsg = Language.apply("发起") + "[" + czname + "]"
				+ Language.apply("冲正:") + req.type + "," + req.fphm + ","
				+ req.track2 + "," + ManipulatePrecision.doubleToString(req.je)
				+ "," + Language.apply("返回:");

		// 记录面值卡交易日志
		//bld = mzkAccountLog(false, null, req, null);

		// 发送冲正交易
		MzkResultDef ret = new MzkResultDef();

		if (!sendMzkSale(req, ret)) {
			// 记录日志表明发送过冲正数据
			AccessDayDB.getDefault().writeWorkLog(czmsg + Language.apply("失败"),
					StatusType.WORK_SENDERROR);

			return false;
		} else {
			// 记录应答日志
			//mzkAccountLog(true, bld, req, ret);

			// 记录日志表明发送过冲正数据
			AccessDayDB.getDefault().writeWorkLog(czmsg + Language.apply("成功"),
					StatusType.WORK_SENDERROR);

			// 冲正发送成功,删除冲正文件
			deleteMzkCz(czfile);
			return true;
		}
	}

	// true: 明码显示
	// false: 密码显示
	public boolean passwdMode() {
		return true;
	}

	protected boolean saveFindMzkResultToSalePay() {
		
		salepay.batch = "";
		salepay.payno = mzkret.cardno;
		salepay.kye = mzkret.ye;

		return true;
	}

	public void doAfterFail(Cbcp_PaymentZfbEvent zfbEvent) {
		zfbEvent.shell.close();
		zfbEvent.shell.dispose();
	}

	// 自动计算付款金额,并生成付款方式
	public boolean AutoCalcMoney() {
		if (SellType.ISSALE(saleBS.saletype))
			return true;
		else
			return false;
	}

	public void showAccountYeMsg() {

	}

	// 判断是否是支付宝
	public boolean isCzFile(String filename) {
		if (filename.startsWith("Zfb_") && filename.endsWith(".cz")) {
			return true;
		} else {
			return false;
		}
	}
	
	/*
	public static Object jsonList(String jsonlist,String para){
		
		JSONObject objjson =  JSONObject.fromObject(jsonlist);
		System.out.println(objjson.toString());
		
		Object obj = (Object) objjson.get(para);
		
		return obj;
	}
	
	public static Map arrayJsontoMap(String jsonString,String[] para){
		JSONArray array = JSONArray.fromObject(jsonString); 
        Map map = new HashMap(); 
		for(int i = 0; i < array.size(); i++){
			 JSONArray jsonObject = JSONArray.fromObject(array.get(i)); 
			JSONObject obj = (JSONObject)jsonObject.get(0);
			Object keyvalue = "";
			for (int j = 0; j < para.length; j++) {
				String key = para[j];
				keyvalue = keyvalue.toString() + (Object)obj.get(key)+",";
			}

			map.put(i, keyvalue);
		} 
		return map; 
	}
	*/
	public boolean collectAccountClear()
	{
		return true;
	}
	
	public boolean createSalePay(String money)
	{	
		mzkreq.je = Convert.toDouble(money);
		
		if(super.createSalePay(money))
		{
			if(!CommonMethod.isNull(mzkreq.type) && mzkreq.type.equals("RULEPAY"))
			{
				//把参与的商品打上已经使用标记
				calcCommodJeFree(Cbcp_WCCRuleDef.WCC_CALC_MARK_MATCH + wcc_couponno);
				markCommods(wcc_rule,Cbcp_WCCRuleDef.WCC_CALC_MARK_MATCH + wcc_couponno,Cbcp_WCCRuleDef.WCC_CALC_MARK_USED + wcc_couponno);
							
				salepay.ybje = Double.parseDouble(saleBS.getPayMoneyByPrecision(wcc_value, paymode));
				salepay.je = ManipulatePrecision.doubleConvert(salepay.ybje * salepay.hl, 2, 1);
								
				salepay.idno = String.valueOf(wcc_rule.seqno);//记录当前规则流水			

				//如果当前交易产生了会员卡号
				if(CommonMethod.isNull(saleBS.saleHead.hykh) && !CommonMethod.isNull(wcc_cardno))
				{	
					salepay.str6 = wcc_cardno;
					saleBS.saleHead.hykh = wcc_cardno;
					saleBS.saleEvent.setVIPInfo(wcc_cardno);
				}
			}
			
			//刷新付款方式
			if(!CommonMethod.isNull(salepay.str4))
			{
				PayModeDef  payModeDef  = DataService.getDefault().searchPayMode(salepay.str4);
				if(payModeDef != null)
				{	
					salepay.paycode = payModeDef.code;
					salepay.payname = payModeDef.name;				
				}
			}
			
			//记录断点
			saleBS.writeBrokenData();
			return true;
		}
		return false;
	}
	
	public boolean parseRetRule(String rule)
	{
		if(rule == null || rule.isEmpty()) return false;
		
		String text = null;
		String[] values = rule.split(";"); 
		
		if(values.length > 0) 
		{	
			text = values[0];
			if(text != null && text.length() > 0)  wcc_value = Double.parseDouble(text);
		}
		
		if(values.length > 2) 
		{
			text = values[2];
			if(text != null && text.length() > 0)  wcc_billno = text.trim();
		}
		
		if(values.length > 3) 
		{
			text = values[3];
			if(text != null && text.length() > 0)  wcc_cardno = text.trim();
		}
		
		return true;
	}
	
	public void parseChannels(String channels)
	{
		if(channels == null || channels.isEmpty()) return;
		String[] values;
		values = channels.split(";");
		mzkret.str3 = values[4].toString();
	}
	
	//查找微信券(wechatcoupon)的收券规则
	public boolean findWCCRules(String billno)
	{
		wcc_rules =  new Vector<Cbcp_WCCRuleDef>();
		if(!((Cbcp_DataService)DataService.getDefault()).findWCCRules(billno, wcc_rules)) return false;
		return true;
	}
	
	//判断规则是否匹配
	public boolean rulesISMatch()
	{
		int i,j,k;
		double je,zje;
		
		SaleGoodsDef commod = null;
		
		for(i = 0; i < wcc_rules.size() ; ++ i)
		{
			zje = 0;
			
			wcc_rule = (Cbcp_WCCRuleDef)wcc_rules.elementAt(i);
			//规则本身就是排除的,则不计算
			if(ruleISRemove(wcc_rule)) continue;			
			//循环商品列表
			for(j = 0 ; j < saleBS.saleGoods.size(); ++j)
			{
				//商品已经用券则不计算
				if(commodISUsed(wcc_rule.mode,j)) continue;
				
				//商品是否满足规则
				if(ruleMatchCommod(wcc_rule,j))
				{	
					//是否需要计算排除的规则
					if(ruleNeedCalcRemove(wcc_rule))
					{
						//循环规则,判断当前商品是否需要排除
						for(k = 0;k < wcc_rules.size(); ++ k)
						{
							if(k == i ) continue;
							if(ruleMatchCommod(wcc_rules.elementAt(k),j) && ruleISRemove(wcc_rules.elementAt(k))) break;
						}
						
						//是排除商品,则不计算
						if(k < wcc_rules.size()) continue; 
					}
					
					commod = ((SaleGoodsDef)(saleBS.saleGoods.elementAt(j)));
					je = commod.hjje - commod.hjzk;
					
					//判断单个商品是否满足规则
					if(je >= wcc_rule.value)
					{ 
						markCommod(wcc_rule,j,Cbcp_WCCRuleDef.WCC_CALC_MARK_MATCH + wcc_couponno);
						
						markCommods(wcc_rule,Cbcp_WCCRuleDef.WCC_CALC_MARK_SUM + wcc_couponno,Cbcp_WCCRuleDef.WCC_CALC_MARK_FREE);
						
						return true;
					}
					
					//否则商品参与到金额统计
					zje += je;
					markCommod(wcc_rule,j,Cbcp_WCCRuleDef.WCC_CALC_MARK_SUM + wcc_couponno);
					
					//合计金额满足
					if(zje >= wcc_rule.value)
					{
						markCommods(wcc_rule,Cbcp_WCCRuleDef.WCC_CALC_MARK_SUM + wcc_couponno,Cbcp_WCCRuleDef.WCC_CALC_MARK_MATCH + wcc_couponno);
						
						return true;	
					}
				}
			}
					
			//还原当前已经统计过的商品标示
			markCommods(wcc_rule,Cbcp_WCCRuleDef.WCC_CALC_MARK_SUM + wcc_couponno,Cbcp_WCCRuleDef.WCC_CALC_MARK_FREE);
		}
		
		wcc_rule = null;
		return false;
	}
			
	//判断规则是否匹配
	public boolean rulesISMatchNew()
	{
		int i,j,k;
		double je,zje;
		
		SaleGoodsDef commod = null;
		for(i = 0; i < wcc_rules.size() ; ++ i)
		{
			zje = 0;
			
			wcc_rule = (Cbcp_WCCRuleDef)wcc_rules.elementAt(i);
			
			//规则本身就是排除的,则不计算
			if(ruleISRemove(wcc_rule)) continue;			
			
			//循环商品列表
			for(j = 0 ; j < saleBS.saleGoods.size(); ++j)
			{
				//非全场商品已经用券则不计算
				if(ruleMatchISNotAll(wcc_rule) && commodISUsed(wcc_rule.mode,j)) continue;
				
				//商品是否满足规则
				if(ruleMatchCommod(wcc_rule,j))
				{	
					//是否需要计算排除的规则
					if(ruleNeedCalcRemove(wcc_rule))
					{
						//循环规则,判断当前商品是否需要排除
						for(k = 0;k < wcc_rules.size(); ++ k)
						{
							if(k == i ) continue;
							if(ruleMatchCommod(wcc_rules.elementAt(k),j) && ruleISRemove(wcc_rules.elementAt(k))) break;
						}
						
						//是排除商品,则不计算
						if(k < wcc_rules.size()) continue; 
					}
					
					if(ruleMatchISNotAll(wcc_rule))
					{
						commod = ((SaleGoodsDef)(saleBS.saleGoods.elementAt(j)));
						je = commod.hjje - commod.hjzk;
						
						//判断单个商品是否满足规则
						if(je >= wcc_rule.value)
						{ 
							markCommod(wcc_rule,j,Cbcp_WCCRuleDef.WCC_CALC_MARK_MATCH + wcc_couponno);
							
							markCommods(wcc_rule,Cbcp_WCCRuleDef.WCC_CALC_MARK_SUM + wcc_couponno,Cbcp_WCCRuleDef.WCC_CALC_MARK_FREE);
							
							return true;
						}
					}	
					else
					{
						je = getCommodJeFree(j);
					}
					
					if(je <= 0) continue;
						
					//否则商品参与到金额统计
					zje += je;
					markCommod(wcc_rule,j,Cbcp_WCCRuleDef.WCC_CALC_MARK_SUM + wcc_couponno);
					
					//合计金额满足
					if(zje >= wcc_rule.value)
					{
						markCommods(wcc_rule,Cbcp_WCCRuleDef.WCC_CALC_MARK_SUM + wcc_couponno,Cbcp_WCCRuleDef.WCC_CALC_MARK_MATCH + wcc_couponno);
						
						return true;	
					}
				}
			}
			
			//还原当前已经统计过的商品标示
			markCommods(wcc_rule,Cbcp_WCCRuleDef.WCC_CALC_MARK_SUM + wcc_couponno,Cbcp_WCCRuleDef.WCC_CALC_MARK_FREE);
		}
		
		wcc_rule = null;
		
		return false;
	}
	
	
	public boolean ruleMatchISNotAll(Cbcp_WCCRuleDef rule)
	{
		if(rule.mode.equals(Cbcp_WCCRuleDef.WCC_CALC_MODE_ALL))
			return false;
		else return true;
	}
	
	public double getCommodJeFree(int index)
	{
		return ((SpareInfoDef)this.saleBS.goodsSpare.elementAt(index)).num1;
	}
	
	public void setCommodFree()
	{
		int i;
		SaleGoodsDef good = null;
		SpareInfoDef spare = null;
		
		for(i = 0; i < saleBS.saleGoods.size(); ++ i)
		{
			good = (SaleGoodsDef)saleBS.saleGoods.elementAt(i);
			spare =(SpareInfoDef)saleBS.goodsSpare.elementAt(i);
			
			setCommodJeFree(spare,good.hjje - good.hjzk);
			setCommodMarkFree(spare);
		}
	}

	public void setCommodMarkFree(SpareInfoDef sp)
	{
		sp.memo3 = Cbcp_WCCRuleDef.WCC_CALC_MARK_FREE;
		sp.memo2 = Cbcp_WCCRuleDef.WCC_CALC_MARK_FREE;
	}
	
	public void setCommodJeFree(SpareInfoDef sp,double je)
	{
		sp.num1 = je;
	}
	
	public void calcCommodJeFree(String ma)
	{
		if(wcc_rule == null) return ;
		
		if(ruleMatchISNotAll(wcc_rule)) return;
		
		int i;SpareInfoDef sp = null;
		double je = wcc_rule.value;
		
		for(i = 0;i < saleBS.goodsSpare.size(); ++ i)
		{
			sp = (SpareInfoDef)this.saleBS.goodsSpare.elementAt(i);
			if(sp.memo3.equals(ma))
			{
				if(Double.compare(sp.num1,je) > 0 )
				{
					sp.num1 -= je;
				}
				else
				{
					je -= sp.num1;
					sp.num1 = 0;
				}
			}
		}
	}
	
	public boolean commodISUsed(String mode,int index)
	{
		SpareInfoDef sp = (SpareInfoDef)this.saleBS.goodsSpare.elementAt(index);

		if(!mode.equals(Cbcp_WCCRuleDef.WCC_CALC_MODE_ALL) && sp.memo2.substring(0, 1).equals(Cbcp_WCCRuleDef.WCC_CALC_MARK_USED) ||
			mode.equals(Cbcp_WCCRuleDef.WCC_CALC_MODE_ALL) && sp.memo3.substring(0, 1).equals(Cbcp_WCCRuleDef.WCC_CALC_MARK_USED)
		  )
			return true;
		
		return false;
	}
	
	public boolean ruleMatchCommod(Cbcp_WCCRuleDef rule,int index)
	{		
		//全场通用
		if(rule.mode.equals(Cbcp_WCCRuleDef.WCC_CALC_MODE_ALL)) return true;
		
		SaleGoodsDef salegood = (SaleGoodsDef)(saleBS.saleGoods.elementAt(index));
		
		if(rule.mode.equals(Cbcp_WCCRuleDef.WCC_CALC_MODE_GDID))
		{
			if (rule.gdid.equals(salegood.code)) return true;
			else return false;
		}
		else if(rule.mode.equals(Cbcp_WCCRuleDef.WCC_CALC_MODE_CATID_AND_PPID))
		{
			if (!rule.catid.equals(salegood.catid.substring(0, rule.catid.length()))) return false;
			if (rule.ppid.equals(salegood.ppcode)) return true;
		}
		else if(rule.mode.equals(Cbcp_WCCRuleDef.WCC_CALC_MODE_CATID))
		{
			if (rule.catid.equals(salegood.catid.substring(0, rule.catid.length()))) return true;
			else return false;
		}
		else if(rule.mode.equals(Cbcp_WCCRuleDef.WCC_CALC_MODE_PPID))
		{
			if (rule.ppid.equals(salegood.ppcode)) return true;
			else return false;
		}
		
		return false;
	}
	
	public boolean ruleISRemove(Cbcp_WCCRuleDef rule)
	{
		if(rule.isyq == 'N') return true;
		return false;
	}
	
	public boolean ruleNeedCalcRemove(Cbcp_WCCRuleDef rule)
	{
		if(rule.mode.equals(Cbcp_WCCRuleDef.WCC_CALC_MODE_GDID)) return false;
		return true;
	}
	
	public void markCommod(Cbcp_WCCRuleDef rule,int index,String mark)
	{
		if(rule.mode.equals(Cbcp_WCCRuleDef.WCC_CALC_MODE_ALL))
			((SpareInfoDef)this.saleBS.goodsSpare.elementAt(index)).memo3 = mark;
		else	
			((SpareInfoDef)this.saleBS.goodsSpare.elementAt(index)).memo2 = mark;
	}
	
	public void markCommods(String ma,String mb)
	{
		int i;
		SpareInfoDef sp;
		for(i = 0;i< this.saleBS.goodsSpare.size();++ i)
		{	
			sp = (SpareInfoDef)this.saleBS.goodsSpare.elementAt(i);
			if(sp.memo3.equals(ma)) 
				sp.memo3 = mb;
			if(sp.memo2.equals(ma)) 
				sp.memo2 = mb;
		}	
	}
	
	public void markCommods(Cbcp_WCCRuleDef rule,String ma,String mb)
	{
		int i;
		SpareInfoDef sp;
		for(i = 0;i< this.saleBS.goodsSpare.size();++ i)
		{	
			sp = (SpareInfoDef)this.saleBS.goodsSpare.elementAt(i);
			if(rule.mode.equals(Cbcp_WCCRuleDef.WCC_CALC_MODE_ALL))
			{	
				if(sp.memo3.equals(ma)) sp.memo3 = mb;
			}
			else
			{	
				if(sp.memo2.equals(ma)) sp.memo2 = mb;
			}
		}
	}
	
	public void cancelMarks()
	{
		int i;
		SpareInfoDef sp;
		String couponno = null;
		for(i = 0;i< this.saleBS.goodsSpare.size();++ i)
		{	
			//salepay.memo里面存的是支付的券号
			if(salepay.memo != null)
			{
				sp = (SpareInfoDef)this.saleBS.goodsSpare.elementAt(i);
				couponno = sp.memo2.substring(1);
				if(couponno != null && couponno.equals(salepay.memo)) 
					sp.memo2 = Cbcp_WCCRuleDef.WCC_CALC_MARK_FREE;
				
				couponno = sp.memo3.substring(1);
				if(couponno != null && couponno.equals(salepay.memo)) 
					sp.memo3 = Cbcp_WCCRuleDef.WCC_CALC_MARK_FREE;
			}
		}
		
		if(!CommonMethod.isNull(salepay.str6) && !CommonMethod.isNull(saleBS.saleHead.hykh) && salepay.str6.equals(saleBS.saleHead.hykh))
		{
			saleBS.saleHead.hykh = "";
			saleBS.saleEvent.setVIPInfo(saleBS.saleHead.hykh);
		}
	}
}
