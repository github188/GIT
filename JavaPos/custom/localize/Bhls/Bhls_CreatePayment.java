package custom.localize.Bhls;

import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentFjk;
import com.efuture.javaPos.Payment.PaymentFjkPaper;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Bhls_CreatePayment extends CreatePayment 
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
    	
		return new Bhls_PaymentMzk();
	}
	
	//是否允许直接在金额款输入付款金额
	public boolean allowQuickInputMoney(PayModeDef pay)
	{
		if (CreatePayment.getDefault().isPaymentFjk(pay.code))
		{
			return false;
		}
		else
		{
			return super.allowQuickInputMoney(pay);
		}
	}
	
    public PaymentFjk getPaymentFjk(PayModeDef mode,SaleBS sale)
    {
    	return new PaymentFjk(mode,sale);
    }
    
    public PaymentFjk getPaymentFjk(SalePayDef pay, SaleHeadDef head)
    {
    	return new PaymentFjk(pay,head);
    }

	public Payment createLocalizePayment(boolean flag,PayModeDef mode,SaleBS sale,SalePayDef pay,SaleHeadDef head)
    {	
		Payment payment = createCustomPayment(flag, mode, sale, pay, head);
		if (payment != null)
		{
			return payment;
		}
		else if (CreatePayment.getDefault().isPaymentFjk(mode.code))
		{
	    	if (GlobalInfo.sysPara.fjkyetype != null && !GlobalInfo.sysPara.fjkyetype.equals(""))
	    	{
	    		String s[] = GlobalInfo.sysPara.fjkyetype.split(";");
	    		for (int i=0;i<s.length;i++)
	    		{
	    			String p[] = s[i].split("=");
	    			if (mode.code.trim().equals(p[0].trim()) && p.length >= 2)
	    			{
	    				String t[] = p[1].split("\\|");
	    				if (t[0].trim().equals("Z"))
	    				{
							PaymentFjkPaper payobj = null;
							if (flag)
							{
								payobj = new PaymentFjkPaper(mode,sale);
							}
							else
							{
								payobj = new PaymentFjkPaper(pay,head);
							}
							if (t.length >= 2) payobj.setAccountYeType(t[1].trim());
							return payobj;
	    				}
	    				else if (t[0].trim().equals("D"))
	    				{
	    					PaymentFjk payobj = null;
							if (flag)
							{
								payobj = getPaymentFjk(mode,sale);
							}
							else
							{
								payobj = getPaymentFjk(pay,head);
							}
							if (t.length >= 2) payobj.setAccountYeType(t[1].trim());
							return payobj;
	    				}
	    			}
	    		}
	    	}
	    	
	    	// 缺省创建PaymentFjk对象
			if (flag)
			{
				return new PaymentFjk(mode,sale);
			}
			else
			{
				return new PaymentFjk(pay,head);
			}
		}
		else if (mode.type == '4')
		{
			if (flag)
			{
				return new Bhls_PaymentMzk(mode,sale);
			}
			else
			{
				return new Bhls_PaymentMzk(pay,head);
			}
		}
		else
		{
			return super.createLocalizePayment(flag, mode, sale, pay, head);
		}
    }
}
