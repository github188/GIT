package custom.localize.Hzww;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Device.RdPlugins;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Bstd.Bstd_SaleBS;

public class Hzww_SaleBS extends Bstd_SaleBS
{
	// 清零
	public void execCustomKey1(boolean keydownonsale)
	{
		ProgressBox box = new ProgressBox();
		box.setText("正在与称通讯,请稍等...");

		if (RdPlugins.getDefault().getPlugins1().exec(0, null))
		{
			if (keydownonsale)
				new MessageBox("清零成功!");
		}
		else
		{
			if (keydownonsale)
				new MessageBox("清零失败!");
		}

		box.close();
		box = null;

	}

	// 去皮
	public void execCustomKey0(boolean keydownonsale)
	{
		ProgressBox box = new ProgressBox();
		box.setText("正在与称通讯,请稍等...");

		if (RdPlugins.getDefault().getPlugins1().exec(1, null))
		{
			new MessageBox("去皮成功!");
		}
		else
		{
			new MessageBox("去皮失败!");
		}

		box.close();
		box = null;

	}

	// 称重
	public void execCustomKey2(boolean keydownonsale)
	{
		doShowInfoFinish();
	}

	public boolean findGoods(String code, String yyyh, String gz, String memo)
	{
		String comcode = "";
		String barcode = "";
		boolean isdzcm;
		double dzcmjg = 0;
		double dzcmsl = 0;
		String dzcmscsj = "";
		double quantity = 0;
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
			dzcmscsj = codeInfo[3];

			// 验证电子秤校验位
			if (!verifyDzcmCheckbit(barcode))
			{
				new MessageBox(Language.apply("电子秤码校验位错误"), null, false);

				return false;
			}

			if (dzcmjg <= 0 && dzcmsl <= 0)
			{
				new MessageBox(Language.apply("该电子秤格式条码无效"), null, false);

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

		quantity *= Convert.toDouble(slbuf.toString());

		// 获得最小批量数量
		quantity = getMinPlsl(quantity, goodsDef);

		// 电子秤商品记录原始电子秤码
		goodsDef.inputbarcode = barcode;
		if (isdzcm)
			goodsDef.barcode = convertDzcmBarcode(goodsDef, barcode, isdzcm);

		// 设置商品缺省售价
		price = setGoodsDefaultPrice(goodsDef);

		// 电子秤条码的数量价格处理
		int dzcprice = 1;
		double allprice = quantity * price;
		isdzcm = goodsDef.isdzc == 'Y' ? true : false;

		// 检查找到的商品是否允许销售
		if (!checkFindGoodsAllowSale(goodsDef, quantity, isdzcm, dzcmsl, dzcmjg))
			return false;

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
					String[] title = { Language.apply("商品编码"), Language.apply("数量"), Language.apply("单价"), Language.apply("合计折扣"), Language.apply("应付金额") };
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

					cho = new MutiSelectForm().open(Language.apply("请选择退货商品信息"), title, width, choice);
				}
				thSaleGoods = (SaleGoodsDef) back.elementAt(cho);

				if (thSaleGoods.sl < quantity)
				{
					new MessageBox(Language.apply("该商品退货数量大于原销售数量\n\n不能退货"));
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
							// 检查价格
							if (price <= 0 && goodsDef.type != 'Z')
							{
								new MessageBox(Language.apply("该商品价格必须大于0"));
							}
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

	public boolean checkFindGoodsAllowSale(GoodsDef goodsDef, double quantity, boolean isdzcm, double dzcmsl, double dzcmjg)
	{
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
			double hjsl = ManipulatePrecision.mul(quantity, goodsDef.bzhl) + calcSameGoodsQuantity(goodsDef.code, goodsDef.gz);
			if (goodsDef.kcsl < hjsl)
			{
				if (GlobalInfo.sysPara.xhisshowsl == 'Y')
					new MessageBox(Language.apply("该商品库存为{0}\n库存不足,不能销售", new Object[] { ManipulatePrecision.doubleToString(goodsDef.kcsl) }));
				else
					new MessageBox(Language.apply("该商品库存不足,不能销售"));

				return false;
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

	public boolean doShowInfoFinish()
	{
		ProgressBox box = null;

		try
		{
			int index = saleEvent.table.getSelectionIndex();
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);

			if (index < 0)
				return true;

			if (saleGoodsDef.flag != '2')
			{
				new MessageBox("该商品为非称重商品,无法称量!");
				return true;
			}

			// 先清零
			execCustomKey1(false);
			new MessageBox("请将商品放置称台,待重量稳定后,按任意键获取重量");

			box = new ProgressBox();
			box.setText("正在获取重量,请稍等...");

			if (!RdPlugins.getDefault().getPlugins1().exec(2, null))
			{
				new MessageBox("重量获取失败!");
				NewKeyListener.sendKey(GlobalVar.Del);

				return true;
			}

			double scale = Convert.toDouble(RdPlugins.getDefault().getPlugins1().getObject());
			if (scale <= 0)
			{
				int ret = new MessageBox("所称商品重量为零,是否重新获取重量?", null, true).verify();

				if (ret == GlobalVar.Key1)
					NewKeyListener.sendKey(GlobalVar.CustomKey2);
				else
					NewKeyListener.sendKey(GlobalVar.Del);

				return true;
			}

			if (updateQuantity(scale))
				refreshSaleForm();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (box != null)
				box.close();

			box = null;
		}

		return true;
	}

	public boolean updateQuantity(double quantity)
	{
		int index = saleEvent.table.getSelectionIndex();

		if (index < 0 || quantity <= 0)
			return false;

		SaleGoodsDef oldGoodsDef = null;
		SpareInfoDef oldSpare = null;

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);

		// 备份数据
		oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();

		if (info != null)
			oldSpare = (SpareInfoDef) info.clone();

		// 重算商品应收
		double oldsl = saleGoodsDef.sl;
		saleGoodsDef.sl = quantity;
		saleGoodsDef.hjje = ManipulatePrecision.doubleConvert(saleGoodsDef.sl * saleGoodsDef.jg, 2, 1);
		double lszre = ManipulatePrecision.doubleConvert(saleGoodsDef.lszre / oldsl * quantity);
		double lszzk = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke / oldsl * quantity);
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

		return true;
	}

}
