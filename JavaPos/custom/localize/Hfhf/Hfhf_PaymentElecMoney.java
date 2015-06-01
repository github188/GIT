package custom.localize.Hfhf;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Hfhf.Hfhf_ElecMoney.LockPayResult;

public class Hfhf_PaymentElecMoney extends Payment
{
	public Hfhf_PaymentElecMoney(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	// 该构造函数用于红冲小票时,通过小票付款明细创建对象
	public Hfhf_PaymentElecMoney(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public int getAccountInputMode()
	{
		return TextBox.MsrInput;
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

			if (SellType.ISBACK(saleBS.saletype))
			{
				new MessageBox("不允许使用该付款方式退货");
				return null;
			}
			// 打开明细输入窗口
			new Hfhf_ElecMoneyForm().open(this, saleBS);

			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	public boolean createPayment(String cust, String elecID, String money)
	{
		try
		{
			double amount = Convert.toDouble(money);

			if (SellType.ISBACK(saleBS.saletype))
				amount = amount * -1;

			LockPayResult result = Hfhf_CrmModule.getDefault().lockElecMoney(saleBS.saletype, cust, elecID, amount);
			if (result == null)
				return false;

			if (!Hfhf_CrmModule.getDefault().confirmElecMoney(result.ReferNo))
				return false;

			Hfhf_PaymentElecMoney pay = new Hfhf_PaymentElecMoney(paymode, saleBS);
			pay.paymode = (PayModeDef) this.paymode.clone();
			pay.salehead = this.salehead;
			pay.saleBS = this.saleBS;

			if (pay.createSalePay(money))
			{
				pay.salepay.idno = cust;
				pay.salepay.payno = elecID;
				pay.salepay.kye = result.Balance;
				pay.salepay.batch = result.ReferNo;

				saleBS.addSalePayObject(pay.salepay, pay);

				alreadyAddSalePay = true;
				return true;
			}

			// 撤销
			Hfhf_CrmModule.getDefault().cancelElecMoney(saleBS.saletype, cust, elecID, amount, result.ReferNo);

			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.out.println(ex.getMessage());
			return false;
		}
	}

	public boolean cancelPay()
	{
		if (Hfhf_CrmModule.getDefault().cancelElecMoney(saleBS.saletype, salepay.idno, salepay.payno, salepay.je, salepay.batch))
			return true;

		return false;
	}

	public String getElecMoneyPayRuleInfo(String cardno, double totalAmount, double useableAmount, double saleAmount, double payAmount)
	{
		if (SellType.ISBACK(saleBS.saletype))
			return "";

		StringBuffer sb = new StringBuffer();
		sb.append("币号:" + cardno + "\n");
		sb.append("整单每满");
		sb.append(saleAmount + "元可用电子币");
		sb.append(payAmount + "元\n");

		double availableMoney = getAvailableMoney(totalAmount, useableAmount, saleAmount, payAmount);
		sb.append("当前可用" + Math.min(availableMoney, totalAmount) + "元");

		return sb.toString();

	}

	public double getAvailableMoney(double totalAmount, double useableAmount, double saleAmount, double payAmount)
	{
		if (SellType.ISBACK(saleBS.saletype))
			return 0.00;

		int availabelTimes = 0;

		availabelTimes = ManipulatePrecision.integerDiv(totalAmount, saleAmount);

		// 小于1，不具备用券条件
		if (availabelTimes < 1)
			return 0;

		// 计算出总的可用券额
		double availableAmount = ManipulatePrecision.doubleConvert(availabelTimes * payAmount, 2, 1);
		availableAmount = Math.min(availableAmount, totalAmount); // 在最大可用券额与最多可用金额之中取小
		availableAmount = Math.min(availableAmount, useableAmount);

		return availableAmount;
	}
}
