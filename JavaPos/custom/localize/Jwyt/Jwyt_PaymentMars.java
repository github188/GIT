package custom.localize.Jwyt;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Jwyt_PaymentMars extends Payment
{
	public Jwyt_PaymentMars()
	{

	}

	public Jwyt_PaymentMars(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Jwyt_PaymentMars(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public SalePayDef inputPay(String money)
	{
		try
		{

			if (Jwyt_MarsModule.getDefault().getMarSaleRet() != null && !Jwyt_MarsModule.getDefault().getMarSaleRet().isMzkCash())
			{
				new MessageBox("当前券类型不允许使用该付款方式");
				return null;
			}

			// 打开明细输入窗口
			new Jwyt_MarsPayForm().open(this, saleBS);

			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;
	}

	public boolean backIsExistSameCoupon(String asisstant, String tcode, double money)
	{
		if (SellType.ISBACK(saleBS.saletype))
		{
			for (int i = 0; i < saleBS.salePayment.size(); i++)
			{
				SalePayDef spd = (SalePayDef) saleBS.salePayment.get(i);
				if (spd.paycode.equals("08") && spd.payno.equals(asisstant) && spd.idno.equals(tcode) && spd.ybje == money)
					return false;
			}
		}
		return true;
	}

	protected boolean checkMoneyValid(String money, double ye)
	{
		try
		{
			if (saleBS.isSpecifyTicketBack() && GlobalInfo.sysPara.isctrlthpay == 'Y')
			{
				if (saleBS.backPayment != null && saleBS.backPayment.size() > 0 && !money.equals(""))
				{
					for (int i = 0; i < saleBS.backPayment.size(); i++)
					{
						SalePayDef tmpPay = (SalePayDef) saleBS.backPayment.get(i);
						if (!tmpPay.paycode.equals("08"))
							continue;

						if (Jwyt_MarsModule.getDefault().getMarSaleRet().assistcode.equals(tmpPay.payno) && Jwyt_MarsModule.getDefault().getMarSaleRet().tcodeid.equals(tmpPay.idno) && ManipulatePrecision.doubleConvert(Double.parseDouble(money)) == ManipulatePrecision.doubleConvert(tmpPay.je)) { return super.checkMoneyValid(money, ye); }
					}
				}
				new MessageBox("请核对当前[短信码/金额]是否与原始小票付款一致");
				return false;
			}
			return super.checkMoneyValid(money, ye);
		}
		catch (Exception ex)
		{
			new MessageBox(ex.getMessage());
			return false;
		}
	}

	public int existPayment(String code, String account)
	{
		SalePayDef saledef = null;
		for (int i = 0; i < saleBS.salePayment.size(); i++)
		{
			saledef = (SalePayDef) saleBS.salePayment.elementAt(i);
			
			if (saledef.paycode.equals(code) && saledef.payno.trim().equals(account))
				return i;
		}

		return -1;
	}

	public boolean createSalePay(String cardno, String money)
	{
		
		if (Convert.toDouble(Jwyt_MarsModule.getDefault().getMarSaleRet().balancemoney) <= 0)
		{
			new MessageBox("券中余额不足!");
			return false;
		}

		// 判断退货时是否退了多张相同券
		if (!backIsExistSameCoupon(Jwyt_MarsModule.getDefault().getMarSaleRet().assistcode, Jwyt_MarsModule.getDefault().getMarSaleRet().tcodeid, Convert.toDouble(money)))
			return false;

		if (super.createSalePay(money))
		{
			salepay.payno = Jwyt_MarsModule.getDefault().getMarSaleRet().assistcode;
			salepay.idno = Jwyt_MarsModule.getDefault().getMarSaleRet().tcodeid;
			salepay.batch = Jwyt_MarsModule.getDefault().getMarSaleRet().transseq;
			salepay.memo = Jwyt_MarsModule.getDefault().getMarSaleRet().couponid + Jwyt_MarsModule.getDefault().getMarSaleRet().couponname + "," + Jwyt_MarsModule.getDefault().getMarSaleRet().coupontype;
			salepay.kye = Convert.toDouble(Jwyt_MarsModule.getDefault().getMarSaleRet().balancemoney);
			salepay.str2 = "EWM";

			return true;
		}

		return false;
	}
}
