package custom.localize.Zmjc;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.ParaNodeDef;
import com.efuture.javaPos.Struct.SaleCustDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

import custom.localize.Bcrm.Bcrm_AccessBaseDB;
import custom.localize.Bcrm.Bcrm_DataService;

public class Zmjc_DataService extends Bcrm_DataService
{
	
	public boolean findClkInfo(String cardNO, ClkDef clk)
	{
		try
		{
			if (GlobalInfo.isOnline)
			{	
				Zmjc_NetService netservice = (Zmjc_NetService)NetService.getDefault();				
				return netservice.findClkInfo(cardNO, clk);
			}
			else
			{
				PosLog.getLog(this.getClass().getSimpleName()).info(Language.apply("查询常旅卡失败,网络不通"));
				new MessageBox(Language.apply("查询常旅卡失败,网络不通"));
			}
			
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		return false;
	}

//	 下载顾客信息和航班信息
	public boolean getNetMemoInfo()
	{
		if (GlobalInfo.isOnline)
		{
			Zmjc_NetService netService = (Zmjc_NetService)NetService.getDefault();
			//从POSSERVER获取顾客信息,并保存到local.db3
			netService.getSaleCfg();
			
			//从POSSERVER获取航班信息,并保存到local.db3
			netService.getFlights();
			
			//从POSSERVER获取返程航班信息,并保存到local.db3
			if (GlobalInfo.sysPara.isInputZCD == 'Y') netService.getRetFlights();
		}

		return true;
	}
	
	public boolean sendSaleDataCust(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, SaleCustDef saleCust, Sqldb sql)
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
		result = NetService.getDefault().sendSaleDataCust(saleHead, saleGoods, salePayment, saleCust, retValue);

		if (result==133)
		{
			//当返回结果为133时，则视为删除本地小票 for yans by 2013.8.31 for三亚
			deleteOneSell(saleHead.syjh, saleHead.fphm);
		}
		
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

		// 需要联网实时计算积分
		if (GlobalInfo.sysPara.calcjfbyconnect == 'Y' || GlobalInfo.sysPara.calcjfbyconnect == 'A')
		{
			getCustomerSellJf(saleHead);
		}

		// 需要将小票送往WebService
		sendSaleWebService(saleHead, saleGoods, salePayment);

		return true;
	}
	
	 public boolean findPopRuleCRM(GoodsPopDef popDef,String code,String gz,String uid,String rulecode,String catid,String ppcode,String time,String cardno,String cardtype, String isfjk, String grouplist,String djlb)
	 {		 
		 //return super.findPopRuleCRM(popDef, code, gz, uid, rulecode, catid, ppcode, time, cardno, cardtype, djlb);
		 //暂不处理CRM促销
		 //return false;
		 
		 if (GlobalInfo.isOnline)
	    	{
			 Zmjc_NetService netservice = (Zmjc_NetService)NetService.getDefault();
				boolean suc =  netservice.findPopRuleCRM(popDef, code, gz, uid, rulecode, catid, ppcode, time, cardno, cardtype,isfjk,grouplist,djlb, NetService.getDefault().getMemCardHttp(CmdDef.FINDCRMPOP), CmdDef.FINDCRMPOP);
		   		if (GlobalInfo.sysPara.searchPosAndCUST.equals("Y"))
	    		{
		   			GoodsPopDef popDef1 = new GoodsPopDef();
		   			boolean suc1 = netservice.findPopRuleCRM(popDef1, code, gz, uid, rulecode, catid, ppcode, time, cardno, cardtype,isfjk,grouplist,djlb,  NetService.getDefault().getMemCardHttp(CmdDef.FINDCRMPOP + 200), (CmdDef.FINDCRMPOP + 200));
		   			popDef.type = popDef1.type;
		   			popDef.mode = popDef.mode+"|"+popDef1.mode;
		   			popDef.jsrq = popDef1.jsrq;
		   			
		   			suc = suc || suc1;
	    		}
		   		
		   		return suc;
	    	}
	    	else
	    	{
	    		Bcrm_AccessBaseDB accessbasedb = (Bcrm_AccessBaseDB)AccessBaseDB.getDefault();
				return accessbasedb.findPopRuleCRM(popDef, code, gz, uid, rulecode, catid, ppcode, time,cardno,cardtype);
	    	}	
	 }
	 
	 public boolean deleteOneSell(String syjh, long fphm)
	 {
		 return true;
	 }
	 
	 public boolean getGoodsPackList(Vector goodsPackList, String goodsCode, String gz, double lsj)
	 {
		 if (GlobalInfo.isOnline)
		 {
			 return ((Zmjc_NetService)NetService.getDefault()).getGoodsPackList(goodsPackList, goodsCode, gz, lsj);
		 }
		 else
		 {
			 return ((Zmjc_AccessBaseDB)AccessBaseDB.getDefault()).getGoodsPackList(goodsPackList, goodsCode, gz, lsj);
		 }
	 }
	 
	 //查找价随量变规则
	 public boolean findBatchRule(SpareInfoDef sid, String code, String gz, String uid, String gys, String catid, String ppcode, String time, String cardno, String cardtype, String isfjk, String grouplist, String djlb, Http http)
	 {
		 if (GlobalInfo.isOnline)
		 {
			 return ((Zmjc_NetService) NetService.getDefault()).findBatchRule(sid, code, gz, uid, gys, catid, ppcode, time, cardno, cardtype, isfjk, grouplist, djlb, http);
		 }
		 else
		 {
			 return false;
		 }
	 }
	 
	 public int getGoodsDefResult(int result)
	 {

			// 播放声音
			playGoodsSound(result);
			//super.getGoodsDefResult(result);
			switch (result)
			{
				case 0:
					break;

				case 2:
					// if (GlobalInfo.syjDef.issryyy == 'N')
					// GlobalInfo.saleform.getSaleEvent().setBigInfo("该商品不属于收银机的收银范围",
					// "","");
					// else new MessageBox("该商品不属于收银机的收银范围");
					if (GlobalInfo.syjDef.issryyy == 'N')
					{
						if (GlobalInfo.sysPara.isinputnextgoods == 'Y')
							new MessageBox(Language.apply("该商品不属于收银机的收银范围"), GlobalVar.Exit);
						else
							new MessageBox(Language.apply("该商品不属于收银机的收银范围"), GlobalVar.Exit);
					}
					else
					{
						new MessageBox(Language.apply("该商品不属于收银机的收银范围"), GlobalVar.Exit);
					}
					break;

				case 3:

					// if (GlobalInfo.syjDef.issryyy == 'N')
					// GlobalInfo.saleform.getSaleEvent().setBigInfo("该商品不属于营业员的营业柜组",
					// "", "", -1);
					// else new MessageBox("该商品不属于营业员的营业柜组");
					if (GlobalInfo.syjDef.issryyy == 'N')
					{
						if (GlobalInfo.sysPara.isinputnextgoods == 'Y')
							new MessageBox(Language.apply("该商品不属于营业员的营业柜组"), GlobalVar.Exit);
						else
							new MessageBox(Language.apply("该商品不属于营业员的营业柜组"), GlobalVar.Exit);
					}
					else
					{
						new MessageBox(Language.apply("该商品不属于营业员的营业柜组"), GlobalVar.Exit);
					}
					break;

				case 4:
				case 5:
					//new MessageBox(Language.apply("该商品有多个柜组，需确定柜组"), GlobalVar.Exit); wangyong update for 2014.5.15 组包码里的商品为多柜组时不提示
					break;
					
				case 6:
					new MessageBox(Language.apply("该商品不在总部的控价范围内"), GlobalVar.Exit);
					break;
					
				case 7:
					new MessageBox(Language.apply("该商品重量异常"), GlobalVar.Exit);
					break;
					
				case 9:

					// if (GlobalInfo.syjDef.issryyy == 'N')
					// GlobalInfo.saleform.getSaleEvent().setBigInfo("该商品不能看板销售",
					// "","");
					// else new MessageBox("该商品不能看板销售");
					if (GlobalInfo.syjDef.issryyy == 'N')
					{
						if (GlobalInfo.sysPara.isinputnextgoods == 'Y')
							new MessageBox(Language.apply("该商品不能看板销售"), GlobalVar.Exit);
						else
							new MessageBox(Language.apply("该商品不能看板销售"), GlobalVar.Exit);
					}
					else
					{
						new MessageBox(Language.apply("该商品不能看板销售"), GlobalVar.Exit);
					}
					break;

				case 10:
					new MessageBox(Language.apply("该商品不允许在当前交易类型下销售"), GlobalVar.Exit);
					break;

				default:
					// if (GlobalInfo.syjDef.issryyy == 'N')
					// GlobalInfo.saleform.getSaleEvent().setBigInfo("找不到该商品信息",
					// "","");
					// else new MessageBox("找不到该商品信息");
					if (GlobalInfo.syjDef.issryyy == 'N')
					{
						if (GlobalInfo.sysPara.isinputnextgoods == 'Y')
							new MessageBox(Language.apply("找不到该商品信息"), GlobalVar.Exit);
						else
							new MessageBox(Language.apply("找不到该商品信息"), GlobalVar.Exit);
					}
					else
					{
						new MessageBox(Language.apply("找不到该商品信息"), GlobalVar.Exit);
					}
					result = -1;
					break;
			}

			return result;
	 }
	 
	 
	 /**
	  * 获取原退货小票的顾客信息
	  * @param thSyjh 原收银机号
	  * @param thFphm 原小票号
	  * @param saleCust 顾客信息
	  * @return
	  */
	 public int getBackSaleCustomerInfo(String thSyjh, long thFphm, SaleCustDef saleCust)
	 {
		 if (!GlobalInfo.isOnline) 
		 {
			 new MessageBox(Language.apply("获取原退货小票的顾客信息失败:网络不通！"));
			 return -9;
		 }
		 int inRet = -1;
		 try
		 {
			 inRet = ((Zmjc_NetService)NetService.getDefault()).getBackSaleCustomerInfo(thSyjh, thFphm ,saleCust);
			 if (inRet==0) 
			 {
				 splitCustomer(saleCust);
			 }
		 }
		 catch(Exception ex)
		 {
			 PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		 }
		 return inRet;
	 }
	 
	 private void splitCustomer(SaleCustDef saleCust)
	 {
		 try
		 {
			 if (saleCust != null && saleCust.custCount()>0)
			 {
				 ParaNodeDef nodeMemo = saleCust.custItem(CustInfoDef.CUST_SCMEMO);
				 if (nodeMemo!=null && nodeMemo.value!=null && nodeMemo.value.length()>0)
				 {
					String[] arr = nodeMemo.value.split(CustInfoDef.getSplitRegex());//分解CUST_SCMEMO1 ~ CUST_SCMEMO20

					String memoCode = "";
					String memoValue = "";

					for (int i = 0; i < arr.length; i++)
					{
						memoCode = CustInfoDef.CUST_SCMEMO + i;
						memoValue = arr[i];
						saleCust.custAdd(memoCode, memoValue);
					}
				 }
			 }
		 }
		 catch(Exception ex)
		 {
			 PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		 }
	 }
	
}
