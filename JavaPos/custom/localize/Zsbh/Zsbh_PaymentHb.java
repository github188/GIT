package custom.localize.Zsbh;


import java.util.Vector;

import org.eclipse.swt.events.KeyEvent;

import com.api.Goods;
import com.api.RedEnvelopeApi;
import com.api.Result;
import com.api.Returns;
import com.api.Revoke;
import com.api.TradeFirst;
import com.api.TradeLater;
import com.api.TradeResult;
import com.api.WriteBack;
import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.PaymentCoupon;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Zsbh_PaymentHb extends PaymentCoupon {
	

	
	public SalePayDef inputPay(String money)
	{
		try
		{
			// 退货小票不能使用,退货扣回按销售算
			if (checkMzkIsBackMoney() && GlobalInfo.sysPara.thmzk != 'Y')
			{
//				new MessageBox("退货时不能使用" + paymode.name);
				new MessageBox(Language.apply("退货时不能使用{0}" ,new Object[]{paymode.name}));
				return null;
			}

			// 先检查是否有冲正未发送
			if (!sendAccountCz())
				return null;
			
			//是否通过外部设备读取卡号
			if(!autoFindCard()) return null;

			// 打开明细输入窗口
			new Zsbh_PaymentHbForm().open(this, saleBS);

			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;
	}
	
	public void setPwdAndYe(Zsbh_PaymentHbEvent event, KeyEvent e)
	{
		if (isPasswdInput())
		{
			// 显示密码
			event.yeTips.setText(getPasswdLabel());
			event.yeTxt.setVisible(false);
			event.pwdTxt.setVisible(true);
			event.yeTxt.setText(ManipulatePrecision.doubleToString(getAccountYe()));

			if (e != null)
				e.data = "focus";
			event.pwdTxt.setFocus();
			event.pwdTxt.selectAll();
		}
		else
		{
			// 显示余额
			event.yeTips.setText(Language.apply("扫手机码"));
			event.yeTxt.setVisible(true);
			event.pwdTxt.setVisible(false);
			event.yeTxt.setText(ManipulatePrecision.doubleToString(getAccountYe()));

			// 输入金额
			if (e != null)
				e.data = "focus";
			event.moneyTxt.setFocus();
			event.moneyTxt.selectAll();
		}
	}
	
	public boolean realAccountPay()
	{
//		if (GlobalInfo.sysPara.cardrealpay == 'Y')
//		{
			// 付款即时记账
			if (mzkAccount(true))
			{
				deleteMzkCz();

				return true;
			}
			else
			{
				return false;
			}
//		}
//		else
//		{
//			// 不即时记账
//			return true;
//		}
	}
	
	public boolean collectAccountPay()
	{
		// 如果不是即时记账,则集中记账
//		if (GlobalInfo.sysPara.cardrealpay != 'Y' || salepay.batch == null || salepay.batch.trim().length() <= 0)
//		{
		if(!SellType.ISSALE(salehead.djlb))
		{
			// 付款记账
			if (mzkAccount(true))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
//		}
//		else
//		{
			// 已记账,直接返回
			return true;
//		}
	}
	
	public void showAccountYeMsg()
	{
	
	}
	
	public boolean isPasswdInput()
	{
		return true;
	}
	
	public String GetMzkCzFile()
	{
		return ConfigClass.LocalDBPath + "/Hb_" + mzkreq.seqno + ".cz";
	}
	
//	 判断是否是红包
	public boolean isCzFile(String filename)
	{
		if (filename.startsWith("Hb_") && filename.endsWith(".cz"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean findMzk(String track1, String track2, String track3)
	{
		
		// 设置请求数据
		setRequestDataByFind(track1, track2, track3);

		return true;
	}
	
	public boolean checkMzkMoneyValid()
	{
		return true;
	}
	
	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
//		return DataService.getDefault().sendMzkSale(req, ret);
		if(SellType.ISSALE(salehead.djlb))
		{
			if(saleBS.salePayment.size() == 0)
			{
				return sendsaleone();
			}

			boolean one = true;
			
			for(int i =0;i<saleBS.salePayment.size();i++)
			{
				SalePayDef spd = (SalePayDef) saleBS.salePayment.elementAt(i);
				if(spd.paycode.equals(GlobalInfo.sysPara.hbPaymentCode))
				{
					one = false;
					break;
				}
			}
			if(one)
			{
				return sendsaleone();
			}
			else
			{
				return sensaletwo();
			}
		}
		else
		{
			//发送退货
			return sendback();
		}
	}
	
	//发送红包退货
	public boolean sendback()
	{
		
		try{
			if(saleBS.thFphm <= 0 && (saleBS.thSyjh == null||saleBS.thSyjh.length() <= 0))return true;
			Returns returns = new Returns();
			returns.setTid(String.valueOf(saleBS.thFphm));
			returns.setPid(saleBS.thSyjh);
			returns.setOid(ConfigClass.Market);
			returns.setTtid(String.valueOf(salehead.fphm));
			returns.setTpid(salehead.syjh);
			returns.setToid(salehead.mkt);

			StringBuffer msglog = new StringBuffer();
			msglog.append( "整单退货：request=【[returns]:tid="+returns.getTid()+"pid="+returns.getPid()+"oid="+returns.getOid()+"ttid="+returns.getTtid()+"tpid"+returns.getTpid()+"toid="+returns.getToid()+"】");
			PosLog.getLog(this.getClass()).error(msglog.toString());
			
			Result result = RedEnvelopeApi.redReturns(returns);
			
			msglog = new StringBuffer();
			msglog.append("整单退货：result=【[result]rcode="+result.getRcode()+"rdesc="+result.getRdesc()+"】");
			PosLog.getLog(this.getClass()).error(msglog.toString());
			
			if(result != null){
				int rcode = result.getRcode();
				String rdesc = result.getRdesc();
				if(rcode == 1)
				{
				
					saleBS.saleHead.yfphm = String.valueOf(saleBS.thFphm);
					saleBS.saleHead.ysyjh = saleBS.thSyjh;
				
					saleBS.thFphm = 0;
					saleBS.thSyjh = "";
//					成功先写冲正文件
					/*if (!writeMzkCz()) {
						return false;
					}*/
					return true;
				}
				else
				{
					new MessageBox(rdesc);
					return false;
				}
			}
		}catch (Exception e) {}
		
		return false;
	}
	
	protected String getDisplayAccountInfo()
	{
		return Language.apply("手 机 号");
	}
	
	public String getPasswdLabel()
	{
		return Language.apply("扫手机码");
	}
	
	protected boolean needFindAccount()
	{
		return true;
	}
	
	public int getAccountInputMode()
	{
		return TextBox.DoubleInput;
	}
	
	public void specialDeal(Zsbh_PaymentHbEvent event)
	{
	}
	
	public void doAfterFail(Zsbh_PaymentHbEvent mzkEvent)
	{
		mzkEvent.shell.close();
		mzkEvent.shell.dispose();
	}
	
	//发送首个红包消费
	public boolean sendsaleone()
	{
		try{
			TradeFirst tradefirst = new TradeFirst();
//			oid+pid+tid+yyyyMMddHHmmss
			//保存红包序号
			salehead.str6 = salehead.mkt+salehead.syjh+salehead.fphm+ManipulateDateTime.getCurrentDateTime().replaceAll("/", "").replaceAll(" ", "").replaceAll(":", "");
			mzkreq.str2 = salehead.str6;
			tradefirst.setRid(salehead.str6);
			tradefirst.setTid(String.valueOf(salehead.fphm));
			tradefirst.setPid(salehead.syjh);
			tradefirst.setOid(ConfigClass.Market);
			tradefirst.setOtype(1);
			tradefirst.setCard(salehead.hykh); //是会员
//			tradefirst.setCard("");//不是会员
			tradefirst.setTsum(ManipulatePrecision.doubleConvert(salehead.ysje,1,1));
			tradefirst.setCsum(ManipulatePrecision.doubleConvert(salehead.ysje-salehead.sjfk,1,1));
			tradefirst.setCode(mzkreq.passwd);
			tradefirst.setPhone(mzkreq.track2); //要求用户提供手机号码
//			tradefirst.setPhone(""); //不要求用户提供手机号码
			
			StringBuffer msglog = new StringBuffer();
			msglog.append( "首单红包：request=【[tradefirst]:otype="+tradefirst.getOtype()+"rid="+tradefirst.getRid()+"tid="+tradefirst.getTid()+"pid="+tradefirst.getPid()+"oid="+tradefirst.getOid()+"card="+tradefirst.getCard()+"tsum"+tradefirst.getTsum()+"csum"+tradefirst.getCsum()+"phone="+tradefirst.getPhone()+"code="+tradefirst.getCode());
			
			for (int i = 0; i < saleBS.saleGoods.size(); i++) {
				SaleGoodsDef gd = (SaleGoodsDef) saleBS.saleGoods.elementAt(i);
				GoodsDef gds = (GoodsDef)saleBS.goodsAssistant.elementAt(i);
				Goods goods1 = new Goods();
				goods1.setGid(gd.code);
				goods1.setGprice(gd.jg);
				double gdecprice = 0;
				int gnum = 0;
				//小数电子称商品传数量1
				if(gds.isdzc == 'Y' || ((gd.sl - (int)gd.sl) > 0))
				{
					gnum = 1;
					gdecprice = gd.hjje-gd.hjzk;
				}
				else
				{
					gnum = Convert.toInt(gd.sl);
					gdecprice = gd.jg-(gd.hjzk/gd.sl);
				}
				goods1.setGdecprice(gdecprice);
				goods1.setGnum(gnum);
				tradefirst.getGlist().add(goods1);
				msglog.append("[Goods]:gid="+goods1.getGdecprice()+"gprice="+goods1.getGprice()+"gdesprice="+goods1.getGdecprice()+"glist="+goods1.getGnum());
				
			}
			PosLog.getLog(this.getClass()).error(msglog.append("】").toString());

				TradeResult traderesult = RedEnvelopeApi.redTradeFirst(tradefirst);
				
			msglog = new StringBuffer();
			msglog.append("首单红包：result=【[traderesult]rcode="+traderesult.getRcode()+"rdesc="+traderesult.getRdesc()+"ysum="+traderesult.getYsum()+"】");
			PosLog.getLog(this.getClass()).error(msglog.toString());
				
				if(traderesult != null){
				int rcode = traderesult.getRcode();
				String rdesc = traderesult.getRdesc();
				String gid = traderesult.getGid();
				double ysum = traderesult.getYsum();
		
				if(rcode == 1 && ysum > 0)
//				if(true)
				{
					salepay.je = ysum;
					salepay.ybje = ysum;
					//成功先写冲正文件
					if (!writeMzkCz()) {
						return false;
					}
					return true;
				}
				else
				{
					new MessageBox(rdesc);
					return false;
				}
			} 
		}catch (Exception e) {}
		
		return false;
	}
	
	//发送整单红包消费
	public boolean sensaletwo()
	{
		try{
			TradeLater tradelater = new TradeLater();
			mzkreq.str2 = salehead.str6;//保存序号
			tradelater.setRid(salehead.str6);
			tradelater.setCsum(mzkreq.je);
			tradelater.setCode(mzkreq.passwd);
			tradelater.setPhone(mzkreq.track2); //要求用户提供手机号码
			//tradelater.setPhone(""); //不要求用户提供手机号码
			
			StringBuffer msglog = new StringBuffer();
			msglog.append("整单红包：request=【[tradelater]:rid="+tradelater.getRid()+"csum"+tradelater.getCsum()+"phone="+tradelater.getPhone()+"code="+tradelater.getCode()+"】");
			PosLog.getLog(this.getClass()).error(msglog.toString());
			
			TradeResult traderesult = RedEnvelopeApi.redTradeLater(tradelater);
			
			msglog = new StringBuffer();
			msglog.append("整单红包：result=【[traderesult]rcode="+traderesult.getRcode()+"rdesc="+traderesult.getRdesc()+"ysum="+traderesult.getYsum()+"】");
			PosLog.getLog(this.getClass()).error(msglog.toString());	
			
			if(traderesult != null){
				int rcode = traderesult.getRcode();
				String rdesc = traderesult.getRdesc();
				String gid = traderesult.getGid();
				double ysum = traderesult.getYsum();
			
				if(rcode == 1 && ysum > 0)
//				if(true)
				{
					salepay.je = ysum;
					salepay.ybje = ysum;
//					成功先写冲正文件
					if (!writeMzkCz()) {
						return false;
					}
					return true;
				}
				else
				{
					new MessageBox(rdesc);
					return false;
				}
			} 
		}catch (Exception e) {}
		
		return false;
	}
	
	public boolean mzkAccount(boolean isAccount)
	{
		do
		{
			// 退货交易卡号为空时提示刷卡
			paynoMsrflag = false;
			if (!paynoMSR())
				return false;

			// 设置交易类型,isAccount=true是记账,false是撤销
			if (isAccount)
			{
				if (SellType.SELLSIGN(salehead.djlb) > 0)
					mzkreq.type = "01"; // 消费,减
				else
					mzkreq.type = "03"; // 退货,加
			}
			else
			{
				if (SellType.SELLSIGN(salehead.djlb) > 0)
					mzkreq.type = "03"; // 退货,加
				else
					mzkreq.type = "01"; // 消费,减
			}

			// 保存交易数据进行交易
			if (!setRequestDataByAccount())
			{
				if (paynoMsrflag)
				{
					salepay.payno = "";
					continue;
				}
				return false;
			}

			/*// 先写冲正文件
			if (!writeMzkCz())
			{
				if (paynoMsrflag)
				{
					salepay.payno = "";
					continue;
				}
				return false;
			}*/

			// 记录面值卡交易日志
			BankLogDef bld = mzkAccountLog(false, null, mzkreq, mzkret);

			// 发送交易请求
			if (!sendMzkSale(mzkreq, mzkret))
			{
				if (paynoMsrflag)
				{
					salepay.payno = "";
					continue;
				}
				return false;
			}

			// 记录应答信息, batch标记本付款方式已记账,这很重要
			saveAccountMzkResultToSalePay();

			// 记账完成操作,可用于记录记账日志或其他操作
			return mzkAccountFinish(isAccount, bld);
		} while (true);
	}
	
	protected boolean saveFindMzkResultToSalePay()
	{
		if (!super.saveFindMzkResultToSalePay()) { return false; }

		mzkret.cardno = mzkreq.track2;//没有查询直接消费

		salepay.payno = mzkret.cardno;
		
		salepay.str1 = mzkreq.passwd;//红包号
		return true;
	}
	
//	 保存交易数据进行交易
	protected boolean setRequestDataByAccount()
	{
		if(salepay == null)salepay = new SalePayDef();
		return super.setRequestDataByAccount();
	}
	
	protected void saveAccountMzkResultToSalePay()
	{
		if(salepay == null)salepay = new SalePayDef();
		mzkreq.str2 = salehead.str6;//保存交易序号
		super.saveAccountMzkResultToSalePay();
	}
	
	
	//取消付款，撤销
	//整单红包撤销
     public boolean sendhbcancel(Vector salePayment) {
    	 try{
    		 
    		 //红包接口只支持整单红包撤销。如果存在多个红包付款，循环调用撤销，调用接口返回会不成功，撤销就会有问题
    		 if(salehead.str6.length() <= 0)return true;
    		 
    		 	double ysum=0;
    		 	String jh = "";
    			Revoke revoke = new Revoke();
    			revoke.setRid(salehead.str6);
    			if(salePayment.size() > 0)
    			{
    				for(int i =0;i<salePayment.size();i++)
    				{
    					SalePayDef sp = (SalePayDef)salePayment.elementAt(i);
    					
    					if(sp.paycode.equals(GlobalInfo.sysPara.hbPaymentCode))
    					{
    		    			revoke.getCodelist().add(sp.str1);
    		    			ysum = ysum+sp.je;
    		    			jh += "(codelist="+sp.str1+")";
    					}
    				}
    			}
    			revoke.setYsum(ysum);
    			
    		StringBuffer msglog = new StringBuffer();
    		msglog.append("整单撤销:request=【[revoke]:rid="+revoke.getRid()+"ysum"+revoke.getYsum()+"codelist="+jh+"】");
    		PosLog.getLog(this.getClass()).error(msglog.toString());		
    			
    		Result result = RedEnvelopeApi.redRevoke(revoke);

	    	msglog = new StringBuffer();
	    	msglog.append("整单撤销:result=【[result]:rcode="+ result.getRcode() +"rdesc"+result.getRdesc()+"】");
	    	PosLog.getLog(this.getClass()).error(msglog.toString());
	    	
    			if(result != null){
    				int rcode = result.getRcode();
    				String rdesc = result.getRdesc();	
    				if(rcode == 1)
    				{
    					salehead.str6 = "";
    					return true;
    				}
    				else
    				{
    					new MessageBox(rdesc);
    					return false;
    				}
    			}
    		}catch (Exception e) {}
    		
    		return false;
	}
     
     //发送红包冲正
     public boolean sendAccountCzData(MzkRequestDef req, String czfile, String czname)
 	{
    	 try{
    		 	mzkreq.seqno = req.seqno;
    			WriteBack writeback = new WriteBack();
    			writeback.setRid(req.str2);
    			writeback.setCode(req.passwd);
    			
    			StringBuffer msglog = new StringBuffer();
    			msglog.append( "整单冲正：request=【[writeback]:rid="+writeback.getRid()+"code="+writeback.getCode()+"】");
    			PosLog.getLog(this.getClass()).error(msglog.toString());
    			
    			Result result = RedEnvelopeApi.redWriteBack(writeback);
    			
    			msglog = new StringBuffer();
    			msglog.append("整单冲正：result=【[result]rcode="+result.getRcode()+"rdesc="+result.getRdesc()+"】");
    			PosLog.getLog(this.getClass()).error(msglog.toString());	
    			
    			
    			if(result != null){
    				int rcode = result.getRcode();
    				String rdesc = result.getRdesc();
    				if(rcode == 1)
    				{
    					deleteMzkCz();
    					return true;
    				}
    				else
    				{
    					new MessageBox(rdesc);
    					return false;
    				}
    			}
    		}catch (Exception e) {}
    	return false;
     }

//	 保存交易数据进行交易
	/*protected boolean setRequestDataByAccount()
	{
		if(!SellType.ISSALE(salehead.djlb))
		{
			return true;
		}
		return super.setRequestDataByAccount();
	}*/
	
	
	/*protected void saveAccountMzkResultToSalePay()
	{
		if(!SellType.ISSALE(salehead.djlb))
		{
			return;
		}
		super.saveAccountMzkResultToSalePay();
	}*/
	
	public boolean mzkAccountFinish(boolean isAccount, BankLogDef bld)
	{
		return true;
	}
	
/*//	 查找可以收该付款方式的对应商品
	public Vector getGoodsListByPayRule()
	{
		if(!SellType.ISSALE(saleBS.saletype))
		{
			return null;
		}
		// 查找可以收该付款方式的对应商品列表
		// 商品编码,商品名称,已付金额,限制金额,分摊金额,对应商品行号
		Vector v = new Vector();

		// 非有收款规则的付款方式，所有商品都可以用
		// 是有收款规则的付款方式，只有对应商品可收
		if (allowgoods == null || allowgoods.size() <= 0)
		{
			// Y-只分摊有规则的付款/A-分摊所有的付款
//			if (GlobalInfo.sysPara.havePayRule == 'A')
			if(true)
			{
				for (int i = 0; i < saleBS.saleGoods.size(); i++)
				{
					SaleGoodsDef sg = (SaleGoodsDef) saleBS.saleGoods.elementAt(i);

					// 计算商品可收付款的金额 = 成交价 - 已分摊的付款
					double yfje = getGoodsApportionTotal(i);
					double limitje = ManipulatePrecision.doubleConvert(sg.hjje - sg.hjzk - yfje);
					if (limitje > 0)
					{
						String[] row = { sg.barcode, sg.name, ManipulatePrecision.doubleToString(yfje), ManipulatePrecision.doubleToString(limitje), "", String.valueOf(i) };
						v.add(row);
					}
				}
			}
			else
			{
				return null;
			}
		}
		else
		{
			for (int n = 0; n < allowgoods.size(); n++)
			{
				PayRuleDef pr = (PayRuleDef) allowgoods.elementAt(n);

				for (int j = 0; pr.goodslist != null && j < pr.goodslist.size(); j++)
				{
					int sgindex = Convert.toInt((String) pr.goodslist.elementAt(j));
					SaleGoodsDef sg = (SaleGoodsDef) saleBS.saleGoods.elementAt(sgindex);

					// 计算商品可收付款的金额 = 成交价 - 已分摊的付款
					double yfje = getGoodsApportionTotal(sgindex);
					double limitje = ManipulatePrecision.doubleConvert(sg.hjje - sg.hjzk - yfje);
					if (limitje > 0)
					{
						String[] row = { sg.barcode, sg.name, ManipulatePrecision.doubleToString(yfje), ManipulatePrecision.doubleToString(limitje), "", String.valueOf(sgindex) };
						v.add(row);
					}
				}
			}
		}

		return v;
	}*/

	//获取手机号
	public String getPhoneAccountIn() {
		if(saleBS.salePayment.size() > 0)
		{
			for(int i =saleBS.salePayment.size();i>0;i--)
			{
				SalePayDef sp = (SalePayDef)saleBS.salePayment.elementAt(i-1);
				
				if(sp.paycode.equals(GlobalInfo.sysPara.hbPaymentCode))
				{
					return sp.payno;
				}
			}
		}
		return "";
	}
	
}
