package custom.localize.Zspj;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.CalcRulePopDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

import custom.localize.Zsbh.Zsbh_PaymentCoupon;

public class Zspj_PaymentCoupon extends Zsbh_PaymentCoupon
{
	public Zspj_PaymentCoupon()
	{
	}

	public Zspj_PaymentCoupon(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Zspj_PaymentCoupon(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public boolean paymentApportion(SalePayDef spay, Payment payobj, boolean test)
	{
		// 退货时不进行分摊
		if (SellType.ISBACK(salehead.djlb)) { return true; }

		// 开放分组
		Vector v = new Vector();

		for (int z = 0; z < vi.size(); z++)
		{
			CalcRulePopDef calPop = (CalcRulePopDef) vi.elementAt(z);

			// str3 模拟下此规则的已经分摊的金额
			if (test)
			{
				calPop.str3 = calPop.str2;
			}

			// 得到本规则所有商品总共满收
			int num1 = ManipulatePrecision.integerDiv(calPop.popje, Convert.toDouble(calPop.catid));
			double cxje = ManipulatePrecision.doubleConvert(num1 * Convert.toDouble(calPop.str1));
			double yfje = 0;

			for (int x = calPop.row_set.size() - 1; x >= 0; x--)
			{
				int i = Convert.toInt(((String[]) calPop.row_set.elementAt(x))[0]);
				GoodsDef goods = (GoodsDef) saleBS.goodsAssistant.elementAt(i);

				// 计算每个商品的最大可收金额,第一个商品用减计算出最大可收
				SaleGoodsDef sgd = (SaleGoodsDef) saleBS.saleGoods.elementAt(i);
				double ksje = 0;
				ksje = ManipulatePrecision.doubleConvert((sgd.hjje - sgd.hjzk) / Convert.toDouble(calPop.catid) * Convert.toDouble(calPop.str1));

				if (x == 0)
				{
					ksje = Math.max(ksje, ManipulatePrecision.sub(cxje, yfje));
				}
				else if (ksje > ManipulatePrecision.sub(cxje, yfje))
				{
					ksje = ManipulatePrecision.sub(cxje, yfje);
				}

				yfje = ManipulatePrecision.add(yfje, ksje);

				// 计算商品
				Object[] rows = { String.valueOf(i), String.valueOf(0), String.valueOf(goods.str4.split("\\|").length), calPop, String.valueOf(ksje) };
				int j = 0;

				for (j = 0; j < v.size(); j++)
				{
					Object[] rows1 = (Object[]) v.elementAt(j);

					if (Convert.toInt(rows1[2]) > Convert.toInt(rows[2]))
					{
						break;
					}

					if (Convert.toInt(rows1[2]) == Convert.toInt(rows[2]))
					{
						if (Convert.toInt(rows1[0]) < Convert.toInt(rows[0]))
						{
							break;
						}
					}
				}

				if (j < v.size())
				{
					v.add(j, rows);
				}
				else
				{
					v.add(rows);
				}
			}
		}

		double syje = ManipulatePrecision.doubleConvert(spay.je - spay.num1);

		for (int i = 0; i < v.size(); i++)
		{
			Object[] rows1 = (Object[]) v.elementAt(i);
			SpareInfoDef spinfo = (SpareInfoDef) saleBS.goodsSpare.elementAt(Convert.toInt(rows1[0]));
			CalcRulePopDef calPop = (CalcRulePopDef) rows1[3];

			int oldpayindex = -1;
			if (test)
				oldpayindex = getpaymentIndex(paymode.code, mzkret.cardno, spay.idno);

			double je1 = getValidValue(Convert.toInt(rows1[0]), oldpayindex); // 商品剩余未分摊金额

			// 商品按金额比例计算分摊金额
			// SaleGoodsDef sgd = (SaleGoodsDef)
			// saleBS.saleGoods.elementAt(Convert.toInt(rows1[0]));
			double je2 = Convert.toDouble(rows1[4]);// ManipulatePrecision.doubleConvert((sgd.hjje
													// - sgd.hjzk) /
													// Convert.toDouble(calPop.catid)
													// *
													// Convert.toDouble(calPop.str1));

			// 此规则最大能分摊金额
			int num1 = ManipulatePrecision.integerDiv(calPop.popje, Convert.toDouble(calPop.catid));
			double je3 = ManipulatePrecision.doubleConvert(num1 * Convert.toDouble(calPop.str1));

			// 此规则最大能分摊金额 - 此规则已分摊金额
			double je4 = 0;

			if (!test)
			{
				je4 = ManipulatePrecision.doubleConvert(je3 - Convert.toDouble(calPop.str2));
			}
			else
			{
				je4 = ManipulatePrecision.doubleConvert(je3 - Convert.toDouble(calPop.str3));
			}

			// 计算是否存在相同的券付款 , 减去已付款的此券金额（不包含同卡的已付金额）
			double tqfk = getftje(spinfo, spay.paycode, spay.payno, spay.idno.charAt(0));
			double je5 = ManipulatePrecision.doubleConvert(je2 - tqfk);

			// 比较商品最大能收金额和规则能收最大金额
			double je6 = Math.min(je4, je5);

			// 比较余额和商品最大能收金额
			je6 = Math.min(je6, je1);

			if (spinfo.payft == null)
			{
				spinfo.payft = new Vector();
			}

			double spje = Math.min(syje, je6);

			if (!test)
			{
				String[] ft = new String[] { String.valueOf(spay.num5), spay.paycode, spay.payname, String.valueOf(spje) };
				spinfo.payft.add(ft);
				calPop.str2 = ManipulatePrecision.doubleToString(Convert.toDouble(calPop.str2) + spje);
			}
			else
			{
				calPop.str3 = ManipulatePrecision.doubleToString(Convert.toDouble(calPop.str3) + spje);
			}

			syje = ManipulatePrecision.doubleConvert(syje - spje);

			if (syje <= 0)
			{
				break;
			}
		}
		
		//平价不存在分摊，不能判断这个
/*		if (syje > 0)
		{
			spay.num1 = ManipulatePrecision.doubleConvert(syje + spay.num1);
		}*/

		return true;
	}
	
	public double getCouponJe(String paycode, String payno, String couponID, String hl, int oldpayindex)
	{
		if (vi == null)
		{
			vi = new Vector();
		}
		else
		{
			vi.removeAllElements();
		}
		
		return saleBS.calcPayBalance();
	}
}
