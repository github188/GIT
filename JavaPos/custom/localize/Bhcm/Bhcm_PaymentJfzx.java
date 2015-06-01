package custom.localize.Bhcm;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Bhcm_PaymentJfzx extends Payment
{
	public Bhcm_PaymentJfzx()
	{
		super();
	}

	public Bhcm_PaymentJfzx(PayModeDef mode, SaleBS sale)
	{
		super(mode, sale);
	}

	// 该构造函数用于红冲小票时,通过小票付款明细创建对象
	public Bhcm_PaymentJfzx(SalePayDef pay, SaleHeadDef head)
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
		if (saleBS.curCustomer == null)
		{
			new MessageBox("没有刷会员卡，不允许积分消费和积分退货");
			return false;
		}

		String payno = null;
		try
		{
			if (SellType.ISBACK(saleBS.saletype))
			{
				// 检查当前卡是否有积分规则
				boolean hav = false;				
				if (saleBS.curCustomer.memo != null && saleBS.curCustomer.memo.split(",").length >= 2) hav = true;
				
				// 检查原交易小票的付款方式里面是否存在积分消费,否则按当前卡的当前规则计算
				if (saleBS.backPayment != null && saleBS.backPayment.size() > 0)
				{
					for (int i = 0; i < saleBS.backPayment.size(); i++)
					{
						SalePayDef spd = (SalePayDef) saleBS.backPayment.elementAt(i);
						if (DataService.getDefault().searchPayMode(spd.paycode).code.equals("0508"))
						{
							String[] memos = spd.idno.split(",");
							if (memos.length >= 3)
							{
								saleBS.curCustomer.memo = memos[1] + "," + memos[2];
							}
							break;
						}
					}
				}
				
				// 选择积分规则
				if (hav)
				{
/*					不选择,总是退回当前交易刷的卡,如果要换卡可在交易界面换卡刷
					String[] title = { "方式" };
					int[] width = { 500 };
					Vector contents = new Vector();
					contents.add(new String[] { "将积分存入原消费会员卡上" });
					contents.add(new String[] { "将积分存入其他的会员卡上" });
					int choice = new MutiSelectForm().open("请选择", title, width, contents);
					if (choice == 1) hav = false;
*/					
				}
				else
				{
					new MessageBox("没有找到积分消费规则，按【任意键】后请刷会员卡");
				}
				
				// 无积分规则
				if (!hav)
				{
					// 读取会员卡
					HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
					String track2 = bs.readMemberCard(true);
					if (track2 == null || track2.equals("")) return false;

					// 查找会员卡
					CustomerDef cust = bs.findMemberCard(track2);
					if (cust == null) return false;

					payno = cust.code;

					if (saleBS.curCustomer.memo == null || saleBS.curCustomer.memo.trim().length() <= 0)
					{
						saleBS.curCustomer.memo = cust.memo;
					}
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		if (super.createSalePay(money))
		{
			if (payno == null) salepay.payno = saleBS.curCustomer.code;
			else salepay.payno = payno;

			String[] num = saleBS.curCustomer.memo.split(",");
			double num1 = Double.parseDouble(num[0]);
			double num2 = Double.parseDouble(num[1]);
			salepay.idno = String.valueOf(ManipulatePrecision.mul(num1, ManipulatePrecision.div(salepay.ybje, num2))) + "," + saleBS.curCustomer.memo;
			return true;
		}

		return false;
	}
}
