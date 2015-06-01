package custom.localize.Szxw;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bhls.Bhls_PaymentMzk;

public class Szxw_PaymentMzk extends Bhls_PaymentMzk
{
	public Szxw_PaymentMzk()
	{
		super();
	}

	public Szxw_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		super(mode, sale);
	}

	public Szxw_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		super(pay, head);
	}

	protected void saveAccountMzkResultToSalePay()
	{
		super.saveAccountMzkResultToSalePay();

		// 定金单付款方式,memo记录定金单余额生成的新的定金单和金额,小票打印新的定金单
		if (salepay.paycode.equals("0411"))
		{
			salepay.memo = mzkret.memo; // memo = 新定金单号,新定金单金额
			salepay.str4 = mzkret.str1; // str1 = 原付款代码,原卡号，柜组
		}
	}

	protected boolean saveFindMzkResultToSalePay()
	{
		if (!super.saveFindMzkResultToSalePay()) return false;

		if (salepay.paycode.equals("0400") && !mzkret.cardpwd.equals("A"))
		{
			salepay.paycode = "0401";
			salepay.payname = DataService.getDefault().searchPayMode("0401").name;
		}

		if (salepay.paycode.equals("0401") && !mzkret.cardpwd.equals("B"))
		{
			salepay.paycode = "0400";
			salepay.payname = DataService.getDefault().searchPayMode("0400").name;
		}

		return true;
	}

	public boolean checkMzkMoneyValid()
	{
		if (super.checkMzkMoneyValid())
		{
			if (salepay.paycode.equals("0400") || salepay.paycode.equals("0401"))
			{
				if (!mzkret.cardpwd.equals("A") && !mzkret.cardpwd.equals("B"))
				{
					new MessageBox("卡类型未定义");
					return false;
				}
			}

			return true;
		}

		return false;
	}
	
	public double getAccountYe()
	{
		return mzkret.ye;
	}
	
	public double getAccountAllowPay()
	{
		if (SellType.ISBACK(mzkreq.invdjlb) && !saleBS.isRefundStatus())
		{
			return 999999.99;
		}
		
		if (this.allowpayje >= 0) return Math.min(this.allowpayje,mzkret.ye);
		else return mzkret.ye;
	}

	public int getAccountInputMode()
	{
		//定金单单号手输
		if (paymode != null && paymode.code.equals("0411")) { return TextBox.IntegerInput; }
		return super.getAccountInputMode();
	}
}
