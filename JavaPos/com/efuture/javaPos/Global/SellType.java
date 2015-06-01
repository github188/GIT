package com.efuture.javaPos.Global;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Struct.SaleHeadDef;

public class SellType
{
	/*
	 * public final static String RETAIL_SALE = "1"; //零售销售 public final static
	 * String RETAIL_SALE_HC = "2"; //红冲零售销售 public final static String
	 * RETAIL_SALE_CLEAR = "3"; //取消零售销售
	 * 
	 * public final static String RETAIL_BACK = "4"; //零售退货 public final static
	 * String RETAIL_BACK_HC = "5"; //红冲零售退货 public final static String
	 * RETAIL_BACK_CLEAR = "6"; //取消零售退货
	 * 
	 * public final static String BATCH_SALE = "A"; //批发销售 public final static
	 * String BATCH_SALE_HC = "B"; //红冲批发销售 public final static String
	 * BATCH_SALE_CLEAR = "C"; //取消批发销售
	 * 
	 * public final static String BATCH_BACK = "D"; //批发退货 public final static
	 * String BATCH_BACK_HC = "E"; //红冲批发退货 public final static String
	 * BATCH_BACK_CLEAR = "F"; //取消批发退货
	 * 
	 * public final static String EARNEST_SALE = "G"; //预收定金 public final static
	 * String EARNEST_SALE_HC = "H"; //红冲预收定金 public final static String
	 * EARNEST_SALE_CLEAR = "I"; //取消预收定金
	 * 
	 * public final static String EARNEST_BACK = "J"; //定金退货 public final static
	 * String EARNEST_BACK_HC = "K"; //红冲定金退货 public final static String
	 * EARNEST_BACK_CLEAR = "L"; //取消定金退货
	 * 
	 * public final static String PREPARE_TAKE = "M"; //预售提货 public final static
	 * String PREPARE_TAKE_HC = "N"; //红冲预售提货 public final static String
	 * PREPARE_TAKE_CLEAR = "O"; //取消预售提货
	 * 
	 * public final static String PREPARE_SALE = "P"; //预售销售 public final static
	 * String PREPARE_SALE_HC = "Q"; //红冲预售销售 public final static String
	 * PREPARE_SALE_CLEAR = "R"; //取消预售销售
	 * 
	 * public final static String PREPARE_BACK = "S"; //预售退货 public final static
	 * String PREPARE_BACK_HC = "T"; //红冲预售退货 public final static String
	 * PREPARE_BACK_CLEAR = "U"; //取消预售退货
	 * 
	 * public final static String PURCHANSE_COUPON = "V"; //买券交易 public final
	 * static String PURCHANSE_COUPON_HC = "W"; //红冲买券交易 public final static
	 * String PURCHANSE_COUPON_CLEAR = "X"; //取消买券交易
	 * 
	 * public final static String PURCHANSE_COUPON_BACK = "Y"; //退券交易 public
	 * final static String PURCHANSE_COUPON_BACK_HC = "Z"; //红冲退券交易 public final
	 * static String PURCHANSE_COUPON_BACK_CLEAR = "["; //取消退券交易
	 * 
	 * public final static String EXERCISE_SALE = ":"; //练习销售 public final
	 * static String EXERCISE_BACK = ";"; //练习退货 public final static String
	 * CHECK_INPUT = "="; //盘点输入 public final static String JDFH_SALE = "<";
	 * //家电发货 public final static String JDFH_BACK = ">"; //家电退货
	 * 
	 * public final static String JDXX_FK = "("; //家电下乡返款 public final static
	 * String JDXX_FK_HC = ")"; //红冲家电下乡返款 public final static String JDXX_BACK
	 * = "$"; //家电下乡返款退货 public final static String JDXX_BACK_HC = "%";
	 * //红冲家电下乡返款退货
	 */

	public final static String RETAIL_SALE = "1"; // 零售销售
	public final static String RETAIL_SALE_HC = "2"; // 红冲零售销售
	public final static String RETAIL_SALE_CLEAR = "3"; // 取消零售销售

	public final static String RETAIL_BACK = "4"; // 零售退货
	public final static String RETAIL_BACK_HC = "5"; // 红冲零售退货
	public final static String RETAIL_BACK_CLEAR = "6"; // 取消零售退货

	public final static String BATCH_SALE = "A"; // 批发销售
	public final static String BATCH_SALE_HC = "B"; // 红冲批发销售
	public final static String BATCH_SALE_CLEAR = "C"; // 取消批发销售

	public final static String BATCH_BACK = "D"; // 批发退货
	public final static String BATCH_BACK_HC = "E"; // 红冲批发退货
	public final static String BATCH_BACK_CLEAR = "F"; // 取消批发退货

	public final static String EARNEST_SALE = "G"; // 预收定金
	public final static String EARNEST_SALE_HC = "H"; // 红冲预收定金
	public final static String EARNEST_SALE_CLEAR = "I"; // 取消预收定金

	public final static String EARNEST_BACK = "J"; // 定金退货
	public final static String EARNEST_BACK_HC = "K"; // 红冲定金退货
	public final static String EARNEST_BACK_CLEAR = "L"; // 取消定金退货

	public final static String PREPARE_TAKE = "M"; // 预售提货
	public final static String PREPARE_TAKE_HC = "N"; // 红冲预售提货
	public final static String PREPARE_TAKE_CLEAR = "O"; // 取消预售提货

	public final static String PREPARE_SALE = "P"; // 预售销售
	public final static String PREPARE_SALE_HC = "Q"; // 红冲预售销售
	public final static String PREPARE_SALE_CLEAR = "R"; // 取消预售销售

	public final static String PREPARE_BACK = "S"; // 预售退货
	public final static String PREPARE_BACK_HC = "T"; // 红冲预售退货
	public final static String PREPARE_BACK_CLEAR = "U"; // 取消预售退货

	public final static String PURCHANSE_COUPON = "V"; // 买券交易
	public final static String PURCHANSE_COUPON_HC = "W"; // 红冲买券交易
	public final static String PURCHANSE_COUPON_CLEAR = "X"; // 取消买券交易

	public final static String PURCHANSE_COUPON_BACK = "Y"; // 退券交易
	public final static String PURCHANSE_COUPON_BACK_HC = "Z"; // 红冲退券交易
	public final static String PURCHANSE_COUPON_BACK_CLEAR = "["; // 取消退券交易

	public final static String EXERCISE_SALE = ":"; // 练习销售
	public final static String EXERCISE_BACK = ";"; // 练习退货
	public final static String CHECK_INPUT = "="; // 盘点输入
	// public final static String JDFH_SALE= "<"; //家电发货
	// public final static String JDFH_BACK= ">"; //家电退货

	public final static String HH_SALE = "]"; // 换货销售(小换大）
	public final static String HH_BACK = "-"; // 换货退货（大换小）

	public final static String JDXX_FK = "("; // 家电下乡返款
	public final static String JDXX_FK_HC = ")"; // 红冲家电下乡返款
	public final static String JDXX_BACK = "$"; // 家电下乡返款退货
	public final static String JDXX_BACK_HC = "%"; // 红冲家电下乡返款退货

	public final static String JS_FK = "C1"; // 结算
	public final static String JS_FK_HC = "C2"; // 红冲结算
	public final static String JS_FK_BACK = "C4"; // 退结算
	public final static String JS_FK_BACK_HC = "C5"; // 红冲退结算

	public final static String JF_FK = "D1"; // 缴费
	public final static String JF_FK_HC = "D2"; // 红冲缴费
	public final static String JF_FK_BACK = "D4"; // 退缴费
	public final static String JF_FK_BACK_HC = "D5"; // 红冲退缴费

	public final static String GROUPBUY_SALE = "T1"; // 团购销售
	public final static String GROUPBUY_SALE_HC = "T2"; // 红冲团购销售
	public final static String GROUPBUY_SALE_CLEAR = "T3"; // 取消团购销售
	public final static String GROUPBUY_BACK = "T4"; // 团购退货
	public final static String GROUPBUY_BACK_HC = "T5"; // 红冲团购退货
	public final static String GROUPBUY_BACK_CLEAR = "T6"; // 取消团购退货

	public final static String CARD_SALE = "E1"; // 前台售卡
	public final static String CARD_SALE_HC = "E2"; // 红冲售卡
	public final static String CARD_SALE_CLEAR = "E3"; // 取消红冲售卡
	public final static String CARD_BACK = "E4"; // 售卡退货
	public final static String CARD_BACK_HC = "E5"; // 红冲售卡退货
	public final static String CARD_BACK_CLEAR = "E6"; // 取消红冲售卡退货

	public final static String PURCHANSE_JF = "J1"; // 买积分交易
	public final static String PURCHANSE_JF_HC = "J2"; // 红冲买积分交易
	public final static String PURCHANSE_JF_BACK = "J4"; // 退积分交易
	public final static String PURCHANSE_JF_BACK_HC = "J5"; // 红冲退积分交易

	// 超市发印花换购
	public final static String STAMP_SALE = "F1"; // 印花换购销售
	public final static String STAMP_SALE_HC = "F2"; // 印花换购销售红冲
	public final static String STAMP_SALE_CLEAR = "F3"; // 印花换购销售取消
	public final static String STAMP_BACK = "F4"; // 印花换购退货
	public final static String STAMP_BACK_HC = "F5"; // 印花换购退货红冲
	public final static String STAMP_BACK_CLEAR = "F6"; // 印花换购退货取消

	// 客户化功能NMZD
	public final static String PREPARE_SALE1 = "p"; // 预售销售
	public final static String PREPARE_SALE_HC1 = "q"; // 红冲预售销售
	public final static String PREPARE_SALE_CLEAR1 = "r"; // 取消预售销售

	public final static String PREPARE_BACK1 = "s"; // 预售退货
	public final static String PREPARE_BACK_HC1 = "t"; // 红冲预售退货
	public final static String PREPARE_BACK_CLEAR1 = "u"; // 取消预售退货

	public static SellType currentSellType = null;

	public static SellType getDefault()
	{
		if (SellType.currentSellType == null)
		{
			SellType.currentSellType = CustomLocalize.getDefault().createSellType();
		}

		return SellType.currentSellType;
	}

	public String typeExchange(String type, char hhFlag, SaleHeadDef salehead)
	{
		if (type.equals(RETAIL_SALE))
		{
			if (hhFlag == 'Y')
			{
				return Language.apply("换货销售");
			}
			else
			{
				return Language.apply("零售销售");
			}
		}
		else if (type.equals(RETAIL_SALE_HC))
		{
			if (hhFlag == 'Y')
			{
				return Language.apply("冲换货销");
			}
			else
			{
				return Language.apply("红冲销售");
			}
		}
		else if (type.equals(RETAIL_SALE_CLEAR))
			return Language.apply("取消销售");
		else if (type.equals(RETAIL_BACK))
		{
			if (hhFlag == 'Y')
			{
				return Language.apply("换货退货");
			}
			else
			{
				return Language.apply("零售退货");
			}
		}
		else if (type.equals(RETAIL_BACK_HC))
			if (hhFlag == 'Y')
			{
				return Language.apply("冲换货退");
			}
			else
			{
				return Language.apply("红冲退货");
			}
		else if (type.equals(RETAIL_BACK_CLEAR))
			return Language.apply("取消退货");

		else if (type.equals(BATCH_SALE))
			return Language.apply("批发销售");

		else if (type.equals(BATCH_SALE_HC))
			return Language.apply("红冲批销");

		else if (type.equals(BATCH_SALE_CLEAR))
			return Language.apply("取消批销");

		else if (type.equals(BATCH_BACK))
			return Language.apply("批发退货");

		else if (type.equals(BATCH_BACK_HC))
			return Language.apply("红冲批退");

		else if (type.equals(BATCH_BACK_CLEAR))
			return Language.apply("取消批退");

		else if (type.equals(EARNEST_SALE))
			return Language.apply("预收定金");

		else if (type.equals(EARNEST_SALE_HC))
			return Language.apply("冲收定金");

		else if (type.equals(EARNEST_SALE_CLEAR))
			return Language.apply("取消收定");

		else if (type.equals(EARNEST_BACK))
			return Language.apply("退收定金");

		else if (type.equals(EARNEST_BACK_HC))
			return Language.apply("冲退定金");

		else if (type.equals(EARNEST_BACK_CLEAR))
			return Language.apply("消退定金");

		else if (type.equals(PREPARE_TAKE))
			return Language.apply("预售提货");

		else if (type.equals(PREPARE_TAKE_HC))
			return Language.apply("冲预提货");

		else if (type.equals(PREPARE_TAKE_CLEAR))
			return Language.apply("消预提货");

		else if (type.equals(PREPARE_SALE))
			return Language.apply("预售销售");

		else if (type.equals(PREPARE_SALE_HC))
			return Language.apply("红冲预售");

		else if (type.equals(PREPARE_SALE_CLEAR))
			return Language.apply("取消预售");

		else if (type.equals(PREPARE_BACK))
			return Language.apply("预售退货");

		else if (type.equals(PREPARE_BACK_HC))
			return Language.apply("冲预退货");

		else if (type.equals(PREPARE_BACK_CLEAR))
			return Language.apply("消预退货");

		else if (type.equals(EXERCISE_SALE))
			return Language.apply("练习销售");

		else if (type.equals(EXERCISE_BACK))
			return Language.apply("练习退货");

		else if (type.equals(PURCHANSE_COUPON))
			return Language.apply("买券销售");

		else if (type.equals(PURCHANSE_COUPON_HC))
			return Language.apply("红冲买券");

		else if (type.equals(PURCHANSE_COUPON_BACK))
			return Language.apply("买券退货");

		else if (type.equals(PURCHANSE_COUPON_BACK_HC))
			return Language.apply("红冲退券");

		else if (type.equals(CHECK_INPUT))
			return Language.apply("商品盘点");

		else if (type.equals(JDXX_FK))
			return Language.apply("家电返款");

		else if (type.equals(JDXX_FK_HC))
			return Language.apply("返款红冲");

		else if (type.equals(JDXX_BACK))
			return Language.apply("返款退货");

		else if (type.equals(JDXX_BACK_HC))
			return Language.apply("退返红冲");

		else if (type.equals(JS_FK))
			return Language.apply("结算交易");
		else if (type.equals(JS_FK_HC))
			return Language.apply("红冲结算");
		else if (type.equals(JS_FK_BACK))
			return Language.apply("退结交易");
		else if (type.equals(JS_FK_BACK_HC))
			return Language.apply("红冲退结");

		else if (type.equals(JF_FK))
			return Language.apply("缴费交易");
		else if (type.equals(JF_FK_HC))
			return Language.apply("红冲缴费");
		else if (type.equals(JF_FK_BACK))
			return Language.apply("退缴交易");
		else if (type.equals(JF_FK_BACK_HC))
			return Language.apply("红冲缴费");

		else if (type.equals(GROUPBUY_SALE))
			return Language.apply("团购销售");
		else if (type.equals(GROUPBUY_SALE_HC))
			return Language.apply("红冲团购销售");
		else if (type.equals(GROUPBUY_SALE_CLEAR))
			return Language.apply("取消团购销售");
		else if (type.equals(GROUPBUY_BACK))
			return Language.apply("团购退货");
		else if (type.equals(GROUPBUY_BACK_HC))
			return Language.apply("红冲团购退货");
		else if (type.equals(GROUPBUY_BACK_CLEAR))
			return Language.apply("取消团购退货");
		else if (type.equals(CARD_SALE))
			return Language.apply("买卡交易");
		else if (type.equals(CARD_SALE_HC))
			return Language.apply("买卡红冲");
		else if (type.equals(CARD_SALE_CLEAR))
			return Language.apply("取消买卡");
		else if (type.equals(CARD_BACK))
			return Language.apply("退卡交易");
		else if (type.equals(CARD_BACK_HC))
			return Language.apply("退卡红冲");
		else if (type.equals(CARD_BACK_CLEAR))
			return Language.apply("取消退卡");
		else if (type.equals(PREPARE_SALE1))
			return Language.apply("预销售A");
		else if (type.equals(PREPARE_SALE_HC1))
			return Language.apply("预销冲A");
		else if (type.equals(PREPARE_SALE_CLEAR1))
			return Language.apply("预取消A");
		else if (type.equals(PREPARE_BACK1))
			return Language.apply("预退货A");
		else if (type.equals(PREPARE_BACK_HC1))
			return Language.apply("预退冲A");

		else if (type.equals(PURCHANSE_JF))
			return Language.apply("购买积分");
		else if (type.equals(PURCHANSE_JF_HC))
			return Language.apply("红冲买积分");
		else if (type.equals(PURCHANSE_JF_BACK))
			return Language.apply("退买积分");
		else if (type.equals(PURCHANSE_JF_BACK_HC))
			return Language.apply("红冲退买积分");
		else if (type.equals(HH_SALE) || type.equals(HH_BACK))
			return Language.apply("换货");

		else if (type.equals(STAMP_SALE))
			return Language.apply("印花换购");
		else if (type.equals(STAMP_SALE_HC))
			return Language.apply("印花红冲");
		else if (type.equals(STAMP_SALE_CLEAR))
			return Language.apply("印花取消");
		else if (type.equals(STAMP_BACK))
			return Language.apply("印花退货");
		else if (type.equals(STAMP_BACK_HC))
			return Language.apply("印退红冲");
		else if (type.equals(STAMP_BACK_CLEAR))
			return Language.apply("印退取消");

		else
		{
			new MessageBox(Language.apply("出现不明交易类型,请联系电脑中心"));
			return Language.apply("未知交易");
		}

	}

	public static boolean VALIDTYPE(String c)
	{
		if (c.equals(RETAIL_SALE))
			return true;
		if (c.equals(RETAIL_SALE_HC))
			return true;
		if (c.equals(RETAIL_SALE_CLEAR))
			return true;
		if (c.equals(RETAIL_BACK))
			return true;
		if (c.equals(RETAIL_BACK_HC))
			return true;
		if (c.equals(RETAIL_BACK_CLEAR))
			return true;
		if (c.equals(BATCH_SALE))
			return true;
		if (c.equals(BATCH_SALE_HC))
			return true;
		if (c.equals(BATCH_SALE_CLEAR))
			return true;
		if (c.equals(BATCH_BACK))
			return true;
		if (c.equals(BATCH_BACK_HC))
			return true;
		if (c.equals(BATCH_BACK_CLEAR))
			return true;
		if (c.equals(EARNEST_SALE))
			return true;
		if (c.equals(EARNEST_SALE_HC))
			return true;
		if (c.equals(EARNEST_SALE_CLEAR))
			return true;
		if (c.equals(EARNEST_BACK))
			return true;
		if (c.equals(EARNEST_BACK_HC))
			return true;
		if (c.equals(EARNEST_BACK_CLEAR))
			return true;
		if (c.equals(PREPARE_TAKE))
			return true;
		if (c.equals(PREPARE_TAKE_HC))
			return true;
		if (c.equals(PREPARE_TAKE_CLEAR))
			return true;
		if (c.equals(PREPARE_SALE))
			return true;
		if (c.equals(PREPARE_SALE_HC))
			return true;
		if (c.equals(PREPARE_SALE_CLEAR))
			return true;
		if (c.equals(PREPARE_BACK))
			return true;
		if (c.equals(PREPARE_BACK_HC))
			return true;
		if (c.equals(PREPARE_BACK_CLEAR))
			return true;
		if (c.equals(PREPARE_SALE1))
			return true;
		if (c.equals(PREPARE_SALE_HC1))
			return true;
		if (c.equals(PREPARE_SALE_CLEAR1))
			return true;
		if (c.equals(PREPARE_BACK1))
			return true;
		if (c.equals(PREPARE_BACK_HC1))
			return true;
		if (c.equals(PREPARE_BACK_CLEAR1))
			return true;
		if (c.equals(EXERCISE_SALE))
			return true;
		if (c.equals(EXERCISE_BACK))
			return true;
		if (c.equals(PURCHANSE_COUPON))
			return true;
		if (c.equals(PURCHANSE_COUPON_HC))
			return true;
		if (c.equals(PURCHANSE_COUPON_BACK))
			return true;
		if (c.equals(PURCHANSE_COUPON_BACK))
			return true;
		if (c.equals(PURCHANSE_COUPON_BACK))
			return true;
		if (c.equals(PURCHANSE_COUPON_BACK))
			return true;
		if (c.equals(PURCHANSE_COUPON_BACK_HC))
			return true;
		if (c.equals(JDXX_FK))
			return true;
		if (c.equals(JDXX_FK_HC))
			return true;
		if (c.equals(JDXX_BACK))
			return true;
		if (c.equals(JDXX_BACK_HC))
			return true;

		if (c.equals(JS_FK))
			return true;
		if (c.equals(JS_FK_HC))
			return true;
		if (c.equals(JS_FK_BACK))
			return true;
		if (c.equals(JS_FK_BACK_HC))
			return true;
		if (c.equals(JF_FK))
			return true;
		if (c.equals(JF_FK_HC))
			return true;
		if (c.equals(JF_FK_BACK))
			return true;
		if (c.equals(JF_FK_BACK_HC))
			return true;

		if (c.equals(GROUPBUY_SALE))
			return true;
		if (c.equals(GROUPBUY_SALE_HC))
			return true;
		if (c.equals(GROUPBUY_SALE_CLEAR))
			return true;
		if (c.equals(GROUPBUY_BACK))
			return true;
		if (c.equals(GROUPBUY_BACK_HC))
			return true;
		if (c.equals(GROUPBUY_BACK_CLEAR))
			return true;

		if (c.equals(CARD_SALE))
			return true;
		if (c.equals(CARD_SALE_HC))
			return true;
		if (c.equals(CARD_SALE_CLEAR))
			return true;
		if (c.equals(CARD_BACK))
			return true;
		if (c.equals(CARD_BACK_HC))
			return true;
		if (c.equals(CARD_BACK_CLEAR))
			return true;

		if (c.equals(PURCHANSE_JF))
			return true;
		if (c.equals(PURCHANSE_JF_HC))
			return true;
		if (c.equals(PURCHANSE_JF_BACK))
			return true;
		if (c.equals(PURCHANSE_JF_BACK_HC))
			return true;
		if (c.equals(HH_SALE) || c.equals(HH_BACK))
			return true;

		if (c.equals(STAMP_SALE))
			return true;
		if (c.equals(STAMP_SALE_HC))
			return true;
		if (c.equals(STAMP_SALE_CLEAR))
			return true;
		if (c.equals(STAMP_BACK))
			return true;
		if (c.equals(STAMP_BACK_HC))
			return true;
		if (c.equals(STAMP_BACK_CLEAR))
			return true;

		return false;
	}

	// 该函数返回交易类型的数据正负号
	public static int SELLSIGN(String c)
	{
		if (ISClEAR(c)) { return 0; }

		if ((c.equals(RETAIL_SALE)) || (c.equals(RETAIL_BACK_HC)) || 
				(c.equals(BATCH_SALE)) || (c.equals(BATCH_BACK_HC)) ||
				(c.equals(EARNEST_SALE)) || (c.equals(EARNEST_BACK_HC)) || 
				(c.equals(PREPARE_TAKE)) || (c.equals(PREPARE_SALE)) ||
				(c.equals(PREPARE_BACK_HC)) || (c.equals(PREPARE_SALE1)) || 
				(c.equals(PREPARE_BACK_HC1)) || (c.equals(EXERCISE_SALE)) || 
				(c.equals(PURCHANSE_COUPON)) || (c.equals(CHECK_INPUT)) || 
				(c.equals(JDXX_BACK)) || (c.equals(JDXX_FK_HC)) || 
				(c.equals(JS_FK) || c.equals(JS_FK_BACK_HC)) || 
				(c.equals(JF_FK) || c.equals(JF_FK_BACK_HC)) || 
				(c.equals(GROUPBUY_SALE) || c.equals(GROUPBUY_BACK_HC)
						|| c.equals(PURCHANSE_COUPON_BACK_HC) 
						|| c.equals(CARD_SALE)) || (c.equals(HH_SALE)) 
						|| (c.equals(PURCHANSE_JF) || c.equals(PURCHANSE_JF_BACK_HC))
						||(c.equals(STAMP_SALE)||c.equals(STAMP_BACK_HC)))
		{
			return 1;
		}
		else
		{
			return -1;
		}
	}

	public static boolean NOPOP(String c)
	{
		if (c.equals(JS_FK))
			return true;

		if (c.equals(JF_FK))
			return true;

		if (c.equals(SellType.GROUPBUY_SALE))
			return true;

		if (c.equals(SellType.CARD_SALE))
			return true;

		return false;
	}

	public static boolean ISSALE(String c)
	{
		if (c.equals(RETAIL_SALE))
			return true;
		if (c.equals(BATCH_SALE))
			return true;
		if (c.equals(EARNEST_SALE))
			return true;
		if (c.equals(PREPARE_TAKE))
			return true;
		if (c.equals(PREPARE_SALE))
			return true;
		if (c.equals(PREPARE_SALE1))
			return true;
		if (c.equals(EXERCISE_SALE))
			return true;
		if (c.equals(PURCHANSE_COUPON))
			return true;
		if (c.equals(JS_FK))
			return true;
		if (c.equals(JF_FK))
			return true;
		if (c.equals(GROUPBUY_SALE))
			return true;
		if (c.equals(CARD_SALE))
			return true;
		if (c.equals(PURCHANSE_JF))
			return true;
		if (c.equals(STAMP_SALE))
			return true;
		if (c.equals(HH_SALE))
			return true;
		// if (c .equals( JDXX_BACK) return true;

		return false;
	}

	public static boolean ISBACK(String c)
	{
		if (c.equals(RETAIL_BACK))
			return true;
		if (c.equals(BATCH_BACK))
			return true;
		if (c.equals(EARNEST_BACK))
			return true;
		if (c.equals(PREPARE_BACK))
			return true;
		if (c.equals(PREPARE_BACK1))
			return true;
		if (c.equals(EXERCISE_BACK))
			return true;
		if (c.equals(PURCHANSE_COUPON_BACK))
			return true;

		if (c.equals(JS_FK_BACK))
			return true;
		if (c.equals(JF_FK_BACK))
			return true;
		if (c.equals(GROUPBUY_BACK))
			return true;
		if (c.equals(CARD_BACK))
			return true;
		if (c.equals(PURCHANSE_JF_BACK))
			return true;
		if (c.equals(STAMP_BACK))
			return true;
		if (c.equals(HH_BACK))
			return true;

		return false;
	}

	public static boolean ISBATCH(String c)
	{
		if (c.equals(BATCH_SALE))
			return true;
		if (c.equals(BATCH_BACK))
			return true;

		return false;
	}

	public static boolean ISClEAR(String c)
	{
		if (c.equals(RETAIL_SALE_CLEAR))
			return true;
		if (c.equals(RETAIL_BACK_CLEAR))
			return true;
		if (c.equals(BATCH_SALE_CLEAR))
			return true;
		if (c.equals(BATCH_BACK_CLEAR))
			return true;
		if (c.equals(EARNEST_SALE_CLEAR))
			return true;
		if (c.equals(EARNEST_BACK_CLEAR))
			return true;
		if (c.equals(PREPARE_TAKE_CLEAR))
			return true;
		if (c.equals(PREPARE_SALE_CLEAR))
			return true;
		if (c.equals(PREPARE_SALE_CLEAR1))
			return true;
		if (c.equals(PREPARE_BACK_CLEAR))
			return true;
		if (c.equals(PREPARE_BACK_CLEAR1))
			return true;
		if (c.equals(PURCHANSE_COUPON_BACK_CLEAR))
			return true;
		if (c.equals(PURCHANSE_COUPON_CLEAR))
			return true;
		if (c.equals(GROUPBUY_SALE_CLEAR))
			return true;
		if (c.equals(GROUPBUY_BACK_CLEAR))
			return true;
		if (c.equals(CARD_SALE_CLEAR))
			return true;
		if (c.equals(CARD_BACK_CLEAR))
			return true;
		if (c.equals(STAMP_SALE_CLEAR))
			return true;

		return false;
	}

	public static boolean ISHC(String c)
	{
		if (c.equals(RETAIL_SALE_HC))
			return true;
		if (c.equals(RETAIL_BACK_HC))
			return true;
		if (c.equals(BATCH_SALE_HC))
			return true;
		if (c.equals(BATCH_BACK_HC))
			return true;
		if (c.equals(EARNEST_SALE_HC))
			return true;
		if (c.equals(EARNEST_BACK_HC))
			return true;
		if (c.equals(PREPARE_TAKE_HC))
			return true;
		if (c.equals(PREPARE_SALE_HC))
			return true;
		if (c.equals(PREPARE_BACK_HC))
			return true;
		if (c.equals(PREPARE_SALE_HC1))
			return true;
		if (c.equals(PREPARE_BACK_HC1))
			return true;
		if (c.equals(PURCHANSE_COUPON_HC))
			return true;
		if (c.equals(PURCHANSE_COUPON_BACK_HC))
			return true;
		if (c.equals(JDXX_FK_HC))
			return true;
		if (c.equals(JDXX_BACK_HC))
			return true;
		if (c.equals(GROUPBUY_SALE_HC))
			return true;
		if (c.equals(GROUPBUY_BACK_HC))
			return true;
		if (c.equals(CARD_SALE_HC))
			return true;
		if (c.equals(CARD_BACK_HC))
			return true;
		if (c.equals(PURCHANSE_JF_HC))
			return true;
		if (c.equals(PURCHANSE_JF_BACK_HC))
			return true;
		if (c.equals(STAMP_SALE_HC))
			return true;
		if (c.equals(STAMP_BACK_HC))
			return true;
		return false;
	}

	public static boolean ISEXERCISE(String c)
	{
		if (c.equals(EXERCISE_SALE))
			return true;
		if (c.equals(EXERCISE_BACK))
			return true;

		return false;
	}

	public static boolean ISEARNEST(String c)
	{
		if (c.equals(EARNEST_SALE))
			return true;
		if (c.equals(EARNEST_BACK))
			return true;

		return false;
	}

	public static boolean ISCOUPON(String c)
	{
		if (c.equals(PURCHANSE_COUPON))
			return true;
		if (c.equals(PURCHANSE_COUPON_BACK))
			return true;
		return false;
	}

	public static boolean ISHH(String c)
	{
		if (c.equals(HH_SALE))
			return true;
		if (c.equals(HH_BACK))
			return true;
		return false;
	}

	public static boolean ISCHECKINPUT(String c)
	{
		if (c.equals(CHECK_INPUT))
			return true;
		return false;
	}

	public static boolean ISPREPARETAKE(String c)
	{
		if (c.equals(PREPARE_TAKE))
			return true;

		return false;
	}

	public static boolean ISPREPARE(String c)
	{
		if (c.equals(PREPARE_SALE))
			return true;
		if (c.equals(PREPARE_BACK))
			return true;
		if (c.equals(PREPARE_SALE1))
			return true;
		if (c.equals(PREPARE_BACK1))
			return true;

		return false;
	}

	// 判断是否家电下乡返款
	public static boolean ISJDXXFK(String c)
	{
		if (c.equals(JDXX_FK))
			return true;

		return false;
	}

	// 判断是否家电下乡退还返款
	public static boolean ISJDXXFKTH(String c)
	{
		if (c.equals(JDXX_BACK))
			return true;

		return false;
	}

	public static boolean isJF(String c)// 缴费交易
	{
		if (c.equals(JF_FK))
			return true;
		if (c.equals(JF_FK_HC))
			return true;
		if (c.equals(JF_FK_BACK))
			return true;
		if (c.equals(JF_FK_BACK_HC))
			return true;

		return false;
	}

	public static boolean isJS(String c)
	{
		if (c.equals(JS_FK))
			return true;
		if (c.equals(JS_FK_HC))
			return true;
		if (c.equals(JS_FK_BACK))
			return true;
		if (c.equals(JS_FK_BACK_HC))
			return true;
		return false;
	}

	// 判断是否为团购交易
	public static boolean isGroupbuy(String c)
	{
		if (c.equals(GROUPBUY_SALE))
			return true;
		if (c.equals(GROUPBUY_SALE_HC))
			return true;
		if (c.equals(GROUPBUY_SALE_CLEAR))
			return true;
		if (c.equals(GROUPBUY_BACK))
			return true;
		if (c.equals(GROUPBUY_BACK_HC))
			return true;
		if (c.equals(GROUPBUY_BACK_CLEAR))
			return true;
		return false;
	}

	public static boolean ISCARD(String c)
	{
		if (c.equals(CARD_SALE))
			return true;
		if (c.equals(CARD_BACK))
			return true;
		return false;
	}

	public static boolean ISSTAMP(String c)
	{
		if (c.equals(STAMP_SALE))
			return true;
		if (c.equals(STAMP_BACK))
			return true;
		return false;
	}

	// 判断是否高亮
	public boolean COMMONBUSINESS(String djlb, char hhflag, SaleHeadDef salehead)
	{
		if ((djlb.equals(SellType.RETAIL_SALE)) && hhflag != 'Y') { return true; }
		return false;
	}

	// 判断是否换货销售
	public static boolean ISHHSALE(String djlb, char hhflag)
	{
		if (djlb.equals(SellType.RETAIL_SALE) && hhflag == 'Y') { return true; }
		return false;
	}

	// 判断是否买积分
	public static boolean ISJFSALE(String c)
	{
		if (c.equals(PURCHANSE_JF))
			return true;
		if (c.equals(PURCHANSE_JF_HC))
			return true;
		if (c.equals(PURCHANSE_JF_BACK))
			return true;
		if (c.equals(PURCHANSE_JF_BACK_HC))
			return true;
		return false;
	}

	// 从销售交易类别转换为退货交易类别
	public static String getDjlbSaleToBack(String saletype)
	{
		if (saletype.trim().length() == 1)
		{
			char saletype1 = saletype.charAt(0);
			if (SellType.ISEXERCISE(saletype))
			{
				saletype1 += 1;
			}
			else if (SellType.ISJDXXFK(saletype))
			{
				saletype1 = SellType.JDXX_BACK.charAt(0);
			}
			else
			{
				saletype1 += 3;
			}
			return String.valueOf(saletype1);
		}
		else if (saletype.trim().length() > 1)
		{
			StringBuffer buff = new StringBuffer(saletype);
			char saletype1 = saletype.charAt(saletype.length() - 1);
			if (SellType.ISEXERCISE(saletype))
			{
				saletype1 += 1;
			}
			else if (SellType.ISJDXXFK(saletype))
			{
				saletype1 = SellType.JDXX_BACK.charAt(0);
			}
			else
			{
				saletype1 += 3;
			}
			buff.setCharAt(saletype.length() - 1, saletype1);
			return buff.toString();
		}
		return saletype;
	}

	// 从退货交易类别转换为销售交易类别
	public static String getDjlbBackToSale(String saletype)
	{
		if (saletype != null && SellType.ISHH(saletype))
			return SellType.RETAIL_SALE;

		if (saletype.trim().length() == 1)
		{
			char saletype1 = saletype.charAt(0);
			if (SellType.ISEXERCISE(saletype))
			{
				saletype1 -= 1;
			}
			else
			{
				saletype1 -= 3;
			}
			return String.valueOf(saletype1);
		}
		else if (saletype.trim().length() > 1)
		{
			StringBuffer buff = new StringBuffer(saletype);
			char saletype1 = saletype.charAt(saletype.length() - 1);
			if (SellType.ISEXERCISE(saletype))
			{
				saletype1 -= 1;
			}
			else
			{
				saletype1 -= 3;
			}
			buff.setCharAt(saletype.length() - 1, saletype1);
			return buff.toString();
		}

		return saletype;
	}

	// 从任意类别转换为红冲交易类别
	public static String getReFlush(String saletype)
	{
		if (saletype.trim().length() == 1)
		{
			char saletype1 = saletype.charAt(0);
			if (SellType.ISJDXXFK(saletype))
			{
				return SellType.JDXX_FK_HC;
			}
			else if (SellType.ISJDXXFKTH(saletype))
			{
				return SellType.JDXX_BACK_HC;
			}
			else
			{
				saletype1 += 1;
			}
			return String.valueOf(saletype1);
		}
		else if (saletype.trim().length() > 1)
		{
			StringBuffer buff = new StringBuffer(saletype);
			char saletype1 = saletype.charAt(saletype.length() - 1);
			if (SellType.ISJDXXFK(saletype))
			{
				return SellType.JDXX_FK_HC;
			}
			else if (SellType.ISJDXXFKTH(saletype))
			{
				return SellType.JDXX_BACK_HC;
			}
			else
			{
				saletype1 += 1;
			}
			buff.setCharAt(saletype.length() - 1, saletype1);
			return buff.toString();
		}

		return saletype;
	}

}
