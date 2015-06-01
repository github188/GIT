package custom.localize.Jcgj;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.FjkInfoQueryBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.PrintTemplate.CheckGoodsMode;
import com.efuture.javaPos.Struct.CalcRulePopDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.OperRoleDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.SalePayForm;

import custom.localize.Cmls.Cmls_SaleBS;

public class Jcgj_SaleBS extends Cmls_SaleBS
{
	// 指定原小票退货，需要调用银石接口，获取真实的会员积分
	public boolean findBackTicketInfo()
	{
		if (super.findBackTicketInfo())
		{
			if (saleHead.hykh != null && saleHead.hykh.length() > 0)
			{
				new MessageBox("原销售小票刷过会员卡，回车键后请重新刷卡");
				NewKeyListener.sendKey(GlobalVar.MemberGrant);
			}
			return true;
		}
		return false;
	}

	// 开始一笔新的交易前，调用银石的卡初始化接口
	public void initNewSale()
	{
		Jcgj_Svc svc = new Jcgj_Svc("svc_init", null, "");
		if (svc.doYsCard(null)) Jcgj_Svc.isInitSucc = true;
		super.initNewSale();
	}

	// 在指定小票退货时，刷会员卡后，把可用积分传到后台以便计算扣回
	public boolean memberGrant()
	{
		if (super.memberGrant())
		{
			saleHead.str3 = curCustomer.str1; // 持卡人生日
			saleHead.num2 = curCustomer.valuememo;
			return true;
		}
		else return false;
	}

	public void paySellCancel_Extend()
	{
		if (!isNewUseSpecifyTicketBack(false))
		{
			super.paySellCancel_Extend();
		}
	}

	public boolean saleCollectAccountPay()
	{
		Payment p = null;
		boolean czsend = true;

		int n = 0;
		StringBuffer cardSellInfo = new StringBuffer();
		String req = "";
		// 付款对象记账
		for (int i = 0; i < payAssistant.size(); i++)
		{
			p = (Payment) payAssistant.elementAt(i);
			if (p == null) continue;

			// 第一次记账前先检查是否有冲正需要发送
			if (czsend)
			{
				czsend = false;
				if (!p.sendAccountCz()) return false;
			}

			if (p.paymode.code.equals("0405") || p.paymode.code.equals("0408"))
			{
				cardSellInfo.append("01");
				cardSellInfo.append("                    ");
				cardSellInfo.append(Convert.increaseChar(String.valueOf((long) ManipulatePrecision.doubleConvert(SellType.SELLSIGN(saleHead.djlb)
						* p.salepay.ybje * 100, 2, 1)), ' ', 12));
				cardSellInfo.append(Convert.increaseChar(p.salepay.payno, ' ', 19));
				n++;
			}
			else
			{
				// 付款记账
				if (!p.collectAccountPay()) return false;
			}

			if (n > 0)
			{
				req = Convert.increaseChar(ConfigClass.Market + ConfigClass.CashRegisterCode, ' ', 10)
						+ Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 10) + Convert.increaseChar(String.valueOf(saleHead.fphm), ' ', 20)
						+ Convert.increaseChar(String.valueOf(n), ' ', 3) + cardSellInfo.toString();
			}
		}

		// 开始调用银联接口集中记账
		if (req.length() > 0)
		{
			Jcgj_Svc svc = new Jcgj_Svc("svc_nsale", saleHead, req);
			if (svc.doYsCard(null))
			{
				svc = new Jcgj_Svc("svc_commit", null, "");
				if (!svc.doYsCard(null))
				{
					new MessageBox(svc.getMethordName() + "储值卡记账失败");
					return false;
				}
				else return true;
			}
			else
			{
				new MessageBox(svc.getMethordName() + "上传储值卡交易失败");
				return false;
			}
		}

		// 移动充值对象记账
		if (GlobalInfo.useMobileCharge && !mobileChargeCollectAccount(true)) return false;

		return true;

	}

	// 在付款时不允许使用零钞转存的付款方式
	public boolean checkPaymodeValid(PayModeDef mode, String money)
	{
		if (super.checkPaymodeValid(mode, money))
		{
			if (mode.code.equals("0111"))
			{
				new MessageBox("该付款方式已禁用");
				return false;
			}
			return true;
		}
		return false;
	}
	
	private void setBargainZKL()
	{
		if (!SellType.ISSALE(saletype)) { return; }

		if (SellType.NOPOP(saletype)) return;

		if (SellType.ISEARNEST(saletype)) { return; }

		if (SellType.ISPREPARETAKE(saletype)) { return; } 
		
		SaleGoodsDef sg = null;
		GoodsDef goods = null;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			sg = (SaleGoodsDef) saleGoods.get(i);
			goods = (GoodsDef) goodsAssistant.get(i);
			
			if (isBargain(goods, sg))
			{
				if (sg.str1.length() > 0) sg.str1 = sg.str1.substring(0, sg.str1.length() - 1) + "2";
				else sg.num1 = 2;
			}
		}
	}
	
	private boolean isBargain(GoodsDef goods, SaleGoodsDef sgd)
	{
		if ("Y".equals(goods.str1) || isOverLimitDisRate(sgd)) return true;
		return false;
	}

	public boolean doCrmPop()
	{
		setBargainZKL();
		
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
			double zkl = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - getZZK(saleGoodsDef)) / saleGoodsDef.hjje, 2, 1);

//			new MessageBox("折扣率为： " + zkl + "\n折扣门槛为："  + goodsPop1.pophyjzkfd);
			if (zkl < goodsPop1.pophyjzkfd)
			{
//				new MessageBox("进入门槛，开始修改促销码");
				
				if (mjrule.charAt(0) == '9')
				{
					StringBuffer buff = new StringBuffer(mjrule);
					for (int z = 2 ; z < buff.length() - 1; z++)
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
				/*            	
				 if (set.size() >= 2)
				 {
				 new MessageBox("本笔交易存在不同的活动促销\n\n请分单进行收银");
				 doRulePopExit = true;
				 return false;
				 }
				 if (calPop.row_set.size() != saleGoods.size())
				 {
				 new MessageBox("本笔交易部分商品参与活动促销,部分不参与\n\n请分单进行收银");
				 doRulePopExit = true;
				 return false;
				 }       
				 */
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
				/**
				 if (cxgz <= 1)
				 {
				 double cwpayje = 0;
				 for (j=0;j<salePayment.size();j++)
				 {
				 SalePayDef pay = (SalePayDef)salePayment.elementAt(j);
				 cwpayje += pay.je;
				 }
				 cwpayje -= salezlexception;
				 sphj = ManipulatePrecision.doubleConvert(sphj - cwpayje,2,1);
				 }
				 else
				 {*/
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
		
		/*
		if (!SellType.ISSALE(saletype)) { return false; }

		if (SellType.NOPOP(saletype)) return false;

		if (SellType.ISEARNEST(saletype)) { return false; }

		if (SellType.ISPREPARETAKE(saletype)) { return false; }

		SaleGoodsDef sg = null;
		SpareInfoDef info = null;
		GoodsDef goods = null;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			sg = (SaleGoodsDef) saleGoods.get(i);
			goods = (GoodsDef) goodsAssistant.get(i);
			if ((goods.str1 != null && goods.str1.equals("Y")) || isOverLimitDisRate(sg))
			{
				info = (SpareInfoDef) goodsSpare.get(i);
				
				if (info.str1.charAt(0) == '9')
				{
//					new MessageBox("原促销码为： " + info.str1);
					String cxm = "9000000";
					if (info.str1.charAt(info.str1.length() - 1) == '1')
					{
//						new MessageBox("存在积分规则： " + info.str1);
						cxm = "9000001";
					}
					info.str1 = cxm;
//					new MessageBox("新促销码为： " + info.str1);
					sg.str3 = cxm + sg.str3.substring(sg.str3.indexOf(";"));
					if (sg.str1.length() > 0) sg.str1 = sg.str1.substring(0, sg.str1.length() - 1) + "2";
				}
				else
				{
					sg.num1 = 2;
				}
			}
		}
		return super.doCrmPop();
		*/
	}

	public boolean isOverLimitDisRate(SaleGoodsDef sg)
	{
		double zkl = ManipulatePrecision.doubleConvert((sg.hjje - sg.hjzk) / sg.hjje, 2, 1);
		double limitZkl = ManipulatePrecision.doubleConvert(GlobalInfo.sysPara.limitDisRate, 2, 1);
		if (zkl < limitZkl) return true;
		else return false;
	}

	public void execCustomKey1(boolean keydownonsale)
	{
		new FjkInfoQueryBS().QueryFjkInfo();
	}

	public boolean addSaleGoods(GoodsDef goodsDef, String yyyh, double quantity, double price, double allprice, boolean dzcm)
	{
		if (SellType.CARD_SALE.equals(saletype))
		{
			String req = Convert.increaseChar(ConfigClass.Market + ConfigClass.CashRegisterCode, ' ', 10)
					+ Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 10)
					+ Convert.increaseCharForward(String.valueOf((long) ManipulatePrecision.doubleConvert(price * 100, 2, 1)), '0', 12);

			Jcgj_Svc svc = new Jcgj_Svc("svc_pre_card_sale", null, req);
			Jcgj_YsCardDef cardDef = new Jcgj_YsCardDef();

			if (svc.doYsCard(cardDef))
			{
				if (cardDef.cardNo.length() > 0)
				{
					goodsDef.str5 = cardDef.cardNo;
					if (isCardSaleAlready(goodsDef)) return super.addSaleGoods(goodsDef, yyyh, quantity, price, allprice, dzcm);
					else return false;
				}
				else
				{
					new MessageBox("svc_pre_card_sale 返回卡号不正确");
					return false;
				}
			}
			else
			{
				new MessageBox("预售储值卡不成功，请重新扫码");
				return false;
			}
		}
		else return super.addSaleGoods(goodsDef, yyyh, quantity, price, allprice, dzcm);
	}

	public GoodsDef findGoodsInfo(String code, String yyyh, String gz, String dzcmscsj, boolean isdzcm, StringBuffer slbuf)
	{
		GoodsDef goodsDef = super.findGoodsInfo(code, yyyh, gz, dzcmscsj, isdzcm, slbuf);
		if (goodsDef != null)
		{
			if (SellType.CARD_SALE.equals(saletype))
			{
				if (!goodsDef.str5.equalsIgnoreCase("Y"))
				{
					new MessageBox("该商品编码不是售卡类型，不允许销售");
					return null;
				}
			}
			else
			{
				if (goodsDef.str5.equalsIgnoreCase("Y"))
				{
					new MessageBox("该商品编码是售卡类型，不允许销售");
					return null;
				}
			}
		}
		return goodsDef;
	}

	public double getMaxSaleGoodsMoney()
	{
		if (SellType.CARD_SALE.equals(saletype))
		{
			return GlobalInfo.sysPara.maxPriceInCardSale;
		}
		else return super.getMaxSaleGoodsMoney();
	}

	public void printSaleBill()
	{
		// 销售小票，访问过程打印印花
		if (SellType.ISSALE(saletype))
		{
			double je;
			je = saleHead.ysje;
			// 扣掉除券的付款方式金额
			if (GlobalInfo.sysPara.mjPaymentRule.trim().length() > 0)
			{
				String[] payCodes = GlobalInfo.sysPara.mjPaymentRule.split(",");
				SalePayDef salePay;
				for (int i = 0; i < salePayment.size(); i++)
				{
					salePay = (SalePayDef) salePayment.get(i);
					for (int j = 0; j < payCodes.length; j++)
					{
						if (salePay.paycode.equals(payCodes[j])) je -= salePay.je;
					}
				}
			}

			if (je > 0)
			{
				Jcgj_NetService netService = new Jcgj_NetService();
				netService.getPfInfo(saleHead, je, CmdDef.GETPFINFO);
			}
		}
		
		// 售卡
		if (SellType.CARD_SALE.equals(saletype))
		{
			Jcgj_Svc svc;
			String req = "";
			SaleGoodsDef sgd;
			for (int i = 0; i < saleGoods.size(); i++)
			{
				sgd = (SaleGoodsDef) saleGoods.get(i);
				req = Convert.increaseChar(ConfigClass.Market + ConfigClass.CashRegisterCode, ' ', 10)
						+ Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 10) + Convert.increaseChar(sgd.str7, ' ', 19)
						+ Convert.increaseCharForward(String.valueOf((long) ManipulatePrecision.doubleConvert(sgd.lsj * 100, 2, 1)), '0', 12);
				svc = new Jcgj_Svc("svc_card_sale", null, req);
				if (!svc.doYsCard(null)) new MessageBox("售卡充值，请到服务台处理");
			}
		}
		super.printSaleBill();
	}

	public void backSell()
	{
		if (SellType.CARD_SALE.equals(saletype))
		{
			new MessageBox("售卡不允许退货！");
			return;
		}
		super.backSell();
	}

	public SaleGoodsDef goodsDef2SaleGoods(GoodsDef goodsDef, String yyyh, double quantity, double price, double allprice, boolean dzcm)
	{
		SaleGoodsDef sgd = super.goodsDef2SaleGoods(goodsDef, yyyh, quantity, price, allprice, dzcm);
		if (sgd != null && goodsDef.str5 != null && goodsDef.str5.trim().length() > 0)
		{
			sgd.str7 = goodsDef.str5;
			sgd.name = goodsDef.str5 + sgd.name;
		}
		return sgd;
	}

	public boolean isCardSaleAlready(GoodsDef goodsDef)
	{
		if (goodsAssistant == null || goodsAssistant.size() < 1) return true;
		else
		{
			GoodsDef g = null;
			for (int i = 0; i < goodsAssistant.size(); i++)
			{
				g = (GoodsDef) goodsAssistant.get(i);
				if (goodsDef.str5.equals(g.str5))
				{
					new MessageBox("此卡已经预售过，不能重复预售  " + goodsDef.code);
					return false;
				}
			}
			return true;
		}
	}

	public void enterInputCODE()
	{
		if (GlobalInfo.sysPara.isMustCustCoupon == 'Y' && SellType.PURCHANSE_COUPON.equals(saletype))
		{
			if (curCustomer == null)
			{
				new MessageBox("买券交易前请刷会员卡");
				return;
			}
		}
		super.enterInputCODE();
	}

	protected boolean doneDeleteGoods(int index, SaleGoodsDef old_goods)
	{
		// 没有删除权限,不允许删除
		if ((curGrant.privqx != 'Y' && curGrant.privqx != 'Q') && GlobalInfo.syjDef.issryyy != 'Y')
		{
			// 授权
			OperUserDef staff = deleteGoodsGrant(index);
			if (staff == null) return false;

			// 记录日志
			String log = "授权删除,小票号:" + Convert.increaseLong(saleHead.fphm, 7) + ",单价:" + Convert.increaseDou(old_goods.jg, 10) + ",授权:"
					+ Convert.increaseChar(staff.gh, ' ', 6) + ",商品:" + old_goods.barcode;
			AccessDayDB.getDefault().writeWorkLog(log, StatusType.WORK_DELETESALE);
		}
		else
		{
			String log = "删除商品,小票号:" + Convert.increaseLong(saleHead.fphm, 7) + ",单价:" + Convert.increaseDou(old_goods.jg, 10) + ",授权:"
					+ Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 6) + ",商品:" + old_goods.barcode;
			AccessDayDB.getDefault().writeWorkLog(log, StatusType.WORK_DELETESALE);
		}

		// 修改老的盘点单数据 删除时记录删除标志
		if (SellType.ISCHECKINPUT(saletype) && isSpecifyCheckInput() && !"A".equals(old_goods.str8))
		{
			if ("D".equals(old_goods.str8)) return false;
			old_goods.str8 = "D";
			old_goods.name += "[删除]";
			return true;
		}

		SaleGoodsDef cloneGoods = (SaleGoodsDef) old_goods.clone();
		old_goods.sl = 0;

		// 重算因为删除本行，对其他行商品产生的影响
		old_goods.hjje = old_goods.jg * old_goods.sl;
		clearGoodsGrantRebate(index);
		calcGoodsYsje(index);

		// 删除数量为零的商品
		if (0.0 == old_goods.sl)
		{
			if (!delSaleGoodsObject(index)) return false;
		}

		// 计算小票合计
		calcHeadYsje();

		// 删除上次显示列表,刷新显示列表
		if (0.0 == old_goods.sl)
		{
			getDeleteGoodsDisplay(index, cloneGoods);
		}

		return true;
	}

	public boolean operPermission(int type, OperRoleDef oper)
	{
		if (GlobalInfo.syjDef.issryyy != 'Y' && super.operPermission(type, oper)) return true;
		else return false;
	}

	private boolean isGrantRePrint(String roleCode)
	{
		if (GlobalInfo.syjDef.issryyy != 'N') return true;
		if (GlobalInfo.sysPara.grantRole.length() < 1) return false;
		String[] roles = GlobalInfo.sysPara.grantRole.split(",");
		for (int i = 0; i < roles.length; i++)
		{
			if (roles[i].equals(roleCode)) return true;
		}
		return false;
	}

	// 重新打印上一张小票
	public void rePrint()
	{
		ResultSet rs = null;
		SaleHeadDef saleheadprint = null;
		Vector salegoodsprint = null;
		Vector salepayprint = null;

		// 盘点
		if (SellType.ISCHECKINPUT(saletype))
		{
			if (saleGoods == null || saleGoods.size() <= 0) return;

			if (!CheckGoodsMode.getDefault().isLoad()) return;

			MessageBox me = new MessageBox("你确实要打印盘点小票吗?", null, true);

			if (me.verify() != GlobalVar.Key1) return;

			CheckGoodsMode.getDefault().setTemplateObject(saleHead, saleGoods, salePayment);

			CheckGoodsMode.getDefault().printBill();

			return;
		}

		if (GlobalInfo.syjDef.printfs == '1' && saleGoods != null && saleGoods.size() > 0)
		{
			new MessageBox("当前打印为即扫即打并且已有商品交易,不能重打!", null, false);

			return;
		}

		// 检查发票是否打印完,打印完未设置新发票号则不能交易
		if (Printer.getDefault().getSaleFphmComplate()) { return; }

		MessageBox me = new MessageBox("你确实要重印上一张小票吗?", null, true);
		try
		{
			if (me.verify() == GlobalVar.Key1 && getReprintAuth())
			{
				Object obj = null;
				String fphm = null;

				if (!rePrintSellTicket()) return;

				if ((obj = GlobalInfo.dayDB.selectOneData("select max(fphm) from salehead where syjh = '" + ConfigClass.CashRegisterCode + "'")) != null)
				{
					try
					{
						fphm = String.valueOf(obj);

						if ((rs = GlobalInfo.dayDB.selectData("select * from salehead where syjh = '" + ConfigClass.CashRegisterCode
								+ "' and  fphm = " + fphm)) != null)
						{

							if (!rs.next())
							{
								new MessageBox("没有查询到小票头,不能打印!");
								return;
							}

							saleheadprint = new SaleHeadDef();

							if (!GlobalInfo.dayDB.getResultSetToObject(saleheadprint)) { return; }
						}
						else
						{
							new MessageBox("查询小票头失败!", null, false);
							return;
						}
					}
					catch (Exception ex)
					{
						new MessageBox("查询小票头出现异常!", null, false);
						ex.printStackTrace();
						return;
					}
					finally
					{
						GlobalInfo.dayDB.resultSetClose();
					}

					try
					{
						if ((rs = GlobalInfo.dayDB.selectData("select * from SALEGOODS where syjh = '" + ConfigClass.CashRegisterCode
								+ "' and fphm = " + fphm + " order by rowno")) != null)
						{
							boolean ret = false;
							salegoodsprint = new Vector();
							while (rs.next())
							{
								SaleGoodsDef sg = new SaleGoodsDef();

								if (!GlobalInfo.dayDB.getResultSetToObject(sg)) { return; }

								salegoodsprint.add(sg);

								ret = true;
							}

							if (!ret)
							{
								new MessageBox("没有查询到小票明细,不能打印!");
								return;
							}
						}
						else
						{
							new MessageBox("查询小票明细失败!", null, false);
							return;
						}
					}
					catch (Exception ex)
					{
						new MessageBox("查询小票明细出现异常!", null, false);
						ex.printStackTrace();
						return;
					}
					finally
					{
						GlobalInfo.dayDB.resultSetClose();
					}

					try
					{
						if ((rs = GlobalInfo.dayDB.selectData("select * from SALEPAY where syjh = '" + ConfigClass.CashRegisterCode + "' and fphm = "
								+ fphm + " order by rowno")) != null)
						{
							boolean ret = false;
							salepayprint = new Vector();
							while (rs.next())
							{
								SalePayDef sp = new SalePayDef();

								if (!GlobalInfo.dayDB.getResultSetToObject(sp)) { return; }

								salepayprint.add(sp);

								ret = true;
							}
							if (!ret)
							{
								new MessageBox("没有查询到付款明细,不能打印!");
								return;
							}
						}
						else
						{
							new MessageBox("查询付款明细失败!", null, false);
							return;
						}
					}
					catch (Exception ex)
					{
						new MessageBox("查询付款明细出现异常!", null, false);
						ex.printStackTrace();
						return;
					}
					finally
					{
						GlobalInfo.dayDB.resultSetClose();
					}

					saleheadprint.printnum++;
					AccessDayDB.getDefault().updatePrintNum(saleheadprint.syjh, String.valueOf(saleheadprint.fphm),
															String.valueOf(saleheadprint.printnum));
					ProgressBox pb = new ProgressBox();
					pb.setText("现在正在重打印小票,请等待.....");
					try
					{
						printSaleTicket(saleheadprint, salegoodsprint, salepayprint, false);
					}
					finally
					{
						pb.close();
					}
				}
				else
				{
					new MessageBox("当前没有销售数据,不能打印!");
				}
			}
		}
		finally
		{
			saleheadprint = null;

			if (salegoodsprint != null)
			{
				salegoodsprint.clear();
				salegoodsprint = null;
			}

			if (salepayprint != null)
			{
				salepayprint.clear();
				salepayprint = null;
			}
		}
	}

	private boolean rePrintGrant()
	{
		OperUserDef user = null;
		if ((user = DataService.getDefault().personGrant("授权重打印小票")) != null)
		{
			if (!isGrantRePrint(user.role) || (user.privdy != 'Y' && user.privdy != 'L'))
			{
				new MessageBox("当前工号没有重打上笔小票权限!");

				return false;
			}

			String log = "授权重打印上一笔小票,授权工号:" + user.gh;
			AccessDayDB.getDefault().writeWorkLog(log);
		}
		else
		{
			return false;
		}
		return true;
	}

	public boolean rePrintSellTicket()
	{
		// 超市模式
		if (GlobalInfo.syjDef.issryyy == 'N')
		{
			// 该收银角色不允许重打印，需要授权
			if (!isGrantRePrint(GlobalInfo.posLogin.role))
			{
				if (!rePrintGrant()) return false;
			}
			else
			{
				if (curGrant.privdy != 'Y' && curGrant.privdy != 'L')
				{
					if (!rePrintGrant()) return false;
				}
			}
		}
		// 百货模式
		else
		{
			if (curGrant.privdy != 'Y' && curGrant.privdy != 'L')
			{
				if (!rePrintGrant()) return false;
			}
		}
		return true;
	}
}
