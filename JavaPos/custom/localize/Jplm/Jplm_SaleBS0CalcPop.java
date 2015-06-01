package custom.localize.Jplm;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;

import custom.localize.Bstd.Bstd_SaleBS;

public class Jplm_SaleBS0CalcPop extends Bstd_SaleBS
{

	public void calcAllRebate(int index, boolean flag)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		boolean iscalcmktpopprice = false;

		// 指定小票退货时不重算优惠价和会员价
		if (isSpecifyBack(saleGoodsDef))
			return;

		if (SellType.NOPOP(saletype))
			return;

		// 批发销售,预售定金不计算
		if (!SellType.ISSALE(this.saletype) || SellType.ISBATCH(saletype) || SellType.ISEARNEST(saletype) || SellType.ISPREPARETAKE(saletype))
			return;

		// 削价商品和赠品不计算
		if ((saleGoodsDef.flag == '3') || (saleGoodsDef.flag == '1'))
			return;

		if (flag)
		{
			// 清除商品相应自动计算的折扣
			saleGoodsDef.hyzke = 0;
			saleGoodsDef.hyzkfd = goodsDef.hyjzkfd;
			saleGoodsDef.yhzke = 0;
			saleGoodsDef.yhzkfd = 0;
			saleGoodsDef.plzke = 0;
			saleGoodsDef.zszke = 0;
		}

		// 促销优惠
		// 换消状态下不计算定期促销
		if (goodsDef.poptype != '0' && hhflag != 'Y')
		{ // 定价且是单品优惠

			if ((saleGoodsDef.jg > 0) && ((goodsDef.poptype == '1') || (goodsDef.poptype == '7')))
			{
				// 促销折扣
				if ((saleGoodsDef.jg > goodsDef.poplsj) && (goodsDef.poplsj > 0))
				{
					saleGoodsDef.yhzke = (saleGoodsDef.jg - goodsDef.poplsj) * saleGoodsDef.sl;
					saleGoodsDef.yhzkfd = goodsDef.poplsjzkfd;
					iscalcmktpopprice = true;
				}
			}
			else
			{
				// 促销折扣
				if ((1 > goodsDef.poplsjzkl) && (goodsDef.poplsjzkl > 0))
				{
					saleGoodsDef.yhzke = (saleGoodsDef.hjje - getZZK(saleGoodsDef)) * (1 - goodsDef.poplsjzkl);

					saleGoodsDef.yhzkfd = goodsDef.poplsjzkfd;
					calcZZK(index, saleGoodsDef);
					return;
				}
			}

		}

		// 计算了门店优惠促销则不计算规则促销
		if (!iscalcmktpopprice)
		{
			// 促销单缺省允许VIP折上折,可通过促销单定义改变
			popvipzsz = 'Y';

			// 计算商品促销折扣
			calcGoodsPOPRebate(index);
		}

		// 计算会员VIP折上折
		calcGoodsVIPRebate(index);

		calcZZK(index, saleGoodsDef);

	}

	public void calcAllRebate(int index)
	{
		calcAllRebate(index, true);
	}

	public void calcZZK(int index, SaleGoodsDef saleGoodsDef)
	{
		saleGoodsDef.yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.yhzke, 2, 1);
		saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert(saleGoodsDef.hyzke, 2, 1);

		// 按价格精度计算折扣
		if (saleGoodsDef.yhzke > 0)
			saleGoodsDef.yhzke = getConvertRebate(index, saleGoodsDef.yhzke);
		if (saleGoodsDef.hyzke > 0)
			saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);

	}

}
