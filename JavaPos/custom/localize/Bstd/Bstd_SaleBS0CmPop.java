package custom.localize.Bstd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import com.efuture.commonKit.CommonLogger;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ExpressionDeal;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.CmPopGiftsDef;
import com.efuture.javaPos.Struct.CmPopGoodsDef;
import com.efuture.javaPos.Struct.CmPopRuleLadderDef;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.CustomerVipZklDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.ApportPaymentForm;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.SalePayForm;

public class Bstd_SaleBS0CmPop extends SaleBS
{
	protected CommonLogger zkLogger;

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

	public Vector goodsCmPop = null;
	protected boolean haveCmPop = false;
	protected boolean doCmPopExit = false;
	protected boolean needApportionPayment = false;
	protected String payPopPrepareExcp = ""; // 每个促销规则预受限付款列表
	protected String payPopOtherExcp = ""; // 最后付款受限付款列表
	protected final int payNormal = 0; // 0-无满减规则直接付款
	protected final int payPopPrepare = 1; // 1-有满减规则预付除外付款
	protected final int payPopOther = 2; // 2-有满减规则付其他付款
	protected int isPreparePay = payNormal;

	public void initNewSale()
	{
		if (goodsCmPop != null)
			goodsCmPop.removeAllElements();
		else
			goodsCmPop = new Vector();

		payPopPrepareExcp = "";
		payPopOtherExcp = "";

		super.initNewSale();

		if (zkLogger == null)
			zkLogger = new CommonLogger(GlobalInfo.sysPara.isenablezklog == 'Y' ? true : false);
		else
			zkLogger.init(true, "");
	}

	public void writeSellObjectToStream(ObjectOutputStream s) throws Exception
	{
		brokenAssistant.insertElementAt(goodsCmPop, 0);

		super.writeSellObjectToStream(s);
	}

	public void readStreamToSellObject(ObjectInputStream s) throws Exception
	{
		super.readStreamToSellObject(s);

		goodsCmPop = (Vector) brokenAssistant.remove(0);
	}

	public void addCmpopSaleGoodsObject(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		super.addSaleGoodsObject(sg, goods, info);

		// goods不为空才是销售的商品,查找商品对应促销情况
		if (goods != null && info != null)
			findGoodsCMPOPInfo(sg, goods, info);
		else
			goodsCmPop.add(null);
	}

	public boolean delSaleGoodsObject(int index)
	{
		// 删除商品
		if (!super.delSaleGoodsObject(index))
			return false;

		// 删除相应CMPOP
		if (goodsCmPop.size() > index)
			goodsCmPop.removeElementAt(index);

		return true;
	}

	public void calcBatchRebate(int index)
	{
		// 自动计算本商品的累计模式促销
		if (!SellType.ISSALE(this.saletype))
			return;

		if (goodsCmPop == null || goodsCmPop.size() == 0)
			return;

		// 累计模式的促销对其他行商品的影响
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		Vector popvec = (Vector) goodsCmPop.elementAt(index);
		for (int i = 0; popvec != null && i < popvec.size(); i++)
		{
			CmPopGoodsDef cmp = (CmPopGoodsDef) popvec.elementAt(i);

			// 特别指定允许立即计算折扣的累计促销才自动计算(例如批量等),否则都在按付款键以后统一计算
			if (cmp.ruleinfo.summode == '0')
				continue;
			if (cmp.ruleinfo.memo == null || !(cmp.ruleinfo.memo.length() > 0 && cmp.ruleinfo.memo.charAt(0) == 'Y'))
				continue;

			// 先清除所有有相同促销的商品的促销折扣
			boolean havedel = false;
			for (int j = 0; j < saleGoods.size(); j++)
			{
				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(j);
				if (j >= goodsCmPop.size())
					continue;

				Vector popvec1 = (Vector) goodsCmPop.elementAt(j);
				for (int n = 0; popvec1 != null && n < popvec.size(); n++)
				{
					CmPopGoodsDef cmpoth = (CmPopGoodsDef) popvec1.elementAt(n);
					if (cmp.dqid.equals(cmpoth.dqid) && cmp.ruleid.equals(cmpoth.ruleid))
					{
						// 同规则累计,同规则同柜组累计,同规则单品累计,同规则同品类累计,同规则同品牌累计
						if ((cmp.ruleinfo.summode == '1') || (cmp.ruleinfo.summode == '2' && saleGoodsDef.gz.equals(sg.gz)) || (cmp.ruleinfo.summode == '3' && saleGoodsDef.code.equals(sg.code) && saleGoodsDef.gz.equals(sg.gz)) || (cmp.ruleinfo.summode == '4' && saleGoodsDef.catid.equals(sg.catid)) || (cmp.ruleinfo.summode == '5' && saleGoodsDef.ppcode.equals(sg.ppcode)))
						{
							// 恢复促销使用标志
							cmpoth.used = false;

							// 清除本促销产生的折扣
							if (sg.zszke > 0)
								havedel = true;
							sg.zsdjbh = null;
							sg.zszke = 0;
							sg.zszkfd = 0;
							getZZK(sg, CommonMethod.getTraceInfo(), true);

							markPopGoods(sg, null);
							// 清除本促销产生的赠品
						}
					}
				}
			}

			// 再计算累计以后产生的折扣
			boolean havepop = doCmPop(index);

			// 如果doCmPop未刷新才执行取消折扣刷新,否则不执行
			if (!havepop && havedel)
				refreshCmPopUI();
		}
	}

	public void calcGoodsPOPRebate(int index)
	{
		if (!SellType.ISSALE(saletype))
			return;

		if (goodsCmPop == null || goodsCmPop.size() == 0)
			return;

		// 计算不进行累计的促销规则
		Vector popvec = (Vector) goodsCmPop.elementAt(index);
		for (int i = 0; popvec != null && i < popvec.size(); i++)
		{
			CmPopGoodsDef cmp = (CmPopGoodsDef) popvec.elementAt(i);

			// 不累计的促销允许立即计算折扣的促销,则找到商品后立即计算促销折扣
			if (cmp.ruleinfo.summode == '0')
			{
				// 恢复促销使用标志
				cmp.used = false;

				zkLogger.log("Start calcGoodsPOPRebate-> SaleGoodsIndex:" + index + " CmpGoodsIndex:" + i);
				zkLogger.log("CmpInfo-> Dqinfo:" + cmp.dqid + " RuleInfo:" + cmp.ruleinfo.ruleid + " Seqno:" + cmp.cmpopseqno + " Codeid:" + cmp.codeid);

				// 计算分期促销
				calcGoodsCMPOPRebate(index, cmp, i);
				zkLogger.log("Stop calcGoodsPOPRebate-> SaleGoodsIndex:" + index + " CmpGoodsIndex:" + i);
				// 促销单是否允许VIP继续折上折
				this.popvipzsz = cmp.ruleinfo.popzsz;
			}
		}
	}

	public void clearGoodsAllRebate(int index)
	{
		SpareInfoDef sginfo = (SpareInfoDef) goodsSpare.elementAt(index);
		if (sginfo.popzk != null)
			sginfo.popzk.removeAllElements();

		super.clearGoodsAllRebate(index);
	}

	public void customerIsHy(CustomerDef cust)
	{
		// 需要重算商品折扣,重新刷卡以后把商品的促销重新查询一次
		if (cust.ishy == 'Y' || cust.ishy == 'V' || cust.ishy == 'H')
		{
			goodsCmPop.removeAllElements();
			for (int i = 0; i < saleGoods.size(); i++)
			{
				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);
				GoodsDef goods = null;
				if (goodsAssistant.size() > i)
					goods = (GoodsDef) goodsAssistant.elementAt(i);
				SpareInfoDef info = null;
				if (goodsSpare.size() > i)
					info = (SpareInfoDef) goodsSpare.elementAt(i);

				// goods不为空才是销售的商品,查找商品对应促销情况
				if (goods != null && info != null)
					findGoodsCMPOPInfo(sg, goods, info);
				else
					goodsCmPop.add(null);
			}
		}

		// 基类处理重算促销折扣
		super.customerIsHy(cust);
	}

	public SaleGoodsDef SplitSaleGoodsRow(int index, double splitsl)
	{
		SaleGoodsDef newsg = super.SplitSaleGoodsRow(index, splitsl);

		if (newsg.flag == '2' && newsg.num4 > 0)
			newsg.num4 = 0;

		if (newsg != null && goodsCmPop != null && goodsCmPop.size() > index)
		{
			// 新拆分出的商品行促销结果集如果查找有误,clone原促销信息
			Vector pop = (Vector) goodsCmPop.elementAt(index);
			Vector newpop = (Vector) goodsCmPop.elementAt(goodsCmPop.size() - 1);
			if (pop != null && (newpop == null || pop.size() != newpop.size()))
			{
				if (newpop == null)
					newpop = new Vector();
				else
					newpop.removeAllElements();
				for (int i = 0; i < pop.size(); i++)
				{
					CmPopGoodsDef cp = (CmPopGoodsDef) ((CmPopGoodsDef) pop.elementAt(i)).clone();
					cp.used = false;
					newpop.add(cp);
				}
				goodsCmPop.setElementAt(newpop, goodsCmPop.size() - 1);
			}

			// 分拆商品行的折扣分摊
			SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(index);
			SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(index);
			SpareInfoDef newspinfo = (SpareInfoDef) spinfo.clone();
			goodsSpare.setElementAt(newspinfo, goodsSpare.size() - 1);
			for (int j = 0; spinfo.popzk != null && j < spinfo.popzk.size(); j++)
			{
				double zk = 0, newzk = 0;
				String[] s = (String[]) spinfo.popzk.elementAt(j);
				if (s.length > 1)
					newzk = Convert.toDouble(s[1]);

				zk = ManipulatePrecision.doubleConvert(newzk / (sg.sl + newsg.sl) * sg.sl, 2, 1);
				newzk = ManipulatePrecision.doubleConvert(newzk - zk, 2, 1);

				// 原商品行的折扣明细
				s[1] = ManipulatePrecision.doubleToString(zk);
				spinfo.popzk.setElementAt(s, j);

				// 新商品行的折扣明细
				String[] news = (String[]) newspinfo.popzk.elementAt(j);
				news[1] = ManipulatePrecision.doubleToString(newzk);
				newspinfo.popzk.setElementAt(news, j);
			}
		}

		return newsg;
	}

	public void rebateDetail(int index)
	{
		super.rebateDetail(index);

		// 调试模式显示商品参与的促销规则
		if (ConfigClass.DebugMode && goodsCmPop != null && goodsCmPop.size() > index)
		{
			Vector popvec = (Vector) goodsCmPop.elementAt(index);
			if (popvec == null || popvec.size() <= 0)
				return;

			Vector choice = new Vector();
			String[] title = { "促销序号", "档期代码", "档期描述", "规则代码", "规则描述" };
			int[] width = { 120, 100, 200, 120, 200 };
			for (int i = 0; i < popvec.size(); i++)
			{
				CmPopGoodsDef cmp = (CmPopGoodsDef) popvec.elementAt(i);
				choice.add(new String[] { String.valueOf(cmp.cmpopseqno), cmp.dqid, cmp.dqinfo.name, cmp.ruleid, cmp.ruleinfo.rulename });
			}
			new MutiSelectForm().open("查看商品参与的促销详情", title, width, choice, false, 780, 300, false);
		}
	}

	public void payShowRebateDetail(int key)
	{
		// 信用卡追送功能
		if ((key == GlobalVar.WholeRate || key == GlobalVar.WholeRebate))
		{
			getCreditCardZK();
		}
		else
		{
			Vector choice = new Vector();
			String[] title = { "序", "商品编码", "商品名称", "数量", "单价/成交单价", "成交金额" };
			int[] width = { 30, 140, 160, 75, 210, 155 };
			for (int i = 0; i < saleGoods.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);

				String[] row = new String[6];
				row[0] = String.valueOf(i + 1);
				row[1] = sgd.code;
				row[2] = sgd.name;
				row[3] = ManipulatePrecision.doubleToString(sgd.sl, 4, 1, true);
				String s = ManipulatePrecision.doubleToString(sgd.jg, 2, 1, false, 10) + "/" + ManipulatePrecision.doubleToString(ManipulatePrecision.div(ManipulatePrecision.sub(sgd.hjje, sgd.hjzk), sgd.sl), 2, 1);
				row[4] = Convert.increaseCharForward(s, 19);
				s = ManipulatePrecision.doubleToString(sgd.hjje - sgd.hjzk) + "(" + ManipulatePrecision.doubleToString((sgd.hjje - sgd.hjzk) / sgd.hjje * 100, 0, 1, false, 2) + "%)";
				row[5] = Convert.increaseCharForward(s, 14);
				choice.add(row);

				boolean needblank = false;
				if (sgd.hyzke > 0)
				{
					row = new String[6];
					row[0] = "";
					row[1] = "";
					row[2] = sgd.hydjbh != null ? sgd.hydjbh : "";
					row[3] = "";
					row[4] = "会员折off:";
					s = ManipulatePrecision.doubleToString(sgd.hyzke) + "(" + ManipulatePrecision.doubleToString(sgd.hyzke / sgd.hjje * 100, 0, 1, false, 2) + "%)";
					row[5] = Convert.increaseCharForward(s, 14);
					choice.add(row);
					needblank = true;
				}

				if (sgd.yhzke + sgd.zszke > 0)
				{
					// 分项折扣明细
					double zkhj = 0;
					SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(i);
					for (int j = 0; spinfo.popzk != null && j < spinfo.popzk.size(); j++)
					{
						double zk = 0;
						String[] ss = (String[]) spinfo.popzk.elementAt(j);
						if (ss.length > 1)
							zk = Convert.toDouble(ss[1]);

						row = new String[6];
						row[0] = "";
						row[1] = "";
						row[2] = ss[0];
						row[3] = "";
						row[4] = "促销折off:";
						s = ManipulatePrecision.doubleToString(zk) + "(" + ManipulatePrecision.doubleToString(zk / sgd.hjje * 100, 0, 1, false, 2) + "%)";
						row[5] = Convert.increaseCharForward(s, 14);
						choice.add(row);
						needblank = true;

						// 分项折扣合计
						zkhj += zk;
					}

					// 其他促销折扣
					if (ManipulatePrecision.doubleCompare(sgd.yhzke + sgd.zszke, zkhj, 2) > 0)
					{
						row = new String[6];
						row[0] = "";
						row[1] = "";
						row[2] = "";
						row[3] = "";
						row[4] = "促销折off:";
						s = ManipulatePrecision.doubleToString(sgd.yhzke + sgd.zszke - zkhj) + "(" + ManipulatePrecision.doubleToString((sgd.yhzke + sgd.zszke - zkhj) / sgd.hjje * 100, 0, 1, false, 2) + "%)";
						row[5] = Convert.increaseCharForward(s, 14);
						choice.add(row);
						needblank = true;
					}
				}

				if (sgd.hjzk - (sgd.hyzke + sgd.yhzke + sgd.zszke) > 0)
				{
					row = new String[6];
					row[0] = "";
					row[1] = "";
					row[2] = "";
					row[3] = "";
					row[4] = "其他折off:";
					s = ManipulatePrecision.doubleToString(sgd.hjzk - sgd.hyzke - sgd.yhzke - sgd.zszke) + "(" + ManipulatePrecision.doubleToString((sgd.hjzk - sgd.hyzke - sgd.yhzke - sgd.zszke) / sgd.hjje * 100, 0, 1, false, 2) + "%)";
					row[5] = Convert.increaseCharForward(s, 14);
					choice.add(row);
					needblank = true;
				}

				if (needblank)
				{
					row = new String[6];
					row[0] = "";
					row[1] = "";
					row[2] = "";
					row[3] = "";
					row[4] = "";
					row[5] = "";
					choice.add(row);
				}
			}

			new MutiSelectForm().open("查看商品折扣详情", title, width, choice, false, 820, 480, false);
		}
	}

	protected void backupBroken()
	{
		String name = ConfigClass.LocalDBPath + "/Broken.bak";
		if (PathFile.fileExist(name))
			PathFile.deletePath(name);

		PathFile.copyPath(ConfigClass.LocalDBPath + "/Broken.dat", ConfigClass.LocalDBPath + "/Broken.bak");
	}

	public boolean paySellStart()
	{
		if (!super.paySellStart())
			return false; // 不允许进行付款

		// 处理CRM促销
		doCmPopExit = false;

		haveCmPop = doCmPop(-1);

		//备份出促销后的折扣
		backupBroken();
		
		if (doCmPopExit)
			return false; // 不再继续进行付款

		return true;
	}

	public void paySellCancel()
	{
		// 放弃CM促销
		if (haveCmPop)
			delCmPop();

		super.paySellCancel();
	}

	public boolean exitPaySell()
	{
		// 满减预先付款除外付款退出时，允许直接退出
		if (isPreparePay == payPopPrepare)
		{
			return true;
		}
		else
		{
			boolean ret = super.exitPaySell();

			if (ret)
				isPreparePay = payNormal;

			return ret;
		}
	}

	public boolean getPayModeByNeed(PayModeDef paymode)
	{
		if (!super.getPayModeByNeed(paymode))
			return false;

		// 无满减的实际付款，所有付款方式都可以
		if (isPreparePay == payNormal) { return true; }

		// 满减预先付款只先付券类付款方式
		if (isPreparePay == payPopPrepare)
		{
			String[] pay = null;
			if (payPopPrepareExcp.indexOf(",") >= 0)
				pay = payPopPrepareExcp.split(",");
			else
				pay = payPopPrepareExcp.split("\\|");

			for (int i = 0; i < pay.length; i++)
			{
				if (paymode.code.equals(pay[i].trim()) || DataService.getDefault().isChildPayMode(paymode.code, pay[i].trim())) { return true; }
			}

			return false;
		}

		// 满减后再付款只允许付非券类付款方式
		// 券类的付款方式必须在满减前输入完成
		if (isPreparePay == payPopOther)
		{
			String[] pay = null;
			if (payPopOtherExcp.indexOf(",") >= 0)
				pay = payPopOtherExcp.split(",");
			else
				pay = payPopOtherExcp.split("\\|");

			for (int i = 0; i < pay.length; i++)
			{
				if (paymode.code.equals(pay[i].trim())) { return false; }
			}

			return true;
		}

		return true;
	}

	public Vector paymentApportionBySale(SalePayDef spay, Payment payobj)
	{
		// 无需手工分配
		if (isPreparePay != payPopPrepare)
			return null;
		if (!needApportionPayment)
			return null;
		if (GlobalInfo.sysPara.apportMode != 'A')
			return null;

		// 商品编码,商品名称,已付金额,限制金额,分摊金额,对应商品行号
		Vector v = payobj.getGoodsListByPayRule();
		if (v == null)
			return null;

		// 如果付款金额比限制金额的合计大，说明有溢余，溢余部分不进行分摊
		double allftje = spay.je - spay.num1;
		double maxftje = 0;
		for (int i = 0; i < v.size(); i++)
		{
			String[] row = (String[]) v.elementAt(i);
			maxftje += Convert.toDouble(row[3]);
		}
		if (allftje > maxftje)
			allftje = maxftje;

		// 显示分摊窗口，输入分摊金额
		new ApportPaymentForm().open(v, spay.payname + " 共付款 " + ManipulatePrecision.doubleToString(spay.je) + " 元", allftje);

		return v;
	}

	/*
	 * public boolean saleSummary() { if (!super.saleSummary()) return false;
	 * 
	 * // 记录促销明细到商品str3 for (int i = 0; i < saleGoods.size(); i++) {
	 * SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i); if (goodsSpare
	 * == null || goodsSpare.size() <= i) continue; SpareInfoDef spinfo =
	 * (SpareInfoDef) goodsSpare.elementAt(i); if (spinfo == null) continue;
	 * 
	 * // sg.str3 = 促销序号:促销金额 // popzk = 促销序号,促销金额,促销备注 sg.str3 = ""; for (int j
	 * = 0; spinfo.popzk != null && j < spinfo.popzk.size(); j++) { String[] s =
	 * (String[]) spinfo.popzk.elementAt(j); sg.str3 += "," + s[0] + ":" + s[1];
	 * 
	 * if (s.length >= 3 && s[2] != null && !s[2].equals("")) { sg.str3 += ":" +
	 * s[2]; } }
	 * 
	 * if (sg.str3.length() > 0) sg.str3 = sg.str3.substring(1); }
	 * 
	 * return true; }
	 */

	public double getGoodsPaymentApportion(int index, SaleGoodsDef sg)
	{
		SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);
		if (info == null || info.payft == null)
			return 0;

		// 该商品分摊到的不参与计算的付款金额
		double payje = 0;
		for (int j = 0; j < info.payft.size(); j++)
		{
			String[] s = (String[]) info.payft.elementAt(j);
			if (s.length > 3)
			{
				payje += Double.parseDouble(s[3]);
			}
		}

		return payje;
	}

	public void findGoodsRuleFromCRM(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		if (GlobalInfo.sysPara.isenablefq == 'Y')
			NetService.getDefault().findGoodsCouponRule(goods, goods.code, goods.gz, goods.uid, goods.catid, goods.ppcode, saleHead.rqsj, curCustomer != null ? curCustomer.code : "", curCustomer != null ? curCustomer.type : "", saletype, NetService.getDefault().getMemCardHttp(CmdDef.FINDCRMPOP));
	}

	public void filterCMPOPInfo(Vector popvec)
	{
		// 1. 先处理档期，按活动档期分组
		if (popvec != null)
		{
			// 只保留同档期类别最后一个档期,从后往前倒序搜索
			for (int i = popvec.size() - 1; i >= 0; i--)
			{
				CmPopGoodsDef cmp = (CmPopGoodsDef) popvec.elementAt(i);

				// 忽略的档期规则
				if (cmp.popmode == 'C' || cmp.ruleinfo.popmode == 'C')
				{
					popvec.remove(i);
					i = popvec.size();
					continue;
				}

				// 从后往前依次查找匹配的dqtype，若找到相同的dqtype，则将找到的删除，保证同一种类型的档期只有一条
				if (i - 1 >= 0 && !((CmPopGoodsDef) popvec.elementAt(i - 1)).dqid.equals(cmp.dqid))
				{
					// 找到集合中前面同类别的档期
					int j = i - 1;
					for (; j >= 0; j--)
					{
						CmPopGoodsDef cmp1 = (CmPopGoodsDef) popvec.elementAt(j);
						if (cmp.dqinfo.dqtype != null && !cmp.dqinfo.dqtype.trim().equals("") && cmp.dqinfo.dqtype.equals(cmp1.dqinfo.dqtype))
						{
							popvec.remove(j);
							break;
						}
					}

					// 重新找需要选择的规则
					// 若popvect没有与最后一个元素相同的cmp时，
					// 此时j=-1,接着会以popvect倒数第二个元素为准，接着查找与第二个元素相同类型的档期
					if (j >= 0)
					{
						// 重新设i值
						i = popvec.size();
						continue;
					}
				}
			}

			// 2. 再处理规则，选择规则或去掉需要放弃的规则
			for (int i = 0; i < popvec.size(); i++)
			{
				CmPopGoodsDef cmp = (CmPopGoodsDef) popvec.elementAt(i);

				// 同档期活动存在多种规则始终只保留一个规则
				if (i + 1 < popvec.size() && ((CmPopGoodsDef) popvec.elementAt(i + 1)).dqid.equals(cmp.dqid))
				{
					// 手工选择一个规则
					if (cmp.dqinfo.ruleselmode == '0')
					{
						Vector contents = new Vector();
						for (int j = i; j < popvec.size(); j++)
						{
							CmPopGoodsDef cmp1 = (CmPopGoodsDef) popvec.elementAt(j);
							if (!cmp1.dqid.equals(cmp.dqid))
								break;
							contents.add(new String[] { cmp1.ruleid, cmp1.ruleinfo.rulename });
						}
						if (contents.size() <= 1)
							continue;
						String[] title = { "规则代码", "规则描述" };
						int[] width = { 100, 400 };
						int choice = -1;
						do
						{
							choice = new MutiSelectForm().open("请选择该商品参与[" + cmp.dqinfo.name + "]活动的促销形式", title, width, contents);
						}
						while (choice == -1);

						// 删除未选择的规则
						String choicerule = ((String[]) contents.elementAt(choice))[0];
						for (int j = i; j < popvec.size(); j++)
						{
							CmPopGoodsDef cmp1 = (CmPopGoodsDef) popvec.elementAt(j);
							if (!cmp1.dqid.equals(cmp.dqid))
								break;
							if (!cmp1.ruleid.equals(choicerule))
							{
								popvec.remove(j);
								j--;
							}
						}

						// 重新找需要选择的规则
						i = -1;
						continue;
					}
					else if (cmp.dqinfo.ruleselmode == '1') // 只参加最后一个规则
					{
						// 删除同档期前面的规则,保留最后一个规则
						for (int j = i; j < popvec.size(); j++)
						{
							CmPopGoodsDef cmp1 = (CmPopGoodsDef) popvec.elementAt(j);
							if (!cmp1.dqid.equals(cmp.dqid))
								break;
							if (j + 1 < popvec.size() && ((CmPopGoodsDef) popvec.elementAt(j + 1)).dqid.equals(cmp1.dqid))
							{
								popvec.remove(j);
								j--;
							}
						}

						// 重新找需要选择的规则
						i = -1;
						continue;
					}
					else if (cmp.dqinfo.ruleselmode == '3') // 3-按分组规则选择
					{
					}
					else if (cmp.dqinfo.ruleselmode == '4') // 4-手工选择多个规则
					{
					}
				}
			}

			// 从集合中去掉设置为取消促销的规则
			for (int i = 0; i < popvec.size(); i++)
			{
				CmPopGoodsDef cmp = (CmPopGoodsDef) popvec.elementAt(i);
				if (cmp.popmode == 'N' || cmp.ruleinfo.popmode == 'N')
				{
					popvec.remove(i);
					i--;
				}
			}

			// 按规则的优先级倒序排，优先级大的排前面先执行
			if (popvec.size() > 1)
			{
				boolean sort = false;
				Vector newpopvec = new Vector();
				for (int i = 0; i < popvec.size(); i++)
				{
					CmPopGoodsDef cmp = (CmPopGoodsDef) popvec.elementAt(i);

					// 找到比自己优先级低的规则，并把自己插到该规则前
					int j = 0;
					for (; j < newpopvec.size(); j++)
					{
						CmPopGoodsDef cmp1 = (CmPopGoodsDef) newpopvec.elementAt(j);
						if (cmp.ruleinfo.pri > cmp1.ruleinfo.pri)
							break;
					}
					if (j >= newpopvec.size())
						newpopvec.add(cmp);
					else
					{
						newpopvec.insertElementAt(cmp, j);
						sort = true;
					}
				}
				if (sort)
					goodsCmPop.setElementAt(newpopvec, goodsCmPop.size() - 1);
			}
		}
	}

	// CMPOP促销模型
	public void findGoodsCMPOPInfo(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		if (!SellType.ISSALE(this.saletype))
			return;

		// 券连网使用，查找网上失败后不查询本地
		findGoodsRuleFromCRM(sg, goods, info);

		// 查找商品的促销结果集
		Vector popvec = ((Bstd_DataService) DataService.getDefault()).findCMPOPGoods(saleHead.rqsj, goods, curCustomer != null ? curCustomer.code : "", curCustomer != null ? curCustomer.type : "");
		goodsCmPop.add(popvec);

		filterCMPOPInfo(popvec);
	}

	public Vector findSameGroup(CmPopGoodsDef cmp, int group)
	{
		return ((Bstd_DataService) DataService.getDefault()).findCMPOPGroup(cmp.dqid, cmp.ruleid, group);
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
					if (GlobalInfo.syjDef.issryyy != 'N' || ConfigClass.DebugMode)
					{
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
					}

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

	public void addCmPopDetail(int index, CmPopGoodsDef cmp, double zk)
	{
		addCmPopDetail(index, cmp, zk, false);
	}

	public void addCmPopDetail(int index, CmPopGoodsDef cmp, double zk, boolean giftcond)
	{
		if (!giftcond && zk <= 0)
			return;

		SpareInfoDef sginfo = (SpareInfoDef) goodsSpare.elementAt(index);
		if (sginfo.popzk == null)
			sginfo.popzk = new Vector();
		if (!giftcond)
			sginfo.popzk.add(new String[] { String.valueOf(cmp.cmpopseqno), ManipulatePrecision.doubleToString(zk), cmp.popmemo });
		else
		{
			// 检查条件商品是否已经同序号的促销折扣,没有则要加入
			int i = 0;
			for (; i < sginfo.popzk.size(); i++)
			{
				String[] s = (String[]) sginfo.popzk.elementAt(i);
				if (s[0].equals(String.valueOf(cmp.cmpopseqno)))
					break;
			}
			if (i >= sginfo.popzk.size())
			{
				sginfo.popzk.add(new String[] { String.valueOf(cmp.cmpopseqno), ManipulatePrecision.doubleToString(zk), cmp.popmemo });
			}
		}
	}

	public void addCmGiftDetail(int index, CmPopGoodsDef cmp, CmPopGiftsDef cmgift, double zk)
	{
		if (zk <= 0)
			return;

		SpareInfoDef sginfo = (SpareInfoDef) goodsSpare.elementAt(index);
		if (sginfo.popzk == null)
			sginfo.popzk = new Vector();
		sginfo.popzk.add(new String[] { String.valueOf(cmp.cmpopseqno) + "|" + String.valueOf("G" + cmgift.giftseqno), ManipulatePrecision.doubleToString(zk), cmp.popmemo });
	}

	public void refreshCmPopUI()
	{
		// 重算应收
		calcHeadYsje();

		// 刷新商品列表
		saleEvent.updateTable(getSaleGoodsDisplay());
		saleEvent.table.setSelection(saleEvent.table.getItemCount() - 1);
		saleEvent.table.showSelection();
		saleEvent.setTotalInfo();
		saleEvent.setCurGoodsBigInfo();
	}

	public Vector findCmpopGift(String dqid, String ruleid, String ladderid)
	{
		return ((Bstd_DataService) DataService.getDefault()).findCMPOPGift(dqid, ruleid, ladderid);
	}

	public boolean doCmPopGift(int sgindex, SaleGoodsDef cursg, CmPopGoodsDef cmp, String dqid, String ruleid, String ladderid, int ladderbs)
	{
		Vector giftvec = findCmpopGift(dqid, ruleid, ladderid);// ((Bstd_DataService)
		                                                       // DataService.getDefault()).findCMPOPGift(dqid,
		                                                       // ruleid,
		                                                       // ladderid);
		if (giftvec == null)
			return false;

		boolean havegift = false;
		for (int i = 0; i < giftvec.size(); i++)
		{
			CmPopGiftsDef cmgift = (CmPopGiftsDef) giftvec.elementAt(i);
			if (cmgift.joinmode == 'N')
				continue;

			// 同分组内任选一个赠品
			if (i + 1 < giftvec.size() && ((CmPopGiftsDef) giftvec.elementAt(i + 1)).giftgroup == cmgift.giftgroup)
			{
				// 找出同分组需要任选的赠品集合
				Vector contents = new Vector();
				int j = i;
				for (; j < giftvec.size(); j++)
				{
					CmPopGiftsDef cmgift1 = (CmPopGiftsDef) giftvec.elementAt(j);
					if (cmgift1.giftgroup != cmgift.giftgroup)
						break;
					if (cmgift1.joinmode == 'N')
						continue;
					contents.add(new String[] { String.valueOf(j), cmgift1.giftname, ManipulatePrecision.doubleToString(cmgift1.giftsl, 4, 1, true) });
				}

				// 计算下一个分组赠品的集合位置,循环还要i++,因此i=j-1
				i = j - 1;
				if (contents.size() <= 0)
					continue;

				// 确定任选模式是任选组内某一项赠品还是组内赠品任意XX件
				boolean selmode = false; // 组内赠品任意XX件
				for (j = 0; j < contents.size(); j++)
				{
					// giftsl <= 0 任选XX模式
					if (Convert.toDouble(((String[]) contents.elementAt(j))[2]) > 0)
					{
						selmode = true;
						break;
					}
				}

				// 选择赠品项
				// 任选组内某一项,按达到条件的倍数循环进行任选,每次翻倍可以选择不同的赠品集合
				// 组内赠品任意件,按可促销数量进行循环,每1件都可以是一个集合项
				int choice = 0;
				String[] title = { "序号", "赠品描述", "赠品数量" };
				int[] width = { 60, 460, 100 };
				int maxsl = ladderbs;
				if (!selmode)
					maxsl = (int) cmgift.giftmaxsl * ladderbs;
				j = 0;
				while (j < maxsl)
				{
					if (selmode)
						choice = new MutiSelectForm().open("第" + (sgindex + 1) + "行商品参与的[" + cmp.ruleinfo.rulename + "]活动可分(" + j + "/" + maxsl + ")" + "次选择以下任意一项进行促销", title, width, contents, false, 680, 319, false);
					else
						choice = new MutiSelectForm().open("第" + (sgindex + 1) + "行商品参与的[" + cmp.ruleinfo.rulename + "]活动可促销以下商品任意(" + j + "/" + maxsl + ")件", title, width, contents, false, 680, 319, false);
					if (choice >= 0)
					{
						// 只有一个选择条件,则一次性促销所有翻倍,不存在重新选择赠品规则
						int sl = 1;
						if (contents.size() <= 1)
							sl = ladderbs;

						// 执行赠品处理
						int choicerow = Integer.parseInt(((String[]) contents.elementAt(choice))[0]);
						if (calcCmPopGift(giftvec, choicerow, cursg, cmp, sl))
						{
							havegift = true;

							j += sl;
						}
						else
						{
							if (selmode)
								new MessageBox("你选择的促销活动未能实现,请重新选择促销项进行促销");
						}
					}
					else
					{
						if (new MessageBox("你确定要放弃可选择的促销活动吗？", null, true).verify() == GlobalVar.Key1)
							break;
					}
				}
			}
			else
			{
				// 执行赠品处理
				if (calcCmPopGift(giftvec, i, cursg, cmp, ladderbs))
					havegift = true;
				else if (i <= giftvec.size())
					havegift = true;
			}
		}

		return havegift;
	}

	public boolean calcCmPopGift(Vector giftvec, int index, SaleGoodsDef cursg, CmPopGoodsDef cmp, int ladderbs)
	{
		boolean havegift = false;
		CmPopGiftsDef cmgift = (CmPopGiftsDef) giftvec.elementAt(index);

		// 可进行赠送的数量
		double giftsl = ladderbs * cmgift.giftsl;
		if (cmgift.giftmaxsl > 0 && giftsl > cmgift.giftmaxsl)
			giftsl = cmgift.giftmaxsl;
		if (giftsl <= 0)
			giftsl = 1;

		// 根据赠品类型进行处理
		if (cmgift.gifttype == '1' || cmgift.gifttype == '5') // 已销售的商品,进行打折优惠
		{
			for (int j = 0; j < saleGoods.size(); j++)
			{
				if (giftsl <= 0)
					break;
				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(j);

				// 不匹配商品
				if (!mathingCmPopGift(cmgift, sg))
					continue;

				// 不参加的赠品
				if (isNoJoinGift(giftvec, index, sg))
					continue;

				// 要促销的商品如果是条件商品行则不能作为被促销商品，继续找下一个可优惠商品
				int popindex = -1;
				boolean nopop = false;
				Vector popvec = (Vector) goodsCmPop.elementAt(j);
				for (int i = 0; popvec != null && i < popvec.size(); i++)
				{
					CmPopGoodsDef cmp1 = (CmPopGoodsDef) popvec.elementAt(i);
					if ((cmp1.ruleinfo.summode == 0 && cmp1.cmpopseqno == cmp.cmpopseqno) || (cmp1.ruleinfo.summode != 0 && cmp1.dqid.equals(cmp.dqid) && cmp1.ruleid.equals(cmp.ruleid)))
					{
						popindex = i;
						if (cmp1.used)
							nopop = true;
						break;
					}
				}
				if (cmgift.gifttype == '1' && nopop)
					continue;
				if (cmgift.gifttype == '5' && !nopop)
					continue;

				// 计算可参与数量,如果本行商品数量大于可参与促销数量,拆分商品行
				double sl = sg.sl;
				if (ManipulatePrecision.doubleCompare(sl, giftsl, 4) > 0)
				{
					// 按剩余可参与数量计算,并拆分商品行
					sl = giftsl;
					SplitSaleGoodsRow(j, sl);
				}

				// 被促销的商品不能做为促销条件继续计算本促销
				if (popindex >= 0 && popvec != null)
					((CmPopGoodsDef) popvec.elementAt(popindex)).used = true;

				// 促销打折
				double zke = 0;
				if (cmgift.popmode == '1' && sg.jg > cmgift.poplsj && cmgift.poplsj >= 0)
				{
					if (isMemberHyjMode() && cmgift.poplsj > cmgift.pophyj && cmgift.pophyj > 0)
						zke = (sg.jg - cmgift.pophyj) * sl;
					else
						zke = (sg.jg - cmgift.poplsj) * sl;
					zke = ManipulatePrecision.doubleConvert(zke - sg.hjzk);
				}
				else if (cmgift.popmode == '2' && 1 > cmgift.poplsj && cmgift.poplsj >= 0)
				{
					if (isMemberHyjMode() && cmgift.poplsj > cmgift.pophyj && cmgift.pophyj > 0)
						zke = (sg.jg * sl) * (1 - cmgift.pophyj);
					else
						zke = (sg.jg * sl) * (1 - cmgift.poplsj);
					zke = ManipulatePrecision.doubleConvert(zke - sg.hjzk);
				}
				else if (cmgift.popmode == '3' && sg.jg > cmgift.poplsj && cmgift.poplsj > 0)
				{
					if (isMemberHyjMode() && cmgift.pophyj > cmgift.poplsj && cmgift.pophyj > 0)
						zke = cmgift.pophyj * sl;
					else
						zke = cmgift.poplsj * sl;
					if (sg.hjje - sg.hjzk < zke)
						zke = ManipulatePrecision.doubleConvert(sg.hjje - sg.hjzk);
				}

				// 促销折扣
				if (zke > 0)
				{
					// 减少可送数量
					giftsl = ManipulatePrecision.doubleConvert(giftsl - sl, 4, 1);

					// 记录商品折扣
					double oldzszke = sg.zszke;
					sg.zszke += zke;
					sg.zszke = getConvertRebate(j, sg.zszke);
					sg.zsdjbh = String.valueOf(cmp.cmpopseqno) + "|" + String.valueOf("G" + cmgift.giftseqno);
					zke = sg.zszke - oldzszke;
					getZZK(sg, CommonMethod.getTraceInfo(), true);
					markPopGoods(sg, cmp);
					// 记录促销折扣明细
					addCmGiftDetail(j, cmp, cmgift, zke);
					havegift = true;
				}
			}
		}
		else if (cmgift.gifttype == '2')
		{
			// 是赠送单品则直接查找商品,否则要求收银员输入商品
			GoodsDef goodsDef = null;
			SaleGoodsDef gift = null;
			if (cmgift.codemode == '1' || cmgift.codemode == '9')
			{
				goodsDef = findGoodsInfo(cmgift.codeid, cursg.yyyh, cmgift.codegz, "", false, null, false);
				if (goodsDef == null)
				{
					new MessageBox("找不到商品[" + cmgift.codeid + "]的信息\n\n不能赠送此商品!");
					return false;
				}

				// 生成商品
				if (cmgift.popmode == '0')
				{
					goodsDef.lsj = cmgift.poplsj;
					gift = goodsDef2SaleGoods(goodsDef, cursg.yyyh, giftsl, goodsDef.lsj, giftsl * goodsDef.lsj, false);
				}
				else
				{
					gift = goodsDef2SaleGoods(goodsDef, cursg.yyyh, giftsl, goodsDef.lsj, giftsl * goodsDef.lsj, false);

					if (cmgift.popmode == '1' && gift.jg > cmgift.poplsj && cmgift.poplsj >= 0)
						gift.zszke = gift.hjje - cmgift.poplsj * giftsl;
					else if (cmgift.popmode == '2' && 1 > cmgift.poplsj && cmgift.poplsj >= 0)
						gift.zszke = gift.hjje * (1 - cmgift.poplsj);
					else if (cmgift.popmode == '3' && gift.jg > cmgift.poplsj && cmgift.poplsj > 0)
						gift.zszke = cmgift.poplsj * giftsl;
				}
				gift.zszkfd = cmgift.poplsjzkfd;
				gift.zsdjbh = String.valueOf(cmp.cmpopseqno) + "|" + String.valueOf("G" + cmgift.giftseqno);
				gift.flag = '5'; // 普通赠品
				getZZK(gift, CommonMethod.getTraceInfo(), true);

				// 提示顾客是否购买
				String tips = "免费获赠";
				if (gift.hjje - gift.hjzk > 0)
					tips = "用 " + ManipulatePrecision.doubleToString(gift.hjje - gift.hjzk) + " 元购买";

				if (new MessageBox("有促销活动可" + tips + "\n\n" + ManipulatePrecision.doubleToString(giftsl, 4, 1, true) + " X [" + cmgift.codeid + "]" + goodsDef.name + "\n\n顾客需要吗?", null, true).verify() != GlobalVar.Key2)
				{
					// 不允许销红,检查库存
					if ((SellType.ISSALE(saletype) && GlobalInfo.sysPara.isxh != 'Y' && goodsDef.isxh != 'Y'))
					{
						// 统计商品销售数量
						double hjsl = giftsl + calcSameGoodsQuantity(goodsDef.code, goodsDef.gz);
						if (goodsDef.kcsl < hjsl)
						{
							if (GlobalInfo.sysPara.xhisshowsl == 'Y')
								new MessageBox("该商品库存为 " + ManipulatePrecision.doubleToString(goodsDef.kcsl) + "\n库存不足,不能销售");
							else
								new MessageBox("该商品库存不足,不能销售");

							return false;
						}
					}

					// 增加到商品列表,加入的赠品无促销
					addSaleGoodsObject(gift, goodsDef, getGoodsSpareInfo(goodsDef, gift));
					goodsCmPop.set(saleGoods.size() - 1, null);

					// 记录促销折扣明细
					if (gift.zszke > 0)
						addCmGiftDetail(saleGoods.size() - 1, cmp, cmgift, gift.zszke);
					havegift = true;
				}
			}
			else
			{
				double yssl = 0;
				StringBuffer sb = new StringBuffer();
				do
				{
					String text = null;
					if (cmgift.codemode == '2')
						text = "[" + cmgift.codeid + "]柜组";
					else if (cmgift.codemode == '3')
						text = "[" + cmgift.codeid + "]品牌";
					else if (cmgift.codemode == '4')
						text = "[" + cmgift.codeid + "]品类";
					else if (cmgift.codemode == '5')
						text = "[" + cmgift.codeid + "]柜组[" + cmgift.codegz + "]品牌";
					else if (cmgift.codemode == '6')
						text = "[" + cmgift.codeid + "]柜组[" + cmgift.codegz + "]品类";
					else if (cmgift.codemode == '7')
						text = "[" + cmgift.codeid + "]品牌[" + cmgift.codegz + "]品类";
					else if (cmgift.codemode == '8')
						text = "[" + cmgift.codeid + "]柜组[" + cmgift.codegz + "]品牌[" + cmgift.codeuid + "]品类";
					else if (cmgift.codemode == '0')
						text = "全场";
					else
						break;

					// 促销提示描述
					String poptips = null;
					String zkjtips = null;
					if (cmgift.popmode == 0)
						zkjtips = "可用 " + ManipulatePrecision.doubleToString(cmgift.poplsj) + " 元";
					else if (cmgift.popmode == '1' && cmgift.poplsj >= 0)
						zkjtips = "可用 " + ManipulatePrecision.doubleToString(cmgift.poplsj * (giftsl - yssl)) + " 元";
					else if (cmgift.popmode == '2' && 1 > cmgift.poplsj && cmgift.poplsj >= 0)
						zkjtips = "可用 " + ManipulatePrecision.doubleToString(cmgift.poplsj * 100) + "% 的折扣";
					else if (cmgift.popmode == '3' && cmgift.poplsj > 0)
						zkjtips = "可减价 " + ManipulatePrecision.doubleToString(cmgift.poplsj * (giftsl - yssl)) + " 元";
					else
						zkjtips = "可用原价";
					if (cmgift.poppfj > 0)
					{
						poptips = "有促销活动" + zkjtips + "购买" + text + "的\n价值 " + ManipulatePrecision.doubleToString(cmgift.poppfj) + " 元以内的任意 " + ManipulatePrecision.doubleToString((giftsl - yssl), 4, 1, true) + " 件商品";
					}
					else
					{
						poptips = "有促销活动" + zkjtips + "购买" + text + "的\n任意 " + ManipulatePrecision.doubleToString((giftsl - yssl), 4, 1, true) + " 件商品";
					}
					if (new TextBox().open("请输入" + text + "的某件商品", "商品编码", poptips, sb, TextBox.AllInput))
					{
						double bcsl = 1;
						String barcode = sb.toString();
						if (barcode.indexOf("*") > 0 && barcode.indexOf("*") < barcode.length() - 1)
						{
							bcsl = Convert.toDouble(barcode.substring(0, barcode.indexOf("*")));
							barcode = barcode.substring(barcode.indexOf("*") + 1);
							if (bcsl <= 0)
								bcsl = 1;
							if (ManipulatePrecision.doubleCompare(yssl + bcsl, giftsl, 4) > 0)
								bcsl = ManipulatePrecision.doubleConvert(giftsl - yssl, 4, 1);
						}

						if (cmgift.codemode == '2' || cmgift.codemode == '5' || cmgift.codemode == '6' || cmgift.codemode == '8')
							goodsDef = findGoodsInfo(barcode, cursg.yyyh, cmgift.codeid, "", false, null, true);
						else
							goodsDef = findGoodsInfo(barcode, cursg.yyyh, "", "", false, null, false);
						if (goodsDef == null)
							continue;

						// 检查商品是否匹配
						if (!mathingCmPopGift(cmgift, goodsDef))
						{
							new MessageBox("该商品不是" + text + "的商品");
							continue;
						}

						// 检查商品价值是否匹配
						if (cmgift.poppfj > 0 && goodsDef.lsj <= 0)
							goodsDef.lsj = cmgift.poppfj;
						if (cmgift.poppfj > 0 && goodsDef.lsj > cmgift.poppfj)
						{
							new MessageBox("该商品价值 " + ManipulatePrecision.doubleToString(goodsDef.lsj) + " 元\n\n促销活动只能赠价值 " + ManipulatePrecision.doubleToString(cmgift.poppfj) + " 元以内的商品");
							continue;
						}
						else
						{
							// 不允许销红,检查库存
							if ((SellType.ISSALE(saletype) && GlobalInfo.sysPara.isxh != 'Y' && goodsDef.isxh != 'Y'))
							{
								// 统计商品销售数量
								double hjsl = bcsl + calcSameGoodsQuantity(goodsDef.code, goodsDef.gz);
								if (goodsDef.kcsl < hjsl)
								{
									if (GlobalInfo.sysPara.xhisshowsl == 'Y')
										new MessageBox("该商品库存为 " + ManipulatePrecision.doubleToString(goodsDef.kcsl) + "\n库存不足,不能销售");
									else
										new MessageBox("该商品库存不足,不能销售");

									continue;
								}
							}

							// 生成商品
							if (cmgift.popmode == '0')
							{
								goodsDef.lsj = cmgift.poplsj;
								gift = goodsDef2SaleGoods(goodsDef, cursg.yyyh, bcsl, goodsDef.lsj, bcsl * goodsDef.lsj, false);
							}
							else
							{
								gift = goodsDef2SaleGoods(goodsDef, cursg.yyyh, bcsl, goodsDef.lsj, bcsl * goodsDef.lsj, false);

								if (cmgift.popmode == '1' && gift.jg > cmgift.poplsj && cmgift.poplsj >= 0)
									gift.zszke = gift.hjje - cmgift.poplsj * bcsl;
								else if (cmgift.popmode == '2' && 1 > cmgift.poplsj && cmgift.poplsj >= 0)
									gift.zszke = gift.hjje * (1 - cmgift.poplsj);
								else if (cmgift.popmode == '3' && gift.jg > cmgift.poplsj && cmgift.poplsj > 0)
									gift.zszke = cmgift.poplsj * bcsl;
							}
							gift.zszkfd = cmgift.poplsjzkfd;
							gift.zsdjbh = String.valueOf(cmp.cmpopseqno) + "|" + String.valueOf("G" + cmgift.giftseqno);
							gift.flag = '5'; // 普通赠品
							getZZK(gift, CommonMethod.getTraceInfo(), true);

							// 不参加的赠品
							if (isNoJoinGift(giftvec, index, gift))
							{
								new MessageBox("该商品是不参与活动的例外商品");
								continue;
							}

							// 增加到商品列表,加入的赠品无促销
							addSaleGoodsObject(gift, goodsDef, getGoodsSpareInfo(goodsDef, gift));
							goodsCmPop.set(saleGoods.size() - 1, null);

							// 记录促销折扣明细
							if (gift.zszke > 0)
								addCmGiftDetail(saleGoods.size() - 1, cmp, cmgift, gift.zszke);
							havegift = true;
							yssl = ManipulatePrecision.add(yssl, bcsl);

							// 促销数量足够则跳出编码输入循环
							if (ManipulatePrecision.doubleCompare(yssl, giftsl, 4) >= 0)
								break;

							// 刷新UI界面,显示新加入商品列表的商品
							refreshCmPopUI();
						}
					}
					else
					{
						if (new MessageBox("顾客放弃促销活动赠送的商品吗？", null, true).verify() == GlobalVar.Key1)
						{
							// 跳出编码输入循环
							break;
						}
					}
				}
				while (true);
			}
		}
		else if (cmgift.gifttype == '3')
		{
			new MessageBox("有促销活动可到服务台领取礼品\n\n" + ManipulatePrecision.doubleToString(giftsl, 4, 1, true) + " X " + cmgift.giftname);

			GoodsDef goodsDef = new GoodsDef();
			if (cmgift.codemode == '1')
				goodsDef.barcode = cmgift.codeid;
			else
				goodsDef.barcode = "GIFT" + saleGoods.size();
			goodsDef.code = goodsDef.barcode;
			goodsDef.gz = cmgift.codegz;
			goodsDef.uid = cmgift.codeuid;
			goodsDef.name = "请到服务台领取" + cmgift.giftname;
			goodsDef.type = '1';
			goodsDef.unit = "件";
			goodsDef.lsj = 0;

			SaleGoodsDef gift = goodsDef2SaleGoods(goodsDef, cursg.yyyh, giftsl, goodsDef.lsj, 0, false);
			gift.zszke = gift.hjje;
			gift.zszkfd = cmgift.poplsjzkfd;
			gift.zsdjbh = String.valueOf(cmp.cmpopseqno) + "|" + String.valueOf("G" + cmgift.giftseqno);
			gift.flag = '1'; // 赠品联商品,顾客凭赠品联到服务台领取赠品
			getZZK(gift, CommonMethod.getTraceInfo(), true);

			addSaleGoodsObject(gift, null, null);
			havegift = true;
		}
		else if (cmgift.gifttype == '4')
		{
			// 调用过程得到本小票可送券并打印
		}

		// 发生促销刷新界面
		if (havegift)
			refreshCmPopUI();

		return havegift;
	}

	public boolean isNoJoinGift(Vector giftvec, int index, SaleGoodsDef gift)
	{
		CmPopGiftsDef curcmg = (CmPopGiftsDef) giftvec.elementAt(index);
		for (int j = index; j < giftvec.size(); j++)
		{
			CmPopGiftsDef cmgift = (CmPopGiftsDef) giftvec.elementAt(j);
			if (cmgift.giftgroup != curcmg.giftgroup)
				break;
			if (cmgift.joinmode != 'N')
				continue;

			// 商品符合不参加条件
			if (mathingCmPopGift(cmgift, gift)) { return true; }
		}

		return false;
	}

	public boolean mathingCmPopGift(CmPopGiftsDef cmgift, SaleGoodsDef sg)
	{
		if ((cmgift.codemode == '0') || (cmgift.codemode == '1' && sg.code.equals(cmgift.codeid) && (sg.gz.equals(cmgift.codegz) || cmgift.codegz == null || cmgift.codegz.equals("%") || cmgift.codegz.equals("")) && (sg.uid.equals(cmgift.codeuid) || cmgift.codeuid == null || cmgift.codeuid.equals("%") || cmgift.codeuid.equals(""))) || (cmgift.codemode == '2' && sg.gz.equals(cmgift.codeid)) || (cmgift.codemode == '3' && sg.ppcode.equals(cmgift.codeid)) || (cmgift.codemode == '4' && sg.catid.equals(cmgift.codeid)) || (cmgift.codemode == '5' && sg.gz.equals(cmgift.codeid) && sg.ppcode.equals(cmgift.codegz)) || (cmgift.codemode == '6' && sg.gz.equals(cmgift.codeid) && sg.catid.equals(cmgift.codegz)) || (cmgift.codemode == '7' && sg.ppcode.equals(cmgift.codeid) && sg.catid.equals(cmgift.codegz)) || (cmgift.codemode == '8' && sg.gz.equals(cmgift.codeid) && sg.ppcode.equals(cmgift.codegz) && sg.catid.equals(cmgift.codeuid)) || (cmgift.codemode == '9' && sg.barcode.equals(cmgift.codeid) && (sg.gz.equals(cmgift.codegz) || cmgift.codegz == null || cmgift.codegz.equals("%") || cmgift.codegz.equals(""))))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public void markPopGoods(SaleGoodsDef sgd, CmPopGoodsDef cmp)
	{

	}

	public boolean mathingCmPopGift(CmPopGiftsDef cmgift, GoodsDef gd)
	{
		if ((cmgift.codemode == '0') || (cmgift.codemode == '1' && gd.code.equals(cmgift.codeid) && (gd.gz.equals(cmgift.codegz) || cmgift.codegz == null || cmgift.codegz.equals("%") || cmgift.codegz.equals("")) && (gd.uid.equals(cmgift.codeuid) || cmgift.codeuid == null || cmgift.codeuid.equals("%") || cmgift.codeuid.equals(""))) || (cmgift.codemode == '2' && gd.gz.equals(cmgift.codeid)) || (cmgift.codemode == '3' && gd.ppcode.equals(cmgift.codeid)) || (cmgift.codemode == '4' && gd.catid.equals(cmgift.codeid)) || (cmgift.codemode == '5' && gd.gz.equals(cmgift.codeid) && gd.ppcode.equals(cmgift.codegz)) || (cmgift.codemode == '6' && gd.gz.equals(cmgift.codeid) && gd.catid.equals(cmgift.codegz)) || (cmgift.codemode == '7' && gd.ppcode.equals(cmgift.codeid) && gd.catid.equals(cmgift.codegz)) || (cmgift.codemode == '8' && gd.gz.equals(cmgift.codeid) && gd.ppcode.equals(cmgift.codegz) && gd.catid.equals(cmgift.codeuid)) || (cmgift.codemode == '9' && gd.barcode.equals(cmgift.codeid) && (gd.gz.equals(cmgift.codegz) || cmgift.codegz == null || cmgift.codegz.equals("%") || cmgift.codegz.equals(""))))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	// 删除CMPOP促销结果
	public void delCmPop()
	{
		// 恢复所有商品的促销计算标志
		for (int i = 0; i < goodsCmPop.size(); i++)
		{
			Vector popvec = (Vector) goodsCmPop.elementAt(i);
			if (popvec == null)
				continue;
			for (int j = 0; j < popvec.size(); j++)
			{
				CmPopGoodsDef cmp = (CmPopGoodsDef) popvec.elementAt(j);

				// 不累计的促销允许立即计算折扣的促销,则找到商品后立即计算促销折扣,付款时不处理
				if (cmp.ruleinfo.summode == '0')
					continue;

				// 未使用
				cmp.used = false;
			}
		}

		// 取消赠品及折扣
		boolean havedel = false;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);

			// 删除赠送促销产生的商品
			if (saleGoodsDef.flag == '1' || saleGoodsDef.flag == '5' || goodsAssistant.elementAt(i) == null)
			{
				havedel = true;

				delSaleGoodsObject(i);
				getDeleteGoodsDisplay(i, saleGoodsDef);
				i--;
				continue;
			}

			if (saleGoodsDef.zszke > 0)
				havedel = true;

			// 恢复按下付款键时计算的折扣
			saleGoodsDef.zsdjbh = null;
			saleGoodsDef.zszke = 0;
			saleGoodsDef.zszkfd = 0;
			getZZK(saleGoodsDef, CommonMethod.getTraceInfo(), true);

			markPopGoods(saleGoodsDef, null);
		}

		// 恢复商品信息
		if (havedel)
			delCmPopReadData();

		// 恢复了商品折扣刷新界面
		if (havedel)
			refreshCmPopUI();
	}

	public boolean doCmPop(int sgindex)
	{
		// 先总是无满减规则方式的付款
		isPreparePay = payNormal;

		// 不参与促销计算的交易类型
		if (SellType.NOPOP(saletype) || !SellType.ISSALE(saletype) || SellType.ISEARNEST(saletype) || SellType.ISPREPARETAKE(saletype)) { return false; }

		// 先备份当前商品信息,以便放弃时付款时恢复
		doCmPopWriteData();

		// 对goodsCmPop所有的促销按优先级排序,优先级大的先执行
		// 先把商品同档期的所有促销执行完再执行下一个档期各商品的所有促销
		Vector dqvec = new Vector();
		for (int i = 0; i < goodsCmPop.size(); i++)
		{
			Vector popvec = (Vector) goodsCmPop.elementAt(i);
			for (int j = 0; popvec != null && j < popvec.size(); j++)
			{
				CmPopGoodsDef cmp = (CmPopGoodsDef) popvec.elementAt(j);

				// 是否已加入到档期列表
				int n = 0;
				for (n = 0; n < dqvec.size(); n++)
				{
					String dq = (String) dqvec.elementAt(n);
					String[] s = dq.split(",");
					if (s[0].equals(cmp.dqid)) // 先比较档期ID，若档期ID相同再跳出来比优先级
						break;
				}
				if (n >= dqvec.size())
				{
					for (n = 0; n < dqvec.size(); n++)
					{
						String dq = (String) dqvec.elementAt(n);
						String[] s = dq.split(",");
						int pri = Convert.toInt(s[1]);
						// 当前cmp优先级若大于dqvec中的或优先级相等或当当前cmp档期id大于dqvec中优先级则跳出加入到当前n处
						if (cmp.dqinfo.pri > pri || (cmp.dqinfo.pri == pri && cmp.dqinfo.dqid.compareTo(s[0]) > 0))
						{
							break;
						}
					}
					if (n >= dqvec.size()) // 当前cmp若比dqvec中的优先级低，则加到dqvec尾
						dqvec.add(cmp.dqinfo.dqid + "," + cmp.dqinfo.pri);
					else
						// 当前cmp的优先级若比dqvec中某个大，则加入到当前n处
						dqvec.insertElementAt(cmp.dqinfo.dqid + "," + cmp.dqinfo.pri, n);
				}
			}
		}

		// 计算需要进行累计的促销所产生的折扣
		boolean havepop = false;
		for (int n = 0; n < dqvec.size(); n++)
		{
			// 此时的dqvec已经按优先级高低排序了
			String dqid = ((String) dqvec.elementAt(n)).split(",")[0];
			// 在goodsCmPop中查找与dqid相同的促销进行计算
			for (int i = 0; i < goodsCmPop.size(); i++)
			{
				// 指定执行某行商品的累计促销
				if (sgindex >= 0 && i != sgindex) // sgindex 传入的是-1
					continue;

				Vector popvec = (Vector) goodsCmPop.elementAt(i);
				for (int j = 0; popvec != null && j < popvec.size(); j++)
				{
					CmPopGoodsDef cmp = (CmPopGoodsDef) popvec.elementAt(j);

					// 不累计的促销允许立即计算折扣的促销,则找到商品后立即计算促销折扣,付款时不处理
					if (cmp.ruleinfo.summode == '0')
						continue;

					// 按档期顺序执行
					if (!cmp.dqid.equals(dqid))
						continue;

					// 计算促销折扣
					// 将goodsCmpop,popvec，cmp当前位置传入
					// i,j可定位当前计算的促销在goodsCmpop中的位置

					zkLogger.log("Start doCmPop-> SaleGoodsIndex:" + i + " CmpGoodsIndex:" + j);
					zkLogger.log("CmpInfo-> Dqinfo:" + cmp.strdqinfo + " RuleInfo:" + cmp.strruleinfo + " LadderInfo:" + cmp.strruleladder + " Seqno:" + cmp.cmpopseqno + " Codeid:" + cmp.codeid);

					if (calcGoodsCMPOPRebate(i, cmp, j))
					{
						// 调试模式提示促销计算结果，便于了解促销计算规则
						if (ConfigClass.DebugMode)
						{
							refreshCmPopUI();

							new MessageBox("刚计算完的促销是第 " + (i + 1) + " 行商品在\n\n[" + dqid + " - " + cmp.dqinfo.name + "]促销档期内的\n\n[" + cmp.ruleinfo.ruleid + " - " + cmp.ruleinfo.rulename + "]促销规则\n\n请核对促销活动的结果");
						}

						// 设置存在CM促销标记
						havepop = true;

						// 如果计算出有促销,但又未标记促销,则说明本次自身被除外,需要再计算一次
						if (!cmp.used)
							j--;
					}
					zkLogger.log("Stop doCmPop-> SaleGoodsIndex:" + i + " CmpGoodsIndex:" + j, "\r\n");

					// 计算促销时进了预付款,预付款已足够,交易已完成
					if (doCmPopExit)
						return havepop;
				}
			}
		}

		// 计算出CM促销折扣,重算小票
		if (havepop)
			refreshCmPopUI();

		return havepop;
	}

	public void calcGoodsVIPRebate(int index)
	{
		zkLogger.log("Start calcGoodsVIPRebate-> SaleGoodsIndex:" + index);

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// ishy='V'、'Y'表示采用会员VIP折扣模式进行会员优惠,商品允许VIP折扣
		if (goodsDef.isvipzk == 'Y' && isMemberVipMode())
		{
			// 获取VIP折扣率定义
			CustomerVipZklDef zklDef = getGoodsVIPZKL(index);

			// 有VIP折扣率
			if (zklDef != null)
			{
				// 本笔可销售数量
				double sl = saleGoodsDef.sl;

				// 检查会员限量,限量总是按最小单位定义
				if (zklDef.maxslmode != '0' && zklDef.maxsl > 0)
				{
					// 联网检查会员已购买数量
					if (zklDef.maxslmode != '1')
					{
						zklDef.maxsl = NetService.getDefault().findVIPMaxSl("VIP", curCustomer.code, curCustomer.type, zklDef.seqno, saleGoodsDef.code, saleGoodsDef.gz, saleGoodsDef.uid);
						if (zklDef.maxsl < 0)
							zklDef.maxsl = 0;
					}

					// 计算本笔中已经进行限量折扣的商品数量
					double zsl = 0;
					for (int i = 0; i < saleGoods.size(); i++)
					{
						SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);
						// sg.hyzke 这个用于标志已经参与限量打折的商品个数
						if (i != index && sg.code.equals(saleGoodsDef.code) && sg.gz.equals(saleGoodsDef.gz) && sg.uid.equals(saleGoodsDef.uid) && sg.hyzke > 0)
						{
							zsl += ManipulatePrecision.doubleConvert(sg.sl, 2, 1);
						}
					}

					if (zsl >= zklDef.maxsl)
					{
						sl = 0;
					}
					else
					{
						// 剩余能够打折的数量
						double sysl = ManipulatePrecision.doubleConvert(zklDef.maxsl - zsl, 2, 1);
						// sl = Math.min(sysl, saleGoodsDef.sl);
						// 如果是电子秤不进行拆行

						if (saleGoodsDef.flag == '2' && GlobalInfo.sysPara.issplitdzc == 'Y')
						{
							if (ManipulatePrecision.doubleCompare(saleGoodsDef.sl, sysl, 4) > 0)
							{
								sl = 0;
							}
							else
							{
								sl = saleGoodsDef.sl;
							}
						}
						else
						{
							sl = Math.min(sysl, saleGoodsDef.sl);
						}

					}

					if (sl <= 0)
					{
						sl = 0;
					}
					else
					{
						// 将会员价记录下来
						if (saleGoodsDef.flag == '2' && zklDef.zkmode == '1')
							saleGoodsDef.num4 = zklDef.zkl;
					}
				}

				if (GlobalInfo.sysPara.isVipMaxSlMsg == 'Y')
				{
					if (ManipulatePrecision.doubleCompare(saleGoodsDef.sl, sl, 4) > 0)
					{
						if (zklDef.maxsl > 0)
						{
							new MessageBox(saleGoodsDef.code + "[" + saleGoodsDef.name + "]\n\n" + "商品已经超出会员限量: " + ManipulatePrecision.doubleToString(zklDef.maxsl, 4, 1, true) + "\n\n" + "超出的部分以原价进行销售");
						}
						else
						{
							new MessageBox(saleGoodsDef.code + "[" + saleGoodsDef.name + "]\n\n" + "商品已经超出会员限量,当前商品以原价进行销售");
						}
					}
				}

				if (sl <= 0)
					return;

				// 拆分商品行
				if (ManipulatePrecision.doubleCompare(saleGoodsDef.sl, sl, 4) > 0)
					SplitSaleGoodsRow(index, sl);

				// 进行折上折
				if (zklDef.iszsz == 'Y' || zklDef.iszsz == 'A')
				{
					boolean iscontinue = true;
					if (zklDef.iszsz == 'A' && zklDef.zkl > 0)
					{
						// 如果已打折扣已经大于该会员最大可以打的折扣,则不进行会员打折
						if (ManipulatePrecision.doubleCompare(getZZK(saleGoodsDef, CommonMethod.getTraceInfo(), true), (1 - zklDef.zkl) * sl * saleGoodsDef.jg, 2) > 0)
						{
							iscontinue = false;
						}
					}

					if (iscontinue)
					{
						// 得到商品目前已打折比率
						double cjjdn = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * zklDef.zklareadn);
						double cjjup = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * zklDef.zklareaup);

						// 计算折扣区间
						double vipzkl = 1;
						if (saleGoodsDef.hjje - getZZK(saleGoodsDef, CommonMethod.getTraceInfo(), true) >= cjjdn && saleGoodsDef.hjje - getZZK(saleGoodsDef, CommonMethod.getTraceInfo(), true) <= cjjup) // 折扣在区间内
						{
							vipzkl = zklDef.inareazkl;
						}
						else if (saleGoodsDef.hjje - getZZK(saleGoodsDef, CommonMethod.getTraceInfo(), true) > cjjup) // 折扣在区间上
						{
							vipzkl = zklDef.upareazkl;
						}
						else if (saleGoodsDef.hjje - getZZK(saleGoodsDef, CommonMethod.getTraceInfo(), true) < cjjdn) // 折扣在区间下
						{
							vipzkl = zklDef.dnareazkl;
						}

						// 根据折扣模式计算折扣
						if (zklDef.zkmode == '1' && saleGoodsDef.jg > vipzkl && vipzkl >= 0)
						{
							// 按指定价格成交
							if (ManipulatePrecision.doubleCompare(getZZK(saleGoodsDef, CommonMethod.getTraceInfo(), true), (saleGoodsDef.jg - vipzkl) * sl, 2) < 0)
							{
								saleGoodsDef.hyzke += ManipulatePrecision.doubleConvert((saleGoodsDef.jg - vipzkl) * sl - getZZK(saleGoodsDef, CommonMethod.getTraceInfo(), true), 2, 1);
								setGoodsVIPRebateInfo(saleGoodsDef, zklDef);
							}
						}
						else if (zklDef.zkmode == '3' && vipzkl > 0)
						{
							// 成交价基础上再减价金额,再减不能超过商品价值
							double zke = ManipulatePrecision.doubleConvert((vipzkl * sl), 2, 1);
							if (saleGoodsDef.hjje - (getZZK(saleGoodsDef, CommonMethod.getTraceInfo(), true) + zke) < 0)
								zke = saleGoodsDef.hjje - getZZK(saleGoodsDef, CommonMethod.getTraceInfo(), true);
							saleGoodsDef.hyzke += zke;
							setGoodsVIPRebateInfo(saleGoodsDef, zklDef);
						}
						else if (1 > vipzkl && vipzkl >= 0)
						{
							// 成交价基础上再打折指定折扣率
							saleGoodsDef.hyzke += ManipulatePrecision.doubleConvert((1 - vipzkl) * (saleGoodsDef.hjje - getZZK(saleGoodsDef, CommonMethod.getTraceInfo(), true)) * (sl / saleGoodsDef.sl), 2, 1);
							setGoodsVIPRebateInfo(saleGoodsDef, zklDef);
						}

						if (zklDef.iszsz == 'A' && zklDef.zkl > 0)
						{
							// 如果已打折扣已经大于该会员最大可以打的折扣,则不进行会员打折
							if (ManipulatePrecision.doubleCompare(getZZK(saleGoodsDef, CommonMethod.getTraceInfo(), true), (1 - zklDef.zkl) * sl * saleGoodsDef.jg, 2) > 0)
							{
								saleGoodsDef.hyzke = ManipulatePrecision.sub(saleGoodsDef.hyzke, ManipulatePrecision.doubleConvert(getZZK(saleGoodsDef, CommonMethod.getTraceInfo(), true) - ((1 - zklDef.zkl) * sl * saleGoodsDef.jg), 2, 1));
							}
						}
					}
				}
				else
				{
					// 不折上折时，取商品VIP折扣和综合折扣较低者
					if (zklDef.zkmode == '1' && saleGoodsDef.jg > zklDef.zkl && zklDef.zkl >= 0)
					{
						// 指定价格
						if (ManipulatePrecision.doubleCompare(getZZK(saleGoodsDef, CommonMethod.getTraceInfo(), true), (saleGoodsDef.jg - zklDef.zkl) * sl, 2) < 0)
						{
							// 清空其他折扣,只保留会员折扣
							clearGoodsAllRebate(index);

							// 原价和新价的差额记折扣
							saleGoodsDef.hyzke += ManipulatePrecision.doubleConvert((saleGoodsDef.jg - zklDef.zkl) * sl - getZZK(saleGoodsDef, CommonMethod.getTraceInfo(), true), 2, 1);
							setGoodsVIPRebateInfo(saleGoodsDef, zklDef);

						}
					}
					else if (zklDef.zkmode == '3' && zklDef.zkl > 0)
					{
						// 指定减价金额
						if (ManipulatePrecision.doubleCompare(getZZK(saleGoodsDef, CommonMethod.getTraceInfo(), true), zklDef.zkl * sl, 2) < 0)
						{
							// 清空其他折扣,只保留会员折扣
							clearGoodsAllRebate(index);

							// 减价金额不能超过商品价值
							double zke = ManipulatePrecision.doubleConvert(zklDef.zkl * sl);
							if (saleGoodsDef.hjje - zke < 0)
								zke = saleGoodsDef.hjje;
							saleGoodsDef.hyzke += ManipulatePrecision.doubleConvert(zke - getZZK(saleGoodsDef, CommonMethod.getTraceInfo(), true), 2, 1);
							setGoodsVIPRebateInfo(saleGoodsDef, zklDef);
						}
					}
					else if (zklDef.zkmode == '2' && 1 > zklDef.zkl && zklDef.zkl >= 0)
					{
						// 指定折扣率
						if (ManipulatePrecision.doubleCompare(getZZK(saleGoodsDef, CommonMethod.getTraceInfo(), true), (1 - zklDef.zkl) * sl * saleGoodsDef.jg, 2) < 0)
						{
							// 清空其他折扣,只保留会员折扣
							clearGoodsAllRebate(index);

							saleGoodsDef.hyzke += ManipulatePrecision.doubleConvert((1 - zklDef.zkl) * sl * saleGoodsDef.jg - getZZK(saleGoodsDef, CommonMethod.getTraceInfo(), true), 2, 1);
							setGoodsVIPRebateInfo(saleGoodsDef, zklDef);
						}
					}
				}
			}
		}

	}

	public boolean doCmPopWriteData()
	{
		FileOutputStream f = null;

		try
		{
			String name = ConfigClass.LocalDBPath + "/Cmpop.dat";

			f = new FileOutputStream(name);
			ObjectOutputStream s = new ObjectOutputStream(f);

			// 将交易对象写入对象文件
			s.writeObject(saleGoods);
			s.writeObject(goodsAssistant);
			s.writeObject(goodsSpare);
			s.writeObject(goodsCmPop);

			s.flush();
			s.close();
			f.close();
			s = null;
			f = null;

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
		finally
		{
			try
			{
				if (f != null)
					f.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public boolean delCmPopReadData()
	{
		FileInputStream f = null;

		try
		{
			String name = ConfigClass.LocalDBPath + "/Cmpop.dat";
			if (!new File(name).exists())
				return true;

			f = new FileInputStream(name);
			ObjectInputStream s = new ObjectInputStream(f);

			// 读交易对象
			Vector saleGoods1 = (Vector) s.readObject();
			Vector assistant = (Vector) s.readObject();
			Vector spare1 = (Vector) s.readObject();
			Vector goodsCmPop1 = (Vector) s.readObject();

			// 不能更改对象引用，即扫即打时还在引用原对象
			// 赋对象
			/*
			 * saleGoods = saleGoods1; goodsSpare = spare1; goodsCmPop =
			 * goodsCmPop1;
			 */

			saleGoods.clear();
			saleGoods.addAll(saleGoods1);
			goodsAssistant.clear();
			goodsAssistant.addAll(assistant);
			goodsSpare.clear();
			goodsSpare.addAll(spare1);
			goodsCmPop.clear();
			goodsCmPop.addAll(goodsCmPop1);

			// 关闭断点文件
			s.close();
			s = null;
			f.close();
			f = null;

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
		finally
		{
			try
			{
				if (f != null)
					f.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public double getZZK(SaleGoodsDef saleGoodsDef)
	{
		return getZZK(saleGoodsDef, null, false);
	}

	public double getZZK(SaleGoodsDef saleGoodsDef, String line, boolean flag)
	{
		if (flag)
			zkLogger.log(line + " hyzke:" + saleGoodsDef.hyzke + " yhzke:" + saleGoodsDef.yhzke + " lszke:" + saleGoodsDef.lszke + " lszre:" + saleGoodsDef.lszre + " lszzk:" + saleGoodsDef.lszzk + " lszzr:" + saleGoodsDef.lszzr + " plzke:" + saleGoodsDef.plzke + " zszke:" + saleGoodsDef.zszke + " cjzke:" + saleGoodsDef.cjzke + " ltzke:" + saleGoodsDef.ltzke + " hyzklje:" + saleGoodsDef.hyzklje + " qtzke:" + saleGoodsDef.qtzke + " qtzre:" + saleGoodsDef.qtzre + " rulezke:" + saleGoodsDef.rulezke + " mjzke:" + saleGoodsDef.mjzke);

		saleGoodsDef.hjzk = ManipulatePrecision.doubleConvert(saleGoodsDef.hyzke + saleGoodsDef.yhzke + saleGoodsDef.lszke + saleGoodsDef.lszre + saleGoodsDef.lszzk + saleGoodsDef.lszzr + saleGoodsDef.plzke + saleGoodsDef.zszke + saleGoodsDef.cjzke + saleGoodsDef.ltzke + saleGoodsDef.hyzklje + saleGoodsDef.qtzke + saleGoodsDef.qtzre + saleGoodsDef.rulezke + saleGoodsDef.mjzke, 2, 1);

		return saleGoodsDef.hjzk;
	}

}
