package custom.localize.Cbbh;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.Payment;

//重百欠款
public class Cbbh_PaymentCredit extends Payment {
		
	protected boolean checkMoneyValid(String money, double ye)
	{
		//销售的时候判断
		if(!SellType.ISSALE(saleBS.saletype)) 
			return super.checkMoneyValid(money, ye);
		else
		{
			if(CommonMethod.isNull(saleBS.saleHead.cczz_zktmp))
			{
				new MessageBox("本单不能使用此付款方式！");
				return false;
			}
			
			double credit = ((Cbbh_NetService)NetService.getDefault()).getCredit(saleBS.saleHead.cczz_zktmp.trim());
			if(Double.compare(credit, Double.valueOf(money).doubleValue()) > 0)
			{
				return super.checkMoneyValid(money, ye);
			}
			else
			{
				new MessageBox("使用此付款方式支付的金额不能超过["+String.valueOf(credit)+"]");
				return false;
			}
		}
	}
}
