package custom.localize.Sfks;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentFjk;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import bankpay.Bank.NjysST_PaymentBankFunc;
import custom.localize.Bhls.Bhls_CreatePayment;


public class Sfks_CreatePayment extends Bhls_CreatePayment 
{
	public PaymentBankFunc getPaymentBankFunc()
	{
		PaymentBankFunc p = getPaymentBankFuncByMenu();
		
/*		新银联接口不打印签购单
		// 设置为不及时打印签购单,签购单放到小票打印完成以后最后打印
		p.setOnceXYKPrintDoc(false);
*/		
		return p;
	}
	
	public PaymentBankFunc getPaymentBankFuncByMenu()
	{
		//PaymentBankFunc p = new Shylnew_PaymentBankFunc();
	    PaymentBankFunc p = new NjysST_PaymentBankFunc();
	    
		// 设置为弹出银联错误消息的模式
		p.setErrorMsgShowMode(true);
		
		// 设置为不替换银行名称模式
		p.setReplaceBankNameMode(false);
		
		return p;
	}
	
	//是否允许直接在金额款输入付款金额
	public boolean allowQuickInputMoney(PayModeDef pay)
	{
		if (pay.code.trim().equals("0501"))
		{
			return true;
		}
		else
		{
			return super.allowQuickInputMoney(pay);
		}
	}
	
    public PaymentFjk getPaymentFjk()
    {
    	return new Sfks_PaymentFjk();
    }
    
	public Payment createLocalizePayment(boolean flag,PayModeDef mode,SaleBS sale,SalePayDef pay,SaleHeadDef head)
    {	
		if (mode.code.equals("0501"))
		{
			if (flag)
			{
				return new Sfks_PaymentFwq(mode,sale);
			}
			else
			{
				return new Sfks_PaymentFwq(pay,head);
			}				
		}
		else if (mode.type == '5' && mode.code.trim().equals("0500"))
		{
			if (flag)
			{
				return new Sfks_PaymentFjk(mode,sale);
			}
			else
			{
				return new Sfks_PaymentFjk(pay,head);
			}
		}
		else
		{
			return super.createLocalizePayment(flag, mode, sale, pay, head);
		}
    }
}
