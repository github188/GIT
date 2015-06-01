package custom.localize.Wqbh;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bcrm.Bcrm_PaymentMzk;

public class Wqbh_PaymentMzk extends Bcrm_PaymentMzk
{
	public Wqbh_PaymentMzk()
	{
		super();
	}
	
	public Wqbh_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		super(mode,sale);
	}
	
	public Wqbh_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		super(pay,head);
	}
	
	protected boolean setRequestDataByAccount()
	{
		//得到消费序号
		long seqno = getMzkSeqno();
		if (seqno <= 0) return false;

		// 打消费交易包
		mzkreq.seqno = seqno;
		mzkreq.je = salepay.ybje;
		mzkreq.syjh = ConfigClass.CashRegisterCode;
		mzkreq.mktcode = GlobalInfo.sysPara.mktcode;
		if (GlobalInfo.sysPara.mktcode.indexOf(",") > -1 && GlobalInfo.sysPara.mktcode.split(",").length > 1)
		{
			mzkreq.mktcode = GlobalInfo.sysPara.mktcode.split(",")[1];
		}
		mzkreq.syyh = GlobalInfo.posLogin.gh;
		mzkreq.paycode = salepay.paycode;
		mzkreq.invdjlb = ((salehead != null) ? salehead.djlb : "");

		// 告诉后台过程磁道信息是存放的是卡号,只采用卡号记账方式,不使用磁道记账方式
		mzkreq.track1 = "CARDNO";
		mzkreq.track2 = salepay.payno;

		return true;
	}
}
