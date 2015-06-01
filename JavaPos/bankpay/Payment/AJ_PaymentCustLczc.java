package bankpay.Payment;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentCust;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;


//爱家对象
public class AJ_PaymentCustLczc extends PaymentCust
{
    public AJ_PaymentCustLczc()
    {
    }

    public AJ_PaymentCustLczc(PayModeDef mode, SaleBS sale)
    {
    	initPayment(mode, sale);
    }

    public AJ_PaymentCustLczc(SalePayDef pay, SaleHeadDef head)
    {
    	initPayment(pay, head);
    }

    protected boolean needFindAccount()
    {
    	return false;
    }
    
    public boolean autoCreateAccount()
    {
    	setCustomerInfo();
    	
    	// 查询是否已刷此卡
    	if (saleBS.existPayment(paymode.code,mzkret.cardno) >= 0 )
    	{
        	boolean ret = false;
        	if (new MessageBox("此卡已进行付款,你要取消原付款重新输入吗？",null,true).verify() == GlobalVar.Key1)
        	{
        		ret = true;
        		int n = -1;
        		do {
        			n = saleBS.existPayment(paymode.code, mzkret.cardno);
        			if (n >= 0)
        			{
        				if (!saleBS.deleteSalePay(n))
        				{
        					ret = false;
        					break;
        				}
        			}
        		} while(n >= 0);

        		// 重新刷新付款余额及已付款列表
        		saleBS.salePayEvent.refreshSalePayment();
        	}
        	if (!ret)
        	{
        		new MessageBox("此卡已经付款，请先删除原付款");
        		return ret;
        	}
    	}
    	return true;
    }

    // 用会员卡信息赋给面值卡结构
    public boolean setCustomerInfo()
    {
    	
    	if (saleBS.curCustomer != null)
    	{
	    	setRequestDataByFind("",saleBS.curCustomer.code,"");
	    	
	        mzkret.cardno   = saleBS.curCustomer.code;
	        mzkret.cardname = saleBS.curCustomer.name;
	        mzkret.ye       = saleBS.curCustomer.value1;	//零钞余额
	        mzkret.money    = saleBS.curCustomer.value2;	//最大零钞余额
    	}
    	
        return true;
    }
    
    protected String getDisplayAccountInfo()
    {
        return "会员卡号";
    }

    protected String getDisplayStatusInfo()
	{
		return "当前账户零钞余额是:" + ManipulatePrecision.doubleToString(mzkret.ye);
	} 
    
    public boolean createLczcSalePay(double zl)
    {
    	// 初始设置
    	setCustomerInfo();
    	
    	// 创建SalePay
	    if (!createSalePayObject(String.valueOf((zl)))) return false;
	    
	    // 记录账号信息到SalePay
	    if (!saveFindMzkResultToSalePay()) return false;
	    
    	// 零钞转存付款方式金额记负数
        salepay.ybje *= -1;
        salepay.je *= -1;
        
        // 代表零钞转存
        salepay.memo = "3";
        
	    return true;
    }   
}
