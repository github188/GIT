package custom.localize.Lyfc;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

public class Lyfc_SaleBS extends Lyfc_SaleBS0CalcPop
{
	public int getStampCount(String stamp)
	{
		int count = 0;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);
			if (sgd.fhdd != null && sgd.fhdd.equals(stamp))
				count++;
		}

		return count;
	}

	public boolean findGoods(String code, String yyyh, String gz, String memo)
	{
		String[] cutpriceinfo = null;

		if (!GlobalInfo.sysPara.stampprefix.equals("") && code.startsWith(GlobalInfo.sysPara.stampprefix))
		{
			if (saleGoods.size() == 0)
			{
				new MessageBox("请录入该印花条码对应的商品!");
				return false;
			}

			cutpriceinfo = new String[4];
			// 查找削价码对应的商品
			if (!((Lyfc_NetService) NetService.getDefault()).findStampGoods(code, cutpriceinfo) || cutpriceinfo[0] == null || cutpriceinfo[1] == null)
			{
				new MessageBox("检验印花条码有效性失败!");
				return false;
			}

			if (cutpriceinfo != null)
			{
				// 是否控制印花码数量
				if (cutpriceinfo[2].equals("1"))
				{
					int count = getStampCount(code);
					if (count >= Convert.toDouble(cutpriceinfo[3]))
						return true;
				}

				for (int i = 0; i < saleGoods.size(); i++)
				{
					SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);
					GoodsDef gds = (GoodsDef) goodsAssistant.get(i);

					if (sgd.code.equals(cutpriceinfo[0]) && (sgd.fhdd == null || sgd.fhdd.trim().equals("")))
					{
						if (sgd.sl > 1)
							SplitSaleGoodsRow(i, 1);

						gds.poptype = '2';
						gds.couponrule = code;

						if (Convert.toInt(cutpriceinfo[1]) > 1)
							gds.poplsjzkl = ManipulatePrecision.doubleConvert(Convert.toDouble(cutpriceinfo[1]) / 100, 2, 1);
						else
							gds.poplsjzkl = ManipulatePrecision.doubleConvert(Convert.toDouble(cutpriceinfo[1]), 2, 1);

						if (calcStampPop(i))
						{
							sgd.fhdd = code; // 记录印花码
							break;
						}
						continue;
					}
				}

				calcHeadYsje();

				refreshSaleForm();
				return true;
			}
		}

		return super.findGoods(code, yyyh, gz, memo);
	}

	public boolean paySellStart()
	{
		if (super.paySellStart())
		{
			for (int i = 0; i < saleGoods.size(); i++)
				calcStampPop(i);

			return true;
		}
		
		return false;
	}

	public boolean inputQuantity(int index, double quantity)
	{
		if (SellType.isJS(saletype))
			return false;

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

		if (saleGoodsDef.fhdd != null && saleGoodsDef.fhdd.startsWith(GlobalInfo.sysPara.stampprefix))
		{
			if (new MessageBox("该商品存在印花折扣,修改数量后需要重新录入印花码,是否删除?", null, true).verify() != GlobalVar.Key1)
				return false;
		}

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

			// 跳出循环
			break;
		}
		while (true);

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
		saleGoodsDef.fhdd = "";

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

	public boolean allowInputQuantity(int index)
	{
		if (super.allowInputQuantity(index))
		{
			SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(index);
			if (sgd.fhdd != null && sgd.fhdd.startsWith(GlobalInfo.sysPara.stampprefix))
			{
				if (new MessageBox("该商品存在印花折扣,修改数量后需要重新录入印花码,是否删除?", null, true).verify() == GlobalVar.Key1)
					return true;

				return false;
			}

			return true;
		}

		return false;
	}
}
