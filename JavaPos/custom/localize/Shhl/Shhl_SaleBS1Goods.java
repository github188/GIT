package custom.localize.Shhl;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;

public class Shhl_SaleBS1Goods extends Shhl_SaleBS0CalcPop
{

	public boolean findGoods(String code, String yyyh, String gz, String memo)
	{
		if (GlobalInfo.sysPara.customerbysale == 'Y' && (saletype.equals(SellType.RETAIL_SALE) || saletype.equals(SellType.RETAIL_BACK)))
		{
			if (curCustomer == null)
			{
				new MessageBox("请先刷会员卡!");
				NewKeyListener.sendKey(GlobalVar.MemberGrant);
				return true;
			}
		}

		if (!GlobalInfo.sysPara.stampprefix.equals("") && code.startsWith(GlobalInfo.sysPara.stampprefix))
		{
			if (saleGoods.size() == 0)
			{
				new MessageBox("请录入该打折码对应的商品!");
				return false;
			}
			int index = goodsAssistant.size() - 1;
			SaleGoodsDef sgdf = (SaleGoodsDef) saleGoods.get(index);
			if (sgdf.str7 != null && (sgdf.str7.equals(code) || sgdf.str7.startsWith(GlobalInfo.sysPara.stampprefix)))
			{
				new MessageBox("该打折码在此单已被使用!");
				return false;
			}

			String[] cutpriceinfo = new String[3];
			// 查找削价码对应的商品
			if (!((Shhl_NetService) NetService.getDefault()).findStampGoods(code, cutpriceinfo) || cutpriceinfo[0] == null || cutpriceinfo[1] == null)
			{
				new MessageBox("校验打折码有效性失败!");
				return false;
			}
			if (cutpriceinfo != null)
			{
				GoodsDef gds = (GoodsDef) goodsAssistant.get(index);

				if (!gds.code.equals(cutpriceinfo[0]))
				{
					new MessageBox("该打折码无法对商品[" + gds.barcode + "]进行打折!");
					return false;
				}

				gds.poptype = '2';

				if (Convert.toInt(cutpriceinfo[1]) > 1)
					gds.poplsjzkl = ManipulatePrecision.doubleConvert(Convert.toDouble(cutpriceinfo[1]) / 100, 2, 1);
				else
					gds.poplsjzkl = ManipulatePrecision.doubleConvert(Convert.toDouble(cutpriceinfo[1]), 2, 1);

				SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(index);
				sgd.yhdjbh = cutpriceinfo[2]; // 记录优惠单据编号
				sgd.str7 = code;

				calcAllRebate(index, false);

				getZZK(sgd);
				calcHeadYsje();

				refreshSaleForm();
				return true;
			}
		}
		else if (code.startsWith("28"))
		{
			if (saleGoods.size() == 0)
			{
				new MessageBox("请录入该临保码对应的商品!");
				return false;
			}

			if (code.length() < 19)
			{
				new MessageBox("临保码长度不合法!");
				return false;
			}
			int index = goodsAssistant.size() - 1;
			GoodsDef gds = (GoodsDef) goodsAssistant.get(index);
			SaleGoodsDef sgdf = (SaleGoodsDef) saleGoods.get(index);

			if (sgdf.str8 != null && (sgdf.str8.equals(code) || sgdf.str8.startsWith("28")))
			{
				new MessageBox("临保码不可连续使用!");
				return false;
			}

			String goodscode = code.substring(2, 10);
			String selflife = code.substring(10, 18);

			if (!gds.code.equals(goodscode))
			{
				new MessageBox("该临保码无法对商品[" + gds.barcode + "]进行打折!");
				return false;
			}

			String[] discinfo = new String[1];

			// 查找削价码对应的商品
			if (!((Shhl_NetService) NetService.getDefault()).findGoodsSelfLife(goodscode, selflife, discinfo))
			{
				new MessageBox("校验临保码有效性失败!");
				return false;
			}

			if (discinfo == null || discinfo.length == 0)
				return false;

			// 折扣为0代表商品过期
			if (Convert.toDouble(discinfo[0].trim()) == 0)
			{
				new MessageBox("该商品已过期!");

				SaleGoodsDef old_goods = (SaleGoodsDef) saleGoods.elementAt(index);
				SaleGoodsDef cloneGoods = (SaleGoodsDef) old_goods.clone();
				old_goods.sl = 0;

				// 重算因为删除本行，对其他行商品产生的影响
				old_goods.hjje = old_goods.jg * old_goods.sl;
				clearGoodsGrantRebate(index);
				calcGoodsYsje(index);

				// 删除数量为零的商品
				if (0.0 == old_goods.sl)
				{
					if (!delSaleGoodsObject(index))
						return false;
				}

				// 计算小票合计
				calcHeadYsje();

				// 删除上次显示列表,刷新显示列表
				getDeleteGoodsDisplay(index, cloneGoods);

				return false;
			}

			gds.poptype = '2';

			if (Convert.toInt(discinfo[0]) > 1)
				gds.poplsjzkl = ManipulatePrecision.doubleConvert(Convert.toDouble(discinfo[0]) / 100, 2, 1);
			else
				gds.poplsjzkl = ManipulatePrecision.doubleConvert(Convert.toDouble(discinfo[0]), 2, 1);

			SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(goodsAssistant.size() - 1);
			sgd.str8 = code;

			calcAllRebate(goodsAssistant.size() - 1, false);

			getZZK(sgd);
			calcHeadYsje();

			refreshSaleForm();
			return true;
		}

		return super.findGoods(code, yyyh, gz, memo);
	}
}
