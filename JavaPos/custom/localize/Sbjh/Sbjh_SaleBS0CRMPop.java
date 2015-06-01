package custom.localize.Sbjh;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.CalcRulePopDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.SalePayForm;

import custom.localize.Gwzx.Gwzx_SaleBS;

public class Sbjh_SaleBS0CRMPop extends Gwzx_SaleBS
{
//	 新CRM满减促销
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
								//new MessageBox("规则："+row[c]);
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
									double num1 = 0;
									//浮点运算1       = 0.999999,需要进位到两位小数再取整
									//浮点运算299/300 = 0.996666,进位取整=1,还需再乘分母用金额比较，如果大倍数要减1
									//calPop.popDef.str5 = "B";
									if(calPop.popDef.str5.equals("B")){
										//new MessageBox("商品合计："+ sphj+" 已付:"+yfmj +" 条件:"+t);
										num1 = ManipulatePrecision.doubleConvert((sphj - yfmj)/t,2,1);
										//new MessageBox("倍数："+ num1);
										//new MessageBox("num1="+num1);
									}else{
										num = ManipulatePrecision.integerDiv(sphj - yfmj, t);
										num1 = num;
									}
									
									double bcje = 0;
									if (num1 > 0)
									{
										bcje = num1 * je;
									}

									if (bcje > limitje)
									{
										bcje = limitje;
									}

									mjje += bcje;
									yfmj = ManipulatePrecision.doubleConvert(num1 * t + yfmj);
									
									
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
						double num1 = 0;
						//calPop.popDef.str5 = "B";
						if(calPop.popDef.str5.equals("B")){
							if (calPop.popDef.poplsj > 0)
							{
								//浮点运算1       = 0.999999,需要进位到两位小数再取整
								//浮点运算299/300 = 0.996666,进位取整=1,还需再乘分母用金额比较，如果大倍数要减1
								//new MessageBox("商品合计："+ sphj+" 已付:"+yfmj +" 条件:"+calPop.popDef.poplsj);
								num1 = ManipulatePrecision.doubleConvert((sphj - yfmj)/calPop.popDef.poplsj,2,1 );
								//new MessageBox("倍数："+ num1);
								//new MessageBox("num1="+num1);
							}
						}else{
							if (calPop.popDef.poplsj > 0)
							{
								//浮点运算1       = 0.999999,需要进位到两位小数再取整
								//浮点运算299/300 = 0.996666,进位取整=1,还需再乘分母用金额比较，如果大倍数要减1
								num = ManipulatePrecision.integerDiv(sphj - yfmj, calPop.popDef.poplsj);
								num1 = num;
							}
						}
						

						// 满足促销条件，不超过满减限额，不超过参与打折的金额
						double bcje = num1 * calPop.popDef.pophyj;
						
						if (bcje + mjje > limitje) bcje = limitje - mjje;
						if (bcje > 0 && (bcje + mjje <= calPop.popje))
						{
							mjje += bcje;
							yfmj += num1 * calPop.popDef.poplsj;
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
								num1 = ManipulatePrecision.integerDiv(sphj - yfmj, a);

								// 满足促销条件，不超过满减限额，不超过参与打折的金额
								bcje = num1 * b;
								if (bcje + mjje > limitje) bcje = limitje - mjje;
								if (bcje > 0 && (bcje + mjje <= calPop.popje))
								{
									mjje += bcje;
									yfmj += num1 * a;
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
					//calPop.popDef.str5 = "B";
					if(calPop.popDef.str5.equals("B")){
						je = ManipulatePrecision.doubleConvert(je+0.09,1,0);
						
						//new MessageBox("金额:"+je);
					}else{
						je = getDetailOverFlow(je, GlobalInfo.syjDef.sswrfs);
					}
					
					String line1 = "";
					for (int x = 0; x < calPop.row_set.size(); x++)
					{
						line1 += "," + String.valueOf(Convert.toInt((String) calPop.row_set.elementAt(x)) + 1);
					}
					
					line1 = line1.substring(1);

					saleHead.str2 += calPop.popDef.kssj + "\n" + "满减：" + Convert.increaseChar(String.valueOf(je), 8) + "(" + line1 + ")\n";
					
					String info = " ";
					
					if (GlobalInfo.sysPara.mjtype == 'Y'){
						if ((calPop.popDef.str3 != null) && (calPop.popDef.str3.trim().length() > 0)){
							String[] row = calPop.popDef.str3.split(";");

							for (int c = row.length - 1; c >= 0; c--)
							{
								if ((row[c] == null) || (row[c].split(",").length != 4))
								{
									continue;
								}
								double mjtj = Convert.toDouble(row[c].split(",")[2]); //满减条件
								double mjje = Convert.toDouble(row[c].split(",")[3]); //满减金额
								info = "1换"+String.valueOf(ManipulatePrecision.doubleConvert(mjtj/(mjtj-mjje),2,1))+"倍活动";
							}
						}
					}else{
						if ((calPop.popDef.str3 != null) && (calPop.popDef.str3.trim().length() > 0)){
							String[] row = calPop.popDef.str3.split(";");
							for (int c = 0; c < row.length; c++)
							{
								if (row[c] == null || row[c].split(",").length != 2) continue;

								double mjtj = Convert.toDouble(row[c].split(",")[0]); //满减条件
								double mjje = Convert.toDouble(row[c].split(",")[1]); //满减金额
								info = "1换"+String.valueOf(ManipulatePrecision.doubleConvert(mjtj/(mjtj-mjje),2,1))+"倍活动";
							}
						}
					}
					
					
					// 提示满减规则
					new MessageBox("参加活动的金额为 " + ManipulatePrecision.doubleToString(calPop.popje) + " 元\n\n"+info+"减现 "
							+ ManipulatePrecision.doubleToString(je) + " 元");
				}

				// 按百分比减现
				if (calPop.popDef.gz.equals("2"))
				{
					je = calPop.popje * calPop.popDef.poplsjzkl;
					//calPop.popDef.str5 = "B";
					if(calPop.popDef.str5.equals("B")){
						je = ManipulatePrecision.doubleConvert(je+0.09,1,0);
						//new MessageBox("金额："+je);
					}else{
						je = getDetailOverFlow(je, GlobalInfo.syjDef.sswrfs);
					}
					
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
					//popDef.str5 = "B";
					if (popDef.str5.equals("B"))
					{
						if (j == (calPop.row_set.size() - 1))
						{
							lszszk = ManipulatePrecision.doubleConvert(je - yfd, 2, 1);
							
						}
						else
						{
							lszszk = getDetailOverFlow((saleGoodsDef.hjje - getZZK(saleGoodsDef) - getftje(spinfo)) / hj * je, GlobalInfo.syjDef.sswrfs);
							//lszszk = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - getZZK(saleGoodsDef) - getftje(spinfo)) / hj * je, 2, 1);
						}
						
						if (lszszk <= 0) continue;
						if (!createMDPayment(lszszk))
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
	
	protected boolean createMDPayment(double lszszk)
	{
		try
		{
			
			String mdCode[] = GlobalInfo.sysPara.mdcode.split(",");
			if (mdCode[0].trim().equals(""))
			{
				new MessageBox("满抵参数未定义!\n无法计入满抵");
				return false;
			}
			PayModeDef paymode = DataService.getDefault().searchPayMode(mdCode[0]);
			if (paymode == null)
			{
				new MessageBox("[" + mdCode[0] + "]" + "付款方式未定义!\n无法计入满抵");
				return false;
			}
			
			// 付款记账

			//创建一个付款方式对象
			Payment pay = CreatePayment.getDefault().createPaymentByPayMode(paymode, saleEvent.saleBS);
			if (pay == null) {
				return false;
			}
			// inputPay这个方法根据不同的付款方式进行重写
			SalePayDef sp = pay.inputPay(String.valueOf(lszszk));
			
			if (pay.paymode.code.equals(mdCode[0]))
			{
				if (mdCode.length > 1) pay.salepay.idno = mdCode[1];
				//标记为自动生成
				//pay.salepay.str2 = "auto";
			}
//			标记为自动生成
			sp.str2 = "auto";
			// liwj add
			// 标记本行付款唯一序号,用于删除对应商品的分摊
			if (sp != null) sp.num5 = salePayUnique++;
			
			// 加入付款明细
			salePayment.add(sp);
			payAssistant.add(pay);
			
		}
		catch (Exception er)
		{
			er.printStackTrace();
			
			return false;
		}
		return true;
	}
	/*
	//判断满抵促销时   如果商品分摊了满抵金额的行数!=满抵生成的付款方式行数   或者  分摊的满抵金额!=满抵付款方式金额  或者 自动生成的付款方式!=满抵指定付款方式  不能成交
	public boolean checkFinalStatus()
	{
		double mdpayhjje = 0;         //满抵付款方式金额合计
		int  mdpaynum = 0;            //满抵付款方式行数
		
		double mdgoodshjje = 0;       //满抵商品金额合计
		int  mdgoodsnum = 0;          //满抵商品行数
		String mdCode[] = GlobalInfo.sysPara.mdcode.split(",");
		for(int i = 0;i < salePayment.size(); i++){
			SalePayDef spay = (SalePayDef) salePayment.elementAt(i);
			if(spay.str2!=null && spay.str2.equals("auto")&&!mdCode[0].equals(spay.paycode)){
				new MessageBox("本笔小票换倍付款异常,完成交易失败!");
				writeMDData();
				return false;
			}
			if(mdCode[0].equals(spay.paycode)&&spay.str2!=null && spay.str2.equals("auto")){
				mdpayhjje = ManipulatePrecision.doubleConvert(mdpayhjje +spay.je,2,1);
				mdpaynum++;
			}			
		}
		
		for(int j = 0 ; j < saleGoods.size(); j++){
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(j);
			//付款行号:付款代码:分摊金额,付款行号:付款代码:分摊金额
			if((saleGoodsDef.str2).indexOf(":"+mdCode[0]+":")!=-1){
				String ft[] = saleGoodsDef.str2.split(",");
				for(int a = 0;a<ft.length;a++){
					String ftDetail[] = ft[a].split("\\:");
					//分摊明细中付款代码匹配满抵付款代码
					if(ftDetail[1].equals(mdCode[0])){
						double je = Double.parseDouble(ftDetail[2]);
						mdgoodshjje = ManipulatePrecision.doubleConvert(mdgoodshjje +je,2,1);
						mdgoodsnum++;
					}
				}
			}
		}
		
		if((mdgoodsnum!=mdpaynum)||mdpayhjje!=mdgoodshjje) {
			new MessageBox("本笔小票换倍付款异常,完成交易失败!");
			writeMDData();
			return false;
		}
		writeMDData();
		return true;
	}
	*/
	public boolean checkIsSalePay(String code)
	{
		//百佳华在不指定小票退货时  需使用换倍折扣付款  在退货状态下不控制
		if (SellType.ISBACK(saleHead.djlb)){
			return false;
		}
		String mdCode[] = GlobalInfo.sysPara.mdcode.split(",");
		if(code.equals(mdCode[0])){
			return true;
		}else{
			return false;
		}
		
	}
	/*
	public boolean writeMDData()
    {
    	FileOutputStream f = null;
    	
        try
        {
        	String date = GlobalInfo.balanceDate.replaceAll("/","");
        	ManipulateDateTime mt = new ManipulateDateTime();
            String name = ConfigClass.LocalDBPath + "bakLog/"+ date +"/MDData_"+mt.getTimeByEmpty()+"_"+saleHead.fphm+".dat";
          //  PrintWriter pw = null;

    		String dir = name.substring(0, name.lastIndexOf('/'));// 取出目录
			File file = new File(dir);
			if (!file.exists()) //目录不存在，则创建相应的目录
			file.mkdirs();
            
            
	        f = new FileOutputStream(name);
	        ObjectOutputStream s = new ObjectOutputStream(f);
	        
	        // 多写一个当前工号
	        s.writeObject(GlobalInfo.posLogin.gh);
	        
	        // 将交易对象写入对象文件
			brokenAssistant.removeAllElements();
	        writeSellObjectToStream(s);

	        // 多写付款对象
//			s.writeObject(salePayment);
			
	        s.flush();
	        s.close();
	        f.close();
	        s = null;
	        f = null;
	        
	       // AccessDayDB.getDefault().writeWorkLog("写入断电保护文件", StatusType.WORK_WRITEBRODATA);     
	        
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
	            if (f != null) f.close();
        	}
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
	*/
	
	/*
	public void delSalePayObject(int index)
    {
		//SalePayDef pay = (SalePayDef) salePayment.elementAt(index);
		//PosLog.getLog(getClass()).info("======================================================");
		
		//PosLog.getLog(getClass()).info("小票号:"+saleHead.fphm +" 删除付款方式pay.paycode:"+pay.paycode+" "+pay.payname+" "+pay.je+" "+pay.flag+" "+pay.rowno+" "+pay.num5);
		
        // 先删除商品的付款分摊
        paymentApportionDelete(index);
        
        // 删除付款明细
    	salePayment.removeElementAt(index);
		payAssistant.removeElementAt(index);
		
    }
    */
	
	public boolean deleteAllSalePay()
	{
		int mdnum = 0;
		// 删除所有付款方式
		for (int i = 0; i < salePayment.size(); i++)
		{
			SalePayDef spay = (SalePayDef) salePayment.elementAt(i);
			String mdCode[] = GlobalInfo.sysPara.mdcode.split(",");
			if(mdCode.length>0 && spay.paycode.equals(mdCode[0])){
				mdnum++;
				continue;
			}
			if (!deleteSalePay(i))
			{
				return false;
			}
			else
			{
				i--;
			}
		}
		
		if(mdnum>0){
			for (int i = 0; i < salePayment.size(); i++)
			{
				if (!deleteSalePay(i))
				{
					return false;
				}
				else
				{
					i--;
				}
			}
		}

		// 删除所有扣回的付款,用信用卡支付扣回时,取消所有付款也得取消扣回
		if (!deleteAllSaleRefund())
			return false;

		return true;
	}
}
