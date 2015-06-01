package custom.localize.Gbyw;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Gbyw.Gbyw_MzkModule.RetInfoDef;

public class Gbyw_PaymenBanktLczc extends Payment
{
	public Gbyw_PaymenBanktLczc()
	{

	}

	public Gbyw_PaymenBanktLczc(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	// 该构造函数用于红冲小票时,通过小票付款明细创建对象
	public Gbyw_PaymenBanktLczc(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public boolean createLczcSalePay(double zl)
	{
		RetInfoDef retinfo = Gbyw_MzkModule.getDefault().changeSale(true, salepay.je);
		if (retinfo == null)
			return false;

		if (!retinfo.retcode.equals("00"))
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

	public boolean cancelPay()
	{
		RetInfoDef retinfo = Gbyw_MzkModule.getDefault().changeSale(false, salepay.je);
		if (retinfo == null)
			return false;

		if (retinfo.retcode.equals("00"))
			return true;

		return false;
	}
}
