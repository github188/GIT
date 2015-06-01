package custom.localize.Bcsf;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Bcsf_PaymentStamp extends Payment
{
	public Bcsf_PaymentStamp()
	{

	}

	public Bcsf_PaymentStamp(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	// 该构造函数用于红冲小票时,通过小票付款明细创建对象
	public Bcsf_PaymentStamp(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public void initPayment(PayModeDef mode, SaleBS sale)
	{
		saleBS = sale;
		salehead = (saleBS != null) ? saleBS.saleHead : null;
		paymode = mode;
	}

	public boolean createSalePayObject(String money)
	{
		Vector sgds = this.saleBS.saleGoods;
		int stampcount = 0;

		for (int i = 0; i < sgds.size(); i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) sgds.get(i);
			stampcount += sgd.num2;
		}

		new MessageBox("请收取" + stampcount + "枚印花");

		if (super.createSalePayObject(money))
		{
			salepay.num2 = stampcount;
			return true;
		}
		return false;
	}
}
