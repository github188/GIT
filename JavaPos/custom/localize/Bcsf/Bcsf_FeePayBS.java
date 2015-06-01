package custom.localize.Bcsf;

import java.util.Vector;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;

public class Bcsf_FeePayBS
{
	private Bcsf_DailyFeeBS feeItem;

	private double totalmoney; // 应付金额
	private double leavemoney; // 剩余金额

	private double realmoney;// 实付金额
	private double changemoney;// 找零金额

	private Vector feePay = new Vector();

	public Bcsf_FeePayBS(Bcsf_DailyFeeBS feeBS)
	{
		feeItem = feeBS;
		totalmoney = feeItem.getPaymoney();
		leavemoney = totalmoney;
	}

	public boolean payComplete()
	{
	//	if (!totalPay())
	//		return false;

		if (feeItem.sendFeeBill(feePay))
		{
			// 发送成功，置交易完成标志
			feeItem.setPayOK(true);
			// 写打印文件
			feeItem.writeBill(feePay);
			return true;
		}
		return false;
	}

	public void print()
	{
		// 打印
		feeItem.printBill();
	}

	public boolean isPayOK()
	{
		return feeItem.isPayOK();
	}

	// 汇总付款明细
	protected boolean totalPay()
	{
		try
		{
			if (feePay == null || feePay.size() == 0)
				return false;

			for (int i = 0; i < feePay.size(); i++)
			{
				DailyFeePayDef pay1 = (DailyFeePayDef) feePay.get(i);
				for (int j = i + 1; j < feePay.size(); j++)
				{
					DailyFeePayDef pay2 = (DailyFeePayDef) feePay.get(j);
					if (pay1.paytype.equals(pay2.paytype))
					{
						pay1.paymoney += pay2.paymoney;
						feePay.removeElement(pay2);
						j = i + 1;
					}
				}
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	// 删除找零对象
	public void delChangePay()
	{
		for (int i = 0; i < feePay.size(); i++)
		{
			DailyFeePayDef pay = (DailyFeePayDef) feePay.get(i);
			if (pay.paytype.equals("-1"))
			{
				changemoney = ManipulatePrecision.sub(changemoney, pay.paymoney);
				feePay.remove(pay);
				i--;
			}
		}
	}

	public boolean delFeePay(int index)
	{
		try
		{
			if (new MessageBox("确定删除该项付款?", null, true).verify() == GlobalVar.Key1)
			{
				if (feePay != null && feePay.size() != 0)
				{
					DailyFeePayDef pay = (DailyFeePayDef) feePay.get(index);

					realmoney = ManipulatePrecision.sub(realmoney, pay.paymoney);
					leavemoney = ManipulatePrecision.add(leavemoney, pay.paymoney);
					leavemoney = ManipulatePrecision.sub(leavemoney, changemoney);

					feePay.removeElementAt(index);

					// 只要有删除动作，就删除找零对象
					delChangePay();

					return true;
				}
			}
			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean calcFeePayComplete()
	{
		if (ManipulatePrecision.doubleConvert(leavemoney, 2, 1) == 0)
			return true;
		return false;
	}

	public boolean calcChange()
	{
		if (ManipulatePrecision.sub(realmoney, totalmoney) > 0)
		{
			changemoney = ManipulatePrecision.sub(realmoney, totalmoney);

			DailyFeePayDef pay = new DailyFeePayDef();
			pay.cashierid = GlobalInfo.posLogin.gh;
			pay.paymoney = ManipulatePrecision.doubleConvert(changemoney, 2, 1);
			pay.paytype = "-1";
			pay.posid = GlobalInfo.syjDef.syjh;
			pay.sdate = ManipulateDateTime.getCurrentDate() + " " + ManipulateDateTime.getCurrentTime();

			feePay.add(pay);
			return true;
		}
		return false;
	}

	public boolean addFeePay(String paycode, double money)
	{
		if (money == 0)
			return false;

		try
		{
			if (feePay.size() == 0)
			{
				// 生成付款对象
				DailyFeePayDef pay = new DailyFeePayDef();
				pay.cashierid = GlobalInfo.posLogin.gh;
				pay.paymoney = ManipulatePrecision.doubleConvert(money, 2, 1);
				pay.paytype = paycode;
				pay.posid = GlobalInfo.syjDef.syjh;
				pay.sdate = ManipulateDateTime.getCurrentDate() + " " + ManipulateDateTime.getCurrentTime();
				feePay.add(pay);
			}
			else
			{
				for (int i = 0; i < feePay.size(); i++)
				{
					DailyFeePayDef pay = (DailyFeePayDef) feePay.get(i);
					//现金付款进行累计
					if (paycode.equals("01") && pay.paytype.equals(paycode))
					{
						pay.paymoney += ManipulatePrecision.doubleConvert(money, 2, 1);
					}
				}
			}
			// 统计实付金额
			realmoney += ManipulatePrecision.doubleConvert(money, 2, 1);

			// 计算剩余金额
			leavemoney = ManipulatePrecision.sub(totalmoney, realmoney);

			if (ManipulatePrecision.doubleConvert(leavemoney, 2, 1) < 0)
				leavemoney = 0;

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public Vector getPay()
	{
		return feePay;
	}

	public double getTotalmoney()
	{
		return totalmoney;
	}

	public void setTotalmoney(double totalmoney)
	{
		this.totalmoney = totalmoney;
	}

	public double getLeavemoney()
	{
		return leavemoney;
	}

	public void setLeavemoney(double leavemoney)
	{
		this.leavemoney = leavemoney;
	}

	public double getRealmoney()
	{
		return realmoney;
	}

	public void setRealmoney(double realmoney)
	{
		this.realmoney = realmoney;
	}

	public double getChangemoney()
	{
		return changemoney;
	}

	public void setChangemoney(double changemoney)
	{
		this.changemoney = changemoney;
	}

}
