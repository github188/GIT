package custom.localize.Jwyt;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Jwyt_PaymentDeduct extends Payment
{
	public Jwyt_PaymentDeduct()
	{

	}

	public Jwyt_PaymentDeduct(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	// 该构造函数用于红冲小票时,通过小票付款明细创建对象
	public Jwyt_PaymentDeduct(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public void initPayment(PayModeDef mode, SaleBS sale)
	{
		saleBS = sale;
		salehead = (saleBS != null) ? saleBS.saleHead : null;
		paymode = mode;
	}

	public void initPayment(SalePayDef pay, SaleHeadDef head)
	{
		salepay = pay;
		salehead = head;
		paymode = DataService.getDefault().searchPayMode(salepay.paycode);
	}

	public boolean createSalePayObject(String money)
	{
		try
		{
			salepay = new SalePayDef();
			salepay.syjh = saleBS.saleHead.syjh;
			salepay.fphm = saleBS.saleHead.fphm;
			salepay.paycode = paymode.code;
			salepay.payname = paymode.name;
			salepay.flag = '1';
			salepay.ybje = Convert.toDouble(saleBS.getPayMoneyByPrecision(Convert.toDouble(money), paymode));
			salepay.hl = paymode.hl;
			salepay.je = ManipulatePrecision.doubleConvert(salepay.ybje * salepay.hl, 2, 1);
			salepay.payno = "";
			salepay.batch = "";
			salepay.kye = 0;
			salepay.idno = "";
			salepay.memo = "";
			salepay.str1 = "";
			salepay.str2 = "EWM";
			salepay.str3 = "";
			salepay.str4 = "";
			salepay.str5 = "";;
			salepay.num1 = 0;
			salepay.num2 = 0;
			salepay.num3 = 0;
			salepay.num4 = 0;
			salepay.num5 = 0;

			// 可溢余则超额部分记入付款溢余
			if (this.allowpayje >= 0 && ManipulatePrecision.doubleCompare(salepay.ybje, this.allowpayje, 2) > 0 && paymode.isyy == 'Y')
			{
				salepay.num1 = ManipulatePrecision.doubleConvert((salepay.ybje - this.allowpayje) * salepay.hl);
			}

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("生成交易付款对象出现异常\n\n" + ex.getMessage());
			ex.printStackTrace();
			return false;
		}
	}

/*	public boolean cancelPay()
	{
		new MessageBox("无法删除该短信码付款方式\n若想取消,请删除" + salepay.str5 + "商品");
		return false;

	}*/
}
