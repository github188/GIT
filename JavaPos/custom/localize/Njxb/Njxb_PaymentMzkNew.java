package custom.localize.Njxb;

import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.SaleGoodsDef;

public class Njxb_PaymentMzkNew extends PaymentMzk
{
	public void setRequestDataByFind(String track1, String track2, String track3)
	{
		super.setRequestDataByFind(track1, track2, track3);
		// 传入手续费类型以便返回费率
		mzkreq.num1 = ((SaleGoodsDef)saleBS.saleGoods.get(0)).num4;
	}	
	
	protected boolean saveFindMzkResultToSalePay()
	{
		salepay.str6 = mzkret.str4;
		salepay.str2 = mzkret.str1;
		return super.saveFindMzkResultToSalePay();
	}
}
