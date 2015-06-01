package custom.localize.Zcbh;

import bankpay.Payment.Shop_PaymentMzk;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Zcbh_PaymentMzk extends Shop_PaymentMzk
{
	public Zcbh_PaymentMzk()
	{
		super();
	}

	public Zcbh_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Zcbh_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}
	
	public boolean compareInputPasswd(String pwd)
	{
		if (!this.pswInputValid())
		{
			return false;
		}
		if(mzkret.cardno.trim().length() >= 19)  //老卡卡号19位
		{
			if (!pwd.equals(mzkret.cardpwd))
			{
				new MessageBox(Language.apply("输入的验证码不正确!"));

				return false;
			}
		}
		else
		{
			mzkret.cardno = mzkret.cardno.trim();
			if (!pwd.equals(mzkret.cardno.substring(mzkret.cardno.length()-9, mzkret.cardno.length())))
			{
				new MessageBox(Language.apply("输入的验证码不正确!"));

				return false;
			}
		}

		//
		mzkreq.passwd = pwd;

		return true;
	}
	
	
	
	public boolean isPasswdInput()
	{
		if (mzkret.ispw == 'Y' && !GlobalInfo.sysPara.cardpasswd.equals("Y"))
		{
			if(mzkret.cardno.trim().length() == 8)   //8位为会员卡一卡通 有储值功能 付款时要求不验证密码
			{
				return false;
			}
			return true;
		}
		else
		{
			return false;
		}
	}
}