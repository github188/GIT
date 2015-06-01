package custom.localize.Bhdd;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Bhdd_PaymentJfzx extends Payment
{
	public Bhdd_PaymentJfzx()
	{
		super();
	}

	public Bhdd_PaymentJfzx(PayModeDef mode, SaleBS sale)
	{
		super(mode, sale);
	}

	// 该构造函数用于红冲小票时,通过小票付款明细创建对象
	public Bhdd_PaymentJfzx(SalePayDef pay, SaleHeadDef head)
	{
		super(pay, head);
	}

	protected boolean checkMoneyValid(String money, double ye)
	{
		if (super.checkMoneyValid(money, ye))
		{
			for (int i = 0; i < saleBS.salePayment.size(); i++)
			{
				if (DataService.getDefault().searchPayMode(((SalePayDef) saleBS.salePayment.elementAt(i)).paycode).code.equals("0508"))
				{
					new MessageBox("已用积分卡付款\n请删除原付款后重新再试");
					return false;
				}
			}

			if (saleBS.curCustomer == null)
			{
				new MessageBox("未刷会员卡，不能用此付款方式付款");
				return false;
			}

			if (SellType.ISSALE(salehead.djlb) || (saleBS != null && saleBS.isRefundStatus()))
			{
				if (saleBS.curCustomer.valuememo <= 0)
				{
					new MessageBox("此会员卡没有积分，不能用此付款方式付款");
					return false;
				}
			}

			if (SellType.ISBACK(salehead.djlb) && saleBS.curCustomer.memo == null)
			{
				for (int i = 0; i < saleBS.backPayment.size(); i++)
				{
					SalePayDef backPay = (SalePayDef) saleBS.backPayment.get(i);

					if (backPay.paycode.equals("0508"))
					{
						String[] num = backPay.idno.split(",");

						if (num.length != 3)
						{
							break;
						}
						else
						{
							saleBS.curCustomer.memo = num[1] + "," + num[2];
							break;
						}
					}
				}
			}

			if (saleBS.curCustomer.memo == null)
			{
				new MessageBox("未定义折现基数或折现标准，不能用此付款方式付款");
				return false;
			}

			String[] num = saleBS.curCustomer.memo.split(",");
			try
			{
				if (num.length != 2)
				{
					new MessageBox("未定义折现基数或折现标准，不能用此付款方式付款");
					return false;
				}

				double num1 = Double.parseDouble(num[0]);

				double num2 = Double.parseDouble(num[1]);

				if (num1 <= 0 || num2 <= 0)
				{
					new MessageBox("折现基数或折现标准格式错误，不能用此付款方式付款");
					return false;
				}

				double fke = Double.parseDouble(money);

				if (SellType.ISSALE(salehead.djlb) || (saleBS != null && saleBS.isRefundStatus()))
				{
					double num3 = ManipulatePrecision.div(saleBS.curCustomer.valuememo, num1);
					if (num3 < 1)
					{
						new MessageBox("卡积分不足以折现，不能用此付款方式付款");
						return false;
					}

					double num4 = ManipulatePrecision.mul(num3, num2);
					if (fke > num4)
					{
						new MessageBox("超出最大付款金额\n此卡最大付款金额为" + num4);
						return false;
					}
				}

				if (Math.floor(fke / num2) != ManipulatePrecision.div(fke, num2))
				{
					new MessageBox("此付款方式必须是" + num2 + "的整数倍");
					return false;
				}

			}
			catch (Exception er)
			{
				new MessageBox("折现基数或折现标准定义错误，不能用此付款方式付款");
				return false;
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean createSalePay(String money)
	{
		if (super.createSalePay(money))
		{
			salepay.payno = saleBS.curCustomer.code;

			String[] num = saleBS.curCustomer.memo.split(",");
			double num1 = Double.parseDouble(num[0]);
			double num2 = Double.parseDouble(num[1]);
			salepay.idno = String.valueOf(ManipulatePrecision.mul(num1, ManipulatePrecision.div(salepay.ybje, num2))) + "," + saleBS.curCustomer.memo;
			return true;
		}

		return false;
	}
}
