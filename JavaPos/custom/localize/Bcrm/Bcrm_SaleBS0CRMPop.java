package custom.localize.Bcrm;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentFjk;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.CalcRulePopDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.Struct.SuperMarketPopRuleDef;
import com.efuture.javaPos.UI.Design.ApportPaymentForm;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.SalePayForm;

import custom.localize.Bhls.Bhls_SaleBS;

public class Bcrm_SaleBS0CRMPop extends Bhls_SaleBS
{
	public Vector crmPop = null;

	public Vector rulePopSet = null;
	public boolean apportionPay = false;

	public int vipzk1 = 0; // 刷商品时实时计算
	public int vipzk2 = 1; // 付款时计算

	protected Vector ruleReqList = null; // 超市规则促销条件列表
	protected Vector rulePopList = null; // 超市规则促销结果列表
	public double superMarketRuleyhje; // 超市规则促销优惠金额
	public double quantity; // 当前选正行的商品数量
	protected boolean havePaymode = false;

	public Vector getFjkPopVector()
	{
		return crmPop;
	}

	public void writeSellObjectToStream(ObjectOutputStream s) throws Exception
	{
		brokenAssistant.insertElementAt(crmPop, 0);

		super.writeSellObjectToStream(s);
	}

	public void readStreamToSellObject(ObjectInputStream s) throws Exception
	{
		super.readStreamToSellObject(s);

		crmPop = (Vector) brokenAssistant.remove(0);
	}

	public void initNewSale()
	{
		if (crmPop != null)
		{
			crmPop.removeAllElements();
		}
		else
		{
			crmPop = new Vector();
		}

		//
		super.initNewSale();
	}

	public void addSaleGoodsObject(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		super.addSaleGoodsObject(sg, goods, info);

		// 
		sg.memo = "0,0";

		if (SellType.ISCHECKINPUT(saletype)) { return; }

		if (goods != null)
		{
			// 每查找一个商品，就查找商品CRM促销规则
			findGoodsCRMPop(sg, goods, info);
		}
	}

	public boolean delSaleGoodsObject(int index)
	{
		// 删除商品
		if (!super.delSaleGoodsObject(index)) { return false; }

		// 删除对应CRM促销
		if (crmPop.size() > index) crmPop.removeElementAt(index);

		return true;
	}

	public void findGoodsCRMPop(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		String newyhsp = "90000000";
		String cardno = null;
		String cardtype = null;

		if ((curCustomer != null && curCustomer.iszk == 'Y'))
		{
			cardno = curCustomer.code;
			cardtype = curCustomer.type;
		}

		GoodsPopDef popDef = new GoodsPopDef();

		// 非促销商品 或者在退货时，不查找促销信息
		((Bcrm_DataService) DataService.getDefault()).findPopRuleCRM(popDef, sg.code, sg.gz, sg.uid, goods.specinfo, sg.catid, sg.ppcode,
																		saleHead.rqsj, cardno, cardtype, saletype);
		// 换货状态下，不使用任何促销
		if (popDef.yhspace == 0 || hhflag == 'Y')
		{
			popDef.yhspace = 0;
			popDef.memo = "";
			popDef.poppfjzkfd = 1;
		}

		//将收券规则放入GOODSDEF 列表
		goods.memo = popDef.str2;
		goods.num1 = popDef.num1;
		goods.num2 = popDef.num2;
		goods.str4 = popDef.mode;
		info.char3 = popDef.type;

		// 促销联比例
		sg.xxtax = Convert.toDouble(popDef.ksrq); // 促销联比例
		goods.xxtax = Convert.toDouble(popDef.ksrq);
		if (goods.memo == null) goods.memo = "";

		// 增加CRM促销信息
		crmPop.add(popDef);

		// 标志是否为9开头扩展的控制
		boolean append = false;
		// 无促销,此会员不允许促销
		if (popDef.yhspace == 0)
		{
			append = false;
			info.str1 = "0000";
		}
		else if (popDef.yhspace == Integer.parseInt(newyhsp))
		{
			append = true;
			info.str1 = newyhsp;
		}
		else
		{

			if (String.valueOf(popDef.yhspace).charAt(0) != '9')
			{
				if (GlobalInfo.sysPara.iscrmtjprice == 'Y') info.str1 = Convert.increaseInt(popDef.yhspace, 5).substring(0, 4);
				else info.str1 = Convert.increaseInt(popDef.yhspace, 4);

				append = false;
			}
			else
			{
				info.str1 = String.valueOf(popDef.yhspace);

				append = true;
			}
			//询问参加活动类型 满减或者满增
			String yh = info.str1;

			if (append) yh = yh.substring(1);

			StringBuffer buff = new StringBuffer(yh);
			Vector contents = new Vector();

			for (int i = 0; i < buff.length(); i++)
			{
				// 2-任选促销/1-存在促销/0-无促销
				if (buff.charAt(i) == '2')
				{
					if (i == 0)
					{
						contents.add(new String[] { "D", Language.apply("参与打折促销活动"), "0" });
					}
					else if (i == 1)
					{
						contents.add(new String[] { "J", Language.apply("参与减现促销活动"), "1" });
					}
					else if (i == 2)
					{
						contents.add(new String[] { "Q", Language.apply("参与返券促销活动"), "2" });
					}
					else if (i == 3)
					{
						contents.add(new String[] { "Z", Language.apply("参与赠品促销活动"), "3" });
					}
					else if (i == 5)
					{
						contents.add(new String[] { "F", Language.apply("参与积分活动"), "5" });
					}
				}
			}

			if (contents.size() <= 1)
			{
				if (contents.size() > 0)
				{
					String[] row = (String[]) contents.elementAt(0);
					int i = Integer.parseInt(row[2]);
					buff.setCharAt(i, '1');
				}
			}
			else
			{
				String[] title = { Language.apply("代码"), Language.apply("描述") };
				int[] width = { 60, 400 };
				int choice = new MutiSelectForm().open(Language.apply("请选择参与满减满赠活动的规则"), title, width, contents);

				for (int i = 0; i < contents.size(); i++)
				{
					if (i != choice)
					{
						String[] row = (String[]) contents.elementAt(i);
						int j = Integer.parseInt(row[2]);
						buff.setCharAt(j, '0');
					}
					else
					{
						String[] row = (String[]) contents.elementAt(i);
						int j = Integer.parseInt(row[2]);
						buff.setCharAt(j, '1');
					}
				}
			}

			if (append) info.str1 = "9" + buff.toString();
			else info.str1 = buff.toString();
		}

		String line = "";

		String yh = info.str1;
		if (append) yh = info.str1.substring(1);

		if (yh.charAt(0) != '0')
		{
			line += "D";
		}

		if (yh.charAt(1) != '0')
		{
			line += "J";
		}

		if (yh.charAt(2) != '0')
		{
			line += "Q";
		}

		if (yh.charAt(3) != '0')
		{
			line += "Z";
		}

		if (yh.length() > 5 && yh.charAt(5) != '0')
		{
			line += "F";
		}

		if (line.length() > 0)
		{
			sg.name = "(" + line + ")" + sg.name;
		}

		if (!append)
		{
			// str3记录促销组合码
			if (GlobalInfo.sysPara.iscrmtjprice == 'Y') sg.str3 = info.str1 + String.valueOf(Convert.increaseInt(popDef.yhspace, 5).substring(4));
			else sg.str3 = info.str1;
		}
		else
		{
			sg.str3 = info.str1;
		}
		// 将商品属性码,促销规则加入SaleGoodsDef里
		sg.str3 += (";" + goods.specinfo);
		sg.str3 += (";" + popDef.memo);
		sg.str3 += (";" + popDef.poppfjzkl);
		sg.str3 += (";" + popDef.poppfjzkfd);
		sg.str3 += (";" + popDef.poppfj);

		// 只有找到了规则促销单，就记录到小票
		if (!info.str1.equals("0000") || !info.str1.equals(newyhsp))
		{
			sg.zsdjbh = popDef.djbh;
			sg.zszkfd = popDef.poplsjzkfd;
		}
	}

	public boolean paySellPop()
	{
		// 处理CRM促销
		doRulePopExit = false;
		
		if (GlobalInfo.sysPara.isSuperMarketPop == 'Y') doSuperMarketCrmPop();

		haveRulePop = doCrmPop();

		if (doRulePopExit) return false; // 不再继续进行付款

		if (havePaymode)
		{
			havePaymode = false;
			return false;
		}
		return true;
	}

	// 新CRM满减促销
	public boolean doCrmPop()
	{
		boolean haveCrmPop = false;

		//清空，放满减描述
		saleHead.str2 = "";

		// 默认总是不进行分摊付款的
		apportionPay = false;

		// 先总是无满减规则方式的付款
		isPreparePay = payNormal;

		if (!SellType.ISSALE(saletype)) { return false; }

		if (SellType.NOPOP(saletype)) return false;

		if (SellType.ISEARNEST(saletype)) { return false; }

		if (SellType.ISPREPARETAKE(saletype)) { return false; }
		
		if (SellType.ISHH(saletype)) { return false; }

		// 先进行直接打折
		int i = 0;
		double hjzszk = 0;
		for (i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);

			String mjrule = ((SpareInfoDef) goodsSpare.elementAt(i)).str1;
			GoodsPopDef goodsPop1 = (GoodsPopDef) crmPop.elementAt(i);
			double zkl = ((GoodsDef) goodsAssistant.elementAt(i)).maxzkl;

			// 不计算换购商品
			if (((SpareInfoDef) goodsSpare.elementAt(i)).char2 == 'Y') continue;

			if (mjrule.charAt(0) == '9') mjrule = mjrule.substring(1);

			if (mjrule.charAt(0) == '1')
			{
				double sj = saleGoodsDef.hjje - getZZK(saleGoodsDef);
				double dz = ManipulatePrecision.mul(sj, goodsPop1.poplsjzkl);

				double minje = saleGoodsDef.hjje * zkl;

				if (dz < minje)
				{
					saleGoodsDef.zszke = ManipulatePrecision.sub(sj, minje);
					saleGoodsDef.zszkfd = goodsPop1.poplsjzkfd;
					saleGoodsDef.zsdjbh = goodsPop1.djbh;
				}
				else
				{
					saleGoodsDef.zszke = ManipulatePrecision.sub(sj, dz);
					saleGoodsDef.zszkfd = goodsPop1.poplsjzkfd;
					saleGoodsDef.zsdjbh = goodsPop1.djbh;
				}
				// 计算价格精度
				if (saleGoodsDef.zszke > 0) saleGoodsDef.zszke = getConvertRebate(i, saleGoodsDef.zszke);

				getZZK(saleGoodsDef);
				hjzszk += saleGoodsDef.zszke;

				haveCrmPop = true;
			}
		}

		if (hjzszk > 0)
		{
			// 重算应收
			calcHeadYsje();

			// 刷新商品列表
			saleEvent.updateTable(getSaleGoodsDisplay());
			saleEvent.setTotalInfo();

//			new MessageBox("有商品参加活动促销，总共可打折 " + ManipulatePrecision.doubleToString(hjzszk));
			new MessageBox(Language.apply("有商品参加活动促销，总共可打折 {0}" ,new Object[]{ManipulatePrecision.doubleToString(hjzszk)}));
		}

		// 在VIP促销需要除券计算模式下，计算VIP前先提示输入券付款
		boolean vippaycw = false;
		if (GlobalInfo.sysPara.vipPayExcp == 'Y' && checkMemberSale() && curCustomer != null && curCustomer.iszk == 'Y'
				&& GlobalInfo.sysPara.vipPromotionCrm != null && GlobalInfo.sysPara.vipPromotionCrm.equals("2"))
		{
			// 提示先输入券付款
			if (new MessageBox(Language.apply("券付款不参与VIP折扣,请先输入券付款金额\n\n如果顾客没有券付款,请直接按‘退出’键")).verify() != GlobalVar.Exit)
			{
				// 开始预付除外付款方式
				isPreparePay = payPopPrepare;

				// 打开付款窗口
				new SalePayForm().open(saleEvent.saleBS);

				// 付款完成，开始新交易
				if (this.saleFinish)
				{
					sellFinishComplete();

					// 预先付款就已足够,不再继续后续付款
					doRulePopExit = true;
					return false; // 表示没有满减促销,取消付款时无需恢复
				}
			}

			// 进入实付剩余付款方式,只允许非券付款方式进行付款
			isPreparePay = payPopOther;

			// 标记已输入除外付款，后面满减时不再输入除外付款
			vippaycw = true;
		}

		// 如果为VIP折扣区间的打折方式，在满减前计算    	
		if (checkMemberSale() && curCustomer != null && curCustomer.iszk == 'Y' && GlobalInfo.sysPara.vipPromotionCrm != null
				&& GlobalInfo.sysPara.vipPromotionCrm.equals("2"))
		{
			// vipzk2表示按下付款键时才计算VIP折扣
			for (int k = 0; k < saleGoods.size(); k++)
			{
				getVIPZK(k, vipzk2);
			}

			// 重算小票应收  
			calcHeadYsje();

			// 刷新商品列表
			saleEvent.updateTable(getSaleGoodsDisplay());
			saleEvent.setTotalInfo();

			//显示会员卡折扣总金额
			if (saleHead.hyzke > 0) new MessageBox(Language.apply("会员折扣总金额 ：") + saleHead.hyzke);
		}

		// 检查促销折扣控制 如果低于折扣率,不进行满减,返券,返礼促销
		for (int j = 0; j < saleGoods.size(); j++)
		{
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(j);
			String mjrule = ((SpareInfoDef) goodsSpare.elementAt(j)).str1;
			GoodsPopDef goodsPop1 = (GoodsPopDef) crmPop.elementAt(j);
			double zkl = 0;
			if (saleGoodsDef.hjje != 0)
			{
				zkl = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - getZZK(saleGoodsDef)) / saleGoodsDef.hjje, 2, 1);
			}

			if (zkl < goodsPop1.pophyjzkfd)
			{

				if (mjrule.charAt(0) == '9')
				{
					StringBuffer buff = new StringBuffer(mjrule);
					for (int z = 2; z < buff.length(); z++)
					{
						buff.setCharAt(z, '0');
					}
					mjrule = buff.toString();
				}
				else
				{
					mjrule = mjrule.charAt(0) + "000";
				}
				((SpareInfoDef) goodsSpare.elementAt(j)).str1 = mjrule;

				if (GlobalInfo.sysPara.iscrmtjprice == 'Y') saleGoodsDef.str3 = mjrule
						+ String.valueOf(Convert.increaseInt(goodsPop1.yhspace, 5).substring(4))
						+ saleGoodsDef.str3.substring(saleGoodsDef.str3.indexOf(";"));
				else saleGoodsDef.str3 = mjrule + saleGoodsDef.str3.substring(saleGoodsDef.str3.indexOf(";"));
			}
		}

		// 检查是否需要分摊
		for (int j = 0; j < saleGoods.size(); j++)
		{
			String mjrule = ((SpareInfoDef) goodsSpare.elementAt(j)).str1;
			if (mjrule.charAt(0) == '9' && mjrule.length() > 3 && mjrule.charAt(2) == '1') apportionPay = true;
			if (mjrule.charAt(1) == '1') apportionPay = true;
			if (apportionPay)
			{
				break;
			}
		}

		// 再查找是否存在满减或减现
		int j = 0;
		Vector set = new Vector();
		CalcRulePopDef calPop = null;

		// 先按商品分组促销规则
		for (i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			GoodsDef goodsDef = ((GoodsDef) goodsAssistant.elementAt(i));
			GoodsPopDef goodsPop = (GoodsPopDef) crmPop.elementAt(i);
			String mjrule = ((SpareInfoDef) goodsSpare.elementAt(i)).str1;
			String ruleCode = goodsDef.specinfo;

			if (mjrule.charAt(0) == '9') mjrule = mjrule.substring(1);
			// 选择了不参与减现继续下一个商品
			if (mjrule.equals("N") || (mjrule.charAt(1) != '1'))
			{
				continue;
			}

			// 查找是否相同促销规则
			for (j = 0; j < set.size(); j++)
			{
				calPop = (CalcRulePopDef) set.elementAt(j);

				int oldIndex = Integer.parseInt((String) calPop.row_set.elementAt(0));
				SaleGoodsDef saleGoodsDef1 = (SaleGoodsDef) saleGoods.elementAt(oldIndex);
				GoodsDef goodsDef1 = ((GoodsDef) goodsAssistant.elementAt(oldIndex));
				GoodsPopDef goodsPop1 = (GoodsPopDef) crmPop.elementAt(oldIndex);
				String mjrule1 = ((SpareInfoDef) goodsSpare.elementAt(oldIndex)).str1;

				if (mjrule1.charAt(0) == '9') mjrule1 = mjrule1.substring(1);
				// 判断是否为同规则促销
				if (isSamePop(saleGoodsDef, goodsDef, goodsPop, mjrule, saleGoodsDef1, goodsDef1, goodsPop1, mjrule1))
				{
					calPop.row_set.add(String.valueOf(i));

					break;
				}
			}

			if (j >= set.size())
			{
				calPop = new CalcRulePopDef();
				calPop.code = saleGoodsDef.code;
				calPop.gz = saleGoodsDef.gz;
				calPop.uid = saleGoodsDef.uid;
				calPop.rulecode = ruleCode;
				calPop.catid = saleGoodsDef.catid;
				calPop.ppcode = saleGoodsDef.ppcode;
				calPop.popDef = goodsPop;
				calPop.row_set = new Vector();
				calPop.row_set.add(String.valueOf(i));
				set.add(calPop);
			}
		}

		// 无规则促销
		if (set.size() <= 0) { return haveCrmPop; }

		// 满减前先对所有商品进行舍分处理
		this.calcSellPayMoney(true);

		// 引用促销规则集合，用于付款分摊时进行判断，只有一个规则自动平摊到每个商品
		rulePopSet = set;

		// 检查是否要除券
		boolean havepaycw = false;
		for (i = 0; i < set.size(); i++)
		{
			calPop = (CalcRulePopDef) set.elementAt(i);

			if (calPop.popDef.catid.equals("Y"))
			{
				havepaycw = true;
				break;
			}
		}

		// 前面已经进行了VIP除外付款输入,不再输入除外付款
		if (vippaycw) havepaycw = false;

		// 循环两次
		// 第一次先检查是否有满足条件的规则,如果没有则直接返回
		// 第二次检查除券外是否还有满足条件的规则,如果不需要除券,则只用循环一次
		int nwhile = 1;
		do
		{
			// 开始计算商品分组参与计算的合计金额
			for (i = 0; i < set.size(); i++)
			{
				// 如果是能进入第二次循环,说明有交易金额是满足促销条件的规则促销
				// 如果需要扣除券付款,先输入券付款方式
				if ((nwhile >= 2) && havepaycw)
				{
					// 提示先输入券付款
					if (GlobalInfo.sysPara.mjPaymentRule.trim().length() > 0
							&& new MessageBox(Language.apply("本笔交易有活动促销,请先输入券付款金额\n\n如果顾客没有券付款,请直接按‘退出’键")).verify() != GlobalVar.Exit)
					{
						// 开始预付除外付款方式
						isPreparePay = payPopPrepare;

						// 打开付款窗口
						new SalePayForm().open(saleEvent.saleBS);

						// 付款完成，开始新交易
						if (this.saleFinish)
						{
							sellFinishComplete();

							// 预先付款就已足够,不再继续后续付款
							doRulePopExit = true;
							return false;
						}
					}

					// 进入实付剩余付款方式,只允许非券付款方式进行付款
					isPreparePay = payPopOther;

					// 券除外付款只输入一次
					havepaycw = false;
				}

				// 计算同规则商品参与促销的合计
				calPop = (CalcRulePopDef) set.elementAt(i);
				double sphj = 0;
				for (j = 0; j < calPop.row_set.size(); j++)
				{
					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
					sphj += ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef));
				}

				// 如果只有一组促销规则,计算前存在的付款方式都算需要除外的付款
				// 如果有多个组促销规则,除外金额为该商品已分摊的付款金额
				for (j = 0; j < calPop.row_set.size(); j++)
				{
					SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
					if (spinfo.payft == null) continue;
					for (int n = 0; n < spinfo.payft.size(); n++)
					{
						String[] s = (String[]) spinfo.payft.elementAt(n);

						if (!calPop.popDef.catid.equals("Y"))
						{
							String[] pay = GlobalInfo.sysPara.mjPaymentRule.split(",");
							int x = 0;
							for (x = 0; x < pay.length; x++)
							{
								if (s[1].equals(pay[x].trim()))
								{
									break;
								}
							}

							if (x >= pay.length) sphj -= Convert.toDouble(s[3]);
						}
						else
						{
							sphj -= Convert.toDouble(s[3]);
						}
					}
				}

				if (sphj <= 0)
				{
					set.remove(i);
					i--;
					continue;
				}

				// 满减限额
				double limitje = 0;
				if (calPop.popDef.sl <= 0) limitje = 99999999;
				else limitje = calPop.popDef.sl;
				
				if (calPop.popDef.str5 == null || calPop.popDef.str5.trim().length() <= 0) calPop.popDef.str5 = "A";//若为空,则默认为A(满减) wangyong add by 2014.6.26
				
				//判断是满减还是买换券
				if (calPop.popDef.str5.equals("C"))
				{
					//double mhje = 0;   //买换金额
					calPop.popje = sphj;
					int num = 0;
					// 满足促销条件，不超过满减限额，不超过参与打折的金额
					double bcje = 0;
					if (calPop.popDef.str3 != null && calPop.popDef.str3.trim().length() > 0)
					{
						String[] row = calPop.popDef.str3.split(";");

						for (int c = 0; c < row.length; c++)
						{
							double a =0;
							double b =0;
							double min = 0;
							double max = 0;
							if(row[c] == null){
								continue;
							}else if (row[c] != null && row[c].split(",").length == 2){
								a = Convert.toDouble(row[c].split(",")[1]); //
								b = Convert.toDouble(row[c].split(",")[0]); //
								min = 0;
								max = 9999999;
							}else if(row[c] != null && row[c].split(",").length == 4)
							{
								a = Convert.toDouble(row[c].split(",")[3]); //
								b = Convert.toDouble(row[c].split(",")[2]); //
								min = Convert.toDouble(row[c].split(",")[0]); //
								max = Convert.toDouble(row[c].split(",")[1]); //
							}else{
								continue;
							}
							
							/***买换券金额计算公式
								用本单总金额-小值，如果：
									<=0    券金额为0
									>0    用本单总金额/大值，  
									如果<=1  取1倍（倍数）
									    >1  取实际的整数倍（倍数）
								用（倍数*大值）-本单总金额，如果：
									>0
									倍数>1，券金额=本单总金额-大值*（倍数 - 1）+（倍数 - 1）*（大值 - 小值）；
									　　倍数<=1，券金额=本单总金额-小值
									<=0
									　　本单总金额-倍数*大值-小值>0:
									　　券金额=（本单总金额 - 倍数 * 大值 - 小值）+ 倍数*（大值 - 小值）
									　　本单总金额-倍数*大值-小值<=0:
									券金额=倍数*（大值 - 小值）
								*/
							if (a == 0 || b == 0) continue;
							if (sphj < min || sphj > max) continue;
							//同规则商品总金额-小值
							double ce = ManipulatePrecision.doubleConvert(sphj-a,2,0);
							if(ce<=0){
								continue;
							}else{
							//同规则商品总金额/大值
								//　<=1  取1倍（倍数）
								double bs = ManipulatePrecision.doubleConvert(sphj/b,2,0);
								if(bs <=1){
									
									num = 1;
								}else{
									//>1  取实际的整数倍（倍数）
									num = (int)ManipulatePrecision.doubleConvert(bs,0,0,false);
								}
							}
							//（倍数*大值）-本单总金额
							double temp1 = ManipulatePrecision.doubleConvert((num * b)-sphj);
							//>=0
							if(temp1>0){
								if(num>1){
									//券金额=本单总金额-大值*（倍数 - 1）+（倍数 - 1）*（大值 - 小值）
									bcje = ManipulatePrecision.doubleConvert(sphj - b *(num - 1)+(num - 1)*(b - a));
								}else{
									//券金额=本单总金额-小值
									bcje = ManipulatePrecision.doubleConvert(sphj - a);
								}
							}else{
								//本单总金额-倍数*大值-小值
								double temp2 = sphj - num*b -a;
								if(temp2>0){
									//　　券金额=（本单总金额 - 倍数 * 大值 - 小值）+ 倍数*（大值 - 小值）
									bcje = ManipulatePrecision.doubleConvert((sphj - num*b-a)+num*(b-a));
								}else{
									//券金额=倍数*（大值 - 小值）
									bcje = ManipulatePrecision.doubleConvert(num*(b-a));
								}
							}
							
							if (bcje > limitje)
							{
								bcje = limitje;
							}
							if(bcje>0) calPop.mult_Amount = bcje;
							
							
						}
						if(calPop.mult_Amount<=0){
							set.remove(i);
							i--;
						}
					}
				}
				else
				{
					// 检查是否满足条件
					if (calPop.popDef.gz.equals("1")) // 按金额满减
					{
						double mjje = 0;
						calPop.popje = sphj;

						int num = 0;

						// 已参与满减的金额
						double yfmj = 0;

						if (GlobalInfo.sysPara.mjtype == 'Y')
						{
							//检查是否存在促销条件,现在全部的条件都在此地设定 用分号分隔 
							if ((calPop.popDef.str3 != null) && (calPop.popDef.str3.trim().length() > 0))
							{
								String[] row = calPop.popDef.str3.split(";");

								for (int c = row.length - 1; c >= 0; c--)
								{
									if ((row[c] == null) || (row[c].split(",").length != 4))
									{
										continue;
									}

									double a = Convert.toDouble(row[c].split(",")[0]); //参加下限
									double b = Convert.toDouble(row[c].split(",")[1]); //参加上限
									double t = Convert.toDouble(row[c].split(",")[2]); //满减条件
									double je = Convert.toDouble(row[c].split(",")[3]); //满减金额

									if ((je == 0) || (b == 0))
									{
										continue;
									}

									if ((ManipulatePrecision.doubleConvert(sphj - yfmj) >= a) && (ManipulatePrecision.doubleConvert(sphj - yfmj) <= b))
									{
										// 如果满减条件为0，直接取定义的满减金额
										if (t == 0)
										{
											if (je < limitje)
											{
												mjje = je;
											}
											else
											{
												mjje = limitje;
											}

											break;
										}

										//浮点运算1       = 0.999999,需要进位到两位小数再取整
										//浮点运算299/300 = 0.996666,进位取整=1,还需再乘分母用金额比较，如果大倍数要减1
										num = ManipulatePrecision.integerDiv(sphj - yfmj, t);

										double bcje = 0;
										if (num > 0)
										{
											bcje = num * je;
										}

										if (bcje > limitje)
										{
											bcje = limitje;
										}

										mjje += bcje;
										yfmj = ManipulatePrecision.doubleConvert(num * t + yfmj);
										if (mjje >= limitje || (GlobalInfo.sysPara.mjloop == 'N' && yfmj > 0))
										{
											break;
										}
										else
										{
											continue;
										}
									}
									else
									{
										continue;
									}
								}
							}
						}
						else
						{
							if (calPop.popDef.poplsj > 0)
							{
								//浮点运算1       = 0.999999,需要进位到两位小数再取整
								//浮点运算299/300 = 0.996666,进位取整=1,还需再乘分母用金额比较，如果大倍数要减1
								num = ManipulatePrecision.integerDiv(sphj - yfmj, calPop.popDef.poplsj);
							}

							// 满足促销条件，不超过满减限额，不超过参与打折的金额
							double bcje = num * calPop.popDef.pophyj;
							if (bcje + mjje > limitje) bcje = limitje - mjje;
							if (bcje > 0 && (bcje + mjje <= calPop.popje))
							{
								mjje += bcje;
								yfmj += num * calPop.popDef.poplsj;
							}

							// 检查是否存在附加促销条件
							// 允许递归计算满减
							if (yfmj > 0 && GlobalInfo.sysPara.mjloop == 'N')
							{

							}
							else if (calPop.popDef.str3 != null && calPop.popDef.str3.trim().length() > 0)
							{
								String[] row = calPop.popDef.str3.split(";");

								for (int c = 0; c < row.length; c++)
								{
									if (row[c] == null || row[c].split(",").length != 2) continue;

									double a = Convert.toDouble(row[c].split(",")[0]); //满减条件
									double b = Convert.toDouble(row[c].split(",")[1]); //满减金额

									if (a == 0 || b == 0) continue;

									//浮点运算1       = 0.999999,需要进位到两位小数再取整
									//浮点运算299/300 = 0.996666,进位取整=1,还需再乘分母用金额比较，如果大倍数要减1
									num = ManipulatePrecision.integerDiv(sphj - yfmj, a);

									// 满足促销条件，不超过满减限额，不超过参与打折的金额
									bcje = num * b;
									if (bcje + mjje > limitje) bcje = limitje - mjje;
									if (bcje > 0 && (bcje + mjje <= calPop.popje))
									{
										mjje += bcje;
										yfmj += num * a;
									}

									if (yfmj > 0 && GlobalInfo.sysPara.mjloop == 'N')
									{
										break;
									}
								}
							}
						}

						if (mjje > 0)
						{
							calPop.mult_Amount = mjje;
						}
						else
						{
							set.remove(i);
							i--;
						}
					}
					else if (calPop.popDef.gz.equals("2")) // 按百分比减现
					{
						// 无效的减现比例
						if ((calPop.popDef.poplsjzkl <= 0) || (calPop.popDef.poplsjzkl >= 1) || (sphj * calPop.popDef.poplsjzkl > limitje))
						{
							set.remove(i);
							i--;
						}
						else
						{
							calPop.popje = sphj;
						}
					}
					else
					{
						set.remove(i);
						i--;
					}
				}
			}

			// 无有效的、满足条件的规则促销
			if (set.size() <= 0) { return haveCrmPop; }

			// 循环计数,如果不需要除券,则不用进行第二次循环
			nwhile++;
			if (!havepaycw) nwhile++;
		} while (nwhile <= 2);

		String[] pay = GlobalInfo.sysPara.mjPaymentRule.split(",");

		boolean exsit = false;
		for (int jj = 0; jj < salePayment.size(); jj++)
		{
			SalePayDef spay = (SalePayDef) salePayment.elementAt(jj);
			for (int ii = 0; ii < pay.length; ii++)
			{

				if (spay.paycode.equals(pay[ii].trim()))
				{
					exsit = true;
					break;
				}
			}

			if (exsit) break;
		}

		// 满减和收券选其一时
		if (!(GlobalInfo.sysPara.ismj == 'Y' && exsit))
		{
			// str2记录规则串描述供小票打印
			saleHead.str2 = "";

			// 分摊满减折扣金额
			for (i = 0; i < set.size(); i++)
			{
				calPop = (CalcRulePopDef) set.elementAt(i);
				double je = 0;
				double hj = 0;
				//	判断是满减还是买换券
				if (calPop.popDef.str5.equals("C")){
					je = calPop.mult_Amount;
					je = getDetailOverFlow(je, GlobalInfo.syjDef.sswrfs);
					String line1 = "";
					for (int x = 0; x < calPop.row_set.size(); x++)
					{
						line1 += "," + String.valueOf(Convert.toInt((String) calPop.row_set.elementAt(x)) + 1);
					}

					line1 = line1.substring(1);

					saleHead.str2 += calPop.popDef.kssj + "\n" + Language.apply("买换：") + Convert.increaseChar(String.valueOf(je), 8) + "(" + line1 + ")\n";

					// 提示满减规则
//					new MessageBox("参加活动的金额为 " + ManipulatePrecision.doubleToString(calPop.popje) + " 元\n\n买换 "
//							+ ManipulatePrecision.doubleToString(je) + " 元");
					new MessageBox(Language.apply("参加活动的金额为 {0} 元\n\n买换 {1} 元" ,new Object[]{ManipulatePrecision.doubleToString(calPop.popje) ,ManipulatePrecision.doubleToString(je)}));
				}
				else
				{
					// 按金额满减
					if (calPop.popDef.gz.equals("1"))
					{
						je = calPop.mult_Amount;
						je = getDetailOverFlow(je, GlobalInfo.syjDef.sswrfs);
						String line1 = "";
						for (int x = 0; x < calPop.row_set.size(); x++)
						{
							line1 += "," + String.valueOf(Convert.toInt((String) calPop.row_set.elementAt(x)) + 1);
						}
	
						line1 = line1.substring(1);
	
						saleHead.str2 += calPop.popDef.kssj + "\n" + Language.apply("满减：") + Convert.increaseChar(String.valueOf(je), 8) + "(" + line1 + ")\n";
	
						// 提示满减规则
//						new MessageBox("参加活动的金额为 " + ManipulatePrecision.doubleToString(calPop.popje) + " 元\n\n减现 "
//								+ ManipulatePrecision.doubleToString(je) + " 元");
						new MessageBox(Language.apply("参加活动的金额为 {0} 元\n\n减现 {1} 元" ,new Object[]{ManipulatePrecision.doubleToString(calPop.popje) ,ManipulatePrecision.doubleToString(je)}));
					}
	
					// 按百分比减现
					if (calPop.popDef.gz.equals("2"))
					{
						je = calPop.popje * calPop.popDef.poplsjzkl;
						je = getDetailOverFlow(je, GlobalInfo.syjDef.sswrfs);
						String line1 = "";
						for (int x = 0; x < calPop.row_set.size(); x++)
						{
							line1 += "," + (String) calPop.row_set.elementAt(x);
						}
	
						line1 = line1.substring(1);
	
						saleHead.str2 += calPop.popDef.kssj + "\n" + Language.apply("满减：") + Convert.increaseChar(String.valueOf(je), 8) + "(" + line1 + ")\n";
	
						// 提示满减规则
//						new MessageBox("现有促销减现 " + ManipulatePrecision.doubleToString(calPop.popDef.poplsjzkl * 100) + "%\n\n你目前可参加活动的金额为 "
//								+ ManipulatePrecision.doubleToString(calPop.popje) + " 元\n\n你目前可以减现 " + ManipulatePrecision.doubleToString(je) + " 元");
						new MessageBox(Language.apply("现有促销减现 {0}%\n\n你目前可参加活动的金额为 {1} 元\n\n你目前可以减现 {2} 元" ,new Object[]{ManipulatePrecision.doubleToString(calPop.popDef.poplsjzkl * 100) ,ManipulatePrecision.doubleToString(calPop.popje) ,ManipulatePrecision.doubleToString(je)}));
					}
				}
				
				// 记录规则促销单据信息
				for (j = 0; j < calPop.row_set.size(); j++)
				{
					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
					SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
					GoodsPopDef popDef = (GoodsPopDef) crmPop.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
					hj += ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef) - getftje(spinfo));
					saleGoodsDef.zsdjbh = calPop.popDef.djbh;
					saleGoodsDef.zszkfd = popDef.poplsjzkfd;
				}

				// 分摊满减折扣到各商品
				double yfd = 0;
//				int row = -1;
//				double lje = -1;
				for (j = 0; j < calPop.row_set.size(); j++)
				{
					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
					SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
					GoodsPopDef popDef = (GoodsPopDef) crmPop.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));

					// 把剩余未分摊金额，直接分摊到最后一个商品,最后一个商品不处理价格精度
					double lszszk = 0;
					if (popDef.str5 == null || popDef.str5.trim().length()<=0) popDef.str5 = "A";//若为空,则默认为A(满减) wangyong add by 2013.5.12
					
					if (popDef.str5.equals("B"))
					{
						if (j == (calPop.row_set.size() - 1))
						{
							lszszk = ManipulatePrecision.doubleConvert(je - yfd, 2, 1);
						}
						else
						{
							//lszszk = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - getZZK(saleGoodsDef) - getftje(spinfo)) / hj * je, 2, 1);
							lszszk = getDetailOverFlow((saleGoodsDef.hjje - getZZK(saleGoodsDef) - getftje(spinfo)) / hj * je, GlobalInfo.syjDef.sswrfs);
						}
						
						if (lszszk <= 0) continue;

						if (!createMDPayment(lszszk, "MD"))
						{
							havePaymode = true;
							return false;
						}

						// liwj add
						SalePayDef sp = (SalePayDef) salePayment.elementAt(salePayment.size() - 1);
						String s[] = { String.valueOf(sp.num5), sp.paycode, sp.payname, String.valueOf(lszszk) };
						if (spinfo.payft == null) spinfo.payft = new Vector();
						spinfo.payft.add(s);
					}
					else if (popDef.str5.equals("C"))
					{
						if (j == (calPop.row_set.size() - 1))
						{
							lszszk = ManipulatePrecision.doubleConvert(je - yfd, 2, 1);
						}
						else
						{
							lszszk = getDetailOverFlow(ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - getZZK(saleGoodsDef) - getftje(spinfo)) / hj * je, 2, 1));
						}
						
						if (lszszk <= 0) continue;

						if (!createMDPayment(lszszk,"MH"))
						{
							havePaymode = true;
							return false;
						}
						// 打印促销描述
						String popDesc = "";
						if (popDef.jssj.indexOf('|') > -1)
						{
							popDesc = popDef.jssj.split("\\|")[0];
						}
						else
						{
							popDesc = popDef.jssj;
						}
						saleGoodsDef.str7 = popDesc;
						
						// liwj add
						SalePayDef sp = (SalePayDef) salePayment.elementAt(salePayment.size() - 1);
						String s[] = { String.valueOf(sp.num5), sp.paycode, sp.payname, String.valueOf(lszszk) };
						if (spinfo.payft == null) spinfo.payft = new Vector();
						spinfo.payft.add(s);
					}
					else if (popDef.str5.equals("A"))
					{
						//	把剩余未分摊金额，直接分摊到最后一个商品,最后一个商品不处理价格精度

						if (j == (calPop.row_set.size() - 1))
						{
							lszszk = ManipulatePrecision.doubleConvert(je - yfd, 2, 1);
							saleGoodsDef.zszke = ManipulatePrecision.doubleConvert(saleGoodsDef.zszke + lszszk, 2, 1);
						}
						else
						{
							lszszk = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - getZZK(saleGoodsDef) - getftje(spinfo)) / hj * je, 2, 1);
							double oldzszke = saleGoodsDef.zszke;
							saleGoodsDef.zszke += lszszk;
							saleGoodsDef.zszke = getConvertRebate(Integer.parseInt((String) calPop.row_set.elementAt(j)), saleGoodsDef.zszke);
							saleGoodsDef.zszke = getConvertRebate(Integer.parseInt((String) calPop.row_set.elementAt(j)), saleGoodsDef.zszke,
																	getGoodsApportionPrecision());
							lszszk = ManipulatePrecision.doubleConvert(saleGoodsDef.zszke - oldzszke, 2, 1);
						}
						getZZK(saleGoodsDef);
					}
					// 计算已分摊的金额
					yfd += lszszk;
				}
			}

			// 重算应收
			calcHeadYsje();

			// 刷新商品列表
			saleEvent.updateTable(getSaleGoodsDisplay());
			saleEvent.setTotalInfo();
			haveCrmPop = true;
		}
		return haveCrmPop;

	}

	/*
	 public boolean doCrmPop()
	 {
	 boolean haveCrmPop = false;

	 //清空，放满减描述
	 saleHead.str2 = "";

	 // 默认总是不进行分摊付款的
	 apportionPay = false;

	 // 先总是无满减规则方式的付款
	 isPreparePay = payNormal;

	 if (!SellType.ISSALE(saletype)) { return false; }
	 
	 if (SellType.NOPOP(saletype)) return false;

	 if (SellType.ISEARNEST(saletype)) { return false; }

	 if (SellType.ISPREPARETAKE(saletype)) { return false; }

	 // 先进行直接打折
	 int i = 0;
	 double hjzszk = 0;
	 for (i = 0; i < saleGoods.size(); i++)
	 {
	 SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);

	 String mjrule = ((SpareInfoDef) goodsSpare.elementAt(i)).str1;
	 GoodsPopDef goodsPop1 = (GoodsPopDef) crmPop.elementAt(i);
	 double zkl = ((GoodsDef) goodsAssistant.elementAt(i)).maxzkl;

	 // 不计算换购商品
	 if (((SpareInfoDef) goodsSpare.elementAt(i)).char2 == 'Y') continue;

	 if (mjrule.charAt(0) == '9') mjrule = mjrule.substring(1);
	 
	 if (mjrule.charAt(0) == '1')
	 {
	 double sj = saleGoodsDef.hjje - getZZK(saleGoodsDef);
	 double dz = ManipulatePrecision.mul(sj, goodsPop1.poplsjzkl);

	 double minje = saleGoodsDef.hjje * zkl;

	 if (dz < minje)
	 {
	 saleGoodsDef.zszke = ManipulatePrecision.sub(sj, minje);
	 saleGoodsDef.zszkfd = goodsPop1.poplsjzkfd;
	 saleGoodsDef.zsdjbh = goodsPop1.djbh;
	 }
	 else
	 {
	 saleGoodsDef.zszke = ManipulatePrecision.sub(sj, dz);
	 saleGoodsDef.zszkfd = goodsPop1.poplsjzkfd;
	 saleGoodsDef.zsdjbh = goodsPop1.djbh;
	 }
	 // 计算价格精度
	 if (saleGoodsDef.zszke > 0) saleGoodsDef.zszke = getConvertRebate(i, saleGoodsDef.zszke);

	 getZZK(saleGoodsDef);
	 hjzszk += saleGoodsDef.zszke;
	 
	 haveCrmPop = true;
	 }
	 }

	 if (hjzszk > 0)
	 {
	 // 重算应收
	 calcHeadYsje();

	 // 刷新商品列表
	 saleEvent.updateTable(getSaleGoodsDisplay());
	 saleEvent.setTotalInfo();

	 new MessageBox("有商品参加活动促销，总共可打折 " + ManipulatePrecision.doubleToString(hjzszk));
	 }

	 // 在VIP促销需要除券计算模式下，计算VIP前先提示输入券付款
	 boolean vippaycw = false;
	 if (GlobalInfo.sysPara.vipPayExcp == 'Y' && checkMemberSale() && curCustomer != null && curCustomer.iszk == 'Y'
	 && GlobalInfo.sysPara.vipPromotionCrm != null && GlobalInfo.sysPara.vipPromotionCrm.equals("2"))
	 {
	 // 提示先输入券付款
	 if (new MessageBox("券付款不参与VIP折扣,请先输入券付款金额\n\n如果顾客没有券付款,请直接按‘退出’键").verify() != GlobalVar.Exit)
	 {
	 // 开始预付除外付款方式
	 isPreparePay = payPopPrepare;

	 // 打开付款窗口
	 new SalePayForm().open(saleEvent.saleBS);

	 // 付款完成，开始新交易
	 if (this.saleFinish)
	 {
	 sellFinishComplete();

	 // 预先付款就已足够,不再继续后续付款
	 doRulePopExit = true;
	 return false; // 表示没有满减促销,取消付款时无需恢复
	 }
	 }

	 // 进入实付剩余付款方式,只允许非券付款方式进行付款
	 isPreparePay = payPopOther;

	 // 标记已输入除外付款，后面满减时不再输入除外付款
	 vippaycw = true;
	 }

	 // 如果为VIP折扣区间的打折方式，在满减前计算    	
	 if (checkMemberSale() && curCustomer != null && curCustomer.iszk == 'Y' && GlobalInfo.sysPara.vipPromotionCrm != null
	 && GlobalInfo.sysPara.vipPromotionCrm.equals("2"))
	 {
	 // vipzk2表示按下付款键时才计算VIP折扣
	 for (int k = 0; k < saleGoods.size(); k++)
	 {
	 getVIPZK(k, vipzk2);
	 }

	 // 重算小票应收  
	 calcHeadYsje();

	 // 刷新商品列表
	 saleEvent.updateTable(getSaleGoodsDisplay());
	 saleEvent.setTotalInfo();

	 //显示会员卡折扣总金额
	 if (saleHead.hyzke > 0) new MessageBox("会员折扣总金额 ：" + saleHead.hyzke);
	 }

	 // 检查促销折扣控制 如果低于折扣率,不进行满减,返券,返礼促销
	 for (int j = 0; j < saleGoods.size(); j++)
	 {
	 SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(j);
	 String mjrule = ((SpareInfoDef) goodsSpare.elementAt(j)).str1;
	 GoodsPopDef goodsPop1 = (GoodsPopDef) crmPop.elementAt(j);
	 double zkl = 0;
	 if(saleGoodsDef.hjje != 0)
	 {
	 zkl = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - getZZK(saleGoodsDef)) / saleGoodsDef.hjje, 2, 1);
	 }
	 
	 if (zkl < goodsPop1.pophyjzkfd)
	 {
	 
	 if (mjrule.charAt(0) == '9')
	 {
	 StringBuffer buff = new StringBuffer(mjrule);
	 for (int z = 2 ; z < buff.length(); z++)
	 {
	 buff.setCharAt(z, '0');
	 }
	 mjrule = buff.toString();
	 }
	 else
	 {
	 mjrule = mjrule.charAt(0) + "000";
	 }
	 ((SpareInfoDef) goodsSpare.elementAt(j)).str1 = mjrule;

	 if (GlobalInfo.sysPara.iscrmtjprice == 'Y') saleGoodsDef.str3 = mjrule
	 + String.valueOf(Convert.increaseInt(goodsPop1.yhspace, 5).substring(4))
	 + saleGoodsDef.str3.substring(saleGoodsDef.str3.indexOf(";"));
	 else saleGoodsDef.str3 = mjrule + saleGoodsDef.str3.substring(saleGoodsDef.str3.indexOf(";"));
	 }
	 }

	 // 检查是否需要分摊
	 for (int j = 0; j < saleGoods.size(); j++)
	 {
	 String mjrule = ((SpareInfoDef) goodsSpare.elementAt(j)).str1;
	 //GoodsPopDef goodsPop1 = (GoodsPopDef) crmPop.elementAt(j);
	 //String[] rules = goodsPop1.memo.split(",");
	 if (mjrule.charAt(0) == '9' && mjrule.length() > 3 && mjrule.charAt(2) == '1') apportionPay = true;
	 if (mjrule.charAt(1) == '1') apportionPay = true;
	 if (apportionPay)
	 {
	 break;
	 }
	 }

	 // 再查找是否存在满减或减现
	 int j = 0;
	 Vector set = new Vector();
	 CalcRulePopDef calPop = null;

	 // 先按商品分组促销规则
	 for (i = 0; i < saleGoods.size(); i++)
	 {
	 SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
	 GoodsDef goodsDef = ((GoodsDef) goodsAssistant.elementAt(i));
	 GoodsPopDef goodsPop = (GoodsPopDef) crmPop.elementAt(i);
	 String mjrule = ((SpareInfoDef) goodsSpare.elementAt(i)).str1;
	 String ruleCode = goodsDef.specinfo;

	 if (mjrule.charAt(0) == '9') mjrule = mjrule.substring(1);
	 // 选择了不参与减现继续下一个商品
	 if (mjrule.equals("N") || (mjrule.charAt(1) != '1'))
	 {
	 continue;
	 }

	 // 查找是否相同促销规则
	 for (j = 0; j < set.size(); j++)
	 {
	 calPop = (CalcRulePopDef) set.elementAt(j);

	 int oldIndex = Integer.parseInt((String) calPop.row_set.elementAt(0));
	 SaleGoodsDef saleGoodsDef1 = (SaleGoodsDef) saleGoods.elementAt(oldIndex);
	 GoodsDef goodsDef1 = ((GoodsDef) goodsAssistant.elementAt(oldIndex));
	 GoodsPopDef goodsPop1 = (GoodsPopDef) crmPop.elementAt(oldIndex);
	 String mjrule1 = ((SpareInfoDef) goodsSpare.elementAt(oldIndex)).str1;

	 if (mjrule1.charAt(0) == '9') mjrule1 = mjrule1.substring(1);
	 // 判断是否为同规则促销
	 if (isSamePop(saleGoodsDef, goodsDef, goodsPop, mjrule, saleGoodsDef1, goodsDef1, goodsPop1, mjrule1))
	 {
	 calPop.row_set.add(String.valueOf(i));

	 break;
	 }
	 }

	 if (j >= set.size())
	 {
	 calPop = new CalcRulePopDef();
	 calPop.code = saleGoodsDef.code;
	 calPop.gz = saleGoodsDef.gz;
	 calPop.uid = saleGoodsDef.uid;
	 calPop.rulecode = ruleCode;
	 calPop.catid = saleGoodsDef.catid;
	 calPop.ppcode = saleGoodsDef.ppcode;
	 calPop.popDef = goodsPop;
	 calPop.row_set = new Vector();
	 calPop.row_set.add(String.valueOf(i));
	 set.add(calPop);
	 }
	 }

	 // 无规则促销
	 if (set.size() <= 0) { return haveCrmPop; }

	 // 满减前先对所有商品进行舍分处理
	 this.calcSellPayMoney(true);

	 // 引用促销规则集合，用于付款分摊时进行判断，只有一个规则自动平摊到每个商品
	 rulePopSet = set;

	 // 检查是否要除券
	 boolean havepaycw = false;
	 for (i = 0; i < set.size(); i++)
	 {
	 calPop = (CalcRulePopDef) set.elementAt(i);

	 if (calPop.popDef.catid.equals("Y"))
	 {
	 havepaycw = true;
	 break;
	 }
	 }

	 // 前面已经进行了VIP除外付款输入,不再输入除外付款
	 if (vippaycw) havepaycw = false;

	 //int cxgz = set.size();
	 // 循环两次
	 // 第一次先检查是否有满足条件的规则,如果没有则直接返回
	 // 第二次检查除券外是否还有满足条件的规则,如果不需要除券,则只用循环一次
	 int nwhile = 1;
	 do
	 {
	 // 开始计算商品分组参与计算的合计金额
	 for (i = 0; i < set.size(); i++)
	 {
	 // 如果是能进入第二次循环,说明有交易金额是满足促销条件的规则促销
	 // 如果需要扣除券付款,先输入券付款方式
	 if ((nwhile >= 2) && havepaycw)
	 {
	 // 提示先输入券付款
	 if (GlobalInfo.sysPara.mjPaymentRule.trim().length() > 0
	 && new MessageBox("本笔交易有活动促销,请先输入券付款金额\n\n如果顾客没有券付款,请直接按‘退出’键").verify() != GlobalVar.Exit)
	 {
	 // 开始预付除外付款方式
	 isPreparePay = payPopPrepare;

	 // 打开付款窗口
	 new SalePayForm().open(saleEvent.saleBS);

	 // 付款完成，开始新交易
	 if (this.saleFinish)
	 {
	 sellFinishComplete();

	 // 预先付款就已足够,不再继续后续付款
	 doRulePopExit = true;
	 return false;
	 }
	 }

	 // 进入实付剩余付款方式,只允许非券付款方式进行付款
	 isPreparePay = payPopOther;

	 // 券除外付款只输入一次
	 havepaycw = false;
	 }

	 // 计算同规则商品参与促销的合计
	 calPop = (CalcRulePopDef) set.elementAt(i);
	 double sphj = 0;
	 for (j = 0; j < calPop.row_set.size(); j++)
	 {
	 SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
	 sphj += ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef));
	 }

	 // 如果只有一组促销规则,计算前存在的付款方式都算需要除外的付款
	 // 如果有多个组促销规则,除外金额为该商品已分摊的付款金额
	 for (j = 0; j < calPop.row_set.size(); j++)
	 {
	 SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
	 if (spinfo.payft == null) continue;
	 for (int n = 0; n < spinfo.payft.size(); n++)
	 {
	 String[] s = (String[]) spinfo.payft.elementAt(n);

	 if (!calPop.popDef.catid.equals("Y"))
	 {
	 String[] pay = GlobalInfo.sysPara.mjPaymentRule.split(",");
	 int x = 0;
	 for (x = 0; x < pay.length; x++)
	 {
	 if (s[1].equals(pay[x].trim()))
	 {
	 break;
	 }
	 }

	 if (x >= pay.length) sphj -= Convert.toDouble(s[3]);
	 }
	 else
	 {
	 sphj -= Convert.toDouble(s[3]);
	 }
	 }
	 }

	 if (sphj <= 0)
	 {
	 set.remove(i);
	 i--;
	 continue;
	 }

	 // 满减限额
	 double limitje = 0;
	 if (calPop.popDef.sl <= 0) limitje = 99999999;
	 else limitje = calPop.popDef.sl;

	 // 检查是否满足条件
	 if (calPop.popDef.gz.equals("1")) // 按金额满减
	 {
	 double mjje = 0;
	 calPop.popje = sphj;

	 int num = 0;

	 // 已参与满减的金额
	 double yfmj = 0;

	 if (GlobalInfo.sysPara.mjtype == 'Y')
	 {
	 //检查是否存在促销条件,现在全部的条件都在此地设定 用分号分隔 
	 if ((calPop.popDef.str3 != null) && (calPop.popDef.str3.trim().length() > 0))
	 {
	 String[] row = calPop.popDef.str3.split(";");

	 for (int c = row.length - 1; c >= 0; c--)
	 {
	 if ((row[c] == null) || (row[c].split(",").length != 4))
	 {
	 continue;
	 }

	 double a = Convert.toDouble(row[c].split(",")[0]); //参加下限
	 double b = Convert.toDouble(row[c].split(",")[1]); //参加上限
	 double t = Convert.toDouble(row[c].split(",")[2]); //满减条件
	 double je = Convert.toDouble(row[c].split(",")[3]); //满减金额

	 if ((je == 0) || (b == 0))
	 {
	 continue;
	 }

	 if ((ManipulatePrecision.doubleConvert(sphj - yfmj) >= a) && (ManipulatePrecision.doubleConvert(sphj - yfmj) <= b))
	 {
	 // 如果满减条件为0，直接取定义的满减金额
	 if (t == 0)
	 {
	 if (je < limitje)
	 {
	 mjje = je;
	 }
	 else
	 {
	 mjje = limitje;
	 }

	 break;
	 }

	 //浮点运算1       = 0.999999,需要进位到两位小数再取整
	 //浮点运算299/300 = 0.996666,进位取整=1,还需再乘分母用金额比较，如果大倍数要减1
	 num = ManipulatePrecision.integerDiv(sphj - yfmj, t);

	 double bcje = 0;
	 if (num > 0)
	 {
	 bcje = num * je;
	 }

	 if (bcje > limitje)
	 {
	 bcje = limitje;
	 }

	 mjje += bcje;
	 yfmj = ManipulatePrecision.doubleConvert(num * t + yfmj);
	 if (mjje >= limitje || (GlobalInfo.sysPara.mjloop == 'N' && yfmj > 0))
	 {
	 break;
	 }
	 else
	 {
	 continue;
	 }
	 }
	 else
	 {
	 continue;
	 }
	 }
	 }
	 }
	 else
	 {
	 if (calPop.popDef.poplsj > 0)
	 {
	 //浮点运算1       = 0.999999,需要进位到两位小数再取整
	 //浮点运算299/300 = 0.996666,进位取整=1,还需再乘分母用金额比较，如果大倍数要减1
	 num = ManipulatePrecision.integerDiv(sphj - yfmj, calPop.popDef.poplsj);
	 }

	 // 满足促销条件，不超过满减限额，不超过参与打折的金额
	 double bcje = num * calPop.popDef.pophyj;
	 if (bcje + mjje > limitje) bcje = limitje - mjje;
	 if (bcje > 0 && (bcje + mjje <= calPop.popje))
	 {
	 mjje += bcje;
	 yfmj += num * calPop.popDef.poplsj;
	 }

	 // 检查是否存在附加促销条件
	 // 允许递归计算满减
	 if (yfmj > 0 && GlobalInfo.sysPara.mjloop == 'N')
	 {

	 }
	 else if (calPop.popDef.str3 != null && calPop.popDef.str3.trim().length() > 0)
	 {
	 String[] row = calPop.popDef.str3.split(";");

	 for (int c = 0; c < row.length; c++)
	 {
	 if (row[c] == null || row[c].split(",").length != 2) continue;

	 double a = Convert.toDouble(row[c].split(",")[0]); //满减条件
	 double b = Convert.toDouble(row[c].split(",")[1]); //满减金额

	 if (a == 0 || b == 0) continue;

	 //浮点运算1       = 0.999999,需要进位到两位小数再取整
	 //浮点运算299/300 = 0.996666,进位取整=1,还需再乘分母用金额比较，如果大倍数要减1
	 num = ManipulatePrecision.integerDiv(sphj - yfmj, a);

	 // 满足促销条件，不超过满减限额，不超过参与打折的金额
	 bcje = num * b;
	 if (bcje + mjje > limitje) bcje = limitje - mjje;
	 if (bcje > 0 && (bcje + mjje <= calPop.popje))
	 {
	 mjje += bcje;
	 yfmj += num * a;
	 }

	 if (yfmj > 0 && GlobalInfo.sysPara.mjloop == 'N')
	 {
	 break;
	 }
	 }
	 }
	 }

	 if (mjje > 0)
	 {
	 calPop.mult_Amount = mjje;
	 }
	 else
	 {
	 set.remove(i);
	 i--;
	 }
	 }
	 else if (calPop.popDef.gz.equals("2")) // 按百分比减现
	 {
	 // 无效的减现比例
	 if ((calPop.popDef.poplsjzkl <= 0) || (calPop.popDef.poplsjzkl >= 1) || (sphj * calPop.popDef.poplsjzkl > limitje))
	 {
	 set.remove(i);
	 i--;
	 }
	 else
	 {
	 calPop.popje = sphj;
	 }
	 }
	 else
	 {
	 set.remove(i);
	 i--;
	 }
	 }

	 // 无有效的、满足条件的规则促销
	 if (set.size() <= 0) { return haveCrmPop; }

	 // 循环计数,如果不需要除券,则不用进行第二次循环
	 nwhile++;
	 if (!havepaycw) nwhile++;
	 } while (nwhile <= 2);

	 String[] pay = GlobalInfo.sysPara.mjPaymentRule.split(",");

	 boolean exsit = false;
	 for (int jj = 0; jj < salePayment.size(); jj++)
	 {
	 SalePayDef spay = (SalePayDef) salePayment.elementAt(jj);
	 for (int ii = 0; ii < pay.length; ii++)
	 {

	 if (spay.paycode.equals(pay[ii].trim()))
	 {
	 exsit = true;
	 break;
	 }
	 }

	 if (exsit) break;
	 }

	 // 满减和收券选其一时
	 if (!(GlobalInfo.sysPara.ismj == 'Y' && exsit))
	 {
	 // str2记录规则串描述供小票打印
	 saleHead.str2 = "";

	 // 分摊满减折扣金额
	 for (i = 0; i < set.size(); i++)
	 {
	 calPop = (CalcRulePopDef) set.elementAt(i);
	 double je = 0;
	 double hj = 0;

	 // 按金额满减
	 if (calPop.popDef.gz.equals("1"))
	 {
	 je = calPop.mult_Amount;
	 String line1 = "";
	 for (int x = 0; x < calPop.row_set.size(); x++)
	 {
	 line1 += "," + String.valueOf(Convert.toInt((String) calPop.row_set.elementAt(x)) + 1);
	 }

	 line1 = line1.substring(1);

	 saleHead.str2 += calPop.popDef.kssj + "\n" + "满减：" + Convert.increaseChar(String.valueOf(je), 8) + "(" + line1 + ")\n";

	 // 提示满减规则
	 new MessageBox("参加活动的金额为 " + ManipulatePrecision.doubleToString(calPop.popje) + " 元\n\n减现 "
	 + ManipulatePrecision.doubleToString(je) + " 元");
	 }

	 // 按百分比减现
	 if (calPop.popDef.gz.equals("2"))
	 {
	 je = calPop.popje * calPop.popDef.poplsjzkl;

	 String line1 = "";
	 for (int x = 0; x < calPop.row_set.size(); x++)
	 {
	 line1 += "," + (String) calPop.row_set.elementAt(x);
	 }

	 line1 = line1.substring(1);

	 saleHead.str2 += calPop.popDef.kssj + "\n" + "满减：" + Convert.increaseChar(String.valueOf(je), 8) + "(" + line1 + ")\n";

	 // 提示满减规则
	 new MessageBox("现有促销减现 " + ManipulatePrecision.doubleToString(calPop.popDef.poplsjzkl * 100) + "%\n\n你目前可参加活动的金额为 "
	 + ManipulatePrecision.doubleToString(calPop.popje) + " 元\n\n你目前可以减现 " + ManipulatePrecision.doubleToString(je) + " 元");
	 }

	 // 记录规则促销单据信息
	 for (j = 0; j < calPop.row_set.size(); j++)
	 {
	 SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
	 SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
	 GoodsPopDef popDef = (GoodsPopDef) crmPop.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
	 hj += ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef) - getftje(spinfo));
	 saleGoodsDef.zsdjbh = calPop.popDef.djbh;
	 saleGoodsDef.zszkfd = popDef.poplsjzkfd;
	 }

	 // 分摊满减折扣到各商品
	 double yfd = 0;
	 int row = -1;
	 double lje = -1;
	 for (j = 0; j < calPop.row_set.size(); j++)
	 {
	 SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
	 SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));

	 // 把剩余未分摊金额，直接分摊到最后一个商品,最后一个商品不处理价格精度
	 double lszszk = 0;

	 double jsje = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef) - getftje(spinfo));

	 lszszk = ManipulatePrecision.doubleConvert( jsje/ hj * je, 2, 1);
	 double oldzszke = saleGoodsDef.zszke;
	 saleGoodsDef.zszke += lszszk;
	 saleGoodsDef.zszke = getConvertRebate(Integer.parseInt((String) calPop.row_set.elementAt(j)), saleGoodsDef.zszke);
	 saleGoodsDef.zszke = getConvertRebate(Integer.parseInt((String) calPop.row_set.elementAt(j)), saleGoodsDef.zszke,
	 getGoodsApportionPrecision());
	 System.out.println(saleGoodsDef.barcode+" "+saleGoodsDef.zszke+" "+oldzszke);
	 lszszk = ManipulatePrecision.doubleConvert(saleGoodsDef.zszke - oldzszke, 2, 1);
	 
	 getZZK(saleGoodsDef);
	 
	 //计算已分摊的金额
	 //yfd += lszszk;
	 yfd = ManipulatePrecision.doubleConvert(yfd+lszszk, 2, 1);
	 
	 // 计算完全后记录下当前最大可分摊的商品，用于将剩余金额分摊到商品上
	 double jsje1 = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef) - getftje(spinfo));
	 if (jsje1 >= lje)
	 {
	 row = Integer.parseInt((String) calPop.row_set.elementAt(j));
	 lje = jsje1;
	 }

	 // 如果是最后一个可分摊的商品计算完全后，检查已分分摊的金额和应分摊金额的差额，多退少补
	 if (j == calPop.row_set.size() - 1 && ManipulatePrecision.doubleCompare(je,yfd, 2) != 0 && row > -1)
	 {
	 saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(row);
	 lszszk = ManipulatePrecision.doubleConvert(je - yfd, 2, 1);
	 saleGoodsDef.zszke = ManipulatePrecision.doubleConvert(saleGoodsDef.zszke + lszszk, 2, 1);
	 getZZK(saleGoodsDef);
	 }
	 }
	 }

	 // 重算应收
	 calcHeadYsje();

	 // 刷新商品列表
	 saleEvent.updateTable(getSaleGoodsDisplay());
	 saleEvent.setTotalInfo();

	 // 提示收银员查看满减结果
	 //new MessageBox("请核对促销活动的相关折扣金额!");

	 //
	 haveCrmPop = true;
	 }
	 return haveCrmPop;
	 }
	 */

	public boolean isSamePop(SaleGoodsDef salegoods1, GoodsDef goods1, GoodsPopDef popDef1, String mjrule1, SaleGoodsDef salegoods2, GoodsDef goods2, GoodsPopDef popDef2, String mjrule2)
	{
		if ((popDef1.memo.indexOf(",") == 0) || (popDef2.memo.indexOf(",") == 0)) { return false; }

		// 截取出满减规则
		String mjdh1 = popDef1.memo.substring(0, popDef1.memo.indexOf(","));
		String mjdh2 = popDef2.memo.substring(0, popDef2.memo.indexOf(","));

		// 选的是同一个满减规则，且允许跨柜则认为是同一个规则，要进行合计
		if (mjdh1.equalsIgnoreCase(mjdh2)
				&& ((popDef1.ppcode.equalsIgnoreCase("Y") && popDef2.ppcode.equalsIgnoreCase("Y")) || ((!popDef1.ppcode.equalsIgnoreCase("Y") || !popDef2.ppcode
																																								.equalsIgnoreCase("Y")) && salegoods1.gz
																																																		.equalsIgnoreCase(salegoods2.gz))))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public void paySellCancel_Extend()
	{
		// 判断是否需要删除VIP折扣
		if (checkMemberSale() && curCustomer != null && curCustomer.iszk == 'Y' && GlobalInfo.sysPara.vipPromotionCrm != null
				&& GlobalInfo.sysPara.vipPromotionCrm.equals("2"))
		{
			for (int i = 0; i < saleGoods.size(); i++)
			{
				// 不为VIP折扣的商品，不删除VIP折扣
				GoodsDef gd1 = (GoodsDef) goodsAssistant.get(i);
				if (gd1.isvipzk == 'N') continue;

				SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
				if (goodsSpare.size() <= i) continue;
				SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(i);
				if (info.char1 == 'Y')
				{
					continue;
				}
				saleGoodsDef.hyzke = 0;
				getZZK(saleGoodsDef);
			}

			// 重算小票应收  
			calcHeadYsje();

			// 刷新商品列表
			saleEvent.updateTable(getSaleGoodsDisplay());
			saleEvent.setTotalInfo();
		}

	}

	public void paySellCancel()
	{
		paySellCancel_Extend();

		super.paySellCancel();
	}

	public boolean checkHHFlag(PayModeDef paymode)
	{
		try
		{
			// 换退时只需要显示0710换退付款方式就行
			if (!SellType.ISSALE(this.saletype) && hhflag == 'Y')
			{
				if (paymode.code.equals("0710")) return true;
				else return false;
			}
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	public boolean getPayModeByNeed(PayModeDef paymode)
	{
		// 换货状态下不显示积分消费和积分换购
		if (paymode.code.equals("0509")) return false;

		/*// 换退时只需要显示0710换退付款方式就行
		if (!SellType.ISSALE(this.saletype) && hhflag == 'Y')
		{
			if (paymode.code.equals("0710")) return true;
			else return false;
		}*/
		if(!checkHHFlag(paymode)) return false;

		// 买券时不允许使用除券付款方式
		if (SellType.ISCOUPON(this.saletype) || SellType.ISJFSALE(this.saletype))
		{
			String[] pay = null;
			if (GlobalInfo.sysPara.mjPaymentRule.indexOf(",") >= 0) pay = GlobalInfo.sysPara.mjPaymentRule.split(",");
			else pay = GlobalInfo.sysPara.mjPaymentRule.split("\\|");

			for (int i = 0; i < pay.length; i++)
			{
				if (paymode.code.equals(pay[i].trim())) { return false; }
			}

			if (paymode.ismj != 'Y')
			{
				int submode = getPayModeBySuper(paymode.code).size();
				if (submode <= 0) return false;
			}
			return true;
		}

		// 无满减的实际付款，所有付款方式都可以
		if (isPreparePay == payNormal) { return true; }

		// 满减预先付款只先付券类付款方式
		if (isPreparePay == payPopPrepare)
		{
			String[] pay = null;
			if (GlobalInfo.sysPara.mjPaymentRule.indexOf(",") >= 0) pay = GlobalInfo.sysPara.mjPaymentRule.split(",");
			else pay = GlobalInfo.sysPara.mjPaymentRule.split("\\|");

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
			if (GlobalInfo.sysPara.mjPaymentRule.indexOf(",") >= 0) pay = GlobalInfo.sysPara.mjPaymentRule.split(",");
			else pay = GlobalInfo.sysPara.mjPaymentRule.split("\\|");

			for (int i = 0; i < pay.length; i++)
			{
				if (paymode.code.equals(pay[i].trim())) { return false; }
			}

			return true;
		}

		return true;
	}

	public double getGoodsftje(int index)
	{
		if (goodsSpare == null || goodsSpare.size() <= index) return 0;
		SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(index);
		if (spinfo == null || spinfo.payft == null) return 0;

		return getftje(spinfo);
	}

	public double getftje(SpareInfoDef spinfo)
	{
		double ftje = 0;
		if (spinfo.payft != null)
		{
			for (int j = 0; j < spinfo.payft.size(); j++)
			{
				String[] s = (String[]) spinfo.payft.elementAt(j);
				if (s.length > 3) ftje += Convert.toDouble(s[3]);
			}
		}
		return ftje;
	}

	public boolean baseApportion(SalePayDef spay, Payment payobj)
	{
		if (SellType.ISSALE(saleHead.djlb))
		{
			// 受限的MZK
			if (CreatePayment.getDefault().isPaymentMzk(spay.paycode))
			{
				if(GlobalInfo.sysPara.isUseNewMzkRange=='Y')
				{
					//百货新使用范围（精确到单品） WANGYONG ADD BY 2015.1.16
					String mzkretstr2 = ((PaymentMzk) payobj).mzkret.str2;
					
					if (mzkretstr2 != null && mzkretstr2.length() > 0 && mzkretstr2.split(",").length==saleGoods.size())
					{
						String[] goodsList = mzkretstr2.split(",");
						Vector v = new Vector();
						double hjje = 0;

						// 查询出所有能分摊的商品
						
						for (int i = 0; i < saleGoods.size(); i++)
						{
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);
							SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(i);

							if (goodsList[i].trim().equalsIgnoreCase("0"))//(((PaymentMzk) payobj).mzkret.str2.indexOf(sg.gz) >= 0)
							{
								if (spinfo == null) continue;

								// 计算商品可收付款的金额 = 成交价 - 已分摊的付款
								double ftje = getftje(spinfo);

								double maxfdje = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) - ftje);

								hjje += maxfdje;

								if (maxfdje > 0)
								{
									String[] row = {
													sg.barcode,
													sg.name,
													ManipulatePrecision.doubleToString(ftje),
													ManipulatePrecision.doubleToString(maxfdje),
													"",
													String.valueOf(i) };
									v.add(row);
								}
							}
						}

						// 计算损益
						if (ManipulatePrecision.doubleConvert(spay.je - spay.num1) > hjje)
						{
							spay.num1 = ManipulatePrecision.doubleConvert(spay.num1 + (ManipulatePrecision.doubleConvert(spay.je - spay.num1) - hjje));
						}
						// 开始分摊
						double syje = ManipulatePrecision.doubleConvert(spay.je - spay.num1); // 剩余金额

						if (GlobalInfo.sysPara.apportMode == 'C')
						{
							for (int i = v.size() - 1; i >= 0; i--)
							{
								String row[] = (String[]) v.elementAt(i);

								double je = 0;

								if (syje > Convert.toDouble(row[row.length - 3]))
								{
									je = Convert.toDouble(row[row.length - 3]);
								}
								else
								{
									je = syje;
								}
								row[row.length - 2] = String.valueOf(je);

								syje = ManipulatePrecision.doubleConvert(syje - je);

								if (syje <= 0) break;
							}
						}
						else
						{
							for (int i = 0; i < v.size(); i++)
							{
								String row[] = (String[]) v.elementAt(i);

								if (i == (v.size() - 1))
								{
									row[row.length - 2] = String.valueOf(syje);
									continue;
								}

								double je = ManipulatePrecision
																.doubleConvert((Convert.toDouble(row[row.length - 3]) / hjje * ManipulatePrecision
																																					.doubleConvert(spay.je
																																							- spay.num1)));
								row[row.length - 2] = String.valueOf(je);

								syje = ManipulatePrecision.doubleConvert(syje - je);
							}
						}

						// 记录商品分摊金额
						for (int i = 0; i < v.size(); i++)
						{
							String[] row = (String[]) v.elementAt(i);
							if (row[row.length - 2].length() <= 0) continue;

							// 按商品记录对应的付款分摊
							// 付款方式唯一序号,付款代码,付款名称(主要判断A/B券),分摊金额
							SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(Integer.parseInt(row[row.length - 1]));
							if (info.payft == null) info.payft = new Vector();
							String[] ft = new String[] { String.valueOf(spay.num5), spay.paycode, spay.payname, row[row.length - 2] };
							info.payft.add(ft);
						}
					}
				}
				else
				{
					if (((PaymentMzk) payobj).mzkret.str2 != null && ((PaymentMzk) payobj).mzkret.str2.length() > 0)
					{
						Vector v = new Vector();
						double hjje = 0;

						// 查询出所有能分摊的商品
						for (int i = 0; i < saleGoods.size(); i++)
						{
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);
							SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(i);

							if (((PaymentMzk) payobj).mzkret.str2.indexOf(sg.gz) >= 0)
							{
								if (spinfo == null) continue;

								// 计算商品可收付款的金额 = 成交价 - 已分摊的付款
								double ftje = getftje(spinfo);

								double maxfdje = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) - ftje);

								hjje += maxfdje;

								if (maxfdje > 0)
								{
									String[] row = {
													sg.barcode,
													sg.name,
													ManipulatePrecision.doubleToString(ftje),
													ManipulatePrecision.doubleToString(maxfdje),
													"",
													String.valueOf(i) };
									v.add(row);
								}
							}
						}

						// 计算损益
						if (ManipulatePrecision.doubleConvert(spay.je - spay.num1) > hjje)
						{
							spay.num1 = ManipulatePrecision.doubleConvert(spay.num1 + (ManipulatePrecision.doubleConvert(spay.je - spay.num1) - hjje));
						}
						// 开始分摊
						double syje = ManipulatePrecision.doubleConvert(spay.je - spay.num1); // 剩余金额

						if (GlobalInfo.sysPara.apportMode == 'C')
						{
							for (int i = v.size() - 1; i >= 0; i--)
							{
								String row[] = (String[]) v.elementAt(i);

								double je = 0;

								if (syje > Convert.toDouble(row[row.length - 3]))
								{
									je = Convert.toDouble(row[row.length - 3]);
								}
								else
								{
									je = syje;
								}
								row[row.length - 2] = String.valueOf(je);

								syje = ManipulatePrecision.doubleConvert(syje - je);

								if (syje <= 0) break;
							}
						}
						else
						{
							for (int i = 0; i < v.size(); i++)
							{
								String row[] = (String[]) v.elementAt(i);

								if (i == (v.size() - 1))
								{
									row[row.length - 2] = String.valueOf(syje);
									continue;
								}

								double je = ManipulatePrecision
																.doubleConvert((Convert.toDouble(row[row.length - 3]) / hjje * ManipulatePrecision
																																					.doubleConvert(spay.je
																																							- spay.num1)));
								row[row.length - 2] = String.valueOf(je);

								syje = ManipulatePrecision.doubleConvert(syje - je);
							}
						}

						// 记录商品分摊金额
						for (int i = 0; i < v.size(); i++)
						{
							String[] row = (String[]) v.elementAt(i);
							if (row[row.length - 2].length() <= 0) continue;

							// 按商品记录对应的付款分摊
							// 付款方式唯一序号,付款代码,付款名称(主要判断A/B券),分摊金额
							SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(Integer.parseInt(row[row.length - 1]));
							if (info.payft == null) info.payft = new Vector();
							String[] ft = new String[] { String.valueOf(spay.num5), spay.paycode, spay.payname, row[row.length - 2] };
							info.payft.add(ft);
						}
					}
				}
			}

			// 积分消费,按金额平摊
			if (spay.paycode.equals("0508"))
			{
				// 计算可收券的商品总金额
				double kfje = 0;
				for (int i = 0; i < saleGoods.size(); i++)
				{
					SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(i);
					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.get(i);
					if (info.char3 == 'N') continue;
					double je = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - saleGoodsDef.hjzk - getftje(info));
					kfje += je;
				}

				// 计算可收券的商品总金额
				double sy = spay.je;
				int index = -1;
				double maxje = 0;

				double syje = ManipulatePrecision.doubleConvert(spay.je - spay.num1); // 剩余金额

				if (GlobalInfo.sysPara.apportMode == 'C')
				{
					for (int i = saleGoods.size() - 1; i >= 0; i--)
					{
						System.out.println("i = " + i);
						SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(i);
						SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.get(i);
						if (info.char3 == 'N') continue;

						if (info.payft == null) info.payft = new Vector();
						double je = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - saleGoodsDef.hjzk - getftje(info));

						if (je > syje)
						{
							je = syje;
						}

						String[] ft = new String[] { String.valueOf(spay.num5), spay.paycode, spay.payname, String.valueOf(je) };

						info.payft.add(ft);

						syje = ManipulatePrecision.doubleConvert(syje - je);

						if (syje <= 0) break;
					}
				}
				else
				{
					for (int i = 0; i < saleGoods.size(); i++)
					{
						SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(i);
						SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.get(i);
						if (info.char3 == 'N') continue;

						if (info.payft == null) info.payft = new Vector();
						double je = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - saleGoodsDef.hjzk - getftje(info));

						if (je > maxje) index = i;

						double jf = ManipulatePrecision.doubleConvert(je / kfje * syje);
						sy = ManipulatePrecision.doubleConvert(sy - jf);

						if (sy < 0)
						{
							jf = ManipulatePrecision.doubleConvert(sy + jf);
							sy = 0;
						}
						String[] ft = new String[] { String.valueOf(spay.num5), spay.paycode, spay.payname, String.valueOf(jf) };
						info.payft.add(ft);
						if (sy == 0)
						{
							break;
						}
					}

					// 
					if (sy > 0)
					{
						SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);
						String[] ft = (String[]) info.payft.lastElement();
						ft[3] = ManipulatePrecision.doubleToString(Convert.toDouble(ft[3]) + sy);
					}
				}
			}
		}

		return true;
	}

	// 分摊付款方式
	public boolean paymentApportion(SalePayDef spay, Payment payobj)
	{
		if (!SellType.ISSALE(saletype)) { return false; }

		if (SellType.ISEARNEST(saletype)) { return false; }

		if (SellType.NOPOP(saletype)) return false;

		if (SellType.ISPREPARETAKE(saletype)) { return false; }

		if ((spay == null) || (payobj == null)) return false;

		// 检查是否在BS里计算分摊
		if (!payobj.isApportionInBS()) return true;
		if (payobj.isBaseApportion()) return baseApportion(spay, payobj);

		// 除券付款时要将券付款分摊到每个商品
		boolean apportion = false;
		String[] pay = GlobalInfo.sysPara.mjPaymentRule.split(",");
		for (int i = 0; i < pay.length; i++)
		{
			if (spay.paycode.equals(pay[i].trim()))
			{
				apportion = true;
				break;
			}
		}

		// 只要是除券的付款都要分摊,yinliang modify 
		//if (!apportion || !apportionPay) !(apportion&&apportionPay)
		if (!apportion) { return super.paymentApportion(spay, payobj); }

		// 得到该付款对应可分摊商品的列表,v的每行如下
		// 商品编码,商品名称,已付金额，限制金额,分摊金额,对应商品行号
		Vector v = null;
		if (GlobalInfo.sysPara.apportMode != 'A')
		{
			v = customApportion(spay, payobj);
		}
		else
		{
			// 手工分摊时如果除券的预付款时已足够,剩余付款不用进行分摊,yinliang modify
			if (calcPayBalance() <= 0) return false;

			v = getGoodsListByPayment(spay, payobj);
			if (v == null) return super.paymentApportion(spay, payobj);

			// 显示分摊窗口，输入分摊金额
			// 如果付款金额比限制金额的合计大，说明有溢余，溢余部分不进行分摊
			double ftje = ManipulatePrecision.sub(spay.je, spay.num1);
			double maxftje = 0;
			for (int i = 0; i < v.size(); i++)
			{
				String[] row = (String[]) v.elementAt(i);
				maxftje += Convert.toDouble(row[3]);
			}
			if (ftje > maxftje) ftje = maxftje;
			new ApportPaymentForm().open(v, spay.payname + Language.apply(" 共付款 ") + ManipulatePrecision.doubleToString(spay.je) + Language.apply(" 元"), ftje);
		}

		// 记录商品分摊金额
		if (v == null) return false;
		for (int i = 0; i < v.size(); i++)
		{
			String[] row = (String[]) v.elementAt(i);
			if (row[row.length - 2].length() <= 0) continue;

			// 按商品记录对应的付款分摊
			// 付款方式唯一序号,付款代码,付款名称(主要判断A/B券),分摊金额
			SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(Integer.parseInt(row[row.length - 1]));
			if (info.payft == null) info.payft = new Vector();
			String[] ft = new String[] { String.valueOf(spay.num5), spay.paycode, spay.payname, row[row.length - 2] };
			info.payft.add(ft);
		}

		return true;
	}

	//客户化分摊方式
	public Vector customApportion(SalePayDef spay, Payment payobj)
	{
		if (GlobalInfo.sysPara.apportMode == 'B')
		{
			Vector v = new Vector();

			if (!CreatePayment.getDefault().isPaymentFjk(spay.paycode))
			{
				double leftje = spay.je - spay.num1;

				// 计算可收券的商品总金额
				double kfje = 0;
				for (int i = 0; i < saleGoods.size(); i++)
				{
					SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(i);
					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.get(i);
					double je = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - saleGoodsDef.hjzk - getftje(info));
					
//					 以旧换新商品,合计要减
					if (saleGoodsDef.type == '8')
					{
						kfje -= je;
					}
					else
					{
						kfje += je;
					}
				}

				int i = 0;

				for (i = 0; i < (saleGoods.size() - 1); i++)
				{
					SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);
					SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(i);
					int sign = 1;
//					 以旧换新商品,合计要减
					if (sg.type == '8')
					{
						sign = -1;
					}
					
					double je = ManipulatePrecision.doubleConvert(((sg.hjje - sg.hjzk - getftje(info)) / kfje * (spay.je - spay.num1))*sign);
					leftje = ManipulatePrecision.doubleConvert(leftje - je);

					/*
					 new MessageBox(
					 "sg.hjje:" + ManipulatePrecision.doubleToString(sg.hjje) + "\n" +
					 "sg.hjzk:" + ManipulatePrecision.doubleToString(sg.hjzk) + "\n" +
					 "saleHead.hjzje:" + ManipulatePrecision.doubleToString(saleHead.hjzje) + "\n" +
					 "saleHead.hjzke:" + ManipulatePrecision.doubleToString(saleHead.hjzke) + "\n" +
					 "spay.je:" + ManipulatePrecision.doubleToString(spay.je) + "\n" +
					 "je:" + ManipulatePrecision.doubleToString(je) + "\n" +
					 "leftje:" + ManipulatePrecision.doubleToString(leftje) + "\n"
					 
					 );
					 */

					if (je > 0||(je < 0 && sg.type == '8'))
					{
						String[] row = { sg.barcode, sg.name, "0", "0", ManipulatePrecision.doubleToString(je), String.valueOf(i) };
						v.add(row);
					}
				}

				if (saleGoods.size() > 0)
				{
					SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);
					String[] row = { sg.barcode, sg.name, "0", "0", String.valueOf(leftje), String.valueOf(i) };
					v.add(row);
				}
			}
			else
			{
				PaymentFjk ob = null;

				try
				{
					ob = (PaymentFjk) payobj;
				}
				catch (Exception er)
				{
					er.printStackTrace();
				}

				double hjzje = 0;
				Vector row1 = new Vector();

				for (int i = 0; i < saleGoods.size(); i++)
				{
					// 付款方式的规则和商品的CRM促销规则一致,商品才可收
					if (!ob.checkFjkRuleCode(i))
					{
						continue;
					}

					SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);
					if (sg.memo.trim().length() <= 0) continue;
					double maxsqje = 0;
					if (spay.payname.equals(ob.getAccountNameA()))
					{
						maxsqje = Convert.toDouble(sg.memo.split(",")[0]);
					}
					else if (spay.payname.equals(ob.getAccountNameB()))
					{
						maxsqje = Convert.toDouble(sg.memo.split(",")[1]);
					}
					else
					{
						maxsqje = sg.hjje - getZZK(sg);
					}

					if (maxsqje <= 0) continue;

					hjzje += maxsqje;
					row1.add(String.valueOf(i));
				}

				int i = 0;
				double leftje = spay.je - spay.num1;

				for (i = 0; i < (row1.size() - 1); i++)
				{
					SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt(String.valueOf(row1.elementAt(i))));

					if (sg.memo.trim().length() <= 0) continue;
					double maxsqje = 0;
					if (spay.payname.equals(ob.getAccountNameA()))
					{
						maxsqje = Convert.toDouble(sg.memo.split(",")[0]);
					}
					else if (spay.payname.equals(ob.getAccountNameB()))
					{
						maxsqje = Convert.toDouble(sg.memo.split(",")[1]);
					}
					else
					{
						maxsqje = sg.hjje - getZZK(sg);
					}

					double je = ManipulatePrecision.doubleConvert((maxsqje) / (hjzje) * spay.je);
					leftje = ManipulatePrecision.doubleConvert(leftje - je);

					if (je > 0)
					{
						String[] row = { sg.barcode, sg.name, "0", "0", String.valueOf(je), String.valueOf(row1.elementAt(i)) };
						v.add(row);
					}
				}

				if (row1.size() > 0)
				{
					SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt(String.valueOf(row1.elementAt(i))));
					String[] row = { sg.barcode, sg.name, "0", "0", String.valueOf(leftje), String.valueOf(row1.elementAt(i)) };
					v.add(row);
				}
			}

			return v;
		}
		else if (GlobalInfo.sysPara.apportMode == 'C')
		{
			Vector v = new Vector();
			double ft = ManipulatePrecision.doubleConvert(spay.je - spay.num1);
			for (int i = saleGoods.size() - 1; i >= 0; i--)
			{
				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);
				SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(i);
				double ftje = getftje(spinfo);
				double maxfdje = sg.hjje - getZZK(sg) - ftje;

				if (maxfdje < ft)
				{
					String[] row = {
									sg.barcode,
									sg.name,
									ManipulatePrecision.doubleToString(ftje),
									ManipulatePrecision.doubleToString(maxfdje),
									ManipulatePrecision.doubleToString(maxfdje),
									String.valueOf(i) };
					v.add(row);
					ft = ManipulatePrecision.doubleConvert(ft - maxfdje);
				}
				else
				{
					String[] row = {
									sg.barcode,
									sg.name,
									ManipulatePrecision.doubleToString(ftje),
									ManipulatePrecision.doubleToString(maxfdje),
									ManipulatePrecision.doubleToString(ft),
									String.valueOf(i) };
					v.add(row);
					break;
				}
			}
			/*	    	
			 // 计算促销联
			 if (saleHead.djlb == SellType.RETAIL_SALE && GlobalInfo.sysPara.printpopbill == 'Y')
			 {
			 for (int i = 0;i < saleGoods.size(); i++)
			 {
			 SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);
			 SpareInfoDef spinfo = (SpareInfoDef)goodsSpare.elementAt(i);
			 
			 if ((sg.xxtax < 1))
			 {
			 continue;
			 }
			 
			 // 先分摊已在POS计算分摊的除外付款方式
			 double je = 0;
			 if (spinfo.payft == null) spinfo.payft = new Vector();
			 for (int j=0;j < spinfo.payft.size();j++)
			 {
			 String[] rows = (String[]) spinfo.payft.elementAt(j);
			 PayModeDef def = DataService.getDefault().searchPayMode(rows[1]);
			 
			 if ((def.type == '5') || def.code.equals("05") || (GlobalInfo.sysPara.mjPaymentRule+",").indexOf(def.code+",") >= 0)
			 {
			 je += Convert.toDouble(rows[3]);
			 }
			 }
			 // 再均摊哪些不在POS计算分摊的除外付款方式
			 for (int j=0;j<salePayment.size();j++)
			 {
			 SalePayDef sp = (SalePayDef)salePayment.elementAt(j);
			 PayModeDef def = DataService.getDefault().searchPayMode(sp.paycode);
			 
			 if ((def.type == '5') || def.code.equals("05") || (GlobalInfo.sysPara.mjPaymentRule+",").indexOf(def.code+",") >= 0)
			 {
			 int k = 0;
			 for (;k < spinfo.payft.size();k++)
			 {
			 String[] rows = (String[]) spinfo.payft.elementAt(k);
			 if (sp.num5 == Convert.toInt(rows[0])) break;
			 }
			 if (k >= spinfo.payft.size())
			 {
			 je += ManipulatePrecision.doubleConvert(sp.je * ((sg.hjje-sg.hjzk) / (saleHead.hjzje - saleHead.hjzke)));
			 }
			 }
			 }
			 sg.num3 = ManipulatePrecision.doubleConvert(sg.hjje-sg.hjzk - je);
			 if (sg.num3 < 0) sg.num3 = 0;
			 }]
			 }
			 */
			return v;
		}
		else
		{
			return null;
		}
	}

	// 查找可以收该付款方式的对应商品
	public Vector getGoodsListByPayment(SalePayDef spay, Payment payobj)
	{
		// 只有一个促销规则且所有商品都参与该促销,不进行付款分摊
		//if (rulePopSet == null) return null;
		//if (rulePopSet.size() <= 0) return null;
		//if (rulePopSet.size() == 1 && ((CalcRulePopDef)rulePopSet.get(0)).row_set.size() == saleGoods.size() && !apportionPay) return null;

		// 查找可以收该付款方式的对应商品列表
		// 商品编码,商品名称,限制金额,分摊金额,对应商品行号
		Vector v = new Vector();

		// 非有收券规则的付款方式，所有商品都可以用
		// 为有收券规则的付款方式，只有对应商品可收
		if (!CreatePayment.getDefault().isPaymentFjk(spay.paycode))
		{
			for (int i = 0; i < saleGoods.size(); i++)
			{
				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);
				SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(i);
				if (spinfo == null) continue;

				// 计算商品可收付款的金额 = 成交价 - 已分摊的付款
				double ftje = 0;
				if (spinfo.payft != null)
				{
					for (int j = 0; j < spinfo.payft.size(); j++)
					{
						String[] s = (String[]) spinfo.payft.elementAt(j);
						ftje += Convert.toDouble(s[3]);
					}
				}
				double maxfdje = sg.hjje - getZZK(sg) - ftje;
				if (maxfdje > 0)
				{
					String[] row = {
									sg.barcode,
									sg.name,
									ManipulatePrecision.doubleToString(ftje),
									ManipulatePrecision.doubleToString(maxfdje),
									"",
									String.valueOf(i) };
					v.add(row);
				}
			}
		}
		else
		{
			PaymentFjk ob = (PaymentFjk) payobj;
			for (int i = 0; i < saleGoods.size(); i++)
			{
				// 付款方式的规则和商品的CRM促销规则一致,商品才可收
				if (!ob.checkFjkRuleCode(i)) continue;

				// 商品明细的memo记录有该商品最大允许收券金额
				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);
				SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(i);
				if (sg.memo.trim().length() <= 0) continue;
				if (spinfo == null) continue;
				double maxsqje = 0;
				if (spay.payname.equals(ob.getAccountNameA()))
				{
					maxsqje = Convert.toDouble(sg.memo.split(",")[0]);
				}
				else if (spay.payname.equals(ob.getAccountNameB()))
				{
					maxsqje = Convert.toDouble(sg.memo.split(",")[1]);
				}
				else
				{
					maxsqje = sg.hjje - getZZK(sg);
				}

				// 计算该付款方式商品最多允许收的金额 = maxsqje - 已分摊的该付款金额
				if (spinfo.payft != null)
				{
					for (int j = 0; j < spinfo.payft.size(); j++)
					{
						String[] s = (String[]) spinfo.payft.elementAt(j);
						if (spay.paycode.equals(s[1]) && spay.payname.equals(s[2]))
						{
							maxsqje -= Convert.toDouble(s[3]);
						}
					}
				}

				// 计算商品可收付款的金额 = 成交价 - 已分摊的付款
				double ftje = 0;
				if (spinfo.payft != null)
				{
					for (int j = 0; j < spinfo.payft.size(); j++)
					{
						String[] s = (String[]) spinfo.payft.elementAt(j);
						ftje += Convert.toDouble(s[3]);
					}
				}
				double maxfdje = sg.hjje - getZZK(sg) - ftje;
				if (maxfdje > 0)
				{
					// 取可收金额和可收券金额中较小的
					double minje = Math.min(maxfdje, maxsqje);
					String[] row = {
									sg.barcode,
									sg.name,
									ManipulatePrecision.doubleToString(ftje),
									ManipulatePrecision.doubleToString(minje),
									"",
									String.valueOf(i) };
					v.add(row);
				}
			}
		}

		return v;
	}

	public void paymentApportionDelete(int index)
	{
		SalePayDef spay = (SalePayDef) salePayment.elementAt(index);
		Payment payobj = (Payment) payAssistant.elementAt(index);

		if ((spay == null) || (payobj == null)) { return; }

		// 得到该付款的唯一序号
		int seqno = (int) spay.num5;

		// 查找所有商品对应该付款的分摊，并删除
		for (int i = 0; i < goodsSpare.size(); i++)
		{
			SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(i);
			if (info == null || info.payft == null) continue;
			for (int j = 0; j < info.payft.size(); j++)
			{
				String[] s = (String[]) info.payft.elementAt(j);
				if (Convert.toInt(s[0]) == seqno)
				{
					info.payft.removeElementAt(j);
					j--;
				}
			}
		}
	}

	public boolean saleSummary()
	{
		if (!super.saleSummary()) return false;

		// 记录付款分摊到商品明细,发送到后台
		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);

			if (i >= goodsSpare.size()) continue;

			SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(i);
			if (spinfo == null) continue;

			// 付款行号:付款代码:分摊金额
			sg.str2 = "";
			if (spinfo.payft == null) continue;
			for (int j = 0; j < spinfo.payft.size(); j++)
			{
				String[] s = (String[]) spinfo.payft.elementAt(j);
				String rowno = "";
				for (int n = 0; n < salePayment.size(); n++)
				{
					SalePayDef sp = (SalePayDef) salePayment.elementAt(n);
					if ((int) sp.num5 == Convert.toInt(s[0]))
					{
						rowno = String.valueOf(sp.rowno);
						break;
					}
				}
				if (!"".equals(rowno.trim())) sg.str2 += "," + rowno + ":" + s[1] + ":" + s[3];
			}
			if (sg.str2.length() > 0) sg.str2 = sg.str2.substring(1);
		}

		return true;
	}

	//检查商品是否允许折扣
	public boolean checkGoodsRebate(GoodsDef goodsDef, SpareInfoDef info)
	{
		if (!super.checkGoodsRebate(goodsDef, info)) return false;

		// 为商品促销价，不允许打折
		if (info != null && info.char1 == 'Y') return false;

		return true;
	}

	public void calcAllRebate(int index)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// 指定小票退货时不重算优惠价和会员价
		if (isSpecifyBack(saleGoodsDef)) { return; }

		// 批发销售不计算
		if (SellType.ISBATCH(saletype)) { return; }

		if (SellType.ISEARNEST(saletype)) { return; }

		if (SellType.ISCHECKINPUT(saletype)) { return; }

		// 削价商品和赠品不计算,积分换购商品不计算
		if ((saleGoodsDef.flag == '3') || (saleGoodsDef.flag == '1') || this.isHHGoods(saleGoodsDef)) { return; }

		saleGoodsDef.hyzke = 0;
		saleGoodsDef.yhzke = 0;
		saleGoodsDef.yhzkfd = 0;
		saleGoodsDef.zszke = 0;

		// 促销优惠
		// 换消状态下不计算定期促销
		if (goodsDef.poptype != '0' && hhflag != 'Y')
		{
			//定价且是单品优惠
			if ((saleGoodsDef.lsj > 0) && ((goodsDef.poptype == '1') || (goodsDef.poptype == '7')))
			{
				// 促销折扣
				if ((saleGoodsDef.lsj > goodsDef.poplsj) && (goodsDef.poplsj > 0))
				{
					saleGoodsDef.yhzke = (saleGoodsDef.lsj - goodsDef.poplsj) * saleGoodsDef.sl;
					saleGoodsDef.yhzkfd = goodsDef.poplsjzkfd;
				}
			}
			else
			{
				//促销折扣
				if ((1 > goodsDef.poplsjzkl) && (goodsDef.poplsjzkl > 0))
				{
					saleGoodsDef.yhzke = saleGoodsDef.hjje * (1 - goodsDef.poplsjzkl);
					saleGoodsDef.yhzkfd = goodsDef.poplsjzkfd;
				}
			}

			// 
			saleGoodsDef.yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.yhzke, 2, 1);

			// 按价格精度计算折扣
			saleGoodsDef.yhzke = getConvertRebate(index, saleGoodsDef.yhzke);
		}

		// vipzk1表示实时输商品的时候立即计算VIP折扣
		getVIPZK(index, vipzk1);
	}

	/**
	 * @author yinl
	 * @modify 2009-10-30 下午04:23:17
	 * @descri 方法说明
	 */
	public void getVIPZK(int index, int type)
	{
		boolean zszflag = true;
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		GoodsPopDef popDef = (GoodsPopDef) crmPop.elementAt(index);
		SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);

		// 指定小票退货时不重算优惠价和会员价
		if (isSpecifyBack(saleGoodsDef)) { return; }

		// 积分换购商品不计算会员打折
		if (info.char2 == 'Y') { return; }

		if (curCustomer == null || (curCustomer != null && curCustomer.iszk != 'Y')) return;

		// 批发销售不计算
		if (SellType.ISBATCH(saletype)) { return; }

		if (SellType.ISEARNEST(saletype)) { return; }

		if (SellType.ISCOUPON(saletype)) { return; }
		
		if (SellType.ISJFSALE(saletype)) { return; }

		// 削价商品和赠品不计算
		if ((saleGoodsDef.flag == '3') || (saleGoodsDef.flag == '1')) { return; }

		// 不为VIP折扣的商品不重新计算会员折扣额
		if (goodsDef.isvipzk == 'N') return;

		// 折扣门槛
		if (saleGoodsDef.hjje == 0
				|| ManipulatePrecision
										.doubleConvert((saleGoodsDef.hjje - saleGoodsDef.lszke - saleGoodsDef.lszre - saleGoodsDef.lszzk - saleGoodsDef.lszzr)
												/ saleGoodsDef.hjje) < GlobalInfo.sysPara.vipzklimit) return;

		// 商品会员促销价
		if (popDef.jsrq != null && popDef.jsrq.length() > 0 && popDef.jsrq.split(",").length >= 5 && type == vipzk1)
		{
			// 商品会员价促销单号,商品促销价，限量数量 ，已享受数量，积分方式（0:正常积分 ,1:不积分 2:特价积分） 
			String[] arg = popDef.jsrq.split(",");

			double price = Convert.toDouble(arg[1]);
			double max = Convert.toDouble(arg[2]);
			double used = Convert.toDouble(arg[3]);

			// 限量
			boolean isprice = false;
			if (max > 0)
			{
				double q = 0;
				for (int i = 0; i < saleGoods.size(); i++)
				{
					SaleGoodsDef saleGoodsDef1 = (SaleGoodsDef) saleGoods.elementAt(i);
					SpareInfoDef info1 = (SpareInfoDef) goodsSpare.elementAt(i);

					if (i == index) continue;

					if (saleGoodsDef1.code.equals(saleGoodsDef.code) && info1.char1 == 'Y')
					{
						q += saleGoodsDef1.sl;
					}
				}

				if (ManipulatePrecision.doubleConvert(max - used - q) > 0)
				{
					if (ManipulatePrecision.doubleConvert(saleGoodsDef.sl) > ManipulatePrecision.doubleConvert(max - used - q))
					{
//						new MessageBox("此商品存在促销价，但是商品数量[" + saleGoodsDef.sl + "]超出数量限额【" + ManipulatePrecision.doubleConvert(max - used - q)
//								+ "】\n 强制将商品数量修改为【" + ManipulatePrecision.doubleConvert(max - used - q) + "】参与促销价");
						new MessageBox(Language.apply("此商品存在促销价，但是商品数量[{0}]超出数量限额【{1}】\n 强制将商品数量修改为【{2}】参与促销价" ,new Object[]{saleGoodsDef.sl+"" ,ManipulatePrecision.doubleConvert(max - used - q)+"" ,ManipulatePrecision.doubleConvert(max - used - q)+""}));
						saleGoodsDef.sl = ManipulatePrecision.doubleConvert(max - used - q);
						saleGoodsDef.hjje = ManipulatePrecision.doubleConvert(saleGoodsDef.sl * saleGoodsDef.jg, 2, 1);
						calcGoodsYsje(index);
					}
					isprice = true;
				}
			}
			else
			{
				isprice = true;
			}

			if (isprice == true)
			{
				saleGoodsDef.hyzke = 0;
				saleGoodsDef.yhzke = 0;
				saleGoodsDef.lszke = 0;
				saleGoodsDef.lszre = 0;
				saleGoodsDef.lszzk = 0;
				saleGoodsDef.lszzr = 0;

				if (info.str1.length() > 1 && info.str1.charAt(0) == '9')
				{
					StringBuffer buff = new StringBuffer(info.str1);
					for (int z = 1; z < buff.length(); z++)
					{
						buff.setCharAt(z, '0');
					}
					info.str1 = buff.toString();
				}
				else
				{
					info.str1 = "0000";
				}
				saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert((saleGoodsDef.jg - price) * saleGoodsDef.sl);
				saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
				saleGoodsDef.str1 = popDef.jsrq;
				info.char1 = 'Y';
			}
		}

		// 已计算了商品会员促销价，不再继续VIP折扣
		if (info.char1 == 'Y') return;

		if (goodsDef.isvipzk == 'Y')
		{
			// 开始计算VIP折扣
			saleGoodsDef.hyzke = 0;
			saleGoodsDef.hyzkfd = goodsDef.hyjzkfd;
		}

		// 判断促销单是否允许折上折
		if (goodsDef.pophyjzkl % 10 >= 1) zszflag = zszflag && true;
		else zszflag = zszflag && false;

		//是否进行VIP打折,通过CRM促销控制
		boolean vipzk = false;

		//无CRM促销，以分期促销折上折标志为准
		if (popDef.yhspace == 0)
		{
			vipzk = true;
		}
		else
		//存在CRM促销
		{
			//不享用VIP折扣,不进行VIP打折
			if (popDef.pophyjzkl == 0)
			{
				vipzk = false;
			}
			else
			//享用VIP折扣，进行VIP折上折
			{
				vipzk = true;
				zszflag = zszflag && true;
			}
		}

		if (checkMemberSale() && curCustomer != null && goodsDef.isvipzk == 'H' && type == vipzk1)
		{
			double je = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - getZZK(saleGoodsDef)));
			double hyj = 0;
			if (goodsDef.pophyj != 0)
			{
				hyj = goodsDef.pophyj;
			}

			if (goodsDef.hyj != 0)
			{
				if (hyj == 0) hyj = goodsDef.hyj;
				else hyj = Math.min(hyj, goodsDef.hyj);
			}

			if (hyj != 0 && je > ManipulatePrecision.doubleConvert(hyj * saleGoodsDef.sl))
			{
				saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert(je - ManipulatePrecision.doubleConvert(hyj * saleGoodsDef.sl));
				saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
			}

		}
		//存在会员卡， 商品允许VIP折扣， CRM促销单允许享用VIP折扣
		else if (checkMemberSale() && curCustomer != null && goodsDef.isvipzk == 'Y' && vipzk && curCustomer.iszk == 'Y')
		{
			// 获取VIP折扣率定义
			calcVIPZK(index);

			// 折上折标志
			zszflag = zszflag && (goodsDef.num4 == 1);

			// 不计算会员卡折扣
			if (goodsDef.hyj == 1) return;

			// vipzk1 = 输入商品时计算商品VIP折扣,原VIP折上折模式
			if (type == vipzk1 && (GlobalInfo.sysPara.vipPromotionCrm == null || GlobalInfo.sysPara.vipPromotionCrm.equals("1")))
			{
				//有折扣,进行折上折
				if (getZZK(saleGoodsDef) >= 0.01 && goodsDef.hyj < 1.00)
				{
					// 需要折上折
					if (zszflag)
					{
						saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
					}
					else
					{
						// 商品不折上折时，取商品的hyj和综合折扣较低者
						if (ManipulatePrecision.doubleCompare(saleGoodsDef.hjje - getZZK(saleGoodsDef), goodsDef.hyj * saleGoodsDef.hjje, 2) > 0)
						{
							double zke = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * saleGoodsDef.hjje, 2, 1);
							if (zke > getZZK(saleGoodsDef))
							{
								saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert(zke - getZZK(saleGoodsDef), 2, 1);
							}
						}
					}
				}
				else
				{
					//无折扣,按商品缺省会员折扣打折
					saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * saleGoodsDef.hjje, 2, 1);
				}
			}
			else // vipzk2 = 按下付款键时计算商品VIP折扣,起点折扣计算模式 
			if (type == vipzk2 && GlobalInfo.sysPara.vipPromotionCrm != null && GlobalInfo.sysPara.vipPromotionCrm.equals("2"))
			{
				// VIP折扣要除券付款
				double fte = 0;
				if (GlobalInfo.sysPara.vipPayExcp == 'Y') fte = getGoodsftje(index);

				double vipzsz = 0;

				// 直接在以以后折扣的基础上打商品定义的VIP会员折扣率
				if (GlobalInfo.sysPara.vipCalcType.equals("2"))
				{
					vipzsz = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * (saleGoodsDef.hjje - getZZK(saleGoodsDef) - fte), 2, 1);
				}
				else if (GlobalInfo.sysPara.vipCalcType.equals("1"))
				{
					// 当前折扣如果高于门槛则还可以进行VIP折上折,否则VIP不能折上折
					if (getZZK(saleGoodsDef) > 0
							&& zszflag
							&& ManipulatePrecision.doubleCompare(saleGoodsDef.hjje - getZZK(saleGoodsDef), saleGoodsDef.hjje * curCustomer.value3, 2) >= 0)
					{
						vipzsz = ManipulatePrecision.doubleConvert((1 - curCustomer.zkl) * (saleGoodsDef.hjje - getZZK(saleGoodsDef) - fte), 2, 1);
					}

					// 如果VIP折上折以后的成交价 高于 该商品定义的VIP会员折扣率，则商品以商品定义的折扣执行VIP折
					double spvipcjj = ManipulatePrecision.doubleConvert(goodsDef.hyj * (saleGoodsDef.hjje - fte), 2, 1);
					if (ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef) - fte - vipzsz, 2, 1) > spvipcjj)
					{
						vipzsz = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef) - fte - spvipcjj);
					}
				}

				saleGoodsDef.hyzke = vipzsz;
			}

			// 按价格精度计算折扣
			saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
		}

		getZZK(saleGoodsDef);
	}

	public void calcVIPZK(int index)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// 未刷卡
		if (!checkMemberSale() || curCustomer == null) return;

		// 非零售开票
		if (!saletype.equals(SellType.RETAIL_SALE) && !saletype.equals(SellType.PREPARE_SALE))
		{
			goodsDef.hyj = 1;
			return;
		}

		// 查询商品VIP折上折定义
		GoodsPopDef popDef = new GoodsPopDef();
		if (((Bcrm_DataService) DataService.getDefault()).findHYZK(popDef, saleGoodsDef.code, curCustomer.type, saleGoodsDef.gz, saleGoodsDef.catid,
																	saleGoodsDef.ppcode, goodsDef.specinfo))
		{
			// 有柜组和商品的VIP折扣定义
			goodsDef.hyj = popDef.pophyj;
			goodsDef.num4 = popDef.num2;
		}
		else
		{
			// 无柜组和商品的VIP折扣定义,以卡类别的折扣率为VIP打折标准
			goodsDef.hyj = curCustomer.zkl;
			goodsDef.num4 = 1;
		}
	}

	public boolean doSuperMarketCrmPop()
	{
		if (!SellType.ISSALE(saletype)) { return false; }

		if (SellType.NOPOP(saletype)) { return false; }

		if (SellType.ISEARNEST(saletype)) { return false; }

		if (SellType.ISPREPARETAKE(saletype)) { return false; }

		// 初始化超市促销标志
		for (int i = 0; i < saleGoods.size(); i++)
			((SaleGoodsDef) saleGoods.get(i)).isSMPopCalced = 'Y';

		// 排序
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

					if (code.equals(code1)) continue;

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

						gpd = (GoodsPopDef) crmPop.get(j);
						crmPop.setElementAt(crmPop.get(j + 1), j);
						crmPop.setElementAt(gpd, j + 1);
					}
				}
			}
		}

		// 查找规则
		SuperMarketPopRuleDef ruleDef = null;
		Vector notRuleDjbh = new Vector();
		int calcCount = saleGoods.size();
		int k, j, l, m, n;
		double zje, je, t_zje;
		double or_yhsl = 0;//结果为OR关系的时候,存放第一个结果的数量
		long bs, minbs, t_minbs;

		String cardNo = "";
		if (curCustomer != null)
		{
			cardNo = curCustomer.code;
		}

		SaleGoodsDef saleGoodsDef = null;
		GoodsDef goodsDef = null;
		//SpareInfoDef spareInfoDef = null;
		for (int i = 0; i < calcCount + 1; i++)
		{
			// 查找单品优惠单号
			if (i != calcCount)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.get(i);
				goodsDef = (GoodsDef) goodsAssistant.get(i);
				//	spareInfoDef = (SpareInfoDef) goodsSpare.get(i);
				// 只有普通商品才能参与
				if (saleGoodsDef.flag != '2' && saleGoodsDef.flag != '4') continue;
				// 判断是否曾参与规则促销
				if (saleGoodsDef.isSMPopCalced == 'N') continue;
				// 标准百货crm促销，跳过
				//				if (goodsDef.str4.equals("Y")) continue;

				//已经查过的商品无需重复查规则促销
				for (k = 0; k < i; k++)
				{
					if (goodsDef.code.equals(((GoodsDef) goodsAssistant.get(k)).code) && goodsDef.gz.equals(((GoodsDef) goodsAssistant.get(k)).gz)
							&& goodsDef.uid.equals(((GoodsDef) goodsAssistant.get(k)).uid)) break;
				}
				if (k < i) continue;

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
			if (!((Bcrm_DataService) DataService.getDefault()).findSuperMarketPopBillNo(ruleDef, goodsDef.code, goodsDef.gz, goodsDef.catid,
																						goodsDef.ppcode, goodsDef.uid, saleHead.rqsj, saleHead.rqsj,
																						cardNo))
			{
				continue;
			}

			System.out.println("商品：" + goodsDef.code + " 对应规则单号：" + ruleDef.djbh);

			//检查该单据是否已经运算过，如果已经运行过则无需重复运算
			for (k = 0; k < notRuleDjbh.size(); k++)
			{
				if (((String) notRuleDjbh.get(k)).equals(ruleDef.djbh)) break;
			}
			if (k < notRuleDjbh.size()) continue;

			// 查找超市促销规则明细
			ruleReqList = new Vector();
			rulePopList = new Vector();
			if (!((Bcrm_DataService) DataService.getDefault()).findSuperMarketPopRule(ruleReqList, rulePopList, ruleDef) || ruleReqList.size() == 0
					|| rulePopList.size() == 0)
			{
				continue;
			}

			// 初始化条件参数
			for (k = 0; k < saleGoods.size(); k++)
			{
				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
				// 标志为A表示在上一轮规则中被条件排除的商品，因此可以参与本轮规则促销
				sg.isPopExc = ' ';
				if (sg.isSMPopCalced == 'A') ((SaleGoodsDef) saleGoods.get(k)).isSMPopCalced = 'Y';
			}

			for (j = 0; j < ruleReqList.size(); j++)
			{
				for (k = 0; k < saleGoods.size(); k++)
				{
					//商品是否条件匹配
					if (isMatchCommod((SuperMarketPopRuleDef) ruleReqList.get(j), k))
					{
						((SaleGoodsDef) saleGoods.get(k)).isPopExc = 'Y';//表示条件满足
					}
				}
			}

			//先将规则条件中要排除的商品排除掉
			for (j = 0; j < ruleReqList.size(); j++)
			{
				//presentsl为1表示该条件是排除的。
				if (((SuperMarketPopRuleDef) ruleReqList.get(j)).presentsl == 1)
				{
					for (k = 0; k < saleGoods.size(); k++)
					{
						SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
						//商品是否条件匹配
						if (sg.isPopExc == 'Y' && isMatchCommod((SuperMarketPopRuleDef) ruleReqList.get(j), k))
						{
							//将排除商品的标志置为N,表示不参与规则促销
							if (((SuperMarketPopRuleDef) ruleReqList.get(j)).istjn.charAt(0) == '0') //排除条件
							sg.isPopExc = ' ';
							else if (((SuperMarketPopRuleDef) ruleReqList.get(j)).istjn.charAt(0) == '1') //排除结果
							sg.isPopExc = 'N';
							else
							//条件和结果都排除
							{
								sg.isPopExc = ' ';
								sg.isSMPopCalced = 'A';
							}
						}
					}
					//如果是排除条件。那么无论结果是排除条件还是排除结果都不纳入条件计算 
					//例如,单据是一行类别的条件和一行排除结果的条件，如果此处不删除排除结果的那一行数据，
					//并且输入的商品中没有买这个排除结果的商品，会导致后面计算AND条件的时候算出倍数为0的情况。
					//如果是排除条件，则需将条件从条件列表中删除，这样是为了后面算多级
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

				//得到当前级次
				getCurRuleJc(n + 1);

				//匹配规则条件中属于必须满足的条件
				for (l = 0, j = 0; j < ruleReqList.size(); j++)
				{
					SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef) ruleReqList.get(j);
					//条件为AND
					if (ruleReq.presentjs == 1)
					{
						l++;
						je = 0;
						for (k = 0; k < saleGoods.size(); k++)
						{
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
							//商品是否条件匹配
							if ((sg.isPopExc == 'Y' || sg.isPopExc == 'N') && isMatchCommod((SuperMarketPopRuleDef) ruleReqList.get(j), k))
							{
								//yhhyj = 0，表示yhlsj中记录的是数量
								//yhhyj = 1，表示yhlsj中记录的是金额
								if (ruleReq.yhhyj == 0) je += sg.sl;
								else je += sg.hjje - sg.yhzke - sg.hyzke - sg.plzke;
								//								此处如果该变cominfo[k].infostr1[5] = 'A',在计算第2级别的时候,程序不能进入上面的IF条件进行统计，导致无法计算一级以上的级别
								//								避免后面的or判断时又找到该条件
							}
						}

						bs = 0;
						if (ManipulatePrecision.doubleCompare(je, ruleReq.yhlsj, 2) >= 0
								&& ManipulatePrecision.doubleCompare(ruleReq.yhlsj, 0, 2) >= 0)
						{
							bs = new Double(je / ruleReq.yhlsj).longValue();
						}
						if (l == 1) t_minbs = bs;
						else t_minbs = t_minbs > bs ? bs : t_minbs;

						t_zje += je;
					}
				}
				//有必须全满足的条件，并且未全满足时，则认为条件不满足
				if (l > 0 && t_minbs <= 0)
				{
					//还原上一级
					if (n > 0) getCurRuleJc(n);
					break;
				}
				//匹配规则条件中属于非必须满足的条件
				for (je = 0, m = -1, j = 0; j < ruleReqList.size(); j++)
				{
					SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef) ruleReqList.get(j);
					//条件为OR
					if (ruleReq.presentjs == 0)
					{
						m = j;
						for (k = 0; k < saleGoods.size(); k++)
						{
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
							//商品是否条件匹配
							if ((sg.isPopExc == 'Y' || sg.isPopExc == 'N') && isMatchCommod(ruleReq, k))
							{
								//yhhyj = 0，表示yhlsj中记录的是数量
								//yhhyj = 1，表示yhlsj中记录的是金额
								if (ManipulatePrecision.doubleCompare(ruleReq.yhhyj, 0, 2) == 0) je += sg.sl;
								else je += sg.hjje - sg.yhzke - sg.hyzke - sg.plzke;
							}
						}
					}
				}
				t_zje += je;

				//计算or条件的倍数

				if (m >= 0)
				{
					SuperMarketPopRuleDef ruleReqM = (SuperMarketPopRuleDef) ruleReqList.get(m);
					if (ManipulatePrecision.doubleCompare(je, ruleReqM.yhlsj, 2) >= 0 && ManipulatePrecision.doubleCompare(ruleReqM.yhlsj, 0, 2) > 0)
					;
					{
						bs = new Double(je / ruleReqM.yhlsj).longValue();
						if (l > 0) t_minbs = t_minbs > bs ? bs : t_minbs;
						else t_minbs = bs;
					}
				}
				if (t_minbs > 0)
				{
					minbs = t_minbs;
					zje = t_zje;
				}
				else
				{
					//还原上一级
					if (n > 0) getCurRuleJc(n);
					break;
				}
			}

			//有必须全满足的条件，并且未全满足时，则认为条件不满足
			if (minbs <= 0)
			{
				//记录下不匹配的单据号，以便后面的商品再找到该单据时不用再次进行匹配运算
				notRuleDjbh.add(ruleDef.djbh);
				continue;
			}
			else
			{
			}
			//ppistr6中的第1个字符为1时，表示1倍封顶
			if (((SuperMarketPopRuleDef) ruleReqList.get(0)).ppistr5.charAt(0) == '1') minbs = 1;

			//计算促销的结果
			for (j = 0; j < rulePopList.size(); j++)
			{
				SuperMarketPopRuleDef rulePop = (SuperMarketPopRuleDef) rulePopList.get(j);
				//商品优惠 商品优惠对应 一级商品 也对应多级的商品
				if (rulePop.yhdjlb == 'A' || rulePop.yhdjlb == 'E')
				{
					double yhsl = minbs * rulePop.yhhyj;//优惠数量

					//结果为OR关系的时候 按照第一个结果的优惠数量来优惠剩余商品
					if (rulePop.presentjs == 0)
					{
						if (j == 0) or_yhsl = yhsl;
						else yhsl = or_yhsl;
					}

					for (k = 0; k < saleGoods.size() && ManipulatePrecision.doubleCompare(yhsl, 0, 4) > 0; k++)
					{
						SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
						//商品是否结果匹配 排除结果
						if (isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
						{
							//商品拆分
							if (ManipulatePrecision.doubleCompare(sg.sl, yhsl, 4) <= 0)
							{
								yhsl -= sg.sl;
								or_yhsl -= sg.sl;
							}
							else
							{
								//拆分商品行
								splitSalecommod(k, yhsl);
								yhsl = 0;
								or_yhsl = 0;
							}
							double misszke = 0;
							//整单满减
							if (ruleDef.type == '8')
							{
								misszke = sg.yhzke + sg.hyzke + sg.plzke + sg.rulezke;//记录可能被清零的折扣额
							}
							else
							{
								misszke = sg.yhzke + sg.hyzke + sg.plzke;//记录可能被清零的折扣额
							}
							//取价方式判断
							if (rulePop.presentjg == 0)//取价方式 取价格
							{
								//如果是折上折，那么折后金额 = 一般优惠后的金额 * 现在的规则定价 /商品本身的价格
								if (rulePop.iszsz.charAt(0) == '1')
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0)
												- ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);
										sg.mjzke = calcComZkxe(k, sg.mjzke);

										sg.mjdjbh = rulePop.djbh;
										sg.mjzkfd = rulePop.zkfd;
										//统计当前规则促销的折扣金额
										superMarketRuleyhje += sg.mjzke;
									}
									else
									{
										sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0)
												- ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);
										sg.rulezke = calcComZkxe(k, sg.rulezke);

										sg.ruledjbh = rulePop.djbh;
										sg.rulezkfd = rulePop.zkfd;
										//统计当前规则促销的折扣金额
										superMarketRuleyhje += sg.rulezke;
									}
								}
								else
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + sg.yhzke + sg.hyzke + sg.plzke
												+ sg.rulezke, 2, 0)
												- ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);
										if (sg.mjzke > misszke)//如果规则优惠大于一般优惠，一般优惠清零
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
											//统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.mjzke - misszke;
										}
										else
										{
											sg.mjzke = 0;
										}
									}
									else
									{
										sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + sg.yhzke + sg.plzke + sg.hyzke, 2, 0)
												- ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);

										if (sg.rulezke > misszke)//如果规则优惠大于一般优惠，一般优惠清零
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
											//统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.rulezke - misszke;
										}
										else
										{
											sg.rulezke = 0;
										}
									}
								}
							}
							else if (rulePop.presentjg == 1)//取折扣率
							{
								if (rulePop.iszsz.charAt(0) == '1')
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = 0;
										sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0)
												- ManipulatePrecision.doubleConvert((sg.hjje - getZZK(sg)) * rulePop.yhlsj, 2, 0);
										sg.mjzke = calcComZkxe(k, sg.mjzke);

										sg.mjdjbh = rulePop.djbh;
										sg.mjzkfd = rulePop.zkfd;
										//统计当前规则促销的折扣金额
										superMarketRuleyhje += sg.mjzke;
									}
									else
									{
										sg.rulezke = 0;
										sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0)
												- ManipulatePrecision.doubleConvert((sg.hjje - getZZK(sg)) * rulePop.yhlsj, 2, 0);
										sg.rulezke = calcComZkxe(k, sg.rulezke);
										sg.ruledjbh = rulePop.djbh;
										sg.rulezkfd = rulePop.zkfd;
										//统计当前规则促销的折扣金额
										superMarketRuleyhje += sg.rulezke;
									}
								}
								else
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = 0;
										sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + misszke, 2, 0)
												- ManipulatePrecision.doubleConvert((sg.hjje - getZZK(sg) + misszke) * rulePop.yhlsj, 2, 0);

										if (sg.mjzke > misszke)//如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的差额
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd = 0;
											sg.yhzkfd = 0;//清除普通优惠和会员优惠
											sg.rulezke = 0;
											sg.rulezkfd = 0;
											sg.yhdjbh = "";
											sg.ruledjbh = "";
											sg.mjzke = calcComZkxe(k, sg.mjzke);
											sg.mjdjbh = rulePop.djbh;
											sg.mjzkfd = rulePop.zkfd;
											//统计当前规则促销的折扣金额
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
										sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + misszke, 2, 0)
												- ManipulatePrecision.doubleConvert((sg.hjje - getZZK(sg) + misszke) * rulePop.yhlsj, 2, 0);

										if (sg.rulezke > misszke)//如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的额
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd = 0;
											sg.yhzkfd = 0;//清除普通优惠和会员优惠
											sg.yhdjbh = "";
											sg.rulezke = calcComZkxe(k, sg.rulezke);
											sg.ruledjbh = rulePop.djbh;
											sg.rulezkfd = rulePop.zkfd;
											//统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.rulezke - misszke;
										}
										else
										{
											sg.rulezke = 0;
										}
									}
								}
							}
							else if (rulePop.presentjg == 2)//取折扣额
							{
								if (rulePop.iszsz.charAt(0) == '1')
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = rulePop.yhlsj;
										sg.mjzke = calcComZkxe(k, sg.mjzke);

										sg.mjdjbh = rulePop.djbh;
										sg.mjzkfd = rulePop.zkfd;
										superMarketRuleyhje += sg.mjzke;//统计规则促销的折扣金额
									}
									else
									{
										sg.rulezke = rulePop.yhlsj;
										sg.rulezke = calcComZkxe(k, sg.rulezke);

										sg.ruledjbh = rulePop.djbh;
										sg.rulezkfd = rulePop.zkfd;
										superMarketRuleyhje += sg.rulezke;//统计规则促销的折扣金额
									}
								}
								else
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = rulePop.yhlsj;

										if (sg.mjzke > misszke)//如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的差额
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd = 0;
											sg.yhzkfd = 0;//清除普通优惠和会员优惠
											sg.rulezke = 0;
											sg.rulezkfd = 0;

											sg.ruledjbh = "";
											sg.yhdjbh = "";
											sg.mjzke = calcComZkxe(k, sg.mjzke);
											sg.mjdjbh = rulePop.djbh;
											sg.mjzkfd = rulePop.zkfd;
											superMarketRuleyhje += sg.mjzke - misszke;//统计规则促销的折扣金额	
										}
										else
										{
											sg.mjzke = 0;
										}
									}
									else
									{
										sg.rulezke = rulePop.yhlsj;

										if (sg.rulezke > misszke)//如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的差额
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd = 0;
											sg.yhzkfd = 0;//清除普通优惠和会员优惠
											sg.yhdjbh = "";
											sg.rulezke = calcComZkxe(k, sg.rulezke);
											sg.ruledjbh = rulePop.djbh;
											sg.rulezkfd = rulePop.zkfd;
											superMarketRuleyhje += sg.rulezke - misszke;//统计规则促销的折扣金额	
										}
										else
										{
											sg.rulezke = 0;
										}
									}
								}
							}
							else
							//用于其它用途
							{
							}
							sg.isPopExc = 'Y';
						}
					}
				}
				//赠品
				if (rulePop.yhdjlb == 'B' || rulePop.yhdjlb == 'F')
				{
					//'4'表示买赠，该赠品是小票列表中的正常商品，要将其改成正赠品
					if (rulePop.ppistr6.charAt(0) == '4')
					{
						//赠品数量
						quantity = minbs * rulePop.yhlsj;

						for (k = 0; k < saleGoods.size() && ManipulatePrecision.doubleCompare(quantity, 0, 4) > 0; k++)
						{
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
							//商品是否结果匹配  排除结果
							if (isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
							{
								//商品拆分
								if (ManipulatePrecision.doubleCompare(sg.sl, quantity, 4) <= 0)
								{
									quantity -= sg.sl;
								}
								else
								{
									//拆分商品行
									splitSalecommod(k, quantity);
									quantity = 0;
								}
								//将该商品改为赠品
								sg.flag = '1';
								sg.batch = rulePop.ppistr6;

								sg.xxtax = ManipulatePrecision.doubleConvert(minbs * rulePop.yhhyj, 2, 0);
								sg.rulezke += sg.hjje - getZZK(sg) - ManipulatePrecision.doubleConvert(minbs * rulePop.yhhyj, 2, 0);

								sg.ruledjbh = rulePop.djbh; //记录优惠单据编号
								sg.rulezkfd = rulePop.zkfd;

								//该商品的应收金额都记为优惠金额
								superMarketRuleyhje += sg.zszke;
							}
						}
					}
				}
				//任意几个定应收金额 只分多级
				if (rulePop.yhdjlb == 'X')
				{
					if (ManipulatePrecision.doubleCompare(zje, ((SuperMarketPopRuleDef) ruleReqList.get(0)).yhlsj, 2) >= 0
							&& ManipulatePrecision.doubleCompare(((SuperMarketPopRuleDef) ruleReqList.get(0)).yhlsj, 0, 2) > 0)
					{
						double yhje = ManipulatePrecision.doubleConvert(minbs * rulePop.yhlsj, 2, 0);
						long yhsl = new Double(minbs * ((SuperMarketPopRuleDef) ruleReqList.get(0)).yhlsj).longValue();
						double t_zyhje = 0;

						t_zje = 0;
						t_zyhje = 0;

						//yhje应小于小票的应收金额，不然话，小票金额有成负数的可能。
						if (ManipulatePrecision.doubleCompare(yhje, saleHead.ysje, 2) < 0)
						{
							int t_maxjerow = -1;
							long t_yhsl = yhsl;
							//计算本次参与优惠商品的总金额
							for (l = 0; l < ruleReqList.size(); l++)
							{
								SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef) ruleReqList.get(l);
								for (k = 0; k < saleGoods.size(); k++)
								{
									SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
									if (t_yhsl > 0 && isMatchCommod(ruleReq, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
									{
										//商品拆分
										if (ManipulatePrecision.doubleCompare(sg.sl, t_yhsl, 4) <= 0)
										{
											t_yhsl -= (long) sg.sl;
										}
										else
										{
											//拆分商品行
											splitSalecommod(k, t_yhsl);
											t_yhsl = 0;
										}
										//如果是折上折
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

							//计算出优惠金额
							yhje = t_zje - yhje;

							double t_je = 0;
							//将优惠金额按金额占比分摊到本次参与的商品上面
							for (l = 0; l < ruleReqList.size(); l++)
							{
								SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef) ruleReqList.get(l);
								for (k = 0; k < saleGoods.size(); k++)
								{
									SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
									if (yhsl > 0 && isMatchCommod(ruleReq, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
									{
										//商品拆分
										if (ManipulatePrecision.doubleCompare(sg.sl, yhsl, 4) <= 0)
										{
											yhsl -= sg.sl;
										}
										else
										{
											//拆分商品行
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
										//根据折上折来判断，如果非折上折，取低价优先
										if (rulePop.iszsz.charAt(0) == '1')
										{
											if (ruleDef.type == '8')
											{
												sg.mjzke = 0;
												sg.mjzke = (sg.hjje - getZZK(sg)) / t_zje * yhje;
												calcComZkxe(k, sg.mjzke);
												//记录折扣分担
												sg.mjzkfd = rulePop.zkfd;
												//记录优惠单据编号
												sg.mjdjbh = rulePop.djbh;
												//统计当前规则促销的折扣金额
												superMarketRuleyhje += sg.mjzke;

											}
											else
											{
												sg.rulezke = 0;
												sg.rulezke = (sg.hjje - getZZK(sg)) / t_zje * yhje;
												calcComZkxe(k, sg.rulezke);
												//记录折扣分担
												sg.rulezkfd = rulePop.zkfd;
												//记录优惠单据编号
												sg.ruledjbh = rulePop.djbh;
												//统计当前规则促销的折扣金额
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
													//记录折扣分担
													sg.mjzkfd = rulePop.zkfd;
													//记录优惠单据编号
													sg.ruledjbh = rulePop.djbh;
													//统计当前规则促销的折扣金额
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
													//记录折扣分担
													sg.rulezkfd = rulePop.zkfd;
													//记录优惠单据编号
													sg.ruledjbh = rulePop.djbh;
													//统计当前规则促销的折扣金额
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

										//记下金额最大的行号
										if (t_maxjerow >= 0
												&& ManipulatePrecision.doubleCompare(sg.hjje, ((SaleGoodsDef) saleGoods.get(t_maxjerow)).hjje, 2) > 0
												|| t_maxjerow < 0) t_maxjerow = k;
									}
								}
								if (yhsl <= 0) break;
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
							}
						}
					}
					//任意多级的，只判断一个结果
					break;
				}
				//数量促销 数量促销没有折上折(取的单价)，没有分级，取单价
				if (rulePop.yhdjlb == 'N')
				{
					//flag = 1 是全量优惠
					//flag = 2 是超量促销
					//flag = 3 是第n件促销
					//flag = 4 是整箱促销
					long flag = new Double(rulePop.yhhyj).longValue();
					//统计本单参与优惠的商品数量
					double kyhsl = 0;
					for (k = 0; k < calcCount; k++)
					{
						SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
						//条件排除的商品应该参与结果计算
						if (/*cominfo[k].infostr1[5] != ' ' && */isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
						{
							kyhsl += sg.sl;
							//如果前一个商品已经满足了折扣,
							//那么第二个商品必须标示为已经参与促销（此处为标示其已经促销的条件）。
						}
					}

					//优惠数量
					double yhsl = Double.parseDouble(rulePop.ppistr3);
					if (ManipulatePrecision.doubleCompare(yhsl, 0, 2) <= 0) yhsl = 1;//防止后面计算时除0错误

					//超量促销
					if (flag == 2) kyhsl -= yhsl;
					//第n件促销
					if (flag == 3) kyhsl = new Double(kyhsl / yhsl).longValue();
					//整箱促销
					if (flag == 4)
					{
						minbs = new Double(kyhsl / yhsl).longValue();
						long zyhsl = new Double(minbs * yhsl).longValue();//整箱总的优惠数量
						kyhsl = kyhsl > zyhsl ? zyhsl : kyhsl;
					}

					//开始计算优惠
					if (ManipulatePrecision.doubleCompare(kyhsl, 0, 2) > 0)
					{
						for (k = 0; k < calcCount; k++)
						{
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
							if (kyhsl > 0 && isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
							{
								//优惠价低于零售价
								if (sg.lsj > rulePop.yhlsj && rulePop.yhlsj > 0)
								{
									if (kyhsl >= sg.sl)
									{
										kyhsl -= sg.sl;
									}
									else
									{
										//拆分商品行
										splitSalecommod(k, kyhsl);
										kyhsl = -1;
									}
									sg.rulezke = 0;
									double misszke = sg.yhzke + sg.hyzke + sg.plzke;
									sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + misszke, 2, 0)
											- ManipulatePrecision.doubleConvert(rulePop.yhlsj * sg.sl, 2, 0);

									//优惠价低于一般促销
									if (ManipulatePrecision.doubleCompare(sg.rulezke, misszke, 2) > 0)
									{
										sg.yhzke = 0;
										sg.hyzke = 0;
										sg.plzke = 0;
										sg.spzkfd = 0;
										sg.yhzkfd = 0;//清除普通优惠和会员优惠
										sg.yhdjbh = "";
										sg.rulezkfd = rulePop.zkfd;
										calcComZkxe(k, sg.rulezke);
										//记录优惠单据编号
										sg.ruledjbh = rulePop.djbh;
										superMarketRuleyhje += sg.rulezke - misszke;

										//参与满减促销的标志
										sg.isPopExc = 'Y';
									}
									else sg.rulezke = 0;
								}
							}
						}
					}
				}
				//任意几个定单价 只分多级
				if (rulePop.yhdjlb == 'Z')
				{
					if (ManipulatePrecision.doubleCompare(zje, ((SuperMarketPopRuleDef) ruleReqList.get(0)).yhlsj, 2) >= 0
							&& ManipulatePrecision.doubleCompare(((SuperMarketPopRuleDef) ruleReqList.get(0)).yhlsj, 0, 2) > 0)
					{
						//参与优惠的商品明细数量
						long yhsl = new Double(minbs * ((SuperMarketPopRuleDef) ruleReqList.get(0)).yhlsj).longValue();

						for (l = 0; l < ruleReqList.size(); l++)
						{
							SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef) ruleReqList.get(l);
							for (k = 0; k < saleGoods.size(); k++)
							{
								SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
								if (yhsl > 0 && isMatchCommod(ruleReq, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
								{
									//商品拆分
									if (ManipulatePrecision.doubleCompare(sg.sl, yhsl, 4) <= 0)
									{
										yhsl -= sg.sl;
									}
									else
									{
										//拆分商品行
										splitSalecommod(k, yhsl);

										yhsl = 0;
									}
									double misszke = 0;
									if (i == calcCount + 1) misszke = sg.yhzke + sg.hyzke + sg.plzke + sg.rulezke;//记录可能被清零的折扣额
									else misszke = sg.yhzke + sg.hyzke + sg.plzke;//记录可能被清零的折扣额

									if (rulePop.iszsz.charAt(0) == '1')
									{
										if (i == calcCount + 1)
										{
											sg.mjzke = 0;
											sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0)
													- ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);
											calcComZkxe(k, sg.mjzke);
											sg.mjdjbh = rulePop.djbh;
											sg.mjzkfd = rulePop.zkfd;
											//统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.mjzke;
										}
										else
										{
											sg.rulezke = 0;
											sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0)
													- ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);
											calcComZkxe(k, sg.rulezke);
											sg.ruledjbh = rulePop.djbh;
											sg.rulezkfd = rulePop.zkfd;
											//统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.rulezke;
										}
									}
									else
									{
										if (i == calcCount + 1)
										{
											sg.mjzke = 0;
											sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + misszke, 2, 0)
													- ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);

											if (sg.mjzke > misszke)//如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的差额
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
												//统计当前规则促销的折扣金额
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
											sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + misszke, 2, 0)
													- ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);

											if (sg.rulezke > misszke)//如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的差额
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
												//统计当前规则促销的折扣金额
												superMarketRuleyhje += sg.rulezke - misszke;
											}
											else
											{
												sg.rulezke = 0;
											}
										}
									}
									sg.isPopExc = 'Y';
								}
							}
							if (yhsl <= 0) break;
						}
					}
					//任意多级的，只判断一个结果
					break;
				}
				//对指定商品固定优惠金额
				if (rulePop.yhdjlb == 'V')
				{
					double yhsl = minbs * rulePop.yhhyj;//优惠数量
					double yhje = ManipulatePrecision.doubleConvert(minbs * rulePop.yhlsj, 2, 0);

					//结果为OR关系的时候 按照第一个结果的优惠数量来优惠剩余商品
					long and_flag = new Double(rulePop.presentjs).longValue();

					or_yhsl = yhsl;
					zje = 0;
					t_zje = 0;

					while (and_flag == 1 || and_flag == 0 && ManipulatePrecision.doubleCompare(or_yhsl, 0, 4) > 0 && j < rulePopList.size())
					{
						//统计参与优惠金额分配的商品总金额
						for (k = 0; k < saleGoods.size() && ManipulatePrecision.doubleCompare(or_yhsl, 0, 4) > 0; k++)
						{
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
							//商品是否结果匹配
							if (isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
							{
								//商品拆分
								if (ManipulatePrecision.doubleCompare(sg.sl, or_yhsl, 4) <= 0)
								{
									or_yhsl -= sg.sl;
								}
								else
								{
									//拆分商品行
									splitSalecommod(k, or_yhsl);
									or_yhsl = 0;
								}
								zje += sg.hjje;
								t_zje += sg.hjje - getZZK(sg);
							}
						}
						if (and_flag == 1) break;
						j++;
					}
					if (ManipulatePrecision.doubleCompare(yhje, t_zje, 2) > 0) yhje = t_zje;

					//将优惠金额分担到商品明细上
					if (ManipulatePrecision.doubleCompare(zje, 0, 2) > 0 && ManipulatePrecision.doubleCompare(yhje, 0, 2) > 0
							&& ManipulatePrecision.doubleCompare(zje - yhje, 0, 2) >= 0)
					{
						int maxrow = -1;
						j = 0;
						t_zje = 0;
						while (and_flag == 1 || and_flag == 0 && ManipulatePrecision.doubleCompare(yhsl, 0, 4) > 0 && j < rulePopList.size())
						{
							for (k = 0; k < saleGoods.size() && ManipulatePrecision.doubleCompare(yhsl, 0, 4) > 0; k++)
							{
								SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
								//商品是否结果匹配
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
									//记录折扣分担
									sg.rulezkfd = rulePop.zkfd;
									//记录优惠单据编号
									sg.ruledjbh = rulePop.djbh;
									sg.isPopExc = 'Y';

									superMarketRuleyhje += je;
									t_zje += je;

									//记下金额最大的行号
									if (maxrow >= 0 && ManipulatePrecision.doubleCompare(sg.hjje, ((SaleGoodsDef) saleGoods.get(maxrow)).hjje, 2) > 0
											|| maxrow < 0) maxrow = k;
								}
							}
							if (and_flag == 1) break;
							j++;
						}
						//将未分配完的金额分配到金额最大的商品上
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
						}
					}
					//或条件时，已经优惠完了，所以要退出
					if (and_flag == 0) break;
				}
			}

			//将已参与规则促销的商品打上标志
			for (k = 0; k < saleGoods.size(); k++)
			{
				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
				//商品是否条件匹配
				if (sg.isPopExc != ' ')
				{
					sg.isSMPopCalced = 'N';
					sg.isPopExc = ' ';
				}
			}
			//类别为8表示整单满减
			if (ruleDef.type == '8') break;
		}

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

	private boolean splitSalecommod(int n, double newsl)
	{
		SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(n);
		GoodsDef goods = (GoodsDef) goodsAssistant.get(n);
		SpareInfoDef spare = (SpareInfoDef) goodsSpare.get(n);
		GoodsPopDef goodsPop = (GoodsPopDef) crmPop.get(n);

		if (saleGoods.size() <= 0 || n < 0 || n >= saleGoods.size()) return false;
		if (newsl >= sg.sl) return false;

		SaleGoodsDef newSg = (SaleGoodsDef) sg.clone();
		GoodsDef newGoods = (GoodsDef) goods.clone();
		SpareInfoDef newSpare = (SpareInfoDef) spare.clone();
		GoodsPopDef newGoodsPop = (GoodsPopDef) goodsPop.clone();

		double zje = sg.hjje;
		double rulezke = sg.rulezke;
		double mjzke = sg.mjzke;

		newSg.sl = sg.sl - newsl;
		sg.sl = newsl;

		//重算金额
		sg.hjje = ManipulatePrecision.doubleConvert(sg.sl * sg.jg, 2, 0);
		newSg.hjje = ManipulatePrecision.doubleConvert(newSg.sl * newSg.jg, 2, 0);

		//将拆分的商品的规则促销折扣金额进行分摊，此处必须分摊，不然会导致在计算整单的时候，出现成交金额为负数的情况
		sg.rulezke = (sg.hjje / zje) * rulezke;
		newSg.rulezke = (newSg.hjje / zje) * rulezke;
		sg.mjzke = (sg.hjje / zje) * mjzke;
		newSg.mjzke = (newSg.hjje / zje) * mjzke;

		getZZK(sg);
		getZZK(newSg);
		saleGoods.add(newSg);
		goodsAssistant.add(newGoods);
		goodsSpare.add(newSpare);
		crmPop.add(newGoodsPop);
		/*
		 //计算会员折扣和优惠折扣
		 CalculateAllRebate(n);
		 //批量销售折扣处理,重算优惠折扣和会员折扣
		 CalculateBatchRebate(n);

		 //计算会员折扣和优惠折扣
		 CalculateAllRebate(salecom_num-1);
		 //批量销售折扣处理,重算优惠折扣和会员折扣
		 CalculateBatchRebate(salecom_num-1);

		 //记录打印标志(打印标志取原行上的标志)
		 printflag[salecom_num-1] = printflag[n];

		 //	//记录断点
		 //	WriteBroken(n,BROKEN_UPDATE);
		 //	WriteBroken(salecom_num-1,BROKEN_APPEND);

		 //刷新被拆分的原商品行
		 if (n >= winfirst && n < winfirst + PAGE_SALE_COM - 1) DispOneSaleCommod(n - winfirst,n);

		 //刷新商品列表
		 DispSaleCommod();

		 CreateInputLine(2);
		 */
		// 刷新商品列表
		saleEvent.updateTable(getSaleGoodsDisplay());
		saleEvent.setTotalInfo();

		return true;
	}

	private double calcComZkxe(int k, double zke)
	{
		if (k < 0 || k >= saleGoods.size()) return zke;

		SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
		if (ManipulatePrecision.doubleCompare(sg.hjje - getZZK(sg), 0, 2) <= 0)
		{
			zke = 0;//引用数据类型,将实参清零
			zke = sg.hjje - getZZK(sg);
		}
		//计算价格精度
		zke = getConvertPrice(zke, (GoodsDef) goodsAssistant.elementAt(k));
		return zke;
	}

	private void getCurRuleJc(int jc)
	{
		int i;
		// 获得条件在jc传入值的级别所对应的级别值
		for (i = 0; i < ruleReqList.size(); i++)
		{
			SuperMarketPopRuleDef reqDef = (SuperMarketPopRuleDef) ruleReqList.get(i);
			double a = Double.parseDouble(reqDef.ppistr1.split("\\|")[jc - 1]);
			if (a > 0) reqDef.yhlsj = a;

		}
		for (i = 0; i < rulePopList.size(); i++)
		{
			SuperMarketPopRuleDef reqPop = (SuperMarketPopRuleDef) rulePopList.get(i);
			double a = Double.parseDouble(reqPop.ppistr1.split("\\|")[jc - 1]);
			if (a > 0) reqPop.yhlsj = a;
			double b = Double.parseDouble(reqPop.ppistr2.split("\\|")[jc - 1]);
			if (b > 0) reqPop.yhhyj = b;
		}
	}

	private boolean isMatchCommod(SuperMarketPopRuleDef ruleDef, int index)
	{
		SaleGoodsDef sg = ((SaleGoodsDef) saleGoods.get(index));
		GoodsDef goodsDef = ((GoodsDef) goodsAssistant.get(index));

		// 整单的规则,整单优先级最高 
		if (ruleDef.type == '8') return true;

		//只有正常的商品才参与规则促销
		if (sg.flag != '4' && sg.flag != '2') { return false; }

		//如果电子称商品不是排除条件
		if (ruleDef.presentsl != 1 || sg.flag != '2')
		{
			//如果电子称商品条件不是满减/满返，结果也不是满减/满返 
			if (!(ruleDef.yhdjlb == '8' && (ruleDef.ppistr3.charAt(0) == 'G' || ruleDef.ppistr3.charAt(0) == 'C') || ruleDef.yhdjlb == 'G' || ruleDef.yhdjlb == 'C')
					&& sg.flag == '2') { return false; }
		}

		//条件为整单的时候如果是结果为非整单。此处就不判断商品是否参与了规则促销，
		//不然在结果匹配的时候会因为商品参与了非整单规则促销而无法参与整单的规则促销
		//在整单规则的时候初始化商品标识的时候使用
		if (((SuperMarketPopRuleDef) ruleReqList.get(0)).type != '8')
		{
			//不参与规则促销
			if (sg.isSMPopCalced != 'Y') { return false; }
		}

		switch (ruleDef.type)
		{
			case '1'://单品
				if (!ruleDef.code.equals(goodsDef.code)) break;
				if ((ruleDef.gz.equals(goodsDef.gz) || ruleDef.gz.equals("0")) && (ruleDef.spec.equals(goodsDef.uid) || ruleDef.spec.equals("AL"))) { return true; }
				break;
			case '2'://柜组
				if (!ruleDef.code.equals(goodsDef.gz)) break;
				return true;
			case '3'://类别
				if (!ruleDef.code.equals(goodsDef.catid.substring(0, ruleDef.code.length()))) break;
				return true;
			case '4'://柜组品牌
				if (!ruleDef.code.equals(goodsDef.gz)) break;
				if (ruleDef.pp.equals(goodsDef.ppcode)) { return true; }
				break;
			case '5'://类别品牌
				if (!ruleDef.code.equals(goodsDef.catid.substring(0, ruleDef.code.length()))) break;
				if (ruleDef.pp.equals(goodsDef.ppcode)) { return true; }
				break;
			case '6'://品牌
				if (!ruleDef.code.equals(goodsDef.ppcode)) break;
				return true;
			case '7'://生鲜单品
				if (!ruleDef.code.equals(goodsDef.code)) break;
				if ((ruleDef.gz.equals(goodsDef.gz) || ruleDef.gz.equals("0")) && (ruleDef.spec.equals(goodsDef.uid) || ruleDef.spec.equals("AL"))) { return true; }
				break;
		}
		return false;
	}

	protected boolean createMDPayment(double lszszk)
	{
		String mdCode[] = GlobalInfo.sysPara.mdcode.split(",");
		if (mdCode[0].trim().equals(""))
		{
			new MessageBox(Language.apply("满抵参数未定义!\n无法计入满抵"));
			return false;
		}
		PayModeDef paymode = DataService.getDefault().searchPayMode(mdCode[0]);
		if (paymode == null)
		{
//			new MessageBox("[" + mdCode[0] + "]" + "付款方式未定义!\n无法计入满抵");
			new MessageBox(Language.apply("[{0}]付款方式未定义!\n无法计入满抵" ,new Object[]{mdCode[0]}));
			return false;
		}
		// 付款记账

		//创建一个付款方式对象
		Payment pay = CreatePayment.getDefault().createPaymentByPayMode(paymode, saleEvent.saleBS);
		if (pay == null) return false;

		// inputPay这个方法根据不同的付款方式进行重写
		SalePayDef sp = pay.inputPay(String.valueOf(lszszk));

		if (pay.paymode.code.equals(mdCode[0]))
		{
			if (mdCode.length > 1) pay.salepay.idno = mdCode[1];
		}

		// liwj add
		// 标记本行付款唯一序号,用于删除对应商品的分摊
		if (sp != null) sp.num5 = salePayUnique++;

		// 加入付款明细
		salePayment.add(sp);
		payAssistant.add(pay);

		return true;
	}
	
	protected boolean createMDPayment(double lszszk ,String type)
	{
		String mdCode[] = GlobalInfo.sysPara.mdcode.split(",");
		String text = "";
		if(type.equals("MH")){
			text = Language.apply("买换");
		}else {
			
			text = Language.apply("满抵");
		}
		
		if (mdCode[0].trim().equals(""))
		{
//			new MessageBox(text+"参数未定义!\n无法计入"+text);
			new MessageBox(Language.apply("{0}参数未定义!\n无法计入{1}" ,new Object[]{text ,text}));
			return false;
		}
		PayModeDef paymode = DataService.getDefault().searchPayMode(mdCode[0]);
		if (paymode == null)
		{
//			new MessageBox("[" + mdCode[0] + "]" + "付款方式未定义!\n无法计入"+text);
			new MessageBox(Language.apply("[{0}]付款方式未定义!\n无法计入{1}",new Object[]{mdCode[0] ,text}));
			return false;
		}
		// 付款记账

		//创建一个付款方式对象
		Payment pay = CreatePayment.getDefault().createPaymentByPayMode(paymode, saleEvent.saleBS);
		if (pay == null) return false;

		// inputPay这个方法根据不同的付款方式进行重写
		SalePayDef sp = pay.inputPay(String.valueOf(lszszk));

		if (pay.paymode.code.equals(mdCode[0]))
		{
			if (mdCode.length > 1) pay.salepay.idno = mdCode[1];
		}

		// liwj add
		// 标记本行付款唯一序号,用于删除对应商品的分摊
		if (sp != null) sp.num5 = salePayUnique++;

		// 加入付款明细
		salePayment.add(sp);
		payAssistant.add(pay);

		return true;
	}
}
