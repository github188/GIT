package custom.localize.Bjys;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentSerialMzk;
import com.efuture.javaPos.Payment.PaymentSerialMzkForm;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;


public class Bjys_PaymentJlj extends PaymentSerialMzk
{
	public Bjys_PaymentJlj()
	{
		super();
	}
	
	public Bjys_PaymentJlj(PayModeDef mode,SaleBS sale)
	{
		initPayment(mode,sale);
	}

	public Bjys_PaymentJlj(SalePayDef pay,SaleHeadDef head)
	{
		initPayment(pay,head);
	}
	
	public SalePayDef inputPay(String money)
	{
		try
		{
			if (SellType.ISBACK(saleBS.saletype)&&GlobalInfo.sysPara.thmzk != 'Y')
			{
				new MessageBox("退货时不允许使用面值卡");
				return null;
			}
			
			// 先检查是否有冲正未发送
			if (!sendAccountCz()) return null;
			
			// 打开明细输入窗口
			new PaymentSerialMzkForm().open(this,saleBS,false);
			
			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
        }
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return null;
	}
	
	protected boolean checkMoneyValid(String money,double ye)
	{	
		if ((Convert.toDouble(money)%100)!= 0)
		{
			new MessageBox(paymode.name + "付款金额必须是100元的倍数,请关闭窗口后重新输入!");
			return false;
		}
		
		if (Convert.toDouble(money) - ye >= 100)
		{
			new MessageBox(paymode.name + "溢余金额不允许超过100元,请关闭窗口后重新输入!");
			return false;
		}
		
		return super.checkMoneyValid(money, ye);
	}
}
