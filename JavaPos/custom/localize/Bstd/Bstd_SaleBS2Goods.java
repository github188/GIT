package custom.localize.Bstd;

import java.util.ArrayList;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.GoodsBarcodeDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Bstd_SaleBS2Goods extends Bstd_SaleBS1JfExchange
{
	protected int curLine = 0;
	protected boolean isSameGoods = false;
	protected int checkIndex = -1;

	protected char goodscombineFlag = GlobalInfo.sysPara.isHbGoods;

	public GoodsDef findGoodsInfo(String code, String yyyh, String gz, String dzcmscsj, boolean isdzc, StringBuffer slbuf)
	{
		// 条码转换
		String barcode = findGoodsBarcode(code, slbuf);
		if (barcode == null)
			return null;

		// 找商品信息
		return super.findGoodsInfo(barcode, yyyh, gz, dzcmscsj, isdzc, slbuf);
	}

	private String findGoodsBarcode(String code, StringBuffer slbuf)
	{
		ArrayList codelist = new ArrayList();

		if (!((Bstd_DataService) DataService.getDefault()).findGoodsBarcodeList(code, codelist))
		{
			return code;
		}
		else
		{
			if (codelist.size() <= 0)
			{
				return code;
			}
			else
			{
				int choice = 0;
				if (codelist.size() > 1)
				{
					// 弹出窗口选择商品
					Vector contents = new Vector();
					for (int i = 0; i < codelist.size(); i++)
					{
						GoodsBarcodeDef ccd = (GoodsBarcodeDef) codelist.get(i);
						contents.add(new String[] { ccd.gdbarcode, ccd.gdname, ManipulatePrecision.doubleToString(ccd.gdbzhl, 4, 1, true) });
					}

					String[] title = { "条码", "描述", "含量" };
					int[] width = { 145, 300, 100 };
					choice = new MutiSelectForm().open("请选择商品", title, width, contents);

					if (choice == -1) { return null; }
				}

				// 对应商品
				GoodsBarcodeDef ccd = (GoodsBarcodeDef) codelist.get(choice);
				if (ccd.gdbzhl > 0)
				{
					slbuf.delete(0, slbuf.length());
					slbuf.append(String.valueOf(ccd.gdbzhl));
				}
				return ccd.gdbarcode;
			}
		}
	}

	public double getMinPlsl(double quantity, GoodsDef goodsDef)
	{
		if (goodsDef.minplsl > 0)
			quantity = ManipulatePrecision.mul(quantity, goodsDef.minplsl);

		return quantity;
	}

	public boolean inputRebatePrice(int index)
	{
		if (GlobalInfo.sysPara.enableiputzszrebate == 'Y')
			return inputRebateZSZPrice(index);
		else
			return super.inputRebatePrice(index);
	}

	public boolean inputRebateZSZPrice(int index)
	{
		double grantzkl = 0;
		boolean grantflag = false;

		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack())
			return false;

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);

		// 小计、削价不处理
		if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3')) { return false; }

		// 服务费、以旧换新不处理
		if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8')) { return false; }

		// 不能打折
		if (!checkGoodsRebate(goodsDef, info))
		{
			new MessageBox("该商品不允许打折!");

			return false;
		}

		// 备份数据
		SaleGoodsDef oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();

		// 授权
		if ((curGrant.dpzkl * 100) >= 100 || !checkGoodsGrantRange(goodsDef, curGrant.grantgz))
		{
			OperUserDef staff = inputRebateGrant(index);
			if (staff == null)
				return false;

			// 本次授权折扣
			grantzkl = staff.dpzkl;
			grantflag = breachRebateGrant(staff);

			// 记录授权工号
			saleGoodsDef.sqkh = staff.gh;
			saleGoodsDef.sqktype = '1';
			saleGoodsDef.sqkzkfd = staff.privje1;

			// 记录日志
			String log = "授权单品折让,小票号:" + saleHead.fphm + ",商品:" + saleGoodsDef.barcode + ",折扣权限:" + grantzkl * 100 + "%,授权:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);
		}
		else
		{
			// 本次授权折扣
			grantzkl = curGrant.dpzkl;
			grantflag = breachRebateGrant(curGrant);

			// 记录授权工号
			saleGoodsDef.sqkh = cursqkh;
			saleGoodsDef.sqktype = cursqktype;
			saleGoodsDef.sqkzkfd = cursqkzkfd;
		}

		// 计算权限允许的最大折扣额
		double maxzkl = 0;
		if (grantflag)
		{
			// new MessageBox("允许突破最低折扣");
			// 允许突破最低折扣
			maxzkl = getMaxRebateGrant(grantzkl, 0);
		}
		else
		{
			// 不允许最低折扣
			maxzkl = getMaxRebateGrant(grantzkl, goodsDef);
		}

		double maxzre = 0.0;

		if (saleGoodsDef.lszre > 0)
		{
			double haszre = ManipulatePrecision.doubleConvert((1 - maxzkl) * (saleGoodsDef.hjje - saleGoodsDef.hjzk + saleGoodsDef.lszre), 2, 1);
			if (saleGoodsDef.lszre >= haszre)
				return true;
			else
				maxzre = ManipulatePrecision.doubleConvert(haszre - saleGoodsDef.lszre, 2, 1);
		}
		else
		{
			// 在折上折的基础上再进行折扣
			maxzre = ManipulatePrecision.doubleConvert((1 - maxzkl) * (saleGoodsDef.hjje - saleGoodsDef.hjzk), 2, 1);
		}
		// goodsDef.maxzke为最低限价
		if ((goodsDef.maxzke * saleGoodsDef.sl) <= saleGoodsDef.hjje && saleGoodsDef.hjje - (goodsDef.maxzke * saleGoodsDef.sl) < maxzre)
		{
			// 保证商品要以最低限额售出
			maxzre = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - (goodsDef.maxzke * saleGoodsDef.sl), 2, 1);
		}

		// 输入折让
		String maxzremsg = "收银员对该商品进行折让";

		StringBuffer buffer = new StringBuffer();

		if (GlobalInfo.sysPara.rebatepriacemode == 'Y')
		{
			// 计算最大折让到金额
			double lszre = saleGoodsDef.hjje - getZZK(saleGoodsDef) - maxzre;
			lszre = ManipulatePrecision.doubleConvert(lszre, 2, 1);

			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzremsg = "收银员对该商品的单品折扣权限为 " + ManipulatePrecision.doubleToString(maxzkl * 100, 2, 1, true) + "%\n你目前对该商品最多只能够折让到 " + ManipulatePrecision.doubleToString(lszre, 2, 1, true) + " 元";
			}

			if (!new TextBox().open("请输入单品折让后的成交价" + (grantflag == true ? "(允许突破最低折扣)" : ""), "单品折让", maxzremsg, buffer, lszre, ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - saleGoodsDef.hjzk, 2, 1), true))
			{
				// 恢复数据
				saleGoods.setElementAt(oldGoodsDef, index);

				return false;
			}

			// 得到折让额
			lszre = Convert.toDouble(buffer.toString());

			// 清除所有手工折扣,按输入的成交价计算最终折让
			saleGoodsDef.lszke = 0;
			saleGoodsDef.lszre = 0;
			saleGoodsDef.lszzk = 0;
			saleGoodsDef.lszzr = 0;
			saleGoodsDef.lszre = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef) - lszre, 2, 1);

		}
		else
		{
			// 计算最大可折让金额
			double lszre = ManipulatePrecision.doubleConvert(maxzre, 2, 1);
			if (lszre < 0)
				lszre = 0;

			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzremsg = "收银员对该商品的单品折扣权限为 " + ManipulatePrecision.doubleToString(maxzkl * 100, 2, 1, true) + "%\n你目前对该商品最多还可以再折让 " + ManipulatePrecision.doubleToString(lszre, 2, 1, true) + " 元";
			}

			if (!new TextBox().open("请输入单品要折让的金额" + (grantflag == true ? "(允许突破最低折扣)" : ""), "单品折让", maxzremsg, buffer, 0, lszre, true))
			{
				// 恢复数据
				saleGoods.setElementAt(oldGoodsDef, index);

				return false;
			}

			saleGoodsDef.lszre += ManipulatePrecision.doubleConvert(Convert.toDouble(buffer.toString()), 2, 1);
		}

		// 得到折让额

		if (saleGoodsDef.lszre < 0)
			saleGoodsDef.lszre = 0;
		saleGoodsDef.lszre = getConvertRebate(index, saleGoodsDef.lszre);

		// 重算商品折扣合计
		getZZK(saleGoodsDef);

		// 重算小票应收
		calcHeadYsje();

		return true;
	}

	public boolean modifyGoodsRebate(double rebate, boolean iszsz)
	{
		if (rebate > 10)
			rebate = rebate / 100;

		// 计算整单最打可打折金额
		double sumzzk = 0, sumqtzzk = 0, lastzzk = 0, hjcjj = 0, hjzke = 0;
		int lastzzkrow = -1;
		SaleGoodsDef saleGoodsDef;
		GoodsDef goodsDef;

		for (int i = 0; i < saleGoods.size(); i++)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			goodsDef = (GoodsDef) goodsAssistant.elementAt(i);

			// 非折上折时
			if (!iszsz && (saleGoodsDef.yhzke > 0 || saleGoodsDef.hyzke > 0))
				continue;

			// 小记、削价不处理
			if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3'))
			{
				continue;
			}

			// 服务费、以旧换新不处理
			if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8'))
			{
				continue;
			}

			// 累计可折扣金额
			sumzzk += ManipulatePrecision.doubleConvert((1 - rebate) * saleGoodsDef.hjje, 2, 1);
			sumqtzzk += saleGoodsDef.qtzke;
			hjcjj += saleGoodsDef.hjje - saleGoodsDef.hjzk;
			hjzke += saleGoodsDef.hjzk;

			// 不能打折
			if (!checkGoodsRebate(goodsDef))
			{
				continue;
			}

			// 计算商品权限允许的最大折扣额,找可折让金额最大的商品
			double maxzkl = getMaxRebateGrant(rebate, goodsDef);
			double maxzzk = ManipulatePrecision.doubleConvert((1 - maxzkl) * saleGoodsDef.hjje, 2, 1);
			if (maxzzk > lastzzk)
			{
				lastzzk = maxzzk;
				lastzzkrow = i;
			}
		}

		// 得到折扣金额,打折后按收银机定义四舍五入
		double zzkje = ManipulatePrecision.doubleConvert(rebate * (hjcjj + sumqtzzk), 2, 1);
		double tempysje = (saleHead.hjzje - saleHead.hjzke + sumqtzzk) - zzkje;
		double tempyfje = getDetailOverFlow(tempysje);
		zzkje = ManipulatePrecision.sub(zzkje, ManipulatePrecision.sub(tempyfje, tempysje));

		// 把总折扣额分摊到每个商品
		double hjzzk = 0;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			goodsDef = (GoodsDef) goodsAssistant.elementAt(i);

			if (!iszsz && (saleGoodsDef.yhzke > 0 || saleGoodsDef.hyzke > 0))
				continue;

			// 小记、削价不处理
			if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3'))
			{
				continue;
			}

			// 服务费、以旧换新不处理
			if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8'))
			{
				continue;
			}

			// 不能打折
			if (!checkGoodsRebate(goodsDef))
			{
				continue;
			}

			// 计算商品权限允许的最大折扣额
			double maxzkl = getMaxRebateGrant(rebate, goodsDef);

			double maxzzk = ManipulatePrecision.doubleConvert((1 - maxzkl) * saleGoodsDef.hjje, 2, 1);

			// 取消其他手工折扣,计算最终折扣
			saleGoodsDef.sqkh = "";
			saleGoodsDef.sqktype = '\0';

			// 每个商品分摊的折让按金额占比计算
			if (i != lastzzkrow)
			{
				if (GlobalInfo.sysPara.batchtotalrebate == 'N')
				{
					saleGoodsDef.qtzke = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - saleGoodsDef.hjzk + saleGoodsDef.qtzke) / (hjcjj + sumqtzzk) * zzkje, 2, 1);
				}
				else
				{
					saleGoodsDef.qtzke = ManipulatePrecision.doubleConvert(maxzzk / sumzzk * zzkje, 2, 1);
				}
				if (getZZK(saleGoodsDef) > maxzzk)
				{
					saleGoodsDef.qtzke -= getZZK(saleGoodsDef) - maxzzk;
					saleGoodsDef.qtzke = ManipulatePrecision.doubleConvert(saleGoodsDef.qtzke, 2, 1);
				}
				if (saleGoodsDef.qtzke < 0)
					saleGoodsDef.qtzke = 0;
				saleGoodsDef.qtzke = getConvertRebate(i, saleGoodsDef.qtzke);
				saleGoodsDef.qtzke = getConvertRebate(i, saleGoodsDef.qtzke, getGoodsApportionPrecision());

				// 重算商品折扣合计
				getZZK(saleGoodsDef);

				// 计算已分摊的总折让
				hjzzk += saleGoodsDef.qtzke;
			}
		}

		// 可折让金额最大商品的折扣用减法直接等于剩余的分摊,最后计算
		if (lastzzkrow >= 0)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(lastzzkrow);
			saleGoodsDef.qtzke = ManipulatePrecision.doubleConvert(zzkje - hjzzk, 2, 1);
			if (getZZK(saleGoodsDef) > lastzzk)
			{
				saleGoodsDef.qtzke -= getZZK(saleGoodsDef) - lastzzk;
				saleGoodsDef.qtzke = ManipulatePrecision.doubleConvert(saleGoodsDef.qtzke, 2, 1);
			}
			if (saleGoodsDef.qtzke < 0)
				saleGoodsDef.qtzke = 0;
			getZZK(saleGoodsDef);
		}

		return true;
	}

	public boolean findGoods(String code, String yyyh, String gz, String memo)
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
		quantity *= Convert.toDouble(slbuf.toString());

		// 获得最小批量数量
		quantity = getMinPlsl(quantity, goodsDef);

		// 电子秤商品记录原始电子秤码
		goodsDef.inputbarcode = barcode;
		if (isdzcm)
			goodsDef.barcode = convertDzcmBarcode(goodsDef, barcode, isdzcm);

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
				} while (true);
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
				} while (true);
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
					if (GlobalInfo.sysPara.isCalcAsPfj == 'Y' && (goodsDef.pfj > 0 && ManipulatePrecision.doubleCompare(goodsDef.lsj, goodsDef.pfj, 2) != 0))
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
						quantity = ManipulatePrecision.doubleConvert((dzcmjg / goodsDef.lsj), 4, 1);
						price = goodsDef.lsj;
						allprice = dzcmjg;
						dzcprice = 2;
					}
				}
			}
			else if ((dzcmsl > 0) && (dzcmjg > 0)) // 即有数量又有价格
			{

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
							// 检查价格
							if (price <= 0 && goodsDef.type != 'Z')
							{
								new MessageBox("该商品价格必须大于0");
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

					} while (true);
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

	protected boolean combineGoods(SaleGoodsDef saleGoodsDef)
	{
		try
		{
			if (GlobalInfo.sysPara.isHbGoods == 'Y' && saleGoods.size() >= 1)// 是否合并商品
			{
				// 拦截哪些交易类型不让合并
				if (SellType.ISBATCH(saletype))
				{
					// 同一张单中存在相同编码商品
					if (isSameGoods)
						return true;
					else
						return false;
				}

				SaleGoodsDef saleGoodsDefTmp = null;
				for (int i = 0; i < saleGoods.size(); i++)
				{
					saleGoodsDefTmp = (SaleGoodsDef) saleGoods.elementAt(i);
					if (saleGoodsDefTmp == null)
						continue;

					if (saleGoodsDefTmp.lszke > 0 || saleGoodsDefTmp.lszre > 0 || saleGoodsDefTmp.lszzk > 0 || saleGoodsDefTmp.lszzr > 0)
						continue;

					// 判断合并条件,电子秤条码不能累加
					if (saleGoodsDef.flag != '2' && saleGoodsDefTmp.code.equals(saleGoodsDef.code) && saleGoodsDefTmp.gz.equals(saleGoodsDef.gz) && (saleGoodsDefTmp.barcode.equals(saleGoodsDef.barcode) || saleGoodsDefTmp.barcode.equals(saleGoodsDef.inputbarcode)) && saleGoodsDefTmp.bzhl == saleGoodsDef.bzhl && saleGoodsDefTmp.jg == saleGoodsDef.jg && saleGoodsDefTmp.lsj == saleGoodsDef.lsj)
					{
						saleGoodsDefTmp.sl += saleGoodsDef.sl;// 新数量
						saleGoodsDefTmp.hjje = ManipulatePrecision.doubleConvert(saleGoodsDefTmp.sl * saleGoodsDefTmp.jg, 2, 1);
						curLine = i;
						calcGoodsYsje(i);

						return true;// 合并成功
					}
				}
			}
			curLine = -1;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return false;// 合并失败
	}

	public boolean addSaleGoods(GoodsDef goodsDef, String yyyh, double quantity, double price, double allprice, boolean dzcm)
	{
		// 如果数量小于等于0，则要求重新扫码
		if (quantity == 0 || quantity < 0)
		{
			new MessageBox("商品数量输入无效,请重新输入");
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

			// 合并商品
			if (!combineGoods(saleGoodsDef))
			{
				// 增加商品明细
				addSaleGoodsObject(saleGoodsDef, goodsDef, getGoodsSpareInfo(goodsDef, saleGoodsDef));
				// 计算商品应收
				calcGoodsYsje(saleGoods.size() - 1);
			}
		}

		// 计算小票应收
		calcHeadYsje();

		return true;
	}

	public void addSaleGoodsObject(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		addCmpopSaleGoodsObject(sg, goods, info);

		// 是否自动进行积分换购
		if (GlobalInfo.sysPara.autojfexchange == 'Y')
		{
			// 换货状态不允许使用积分换购
			if (hhflag == 'Y') { return; }

			// 没有刷会员卡不允许积分换购
			if (curCustomer == null) { return; }

			// 无0509付款方式,不能进行积分换购
			PayModeDef paymode = DataService.getDefault().searchPayMode("0509");
			if (paymode == null) { return; }

			NewKeyListener.sendKey(GlobalVar.JfExchange);
		}
	}

	public SaleGoodsDef goodsDef2SaleGoods(GoodsDef goodsDef, String yyyh, double quantity, double price, double allprice, boolean dzcm)
	{
		SaleGoodsDef sgd = super.goodsDef2SaleGoods(goodsDef, yyyh, quantity, price, allprice, dzcm);

		if (sgd != null)
		{
			sgd.costfactor = Convert.toDouble(goodsDef.str1);
		}

		return sgd;
	}

	public void preShopSale()
	{
		GlobalInfo.sysPara.isHbGoods = 'N';
		super.preShopSale();
	}

	public void doSaleGoodsDisplayEvent(SaleGoodsDef oldGoods, int index)
	{
		if (SellType.ISCHECKINPUT(saletype))
			checkIndex = index;
	}

	public boolean doShowInfoFinish()
	{
		// 拦截批发，盘点交易不让光标跳行
		if (SellType.ISBATCH(saletype) || SellType.ISCHECKINPUT(saletype))
		{
			saleEvent.table.setSelection(checkIndex);
			saleEvent.table.showSelection();
			return true;
		}

		if (curLine != -1)
		{
			saleEvent.table.setSelection(curLine);
			saleEvent.table.showSelection();
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

		if (saleGoodsDef.flag == '2')
			return false;

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
					if (SellType.ISCOUPON(saletype) || GlobalInfo.sysPara.goodsAmountInteger == 'Y' && goodsDef.isdzc != 'Y')
					{
						if (!new TextBox().open("请输入该商品数量", "数量", "", buffer, 1, getMaxSaleGoodsQuantity(), true, TextBox.IntegerInput, -1)) { return false; }
					}
					else
					{
						if (!new TextBox().open("请输入该商品数量", "数量", "", buffer, 0.0001, getMaxSaleGoodsQuantity(), true)) { return false; }
					}
					newsl = Double.parseDouble(buffer.toString());
				}
				newsl = ManipulatePrecision.doubleConvert(newsl, 4, 1);
				flag = true;
			}

			// 电子称商品根据价格因子控制输入的数量
			if (saleGoodsDef.flag == '2' && GlobalInfo.sysPara.enabledzcCoefficient == 'Y')
			{
				String dscsl = "";
				if (saleGoodsDef.costfactor == 1)
				{
					dscsl = String.valueOf(newsl);
					dscsl = dscsl.substring(dscsl.indexOf(".") + 1);

					if (Convert.toInt(dscsl) > 0)
					{
						new MessageBox("电子称数量不合法\n价格因子为1的商品,数量不允许出现小数");
						return false;
					}
				}
			}

			// 检查销红
			if (SellType.ISSALE(saletype) && (GlobalInfo.sysPara.isxh != 'Y') && (goodsDef.kcsl > 0))
			{
				// 统计商品数量
				double hjsl = calcSameGoodsQuantity(goodsDef.code, goodsDef.gz);
				hjsl = (hjsl - ManipulatePrecision.mul(saleGoodsDef.sl, goodsDef.bzhl)) + ManipulatePrecision.mul(newsl, goodsDef.bzhl);

				if (goodsDef.kcsl < hjsl)
				{
					if (GlobalInfo.sysPara.xhisshowsl == 'Y')
						new MessageBox("销售数量已大于该商品库存【" + goodsDef.kcsl + "】\n\n不能销售");
					else
						new MessageBox("销售数量已大于该商品库存,不能销售");

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
					new MessageBox("退货数量已大于该商品原销售数量\n\n不能退货");
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
					new MessageBox("该商品只有【" + goodsDef.infonum1 + "】个促销数量\n\n商品数量修改无效");
					if (flag)
						return false;
					continue;
				}
			}

			// 跳出循环
			break;
		} while (true);

		if (newsl < 0)
			return false;

		// 无权限
		if ((newsl < saleGoodsDef.sl) && (curGrant.privqx != 'Y') && (curGrant.privqx != 'Q'))
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

		if (saleGoodsDef.yhdjbh != null && saleGoodsDef.yhdjbh.equals("w"))
		{
			double yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.mjzke / oldsl * newsl);
			double needjf = 0;

			if (newsl > oldsl)
			{
				needjf = ManipulatePrecision.doubleConvert((newsl - oldsl) * (saleGoodsDef.num4 / oldsl), 2, 1);

				if (curCustomer.valuememo - needjf < 0)
				{
					new MessageBox("商品数量过大,所需积分已超过会员当前总积分【" + curCustomer.valuememo + "】\n\n商品数量修改无效");
					goodsSpare.setElementAt(oldSpare, index);
					saleGoods.setElementAt(oldGoodsDef, index);
					calcHeadYsje();

					return false;
				}
				saleGoodsDef.mjzke = yhzke;
				saleGoodsDef.num4 = ManipulatePrecision.doubleConvert(saleGoodsDef.num4 + needjf, 2, 1);
				curCustomer.valuememo = ManipulatePrecision.doubleConvert(curCustomer.valuememo - needjf, 2, 1);
			}
			else if (newsl < oldsl)
			{
				needjf = ManipulatePrecision.doubleConvert((newsl - oldsl) * (saleGoodsDef.num4 / oldsl), 2, 1);
				saleGoodsDef.mjzke = yhzke;
				saleGoodsDef.num4 = ManipulatePrecision.doubleConvert(saleGoodsDef.num4 + needjf, 2, 1);
				curCustomer.valuememo = ManipulatePrecision.doubleConvert(curCustomer.valuememo - needjf, 2, 1);
			}
		}

		getZZK(saleGoodsDef);
		calcGoodsYsje(index);

		// 重算小票应收
		calcHeadYsje();

		// 数量过大
		if (saleHead.ysje > getMaxSaleMoney())
		{
			new MessageBox("商品数量过大,导致销售金额达到上限\n\n商品数量修改无效");

			// 恢复数量
			goodsSpare.setElementAt(oldSpare, index);
			saleGoods.setElementAt(oldGoodsDef, index);
			calcHeadYsje();

			return false;
		}

		// 退货金额过大
		if (SellType.ISBACK(saletype) && saleHead.ysje > curGrant.thxe)
		{
			new MessageBox("商品数量过大,导致退货金额超过限额\n\n商品数量修改无效");

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

	public boolean deleteGoods(int index)
	{
		double sgdjf = 0;
		SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(index);
		if (curCustomer != null && sgd.yhdjbh != null && sgd.yhdjbh.equals("w"))
			sgdjf = sgd.num4;

		if (super.deleteGoods(index))
		{
			if (curCustomer != null)
				curCustomer.valuememo = curCustomer.valuememo + sgdjf;
			return true;
		}

		return false;
	}

	protected boolean doneDeleteGoods(int index, SaleGoodsDef old_goods)
	{
		try
		{
			SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(index);
			if (sgd.yhdjbh != null && sgd.yhdjbh.equals("w") && sgd.yhzke > 0)
			{
				curCustomer.valuememo = curCustomer.valuememo + sgd.num4;
				sgd.yhdjbh = "";
				sgd.yhzke = 0;
				sgd.num4 = 0;

				getZZK(sgd);
			}

			return super.doneDeleteGoods(index, old_goods);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
}
