package custom.localize.Shhl;

import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ExpressionDeal;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.CmPopGoodsDef;
import com.efuture.javaPos.Struct.CmPopRuleLadderDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.SalePayForm;

import custom.localize.Bstd.Bstd_SaleBS;
//import custom.localize.Bstd.Bstd_SaleBS0CmPop.PopRuleGoods;
//import custom.localize.Bstd.Bstd_SaleBS0CmPop.PopRuleGoodsGroup;

public class Shhl_SaleBS0CalcPop extends Bstd_SaleBS
{
	class PopRuleGoods
	{
		public int sgindex;
		public int cmindex;
	};
	
	class PopRuleGoodsGroup
	{
		public int goodsgroup;
		public int goodsgrouprow;
		public char condmode;
		public double condsl;
		public double condje;
		public double popje;
		public Vector goodslist;
	};
	
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
	
	public boolean calcGoodsCMPOPRebate(int index, CmPopGoodsDef cmp, int cmpindex)
	{

		// goodsCmppop与saleGoods中索引一 一对应
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		boolean havepop = false;

		if (saleGoodsDef.yhdjbh != null && saleGoodsDef.yhdjbh == "w")
			return true;

		// 该规则已使用,不重新计算
		if (cmp.used)
			return havepop;

		// goodsgrouprow>0,表示同分组有其他商品条件,先检查同分组条件
		Vector groupvec = null;
		int groupbs = -1;
		if (cmp.goodsgrouprow > 0 || cmp.ruleinfo.isonegroup == 'Y')
		{
			// 如果整个规则是一个分组则group标记为负,查询时找整个规则
			int group = cmp.goodsgroup;
			if (cmp.ruleinfo.isonegroup == 'Y')
				group = -1;

			// 查找规则组内的商品范围 传入当前cmp的档期id,规则id，去找同组的促销
			Vector grpvec = findSameGroup(cmp, group);// ((Bstd_DataService)
			                                          // DataService.getDefault()).findCMPOPGroup(cmp.dqid,
			                                          // cmp.ruleid, group);
			for (int n = 0; grpvec != null && n < grpvec.size(); n++)
			{ // 遍历组内促销，用找下来的组内促销逐条和所有商品的促销进行对比 ,对比条件:dqid,ruleid, isonegroup,
				// 若相同，则加入groupvec，表示是同一组内的
				CmPopGoodsDef grpcmp = (CmPopGoodsDef) grpvec.elementAt(n);

				// 商品列表中有商品符合该规则
				double grpsl = 0;
				double grpje = 0;
				for (int i = 0; i < saleGoods.size(); i++)
				{
					SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);
					Vector popvec = (Vector) goodsCmPop.elementAt(i); // 第i个商品参与的所有促销规则
					for (int j = 0; popvec != null && j < popvec.size(); j++)
					{ // 循环取出规则与当前组内促销对比
						CmPopGoodsDef cp = (CmPopGoodsDef) popvec.elementAt(j);
						if (grpcmp.dqid.equals(cp.dqid) && grpcmp.ruleid.equals(cp.ruleid) && !cp.used && ((cmp.ruleinfo.isonegroup != 'Y' && grpcmp.goodsgroup == cp.goodsgroup && grpcmp.goodsgrouprow == cp.goodsgrouprow) || (cmp.ruleinfo.isonegroup == 'Y' && grpcmp.goodsgroup == cp.goodsgroup)))
						{
							// groupvec记录参与组合促销条件的商品行
							if (groupvec == null)
								groupvec = new Vector();
							int k = 0;
							for (; k < groupvec.size(); k++)
							{
								PopRuleGoodsGroup prgp = (PopRuleGoodsGroup) groupvec.elementAt(k);
								if ((cmp.ruleinfo.isonegroup != 'Y' && grpcmp.goodsgroup == prgp.goodsgroup && grpcmp.goodsgrouprow == prgp.goodsgrouprow) || (cmp.ruleinfo.isonegroup == 'Y' && grpcmp.goodsgroup == prgp.goodsgroup))
									break;
							}
							if (k >= groupvec.size())
							{
								PopRuleGoodsGroup prgp = new PopRuleGoodsGroup();
								prgp.goodsgroup = grpcmp.goodsgroup;
								prgp.goodsgrouprow = grpcmp.goodsgrouprow;
								prgp.condmode = grpcmp.condmode;
								prgp.condsl = grpcmp.condsl;
								prgp.condje = grpcmp.condje;
								prgp.popje = grpcmp.poplsj;
								prgp.goodslist = new Vector();
								prgp.goodslist.add(String.valueOf(i));
								groupvec.add(prgp);
							}
							else
							{
								// 参与本组的商品行用|分隔
								PopRuleGoodsGroup prgp = (PopRuleGoodsGroup) groupvec.elementAt(k);
								int m = 0;
								for (; m < prgp.goodslist.size(); m++)
									if (i == Convert.toInt(prgp.goodslist.elementAt(m)))
										break;
								if (m >= prgp.goodslist.size())
									prgp.goodslist.add(String.valueOf(i));
							}

							// 同组累计
							grpsl += sg.sl;
							grpje += sg.hjje - sg.hjzk;
							break;
						}
					}
				}

				// 检查是否符合分组商品的条件及满足分组条件最少的倍数
				if ((grpcmp.condmode == '0' && (grpsl > 0 || grpje > 0)) || (grpcmp.condmode == '1' && grpsl >= grpcmp.condsl && grpcmp.condsl > 0) || (grpcmp.condmode == '2' && grpje >= grpcmp.condje && grpcmp.condje > 0) || (grpcmp.condmode == '3' && grpsl >= grpcmp.condsl && grpje >= grpcmp.condje && grpcmp.condsl > 0 && grpcmp.condje > 0))
				{
					int bs = -1;
					if (grpcmp.condmode == '1')
					{
						bs = ManipulatePrecision.integerDiv(grpsl, grpcmp.condsl);

						// condje>=1表示赠送结果集存在和条件相同的商品,例如买2送1
						// 因此按单倍把结果商品计算以后不参与条件运算，剩余部分数量再次计算促销
						if (bs > 1 && grpcmp.condje >= 1)
							bs = 1;
					}
					if (grpcmp.condmode == '2')
					{
						bs = ManipulatePrecision.integerDiv(grpje, grpcmp.condje);

						// condsl>=1表示赠送结果集存在和条件相同的商品,例如买2送1
						// 因此按单倍把结果商品计算以后不参与条件运算，剩余部分数量再次计算促销
						if (bs > 1 && grpcmp.condsl >= 1)
							bs = 1;
					}
					if (grpcmp.condmode == '3')
					{
						bs = ManipulatePrecision.integerDiv(grpsl, grpcmp.condsl);
						if (ManipulatePrecision.integerDiv(grpje, grpcmp.condje) < bs)
							bs = ManipulatePrecision.integerDiv(grpje, grpcmp.condje);
					}
					if (groupbs < 0 || (bs >= 0 && bs < groupbs))
						groupbs = bs;
				}
				else
				{
					// 不满足分组内其他条件
					return havepop;
				}
			}
		}

		// 记录同规则的商品行
		Vector rulegoods = new Vector();
		Vector rulegifts = new Vector();
		double popje = 0, hjcxsl = 0, hjcxje = 0, hjzje = 0, hjcjj = 0;
		do
		{
			// 标记为已参与计算的规则
			cmp.used = true;
			PopRuleGoods prg = new PopRuleGoods();
			prg.sgindex = index;
			prg.cmindex = cmpindex;
			rulegoods.add(prg);

			// 对商品进行规则累计
			hjzje = saleGoodsDef.hjje;
			hjcjj = saleGoodsDef.hjje - saleGoodsDef.hjzk;
			hjcxsl = saleGoodsDef.sl;
			if (cmp.ruleinfo.popzsz == 'Y')
				hjcxje = saleGoodsDef.hjje - saleGoodsDef.hjzk - getGoodsPaymentApportion(index, saleGoodsDef);
			else
				hjcxje = saleGoodsDef.hjje - getGoodsPaymentApportion(index, saleGoodsDef);

			// 查找需要进行规则累积的商品集合
			if (cmp.ruleinfo.summode != '0')
			{
				for (int i = 0; i < saleGoods.size(); i++)
				{
					if (i == index)
						continue;
					// 遍历saleGoods
					SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);

					if (i >= goodsCmPop.size())
						continue;

					Vector popvec = (Vector) goodsCmPop.elementAt(i);
					for (int j = 0; popvec != null && j < popvec.size(); j++)
					{
						CmPopGoodsDef cmpoth = (CmPopGoodsDef) popvec.elementAt(j);
						if (cmpoth.used)
							continue;
						// 只比档期id,规则Id相同的
						if (cmp.dqid.equals(cmpoth.dqid) && cmp.ruleid.equals(cmpoth.ruleid))
						{
							// 同规则累计,同规则同柜组累计,同规则单品累计,同规则同品类累计,同规则同品牌累计
							if ((cmp.ruleinfo.summode == '1') || (cmp.ruleinfo.summode == '2' && saleGoodsDef.gz.equals(sg.gz)) || (cmp.ruleinfo.summode == '3' && saleGoodsDef.code.equals(sg.code) && saleGoodsDef.gz.equals(sg.gz)) || (cmp.ruleinfo.summode == '4' && saleGoodsDef.catid.equals(sg.catid)) || (cmp.ruleinfo.summode == '5' && saleGoodsDef.ppcode.equals(sg.ppcode)))
							{
								hjzje += sg.hjje;
								hjcjj += sg.hjje - sg.hjzk;
								hjcxsl += sg.sl;
								if (cmp.ruleinfo.popzsz == 'Y')
									hjcxje += sg.hjje - sg.hjzk - getGoodsPaymentApportion(i, sg);
								else
									hjcxje += sg.hjje - getGoodsPaymentApportion(i, sg);

								// 标记为已参与计算的规则
								cmpoth.used = true;
								PopRuleGoods prgd = new PopRuleGoods();
								prgd.sgindex = i;
								prgd.cmindex = j;

								// 满金额条件按成交价、行号排序,优先计算金额大的商品促销
								// 满数量条件按无折扣、行号排序,优先计算无折扣的商品促销,多出的数量行以便计算除外
								int n = 0;
								for (n = 0; n < rulegoods.size(); n++)
								{
									int rgindex = ((PopRuleGoods) rulegoods.elementAt(n)).sgindex;
									SaleGoodsDef rg = (SaleGoodsDef) saleGoods.elementAt(rgindex);
									// 满金额条件按成交价、行号排序,优先计算金额大的商品促销
									if (cmp.ruleinfo.condmode == '2')
									{
										double sgcxje = 0, rgcxje = 0;
										if (cmp.ruleinfo.popzsz == 'Y')
										{
											sgcxje = sg.hjje - sg.hjzk - getGoodsPaymentApportion(i, sg);
											rgcxje = rg.hjje - rg.hjzk - getGoodsPaymentApportion(rgindex, rg);
										}
										else
										{
											sgcxje = sg.hjje - getGoodsPaymentApportion(i, sg);
											rgcxje = rg.hjje - getGoodsPaymentApportion(rgindex, rg);
										}
										if (sgcxje > rgcxje)
											break; // 成交价格大的排前面
										if (ManipulatePrecision.doubleCompare(sgcxje, rgcxje, 2) == 0 && i < rgindex)
											break;
									}// 满数量条件按无折扣、行号排序,优先计算无折扣的商品促销,多出的数量行以便计算除外
									else
									{ // 按折扣从小到大排
										if (sg.hjzk < rg.hjzk)
											break; // 合计折扣小的排前面
										if (ManipulatePrecision.doubleCompare(sg.hjzk, rg.hjzk, 2) == 0 && i < rgindex)
											break;
									}
								}
								if (n >= rulegoods.size())
									rulegoods.add(prgd);
								else
									rulegoods.insertElementAt(prgd, n);
								break;
							}
						}
					}
				}
			}

			// 组合促销去掉超过分组内条件的商品行,这些行不参与促销计算
			for (int n = 0; groupvec != null && n < groupvec.size(); n++)
			{
				PopRuleGoodsGroup prgp = (PopRuleGoodsGroup) groupvec.elementAt(n);
				char condmode = prgp.condmode;
				double condsl = prgp.condsl * groupbs;
				double condje = prgp.condje * groupbs;
				Vector sglist = prgp.goodslist;
				double grpsl = 0, grpje = 0;
				for (int k = 0; k < sglist.size(); k++)
				{
					int sgindex = Integer.parseInt((String) sglist.elementAt(k));
					SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(sgindex);
					grpsl += sg.sl;
					grpje += sg.hjje - sg.hjzk;

					// 去掉整行商品超过分组内条件的商品行,这些行不参与促销计算,数量超分组条件的拆行
					if ((condmode == '0') || (condmode == '1' && ManipulatePrecision.doubleCompare(grpsl, condsl, 4) > 0) || (condmode == '2' && ManipulatePrecision.doubleCompare(grpje, condje, 2) > 0 && ManipulatePrecision.doubleCompare(grpje - condje, (sg.hjje - sg.hjzk) / sg.sl, 2) >= 0) || (condmode == '3' && ManipulatePrecision.doubleCompare(grpsl, condsl, 4) > 0 && ManipulatePrecision.doubleCompare(grpje, condje, 2) > 0 && ManipulatePrecision.doubleCompare(grpje - condje, (sg.hjje - sg.hjzk) / sg.sl, 2) >= 0))
					{
						// 取消该行
						for (int j = 0; j < rulegoods.size(); j++)
						{
							PopRuleGoods prgd = (PopRuleGoods) rulegoods.elementAt(j);
							if (sgindex != prgd.sgindex)
								continue;

							// 无分组条件的商品行应该放到商品集合的最后，先计算有组内条件的商品行，再计算无条件的商品行
							if (condmode == '0')
							{
								rulegoods.add(prgd);
								rulegoods.remove(j);
								break;
							}

							// 加上本行商品数量超过分组条件需要拆分
							double sl = sg.sl;
							if (condmode == '1')
								sl = ManipulatePrecision.doubleConvert(grpsl - condsl, 4, 1);
							if (condmode == '2')
								sl = (int) ((grpje - condje) / ((sg.hjje - sg.hjzk) / sg.sl));
							if (condmode == '3')
								sl = Math.min(grpsl - condsl, (int) ((grpje - condje) / ((sg.hjje - sg.hjzk) / sg.sl)));
							if (ManipulatePrecision.doubleCompare(sg.sl, sl, 4) > 0)
							{
								SaleGoodsDef newsg = SplitSaleGoodsRow(sgindex, sg.sl - sl);
								if (newsg != null)
								{
									// 刷新拆分商品行
									refreshCmPopUI();

									// 合计数据去掉该商品行
									hjzje -= newsg.hjje;
									hjcjj -= newsg.hjje - newsg.hjzk;
									hjcxsl -= newsg.sl;
									if (cmp.ruleinfo.popzsz == 'Y')
										hjcxje -= newsg.hjje - newsg.hjzk - getGoodsPaymentApportion(saleGoods.size() - 1, newsg);
									else
										hjcxje -= newsg.hjje - getGoodsPaymentApportion(saleGoods.size() - 1, newsg);
								}
								break;
							}
							else
							{
								// 恢复该行促销标记
								if (goodsCmPop != null && goodsCmPop.size() > sgindex)
								{
									Vector popvec = (Vector) goodsCmPop.elementAt(sgindex);
									if (popvec != null && popvec.size() > prgd.cmindex)
										((CmPopGoodsDef) popvec.elementAt(prgd.cmindex)).used = false;
								}

								// 合计数据去掉该商品行
								hjzje -= sg.hjje;
								hjcjj -= sg.hjje - sg.hjzk;
								hjcxsl -= sg.sl;
								if (cmp.ruleinfo.popzsz == 'Y')
									hjcxje -= sg.hjje - sg.hjzk - getGoodsPaymentApportion(sgindex, sg);
								else
									hjcxje -= sg.hjje - getGoodsPaymentApportion(sgindex, sg);

								rulegoods.remove(j);
								break;
							}
						}
					}
				}
			}

			// 初始化折扣金额和赠品规则
			popje = 0;
			rulegifts.removeAllElements();

			// 无消费条件,则按规则的所有阶梯进行赠送(档期ID,规则ID,阶梯ID,促销倍数)
			if (cmp.ruleinfo.condmode == '0')
			{
				rulegifts.add(cmp.dqid + "," + cmp.ruleid + ",%,1");
			}
			else
			{
				// 有消费条件,按阶梯循环计算达到消费条件的促销金额,阶梯集合已倒序排序,优先级高的先执行
				// codntype,0=够满方式,只计算一个阶梯条件/1=每满方式,剩余部分循环参与下一个阶梯
				// condmode,1=数量类促销只计算一个阶梯,超过条件部分的数量被拆分并记为未参与本次促销,去参与其他促销或再次计算时参与下一个阶梯
				boolean loopladder = ((cmp.ruleinfo.condtype == '0' || cmp.ruleinfo.condmode == '1') ? false : true);

				// 累计已参与条件计算部分
				double yfsl = 0, yfje = 0;
				CmPopRuleLadderDef poprl = null;
				for (int i = 0; cmp.ruleladder != null && i < cmp.ruleladder.size(); i++)
				{
					poprl = (CmPopRuleLadderDef) cmp.ruleladder.elementAt(i);

					// 计算达到条件的倍数和参与达到条件的合计，剩余的合计参与下一个阶梯
					int laderbs = 0;
					// 达到数量
					if (cmp.ruleinfo.condmode == '1' && (hjcxsl - yfsl) >= poprl.levelsl)
					{
						if (poprl.levelminus == 'Y')
						{
							laderbs = ManipulatePrecision.integerDiv((hjcxsl - yfsl - poprl.levelsl), poprl.condsl);
						}
						else
						{
							// 剩余数量
							laderbs = ManipulatePrecision.integerDiv((hjcxsl - yfsl), poprl.condsl);
						}

						// condje>=1表示赠送结果集存在和条件相同的商品,例如买2送1
						// 因此按单倍把结果商品计算以后不参与条件运算，剩余部分数量再次计算促销
						if (laderbs > 1 && poprl.condje >= 1)
							laderbs = 1;

						// 如果阶梯数量等于0相当于没有数量条件，则默认有一倍阶梯
						if (laderbs == 0 && poprl.condsl == 0)
							laderbs = 1;
					}
					// 达到金额
					else if ((cmp.ruleinfo.condmode == '2' || cmp.ruleinfo.condmode == '4') && (hjcxje - yfje) >= poprl.levelje)
					{
						if (poprl.levelminus == 'Y')
						{
							laderbs = ManipulatePrecision.integerDiv((hjcxje - yfje - poprl.levelje), poprl.condje);
						}
						else
						{
							laderbs = ManipulatePrecision.integerDiv((hjcxje - yfje), poprl.condje);
						}

						// 满金额条件但对数量有分组要求
						if (laderbs > 0 && poprl.condsl > 0 && poprl.memo != null && poprl.memo.length() > 0 && poprl.memo.charAt(0) != '0' && poprl.memo.length() > 1 && (poprl.memo.charAt(1) == 'E' || poprl.memo.charAt(1) == 'L' || poprl.memo.charAt(1) == 'G'))
						{
							// 对参与规则的商品按分组方式进行分组,然后判断是否符合数量条件
							Vector grpvec = new Vector();
							for (int j = 0; j < rulegoods.size(); j++)
							{
								PopRuleGoods prgd = ((PopRuleGoods) rulegoods.elementAt(j));
								SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(prgd.sgindex);
								GoodsDef gd = (GoodsDef) goodsAssistant.elementAt(prgd.sgindex);

								// 1-按单品/2-按柜组/3-按品牌/4-按品类/5-按柜组+品牌/6-按柜组+品类/7-按品牌+品类/8-按柜+品+类/9-按条码(子商品)/A-按属性1/B-按属性2/C-按属性3/D-按属性4/E-按属性5/F-按属性6/G-按属性7/H-按属性8
								int n = 0;
								for (; n < grpvec.size(); n++)
								{
									int grpidx = Convert.toInt(((String[]) grpvec.elementAt(n))[1].split(",")[0]);
									SaleGoodsDef sggrp = (SaleGoodsDef) saleGoods.elementAt(grpidx);
									GoodsDef gdgrp = (GoodsDef) goodsAssistant.elementAt(grpidx);
									if ((poprl.memo.charAt(0) == '1' && sg.code.equals(sggrp.code) && sg.gz.equals(sggrp.gz)) || (poprl.memo.charAt(0) == '2' && sg.gz.equals(sggrp.gz)) || (poprl.memo.charAt(0) == '3' && sg.ppcode.equals(sggrp.ppcode)) || (poprl.memo.charAt(0) == '4' && sg.catid.equals(sggrp.catid)) || (poprl.memo.charAt(0) == '5' && sg.gz.equals(sggrp.gz) && sg.ppcode.equals(sggrp.ppcode)) || (poprl.memo.charAt(0) == '6' && sg.gz.equals(sggrp.gz) && sg.catid.equals(sggrp.catid)) || (poprl.memo.charAt(0) == '7' && sg.ppcode.equals(sggrp.ppcode) && sg.catid.equals(sggrp.catid)) || (poprl.memo.charAt(0) == '8' && sg.gz.equals(sggrp.gz) && sg.ppcode.equals(sggrp.ppcode) && sg.catid.equals(sggrp.catid)) || (poprl.memo.charAt(0) == '9' && sg.barcode.equals(sggrp.barcode)) || (poprl.memo.charAt(0) == 'A' && gd.attr01 != null && gd.attr01.equals(gdgrp.attr01)) || (poprl.memo.charAt(0) == 'B' && gd.attr02 != null && gd.attr02.equals(gdgrp.attr02)) || (poprl.memo.charAt(0) == 'C' && gd.attr03 != null && gd.attr03.equals(gdgrp.attr03)) || (poprl.memo.charAt(0) == 'D' && gd.attr04 != null && gd.attr04.equals(gdgrp.attr04)) || (poprl.memo.charAt(0) == 'E' && gd.attr05 != null && gd.attr05.equals(gdgrp.attr05)) || (poprl.memo.charAt(0) == 'F' && gd.attr06 != null && gd.attr06.equals(gdgrp.attr06)) || (poprl.memo.charAt(0) == 'G' && gd.attr07 != null && gd.attr07.equals(gdgrp.attr07)) || (poprl.memo.charAt(0) == 'H' && gd.attr08 != null && gd.attr08.equals(gdgrp.attr08)))
									{
										break;
									}
								}
								if (n >= grpvec.size())
								{
									String[] s = new String[] { "0", String.valueOf(prgd.sgindex) };
									if (cmp.ruleinfo.popzsz == 'Y')
										s[0] = String.valueOf(Convert.toDouble(s[0]) + (sg.hjje - sg.hjzk - getGoodsPaymentApportion(prgd.sgindex, sg)));
									else
										s[0] = String.valueOf(Convert.toDouble(s[0]) + (sg.hjje - getGoodsPaymentApportion(prgd.sgindex, sg)));
									grpvec.add(s);
								}
								else
								{
									String[] s = (String[]) grpvec.elementAt(n);
									if (cmp.ruleinfo.popzsz == 'Y')
										s[0] = String.valueOf(Convert.toDouble(s[0]) + (sg.hjje - sg.hjzk - getGoodsPaymentApportion(prgd.sgindex, sg)));
									else
										s[0] = String.valueOf(Convert.toDouble(s[0]) + (sg.hjje - getGoodsPaymentApportion(prgd.sgindex, sg)));
									s[1] = s[1] + "," + String.valueOf(prgd.sgindex);
									grpvec.set(n, s);
								}
							}

							// 当分组数量超过限制,按分组金额排序,排除金额较小分组商品不参与促销
							if (poprl.memo.length() > 1 && (poprl.memo.charAt(1) == 'L' || poprl.memo.charAt(1) == 'E') && grpvec.size() > poprl.condsl)
							{
								// 冒泡排序
								for (int j = 0; j < grpvec.size(); j++)
								{
									String[] s = (String[]) grpvec.elementAt(j);
									for (int n = j + 1; n < grpvec.size(); n++)
									{
										String[] s1 = (String[]) grpvec.elementAt(n);
										if (Convert.toDouble(s[0]) < Convert.toDouble(s1[0]))
										{
											// 交换,金额大的分组排前面
											String[] s2 = new String[] { s[0], s[1] };
											grpvec.set(j, s1);
											grpvec.set(n, s2);
										}
									}
								}

								// 去掉超过分组条件商品的促销标记
								for (int j = (int) poprl.condsl; j < grpvec.size(); j++)
								{
									// 除开多余分组的商品
									String[] s = ((String[]) grpvec.elementAt(j))[1].split(",");
									for (int n = 0; n < s.length; n++)
									{
										int sgindex = Convert.toInt(s[n]);

										// 查找商品行号在rulegoods中的位置,将商品从参与促销的集合中去掉
										int k = 0;
										PopRuleGoods prgd = null;
										for (; k < rulegoods.size(); k++)
										{
											prgd = ((PopRuleGoods) rulegoods.elementAt(k));
											if (prgd.sgindex == sgindex)
												break;
										}

										// 恢复该行促销标记并从集合中除开
										if (k < rulegoods.size() && prgd != null)
										{
											SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(prgd.sgindex);
											if (goodsCmPop != null && goodsCmPop.size() > prgd.sgindex)
											{
												Vector popvec = (Vector) goodsCmPop.elementAt(prgd.sgindex);
												if (popvec != null && popvec.size() > prgd.cmindex)
													((CmPopGoodsDef) popvec.elementAt(prgd.cmindex)).used = false;
											}

											// 合计数据去掉该商品行
											hjzje -= sg.hjje;
											hjcjj -= sg.hjje - sg.hjzk;
											hjcxsl -= sg.sl;
											if (cmp.ruleinfo.popzsz == 'Y')
												hjcxje -= sg.hjje - sg.hjzk - getGoodsPaymentApportion(prgd.sgindex, sg);
											else
												hjcxje -= sg.hjje - getGoodsPaymentApportion(prgd.sgindex, sg);

											// 从促销集合中排除商品
											rulegoods.remove(k);
										}
									}

									// 除开分组
									grpvec.remove(j);
									j--;
								}
							}

							// 分组后的数量是否符合条件
							if ((poprl.memo.length() > 1 && poprl.memo.charAt(1) == 'E' && grpvec.size() == poprl.condsl) || (poprl.memo.length() > 1 && poprl.memo.charAt(1) == 'G' && grpvec.size() >= poprl.condsl) || (poprl.memo.length() > 1 && poprl.memo.charAt(1) == 'L' && grpvec.size() <= poprl.condsl))
							{
								// 分组数量已符合条件
								if (poprl.levelminus == 'Y')
									laderbs = ManipulatePrecision.integerDiv((hjcxje - yfje - poprl.levelje), poprl.condje);
								else
									laderbs = ManipulatePrecision.integerDiv((hjcxje - yfje), poprl.condje);
							}
							else
							{
								// 分组数量不符合条件
								laderbs = 0;
							}
						}
					}
					// 数量金额同时达到
					else if (cmp.ruleinfo.condmode == '3' && (hjcxsl - yfsl) >= poprl.levelsl && (hjcxje - yfje) >= poprl.levelje)
					{
						if (poprl.levelminus == 'Y')
						{
							laderbs = ManipulatePrecision.integerDiv((hjcxsl - yfsl - poprl.levelsl), poprl.condsl);
							if (ManipulatePrecision.integerDiv((hjcxje - yfje - poprl.levelje), poprl.condje) < laderbs)
							{
								laderbs = ManipulatePrecision.integerDiv((hjcxje - yfje - poprl.levelje), poprl.condje);
							}
						}
						else
						{
							laderbs = ManipulatePrecision.integerDiv((hjcxsl - yfsl), poprl.condsl);
							if (ManipulatePrecision.integerDiv((hjcxje - yfje), poprl.condje) < laderbs)
							{
								laderbs = ManipulatePrecision.integerDiv((hjcxje - yfje), poprl.condje);
							}
						}
					}
					if (laderbs <= 0)
						continue;
					if (poprl.maxfb > 0 && laderbs > poprl.maxfb)
						laderbs = poprl.maxfb;
					if (groupbs >= 0 && laderbs > groupbs)
						laderbs = groupbs;

					// 计算商品参与本阶梯已使用条件额,剩余部分循环参与下一个阶梯
					if (cmp.ruleinfo.condmode == '1')
					{
						yfsl += poprl.condsl * laderbs;
					}
					else if (cmp.ruleinfo.condmode == '2')
					{
						yfje += poprl.condje * laderbs;
					}
					else if (cmp.ruleinfo.condmode == '3')
					{
						yfsl += poprl.condsl * laderbs;
						yfje += poprl.condje * laderbs;
					}// 单品总价按照比例打折不参加阶梯
					else if (cmp.ruleinfo.condmode == '4')
					{
						yfje += hjcxje;
					}

					// 只要满足了阶梯条件,就需要根据该阶梯查找对应的赠品(档期ID,规则ID,阶梯ID,促销倍数)
					rulegifts.add(poprl.dqid + "," + poprl.ruleid + "," + poprl.ladderid + "," + laderbs);

					// 促销金额采用动态表达式运算,laddername填写表达式
					if (poprl.laddername.toLowerCase().startsWith("calc|"))
					{
						String summarylabel = poprl.laddername.toLowerCase();
						String exp = summarylabel.substring(summarylabel.indexOf("calc|") + 5);

						// 替换关键字字段值
						String fld = "";
						int start = 0, end = 0;
						while (exp.indexOf(":", start) >= 0)
						{
							start = exp.indexOf(":", start) + 1;
							end = -1;
							for (int ii = start; ii < exp.length(); ii++)
							{
								if (!((exp.charAt(ii) >= '0' && exp.charAt(ii) <= '9') || (exp.charAt(ii) >= 'a' && exp.charAt(ii) <= 'z')))
								{
									end = ii;
									break;
								}
							}
							if (end >= 0)
								fld = exp.substring(start, end);
							else
								fld = exp.substring(start);
							String val = "0";
							if (fld.equalsIgnoreCase("hjzje"))
							{
								val = String.valueOf(hjzje);
							}
							else if (fld.equalsIgnoreCase("hjcjj"))
							{
								val = String.valueOf(hjcjj);
							}
							else if (fld.equalsIgnoreCase("hjzsl"))
							{
								val = String.valueOf(hjcxsl);
							}
							exp = ExpressionDeal.replace(exp, ":" + fld, val);
						}

						// 计算表达式
						String val = ExpressionDeal.SpiltExpression(exp);
						poprl.popje = Convert.toDouble(val);
					}

					// 根据促销结果形式计算出本阶梯条件应该折扣的金额
					double laderje = 0;
					if ((cmp.ruleinfo.popmode == '0') || (cmp.ruleinfo.popmode == '1' && poprl.popje >= 0) || (cmp.ruleinfo.popmode == '2' && 1 > poprl.popje && poprl.popje >= 0) || (cmp.ruleinfo.popmode == '3' && poprl.popje >= 0))
					{
						// 满数量条件的促销,超过条件部分的数量被拆分并记为未参与本次促销,去参与其他促销或再次计算时参与下一个阶梯
						double bhjzje = 0, bhjcjj = 0;
						if (cmp.ruleinfo.condmode == '1')
						{
							double sl = 0, ftsl = poprl.condsl * laderbs;
							for (int j = 0; j < rulegoods.size(); j++)
							{
								PopRuleGoods prgd = ((PopRuleGoods) rulegoods.elementAt(j));
								SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(prgd.sgindex);

								// 拆分商品行
								if (ftsl - sl > 0 && ManipulatePrecision.doubleCompare(sg.sl, ftsl - sl, 4) > 0)
								{
									SaleGoodsDef newsg = SplitSaleGoodsRow(prgd.sgindex, ftsl - sl);
									if (newsg != null)
									{
										// 刷新拆分商品行
										refreshCmPopUI();

										// 够满则拆分的商品行要标记为已运算不再计算,每满记未运算可再计算
										Vector popvec = (Vector) goodsCmPop.elementAt(saleGoods.size() - 1);
										if (popvec != null && popvec.size() > prgd.cmindex)
											((CmPopGoodsDef) popvec.elementAt(prgd.cmindex)).used = (cmp.ruleinfo.condtype == '0') ? true : false;

										// 合计数据去掉被拆分的商品行
										hjzje -= newsg.hjje;
										hjcjj -= newsg.hjje - newsg.hjzk;
										hjcxsl -= newsg.sl;
										if (cmp.ruleinfo.popzsz == 'Y')
											hjcxje -= newsg.hjje - newsg.hjzk - getGoodsPaymentApportion(saleGoods.size() - 1, newsg);
										else
											hjcxje -= newsg.hjje - getGoodsPaymentApportion(saleGoods.size() - 1, newsg);
									}
								}

								sl += sg.sl;
								if (sl > ftsl && (sl - ftsl) >= sg.sl)
								{
									// 超过数量条件部分从本次促销集合中删除
									// 够满则拆分的商品行要标记为已运算不再计算,每满记未运算可再计算
									Vector popvec = (Vector) goodsCmPop.elementAt(prgd.sgindex);
									if (popvec != null && popvec.size() > prgd.cmindex)
										((CmPopGoodsDef) popvec.elementAt(prgd.cmindex)).used = (cmp.ruleinfo.condtype == '0') ? true : false;

									// 合计数据去掉该商品行
									hjzje -= sg.hjje;
									hjcjj -= sg.hjje - sg.hjzk;
									hjcxsl -= sg.sl;
									if (cmp.ruleinfo.popzsz == 'Y')
										hjcxje -= sg.hjje - sg.hjzk - getGoodsPaymentApportion(prgd.sgindex, sg);
									else
										hjcxje -= sg.hjje - getGoodsPaymentApportion(prgd.sgindex, sg);

									rulegoods.remove(j);
									j--;
									continue;
								}
								if (sl <= ftsl)
								{
									bhjzje += sg.hjje;
									bhjcjj += sg.hjje - sg.hjzk;
								}
								else
								{
									bhjzje += sg.jg * (sg.sl - (sl - ftsl));
									bhjcjj += (sg.jg - ManipulatePrecision.doubleConvert(sg.hjzk / sg.sl)) * (sg.sl - (sl - ftsl));
								}
							}

							// myShop特色逢倍促销,逢倍数以后第N倍的那个商品促销,折扣只分摊到N倍的商品行,其他商品行不促销
							if (cmp.ruleinfo.rulename != null && cmp.ruleinfo.rulename.trim().equals("逢倍促销"))
							{
								int bs = (int) poprl.condsl;
								int xl = poprl.maxfb;
								sl = 0;
								for (int j = 0; j < rulegoods.size(); j++)
								{
									PopRuleGoods prgd = (PopRuleGoods) rulegoods.elementAt(j);
									SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(prgd.sgindex);

									// 不是倍数行不参与分摊
									sl += sg.sl;
									if ((sl % bs != 0 && sg.sl <= sl % bs) || (xl > 0 && sl / bs > xl))
									{
										// 合计数据去掉该商品行
										hjzje -= sg.hjje;
										hjcjj -= sg.hjje - sg.hjzk;
										hjcxsl -= sg.sl;
										if (cmp.ruleinfo.popzsz == 'Y')
											hjcxje -= sg.hjje - sg.hjzk - getGoodsPaymentApportion(prgd.sgindex, sg);
										else
											hjcxje -= sg.hjje - getGoodsPaymentApportion(prgd.sgindex, sg);

										rulegoods.remove(j);
										j--;
									}
								}
							}
						}
						else
						{
							// 满金额条件
							bhjzje = hjzje;
							bhjcjj = hjcjj;
						}

						// 如果是每满方式(0=够满/1=每满)且满金额条件按条件金额计算比例
						boolean everymoney = false;
						if (cmp.ruleinfo.condtype != '0' && cmp.ruleinfo.condmode != '1')
							everymoney = true;

						// 计算促销折扣
						if (cmp.ruleinfo.popmode == '1') // 指定促销价格
						{
							if (cmp.ruleinfo.popzsz == 'Y')
							{
								// 折上折模式,可打折金额要除掉已折扣部分
								laderje = (everymoney ? poprl.condje * laderbs : bhjcjj) - (poprl.popje * laderbs) - popje;
							}
							else
							{
								// 非折上折时,商品分摊时先清空已折扣金额,再折到成交价
								laderje = (everymoney ? poprl.condje * laderbs : bhjzje) - (poprl.popje * laderbs) - popje;
							}
						}
						else if (cmp.ruleinfo.popmode == '2') // 指定打折比率
						{

							// 指定打折比率,不能乘倍数
							if (cmp.ruleinfo.popzsz == 'Y')
							{
								// 折上折模式,在成交价基础上再打折
								if (cmp.ruleinfo.condmode == '4')
									laderje = (1 - poprl.popje) * bhjcjj;
								else
									laderje = (1 - poprl.popje) * (everymoney ? poprl.condje * laderbs : bhjcjj);

								if (popje + laderje > bhjcjj)
									laderje = bhjcjj - popje;
							}
							else
							{
								if (cmp.ruleinfo.condmode == '4')
									laderje = (1 - poprl.popje) * bhjzje;
								else
									// 非折上折时,在原价基础上计算折扣,并清空其他折扣
									laderje = (1 - poprl.popje) * (everymoney ? poprl.condje * laderbs : bhjzje);

								if (popje + laderje > bhjzje)
									laderje = bhjzje - popje;
							}
						}
						else if (cmp.ruleinfo.popmode == '3') // 指定减价金额
						{
							laderje = poprl.popje * laderbs;
							if (cmp.ruleinfo.popzsz == 'Y')
							{
								// 折上折模式,在成交价基础上再减价,如果累计减价金额超过成交价最多减到0
								if (popje + laderje > bhjcjj)
									laderje = bhjcjj - popje;
							}
							else
							{
								// 非折上折时,在原价基础上计算减价,并清空其他折扣,如果累计减价金额超过成交价最多减到0
								if (popje + laderje > bhjzje)
									laderje = bhjzje - popje;
							}
						}

						// 促销金额不能<0
						if (laderje < 0)
							laderje = 0;
					}
					else if ((cmp.ruleinfo.popmode == '4' && poprl.popje >= 0) || (cmp.ruleinfo.popmode == '5' && 1 > poprl.popje && poprl.popje >= 0) || (cmp.ruleinfo.popmode == '6' && poprl.popje >= 0))
					{
						// 指定单个商品的促销价值,达到条件的所有商品数量都参与促销
						double sgsl = 0, condsl = poprl.condsl * laderbs;
						for (int j = 0; j < rulegoods.size(); j++)
						{
							PopRuleGoods prgd = (PopRuleGoods) rulegoods.elementAt(j);
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(prgd.sgindex);

							// 每满数量条件,超过数量部分从本次促销集合中删除,可参与其他促销或再次计算时参与下一个阶梯
							if (cmp.ruleinfo.condmode == '1' && cmp.ruleinfo.condtype != '0')
							{
								if (condsl - sgsl > 0 && ManipulatePrecision.doubleCompare(sg.sl, condsl - sgsl, 4) > 0)
								{
									SaleGoodsDef newsg = SplitSaleGoodsRow(prgd.sgindex, condsl - sgsl);
									if (newsg != null)
									{
										// 刷新拆分商品行
										refreshCmPopUI();

										// 够满则拆分的商品行要标记为已运算不再计算,每满记未运算可再计算
										Vector popvec = (Vector) goodsCmPop.elementAt(saleGoods.size() - 1);
										if (popvec != null && popvec.size() > prgd.cmindex)
											((CmPopGoodsDef) popvec.elementAt(prgd.cmindex)).used = (cmp.ruleinfo.condtype == '0') ? true : false;

										// 合计数据去掉被拆分的商品行
										hjzje -= newsg.hjje;
										hjcjj -= newsg.hjje - newsg.hjzk;
										hjcxsl -= newsg.sl;
										if (cmp.ruleinfo.popzsz == 'Y')
											hjcxje -= newsg.hjje - newsg.hjzk - getGoodsPaymentApportion(saleGoods.size() - 1, newsg);
										else
											hjcxje -= newsg.hjje - getGoodsPaymentApportion(saleGoods.size() - 1, newsg);
									}
								}
								sgsl += sg.sl;
								if (sgsl > condsl && (sgsl - condsl) >= sg.sl)
								{
									// 超过数量条件部分从本次促销集合中删除
									// 够满则拆分的商品行要标记为已运算不再计算,每满记未运算可再计算
									Vector popvec = (Vector) goodsCmPop.elementAt(prgd.sgindex);
									if (popvec != null && popvec.size() > prgd.cmindex)
										((CmPopGoodsDef) popvec.elementAt(prgd.cmindex)).used = (cmp.ruleinfo.condtype == '0') ? true : false;

									// 合计数据去掉该商品行
									hjzje -= sg.hjje;
									hjcjj -= sg.hjje - sg.hjzk;
									hjcxsl -= sg.sl;
									if (cmp.ruleinfo.popzsz == 'Y')
										hjcxje -= sg.hjje - sg.hjzk - getGoodsPaymentApportion(prgd.sgindex, sg);
									else
										hjcxje -= sg.hjje - getGoodsPaymentApportion(prgd.sgindex, sg);

									rulegoods.remove(j);
									j--;
									continue;
								}
							}
							else
							{
								sgsl += sg.sl;
							}
						}

						// 每满数量条件相当于限量,例如每满10个每个X元,则限量应该是条件10*翻倍
						// 指定单个商品的促销价格,poprl.maxfb表示限量个数
						double zsl = poprl.maxfb;
						if (zsl > 0)
							zsl = Math.min(zsl, sgsl);
						else
							zsl = sgsl;

						// 计算
						double sysl = zsl, sl = 0;
						double levelsl = 0, fhsl = 0;
						boolean fh = false;
						for (int j = 0; j < rulegoods.size(); j++)
						{
							// 已计算完限制数量退出循环
							if (sysl <= 0)
								break;

							// 每个商品单个计算
							PopRuleGoods prgd = (PopRuleGoods) rulegoods.elementAt(j);
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(prgd.sgindex);

							// 除门槛条件的规则,未达到门槛条件的商品行不参与计算
							if (cmp.ruleinfo.condmode == '1' && poprl.levelminus == 'Y')
							{
								levelsl += sg.sl;
								if (levelsl <= poprl.levelsl)
									continue;
								else
								{
									// 只计算一次
									if (!fh)
									{
										fh = true;
										fhsl = sg.sl - (levelsl - poprl.levelsl);
									}
									else
										fhsl = 0;
								}
							}

							// 计算可促销数量
							if (zsl > 0 && (sg.sl - fhsl) > sysl)
								sl = sysl;
							else
								sl = (sg.sl - fhsl);
							sysl -= sl;

							// 拆分超过门槛数量的商品行
							if (ManipulatePrecision.doubleCompare(sg.sl, sl, 4) > 0)
							{
								SaleGoodsDef newsg = SplitSaleGoodsRow(prgd.sgindex, sl);
								if (newsg != null)
								{
									// 刷新拆分商品行
									refreshCmPopUI();

									// 拆分的商品行已参与促销条件计算
									Vector popvec = (Vector) goodsCmPop.elementAt(saleGoods.size() - 1);
									if (popvec != null && popvec.size() > prgd.cmindex)
										((CmPopGoodsDef) popvec.elementAt(prgd.cmindex)).used = true;
								}
							}

							// 计算促销折扣
							double zke = 0;
							if (cmp.ruleinfo.popmode == '4') // 指定成交价
							{
								// 商品当前折扣低于可促销的折扣则补足促销折扣
								if (sg.jg > poprl.popje && ManipulatePrecision.doubleCompare(getZZK(sg, CommonMethod.getTraceInfo(), true), (sg.jg - poprl.popje) * sl, 2) < 0)
								{
									// 不允许折上折,清空其他折扣
									if (cmp.ruleinfo.popzsz != 'Y')
										clearGoodsAllRebate(prgd.sgindex);
									zke = ManipulatePrecision.doubleConvert((sg.jg - poprl.popje) * sl - getZZK(sg, CommonMethod.getTraceInfo(), true), 2, 1);
								}
							}
							else if (cmp.ruleinfo.popmode == '5') // 指定打折率
							{
								// 促销允许折上折则在当前成交价基础上再折,不允许折上折低价优先清空其他折扣
								if (cmp.ruleinfo.popzsz == 'Y')
								{
									zke = ManipulatePrecision.doubleConvert((1 - poprl.popje) * (sg.hjje - getZZK(sg, CommonMethod.getTraceInfo(), true)) * (sl / sg.sl), 2, 1);
								}
								else if (ManipulatePrecision.doubleCompare(getZZK(sg, CommonMethod.getTraceInfo(), true), (1 - poprl.popje) * sl * sg.jg, 2) < 0)
								{
									clearGoodsAllRebate(prgd.sgindex);
									zke = ManipulatePrecision.doubleConvert((1 - poprl.popje) * sl * sg.jg - getZZK(sg, CommonMethod.getTraceInfo(), true), 2, 1);
								}
							}
							else if (cmp.ruleinfo.popmode == '6') // 指定减价额
							{
								// 允许折上折则成交价基础上再减价金额,再减不能超过商品价值
								// 不允许折上折则低价优先清空其他折扣,减价金额不能超过商品价值
								if (cmp.ruleinfo.popzsz == 'Y')
								{
									zke = ManipulatePrecision.doubleConvert((poprl.popje * sl), 2, 1);
									if (sg.hjje - (getZZK(sg, CommonMethod.getTraceInfo(), true) + zke) < 0)
										zke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg, CommonMethod.getTraceInfo(), true), 2, 1);
								}
								else if (ManipulatePrecision.doubleCompare(getZZK(sg, CommonMethod.getTraceInfo(), true), poprl.popje * sl, 2) < 0)
								{
									clearGoodsAllRebate(prgd.sgindex);

									zke = ManipulatePrecision.doubleConvert(poprl.popje * sl);
									if (sg.hjje - zke < 0)
										zke = sg.hjje;
									zke = ManipulatePrecision.doubleConvert(zke - getZZK(sg, CommonMethod.getTraceInfo(), true), 2, 1);
								}
							}

							// 记录促销折扣明细
							if (zke > 0)
							{
								// 标记有促销
								havepop = true;

								double oldzke = sg.zszke;
								sg.zszke += zke;
								sg.zszke = getConvertRebate(prgd.sgindex, sg.zszke);
								sg.zsdjbh = String.valueOf(cmp.cmpopseqno);
								zke = sg.zszke - oldzke;
								getZZK(sg, CommonMethod.getTraceInfo(), true);
								addCmPopDetail(prgd.sgindex, cmp, zke);

								markPopGoods(sg, cmp);
							}
						}
					}

					// 累计促销金额
					popje += laderje;

					// 不再执行剩余阶梯,跳出条件阶梯循环
					if (!loopladder)
						break;
				}
			}

			// 检查促销折扣是否超限
			if (cmp.ruleinfo.maxpopje > 0 && popje > cmp.ruleinfo.maxpopje)
				popje = cmp.ruleinfo.maxpopje;
			if (cmp.ruleinfo.popzsz == 'Y' && popje > hjcjj)
				popje = hjcjj;
			if (cmp.ruleinfo.popzsz != 'Y' && popje > hjzje)
				popje = hjzje;

			// 有除外付款方式且初始合计数据满足消费条件,则进行除外付款方式的预付款
			// 然后再次计算除外付款以后是否还满足消费条件
			if (cmp.ruleinfo.payexcp != null && !cmp.ruleinfo.payexcp.trim().equals("") && isPreparePay == payNormal && cmp.ruleinfo.summode != '0' && (popje > 0 || rulegoods.size() > 0) && (!cmp.ruleinfo.payexcp.trim().equalsIgnoreCase("MKTPAYEXCP") || (cmp.ruleinfo.payexcp.trim().equalsIgnoreCase("MKTPAYEXCP") && GlobalInfo.sysPara.mjPaymentRule != null && GlobalInfo.sysPara.mjPaymentRule.length() > 0)))
			{
				// 只有一个促销规则且所有商品都参与该促销,则可自动进行付款分摊,否则人工输入付款方式分摊
				if (rulegoods.size() == saleGoods.size())
					this.needApportionPayment = false;
				else
					this.needApportionPayment = true;

				// 设置除外付款方式,=MKTPAYEXCP表示以门店参数定义受限付款方式为准,分两个变量记以便支持不同促销不同除外付款
				if (!cmp.ruleinfo.payexcp.trim().equalsIgnoreCase("MKTPAYEXCP"))
					payPopPrepareExcp = cmp.ruleinfo.payexcp.trim();
				else
					payPopPrepareExcp = GlobalInfo.sysPara.mjPaymentRule;
				payPopOtherExcp += payPopPrepareExcp + "|";

				// 提示先输入券付款
				if (new MessageBox("本笔交易有需要除券的活动促销,请先输入券付款金额\n\n如果顾客没有券付款,请直接按‘退出’键").verify() != GlobalVar.Exit)
				{
					// 开始预付除外付款方式
					isPreparePay = payPopPrepare;

					// 除外付款必须进行分摊,设置分摊模式
					char temphavePayRule = GlobalInfo.sysPara.havePayRule;
					GlobalInfo.sysPara.havePayRule = 'A';

					// 打开付款窗口
					new SalePayForm().open(saleEvent.saleBS);

					// 还原分摊模式
					GlobalInfo.sysPara.havePayRule = temphavePayRule;

					// 付款完成，开始新交易
					if (this.saleFinish)
					{
						sellFinishComplete();

						// 预先付款就已足够,不再继续后续付款
						doCmPopExit = true;
						return havepop;
					}
				}

				// 进入实付剩余付款方式,只允许非券付款方式进行付款
				isPreparePay = payPopOther;

				// 清除记录的数据
				for (int j = 0; j < rulegoods.size(); j++)
				{
					PopRuleGoods prgd = (PopRuleGoods) rulegoods.elementAt(j);
					Vector popvec = (Vector) goodsCmPop.elementAt(prgd.sgindex);
					if (popvec != null && popvec.size() > prgd.cmindex)
					{
						CmPopGoodsDef cp = (CmPopGoodsDef) popvec.elementAt(prgd.cmindex);
						cp.used = false;
					}
				}
				rulegoods.removeAllElements();

				// 再次计算除外付款以后是否还满足消费条件
				continue;
			}
			else
			{
				// 不再计算除外付款以后是否还满足消费条件
				break;
			}
		}
		while (true);

		// 是折扣形式的促销则先计算折扣金额
		if (cmp.ruleinfo.popmode != '0')
		{
			// 规则中的商品不累计,则每个商品都按促销折扣计算
			// 规则中的商品需累计,则总的促销金额分摊到各商品
			if (cmp.ruleinfo.summode == '0') // 不累计方式的规则的折扣记入yhzke,取消付款才不会被清0
			{
				// 折扣金额
				double zke = 0;

				// 为7时取cmPopGoodsDef中的popmode的设置
				if (cmp.ruleinfo.popmode == '7')
				{
					// 计算是否超过限量
					double sl = saleGoodsDef.sl;
					if (cmp.popmaxmode != '0' && cmp.popmax > 0)
					{
						// 联网检查会员已购买数量
						double maxsl = cmp.popmax;
						if (cmp.popmaxmode != '1')
						{
							if (curCustomer != null)
								maxsl = NetService.getDefault().findVIPMaxSl("POP", curCustomer.code, curCustomer.type, cmp.cmpopseqno, saleGoodsDef.code, saleGoodsDef.gz, saleGoodsDef.uid);
							else
								maxsl = NetService.getDefault().findVIPMaxSl("POP", "", "", cmp.cmpopseqno, saleGoodsDef.code, saleGoodsDef.gz, saleGoodsDef.uid);
							if (maxsl < 0)
								maxsl = 0;
						}

						// 计算本笔交易可销售数量
						double yxsl = 0;
						for (int i = 0; i < saleGoods.size(); i++)
						{
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);
							Vector popvec = (Vector) goodsCmPop.elementAt(i);
							for (int j = 0; popvec != null && j < popvec.size(); j++)
							{
								CmPopGoodsDef cmpoth = (CmPopGoodsDef) popvec.elementAt(j);
								if (i != index && cmpoth.cmpopseqno == cmp.cmpopseqno && cmpoth.used)
								{
									yxsl += sg.sl;
									break;
								}
							}
						}
						if (maxsl - yxsl > 0)
							sl = Math.min(maxsl - yxsl, saleGoodsDef.sl);
						else
							sl = 0;
						if (sl < 0)
							sl = 0;
					}

					// 有足够数量
					if (ManipulatePrecision.doubleCompare(sl, 0, 4) > 0)
					{
						// 拆分商品行
						if (ManipulatePrecision.doubleCompare(saleGoodsDef.sl, sl, 4) > 0)
							SplitSaleGoodsRow(index, sl);

						// 指定促销价用成交价四舍五入以后再减
						if (cmp.popmode == '1' && saleGoodsDef.jg > cmp.poplsj && cmp.poplsj >= 0)
						{
							zke = ManipulatePrecision.doubleConvert(saleGoodsDef.jg * sl) - ManipulatePrecision.doubleConvert(cmp.poplsj * sl);
							saleGoodsDef.yhzke += zke;
							saleGoodsDef.yhzkfd = cmp.poplsjzkfd;

							// 会员价差记入会员折扣
							if (isMemberHyjMode() && cmp.poplsj > cmp.pophyj && cmp.pophyj >= 0)
							{
								double hyzk = ManipulatePrecision.doubleConvert(cmp.poplsj * sl) - ManipulatePrecision.doubleConvert(cmp.pophyj * sl);
								saleGoodsDef.hyzke += hyzk;
								saleGoodsDef.hyzkfd = cmp.pophyjzkfd;
								zke += hyzk;
							}
						}
						else if (cmp.popmode == '2' && 1 > cmp.poplsj && cmp.poplsj >= 0)
						{
							zke = ManipulatePrecision.doubleConvert(ManipulatePrecision.doubleConvert(saleGoodsDef.jg * sl) * (1 - cmp.poplsj));
							saleGoodsDef.yhzke += zke;
							saleGoodsDef.yhzkfd = cmp.poplsjzkfd;

							// 会员价差记入会员折扣
							if (isMemberHyjMode() && cmp.poplsj > cmp.pophyj && cmp.pophyj > 0)
							{
								double hyzk = ManipulatePrecision.doubleConvert(ManipulatePrecision.doubleConvert(saleGoodsDef.jg * sl) * (cmp.poplsj - cmp.pophyj));
								saleGoodsDef.hyzke += hyzk;
								saleGoodsDef.hyzkfd = cmp.pophyjzkfd;
								zke += hyzk;
							}
						}
						else if (cmp.popmode == '3' && saleGoodsDef.jg > cmp.poplsj && cmp.poplsj > 0)
						{
							zke = ManipulatePrecision.doubleConvert(cmp.poplsj * sl);
							saleGoodsDef.yhzke += zke;
							saleGoodsDef.yhzkfd = cmp.poplsjzkfd;

							// 会员价差记入会员折扣
							if (isMemberHyjMode() && cmp.pophyj > cmp.poplsj && cmp.pophyj > 0)
							{
								double hyzk = ManipulatePrecision.doubleConvert(cmp.pophyj * sl) - ManipulatePrecision.doubleConvert(cmp.poplsj * sl);
								saleGoodsDef.hyzke += hyzk;
								saleGoodsDef.hyzkfd = cmp.pophyjzkfd;
								zke += hyzk;
							}
						}
					}
				}
				else
				{
					zke = popje;

					saleGoodsDef.yhzke += popje;
					saleGoodsDef.yhzkfd = cmp.poplsjzkfd;
				}

				if (zke > 0)
				{
					// 标记有促销
					havepop = true;

					// 记录促销序号
					double oldzke = saleGoodsDef.yhzke;
					saleGoodsDef.yhzke = getConvertRebate(index, saleGoodsDef.yhzke);
					zke += (saleGoodsDef.yhzke - oldzke);
					saleGoodsDef.yhdjbh = String.valueOf(cmp.cmpopseqno);

					// 汇总商品总折扣
					getZZK(saleGoodsDef, CommonMethod.getTraceInfo(), true);

					// 记录促销折扣明细
					addCmPopDetail(index, cmp, zke);
				}
			}
			else
			// 累计方式
			{
				// 非折上折检查总折扣额和当前成交价的折扣，取低价优先
				if (popje > 0 && cmp.ruleinfo.popzsz != 'Y')
				{
					if (ManipulatePrecision.doubleCompare(hjzje - hjcjj, popje, 2) < 0)
					{
						// 清除商品的其他折扣,重算累计用于分摊
						hjzje = hjcjj = hjcxsl = hjcxje = 0;
						for (int j = 0; j < rulegoods.size(); j++)
						{
							PopRuleGoods prgd = (PopRuleGoods) rulegoods.elementAt(j);

							// 清除其他折扣
							clearGoodsAllRebate(prgd.sgindex);

							// 非折上折按原价计算累计,因为如果算出的分摊折扣比当前折扣低则当前折扣放弃
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(prgd.sgindex);
							hjzje += sg.hjje;
							hjcjj += sg.hjje - sg.hjzk;
							hjcxsl += sg.sl;
							hjcxje += sg.hjje - getGoodsPaymentApportion(prgd.sgindex, sg);
						}
					}
					else
					{
						// 当前折扣比计算的促销促销折扣低，放弃本次计算的促销折扣
						popje = 0;
					}
				}

				// 将总的促销金额分摊到各个商品
				if (popje > 0)
				{
					// 标记有促销
					havepop = true;

					// 百货或调试模式提示促销规则
						Vector contents = new Vector();
						for (int j = 0; j < rulegoods.size(); j++)
						{
							PopRuleGoods prgd = (PopRuleGoods) rulegoods.elementAt(j);

							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(prgd.sgindex);
							contents.add(new String[] { (prgd.sgindex + 1) + ":" + sg.code, sg.name, ManipulatePrecision.doubleToString(sg.sl, 4, 1, true), ManipulatePrecision.doubleToString(sg.hjje - sg.hjzk), ManipulatePrecision.doubleToString(sg.hjje - sg.hjzk - getGoodsPaymentApportion(prgd.sgindex, sg)) });
						}
						contents.add(new String[] { "合计", "", ManipulatePrecision.doubleToString(hjcxsl, 4, 1, true), ManipulatePrecision.doubleToString(hjcjj), ManipulatePrecision.doubleToString(hjcxje) });
						String[] title = { "商品编码", "商品名称", "数量", "成交价", "活动金额" };
						int[] width = { 130, 200, 60, 115, 115 };
						new MutiSelectForm().open("以下商品参加[" + cmp.ruleinfo.rulename + "]活动,总共可享受 " + ManipulatePrecision.doubleToString(popje) + " 元的促销折扣", title, width, contents, false, 675, 319, 645, 192, false);

					// 组合促销中组内分组折扣占比需要先保存计算折扣前的原始数据,确保分摊折扣后占比不发生变化
					if (cmp.ruleinfo.isonegroup == 'Y')
					{
						for (int j = 0; j < rulegoods.size(); j++)
						{
							PopRuleGoods prgd = (PopRuleGoods) rulegoods.elementAt(j);

							SaleGoodsDef sgk = (SaleGoodsDef) saleGoods.elementAt(prgd.sgindex);
							if (cmp.ruleinfo.popzsz == 'Y')
							{
								sgk.num9 = ManipulatePrecision.doubleConvert(sgk.hjje - sgk.hjzk - getGoodsPaymentApportion(prgd.sgindex, sgk));
							}
							else
							{
								sgk.num9 = ManipulatePrecision.doubleConvert(sgk.hjje - getGoodsPaymentApportion(prgd.sgindex, sgk));
							}
						}
					}

					// 分摊促销折扣
					double ftje = 0;
					for (int j = 0; j < rulegoods.size(); j++)
					{
						PopRuleGoods prgd = (PopRuleGoods) rulegoods.elementAt(j);
						SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(prgd.sgindex);

						// 折扣金额已分摊完
						if (popje - ftje <= 0)
							continue;

						// 把剩余未分摊金额，直接分摊到最后一个商品
						double zke = 0;
						if (j == (rulegoods.size() - 1))
						{
							zke = ManipulatePrecision.doubleConvert(popje - ftje, 2, 1);
						}
						else
						{
							// 计算组合促销中分组折扣占比
							double popzb = 1;
							double cjjzb = 1;
							boolean havegrpzb = false;
							for (int n = 0; groupvec != null && n < groupvec.size(); n++)
							{
								double pop = ((PopRuleGoodsGroup) groupvec.elementAt(n)).popje;
								if (pop > 0)
								{
									havegrpzb = true;
									break;
								}
							}
							if (havegrpzb && cmp.ruleinfo.isonegroup == 'Y')
							{
								for (int n = 0; groupvec != null && n < groupvec.size(); n++)
								{
									double pop = ((PopRuleGoodsGroup) groupvec.elementAt(n)).popje;
									Vector sglist = ((PopRuleGoodsGroup) groupvec.elementAt(n)).goodslist;

									// 检查本商品属于当前分组
									int m = 0;
									for (; m < sglist.size(); m++)
										if (prgd.sgindex == Integer.parseInt((String) sglist.elementAt(m)))
											break;
									if (m >= sglist.size())
										continue;

									// 计算分组商品的合计金额在所有商品的占比
									double hjje = 0, gpje = 0;
									for (int k = 0; k < rulegoods.size(); k++)
									{
										PopRuleGoods prgk = (PopRuleGoods) rulegoods.elementAt(k);
										SaleGoodsDef sgk = (SaleGoodsDef) saleGoods.elementAt(prgk.sgindex);
										hjje += sgk.num9;

										// 属于组内商品
										for (m = 0; m < sglist.size(); m++)
										{
											if (prgk.sgindex == Integer.parseInt((String) sglist.elementAt(m)))
											{
												gpje += sgk.num9;
												break;
											}
										}
									}
									cjjzb = gpje / hjje;

									// 分组折扣占比
									if (cmp.ruleinfo.popmode == '1')
										popzb = (gpje - pop * groupbs) / popje;
									else if (cmp.ruleinfo.popmode == '2')
										popzb = pop;
									else if (cmp.ruleinfo.popmode == '3')
										popzb = (pop * groupbs) / popje;
									break;
								}
							}

							// 计算本行商品可分摊的折扣金额
							if (cmp.ruleinfo.popzsz == 'Y')
							{
								zke = ManipulatePrecision.doubleConvert((sg.hjje - sg.hjzk - getGoodsPaymentApportion(prgd.sgindex, sg)) / (hjcxje * cjjzb) * (popje * popzb), 2, 1);
							}
							else
							{
								zke = ManipulatePrecision.doubleConvert((sg.hjje - getGoodsPaymentApportion(prgd.sgindex, sg)) / (hjcxje * cjjzb) * (popje * popzb), 2, 1);
							}
						}

						if (zke > 0)
						{
							// 折扣后成交价不能小于0
							if (sg.hjje - (getZZK(sg, CommonMethod.getTraceInfo(), true) + zke) < 0)
								zke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg, CommonMethod.getTraceInfo(), true), 2, 1);
							double oldzszke = sg.zszke;
							sg.zszke += zke;
							if (j != (rulegoods.size() - 1)) // 最后一个商品不计算价格精度
							{
								sg.zszke = getConvertRebate(prgd.sgindex, sg.zszke);
								zke = sg.zszke - oldzszke;
							}
							sg.zsdjbh = String.valueOf(cmp.cmpopseqno);
							getZZK(sg, CommonMethod.getTraceInfo(), true);

							// 记录促销折扣明细
							addCmPopDetail(prgd.sgindex, cmp, zke);
						}

						// 计算已分摊的金额
						ftje += zke;
					}
				}
			}
		}

		// 有促销先刷新界面
		if (havepop)
			refreshCmPopUI();

		// 不管什么促销形式都检查是否有相应的赠送结果
		for (int j = 0; j < rulegifts.size(); j++)
		{
			String[] s = ((String) rulegifts.elementAt(j)).split(",");
			if (s.length < 4)
				continue;
			String dqid = s[0];
			String ruleid = s[1];
			String ladderid = s[2];
			int ladderbs = Integer.parseInt(s[3]);
			if (doCmPopGift(index, saleGoodsDef, cmp, dqid, ruleid, ladderid, ladderbs))
			{
				// 标记存在促销
				havepop = true;

				// 将产生促销赠送的条件商品设置促销
				for (int i = 0; i < rulegoods.size(); i++)
				{
					PopRuleGoods prgd = (PopRuleGoods) rulegoods.elementAt(i);
					addCmPopDetail(prgd.sgindex, cmp, 0, true);
				}
			}
		}

		// 未计算到促销还原商品规则的使用标记
		if (!havepop)
		{
			cmp.used = false;
			for (int j = 0; j < rulegoods.size(); j++)
			{
				PopRuleGoods prgd = (PopRuleGoods) rulegoods.elementAt(j);
				if (goodsCmPop != null && goodsCmPop.size() > prgd.sgindex)
				{
					Vector popvec = (Vector) goodsCmPop.elementAt(prgd.sgindex);
					if (popvec != null && popvec.size() > prgd.cmindex)
						((CmPopGoodsDef) popvec.elementAt(prgd.cmindex)).used = false;
				}
			}
		}

		return havepop;
	
	}
}
