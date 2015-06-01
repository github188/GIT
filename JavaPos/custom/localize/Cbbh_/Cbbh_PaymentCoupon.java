package custom.localize.Cbbh;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.ICCard;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentCoupon;
import com.efuture.javaPos.Struct.CalcRulePopDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

public class Cbbh_PaymentCoupon extends PaymentCoupon
{
	protected String autoTrack1 = null;
	protected String autoTrack2 = null;
	protected String autoTrack3 = null;

	public Cbbh_PaymentCoupon()
	{
	}

	public Cbbh_PaymentCoupon(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Cbbh_PaymentCoupon(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}
	
	public boolean CreateNewjPayment(int index, double money, StringBuffer bufferStr)
	{
		try
		{
			if (money <= 0)
			{
				new MessageBox(Language.apply("付款金额必须大于0"));

				return false;
			}

			Cbbh_PaymentCoupon cpf = new Cbbh_PaymentCoupon(paymode, saleBS);

			cpf.paymode = (PayModeDef) this.paymode.clone();
			cpf.salehead = this.salehead;
			cpf.saleBS = this.saleBS;
			cpf.couponList = this.couponList;

			cpf.mzkreq = (MzkRequestDef) mzkreq.clone();
			cpf.mzkret = (MzkResultDef) mzkret.clone();

			// ///////////////////// 创建新的付款明细对象
			// 设置券类型
			String[] rows = (String[]) couponList.elementAt(index);

			if (Convert.toInt(rows[5]) > 0)
			{
				cpf.CouponType = Convert.toInt(rows[5]);
			}

			cpf.mzkreq.memo = rows[0];
			cpf.mzkret.ye = Convert.toDouble(rows[2]);

			if ((GlobalInfo.sysPara.fjkkhhl != null) && (GlobalInfo.sysPara.fjkkhhl.length() > 0) && saleBS.isRefundStatus() && !SellType.ISCOUPON(saleBS.saletype))
			{
				String[] lines = null;
				if (GlobalInfo.sysPara.fjkkhhl.indexOf(";") >= 0)
					lines = GlobalInfo.sysPara.fjkkhhl.split(";");
				else if (GlobalInfo.sysPara.fjkkhhl.indexOf("|") >= 0)
					lines = GlobalInfo.sysPara.fjkkhhl.split("\\|");

				if (lines == null)
					lines = new String[] { GlobalInfo.sysPara.fjkkhhl };

				if (lines != null)
				{
					int i = 0;

					for (i = 0; i < lines.length; i++)
					{
						String l = lines[i];

						if (l.indexOf(",") > 0)
						{
							String cid = l.substring(0, l.indexOf(","));

							if (cid.equals(rows[0]))
							{
								cpf.paymode.hl = Convert.toDouble(l.substring(l.indexOf(",") + 1));

								break;
							}
						}
					}

					if (i >= lines.length)
					{
						cpf.paymode.hl = Convert.toDouble(rows[3]);
					}
				}
			}
			else
			{
				cpf.paymode.hl = Convert.toDouble(rows[3]);
			}
			cpf.allowpayje = this.allowpayje;

			// 查询并删除原付款
			// 如果是退货且非扣回时，不删除原付款方式
			if (!(SellType.ISBACK(salehead.djlb) && !saleBS.isRefundStatus()) || GlobalInfo.sysPara.isBackPaymentCover == 'Y')
			{
				if (!deletePayment(index, cpf))
				{
					new MessageBox(Language.apply("删除原付款方式失败！"));

					return false;
				}
			}
			/*
			if (!(SellType.ISBACK(salehead.djlb) && !saleBS.isRefundStatus()) && !deletePayment(index, cpf))
			{
				(GlobalInfo.sysPara.isBackPaymentCover == 'N')
				new MessageBox("删除原付款方式失败！");

				return false;
			}
			*/

			//新促销卡内现在只有返利劵，这里取消控制--maxun
			/*if (this.allowpayje >= 0 && money > this.allowpayje && paymode.isyy != 'Y')
			{
//				new MessageBox("该付款方式最多允许付款 " + ManipulatePrecision.doubleToString(allowpayje) + " 元");
				new MessageBox(Language.apply("该付款方式最多允许付款 {0} 元" ,new Object[]{ManipulatePrecision.doubleToString(allowpayje)}));

				return false;
			}*/

			double yy = 0;
			if (yyje > 0 && sjje > 0)
			{
				double min = Math.min(ManipulatePrecision.doubleConvert(sjje / cpf.paymode.hl), cpf.allowpayje);
				if (sjje > 0 && money > min)
				{
//					new MessageBox("最大可退金额为: " + min);
					new MessageBox(Language.apply("最大可退金额为: {0}" ,new Object[]{min+""}));
					return false;
				}

				if (GlobalInfo.sysPara.oldqpaydet == 'A')
				{
					StringBuffer buf = new StringBuffer();
					buf.append(ManipulatePrecision.doubleToString(money + (yyje / cpf.paymode.hl)));
					TextBox txt = new TextBox();
//					txt.open("请输入券面值", "券面值", "实际付款为:" + ManipulatePrecision.doubleToString(money) + "\n最大券面值为:" + ManipulatePrecision.doubleToString(money + (yyje / cpf.paymode.hl)), buf, 0, ManipulatePrecision.doubleConvert(money + (yyje / cpf.paymode.hl)), true, TextBox.DoubleInput, -1);
					txt.open(Language.apply("请输入券面值"), Language.apply("券面值"), Language.apply("实际付款为:{0}\n最大券面值为:{1}" ,new Object[]{ManipulatePrecision.doubleToString(money) ,ManipulatePrecision.doubleToString(money + (yyje / cpf.paymode.hl))}), buf, 0, ManipulatePrecision.doubleConvert(money + (yyje / cpf.paymode.hl)), true, TextBox.DoubleInput, -1);
					double yfk = money;
					money = Convert.toDouble(buf.toString());
					if (money > yfk)
						yy = ManipulatePrecision.doubleConvert(money - yfk);
				}
				else
				{
					StringBuffer buf = new StringBuffer();
					// buf.append(ManipulatePrecision.doubleToString((yyje/cpf.paymode.hl)));
					TextBox txt = new TextBox();
//					txt.open("请输入此券益余金额", "益余金额", "实际付款为:" + ManipulatePrecision.doubleToString(money) + "\n最大益余金额为:" + ManipulatePrecision.doubleToString((yyje / cpf.paymode.hl)), buf, 0, ManipulatePrecision.doubleConvert((yyje / cpf.paymode.hl)), true, TextBox.DoubleInput, -1);
					txt.open(Language.apply("请输入此券益余金额"), Language.apply("益余金额"), Language.apply("实际付款为:{0}\n最大益余金额为:{1}" ,new Object[]{ManipulatePrecision.doubleToString(money) ,ManipulatePrecision.doubleToString((yyje / cpf.paymode.hl))}), buf, 0, ManipulatePrecision.doubleConvert((yyje / cpf.paymode.hl)), true, TextBox.DoubleInput, -1);

					if (Convert.toDouble(buf.toString()) > 0)
						yy = Convert.toDouble(buf.toString());
				}
			}
			// 创建付款对象
			if (cpf.createSalePay(String.valueOf(money + yy)))
			{
				// 设置付款方式名称
				cpf.salepay.payname = rows[1];
				if (yy > 0)
					cpf.salepay.num1 = ManipulatePrecision.doubleConvert(yy * cpf.salepay.hl);

				// 增加已付款
				if (SellType.ISBACK(saleBS.saletype) && saleBS.isRefundStatus())
				{
					cpf.salepay.payname += Language.apply("扣回");
					saleBS.addSaleRefundObject(cpf.salepay, cpf);

				}
				else
				{
					saleBS.addSalePayObject(cpf.salepay, cpf);
				}

				alreadyAddSalePay = true;

				// 记录当前付款方式
				rows[4] = String.valueOf(cpf.salepay.num5);

				addMessage(cpf, bufferStr);

				// 开始分摊到各个商品
				paymentApportion(cpf.salepay, cpf, false);

				if (GlobalInfo.sysPara.oldqpaydet != 'N' && sjje > 0 && yyje > 0)
				{
					isCloseShell = true;
				}

				return true;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return false;
	}
	
	public boolean createSalePay(String money)
	{
		try
		{
			// 创建付款信息
			if (super.createSalePay(money))
			{
				// salepay对象有效
				if (checkMzkMoneyValid())
				{
					// 记录帐号信息
					if (saveFindMzkResultToSalePay())
					{
						// 显示余额提示
						showAccountYeMsg();

						// 需要即时记账
						if (realAccountPay())
							return true;
					}
				}
			}
		}
		catch (Exception ex)
		{
			new MessageBox(Language.apply("生成交易付款对象失败\n\n") + ex.getMessage());
			ex.printStackTrace();
		}

		//
		salepay = null;
		return false;
	}

	protected boolean saveFindMzkResultToSalePay()
	{
		String memo = mzkreq.memo;		
		if (!super.saveFindMzkResultToSalePay()) { return false; }
		mzkreq.memo = memo;
		salepay.idno = memo;
		return true;
	}
	

	// 查询可付金额
	public double getCouponJe(String paycode, String payno, String couponID, String hl, int oldpayindex)
	{

		CalcRulePopDef calPop = null;
		// 如果原付款行号为-1，查询对应的商品行号
		if (oldpayindex == -1)
		{
			oldpayindex = getpaymentIndex(paycode, payno, couponID);
		}

		if (vi == null)
		{
			vi = new Vector();
		}
		else
		{
			vi.removeAllElements();
		}
		// 行号，满金额，收金额，分组规则，剩余未付金额
		for (int i = 0; i < saleBS.goodsAssistant.size(); i++)
		{
			GoodsDef goods = (GoodsDef) saleBS.goodsAssistant.elementAt(i);
			SaleGoodsDef sgd = (SaleGoodsDef) saleBS.saleGoods.elementAt(i);
			SpareInfoDef spinfo = (SpareInfoDef) saleBS.goodsSpare.elementAt(i);

			if (goods.str4 != null && goods.str4.length() > 0)
			{				
				String line = "";
				String[] gz = goods.str4.split("\\|");
				for(int k=0; k<gz.length; k++)
				{
					String[] arr = gz[k].split(",");
					//是否存在此券
					if (arr.length > 1 && arr[0].trim().equals(couponID))
					{
						line = gz[k].trim();
						break;

					}
				}
				// 券ID，收券条件，收券金额， 活动单号，是否跨柜
				if (line.length()>0)//goods.str4.indexOf(couponID + ",") >= 0)
				{
					/*String line = goods.str4.substring(goods.str4.indexOf(couponID + ","));
					if (line.indexOf("|") >= 0)
						line = line.substring(0, line.indexOf("|"));
					*/
					

					String[] values = line.split(",");

					String isOverGz = "Y";
					if (values.length > 4)
						isOverGz = values[4];

					// 剩余可分摊金额
					double je = getValidValue(i, oldpayindex);

					// 相同券已分摊金额
					double je1 = getftje(spinfo, paycode, payno, couponID.charAt(0));

					// 此券可收金额
					je = ManipulatePrecision.doubleConvert(je + je1);
					if (je >= 0)
					{
						int j = 0;
						for (j = 0; j < vi.size(); j++)
						{
							boolean isMerge = false;
							calPop = (CalcRulePopDef) vi.elementAt(j);
							if (isOverGz.equals("Y"))
							{
								if (calPop.code.equals(values[3]) && calPop.catid.equals(values[1]) && calPop.str1.equals(values[2]))
								{
									isMerge = true;
								}
							}
							else
							{
								if (calPop.str4.equals(sgd.gz) && calPop.code.equals(values[3]) && calPop.catid.equals(values[1]) && calPop.str1.equals(values[2]))
								{
									isMerge = true;
								}
							}
							if (isMerge)
							{
								if (GlobalInfo.sysPara.couponRuleType == 'Y')
								{
									calPop.popje = ManipulatePrecision.doubleConvert(calPop.popje + sgd.hjje - saleBS.getZZK(sgd));
								}
								else
								{
									calPop.popje = ManipulatePrecision.doubleConvert(calPop.popje + je);
								}
								calPop.row_set.add(new String[] { String.valueOf(i), String.valueOf(je) });
								calPop.str2 = ManipulatePrecision.doubleToString((Convert.toDouble(calPop.str2) + je1));
								break;
							}
							// if (calPop.code.equals(values[3]) &&
							// calPop.catid.equals(values[1]) &&
							// calPop.str1.equals(values[2]))
							// {
							// if (GlobalInfo.sysPara.couponRuleType == 'Y')
							// {
							// calPop.popje =
							// ManipulatePrecision.doubleConvert(calPop.popje +
							// sgd.hjje - saleBS.getZZK(sgd));
							// }
							// else
							// {
							// calPop.popje =
							// ManipulatePrecision.doubleConvert(calPop.popje +
							// je);
							// }
							// calPop.row_set.add(new String[] {
							// String.valueOf(i), String.valueOf(je) });
							// calPop.str2 =
							// ManipulatePrecision.doubleToString((Convert.toDouble(calPop.str2)
							// + je1));
							// break;
							// }
						}

						if (j >= vi.size())
						{
							if (Convert.toDouble(values[1]) <= 0)
								continue;

							calPop = new CalcRulePopDef();
							// calPop.code = values[3]; // 活动单号
							// calPop.rulecode = values[0]; // 规则码
							// calPop.catid = values[1]; // 条件金额
							// calPop.str1 = values[2]; // 收券金额
							// calPop.str2 = String.valueOf(je1); //
							calPop.rulecode = values[0]; // 规则码
							calPop.catid = values[1]; // 条件金额
							calPop.str1 = values[2]; // 收券金额
							if (values.length > 3)
								calPop.code = values[3]; // 活动单号
							else 
								calPop.code="";
							if (values.length > 4)
								calPop.str3 = values[4]; // 是否跨柜统计标志
							else
								calPop.str3 = "Y"; // 默认跨柜
							calPop.str4 = sgd.gz; // 柜组

							if (GlobalInfo.sysPara.couponRuleType == 'Y')
							{
								calPop.popje = ManipulatePrecision.doubleConvert(calPop.popje + sgd.hjje - saleBS.getZZK(sgd));
							}
							else
							{
								calPop.popje = ManipulatePrecision.doubleConvert(calPop.popje + je);
							}
							calPop.row_set = new Vector();
							calPop.row_set.add(new String[] { String.valueOf(i), String.valueOf(je) });// 行数和金额
							vi.add(calPop);
						}
					}
				}
			}
		}

		// 计算满收金额
		double ksje = 0; // 可收金额
		// double syze = 0; // 剩余总额
		for (int i = 0; i < vi.size(); i++)
		{
			calPop = (CalcRulePopDef) vi.elementAt(i);

			// 计算商品合计
			double syje = 0;

			if (calPop.popje >= Convert.toDouble(calPop.catid))
			{
				for (int j = 0; j < calPop.row_set.size(); j++)
				{
					String[] row = (String[]) calPop.row_set.elementAt(j);
					syje += Convert.toDouble(row[1]);
				}

				int num = ManipulatePrecision.integerDiv(calPop.popje, Convert.toDouble(calPop.catid));
				double je1 = ManipulatePrecision.doubleConvert(num * Convert.toDouble(calPop.str1));
				ksje += Math.min(je1, syje);
			}
			else
			{
				vi.remove(i);
				i--;
			}
		}

		// 减去已付款的此券金额（不包含同卡的已付金额）
		double yfje = 0;

		for (int i = 0; i < saleBS.salePayment.size(); i++)
		{
			SalePayDef sp = (SalePayDef) saleBS.salePayment.elementAt(i);

			if (DataService.getDefault().searchPayMode(sp.paycode).type == '5')
			{
				// 不同类型付款方式
				if (!isSameTypePayment(sp))
				{
					continue;
				}

				// 同卡
				if (sp.paycode.equals(paycode) && sp.payno.equals(payno))
				{
					continue;
				}

				if (sp.idno.charAt(0) == couponID.charAt(0)) // 券种相同
				{
					yfje += ManipulatePrecision.doubleConvert(sp.je - sp.num1);
				}
			}
		}

		double maxkfje = ManipulatePrecision.doubleConvert(ksje - yfje);

		// 模拟分摊，查看是否存在损益
		SalePayDef sp = new SalePayDef();
		sp.je = maxkfje;
		sp.paycode = paycode;
		sp.payno = payno;
		sp.idno = couponID;

		if (paymentApportion(sp, null, true))
		{
			maxkfje = ManipulatePrecision.doubleConvert(maxkfje - sp.num1);
		}

		return maxkfje;
	}
	
//	 分摊付款方式
	public boolean paymentApportion(SalePayDef spay, Payment payobj, boolean test)
	{
		//小a劵不分摊
		if(spay.idno.equalsIgnoreCase("a") || vi == null)return true;
		
		return super.paymentApportion(spay,payobj,test);
	}
	
//	 查询可付金额
	public String getValidJe(int index)
	{
		if(true)return "";
		String[] rows = (String[]) couponList.elementAt(index);
		String line = "";
		// 退货时不记算最大能退金额
		if (ISSALE(salehead.djlb))
		{
			//换货劵金额随便输入，由后台判断给出最终可用劵金额
			if (SellType.HH_SALE.equals(salehead.djlb) || SellType.HH_BACK.equals(salehead.djlb))
			{
				allowpayje = Double.parseDouble(rows[2]);
			}
			else
			{
				allowpayje = getCouponJe(paymode.code, mzkret.cardno, rows[0], rows[3], Convert.toInt(rows[4]));
			}
			
			double hl = Convert.toDouble(rows[3]);
			// 总是进位到分,确保hl*原币一定是大于应收
			allowpayje = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(allowpayje, hl) + 0.009, 2, 0);
			double showprice = saleBS.getDetailOverFlow(allowpayje);
//			line = "可收" + rows[1] + "金额为： " + ManipulatePrecision.doubleToString(allowpayje);
			line = Language.apply("可收{0}金额为：{1}" ,new Object[]{rows[1] ,ManipulatePrecision.doubleToString(allowpayje)});
//			line += "\n汇率为 " + ManipulatePrecision.doubleToString(hl);
			line += Language.apply("\n汇率为 {0}" ,new Object[]{ManipulatePrecision.doubleToString(hl)});
			allowpayje = Math.max(allowpayje, showprice);
		}
		else
		{
			double hl = 1;
//			line = "汇率为 " + ManipulatePrecision.doubleToString(hl);
			line = Language.apply("汇率为 {0}" ,new Object[]{ManipulatePrecision.doubleToString(hl)});

			if (GlobalInfo.sysPara.fjkkhhl != null && GlobalInfo.sysPara.fjkkhhl.length() > 0 && saleBS.isRefundStatus() && !SellType.ISCOUPON(saleBS.saletype))
			{
				String[] lines = null;
				if (GlobalInfo.sysPara.fjkkhhl.indexOf(";") >= 0)
					lines = GlobalInfo.sysPara.fjkkhhl.split(";");
				else if (GlobalInfo.sysPara.fjkkhhl.indexOf("|") >= 0)
					lines = GlobalInfo.sysPara.fjkkhhl.split("\\|");

				if (lines == null)
					lines = new String[] { GlobalInfo.sysPara.fjkkhhl };
				int i = 0;
				for (i = 0; i < lines.length; i++)
				{
					String l = lines[i];
					if (l.indexOf(",") > 0)
					{
						String cid = l.substring(0, l.indexOf(","));
						if (cid.equals(rows[0]))
						{
							hl = Convert.toDouble(l.substring(l.indexOf(",") + 1));
							if (hl != 1)
								line = Language.apply("注意:此项请输入要扣回的积分数\n");
//							 	line += "汇率为 " + ManipulatePrecision.doubleToString(hl);
								line = Language.apply("汇率为 {0}" ,new Object[]{ManipulatePrecision.doubleToString(hl)});
							break;
						}
					}
				}
			}
			else
			{
				hl = Convert.toDouble(rows[3]);

//				line = "汇率为 " + rows[3];
				line = Language.apply("汇率为 {0}" ,new Object[]{rows[3]});
			}

			allowpayje = getBackCouponJe(paymode.code, mzkret.cardno, rows[0], String.valueOf(hl), Convert.toInt(rows[4]));

			if (sjje > 0)
			{
				allowpayje = Math.min(ManipulatePrecision.doubleConvert(sjje / Convert.toDouble(rows[3])), allowpayje);
			}

//			line += "\n剩余未付金额为:" + allowpayje;
			line += Language.apply("\n剩余未付金额为:{0}" ,new Object[]{allowpayje+""});
		}

		if (SellType.ISCOUPON(saleBS.saletype) && SellType.ISBACK(saleBS.saletype) && saleBS.isRefundStatus())
		{

			for (int i = 0; i < saleBS.refundlist.size(); i++)
			{
				String[] row = (String[]) saleBS.refundlist.elementAt(i);
				if (row[0].equals(rows[0]))
				{
//					line += "\n此券需扣回金额为 " + row[1] + " :" + row[2] + "\n";
					line += Language.apply("\n此券需扣回金额为 {0} :{1}\n" ,new Object[]{row[1] ,row[2]});
					break;
				}
			}
		}
		return line;
	}
	

	 public static boolean ISSALE(String c)
	    {
	        if (c .equals( SellType.RETAIL_SALE)) return true;
	        if (c .equals( SellType.BATCH_SALE)) return true;
	        if (c .equals( SellType.EARNEST_SALE)) return true;
	        if (c .equals( SellType.PREPARE_TAKE)) return true;
	        if (c .equals( SellType.PREPARE_SALE)) return true;
	        if (c .equals( SellType.PREPARE_SALE1)) return true;
	        if (c .equals( SellType.EXERCISE_SALE)) return true;
	        if (c .equals( SellType.PURCHANSE_COUPON)) return true;
	        if (c.equals(SellType.JS_FK)) return true;
	        if (c.equals(SellType.JF_FK)) return true;
	        if (c.equals(SellType.GROUPBUY_SALE)) return true;
	        if (c.equals(SellType.CARD_SALE)) return true;
	        if (c.equals(SellType.PURCHANSE_JF)) return true;
//	        if (c.equals(HH_SALE)) return true;
//	        if (c .equals( JDXX_BACK) return true;
	        
	        return false;
	    }
	
	 
	 public boolean autoFindCard()
		{
			ProgressBox pb = null;
			try
			{
				if(!isAutoFindCard())return true;
				
				this.autoTrack1 = null;
				this.autoTrack2 = null;
				this.autoTrack3 = null;
				
				//读取卡号
				pb = new ProgressBox();
				pb.setText("请在银联设备上刷卡...");
				String strTrack = ICCard.getDefault().findCard();
				PosLog.getLog(this.getClass().getSimpleName()).info("银联设备上刷卡 strTrack=[" + String.valueOf(strTrack) + "].");
				if(strTrack==null)
				{
					new MessageBox("从银联设备上读卡失败！");
					return false;
				}
				String[] arrTrack = strTrack.split(";");
				this.autoTrack1 = arrTrack[0].trim();
				if(arrTrack.length>1) this.autoTrack2 = arrTrack[1].trim();
				if(arrTrack.length>2) this.autoTrack3 = arrTrack[2].trim();
				return true;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			}
			finally
			{
				if(pb!=null) 
				{
					pb.close();
					pb=null;
				}
			}
			return false;
		}
		
		public boolean isAutoFindCard()
		{
			try
			{
				if(GlobalInfo.sysPara.isUseBankReadTrack=='Y') return true;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			return false;
		}

		public boolean findFjk(String track1, String track2, String track3)
		{
			if(isAutoFindCard())
			{
				track1=this.autoTrack1;
				track2=this.autoTrack2;
				track3=this.autoTrack3;
				PosLog.getLog(this.getClass().getSimpleName()).info("findFjk() track1=[" + String.valueOf(autoTrack1) + "],track2=[" + String.valueOf(autoTrack2) + "],track3=[" + String.valueOf(autoTrack3) + "].");
			}
			return super.findFjk(track1, track2, track3);
		}
		
}
