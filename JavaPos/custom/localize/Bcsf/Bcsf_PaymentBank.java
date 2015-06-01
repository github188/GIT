package custom.localize.Bcsf;

import com.efuture.javaPos.Payment.PaymentBank;


// 只有广众和知而行才使用当前对象
public class Bcsf_PaymentBank extends PaymentBank
{
	// 不显示撤消提示
	public String getCancelPayHint()
	{
		return null;
	}
	
	// 不显示撤消提示
	public String getBackPayHint()
	{
		return null;
	}
}
