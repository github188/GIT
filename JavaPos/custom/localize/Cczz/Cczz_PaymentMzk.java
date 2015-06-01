package custom.localize.Cczz;

import org.eclipse.swt.events.KeyEvent;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzkEvent;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bcrm.Bcrm_PaymentMzk;

public class Cczz_PaymentMzk extends Bcrm_PaymentMzk{
	public Cczz_PaymentMzk()
	{
		super();
	}
	
	public Cczz_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		super(mode,sale);
	}
	
	public Cczz_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		super(pay,head);
	}
	
	//保存交易数据进行交易
	protected boolean setRequestDataByAccount()
    {
		if (super.setRequestDataByAccount())
		{
			if (salepay.str1.indexOf("CCZZ") == 0)
			{
				String[] rows = salepay.str1.split(";");
				mzkreq.je = Convert.toDouble(rows[2]);
			}
			return true;
		}
		
		return false;
    }
	
	// 99卡不显示卡余额
	public void setPwdAndYe(PaymentMzkEvent event, KeyEvent e)
	{
    	if (isPasswdInput())
    	{
    		// 显示密码
    		event.yeTips.setText(getPasswdLabel());        		
    		event.yeTxt.setVisible(false);
    		event.pwdTxt.setVisible(true);
    		if (!"99".equals(mzkret.func.substring(1, 3)))
    		{
    			event.yeTxt.setText(ManipulatePrecision.doubleToString(getAccountYe()));	
    		}
        	if (e != null) e.data = "focus";
        	event.pwdTxt.setFocus();
        	event.pwdTxt.selectAll();
    	}
    	else
    	{
            // 显示余额
    		event.yeTips.setText("账户余额");
    		event.yeTxt.setVisible(true);
    		event.pwdTxt.setVisible(false);
    		if ("99".equals(mzkret.func.substring(1, 3)))
    		{
    			event.yeTxt.setVisible(false);
    		}
    		else
    		{
    			event.yeTxt.setText(ManipulatePrecision.doubleToString(getAccountYe()));	
    		}

            // 输入金额
            if (e != null) e.data = "focus";
            event.moneyTxt.setFocus();
            event.moneyTxt.selectAll();
    	}
	}
	
	// 99卡不提示卡余额
	public void showAccountYeMsg()
	{
		if ("99".equals(mzkret.func.substring(1, 3)))
		{
			if(!messDisplay) return;
			
		    StringBuffer info = new StringBuffer();
		    
		    String text = "付";
		    if (checkMzkIsBackMoney())
		    {
		    	text = "退";
		    }	   
		    info.append("本次"+text+"款额: " + Convert.appendStringSize("",ManipulatePrecision.doubleToString(salepay.je),0,12,12,1) + "\n");	    
		    new MessageBox(info.toString());
		}
		else
		{
			super.showAccountYeMsg();
		}
	}
	
	// 记录卡类型，打印时判断是否打印卡余额
	public boolean createSalePay(String money)
	{
		if (super.createSalePay(money))
		{
			if (mzkret.func.length() > 2)
			{
				salepay.str4 = mzkret.func.substring(1, 3);
			}
			return true;
		}
		else
		{
			return false;
		}
	}
}
