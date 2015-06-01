package com.efuture.javaPos.Payment;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Bank.WuHanTong_PaymentBankFunc;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class PaymentWuHanTong extends PaymentBank
{
	WuHanTong_PaymentBankFunc pbfunc;

	public PaymentWuHanTong()
	{
		super();
	}

	public PaymentWuHanTong(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public PaymentWuHanTong(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public void initPayment(PayModeDef mode, SaleBS sale)
	{
		super.initPayment(mode, sale);
		pbfunc = new WuHanTong_PaymentBankFunc();
	}

	public void initPayment(SalePayDef pay, SaleHeadDef head)
	{
		super.initPayment(pay, head);
		pbfunc = new WuHanTong_PaymentBankFunc();
	}

	public boolean createWhtZcSalePay(double zl)
	{
		if (SellType.ISBACK(salehead.djlb))
		{
			new MessageBox(Language.apply("退货交易不能使用武汉通转存"));
			return false;
		}

		if (!pbfunc.wuHanTongLczc(zl))
			return false;

		// 创建SalePay
		if (!createSalePayObject(String.valueOf((zl))))
			return false;

		// 零钞转存付款方式金额记负数
		salepay.ybje *= -1;
		salepay.je *= -1;

		// 代表零钞转存
		salepay.memo = "3";

		return true;
	}
}
