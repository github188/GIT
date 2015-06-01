package custom.localize.Jplm;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

public class Jplm_PaymentCoupon extends PaymentMzk
{
	protected static Vector coupons = new Vector();
	private double yfcoupon = 0.0; // 当前一共要收多少券
	private double alreadycoupon = 0.0;// 目前已付款多少券

	// 当前是否有券可用
	private boolean useabledcoupon = false;

	public void initVetor()
	{
		coupons.removeAllElements();
		coupons.clear();
	}

	public void clearPayItem()
	{
		for (int i = 0; i < saleBS.goodsSpare.size(); i++)
		{
			SpareInfoDef spinfo = (SpareInfoDef) saleBS.goodsSpare.elementAt(i);

			if (spinfo == null || spinfo.payft == null)
				continue;

			for (int j = 0; j < spinfo.payft.size(); j++)
			{
				String[] item = (String[]) spinfo.payft.get(j);
				if (item[0].equals("0508"))
				{
					spinfo.payft.remove(j);
					j--;
				}
			}
		}
	}

	public Jplm_PaymentCoupon()
	{

	}

	public Jplm_PaymentCoupon(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Jplm_PaymentCoupon(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public boolean cancelPay()
	{
		clearPayItem();
		return true;
	}

	public SalePayDef inputPay(String money)
	{
		try
		{
			// 退货小票不能使用,退货扣回按销售算
			if (checkMzkIsBackMoney())
			{
				new MessageBox("退货时不能使用" + paymode.name, null, false);

				return null;
			}

			// 先检查是否有冲正未发送
			if (!sendAccountCz())
				return null;

			if (checkExistSamePay())
				return null;

			this.yfcoupon = saleBS.calcPayBalance();
			alreadycoupon = 0.0;

			// 打开明细输入窗口
			//new Jplm_PaymentCouponForm().open(this, saleBS);

			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	private boolean checkExistSamePay()
	{
		SalePayDef saledef = null;
		for (int i = 0; i < saleBS.salePayment.size(); i++)
		{
			saledef = (SalePayDef) saleBS.salePayment.elementAt(i);

			if (saledef == null)
				continue;

			if (saledef.paycode.equals("0508"))
			{
				if (paymode != null && paymode.code.equals("0508"))
				{
					new MessageBox("已存在0508券付款,请删除后重新付款");
					return true;
				}
			}
		}
		return false;
	}

	public boolean getUseabledcoupon()
	{
		return this.useabledcoupon;
	}

	public Vector getCoupons()
	{
		return coupons;
	}

	public double getCouponMoney(int index)
	{
		if (coupons == null || coupons.size() == 0 || index < 0 || index > coupons.size() - 1)
			return 0.0;

		return ((Jplm_CouponDef) coupons.elementAt(index)).money;
	}

	public void setCouponEnablemoney(int index, double money)
	{
		try
		{
			if (coupons == null || coupons.size() == 0 || index < 0 || index > coupons.size() - 1)
				return;

			Jplm_CouponDef coupon = (Jplm_CouponDef) coupons.elementAt(index);

			if (coupon == null)
				return;

			if (money > 0)
				coupon.enablemoney = money;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public boolean apportionNonCommonCouponToSameTypeGoods(Jplm_CouponDef coupon)
	{
		try
		{
			SalePayDef spay = null;

			// 查找此类型券对应的付款对象
			for (int a = 0; a < saleBS.salePayment.size(); a++)
			{
				SalePayDef tmpPay = (SalePayDef) saleBS.salePayment.get(a);
				if (tmpPay.str2.equals(coupon.type))
				{
					spay = tmpPay;
					break;
				}
			}

			if (spay == null)
				return false;

			if (Jplm_CouponDef.goodsindex == null || Jplm_CouponDef.goodsindex.indexOf(",") < 0)
				return false;

			String[] strIndex = coupon.goodsindex.split(",");

			SaleGoodsDef sgd = null;
			SpareInfoDef spinfo = null;

			for (int i = 0; i < coupons.size(); i++)
			{
				Jplm_CouponDef tmpCoupon = (Jplm_CouponDef) coupons.get(i);

				for (int j = 0; j < strIndex.length; j++)
				{
					// 没有分摊的商品则直接跳出
					if (strIndex[j] == null)
						break;

					int index = Convert.toInt(strIndex[j]);

					sgd = (SaleGoodsDef) saleBS.saleGoods.elementAt(index);
					spinfo = (SpareInfoDef) saleBS.goodsSpare.elementAt(index);

					if (spinfo.payft == null)
						spinfo.payft = new Vector();

					double ftje = ManipulatePrecision.doubleConvert((sgd.hjje - sgd.hjzk) / Jplm_CouponDef.totalmoney * (tmpCoupon.amount - tmpCoupon.excep), 2, 1);

					String[] ftinfo = new String[] { spay.paycode, tmpCoupon.cardno, String.valueOf(ftje) };
					spinfo.payft.add(ftinfo);
					System.out.println(sgd.barcode + " 非通收券分摊卡号及金额:" + tmpCoupon.cardno + ":" + ftje);
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.out.println(ex.getMessage());
			return false;
		}

	}

	// 单个商品的分摊
	private double getGoodsApprotion(SaleGoodsDef sgd, SpareInfoDef spinfo)
	{
		double totalftje = 0.0;

		try
		{
			for (int b = 0; b < spinfo.payft.size(); b++)
			{
				String[] ftinfo = (String[]) spinfo.payft.get(b);
				totalftje += Convert.toDouble(ftinfo[2]);
			}

			System.out.println(sgd.barcode + " 券分摊金额:" + totalftje);
			return totalftje;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return totalftje;
		}
	}

	// 多个商品的分摊
	private double getTotalApportion()
	{
		double totalftje = 0.0;

		try
		{
			SaleGoodsDef sgd = null;
			SpareInfoDef spinfo = null;

			for (int j = 0; j < saleBS.saleGoods.size(); j++)
			{
				sgd = (SaleGoodsDef) saleBS.saleGoods.elementAt(j);
				spinfo = (SpareInfoDef) saleBS.goodsSpare.elementAt(j);

				System.out.println(j + 1 + ":" + sgd.barcode + "商品金额:" + (sgd.hjje - sgd.hjzk));

				if (spinfo.payft == null)
				{
					if (sgd != null)
						totalftje = totalftje + (sgd.hjje - sgd.hjzk);

					System.out.println(j + 1 + ":" + sgd.barcode + "分摊金额:" + 0);

					continue;
				}

				double tmpftje = 0.0;

				for (int b = 0; b < spinfo.payft.size(); b++)
				{
					String[] ftinfo = (String[]) spinfo.payft.get(b);
					tmpftje += Convert.toDouble(ftinfo[2]);
				}

				System.out.println(j + 1 + ":" + sgd.barcode + "分摊金额:" + tmpftje);

				totalftje = totalftje + (sgd.hjje - sgd.hjzk - tmpftje);
			}

			System.out.println("整单扣除分摊金额共余:" + totalftje);
			return totalftje;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return totalftje;
		}
	}

	public boolean createCouponPay()
	{
		if (!procNonCommonCoupon())
			return false;

		return true;
	}

	// 根据金额对同类型券进行排序 flag保留字段，方便后期排序
	protected void sortSameTypeCouponByMoney(Jplm_CouponDef coupon, boolean flag)
	{
		try
		{
			if (coupons.size() < 1)
			{
				coupons.add(coupon);
				// addCouponAndSumry(coupon);
				return;
			}

			for (int i = 0; i < coupons.size(); i++)
			{
				Jplm_CouponDef tmpCoupon = (Jplm_CouponDef) coupons.get(i);
				if (coupon.type.equals(tmpCoupon.type))
				{
					if (coupon.money >= tmpCoupon.money)
					{
						coupons.insertElementAt(coupon, (i == 0 ? 0 : i));
						return;
					}
					else
					{
						if (i == coupons.size() - 1)
						{
							coupons.add(coupon);
							return;
						}

						if (!((Jplm_CouponDef) coupons.get(i + 1)).type.equals(coupon.type))
						{
							coupons.insertElementAt(coupon, i + 1);
							return;
						}
					}
				}
			}

			coupons.add(coupon);
			// addCouponAndSumry(coupon);

			return;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.out.println(ex.getMessage());
			return;
		}
	}

	// 得到券最大可用信息
	public String getCouponTypeMoneyInfo(int index)
	{
		String couponInfo = "无用券信息";
		boolean isFind = false;

		Jplm_CouponDef coupon = (Jplm_CouponDef) coupons.get(index);

		if (coupon == null)
			return couponInfo;

		if (!isFind)
			return couponInfo;

		couponInfo = couponInfo + "券类型" + coupon.type + "满" + coupon.cardinalnumber + "元可用" + coupon.availablemoney + "元券,最多可用" + coupon.mostmoney + "元券\n";
		couponInfo = couponInfo + "整单可用" + coupon.type + "类型券累计金额为" + coupon.totalmoney + "元\n";

		int availabelTimes = ManipulatePrecision.integerDiv(coupon.totalmoney, coupon.cardinalnumber);

		if (availabelTimes < 1)
			return couponInfo = couponInfo + "整单可用" + coupon.type + "券额为0元";

		// 计算出总的可用券额
		double availableAmount = ManipulatePrecision.doubleConvert(availabelTimes * coupon.availablemoney, 2, 1);
		couponInfo = couponInfo + "整单可用" + coupon.type + "券额为" + Math.min(coupon.money, Math.min(availableAmount, coupon.mostmoney)) + "元";

		return couponInfo;
	}

	// 根据收券类型对券相同的商品进行汇总
	public boolean sumryGoodsByNonCommonCouponType()
	{
		try
		{
			for (int i = 0; i < coupons.size(); i++)
			{
				Jplm_CouponDef tmpCoupon = (Jplm_CouponDef) coupons.get(i);

				for (int j = 0; j < saleBS.saleGoods.size(); j++)
				{
					SaleGoodsDef sgd = (SaleGoodsDef) saleBS.saleGoods.elementAt(j);
					double money = 0;
					double ysje = 0;

					// 全场
					if (tmpCoupon.type.equals("0") && tmpCoupon.condition.equals("ALL"))
					{
						ysje = ManipulatePrecision.doubleConvert(sgd.hjje - sgd.hjzk, 2, 1);
						Jplm_CouponDef.totalmoney = ManipulatePrecision.doubleConvert(Jplm_CouponDef.totalmoney + ysje, 2, 1);

						if (ysje > money)
						{
							Jplm_CouponDef.maxmoneyindex = j;
							money = ysje;
						}

						if (Jplm_CouponDef.goodsindex == null)
							Jplm_CouponDef.goodsindex = "";

						Jplm_CouponDef.goodsindex += j + ",";

					}
					// 品牌
					else if (tmpCoupon.type.equals("1") && tmpCoupon.condition.equals(sgd.ppcode))
					{
						ysje = ManipulatePrecision.doubleConvert(sgd.hjje - sgd.hjzk, 2, 1);
						Jplm_CouponDef.totalmoney = ManipulatePrecision.doubleConvert(Jplm_CouponDef.totalmoney + ysje, 2, 1);

						if (ysje > money)
						{
							Jplm_CouponDef.maxmoneyindex = j;
							money = ysje;
						}

						if (Jplm_CouponDef.goodsindex == null)
							Jplm_CouponDef.goodsindex = "";

						Jplm_CouponDef.goodsindex += j + ",";

					}
					// 品类
					else if (tmpCoupon.type.equals("2") && tmpCoupon.condition.equals(sgd.catid))
					{
						ysje = ManipulatePrecision.doubleConvert(sgd.hjje - sgd.hjzk, 2, 1);
						Jplm_CouponDef.totalmoney = ManipulatePrecision.doubleConvert(Jplm_CouponDef.totalmoney + ysje, 2, 1);

						if (ysje > money)
						{
							Jplm_CouponDef.maxmoneyindex = j;
							money = ysje;
						}

						if (Jplm_CouponDef.goodsindex == null)
							Jplm_CouponDef.goodsindex = "";

						Jplm_CouponDef.goodsindex += j + ",";

					}
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.out.println(ex.getMessage());
			return false;
		}
	}

	// 先处理非通收券
	protected boolean procNonCommonCoupon()
	{
		// 1.按非通收券类型汇总商品收券
		if (!sumryGoodsByNonCommonCouponType())
		{
			new MessageBox("券付款失败\n汇总非全场券失败");
			return false;
		}

		// 2.计算非通收券
		if (!calcNonCommonCoupon())
		{
			new MessageBox("券付款失败\n计算非全场券失败");
			return false;
		}

		// 3.根据券类型创建付款
		if (!createNonCommonCouponPay())
		{
			new MessageBox("券付款失败\n创建非全场券付款失败");
			return false;
		}
		return true;
	}

	// 传进来的是NonCommon或Common
	private boolean calcCoupon(Jplm_CouponDef coupon)
	{
		try
		{
			// 当前总付款券金额若大于或等于salebs传过来的金额时，就不能再对余下的券进行扣款了
			if (ManipulatePrecision.doubleConvert(alreadycoupon, 2, 1) >= yfcoupon)
				return true;

			// 券置为已使用
			coupon.isused = true;

			int availabelTimes = ManipulatePrecision.integerDiv(Jplm_CouponDef.totalmoney, coupon.cardinalnumber);

			// 小于1，不具备用券条件
			if (availabelTimes < 1)
				return false;

			// 计算出总的可用券额
			double availableAmount = ManipulatePrecision.doubleConvert(availabelTimes * coupon.availablemoney, 2, 1);
			availableAmount = Math.min(availableAmount, coupon.mostmoney); // 在最大可用券额与最多可用金额之中取小
			availableAmount = Math.min(availableAmount, yfcoupon - alreadycoupon);

			// 标识当笔有券可用
			useabledcoupon = true;

			// 将计券面值金额清0
			coupon.money = 0.0;

			// 对券进行扣款标识
			for (int j = 0; j < coupons.size(); j++)
			{
				Jplm_CouponDef payCoupon = (Jplm_CouponDef) coupons.get(j);

				// 先计算溢余
				payCoupon.excep = ManipulatePrecision.doubleConvert(payCoupon.money - payCoupon.enablemoney, 2, 1);
				coupon.excep += payCoupon.excep;

				// 当券不够付款时，继续利用下一张
				if (coupon.amount + payCoupon.enablemoney < availableAmount)
				{
					payCoupon.isused = true;
					payCoupon.amount = payCoupon.enablemoney;
					coupon.amount += payCoupon.enablemoney;
					coupon.money += payCoupon.money;

					continue;
				}
				// 当券付款与可用券额相等时
				else if (coupon.amount + payCoupon.enablemoney == availableAmount)
				{
					payCoupon.isused = true;
					payCoupon.amount = payCoupon.enablemoney;
					coupon.amount += payCoupon.enablemoney;
					coupon.money += payCoupon.money;

					break;
				}
				else
				{
					payCoupon.isused = true;
					payCoupon.amount = payCoupon.enablemoney;

					// 存在损溢
					if (payCoupon.enablemoney > (availableAmount - coupon.amount))
					{
						double exceptionmoney = (payCoupon.enablemoney - (availableAmount - coupon.amount));
						payCoupon.excep += exceptionmoney;
						coupon.excep += exceptionmoney;
					}

					coupon.amount += payCoupon.enablemoney;
					coupon.money += payCoupon.money;

					break;
				}
			}

			alreadycoupon += (coupon.money - coupon.excep);
			coupon.minlimitmoney = ManipulatePrecision.doubleConvert(ManipulatePrecision.integerDiv(coupon.money - coupon.excep, coupon.availablemoney) * coupon.cardinalnumber, 2, 1);

			// 如果修改后的券金额反算后为0，那么将其默认的限制金额设置成券规则金额
			if (coupon.minlimitmoney == 0)
				coupon.minlimitmoney = coupon.availablemoney;

			// 记录扣券金额及各券的损溢
			coupon.paycardno = getUsedCouponInfo(0); // card1,card2,...
			coupon.paycardexcep = getUsedCouponInfo(1); // je1,je2,....

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.out.println(ex.getMessage());
			return false;
		}
	}

	// 计算非通收券
	public boolean calcNonCommonCoupon()
	{
		try
		{
			for (int i = 0; i < coupons.size(); i++)
			{
				Jplm_CouponDef nonCommonCoupon = (Jplm_CouponDef) coupons.get(i);
				calcCoupon(nonCommonCoupon);
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.out.println(ex.getMessage());
			return false;
		}
	}

	public boolean checkMzkMoneyValid()
	{
		// 由于是一次性处理扣款，所以不用检查余额
		return true;
	}

	protected boolean saveFindMzkResultToSalePay()
	{
		salepay.batch = "";
		// salepay.payno = mzkret.cardno;
		salepay.kye = 0;

		return true;
	}

	public void showAccountYeMsg()
	{

	}

	public boolean addCouponSalePay(Jplm_CouponDef coupon)
	{
		try
		{
			Jplm_PaymentCoupon pay = new Jplm_PaymentCoupon(paymode, saleBS);
			pay.paymode = (PayModeDef) this.paymode.clone();
			pay.salehead = this.salehead;
			pay.saleBS = this.saleBS;

			pay.mzkreq = (MzkRequestDef) mzkreq.clone();
			pay.mzkret = (MzkResultDef) mzkret.clone();

			if (pay.createSalePay(String.valueOf(coupon.money)))
			{
				// 设置付款方式名称
				pay.salepay.payname = coupon.type + "券";

				// 记录下
				// pay.salepay.str1 = coupon.type + "#" +
				// String.valueOf(coupon.minlimitmoney) + "#" +
				// coupon.limitpaycode;

				// 记录券类型，方便分摊
				pay.salepay.str2 = coupon.type;
				// pay.salepay.str3用到基类换货标志了故不能使用
				pay.salepay.str4 = coupon.paycardno;
				pay.salepay.str5 = coupon.paycardexcep;

				pay.salepay.memo = coupon.memo;

				// 记录用券信息，方便打印
				// pay.salepay.bankno = getUsedCouponInfo(coupon.type, 2);

				if (coupon.excep > 0)
					pay.salepay.num1 = ManipulatePrecision.doubleConvert(coupon.excep * pay.salepay.hl);

				// 增加已付款
				saleBS.addSalePayObject(pay.salepay, pay);
				alreadyAddSalePay = true;
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.out.println(ex.getMessage());
			return false;
		}
	}

	public boolean mzkAccount(boolean isAccount)
	{
		// 如果batch有值且payno也有值，证明已经交易成功，则不在发送交易请求
		if (salepay.batch != null && salepay.batch.trim().length() > 0 && salepay.payno != null && salepay.payno.trim().length() > 0)
			return true;

		return super.mzkAccount(isAccount);
	}

	// 保存交易数据进行交易
	protected boolean setRequestDataByAccount()
	{
		// 得到消费序号
		long seqno = getMzkSeqno();
		if (seqno <= 0)
			return false;

		// 打消费交易包
		mzkreq.seqno = seqno;
		mzkreq.syjh = ConfigClass.CashRegisterCode;
		mzkreq.mktcode = GlobalInfo.sysPara.mktcode;
		mzkreq.syyh = GlobalInfo.posLogin.gh;
		mzkreq.paycode = salepay.paycode;
		mzkreq.invdjlb = ((salehead != null) ? salehead.djlb : "");
		mzkreq.track1 = "";

		mzkreq.je = salepay.ybje - salepay.num1; // 券金额减去损溢
		mzkreq.track2 = salepay.str4;
		mzkreq.track3 = salepay.str5;

		return true;
	}

	public boolean collectAccountPay()
	{
		if (super.collectAccountPay())
			return deleteMzkCz();

		return false;
	}

	// 找同类型的且已使用的券
	private String getUsedCouponInfo(int oprtype)
	{
		String line = "";

		for (int i = 0; i < coupons.size(); i++)
		{
			Jplm_CouponDef coupon = (Jplm_CouponDef) coupons.get(i);

			if (coupon.isused)
			{
				if (oprtype == 0)
					line += coupon.cardno + ",";
				else if (oprtype == 1)
					line += String.valueOf(coupon.excep) + ",";
				else
					line = String.valueOf(coupon.money) + "|" + coupon.cardno + ",";
			}
		}
		if (line.length() > 0)
			line = line.substring(0, line.length() - 1);

		System.out.println(line);

		return line;
	}

	protected boolean createNonCommonCouponPay()
	{
		for (int i = 0; i < coupons.size(); i++)
		{
			Jplm_CouponDef coupon = (Jplm_CouponDef) coupons.get(i);

			// 未使用的券不创建付款对象
			if (!coupon.isused)
				continue;

			if (coupon.amount <= 0)
				continue;

			if (addCouponSalePay(coupon))
				apportionNonCommonCouponToSameTypeGoods(coupon);
		}
		return true;
	}

	protected String getDisplayAccountInfo()
	{
		return "请 刷 卡";
	}

	public boolean unNeedFindFjkDone(String je)
	{
		return true;
	}

	public boolean needFindFjk(String track1, String track2, String track3)
	{
		return true;
	}

	public boolean findFjk(String track1, String track2, String track3)
	{
		if ((track1.trim().length() <= 0) && (track2.trim().length() <= 0) && (track3.trim().length() <= 0))
			return false;

		// 设置查询条件
		setRequestDataByFind(track1, track2, track3);

		Jplm_CouponDef coupon = new Jplm_CouponDef();

		// 查找券信息
		if (((Jplm_NetService) NetService.getDefault()).sendFjkSale(mzkreq, coupon))
		{
			// 检查付款列表中是否已存在同类型的券付款
			for (int j = 0; j < saleBS.salePayment.size(); j++)
			{
				SalePayDef sp = (SalePayDef) saleBS.salePayment.get(j);
				if ((sp.paycode.equals("0508")) && sp.str2.equals(coupon.type))
				{
					new MessageBox("已存在券类型" + coupon.type + "的付款");
					return false;
				}
			}

			if (coupons.size() > 0)
			{
				if (paymode != null && paymode.code.equals("0508"))
				{
					// 检查券中是否存在多张全场券
					for (int i = 0; i < coupons.size(); i++)
					{
						Jplm_CouponDef tmpCoupon = (Jplm_CouponDef) coupons.get(i);

						if (!tmpCoupon.type.equals(coupon.type))
						{
							new MessageBox("该券类型与上张类型不匹配!");
							return false;
						}

					}

					// 防止同样的卡刷多次
					for (int i = 0; i < coupons.size(); i++)
					{
						Jplm_CouponDef tmpCoupon = (Jplm_CouponDef) coupons.get(i);
						if (tmpCoupon.type.equals(coupon.type) && tmpCoupon.cardno.equals(coupon.cardno))
						{
							new MessageBox(coupon.cardno + "已存在");
							return false;
						}
					}
				}
			}

			sortSameTypeCouponByMoney(coupon, true);

			return true;
		}
		return false;
	}

	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
		Jplm_CouponDef coupon = new Jplm_CouponDef();
		boolean isRet = ((Jplm_NetService) NetService.getDefault()).sendFjkSale(req, coupon);

		if (isRet && coupon != null && coupon.cardno.trim().length() > 0)
		{
			// 记录下返回的卡号，做为交易成功的标志
			ret.cardno = coupon.cardno;
			return true;
		}

		return false;

	}
}
