package custom.localize.Hhdl;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bstd.Bstd_DataService;

public class Hhdl_DataService extends Bstd_DataService
{
	public Vector getSaleTicketMSInfo(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		//退货状态下，并且非全退状态
		if (SellType.ISBACK(saleHead.djlb) && saleHead.str3.equals("N"))
		{
			new MessageBox("非整单退货,无法计算退券");
			return null;
		}

		if (GlobalInfo.sysPara.calcfqbyreal != 'A')
			return null;

		// 查询小票实时赠品信息
		Vector v = new Vector();
		NetService netservice = NetService.getDefault();
		if (netservice.getSaleTicketMSInfo(v, GlobalInfo.sysPara.mktcode, saleHead.syjh, String.valueOf(saleHead.fphm), saleHead.printnum > 0 ? "Y" : "N", NetService.getDefault().getMemCardHttp(CmdDef.GETMSINFO)))
			return v;

		return null;
	}

}
