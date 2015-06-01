package com.efuture.javaPos.Payment;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.R5CouponDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class PaymentR5Coupon extends PaymentCoupon
{
	R5CouponDef ret = null;

	public PaymentR5Coupon()
	{

	}

	public PaymentR5Coupon(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public PaymentR5Coupon(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public boolean isAllowPay(String type,double money)
	{
//		if (ret.paymoney <= 0)
//		{
		if (money <= 0)
		{
			new MessageBox(Language.apply("付款金额必须大于0"));

			return false;
		}
		
		if (money > ret.oldmoney)
		{
			new MessageBox(Language.apply("付款金额 "+ money +"大于券支付额" + ret.oldmoney + "元"));

			return false;
		}
			if (ret.amount <= 0)
				return true;

			
			//没有设置最低消费的情况
			if(ret.paymoney == 0.0)
			{
//				计算已经用了该类型券金额
				double totalQuan = getAlreadyPayquan(ret.type);
				
//				当前可用券金额
				double currentpay = ManipulatePrecision.doubleConvert(ret.amount - totalQuan);
				
				if (ManipulatePrecision.doubleConvert(totalQuan+money) > ret.amount)
				{
					new MessageBox("此类型券最多只允许支付" + ret.amount + "元");
					return false;
				}
				
				return true;
			}
			
			if(GlobalInfo.sysPara.isDouble != 'Y')
			{
				double totalQuan = getAlreadyPayquan(type);
				if (ManipulatePrecision.doubleConvert(totalQuan+money) > ret.amount)
				{
					new MessageBox("此类型券最多只允许支付" + ret.amount + "元");
					return false;
				}
				
			}
			else
			{
//				没有设置支付方式的这个条件的情况（只要应收金额满足就行）
				if(ret.paytype == null || ret.paytype.length() <= 0)
				{
					double totalQuan = getAlreadyPayquan(type);
					
					if(ret.FanQuanTotal >= ret.paymoney)
					{
//						计算当前该类型券可用张数
						long num = (long)ManipulatePrecision.doubleConvert((ret.FanQuanTotal/ret.paymoney), 0, 0);
						//计算总可用券金额
						double keyongquan = num * ret.amount;
						
//						当前可用券金额
						double currentpay = ManipulatePrecision.doubleConvert(keyongquan - totalQuan);
						
						if (money > currentpay )
						{
							new MessageBox("此类型券目前最多只允许支付" + currentpay + "元");
							return false;
						}
					}
					else
						new MessageBox("此笔小票不能用券支付；");	
				}
				else
				{
					double totalQuan = getAlreadyPayquan(type);
					double totalMoney = getAlreadyPaymoney();
					
//					计算当前该类型券可用张数
					long num = (long)ManipulatePrecision.doubleConvert((((totalMoney<ret.FanQuanTotal)?totalMoney:ret.FanQuanTotal)/ret.paymoney), 0, 0);
					//计算可用券金额
					double keyongquan = num * ret.amount;
					

					if (ManipulatePrecision.doubleConvert(totalQuan+money) > keyongquan)
					{
						new MessageBox("此类型券最多只允许支付" + keyongquan + "元");
						return false;
					}
				}
			}

			return true;
//		}

		/*if (saleBS.calcPayBalance() < ret.paymoney)
		{
			new MessageBox("该券未达到满" + ret.paymoney + "元可用" + ret.amount + "元券的条件");
			return false;
		}

		if (saleBS.calcPayBalance() - (getUseCount(type) * ret.paymoney) < ret.paymoney)
		{
			new MessageBox("该券当前已不符合满额用券规则");
			return false;
		}*/
//		return true;
	}

	private int getUseCount(String type)
	{
		int count = 0;
		SalePayDef saledef = null;
		for (int i = 0; i < saleBS.salePayment.size(); i++)
		{
			saledef = (SalePayDef) saleBS.salePayment.elementAt(i);

			if (saledef == null)
				continue;

			if (!saledef.str4.split(",")[0].equals(type))
				continue;

			count++;
		}
		return count;
	}

	private boolean checkExistSameType(String type)
	{
//		折扣券
		if(ret.flag == 4 && isPayzkq(String.valueOf(ret.flag)))
		{
			new MessageBox("无法支付\n同一张小票只允许使用一张折扣券!");
			return true;
		}
		
		
		SalePayDef saledef = null;
		for (int i = 0; i < saleBS.salePayment.size(); i++)
		{
			saledef = (SalePayDef) saleBS.salePayment.elementAt(i);

			if (saledef == null)
				continue;

			if (!saledef.str4.split(",")[0].equals(type) && saledef.str4.split(",")[0].length() > 0)
			{
				new MessageBox("无法支付\n同一张小票只允许使用一种类型的券!");
				return true;
			}
		}
		return false;

	}

	private boolean checkExistSamePay(String payno)
	{
		SalePayDef saledef = null;
		for (int i = 0; i < saleBS.salePayment.size(); i++)
		{
			saledef = (SalePayDef) saleBS.salePayment.elementAt(i);

			if (saledef == null)
				continue;

			if (saledef.payno.equals(payno))
			{
				new MessageBox("已存在" + payno + ",请删除后重新付款!");
				return true;
			}
		}
		return false;
	}

	public boolean checkPayRule(String type,double money)
	{
		if (ret == null)
			return false;
		
		if(ret.flag == 4)
		{
			if(ret.paymoney == 0.0)
			{
				//不受限制
				return true;
			}
			else
			{
				if(ret.FanQuanTotal < ret.paymoney)
				{
					new MessageBox("该券未达到满" + ret.paymoney + "元可用" + ret.oldmoney + "元折扣券的条件");
					return false;
				}else
					return true;
			}
		}
		
		
//		没有设置最低消费的情况
		if(ret.paymoney == 0.0)
		{
//			计算已经用了该类型券金额
			double totalQuan = getAlreadyPayquan(ret.type);
			
//			当前可用券金额
			double currentpay = ManipulatePrecision.doubleConvert(ret.amount - totalQuan);
			
			if (ManipulatePrecision.doubleConvert(totalQuan+money) > ret.amount)
			{
				new MessageBox("本笔小票最多可用券:" + ret.amount + "元");
				return false;
			}
			
			return true;
		}
		
		if(GlobalInfo.sysPara.isDouble != 'Y')
		{
//			 付款方式限定为空时，表示不控制
			if (ret.paytype == null || ret.paytype.equals(""))
			{
				if(ret.FanQuanTotal < ret.paymoney)
				{
					new MessageBox("该券未达到满" + ret.paymoney + "元可用" + ret.amount + "元券的条件");
				}else
					return true;
			}
			else
			{
				double totalQuan = getAlreadyPayquan(type);
				double totalMoney = getAlreadyPaymoney();
				if(((totalMoney<ret.FanQuanTotal)?totalMoney:ret.FanQuanTotal) < ret.paymoney)
				{
					new MessageBox("该券未达到满" + ret.paymoney + "元可用" + ret.amount + "元券的条件");
					return false;
				}
				else if(ManipulatePrecision.doubleConvert(totalQuan+money) > ret.amount)
				{
//					new MessageBox("该券不满足付款限定规则,无法使用!");
					new MessageBox("本笔小票最多可用券:" + ret.amount + "元");
					return false;
				}
			}
			
		}
		else
		{
//			 付款方式限定为空时，表示不控制
			if (ret.paytype == null || ret.paytype.equals(""))
			{
				double totalQuan = getAlreadyPayquan(type);
//				double totalMoney = getAlreadyPaymoney();

				//计算当前该类型券可用张数
				long num = (long)ManipulatePrecision.doubleConvert((ret.FanQuanTotal/ret.paymoney), 0, 0);
				//计算可用券金额
				double keyongquan = num * ret.amount;
				
				if(ret.FanQuanTotal < ret.paymoney)
				{
					new MessageBox("该券未达到满" + ret.paymoney + "元可用" + ret.amount + "元券的条件");
					return false;
				}
				else if(ManipulatePrecision.doubleConvert(totalQuan+money) > keyongquan)
				{
//					new MessageBox("该券不满足付款限定规则,无法使用!");
					new MessageBox("本笔小票最多可用券:" + keyongquan + "元");
					return false;
				}
			}
			else
			{
				double totalQuan = getAlreadyPayquan(type);
				double totalMoney = getAlreadyPaymoney();

				//计算当前该类型券可用张数
				long num = (long)ManipulatePrecision.doubleConvert((((totalMoney<ret.FanQuanTotal)?totalMoney:ret.FanQuanTotal)/ret.paymoney), 0, 0);
				//计算可用券金额
				double keyongquan = num * ret.amount;
				
				
				if(totalMoney < ret.paymoney)
				{
					new MessageBox("该券未达到满" + ret.paymoney + "元可用" + ret.amount + "元券的条件");
					return false;
				}
				else if(ManipulatePrecision.doubleConvert(totalQuan+money) > keyongquan)
				{
//					new MessageBox("该券不满足付款限定规则,无法使用!");
					new MessageBox("本笔小票最多可用券:" + keyongquan + "元");
					return false;
				}
			}
		}
		
		return true;
	}

	public SalePayDef inputPay(String money)
	{
		try
		{
			// 退货小票不能使用,退货扣回按销售算
			if (checkMzkIsBackMoney() && (GlobalInfo.sysPara.thmzk != 'Y'))
			{
				new MessageBox(Language.apply("退货时不能使用") + paymode.name, null, false);

				return null;
			}

			// 先检查是否有冲正未发送
			if (!sendAccountCz()) { return null; }

			// 是否通过外部设备读取卡号
			if (!autoFindCard())
				return null;

			// 打开明细输入窗口
			new PaymentCouponForm().open(this, saleBS);

			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;
	}

	/*
	 * public void initPayment(PayModeDef mode, SaleBS sale)
	 * {
	 * super.initPayment(mode, sale);
	 * }
	 */

	public int getAccountInputMode()
	{
		return TextBox.AllInput;
	}

	public String getDisplayCardno()
	{
		return ret.couponno;
	}

	public String getValidJe(int index)
	{
//		折扣券
		if(ret.flag == 4)
		{
			String line = "本笔小票参与用券商品总金额为:" + String.valueOf(ret.FanQuanTotal) + "\n折扣总金额为:" + String.valueOf(ret.oldmoney);
			return line;
		}
		else
			return "可支付金额:" + String.valueOf(allowpayje);
	}

	public String getDefaultMoney()
	{
		return String.valueOf(allowpayje);
	}
	
	public boolean CreateNewjPayment(int index, double money, StringBuffer bufferStr)
	{
		if (ret == null)
			return false;
		
		if (money <= 0)
		{
			new MessageBox(Language.apply("付款金额必须大于0"));

			return false;
		}
		
		if (money > ret.oldmoney)
		{
			new MessageBox(Language.apply("付款金额 "+ money +"大于券余额" + ret.oldmoney + "元"));

			return false;
		}

		if (checkExistSamePay(ret.couponno))
			return false;
		
//		 控制不允许同类型券多次付款
		if (checkExistSameType(ret.type))
			return false;
		
		if (!checkPayRule(ret.type,money))
			return false;

		if (!isAllowPay(ret.type,money))
			return false;

		if (super.createSalePayObject(money + ""))
		{
			salepay.payno = ret.couponno;
			// 记录券类型
			salepay.str4 = ret.type + "," + ret.flag;
			return true;
		}

		return false;

	}

	public boolean initList()
	{
		if (ret == null)
			return false;

		couponList.clear();
		couponList.removeAllElements();

		String[] row = new String[] { ret.couponno, ret.getCouponName(), String.valueOf(ret.oldmoney), "1" };
		couponList.add(row);
		
		
		//没有设置最低消费的情况
		if(ret.paymoney == 0.0)
		{
			//折扣券
			if(ret.flag == 4)
			{
				if(isPayzkq(String.valueOf(ret.flag)))
				{
					this.allowpayje = 0.0;
				}
				else
					this.allowpayje = ret.oldmoney;
			}
			else
			{
//				计算已经用了该类型券金额
				double totalQuan = getAlreadyPayquan(ret.type);
				
//				当前可用券金额
				double currentpay = ManipulatePrecision.doubleConvert(ret.amount - totalQuan);
				
				if (ret.oldmoney < currentpay)
					this.allowpayje = ret.oldmoney;
				else
					this.allowpayje = currentpay;
			}
			
			return true;
		}
		
//		折扣券
		if(ret.flag == 4)
		{
			if (ret.FanQuanTotal >= ret.paymoney)
				this.allowpayje = ret.oldmoney;
			else
				this.allowpayje = 0.0;
			return true;
		}
		

		if(GlobalInfo.sysPara.isDouble != 'Y')
		{
//			没有设置支付方式的这个条件的情况（只要应收金额满足就行）
			if(ret.paytype == null || ret.paytype.length() <= 0)
			{
//				计算已经用了该类型券金额
				double totalQuan = getAlreadyPayquan(ret.type);
				
				if(ret.FanQuanTotal >= ret.paymoney)
				{
//					当前可用券金额
					double currentpay = ManipulatePrecision.doubleConvert(ret.amount - totalQuan);
					
					if (ret.oldmoney < currentpay)
						this.allowpayje = ret.oldmoney;
					else
						this.allowpayje = currentpay;
				}
				else
				{
					this.allowpayje = 0.0;
				}
				
			}
			else
			{
				if(ret.FanQuanTotal >= ret.paymoney)
				{
					double totalQuan = getAlreadyPayquan(ret.type);
					if(totalQuan < ret.amount)
					{
						if (ret.money < ret.amount)
							this.allowpayje = ret.money;
						else
							this.allowpayje = ManipulatePrecision.doubleConvert(ret.amount - totalQuan);
					}
					else
						this.allowpayje = 0.0;
				}
				else
				{
					this.allowpayje = 0.0;
				}
				
			}
		}
		else
		{
			//没有设置支付方式的这个条件的情况（只要应收金额满足就行）
			if(ret.paytype == null || ret.paytype.length() <= 0)
			{
				if(ret.FanQuanTotal >= ret.paymoney)
				{
//					计算已经用了该类型券金额
					double totalQuan = getAlreadyPayquan(ret.type);
					
//					计算当前该类型券可用张数
					long num = (long)ManipulatePrecision.doubleConvert((ret.FanQuanTotal/ret.paymoney), 0, 0);
					//计算总可用券金额
					double keyongquan = num * ret.amount;
					
//					当前可用券金额
					double currentpay = ManipulatePrecision.doubleConvert(keyongquan - totalQuan);
					
					if (ret.oldmoney < currentpay)
						this.allowpayje = ret.oldmoney;
					else
						this.allowpayje = currentpay;
				}
				else
				{
					this.allowpayje = 0.0;
				}
				
			}
//			设置了支付方式的这个条件的情况
			else
			{
//				计算已经用了该类型券金额
				double totalQuan = getAlreadyPayquan(ret.type);
//				计算已经支付的除券外的总金额
				double totalMoney = getAlreadyPaymoney();
				if(ret.FanQuanTotal >= ret.paymoney)
				{
//					计算当前该类型券可用张数
					long num = (long)ManipulatePrecision.doubleConvert((((totalMoney<ret.FanQuanTotal)?totalMoney:ret.FanQuanTotal)/ret.paymoney), 0, 0);
					//计算总可用券金额
					double keyongquan = num * ret.amount;
					
					double currentpay = ManipulatePrecision.doubleConvert(keyongquan - totalQuan);
					
					if (ret.oldmoney < currentpay)
						this.allowpayje = ret.oldmoney;
					else
						this.allowpayje = currentpay;
				}
				else
				{
					this.allowpayje = 0.0;
				}
			}
		}


		return true;
	}

	public void specialDeal(PaymentCouponEvent event)
	{
		Table table = event.table;
		TableColumn[] column = table.getColumns();
		for (int i = 0; i < column.length; i++)
		{
			if (i == 0)
				column[0].setText(Language.apply("纸券名称"));
			if (i == 1)
				column[1].setText(Language.apply("纸券余额"));
			if (i == 2)
				column[2].setText(Language.apply("纸券付款"));
		}
	}

	public boolean findR5Fjk(String track1, String track2, String track3)
	{
		ProgressBox box = null;
		try
		{
			ret = new R5CouponDef();

			box = new ProgressBox();
			box.setText("正在预上传小票...");
			if (NetService.getDefault().sendSaleBill(saleBS.saleHead, saleBS.saleGoods) != 0)
			{
				new MessageBox("预上传小票,无法计算用券!");
				return false;
			}

			box.setText("正在计算用券...");
			if (NetService.getDefault().getR5SaleCoupon(track2, paymode.code, ret))
			{
				ret.couponno = mzkreq.track2;
				
				//折扣券
				if(ret.flag == 4)
				{
					ret.oldmoney = ManipulatePrecision.doubleConvert(ret.FanQuanTotal - ret.money);
					
					ret.amount = ret.oldmoney;
				}
				return true;
			}

			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (box != null)
				box.close();
		}
	}

	public boolean findFjk(String track1, String track2, String track3)
	{
		if ((track1.trim().length() <= 0) && (track2.trim().length() <= 0) && (track3.trim().length() <= 0)) { return false; }

		// 解析磁道
		String[] s = parseFjkTrack(track1, track2, track3);

		if (s == null) { return false; }

		track1 = s[0];
		track2 = s[1];
		track3 = s[2];

		// 设置查询条件
		setRequestDataByFind(track1, track2, track3);

		return findR5Fjk(track1, track2, track3);
		/*
		 * if (mzkreq.type.equals("05"))
		 * {
		 * 
		 * }
		 * else
		 * {
		 * // 发送查询交易
		 * return DataService.getDefault().sendFjkSale(mzkreq, mzkret);
		 * }
		 */
	}

	public boolean findFjkInfo(String track1, String track2, String track3, ArrayList fjklist)
	{
		if ((track1.trim().length() <= 0) && (track2.trim().length() <= 0) && (track3.trim().length() <= 0)) { return false; }

		// 解析磁道
		String[] s = parseFjkTrack(track1, track2, track3);

		if (s == null) { return false; }

		track1 = s[0];
		track2 = s[1];
		track3 = s[2];

		//
		setRequestDataByFind(track1, track2, track3);

		return DataService.getDefault().getFjkInfo(mzkreq, fjklist);
	}

	public boolean checkMzkMoneyValid()
	{
		if (!super.checkMzkMoneyValid()) { return false; }

		if ((CouponType == 2) && !checkMzkIsBackMoney())
		{
			// 券必须一次付完,输入金额和可收金额
			if (ManipulatePrecision.doubleCompare(salepay.ybje, this.getAccountYe(), 2) != 0)
			{
				// if (new MessageBox(salepay.payname +
				// "的每张券必须一次性付完!\n是否将剩余部分计入损溢？", null, true).verify() ==
				// GlobalVar.Key1)
				if (new MessageBox(Language.apply("{0}的每张券必须一次性付完!\n是否将剩余部分计入损溢？", new Object[] { salepay.payname }), null, true).verify() == GlobalVar.Key1)
				{
					// num1记录券付款溢余部分
					// salepay.num1 =
					// ManipulatePrecision.sub(ManipulatePrecision.mul(Double.parseDouble(this.getAccountYe()),
					// salepay.hl), ManipulatePrecision.mul(salepay.ybje,
					// salepay.hl));
					salepay.num1 = ManipulatePrecision.sub(ManipulatePrecision.mul(this.getAccountYe(), salepay.hl), Math.min(salepay.je, this.saleBS.calcPayBalance()));
					salepay.ybje = this.getAccountYe();
					salepay.je = ManipulatePrecision.doubleConvert(salepay.ybje * salepay.hl, 2, 1);

					return true;
				}
				else
				{
					return false;
				}
			}
		}

		return true;
	}

	public int choicFjkType()
	{
		return -1;
	}

	// 分摊付款方式
	public boolean paymentApportion(SalePayDef spay, Payment payobj, boolean test)
	{/*
	 * // 退货时不进行分摊
	 * if (SellType.ISBACK(salehead.djlb)) { return true; }
	 * 
	 * // 开放分组
	 * Vector v = new Vector();
	 * 
	 * for (int z = 0; z < vi.size(); z++)
	 * {
	 * CalcRulePopDef calPop = (CalcRulePopDef) vi.elementAt(z);
	 * 
	 * // str3 模拟下此规则的已经分摊的金额
	 * if (test)
	 * {
	 * calPop.str3 = calPop.str2;
	 * }
	 * 
	 * // 得到本规则所有商品总共满收
	 * int num1 = ManipulatePrecision.integerDiv(calPop.popje,
	 * Convert.toDouble(calPop.catid));
	 * double cxje = ManipulatePrecision.doubleConvert(num1 *
	 * Convert.toDouble(calPop.str1));
	 * double yfje = 0;
	 * 
	 * for (int x = calPop.row_set.size() - 1; x >= 0; x--)
	 * {
	 * int i = Convert.toInt(((String[]) calPop.row_set.elementAt(x))[0]);
	 * GoodsDef goods = (GoodsDef) saleBS.goodsAssistant.elementAt(i);
	 * 
	 * // 计算每个商品的最大可收金额,第一个商品用减计算出最大可收
	 * SaleGoodsDef sgd = (SaleGoodsDef) saleBS.saleGoods.elementAt(i);
	 * double ksje = 0;
	 * ksje = ManipulatePrecision.doubleConvert((sgd.hjje - sgd.hjzk) /
	 * Convert.toDouble(calPop.catid) * Convert.toDouble(calPop.str1));
	 * 
	 * if (x == 0)
	 * {
	 * ksje = Math.max(ksje, ManipulatePrecision.sub(cxje, yfje));
	 * }
	 * else if (ksje > ManipulatePrecision.sub(cxje, yfje))
	 * {
	 * ksje = ManipulatePrecision.sub(cxje, yfje);
	 * }
	 * 
	 * yfje = ManipulatePrecision.add(yfje, ksje);
	 * 
	 * // 计算商品
	 * Object[] rows = { String.valueOf(i), String.valueOf(0),
	 * String.valueOf(goods.str4.split("\\|").length), calPop,
	 * String.valueOf(ksje) };
	 * int j = 0;
	 * 
	 * for (j = 0; j < v.size(); j++)
	 * {
	 * Object[] rows1 = (Object[]) v.elementAt(j);
	 * 
	 * if (Convert.toInt(rows1[2]) > Convert.toInt(rows[2]))
	 * {
	 * break;
	 * }
	 * 
	 * if (Convert.toInt(rows1[2]) == Convert.toInt(rows[2]))
	 * {
	 * if (Convert.toInt(rows1[0]) < Convert.toInt(rows[0]))
	 * {
	 * break;
	 * }
	 * }
	 * }
	 * 
	 * if (j < v.size())
	 * {
	 * v.add(j, rows);
	 * }
	 * else
	 * {
	 * v.add(rows);
	 * }
	 * }
	 * }
	 * 
	 * double syje = ManipulatePrecision.doubleConvert(spay.je - spay.num1);
	 * 
	 * for (int i = 0; i < v.size(); i++)
	 * {
	 * Object[] rows1 = (Object[]) v.elementAt(i);
	 * SpareInfoDef spinfo = (SpareInfoDef)
	 * saleBS.goodsSpare.elementAt(Convert.toInt(rows1[0]));
	 * CalcRulePopDef calPop = (CalcRulePopDef) rows1[3];
	 * 
	 * int oldpayindex = -1;
	 * if (test)
	 * oldpayindex = getpaymentIndex(paymode.code, mzkret.cardno, spay.idno);
	 * 
	 * double je1 = getValidValue(Convert.toInt(rows1[0]), oldpayindex); //
	 * 商品剩余未分摊金额
	 * 
	 * // 商品按金额比例计算分摊金额
	 * // SaleGoodsDef sgd = (SaleGoodsDef)
	 * // saleBS.saleGoods.elementAt(Convert.toInt(rows1[0]));
	 * double je2 = Convert.toDouble(rows1[4]);//
	 * ManipulatePrecision.doubleConvert((sgd.hjje
	 * // - sgd.hjzk) /
	 * // Convert.toDouble(calPop.catid)
	 * // *
	 * // Convert.toDouble(calPop.str1));
	 * 
	 * // 此规则最大能分摊金额
	 * int num1 = ManipulatePrecision.integerDiv(calPop.popje,
	 * Convert.toDouble(calPop.catid));
	 * double je3 = ManipulatePrecision.doubleConvert(num1 *
	 * Convert.toDouble(calPop.str1));
	 * 
	 * // 此规则最大能分摊金额 - 此规则已分摊金额
	 * double je4 = 0;
	 * 
	 * if (!test)
	 * {
	 * je4 = ManipulatePrecision.doubleConvert(je3 -
	 * Convert.toDouble(calPop.str2));
	 * }
	 * else
	 * {
	 * je4 = ManipulatePrecision.doubleConvert(je3 -
	 * Convert.toDouble(calPop.str3));
	 * }
	 * 
	 * // 计算是否存在相同的券付款 , 减去已付款的此券金额（不包含同卡的已付金额）
	 * double tqfk = getftje(spinfo, spay.paycode, spay.payno,
	 * spay.idno.charAt(0));
	 * double je5 = ManipulatePrecision.doubleConvert(je2 - tqfk);
	 * 
	 * // 比较商品最大能收金额和规则能收最大金额
	 * double je6 = Math.min(je4, je5);
	 * 
	 * // 比较余额和商品最大能收金额
	 * je6 = Math.min(je6, je1);
	 * 
	 * if (spinfo.payft == null)
	 * {
	 * spinfo.payft = new Vector();
	 * }
	 * 
	 * double spje = Math.min(syje, je6);
	 * 
	 * if (!test)
	 * {
	 * String[] ft = new String[] { String.valueOf(spay.num5), spay.paycode,
	 * spay.payname, String.valueOf(spje) };
	 * spinfo.payft.add(ft);
	 * calPop.str2 =
	 * ManipulatePrecision.doubleToString(Convert.toDouble(calPop.str2) + spje);
	 * }
	 * else
	 * {
	 * calPop.str3 =
	 * ManipulatePrecision.doubleToString(Convert.toDouble(calPop.str3) + spje);
	 * }
	 * 
	 * syje = ManipulatePrecision.doubleConvert(syje - spje);
	 * 
	 * if (syje <= 0)
	 * {
	 * break;
	 * }
	 * }
	 * 
	 * if (syje > 0)
	 * {
	 * spay.num1 = ManipulatePrecision.doubleConvert(syje + spay.num1);
	 * }
	 */
		return true;
	}

	public double getCouponJe(String paycode, String payno, String couponID, String hl, int oldpayindex)
	{
		if (GlobalInfo.sysPara.istsfq == 'Y')
			return saleBS.calcPayBalance();
		else
			return super.getCouponJe(paycode, payno, couponID, hl, oldpayindex);
	}

	/*
	 * public boolean initList()
	 * {
	 * try
	 * {
	 * couponList.clear();
	 * couponList.removeAllElements();
	 * 
	 * String[] lines = { mzkret.cardno, mzkret.cardname,
	 * String.valueOf(mzkret.ye), String.valueOf(mzkret.value1), "-1",
	 * mzkret.memo };
	 * couponList.add(lines);
	 * 
	 * return true;
	 * }
	 * catch (Exception er)
	 * {
	 * er.printStackTrace();
	 * return false;
	 * }
	 * }
	 */
//	获取已支付券金额
	public double getAlreadyPayquan(String type)
	{
		double totalQuan = 0.0;
		SalePayDef saledef = null;
		for (int i = 0; i < saleBS.salePayment.size(); i++)
		{
			saledef = (SalePayDef) saleBS.salePayment.elementAt(i);

			if (saledef == null)
				continue;

			if (!saledef.str4.split(",")[0].equals(type))
				continue;

			totalQuan = ManipulatePrecision.doubleConvert(totalQuan + saledef.je);
		}
		return totalQuan;
	}
	
//	获取已支付金额（券除外）
	public double getAlreadyPaymoney()
	{
		double totalMoney = 0.0;
		
		if(ret.paytype == null || ret.paytype.length() < 0)
		{
			return salehead.ysje;
		}
		
		String[] paycode = ret.paytype.split(",");
		for (int i = 0; i < paycode.length; i++)
		{
			for (int j = 0; j < saleBS.salePayment.size(); j++)
			{
				SalePayDef spay = (SalePayDef) saleBS.salePayment.get(j);
				PayModeDef pmd = DataService.getDefault().searchPayMode(spay.paycode);
				if (pmd == null)
					continue;
				
				if("".equals(paycode[i]))
					continue;
				
				// 根据类型判断
				if (pmd.type != paycode[i].charAt(0))
					continue;

				totalMoney = ManipulatePrecision.doubleConvert(totalMoney + spay.je);
			}
		}
		
		return totalMoney;
	}
	
//折扣券在一笔小票种只能使用一次
	public boolean isPayzkq(String flag)
	{
		boolean sign = false;
		for (int j = 0; j < saleBS.salePayment.size(); j++)
		{
			SalePayDef spay = (SalePayDef) saleBS.salePayment.get(j);
			if(spay.str4.split(",")[1].equals(flag))
			{
				sign = true;
			}
		}
		return sign;
	}
}
