package custom.localize.Bjys;

import bankpay.Payment.AlipayO_Payment;

import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentFjk;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Payment.Bank.Bjjl_PaymentBankFunc;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;


public class Bjys_CreatePayment extends CreatePayment
{
    // 是否允许直接在金额款输入付款金额
    public boolean allowQuickInputMoney(PayModeDef pay)
    {
		if (pay.type == '5')
		{
			return false;
		}
		else
		{
			return super.allowQuickInputMoney(pay);
		}
    }
	
    public PaymentFjk getPaymentFjk()
    {
        return new Bjys_PaymentFjk();
    }
    
	//flag:true代表以销售窗口创建对象;false红冲
    public Payment createPaymentAll(boolean flag,PayModeDef mode,SaleBS sale,SalePayDef pay,SaleHeadDef head)
    {
    	
    	// 客户化付款对象
    	Payment p = createCustomPayment(flag, mode, sale, pay, head);
    	if (p != null)
    	{
    		return p;
    	}	
    	
    	// 允许直接输入付款金额
        if (allowQuickInputMoney(mode))
        {   	
        	if (mode.code.equals("07"))
        	{
	        	if (flag)
	        	{
	        		return new Bjys_PaymentSrj(mode,sale);
	        	}
	        	else
	        	{
	        		return new Bjys_PaymentSrj(pay,head);
	        	}
        	}
        	if (flag)
        	{
        		return new Payment(mode,sale);
        	}
        	else
        	{
        		return new Payment(pay,head);
        	}
        }
        else
        {
        	// 金卡工程,生成相应的金卡工程付款对象
        	if (mode.isbank == 'Y')
        	{
            	if (flag)
            	{
            		return new Bjys_PaymentBank(mode,sale);
            	}
            	else
            	{
            		return new Bjys_PaymentBank(pay,head);
            	}
        	}
        	else
        	{
        		PaymentFjk payobj = null;
        		
        		//返券卡
        		if (mode.type == '5' && mode.code.trim().equals("0500"))
        		{
        			if (flag)
        			{
        				payobj =  new Bjys_PaymentFjk(mode,sale);
        			}
        			else
        			{
        				payobj = new Bjys_PaymentFjk(pay,head);
        			}
        			
        			if (GlobalInfo.sysPara.fjkyetype != null && !GlobalInfo.sysPara.fjkyetype.equals(""))
        	    	{
        				payobj.setAccountYeType(GlobalInfo.sysPara.fjkyetype);
        	    	}
        			
        			return payobj;
        		}
	        	//先根据代码判断
        		if (mode.code.equals("05"))
        		{
                	if (flag)
                	{
                		return new Bjys_PaymentJlj(mode,sale);
                	}
                	else 
                	{
                		return new Bjys_PaymentJlj(pay,head);
                	}
        		}
        		
        		// 根据不同的付款方式,创建相应的付款对象
	        	switch(mode.type)
	        	{
	        		case '4':	// 面值卡付款
	        		{
	                	if (flag)
	                	{
	                		return new PaymentMzk(mode,sale);
	                	}
	                	else
	                	{
	                		return new PaymentMzk(pay,head);
	                	}
	        		}
	        		case '2':	// 支票付款方式
	        		{
	        			if (flag)
	                	{
	                		return new Bjys_PaymentZp(mode,sale);
	                	}
	                	else
	                	{
	                		return new Bjys_PaymentZp(pay,head);
	                	}
	        		}
	        		default:	// 其他付款方式
	        		{
	                	if (flag)
	                	{
	                		return new Bjys_PaymentDetail(mode,sale); 
	                	}
	                	else
	                	{
	                		return new Bjys_PaymentDetail(pay,head);
	                	}
	        		}
	        	}
        	}
        }
    }
    
    public PaymentBankFunc getPaymentBankFunc()
	{
        PaymentBankFunc bank = getConfigBankFunc();

        if (bank == null)
        {
        	bank =  new Bjjl_PaymentBankFunc();
        }
        
        // 不通过菜单选择银联交易类型
        bank.setBankTypeByMenu(false);
        
		//	设置为弹出银联错误消息的模式
		bank.setErrorMsgShowMode(true);
		
		//设置为不及时打印签购单,签购单放到小票打印完成以后最后打印
    	if (GlobalInfo.syjDef.issryyy != 'Y' && GlobalInfo.syjDef.printfs == '1')
        { 
    		bank.setOnceXYKPrintDoc(false);
        }

    	return bank;
	}
    
	public PaymentBankFunc getPaymentBankFuncByMenu()
	{
		PaymentBankFunc bank = super.getPaymentBankFuncByMenu();
		
		// 设置为弹出银联错误消息的模式
        bank.setErrorMsgShowMode(true);
		
        bank.setOnceXYKPrintDoc(true);
		
		return bank;
	}
	

}
