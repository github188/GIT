package custom.localize.Hycs;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.rpc.holders.DoubleHolder;
import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.StringHolder;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Ybsj.CrmConfirmMzkSale;
import custom.localize.Ybsj.CrmMzkSaleResult;
import custom.localize.Ybsj.CrmVipPoint;
import custom.localize.Ybsj.CrmVipPoint.CrmVipPointResult;

public class Hycs_common
{
	String endpoint = "";
	
//	int cardNM = 0; //卡内码
	
	private static Hycs_common father = new Hycs_common();
	
	public static Hycs_common getDefault()
	{
		return father;
	}
	
	public boolean getCustomer(CustomerDef cust, String track)
	{
//		 4H参数控制使用会员卡是否必须联网使用（Y/N）
		if (GlobalInfo.isOnline )
		{
				StringBuffer verifyCode = new StringBuffer();
				String pw = "";
				try
				{
					if (GlobalInfo.sysPara.cardpasswd.equals("A"))
					{
						TextBox txt = new TextBox();

						if (!txt.open("请输入会员验证码", "VerifyCode", "需先输入验证码后才能查询卡资料", verifyCode, 0, 0, false, TextBox.AllInput))
							{return false;}
						else
							pw = verifyCode.toString();
					}
					
					if(!findHYInfo(cust ,track ,pw))
					{
						new MessageBox("查找会员信息失败");
						return false;
					}
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
					new MessageBox("查找会员发生异常");
					return false;
				}
			return true;
		}
		else
		{
			new MessageBox("查询会员卡必须联网");
			
			return false; 
			
		}
	}
	
	public boolean findHYInfo(CustomerDef customer ,String track ,String pw)
	{
		endpoint = Hycs_common.getEndpointURL();
		if(endpoint.equals(""))
		{
			new MessageBox("会员卡访问地址为空，请检查W8参数！");
			
			return false;
		}
		
		PosWebServiceSoapProxy proxy = new PosWebServiceSoapProxy(endpoint);
		
		VipCardHolder Card = new VipCardHolder();
		javax.xml.rpc.holders.BooleanHolder getVipCardResult = new javax.xml.rpc.holders.BooleanHolder();
		StringHolder msg = new StringHolder();
		VipCard cust = new VipCard();
		try
		{
			proxy.getVipCard(0, track, "", pw, GlobalInfo.sysPara.mktcode, getVipCardResult, msg, Card);
			
			cust = Card.value;
			
			if(getVipCardResult.value)
			{
				if(cust != null)
				{
					customer.code = cust.getCardCode();     //卡号
					customer.track = track.trim();          //磁道
					customer.str3 = String.valueOf(cust.getCardId());     //卡内码
					
					customer.name = cust.getVipName();     //持卡人姓名
					customer.valuememo = cust.getValidCent(); //当前可以积分
					
					customer.type = String.valueOf(cust.getCardTypeId());   //卡类型
					
					customer.isjf = ( (cust.isCanCent() == true)? 'Y' : 'N' );      //是否有积分账户
					customer.iszk = ( (cust.isCanDisc() == true)? 'Y' : 'N' );      //是否可以会员折扣
					
					customer.status = "Y";
					customer.ishy = 'Y';
						
					customer.zkl = 1;
					
//		                customer.func = "N";
//						customer.value1 = type.value1;
//						customer.value2 = type.value2;
//						customer.value3 = type.value3;
//						customer.value4 = type.value4;
//						customer.value5 = type.value5;
//						customer.valstr1 = type.valstr1;
//						customer.valstr2 = type.valstr2;
//						customer.valstr3 = type.valstr3;
//						customer.valnum1 = type.valnum1;
//						customer.valnum2 = type.valnum2;
//						customer.valnum3 = type.valnum3;
//						customer.memo = "查询成功";
					
					if (ConfigClass.DebugMode)
					{
						PosLog.getLog(this.getClass()).info("[会员卡查询成功]卡号:" + cust.getCardCode() + "门店号" + GlobalInfo.sysPara.mktcode );
					}
				}
				else
				{
					new MessageBox("查询失败：未收到卡信息");
					
					return false;
				}
				
				return true;
			}
			else
			{
				String errormsg = "卡信息查询失败，" + msg.value.toString();
				new MessageBox( errormsg );
				PosLog.getLog(this.getClass()).info("[会员卡查询失败]磁道号:" + track + "门店号:" + GlobalInfo.sysPara.mktcode + "查询失败原因:" + errormsg);
				
				return false;
			}
			
		} catch (Exception e) {
			PosLog.getLog(this.getClass()).info("[会员卡查询异常]磁道号:" + track + "门店号:" + GlobalInfo.sysPara.mktcode + "查询异常原因:" + e.getMessage());
			
			e.printStackTrace();
			new MessageBox("查询会员卡信息发生异常");
			return false;
		}    
	}
	
	
	public boolean GetCashCard(MzkRequestDef mzkreq, MzkResultDef mzkret)
	{
		endpoint = Hycs_common.getEndpointURL();
		if(endpoint.equals(""))
		{
			new MessageBox("储值卡访问地址为空，请检查W8参数！");
			
			return false;
		}
		
		PosWebServiceSoapProxy proxy = new PosWebServiceSoapProxy(endpoint);
		
		CashCardHolder card = new CashCardHolder();
		javax.xml.rpc.holders.BooleanHolder getCashCardResult = new javax.xml.rpc.holders.BooleanHolder();
		StringHolder msg = new StringHolder();
		CashCard cardinfo = new CashCard();
		try
		{
		 // 0磁道  ，整磁道 ，空 ， 验证磁道后三位 ， 密码空 ， 记录我们的门店号
			proxy.getCashCard(0,mzkreq.track2,"",mzkreq.passwd ,"" ,mzkreq.mktcode , getCashCardResult, msg, card);
			
			cardinfo = card.value;
			
			if(getCashCardResult.value)
			{
				if (ConfigClass.DebugMode)
				{
					PosLog.getLog(this.getClass()).info("[储值卡查询成功]" + mzkreq.str1 + "  卡号:" + cardinfo.getCardCode() + "门店号" + mzkreq.mktcode + "卡余额" + cardinfo.getBalance());
				}
				
				if(cardinfo != null)
				{
					double money = Convert.toDouble(cardinfo.getBalance());
					if (money < 0)
					{
						new MessageBox("无可用余额,无法进行交易");
						return false;
					}
					
					mzkreq.str2 = "Y";
					mzkreq.memo = "查余成功";
			
					mzkret.cardno = cardinfo.getCardCode().trim();   //卡号
					mzkret.cardname = mzkreq.track2;                 //磁道
//					mzkret.cardname = ((paymode != null) ? paymode.name : "");
					mzkret.status = "Y";
					mzkret.ye = money;                                       //卡余额
					mzkret.str3 = String.valueOf(cardinfo.getCardTypeId());  //卡类型
					mzkreq.track1 = String.valueOf(cardinfo.getCardId());      //卡内码
//					cardNM = cardinfo.getCardId();
//					mzkret.ispw = 'Y';
				}
				else
				{
					new MessageBox("查询储值卡失败：未收到卡信息");
					
					return false;
				}
				
				return true;
			}
			else
			{
				String errormsg = "卡信息查询失败，" + msg.value.toString();
				new MessageBox( errormsg );
				PosLog.getLog(this.getClass()).info("[储值卡查询失败]" + mzkreq.str1 + "  磁道号:" + mzkreq.track2 + "门店号:" + mzkreq.mktcode + "失败原因:" + errormsg);
				
				return false;
			}
			
		} catch (Exception e) {
			PosLog.getLog(this.getClass()).info("[储值卡查询异常]" + mzkreq.str1 + "  磁道号:" + mzkreq.track2 + "门店号:" + mzkreq.mktcode + "异常原因:" + e.getMessage());
			
			e.printStackTrace();
			new MessageBox("查询储值卡信息发生异常");
			return false;
		}    
	}
	
	//准备储值卡支付交易
	public String PrepareTransCashCardPayment2( MzkRequestDef mzkreq )
	{	
		double money = mzkreq.je;
		if (mzkreq.type.equals("03"))
		{
			money = money * -1;
		}
		
		endpoint = Hycs_common.getEndpointURL();
		if(endpoint.equals(""))
		{
			new MessageBox("储值卡访问地址为空，请检查W8参数！");
			
			return null;
		}
		
		PosWebServiceSoapProxy proxy = new PosWebServiceSoapProxy(endpoint);
		
		IntHolder transId = new IntHolder();
		javax.xml.rpc.holders.BooleanHolder prepareTransCashCardPayment2Result = new javax.xml.rpc.holders.BooleanHolder();
		StringHolder msg = new StringHolder();
		
		CashCardPayment[] payments  = new CashCardPayment[1];
		for(int i = 0 ;i<payments.length; i++)
		{
			CashCardPayment payment = new CashCardPayment();
//			payment.setCardId(cardNM);//卡内码
			payment.setCardId(Convert.toInt(mzkreq.track1));//卡内码
			payment.setPayMoney(Convert.toDouble(money));          //支付金额
	
			payments[i] = payment;
		}
		
		String str = mzkreq.str1.substring(0, 4) + "-" + mzkreq.str1.substring(5, 7) + "-" + mzkreq.str1.substring(8, 10); //记帐日期(格式：yyyy-mm-dd)

		try
		{
			proxy.prepareTransCashCardPayment2(mzkreq.mktcode, mzkreq.syjh, (int)mzkreq.fphm, mzkreq.syyh, str, payments, prepareTransCashCardPayment2Result, msg, transId);
			
			String ID = String.valueOf(transId.value);   //交易ID
			
			if(prepareTransCashCardPayment2Result.value && !ID.equals("0"))
			{
				if (ConfigClass.DebugMode)
				{
					PosLog.getLog(this.getClass()).info("[储值卡预支付成功]" + mzkreq.str1 + "门店号" + mzkreq.mktcode + "交易ID" + ID + "记账日期" + str);
				}
				//记录交易ID，为确认支付做准备
				mzkreq.track3 = ID;
				
				return ID;
			}
			else
			{
				String errormsg = "预支付交易失败，" + msg.value.toString();
				new MessageBox( errormsg );
				PosLog.getLog(this.getClass()).info("[储值卡预支付失败]" + mzkreq.str1 + "  磁道号:" + mzkreq.track2 + "门店号:" + mzkreq.mktcode + "记账日期" + str + "失败原因:" + errormsg);
				return null;
			}
			
		} catch (Exception e) {
			PosLog.getLog(this.getClass()).info("[储值卡预支付异常]" + mzkreq.str1 + "  磁道号:" + mzkreq.track2 + "门店号:" + mzkreq.mktcode + "记账日期" + str + "异常原因:" + e.getMessage());
			
			e.printStackTrace();
			new MessageBox("准备支付储值卡失败发生异常");
			return null;	
		}    
	}
	
	
//	//确认储值卡支付交易
	public boolean ConfirmTransCashCardPayment(MzkRequestDef mzkreq)
	{
		double money = mzkreq.je;
		if (mzkreq.type.equals("03"))
		{
			money = money * -1;
		}
		
		endpoint = Hycs_common.getEndpointURL();
		if(endpoint.equals(""))
		{
			new MessageBox("储值卡访问地址为空，请检查W8参数！");
			
			return false;
		}
		
		PosWebServiceSoapProxy proxy = new PosWebServiceSoapProxy(endpoint);
		
		javax.xml.rpc.holders.BooleanHolder confirmTransCashCardPaymentResult = new javax.xml.rpc.holders.BooleanHolder();
		StringHolder msg = new StringHolder();
		
		try
		{   //CRM消费流水号（serverBillId）,没有传0
			proxy.confirmTransCashCardPayment(Convert.toInt(mzkreq.track3), 0, money, confirmTransCashCardPaymentResult, msg);
						
			if(confirmTransCashCardPaymentResult.value)
			{
				if (ConfigClass.DebugMode)
				{
					PosLog.getLog(this.getClass()).info("[储值卡确认支付成功]" + mzkreq.str1 + "  磁道号:" + mzkreq.track2 + "门店号:" + mzkreq.mktcode + "交易id" + mzkreq.track3 + "小票号" + mzkreq.fphm); 
				}
				
				return true;			
			}
			else
			{
				new MessageBox("交易失败:" + msg.value);
				
				PosLog.getLog(this.getClass()).info("[储值卡确认支付失败]" + mzkreq.str1 + "  磁道号:" + mzkreq.track2 + "门店号:" + mzkreq.mktcode + "交易id" + mzkreq.track3 + "小票号" + mzkreq.fphm + "失败原因:" + msg.value);
				return false;
			}
			
		} catch (Exception e) {
			PosLog.getLog(this.getClass()).info("[储值卡确认支付异常]" + mzkreq.str1 + "  磁道号:" + mzkreq.track2 + "门店号:" + mzkreq.mktcode + "交易id" + mzkreq.track3 + "小票号" + mzkreq.fphm  + "异常原因:" + e.getMessage());
			
			e.printStackTrace();
			new MessageBox("确认支付储值卡失败发生异常");
			return false;
		}    
	}
	
	
//	//取消储值卡支付交易
	public boolean CancelTransCashCardPayment(MzkRequestDef mzkreq)
	{
		double money = mzkreq.je;
		//只支持传正值
//		if (mzkreq.type.equals("03") || mzkreq.type.equals("04"))
//		{
//			money = money * -1;
//		}
		
		endpoint = Hycs_common.getEndpointURL();
		if(endpoint.equals(""))
		{
			new MessageBox("储值卡访问地址为空，请检查W8参数！");
			
			return false;
		}
		
		PosWebServiceSoapProxy proxy = new PosWebServiceSoapProxy(endpoint);
		
		javax.xml.rpc.holders.BooleanHolder cancelTransCashCardPaymentResult = new javax.xml.rpc.holders.BooleanHolder();
		StringHolder msg = new StringHolder();
		
		try
		{   //CRM消费流水号（serverBillId）,没有则传0
			proxy.cancelTransCashCardPayment(Convert.toInt(mzkreq.track3), 0, money, cancelTransCashCardPaymentResult, msg);
						
			if(cancelTransCashCardPaymentResult.value)
			{
				if (ConfigClass.DebugMode)
				{
					PosLog.getLog(this.getClass()).info("[储值卡撤销支付成功]" + mzkreq.str1 + "  磁道号:" + mzkreq.track2 + "门店号:" + mzkreq.mktcode + "交易id" + mzkreq.track3 + "小票号" + mzkreq.fphm); 
				}
				
				return true;			
			}
			else
			{
				new MessageBox("交易失败:" + msg.value);
				
				PosLog.getLog(this.getClass()).info("[储值卡撤销支付失败]" + mzkreq.str1 + "  磁道号:" + mzkreq.track2 + "门店号:" + mzkreq.mktcode + "交易id" + mzkreq.track3 + "小票号" + mzkreq.fphm + "失败原因:" + msg.value);
				return false;
			}
			
		} catch (Exception e) {
			PosLog.getLog(this.getClass()).info("[储值卡撤销支付异常]" + mzkreq.str1 + "  磁道号:" + mzkreq.track2 + "门店号:" + mzkreq.mktcode + "交易id" + mzkreq.track3 + "小票号" + mzkreq.fphm  + "异常原因:" + e.getMessage());
			
			e.printStackTrace();
			new MessageBox("确认支付储值卡失败发生异常");
			return false;
		}    
	}
	
	
	public boolean mzkSaleCZ(MzkRequestDef mzkreq, MzkResultDef mzkret)
	{
		try
		{
			// 02,04发送冲正
			if (mzkreq.type.equals("02") || mzkreq.type.equals("04"))
				return CancelTransCashCardPayment(mzkreq);

			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("储值卡消费发生异常");
			return false;
		}
	}
	
	
	//发送积分给商友后台
	public boolean UpdateVipCent(SaleHeadDef saleHead, double point)
	{
		endpoint = Hycs_common.getEndpointURL();
		if(endpoint.equals(""))
		{
			new MessageBox("储值卡访问地址为空，请检查W8参数！");
			
			return false;
		}
		
		PosWebServiceSoapProxy proxy = new PosWebServiceSoapProxy(endpoint);
		DoubleHolder validCent = new DoubleHolder();
		
		javax.xml.rpc.holders.BooleanHolder updateVipCentResult = new javax.xml.rpc.holders.BooleanHolder();
		StringHolder msg = new StringHolder();
		try
		{
			if (SellType.ISBACK(saleHead.djlb))
			{
//				point = point * -1;（后台算出的退货积分已经是负值）
				saleHead.ysje = saleHead.ysje * -1;
			}
			//updateType 31前台积分消费   saleHead.str4--会员ID
			proxy.updateVipCent(Convert.toInt(saleHead.str4), point, 31, saleHead.mkt, saleHead.syjh, (int)saleHead.fphm, updateVipCentResult, validCent, msg);
			
			if(updateVipCentResult.value)
			{
				if (ConfigClass.DebugMode)
				{
					PosLog.getLog(this.getClass()).info("[会员卡积分成功]" + "卡内码:" + saleHead.str4 + "门店号" + saleHead.mkt + "收银机号" + saleHead.syjh + "小票号" + saleHead.fphm + "本次积分" + point + "累计可用积分" + validCent.value);
				}
				saleHead.ljjf = validCent.value;//累计可用积分
				saleHead.bcjf = point;          //本次积分
				
				return true;
			}
			else
			{
				String errormsg = "发送积分失败，" + msg.value.toString();
				new MessageBox( errormsg );
				PosLog.getLog(this.getClass()).info("[会员卡积分失败]" + "卡内码:" + saleHead.memo + "门店号" + saleHead.mkt + "收银机号" + saleHead.syjh + "小票号" + saleHead.fphm + "积分失败原因:" + errormsg);
				
				return false;
			}
			
		} catch (Exception e) {
			PosLog.getLog(this.getClass()).info("[会员卡积分异常]" + "卡内码:" + saleHead.memo + "门店号" + saleHead.mkt + "收银机号" + saleHead.syjh + "小票号" + saleHead.fphm + "积分异常原因:" + e.getMessage());
			
			e.printStackTrace();
			new MessageBox("发送积分发生异常");
			return false;
		}    
		      
	}
	
	
	public static String getEndpointURL()
	{
		String endpoint = "";
		if(GlobalInfo.sysPara.WebserviceURL != null && !GlobalInfo.sysPara.WebserviceURL.equals(""))
		{
			endpoint = GlobalInfo.sysPara.WebserviceURL;
			
			return endpoint;
		}else
		{
			new MessageBox("储值卡访问地址为空，请检查W8参数！");
			
			return "";
		}
	}
}
