package custom.localize.Hyjw;

import java.util.Vector;

import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bstd.Bstd_DataService;

public class Hyjw_DataService extends Bstd_DataService
{
	public boolean sendHykJf(SaleHeadDef saleHead, Vector saleGoods, Vector salePay, boolean again)
	{
		if (saleHead.hykh.length() > 0)
		{
			if (Hyjw_MzkModule.getDefault().sendScore(saleHead.djlb, saleHead.bcjf))
			{
				AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDHYKJF, GlobalInfo.balanceDate + "," + saleHead.fphm);
				return false;
			}
		}
		return true;
	}
}
