package custom.localize.Bgtx;


import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Cmls.Cmls_DataService;

public class Bgtx_DataService extends Cmls_DataService
{
	public boolean findHYZK(GoodsPopDef popDef,String code,String custtype,String gz,String catid,String ppcode,String specialInfo)
    {
//    	if (GlobalInfo.isOnline)
//    	{
    		Bgtx_NetService netservice = (Bgtx_NetService)NetService.getDefault();
			return netservice.findHYZK(popDef, code, custtype, gz, catid, ppcode, specialInfo, NetService.getDefault().getMemCardHttp(CmdDef.GETCRMVIPZK));
//    	}
//    	else
//    	{
//    		return false;
//    	}
    }
    
//	 获取小票实时积分
	public void getCustomerSellJf(SaleHeadDef saleHead)
	{
		if (!Bgtx_CustomLocalize.crmMode())
		{
			super.getCustomerSellJf(saleHead);
			return ;
		}
		
		String[] row = new String[4];

	        if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
	        {
//	            if (((Bgtx_NetService)NetService.getDefault()).getCRMSellJf(row, saleHead.mkt, saleHead.syjh, String.valueOf(saleHead.fphm),"",""))
	        	if (((Bgtx_NetService)NetService.getDefault()).getCustomerSellJf(row, saleHead.mkt, saleHead.syjh, String.valueOf(saleHead.fphm)))
	        	{
	                saleHead.bcjf = Convert.toDouble(row[0]);
	                saleHead.ljjf = Convert.toDouble(row[1]);
	                saleHead.num4 = Convert.toDouble(row[3]);
	                saleHead.str5 = row[2];

	                AccessDayDB.getDefault().updateSaleJf(saleHead.fphm, 1, ManipulatePrecision.doubleConvert(saleHead.bcjf+saleHead.num4), saleHead.ljjf);
	                AccessDayDB.getDefault().updateSaleHeadStr(saleHead.fphm,"str5",saleHead.str5);
//没积分也要弹提示框
	                if (saleHead.ljjf > 0)
	                {
	                    StringBuffer sb = new StringBuffer();
	                    sb.append("累计积分: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(saleHead.ljjf), 0, 10, 10, 1) + "\n");
	                    sb.append("本次积分: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(saleHead.bcjf), 0, 10, 10, 1) + "\n");
	                    sb.append("倍享积分: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(saleHead.num4), 0, 10, 10, 1) + "\n");
	                    new MessageBox(sb.toString());
	                }
	            }
	            else
	            {
	                saleHead.bcjf = 0;
	                new MessageBox("计算本笔交易小票积分失败\n请到会员中心查询积分!");
	            }
	            
	            // 打印会员信息
//	            String[] row1 = new String[10];
//	            if (((Bgtx_NetService)NetService.getDefault()).javaGetCustXF(row1, saleHead.hykh, saleHead.syjh, String.valueOf(saleHead.fphm)))
//	            {
//	            	new MessageBox("月消费："+row1[1]+"\n年消费："+row1[0]);
//	            	
//	            	saleHead.str5 = saleHead.str5 + "\n" + "月消费："+row1[1]+"\n年消费："+row1[0];
//	            	AccessDayDB.getDefault().updateSaleHeadStr(saleHead.fphm,"str5",saleHead.str5);
//	            }
	        }
	}
	
	
	 public boolean getCustomer(CustomerDef cust, String track)
	    {
//	        if (GlobalInfo.isOnline)
//	        {
	            if (!NetService.getDefault().getCustomer(cust,track))
	            {
	            	new MessageBox(Language.apply("无此顾客卡信息!"), null, false);
	                return false;
	            }
//	        }
//	        else
//	        {	
//	            if (!AccessBaseDB.getDefault().getCustomer(cust,track))
//	            {
//	            	new MessageBox(Language.apply("无此顾客卡信息!"), null, false);
//	            	
//	                return false;
//	            }
//	        }
	        
	        return true;
	    }
	 
	 
	 public boolean sendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Sqldb sql)
		{
			if (!GlobalInfo.isOnline)// && !(ConfigClass.DataBaseEnable.equals("Y")
										// && GlobalInfo.RemoteDB != null)
			{ return false; }

			boolean again;

			// 送网小票返回数据
			Vector retValue = new Vector();

			// sql对象为空,非重发小票
			if (sql == null)
			{
				again = false;
			}
			else
			{
				again = true;
			}

			// 发送小票
			int result = 0;
			/*
			 * // 如果 ConfigClass.DataBaseEnable.equals("Y") 条件成立 则小票信息发送到远程数据库 if
			 * (ConfigClass.DataBaseEnable.equals("Y")) { result =
			 * AccessRemoteDB.getDefault().writeSale(saleHead, saleGoods,
			 * salePayment); } else { result =
			 * NetService.getDefault().sendSaleData(saleHead, saleGoods,
			 * salePayment, retValue); }
			 */
			
			result = NetService.getDefault().sendSaleData(saleHead, saleGoods, salePayment, retValue);

			// 非重发如果返回不为0，表示小票发送失败
			if (!again && result != 0)
				return false;

			// 重发小票，如果返回为2表示小票已存在，0表示成功，其他为送网失败
			if (again && result != 0 && result != 2)
				return false;

			// 得到返回数据,可对返回数据进行处理
			if (retValue.size() > 0)
			{
				String memo = retValue.elementAt(0).toString();
				double value = Double.parseDouble(CommonMethod.isNull(retValue.elementAt(1).toString(), "0"));

				updateSendSaleData(saleHead, memo, value, sql);
			}

			// 发送小票成功后更新小票送网标志
			if (sql == null)
			{
				// 更新小票送网标志
				AccessDayDB.getDefault().updateSaleBz(saleHead.fphm, 1, 'Y');
			}
			else
			{
				// 重发未送网小票时，不能用sql的execute(sqltext)方法
				// 和前面selectData换一个对象执行,否则冲突
				// 更新小票送网标志
				sql.setSql("update SALEHEAD set netbz = 'Y' where syjh = '" + saleHead.syjh + "' and fphm = " + String.valueOf(saleHead.fphm));
				sql.executeSql();
			}

			// 需要将小票发送到独立会员服务器
			if (GlobalInfo.sysPara.sendsaletocrm == 'Y')
			{
				sendSaleDataToMemberDB(saleHead, saleGoods, salePayment, again);
			}

			// 需要联网实时计算返券
			if (GlobalInfo.sysPara.calcfqbyreal == 'Y')
			{
				getSellRealFQ(saleHead);
			}

			if (GlobalInfo.sysPara.calcmystorecouponbyreal != 'N')
			{
				CreatePayment.getDefault().getPaymentMyStore().getMyStoreCoupon(saleHead, saleGoods, salePayment);
			}

			// 需要联网实时计算积分
			if (GlobalInfo.sysPara.calcjfbyconnect == 'Y' || GlobalInfo.sysPara.calcjfbyconnect == 'A')
			{
				getCustomerSellJf(saleHead, saleGoods, salePayment);
			}

			// 需要将小票送往WebService
			sendSaleWebService(saleHead, saleGoods, salePayment);

			return true;
		}
	 
	 
		// CRM会员卡交易
		public boolean sendHykSale(MzkRequestDef req, MzkResultDef ret)
		{
//			if (GlobalInfo.isOnline)
//			{
				return NetService.getDefault().sendHykSale(req, ret);
//			}
//			else
//			{
//				new MessageBox(Language.apply("会员卡交易必须联网使用!"));
//			}
//
//			return false;
		}
		
//		 面值卡交易
		public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
		{
//			if (GlobalInfo.isOnline)
//			{
				return NetService.getDefault().sendMzkSale(req, ret);
//			}
//			else
//			{
//				new MessageBox(Language.apply("面值卡必须联网使用!"));
//			}
//
//			return false;
		}
		
//		 面值卡查询
		public boolean getMzkInfo(MzkRequestDef req, MzkResultDef ret)
		{
//			if (GlobalInfo.isOnline)
//			{
				return NetService.getDefault().getMzkInfo(req, ret);
//			}
//			else
//			{
//				new MessageBox(Language.apply("查询面值卡必须联网使用!"));
//			}
//
//			return false;
		}
		
	    public void updateSendSaleData(SaleHeadDef saleHead, String memo, double value, Sqldb sql)
	    {
	        super.updateSendSaleData(saleHead, memo, value, sql);

	        if ((memo != null) && memo.trim().equals(""))
	        {
	            return;
	        }

//	        记录小票中奖信息
	        saleHead.str6 = memo.trim();
	    }
		
}
