package custom.localize.Cbbh;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bcrm.Bcrm_DataService;

public class Cbbh_DataService extends Bcrm_DataService
{
	public boolean findWCCRules(String billno,Vector rules)
	{
		if (GlobalInfo.isOnline)
		{
            if (!((Cbbh_NetService)NetService.getDefault()).findWCCRules(billno, rules)) return false;			
		}
		else
		{
			if (!((Cbbh_AccessBaseDB)AccessBaseDB.getDefault()).findWCCRules(billno, rules)) return false;
		}
		
		return true;
	}

    /*public boolean getHHback(String ysyjh, StringBuffer yfphm)
	{
		if (((Cbbh_AccessDayDB) Cbbh_AccessDayDB.getDefault()).getlasthhbackinfo(ysyjh, yfphm)) { return true; }

		return false;
	}*/
	
	/*public boolean getBackSaleInfo(String syjh, String fphm, SaleHeadDef shd, Vector saleDetailList, Vector payDetail)
	{
		if(super.getBackSaleInfo(syjh, fphm, shd, saleDetailList, payDetail))
		{
			//按原单退货，且刷了会员卡时，则从CRM里检查是否能退货
			if(syjh!=null && Convert.toLong(fphm)>0 && shd.hykh!=null)
			{
				if(shd.hykh.length()>0)
				{
					//检查是否能退货
					//if(!NetService.getDefault().checkSaleTH_Dos(GlobalInfo.sysPara.mktcode, syjh, Convert.toLong(fphm))) return false;
				}
			}
			return true;
		}
		return false;
	}*/
	// 获取私有参数
	public boolean getNetSysPara()
	{
		if (GlobalInfo.isOnline)
        {
            if (!NetService.getDefault().getSysPara()) return false;
        }

        // 读取CRM参数信息
		if (GlobalInfo.isOnline)
		{
			// 读取POS参数但不做paraFinish处理,以避免重复处理
			if (!AccessLocalDB.getDefault().readSysPara(false)) return false;
			/*
			if (NetService.getDefault().getMemCardHttp(CmdDef.GETCRMPARA) != GlobalInfo.localHttp)
			{
				if (!NetService.getDefault().getSysPara(null,false,CmdDef.GETCRMPARA)) return false;
			}*/
			
			if (!NetService.getDefault().getSysPara(NetService.getDefault().getMemCardHttp(CmdDef.GETCRMPARA),false,CmdDef.GETCRMPARA)) return false;
		}
        
        return AccessLocalDB.getDefault().readSysPara();
	}
	
	public boolean sendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{	
		return sendSaleData(saleHead, saleGoods, salePayment, null);
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
			boolean isSendToCrm = false;
			if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
			{
				isSendToCrm=true;
			}
			else
			{
				if(GlobalInfo.sysPara.noCustSendToPop=='Y')
				{
					isSendToCrm=true;
				}
			}
			
			if(isSendToCrm) sendSaleDataToMemberDB(saleHead, saleGoods, salePayment, again);
		}

		// 需要联网实时计算返券
		if (GlobalInfo.sysPara.calcfqbyreal == 'Y')
		{
			getSellRealFQ(saleHead);
		}

		/*if (GlobalInfo.sysPara.calcmystorecouponbyreal != 'N')
		{
			CreatePayment.getDefault().getPaymentMyStore().getMyStoreCoupon(saleHead, saleGoods, salePayment);
		}*/

		/*// 需要联网实时计算积分
		if (GlobalInfo.sysPara.calcjfbyconnect == 'Y' || GlobalInfo.sysPara.calcjfbyconnect == 'A')
		{
			getCustomerSellJf(saleHead, saleGoods, salePayment);
		}*/

		/*// 需要将小票送往WebService
		sendSaleWebService(saleHead, saleGoods, salePayment);*/

		return true;
	}
	

	// 发生小票到CRM
	public void sendSaleDataToMemberDB(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, boolean again)
	{
		boolean sendok = true;

		Vector vecJF = new Vector();
		int result = NetService.getDefault().sendExtendSaleData(saleHead, saleGoods, salePayment, vecJF);

		// 非重发如果返回不为0，表示小票发送失败
		if (!again && result != 0)
			sendok = false;

		// 重发小票，如果返回为2表示小票已存在，0表示成功，其他为送网失败
		if (again && result != 0 && result != 2)
			sendok = false;

		if (result == -500)
			sendok = true;

		// 送网失败，记录小票未发送到CRM数据源的命令任务
		if (!sendok)
		{
			AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDINVTOEXTEND, GlobalInfo.balanceDate + "," + saleHead.fphm);
			
			/*// 提示
			if (!NetService.getDefault().isStopService())
				new MessageBox(Language.apply("上传小票到会员服务器失败\n请去会员中心查询!"));*/
		}
		else
		{
			//记录小票的实时计算积分
			if(vecJF!=null && vecJF.size()>=2)
			{
				saleHead.bcjf=Convert.toDouble(vecJF.elementAt(0));
				saleHead.ljjf=Convert.toDouble(vecJF.elementAt(1));
				getCustomerSellJf(saleHead);
			}
		}
	}
	

	// 获取小票实时返券
	public void getSellRealFQ(SaleHeadDef saleHead)
	{
		String[] row = new String[3];

		if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
		{
			if (NetService.getDefault().getSellRealFQ(row, GlobalInfo.sysPara.mktcode, saleHead.syjh, String.valueOf(saleHead.fphm)))
			{
				//saleHead.memo = row[0] + "," + row[1];
				saleHead.memo = row[0];

				//double faq = Convert.toDouble(row[0]);
				//double fbq = Convert.toDouble(row[1]);
				//AccessDayDB.getDefault().updateSaleJf(saleHead.fphm, 2, faq, fbq);
				AccessDayDB.getDefault().updateSaleJf(saleHead.fphm, 4, 0, 0, saleHead.memo);
				PosLog.getLog(this.getClass()).info("getSellRealFQ_Dos(" + saleHead.syjh + "," + saleHead.fphm + ") " + saleHead.memo);
				//重百不提示

				/*// 提示
				if ((Convert.toDouble(row[0]) > 0) || (Convert.toDouble(row[1]) > 0))
				{
					StringBuffer sb = new StringBuffer();
					sb.append(Language.apply("本笔交易有活动返券\n"));
					sb.append(Language.apply("返A券: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(faq), 0, 10, 10, 1) + "\n");
					sb.append(Language.apply("返B券: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(fbq), 0, 10, 10, 1));
					new MessageBox(sb.toString());
				}*/
			}
			else
			{
				//saleHead.memo = "-1,-1";
				saleHead.memo = "";
				//new MessageBox(Language.apply("计算本笔交易小票返券失败\n请到会员中心查询返券!"));
				PosLog.getLog(this.getClass()).info("getSellRealFQ_Dos(" + saleHead.syjh + "," + saleHead.fphm + ") 实时返券失败");
			}
		}
	}

	// 获取小票实时积分
	public void getCustomerSellJf(SaleHeadDef saleHead)
	{
		//String[] row = new String[4];

		if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
		{
			AccessDayDB.getDefault().updateSaleJf(saleHead.fphm, 1, saleHead.bcjf, saleHead.ljjf);
			/*if (NetService.getDefault().getCustomerSellJf(row, GlobalInfo.sysPara.mktcode, saleHead.syjh, String.valueOf(saleHead.fphm), saleHead.hykh, saleHead.hytype))
			{
				saleHead.bcjf = Double.parseDouble(row[0]);
				saleHead.ljjf = Double.parseDouble(row[1]);
				saleHead.str5 = row[2];

				if (GlobalInfo.sysPara.sendhyjf == 'Y')
				{
					if (!sendHykJf(saleHead))
					{
						new MessageBox(Language.apply("本笔积分同步失败无法获得累计积分\n请到会员中心查询累计积分!"));
					}
				}

				if ((Math.abs(saleHead.bcjf) > 0 || Math.abs(saleHead.ljjf) > 0) && GlobalInfo.sysPara.calcjfbyconnect == 'Y')
				{
					StringBuffer sb = new StringBuffer();
					sb.append(Language.apply("本笔交易存在积分\n"));
					sb.append(Language.apply("本次积分: ") + Convert.appendStringSize("", String.valueOf(saleHead.bcjf), 0, 10, 10, 1) + "\n");
					sb.append(Language.apply("累计积分: ") + Convert.appendStringSize("", String.valueOf(saleHead.ljjf), 0, 10, 10, 1));

					new MessageBox(sb.toString());
				}

				AccessDayDB.getDefault().updateSaleJf(saleHead.fphm, 1, saleHead.bcjf, saleHead.ljjf);
			}
			else
			{
				saleHead.bcjf = 0;
				new MessageBox(Language.apply("计算本笔交易小票积分失败\n请到会员中心查询积分!"));
			}*/
		}
	}
	
	public int getGoodsDefResult(int result)
	{
		// 播放声音
		playGoodsSound(result);

		switch (result)
		{
			case 0:
				break;

			case 4:
				// new MessageBox("该商品有多个柜组，需确定柜组");
				break;

			case 2:
				// if (GlobalInfo.syjDef.issryyy == 'N')
				// GlobalInfo.saleform.getSaleEvent().setBigInfo("该商品不属于收银机的收银范围",
				// "","");
				// else new MessageBox("该商品不属于收银机的收银范围");
				/*if (GlobalInfo.syjDef.issryyy == 'N')
				{
					if (GlobalInfo.sysPara.isinputnextgoods == 'Y')
						new MessageBox(Language.apply("该商品不属于收银机的收银范围"), GlobalVar.Validation);
					else
						new MessageBox(Language.apply("该商品不属于收银机的收银范围"), GlobalVar.Enter);
				}
				else
				{
					new MessageBox(Language.apply("该商品不属于收银机的收银范围"));
				}*/
				break;

			case 3:

				// if (GlobalInfo.syjDef.issryyy == 'N')
				// GlobalInfo.saleform.getSaleEvent().setBigInfo("该商品不属于营业员的营业柜组",
				// "", "", -1);
				// else new MessageBox("该商品不属于营业员的营业柜组");
				/*if (GlobalInfo.syjDef.issryyy == 'N')
				{
					if (GlobalInfo.sysPara.isinputnextgoods == 'Y')
						new MessageBox(Language.apply("该商品不属于营业员的营业柜组"), GlobalVar.Validation);
					else
						new MessageBox(Language.apply("该商品不属于营业员的营业柜组"), GlobalVar.Enter);
				}
				else
				{
					new MessageBox(Language.apply("该商品不属于营业员的营业柜组"));
				}*/
				break;

			case 9:

				// if (GlobalInfo.syjDef.issryyy == 'N')
				// GlobalInfo.saleform.getSaleEvent().setBigInfo("该商品不能看板销售",
				// "","");
				// else new MessageBox("该商品不能看板销售");
				if (GlobalInfo.syjDef.issryyy == 'N')
				{
					if (GlobalInfo.sysPara.isinputnextgoods == 'Y')
						new MessageBox(Language.apply("该商品不能看板销售"), GlobalVar.Validation);
					else
						new MessageBox(Language.apply("该商品不能看板销售"), GlobalVar.Enter);
				}
				else
				{
					new MessageBox(Language.apply("该商品不能看板销售"));
				}
				break;

			case 10:
				new MessageBox(Language.apply("该商品不允许在当前交易类型下销售"));
				break;

			default:
				// if (GlobalInfo.syjDef.issryyy == 'N')
				// GlobalInfo.saleform.getSaleEvent().setBigInfo("找不到该商品信息",
				// "","");
				// else new MessageBox("找不到该商品信息");
				if (GlobalInfo.syjDef.issryyy == 'N')
				{
					if (GlobalInfo.sysPara.isinputnextgoods == 'Y')
						new MessageBox(Language.apply("找不到该商品信息"), GlobalVar.Validation);
					else
						new MessageBox(Language.apply("找不到该商品信息"), GlobalVar.Enter);
				}
				else
				{
					new MessageBox(Language.apply("找不到该商品信息"));
				}
				result = -1;
				break;
		}

		return result;
	}
}
