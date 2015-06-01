package com.efuture.javaPos.Payment;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.PayRuleDef;
import com.efuture.javaPos.Struct.PaymentLimitDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

public class Payment
{
	public PayModeDef paymode = null;
	public SaleBS saleBS = null;
	public SalePayDef salepay = null;
	public SaleHeadDef salehead = null;

	public boolean alreadyAddSalePay = false;

	public Vector allowgoods = null;
	public double allowpayje = -1;
	public boolean allowpayjealready = false;
	public String curpayflag = null;
	public String inputMoney = null;// 款员输入的付款金额

	public Payment()
	{

	}

	public Payment(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	// 该构造函数用于红冲小票时,通过小票付款明细创建对象
	public Payment(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public void initPayment(PayModeDef mode, SaleBS sale)
	{
		saleBS = sale;
		salehead = (saleBS != null) ? saleBS.saleHead : null;
		paymode = mode;
	}

	public void initPayment(SalePayDef pay, SaleHeadDef head)
	{
		salepay = pay;
		salehead = head;
		paymode = DataService.getDefault().searchPayMode(salepay.paycode);
	}

	public SalePayDef inputPay(String money)
	{
		try
		{
			if (!isInputPay())
				return null;

			// 生成付款明细对象
			if (!createSalePay(money))
				return null;

			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;
	}

	public boolean isInputPay()
	{
		if (SellType.ISBACK(salehead.djlb))
			return true;

		if (GlobalInfo.sysPara.noinputpaycode == null || GlobalInfo.sysPara.noinputpaycode.equals("") || GlobalInfo.sysPara.noinputpaycode.equals("0000"))
			return true;

		String paycodes[] = GlobalInfo.sysPara.noinputpaycode.split(",");

		for (int i = 0; i < paycodes.length; i++)
		{
			if (paycodes[i].equals(paymode.code))
			{
				// new MessageBox("当前 [" + paymode.code +"] 付款不能进行直接付款!");
				new MessageBox(Language.apply("当前 [{0}] 付款不能进行直接付款!", new Object[] { paymode.code }));
				return false;
			}
		}

		return true;
	}

	// 分摊在BS里计算
	public boolean isApportionInBS()
	{
		return true;
	}

	// 分摊是否在基类里分摊---防止多重重载的情况下不能调用基类
	public boolean isBaseApportion()
	{
		if (!isApportionInBS())
			return false;

		return false;
	}

	public boolean cancelPay()
	{
		return true;
	}

	public boolean collectAccountPay()
	{
		return true;
	}

	public boolean collectAccountClear()
	{
		return true;
	}

	public boolean sendAccountCz()
	{
		return true;
	}

	protected boolean checkMoneyValid(String money, double ye)
	{
		try
		{
			if (money.equals(""))
			{
				new MessageBox(Language.apply("付款金额不能为空!"));

				return false;
			}

			double ybje = Double.parseDouble(saleBS.getPayMoneyByPrecision(Double.parseDouble(money), paymode));

			if (ybje <= 0)
			{
				new MessageBox(Language.apply("付款金额必须大于0"));

				return false;
			}

			if (GlobalInfo.sysPara.payprecision == 'Y')
			{
				if (!checkPayPrecision(Double.parseDouble(money))) { return false; }
			}

			if (ybje < paymode.minval || ybje > paymode.maxval)
			{
				// new MessageBox("该付款方式的有效付款金额必须在\n\n" +
				// ManipulatePrecision.doubleToString(paymode.minval) + " 和 " +
				// ManipulatePrecision.doubleToString(paymode.maxval) + " 之间!");
				new MessageBox(Language.apply("该付款方式的有效付款金额必须在\n\n{0} 和 {1} 之间!", new Object[] { ManipulatePrecision.doubleToString(paymode.minval), ManipulatePrecision.doubleToString(paymode.maxval) }));

				return false;
			}

			// 判断是否溢余应都转换成原币金额再比较,以避免汇率带来的误差
			if (paymode.isyy != 'Y' && ManipulatePrecision.doubleCompare(ybje, Double.parseDouble(saleBS.getPayMoneyByPrecision(ye / paymode.hl, paymode)), 2) > 0)
			{
				new MessageBox(Language.apply("该付款方式不允许溢余!"));

				return false;
			}

			// 检查金额是否超过该付款方式的收款规则
			if (GlobalInfo.sysPara.havePayRule == 'Y' || GlobalInfo.sysPara.havePayRule == 'A')
			{
				if (!this.allowpayjealready)
					this.allowpayje = ManipulatePrecision.doubleConvert(calcPayRuleMaxMoney() / paymode.hl + 0.009, 2, 0);
				if (this.allowpayje >= 0 && paymode.isyy != 'Y' && ManipulatePrecision.doubleCompare(ybje, this.allowpayje, 2) > 0)
				{
					// new MessageBox("该付款方式最多允许付款 " +
					// ManipulatePrecision.doubleToString(allowpayje) + " 元");
					new MessageBox(Language.apply("该付款方式最多允许付款 {0} 元", new Object[] { ManipulatePrecision.doubleToString(allowpayje) }));

					return false;
				}
			}

			// 在销售时检查付款限额
			if (SellType.ISSALE(salehead.djlb) && !checkPaymentLimit(ybje))
				return false;

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return false;
	}

	public double calcPayRuleMaxMoney()
	{
		// 标记为已计算过,避免重复计算影响效率
		this.allowpayjealready = true;

		// 无需计算收款限制规则
		if (GlobalInfo.sysPara.havePayRule != 'Y' && GlobalInfo.sysPara.havePayRule != 'A')
			return -1;
		if (saleBS == null)
			return -1;
		if (SellType.ISBACK(salehead.djlb))
			return -1;

		// 检查每个商品的收款规则,按收款规则进行分组
		if (allowgoods == null)
			allowgoods = new Vector();
		else
			allowgoods.removeAllElements();
		boolean allgoodsrule = false;
		for (int i = 0; i < saleBS.saleGoods.size(); i++)
		{
			// 商品初始都是无付款规则
			boolean haverule = false;
			if (saleBS.goodsSpare != null && saleBS.goodsSpare.size() > i)
			{
				SpareInfoDef info = (SpareInfoDef) saleBS.goodsSpare.elementAt(i);
				if (info != null && info.payrule != null)
				{
					for (int j = 0; j < info.payrule.size(); j++)
					{
						PayRuleDef pr = (PayRuleDef) info.payrule.elementAt(j);
						if (!pr.paycode.equals(paymode.code))
							continue;
						if (curpayflag != null && curpayflag.trim().length() > 0 && !curpayflag.equals(pr.payflag))
							continue;

						// 有对应的收款规则
						haverule = true;

						// 规则为不允许收款 或者 商品其他范围控制不允许分摊该付款
						if (pr.joinmode != 'Y')
							break;
						if (!goodsAllowApportionPay(i))
							break;

						// 按满条件收,同规则累计计算满条件
						if (pr.rulemode == '0')
						{
							int n = 0;
							for (n = 0; n < allowgoods.size(); n++)
							{
								PayRuleDef pr1 = (PayRuleDef) allowgoods.elementAt(n);
								if (pr.ruleid.equals(pr1.ruleid) && ManipulatePrecision.doubleCompare(pr.condje, pr1.condje, 2) == 0 && ManipulatePrecision.doubleCompare(pr.payje, pr1.payje, 2) == 0)
								{
									pr1.goodslist.add(String.valueOf(i));
									break;
								}
							}
							if (n >= allowgoods.size())
							{
								PayRuleDef pr1 = (PayRuleDef) pr.clone();
								if (pr1.goodslist == null)
									pr1.goodslist = new Vector();
								else
									pr1.goodslist.removeAllElements();
								pr1.goodslist.add(String.valueOf(i));
								allowgoods.add(pr1);
							}
						}
						else
						{
							// 按比例收
							PayRuleDef pr1 = (PayRuleDef) pr.clone();
							if (pr1.goodslist == null)
								pr1.goodslist = new Vector();
							else
								pr1.goodslist.removeAllElements();
							pr1.goodslist.add(String.valueOf(i));
							allowgoods.add(pr1);
						}

						break;
					}
				}
			}

			// 如果无对应的收款规则 且 商品其他范围控制允许收款,则该商品全部可收
			if (!haverule && goodsAllowApportionPay(i))
			{
				PayRuleDef pr1 = new PayRuleDef();
				pr1.paycode = paymode.code;
				pr1.ruleid = "#" + String.valueOf(i);
				pr1.rulemode = '1';
				pr1.payje = 1;
				if (pr1.goodslist == null)
					pr1.goodslist = new Vector();
				else
					pr1.goodslist.removeAllElements();
				pr1.goodslist.add(String.valueOf(i));
				allowgoods.add(pr1);
			}
			else
			{
				// 有一个商品是有付款规则的,则这个付款方式就需要检查付款金额
				allgoodsrule = true;
			}
		}

		// 如果所有商品都是无付款规则的,则不用检查付款金额
		if (!allgoodsrule)
		{
			// 置空所有商品都参与该付款
			if (allowgoods != null)
				allowgoods.removeAllElements();
			allowgoods = null;
			return -1;
		}

		// 计算最大允许收款金额
		double maxje = 0, limitje = 0;
		for (int i = 0; i < allowgoods.size(); i++)
		{
			PayRuleDef pr = (PayRuleDef) allowgoods.elementAt(i);

			double hjje = 0, hjye = 0;
			for (int j = 0; pr.goodslist != null && j < pr.goodslist.size(); j++)
			{
				int sgindex = Convert.toInt((String) pr.goodslist.elementAt(j));
				SaleGoodsDef sg = (SaleGoodsDef) saleBS.saleGoods.elementAt(sgindex);
				hjje = ManipulatePrecision.doubleConvert(hjje + (sg.hjje - sg.hjzk));
				hjye = ManipulatePrecision.doubleConvert(hjye + calcGoodsLeavingsApportion(sgindex, sg));
			}

			pr.gdleje = hjye;
			pr.ruleje = 0;
			if (pr.rulemode == '0')
			{
				int bs = ManipulatePrecision.integerDiv(hjje, pr.condje);
				pr.ruleje = ManipulatePrecision.doubleConvert(pr.payje * bs);
			}
			else if (pr.rulemode == '1')
			{
				pr.ruleje = ManipulatePrecision.doubleConvert(hjje * pr.payje);
			}
			else if (pr.rulemode == '2')
			{
				pr.ruleje = ManipulatePrecision.doubleConvert(hjye * pr.payje);
			}
			else if (pr.rulemode == '3')
			{
				int bs = ManipulatePrecision.integerDiv(hjye, pr.condje);
				pr.ruleje = ManipulatePrecision.doubleConvert(pr.payje * bs);
			}

			// new
			// MessageBox("测试: pr.rulemode "+pr.rulemode+" pr.gdleje "+pr.gdleje+" pr.ruleje: "+pr.ruleje);

			maxje = ManipulatePrecision.doubleConvert(maxje + pr.ruleje);
			limitje = ManipulatePrecision.doubleConvert(limitje + Math.min(pr.ruleje, pr.gdleje));
		}
		// new MessageBox("测试: maxje:"+maxje);
		// 剩余付款减去该付款方式的已付款
		maxje = ManipulatePrecision.doubleConvert(maxje - calcPayRuleAlreadyMoney());
		// new MessageBox("测试: maxje - calcPayRuleAlreadyMoney():"+maxje);
		if (maxje < 0)
			maxje = 0;

		// 本次付款金额不能超过可收商品的剩余可付款合计
		if (maxje > limitje)
			maxje = limitje;

		return maxje;
	}

	public boolean checkPaymentLimit(double ybje)
	{
		if (GlobalInfo.payLimit == null || GlobalInfo.payLimit.size() < 1)
			return true;

		double maxje = 0;
		// 付款方式上定义了单笔付款限额时，计算最大可付金额
		PaymentLimitDef payLimit = null;
		boolean isOverLimit = false;
		for (int i = 0; i < GlobalInfo.payLimit.size(); i++)
		{
			payLimit = (PaymentLimitDef) GlobalInfo.payLimit.elementAt(i);
			String[] caplist = payLimit.paycode.split(",");
			// 当前付款方式被限制
			if (("," + payLimit.paycode + ",").indexOf("," + paymode.code + ",") > -1)
			{
				double paidAmount = 0;
				SalePayDef sp = null;
				// 计算已付金额
				for (int j = 0; j < saleBS.salePayment.size(); j++)
				{
					sp = (SalePayDef) saleBS.salePayment.elementAt(j);
					if (("," + payLimit.paycode + ",").indexOf("," + sp.paycode + ",") > -1)
						paidAmount += ManipulatePrecision.doubleConvert(sp.je - sp.num1);
				}

				String tips = null;
				// 单笔限额
				if (payLimit.captype == 'A')
				{
					maxje = ManipulatePrecision.doubleConvert(payLimit.paycap - paidAmount);
					if (ManipulatePrecision.doubleCompare(ybje, maxje, 2) > 0)
					{
						tips = Language.apply("付款方式超出单笔付款限额：") + payLimit.paycap + Language.apply("\n\n本付款方式最大付款金额：") + maxje;
						new MessageBox(tips);
						isOverLimit = true;
					}
				}
				// 全天限额
				else if (payLimit.captype == 'B')
				{
					String sql = "";
					for (int k = 0; k < caplist.length; k++)
					{
						if (k == 0)
							sql = "PAYCODE = '" + caplist[k] + "'";
						else
							sql += " OR PAYCODE = '" + caplist[k] + "'";
					}
					String sql1 = "SELECT SUM(JE) FROM SalePaySummary WHERE (" + sql + ") AND SYYH = '" + Language.apply("全天") +"'";
					Object obj = GlobalInfo.dayDB.selectOneData(sql1);
					double payday = 0;
					if (obj != null)
					{
						payday = Double.parseDouble(String.valueOf(obj));
					}

					maxje = ManipulatePrecision.doubleConvert(payLimit.paycap - paidAmount - payday);
					if (ManipulatePrecision.doubleCompare(ybje, maxje, 2) > 0)
					{
						tips = Language.apply("付款方式超出全天付款限额：") + payLimit.paycap + Language.apply("\n\n本付款方式最大付款金额：") + maxje;
						new MessageBox(tips);
						isOverLimit = true;
					}
				}
			}
		}
		// 超过付款限额，提示授权
		if (isOverLimit)
		{
			if (GlobalInfo.sysPara.limitGH != null && GlobalInfo.sysPara.limitGH.length() > 0)
			{
				if (("," + GlobalInfo.sysPara.limitGH + ",").indexOf(("," + GlobalInfo.posLogin.gh + ",")) < 0)
				{
					OperUserDef staff = DataService.getDefault().personGrant(Language.apply("付款限额授权"));
					if (("," + GlobalInfo.sysPara.limitGH + ",").indexOf(("," + staff.gh + ",")) < 0)
					{
						new MessageBox(Language.apply("工号 ") + staff.gh + Language.apply(" 没有权限突破付款限额"));
						return false;
					}
				}
			}
			else
				return false;
		}
		return true;
	}

	public double calcGoodsLeavingsApportion(int i, SaleGoodsDef sg)
	{
		double ftje = getGoodsApportionTotal(i);
		double pxje = getGoodsMatchMoneyTotal(i);
		double sglimit = ManipulatePrecision.doubleConvert(sg.hjje - sg.hjzk - ftje - pxje);
		if (sglimit < 0)
			sglimit = 0;

		return sglimit;
	}

	public double calcPayRuleAlreadyMoney()
	{
		double yfje = 0;
		for (int i = 0; i < saleBS.salePayment.size(); i++)
		{
			SalePayDef sp = (SalePayDef) saleBS.salePayment.elementAt(i);
			if (!sp.paycode.equals(paymode.code))
				continue;

			if (sp.ispx == 'Y')
				continue;
			// 非覆盖付款输入
			yfje += ManipulatePrecision.doubleConvert(sp.je - sp.num1);
			yfje = ManipulatePrecision.doubleConvert(yfje);
		}

		return yfje;
	}

	public boolean goodsAllowApportionPay(int sgindex)
	{
		// 无其他收款范围控制
		return true;
	}

	// 查找可以收该付款方式的对应商品
	public Vector getGoodsListByPayRule()
	{
		// 查找可以收该付款方式的对应商品列表
		// 商品编码,商品名称,已付金额,限制金额,分摊金额,对应商品行号
		Vector v = new Vector();

		// 非有收款规则的付款方式，所有商品都可以用
		// 是有收款规则的付款方式，只有对应商品可收
		if (allowgoods == null || allowgoods.size() <= 0)
		{
			// Y-只分摊有规则的付款/A-分摊所有的付款
			if (GlobalInfo.sysPara.havePayRule == 'A')
			{
				for (int i = 0; i < saleBS.saleGoods.size(); i++)
				{
					SaleGoodsDef sg = (SaleGoodsDef) saleBS.saleGoods.elementAt(i);

					// 计算商品可收付款的金额 = 成交价 - 已分摊的付款
					double yfje = getGoodsApportionTotal(i);
					double limitje = ManipulatePrecision.doubleConvert(sg.hjje - sg.hjzk - yfje);
					if (limitje > 0)
					{
						String[] row = { sg.barcode, sg.name, ManipulatePrecision.doubleToString(yfje), ManipulatePrecision.doubleToString(limitje), "", String.valueOf(i) };
						v.add(row);
					}
				}
			}
			else
			{
				return null;
			}
		}
		else
		{
			for (int n = 0; n < allowgoods.size(); n++)
			{
				PayRuleDef pr = (PayRuleDef) allowgoods.elementAt(n);

				for (int j = 0; pr.goodslist != null && j < pr.goodslist.size(); j++)
				{
					int sgindex = Convert.toInt((String) pr.goodslist.elementAt(j));
					SaleGoodsDef sg = (SaleGoodsDef) saleBS.saleGoods.elementAt(sgindex);

					// 计算商品可收付款的金额 = 成交价 - 已分摊的付款
					double yfje = getGoodsApportionTotal(sgindex);
					double limitje = ManipulatePrecision.doubleConvert(sg.hjje - sg.hjzk - yfje);
					if (limitje > 0)
					{
						String[] row = { sg.barcode, sg.name, ManipulatePrecision.doubleToString(yfje), ManipulatePrecision.doubleToString(limitje), "", String.valueOf(sgindex) };
						v.add(row);
					}
				}
			}
		}

		return v;
	}

	public Vector paymentApportionByRule()
	{
		// v[x]=商品编码,商品名称,已付金额,限制金额,分摊金额,对应商品行号
		Vector v = getGoodsListByPayRule();
		if (v == null)
			return null;

		// 根据分摊模式自动将付款分摊到各商品
		if (GlobalInfo.sysPara.apportMode == 'C' || GlobalInfo.sysPara.apportMode == 'D')
		{
			// 按商品倒序分摊 or 按商品顺序分摊
			return paymentApportionByRuleCD(v);
		}
		else if (GlobalInfo.sysPara.apportMode == 'E' || GlobalInfo.sysPara.apportMode == 'F')
		{
			// 按比例大小分摊 or 按可收大小分摊
			return paymentApportionByRuleEF(v);
		}
		else
		{
			// 如果付款金额比限制金额的合计大，说明有溢余，溢余部分不进行分摊
			double allftje = salepay.je - salepay.num1;
			double maxlmje = 0;
			for (int i = 0; i < v.size(); i++)
			{
				String[] row = (String[]) v.elementAt(i);
				maxlmje += Convert.toDouble(row[3]);
			}
			if (allftje > maxlmje)
				allftje = maxlmje;

			// 按金额占比均摊
			double yftje = 0;
			for (int i = 0; i < v.size(); i++)
			{
				String[] row = (String[]) v.elementAt(i);
				double limitje = Convert.toDouble(row[3]);
				double spftje = 0;

				// 最后一个商品用减法
				if (i == (v.size() - 1))
				{
					spftje = ManipulatePrecision.doubleConvert(allftje - yftje);
				}
				else
				{
					double ftzb = 1;
					double hjzb = 1;

					// 按商品收款规则分组计算
					for (int j = 0; allowgoods != null && j < allowgoods.size(); j++)
					{
						// 先找到商品所属规则组
						int n = 0;
						PayRuleDef pr = (PayRuleDef) allowgoods.elementAt(j);
						for (n = 0; pr.goodslist != null && n < pr.goodslist.size(); n++)
						{
							if (Convert.toInt(row[5]) == Convert.toInt((String) pr.goodslist.elementAt(n)))
								break;
						}
						if (n >= pr.goodslist.size())
							continue;

						// 计算同规则分组的商品成交价占比=本分组成交价合计/总的成交价合计
						double hjje = 0, gpje = 0;
						for (int k = 0; k < allowgoods.size(); k++)
						{
							PayRuleDef pr1 = (PayRuleDef) allowgoods.elementAt(k);
							for (n = 0; pr1.goodslist != null && n < pr1.goodslist.size(); n++)
							{
								int sgidx = Convert.toInt((String) pr1.goodslist.elementAt(n));
								SaleGoodsDef sg = (SaleGoodsDef) saleBS.saleGoods.elementAt(sgidx);
								double sgft = getGoodsApportionTotal(sgidx);
								hjje += (sg.hjje - sg.hjzk - sgft);

								// 属于同规则分组商品
								if (k == j)
									gpje += (sg.hjje - sg.hjzk - sgft);
							}
						}
						hjzb = gpje / hjje;

						// 计算同规则分组的商品可收款占比=本分组可收款合计/总的可收款合计
						gpje = Math.min(pr.ruleje, pr.gdleje);
						hjje = 0;
						for (int k = 0; k < allowgoods.size(); k++)
						{
							PayRuleDef pr1 = (PayRuleDef) allowgoods.elementAt(k);
							hjje += Math.min(pr1.ruleje, pr1.gdleje);
						}
						ftzb = gpje / hjje;

						// 计算完本行占比退出循环
						break;
					}

					spftje = ManipulatePrecision.doubleConvert((allftje * ftzb) * (limitje / (maxlmje * hjzb)));
				}

				yftje += spftje;
				row[4] = String.valueOf(spftje);
			}

			return v;
		}
	}

	public Vector paymentApportionByRuleCD(Vector vec)
	{
		// v[x]=商品编码,商品名称,已付金额,限制金额,分摊金额,对应商品行号
		// C = 按商品倒序分摊
		// D = 按商品顺序分摊

		// 先将集合按商品行号进行排序
		Vector v = new Vector();
		for (int i = 0; i < vec.size(); i++)
		{
			int sgindex = Convert.toInt(((String[]) vec.elementAt(i))[5]);
			int j = 0;
			for (; j < v.size(); j++)
			{
				int pxindex = Convert.toInt(((String[]) v.elementAt(j))[5]);
				if (GlobalInfo.sysPara.apportMode == 'C' && sgindex > pxindex)
					break;
				if (GlobalInfo.sysPara.apportMode == 'D' && sgindex < pxindex)
					break;
			}
			if (j < v.size())
				v.insertElementAt(vec.elementAt(i), j);
			else
				v.add(vec.elementAt(i));
		}

		// 如果付款金额比限制金额的合计大，说明有溢余，溢余部分不进行分摊
		double allftje = salepay.je - salepay.num1;
		double maxlmje = 0;
		for (int i = 0; i < v.size(); i++)
		{
			String[] row = (String[]) v.elementAt(i);
			maxlmje += Convert.toDouble(row[3]);
		}
		if (allftje > maxlmje)
			allftje = maxlmje;

		// 把金额分摊完为止
		double yftje = 0;
		for (int i = 0; i < v.size(); i++)
		{
			String[] row = (String[]) v.elementAt(i);
			double limitje = Convert.toDouble(row[3]);
			PayRuleDef pr = getGoodsApportionPayRule(Convert.toInt(row[5]));
			if (pr != null && ManipulatePrecision.doubleCompare(limitje, pr.ruleje, 2) > 0)
				limitje = pr.ruleje;

			double spftje = ManipulatePrecision.doubleConvert(allftje - yftje);
			if (spftje < 0)
				spftje = 0;
			if (ManipulatePrecision.doubleCompare(spftje, limitje, 2) > 0)
				spftje = limitje;

			yftje += spftje;
			row[4] = String.valueOf(spftje);
		}

		return v;
	}

	public Vector paymentApportionByRuleEF(Vector vec)
	{
		// v[x]=商品编码,商品名称,已付金额,限制金额,分摊金额,对应商品行号
		// E = 按比例大小分摊
		// F = 按可收大小分摊

		// 先将集合按商品行号进行排序
		Vector v = new Vector();
		for (int i = 0; i < vec.size(); i++)
		{
			int sgindex = Convert.toInt(((String[]) vec.elementAt(i))[5]);
			PayRuleDef sgpr = getGoodsApportionPayRule(sgindex);
			// double sgje = Convert.toDouble(((String[])vec.elementAt(i))[3]);
			int j = 0;
			for (; j < v.size(); j++)
			{
				int pxindex = Convert.toInt(((String[]) v.elementAt(j))[5]);
				// double pxje =
				// Convert.toDouble(((String[])v.elementAt(j))[3]);
				PayRuleDef pxpr = getGoodsApportionPayRule(pxindex);
				if (GlobalInfo.sysPara.apportMode == 'E')
				{
					if (pxpr != null && sgpr != null && pxpr.rulemode == '0' && ManipulatePrecision.doubleCompare(sgpr.payje / sgpr.condje, pxpr.payje / pxpr.condje, 2) > 0)
						break;
					if (pxpr != null && sgpr != null && pxpr.rulemode != '0' && ManipulatePrecision.doubleCompare(sgpr.payje, pxpr.payje, 2) > 0)
						break;
					if (pxpr == null && sgindex > pxindex)
						break;
				}
				if (GlobalInfo.sysPara.apportMode == 'F')
				{
					// if (pxpr != null && sgpr != null && pxpr.rulemode == '0'
					// &&
					// ManipulatePrecision.doubleCompare(sgpr.payje/sgpr.condje*sgje,pxpr.payje/pxpr.condje*sgje,2)
					// > 0) break;
					// if (pxpr != null && sgpr != null && pxpr.rulemode != '0'
					// &&
					// ManipulatePrecision.doubleCompare(sgpr.payje*sgje,pxpr.payje*sgje,2)
					// > 0) break;
					// if (pxpr == null && sgindex > pxindex) break;

					if (pxpr != null && ManipulatePrecision.doubleCompare(sgpr.ruleje, pxpr.ruleje, 2) > 0)
						break;
					// if (pxpr == null && sgindex > pxindex) break;
					// &&
					// ManipulatePrecision.doubleCompare(sgpr.payje*sgje,pxpr.payje*pxje,2)
					// > 0) break;
				}
			}
			if (j < v.size())
				v.insertElementAt(vec.elementAt(i), j);
			else
				v.add(vec.elementAt(i));
		}

		// 如果付款金额比限制金额的合计大，说明有溢余，溢余部分不进行分摊
		double allftje = salepay.je - salepay.num1;
		double maxlmje = 0;
		for (int i = 0; i < v.size(); i++)
		{
			String[] row = (String[]) v.elementAt(i);
			maxlmje += Convert.toDouble(row[3]);
		}
		if (allftje > maxlmje)
			allftje = maxlmje;

		// 把金额分摊完为止
		double yftje = 0;
		for (int i = 0; i < v.size(); i++)
		{
			String[] row = (String[]) v.elementAt(i);
			double limitje = Convert.toDouble(row[3]);
			PayRuleDef pr = getGoodsApportionPayRule(Convert.toInt(row[5]));
			if (pr != null && ManipulatePrecision.doubleCompare(limitje, pr.ruleje, 2) > 0)
				limitje = pr.ruleje;

			double spftje = ManipulatePrecision.doubleConvert(allftje - yftje);
			if (spftje < 0)
				spftje = 0;
			if (ManipulatePrecision.doubleCompare(spftje, limitje, 2) > 0)
				spftje = limitje;

			yftje += spftje;
			row[4] = String.valueOf(spftje);
		}

		return v;
	}

	public boolean paymentApportionToGoods(Vector v)
	{
		// v[i] = 商品编码,商品名称,已付金额,限制金额,分摊金额,对应商品行号
		for (int i = 0; v != null && i < v.size(); i++)
		{
			String[] row = (String[]) v.elementAt(i);
			if (row[4].length() <= 0)
				continue;
			if (ManipulatePrecision.doubleCompare(Math.abs(Convert.toDouble(row[4])), 0, 2) <= 0)
				continue;

			// 按商品记录对应的付款分摊
			int sgindex = Integer.parseInt(row[5]);
			if (saleBS.goodsSpare == null || saleBS.goodsSpare.size() <= sgindex)
				continue;

			// 先找到商品所属规则
			PayRuleDef prd = getGoodsApportionPayRule(sgindex);

			// 分摊付款金额是否需要配现
			double pxje = 0;
			if (prd != null)
			{
				if (prd.rulemode == '2')
				{
					// 配现是可收比例的几倍,配现金额就应该是收款金额的几倍
					double payje = Convert.toDouble(row[4]);
					pxje = ManipulatePrecision.doubleConvert(payje * ((1 - prd.payje) / prd.payje));
					if (pxje > 0)
						salepay.ispx = 'Y';
				}
				if (prd.rulemode == '3')
				{
					// 整除有多的,配现应该按多一倍计算
					int bs = ManipulatePrecision.integerDiv(Convert.toDouble(row[4]), prd.payje);
					if (ManipulatePrecision.doubleCompare(prd.payje * bs, Convert.toDouble(row[4]), 2) < 0)
						bs++;
					pxje = ManipulatePrecision.doubleConvert(bs * prd.condje);
					if (pxje > 0)
						salepay.ispx = 'Y';
				}
			}

			// 付款方式唯一序号,付款代码,付款名称(主要判断同付款代码不同付款FLAG),分摊金额[,配现金额]

			SpareInfoDef info = (SpareInfoDef) saleBS.goodsSpare.elementAt(sgindex);
			if (info.payft == null)
				info.payft = new Vector();
			String[] ft = new String[] { String.valueOf(salepay.num5), salepay.paycode, salepay.payname, row[4], String.valueOf(pxje) };
			info.payft.add(ft);
		}

		return true;
	}

	public PayRuleDef getGoodsApportionPayRule(int sgindex)
	{
		for (int j = 0; allowgoods != null && j < allowgoods.size(); j++)
		{
			int n = 0;
			PayRuleDef pr = (PayRuleDef) allowgoods.elementAt(j);
			for (n = 0; pr.goodslist != null && n < pr.goodslist.size(); n++)
			{
				if (sgindex == Convert.toInt((String) pr.goodslist.elementAt(n)))
					break;
			}
			if (n >= pr.goodslist.size())
				continue;
			return pr;
		}

		return null;
	}

	public double getGoodsApportionTotal(int index)
	{
		if (saleBS.goodsSpare == null || saleBS.goodsSpare.size() <= index)
			return 0;
		SpareInfoDef spinfo = (SpareInfoDef) saleBS.goodsSpare.elementAt(index);
		if (spinfo == null || spinfo.payft == null)
			return 0;

		double ftje = 0;
		for (int j = 0; j < spinfo.payft.size(); j++)
		{
			String[] s = (String[]) spinfo.payft.elementAt(j);
			if (s.length > 3)
				ftje += Convert.toDouble(s[3]);
		}
		return ftje;
	}

	public double getGoodsMatchMoneyTotal(int index)
	{
		if (saleBS.goodsSpare == null || saleBS.goodsSpare.size() <= index)
			return 0;
		SpareInfoDef spinfo = (SpareInfoDef) saleBS.goodsSpare.elementAt(index);
		if (spinfo == null || spinfo.payft == null)
			return 0;

		double pxje = 0;
		for (int j = 0; j < spinfo.payft.size(); j++)
		{
			String[] s = (String[]) spinfo.payft.elementAt(j);
			if (s.length > 4)
				pxje += Convert.toDouble(s[4]);
		}
		return pxje;
	}

	// 校验付款精度
	public boolean checkPayPrecision(double ybje)
	{
		double strPrecision = paymode.sswrjd;;

		if (paymode.sswrjd != 1 && paymode.sswrjd != 0.1 && paymode.sswrjd != 0.01)
		{
			strPrecision = 0.01;
		}

		double ybje1 = 0;
		String msg = "";
		if (strPrecision == 1)
		{
			ybje1 = ManipulatePrecision.doubleConvert(ybje, 0, 0);
			msg = Language.apply("元");
		}
		else if (strPrecision == 0.1)
		{
			ybje1 = ManipulatePrecision.doubleConvert(ybje, 1, 0);
			msg = Language.apply("角");
		}
		else if (strPrecision == 0.01)
		{
			ybje1 = ManipulatePrecision.doubleConvert(ybje, 2, 0);
			msg = Language.apply("分");
		}

		if (ybje1 != ybje)
		{
			new MessageBox(Language.apply("金额输入错误，本付款方式只能输入到") + msg);
			return false;
		}

		return true;
	}

	public boolean createSalePay(String money)
	{
		try
		{
			// 检查金额是否有效
			double ye = saleBS.calcPayBalance();

			if (checkMoneyValid(money, ye))
			{
				if (!createSalePayObject(money))
					return false;

				return true;
			}
		}
		catch (Exception ex)
		{
			new MessageBox(Language.apply("生成付款对象出现异常\n\n") + ex.getMessage());
			ex.printStackTrace();
		}

		//
		salepay = null;
		return false;
	}

	public boolean createSalePayObject(String money)
	{
		try
		{
			salepay = new SalePayDef();
			salepay.syjh = saleBS.saleHead.syjh;
			salepay.fphm = saleBS.saleHead.fphm;
			salepay.paycode = paymode.code;
			salepay.payname = paymode.name;
			salepay.flag = '1';
			salepay.ybje = Double.parseDouble(saleBS.getPayMoneyByPrecision(Double.parseDouble(money), paymode));
			salepay.hl = paymode.hl;
			salepay.je = ManipulatePrecision.doubleConvert(salepay.ybje * salepay.hl, 2, 1);
			salepay.payno = "";
			salepay.batch = "";
			salepay.kye = 0;
			salepay.idno = "";
			salepay.memo = "";
			salepay.str1 = "";
			salepay.str2 = "";
			salepay.str3 = "";
			salepay.str4 = "";
			salepay.str5 = "";
			salepay.num1 = 0;
			salepay.num2 = 0;
			salepay.num3 = 0;
			salepay.num4 = 0;
			salepay.num5 = 0;
			salepay.num6 = 0;

			// 可溢余则超额部分记入付款溢余
			if (this.allowpayje >= 0 && ManipulatePrecision.doubleCompare(salepay.ybje, this.allowpayje, 2) > 0 && paymode.isyy == 'Y')/*
																																		 * &&
																																		 * paymode
																																		 * .
																																		 * iszl
																																		 * !=
																																		 * 'Y'
																																		 * )
																																		 */
			{
				salepay.num1 = ManipulatePrecision.doubleConvert((salepay.ybje - this.allowpayje) * salepay.hl);
			}

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox(Language.apply("生成交易付款对象出现异常\n\n") + ex.getMessage());
			ex.printStackTrace();
			return false;
		}
	}

	public int getAccountInputMode()
	{
		return TextBox.MsrKeyInput;
	}

	public boolean calcFjkMaxJe()
	{
		return true;
	}

	public int getJoinPay()
	{
		return -1;
	}

	// 预留函数1
	public boolean extendAction1()
	{
		return true;
	}

	// 预留函数2
	public boolean extendAction2(String param1, String param2)
	{
		return true;
	}
}
