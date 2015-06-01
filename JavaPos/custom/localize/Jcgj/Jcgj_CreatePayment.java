package custom.localize.Jcgj;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentCustJfSale;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bcrm.Bcrm_CreatePayment;

public class Jcgj_CreatePayment extends Bcrm_CreatePayment
{
	public PaymentCustJfSale getPaymentJfChange(PayModeDef mode, SaleBS sale)
	{
		return new Jcgj_PaymentCustJfSale(mode, sale);
	}

	public boolean sendAllPaymentCz()
	{
		// 按付款方式发送各自的冲正数据
		boolean ok = true;
		boolean yscz = false;
		for (int i = 0; GlobalInfo.payMode != null && i < GlobalInfo.payMode.size(); i++)
		{
			PayModeDef pmd = (PayModeDef) GlobalInfo.payMode.elementAt(i);
			
			// 只要发现存在银石结账的付款方式，一次全部撤销
			if (pmd.code.equals("0405") || pmd.code.equals("0509") || pmd.code.equals("0111") || pmd.code.equals("0408"))
			{
				if (noYsCz()) continue;
				if (yscz) continue;
				SaleHeadDef saleHead = GlobalInfo.saleform.sale.saleBS.saleHead;
				Jcgj_Svc svc = new Jcgj_Svc("svc_void_sale", saleHead, "");
				if (svc.doYsCard(null))
				{
					yscz = true;
					continue;
				}
				else
				{
					new MessageBox("银石卡系统冲正失败！");
					return false;
				}
			}

			ok = createPaymentAll(true, pmd, null, null, null).sendAccountCz();
		}
		return ok;
	}

	public boolean noYsCz()
	{
		if (GlobalInfo.syjStatus.status == StatusType.STATUS_START || GlobalInfo.syjStatus.status == StatusType.STATUS_LOGIN
				|| GlobalInfo.syjStatus.status == StatusType.STATUS_SHUTDOWN) { return true; }
		return false;
	}
}
