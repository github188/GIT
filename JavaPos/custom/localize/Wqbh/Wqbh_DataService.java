package custom.localize.Wqbh;

import java.util.Vector;

import net.sf.json.JSONObject;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.RdPlugins;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bcrm.Bcrm_DataService;

public class Wqbh_DataService extends Bcrm_DataService
{
	protected BankLogDef bld = null;
	public int sendSaleWebService(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		//saleHead.str6 = "9999999999025288=000001000640422600";
		if(saleHead.hykh!=null && !saleHead.hykh.trim().equals("") && saleHead.str6!=null && !saleHead.str6.trim().equals("")&&saleHead.bcjf!=0) 
			sendDHYJF(saleHead,saleGoods,salePayment);
		return 0;
	}

	private boolean sendDHYJF(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		if(GlobalInfo.sysPara.isNEWDHY.equals("Y")){
			if((!SellType.ISSALE(saleHead.djlb))&&(!SellType.ISBACK(saleHead.djlb)))//红冲的不执行
				return true;
			//新接口方式
			//1.将后台计算出来的本次积分发送给大会员
			/*
			 * 　　String    memberId          //* 会员ID
	　　             String    OrderNo          //* 订单号(流水号)
	　　             String    point           //* 退款积分
	　　             String    storeId        //  门店ID
	　　             String    LoginToken    //* 现金消费额
	　　             int       type         //* 0交易 1退货
	　　             int       tradeCode   //* 交易类型 8002
	　　             int       tradeSrc   //* 交易来源 4
			 */
			String storeId=GlobalInfo.sysPara.WHstoreId;
			//门店号+收银机号+小票号+行号（+1，消费为0）
			String mktcode = GlobalInfo.sysPara.mktcode;
			if(GlobalInfo.sysPara.mktcode.indexOf(",")!=-1){
				mktcode = GlobalInfo.sysPara.mktcode.substring(GlobalInfo.sysPara.mktcode.indexOf(",")+1);
			}
	 		String OrderNo = mktcode+"|"+GlobalInfo.syjDef.syjh+"|"+saleHead.fphm+"|"+"0";
	 		String point = String.valueOf(Math.abs(saleHead.bcjf));
	 		String memberResOutput="";
	 		String memberReqInput="";
	 		Wqbh_DHYInterface DHY=new Wqbh_DHYInterface();
	 		if(!"".equals(saleHead.str8)){
	 			GlobalInfo.sysPara.commMerchantId=saleHead.str8;
	 		}
	 		if( SellType.ISSALE(saleHead.djlb))
	 		{//销售小票&&交易类型0  = 加积分
	 		 //销售小票大会员积分消费付款	
	 		  memberReqInput="memberId="+GlobalInfo.sysPara.commMerchantId+"&orderNo="+OrderNo+"&point="+point+"&storeId="+storeId+"&loginToken="+saleHead.str1+"&type=0&tradeCode=8002&tradeSrc=4";
	 		  memberResOutput=DHY.AddDHYJF(memberReqInput);  
	 		}else if((SellType.ISBACK(saleHead.djlb))){
	 			//交易类型1 || (退货小票&&交易类型1) = 减积分s
	 		  memberReqInput="memberId="+GlobalInfo.sysPara.commMerchantId+"&orderNo="+OrderNo+"&point="+point+"&storeId="+storeId+"&loginToken="+saleHead.str1+"&type=1&tradeCode=8002&tradeSrc=4";
	 		  memberResOutput=DHY.MinusDHYJF(memberReqInput);	
	 		}
	 		
	 			if(memberResOutput!=null && memberResOutput.trim().length()>0)
	 			{
	 				JSONObject js= JSONObject.fromObject(memberResOutput);
	 				if(js.getString("status").equals("0"))//接口返回成功
	 				{
	 				    PosLog.getLog(getClass()).info("小票号:"+saleHead.fphm +"流水号: "+js.getString("serialNo") );
	 				    saleHead.str3 =js.getString("serialNo") ;
	 				    saleHead.ljjf =js.getDouble("amount");
	 				    new MessageBox("大会员增加/扣减积分接口成功！");
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
							bld.fphm = saleHead.fphm;// 小票号
							bld.syyh = (GlobalInfo.posLogin != null ? GlobalInfo.posLogin.gh
									: "");// 收银员
							if (SellType.ISSALE(saleHead.djlb))
							  bld.type = "5";// 销售积分 交易类型
							else
							  bld.type = "6";//积分退货 交易类型	
							bld.je = Convert.toDouble(point);// 交易积分
							bld.cardno = saleHead.hykh;// 卡号
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
	 						if (!AccessDayDB.getDefault().writeBankLog(bld)) { return false; }
	 						BankLogSend();
	 					}
	 					catch (Exception ex)
	 					{
	 						ex.printStackTrace();

	 						new MessageBox("写入请求数据交易日志失败\n\n" + ex.getMessage(), null, false);
	 						bld = null;

	 						return false;
	 					}
	 				}else{
	 					new MessageBox("大会员积分调整接口返回数据失败："+js.getString("status")+js.getString("message"));
	 					return false;
	 				}
	 			}else{
 					new MessageBox("大会员积分调整接口用失败");
 					return false;
 				}
			
		}else{
			/*
			 * 　　char TenantID[15]  //* 商户代码
	　　char       CasherID[20]          //* 收银员号
	　　char       Track[40]             //* 磁道信息
	　　char       ReceiptNumber[11]     //  收银流水号
	　　char       CashAmount[12]        //* 现金消费额
	　　char       BounsAlter [12]       //* 积分赠送额
	　　char       ReCashAmount[12]      //* 现金消费退还额
	　　char       ReBonusAlter[12]      //* 积分赠送退还额
			 */
//			门店号+收银机号+小票号+行号（+1，消费为0）
			String mktcode = GlobalInfo.sysPara.mktcode;
			if(GlobalInfo.sysPara.mktcode.indexOf(",")!=-1){
				mktcode = GlobalInfo.sysPara.mktcode.substring(GlobalInfo.sysPara.mktcode.indexOf(",")+1);
			}
			//String syjh = mktcode+GlobalInfo.syjDef.syjh+saleHead.fphm+"0";
			String syjh = GlobalInfo.syjDef.syjh;
			String syyh = GlobalInfo.posLogin.gh;
	 		//String track = Convert.increaseChar(saleHead.str6,40);
	 		//String fphm = String.valueOf(saleHead.fphm);
	 		String fphm = mktcode+"|"+GlobalInfo.syjDef.syjh+"|"+saleHead.fphm+"|"+"0";
	 		String cashAmount = "";
		 	String bounsAlter = "";
		 	String reCashAmount = "";
		 	String reBonusAlter = "";
		 	String transType = "";
	 		if(SellType.ISBACK(saleHead.djlb)||saleHead.djlb.equals(SellType.RETAIL_SALE_HC))
	 		{
	 			transType = "03";
	 			cashAmount = "0";
	 	 		bounsAlter = "0";
	 	 		reCashAmount = String.valueOf(calcPayFPMoney(saleHead,saleGoods,salePayment));
	 	 		reBonusAlter = String.valueOf(Math.abs(saleHead.bcjf));
	 	 		if(Double.parseDouble(reBonusAlter)==0) return true;
	 			
	 		}else {
	 			transType = "01";
	 			cashAmount = String.valueOf(calcPayFPMoney(saleHead,saleGoods,salePayment));
	 	 		bounsAlter = String.valueOf(Math.abs(saleHead.bcjf));
	 	 		reCashAmount = "0";
	 	 		reBonusAlter = "0";
	 		}
	 		
	 		String bonusAlterReturn = "";
	 		PosLog.getLog(getClass()).info("收银机号:"+syjh+" 收银员号："+syyh+" 轨道号:"+saleHead.str6+" 小票号:"+fphm+" cashAmount:"+cashAmount+" bounsAlter:"+bounsAlter+" reCashAmount:"+reCashAmount+" reBonusAlter:"+reBonusAlter);
	 		
	 		if (RdPlugins.getDefault().getPlugins1().exec(13,syjh+","+syyh+","+saleHead.str6+","+fphm+","+cashAmount+","+bounsAlter+","+reCashAmount+","+reBonusAlter+","+transType))
	 		//if (RdPlugins.getDefault().getPlugins1().exec(13,syjh+","+syyh+","+saleHead.str6+","+fphm+","+cashAmount+","+bounsAlter+","+reCashAmount+","+reBonusAlter))
	 		 			
	 		//if (RdPlugins.getDefault().getPlugins1().exec(13,GlobalInfo.syjDef.syjh+","+GlobalInfo.posLogin.gh+","+saleHead.str6+","+saleHead.fphm+","+calcPayFPMoney(saleHead,saleGoods,salePayment)+","+saleHead.bcjf+","+"0"+","+"0"))
	 	 		
	 		{
	 			bonusAlterReturn = (String) RdPlugins.getDefault().getPlugins1().getObject();
	 			if(bonusAlterReturn!=null && bonusAlterReturn.trim().length()>0&&bonusAlterReturn.substring(0, 2).equals("00"))
	 			{
	 				PosLog.getLog(getClass()).info("小票号:"+saleHead.fphm +" "+bonusAlterReturn);
	 				new MessageBox("大会员积分余额:    "+bonusAlterReturn.substring(39, 51).trim()+"\n大会员可用积分:    "+bonusAlterReturn.substring(51,63).trim());
	 				saleHead.ljjf=Convert.toDouble(bonusAlterReturn.substring(51,63).trim());
	 				if(GlobalInfo.sysPara.isPrintDHY.equals("Y")){
	 					Wqbh_MenuFuncBS wm = new Wqbh_MenuFuncBS();
	 	 				wm.PrintDHYDoc();
	 				}
	 			}else{
	 				PosLog.getLog(getClass()).info("小票号:"+saleHead.fphm +" "+bonusAlterReturn);
	 				new MessageBox("发送大会员积分失败!");
	 				return false;
	 			}
	 			
	 		}
		}
		
		return true;
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
	protected double calcPayFPMoney(SaleHeadDef salehead, Vector saleGoods, Vector salepay)
	{
		double je = salehead.sjfk - salehead.zl;

		String payex = "," + GlobalInfo.sysPara.fpjepayex + ",";
		for (int i = 0; i < salepay.size(); i++)
		{
			SalePayDef sp = (SalePayDef) salepay.elementAt(i);
			if (sp.flag == '1' && payex.indexOf("," + sp.paycode + ",") >= 0)
			{
				je -= sp.je;
				
				for (int j = 0; j < salepay.size();j++)
				{
					SalePayDef sp1 = (SalePayDef) salepay.elementAt(j);
					if (sp1.flag == '2' && sp1.paycode.equals(sp.paycode))
					{
						je += sp1.je;
					}
				}
			}
		}
		return je;
	}
	
//	 获取小票实时积分
    public void getCustomerSellJf(SaleHeadDef saleHead)
    {
        String[] row = new String[5];

        if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
        {
            if (NetService.getDefault().getCustomerSellJf(row, saleHead.mkt, saleHead.syjh, String.valueOf(saleHead.fphm)))
            {
                saleHead.bcjf = Convert.toDouble(row[0]);
                if(saleHead.str6!=null && !saleHead.str6.trim().equals("")){//str6是记录的大会员卡号
                  System.out.println(saleHead.ljjf);  
                }else
                    saleHead.ljjf =  Convert.toDouble(row[1]);
                saleHead.str5 = row[2];
                if ((!row[3].isEmpty()&&!row[3].equals("")))
                saleHead.num4 = Convert.toDouble(row[3]);
//                if(row.length>4){
//                	double jftz = Convert.toDouble(row[4]);
//                	if(saleHead.str6!=null && !saleHead.str6.trim().equals("")){
//                    	saleHead.bcjf = jftz;
//                    }
//                }
                
                
                if (GlobalInfo.sysPara.sendhyjf == 'Y')
                {
                	if (!sendHykJf(saleHead))
                	{
                		 new MessageBox("本笔积分同步失败无法获得累计积分\n请到会员中心查询累计积分!");
                	}
                }
                
                if (saleHead.ljjf != 0)
                {
                    StringBuffer sb = new StringBuffer();
                    sb.append("累计积分: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(saleHead.ljjf), 0, 10, 10, 1) + "\n");
                    if (saleHead.bcjf != 0) sb.append("本次积分: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(saleHead.bcjf), 0, 10, 10, 1) + "\n");
                    if (saleHead.num4 != 0) sb.append("倍享积分: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(saleHead.num4), 0, 10, 10, 1) + "\n");
                    new MessageBox(sb.toString());
                }
                
                AccessDayDB.getDefault().updateSaleJf(saleHead.fphm, 1, saleHead.bcjf, saleHead.ljjf,String.valueOf(saleHead.num4));
            }
            else
            {
                saleHead.bcjf = 0;
                new MessageBox("计算本笔交易小票积分失败\n请到会员中心查询积分!");
            }
        }
    }
    
}
