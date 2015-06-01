package custom.localize.Bcrm;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentCustJfSale;
import com.efuture.javaPos.Payment.PaymentCustLczc;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bhls.Bhls_CreatePayment;

public class Bcrm_CreatePayment extends Bhls_CreatePayment
{
	public PaymentMzk getPaymentMzk()
	{
    	// 检索自定义付款对象列表中是否有面值卡付款对象,如果有则以该付款对象为面值卡查询的对象
    	String[] pay = getCustomPaymentDefine("PaymentMzk");
    	if (pay != null)
    	{
			try
			{
				Class cl = payClassName(pay[0]);
				if (cl != null) return (PaymentMzk)cl.newInstance();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
    	}
    	
		return new Bcrm_PaymentMzk();
	}
	
	// 是否允许直接在金额款输入付款金额
	public boolean allowQuickInputMoney(PayModeDef pay)
	{
		if (pay.code.trim().equals("0111") || pay.code.trim().equals("0508"))
		{
			return false;
		}
		else
		{
			return super.allowQuickInputMoney(pay);
		}
	}
	
    
    public boolean isPaymentJfxf(SalePayDef sp)
    {	
    	String[] pay = getCustomPaymentDefine("PaymentCustJfSale");
    	if (pay != null)
    	{
    		for (int i=1;i<pay.length;i++)
    		{
    			if (sp.paycode.equals(pay[i]) && sp.memo.trim().equals("1")) return true;
    		}
    	}
    	
    	if (sp.paycode.equals("0508") && sp.memo.trim().equals("1")) return true;
        else return false;
    }
	
	public Payment createLocalizePayment(boolean flag,PayModeDef mode,SaleBS sale,SalePayDef pay,SaleHeadDef head)
    {	
		Payment payment = createCustomPayment(flag, mode, sale, pay, head);
		if (payment != null)
		{
			return payment;
		}
		else if (mode.type == '4')
		{
			if (flag)
			{
				return new Bcrm_PaymentMzk(mode,sale);
			}
			else
			{
				return new Bcrm_PaymentMzk(pay,head);
			}
		}
		else if (mode.code.trim().equals("0111"))
		{
			if (flag)
        	{
        		return new PaymentCustLczc(mode,sale);
        	}
        	else
        	{
        		return new PaymentCustLczc(pay,head);
        	}
		}
		else if (mode.code.trim().equals("0508"))
		{
			if (flag)
        	{
        		return new PaymentCustJfSale(mode,sale);
        	}
        	else
        	{
        		return new PaymentCustJfSale(pay,head);
        	}
		}
		else
		{
			return super.createLocalizePayment(flag, mode, sale, pay, head);
		}
    }
}
