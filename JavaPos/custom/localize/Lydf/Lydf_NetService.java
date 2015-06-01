package custom.localize.Lydf;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bstd.Bstd_NetService;

public class Lydf_NetService extends Bstd_NetService
{
	public boolean sendSaleTax(SaleHeadDef saleHead, Lydf_TaxInfo tax)
	{
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		try
		{
			if (!GlobalInfo.isOnline)
				return false;

			int result = -1;

			cmdHead = new CmdHead(CmdDef.LYDF_SENDTAX);

			String[] value = { GlobalInfo.sysPara.mktcode, saleHead.syjh, saleHead.syyh, String.valueOf(saleHead.fphm), String.valueOf(saleHead.djlb), String.valueOf(tax.oprtype), String.valueOf(saleHead.ysje), saleHead.yfphm, tax.RQ, tax.JE, tax.SE, tax.PH, tax.SK, tax.BZ, tax.LSH,ManipulateDateTime.getCurrentDateTime(), tax.memo };
			String[] arg = { "mkt", "syjh", "syyh", "fphm", "djlb", "oprtype", "kpje", "ythfp", "taxrq", "taxje", "taxse", "taxph", "taxsk", "taxbz", "taxlsh","sendrq", "memo" };

			line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

			result = HttpCall(line, "税控发票发送失败");

			if (result == 0)
				return true;

			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
}
