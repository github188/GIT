package custom.localize.Bjkl;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SuperMarketPopRuleDef;

import custom.localize.Bstd.Bstd_DataService;

public class Bjkl_DataService extends Bstd_DataService
{

	public boolean findSuperMarketPopBillNo(SuperMarketPopRuleDef ruleDef, String code, String gz, String catid, String ppcode, String spec, String time, String yhtime, String cardno)
	{
		if (GlobalInfo.isOnline)
		{
			Bjkl_NetService netservice = ((Bjkl_NetService) NetService.getDefault());
			boolean suc = netservice.findSuperMarketPopBillNo(ruleDef, code, gz, catid, ppcode, spec, time, yhtime, cardno, NetService.getDefault().getMemCardHttp(CmdDef.GETSMPOPBILLNO), CmdDef.GETSMPOPBILLNO);
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
			return ((Bjkl_AccessBaseDB) AccessBaseDB.getDefault()).findSuperMarketPopBillNo(ruleDef, code, gz, catid, ppcode, spec, time, yhtime, cardno);
		}

	}

	// 根据规则单号查询超市促销规则
	public boolean findSuperMarketPopRule(Vector ruleReqList, Vector rulePopList, SuperMarketPopRuleDef ruleDef)
	{

		if (GlobalInfo.isOnline)
		{
			Bjkl_NetService netservice = ((Bjkl_NetService) NetService.getDefault());
			boolean suc = netservice.findSuperMarketPopRule(ruleReqList, rulePopList, ruleDef, NetService.getDefault().getMemCardHttp(CmdDef.GETSMPOPRULE), CmdDef.GETSMPOPRULE);
			return suc;
		}
		else
		{

			//GlobalInfo.isOnline = true;

			return ((Bjkl_AccessBaseDB) AccessBaseDB.getDefault()).findSuperMarketPopRule(ruleReqList, rulePopList, ruleDef);			
		}
	}
	
	// 京客隆使用第三方卡系统，因此没有写的对应的过程// 得到顾客卡类型
	public boolean getNetCustomerType()
	{
		return true;
	}
}
