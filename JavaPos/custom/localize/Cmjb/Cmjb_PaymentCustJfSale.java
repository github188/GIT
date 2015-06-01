package custom.localize.Cmjb;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentCustJfSale;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Cmjb_PaymentCustJfSale extends PaymentCustJfSale 
{
	public Cmjb_PaymentCustJfSale()
	{
	}
	
	public Cmjb_PaymentCustJfSale(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode,sale);
	}
	
	public Cmjb_PaymentCustJfSale(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay,head);
	}
	
	protected String getDisplayStatusInfo()
	{
		String line = "";
		
		if (SellType.ISSALE(salehead.djlb))
		{			
			allowpayje = Math.min(getAccountAllowPay(), mzkret.ye);
			
			if (mzkret.memo != null && mzkret.memo.split(",").length == 2)
			{
				String[] num = mzkret.memo.split(",");
				
//				line +="当前会员的积分是:" + mzkret.value1 + "\n" + (int)Double.parseDouble(num[0]) +" 积分兑换 " + ManipulatePrecision.doubleToString(Double.parseDouble(num[1])) + "元\n最大可收积分消费金额: "+allowpayje+"元";
				line +=Language.apply("当前会员的积分是:{0}\n{1} 积分兑换 {2}元\n最大可收积分消费金额: {3}元" ,new Object[]{mzkret.value1+"" ,(int)Double.parseDouble(num[0])+"" ,ManipulatePrecision.doubleToString(Double.parseDouble(num[1])) ,allowpayje+""});
				line += "\n档期积分 可兑换积分为:" + saleBS.curCustomer.valstr4;
			}
		}
		return line;
	}

}
