package custom.localize.Sdyz;

import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Sdyz_PaymentMzk extends PaymentMzk
{ 
	public Sdyz_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		super(mode,sale);
	}

	public Sdyz_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		super(pay,head);
	}

	public Sdyz_PaymentMzk()
	{
		super();
	}

	public void setRequestDataBySalePay()
	{
		super.setRequestDataBySalePay();

		// 银座卡交易门店号要加前缀
		mzkreq.mktcode = Sdyz_CustomGlobalInfo.getDefault().sysPara.cardflag + GlobalInfo.sysPara.mktcode;
	}
	
	public void setRequestDataByFind(String track1, String track2, String track3)
	{
		super.setRequestDataByFind(track1,track2,track3);

		// 银座卡交易门店号要加前缀
		mzkreq.mktcode = Sdyz_CustomGlobalInfo.getDefault().sysPara.cardflag + GlobalInfo.sysPara.mktcode;
	}
}
