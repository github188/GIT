package custom.localize.Tygc;


import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.PrintTemplate.CheckGoodsMode;
import com.efuture.javaPos.Struct.CalcRulePopDef;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.SalePayForm;

import custom.localize.Cmls.Cmls_SaleBS;
import custom.localize.Tygc.Tygc_SaleBS;
import custom.localize.Tygc.ReprintDef;

public class Tygc_SaleBS extends Cmls_SaleBS
{
	public void addSaleGoodsObject(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		super.addSaleGoodsObject(sg, goods, info);
		if(goods!=null){
//			tu测试
			sg.str6 = goods.str5;
			//sg.str6 = "5";
		}
		
	}
	public void execCustomKey2(boolean keydownonsale)
	{
		if (GlobalInfo.syjDef.isprint == 'Y' && Printer.getDefault() != null )
		{
			if( Printer.getDefault().getStatus()){
				Printer.getDefault().close();
				new MessageBox("已断开打印机设备!");
			}else{
				Printer.getDefault().open();
				Printer.getDefault().setEnable(true);
				new MessageBox("已连接打印机设备!");
			}
		}
	}
//	会员授权
	public boolean memberGrant()
	{
//		 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (saleGoods.size() > 0 && !memberAfterGoodsMode() && !isNewUseSpecifyTicketBack(false))
		{
			new MessageBox("必须在输入商品前进行刷会员卡\n请把商品清除后再重刷卡");
			return false;
		}

		if (isPreTakeStatus())
		{
			new MessageBox("预售提货状态下不允许重新刷卡");
			return false;
		}

		// 会员卡必须在商品输入前,则输入了商品以后不能刷卡,指定小票除外
		if (GlobalInfo.sysPara.customvsgoods == 'A' && saleGoods.size() > 0 && !isNewUseSpecifyTicketBack(false))
		{
			new MessageBox("必须在输入商品前进行刷会员卡\n\n请把商品清除后再重刷卡");
			return false;
		}

		// 读取会员卡
		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
		String track2 = bs.readMemberCard();
		if (track2 == null || track2.equals("")) return false;

		// 查找会员卡
		CustomerDef cust = bs.findMemberCard(track2);

		if (cust == null) return false;

		// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (isNewUseSpecifyTicketBack(false))
		{
			// 指定小票退仅记录卡号,不执行商品重算等处理
			curCustomer = cust;
			saleHead.hykh = cust.code;
			saleHead.hytype = cust.type;
			saleHead.str4 = cust.valstr2;
			saleHead.hymaxdate = cust.maxdate;
			return true;
		}
		else
		{
			// 记录会员卡
			return memberGrantFinish(cust);
		}
	}
	
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
				if (calPop.popDef.str5 == null || calPop.popDef.str5.trim().length() <= 0) calPop.popDef.str5 = "A";//若为空,则默认为A(满减) tujie add by 2015.4.10
				
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
							
							if(bcje>0) calPop.mult_Amount = bcje;
							
							
						}
						if(calPop.mult_Amount<=0){
							set.remove(i);
							i--;
						}
					}
					//D表示满折
				}else if(calPop.popDef.str5.equals("D")){

					// 检查是否满足条件
					if (calPop.popDef.gz.equals("1")) // 按金额满减
					{
						double mjje = 0;
						calPop.popje = sphj;

					//	int num = 0;

						// 已参与满减的金额
						double yfmj = 0;

						
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
									double t = Convert.toDouble(row[c].split(",")[2]); //满折条件
									//double je = Convert.toDouble(row[c].split(",")[3]); //满减金额
									double zk = Convert.toDouble(row[c].split(",")[3]); //满折折扣
									if ((zk == 0)|| (zk == 1)|| (b == 0))
									{
										continue;
									}

									if ((ManipulatePrecision.doubleConvert(sphj - yfmj) >= a) && (ManipulatePrecision.doubleConvert(sphj - yfmj) <= b))
									{
										double zkje = ManipulatePrecision.doubleConvert((sphj - yfmj)*(1-zk));
										// 如果满减条件为0，直接取定义的满减金额
										if (t == 0)
										{
											
											if (zkje < limitje)
											{
												mjje = zkje;
											}
											else
											{
												mjje = limitje;
											}

											break;
										}

										//浮点运算1       = 0.999999,需要进位到两位小数再取整
										//浮点运算299/300 = 0.996666,进位取整=1,还需再乘分母用金额比较，如果大倍数要减1
//										num = ManipulatePrecision.integerDiv(sphj - yfmj, t);
//
//										double bcje = 0;
//										if (num > 0)
//										{
//											bcje = num * zk;
//										}
//
										double bcje = zkje;
										if (bcje > limitje)
										{
											bcje = limitje;
										}

										mjje += bcje;
										//yfmj = ManipulatePrecision.doubleConvert(num * t + yfmj);
										yfmj = ManipulatePrecision.doubleConvert(mjje);
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

					saleHead.str2 += calPop.popDef.kssj + "\n" + "买换：" + Convert.increaseChar(String.valueOf(je), 8) + "(" + line1 + ")\n";

					// 提示满减规则
					new MessageBox("参加活动的金额为 " + ManipulatePrecision.doubleToString(calPop.popje) + " 元\n\n买换 "
							+ ManipulatePrecision.doubleToString(je) + " 元");
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
	
						saleHead.str2 += calPop.popDef.kssj + "\n" + "满减：" + Convert.increaseChar(String.valueOf(je), 8) + "(" + line1 + ")\n";
	
						// 提示满减规则
						new MessageBox("参加活动的金额为 " + ManipulatePrecision.doubleToString(calPop.popje) + " 元\n\n减现 "
								+ ManipulatePrecision.doubleToString(je) + " 元");
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
	
						saleHead.str2 += calPop.popDef.kssj + "\n" + "满减：" + Convert.increaseChar(String.valueOf(je), 8) + "(" + line1 + ")\n";
	
						// 提示满减规则
						new MessageBox("现有促销减现 " + ManipulatePrecision.doubleToString(calPop.popDef.poplsjzkl * 100) + "%\n\n你目前可参加活动的金额为 "
								+ ManipulatePrecision.doubleToString(calPop.popje) + " 元\n\n你目前可以减现 " + ManipulatePrecision.doubleToString(je) + " 元");
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
							lszszk = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - getZZK(saleGoodsDef) - getftje(spinfo)) / hj * je, 2, 1);
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
					else if (popDef.str5.equals("A")||popDef.str5.equals("D"))
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
	

	
//	 重新打印上一张小票
	public void rePrint()
	{
		ResultSet rs = null;
		SaleHeadDef saleheadprint = null;
		Vector salegoodsprint = null;
		Vector salepayprint = null;

		// 盘点
		if (SellType.ISCHECKINPUT(saletype))
		{
			if (saleGoods == null || saleGoods.size() <= 0)
				return;

			if (!CheckGoodsMode.getDefault().isLoad())
				return;

			MessageBox me = new MessageBox(Language.apply("你确实要打印盘点小票吗?"), null, true);

			if (me.verify() != GlobalVar.Key1)
				return;

			CheckGoodsMode.getDefault().setTemplateObject(saleHead, saleGoods, salePayment);

			CheckGoodsMode.getDefault().printBill();

			return;
		}

		if (GlobalInfo.syjDef.printfs == '1' && saleGoods != null && saleGoods.size() > 0)
		{
			new MessageBox(Language.apply("当前打印为即扫即打并且已有商品交易,不能重打!"), null, false);

			return;
		}

		// 检查发票是否打印完,打印完未设置新发票号则不能交易
		if (Printer.getDefault().getSaleFphmComplate()) { return; }

		MessageBox me = new MessageBox(Language.apply("你确实要重印上一张小票吗?"), null, true);
		try
		{
			if (me.verify() == GlobalVar.Key1 && getReprintAuth())
			{
				Object obj = null;
				String fphm = null;
				OperUserDef user = null;  
				if (curGrant.privdy != 'Y' && curGrant.privdy != 'L')
				{
					
					if ((user = DataService.getDefault().personGrant(Language.apply("授权重打印小票"))) != null)
					{
						if (user.privdy != 'Y' && user.privdy != 'L')
						{
							new MessageBox(Language.apply("当前工号没有重打上笔小票权限!"));

							return;
						}

						String log = Language.apply("授权重打印上一笔小票,授权工号:") + user.gh;
						AccessDayDB.getDefault().writeWorkLog(log);
					}
					else
					{
						return;
					}
				}


				if ((obj = GlobalInfo.dayDB.selectOneData("select max(fphm) from salehead where syjh = '" + ConfigClass.CashRegisterCode + "'")) != null)
				{
					try
					{
						fphm = String.valueOf(obj);

						if ((rs = GlobalInfo.dayDB.selectData("select * from salehead where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " + fphm)) != null)
						{

							if (!rs.next())
							{
								new MessageBox(Language.apply("没有查询到小票头,不能打印!"));
								return;
							}

							saleheadprint = new SaleHeadDef();

							if (!GlobalInfo.dayDB.getResultSetToObject(saleheadprint)) { return; }
						}
						else
						{
							new MessageBox(Language.apply("查询小票头失败!"), null, false);
							return;
						}
					}
					catch (Exception ex)
					{
						new MessageBox(Language.apply("查询小票头出现异常!"), null, false);
						ex.printStackTrace();
						return;
					}
					finally
					{
						GlobalInfo.dayDB.resultSetClose();
					}
					if(user!=null)saleheadprint.str9 = user.gh;//临时记录授权工号
					try
					{
						if ((rs = GlobalInfo.dayDB.selectData("select * from SALEGOODS where syjh = '" + ConfigClass.CashRegisterCode + "' and fphm = " + fphm + " order by rowno")) != null)
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
								new MessageBox(Language.apply("没有查询到小票明细,不能打印!"));
								return;
							}
						}
						else
						{
							new MessageBox(Language.apply("查询小票明细失败!"), null, false);
							return;
						}
					}
					catch (Exception ex)
					{
						new MessageBox(Language.apply("查询小票明细出现异常!"), null, false);
						ex.printStackTrace();
						return;
					}
					finally
					{
						GlobalInfo.dayDB.resultSetClose();
					}

					try
					{
						if ((rs = GlobalInfo.dayDB.selectData("select * from SALEPAY where syjh = '" + ConfigClass.CashRegisterCode + "' and fphm = " + fphm + " order by rowno")) != null)
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
								new MessageBox(Language.apply("没有查询到付款明细,不能打印!"));
								return;
							}
						}
						else
						{
							new MessageBox(Language.apply("查询付款明细失败!"), null, false);
							return;
						}
					}
					catch (Exception ex)
					{
						new MessageBox(Language.apply("查询付款明细出现异常!"), null, false);
						ex.printStackTrace();
						return;
					}
					finally
					{
						GlobalInfo.dayDB.resultSetClose();
					}

					saleheadprint.printnum++;
					AccessDayDB.getDefault().updatePrintNum(saleheadprint.syjh, String.valueOf(saleheadprint.fphm), String.valueOf(saleheadprint.printnum));
					ProgressBox pb = new ProgressBox();
					pb.setText(Language.apply("现在正在重打印小票,请等待....."));
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
					new MessageBox(Language.apply("当前没有销售数据,不能打印!"));
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
	
	
	SaleHeadDef tempsalehead = null;
	static String[] b = null;
	
//	public void printSaleTicket(SaleHeadDef vsalehead, Vector vsalegoods, Vector vsalepay, boolean isRed)
//	{
//		String type = "SalePrintMode.ini";
//		if (vsalehead != null && vsalehead.djlb != null)
//			type = vsalehead.djlb;
//
//		Vector tempsalegoods = null;
//		Vector tempsalepay = null;
//
//		try
//		{
//			tempsalehead = SaleBillMode.getDefault(type).getSalehead();
//			tempsalegoods = SaleBillMode.getDefault(type).getSalegoods();
//			tempsalepay = SaleBillMode.getDefault(type).getSalepay();
//
//			// 联网获取赠送打印清单
//			DataService dataservice = (DataService) DataService.getDefault();
//			Vector gifts = dataservice.getSaleTicketMSInfo(vsalehead, vsalegoods, vsalepay);
//			SaleBillMode.getDefault(type).setSaleTicketMSInfo(vsalehead, gifts);
//
//			// 检查是否需要重打印赠品联授权
//			boolean bok = true;
//			
//			
//			if (vsalehead.printnum > 0 && SaleBillMode.getDefault(type).needMSInfoPrintGrant())
//			{
//				if (GlobalInfo.posLogin.priv.charAt(1) != 'Y')
//				{
//					OperUserDef staff = DataService.getDefault().personGrant(Language.apply("重打印赠券授权"));
//
//					if (staff == null || staff.priv.charAt(1) != 'Y')
//					{
//						new MessageBox(Language.apply("此交易存在赠券或者赠品\n该审批员无重打印赠品或者赠券权限"));
//						bok = false;
//					}
//				}
//			}
//			
//	 		if(!getReprint(vsalehead)) return ;
//			
//			if (!bok)
//			{
//				SaleBillMode.getDefault(type).setSaleTicketMSInfo(vsalehead, null);
//			}
//
//			if (vsalehead != null && vsalegoods != null && vsalepay != null)
//			{
//				SaleBillMode.getDefault(type).setTemplateObject(vsalehead, vsalegoods, vsalepay);
//				SaleBillMode.getDefault(type).printBill();
//				
//				
//				if(vsalehead.printnum > 0)
//				{
//					String[] s = Tygc_SaleBS.getResult();   //上传字段
//
//					s[7] = String.valueOf(Printer.getDefault().getCurrentSaleFphm()-1);
//					new Tygc_NetService().postReprint(s);
//				}
//				
//			
//			}
//			else
//			{
//				new MessageBox(Language.apply("未发现小票对象，不能打印\n或\n打印模版读取失败"));
//			}
//		}
//		catch (Exception ex)
//		{
//			ex.printStackTrace();
//		}
//		finally
//		{
//			SaleBillMode.getDefault(type).setTemplateObject(tempsalehead, tempsalegoods, tempsalepay);
//		}
//	}
//	
	
	
	protected boolean getReprint(SaleHeadDef vsalehead)
	{
//		从本地查找重打原因
		ResultSet rs = null;
		ReprintDef print = null;
		try{			
			if ((rs = GlobalInfo.localDB.selectData("select IWID,IWMEMO,IWSTATUS from REPRINT")) != null)
			{
				if (!rs.next())
				{
					new MessageBox(Language.apply("没有查询到小票打印原因!"));
					return false;
				}

				String code = "";
				String text = "";
				String log = "";
				String printType = "2";              //发票打印类型  1正常销售 2重打
				
				String startfph = String.valueOf(Printer.getDefault().getCurrentSaleFphm());
				String usedfphnum = "";
				
				Vector reprint = new Vector();
				String[] title =  { "打印原因ID" ,"原因说明" ,"是否启用(Y/N)"};
				int[] width = { 150, 250 ,150};

				do{
					print = new ReprintDef();
					
					if (!GlobalInfo.localDB.getResultSetToObject(print)) { return false; }
					
					reprint.add(new String[] {String.valueOf(print.IWID), print.IWMEMO, print.IWSTATUS});
				}while(rs.next());
				String gh = "";
				if(vsalehead.str9 == null || vsalehead.str9.equals(""))  gh = GlobalInfo.posLogin.gh;		
				else gh = vsalehead.str9;
				int choice = new MutiSelectForm().open("请选择打印原因", title, width, reprint, true);
				if (choice == -1)
				{
					new MessageBox(Language.apply("没有选择打印原因"));
					log = "收银机号:" + ConfigClass.CashRegisterCode + ",小票号:" + vsalehead.fphm + ",发票打印类型:" + printType + ",打印原因:" + "0" + "没有选打印原因" + ",授权工号:"+ gh ;
					return false;
				}else {
					String[] row = (String[]) (reprint.elementAt(choice));
					code = row[0].toString();
					text = row[2].toString();
					log = "收银机号:" + ConfigClass.CashRegisterCode + ",小票号:" + vsalehead.fphm + ",发票打印类型:" + printType + ",打印原因:" + code + ",授权工号:" + gh;
				}
				AccessDayDB.getDefault().writeWorkLog(log,"1234");
				
				
				String[] s = {GlobalInfo.sysPara.mktcode, ConfigClass.CashRegisterCode ,String.valueOf(vsalehead.fphm) ,printType ,code ,gh ,startfph, usedfphnum, ManipulateDateTime.getDateTimeByClock(),"0"};
				b = s;
				
				return true;
			}
			else
			{
				new MessageBox(Language.apply("没有查询到小票打印原因!"), null, false);
				return false;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			new MessageBox(Language.apply("获取打印原因列表异常"));
			return false;
		}		
	}
	
	public static String[] getResult()
	{
		return b;
	}

}
