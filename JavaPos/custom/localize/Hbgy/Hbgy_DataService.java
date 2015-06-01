package custom.localize.Hbgy;

import java.util.Vector;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.GoodsDef;

import custom.localize.Bstd.Bstd_AccessBaseDB;
import custom.localize.Bstd.Bstd_DataService;
import custom.localize.Bstd.Bstd_NetService;

public class Hbgy_DataService extends Bstd_DataService
{
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
			return ((Hbgy_AccessBaseDB) AccessBaseDB.getDefault()).findCMPOPGoods(rqsj, goods, cardno, cardtype);
		}
	}
}
