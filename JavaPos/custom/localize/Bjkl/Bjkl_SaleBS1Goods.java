package custom.localize.Bjkl;

import java.util.ArrayList;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsJFRule;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.ShopPreSaleDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Bjkl_SaleBS1Goods extends Bjkl_SaleBS0CalcPop
{
	public boolean allowEditGoods()
	{
		if (SellType.ISCARD(saletype))
		{
			new MessageBox("售卡交易不允许修改!");
			return false;
		}
		return super.allowEditGoods();
	}

	public boolean superFindGoods(String code, String yyyh, String gz, String memo)
	{
		String comcode = "";
		String barcode = "";
		boolean isdzcm;
		double dzcmjg = 0;
		double dzcmsl = 0;
		String dzcmscsj = "";
		double quantity = 1;
		double price = 0;

		// 检查是否允许找商品
		if (!allowStartFindGoods())
			return false;

		// 分解输入码 数量*编码
		String[] s = convertQuantityBarcode(code);
		if (s == null)
			return false;
		quantity = Convert.toDouble(s[0]);
		barcode = s[1];

		// 解析电子秤码
		String[] codeInfo = new String[4];
		isdzcm = analyzeBarcode(barcode, codeInfo);

		if (isdzcm)
		{
			comcode = codeInfo[0];
			dzcmjg = ManipulatePrecision.doubleConvert(Double.parseDouble(codeInfo[1]), 2, 1);
			dzcmsl = ManipulatePrecision.doubleConvert(Double.parseDouble(codeInfo[2]), 4, 1);

			// 京客隆特色修改。商品论个买，当数量小于 0.01且 大于0时，表示一个。
			if (dzcmsl < 0.01 && dzcmsl > 0)
			{
				dzcmsl = 1;
			}

			dzcmscsj = codeInfo[3];

			// 验证电子秤校验位
			if (!verifyDzcmCheckbit(barcode))
			{
				new MessageBox("电子秤码校验位错误", null, false);

				return false;
			}

			if (dzcmjg <= 0 && dzcmsl <= 0)
			{
				new MessageBox("该电子秤格式条码无效", null, false);

				return false;
			}
		}
		else
		{
			comcode = barcode;
		}

		// 查找详细商品资料,可支持数量转换
		StringBuffer slbuf = new StringBuffer("1");

		GoodsDef goodsDef = findGoodsInfo(comcode, yyyh, gz, dzcmscsj, isdzcm, slbuf);
		if (goodsDef == null)
			return false;

		// 促销时，一品多码问题。一个编码对应多个条码，规格 uid 不一致，而促销里面此规格字段为 00，那么比较时将导致促销规则不匹配
		goodsDef.uid = "00";

		quantity *= Convert.toDouble(slbuf.toString());

		// 获得最小批量数量
		quantity = getMinPlsl(quantity, goodsDef);

		// 电子秤商品记录原始电子秤码
		goodsDef.inputbarcode = barcode;
		// isdzcm = true;
		if (isdzcm)
		{
			goodsDef.barcode = convertDzcmBarcode(goodsDef, barcode, isdzcm);
			goodsDef.isdzc = 'Y';
			// 京客隆促销价格传到电子称，这里把促销价格poplsj 赋给 商品零售价 lsj，用于反算数量
			// goodsDef.lsj = goodsDef.poplsj;
		}

		// 设置商品缺省售价
		price = setGoodsDefaultPrice(goodsDef);

		// 当电子称价格因子为1时，表示商品数量按个卖，忽略掉条码上的重量
		if (GlobalInfo.sysPara.enabledzcCoefficient == 'Y')
		{
			if (Convert.toDouble(goodsDef.str1) == 1)
			{
				String dscsl = String.valueOf(dzcmsl);
				dscsl = dscsl.substring(dscsl.indexOf(".") + 1);

				if (Convert.toInt(dscsl) > 0)
				{
					new MessageBox("电子称数量不合法\n价格因子为1的商品,数量不允许出现小数");
					return false;
				}
			}
		}

		// 京客隆要求，goodsDef.lsj 零售价为1的商品为联营商品，默认让收银员输入商品数量
		if (!isdzcm && goodsDef.num2 == 1)
		{
			StringBuffer sb = new StringBuffer("1");
			TextBox txt = new TextBox();

			// 只能刷卡
			if (!txt.open(Language.apply("请输入商品数量"), Language.apply("商品"), "", sb, 0, getMaxSaleGoodsQuantity(), true, TextBox.DoubleInput))
				return false;

			quantity = Convert.toDouble(sb.toString());

			if (goodsDef.isdzc == 'Y')
			{
				dzcmsl = quantity;
				// allprice = quantity * goodsDef.lsj;
			}
		}

		// 电子秤码没有通过条码解析销售，补入商品价格或数量
		if (goodsDef.isdzc == 'Y' && !isdzcm)
		{
			// 输入价格模式
			if (GlobalInfo.sysPara.dzccodesale == 'Y')
			{
				isdzcm = true;

				StringBuffer pricestr = new StringBuffer();
				do
				{
					pricestr.delete(0, pricestr.length());
					pricestr.append(price);

					boolean done = new TextBox().open("请输入商品[" + goodsDef.inputbarcode + "]" + (goodsDef.name.trim().length() > 20 ? goodsDef.name.trim().substring(0, 19) : goodsDef.name.trim()) + "价格", "价格", "", pricestr, 0.01, getMaxSaleGoodsMoney(), true);

					if (!done)
					{
						return false;
					}
					else
					{
						dzcmjg = ManipulatePrecision.doubleConvert(getConvertPrice(Double.parseDouble(pricestr.toString()), goodsDef), 2, 1);

						if (dzcmjg <= 0)
						{
							new MessageBox("该商品价格必须大于0");
						}
						else
						{
							break;
						}
					}
				}
				while (true);
			}

			// 输入数量模式
			if (GlobalInfo.sysPara.dzccodesale == 'A')
			{
				isdzcm = true;

				StringBuffer slstr = new StringBuffer();
				do
				{
					slstr.delete(0, slstr.length());
					slstr.append(0.000);

					boolean done = new TextBox().open("请输入商品[" + goodsDef.inputbarcode + "]" + (goodsDef.name.trim().length() > 20 ? goodsDef.name.trim().substring(0, 19) : goodsDef.name.trim()) + "数量", "数量", "", slstr, 0.01, getMaxSaleGoodsQuantity(), true);

					if (!done)
					{
						return false;
					}
					else
					{
						dzcmsl = Double.parseDouble(slstr.toString());

						if (dzcmsl <= 0)
						{
							new MessageBox("该商品数量必须大于0");
						}
						else
						{
							break;
						}
					}
				}
				while (true);
			}

			// 默认模式，数量默认为1
			if (GlobalInfo.sysPara.dzccodesale == 'B')
			{
				isdzcm = true;
				if (dzcmsl <= 0)
				{
					dzcmsl = 1;
				}
			}
		}

		// 电子秤条码的数量价格处理
		int dzcprice = 0;
		double allprice = 0;

		if (isdzcm)
		{
			dzcmjgzk = 0;

			if ((dzcmsl > 0) && (dzcmjg <= 0)) // 只有数量
			{
				// bzhl记录电子秤称重单位和商品主档单位的转换比例
				if (goodsDef.bzhl <= 0)
					goodsDef.bzhl = 1;
				quantity = ManipulatePrecision.doubleConvert(dzcmsl / goodsDef.bzhl, 4, 1);
				price = ManipulatePrecision.doubleConvert(goodsDef.lsj, 2, 1);
				allprice = quantity * price;
				dzcprice = 1;

				// 电子秤打印的合计一般都是从第三位截断再四舍五入
				allprice = ManipulatePrecision.doubleConvert(allprice, 3, 0);
				allprice = ManipulatePrecision.doubleConvert(allprice, 2, 1);

				// 按价格精度进行计算,差额记折扣
				double jg = getConvertPrice(allprice, goodsDef);
				if (ManipulatePrecision.doubleCompare(allprice, jg, 2) != 0)
				{
					dzcmjgzk = ManipulatePrecision.sub(allprice, jg);
				}
			}
			else if ((dzcmsl <= 0) && (dzcmjg > 0)) // 只有金额
			{
				goodsDef.num4 = 1;

				if (goodsDef.lsj <= 0) // 不定价商品
				{
					quantity = 1;
					price = dzcmjg;
					allprice = price;
					dzcprice = 1;
				}
				else
				// 定价商品,反算数量
				{
					// pfj存放电子秤实际秤上的价格(可能是促销价),如果和商品主档价格不一致,说明有促销,
					// 用秤的价格反算出数量然后再正常计算促销
					if ((goodsDef.pfj > 0 && ManipulatePrecision.doubleCompare(goodsDef.lsj, goodsDef.pfj, 2) == 0))
					{
						quantity = ManipulatePrecision.doubleConvert((dzcmjg / goodsDef.pfj), 4, 1);
						price = goodsDef.lsj;
						allprice = ManipulatePrecision.doubleConvert(quantity * price);
						dzcprice = 2;

						if (SellType.ISBACK(saletype))
						{
							dzcmjgzk = allprice - ManipulatePrecision.doubleConvert(quantity * goodsDef.pfj);
						}
					}
					else
					{
						// 1.当电子称商品存在促销价时，京客隆促销价格传到电子称，这里用促销价格poplsj 反算数量
						// goodsDef.lsj = goodsDef.poplsj;
						double jg = goodsDef.lsj;
						if (goodsDef.poplsj > 0 && goodsDef.poplsj < goodsDef.lsj)
						{
							jg = goodsDef.poplsj;
						}
						quantity = ManipulatePrecision.doubleConvert((dzcmjg / jg), 4, 1);
						price = goodsDef.lsj;
						// allprice = dzcmjg;
						// 2. 这时电子称商品总金额 通过正常售价 和数量计算，在之后做促销时，再计算电子称商品优惠后 总金额
						allprice = ManipulatePrecision.doubleConvert(quantity * price);
						dzcprice = 2;
					}
				}
			}
			else if ((dzcmsl > 0) && (dzcmjg > 0)) // 即有数量又有价格
			{
				goodsDef.num4 = 2;

				// bzhl记录电子秤称重单位和商品主档单位的转换比例
				// 如果定价商品单价*数量的成交金额已经与秤的成交价四舍五入精度后一致,则无需重算商品单价
				if (goodsDef.bzhl <= 0)
					goodsDef.bzhl = 1;
				quantity = ManipulatePrecision.doubleConvert(dzcmsl / goodsDef.bzhl, 4, 1);
				allprice = dzcmjg;
				if (goodsDef.lsj > 0 && ManipulatePrecision.doubleCompare(goodsDef.lsj * quantity, allprice, ManipulatePrecision.getDoubleScale(allprice)) == 0)
				{
					// 电子秤的成交价可能到角,秤的成交价和数量*单价到分的成交价之间的四舍五入差额记折扣
					if (ManipulatePrecision.doubleCompare(goodsDef.lsj * quantity, allprice, 2) != 0)
					{
						allprice = ManipulatePrecision.doubleConvert(goodsDef.lsj * quantity, 2, 1);
						dzcmjgzk = ManipulatePrecision.sub(allprice, dzcmjg);
						dzcmjgzk = ManipulatePrecision.doubleConvert(dzcmjgzk, 2, 1);
					}
				}
				else
				{
					goodsDef.lsj = goodsDef.hyj = goodsDef.pfj = ManipulatePrecision.doubleConvert(dzcmjg / (dzcmsl / goodsDef.bzhl), 2, 1);
				}
				price = goodsDef.lsj;
			}
		}

		// 当销售数量大于库存数量时，通过 goodsDef.num2 保存修改后的数量，因此这里把 字段值设为 0 ，以防影响下面判断
		double num = 0;
		if (goodsDef.num3 > 0)
		{
			num = goodsDef.num3;
			goodsDef.num3 = 0;
		}

		// 检查找到的商品是否允许销售
		if (!checkFindGoodsAllowSale(goodsDef, quantity, isdzcm, dzcmsl, dzcmjg))
			return false;

		// 当销售数量大于库存数量时，重新设置商品数量，使用goodsDef.num2记录了修改后的商品数量 重新赋给 quantity
		if (goodsDef.num3 > 0)
		{
			quantity = goodsDef.num3;
			goodsDef.num3 = num;
		}

		// 未定价商品或退货或批发要求输入售价
		if (isPriceConfirm(goodsDef))
		{
			// 指定小票退货,查询退货原始交易信息
			if (isSpecifyBack())
			{
				Vector back = new Vector();

				if (!DataService.getDefault().getBackGoodsDetail(back, thSyjh, String.valueOf(thFphm), goodsDef.code, goodsDef.gz, goodsDef.uid)) { return false; }

				int cho = 0;
				if (back.size() > 1)
				{
					Vector choice = new Vector();
					String[] title = { "商品编码", "数量", "单价", "合计折扣", "应付金额" };
					int[] width = { 100, 100, 100, 100, 100 };
					String[] row = null;
					for (int j = 0; j < back.size(); j++)
					{
						thSaleGoods = (SaleGoodsDef) back.elementAt(j);
						row = new String[5];
						row[0] = thSaleGoods.code;
						row[1] = ManipulatePrecision.doubleToString(thSaleGoods.sl, 4, 1, true);
						row[2] = ManipulatePrecision.doubleToString(thSaleGoods.lsj, 2, 1);
						row[3] = ManipulatePrecision.doubleToString(thSaleGoods.hjzk, 2, 1);
						row[4] = ManipulatePrecision.doubleToString(thSaleGoods.hjje - thSaleGoods.hjzk, 2, 1);
						choice.add(row);
					}

					cho = new MutiSelectForm().open("请选择退货商品信息", title, width, choice);
				}
				thSaleGoods = (SaleGoodsDef) back.elementAt(cho);

				if (thSaleGoods.sl < quantity)
				{
					new MessageBox("该商品退货数量大于原销售数量\n\n不能退货");
					thSaleGoods = null;
					return false;
				}
			}

			// 如果是指定小票退货，不进行价格确认
			// 如果是电子秤商品且价格确定，不进行价格确认
			if (!isConfirmPrice(isdzcm, dzcprice, goodsDef))
			{
			}
			else
			{
				if (!isonlinegdjging && memo == null)
				{
					StringBuffer pricestr = new StringBuffer();
					do
					{
						pricestr.delete(0, pricestr.length());
						// pricestr.append(price);
						pricestr.append(goodsDef.lsj);

						String strprice = setGoodsLSJ(goodsDef, pricestr);
						if (strprice == null) { return false; }
						price = ManipulatePrecision.doubleConvert(getConvertPrice(Double.parseDouble(strprice), goodsDef), 2, 1);

						if (GlobalInfo.sysPara.isGoodsMoney0 != 'Y' && price <= 0)
						{
							// 京客隆要求 正常商品价格为0 可以销售
							break;
							// 检查价格
							// if (price <= 0 && goodsDef.type != 'Z')
							// {
							// new MessageBox("该商品价格必须大于0");
							// }
						}
						else
						{
							// 电子秤商品重新计算
							if (isdzcm && (dzcprice > 0))
							{
								if (dzcprice == 1)
								{
									allprice = quantity * price;
								}
								else
								{
									quantity = ManipulatePrecision.doubleConvert(dzcmjg / price, 4, 1);
								}
							}

							// 是否允许在商品退货时,商品是否在下限和上限的价格之内
							if (!isAllowedBackPriceLimit(goodsDef, price))
								continue;

							break;
						}

					}
					while (true);
				}
			}
		}

		// 如果是联网挂单状态，则不输入商品附加信息
		if (!isonlinegdjging && !inputGoodsAddInfo(goodsDef))
			return false;

		// 检查找到的商品最后是否OK
		if (!allowFinishFindGoods(goodsDef, quantity, price))
			return false;

		// 增加商品到商品明细中
		if (!addSaleGoods(goodsDef, yyyh, quantity, price, allprice, isdzcm))
			return false;

		return true;

	}

	public String setGoodsLSJ(GoodsDef goodsDef, StringBuffer pricestr)
	{
		double min = 0.00;

		// if (goodsDef.type == 'Z')
		// {
		// min = 0;
		// }

		if (SellType.ISBATCH(saletype))
		{
			pricestr.delete(0, pricestr.length());
			pricestr.append(goodsDef.pfj);
		}

		boolean done = new TextBox().open(Language.apply("请输入商品[") + goodsDef.inputbarcode + "]" + (goodsDef.name.trim().length() > 20 ? goodsDef.name.trim().substring(0, 19) : goodsDef.name.trim()) + Language.apply("价格"), Language.apply("价格"), "", pricestr, min, getMaxSaleGoodsMoney(), true);
		if (!done)
			return null;
		return pricestr.toString();

	}

	public boolean findGoods(String code, String yyyh, String gz, String memo)
	{

		if (SellType.ISCARD(saletype))
		{
			new MessageBox("售卡交易不允许添加商品!");
			return false;
		}

		// if (preSale != null)
		// {
		// new MessageBox("果篮/礼盒销售不允许新增商品!");
		// return false;
		// }

		// 处理贴花码销售
		if (code.length() == 13 && !GlobalInfo.sysPara.stampprefix.equals("") && code.startsWith(GlobalInfo.sysPara.stampprefix))
		{
			if (saleGoods.size() == 0)
			{
				new MessageBox("请录入贴花码对应的商品!");
				return false;
			}

			if (!verifyDzcmCheckbit(code))
			{
				new MessageBox("错误的贴花码!");
				return false;
			}

			String[] cutpriceinfo = new String[2];

			// 待定2 900939 2100 01
			cutpriceinfo[0] = code.substring(2, 8);
			cutpriceinfo[1] = code.substring(8, 12);

			GoodsDef gds = (GoodsDef) goodsAssistant.get(goodsAssistant.size() - 1);

			// 先整体匹配
			if (!gds.code.equals(cutpriceinfo[0]))
			{
				// 转成整形再次匹配
				if (Convert.toInt(gds.code) != Convert.toInt(cutpriceinfo[0]))
				{
					new MessageBox("该贴花码无法对商品[" + gds.barcode + "]进行打折!");

					return false;
				}
			}

			SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(saleGoods.size() - 1);

			if (sgd.fhdd != null && sgd.fhdd.startsWith(GlobalInfo.sysPara.stampprefix) && sgd.fhdd.trim().length() == 13)
			{
				new MessageBox("商品[" + gds.barcode + "]已享受贴花折扣!");
				return false;
			}

			// 清除折扣，
			// clearGoodsAllRebate(saleGoods.size() - 1);

			if (ManipulatePrecision.doubleConvert(sgd.hjje - sgd.hjzk - Convert.toDouble(cutpriceinfo[1]) / 10, 2, 1) < 0)
			{
				new MessageBox("此贴花折扣已超出商品目前成交价!");
				return false;
			}

			// 记录到临时折扣
			sgd.lszke += ManipulatePrecision.doubleConvert(Convert.toDouble(cutpriceinfo[1]) / 10, 2, 1);
			// 记录贴花码到发货地点字段
			sgd.fhdd = code;

			getZZK(sgd);
			calcHeadYsje();
			refreshSaleForm();
			return true;
		}
		else if (code.length() == 13 && code.startsWith("25"))
		{
			preShopSale(code);
			return true;
		}
		else if (code.length() > 18 && code.split(" ").length > 1)
		{
			String[] codes = code.split(" ");
			for (int i = 0; i < codes.length; i++)
			{
				if (superFindGoods(codes[i], "", "", memo))
				{
					SaleGoodsDef salegoods = (SaleGoodsDef) this.saleGoods.get(saleGoods.size() - 1);
					if (salegoods == null)
					{
						new MessageBox(Language.apply("查找出的商品数据有误,请重试"));
						initOneSale(this.saletype);
						break;
					}

					calcHeadYsje();
					saleEvent.updateSaleGUI();
				}
				else
				{
					if (new MessageBox(Language.apply("未找到[{0}]的商品\n是否继续查找下一个", new Object[] { codes[i] }), null, true).verify() == GlobalVar.Key2)
						break;
				}
			}
		}

		return superFindGoods(code, yyyh, gz, memo);
	}



	public void preShopSale(String billid)
	{
		ArrayList listgoods = null;
		boolean findflag = false;
		// StringBuffer billid = null;
		try
		{
			if (!SellType.ISSALE(this.saletype))
				return;

			// // 先清除当前数据
			// if (saleGoods.size() > 0)
			// {
			// if (new MessageBox(Language.apply("确定清除当前所录入的商品吗?"), null,
			// true).verify() == GlobalVar.Key1)
			// initOneSale(this.saletype);
			// else
			// return;
			// }

			// billid = new StringBuffer();
			// if (!new TextBox().open(Language.apply("请输入单号"), "",
			// Language.apply("请输入预售单号"), billid, 0, 0, false,
			// TextBox.AllInput))
			// return;

			listgoods = new ArrayList();

			if (NetService.getDefault().getShopPreSaleGoods(listgoods, billid))
			{
				if (listgoods.size() == 0)
					return;

				for (int i = 0; i < listgoods.size(); i++)
				{
					preSale = (ShopPreSaleDef) listgoods.get(i);

					if (preSale == null)
					{
						new MessageBox(Language.apply("获取单据中商品数据有误,请重试"));
						initOneSale(this.saletype);
						findflag = false;
						break;
					}

					preSale.index = i;

					if (findGoods(preSale.barcode, "", ""))
					// if (superFindGoods(preSale.barcode, "", "", null))
					{
						SaleGoodsDef salegoods = (SaleGoodsDef) this.saleGoods.get(i);
						if (salegoods == null)
						{
							new MessageBox(Language.apply("查找出的商品数据有误,请重试"));
							initOneSale(this.saletype);
							findflag = false;
							break;
						}

						calcHeadYsje();
						saleEvent.updateSaleGUI();
					}
					else
					{
						if (new MessageBox(Language.apply("未找到[{0}]的商品\n是否继续查找下一个", new Object[] { preSale.barcode }), null, true).verify() == GlobalVar.Key2)
							break;
					}
					findflag = true;
				}
				return;
			}
			else
			{
				new MessageBox(Language.apply("未找到此单据对应的商品明细\n请确认该单据是否已经被处理"));
				return;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.out.println(ex.getMessage());
		}
		finally
		{
			if (findflag && billid != null && !billid.toString().equals(""))
				saleHead.str1 = billid.toString();

			preSale = null;
		}
	}

	public boolean checkFindGoodsAllowSale(GoodsDef goodsDef, double quantity, boolean isdzcm, double dzcmsl, double dzcmjg)
	{
		// 检查电子秤码,'Y'秤重的电子秤商品,'O'非秤重的电子秤商品;非秤重的电子秤商品允许输入数量
		if (isdzcm)
		{
			if ((goodsDef.isdzc != 'Y') && (goodsDef.isdzc != 'O'))
			{
				new MessageBox(Language.apply("该商品不是电子秤商品\n不能用电子秤码销售"));

				return false;
			}

			if ((dzcmsl > 0) && (dzcmjg < 0) && (goodsDef.lsj <= 0))
			{
				new MessageBox(Language.apply("该电子秤商品未定价,不能销售"));

				return false;
			}

			if ((goodsDef.isdzc == 'Y') && (dzcmsl > 0 && ManipulatePrecision.doubleCompare(ManipulatePrecision.doubleConvert(dzcmsl / goodsDef.bzhl, 4, 1), quantity, 4) != 0))
			{
				new MessageBox(Language.apply("电子秤条码不允许输入数量"));

				return false;
			}
		}
		else
		{
			if ((goodsDef.isdzc == 'Y'))
			{
				new MessageBox(Language.apply("该商品是电子秤商品\n不能直接销售"));

				return false;
			}
		}

		// 以旧换新码处理
		if (goodsDef.type == '8')
		{
			if (!checkOldExChangeNew(goodsDef))
			{
				new MessageBox(Language.apply("请先输入以旧换新码对应的新品编码"));

				return false;
			}
		}

		// 子母商品销售
		if (goodsDef.type == '6')
		{
			new MessageBox(Language.apply("母商品不能直接销售，请选择相应的子商品销售!"));
			return false;
		}

		// 特卖码商品是否允许销售
		if (goodsDef.type == 'T' && goodsDef.iszs != 'Y' && SellType.ISSALE(saletype))
		{
			new MessageBox(Language.apply("特卖码未生效或已过期,不能销售！"));
			return false;
		}

		if (GlobalInfo.sysPara.isEARNESTZT == 'N' && SellType.ISEARNEST(saletype) && goodsDef.iszt != 'Y')
		{
			new MessageBox(Language.apply("该商品不能进行") + SellType.getDefault().typeExchange(saletype, saleHead.hhflag, saleHead));
			return false;
		}

		// 不允许销红,检查库存
		if ((SellType.ISSALE(saletype) && GlobalInfo.sysPara.isxh != 'Y' && goodsDef.isxh != 'Y'))
		{
			// 统计商品销售数量
			double ysl = calcSameGoodsQuantity(goodsDef.code, goodsDef.gz); // 已经录入的商品数量
			double hjsl = ManipulatePrecision.mul(quantity, goodsDef.bzhl) + ysl;
			if (goodsDef.kcsl < hjsl)
			{
				if (GlobalInfo.sysPara.xhisshowsl == 'Y')
				{
					// 当商品库存大于 0 ，但销售商品数量大于 库存数量时，修改销售数量 至小于库存数量，然后就可以正常销售
					// 商品修改过的数量通过备用字段 goodsDef.num2 传出
					if (goodsDef.kcsl > 0)
					{
						double num = ManipulatePrecision.doubleConvert(goodsDef.kcsl - ysl, 2, 1);
						if (num < 0)
							num = 0;
						StringBuffer sb = new StringBuffer();
						TextBox txt = new TextBox();
						// 只能刷卡
						if (!txt.open(Language.apply("请输入商品数量"), Language.apply("商品"), Language.apply("此商品当前库存数量为 " + num + " "), sb, 0, num, true, TextBox.DoubleInput))
							return false;

						hjsl = Convert.toDouble(sb.toString());

						if (hjsl <= 0)
						{
							new MessageBox("数量不能为0");
							return false;
						}
						if (hjsl > 0 && hjsl <= goodsDef.kcsl)
						{
							// 修改了商品数量，使用这个暂记录数量
							goodsDef.num3 = hjsl;
						}
					}
					else
					{
						double price = goodsDef.lsj;
						double amount = 0;
						if (goodsDef.poplsj > 0 && goodsDef.poplsj < goodsDef.lsj)
						{
							price = goodsDef.poplsj;
						}
						amount = ManipulatePrecision.doubleConvert(price * hjsl, 2, 1);

						StringBuffer sb = new StringBuffer();
						sb.append("编  码:" + Convert.appendStringSize("", goodsDef.code, 1, 17, 17, 0) + "\n");
						sb.append("名  称:" + Convert.appendStringSize("", goodsDef.name, 1, 17, 17, 0) + "\n");
						sb.append("数  量:" + Convert.appendStringSize("", hjsl + "", 1, 17, 17, 0) + "\n");
						sb.append("价  格:" + Convert.appendStringSize("", price + "", 1, 17, 17, 0) + "\n");
						sb.append("总金额:" + Convert.appendStringSize("", amount + "", 1, 17, 17, 0) + "\n");
						sb.append("\n该商品库存为 " + goodsDef.kcsl + "\n库存不足,不能销售");

						new MessageBox(sb.toString());

						return false;
					}
				}
				else
				{
					new MessageBox(Language.apply("该商品库存不足,不能销售"));

					return false;
				}

			}
		}

		// T代表此商品已经被停用
		if (SellType.ISSALE(saletype) && goodsDef.iszs == 'T')
		{
			new MessageBox(Language.apply("当前商品已停售,不能销只能退！"));
			return false;
		}

		return true;
	}

	public boolean inputQuantity(int index, double quantity)
	{
		if (SellType.isJS(saletype)) { return false; }

		SaleGoodsDef oldGoodsDef = null;
		SpareInfoDef oldSpare = null;
		double newsl = -1;
		boolean flag = false;
		// 如果输入了
		if (quantity >= 0)
		{
			flag = true;
			newsl = quantity;
		}

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);

		if (SellType.ISCHECKINPUT(saletype) && isSpecifyCheckInput() && "D".equals(saleGoodsDef.str8))
			return false;
		// 判断是否允许修改数量
		if (!allowInputQuantity(index))
			return false;

		// 输入数量
		StringBuffer buffer = new StringBuffer();
		do
		{
			if (!flag)
			{
				buffer.delete(0, buffer.length());
				buffer.append(ManipulatePrecision.doubleToString(saleGoodsDef.sl, 4, 1, true));

				// 检查是否从电子称里获取重量金额
				boolean input = true;

				if (input)
				{
					// 得到商品可买最大数量，销洪商品除外
					double sl = getMaxSaleGoodsQuantity();

					if (goodsDef.isxh != 'Y')
					{
						double ysl = calcSameGoodsQuantity(goodsDef.code, goodsDef.gz) - saleGoodsDef.sl; // 已经录入的商品数量
						                                                                                  // -
						                                                                                  // 当前行的数量
						double sysl = goodsDef.kcsl - ysl; // 剩余数量

						if (sysl < 0)
							sl = sysl = 0;

						if (sysl > 0 && sysl < sl && goodsDef.isxh != 'Y')
							sl = sysl;

						sl = ManipulatePrecision.doubleConvert(sl, 2, 1);

						if (SellType.ISCOUPON(saletype) || SellType.ISJFSALE(saletype) || (saleEvent.yyyh.getText().trim().equals(Language.apply("超市")) && GlobalInfo.sysPara.goodsAmountInteger == 'Y') && goodsDef.isdzc != 'Y')
						{
							if (!new TextBox().open(Language.apply("请输入该商品数量"), Language.apply("数量"), "此商品当前库存数量为 " + sl + " ", buffer, 1, sl, true, TextBox.IntegerInput, -1)) { return false; }
						}
						else
						{
							if (!new TextBox().open(Language.apply("请输入该商品数量"), Language.apply("数量"), "此商品当前库存数量为 " + sl + " ", buffer, 0.0001, sl, true)) { return false; }
						}
					}
					else
					{
						sl = ManipulatePrecision.doubleConvert(sl, 2, 1);

						if (SellType.ISCOUPON(saletype) || SellType.ISJFSALE(saletype) || (saleEvent.yyyh.getText().trim().equals(Language.apply("超市")) && GlobalInfo.sysPara.goodsAmountInteger == 'Y') && goodsDef.isdzc != 'Y')
						{
							if (!new TextBox().open(Language.apply("请输入该商品数量"), Language.apply("数量"), "此商品当前库存数量为 " + sl + " ", buffer, 1, sl, true, TextBox.IntegerInput, -1)) { return false; }
						}
						else
						{
							if (!new TextBox().open(Language.apply("请输入该商品数量"), Language.apply("数量"), "此商品当前库存数量为 " + sl + " ", buffer, 0.0001, sl, true)) { return false; }
						}
					}
					newsl = Double.parseDouble(buffer.toString());
				}

				newsl = ManipulatePrecision.doubleConvert(newsl, 4, 1);

				if (newsl == 0)
					return false;

				flag = true;
			}
			// 检查销红
			if (SellType.ISSALE(saletype) && (GlobalInfo.sysPara.isxh != 'Y') && (goodsDef.kcsl > 0) && goodsDef.isxh != 'Y')
			{
				// 统计商品数量
				double hjsl = calcSameGoodsQuantity(goodsDef.code, goodsDef.gz);
				hjsl = (hjsl - ManipulatePrecision.mul(saleGoodsDef.sl, goodsDef.bzhl)) + ManipulatePrecision.mul(newsl, goodsDef.bzhl);

				if (goodsDef.kcsl < hjsl)
				{
					if (GlobalInfo.sysPara.xhisshowsl == 'Y')
					{
						double price = goodsDef.lsj;
						double amount = 0;
						if (goodsDef.poplsj > 0 && goodsDef.poplsj < goodsDef.lsj)
						{
							price = goodsDef.poplsj;
						}
						amount = ManipulatePrecision.doubleConvert(price * hjsl, 2, 1);
						new MessageBox(Language.apply(goodsDef.name + "\n数量：" + hjsl + " 价格:" + price + "\n总金额: " + amount + " 元 \n\n该商品库存为{0}\n库存不足,不能销售", new Object[] { ManipulatePrecision.doubleToString(goodsDef.kcsl) }));

					}
					else
						new MessageBox(Language.apply("销售数量已大于该商品库存,不能销售"));

					if (flag)
						return false;
					continue;
				}
			}

			// 指定小票退货
			if (isSpecifyBack(saleGoodsDef))
			{
				// 统计商品数量
				double hjsl = calcSameGoodsQuantity(goodsDef.code, goodsDef.gz);
				hjsl = (hjsl - ManipulatePrecision.mul(saleGoodsDef.sl, goodsDef.bzhl)) + ManipulatePrecision.mul(newsl, goodsDef.bzhl);

				if (goodsDef.kcsl < hjsl)
				{
					new MessageBox(Language.apply("退货数量已大于该商品原销售数量\n\n不能退货"));
					if (flag)
						return false;
					continue;
				}
			}

			// 检查印花限量优惠
			if (stampList != null && stampList.size() > 0 && SellType.ISSALE(saletype) && goodsDef.poptype != '0' && goodsDef.infonum1 > -9999.00)
			{
				double hjsl = calcSameGoodsQuantity(goodsDef.code, goodsDef.gz) + newsl - saleGoodsDef.sl;
				if (goodsDef.infonum1 < hjsl)
				{
					new MessageBox(Language.apply("该商品只有【{0}】个促销数量\n\n商品数量修改无效", new Object[] { goodsDef.infonum1 + "" }));
					if (flag)
						return false;
					continue;
				}
			}

			// 跳出循环
			break;
		}
		while (true);

		if (newsl < 0)
			return false;

		// 无权限 // 京客隆 iszt 标记为 Y 时，表示修改商品数量不需要授权
		if ((newsl < saleGoodsDef.sl) && ((curGrant.privqx != 'Y') && (curGrant.privqx != 'Q')) && (goodsDef.iszt != 'N'))
		{
			//
			OperUserDef staff = inputQuantityGrant(index);
			if (staff == null)
				return false;

			// 记录日志
			String log = "授权修改数量,小票号:" + saleHead.fphm + ",商品:" + saleGoodsDef.barcode + ",数量:" + newsl + ",授权:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);
		}

		// 备份数据
		oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();

		if (info != null)
			oldSpare = (SpareInfoDef) info.clone();

		// 重算商品应收
		double oldsl = saleGoodsDef.sl;
		saleGoodsDef.sl = newsl;
		saleGoodsDef.hjje = ManipulatePrecision.doubleConvert(saleGoodsDef.sl * saleGoodsDef.jg, 2, 1);
		double lszre = ManipulatePrecision.doubleConvert(saleGoodsDef.lszre / oldsl * newsl);
		double lszzk = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke / oldsl * newsl);
		clearGoodsGrantRebate(index);
		saleGoodsDef.lszre = lszre;
		saleGoodsDef.lszke = lszzk;

		getZZK(saleGoodsDef);
		calcGoodsYsje(index);

		// 重算小票应收
		calcHeadYsje();

		// 数量过大
		if (saleHead.ysje > getMaxSaleMoney())
		{
			new MessageBox(Language.apply("商品数量过大,导致销售金额达到上限\n\n商品数量修改无效"));

			// 恢复数量
			goodsSpare.setElementAt(oldSpare, index);
			saleGoods.setElementAt(oldGoodsDef, index);
			calcHeadYsje();

			return false;
		}

		// 退货金额过大
		if (SellType.ISBACK(saletype) && saleHead.ysje > curGrant.thxe)
		{
			new MessageBox(Language.apply("商品数量过大,导致退货金额超过限额\n\n商品数量修改无效"));

			// 恢复数量
			goodsSpare.setElementAt(oldSpare, index);
			saleGoods.setElementAt(oldGoodsDef, index);
			calcHeadYsje();

			return false;
		}

		// 盘点处理
		if (SellType.ISCHECKINPUT(saletype) && isSpecifyCheckInput() && !"U".equals(saleGoodsDef.str8))
		{
			if ("A".equals(saleGoodsDef.str8))
			{
				saleGoodsDef.name += "[修改]";
				saleGoodsDef.str8 = "A";
			}
			else if (saleGoodsDef.str8 == null || saleGoodsDef.str8.length() == 0)
			{
				saleGoodsDef.name += "[修改]";
				saleGoodsDef.str8 = "U";
			}
		}

		return true;
	}

	public boolean allowInputQuantity(int index)
	{
		if (isSpecifyBack())
		{
			new MessageBox("指定小票退货不能修改数量");
			return false;
		}

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);

		// 允许电子称修改数量
		if (GlobalInfo.sysPara.enabledzcCoefficient == 'Y')// &&
		                                                   // saleGoodsDef.costfactor
		                                                   // == 1)
			return true;

		// 京客隆要求放开控制
		// if (saleGoodsDef.flag == '2')
		// return false;

		return true;
	}

}
