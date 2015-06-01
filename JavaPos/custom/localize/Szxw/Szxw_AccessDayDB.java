package custom.localize.Szxw;

import java.util.Vector;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Szxw_AccessDayDB extends AccessDayDB
{
	public boolean checkSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{

		int i;
		double je, zl;
		SaleGoodsDef saleGoodsDef = null;
		SalePayDef salePayDef = null;

		// 检查交易类型
		if (!SellType.VALIDTYPE(saleHead.djlb))
		{
			new MessageBox("[" + saleHead.djlb + "]交易类型无效!");

			return false;
		}

		// 反算合计折扣额
		if (saleHead.hjzje == 0)
		{
			saleHead.hjzje = ManipulatePrecision.doubleConvert(saleHead.ysje + saleHead.hjzke, 2, 1);
		}

		// 检查销售主单平衡
		if (salePayment.size() > 0
				&& ManipulatePrecision.doubleCompare(saleHead.sjfk - saleHead.zl, saleHead.ysje + saleHead.sswr_sysy + saleHead.fk_sysy, 2) != 0)
		{
			new MessageBox("交易主单数据相互不平!\n\n实际付款 - 找零 = " + ManipulatePrecision.doubleToString(saleHead.sjfk - saleHead.zl) + "\n应收金额 + 损溢 = "
					+ ManipulatePrecision.doubleToString(saleHead.ysje + saleHead.sswr_sysy + saleHead.fk_sysy));
			return false;
		}
		if (ManipulatePrecision.doubleCompare(saleHead.ysje, saleHead.hjzje - saleHead.hjzke, 2) != 0)
		{
			new MessageBox("交易主单数据相互不平!\n\n应收金额 = " + ManipulatePrecision.doubleToString(saleHead.ysje) + "\n合计金额 - 合计折扣 = "
					+ ManipulatePrecision.doubleToString(saleHead.hjzje - saleHead.hjzke));
			return false;
		}
		if (ManipulatePrecision.doubleCompare(saleHead.hjzke, saleHead.yhzke + saleHead.hyzke + saleHead.lszke, 2) != 0)
		{
			new MessageBox("交易主单数据相互不平!\n\n合计折扣 = " + ManipulatePrecision.doubleToString(saleHead.hjzke) + "\n折扣明细 = "
					+ ManipulatePrecision.doubleToString(saleHead.yhzke + saleHead.hyzke + saleHead.lszke));
			return false;
		}

		// 检查主单和商品明细之间的平衡
		je = 0;
		for (i = 0; i < saleGoods.size(); i++)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);

			if (saleGoodsDef.flag == '1')
			{
				continue;
			}

			if (saleGoodsDef.sl == 0 || saleGoodsDef.sl < 0)
			{
				new MessageBox("第 " + (i + 1) + " 行商品 [" + saleGoodsDef.code + "] 数量不合法\n请修改此行商品数量或者删除此商品后重新录入");
				return false;
			}

			if (saleGoodsDef.type == '8')
			{
				je -= saleGoodsDef.hjje - saleGoodsDef.hjzk;
			}
			else
			{
				je += saleGoodsDef.hjje - saleGoodsDef.hjzk;
			}
		}
		if (ManipulatePrecision.doubleCompare(saleHead.ysje, je, 2) != 0)
		{
			new MessageBox("交易主单和商品明细不平!\n\n主单应收金额 = " + ManipulatePrecision.doubleToString(saleHead.ysje) + "\n商品合计金额 = "
					+ ManipulatePrecision.doubleToString(je));
			return false;
		}

		// 检查主单和付款明细
		je = 0;
		zl = 0;
		for (i = 0; i < salePayment.size(); i++)
		{
			salePayDef = (SalePayDef) salePayment.elementAt(i);

			if (salePayDef.paycode.equalsIgnoreCase("DJQF"))
			{
				continue;
			}

			if (salePayDef.flag == '2')
			{
				zl += salePayDef.je;
			}
			else
			{
				// 不是扣回记入付款汇总
				if (!isBuckleMoney(salePayDef))
				{
					je += salePayDef.je;
				}
			}
		}
		if (ManipulatePrecision.doubleCompare(saleHead.sjfk, je, 2) != 0)
		{
			new MessageBox("交易主单和付款明细不平!\n\n主单实际付款 = " + ManipulatePrecision.doubleToString(saleHead.sjfk) + "\n付款合计金额 = "
					+ ManipulatePrecision.doubleToString(je));
			return false;
		}

		return true;

	}
}
