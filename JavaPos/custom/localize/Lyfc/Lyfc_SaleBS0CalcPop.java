package custom.localize.Lyfc;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;

import custom.localize.Bstd.Bstd_SaleBS;

public class Lyfc_SaleBS0CalcPop extends Bstd_SaleBS
{
	public void calcAllRebate(int index)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// 指定小票退货时不重算优惠价和会员价
		if (isSpecifyBack(saleGoodsDef)) { return; }

		if (SellType.NOPOP(saletype))
			return;

		// 批发销售,预售定金不计算
		if (!SellType.ISSALE(this.saletype) || SellType.ISBATCH(saletype) || SellType.ISEARNEST(saletype) || SellType.ISPREPARETAKE(saletype)) { return; }

		// 削价商品和赠品不计算
		if ((saleGoodsDef.flag == '3') || (saleGoodsDef.flag == '1' || this.isHHGoods(saleGoodsDef))) { return; }

		// 清除商品相应自动计算的折扣
		saleGoodsDef.hyzke = 0;
		saleGoodsDef.hyzkfd = goodsDef.hyjzkfd;
		saleGoodsDef.yhzke = 0;
		saleGoodsDef.yhzkfd = 0;
		saleGoodsDef.plzke = 0;
		saleGoodsDef.zszke = 0;
		saleGoodsDef.fhdd = "";

		// 促销单缺省允许VIP折上折,可通过促销单定义改变
		popvipzsz = 'Y';

		// 计算商品促销折扣
		calcGoodsPOPRebate(index);

		// 计算会员VIP折上折
		calcGoodsVIPRebate(index);

		if (calcStampPop(index))
			saleGoodsDef.fhdd = goodsDef.couponrule;

		saleGoodsDef.yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.yhzke, 2, 1);
		saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert(saleGoodsDef.hyzke, 2, 1);

		// 按价格精度计算折扣
		if (saleGoodsDef.yhzke > 0)
			saleGoodsDef.yhzke = getConvertRebate(index, saleGoodsDef.yhzke);
		if (saleGoodsDef.hyzke > 0)
			saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
	}

	public boolean calcStampPop(int index)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// 指定小票退货时不重算优惠价和会员价
		if (isSpecifyBack(saleGoodsDef))
			return false;

		if (SellType.NOPOP(saletype))
			return false;

		// 批发销售,预售定金不计算
		if (!SellType.ISSALE(this.saletype) || SellType.ISBATCH(saletype) || SellType.ISEARNEST(saletype) || SellType.ISPREPARETAKE(saletype))
			return false;

		//存在规则促销
		if (saleGoodsDef.zszke > 0)
			saleGoodsDef.fhdd = "";

		// 促销优惠
		// 换消状态下不计算定期促销
		if (goodsDef.poptype != '0' && hhflag != 'Y')
		{
			// 促销折扣
			if ((1 > goodsDef.poplsjzkl) && (goodsDef.poplsjzkl > 0))
			{
				double stampzke = saleGoodsDef.hjje * (1 - goodsDef.poplsjzkl);
				// 如果优惠折扣额大于印花折扣，则不取印花折扣
				if (saleGoodsDef.yhzke > stampzke)
					return false;

				if (saleGoodsDef.hyzke > stampzke)
					return false;

				if (saleGoodsDef.zszke > stampzke)
					return false;

				if (saleGoodsDef.yhzke > 0)
				{
					saleGoodsDef.yhzke = 0;
					saleGoodsDef.yhdjbh = "";

					saleGoodsDef.yhzke = stampzke;
					calcZZK(index, saleGoodsDef);
					return true;
				}

				if (saleGoodsDef.hyzke > 0)
				{
					saleGoodsDef.hyzke = 0;
					saleGoodsDef.hydjbh = "";

					saleGoodsDef.yhzke = stampzke;
					calcZZK(index, saleGoodsDef);
					return true;
				}

				if (saleGoodsDef.zszke > 0)
				{
					saleGoodsDef.zszke = 0;
					saleGoodsDef.zsdjbh = "";

					saleGoodsDef.yhzke = stampzke;
					calcZZK(index, saleGoodsDef);
					return true;
				}

				saleGoodsDef.yhzke = stampzke;
				calcZZK(index, saleGoodsDef);
				return true;
			}
		}

		return false;

	}

	public void calcZZK(int index, SaleGoodsDef saleGoodsDef)
	{
		saleGoodsDef.yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.yhzke, 2, 1);

		// 按价格精度计算折扣
		if (saleGoodsDef.yhzke > 0)
			saleGoodsDef.yhzke = getConvertRebate(index, saleGoodsDef.yhzke);

		getZZK(saleGoodsDef);
	}
}
