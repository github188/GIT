package custom.localize.Wqbh;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.RdPlugins;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class WqbhDHY_PaymentMzk extends PaymentMzk
{
	protected BankLogDef bld = null;
	String uid="";
	String loginToken="";
	int i=0;
	Wqbh_SaleBS ws = new Wqbh_SaleBS();
	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret) {
		/*
		 * // yinliang test if (ConfigClass.isDeveloperMode()) { ret.cardno =
		 * "0123456789"; ret.cardname = "模拟测试卡"; ret.money = 100; ret.ye = 100;
		 * new MessageBox("当前为模拟测试数据的虚假数据,仅供测试部分流程!!!"); return true; } else
		 */
		String pwd = "";
		if (req.track2.indexOf("=")==-1 && req.track2.length() > 12) {
			pwd = req.track2.substring(11);
			req.track2 = req.track2.substring(0, 11);
		}
		if (GlobalInfo.sysPara.isNEWDHY.equals("Y")) {// 大会员支付在参数开启的时候调新接口，关闭的时候走老接口
			if (req.track2.length()==11) i=1;
			String memberInfoReturn = ws.findMemberNewDHYCard(req.track2);
			if (memberInfoReturn != null
					&& memberInfoReturn.trim().length() > 0) {
				try {
					JSONObject js = JSONObject.fromObject(memberInfoReturn);
					if (js.getString("status").equals("0")) {// 返回成功
						String data = js.getString("data");
						JSONObject jsdata = JSONObject.fromObject(data);
					    loginToken=jsdata.getString("loginToken");
					    uid=jsdata.getString("uid");
					    salehead.str8=uid;
						String member = jsdata.getString("member");
						JSONObject jsmember = JSONObject.fromObject(member);
						ret.cardno = jsmember.getString("cardNo");
						ret.money = 99999999;
						ret.ye = Double
								.parseDouble(jsmember.getString("avlPoint"));
						ret.cardname = jsmember.getString("nickName");
						ret.cardpwd = pwd;
						return true;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} else {
			String result = ws.findMemberDHYCard(req.track2);
			if (result != null && result.trim().length() > 0) {
				if (result.substring(0, 2).equals("00")) {
					byte[] mir = result.getBytes();
					ret.cardno = req.track2;
					ret.money = 99999999;
					ret.ye = Double.parseDouble(new String(
							subBytes(mir, 33, 12)).trim());
					ret.cardname = new String(subBytes(mir, 213, 32)).trim();
					ret.cardpwd = pwd;
					return true;
				}
			}
		}

		// return DataService.getDefault().sendMzkSale(req,ret);
		return false;
	}
	
	public boolean mzkAccount(boolean isAccount)
	{	
		do 
		{
			// 退货交易卡号为空时提示刷卡
			paynoMsrflag = false;
			if (!paynoMSR()) return false;
			
			// 设置交易类型,isAccount=true是记账,false是撤销
			if (GlobalInfo.sysPara.isNEWDHY.equals("Y")) {
				if (isAccount) {
				   if (SellType.ISSALE(salehead.djlb))
					   mzkreq.type = "0"; // 消费,减
				   else
					   mzkreq.type = "1"; // 退货,加   
				} else {
				    if (SellType.ISBACK(salehead.djlb)||SellType.ISSALE(salehead.djlb))
					   mzkreq.type = "2"; // 取消积分
					else
					   mzkreq.type = "0"; // 
				}
			} else {
				if (isAccount) {
					if (SellType.SELLSIGN(salehead.djlb) > 0)
						mzkreq.type = "01"; // 消费,减
					else
						mzkreq.type = "03"; // 退货,加
				} else {
					if (SellType.SELLSIGN(salehead.djlb) > 0)
						mzkreq.type = "03"; // 退货,加
					else
						mzkreq.type = "01"; // 消费,减
				}
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
			
			// 先写冲正文件
			if (!writeMzkCz()) 
			{
				if (paynoMsrflag) 
				{
					salepay.payno = "";
					continue;
				}
				return false;
			}
	
			// 记录面值卡交易日志
			BankLogDef bld = mzkAccountLog(false,null,mzkreq,mzkret);
			
			// 发送交易请求
			if (!sendDHYJFSale(mzkreq,mzkret,salepay)) 
			{
				if (paynoMsrflag) 
				{
					salepay.payno = "";
					continue;
				}
				return false;
			}
	
			salepay.num2 = salepay.num5+1;
			// 记录应答信息, batch标记本付款方式已记账,这很重要
			saveAccountMzkResultToSalePay();
			if(GlobalInfo.sysPara.isPrintDHY.equals("Y")){
				Wqbh_MenuFuncBS wm = new Wqbh_MenuFuncBS();
				wm.PrintDHYDoc();
			}
			// 记账完成操作,可用于记录记账日志或其他操作
			return mzkAccountFinish(isAccount,bld);
		} while(true);
	}

	private boolean sendDHYJFSale(MzkRequestDef mzkreq, MzkResultDef mzkret, SalePayDef salepay)
	{
		if (GlobalInfo.sysPara.isNEWDHY.equals("Y")){//参数开启走新会员流程
			if (!SellType.ISHC(salehead.djlb)){
			if (sendNEWDHYJFSale(mzkreq,mzkret,salepay))
				return true;
			}else
				return true;
		}else{
			

		/*
		 * 　　char       TenantID[15]          //* 商户代码
　　char       CasherID[20]          //* 收银员号
　　char       Track[40]             //* 磁道信息
　　char       ReceiptNumber[11]     //  收银流水号
　　char       CashAmount[12]        //* 现金消费额
　　char       BounsAlter [12]       //* 积分赠送额
　　char       ReCashAmount[12]      //* 现金消费退还额
　　char       ReBonusAlter[12]      //* 积分赠送退还额
		 */

		//门店号+收银机号+小票号+行号（+1，消费为0）
		String mktcode = GlobalInfo.sysPara.mktcode;
		if(GlobalInfo.sysPara.mktcode.indexOf(",")!=-1){
			mktcode = GlobalInfo.sysPara.mktcode.substring(GlobalInfo.sysPara.mktcode.indexOf(",")+1);
		}
		//String syjh = mktcode+GlobalInfo.syjDef.syjh+mzkreq.fphm+(salepay.num5+1);
		String syjh = GlobalInfo.syjDef.syjh;
 		String syyh = GlobalInfo.posLogin.gh;
 		String track = mzkreq.track2;
 		//String fphm = String.valueOf(mzkreq.fphm);
 		String hh = String.valueOf(salepay.num5+1);
 		if(hh.indexOf(".")!=-1){
 			hh = hh.substring(0,hh.indexOf("."));
 		}
 		String fphm =  mktcode+"|"+GlobalInfo.syjDef.syjh+"|"+mzkreq.fphm+"|"+hh;
 		String cashAmount = "";
	 	String bounsAlter = "";
	 	String reCashAmount = "";
	 	String reBonusAlter = "";
	 	String transType = "";
//	  交易类型,'01'-消费,'02'-消费冲正,'03'-退货,'04'-退货冲正,'05'-查询,'06'-冻结
	 	/**
	 	 * “现金消费金额”[不可为空]:是由于购物小票采用现金类支付的金额
	 	 * “积分赠送额”[不可为空]:正数表示消费产生的积分，负数表示支付用掉的积分
	 	 * “现金消费退还额”[不可为空]:退货时采用现金类支付的金额
	 	 * “积分赠送退还积分”[不可为空]:正数表示退货时现金类支付退货产生的退还积分，负数表示退货时积分类支付退货产生的退还积分
	 	 */
 		if(mzkreq.type.equals("01") && SellType.ISSALE(salehead.djlb))
 		{//销售小票&&交易类型01  = 减积分
 		 //销售小票大会员积分消费付款
 			cashAmount = "0";
 			bounsAlter = "0";
 	 		reCashAmount = "0";
 	 		reBonusAlter = String.valueOf(mzkreq.je);
 	 		transType = "05";
 	 		
 		}else if(mzkreq.type.equals("04")||(mzkreq.type.equals("01") && (SellType.ISBACK(salehead.djlb)||salehead.djlb.equals( SellType.RETAIL_BACK_HC)))){
 			//交易类型04 || (退货小票&&交易类型01) = 减积分
 			//退货冲正  或者  退货小票大会员积分退款撤销
 			cashAmount = "0";
 			bounsAlter = "0";
 	 		reCashAmount = "0";
 	 		reBonusAlter = String.valueOf(mzkreq.je);
 	 		transType = "08";
 		}
 		else if(mzkreq.type.equals("02")||mzkreq.type.equals("03")){
 			if((mzkreq.type.equals("03") && (SellType.ISSALE(salehead.djlb))||salehead.djlb.equals( SellType.RETAIL_SALE_HC))||mzkreq.type.equals("02")){
 				//（交易类型03 && 销售小票）||交易类型02  =  加积分
 				//销售小票大会员积分消费撤销 或者 消费冲正
 				cashAmount = "0";
 	 	 		bounsAlter = String.valueOf(mzkreq.je);
 	 	 		reCashAmount = "0";
 	 	 		reBonusAlter = "0";
 	 	 		transType = "06";
 				
 			}
 			else if(mzkreq.type.equals("03") &&SellType.ISBACK(salehead.djlb)){
 				//交易类型03 && 退货小票 = 加积分
 				//退货交易大会员积分退款
 				cashAmount = "0";
 	 			bounsAlter = String.valueOf(Math.abs(mzkreq.je));
 	 	 		reCashAmount = "0";
 	 	 		reBonusAlter = "0";
 	 	 		transType = "07";
 			}else {
 				new MessageBox("未找到匹配交易类型，无法发送积分调整数据!");
 				return false;
 			}
 			
 		}
 		else {
 				new MessageBox("未找到匹配交易类型，无法发送积分调整数据!");
 				return false;
 			}
 		/*
 		 * if(mzkreq.type.equals("01"))
 		{
 			cashAmount = "0";
 			bounsAlter = "0";
 	 		reCashAmount = "0";
 	 		reBonusAlter = String.valueOf(mzkreq.je);
 	 		transType = "05";
 	 		
 		}else if(mzkreq.type.equals("04")){
 			cashAmount = "0";
 			bounsAlter = "0";
 	 		reCashAmount = "0";
 	 		reBonusAlter = String.valueOf(mzkreq.je);
 	 		transType = "08";
 		}
 		else if(mzkreq.type.equals("02")||mzkreq.type.equals("03")){
 			if(mzkreq.type.equals("03") && mzkreq.je<0){
 				cashAmount = "0";
 	 			bounsAlter = "0";
 	 	 		reCashAmount = "0";
 	 	 		reBonusAlter = String.valueOf(Math.abs(mzkreq.je));
 	 	 		transType = "07";
 			}else{
 				cashAmount = "0";
 	 	 		bounsAlter = String.valueOf(mzkreq.je);
 	 	 		reCashAmount = "0";
 	 	 		reBonusAlter = "0";
 	 	 		transType = "06";
 			}
 			
 		}
 		 */
 		
 		String bonusAlterReturn = "";
 		if (RdPlugins.getDefault().getPlugins1().exec(13,syjh+","+syyh+","+track+","+fphm+","+cashAmount+","+bounsAlter+","+reCashAmount+","+reBonusAlter+","+transType))
  			//if (RdPlugins.getDefault().getPlugins1().exec(13,syjh+","+syyh+","+track+","+fphm+","+cashAmount+","+bounsAlter+","+reCashAmount+","+reBonusAlter))
  		{
 			/**
 			 * 　　char       ResultCode[2]        // 应答代码
　　char       TraceNumber[12]      // 主机流水号
　　char       BatchNumber[6]       // 批次号
　　char       CardNumber[19]       // 卡号
　　char       Bonus[12]            // 当前积分余额
　　char       ValidBonus[12]       // 当前可用积分
 			 */
  			bonusAlterReturn = (String) RdPlugins.getDefault().getPlugins1().getObject();
 			if(bonusAlterReturn!=null && bonusAlterReturn.trim().length()>0)
 			{
 				PosLog.getLog(getClass()).info("小票号:"+mzkreq.fphm +" "+bonusAlterReturn);
 				if(bonusAlterReturn.substring(0, 2).equals("00"))
 				{
 					mzkret.str1 =  bonusAlterReturn.substring(2,14).trim();
 					mzkret.str2 =  bonusAlterReturn.substring(14,20).trim();
 					mzkret.cardno =  bonusAlterReturn.substring(20,39).trim();
 					mzkret.ye =  Double.parseDouble(bonusAlterReturn.substring(51,63).trim());
 					mzkret.str4 = mzkreq.track2;
 					return true;
 				}else{
 					new MessageBox("大会员积分调整接口返回数据失败："+bonusAlterReturn.substring(0, 2));
 				}
 			}
 		}
		}
		return false;	
	}
	
	private boolean sendNEWDHYJFSale(MzkRequestDef mzkreq, MzkResultDef mzkret,
			SalePayDef salepay) {

		/*
		 * String memberId //* 会员ID 
		 * String OrderNo //* 订单号(流水号) 
		 * String point //* 退款积分 
		 * String storeId // 门店ID 
		 * String LoginToken //*  
		 * int type //* 0交易 1退货 
		 * int tradeCode //* 交易类型 8002 
		 * int tradeSrc //* 交易来源 4
		 */

		// 门店号+收银机号+小票号+行号（+1，消费为0）
		String mktcode = GlobalInfo.sysPara.mktcode;
		if (GlobalInfo.sysPara.mktcode.indexOf(",") != -1) {
			mktcode = GlobalInfo.sysPara.mktcode
					.substring(GlobalInfo.sysPara.mktcode.indexOf(",") + 1);
		}
		String hh="";
		if(mzkreq.type.equals("2")) 
		 hh = String.valueOf(salepay.num5 + 1);
		else
		 hh = String.valueOf(saleBS.salePayUnique + 1);
		if (hh.indexOf(".") != -1) {
			hh = hh.substring(0, hh.indexOf("."));
		}
		String storeId = GlobalInfo.sysPara.WHstoreId;
		String OrderNo = mktcode + "|" + GlobalInfo.syjDef.syjh + "|"
				+ mzkreq.fphm + "|" + hh;
		//String remark =mktcode+"|"+GlobalInfo.syjDef.syjh+"|"+salehead.yfphm+"|"+hh;
		String point = String.valueOf(mzkreq.je);
		String memberResOutput = "";
		String memberReqInput = "";
		Wqbh_DHYInterface DHY = new Wqbh_DHYInterface();
		memberReqInput = "memberId=" + uid + "&orderNo=" + OrderNo + "&point="
				+ point + "&storeId=" + storeId + "&loginToken=" + loginToken
				+ "&type=" + mzkreq.type + "&tradeCode=8002&tradeSrc=4";
		 if (mzkreq.type.equals("2")) {// 删除付款方式 取消积分
			 
			memberReqInput = "memberId=" + uid +"&serialNo=" + bld.trace+ "&orderNo=" + OrderNo
					+ "&tradeCode=8002&tradeSrc=4";
			memberResOutput = DHY.CancelJF(memberReqInput);
		} else if (mzkreq.type.equals("0") && SellType.ISSALE(salehead.djlb)) {// 销售小票&&交易类型0
																		// = 减积分
			// 销售小票大会员积分消费付款
			if (i==1){//输手机号的要输支付密码
				String result=ws.chkPWD(uid, loginToken);

				JSONObject rs=JSONObject.fromObject(result);
				if (!rs.getString("status").equals("0")){//验证支付密码接口返回成功
					new MessageBox("验证支付密码错误，错误代码："+rs.getString("status")+"错误原因："+rs.getString("message"));
					return false;
				}
			}
			memberResOutput = DHY.MinusDHYJF(memberReqInput);
		} else if (mzkreq.type.equals("1") && (SellType.ISBACK(salehead.djlb))) {
			// 交易类型1 || (退货小票&&交易类型1) = 加积分
			memberResOutput = DHY.AddDHYJF(memberReqInput);
		}

		if (memberResOutput != null && memberResOutput.trim().length() > 0) {
			JSONObject js = JSONObject.fromObject(memberResOutput);
			if (js.getString("status").equals("0"))// 接口返回成功
			{
				salehead.ljjf=js.getDouble("amount");//增减积分接口和红冲都返回可用积分
				PosLog.getLog(getClass()).info(
						"小票号:" + mzkreq.fphm);
				if (!mzkreq.type.equals("2")){
					try {
						bld = new BankLogDef();

						Object obj = GlobalInfo.dayDB
								.selectOneData("select max(rowcode) from BANKLOG");

						if (obj == null) {
							bld.rowcode = 1;
						} else {
							bld.rowcode = Integer.parseInt(String.valueOf(obj)) + 1;
						}
						bld.rqsj = ManipulateDateTime.getCurrentDateTime();// 交易时间
						bld.syjh = GlobalInfo.syjDef.syjh;// 收银机号
						bld.fphm = GlobalInfo.syjStatus.fphm;// 小票号
						bld.syyh = (GlobalInfo.posLogin != null ? GlobalInfo.posLogin.gh
								: "");// 收银员
						bld.type = mzkreq.type;// 交易类型
						bld.je = mzkreq.je;// 交易积分
						bld.cardno = mzkret.cardno;// 卡号
						bld.trace = Long.parseLong(js.getString("serialNo"));// 流水号
						bld.authno = "";
						bld.bankinfo = "";
						bld.crc = "";
						bld.retcode = "0";
						bld.retmsg = "";
						bld.retbz = 'N';
						bld.net_bz = 'N';
						bld.allotje = 0;
						bld.memo = "";
						bld.memo1 = "";
						bld.memo2 = "";
						bld.tempstr = "";
						bld.tempstr1 = "";
                        salehead.str7=js.getString("serialNo");
						// 写本地交易日志
						if (!AccessDayDB.getDefault().writeBankLog(bld)) {
							new MessageBox("大会员积分消费写本地记录失败！");
						}
						if (!BankLogSend()){
							new MessageBox("大会员积分消费记录送网失败！");
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						new MessageBox("写入大会员交易日志失败\n\n" + ex.getMessage(),
								null, false);
						bld = null;
						return false;
					}
				}
				if (mzkreq.type.equals("2")) {// 删除了的不打标记
					salehead.num2 =salehead.num2-1;
				} else
					salehead.num2 =salehead.num2+1;
				return true;
			} else {
				new MessageBox("大会员积分交易接口返回数据失败：" + js.getString("status")
						+ js.getString("message"));
			}
		}

		return false;
	}
	
	public boolean BankLogSend()
	{
		ProgressBox pb = null;

		try
		{
			pb = new ProgressBox();

			pb.setText("正在发送第三方支付交易日志,请等待...");

			//
			if (NetService.getDefault().sendBankLog(bld))
			{
				//
				bld.net_bz = 'Y';

				//
				if (!AccessDayDB.getDefault().updateBankLog(bld)) { return false; }
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
			}
		}
	}
	
	public boolean writeMzkCz()
	{
		return true;
	}
	/*
	public int getAccountInputMode()
	{
//		if (paymode != null && paymode.type == '5')
//			return TextBox.MsrKeyInput;
//		return TextBox.MsrInput;
		

		String[] title = { "输入类型" };
		int[] width = { 440 };
		Vector contents = new Vector();
		contents.add(new String[] { "刷卡输入" });
		contents.add(new String[] { "手机号输入" });
		
		
		int choice = new MutiSelectForm().open("请选择输入方式", title, width, contents);
		if (choice == -1||choice == 0)
		{
			
			return TextBox.MsrInput;
		}
		else 
		{
			return 0;
		}
	
	}
*/
	
	public boolean cancelPay()
	{
		// 如果不是即时记账,则可直接取消付款
//		if (GlobalInfo.sysPara.cardrealpay != 'Y' || salepay.batch == null || salepay.batch.trim().length() <= 0)
//		{
//			// 未记账,直接返回,取消付款
//			return true;
//		}
//		else
//		{
//			// 即时记账模式,取消已记账的付款
//			if (GlobalInfo.sysPara.cardrealpay == 'Y')
//			{
				if (mzkAccount(false))
				{
					deleteMzkCz();

					return true;
				}
				else
				{
					return false;
				}
//			}
//
//			return true;
//		}
	}

	public boolean collectAccountPay()
	{
		// 如果不是即时记账,则集中记账
		if (salepay.batch == null || salepay.batch.trim().length() <= 0)
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
		else
		{
			// 已记账,直接返回
			return true;
		}
	}
	
	public boolean isRealAccountPay()
	{
//		if (GlobalInfo.sysPara.cardrealpay == 'Y') { return true; }
//
//		return false;
		return true;
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
	
	// 保存交易数据进行交易
	protected boolean setRequestDataByAccount()
	{
		// 得到消费序号
		long seqno = getMzkSeqno();
		if (seqno <= 0)
			return false;

		// 打消费交易包
		mzkreq.seqno = seqno;
		mzkreq.je = salepay.ybje;
		mzkreq.syjh = ConfigClass.CashRegisterCode;
		mzkreq.mktcode = GlobalInfo.sysPara.mktcode;
		mzkreq.fphm = GlobalInfo.syjStatus.fphm;
		mzkreq.syyh = GlobalInfo.posLogin.gh;
		mzkreq.paycode = salepay.paycode;
		mzkreq.invdjlb = ((salehead != null) ? salehead.djlb : "");

		// 告诉后台过程磁道信息是存放的是卡号,只采用卡号记账方式,不使用磁道记账方式
		mzkreq.track1 = "CARDNO";
		mzkreq.track2 = salepay.str2;

		return true;
	}
	
	protected boolean saveFindMzkResultToSalePay()
	{
		salepay.batch = "";
		salepay.payno = mzkret.cardno;
		salepay.kye = mzkret.ye;
		salepay.str2 = mzkret.cardno;
		return true;
	}
	
	public static byte[] subBytes(byte[] src, int begin, int count) {  
	    byte[] bs = new byte[count];  
	    for (int i=begin; i<begin+count; i++) bs[i-begin] = src[i];  
	    return bs;  
	}  
	
	public int getAccountInputMode()
	{
		return TextBox.MsrKeyInput;
	}
}
