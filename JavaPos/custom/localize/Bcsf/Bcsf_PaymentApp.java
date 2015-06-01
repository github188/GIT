package custom.localize.Bcsf;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Bcsf_PaymentApp extends PaymentMzk
{
	private double curje = 0.0;

	public Bcsf_PaymentApp()
	{
	}

	public Bcsf_PaymentApp(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Bcsf_PaymentApp(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	protected boolean getPasswdBeforeFindMzk(StringBuffer passwd)
	{
		TextBox txt = new TextBox();
		if (!txt.open(Language.apply("请输入券验证码"), "PASSWORD", Language.apply("需要先输入券验证码才能查询券资料"), passwd, 0, 0, false, TextBox.AllInput))
			return false;

		return true;
	}

	protected String getDisplayStatusInfo()
	{
		allowpayje = ManipulatePrecision.doubleConvert(calcPayRuleMaxMoney() / paymode.hl);
		allowpayje = Math.min(allowpayje, mzkret.ye);

		return "当前可使用券金额:" + allowpayje;
	}

	public double calcPayRuleMaxMoney()
	{
		double hjje = 0;
		try
		{
			for (int i = 0; i < saleBS.saleGoods.size(); i++)
			{
				GoodsDef gd = (GoodsDef) saleBS.goodsAssistant.elementAt(i);
				SaleGoodsDef sg = (SaleGoodsDef) saleBS.saleGoods.elementAt(i);

				if (gd.couponrule != null && gd.couponrule.equals("Y"))
					hjje = hjje + (sg.hjje - saleBS.getZZK(sg));
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}
		curje = Math.min(mzkret.ye,ManipulatePrecision.doubleConvert(hjje - appTotalPay()));
		return curje;
	}

	private double appTotalPay()
	{
		double total = 0.0;

		for (int i = 0; i < saleBS.salePayment.size(); i++)
		{
			SalePayDef spd = (SalePayDef) saleBS.salePayment.get(i);
			if (spd.str6.equals("App"))
				total = total + (spd.je - spd.num1);
		}
		return total;
	}

	public boolean createSalePay(String money)
	{
		if(curje == 0)
		{
			new MessageBox("当前无可用券商品或已享受券优惠!");
			return false;
		} 
		
		if (curje != Convert.toDouble(money))
		{
			new MessageBox("不允许修改金额!");
			return false;
		}

		if (!existSameCard())
			return false;

		if (super.createSalePay(String.valueOf(mzkret.ye)))
		{
			salepay.str6 = "App";
			return true;
		}

		return false;
	}

	public boolean checkMzkIsBackMoney()
	{
		return false;
	}
	
	public void showAccountYeMsg()
	{
		
	}
	private boolean existSameCard()
	{
		for (int i = 0; i < saleBS.salePayment.size(); i++)
		{
			SalePayDef spd = (SalePayDef) saleBS.salePayment.get(i);
			if (spd.str6.equals("App"))
			{
				if (spd.payno.equals(super.getDisplayCardno()))
				{
					new MessageBox("已存在券号" + spd.payno + "的付款!");
					return false;
				}
			}
		}

		return true;
	}
}
