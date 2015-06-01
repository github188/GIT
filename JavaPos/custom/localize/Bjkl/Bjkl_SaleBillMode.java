package custom.localize.Bjkl;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bstd.Bstd_SaleBillMode;

public class Bjkl_SaleBillMode extends Bstd_SaleBillMode
{
	protected final static int SBM_CARDFLAG = 301;

	public void printDetail()
	{
		// 设置打印区域
		setPrintArea("Detail");

		// 循环打印商品明细
		for (int i = 0; i < salegoods.size(); i++)
		{
			// SaleGoodsDef sgd = (SaleGoodsDef) salegoods.elementAt(i);

			// 赠品商品不打印
			// if (sgd.flag == '1')
			// {
			// continue;
			// }

			printVector(getCollectDataString(Detail, i, Width));
		}
	}

	protected String extendCase(PrintTemplateItem item, int index)
	{
		String line = null;
		try
		{
			line = super.extendCase(item, index);

			switch (Integer.parseInt(item.code))
			{
			// case SBM_fphm: // 小票号码
			// line = Convert.increaseLong(salehead.fphm, 6);
			//
			// break;
			case SBM_CARDFLAG:
				if (SellType.ISCARD(salehead.djlb) && index >= 0 && index < salegoods.size())
				{
					SaleGoodsDef sgd = (SaleGoodsDef) salegoods.get(index);
					if (salehead.ismemo) // 收银员联
					{
						if (sgd.str4.trim().length() == 10)
							line = "成功";
						else
							line = "失败";
					}
					else
					{// 顾客联默认均为成功
						line = "成功";
					}
					break;
				}
			case SBM_sjfk: // 实收金额
			{
				double sjfk = salehead.sjfk * SellType.SELLSIGN(salehead.djlb);
				for (int i = 0; i < salepay.size(); i++)
				{
					SalePayDef pay = (SalePayDef) salepay.elementAt(i);
					if (pay.paycode.equals("0112") && pay.memo.equals("3"))
					{
						sjfk = sjfk + Math.abs(pay.je);
						break;
					}
				}

				line = ManipulatePrecision.doubleToString(sjfk);

			}
				break;
			case SBM_ybje:
			{
				SalePayDef pay = (SalePayDef) salepay.elementAt(index);
				if (pay.paycode.equals("0112") && pay.memo.equals("3"))
				{
					double je = pay.ybje * SellType.SELLSIGN(salehead.djlb);
					if (je < 0)
					{
						line = "(存入)" + ManipulatePrecision.doubleToString(je * -1, 2, 1);
						break;
					}
				}

				line = ManipulatePrecision.doubleToString(pay.ybje * SellType.SELLSIGN(salehead.djlb));
				break;
			}

			} // end switch

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return line;
	}
}
