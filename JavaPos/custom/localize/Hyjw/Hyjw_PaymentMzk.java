package custom.localize.Hyjw;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Hyjw.Hyjw_MzkModule.RetInfoDef;

public class Hyjw_PaymentMzk extends Payment
{
	public Hyjw_PaymentMzk()
	{
	}

	public Hyjw_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	// 该构造函数用于红冲小票时,通过小票付款明细创建对象
	public Hyjw_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public SalePayDef inputPay(String money)
	{
		// 打开明细输入窗口
		new Hyjw_PaymentMzkForm().open(this, saleBS);

		Hyjw_MzkModule.getDefault().initData();
		// 如果付款成功,则salepay已在窗口中生成
		return salepay;

	}

	public boolean createSalePay(String cardno, String money)
	{
		int which = 0;
		double je = 0;
		double total = Convert.toDouble(money);

		/*
		 * if (paymode.code.equals("0404"))
		 * {
		 * if (createSalePayObject(money))
		 * {
		 * salepay.payno = cardno;
		 * return true;
		 * }
		 * new MessageBox("创建积分消费付款失败!");
		 * return false;
		 * }
		 */

		while (true)
		{
			which = Hyjw_MzkModule.getDefault().useWhichAccount();

			switch (which)
			{
			case 0:
				new MessageBox("没有可用余额帐户,创建付款失败!");
				break;
			case 1:
				je = Hyjw_MzkModule.getDefault().getAccountMoney(1, total);

				if (je <= 0)
					continue;

				// 电子红包
				if (createMzkPay(1, "0401", cardno, je))
				{
					total = ManipulatePrecision.doubleConvert(total - je, 2, 1);
					if (total > 0)
						continue;

					return true;
				}
				new MessageBox("创建电子红包付款失败!");
				break;
			case 2:
				je = Hyjw_MzkModule.getDefault().getAccountMoney(2, total);
				if (je <= 0)
					continue;

				// 积分返利
				if (createMzkPay(2, "0402", cardno, je))
				{
					total = ManipulatePrecision.doubleConvert(total - je, 2, 1);
					if (total > 0)
						continue;

					return true;
				}
				new MessageBox("创建积分返利付款失败!");
				break;

			case 3:
				je = Hyjw_MzkModule.getDefault().getAccountMoney(3, total);

				if (je <= 0)
					return false;

				// 储值余额
				if (createMzkPay(3, paymode.code, cardno, je))
				{
					total = ManipulatePrecision.doubleConvert(total - je, 2, 1);

					return true;
				}
				new MessageBox("创建储值卡付款失败!");
				break;
			}

			break;
		}

		return false;
	}

	public boolean createMzkPay(int code, String paycode, String cardno, double money)
	{
		PayModeDef pmd = DataService.getDefault().searchPayMode(paycode);
		Hyjw_PaymentMzk pay = new Hyjw_PaymentMzk(pmd, saleBS);

		if (pay.createSalePayObject(money + ""))
		{
			pay.salepay.payno = cardno;
			Hyjw_MzkModule.getDefault().updateAccount(1, money);
			pay.salepay.kye = Hyjw_MzkModule.getDefault().getAccountKye(code);

			saleBS.addSalePayObject(pay.salepay, pay);
			alreadyAddSalePay = true;
			return true;
		}
		return false;
	}

	public boolean createSalePayObject(String money)
	{
		RetInfoDef retinfo = null;
		// 红包
		if (paymode.code.equals("0401"))
		{
			// 是否退货 撤销
			if (SellType.ISBACK(saleBS.saletype))
				retinfo = Hyjw_MzkModule.getDefault().elecPagSale(false, Convert.toDouble(money));
			else
				retinfo = Hyjw_MzkModule.getDefault().elecPagSale(true, Convert.toDouble(money));

			return super.createSalePayObject(retinfo.tradeAmount + "");
		}
		else if (paymode.code.equals("0402"))
		{
			// 是否退货
			if (SellType.ISBACK(saleBS.saletype))
			{
				// 是否当日退货
				if (saleBS.thFphm != 0)
				{
					Object obj = GlobalInfo.dayDB.selectOneData("select count(*) from salehead where fphm=" + saleBS.thFphm + "");
					// 隔日退货
					if (obj == null)
					{
						retinfo = Hyjw_MzkModule.getDefault().scoreRebateSale(3, Convert.toDouble(money));
					}
					// 撤销
					else
					{
						retinfo = Hyjw_MzkModule.getDefault().scoreRebateSale(2, Convert.toDouble(money));
					}
				}
			}
			else
			{
				retinfo = Hyjw_MzkModule.getDefault().scoreRebateSale(1, Convert.toDouble(money));
			}

			return super.createSalePayObject(retinfo.tradeAmount + "");
		}
		else if (paymode.code.equals("0403"))
		{
			// 是否退货
			if (SellType.ISBACK(saleBS.saletype))
			{
				// 是否当日退货
				if (saleBS.thFphm != 0)
				{
					Object obj = GlobalInfo.dayDB.selectOneData("select count(*) from salehead where fphm=" + saleBS.thFphm + "");
					// 隔日退货
					if (obj == null)
					{
						retinfo = Hyjw_MzkModule.getDefault().mzkSale(3, Convert.toDouble(money));
					}
					// 撤销
					else
					{
						retinfo = Hyjw_MzkModule.getDefault().mzkSale(2, Convert.toDouble(money));
					}
				}
			}
			else
			{
				retinfo = Hyjw_MzkModule.getDefault().mzkSale(1, Convert.toDouble(money));
			}
			return super.createSalePayObject(retinfo.tradeAmount + "");
		}
		else if (paymode.code.equals("0404"))
		{
			// 是否退货
			if (SellType.ISBACK(saleBS.saletype))
			{
				// 是否当日退货
				if (saleBS.thFphm != 0)
				{
					Object obj = GlobalInfo.dayDB.selectOneData("select count(*) from salehead where fphm=" + saleBS.thFphm + "");
					// 隔日退货
					if (obj == null)
					{
						retinfo = Hyjw_MzkModule.getDefault().scoreSale(3, Convert.toDouble(money));
					}
					// 撤销
					else
					{
						retinfo = Hyjw_MzkModule.getDefault().scoreSale(2, Convert.toDouble(money));
					}
				}
			}
			else
			{
				retinfo = Hyjw_MzkModule.getDefault().scoreSale(1, Convert.toDouble(money));
			}

			return super.createSalePayObject(retinfo.tradeAmount + "");
		}
		return false;
	}

	// 用于撤销
	public boolean cancelPay()
	{
		RetInfoDef retinfo = null;
		if (paymode.code.equals("0401") || paymode.code.equals("0402"))
		{
			new MessageBox("无法取消该付款!");
			return false;

			/*
			 * if (SellType.ISBACK(saleBS.saletype))
			 * {
			 * // retinfo = Hyjw_MzkModule.getDefault().elecPagSale(true,
			 * // salepay.je);
			 * }
			 * 
			 * else
			 * {
			 * retinfo = Hyjw_MzkModule.getDefault().elecPagSale(false,
			 * salepay.je);
			 * }
			 * 
			 * if (retinfo == null)
			 * return false;
			 * 
			 * if (retinfo.retcode.equals("00"))
			 * {
			 * Hyjw_MzkModule.getDefault().updateAccount(1,
			 * SellType.ISBACK(saleBS.saletype) ? salepay.je * -1 : salepay.je);
			 * return true;
			 * }
			 * }
			 * else if (paymode.code.equals("0402"))
			 * {
			 * if (SellType.ISBACK(saleBS.saletype))
			 * {
			 * // retinfo = Hyjw_MzkModule.getDefault().scoreRebateSale(1,
			 * // salepay.je);
			 * }
			 * /*
			 * else
			 * {
			 * retinfo = Hyjw_MzkModule.getDefault().scoreRebateSale(2,
			 * salepay.je);
			 * }
			 * 
			 * 
			 * if (retinfo == null)
			 * return false;
			 * 
			 * if (retinfo.retcode.equals("00"))
			 * {
			 * Hyjw_MzkModule.getDefault().updateAccount(2,
			 * SellType.ISBACK(saleBS.saletype) ? salepay.je * -1 : salepay.je);
			 * return true;
			 * }
			 */

		}
		else if (paymode.code.equals("0403"))
		{
			if (SellType.ISBACK(saleBS.saletype))
			{
				retinfo = Hyjw_MzkModule.getDefault().mzkSale(1, salepay.je);
			}
			else
			{
				retinfo = Hyjw_MzkModule.getDefault().mzkSale(2, salepay.je);
			}

			if (retinfo == null)
				return false;

			if (retinfo.retcode.equals("00"))
			{
				Hyjw_MzkModule.getDefault().updateAccount(3, SellType.ISBACK(saleBS.saletype) ? salepay.je * -1 : salepay.je);
				return true;
			}
		}

		return false;
	}
}
