package custom.localize.Bszm;

import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;


import custom.localize.Bstd.Bstd_DataService;

public class Bszm_DataService extends Bstd_DataService
{
	private String getJfGoodsReqeustString(Vector para)
	{
		String retString = "";
		try
		{
			if (para == null || para.size() == 0)
				return "";

			for (int i = 0; i < para.size(); i++)
			{
				SaleGoodsDef goods = (SaleGoodsDef) para.get(i);
				{
					retString += "[goodsid]" + goods.code + "[/goodsid]" + "[salevalue]" + 
					String.valueOf(goods.hjje) + "[/salevalue]" + "[discvalue]" + String.valueOf(goods.hjzk) + "[/discvalue]" 
					+ ((i==para.size()-1)?"":"@");
				}
			}
			System.out.println(retString);
			return retString;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "";
		}
	}

	private String getJfPayRequestString(Vector para)
	{
		String retString = "";
		try
		{
			if (para == null || para.size() == 0)
				return "";

			for (int i = 0; i < para.size(); i++)
			{
				SalePayDef pay = (SalePayDef) para.get(i);
				{
					retString += "[paytypeid]" + pay.paycode + "[/paytypeid]" + "[realvalue]" + pay.je + "[/realvalue]" +((i==para.size()-1)?"":"@");
				}
			}
			System.out.println(retString);
			return retString;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "";
		}
	}

	// 获取实时返券
	public void getSellRealFQ(SaleHeadDef saleHead)
	{
		// 返回两行，第一行提示信息，第二行为返券最大金额
		String[] row = new String[2];
		saleHead.memo = "OK#100";
		if (SellType.ISSALE(saleHead.djlb))
		{
			if (NetService.getDefault().getSellRealFQ(row, GlobalInfo.sysPara.mktcode, saleHead.syjh, String.valueOf(saleHead.fphm)))
			{
				if (row[0].trim().equals(""))
					return;

				saleHead.memo = row[0] + "#" + row[1];

				// double limitJe = Convert.toDouble(row[1]);
				AccessDayDB.getDefault().updateSaleJf(saleHead.fphm, 4, 0, 0, saleHead.memo);

				StringBuffer sb = new StringBuffer();
				// sb.append("本笔交易有活动返券\n");
				sb.append("返券提示: " + row[0].trim());
				// sb.append("返券限额: " +
				// ManipulatePrecision.doubleToString(limitJe));
				new MessageBox(sb.toString());
			}
		}
		else
		// TCRC需要在退货时检查扣除的返券金额并提示
		{/*
		 * if (NetService.getDefault().getSellRealFQ(row,
		 * GlobalInfo.sysPara.mktcode, saleHead.syjh,
		 * String.valueOf(saleHead.fphm))) { saleHead.memo = row[0] + "," +
		 * row[1] + "," + row[2];
		 * 
		 * double faq = Convert.toDouble(row[0]); double fbq =
		 * Convert.toDouble(row[1]);
		 * AccessDayDB.getDefault().updateSaleJf(saleHead.fphm, 4, 0, 0,
		 * saleHead.memo);
		 * 
		 * // 提示 if ((Convert.toDouble(row[0]) != 0) ||
		 * (Convert.toDouble(row[1]) != 0)) { StringBuffer sb = new
		 * StringBuffer(); sb.append("本笔交易存在扣券\n"); sb.append("返A券: " +
		 * Convert.appendStringSize("", ManipulatePrecision.doubleToString(faq),
		 * 0, 10, 10, 1) + "\n"); sb.append("返B券: " +
		 * Convert.appendStringSize("", ManipulatePrecision.doubleToString(fbq),
		 * 0, 10, 10, 1)); new MessageBox(sb.toString()); } }
		 */
		}
	}

	public void getCustomerSellJf(SaleHeadDef saleHead, Vector saleGoods, Vector salePay)
	{
		try
		{
			String[] curJf = new String[1];
			// saleHead.hykh = "1010095663";
			if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
			{
				if (((Bszm_NetService) NetService.getDefault()).getCustomerSellJf(curJf, GlobalInfo.sysPara.mktcode, saleHead.hykh, String.valueOf(saleHead.ysje), getJfGoodsReqeustString(saleGoods), getJfPayRequestString(salePay)))
				{
					if (curJf[0] != null)
						saleHead.bcjf = Double.parseDouble(curJf[0].trim());
					// saleHead.ljjf = row[1].doubleValue();

					/*
					 * if (GlobalInfo.sysPara.sendhyjf == 'Y') { if
					 * (!sendHykJf(saleHead)) { new
					 * MessageBox("本笔积分同步失败无法获得累计积分\n请到会员中心查询累计积分!"); } }
					 */

					if ((Math.abs(saleHead.bcjf) > 0 || Math.abs(saleHead.ljjf) > 0) && GlobalInfo.sysPara.calcjfbyconnect == 'Y')
					{
						StringBuffer sb = new StringBuffer();
						sb.append("本笔交易产生的积分\n\n");
						sb.append("本次积分: " + Convert.appendStringSize("", String.valueOf(saleHead.bcjf), 0, 10, 10, 1) + "\n");
						// sb.append("累计积分: " + Convert.appendStringSize("",
						// String.valueOf(saleHead.ljjf), 0, 10, 10, 1));

						new MessageBox(sb.toString());
					}

					AccessDayDB.getDefault().updateSaleJf(saleHead.fphm, 1, saleHead.bcjf, saleHead.bcjf);
				}
				else
				{
					saleHead.bcjf = 0;
					new MessageBox("计算本笔交易小票积分失败\n请到会员中心查询积分!");
				}
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}

	public boolean sendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Sqldb sql)
	{
		if (!GlobalInfo.isOnline ) { return false; }//&& !(ConfigClass.DataBaseEnable.equals("Y") && GlobalInfo.RemoteDB != null)

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
		// 如果 ConfigClass.DataBaseEnable.equals("Y") 条件成立 则小票信息发送到远程数据库
		/*if (ConfigClass.DataBaseEnable.equals("Y"))
		{
			result = AccessRemoteDB.getDefault().writeSale(saleHead, saleGoods, salePayment);
		}
		else
		{
			result = NetService.getDefault().sendSaleData(saleHead, saleGoods, salePayment, retValue);
		}*/

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

		if (GlobalInfo.sysPara.whenprintbill == 'Y')
		{
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

			// 需要联网实时计算积分
			if (GlobalInfo.sysPara.calcjfbyconnect == 'Y' || GlobalInfo.sysPara.calcjfbyconnect == 'A')
			{
				getCustomerSellJf(saleHead, saleGoods, salePayment);
			}
		}

		// 需要将小票送往WebService
		sendSaleWebService(saleHead, saleGoods, salePayment);

		return true;
	}


	
	
}
