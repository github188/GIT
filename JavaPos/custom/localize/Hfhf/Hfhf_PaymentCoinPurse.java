package custom.localize.Hfhf;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Hfhf.Hfhf_CoinPurse.VipCoinPayResult;
import custom.localize.Hfhf.Hfhf_CoinPurse.VipCoinPurse;

public class Hfhf_PaymentCoinPurse extends Hfhf_PaymentScore
{
	private VipCoinPurse coinPurse;

	public Hfhf_PaymentCoinPurse()
	{

	}

	public Hfhf_PaymentCoinPurse(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Hfhf_PaymentCoinPurse(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public SalePayDef inputPay(String money)
	{
		try
		{
			if (!GlobalInfo.isOnline)
			{
				new MessageBox("断网状态下无法使用此功能!");
				return null;
			}

			if (saleBS.curCustomer == null || !saleBS.curCustomer.valstr3.equals("szd"))
			{
				new MessageBox("未刷集团会员卡!");
				return null;
			}

			if (saleBS.curCustomer.type.equals("blank"))
			{
				new MessageBox("该卡为白卡,无法使用零钱包!");
				return null;
			}

			// 打开明细输入窗口
			new Hfhf_ScoreForm().open(this, saleBS);

			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;
	}

	public boolean createSalePay(String cardno, String money)
	{
		double amount = Convert.toDouble(money);
		VipCoinPayResult payRet = null;

		if (!SellType.ISBACK(saleBS.saletype))
		{
			if (amount > coinPurse.Balance)
			{
				new MessageBox("消费金额大于余额!");
				return false;
			}

			payRet = Hfhf_CrmModule.getDefault().lockChangePocket(saleBS.saletype, cardno, amount * -1);
			if (payRet == null)
				return false;

			if (!Hfhf_CrmModule.getDefault().confirmChangePocket(payRet.ReferNo))
				return false;
		}
		else
		{
			payRet = Hfhf_CrmModule.getDefault().rechangeChangePocket(saleBS.saletype, saleBS.curCustomer.code, amount);

			if (payRet == null)
				return false;
		}

		if (super.createSalePay(money))
		{
			salepay.payno = cardno;
			salepay.kye = payRet.Balance;
			salepay.batch = payRet.ReferNo;

			return true;
		}

		Hfhf_CrmModule.getDefault().cancelChangePocket(payRet.ReferNo);
		return false;
	}

	public static boolean isPaymentLczc(SalePayDef sp)
	{
		if (sp.paycode.equals("0404") && sp.memo.trim().equals("3"))
			return true;

		return false;
	}

	public boolean createLczcSalePay(double zl)
	{
		VipCoinPayResult payRet = Hfhf_CrmModule.getDefault().rechangeChangePocket(saleBS.saletype, saleBS.curCustomer.code, zl);

		if (payRet == null)
			return false;

		// 创建SalePay
		if (!createSalePayObject(String.valueOf((zl))))
			return false;

		salepay.payno = saleBS.curCustomer.valstr3 + saleBS.curCustomer.code;
		salepay.kye = payRet.Balance;
		salepay.batch = payRet.ReferNo;

		// 零钞转存付款方式金额记负数
		salepay.ybje *= -1;
		salepay.je *= -1;

		// 代表零钞转存
		salepay.memo = "3";

		return true;
	}

	public boolean cancelPay()
	{
		if (!Hfhf_CrmModule.getDefault().cancelChangePocket(salepay.batch))
			return false;

		return true;
	}

	public boolean getCrmCardData(String cardno)
	{
		ProgressBox prg = new ProgressBox();

		try
		{
			prg.setText("正在查询零钱包余额,请稍候...");
			coinPurse = Hfhf_CrmModule.getDefault().queryChangePocket(cardno, true);
			if (coinPurse == null)
				return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			prg.close();
			prg = null;
		}
		return true;
	}

	public double getYeInfo()
	{
		if (coinPurse != null)
			return coinPurse.Balance;

		return 0.00;
	}

	public double getValidMoney()
	{
		return Math.min(saleBS.calcPayBalance(), coinPurse.Balance);
	}

	public String getStatusInfo()
	{
		// if (coinPurse != null)
		// return "开户时间:" + coinPurse.OpenDate + "\n" + "余额更新时间" +
		// coinPurse.UpdateDate;

		return "";
	}
}
