package com.efuture.javaPos.Logic;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.CustomerVipZklDef;
import com.efuture.javaPos.Struct.GoodsAmountDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.Struct.SuperMarketPopRuleDef;

// 数据计算业务
public class SaleBS1Calc extends SaleBS0Data
{
	protected char popvipzsz = 'Y'; // VIP折上折标记

	public SaleBS1Calc()
	{
		super();
	}

	// 增加商品到数据类里
	public boolean addSaleGoods(GoodsDef goodsDef, String yyyh, double quantity, double price, double allprice, boolean dzcm)
	{
		// 如果数量小于等于0，则要求重新扫码
		if (quantity == 0 || quantity < 0)
		{
			new MessageBox(Language.apply("商品数量输入无效,请重新输入"));
			return false;
		}

		// 指定小票退货
		if (isSpecifyBack())
		{
			if ((thSaleGoods == null) || (thSaleGoods.sl <= 0)) { return false; }

			// 将原退货数量保存到商品库存，用于改数量时检查退货数量是否超过原小票
			goodsDef.kcsl = thSaleGoods.sl;

			//
			thSaleGoods.syjh = saleHead.syjh; // 收银机号,主键
			thSaleGoods.fphm = saleHead.fphm; // 小票号,主键
			thSaleGoods.rowno = saleGoods.size() + 1; // 行号,主键
			thSaleGoods.yyyh = yyyh;
			thSaleGoods.yfphm = thFphm;
			thSaleGoods.ysyjh = thSyjh;

			//
			thSaleGoods.name = goodsDef.name;
			thSaleGoods.unit = goodsDef.unit;

			// 重算折扣
			thSaleGoods.hjje = ManipulatePrecision.doubleConvert((thSaleGoods.hjje / thSaleGoods.sl) * quantity, 2, 1); // 合计金额
			thSaleGoods.hyzke = ManipulatePrecision.doubleConvert((thSaleGoods.hyzke / thSaleGoods.sl) * quantity, 2, 1); // 会员折扣额(来自会员优惠)
			thSaleGoods.yhzke = ManipulatePrecision.doubleConvert((thSaleGoods.yhzke / thSaleGoods.sl) * quantity, 2, 1); // 优惠折扣额(来自营销优惠)
			thSaleGoods.lszke = ManipulatePrecision.doubleConvert((thSaleGoods.lszke / thSaleGoods.sl) * quantity, 2, 1); // 零时折扣额(来自手工打折)
			thSaleGoods.lszre = ManipulatePrecision.doubleConvert((thSaleGoods.lszre / thSaleGoods.sl) * quantity, 2, 1); // 零时折让额(来自手工打折)
			thSaleGoods.lszzk = ManipulatePrecision.doubleConvert((thSaleGoods.lszzk / thSaleGoods.sl) * quantity, 2, 1); // 零时总品折扣
			thSaleGoods.lszzr = ManipulatePrecision.doubleConvert((thSaleGoods.lszzr / thSaleGoods.sl) * quantity, 2, 1); // 零时总品折让
			thSaleGoods.plzke = ManipulatePrecision.doubleConvert((thSaleGoods.plzke / thSaleGoods.sl) * quantity, 2, 1); // 批量折扣
			thSaleGoods.zszke = ManipulatePrecision.doubleConvert((thSaleGoods.zszke / thSaleGoods.sl) * quantity, 2, 1); // 赠送折扣
			thSaleGoods.hjzk = getZZK(thSaleGoods);

			thSaleGoods.sl = ManipulatePrecision.doubleConvert(quantity, 4, 1);

			// 增加商品明细
			addSaleGoodsObject(thSaleGoods, goodsDef, getGoodsSpareInfo(goodsDef, thSaleGoods));
		}
		else
		{
			// 生成商品明细
			SaleGoodsDef saleGoodsDef = goodsDef2SaleGoods(goodsDef, yyyh, quantity, price, allprice, dzcm);

			if (SellType.ISBACK(saletype) && (thSyjh != null) && GlobalInfo.sysPara.inputydoc == 'D')
			{
				// 记录原小票号/收银机号
				saleGoodsDef.yfphm = thFphm;
				saleGoodsDef.ysyjh = thSyjh;
			}
			// 增加商品明细
			addSaleGoodsObject(saleGoodsDef, goodsDef, getGoodsSpareInfo(goodsDef, saleGoodsDef));

			// 计算商品应收
			calcGoodsYsje(saleGoods.size() - 1);

		}

		// 计算小票应收
		calcHeadYsje();

		return true;
	}

	public SpareInfoDef getGoodsSpareInfo(GoodsDef goodsDef, SaleGoodsDef saleGoodsDef)
	{
		SpareInfoDef info = new SpareInfoDef();

		return info;
	}

	public SaleGoodsDef goodsDef2SaleGoods(GoodsDef goodsDef, String yyyh, double quantity, double price, double allprice, boolean dzcm)
	{
		SaleGoodsDef saleGoodsDef = new SaleGoodsDef();

		saleGoodsDef.syjh = saleHead.syjh; // 收银机号,主键
		saleGoodsDef.fphm = saleHead.fphm; // 小票号,主键
		saleGoodsDef.rowno = saleGoods.size() + 1; // 行号,主键
		saleGoodsDef.yyyh = yyyh; // 营业员
		saleGoodsDef.fph = curyyyfph; // 营业员手工单发票号
		saleGoodsDef.barcode = goodsDef.barcode; // 商品条码
		saleGoodsDef.code = goodsDef.code; // 商品编码
		saleGoodsDef.type = goodsDef.type; // 编码类别
		saleGoodsDef.gz = goodsDef.gz; // 商品柜组
		saleGoodsDef.catid = goodsDef.catid; // 商品品类
		saleGoodsDef.ppcode = goodsDef.ppcode; // 商品品牌
		saleGoodsDef.uid = goodsDef.uid; // 多单位码
		saleGoodsDef.batch = curBatch; // 批号
		saleGoodsDef.yhdjbh = goodsDef.popdjbh; // 优惠单据编号
		saleGoodsDef.name = ((goodsDef.name == null) ? "" : goodsDef.name.trim()); // 名称
		saleGoodsDef.unit = goodsDef.unit; // 单位
		saleGoodsDef.bzhl = goodsDef.bzhl; // 包装含量
		saleGoodsDef.sl = ManipulatePrecision.doubleConvert(quantity, 4, 1); // 销售数量
		saleGoodsDef.lsj = goodsDef.lsj; // 零售价
		saleGoodsDef.jg = ManipulatePrecision.doubleConvert(price, 2, 1); // 销售价格
		saleGoodsDef.hjzk = 0; // 合计折扣,等于各种折扣之和
		saleGoodsDef.hyzke = 0; // 会员折扣额(来自会员优惠)
		saleGoodsDef.hyzkfd = goodsDef.hyjzkfd; // 会员折扣分担
		saleGoodsDef.yhzke = 0; // 优惠折扣额(来自营销优惠)
		saleGoodsDef.yhzkfd = 0; // 优惠折扣分担
		saleGoodsDef.lszke = 0; // 零时折扣额(来自手工打折)
		saleGoodsDef.lszre = 0; // 零时折让额(来自手工打折)
		saleGoodsDef.lszzk = 0; // 零时总品折扣
		saleGoodsDef.lszzr = 0; // 零时总品折让
		saleGoodsDef.plzke = 0; // 批量折扣
		saleGoodsDef.zszke = 0; // 赠送折扣
		saleGoodsDef.lszkfd = curPfzkfd; // 临时折扣分担
		saleGoodsDef.sqkh = ""; // 单品授权卡号
		saleGoodsDef.sqktype = '\0'; // 单品授权卡类别
		saleGoodsDef.sqkzkfd = 0; // 单品授权卡授权折扣分担
		saleGoodsDef.isvipzk = goodsDef.isvipzk; // 是否允许VIP折扣（Y/N）
		saleGoodsDef.xxtax = goodsDef.xxtax; // 税率
		saleGoodsDef.flag = '4'; // 商品标志，1-赠品,2-电子秤条码，3-削价，4-一般

		// 合计金额
		if (dzcm)
		{
			saleGoodsDef.hjje = ManipulatePrecision.doubleConvert(allprice, 2, 1);
			saleGoodsDef.flag = '2';
			if (SellType.ISBACK(saletype))
				saleGoodsDef.yhzke = dzcmjgzk;
			else
				saleGoodsDef.lszke = dzcmjgzk;
		}
		else
		{ // 良品铺子当ISDZC='A'时，当做电子秤处理
			if (goodsDef.isdzc == 'A')
				saleGoodsDef.flag = '2';
			saleGoodsDef.hjje = ManipulatePrecision.doubleConvert(saleGoodsDef.jg * saleGoodsDef.sl, 2, 1);
		}

		// 以旧换新码
		if (goodsDef.type == '8')
		{
			saleGoodsDef.yjhxcode = goodsDef.fxm;
			((SaleGoodsDef) (saleGoods.lastElement())).yjhxcode = goodsDef.code;
		}

		saleGoodsDef.inputbarcode = goodsDef.inputbarcode;
		saleGoodsDef.ysyjh = ""; // 原收银机号
		saleGoodsDef.yfphm = 0; // 原小票号
		saleGoodsDef.fhdd = ""; // 发货地点
		saleGoodsDef.memo = ""; // 备注
		saleGoodsDef.str1 = ""; // 备用字段
		saleGoodsDef.str2 = ""; // 备用字段
		saleGoodsDef.num1 = 0; // 备用字段
		saleGoodsDef.num2 = 0; // 备用字段
		
		if(SellType.ISHH(saletype))
		{
			if(saleGoodsDef.str13==null || (saleGoodsDef.str13!=null && !saleGoodsDef.str13.equalsIgnoreCase("T")))
			{
				saleGoodsDef.str13="S";
			}
			saleGoodsDef.ysyjh = this.thSyjh;
			saleGoodsDef.yfphm = this.thFphm;
		}

		// 家电下乡返款交易,记录退货原小票号
		if (SellType.ISJDXXFK(saletype) && GlobalInfo.sysPara.jdxxfkflag == 'Y')
		{
			saleGoodsDef.yfphm = thFphm;
			saleGoodsDef.ysyjh = thSyjh;
		}

		return saleGoodsDef;
	}

	// 将所有的折扣全部清0
	public void clearZZK(SaleGoodsDef saleGoodsDef)
	{
		saleGoodsDef.hyzke = 0;
		saleGoodsDef.yhzke = 0;
		saleGoodsDef.lszke = 0;
		saleGoodsDef.lszre = 0;
		saleGoodsDef.lszzk = 0;
		saleGoodsDef.lszzr = 0;
		saleGoodsDef.plzke = 0;
		saleGoodsDef.zszke = 0;

		saleGoodsDef.cjzke = 0; // 厂家折扣
		saleGoodsDef.ltzke = 0;
		saleGoodsDef.hyzklje = 0;
		saleGoodsDef.qtzke = 0;
		saleGoodsDef.qtzre = 0;
	}

	public void clearGoodsAllRebate(int index)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);

		clearZZK(saleGoodsDef);

		// 根据价格精度进行截断或四舍五入
		if (GlobalInfo.sysPara.isForceRound == 'Y')
		{
			// 按价格精度计算折扣
			if (saleGoodsDef.hjje > 0)
			{
				double hjje = getConvertPrice(saleGoodsDef.hjje, (GoodsDef) goodsAssistant.elementAt(index));
				saleGoodsDef.qtzre = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - hjje);
			}
		}

		getZZK(saleGoodsDef);
	}

	public void clearGoodsGrantRebate(int index)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);

		//
		saleGoodsDef.lszke = 0;
		saleGoodsDef.lszre = 0;
		saleGoodsDef.lszzk = 0;
		saleGoodsDef.lszzr = 0;

		saleGoodsDef.cjzke = 0; // 厂家折扣
		saleGoodsDef.ltzke = 0; // 零头折扣

		getZZK(saleGoodsDef);
	}

	public void calcGoodsYsje(int index)
	{
		if ((index < 0) || (index >= saleGoods.size())) { return; }

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);

		// 根据价格精度进行截断或四舍五入
		if (GlobalInfo.sysPara.isForceRound == 'Y')
		{
			// 按价格精度计算折扣
			if (saleGoodsDef.hjje > 0)
			{
				double hjje = getConvertPrice(saleGoodsDef.hjje, (GoodsDef) goodsAssistant.elementAt(index));
				saleGoodsDef.qtzre = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - hjje);
			}
		}

		// 计算会员折扣和优惠折扣
		calcAllRebate(index);

		// 计算批量销售折扣,根据情况会重算优惠折扣和会员折扣
		calcBatchRebate(index);

		// 计算商品的合计
		getZZK(saleGoodsDef);
	}

	public double getZZK(SaleGoodsDef saleGoodsDef)
	{
		saleGoodsDef.hjzk = ManipulatePrecision.doubleConvert(saleGoodsDef.hyzke + saleGoodsDef.yhzke + saleGoodsDef.lszke + saleGoodsDef.lszre + saleGoodsDef.lszzk + saleGoodsDef.lszzr + saleGoodsDef.plzke + saleGoodsDef.zszke + saleGoodsDef.cjzke + saleGoodsDef.ltzke + saleGoodsDef.hyzklje + saleGoodsDef.qtzke + saleGoodsDef.qtzre + saleGoodsDef.rulezke + saleGoodsDef.mjzke, 2, 1);

		return saleGoodsDef.hjzk;
	}

	public SaleGoodsDef SplitSaleGoodsRow(int index, double splitsl)
	{
		if (index < 0 || index >= saleGoods.size())
			return null;
		if (ManipulatePrecision.doubleCompare(splitsl, 0, 4) <= 0)
			return null;

		SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(index);
		if (ManipulatePrecision.doubleCompare(sg.sl, splitsl, 4) <= 0)
			return null;

		SaleGoodsDef newsg = (SaleGoodsDef) sg.clone();

		sg.sl = ManipulatePrecision.doubleConvert(splitsl, 4, 1);
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
		sg.hjzk = getZZK(sg);

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
		newsg.hjzk = getZZK(newsg);

		GoodsDef newgd = (GoodsDef) ((GoodsDef) goodsAssistant.elementAt(index)).clone();
		addSaleGoodsObject(newsg, newgd, getGoodsSpareInfo(newgd, newsg));

		return newsg;
	}

	// 当前是否刷会员卡
	public boolean checkMemberSale()
	{
		boolean isCustomer = false;

		if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
		{
			isCustomer = true;
		}

		return isCustomer;
	}

	public void calcAllRebate(int index)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// 指定小票退货时不重算优惠价和会员价
		if (isSpecifyBack(saleGoodsDef)) { return; }

		if (SellType.NOPOP(saletype))
			return;

		// 批发销售,预售定金不计算
		if (!SellType.ISSALE(this.saletype) || SellType.ISBATCH(saletype) || SellType.ISEARNEST(saletype) || SellType.ISPREPARETAKE(saletype)) { return; }

		// 削价商品和赠品不计算
		if ((saleGoodsDef.flag == '3') || (saleGoodsDef.flag == '1' || this.isHHGoods(saleGoodsDef))) { return; }

		// 清除商品相应自动计算的折扣
		saleGoodsDef.hyzke = 0;
		saleGoodsDef.hyzkfd = goodsDef.hyjzkfd;
		saleGoodsDef.yhzke = 0;
		saleGoodsDef.yhzkfd = 0;
		saleGoodsDef.plzke = 0;
		saleGoodsDef.zszke = 0;

		// 促销单缺省允许VIP折上折,可通过促销单定义改变
		popvipzsz = 'Y';

		// 计算商品促销折扣
		calcGoodsPOPRebate(index);

		// 计算会员VIP折上折
		calcGoodsVIPRebate(index);

		//
		saleGoodsDef.yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.yhzke, 2, 1);
		saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert(saleGoodsDef.hyzke, 2, 1);

		// 按价格精度计算折扣
		if (saleGoodsDef.yhzke > 0)
			saleGoodsDef.yhzke = getConvertRebate(index, saleGoodsDef.yhzke);
		if (saleGoodsDef.hyzke > 0)
			saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
	}

	public boolean isUseMemberHyj(char isvipzk)
	{
		// wangyong add 2011.7.18 为了兼容城乡的会员价模式
		return true;
	}

	public boolean isMemberHyjMode()
	{
		if (checkMemberSale() && (curCustomer != null) && (curCustomer.ishy == 'H'))
			return true;
		else
			return false;
	}

	public boolean isMemberVipMode()
	{
		if (checkMemberSale() && (curCustomer != null) && (curCustomer.ishy == 'Y' || curCustomer.ishy == 'V'))
			return true;
		else
			return false;
	}

	public void calcGoodsPOPRebate(int index)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		if(this.isHHGoods(saleGoodsDef)) return;
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

	public void calcGoodsVIPRebate(int index)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		if(this.isHHGoods(saleGoodsDef)) return;
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

					// 计算本笔交易可销售数量
					double zsl = 0;
					for (int i = 0; i < saleGoods.size(); i++)
					{
						SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);

						if (i != index && sg.code.equals(saleGoodsDef.code) && sg.gz.equals(saleGoodsDef.gz) && sg.uid.equals(saleGoodsDef.uid) && sg.hyzke > 0)
						{
							zsl += sg.sl;
						}
					}

					if (zsl >= zklDef.maxsl)
					{
						sl = 0;
					}
					else
					{
						// 剩余能够打折的数量
						double sysl = zklDef.maxsl - zsl;

						// 如果是电子秤不进行拆行
						if (saleGoodsDef.flag == '2')
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

					if (sl < 0)
						sl = 0;
				}

				if (GlobalInfo.sysPara.isVipMaxSlMsg == 'Y')
				{
					if (ManipulatePrecision.doubleCompare(saleGoodsDef.sl, sl, 4) > 0)
					{
						if (zklDef.maxsl > 0)
						{
							new MessageBox(saleGoodsDef.code + "[" + saleGoodsDef.name + "]\n\n" + Language.apply("商品已经超出会员限量: ") + ManipulatePrecision.doubleToString(zklDef.maxsl, 4, 1, true) + "\n\n" + Language.apply("超出的部分以原价进行销售"));
						}
						else
						{
							new MessageBox(saleGoodsDef.code + "[" + saleGoodsDef.name + "]\n\n" + Language.apply("商品已经超出会员限量,当前商品以原价进行销售"));
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
						if (ManipulatePrecision.doubleCompare(getZZK(saleGoodsDef), (1 - zklDef.zkl) * sl * saleGoodsDef.jg, 2) > 0)
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
						if (saleGoodsDef.hjje - getZZK(saleGoodsDef) >= cjjdn && saleGoodsDef.hjje - getZZK(saleGoodsDef) <= cjjup) // 折扣在区间内
						{
							vipzkl = zklDef.inareazkl;
						}
						else if (saleGoodsDef.hjje - getZZK(saleGoodsDef) > cjjup) // 折扣在区间上
						{
							vipzkl = zklDef.upareazkl;
						}
						else if (saleGoodsDef.hjje - getZZK(saleGoodsDef) < cjjdn) // 折扣在区间下
						{
							vipzkl = zklDef.dnareazkl;
						}

						// 根据折扣模式计算折扣
						if (zklDef.zkmode == '1' && saleGoodsDef.jg > vipzkl && vipzkl >= 0)
						{
							// 按指定价格成交
							if (ManipulatePrecision.doubleCompare(getZZK(saleGoodsDef), (saleGoodsDef.jg - vipzkl) * sl, 2) < 0)
							{
								saleGoodsDef.hyzke += ManipulatePrecision.doubleConvert((saleGoodsDef.jg - vipzkl) * sl - getZZK(saleGoodsDef), 2, 1);
								setGoodsVIPRebateInfo(saleGoodsDef, zklDef);
							}
						}
						else if (zklDef.zkmode == '3' && vipzkl > 0)
						{
							// 成交价基础上再减价金额,再减不能超过商品价值
							double zke = ManipulatePrecision.doubleConvert((vipzkl * sl), 2, 1);
							if (saleGoodsDef.hjje - (getZZK(saleGoodsDef) + zke) < 0)
								zke = saleGoodsDef.hjje - getZZK(saleGoodsDef);
							saleGoodsDef.hyzke += zke;
							setGoodsVIPRebateInfo(saleGoodsDef, zklDef);
						}
						else if (1 > vipzkl && vipzkl >= 0)
						{
							// 成交价基础上再打折指定折扣率
							saleGoodsDef.hyzke += ManipulatePrecision.doubleConvert((1 - vipzkl) * (saleGoodsDef.hjje - getZZK(saleGoodsDef)) * (sl / saleGoodsDef.sl), 2, 1);
							setGoodsVIPRebateInfo(saleGoodsDef, zklDef);
						}

						if (zklDef.iszsz == 'A' && zklDef.zkl > 0)
						{
							// 如果已打折扣已经大于该会员最大可以打的折扣,则不进行会员打折
							if (ManipulatePrecision.doubleCompare(getZZK(saleGoodsDef), (1 - zklDef.zkl) * sl * saleGoodsDef.jg, 2) > 0)
							{
								saleGoodsDef.hyzke = ManipulatePrecision.sub(saleGoodsDef.hyzke, ManipulatePrecision.doubleConvert(getZZK(saleGoodsDef) - ((1 - zklDef.zkl) * sl * saleGoodsDef.jg), 2, 1));
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
						if (ManipulatePrecision.doubleCompare(getZZK(saleGoodsDef), (saleGoodsDef.jg - zklDef.zkl) * sl, 2) < 0)
						{
							// 清空其他折扣,只保留会员折扣
							clearGoodsAllRebate(index);

							// 原价和新价的差额记折扣
							saleGoodsDef.hyzke += ManipulatePrecision.doubleConvert((saleGoodsDef.jg - zklDef.zkl) * sl - getZZK(saleGoodsDef), 2, 1);
							setGoodsVIPRebateInfo(saleGoodsDef, zklDef);

						}
					}
					else if (zklDef.zkmode == '3' && zklDef.zkl > 0)
					{
						// 指定减价金额
						if (ManipulatePrecision.doubleCompare(getZZK(saleGoodsDef), zklDef.zkl * sl, 2) < 0)
						{
							// 清空其他折扣,只保留会员折扣
							clearGoodsAllRebate(index);

							// 减价金额不能超过商品价值
							double zke = ManipulatePrecision.doubleConvert(zklDef.zkl * sl);
							if (saleGoodsDef.hjje - zke < 0)
								zke = saleGoodsDef.hjje;
							saleGoodsDef.hyzke += ManipulatePrecision.doubleConvert(zke - getZZK(saleGoodsDef), 2, 1);
							setGoodsVIPRebateInfo(saleGoodsDef, zklDef);
						}
					}
					else if (zklDef.zkmode == '2' && 1 > zklDef.zkl && zklDef.zkl >= 0)
					{
						// 指定折扣率
						if (ManipulatePrecision.doubleCompare(getZZK(saleGoodsDef), (1 - zklDef.zkl) * sl * saleGoodsDef.jg, 2) < 0)
						{
							// 清空其他折扣,只保留会员折扣
							clearGoodsAllRebate(index);

							saleGoodsDef.hyzke += ManipulatePrecision.doubleConvert((1 - zklDef.zkl) * sl * saleGoodsDef.jg - getZZK(saleGoodsDef), 2, 1);
							setGoodsVIPRebateInfo(saleGoodsDef, zklDef);
						}
					}
				}
			}
		}
	}

	public void setGoodsVIPRebateInfo(SaleGoodsDef sgd, CustomerVipZklDef zklDef)
	{
		sgd.hydjbh = String.valueOf(zklDef.seqno);
	}

	public CustomerVipZklDef getGoodsVIPZKL(int index)
	{
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// 未刷卡
		if (!checkMemberSale() || (curCustomer == null)) { return null; }

		// 查询商品VIP折上折折扣率定义
		CustomerVipZklDef zklDef = new CustomerVipZklDef();

		if (DataService.getDefault().findVIPZKL(zklDef, curCustomer.code, curCustomer.type, goodsDef))
		{
			// 促销允许折上折且会员类允许折上折，VIP折扣才允许折上折
			if (!(popvipzsz == 'Y' && (zklDef.iszsz == 'Y' || zklDef.iszsz == 'A')))
				zklDef.iszsz = 'N';

			// 有柜组和商品的VIP折扣定义
			return zklDef;
		}
		else
		{
			// 无限量序号
			zklDef.seqno = 0;

			// 促销允许折上折且会员类允许折上折，VIP折扣才允许折上折
			zklDef.iszsz = popvipzsz;

			// 无柜组和商品的VIP折扣定义,以卡类别的折扣率为VIP打折标准
			zklDef.zkmode = '2'; // 比例模式
			zklDef.zkl = curCustomer.zkl;
			zklDef.zklareadn = 0;
			zklDef.zklareaup = 1;
			zklDef.inareazkl = curCustomer.zkl;
			zklDef.dnareazkl = curCustomer.zkl;
			zklDef.upareazkl = curCustomer.zkl;

			return zklDef;
		}
	}

	// 计算相同商品的合计数量
	public double calcSameGoodsQuantity(String code, String gz)
	{
		double hjsl = 0;
		SaleGoodsDef goods = null;

		for (int j = 0; j < saleGoods.size(); j++)
		{
			goods = (SaleGoodsDef) saleGoods.elementAt(j);

			if (goods.code.equals(code) && goods.gz.equals(gz) && !this.isHHGoods(goods))
			{
				hjsl += ManipulatePrecision.mul(goods.sl, goods.bzhl);
			}
		}

		return hjsl;
	}
	
	public double calcSameGoodsQuantity(GoodsDef goods)
	{
		return calcSameGoodsQuantity(goods.code,goods.gz);
	}

	public void calcBatchRebate(int index)
	{
		int i;
		double sl;
		double jg;
		GoodsAmountDef pl = new GoodsAmountDef();
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);

		// 退货不处理
		if (SellType.ISBACK(saletype)) { return; }

		// 削价商品或赠品不处理
		if ((saleGoodsDef.flag == '3') || (saleGoodsDef.flag <= '1') || this.isHHGoods(saleGoodsDef)) { return; }

		// 无批量
		if ((goodsDef.minplsl <= 0) || (saleGoodsDef.lsj <= 0)) { return; }

		// 当前是否刷会员卡
		boolean isMember = checkMemberSale();

		// 计算同等商品的数量
		SaleGoodsDef saleGoodsDef1 = null;
		sl = 0;

		for (i = 0; i < saleGoods.size(); i++)
		{
			saleGoodsDef1 = (SaleGoodsDef) saleGoods.elementAt(i);

			if (saleGoodsDef1.code.trim().equals(saleGoodsDef.code.trim()) && saleGoodsDef1.gz.trim().equals(saleGoodsDef.gz.trim()) && saleGoodsDef1.uid.trim().equals(saleGoodsDef.uid.trim()) && (saleGoodsDef.flag != '3') && (saleGoodsDef1.flag > '1'))
			{
				sl += saleGoodsDef1.sl;
			}
		}

		if ((sl >= goodsDef.minplsl) && DataService.getDefault().findAmountDef(pl, saleGoodsDef.code, saleGoodsDef.gz, saleGoodsDef.uid, sl))
		{
			if (isMember)
			{
				jg = pl.plhyj;
			}
			else
			{
				jg = pl.pllsj;
			}

			// 批量价低于优惠价 会员价
			if ((saleGoodsDef.lsj - jg) > ((saleGoodsDef.hyzke + saleGoodsDef.yhzke) / saleGoodsDef.sl))
			{
				for (i = 0; i < saleGoods.size(); i++)
				{
					saleGoodsDef1 = (SaleGoodsDef) saleGoods.elementAt(i);

					if (saleGoodsDef1.code.trim().equals(saleGoodsDef.code.trim()) && saleGoodsDef1.gz.trim().equals(saleGoodsDef.gz.trim()) && saleGoodsDef1.uid.trim().equals(saleGoodsDef.uid.trim()) && (saleGoodsDef.flag != '3') && (saleGoodsDef1.flag > '1'))
					{
						// 放弃优惠
						saleGoodsDef1.yhdjbh = "";
						saleGoodsDef1.yhzke = 0;
						saleGoodsDef1.yhzkfd = 0;
						saleGoodsDef1.hyzke = 0;

						saleGoodsDef1.hyzkfd = goodsDef.hyjzkfd;

						// 计算批量折扣
						saleGoodsDef1.plzke = ManipulatePrecision.doubleConvert((saleGoodsDef.lsj - jg) * saleGoodsDef1.sl, 2, 1);

						// 计算价格精度
						saleGoodsDef1.plzke = getConvertRebate(index, saleGoodsDef1.plzke);

						// 记录批量单号
						saleGoodsDef1.yhdjbh = pl.gz;

						getZZK(saleGoodsDef1);
					}
				}

				saleGoodsDef1 = null;
			}
		}
		else
		{
			for (i = 0; i < saleGoods.size(); i++)
			{
				saleGoodsDef1 = (SaleGoodsDef) saleGoods.elementAt(i);

				if (saleGoodsDef1.code.trim().equals(saleGoodsDef.code.trim()) && saleGoodsDef1.gz.trim().equals(saleGoodsDef.gz.trim()) && saleGoodsDef1.uid.trim().equals(saleGoodsDef.uid.trim()) && (saleGoodsDef.flag != '3') && (saleGoodsDef1.flag > '1'))
				{
					calcAllRebate(i);
				}
			}
		}
	}

	public double getConvertRebate(int i, double zkje)
	{
		return getConvertRebate(i, zkje, -1);
	}

	public double getConvertRebate(int i, double zkje, double jd)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(i);
		double je;
		double zk;
		double jgjd;

		if (jd <= 0)
		{
			if (goodsDef.jgjd == 0)
			{
				jgjd = 0.01;
			}
			else
			{
				jgjd = goodsDef.jgjd;
			}
		}
		else
		{
			jgjd = jd;
		}

		je = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef), 2, 1);

		je = ManipulatePrecision.doubleConvert(ManipulatePrecision.doubleConvert(ManipulatePrecision.doubleConvert(je / jgjd, 2, 1), 0, 1) * jgjd, 2, 1);

		zk = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef) - je, 2, 1);

		zk = ManipulatePrecision.doubleConvert(zkje + zk, 2, 1);

		if (zk < 0)
		{
			zk = 0;
		}

		return zk;
	}

	public double getConvertPrice(double jg, GoodsDef gd)
	{
		int jd;

		if (gd.jgjd == 0)
		{
			jd = 2;
		}
		else
		{
			jd = ManipulatePrecision.getDoubleScale(gd.jgjd);
		}

		// 四舍五入到商品价格精度
		return ManipulatePrecision.doubleConvert(jg, jd, 1);
	}

	// 统计条件
	public boolean statusCond(SaleGoodsDef saleGoodsDef)
	{
		if (saleGoodsDef.flag == '0') { return false; }

		return true;
	}

	public void calcHeadYsje()
	{
		SaleGoodsDef saleGoodsDef = null;
		int sign = 1;

		saleHead.hjzje = 0;
		saleHead.hjzsl = 0;
		saleHead.hjzke = 0;
		saleHead.hyzke = 0;
		saleHead.yhzke = 0;
		saleHead.lszke = 0;

		for (int i = 0; i < saleGoods.size(); i++)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);

			if (!statusCond(saleGoodsDef))
			{
				continue;
			}

			// 合计商品件数(电子秤商品总是按1件记数)
			int spjs = (int) saleGoodsDef.sl;
			if (saleGoodsDef.flag == '2')
				spjs = 1;
			saleHead.hjzsl += spjs;

			// 以旧换新商品,合计要减
			if (saleGoodsDef.type == '8' || this.isHHGoods(saleGoodsDef))
			{
				sign = -1;
			}
			else
			{
				sign = 1;
			}

			// 计算小票头汇总
			saleHead.hjzje = ManipulatePrecision.doubleConvert(saleHead.hjzje + (saleGoodsDef.hjje * sign), 2, 1); // 合计总金额
			saleHead.hjzke = ManipulatePrecision.doubleConvert(saleHead.hjzke + (saleGoodsDef.hjzk * sign), 2, 1); // 合计折扣额

			saleHead.hyzke = ManipulatePrecision.doubleConvert(saleHead.hyzke + (saleGoodsDef.hyzke * sign), 2, 1); // 会员折扣额(来自会员优惠)
			saleHead.hyzke = ManipulatePrecision.doubleConvert(saleHead.hyzke + (saleGoodsDef.hyzklje * sign), 2, 1); // 会员折扣率金额(来自会员优惠)

			saleHead.yhzke = ManipulatePrecision.doubleConvert(saleHead.yhzke + (saleGoodsDef.yhzke * sign), 2, 1); // 优惠折扣额(来自营销优惠)
			saleHead.yhzke = ManipulatePrecision.doubleConvert(saleHead.yhzke + (saleGoodsDef.zszke * sign), 2, 1); // 赠送折扣
			saleHead.yhzke = ManipulatePrecision.doubleConvert(saleHead.yhzke + (saleGoodsDef.rulezke * sign), 2, 1); // 超市规则促销折扣（非整单折扣）
			saleHead.yhzke = ManipulatePrecision.doubleConvert(saleHead.yhzke + (saleGoodsDef.mjzke * sign), 2, 1); // 超市规则促销折扣（整单折扣）

			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.lszke * sign), 2, 1); // 零时折扣额(来自手工打折)
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.lszre * sign), 2, 1); // 零时折让额(来自手工打折)
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.lszzk * sign), 2, 1); // 零时总品折扣
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.lszzr * sign), 2, 1); // 零时总品折让
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.plzke * sign), 2, 1); // 批量折扣
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.cjzke * sign), 2, 1); // 厂家折扣
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.ltzke * sign), 2, 1); // 零头折扣
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.qtzke * sign), 2, 1); // 其他折扣
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.qtzre * sign), 2, 1); // 其他折扣
		}

		saleHead.ysje = ManipulatePrecision.doubleConvert(saleHead.hjzje - saleHead.hjzke, 2, 1);

		// 计算应付
		calcHeadYfje();
	}

	public double calcHeadYfje()
	{
		// 根据收银机定义计算应付
		saleyfje = getDetailOverFlow(saleHead.ysje);
		saleHead.sswr_sysy = ManipulatePrecision.sub(saleyfje, saleHead.ysje);

		return saleyfje;
	}

	public double getDetailOverFlow(double allMoney)
	{
		return getDetailOverFlow(allMoney, GlobalInfo.syjDef.sswrfs);
	}

	public double getDetailOverFlow(double allMoney, char type1)
	{
		double result = allMoney;

		try
		{
			char type = type1;

			// 收银截断方式，0-精确到分、1-四舍五入到角、2-截断到角、3-四舍五入到元、4-截断到元、5-进位到角、6-进位到元
			// 7-5舍6入到角
			switch (type)
			{
				case '0':
					result = ManipulatePrecision.doubleConvert(allMoney, 2, 1);

					break;

				case '1':
					result = ManipulatePrecision.doubleConvert(allMoney, 1, 1);

					break;

				case '2':
					result = ManipulatePrecision.doubleConvert(allMoney, 1, 0);

					break;

				case '3':
					result = ManipulatePrecision.doubleConvert(allMoney, 0, 1);

					break;

				case '4':
					result = ManipulatePrecision.doubleConvert(allMoney, 0, 0);

					break;
				case '5':
					result = ManipulatePrecision.doubleConvert(allMoney + 0.09, 1, 0);

					break;
				case '6':
					result = ManipulatePrecision.doubleConvert(allMoney + 0.9, 0, 0);

					break;

				case '7':
					result = ManipulatePrecision.doubleConvert(allMoney - 0.01, 1, 1);

					break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return result;
	}

	public double getGoodsApportionPrecision()
	{
		// 定义了商品舍入精度，则商品在分摊折扣时按该精度进行分摊
		if (GlobalInfo.sysPara.goodsPrecision > 0)
			return GlobalInfo.sysPara.goodsPrecision;

		// 否则按收银机收银截断方式的精度进行分摊
		if (GlobalInfo.syjDef.sswrfs == '0')
			return 0.01;
		if (GlobalInfo.syjDef.sswrfs == '1' || GlobalInfo.syjDef.sswrfs == '2')
			return 0.1;
		if (GlobalInfo.syjDef.sswrfs == '3' || GlobalInfo.syjDef.sswrfs == '4')
			return 1;

		return 0.01;
	}

	public void calcSellPayMoney(boolean calc)
	{
		if (ManipulatePrecision.doubleCompare(GlobalInfo.sysPara.goodsPrecision, 0.01, 2) <= 0)
			return;

		// 检查每行商品成交价是否需要去零头
		boolean have = false;
		int jd = ManipulatePrecision.getDoubleScale(GlobalInfo.sysPara.goodsPrecision);
		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);

			// 先清空零头折扣
			if (sg.ltzke > 0)
			{
				sg.ltzke = 0;
				getZZK(sg);

				have = true;
			}

			// 再检查商品是否存在零头,计算零头折扣
			if (calc)
			{
				double cjj = ManipulatePrecision.doubleConvert(sg.hjje - sg.hjzk);
				double cje = ManipulatePrecision.doubleConvert(cjj, jd, 0);
				if (ManipulatePrecision.doubleCompare(cjj, cje, 2) > 0)
				{
					have = true;

					sg.ltzke = cjj - cje;
					sg.ltzke = ManipulatePrecision.doubleConvert(sg.ltzke);
					getZZK(sg);
				}
			}
		}

		if (have)
		{
			// 重算小票应收
			calcHeadYsje();

			// 刷新商品列表
			saleEvent.updateTable(getSaleGoodsDisplay());
			saleEvent.setTotalInfo();
		}
	}

	// 根据付款方式的付款精度,计算付款金额
	public String getPayMoneyByPrecision(double je, PayModeDef mode)
	{/*
		int jd;

		if (mode.sswrjd == 0)
		{
			jd = 2;
		}
		else
		{
			jd = ManipulatePrecision.getDoubleScale(mode.sswrjd);
		}

		double ye = 0;

		// 超市V8
		if (mode.sswrfs == 'Y')
		{
			ye = ManipulatePrecision.doubleConvert(je, jd, 0);
		}
		else if (mode.sswrfs == 'N')
		{
			ye = ManipulatePrecision.doubleConvert(je, jd, 1);
		}
		else
		{ // 超市R5
			switch (mode.sswrfs)
			{
				case '0':
					ye = ManipulatePrecision.doubleConvert(je, 2, 1);

					break;

				case '1':
					ye = ManipulatePrecision.doubleConvert(je, 1, 1);

					break;

				case '2':
					ye = ManipulatePrecision.doubleConvert(je, 1, 0);

					break;

				case '3':
					ye = ManipulatePrecision.doubleConvert(je, 0, 1);

					break;

				case '4':
					ye = ManipulatePrecision.doubleConvert(je, 0, 0);

					break;
				case '5':
					ye = ManipulatePrecision.doubleConvert(je + 0.09, 1, 0);

					break;
				case '6':
					ye = ManipulatePrecision.doubleConvert(je + 0.9, 0, 0);

					break;

				case '7':
					ye = ManipulatePrecision.doubleConvert(je - 0.01, 1, 1);

					break;
			}
		}

		// 如果按付款方式精度计算以后的余额比原余额小,则补上精度，保证缺省余额是足够的
		if (ManipulatePrecision.doubleCompare(je - ye, GlobalInfo.sysPara.lackpayfee, 2) > 0)
		{
			ye = ManipulatePrecision.doubleConvert(je + mode.sswrjd, jd, 1);
		}

		return ManipulatePrecision.doubleToString(ye, jd, 1);
	*/
	

        int jd;

        if (mode.sswrjd == 0)
        {
            jd = 2;
        }
        else
        {
            jd = ManipulatePrecision.getDoubleScale(mode.sswrjd);
        }

        double ye = 0 ;
        
        if (mode.sswrfs == 'Y')
        {
        	ye = ManipulatePrecision.doubleConvert(je, jd, 0);
        }
        else
        {
        	ye = ManipulatePrecision.doubleConvert(je, jd, 1);
        }
        
    	// 如果按付款方式精度计算以后的余额比原余额小,则补上精度，保证缺省余额是足够的
        if (ManipulatePrecision.doubleCompare(je - ye,GlobalInfo.sysPara.lackpayfee,2) > 0)
        {
            ye = ManipulatePrecision.doubleConvert(je + mode.sswrjd, jd, 1);
        }

        return ManipulatePrecision.doubleToString(ye, jd, 1);
    
	}

	// 计算印花促销
	public void calcStampPop(int n, int m)
	{
		if (stampList.size() <= 0)
			return;

		int i, j;
		SuperMarketPopRuleDef pyh;
		SaleGoodsDef old_com;
		GoodsDef old_info;

		SaleGoodsDef sg = null;
		SaleGoodsDef sg1 = null;
		GoodsDef gd = null;
		GoodsDef gd1 = null;

		for (i = n; i < saleGoods.size(); i++)
		{
			sg = (SaleGoodsDef) saleGoods.get(i);
			gd = (GoodsDef) goodsAssistant.get(i);
			String stampCode = null;
			for (j = m; j < stampList.size(); j++)
			{
				stampCode = (String) stampList.get(j);
				// 小记,削价,服务费,以旧换新不处理
				if (sg.flag == '0' || sg.flag == '3')
					continue;
				if (sg.type == '7' || sg.type == '8' || this.isHHGoods(sg))
					continue;

				// 查找印花优惠
				pyh = new SuperMarketPopRuleDef();
				if (!DataService.getDefault().findStamYh(pyh, gd.code, gd.gz, gd.catid, gd.ppcode, gd.uid, saleHead.rqsj, saleHead.rqsj, stampCode))
					continue;

				// 检查限量优惠
				if (GlobalInfo.isOnline && SellType.ISSALE(saletype) && pyh.type != '0' && pyh.yhplsl > -9999.00)
				{
					double hjsl = sg.sl;
					for (int l = 0; l < saleGoods.size(); l++)
					{
						sg1 = (SaleGoodsDef) saleGoods.get(l);
						gd1 = (GoodsDef) goodsAssistant.get(l);
						if (l != i && sg1.code.equals(sg.code) && sg1.gz.equals(sg.gz) && gd1.uid.equals(gd.uid) && sg1.yhdjbh.equals(pyh.djbh))
							hjsl += sg1.sl;
					}

					if (pyh.yhplsl < hjsl)
					{
						new MessageBox(Language.apply("该商品只有{0}个促销数量\n\n商品数量超过", new Object[]{pyh.yhplsl + ""}));
						continue; // 超过促销限量
					}
				}

				// 记录原优惠信息
				old_com = (SaleGoodsDef) sg.clone();
				old_info = (GoodsDef) gd.clone();

				gd.popdjbh = pyh.djbh;
				sg.yhdjbh = pyh.djbh;

				gd.poptype = pyh.type;
				sg.yhzkfd = pyh.zkfd;
				gd.poplsj = pyh.yhlsj;
				gd.pophyj = pyh.yhhyj;
				gd.poplsjzkl = pyh.yhzkl;
				gd.pophyjzkl = pyh.yhhyzkl;
				gd.infonum1 = pyh.yhplsl;

				// 计算印花折扣
				calcStampRebate(i);

				// 计算批量销售折扣,根据情况会重算优惠折扣和会员折扣
				calcBatchRebate(i);

				// 计算商品的合计折扣
				getZZK(sg);

				// 判断印花优惠是否低于正常促销
				if (ManipulatePrecision.doubleCompare(old_com.yhzke, sg.yhzke, 2) >= 0)
				{
					// 还原优惠
					sg = old_com;
					gd = old_info;
				}
				/*
				 * else { if (i >= winfirst && i < winfirst+PAGE_SALE_COM - 1)
				 * DispOneSaleCommod(i - winfirst,i);
				 * 
				 * //修改断点中相应记录 WriteBroken(i,BROKEN_UPDATE);
				 * 
				 * // k = 1; //即扫即打 if (GSysSyj.printfs == '1' &&
				 * (!ISLXSALE(saletype) || GSysPara.lxprint == 'Y') &&
				 * printflag[i] == 'Y') {
				 * GetMinusSaleCom(old_com,salecom[salecom_num]);
				 * 
				 * // PrintSaleLocal(PRT_SECTION_COMMOD,salecom_num); //打印商品明细
				 * char prtbuf[80];
				 * sprintf(prtbuf,"%4d<-此行取消%10.2f",comrowno[i],
				 * GetSaleSign(salecom
				 * [salecom_num].zje-ZZK(salecom[salecom_num])));
				 * PrinterDraft(prtbuf);
				 * 
				 * PrintSaleLocal(PRT_SECTION_COMMOD,i); //打印商品明细 } }
				 */
			}
		}
	}

	public void calcStampRebate(int index)
	{
		if (stampList.size() < 1)
			return;

		SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(index);
		GoodsDef gd = (GoodsDef) goodsAssistant.get(index);

		// 指定小票退货时不重算优惠价和会员价
		if (isSpecifyBack(sgd)) { return; }

		if (SellType.NOPOP(saletype))
			return;

		// 批发销售,预售定金不计算
		if (!SellType.ISSALE(this.saletype) || SellType.ISBATCH(saletype) || SellType.ISEARNEST(saletype) || SellType.ISPREPARETAKE(saletype)) { return; }

		// 削价商品和赠品不计算
		if ((sgd.flag == '3') || (sgd.flag == '1') || this.isHHGoods(sgd)) { return; }

		// 清除商品相应自动计算的折扣
		sgd.hyzke = 0;
		sgd.yhzke = 0;
//		sgd.yhzkfd = 1;
		sgd.plzke = 0;
		sgd.zszke = 0;

		// 如果是削价商品或赠品返回
		if (sgd.flag == '3' || sgd.flag == '1')
			return;

		// 营销优惠
		if (gd.poptype != '0')
		{
			// 定价且是单品优惠
			if (sgd.lsj > 0.009 && (gd.poptype == '1' || gd.poptype == '7'))
			{
				// 会员消费
				if (curCustomer != null && gd.poplsj >= gd.hyj && gd.hyj > 0)
				{
					if (sgd.lsj > gd.pophyj && gd.pophyj > 0)
						sgd.yhzke = ManipulatePrecision.doubleConvert(sgd.lsj * sgd.sl, 2, 0) - ManipulatePrecision.doubleConvert(gd.pophyj * sgd.sl, 2, 0);
				}
				else
				{
					if (sgd.lsj > gd.poplsj && gd.poplsj > 0)
						sgd.yhzke = ManipulatePrecision.doubleConvert(sgd.lsj * sgd.sl, 2, 0) - ManipulatePrecision.doubleConvert(gd.poplsj * sgd.sl, 2, 0);
				}
			}
			else
			{
				// 会员消费
				if (curCustomer != null && gd.poplsjzkl > gd.pophyjzkl)
				{
					if (1 > gd.pophyjzkl && gd.pophyjzkl > 0)
						sgd.yhzke = sgd.hjje - ManipulatePrecision.doubleConvert(ManipulatePrecision.doubleConvert(gd.pophyjzkl * sgd.lsj, 2, 0) * sgd.sl, 2, 0);
					// salecom[n].hyzke = salecom[n].zje -
					// FloatConvert(FloatConvert(cominfo[n].yhhyzkl*salecom[n].lsj,2)*salecom[n].sl,2);
				}
				else
				{
					if (1 > gd.poplsjzkl && gd.poplsjzkl > 0)
						sgd.yhzke = sgd.hjje - ManipulatePrecision.doubleConvert(ManipulatePrecision.doubleConvert(gd.poplsjzkl * sgd.lsj, 2, 0) * sgd.sl, 2, 0);
				}
			}
		}

		// 按价格精度计算折扣
		if (sgd.yhzke > 0)
			sgd.yhzke = getConvertRebate(index, sgd.yhzke);
		if (sgd.hyzke > 0)
			sgd.hyzke = getConvertRebate(index, sgd.hyzke);
	}
}
