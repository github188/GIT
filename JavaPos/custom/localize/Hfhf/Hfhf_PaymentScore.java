package custom.localize.Hfhf;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Hfhf.Hfhf_VipScore.ExchangebleScore;
import custom.localize.Hfhf.Hfhf_VipScore.ExchangebleScoreResult;

public class Hfhf_PaymentScore extends Payment
{
	private ExchangebleScore scoreinfo;
	private String scorestr = "";

	public Hfhf_PaymentScore()
	{

	}

	public Hfhf_PaymentScore(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Hfhf_PaymentScore(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public int getMsrInputMode()
	{
		return TextBox.MsrRetTracks;
	}

	public String getYeLable()
	{
		return "积分余额";
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

			if (saleBS.curCustomer == null || !saleBS.curCustomer.valstr3.equals("szd"))
			{
				new MessageBox("未刷集团会员卡，无法使用该付款方式");
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
		ExchangebleScoreResult lockRet = null;
		double point = 0.0;
		double amount = Convert.toDouble(money);

		if (SellType.ISSALE(saleBS.saletype))
		{
			if (!checkScoreMoneyValid(amount, scoreinfo.BaseCash))
				return false;

			point = ManipulatePrecision.integerDiv(amount, scoreinfo.BaseCash) * scoreinfo.BasePoint;

			lockRet = Hfhf_CrmModule.getDefault().lockExchangebleScore(saleBS.saletype, cardno, amount, point);
			if (lockRet == null)
				return false;

			if (!Hfhf_CrmModule.getDefault().confirmExchangebleScore(saleBS.saletype, cardno, amount, point, lockRet.ReferNo))
				return false;
		}

		if (SellType.ISBACK(saleBS.saletype))
		{
			if (saleBS != null && saleBS.isNewUseSpecifyTicketBack(false))
			{
				String[] row = null;

				Vector v = new Vector();
				for (int i = 0; i < saleBS.backPayment.size(); i++)
				{
					SalePayDef spd = (SalePayDef) saleBS.backPayment.elementAt(i);
					if (paymode.code.equals(spd.paycode))
					{
						row = spd.idno.split(",");

						try
						{
							Double.parseDouble(row[1]);
							Double.parseDouble(row[2]);

							scorestr = row[1] + "," + row[2];

						}
						catch (Exception er)
						{
							new MessageBox(spd.idno);
							continue;
						}
						v.add(new String[] { Convert.increaseChar(row[1], 10) + "积分 兑换 " + Convert.increaseChar(row[2], 10) + "元", spd.idno });
					}
				}

				if (v.size() > 0)
				{
					String[] title = { "积分规则描述" };
					int[] width = { 460 };

					int choice = -1;
					if (v.size() == 1)
						choice = 0;
					else
						choice = new MutiSelectForm().open("请选择你使用的积分规则", title, width, v);

					if (choice >= 0)
					{
						String[] line = (String[]) v.elementAt(choice);
						row = line[1].split(",");
					}
				}

				if (!checkScoreMoneyValid(amount, Convert.toDouble(row[2])))
					return false;

				point = ManipulatePrecision.mul(Convert.toDouble(row[1]), ManipulatePrecision.div(amount, Convert.toDouble(row[2])));

				amount = amount * -1;
				point = point * -1;
				lockRet = Hfhf_CrmModule.getDefault().lockExchangebleScore(saleBS.saletype, cardno, amount, point);
				if (lockRet == null)
					return false;

				if (!Hfhf_CrmModule.getDefault().confirmExchangebleScore(saleBS.saletype, cardno, amount, point, lockRet.ReferNo))
					return false;
			}
			else
			{
				if (!checkScoreMoneyValid(amount, scoreinfo.BaseCash))
					return false;

				point = ManipulatePrecision.integerDiv(amount, scoreinfo.BaseCash) * scoreinfo.BasePoint;

				amount = amount * -1;
				point = point * -1;
				lockRet = Hfhf_CrmModule.getDefault().lockExchangebleScore(saleBS.saletype, cardno, amount, point);
				if (lockRet == null)
					return false;

				if (!Hfhf_CrmModule.getDefault().confirmExchangebleScore(saleBS.saletype, cardno, amount, point, lockRet.ReferNo))
					return false;

			}
		}

		if (super.createSalePay(money))
		{
			// 如果是指定小票退货，查询原小票是否有积分的付款方式

			// 分解积分折现规则
			String[] num = scorestr.split(",");

			// num1:积分数 num2:折现金额
			double num1 = Double.parseDouble(num[0]);
			double num2 = Double.parseDouble(num[1]);

			// salepay.num1 = ManipulatePrecision.mul(num1,
			// ManipulatePrecision.div(salepay.ybje, num2));

			salepay.idno = String.valueOf(salepay.num1) + "," + num1 + "," + num2 + ",,,," + ((saleBS.curCustomer != null) ? String.valueOf(saleBS.curCustomer.value4) : "") + "," + ((saleBS.curCustomer != null) ? saleBS.curCustomer.valstr2 : "");
			salepay.payno = cardno;
			salepay.batch = lockRet.ReferNo;

			// 1-积分消费
			salepay.memo = "1";

			return true;
		}

		Hfhf_CrmModule.getDefault().cancelExchangebleScore(saleBS.saletype, cardno, amount, point, lockRet.ReferNo);

		return false;
	}

	public boolean cancelPay()
	{
		if (!Hfhf_CrmModule.getDefault().cancelExchangebleScore(saleBS.saletype, salepay.payno, salepay.je, salepay.num1, salepay.batch))
			return false;

		return true;
	}

	public boolean getCrmCardData(String cardno)
	{
		ProgressBox prg = new ProgressBox();

		try
		{
			prg.setText("正在查询会员积分,请稍候...");
			scoreinfo = Hfhf_CrmModule.getDefault().queryExchangebleScoreBalance(cardno);

			if (scoreinfo == null)
				return false;

			scorestr = scoreinfo.BasePoint + "," + scoreinfo.BaseCash;
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
		if (scoreinfo != null)
			return scoreinfo.Points;

		return 0;
	}

	public double getValidMoney()
	{
		if (scoreinfo != null)
		{
			if (scoreinfo.Points < scoreinfo.BasePoint)
			{
				new MessageBox("积分总数不符合折现比例，不能用此付款方式付款");
				return 0;
			}

			int times = ManipulatePrecision.integerDiv(saleBS.calcPayBalance(), scoreinfo.BaseCash);

			if (times < 1)
			{
				new MessageBox("当前整单金额不符合折现比例，不能用此付款方式付款");
				return 0;
			}

			int i = times;

			for (; i > 0; i--)
			{
				if (i * scoreinfo.BasePoint <= scoreinfo.Points)
					break;
			}

			return i * scoreinfo.BaseCash;
		}

		return 0;
	}

	protected boolean checkScoreMoneyValid(double ybje, double times)
	{
		if (ManipulatePrecision.mod(ybje, times) != 0)
		{
			new MessageBox("消费金额必须为" + times + " 的整数倍!");
			return false;
		}

		return true;
	}

	public String getStatusInfo()
	{
		if (scoreinfo != null)
			return "";

		return "兑换比例\n" + scoreinfo.BasePoint + ":" + scoreinfo.BasePoint;
	}
}