package custom.localize.Jnyz;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentCustJfSale;

public class Jnyz_PaymentCustJfSale extends PaymentCustJfSale {

	protected boolean needFindAccount()
	{
			if (GlobalInfo.sysPara.isReMSR == 'Y')
				return true;
			if (saleBS.curCustomer != null)
				return false;
			else
				return true;
		
	}
	
	
	 // 将会员卡信息赋给面值卡结构
    public boolean setCustomerInfo()
    {
//    	if (!super.setCustomerInfo()) return false;
    	
    	if (saleBS.curCustomer != null)
		{
			setRequestDataByFind("", saleBS.curCustomer.code, "");

			mzkret.cardno = saleBS.curCustomer.code;
			mzkret.cardname = saleBS.curCustomer.name;
			mzkret.ye = saleBS.curCustomer.value1; // 零钞余额
			mzkret.money = saleBS.curCustomer.value2; // 最大零钞余额

			mzkret.memo = saleBS.curCustomer.memo;
			mzkret.value1 = saleBS.curCustomer.valuememo;
	    	//
	        mzkret.ye = calcJfeSaleMoney();
	        mzkret.money = 999999;
	        
	        return true;
		}
    	
        if (mzkret.ye <= 0) return false;
		
        return false;
    }	
	
//	计算积分转换成金额
	public double calcJfeSaleMoney()
	{
			//后台计算折现金额
		if(saleBS.curCustomer.value3 >0)
			return saleBS.curCustomer.value3;
		else
			return 0;
		
		/*if (saleBS.curCustomer	== null)
		{
			new MessageBox("当前未刷会员卡\n无法使用" + paymode.name + "付款方式!");
			return 0;
		}
			
		if (saleBS.curCustomer.valuememo <= 0)
		{
			new MessageBox("此会员卡没有积分，不能用此付款方式付款");
			return 0;
		}
		
		String[] num = saleBS.curCustomer.memo.split(",");
		try
		{
			if (num.length != 2)
			{
				new MessageBox("未定义折现基数或折现标准，不能用此付款方式付款");
				return 0;
			}
			
			double num1 = Double.parseDouble(num[0]);
			double num2 = Double.parseDouble(num[1]);
			
			if (num1 <=0 || num2 <= 0)
			{
				new MessageBox("未定义折现基数或折现标准，不能用此付款方式付款"+saleBS.curCustomer.memo);
				return 0;
			}
			double sum = saleBS.curCustomer.valuememo/num1;
			
			double num3 = ManipulatePrecision.doubleConvert(sum,2,0);
			if (num3 < 1)
			{
				new MessageBox("卡积分不足以折现，不能用此付款方式付款");
				return 0;
			}
			
			return ManipulatePrecision.mul(num3, num2);
		}
		catch(Exception er)
		{
			new MessageBox("折现基数或折现标准定义错误,或者，不能用此付款方式付款");
			return 0;
		}*/
	}
	
	
	public double getAccountAllowPay()
	{
		if (this.allowpayje >= 0)
			return Math.min(this.allowpayje, mzkret.ye);
		else
			return mzkret.ye;
	}

	
}
