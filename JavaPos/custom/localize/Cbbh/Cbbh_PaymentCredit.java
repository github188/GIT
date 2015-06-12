package custom.localize.Cbbh;


import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.SalePayDef;

//重百欠款
public class Cbbh_PaymentCredit extends Payment {
	
	public double calcPayAlreadyMoney()
	{
		double je = 0;
		for (int i = 0; i < saleBS.salePayment.size(); i++)
		{
			SalePayDef sp = (SalePayDef) saleBS.salePayment.elementAt(i);
			if (!sp.paycode.equals(paymode.code)) continue;

			je += ManipulatePrecision.doubleConvert(sp.je);
		}

		return je;
	}
	
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
			
			StringBuffer je = new StringBuffer(0);
			if(!((Cbbh_NetService)NetService.getDefault()).getCredit(saleBS.saleHead.cczz_zktmp.trim(),je))
			{
				new MessageBox("调用SAP-WEBSERVICE查询[" +saleBS.saleHead.cczz_zktmp.trim() + "]的欠款额度出现错误,暂不能使用此付款方式！");
				return false;
			}
			
			double credit = Double.valueOf(je.toString()).doubleValue();
			//减去已经付款金额
			double credit_real = credit - calcPayAlreadyMoney();
			if(Double.compare(credit_real, Double.valueOf(money).doubleValue()) > 0)
			{
				return super.checkMoneyValid(money, ye);
			}
			else
			{
				new MessageBox("使用此付款方式支付的金额总共不能超过["+String.valueOf(credit)+"]");
				return false;
			}
		}
	}
}
