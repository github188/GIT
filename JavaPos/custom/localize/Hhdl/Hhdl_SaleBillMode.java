package custom.localize.Hhdl;

import java.util.Vector;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bstd.Bstd_SaleBillMode;

public class Hhdl_SaleBillMode extends Bstd_SaleBillMode
{
	private Vector couponGift = null;

	private double getCouponExcept()
	{
		double money = 0;

		for (int i = 0; i < salepay.size(); i++)
		{
			SalePayDef spay = (SalePayDef) salepay.elementAt(i);
			if ((spay.paycode.equals("0508") || spay.paycode.equals("0555")) && spay.num1 > 0)
			{
				money += spay.num1;
			}
		}
		return ManipulatePrecision.doubleConvert(money, 2, 1);
	}

	protected String sumryCouponPrint()
	{
		String line = "";
		try
		{
			for (int x = 0; x < salepay.size(); x++)
			{
				SalePayDef spay = (SalePayDef) salepay.elementAt(x);
				if (spay.paycode.equals("0508") || spay.paycode.equals("0555"))
					line += (spay.payname + "     " + String.valueOf(ManipulatePrecision.doubleConvert(spay.je - spay.num1, 2, 1)) + "\n");
			}

			return line;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public void printPay()
	{
		// 设置打印区域
		setPrintArea("Pay");

		// 循环打印付款明细
		for (int i = 0; i < salepay.size(); i++)
		{
			SalePayDef spd = (SalePayDef) salepay.elementAt(i);

			if (spd.paycode.equals("0508") || spd.paycode.equals("0555"))
				continue;

			// 找零付款不打印
			if (spd.flag == '2')
				continue;

			printVector(getCollectDataString(Pay, i, Width));
		}

		// 汇总打印券付款信息
		Printer.getDefault().printLine_Normal(sumryCouponPrint());
	}

	protected String extendCase(PrintTemplateItem item, int index)
	{
		String line = null;
		SaleGoodsDef sgd = null;

		switch (Integer.parseInt(item.code))
		{
			case SBM_sjfk: // 实收金额
				line = ManipulatePrecision.doubleToString((salehead.sjfk - getCouponExcept()) * SellType.SELLSIGN(salehead.djlb));
				break;
			case SBM_cjdj: // 成交单价
				sgd = (SaleGoodsDef) salegoods.elementAt(index);
				if (sgd.flag == '2' && sgd.num4 > 0)
					line = ManipulatePrecision.doubleToString(sgd.num4);
				else if (sgd.flag == '2') // 电子秤打印4位小数成交单价
					line = ManipulatePrecision.doubleToString(ManipulatePrecision.div(sgd.hjje - sgd.hjzk, sgd.sl), 4, 1);
				else
					line = ManipulatePrecision.doubleToString(ManipulatePrecision.div(sgd.hjje - sgd.hjzk, sgd.sl), 2, 1);

				break;
			case SBM_Memo:
				if (couponGift != null)
				{
					for (int i = 0; i < couponGift.size(); i++)
					{
						Hhdl_CouponGiftDef def = (Hhdl_CouponGiftDef) this.couponGift.elementAt(i);
						if (def.type.equals("1"))
						{
							line = def.memo;
							break;
						}
					}
				}

				break;

			case SBM_ybje: // 付款方式金额
				SalePayDef pay = (SalePayDef) salepay.elementAt(index);
				if (pay.paycode.equals("0111") || pay.memo.equals("3"))
				{
					double je = pay.ybje * SellType.SELLSIGN(salehead.djlb);
					if (je < 0)
					{
						line = "(存入)" + ManipulatePrecision.doubleToString(je*-1, 2, 1);
						break;
					}
				}

				line = ManipulatePrecision.doubleToString(pay.ybje * SellType.SELLSIGN(salehead.djlb));
				break;
		}

		if (line == null)
			return super.extendCase(item, index);
		else
			return line;
	}

	public void printSaleTicketMSInfo()
	{
		if (this.zq == null || this.zq.size() <= 0)
			return;

		if (this.salemsinvo != 0 && salehead.fphm != this.salemsinvo)
		{
			this.salemsinvo = 0;
			this.zq = null;
			return;
		}

		for (int i = 0; i < this.zq.size(); i++)
		{
			Hhdl_CouponGiftDef def = (Hhdl_CouponGiftDef) zq.elementAt(i);

			if (Hhdl_GiftBillMode.getDefault().checkTemplateFile())
			{
				Hhdl_GiftBillMode.getDefault().setTemplateObject(salehead, def);
				Hhdl_GiftBillMode.getDefault().PrintGiftBill();
			}
		}
	}

	public void setSaleTicketMSInfo(SaleHeadDef sh, Vector gifts)
	{
		// 记录小票赠送清单
		this.salemsinvo = sh.fphm;
		this.couponGift = gifts;

		Vector couponInfo = new Vector();
		double couponJe = 0.0;
		StringBuffer sbJe = new StringBuffer();
		StringBuffer sbGoods = new StringBuffer();

		for (int i = 0; gifts != null && i < gifts.size(); i++)
		{
			Hhdl_CouponGiftDef gift = (Hhdl_CouponGiftDef) gifts.elementAt(i);

			// 出错时显示提示信息
			if (gift.type.trim().equals("-1"))
			{
				new MessageBox(gift.memo);
				break;
			}
			// 赠券
			else if (gift.type.trim().equals("0"))
			{
				couponInfo.add(gift);
				sbJe.append("券号: " + gift.code + "  ");
				sbJe.append("券名: " + gift.info + "  ");
				sbJe.append("券额: " + Convert.increaseChar(ManipulatePrecision.doubleToString(gift.je), 14) + "\n");
				couponJe += gift.je;
			}
			// 赠品
			else if (gift.type.trim().equals("1"))
			{
				couponInfo.add(gift);
				if (gift.memo != null && gift.memo.indexOf(",") != -1)
				{
					String[] goods = gift.memo.split(",");

					if (goods.length > 0)
						sbGoods.append("商品编码: " + Convert.increaseChar(goods[0], 6) + "  ");
					if (goods.length > 1)
						sbGoods.append("名称: " + Convert.increaseChar(goods[1], 10) + "  ");
					if (goods.length > 2)
						sbGoods.append("规格: " + Convert.increaseChar(goods[2], 10) + "  ");
					if (goods.length > 3)
						sbGoods.append("数量: " + Convert.increaseChar(goods[3], 8));

					sbGoods.append("\n");
				}
			}
		}

		if (couponJe > 0)
		{
			sbJe.append("\n本单赠券总额: " + Convert.increaseChar(ManipulatePrecision.doubleToString(couponJe), 14));
			new MessageBox(sbJe.toString());
		}

		if (sbGoods.toString().length() > 4)
			new MessageBox("本单赠品信息\n\n" + sbGoods.toString());

		// 设置
		if (couponInfo.size() > 0)
			this.zq = couponInfo;
		else
			this.zq = null;
	}
}
