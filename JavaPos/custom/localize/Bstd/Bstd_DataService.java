package custom.localize.Bstd;

import java.util.ArrayList;
import java.util.Vector;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TimeDate;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.JfSaleRuleDef;
import com.efuture.javaPos.Struct.SuperMarketPopRuleDef;
import com.efuture.javaPos.UI.Design.SetSystemTimeForm;

public class Bstd_DataService extends DataService
{

	public boolean findU51PopBillNo(SuperMarketPopRuleDef ruleDef, String code, String gz, String catid, String ppcode, String spec, String time, String yhtime, String cardno)
	{
		if (GlobalInfo.isOnline)
		{
			Bstd_NetService netservice = ((Bstd_NetService) NetService.getDefault());
			boolean suc = netservice.findU51PopBillNo(ruleDef, code, gz, catid, ppcode, spec, time, yhtime, cardno, NetService.getDefault().getMemCardHttp(CmdDef.GETSMPOPBILLNO), CmdDef.GETSMPOPBILLNO);
			if (suc)
			{

				if (ruleDef.djbh.length() > 0)
					return true;
				else
					return false;
			}
			return suc;
		}
		else
		{
			return ((Bstd_AccessBaseDB) AccessBaseDB.getDefault()).findU51PopBillNo(ruleDef, code, gz, catid, ppcode, spec, time, yhtime, cardno);
		}

	}

	// 根据规则单号查询超市促销规则
	public boolean findU51PopRule(Vector ruleReqList, Vector rulePopList, SuperMarketPopRuleDef ruleDef)
	{
		if (GlobalInfo.isOnline)
		{
			Bstd_NetService netservice = ((Bstd_NetService) NetService.getDefault());
			boolean suc = netservice.findU51PopRule(ruleReqList, rulePopList, ruleDef, NetService.getDefault().getMemCardHttp(CmdDef.GETSMPOPRULE), CmdDef.GETSMPOPRULE);
			return suc;
		}
		else
		{
			return ((Bstd_AccessBaseDB) AccessBaseDB.getDefault()).findU51PopRule(ruleReqList, rulePopList, ruleDef);			
		}
	}
	
	// 一条码对多编码处理
	public boolean findGoodsBarcodeList(String code, ArrayList codelist)
	{
		// 联网本地优先查询(Y-联网优先本地查询再查网上/Z-联网只查询本地)
		if (GlobalInfo.isOnline && GlobalInfo.sysPara.localfind != 'N')
		{
			((Bstd_AccessBaseDB) AccessBaseDB.getDefault()).findGoodsBarcodeList(code, codelist);
			if (codelist == null || codelist.size() <= 0)
			{
				// Z-联网只查询本地,失败不再查询网上
				if (GlobalInfo.sysPara.localfind == 'Z') { return false; }
			}
			else
			{
				return true;
			}
		}

		if (GlobalInfo.isOnline)
		{
			((Bstd_NetService) NetService.getDefault()).findGoodsBarcodeList(code, codelist);
		}
		else
		{
			((Bstd_AccessBaseDB) AccessBaseDB.getDefault()).findGoodsBarcodeList(code, codelist);
		}

		if (codelist == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	public Vector findCMPOPGoods(String rqsj, GoodsDef goods, String cardno, String cardtype)
	{
		// 联网本地优先查询(Y-联网优先本地查询再查网上/Z-联网只查询本地)
		if (GlobalInfo.isOnline && GlobalInfo.sysPara.localfind != 'N')
		{
			Vector v = ((Bstd_AccessBaseDB) AccessBaseDB.getDefault()).findCMPOPGoods(rqsj, goods, cardno, cardtype);
			if (v == null || v.size() <= 0)
			{
				// Z-联网只查询本地,失败不再查询网上
				if (GlobalInfo.sysPara.localfind == 'Z') { return v; }
			}
			else
			{
				return v;
			}
		}

		if (GlobalInfo.isOnline)
		{
			return ((Bstd_NetService) NetService.getDefault()).findCMPOPGoods(rqsj, goods, cardno, cardtype);
		}
		else
		{
			return ((Bstd_AccessBaseDB) AccessBaseDB.getDefault()).findCMPOPGoods(rqsj, goods, cardno, cardtype);
		}
	}


	public Vector findCMPOPGroup(String dqid, String ruleid, int group)
	{
		// 联网本地优先查询(Y-联网优先本地查询再查网上/Z-联网只查询本地)
		if (GlobalInfo.isOnline && GlobalInfo.sysPara.localfind != 'N')
		{
			Vector v = ((Bstd_AccessBaseDB) AccessBaseDB.getDefault()).findCMPOPGroup(dqid, ruleid, group);
			if (v == null || v.size() <= 0)
			{
				// Z-联网只查询本地,失败不再查询网上
				if (GlobalInfo.sysPara.localfind == 'Z') { return v; }
			}
			else
			{
				return v;
			}
		}

		if (GlobalInfo.isOnline)
		{
			return ((Bstd_NetService) NetService.getDefault()).findCMPOPGroup(dqid, ruleid, group);
		}
		else
		{
			return ((Bstd_AccessBaseDB) AccessBaseDB.getDefault()).findCMPOPGroup(dqid, ruleid, group);
		}
	}

	public Vector findCMPOPGift(String dqid, String ruleid, String ladderid)
	{
		// 联网本地优先查询(Y-联网优先本地查询再查网上/Z-联网只查询本地)
		if (GlobalInfo.isOnline && GlobalInfo.sysPara.localfind != 'N')
		{
			Vector v = ((Bstd_AccessBaseDB) AccessBaseDB.getDefault()).findCMPOPGift(dqid, ruleid, ladderid);
			if (v == null || v.size() <= 0)
			{
				// Z-联网只查询本地,失败不再查询网上
				if (GlobalInfo.sysPara.localfind == 'Z') { return v; }
			}
			else
			{
				return v;
			}
		}

		if (GlobalInfo.isOnline)
		{
			return ((Bstd_NetService) NetService.getDefault()).findCMPOPGift(dqid, ruleid, ladderid,CmdDef.FINDPOPGIFT);
		}
		else
		{
			return ((Bstd_AccessBaseDB) AccessBaseDB.getDefault()).findCMPOPGift(dqid, ruleid, ladderid);
		}
	}


	// 查找商品是否存在换购规则
	public boolean getJfExchangeGoods(JfSaleRuleDef jsrd, String barcode, String custcode, String type)
	{
		if (!GlobalInfo.isOnline)
			return false;

		if (!((Bstd_NetService) NetService.getDefault()).getJfExchangeGoods(jsrd, barcode, custcode, type))
			return false;

		return true;
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

				if (ConfigClass.ShowDateDialog)
				{
					new SetSystemTimeForm(true);
				}
				else
				{
					// 设置系统时间
					ManipulateDateTime mdt = new ManipulateDateTime();
					mdt.setDateTime(ManipulateDateTime.getConversionDate(mdt.getDateByEmpty().trim()), ManipulateDateTime.getConversionTime(mdt.getTimeByEmpty().trim()));
				}
			}

			return false;
		}
	}
}
