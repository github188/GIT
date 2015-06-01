package custom.localize.Htsc;

import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.GoodsPopDef;

import custom.localize.Bcrm.Bcrm_AccessBaseDB;
import custom.localize.Cmls.Cmls_DataService;
import custom.localize.Htsc.Htsc_NetService;

public class Htsc_DataService extends Cmls_DataService 
{
	//查找满减满增促销
		public boolean findPopRuleCRM(GoodsPopDef popDef, String code, String gz, String uid, String rulecode, String catid, String ppcode, String time, String cardno, String cardtype, String isfjk, String grouplist, String djlb,String ssdh)
		{
			if (GlobalInfo.isOnline)
			{
				Htsc_NetService netservice = (Htsc_NetService) NetService.getDefault();
				boolean suc = netservice.findPopRuleCRM(popDef, code, gz, uid, rulecode, catid, ppcode, time, cardno, cardtype, isfjk, grouplist, djlb,
														NetService.getDefault().getMemCardHttp(CmdDef.FINDCRMPOP), CmdDef.FINDCRMPOP,ssdh);

				if (GlobalInfo.sysPara.searchPosAndCUST.equals("Y"))
				{
					GoodsPopDef popDef1 = new GoodsPopDef();
					boolean suc1 = netservice.findPopRuleCRM(popDef1, code, gz, uid, rulecode, catid, ppcode, time, cardno, cardtype, isfjk, grouplist,
																djlb, NetService.getDefault().getMemCardHttp(CmdDef.FINDCRMPOP + 200),
																CmdDef.FINDCRMPOP + 200,ssdh);
					popDef.type = popDef1.type;
					popDef.mode = popDef.mode + "|" + popDef1.mode;
					popDef.jsrq = popDef1.jsrq;

					suc = suc || suc1;
				}

				return suc;
			}
			else
			{
				Bcrm_AccessBaseDB accessbasedb = (Bcrm_AccessBaseDB) AccessBaseDB.getDefault();
				return accessbasedb.findPopRuleCRM(popDef, code, gz, uid, rulecode, catid, ppcode, time, cardno, cardtype);
			}
		}
}
