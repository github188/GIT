package custom.localize.Jdhx;

import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;

import custom.localize.Bstd.Bstd_SaleBSU51;

public class Jdhx_SaleBS0Cmpop extends Bstd_SaleBSU51
{
	protected void clearVipZkfd(SaleGoodsDef saleGoodsDef)
	{
		saleGoodsDef.hyzke = 0;
		saleGoodsDef.hyzkfd = 0;
		saleGoodsDef.yhzke = 0;
		saleGoodsDef.yhzkfd = 0;
		saleGoodsDef.plzke = 0;
		saleGoodsDef.zszke = 0;
		saleGoodsDef.yhdjbh = saleGoodsDef.hydjbh = "";
	}

	public void calcGoodsPOPRebate(int index)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		if (this.isHHGoods(saleGoodsDef))
			return;

		// 批发状态下不算促销
		if (SellType.ISBATCH(saletype))
			return;

		clearVipZkfd(saleGoodsDef);

		// 促销优惠
		if (goodsDef.poptype != '0')
		{
			// 定价且是单品优惠
			if ((saleGoodsDef.lsj > 0) && ((goodsDef.poptype == '1') || (goodsDef.poptype == '7')))
			{
				if (isMemberHyjMode() && isUseMemberHyj(saleGoodsDef.isvipzk))
				{
					// 先比促销零售价
					if (goodsDef.lsj >= goodsDef.poplsj && goodsDef.poplsj > 0)
					{
						clearVipZkfd(saleGoodsDef);

						saleGoodsDef.yhzke = (goodsDef.lsj - goodsDef.poplsj) * saleGoodsDef.sl;
						saleGoodsDef.yhzkfd = goodsDef.poplsjzkfd;
						saleGoodsDef.yhdjbh = goodsDef.popdjbh;

						// 再比会员促销零售价
						if (goodsDef.poplsj > goodsDef.pophyj && goodsDef.pophyj > 0)
						{
							clearVipZkfd(saleGoodsDef);

							saleGoodsDef.hyzke = (goodsDef.lsj - goodsDef.pophyj) * saleGoodsDef.sl;
							saleGoodsDef.hyzkfd = goodsDef.poplsjzkfd;
							saleGoodsDef.hydjbh = goodsDef.popdjbh;

							// 再比会员促销价
							if (goodsDef.pophyj > goodsDef.hypophyj && goodsDef.hypophyj > 0)
							{
								clearVipZkfd(saleGoodsDef);

								saleGoodsDef.hyzke = (goodsDef.lsj - goodsDef.hypophyj) * saleGoodsDef.sl;
								saleGoodsDef.hyzkfd = goodsDef.hypopzkfd;
								saleGoodsDef.hydjbh = goodsDef.hypopdjbh;
							}
							return;
						}// 促销从还要与会员促销价再比
						else if (goodsDef.poplsj > goodsDef.hypophyj && goodsDef.hypophyj > 0)
						{
							clearVipZkfd(saleGoodsDef);

							saleGoodsDef.hyzke = (goodsDef.lsj - goodsDef.hypophyj) * saleGoodsDef.sl;
							saleGoodsDef.hyzkfd = goodsDef.hypopzkfd;
							saleGoodsDef.hydjbh = goodsDef.hypopdjbh;
							return;
						}
					}// 零售价小于促销价的情况下，但又小于促销会员价
					else if (goodsDef.lsj >= goodsDef.pophyj && goodsDef.pophyj > 0)
					{
						clearVipZkfd(saleGoodsDef);

						saleGoodsDef.hyzke = (goodsDef.lsj - goodsDef.pophyj) * saleGoodsDef.sl;
						saleGoodsDef.hyzkfd = goodsDef.poplsjzkfd;
						saleGoodsDef.hydjbh = goodsDef.popdjbh;

						// 再比会员促销价
						if (goodsDef.pophyj > goodsDef.hypophyj && goodsDef.hypophyj > 0)
						{
							clearVipZkfd(saleGoodsDef);

							saleGoodsDef.hyzke = (goodsDef.lsj - goodsDef.hypophyj) * saleGoodsDef.sl;
							saleGoodsDef.hyzkfd = goodsDef.hypopzkfd;
							saleGoodsDef.hydjbh = goodsDef.hypopdjbh;
						}
						return;
					}// 零售价小于促销会员价的情况下，但又小于会员价
					else if (goodsDef.lsj >= goodsDef.hypophyj && goodsDef.hypophyj > 0)
					{
						clearVipZkfd(saleGoodsDef);

						saleGoodsDef.hyzke = (goodsDef.lsj - goodsDef.hypophyj) * saleGoodsDef.sl;
						saleGoodsDef.hyzkfd = goodsDef.hypopzkfd;
						saleGoodsDef.hydjbh = goodsDef.hypopdjbh;
						return;
					}
				}
				// 促销折扣
				else if ((saleGoodsDef.lsj > goodsDef.poplsj) && (goodsDef.poplsj > 0))
				{
					clearVipZkfd(saleGoodsDef);
					saleGoodsDef.yhzke = (saleGoodsDef.lsj - goodsDef.poplsj) * saleGoodsDef.sl;
					saleGoodsDef.yhzkfd = goodsDef.poplsjzkfd;
					saleGoodsDef.yhdjbh = goodsDef.popdjbh;
				}
			}
			else
			{
				// 刷了会员就得比分期普通折扣，会员折扣，会员促销折扣
				if (isMemberHyjMode() && isUseMemberHyj(saleGoodsDef.isvipzk))
				{
					if (1 > goodsDef.poplsjzkl && goodsDef.poplsjzkl > 0)
					{
						clearVipZkfd(saleGoodsDef);
						saleGoodsDef.yhzke = saleGoodsDef.hjje * (1 - goodsDef.poplsjzkl);
						saleGoodsDef.yhzkfd = goodsDef.poplsjzkfd;
						saleGoodsDef.yhdjbh = goodsDef.popdjbh;

						// 促销售价折扣低于促销会员折扣
						if (1 > goodsDef.pophyjzkl && goodsDef.poplsjzkl > goodsDef.pophyjzkl && goodsDef.pophyjzkl > 0)
						{
							clearVipZkfd(saleGoodsDef);
							saleGoodsDef.hyzke = saleGoodsDef.hjje * (1 - goodsDef.pophyjzkl);
							saleGoodsDef.hyzkfd = goodsDef.poplsjzkfd;
							saleGoodsDef.hydjbh = goodsDef.popdjbh;

							// 比会员价，会员价取的不是折扣比率，而是折扣额(U51变态的部分)
							if (goodsDef.hypophyj > 0)
							{
								double tmphyzke = (goodsDef.lsj - goodsDef.hypophyj) * saleGoodsDef.sl;
								if (saleGoodsDef.hyzke < tmphyzke)
								{
									clearVipZkfd(saleGoodsDef);
									saleGoodsDef.hyzke = tmphyzke;
									saleGoodsDef.hyzkfd = goodsDef.hypopzkfd;
									saleGoodsDef.hydjbh = goodsDef.hypopdjbh;
									return;
								}
							}
						}
						else if (goodsDef.hypophyj > 0)
						{
							double tmphyzke = (goodsDef.lsj - goodsDef.hypophyj) * saleGoodsDef.sl;
							if (saleGoodsDef.hyzke < tmphyzke)
							{
								clearVipZkfd(saleGoodsDef);
								saleGoodsDef.hyzke = tmphyzke;
								saleGoodsDef.hyzkfd = goodsDef.hypopzkfd;
								saleGoodsDef.hydjbh = goodsDef.hypopdjbh;

								return;
							}
						}

					}
				}
				// 促销折扣
				else if ((1 > goodsDef.poplsjzkl) && (goodsDef.poplsjzkl > 0))
				{
					clearVipZkfd(saleGoodsDef);
					saleGoodsDef.yhzke = saleGoodsDef.hjje * (1 - goodsDef.poplsjzkl);
					saleGoodsDef.yhzkfd = goodsDef.poplsjzkfd;
					saleGoodsDef.yhdjbh = goodsDef.popdjbh;
				}
			}
		}
		else
		// 会员定价
		{
			if (isMemberHyjMode() && isUseMemberHyj(saleGoodsDef.isvipzk))
			{
				// ishy='H'表示采用VIP会员价模式进行会员优惠
				if (saleGoodsDef.lsj > 0)
				{
					if ((saleGoodsDef.lsj > goodsDef.hyj) && (goodsDef.hyj > 0))
					{
						clearVipZkfd(saleGoodsDef);
						saleGoodsDef.hyzke = (saleGoodsDef.lsj - goodsDef.hyj) * saleGoodsDef.sl;
						saleGoodsDef.hyzkfd = 1;
					}
				}
				else
				{
					if ((1 > goodsDef.hyj) && (goodsDef.hyj > 0))
					{
						clearVipZkfd(saleGoodsDef);
						saleGoodsDef.hyzke = saleGoodsDef.hjje * (1 - goodsDef.hyj);
						saleGoodsDef.hyzkfd = 1;
					}
				}
			}
		}
	}
}
