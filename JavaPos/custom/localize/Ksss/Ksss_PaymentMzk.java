package custom.localize.Ksss;


import custom.localize.Bcrm.Bcrm_PaymentMzk;

public class Ksss_PaymentMzk extends Bcrm_PaymentMzk
{
	public void showAccountYeMsg()
	{
		// 0402团购券消费之后不提示券信息
		if ("0402".equals(salepay.paycode)) messDisplay = false;
		super.showAccountYeMsg();
	}
}
