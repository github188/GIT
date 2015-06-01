package custom.localize.Jwyt;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.commonKit.TimeDate;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.JfSaleRuleDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bstd.Bstd_DataService;
import custom.localize.Bstd.Bstd_NetService;

public class Jwyt_DataService extends Bstd_DataService
{

	public Vector findEWMCMPOPGoods(String rqsj, GoodsDef goods, String cardno, String cardtype)
	{
		// 联网本地优先查询(Y-联网优先本地查询再查网上/Z-联网只查询本地)
		/*
		 * if (GlobalInfo.isOnline && GlobalInfo.sysPara.localfind != 'N') {
		 * Vector v = ((Bstd_AccessBaseDB)
		 * AccessBaseDB.getDefault()).findCMPOPGoods(rqsj, goods, cardno,
		 * cardtype); if (v == null || v.size() <= 0) { // Z-联网只查询本地,失败不再查询网上 if
		 * (GlobalInfo.sysPara.localfind == 'Z') { return v; } } else { return
		 * v; } }
		 */

		if (GlobalInfo.isOnline) { return ((Bstd_NetService) NetService.getDefault()).findCMPOPGoods(rqsj, goods, cardno, cardtype, CmdDef.FINDGOODEWMSCMPOP); }
		return null;
		/*
		 * else { return ((Bstd_AccessBaseDB)
		 * AccessBaseDB.getDefault()).findCMPOPGoods(rqsj, goods, cardno,
		 * cardtype); }
		 */
	}

	// 查找满减满增促销
	public boolean findPopRuleCRM(GoodsPopDef popDef, String code, String gz, String uid, String rulecode, String catid, String ppcode, String time, String cardno, String cardtype, String djlb)
	{
		if (GlobalInfo.isOnline)
		{
			Jwyt_NetService netservice = (Jwyt_NetService) NetService.getDefault();
			return netservice.findPopRuleCRM(popDef, code, gz, uid, rulecode, catid, ppcode, time, cardno, cardtype, NetService.getDefault().getMemCardHttp(CmdDef.FINDCRMPOP));
		}
		else
		{
			Jwyt_AccessBaseDB accessbasedb = (Jwyt_AccessBaseDB) AccessBaseDB.getDefault();
			return accessbasedb.findPopRuleCRM(popDef, code, gz, uid, rulecode, catid, ppcode, time, cardno, cardtype);
		}
	}

	public boolean findHYZK(GoodsPopDef popDef, String code, String custtype, String gz, String catid, String ppcode, String specialInfo)
	{
		if (GlobalInfo.isOnline)
		{
			Jwyt_NetService netservice = (Jwyt_NetService) NetService.getDefault();
			return netservice.findHYZK(popDef, code, custtype, gz, catid, ppcode, specialInfo, NetService.getDefault().getMemCardHttp(CmdDef.GETCRMVIPZK));
		}
		else
		{
			Jwyt_AccessBaseDB accessbasedb = (Jwyt_AccessBaseDB) AccessBaseDB.getDefault();
			return accessbasedb.findHYZK(popDef, code, custtype, gz, catid, ppcode, specialInfo);
		}
	}

	public void getCustomerSellJf(SaleHeadDef saleHead)
	{
		getSellJf(saleHead);
	}

	// 获取小票实时积分
	public void getSellJf(SaleHeadDef saleHead)
	{
		if (SellType.ISBACK(saleHead.djlb))
		{
			if (saleHead.hykh == null || saleHead.hykh.equals(""))
				saleHead.hykh = "";

			getCustomerJf(saleHead);
			return;
		}

		if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
		{
			getCustomerJf(saleHead);
			return;
		}
		return;
	}

	public void getCustomerJf(SaleHeadDef saleHead)
	{
		String[] row = new String[4];

		if (NetService.getDefault().getCustomerSellJf(row, GlobalInfo.sysPara.mktcode, saleHead.syjh, String.valueOf(saleHead.fphm), saleHead.hykh, saleHead.hytype))
		{
			saleHead.bcjf = Double.parseDouble(row[0]);
			saleHead.ljjf = Double.parseDouble(row[1]);
			saleHead.str5 = row[2];

			if (GlobalInfo.sysPara.sendhyjf == 'Y')
			{
				if (!sendHykJf(saleHead))
				{
					new MessageBox("本笔积分同步失败无法获得累计积分\n请到会员中心查询累计积分!");
				}
			}

			if ((Math.abs(saleHead.bcjf) > 0 || Math.abs(saleHead.ljjf) > 0) && GlobalInfo.sysPara.calcjfbyconnect == 'Y')
			{
				StringBuffer sb = new StringBuffer();
				sb.append("本笔交易存在积分\n");
				sb.append("本次积分: " + Convert.appendStringSize("", String.valueOf(saleHead.bcjf), 0, 10, 10, 1) + "\n");
				sb.append("累计积分: " + Convert.appendStringSize("", String.valueOf(saleHead.ljjf), 0, 10, 10, 1));

				new MessageBox(sb.toString());
			}

			AccessDayDB.getDefault().updateSaleJf(saleHead.fphm, 1, saleHead.bcjf, saleHead.ljjf);
		}
		else
		{
			saleHead.bcjf = 0;
			new MessageBox("计算本笔交易小票积分失败\n请到会员中心查询积分!");
		}

	}

	public boolean getServerTime(boolean settime)
	{
		TimeDate time = new TimeDate();

		if (NetService.getDefault().getServerTime(time))
		{
			ManipulateDateTime mdt = new ManipulateDateTime();

			// 设置本机时间
			mdt.setDateTime(time);

			GlobalInfo.isOnline = true;

			return true;
		}
		else
		{
			GlobalInfo.isOnline = false;

			if (settime)
			{
				new MessageBox("连接网络失败,系统进入脱网状态!");

				// 设置系统时间
				ManipulateDateTime mdt = new ManipulateDateTime();
				mdt.setDateTime(ManipulateDateTime.getConversionDate(mdt.getDateByEmpty().trim()), ManipulateDateTime.getConversionTime(mdt.getTimeByEmpty().trim()));

				// 弹出窗口手工设置时间,仅设置时间
				// new SetSystemTimeForm(true);
			}

			return false;
		}
	}

	// 查找商品是否存在换购规则
	public boolean getJfExchangeGoods(JfSaleRuleDef jsrd, String barcode, String gz, String custcode, String type)
	{
		if (!GlobalInfo.isOnline)
			return false;

		if (!((Jwyt_NetService) NetService.getDefault()).getJfExchangeGoods(jsrd, barcode, gz, custcode, type))
			return false;

		return true;
	}

	public boolean sendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Sqldb sql)
	{
		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.elementAt(i);
			sgd.str4 = "";
		}

		return super.sendSaleData(saleHead, saleGoods, salePayment, sql);
	}
}
