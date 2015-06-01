package custom.localize.Bjkl;

import java.util.Vector;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsJFRule;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.Struct.SuperMarketPopRuleDef;

import custom.localize.Bstd.Bstd_SaleBS;

public class Bjkl_SaleBS0CalcPop extends Bstd_SaleBS
{
	// int electcodetype = 0; // 1-13位;2-18位
	// boolean electcodeisrealpop = false;

	Vector ruleReqList = null; // 超市规则促销条件列表
	Vector rulePopList = null; // 超市规则促销结果列表
	public double superMarketRuleyhje; // 超市规则促销优惠金额
	public double quantity; // 当前选正行的商品数量

	public boolean paySellStart()
	{
		if (!super.paySellStart())
			return false; // 不允许进行付款

		// 记录付款前的商品信息
		sortSaleGoodsForPrint();
		doCmPopWriteData();
		doU51CmPop();

		// 处理CRM促销
		doCmPopExit = false;

		return true;
	}

	public void paySellCancel()
	{
		// 恢复到付款前的商品明细状态
		delCmPop();
		super.paySellCancel();
	}

	public void delCmPop()
	{
		delCmPopReadData();
	}

	public void calcGoodsPOPRebate(int index)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		if (this.isHHGoods(saleGoodsDef))
			return;

		// 判断13位,18位促销情况
		if (saleGoodsDef.flag == '2')
		{
			if (goodsDef.num4 == 2)
				return;
		}

		// 促销优惠
		if (goodsDef.poptype != '0')
		{
			// 定价且是单品优惠
			if ((saleGoodsDef.lsj > 0) && ((goodsDef.poptype == '1') || (goodsDef.poptype == '7')))
			{
				// 促销折扣
				if ((saleGoodsDef.lsj > goodsDef.poplsj) && (goodsDef.poplsj > 0))
				{
					saleGoodsDef.yhzke = (saleGoodsDef.lsj - goodsDef.poplsj) * saleGoodsDef.sl;
					saleGoodsDef.yhzkfd = goodsDef.poplsjzkfd;
				}

				// 会员折扣
				if (isMemberHyjMode() && isUseMemberHyj(saleGoodsDef.isvipzk))
				{
					if ((goodsDef.poplsj > goodsDef.pophyj) && (goodsDef.pophyj > 0))
					{
						saleGoodsDef.hyzke = (goodsDef.poplsj - goodsDef.pophyj) * saleGoodsDef.sl;
						saleGoodsDef.hyzkfd = goodsDef.pophyjzkfd;
					}
				}
			}
			else
			{
				// 促销折扣
				if ((1 > goodsDef.poplsjzkl) && (goodsDef.poplsjzkl > 0))
				{
					saleGoodsDef.yhzke = saleGoodsDef.hjje * (1 - goodsDef.poplsjzkl);
					saleGoodsDef.yhzkfd = goodsDef.poplsjzkfd;
				}

				// 会员折扣
				if (isMemberHyjMode() && isUseMemberHyj(saleGoodsDef.isvipzk))
				{
					if ((goodsDef.poplsjzkl > goodsDef.pophyjzkl) && (goodsDef.pophyjzkl > 0))
					{
						saleGoodsDef.hyzke = saleGoodsDef.hjje * (goodsDef.poplsjzkl - goodsDef.pophyjzkl);
						saleGoodsDef.hyzkfd = goodsDef.pophyjzkfd;
					}
				}
			}
		}
		else
		// 非促销优惠
		{
			if (isMemberHyjMode() && isUseMemberHyj(saleGoodsDef.isvipzk))
			{
				// ishy='H'表示采用VIP会员价模式进行会员优惠
				if (saleGoodsDef.lsj >= 0)
				{
					if ((saleGoodsDef.lsj > goodsDef.hyj) && (goodsDef.hyj > 0))
					{
						saleGoodsDef.hyzke = (saleGoodsDef.lsj - goodsDef.hyj) * saleGoodsDef.sl;
						saleGoodsDef.hyzkfd = goodsDef.hyjzkfd;
					}
				}
				else
				{
					if ((1 > goodsDef.hyj) && (goodsDef.hyj > 0))
					{
						saleGoodsDef.hyzke = saleGoodsDef.hjje * (1 - goodsDef.hyj);
						saleGoodsDef.hyzkfd = goodsDef.hyjzkfd;
					}
				}
			}
		}
	}

	public void sortSaleGoodsForPrint()
	{
		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);
			sgd.num5 = i;
		}
	}

	public boolean doU51CmPop()
	{
		if (!SellType.ISSALE(saletype)) { return false; }

		if (SellType.NOPOP(saletype)) { return false; }

		if (SellType.ISEARNEST(saletype)) { return false; }

		if (SellType.ISPREPARETAKE(saletype)) { return false; }

		// 初始化超市促销标志
		for (int i = 0; i < saleGoods.size(); i++)
			((SaleGoodsDef) saleGoods.get(i)).isSMPopCalced = 'Y';

		// 排序
		sortSalegoods();

		// 查找规则
		SuperMarketPopRuleDef ruleDef = null;
		Vector notRuleDjbh = new Vector();
		int calcCount = saleGoods.size();
		int k, j, l, m, n;
		double zje, je, t_zje;
		double or_yhsl = 0;// 结果为OR关系的时候,存放第一个结果的数量
		long bs, minbs, t_minbs;

		String cardNo = "";
		if (curCustomer != null)
		{
			cardNo = curCustomer.code;
		}

		SaleGoodsDef saleGoodsDef = null;
		GoodsDef goodsDef = null;
		// SpareInfoDef spareInfoDef = null;
		for (int rulepos = 0; rulepos < calcCount + 1; rulepos++)
		{
			// 查找单品优惠单号
			if (rulepos != calcCount)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.get(rulepos);
				goodsDef = (GoodsDef) goodsAssistant.get(rulepos);
				// spareInfoDef = (SpareInfoDef) goodsSpare.get(i);
				// 只有普通商品才能参与
				if (saleGoodsDef.flag != '2' && saleGoodsDef.flag != '4')
					continue;
				// 判断是否曾参与规则促销
				if (saleGoodsDef.isSMPopCalced == 'N')
					continue;
				// 标准百货crm促销，跳过
				// if (goodsDef.str4.equals("Y")) continue;

				// 已经查过的商品无需重复查规则促销
				for (k = 0; k < rulepos; k++)
				{
					if (goodsDef.code.equals(((GoodsDef) goodsAssistant.get(k)).code) && goodsDef.gz.equals(((GoodsDef) goodsAssistant.get(k)).gz) && goodsDef.uid.equals(((GoodsDef) goodsAssistant.get(k)).uid))
						break;
				}
				if (k < rulepos)
					continue;

			}
			// 超找整单超市促销单号
			else
			{
				goodsDef = new GoodsDef();
				goodsDef.code = "ALL";
				goodsDef.gz = ManipulatePrecision.doubleToString(saleHead.ysje);
				goodsDef.catid = "";
				goodsDef.ppcode = "";
				goodsDef.uid = "";
			}

			// 首先查找超市规则促销单号
			ruleDef = new SuperMarketPopRuleDef();
			Bjkl_DataService bjkl_DataService = new Bjkl_DataService();
			if (!bjkl_DataService.findSuperMarketPopBillNo(ruleDef, goodsDef.code, goodsDef.gz, goodsDef.catid, goodsDef.ppcode, goodsDef.uid, "", saleHead.rqsj, cardNo))
			{
				continue;
			}

			System.out.println("商品：" + goodsDef.code + " 对应规则单号：" + ruleDef.djbh);

			// 检查该单据是否已经运算过，如果已经运行过则无需重复运算
			for (k = 0; k < notRuleDjbh.size(); k++)
			{
				if (((String) notRuleDjbh.get(k)).equals(ruleDef.djbh))
					break;
			}
			if (k < notRuleDjbh.size())
				continue;

			// 查找超市促销规则明细
			ruleReqList = new Vector();
			rulePopList = new Vector();
			if (!bjkl_DataService.findSuperMarketPopRule(ruleReqList, rulePopList, ruleDef) || ruleReqList.size() == 0 || rulePopList.size() == 0)
			{
				continue;
			}

			// 初始化条件参数
			for (k = 0; k < saleGoods.size(); k++)
			{
				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
				// 标志为A表示在上一轮规则中被条件排除的商品，因此可以参与本轮规则促销
				sg.isPopExc = ' ';
				if (sg.isSMPopCalced == 'A')
					((SaleGoodsDef) saleGoods.get(k)).isSMPopCalced = 'Y';
			}
			// 1.汇总哪些商品是符合条件的
			for (j = 0; j < ruleReqList.size(); j++)
			{
				for (k = 0; k < saleGoods.size(); k++)
				{
					// 商品是否条件匹配
					if (isMatchCommod((SuperMarketPopRuleDef) ruleReqList.get(j), k))
					{
						((SaleGoodsDef) saleGoods.get(k)).isPopExc = 'Y';// 表示条件满足
					}
				}
			}

			// 先将规则条件中要排除的商品排除掉
			for (j = 0; j < ruleReqList.size(); j++)
			{
				// 2.根据 presentsl为1来判断是否需要执行条件排除的动作。
				if (((SuperMarketPopRuleDef) ruleReqList.get(j)).presentsl == 1)
				{
					for (k = 0; k < saleGoods.size(); k++)
					{
						SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
						// 商品是否条件匹配
						if (sg.isPopExc == 'Y' && isMatchCommod((SuperMarketPopRuleDef) ruleReqList.get(j), k))
						{
							// 将排除商品的标志置为N,表示不参与规则促销
							if (((SuperMarketPopRuleDef) ruleReqList.get(j)).istjn.charAt(0) == '0') // 排除条件
								sg.isPopExc = ' ';
							else if (((SuperMarketPopRuleDef) ruleReqList.get(j)).istjn.charAt(0) == '1') // 排除结果
								sg.isPopExc = 'N';
							else
							// 条件和结果都排除
							{
								sg.isPopExc = ' ';
								sg.isSMPopCalced = 'A';
							}
						}
					}
					// 如果是排除条件。那么无论结果是排除条件还是排除结果都不纳入条件计算
					// 例如,单据是一行类别的条件和一行排除结果的条件，如果此处不删除排除结果的那一行数据，
					// 并且输入的商品中没有买这个排除结果的商品，会导致后面计算AND条件的时候算出倍数为0的情况。
					// 如果是排除条件，则需将条件从条件列表中删除，这样是为了后面算多级
					ruleReqList.remove(j);
					j--;
				}
			}

			minbs = 0;
			zje = 0;

			for (n = 0; n < ((SuperMarketPopRuleDef) ruleReqList.get(0)).jc; n++)
			{
				t_minbs = 0;
				t_zje = 0;

				// 得到当前级次
				getCurRuleJc(n + 1);

				// 匹配规则条件中属于必须满足的条件
				for (l = 0, j = 0; j < ruleReqList.size(); j++)
				{
					SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef) ruleReqList.get(j);
					// 条件为AND
					if (ruleReq.presentjs == 1)
					{
						l++;
						je = 0;
						for (k = 0; k < saleGoods.size(); k++)
						{
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
							// 商品是否条件匹配
							if ((sg.isPopExc == 'Y' || sg.isPopExc == 'N') && isMatchCommod((SuperMarketPopRuleDef) ruleReqList.get(j), k))
							{
								// yhhyj = 0，表示yhlsj中记录的是数量
								// yhhyj = 1，表示yhlsj中记录的是金额
								if (ruleReq.yhhyj == 0)
									je += sg.sl;
								else
									je += sg.hjje - sg.yhzke - sg.hyzke - sg.plzke;
								// 此处如果该变cominfo[k].infostr1[5] =
								// 'A',在计算第2级别的时候,程序不能进入上面的IF条件进行统计，导致无法计算一级以上的级别
								// 避免后面的or判断时又找到该条件
							}
						}

						bs = 0;
						if (ManipulatePrecision.doubleCompare(je, ruleReq.yhlsj, 2) >= 0 && ManipulatePrecision.doubleCompare(ruleReq.yhlsj, 0, 2) >= 0)
						{
							bs = new Double(je / ruleReq.yhlsj).longValue();
						}
						if (l == 1)
							t_minbs = bs;
						else
							t_minbs = t_minbs > bs ? bs : t_minbs;

						t_zje += je;
					}
				}
				// 有必须全满足的条件，并且未全满足时，则认为条件不满足
				if (l > 0 && t_minbs <= 0)
				{
					// 还原上一级
					if (n > 0)
						getCurRuleJc(n);
					break;
				}
				// 匹配规则条件中属于非必须满足的条件
				for (je = 0, m = -1, j = 0; j < ruleReqList.size(); j++)
				{
					SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef) ruleReqList.get(j);
					// 条件为OR
					if (ruleReq.presentjs == 0)
					{
						m = j;
						for (k = 0; k < saleGoods.size(); k++)
						{
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
							// 商品是否条件匹配
							if ((sg.isPopExc == 'Y' || sg.isPopExc == 'N') && isMatchCommod(ruleReq, k))
							{
								// yhhyj = 0，表示yhlsj中记录的是数量
								// yhhyj = 1，表示yhlsj中记录的是金额
								if (ManipulatePrecision.doubleCompare(ruleReq.yhhyj, 0, 2) == 0)
									je += sg.sl;
								else
									je += sg.hjje - sg.yhzke - sg.hyzke - sg.plzke;
							}
						}
					}
				}
				t_zje += je;

				// 计算or条件的倍数

				if (m >= 0)
				{
					SuperMarketPopRuleDef ruleReqM = (SuperMarketPopRuleDef) ruleReqList.get(m);
					if (ManipulatePrecision.doubleCompare(je, ruleReqM.yhlsj, 2) >= 0 && ManipulatePrecision.doubleCompare(ruleReqM.yhlsj, 0, 2) > 0)
					{
						bs = new Double(je / ruleReqM.yhlsj).longValue();
						if (l > 0)
							t_minbs = t_minbs > bs ? bs : t_minbs;
						else
							t_minbs = bs;
					}
				}
				if (t_minbs > 0)
				{
					minbs = t_minbs;
					zje = t_zje;
				}
				else
				{
					// 还原上一级
					if (n > 0)
						getCurRuleJc(n);
					break;
				}
			}

			// 有必须全满足的条件，并且未全满足时，则认为条件不满足
			if (minbs <= 0)
			{
				// 记录下不匹配的单据号，以便后面的商品再找到该单据时不用再次进行匹配运算
				notRuleDjbh.add(ruleDef.djbh);
				continue;
			}
			else
			{
			}
			// ppistr6中的第1个字符为1时，表示1倍封顶
			if (((SuperMarketPopRuleDef) ruleReqList.get(0)).ppistr5.charAt(0) == '1')
				minbs = 1;

			double curmjzje = 0;
			// 计算促销的结果
			for (j = 0; j < rulePopList.size(); j++)
			{
				SuperMarketPopRuleDef rulePop = (SuperMarketPopRuleDef) rulePopList.get(j);
				// 商品优惠 商品优惠对应 一级商品 也对应多级的商品
				if (rulePop.yhdjlb == 'G')
				{
					double mjje = ManipulatePrecision.doubleConvert(minbs * rulePop.yhlsj, 2, 0);

					double comzje = 0;
					double t_zyhje = 0;

					// 减的金额必须>0
					if (ManipulatePrecision.doubleCompare(mjje, 0, 2) > 0)
					{
						// 统计当前满减总金额
						curmjzje += mjje;
						// sprintf(salehead.thsq,"%-.2f",curmjzje);

						// 统计参与促销的商品的总优惠金额，fys 按应收金额进行分摊，因为应收为0时，不应该分摊减的金额
						for (l = 0; l < ruleReqList.size(); l++)
						{
							// presentsl为1表示该条件是排除的。
							// if (rulepoplist[l].presentsl == 0) t_zyhje = 0;
							// //这行代码是错的，会导致已经参与优惠(分期,规则非整单)的商品金额的优惠金额的统计

							for (k = 0; k < saleGoods.size(); k++)
							{
								// if (cominfo[k].infostr1[1] == 'Y' &&
								// IsMatchCommod(&rulepoplist[l],&salecom[k]) &&
								// cominfo[k].infostr1[10] != 'Y' )
								// 这里排除条件的应该参与结果计算
								// if (cominfo[k].infostr1[5] != ' ' &&
								// IsMatchCommod(&rulepoplist[l],k) &&
								// !(cominfo[k].infostr1[5] == 'N' ||
								// cominfo[k].infostr1[1] == 'A')/* &&
								// cominfo[k].infostr1[10] != 'Y'*/)
								SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
								if (isMatchCommod((SuperMarketPopRuleDef) ruleReqList.get(l), k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A')/*
																																					 * &&
																																					 * cominfo
																																					 * [
																																					 * k
																																					 * ]
																																					 * .
																																					 * infostr1
																																					 * [
																																					 * 10
																																					 * ]
																																					 * !=
																																					 * 'Y'
																																					 */)
								{
									// 是否折上折
									if (rulePop.iszsz.charAt(0) == '1')// 是
									{
										comzje += sg.hjje - getZZK(sg);
										t_zyhje += sg.yhzke + sg.hyzke + sg.plzke + sg.rulezke;
									}
									else
									// 否
									{
										comzje += sg.hjje - getZZK(sg);

										if (ruleDef.type == '8')
										{
											t_zyhje += sg.yhzke + sg.hyzke + sg.plzke + sg.rulezke;
										}
										else
										{
											t_zyhje += sg.yhzke + sg.hyzke + sg.plzke;
										}
									}
									// 参与满减促销的标志
									// cominfo[k].infostr1[5] = 'Y';
								}
							}
						}

						int t_maxjerow = -1;
						double t_je = 0;

						// 将减的金额分摊到商品明细上
						for (l = 0; l < ruleReqList.size(); l++)
						{
							for (k = 0; k < saleGoods.size(); k++)
							{
								SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
								if (isMatchCommod((SuperMarketPopRuleDef) ruleReqList.get(l), k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A')/*
																																					 * &&
																																					 * cominfo
																																					 * [
																																					 * k
																																					 * ]
																																					 * .
																																					 * infostr1
																																					 * [
																																					 * 10
																																					 * ]
																																					 * !=
																																					 * 'Y'
																																					 */)
								{
									double yhzke = 0;
									double misszke = 0;

									if (ruleDef.type == '8')
										misszke = sg.yhzke + sg.hyzke + sg.plzke + sg.rulezke;// 记录可能被清零的折扣额
									else
										misszke = sg.yhzke + sg.hyzke + sg.plzke;// 记录可能被清零的折扣额

									// 是否折上折
									if (rulePop.iszsz.charAt(0) == '1')
									{
										yhzke = ManipulatePrecision.doubleConvert((sg.hjje - getZZK(sg)) / comzje * mjje);

										if (ruleDef.type == '8')
										{
											sg.mjzke = 0;
											sg.mjzke = yhzke;
											// /salecom[k].comstr1[0] =
											// 'Y';//是否参与整单满减
											calcComZkxe(k, sg.mjzke);

											sg.mjdjbh = rulePop.djbh;
											sg.mjzkfd = rulePop.zkfd;
											superMarketRuleyhje += sg.mjzke;// 统计规则促销的折扣金额
										}
										else
										{
											sg.rulezke = 0;
											sg.rulezke = yhzke;
											// salecom[k].comstr2[0] =
											// 'Y';//是否参与类别满减
											// salecom[k].comstr3[0] =
											// 'N';//表示为参与统计
											// sprintf(salecom[k].comstr2+1,"满%G减%G",rulepoplist[0].yhlsj,rulePop.yhlsj);
											sg.mjdjbh = rulePop.djbh;// 记录下当前优惠的单号
											sg.mjcode = rulePop.code;// 记录下优惠编码
											calcComZkxe(k, sg.rulezke);

											sg.ruledjbh = rulePop.djbh;
											sg.rulezkfd = rulePop.zkfd;
											superMarketRuleyhje += sg.rulezke;// 统计规则促销的折扣金额
										}
										t_je += yhzke;

										// 将参与优惠的商品打上标记
										sg.isPopExc = 'Y';

										// char buf[100];
										// sprintf(buf,"yhzke:%f,rulezke:%f,t_je:%f",yhzke,salecom[k].rulezke,t_je);
										// MessageBox(buf);
									}
									else
									{
										// 比较满减的金额是否比一般促销的金额更低
										if (ManipulatePrecision.doubleCompare(mjje, t_zyhje, 2) > 0)
										{
											yhzke = ManipulatePrecision.doubleConvert((sg.hjje - getZZK(sg)) / comzje * mjje);

											// 如果是对应的整单满减
											if (ruleDef.type == '8')
											{
												sg.mjzke = 0;
												sg.yhzke = 0;
												sg.hyzke = 0;
												sg.plzke = 0;
												sg.yhzkfd = 0;
												sg.spzkfd = 0;
												sg.rulezke = 0;
												sg.rulezkfd = 0;
												// memset(sg.ruledjbh,0,sizeof(salecom[k].ruledjbh));
												// memset(salecom[k].yhdjbh,0,sizeof(salecom[k].yhdjbh));

												sg.mjzke = yhzke; // 整单满减
												// salecom[k].comstr2[1] =
												// 'Y';//是否参与整单满减
												// char buf[100];
												// sprintf(buf,"mjzke :%f",salecom[k].mjzke);
												// MessageBox(buf);

												calcComZkxe(k, sg.mjzke);

												sg.mjdjbh = rulePop.djbh;
												sg.mjzkfd = rulePop.zkfd;
												superMarketRuleyhje += sg.mjzke - misszke;// 统计规则促销的折扣金额
											}
											else
											// 对应商品满减
											{
												sg.rulezke = 0;
												sg.yhzke = 0;
												sg.hyzke = 0;
												sg.plzke = 0;
												sg.yhzkfd = 0;
												sg.spzkfd = 0;
												// memset(salecom[k].yhdjbh,0,sizeof(salecom[k].yhdjbh));

												sg.rulezke = yhzke; // 单品满减
												// salecom[k].comstr2[0] =
												// 'Y';//是否参与类别满减
												// salecom[k].comstr3[0] =
												// 'N';//表示为参与统计
												// sprintf(salecom[k].comstr2+1,"满%G减%G",rulepoplist[0].yhlsj,rulePop.yhlsj);
												// salecom[k].comstr3+1,rulemxlist[0].djbh);//记录下当前优惠的单号
												// strcpy(salecom[k].comstr4,rulemxlist[0].code);//记录下优惠编码
												// char buf[100];
												// sprintf(buf,"rulezke :%f",salecom[k].rulezke);
												// MessageBox(buf);

												calcComZkxe(k, sg.rulezke);

												sg.ruledjbh = rulePop.djbh;
												sg.rulezkfd = rulePop.zkfd;

												superMarketRuleyhje += sg.rulezke - misszke;// 统计规则促销的折扣金额
											}
											t_je += yhzke;

											// strcpy(salecom[k].comstr3+1,rulemxlist[0].djbh);//记录下当前优惠的单号
											// 参与满减促销的标志
											sg.isPopExc = 'Y';;
										}
										else
										{
											// MessageBox("比较满减的金额是否比一般促销的金额更低");

											// 此处要还原，否则在没有参与满减的情况下也会打印出满减金额
											curmjzje -= mjje;
											saleHead.thsq = String.valueOf(curmjzje); // sprintf(salehead.thsq,"%-.2f",curmjzje);

											t_je = mjje;
											// 参与满减促销的标志
											sg.isPopExc = ' ';

											// char buf[100];
											// sprintf(buf,"t_zyhje:%G,mjje:%G,yhe:%G,i:%d,k:%d,old_salecom_num:%d",t_zyhje,mjje,salecom[k].yhzke
											// + salecom[k].hyzke +
											// salecom[k].plzke,i,k,old_salecom_num);
											// MessageBox(buf);

											// 当前规则有促销金额，则将有促销的商品排除掉
											// if (FloatCmp(t_zyhje,0,2) > 0 &&
											// i < old_salecom_num)
											if (ManipulatePrecision.doubleCompare(t_zyhje, 0, 2) > 0 && rulepos < calcCount)
											{
												// sprintf(buf,"t_zyhje:%G,mjje:%G,yhe:%G,i:%d,k:%d,old_salecom_num:%d",t_zyhje,mjje,salecom[k].yhzke
												// + salecom[k].hyzke +
												// salecom[k].plzke,i,k,old_salecom_num);
												// MessageBox(buf,"N");

												// if (i >= 0) i--;
												if (rulepos >= 0)
													rulepos--;
												if (ManipulatePrecision.doubleCompare(sg.yhzke + sg.hyzke + sg.plzke, 0, 2) > 0)
												{
													// sprintf(buf,"t_zyhje:%G,mjje:%G,yhe:%G,i:%d,k:%d,old_salecom_num:%d",t_zyhje,mjje,salecom[k].yhzke
													// + salecom[k].hyzke +
													// salecom[k].plzke,i,k,old_salecom_num);
													// MessageBox(buf,"Y");
													// 参与满减促销的标志
													sg.isPopExc = 'Y';
												}
											}
										}
									}
									// char temp[100];
									// sprintf(temp,"yhzke : %f,t_je:%f",salecom[k].yhzke,t_je);
									// MessageBox(temp);

									// 记录满减的规则
									if (ManipulatePrecision.doubleCompare(sg.mjzke, 0, 2) > 0)
									{
										// sprintf(salecom[k].comstr1+1,"满%G减%G",rulepoplist[0].yhlsj,rulePop.yhlsj);
									}

									// if (k >= winfirst && k < winfirst +
									// PAGE_SALE_COM - 1) DispOneSaleCommod(k -
									// winfirst,k);
									// 显示汇总信息
									// DispPay();
									// 记下金额最大的行号
									if (t_maxjerow >= 0 && ManipulatePrecision.doubleCompare(sg.hjje, ((SaleGoodsDef) saleGoods.get(t_maxjerow)).hjje, 2) > 0 || t_maxjerow < 0)
										t_maxjerow = k;
								}
							}
						}
						// 未分配完的金额分配到金额最大的商品上
						if (ManipulatePrecision.doubleCompare(Math.abs(mjje - t_je), 0, 2) > 0)
						{
							if (ruleDef.type == '8')
							{
								((SaleGoodsDef) saleGoods.get(t_maxjerow)).mjzke += mjje - t_je;
								calcComZkxe(t_maxjerow, ((SaleGoodsDef) saleGoods.get(t_maxjerow)).mjzke);
							}
							else
							{
								((SaleGoodsDef) saleGoods.get(t_maxjerow)).rulezke += mjje - t_je;
								calcComZkxe(t_maxjerow, ((SaleGoodsDef) saleGoods.get(t_maxjerow)).rulezke);
							}
							superMarketRuleyhje += mjje - t_je;

							// if (t_maxjerow >= winfirst && t_maxjerow <
							// winfirst + PAGE_SALE_COM - 1)
							// DispOneSaleCommod(t_maxjerow -
							// winfirst,t_maxjerow);

							// 显示汇总信息
							// DispPay();
						}
					}
					// 满减只算一条结果
					break;
				}
				if (rulePop.yhdjlb == 'C')
				{
					// 返现金时，如果有多级，则按最高级算返现金额，不算倍数
					// if (rulePop.yhdjlb.jc > 1) minbs = 1;
					double fxje = ManipulatePrecision.doubleConvert(minbs * rulePop.yhhyj);
					// memset(&curcom,0,sizeof(CommodDef));
					// 不然会添加成上一个商品的编码
					SaleGoodsDef sg = new SaleGoodsDef();
					sg.barcode = "911";
					sg.code = "911";

					// /strcpy(curbarcode,"911");
					// strcpy(curcom.barcode,"911");
					// strcpy(curcom.code,"911");
					sg.type = '1';
					// 根据徐艳春要求返现金的柜组记为：门店号+01
					sg.gz = GlobalInfo.sysPara.mktcode + "01";
					// strcpy(curcom.gz,"0");
					sg.catid = "0";
					sg.ppcode = "0";
					sg.uid = "0";
					sg.yhdjbh = "0";
					// if (IsZBCQ())
					// sprintf(curcom.name,"本次可返现金券%G元",fxje);
					// else
					sg.name = "本次可返折扣券" + fxje + "元";
					sg.unit = "元";
					sg.bzhl = 1;
					sg.xxtax = 0;
					// sg.zkfd = 0;
					sg.isvipzk = 0;
					sg.lsj = 0;

					/*
					 * //检查商品个数
					 * if (salecom_num >= MAX_SALECOM)
					 * {
					 * NotRuleDjbh.FreeAll();
					 * // delete[] rulecom;
					 * return;
					 * }
					 */
					quantity = minbs * rulePop.yhlsj;

					// 赠品数量不能为零，因为数量为零会造成小票不能送网
					if (quantity <= 0)
						quantity = 1;

					double price = 0;
					double allprice = 0;
					sg.yhdjbh = rulePop.djbh;

					sg.flag = '1';// 赠品标志
					sg.batch = rulePop.ppistr6;

					addSaleGoodsObject(sg, null, getGoodsSpareInfo(goodsDef, saleGoodsDef));
					// addSaleGoods()
					// AddSaleCommod();

					// DispSaleCommod();

					// 返现金的赠品
					sg.xxtax = (0 - fxje);
					// salecom[salecom_num - 1].xxtax = 0 - fxje;

					// CreateInputLine(2);
				}
				if (rulePop.yhdjlb == 'A' || rulePop.yhdjlb == 'E')
				{
					double yhsl = minbs * rulePop.yhhyj;// 优惠数量
					// double yhje = ManipulatePrecision.doubleConvert(minbs *
					// rulePop.yhlsj, 2, 0);

					// 结果为OR关系的时候 按照第一个结果的优惠数量来优惠剩余商品
					if (rulePop.presentjs == 0)
					{
						if (j == 0)
							or_yhsl = yhsl;
						else
							yhsl = or_yhsl;
					}

					for (k = 0; k < saleGoods.size() && ManipulatePrecision.doubleCompare(yhsl, 0, 4) > 0; k++)
					{
						SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
						// 商品是否结果匹配 排除结果
						if (isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
						{
							// 商品拆分
							if (ManipulatePrecision.doubleCompare(sg.sl, yhsl, 4) <= 0)
							{
								yhsl -= sg.sl;
								or_yhsl -= sg.sl;
							}
							else
							{
								// 拆分商品行
								splitSalecommod(k, yhsl);
								yhsl = 0;
								or_yhsl = 0;
							}
							double misszke = 0;
							// 整单满减
							if (ruleDef.type == '8')
							{
								misszke = sg.yhzke + sg.hyzke + sg.plzke + sg.rulezke;// 记录可能被清零的折扣额
							}
							else
							{
								misszke = sg.yhzke + sg.hyzke + sg.plzke;// 记录可能被清零的折扣额
							}
							// 取价方式判断
							if (rulePop.presentjg == 0)// 取价方式 取价格
							{
								// 如果是折上折，那么折后金额 = 一般优惠后的金额 * 现在的规则定价 /商品本身的价格
								if (rulePop.iszsz.charAt(0) == '1')
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0) - ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);
										sg.mjzke = calcComZkxe(k, sg.mjzke);

										sg.mjdjbh = rulePop.djbh;
										sg.mjzkfd = rulePop.zkfd;
										// 统计当前规则促销的折扣金额
										superMarketRuleyhje += sg.mjzke;
									}
									else
									{
										sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0) - ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);
										sg.rulezke = calcComZkxe(k, sg.rulezke);

										sg.ruledjbh = rulePop.djbh;
										sg.rulezkfd = rulePop.zkfd;
										// 统计当前规则促销的折扣金额
										superMarketRuleyhje += sg.rulezke;
									}
								}
								else
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + sg.yhzke + sg.hyzke + sg.plzke + sg.rulezke, 2, 0) - ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);
										if (sg.mjzke > misszke)// 如果规则优惠大于一般优惠，一般优惠清零
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd = 0;
											sg.yhzkfd = 0;
											sg.rulezke = 0;
											sg.rulezkfd = 0;
											sg.yhdjbh = "";
											sg.ruledjbh = "";
											sg.mjdjbh = rulePop.djbh;
											sg.mjzke = calcComZkxe(k, sg.mjzke);
											sg.mjzkfd = rulePop.zkfd;
											// 统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.mjzke - misszke;
										}
										else
										{
											sg.mjzke = 0;
										}
									}
									else
									{
										sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + sg.yhzke + sg.plzke + sg.hyzke, 2, 0) - ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);

										if (sg.rulezke > misszke)// 如果规则优惠大于一般优惠，一般优惠清零
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd = 0;
											sg.yhzkfd = 0;
											sg.yhdjbh = "";
											sg.ruledjbh = rulePop.djbh;
											sg.rulezke = calcComZkxe(k, sg.rulezke);
											sg.rulezkfd = rulePop.zkfd;
											// 统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.rulezke - misszke;
										}
										else
										{
											sg.rulezke = 0;
										}
									}
								}
							}
							else if (rulePop.presentjg == 1)// 取折扣率
							{
								if (rulePop.iszsz.charAt(0) == '1')
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = 0;
										sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0) - ManipulatePrecision.doubleConvert((sg.hjje - getZZK(sg)) * rulePop.yhlsj, 2, 0);
										sg.mjzke = calcComZkxe(k, sg.mjzke);

										sg.mjdjbh = rulePop.djbh;
										sg.mjzkfd = rulePop.zkfd;
										// 统计当前规则促销的折扣金额
										superMarketRuleyhje += sg.mjzke;
									}
									else
									{
										sg.rulezke = 0;
										sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0) - ManipulatePrecision.doubleConvert((sg.hjje - getZZK(sg)) * rulePop.yhlsj, 2, 0);
										sg.rulezke = calcComZkxe(k, sg.rulezke);
										sg.ruledjbh = rulePop.djbh;
										sg.rulezkfd = rulePop.zkfd;
										// 统计当前规则促销的折扣金额
										superMarketRuleyhje += sg.rulezke;
									}
								}
								else
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = 0;
										sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + misszke, 2, 0) - ManipulatePrecision.doubleConvert((sg.hjje - getZZK(sg) + misszke) * rulePop.yhlsj, 2, 0);

										if (sg.mjzke > misszke)// 如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的差额
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd = 0;
											sg.yhzkfd = 0;// 清除普通优惠和会员优惠
											sg.rulezke = 0;
											sg.rulezkfd = 0;
											sg.yhdjbh = "";
											sg.ruledjbh = "";
											sg.mjzke = calcComZkxe(k, sg.mjzke);
											sg.mjdjbh = rulePop.djbh;
											sg.mjzkfd = rulePop.zkfd;
											// 统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.mjzke - misszke;
										}
										else
										{
											sg.mjzke = 0;
										}
									}
									else
									{
										sg.rulezke = 0;
										sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + misszke, 2, 0) - ManipulatePrecision.doubleConvert((sg.hjje - getZZK(sg) + misszke) * rulePop.yhlsj, 2, 0);

										if (sg.rulezke > misszke)// 如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的额
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd = 0;
											sg.yhzkfd = 0;// 清除普通优惠和会员优惠
											sg.yhdjbh = "";
											sg.rulezke = calcComZkxe(k, sg.rulezke);
											sg.ruledjbh = rulePop.djbh;
											sg.rulezkfd = rulePop.zkfd;
											// 统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.rulezke - misszke;
										}
										else
										{
											sg.rulezke = 0;
										}
									}
								}
							}
							else if (rulePop.presentjg == 2)// 取折扣额
							{
								if (rulePop.iszsz.charAt(0) == '1')
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = rulePop.yhlsj;
										sg.mjzke = calcComZkxe(k, sg.mjzke);

										sg.mjdjbh = rulePop.djbh;
										sg.mjzkfd = rulePop.zkfd;
										superMarketRuleyhje += sg.mjzke;// 统计规则促销的折扣金额
									}
									else
									{
										sg.rulezke = rulePop.yhlsj;
										sg.rulezke = calcComZkxe(k, sg.rulezke);

										sg.ruledjbh = rulePop.djbh;
										sg.rulezkfd = rulePop.zkfd;
										superMarketRuleyhje += sg.rulezke;// 统计规则促销的折扣金额
									}
								}
								else
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = rulePop.yhlsj;

										if (sg.mjzke > misszke)// 如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的差额
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd = 0;
											sg.yhzkfd = 0;// 清除普通优惠和会员优惠
											sg.rulezke = 0;
											sg.rulezkfd = 0;

											sg.ruledjbh = "";
											sg.yhdjbh = "";
											sg.mjzke = calcComZkxe(k, sg.mjzke);
											sg.mjdjbh = rulePop.djbh;
											sg.mjzkfd = rulePop.zkfd;
											superMarketRuleyhje += sg.mjzke - misszke;// 统计规则促销的折扣金额
										}
										else
										{
											sg.mjzke = 0;
										}
									}
									else
									{
										sg.rulezke = rulePop.yhlsj;

										if (sg.rulezke > misszke)// 如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的差额
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd = 0;
											sg.yhzkfd = 0;// 清除普通优惠和会员优惠
											sg.yhdjbh = "";
											sg.rulezke = calcComZkxe(k, sg.rulezke);
											sg.ruledjbh = rulePop.djbh;
											sg.rulezkfd = rulePop.zkfd;
											superMarketRuleyhje += sg.rulezke - misszke;// 统计规则促销的折扣金额
										}
										else
										{
											sg.rulezke = 0;
										}
									}
								}
							}
							else
							// 用于其它用途
							{
							}
							sg.isPopExc = 'Y';
						}
					}
				}
				// 赠品
				if (rulePop.yhdjlb == 'B' || rulePop.yhdjlb == 'F')
				{
					// '4'表示买赠，该赠品是小票列表中的正常商品，要将其改成正赠品
					if (rulePop.ppistr6.charAt(0) == '4')
					{
						// 赠品数量
						quantity = minbs * rulePop.yhlsj;

						for (k = 0; k < saleGoods.size() && ManipulatePrecision.doubleCompare(quantity, 0, 4) > 0; k++)
						{
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
							// 商品是否结果匹配 排除结果
							if (isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
							{
								// 商品拆分
								if (ManipulatePrecision.doubleCompare(sg.sl, quantity, 4) <= 0)
								{
									quantity -= sg.sl;
								}
								else
								{
									// 拆分商品行
									splitSalecommod(k, quantity);
									quantity = 0;
								}
								// 将该商品改为赠品
								sg.flag = '1';
								sg.batch = rulePop.ppistr6;

								sg.xxtax = ManipulatePrecision.doubleConvert(minbs * rulePop.yhhyj, 2, 0);
								sg.rulezke += sg.hjje - getZZK(sg) - ManipulatePrecision.doubleConvert(minbs * rulePop.yhhyj, 2, 0);

								sg.ruledjbh = rulePop.djbh; // 记录优惠单据编号
								sg.rulezkfd = rulePop.zkfd;

								// 该商品的应收金额都记为优惠金额
								superMarketRuleyhje += sg.zszke;
							}
						}
					}
				}
				// 任意几个定应收金额 只分多级
				if (rulePop.yhdjlb == 'X')
				{
					if (ManipulatePrecision.doubleCompare(zje, ((SuperMarketPopRuleDef) ruleReqList.get(0)).yhlsj, 2) >= 0 && ManipulatePrecision.doubleCompare(((SuperMarketPopRuleDef) ruleReqList.get(0)).yhlsj, 0, 2) > 0)
					{
						double yhje = ManipulatePrecision.doubleConvert(minbs * rulePop.yhlsj, 2, 0);
						long yhsl = new Double(minbs * ((SuperMarketPopRuleDef) ruleReqList.get(0)).yhlsj).longValue();
						double t_zyhje = 0;

						t_zje = 0;
						t_zyhje = 0;

						// yhje应小于小票的应收金额，不然话，小票金额有成负数的可能。
						if (ManipulatePrecision.doubleCompare(yhje, saleHead.ysje, 2) < 0)
						{
							int t_maxjerow = -1;
							long t_yhsl = yhsl;
							// 计算本次参与优惠商品的总金额
							for (l = 0; l < ruleReqList.size(); l++)
							{
								SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef) ruleReqList.get(l);
								for (k = 0; k < saleGoods.size(); k++)
								{
									SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
									if (t_yhsl > 0 && isMatchCommod(ruleReq, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
									{
										// 商品拆分
										if (ManipulatePrecision.doubleCompare(sg.sl, t_yhsl, 4) <= 0)
										{
											t_yhsl -= (long) sg.sl;
										}
										else
										{
											// 拆分商品行
											splitSalecommod(k, t_yhsl);
											t_yhsl = 0;
										}
										// 如果是折上折
										if (rulePop.iszsz.charAt(0) == '1')
										{
											t_zje += sg.hjje - getZZK(sg);
										}
										else
										{
											if (ruleDef.type == '8')
											{
												t_zje += sg.hjje - getZZK(sg) + sg.yhzke + sg.hyzke + sg.plzke + sg.rulezke;
												t_zyhje += sg.yhzke + sg.hyzke + sg.plzke + sg.rulezke;
											}
											else
											{
												t_zje += sg.hjje - getZZK(sg) + sg.yhzke + sg.plzke + sg.hyzke;
												t_zyhje += sg.yhzke + sg.hyzke + sg.plzke;
											}
										}
									}
								}
							}

							// 计算出优惠金额
							yhje = t_zje - yhje;

							double t_je = 0;
							// 将优惠金额按金额占比分摊到本次参与的商品上面
							for (l = 0; l < ruleReqList.size(); l++)
							{
								SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef) ruleReqList.get(l);
								for (k = 0; k < saleGoods.size(); k++)
								{
									SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
									if (yhsl > 0 && isMatchCommod(ruleReq, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
									{
										// 商品拆分
										if (ManipulatePrecision.doubleCompare(sg.sl, yhsl, 4) <= 0)
										{
											yhsl -= sg.sl;
										}
										else
										{
											// 拆分商品行
											splitSalecommod(k, yhsl);
											yhsl = 0;
										}
										double misszke = 0;
										if (ruleDef.type == '8')
										{
											misszke = sg.yhzke + sg.yhzke + sg.plzke + sg.rulezke;
										}
										else
										{
											misszke = sg.yhzke + sg.yhzke + sg.plzke;
										}
										// 根据折上折来判断，如果非折上折，取低价优先
										if (rulePop.iszsz.charAt(0) == '1')
										{
											if (ruleDef.type == '8')
											{
												sg.mjzke = 0;
												sg.mjzke = (sg.hjje - getZZK(sg)) / t_zje * yhje;
												calcComZkxe(k, sg.mjzke);
												// 记录折扣分担
												sg.mjzkfd = rulePop.zkfd;
												// 记录优惠单据编号
												sg.mjdjbh = rulePop.djbh;
												// 统计当前规则促销的折扣金额
												superMarketRuleyhje += sg.mjzke;

											}
											else
											{
												sg.rulezke = 0;
												sg.rulezke = (sg.hjje - getZZK(sg)) / t_zje * yhje;
												calcComZkxe(k, sg.rulezke);
												// 记录折扣分担
												sg.rulezkfd = rulePop.zkfd;
												// 记录优惠单据编号
												sg.ruledjbh = rulePop.djbh;
												// 统计当前规则促销的折扣金额
												superMarketRuleyhje += sg.rulezke;
											}
										}
										else
										{
											if (yhje > t_zyhje)
											{
												if (ruleDef.type == '8')
												{
													sg.mjzke = 0;
													sg.mjzke = (sg.hjje - getZZK(sg) + misszke) / t_zje * yhje;

													sg.yhzke = 0;
													sg.hyzke = 0;
													sg.plzke = 0;
													sg.spzkfd = 0;
													sg.yhzkfd = 0;
													sg.rulezke = 0;
													sg.rulezkfd = 0;
													sg.yhdjbh = "";
													sg.ruledjbh = "";
													calcComZkxe(k, sg.mjzke);
													// 记录折扣分担
													sg.mjzkfd = rulePop.zkfd;
													// 记录优惠单据编号
													sg.ruledjbh = rulePop.djbh;
													// 统计当前规则促销的折扣金额
													superMarketRuleyhje += sg.mjzke - misszke;

												}
												else
												{
													sg.rulezke = 0;
													sg.rulezke = (sg.hjje - getZZK(sg) + misszke) / t_zje * yhje;
													sg.yhzke = 0;
													sg.hyzke = 0;
													sg.plzke = 0;
													sg.spzkfd = 0;
													sg.yhzkfd = 0;
													sg.yhdjbh = "";
													calcComZkxe(k, sg.rulezke);
													// 记录折扣分担
													sg.rulezkfd = rulePop.zkfd;
													// 记录优惠单据编号
													sg.ruledjbh = rulePop.djbh;
													// 统计当前规则促销的折扣金额
													superMarketRuleyhje += sg.rulezke - misszke;
												}
											}
											else
											{
												t_je = yhje;
											}
										}
										// 将参与优惠的商品打上标记
										sg.isPopExc = 'Y';
										if (ruleDef.type == '8')
										{
											t_je += sg.mjzke;
										}
										else
										{
											t_je += sg.rulezke;
										}

										// 记下金额最大的行号
										if (t_maxjerow >= 0 && ManipulatePrecision.doubleCompare(sg.hjje, ((SaleGoodsDef) saleGoods.get(t_maxjerow)).hjje, 2) > 0 || t_maxjerow < 0)
											t_maxjerow = k;
									}
								}
								if (yhsl <= 0)
									break;
							}
							if (ManipulatePrecision.doubleCompare(Math.abs(yhje - t_je), 0, 2) > 0)
							{
								if (ruleDef.type == '8')
								{
									((SaleGoodsDef) saleGoods.get(t_maxjerow)).mjzke += yhje - t_je;
									calcComZkxe(t_maxjerow, ((SaleGoodsDef) saleGoods.get(t_maxjerow)).mjzke);
								}
								else
								{
									((SaleGoodsDef) saleGoods.get(t_maxjerow)).rulezke += yhje - t_je;
									calcComZkxe(t_maxjerow, ((SaleGoodsDef) saleGoods.get(t_maxjerow)).rulezke);
								}
								superMarketRuleyhje += yhje - t_je;

								// if (t_maxjerow >= winfirst && t_maxjerow <
								// winfirst + PAGE_SALE_COM - 1)
								// DispOneSaleCommod(t_maxjerow -
								// winfirst,t_maxjerow);
								// //显示汇总信息
								// DispPay();
							}
						}
					}
					// 任意多级的，只判断一个结果
					break;
				}
				// 数量促销 数量促销没有折上折(取的单价)，没有分级，取单价
				if (rulePop.yhdjlb == 'N')
				{
					// flag = 1 是全量优惠
					// flag = 2 是超量促销
					// flag = 3 是第n件促销
					// flag = 4 是整箱促销
					long flag = new Double(rulePop.yhhyj).longValue();
					// 统计本单参与优惠的商品数量
					double kyhsl = 0;
					for (k = 0; k < calcCount; k++)
					{
						SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
						// 条件排除的商品应该参与结果计算
						if (/* cominfo[k].infostr1[5] != ' ' && */isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
						{
							kyhsl += sg.sl;
							// 如果前一个商品已经满足了折扣,
							// 那么第二个商品必须标示为已经参与促销（此处为标示其已经促销的条件）。
						}
					}

					// 优惠数量
					double yhsl = Double.parseDouble(rulePop.ppistr3);
					if (ManipulatePrecision.doubleCompare(yhsl, 0, 2) <= 0)
						yhsl = 1;// 防止后面计算时除0错误

					// 超量促销
					if (flag == 2)
						kyhsl -= yhsl;
					// 第n件促销
					if (flag == 3)
						kyhsl = new Double(kyhsl / yhsl).longValue();
					// 整箱促销
					if (flag == 4)
					{
						minbs = new Double(kyhsl / yhsl).longValue();
						long zyhsl = new Double(minbs * yhsl).longValue();// 整箱总的优惠数量
						kyhsl = kyhsl > zyhsl ? zyhsl : kyhsl;
					}

					// 开始计算优惠
					if (ManipulatePrecision.doubleCompare(kyhsl, 0, 2) > 0)
					{
						for (k = 0; k < calcCount; k++)
						{
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
							if (kyhsl > 0 && isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
							{
								// 优惠价低于零售价
								if (sg.lsj > rulePop.yhlsj && rulePop.yhlsj > 0)
								{
									if (kyhsl >= sg.sl)
									{
										kyhsl -= sg.sl;
									}
									else
									{
										// 拆分商品行
										splitSalecommod(k, kyhsl);
										kyhsl = -1;
									}
									sg.rulezke = 0;
									double misszke = sg.yhzke + sg.hyzke + sg.plzke;
									sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + misszke, 2, 0) - ManipulatePrecision.doubleConvert(rulePop.yhlsj * sg.sl, 2, 0);

									// 优惠价低于一般促销
									if (ManipulatePrecision.doubleCompare(sg.rulezke, misszke, 2) > 0)
									{
										sg.yhzke = 0;
										sg.hyzke = 0;
										sg.plzke = 0;
										sg.spzkfd = 0;
										sg.yhzkfd = 0;// 清除普通优惠和会员优惠
										sg.yhdjbh = "";
										sg.rulezkfd = rulePop.zkfd;
										calcComZkxe(k, sg.rulezke);
										// 记录优惠单据编号
										sg.ruledjbh = rulePop.djbh;
										superMarketRuleyhje += sg.rulezke - misszke;

										// if (k >= winfirst && k < winfirst +
										// PAGE_SALE_COM - 1)
										// DispOneSaleCommod(k - winfirst,k);
										//
										// //显示汇总信息
										// DispPay();

										// 参与满减促销的标志
										sg.isPopExc = 'Y';
									}
									else
										sg.rulezke = 0;
								}
							}
						}
					}
				}
				// 任意几个定单价 只分多级
				if (rulePop.yhdjlb == 'Z')
				{
					if (ManipulatePrecision.doubleCompare(zje, ((SuperMarketPopRuleDef) ruleReqList.get(0)).yhlsj, 2) >= 0 && ManipulatePrecision.doubleCompare(((SuperMarketPopRuleDef) ruleReqList.get(0)).yhlsj, 0, 2) > 0)
					{
						// 参与优惠的商品明细数量
						long yhsl = new Double(minbs * ((SuperMarketPopRuleDef) ruleReqList.get(0)).yhlsj).longValue();

						for (l = 0; l < ruleReqList.size(); l++)
						{
							SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef) ruleReqList.get(l);
							for (k = 0; k < saleGoods.size(); k++)
							{
								SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
								if (yhsl > 0 && isMatchCommod(ruleReq, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
								{
									// 商品拆分
									if (ManipulatePrecision.doubleCompare(sg.sl, yhsl, 4) <= 0)
									{
										yhsl -= sg.sl;
									}
									else
									{
										// 拆分商品行
										splitSalecommod(k, yhsl);

										yhsl = 0;
									}
									double misszke = 0;
									if (rulepos == calcCount + 1)
										misszke = sg.yhzke + sg.hyzke + sg.plzke + sg.rulezke;// 记录可能被清零的折扣额
									else
										misszke = sg.yhzke + sg.hyzke + sg.plzke;// 记录可能被清零的折扣额

									if (rulePop.iszsz.charAt(0) == '1')
									{
										if (rulepos == calcCount + 1)
										{
											sg.mjzke = 0;
											sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0) - ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);
											calcComZkxe(k, sg.mjzke);
											sg.mjdjbh = rulePop.djbh;
											sg.mjzkfd = rulePop.zkfd;
											// 统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.mjzke;
										}
										else
										{
											sg.rulezke = 0;
											sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0) - ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);
											calcComZkxe(k, sg.rulezke);
											sg.ruledjbh = rulePop.djbh;
											sg.rulezkfd = rulePop.zkfd;
											// 统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.rulezke;
										}
									}
									else
									{
										if (rulepos == calcCount + 1)
										{
											sg.mjzke = 0;
											sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + misszke, 2, 0) - ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);

											if (sg.mjzke > misszke)// 如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的差额
											{
												sg.yhzke = 0;
												sg.hyzke = 0;
												sg.plzke = 0;
												sg.spzkfd = 0;
												sg.yhzkfd = 0;
												sg.rulezke = 0;
												sg.rulezkfd = 0;
												sg.ruledjbh = "";
												sg.yhdjbh = "";
												calcComZkxe(k, sg.mjzke);
												sg.mjdjbh = rulePop.djbh;
												sg.mjzkfd = rulePop.zkfd;
												// 统计当前规则促销的折扣金额
												superMarketRuleyhje += sg.mjzke - misszke;
											}
											else
											{
												sg.mjzke = 0;
											}
										}
										else
										{
											sg.rulezke = 0;
											sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + misszke, 2, 0) - ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);

											if (sg.rulezke > misszke)// 如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的差额
											{
												sg.yhzke = 0;
												sg.hyzke = 0;
												sg.plzke = 0;
												sg.spzkfd = 0;
												sg.yhzkfd = 0;
												sg.yhdjbh = "";
												sg.ruledjbh = rulePop.djbh;
												calcComZkxe(k, sg.rulezke);
												sg.rulezkfd = rulePop.zkfd;
												// 统计当前规则促销的折扣金额
												superMarketRuleyhje += sg.rulezke - misszke;
											}
											else
											{
												sg.rulezke = 0;
											}
										}
									}
									sg.isPopExc = 'Y';

									// if (k >= winfirst && k < winfirst +
									// PAGE_SALE_COM - 1) DispOneSaleCommod(k -
									// winfirst,k);
									//
									// //显示汇总信息
									// DispPay();
								}
							}
							if (yhsl <= 0)
								break;
						}
					}
					// 任意多级的，只判断一个结果
					break;
				}
				// 对指定商品固定优惠金额
				if (rulePop.yhdjlb == 'V')
				{
					double yhsl = minbs * rulePop.yhhyj;// 优惠数量
					double yhje = ManipulatePrecision.doubleConvert(minbs * rulePop.yhlsj, 2, 0);

					// 结果为OR关系的时候 按照第一个结果的优惠数量来优惠剩余商品
					long and_flag = new Double(rulePop.presentjs).longValue();

					or_yhsl = yhsl;
					zje = 0;
					t_zje = 0;

					while (and_flag == 1 || and_flag == 0 && ManipulatePrecision.doubleCompare(or_yhsl, 0, 4) > 0 && j < rulePopList.size())
					{
						// 统计参与优惠金额分配的商品总金额
						for (k = 0; k < saleGoods.size() && ManipulatePrecision.doubleCompare(or_yhsl, 0, 4) > 0; k++)
						{
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
							// 商品是否结果匹配
							if (isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
							{
								// 商品拆分
								if (ManipulatePrecision.doubleCompare(sg.sl, or_yhsl, 4) <= 0)
								{
									or_yhsl -= sg.sl;
								}
								else
								{
									// 拆分商品行
									splitSalecommod(k, or_yhsl);
									or_yhsl = 0;
								}
								zje += sg.hjje;
								t_zje += sg.hjje - getZZK(sg);
							}
						}
						if (and_flag == 1)
							break;
						j++;
					}
					if (ManipulatePrecision.doubleCompare(yhje, t_zje, 2) > 0)
						yhje = t_zje;

					// 将优惠金额分担到商品明细上
					if (ManipulatePrecision.doubleCompare(zje, 0, 2) > 0 && ManipulatePrecision.doubleCompare(yhje, 0, 2) > 0 && ManipulatePrecision.doubleCompare(zje - yhje, 0, 2) >= 0)
					{
						int maxrow = -1;
						j = 0;
						t_zje = 0;
						while (and_flag == 1 || and_flag == 0 && ManipulatePrecision.doubleCompare(yhsl, 0, 4) > 0 && j < rulePopList.size())
						{
							for (k = 0; k < saleGoods.size() && ManipulatePrecision.doubleCompare(yhsl, 0, 4) > 0; k++)
							{
								SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
								// 商品是否结果匹配
								if (isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
								{
									yhsl -= sg.sl;

									je = ManipulatePrecision.doubleConvert(sg.hjje / zje * yhje, 2, 0);
									if (ruleDef.type == '8')
									{
										sg.mjzke += je;
									}
									else
									{
										sg.rulezke += je;
									}
									// 记录折扣分担
									sg.rulezkfd = rulePop.zkfd;
									// 记录优惠单据编号
									sg.ruledjbh = rulePop.djbh;
									sg.isPopExc = 'Y';

									superMarketRuleyhje += je;
									t_zje += je;
									// if (k >= winfirst && k < winfirst +
									// PAGE_SALE_COM - 1) DispOneSaleCommod(k -
									// winfirst,k);
									// //显示汇总信息
									// DispPay();

									// 记下金额最大的行号
									if (maxrow >= 0 && ManipulatePrecision.doubleCompare(sg.hjje, ((SaleGoodsDef) saleGoods.get(maxrow)).hjje, 2) > 0 || maxrow < 0)
										maxrow = k;
								}
							}
							if (and_flag == 1)
								break;
							j++;
						}
						// 将未分配完的金额分配到金额最大的商品上
						if (ManipulatePrecision.doubleCompare(Math.abs(yhje - t_zje), 0, 2) > 0)
						{
							k = maxrow;
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
							if (ruleDef.type == '8')
							{
								sg.mjzke += yhje - t_zje;
							}
							else
							{
								sg.rulezke += yhje - t_zje;
							}
							superMarketRuleyhje += yhje - t_zje;
							// if (k >= winfirst && k < winfirst + PAGE_SALE_COM
							// - 1) DispOneSaleCommod(k - winfirst,k);
							// //显示汇总信息
							// DispPay();
						}
					}
					// 或条件时，已经优惠完了，所以要退出
					if (and_flag == 0)
						break;
				}
			}

			// 将已参与规则促销的商品打上标志
			for (k = 0; k < saleGoods.size(); k++)
			{
				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
				// 商品是否条件匹配
				if (sg.isPopExc != ' ')
				{
					sg.isSMPopCalced = 'N';
					sg.isPopExc = ' ';
				}
			}
			// 类别为8表示整单满减
			if (ruleDef.type == '8')
				break;
		}
		/*
		 * //打印汇总的折扣额 if (fabs(ruleyhje) > 0 && GSysSyj.printfs == '1' &&
		 * (!ISLXSALE(saletype) || GSysPara.lxprint == 'Y')) { //将最后一个商品打印出来 if
		 * (printflag[old_salecom_num-1]!= 'Y') {
		 * PrintSaleLocal(PRT_SECTION_COMMOD,old_salecom_num-1); //打印商品明细
		 * printflag[old_salecom_num-1] = 'Y'; } char prtbuf[MAX_PAPER_WIDTH+1];
		 * sprintf(prtbuf,"以上商品优惠 %8.2f",ruleyhje); PrinterDraft(prtbuf); }
		 */
		for (k = 0; k < saleGoods.size(); k++)
		{
			getZZK((SaleGoodsDef) saleGoods.get(k));
		}

		// 重算应收
		calcHeadYsje();

		// 刷新商品列表
		saleEvent.updateTable(getSaleGoodsDisplay());
		saleEvent.setTotalInfo();
		return true;

	}

	public void sortSalegoods()
	{
		if (saleGoods.size() > 1)
		{
			SaleGoodsDef sgd = null;
			GoodsDef gd = null;
			SpareInfoDef sid = null;
			GoodsPopDef gpd = null;
			for (int i = 1; i < saleGoods.size(); i++)
			{
				for (int j = 0; j < saleGoods.size() - i; j++)
				{
					String code = ((SaleGoodsDef) saleGoods.get(j)).code;
					String code1 = ((SaleGoodsDef) saleGoods.get(j + 1)).code;

					if (code.equals(code1))
						continue;

					if (code1.length() < code.length() || (code1.length() == code.length() && code1.compareTo(code) < 0))
					{
						sgd = (SaleGoodsDef) saleGoods.get(j);
						saleGoods.setElementAt(saleGoods.get(j + 1), j);
						saleGoods.setElementAt(sgd, j + 1);

						gd = (GoodsDef) goodsAssistant.get(j);
						goodsAssistant.setElementAt(goodsAssistant.get(j + 1), j);
						goodsAssistant.setElementAt(gd, j + 1);

						sid = (SpareInfoDef) goodsSpare.get(j);
						goodsSpare.setElementAt(goodsSpare.get(j + 1), j);
						goodsSpare.setElementAt(sid, j + 1);

						// gpd = (GoodsPopDef) crmPop.get(j);
						// crmPop.setElementAt(crmPop.get(j + 1), j);
						// crmPop.setElementAt(gpd, j + 1);
					}
				}
			}
		}
	}

	private boolean isMatchCommod(SuperMarketPopRuleDef ruleDef, int index)
	{
		SaleGoodsDef sg = ((SaleGoodsDef) saleGoods.get(index));
		GoodsDef goodsDef = ((GoodsDef) goodsAssistant.get(index));

		// 整单的规则,整单优先级最高
		if (ruleDef.type == '8')
			return true;

		// 只有正常的商品才参与规则促销
		if (sg.flag != '4' && sg.flag != '2') { return false; }

		// 如果电子称商品不是排除条件
		if (ruleDef.presentsl != 1 || sg.flag != '2')
		{
			// 如果电子称商品条件不是满减/满返，结果也不是满减/满返
			if (!(ruleDef.yhdjlb == '8' && (ruleDef.ppistr3.charAt(0) == 'G' || ruleDef.ppistr3.charAt(0) == 'C') || ruleDef.yhdjlb == 'G' || ruleDef.yhdjlb == 'C') && sg.flag == '2') { return false; }
		}

		// 条件为整单的时候如果是结果为非整单。此处就不判断商品是否参与了规则促销，
		// 不然在结果匹配的时候会因为商品参与了非整单规则促销而无法参与整单的规则促销
		// 在整单规则的时候初始化商品标识的时候使用
		if (((SuperMarketPopRuleDef) ruleReqList.get(0)).type != '8')
		{
			// 不参与规则促销
			if (sg.isSMPopCalced != 'Y') { return false; }
		}

		switch (ruleDef.type)
		{
		case '1':// 单品
			if (!ruleDef.code.equals(goodsDef.code))
				break;
			if ((ruleDef.gz.equals(goodsDef.gz) || ruleDef.gz.equals("0")) && (ruleDef.spec.equals(goodsDef.uid) || ruleDef.spec.equals("AL"))) { return true; }
			break;
		case '2':// 柜组
			if (!ruleDef.code.equals(goodsDef.gz))
				break;
			return true;
		case '3':// 类别
			if (!ruleDef.code.equals(goodsDef.catid.substring(0, ruleDef.code.length())))
				break;
			return true;
		case '4':// 柜组品牌
			if (!ruleDef.code.equals(goodsDef.gz))
				break;
			if (ruleDef.pp.equals(goodsDef.ppcode)) { return true; }
			break;
		case '5':// 类别品牌
			if (!ruleDef.code.equals(goodsDef.catid.substring(0, ruleDef.code.length())))
				break;
			if (ruleDef.pp.equals(goodsDef.ppcode)) { return true; }
			break;
		case '6':// 品牌
			if (!ruleDef.code.equals(goodsDef.ppcode))
				break;
			return true;
		case '7':// 生鲜单品
			if (!ruleDef.code.equals(goodsDef.code))
				break;
			if ((ruleDef.gz.equals(goodsDef.gz) || ruleDef.gz.equals("0")) && (ruleDef.spec.equals(goodsDef.uid) || ruleDef.spec.equals("AL"))) { return true; }
			break;
		}
		return false;
	}

	private void getCurRuleJc(int jc)
	{
		int i;
		// 获得条件在jc传入值的级别所对应的级别值
		for (i = 0; i < ruleReqList.size(); i++)
		{
			SuperMarketPopRuleDef reqDef = (SuperMarketPopRuleDef) ruleReqList.get(i);
			double a = Double.parseDouble(reqDef.ppistr1.split("\\|")[jc - 1]);
			if (a > 0)
				reqDef.yhlsj = a;

		}
		for (i = 0; i < rulePopList.size(); i++)
		{
			SuperMarketPopRuleDef reqPop = (SuperMarketPopRuleDef) rulePopList.get(i);
			double a = Double.parseDouble(reqPop.ppistr1.split("\\|")[jc - 1]);
			if (a > 0)
				reqPop.yhlsj = a;
			double b = Double.parseDouble(reqPop.ppistr2.split("\\|")[jc - 1]);
			if (b > 0)
				reqPop.yhhyj = b;
		}
	}

	private boolean splitSalecommod(int n, double newsl)
	{
		SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(n);
		GoodsDef goods = (GoodsDef) goodsAssistant.get(n);
		SpareInfoDef spare = (SpareInfoDef) goodsSpare.get(n);
		// GoodsPopDef goodsPop = (GoodsPopDef) crmPop.get(n);

		if (saleGoods.size() <= 0 || n < 0 || n >= saleGoods.size())
			return false;
		if (newsl >= sg.sl)
			return false;

		SaleGoodsDef newsg = (SaleGoodsDef) sg.clone();
		GoodsDef newGoods = (GoodsDef) goods.clone();
		SpareInfoDef newSpare = (SpareInfoDef) spare.clone();
		// GoodsPopDef newGoodsPop = (GoodsPopDef) goodsPop.clone();

		double zje = sg.hjje;
		double rulezke = sg.rulezke;
		double mjzke = sg.mjzke;

		// 重算金额
		sg.sl = ManipulatePrecision.doubleConvert(newsl, 4, 1);
		sg.hjje = ManipulatePrecision.doubleConvert(newsg.hjje / newsg.sl * sg.sl, 2, 1);
		sg.hyzke = ManipulatePrecision.doubleConvert(newsg.hyzke / newsg.sl * sg.sl, 2, 1);
		sg.yhzke = ManipulatePrecision.doubleConvert(newsg.yhzke / newsg.sl * sg.sl, 2, 1);
		sg.plzke = ManipulatePrecision.doubleConvert(newsg.plzke / newsg.sl * sg.sl, 2, 1);
		sg.zszke = ManipulatePrecision.doubleConvert(newsg.zszke / newsg.sl * sg.sl, 2, 1);
		sg.hyzklje = ManipulatePrecision.doubleConvert(newsg.hyzklje / newsg.sl * sg.sl, 2, 1);
		sg.lszke = ManipulatePrecision.doubleConvert(newsg.lszke / newsg.sl * sg.sl, 2, 1);
		sg.lszre = ManipulatePrecision.doubleConvert(newsg.lszre / newsg.sl * sg.sl, 2, 1);
		sg.lszzk = ManipulatePrecision.doubleConvert(newsg.lszzk / newsg.sl * sg.sl, 2, 1);
		sg.lszzr = ManipulatePrecision.doubleConvert(newsg.lszzr / newsg.sl * sg.sl, 2, 1);
		sg.cjzke = ManipulatePrecision.doubleConvert(newsg.cjzke / newsg.sl * sg.sl, 2, 1);
		sg.ltzke = ManipulatePrecision.doubleConvert(newsg.ltzke / newsg.sl * sg.sl, 2, 1);
		sg.qtzke = ManipulatePrecision.doubleConvert(newsg.qtzke / newsg.sl * sg.sl, 2, 1);
		sg.qtzre = ManipulatePrecision.doubleConvert(newsg.qtzre / newsg.sl * sg.sl, 2, 1);

		newsg.sl = ManipulatePrecision.doubleConvert(newsg.sl - sg.sl, 4, 1);
		newsg.hjje = ManipulatePrecision.doubleConvert(newsg.hjje - sg.hjje, 2, 1);
		newsg.hyzke = ManipulatePrecision.doubleConvert(newsg.hyzke - sg.hyzke, 2, 1);
		newsg.yhzke = ManipulatePrecision.doubleConvert(newsg.yhzke - sg.yhzke, 2, 1);
		newsg.plzke = ManipulatePrecision.doubleConvert(newsg.plzke - sg.plzke, 2, 1);
		newsg.zszke = ManipulatePrecision.doubleConvert(newsg.zszke - sg.zszke, 2, 1);
		newsg.hyzklje = ManipulatePrecision.doubleConvert(newsg.hyzklje - sg.hyzklje, 2, 1);
		newsg.lszke = ManipulatePrecision.doubleConvert(newsg.lszke - sg.lszke, 2, 1);
		newsg.lszre = ManipulatePrecision.doubleConvert(newsg.lszre - sg.lszre, 2, 1);
		newsg.lszzk = ManipulatePrecision.doubleConvert(newsg.lszzk - sg.lszzk, 2, 1);
		newsg.lszzr = ManipulatePrecision.doubleConvert(newsg.lszzr - sg.lszzr, 2, 1);
		newsg.cjzke = ManipulatePrecision.doubleConvert(newsg.cjzke - sg.cjzke, 2, 1);
		newsg.ltzke = ManipulatePrecision.doubleConvert(newsg.ltzke - sg.ltzke, 2, 1);
		newsg.qtzke = ManipulatePrecision.doubleConvert(newsg.qtzke - sg.qtzke, 2, 1);
		newsg.qtzre = ManipulatePrecision.doubleConvert(newsg.qtzre - sg.qtzre, 2, 1);

		// 将拆分的商品的规则促销折扣金额进行分摊，此处必须分摊，不然会导致在计算整单的时候，出现成交金额为负数的情况
		sg.rulezke = (sg.hjje / zje) * rulezke;
		newsg.rulezke = (newsg.hjje / zje) * rulezke;
		sg.mjzke = (sg.hjje / zje) * mjzke;
		newsg.mjzke = (newsg.hjje / zje) * mjzke;

		getZZK(sg);
		getZZK(newsg);
		
		addSaleGoodsObject(newsg,newGoods,newSpare);
		
		//saleGoods.add(newsg);
		//goodsAssistant.add(newGoods);
		//goodsSpare.add(newSpare);
		// crmPop.add(newGoodsPop);
		/*
		 * //计算会员折扣和优惠折扣 CalculateAllRebate(n); //批量销售折扣处理,重算优惠折扣和会员折扣
		 * CalculateBatchRebate(n);
		 * 
		 * //计算会员折扣和优惠折扣 CalculateAllRebate(salecom_num-1);
		 * //批量销售折扣处理,重算优惠折扣和会员折扣 CalculateBatchRebate(salecom_num-1);
		 * 
		 * //记录打印标志(打印标志取原行上的标志) printflag[salecom_num-1] = printflag[n];
		 * 
		 * // //记录断点 // WriteBroken(n,BROKEN_UPDATE); //
		 * WriteBroken(salecom_num-1,BROKEN_APPEND);
		 * 
		 * //刷新被拆分的原商品行 if (n >= winfirst && n < winfirst + PAGE_SALE_COM - 1)
		 * DispOneSaleCommod(n - winfirst,n);
		 * 
		 * //刷新商品列表 DispSaleCommod();
		 * 
		 * CreateInputLine(2);
		 */
		// 刷新商品列表
		saleEvent.updateTable(getSaleGoodsDisplay());
		saleEvent.setTotalInfo();

		return true;
	}
	public void addSaleGoodsObject(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		// 记录商品录入顺序
		sg.num5 = saleGoods.size();
		saleGoods.add(sg);
		goodsAssistant.add(goods);
		goodsSpare.add(info);

		// goods不为空才是销售的商品,查找商品对应收款规则
		if ((GlobalInfo.sysPara.havePayRule == 'Y' || GlobalInfo.sysPara.havePayRule == 'A') && goods != null && info != null)
		{
			info.payrule = DataService.getDefault().getGoodsPayRule(goods);
		}

		if (GlobalInfo.sysPara.custompayobj.indexOf("PaymentJfNew") >= 0)
		{
			GoodsJFRule jfrule = new GoodsJFRule();
			if (NetService.getDefault().getGoodsjfrule(sg.code, sg.gz, sg.catid, sg.ppcode, saleHead.rqsj, saleHead.hykh, jfrule))
			{
				info.memo1 = jfrule.jfrule;
				sg.jfrule = jfrule.jfrule;
				sg.str8 = jfrule.jfrule;
			}
		}

		setPreShopSaleQuantity();
	}
	private double calcComZkxe(int k, double zke)
	{
		if (k < 0 || k >= saleGoods.size())
			return zke;

		SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
		if (ManipulatePrecision.doubleCompare(sg.hjje - getZZK(sg), 0, 2) <= 0)
		{
			zke = 0;// 引用数据类型,将实参清零
			zke = sg.hjje - getZZK(sg);
		}
		// 计算价格精度
		zke = getConvertPrice(zke, (GoodsDef) goodsAssistant.elementAt(k));
		return zke;
	}

	public void calcGoodsVIPRebate(int index)
	{
		return;
	}

	public void calcBatchRebate(int index)
	{
		return;
	}

	public double getZZK(SaleGoodsDef saleGoodsDef)
	{
		saleGoodsDef.hjzk = ManipulatePrecision.doubleConvert(saleGoodsDef.hyzke + saleGoodsDef.yhzke + saleGoodsDef.lszke + saleGoodsDef.lszre + saleGoodsDef.lszzk + saleGoodsDef.lszzr + saleGoodsDef.plzke + saleGoodsDef.zszke + saleGoodsDef.cjzke + saleGoodsDef.ltzke + saleGoodsDef.hyzklje + saleGoodsDef.qtzke + saleGoodsDef.qtzre + saleGoodsDef.rulezke + saleGoodsDef.mjzke + saleGoodsDef.num7, 2, 1);

		return saleGoodsDef.hjzk;
	}
}
