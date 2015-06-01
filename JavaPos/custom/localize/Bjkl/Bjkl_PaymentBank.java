package custom.localize.Bjkl;

import com.efuture.commonKit.PosClock;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Bjkl_PaymentBank extends PaymentBank
{
	public Bjkl_PaymentBank()
	{
	}

	public Bjkl_PaymentBank(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Bjkl_PaymentBank(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public SalePayDef inputPay(String money)
	{
		try
		{
			PosClock.setKeepActive(false);
		

			if (money != null && !money.trim().equals(""))
			{
				inputMoney = money;
			}
				
			// 打开金卡输入窗口
			CreatePayment.getDefault().getPaymentBankForm().open(this, PaymentBank.XYKXF);

			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			PosClock.setKeepActive(true);
		}

		return null;
	}
	
	public boolean cancelPay()
	{
		if (!super.cancelPay())
			return false;

		try
		{
			for (int i = 0; i < saleBS.salePayment.size(); i++)
			{
				SalePayDef spd = (SalePayDef) saleBS.salePayment.get(i);
				if (spd.flag == '4' && spd.str4 != null && spd.str4.equals(salepay.rowno + ""))
				{
					saleBS.delSalePayObject(i);
					saleBS.calcPayBalance();
					break;
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return true;
	}
}
