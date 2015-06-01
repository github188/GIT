package custom.localize.Hbgy;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

public class Hbgy_SaleBS extends Hbgy_SaleBS0JfExchange
{
	public boolean memberGrant()
	{
		if (curCustomer != null)
		{
			for (int i = 0; i < this.saleGoods.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);
				if (sgd.yhdjbh.equals("w"))
				{
					new MessageBox("当前商品存在积分换购,不允许更新会员!");
					return false;
				}
			}
		}
		return super.memberGrant();
	}

	public void existCreditFee()
	{
		return;
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
					if (SellType.ISCOUPON(saletype) || (saleEvent.yyyh.getText().trim().equals("超市") && GlobalInfo.sysPara.goodsAmountInteger == 'Y') && goodsDef.isdzc != 'Y')
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

		if (saleGoodsDef.yhdjbh.equals("w"))
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

	public Vector getPayModeBySuper(String sjcode, StringBuffer index, String code)
	{
		Vector child = new Vector();
		String[] temp = null;
		PayModeDef mode = null;
		int k = -1;
		for (int i = 0; i < GlobalInfo.payMode.size(); i++)
		{
			mode = (PayModeDef) GlobalInfo.payMode.elementAt(i);

			if ((mode.sjcode.trim().equals(sjcode.trim()) || (sjcode.equals("0") && mode.sjcode.trim().equals(mode.code))) && getPayModeByNeed(mode))
			{
				k++;

				// 标记code付款方式在vector中的位置
				if (index != null && code != null && mode.code.compareTo(code) == 0)
				{
					index.append(String.valueOf(k));
				}

				temp = new String[2];
				temp[0] = mode.code.trim();
				temp[1] = mode.name;
				if (mode.hl != 1)
					temp[1] = temp[1] + "<" + ManipulatePrecision.doubleToString(mode.hl, 4, 1, false) + ">";

				child.add(temp);
			}
		}

		return child;
	}

	public void calcGovermentRebate(String cardno)
	{
		double totalje = 0;
		for (int i = 0; i < this.saleGoods.size(); i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) this.saleGoods.get(i);
			totalje = totalje + sgd.num6;
		}

		if (GlobalInfo.sysPara.hmkrebate < 1)
			GlobalInfo.sysPara.hmkrebate = GlobalInfo.sysPara.hmkrebate * 100;

		double zkl = (100 - GlobalInfo.sysPara.hmkrebate) / 100;
		double zke = ManipulatePrecision.doubleConvert(totalje * zkl, 2, 1);

		if (zke <= 0)
			return;

		PayModeDef pmd = (PayModeDef) DataService.getDefault().searchPayMode("0402");

		if (pmd == null)
		{
			new MessageBox("未定义政府补贴付款对象!");
			return;
		}

		Payment pay = CreatePayment.getDefault().createPaymentByPayMode(pmd, saleEvent.saleBS);
		if (pay.createSalePayObject(zke + ""))
		{
			pay.salepay.payno = cardno;
			addSalePayObject(pay.salepay, pay);
			calcPayBalance();
		}
	}

	public boolean findGoods(String code, String yyyh, String gz)
	{
		if (saleHead.str3.length() > 1 || saleHead.str4.length() > 1 || saleHead.str5.length() > 1)
		{
			new MessageBox("该单已享受员工卡/惠民卡/购物券折扣\n不能增加商品");
			return false;
		}

		return super.findGoods(code, yyyh, gz);
	}

	public boolean allowEditGoods()
	{
		if (saleHead.str3.length() > 1 || saleHead.str4.length() > 1 || saleHead.str5.length() > 1)
		{
			new MessageBox("该单已享受员工卡/惠民卡/购物券折扣\n不能修改商品");
			return false;
		}

		return super.allowEditGoods();
	}

	public boolean deleteGoods(int index)
	{
		if (saleHead.str3.length() > 1 || saleHead.str4.length() > 1 || saleHead.str5.length() > 1)
		{
			new MessageBox("该单已享受员工卡/惠民卡折扣\n不能删除商品");
			return false;
		}

		double sgdjf = 0;
		SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(index);
		if (curCustomer != null && sgd.yhdjbh.equals("w"))
			sgdjf = sgd.num4;

		if (super.deleteGoods(index))
		{
			if (saleGoods.size() == 0)
			{
				saleHead.str3 = "";
				saleHead.str4 = "";
				saleHead.str5 = "";
			}

			if (curCustomer != null)
				curCustomer.valuememo = curCustomer.valuememo + sgdjf;

			return true;
		}
		return false;
	}

	protected boolean goodsNoZszRebate(int type, String[] retinfo)
	{
		double avalidMoney = ManipulatePrecision.doubleConvert(Convert.toDouble(retinfo[3]) - Convert.toDouble(retinfo[4]), 2, 1);
		if (avalidMoney <= 0.0)
		{
			new MessageBox("本月可享受折扣总金额【" + Convert.toDouble(retinfo[3]) + "】已用完");
			return false;
		}

		// 记录总金额
		saleHead.num6 = ManipulatePrecision.doubleConvert(Convert.toDouble(retinfo[3]), 2, 1);

		String goodsIndex = "";
		double total = 0.0D;
		int maxmoneyindex = 0;
		double exgmoney = 0.0;

		for (int i = 0; i < this.saleGoods.size(); i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) this.saleGoods.get(i);

			if (sgd.hjzk > 0.0D)
			{
				if (i == saleGoods.size() - 1)
					goodsIndex = goodsIndex + maxmoneyindex;

				continue;
			}

			if (i == 0)
			{
				exgmoney = sgd.hjje;
				maxmoneyindex = i;
			}
			else
			{
				if (sgd.hjje > exgmoney)
				{
					exgmoney = sgd.hjje;
					maxmoneyindex = i;
				}
			}

			if (total + sgd.hjje > avalidMoney)
			{
				double tmpmoney = sgd.hjje - (total + sgd.hjje - avalidMoney);

				goodsIndex = goodsIndex + i + "|" + tmpmoney + ",";
				sgd.num6 = tmpmoney;
				total += tmpmoney;
				goodsIndex = goodsIndex + maxmoneyindex;
				break;
			}

			goodsIndex = goodsIndex + i + "|" + sgd.hjje + ",";
			sgd.num6 = sgd.hjje;
			total += sgd.hjje;

			if (i == saleGoods.size() - 1)
				goodsIndex = goodsIndex + maxmoneyindex;
		}

		total = ManipulatePrecision.doubleConvert(total, 2, 1);

		if (goodsIndex.trim().equals(""))
			return false;

		double retzkl = Convert.toDouble(retinfo[2]);

		if (retzkl < 1)
			retzkl = retzkl * 100;

		double zkl = (100 - retzkl) / 100.0D;
		double zke = ManipulatePrecision.doubleConvert(total * zkl, 2, 1);

		saleHead.num10 = zkl;
		ftzke(zke, type, false);
		/*
		 * String[] item = goodsIndex.split(","); if ((item == null) ||
		 * (item.length < 1)) return false;
		 * 
		 * double hasftje = 0.0;
		 * 
		 * for (int j = 0; j < item.length - 1; j++) { String[] ftinfo =
		 * item[j].split("\\|");
		 * 
		 * if (Convert.toInt(ftinfo[0]) == Convert.toInt(item[item.length - 1]))
		 * continue;
		 * 
		 * double sgdftje = (Convert.toDouble(ftinfo[1]) / total) * zke;
		 * SaleGoodsDef sgd = (SaleGoodsDef)
		 * this.saleGoods.get(Convert.toInt(ftinfo[0])); if (type == 0)
		 * sgd.qtzre = ManipulatePrecision.doubleConvert(sgdftje, 2, 1); else
		 * sgd.qtzke = ManipulatePrecision.doubleConvert(sgdftje, 2, 1); hasftje
		 * += sgdftje;
		 * 
		 * getZZK(sgd); }
		 * 
		 * SaleGoodsDef sgdend = (SaleGoodsDef)
		 * this.saleGoods.get(Convert.toInt(item[item.length - 1])); if (type ==
		 * 0) sgdend.qtzre = ManipulatePrecision.doubleConvert(zke - hasftje, 2,
		 * 1); else sgdend.qtzke = ManipulatePrecision.doubleConvert(zke -
		 * hasftje, 2, 1);
		 * 
		 * getZZK(sgdend);
		 */

		String log = "";
		if (type == 0)
		{
			log = "授权整单折扣,小票号:" + this.saleHead.fphm + ",折扣权限:" + retinfo[2] + "%,员工卡:" + retinfo[0];
		}
		else
		{
			log = "授权整单折扣,小票号:" + this.saleHead.fphm + ",折扣权限:" + retinfo[2] + "%,惠民卡:" + retinfo[0];
		}

		AccessDayDB.getDefault().writeWorkLog(log);

		return true;
	}

	protected void ftWdq(String card, double totalzke)
	{

	}

	protected void ftGwj(String card, double totalzke)
	{
		String goodsIndex = "";
		double total = 0.0D;
		int maxmoneyindex = 0;
		double exgmoney = 0.0;

		for (int i = 0; i < this.saleGoods.size(); i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) this.saleGoods.get(i);

			if (sgd.hjzk > 0.0D)
				continue;

			if (i == 0)
			{
				exgmoney = sgd.hjje;
				maxmoneyindex = i;
			}
			else
			{
				if (sgd.hjje > exgmoney)
				{
					exgmoney = sgd.hjje;
					maxmoneyindex = i;
				}
			}

			goodsIndex = goodsIndex + i + "|" + sgd.hjje + ",";
			total += sgd.hjje;

			if (i == saleGoods.size() - 1)
				goodsIndex = goodsIndex + maxmoneyindex;
		}

		total = ManipulatePrecision.doubleConvert(total, 2, 1);

		if (goodsIndex.trim().equals(""))
			return;

		String[] item = goodsIndex.split(",");
		if ((item == null) || (item.length < 1))
			return;

		double hasftje = 0.0;

		for (int j = 0; j < item.length - 1; j++)
		{
			if (j == Convert.toInt(item[item.length - 1]))
				continue;

			String[] ftinfo = item[j].split("\\|");
			double sgdftje = (Convert.toDouble(ftinfo[1]) / total) * totalzke;
			SaleGoodsDef sgd = (SaleGoodsDef) this.saleGoods.get(Convert.toInt(ftinfo[0]));
			sgd.qtzre = ManipulatePrecision.doubleConvert(sgdftje, 2, 1);
			hasftje += sgdftje;
			getZZK(sgd);
		}

		SaleGoodsDef sgdend = (SaleGoodsDef) this.saleGoods.get(Convert.toInt(item[item.length - 1]));
		sgdend.qtzre = ManipulatePrecision.doubleConvert(totalzke - hasftje, 2, 1);
		getZZK(sgdend);

		String log = "授权整单折让,小票号:" + this.saleHead.fphm + ",折让金额:" + totalzke + ",购物券:" + card;
		AccessDayDB.getDefault().writeWorkLog(log);
	}

	protected boolean goodsZszRebate(int type, String[] retinfo)
	{
		double avalidMoney = ManipulatePrecision.doubleConvert(Convert.toDouble(retinfo[3]) - Convert.toDouble(retinfo[4]), 2, 1);
		if (avalidMoney <= 0)
		{
			new MessageBox("本月可享受折扣总金额【" + Convert.toDouble(retinfo[3]) + "】已用完");
			return false;
		}
		// 记录总金额
		saleHead.num6 = ManipulatePrecision.doubleConvert(Convert.toDouble(retinfo[3]), 2, 1);

		String goodsIndex = "";
		double total = 0.0;
		int maxmoneyindex = -1;
		double exgmoney = 0;

		for (int i = 0; i < this.saleGoods.size(); i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) this.saleGoods.get(i);

			if (i == 0)
			{
				exgmoney = sgd.hjje - sgd.hjzk;
				maxmoneyindex = i;
			}
			else
			{
				if (sgd.hjje - sgd.hjzk > exgmoney)
				{
					exgmoney = sgd.hjje - sgd.hjzk;
					maxmoneyindex = i;
				}
			}
			if (total + (sgd.hjje - sgd.hjzk) > avalidMoney)
			{
				double tmpmoney = (sgd.hjje - sgd.hjzk) - (total + (sgd.hjje - sgd.hjzk) - avalidMoney);

				sgd.num6 = tmpmoney;
				goodsIndex = goodsIndex + i + "|" + tmpmoney + ",";
				total += tmpmoney;
				goodsIndex = goodsIndex + maxmoneyindex;
				break;
			}

			goodsIndex = goodsIndex + i + "|" + (sgd.hjje - sgd.hjzk) + ",";
			sgd.num6 = (sgd.hjje - sgd.hjzk);
			total = total + (sgd.hjje - sgd.hjzk);
			if (i == saleGoods.size() - 1)
				goodsIndex = goodsIndex + maxmoneyindex;
		}

		total = ManipulatePrecision.doubleConvert(total, 2, 1);

		if (goodsIndex.trim().equals(""))
			return false;

		double retzkl = Convert.toDouble(retinfo[2]);

		if (retzkl < 1)
			retzkl = retzkl * 100;

		double zkl = (100 - retzkl) / 100;
		double zke = ManipulatePrecision.doubleConvert(total * zkl, 2, 1);

		saleHead.num10 = zkl;

		ftzke(zke, type, true);

		/*
		 * String[] item = goodsIndex.split(","); if ((item == null) ||
		 * (item.length < 1)) return false;
		 * 
		 * double hasftje = 0;
		 * 
		 * for (int j = 0; j < item.length - 1; j++) { String[] ftinfo =
		 * item[j].split("\\|");
		 * 
		 * if (Convert.toInt(ftinfo[0]) == Convert.toInt(item[item.length - 1]))
		 * continue;
		 * 
		 * double sgdftje = Convert.toDouble(ftinfo[1]) / total * zke;
		 * SaleGoodsDef sgd = (SaleGoodsDef)
		 * this.saleGoods.get(Convert.toInt(ftinfo[0])); if (type == 0)
		 * sgd.qtzre = ManipulatePrecision.doubleConvert(sgdftje, 2, 1); else
		 * sgd.qtzke = ManipulatePrecision.doubleConvert(sgdftje, 2, 1); hasftje
		 * += sgdftje;
		 * 
		 * getZZK(sgd); }
		 * 
		 * SaleGoodsDef sgdend = (SaleGoodsDef)
		 * this.saleGoods.get(Convert.toInt(item[item.length - 1])); if (type ==
		 * 0) sgdend.qtzre = ManipulatePrecision.doubleConvert(zke - hasftje, 2,
		 * 1); else sgdend.qtzke = ManipulatePrecision.doubleConvert(zke -
		 * hasftje, 2, 1);
		 * 
		 * getZZK(sgdend);
		 */

		String log = "";
		if (type == 0)
		{
			log = "授权整单折扣,小票号:" + this.saleHead.fphm + ",折扣权限:" + retinfo[2] + "%,员工卡:" + retinfo[0];
		}
		else
		{
			log = "授权整单折扣,小票号:" + this.saleHead.fphm + ",折扣权限:" + retinfo[2] + "%,惠民卡:" + retinfo[0];
		}

		AccessDayDB.getDefault().writeWorkLog(log);

		return true;
	}

	protected void ftzke(double zke, int type, boolean iszsz)
	{
		int lastzzkrow = -1;
		double totalje = 0;
		double tmpje = 0;
		double hjzzk = 0;
		SaleGoodsDef saleGoodsDef = null;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			// 累计可折扣金额
			if (!iszsz)
				totalje = totalje + saleGoodsDef.hjje;
			else
				totalje = totalje + (saleGoodsDef.hjje - saleGoodsDef.hjzk);

			double maxzzk = 0;
			if (!iszsz)
				maxzzk = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje, 2, 1);
			else
				maxzzk = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - saleGoodsDef.hjzk, 2, 1);

			if (maxzzk > tmpje)
			{
				tmpje = maxzzk;
				lastzzkrow = i;
			}
		}

		for (int j = 0; j < saleGoods.size(); j++)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(j);

			// 每个商品分摊的折让按金额占比计算
			if (j != lastzzkrow)
			{
				if (!iszsz)
				{
					if (type == 0)
						saleGoodsDef.qtzre = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje / totalje * zke, 2, 1);
					else
						saleGoodsDef.qtzke = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje / totalje * zke, 2, 1);
				}
				else
				{
					if (type == 0)
						saleGoodsDef.qtzre = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - saleGoodsDef.hjzk) / totalje * zke, 2, 1);
					else
						saleGoodsDef.qtzke = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - saleGoodsDef.hjzk) / totalje * zke, 2, 1);
				}

				// 重算商品折扣合计
				getZZK(saleGoodsDef);
				// 计算已分摊的总折让
				if (type == 0)
					hjzzk += saleGoodsDef.qtzre;
				else
					hjzzk += saleGoodsDef.qtzke;

			}
		}

		// 可折让金额最大商品的折扣用减法直接等于剩余的分摊,最后计算
		if (lastzzkrow >= 0)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(lastzzkrow);
			if (type == 0)
				saleGoodsDef.qtzre = ManipulatePrecision.doubleConvert(zke - hjzzk, 2, 1);
			else
				saleGoodsDef.qtzke = ManipulatePrecision.doubleConvert(zke - hjzzk, 2, 1);

			getZZK(saleGoodsDef);
		}

	}

	public String[] findcard(int type)
	{
		StringBuffer cardno = new StringBuffer();
		TextBox txt = new TextBox();
		String[] retinfo = new String[6];

		if (type == 0)
		{
			if (!txt.open("请刷全场折扣卡", "全场折扣卡", "请将全场折扣卡从刷卡槽刷入", cardno, 0.0D, 0.0D, false, TextBox.MsrKeyInput))
				return null;

		}
		else if (!txt.open("请刷惠民卡", "惠民卡", "若无惠民卡请按【退出键】", cardno, 0.0D, 0.0D, false, TextBox.MsrKeyInput))
			return null;

		if ((txt.Track2 == null) || (txt.Track2.trim().equals("")))
		{
			new MessageBox("数据无效");
			return null;
		}

		retinfo = new String[6];

		if (!((Hbgy_NetService) NetService.getDefault()).findRebateCard(retinfo, type, txt.Track2))
		{
			new MessageBox("查找到卡信息失败!");
			return null;
		}

		if ((retinfo[0] == null) || (retinfo[1] == null) || (retinfo[2] == null) || (retinfo[3] == null) || (retinfo[4] == null) || (retinfo[5] == null))
		{
			new MessageBox("查找到卡数据不完整!");
			return null;
		}

		return retinfo;
	}

	public void execCustomKey1(boolean keydownonsale)
	{
		if (!GlobalInfo.isOnline)
		{
			new MessageBox("断网状态下不允许使用员工卡");
			return;
		}

		if (keydownonsale)
		{
			new MessageBox("请在付款界面使用该功能!");
			return;
		}

		if (saleHead.str3 != null && saleHead.str3.length() > 1)
		{
			new MessageBox("当前交易已刷过员工卡!");
			return;
		}

		if (saleHead.str4 != null && saleHead.str4.length() > 1)
		{
			new MessageBox("当前交易已刷过惠民卡!");
			return;
		}
		String[] retinfo = new String[6];

		retinfo = findcard(0);

		if (retinfo == null)
			return;

		if (GlobalInfo.sysPara.ishmkzsz == 'Y')
		{
			if (!goodsZszRebate(0, retinfo))
				return;
		}
		else
		{
			if (!goodsNoZszRebate(0, retinfo))
				return;
		}

		saleHead.num7 = Convert.toDouble(retinfo[4]);
		this.saleHead.str3 = retinfo[0];
		this.saleHead.str9 = retinfo[5];

		calcHeadYsje();

		if (keydownonsale)
		{
			saleEvent.updateTable(getSaleGoodsDisplay());
			saleEvent.setTotalInfo();
			saleEvent.setCurGoodsBigInfo();
			NewKeyListener.sendKey(GlobalVar.Pay);
		}
		else
		{
			if (salePayEvent != null)
			{
				saleEvent.updateTable(getSaleGoodsDisplay());
				saleEvent.setTotalInfo();
				saleEvent.setCurGoodsBigInfo();
				salePayEvent.refreshFeeLabel();
				salePayEvent.showSalePaymentDisplay();
				salePayEvent.calcPayResult();
			}
		}
	}

	public void execCustomKey2(boolean keydownonsale)
	{
		if (!GlobalInfo.isOnline)
		{
			new MessageBox("断网状态下不允许使用惠民卡");
			return;
		}

		if (keydownonsale)
		{
			new MessageBox("请在付款界面使用该功能!");
			return;
		}

		if (saleHead.str4 != null && saleHead.str4.length() > 1)
		{
			new MessageBox("当前交易已刷过惠民卡!");
			return;
		}

		if (saleHead.str3 != null && saleHead.str3.length() > 1)
		{
			new MessageBox("当前交易已刷过员工卡!");
			return;
		}

		String[] retinfo = new String[6];
		retinfo = findcard(1);

		if (retinfo == null)
			return;

		if (GlobalInfo.sysPara.ishmkzsz == 'Y')
		{
			if (!goodsZszRebate(1, retinfo))
				return;
		}
		else
		{
			if (!goodsNoZszRebate(1, retinfo))
				return;
		}

		saleHead.num7 = Convert.toDouble(retinfo[4]);
		saleHead.str4 = retinfo[0];
		saleHead.str9 = retinfo[5];

		calcGovermentRebate(retinfo[0]);

		calcHeadYsje();

		if (keydownonsale)
		{
			saleEvent.updateTable(getSaleGoodsDisplay());
			saleEvent.setTotalInfo();
			saleEvent.setCurGoodsBigInfo();
			NewKeyListener.sendKey(GlobalVar.Pay);
		}
		else
		{
			if (salePayEvent != null)
			{
				saleEvent.updateTable(getSaleGoodsDisplay());
				saleEvent.setTotalInfo();
				saleEvent.setCurGoodsBigInfo();
				salePayEvent.refreshFeeLabel();
				salePayEvent.showSalePaymentDisplay();
				salePayEvent.calcPayResult();
			}
		}
	}

	public void execCustomKey3(boolean keydownonsale)
	{
		if (saleGoods.size() == 0)
		{
			new MessageBox("请先录完商品后再使用该功能!");
			return;
		}

		if (saleHead.str5 != null && saleHead.str5.length() > 1)
		{
			new MessageBox("当前交易已使用过购物卡!");
			return;
		}

		double totalje = 0;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);
			if (sgd.hjzk > 0)
				continue;
			totalje = totalje + sgd.hjje;
		}
		if (new MessageBox("当前非促销商品总金额:" + totalje + "  是否使用购物券?", null, true).verify() == GlobalVar.Key2)
			return;

		StringBuffer card = new StringBuffer();
		StringBuffer money = new StringBuffer();
		TextBox txt = new TextBox();

		if (!txt.open("请输入购物券卡号", "购物券", "输入购物券卡号", card, 0.0, 0.0, false, TextBox.IntegerInput))
			return;

		if (!txt.open("请输入购物券金额", "购物券", "输入的购物券金额不能大于非促销商品总金额", money, 0.0, 0.0, false, TextBox.MsrKeyInput))
			return;

		if (Convert.toDouble(money.toString()) > totalje)
		{
			new MessageBox("购物券金额不能大于非促销商品总金额");
			return;
		}

		ftGwj(card.toString(), Convert.toDouble(money.toString()));

		this.saleHead.str5 = card.toString();
		calcHeadYsje();

		if (keydownonsale)
		{
			saleEvent.updateTable(getSaleGoodsDisplay());
			saleEvent.setTotalInfo();
			saleEvent.setCurGoodsBigInfo();
			NewKeyListener.sendKey(GlobalVar.Pay);
		}
		else
		{
			if (salePayEvent != null)
			{
				saleEvent.updateTable(getSaleGoodsDisplay());
				saleEvent.setTotalInfo();
				salePayEvent.refreshFeeLabel();
				saleEvent.setCurGoodsBigInfo();
				salePayEvent.calcPayResult();
			}
		}
	}

	public void execCustomKey4(boolean keydownonsale)
	{
		if (saleGoods.size() == 0)
		{
			new MessageBox("请先录完商品后再使用该功能!");
			return;
		}

		if (saleHead.str5 != null && saleHead.str5.length() > 1)
		{
			new MessageBox("当前交易已使用过微店券!");
			return;
		}

	}

	// CMPOP促销模型
	public void findGoodsCMPOPInfo(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		if (!SellType.ISSALE(this.saletype))
			return;

		// 券连网使用，查找网上失败后不查询本地
		findGoodsRuleFromCRM(sg, goods, info);

		// 查找商品的促销结果集
		Vector popvec = ((Hbgy_DataService) DataService.getDefault()).findCMPOPGoods(saleHead.rqsj, goods, curCustomer != null ? curCustomer.code : "", curCustomer != null ? curCustomer.type : "");
		goodsCmPop.add(popvec);

		filterCMPOPInfo(popvec);
	}

	public void doSaleFinshed(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		if (!((Hbgy_NetService) NetService.getDefault()).sendTotalAmount(saleHead, saleGoods))
		{
			AccessDayDB.getDefault().writeWorkLog("上传员工卡/惠民卡销售累计金额失败");
			AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDTOTALAMOUNT, GlobalInfo.balanceDate + "," + saleHead.fphm);
		}

	}

	public void printSaleBill()
	{
		String info = ((Hbgy_NetService) NetService.getDefault()).getGwlInfo();

		if (info != null)
		{
			this.saleHead.str10 = info;
		}
		super.printSaleBill();
	}
}