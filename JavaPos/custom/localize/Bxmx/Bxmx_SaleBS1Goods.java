package custom.localize.Bxmx;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Bxmx_SaleBS1Goods extends Bxmx_SaleBS0PopOrData
{
	// 商品提货券
	public boolean findGoods(String code, String yyyh, String gz)
	{
		if (SellType.ISCARD(saletype))
		{
			new MessageBox("售卡交易不允许录入商品，请刷卡");
			return false;
		}

		if (SellType.ISEARNEST(saletype))
		{
			if (fetchinfo == null)
			{
				new MessageBox("请重新操作并录入取货人信息");
				return false;
			}
		}

		if (isgoodscoupon)
		{
			new MessageBox("商品提货券不允许新增商品");
			return false;
		}

		Vector info = new Vector();
		try
		{
			if (((Bxmx_NetService) NetService.getDefault()).findGoodsCoupon(code, info) && info != null && info.size() > 0)
			{
				if (saleGoods.size() > 0)
				{
					new MessageBox("当前交易未完成，请完成当前交易");
					return false;
				}

				String[] title = { "商品编码", "商品名称", "单位" };
				int[] width = { 200, 200, 100 };
				String[] content = null;
				Vector contents = new Vector();
				for (int i = 0; i < info.size(); i++)
				{
					String[] tmp = (String[]) info.get(i);
					content = new String[3];
					content[0] = tmp[0];
					content[1] = tmp[1];
					content[2] = tmp[2];

					goodscouponye = Convert.toDouble(tmp[3].trim());

					contents.add(content);
				}

				int choice = -1;
				if ((contents.size() > 0 && (choice = new MutiSelectForm().open("请选择一个商品", title, width, contents)) >= 0))
				{
					String[] selgoods = (String[]) info.get(choice);
					goodscouponcode = code;
					code = selgoods[0];
					isgoodscoupon = true;
				}

			}
			return super.findGoods(code, yyyh, gz);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean findCoupon(String code, String yyyh, String gz)
	{
		// 检查是否允许找商品
		if (!allowStartFindGoods())
			return false;

		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);
			if (sgd.code.equals(code))
			{
				new MessageBox("卡/券号:" + code + "已在列表中存在\n不允许重复售卖");
				return false;
			}
		}

		Vector retVec = new Vector();
		// 校验卡号
		if (((Bxmx_NetService) NetService.getDefault()).sellCardOrCoupon(SellType.ISBACK(saletype) ? "2" : "0", SellType.ISCARD(saletype) ? "0" : "1", code, "", "1", "", retVec))
		{
			String[] info = (String[]) retVec.get(0);
			if (info == null || info.length < 1 || info[0] == null)
				return false;;

			String[] cardinfo = info[0].split("#");
			if (cardinfo == null || cardinfo.length < 1)
				return false;

			GoodsDef goodsDef = new GoodsDef();

			if (cardinfo.length < 1 || cardinfo[0] == null)
				return false;

			boolean issamegoods = false;
			for (int i = 0; i < saleGoods.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);
				if (sgd.code.equals(cardinfo[0]))
				{
					new MessageBox("卡/券号:" + cardinfo[0] + "已在列表中存在\n相同卡号不允许重复售卖");
					issamegoods = true;
					break;
				}
			}

			if (issamegoods)
				return false;

			goodsDef.barcode = cardinfo[0];
			goodsDef.code = cardinfo[0];

			if (cardinfo.length < 2 && cardinfo[1] == null)
				return false;

			goodsDef.lsj = Convert.toDouble(cardinfo[1]);

			goodsDef.gz = "00";
			goodsDef.uid = "00";
			goodsDef.name = "卡券商品";
			goodsDef.unit = "张";
			goodsDef.issqkzk = 'Y';
			goodsDef.isvipzk = 'N';

			SaleGoodsDef saleGoodsDef = goodsDef2SaleGoods(goodsDef, saleEvent.yyyh.getText(), 1, goodsDef.lsj, 0, false);

			// 记录退货折扣
			if (cardinfo.length > 2 && cardinfo[2] != null)
				saleGoodsDef.lszzk = Convert.toDouble(cardinfo[2]);

			addSaleGoodsObject(saleGoodsDef, goodsDef, getGoodsSpareInfo(goodsDef, saleGoodsDef));
			getZZK(saleGoodsDef);

			calcHeadYsje();

			refreshSaleForm();
			saleEvent.updateSaleGUI();
			return true;
		}

		return false;
	}

	public void addSalePayObject(SalePayDef spay, Payment payobj)
	{
		super.addSalePayObject(spay, payobj);

		// 不是同一门店时，则提示是否结束付款，若是同一门店，则要付全款
		if (SellType.ISEARNEST(saletype) && !SellType.ISBACK(saletype) && GlobalInfo.sysPara.mktcode.equals(fetchinfo.fetchmkt))
		{
			double ye = calcPayBalance();
			if (ye > 0 && new MessageBox("是否结束预收付款?", null, true).verify() == GlobalVar.Key1)
			{
				PayModeDef pmd = (PayModeDef) DataService.getDefault().searchPayMode("01").clone();
				pmd.code = "00";
				pmd.name = "未付款";

				Payment pay = CreatePayment.getDefault().createPaymentByPayMode(pmd, saleEvent.saleBS);
				SalePayDef sp = pay.inputPay(String.valueOf(ye));

				// 记录下未付金额

				super.addSalePayObject(sp, pay);
				
			}

		}
		

	}

	public boolean inputQuantity(int index)
	{
		if (SellType.ISCOUPON(saletype))
		{
			new MessageBox("卡券销售无法修改数量");
			return false;
		}

		if (SellType.ISPREPARETAKE(saletype))
		{
			new MessageBox("定金取货不允许修改数量");
			return false;
		}
		if (SellType.ISCARD(saletype))
		{
			new MessageBox("售卡不允许修改数量");
			return false;
		}
		if (isgoodscoupon)
		{
			new MessageBox("商品提货券不允许修改数量");
			return false;
		}

		return super.inputQuantity(index);
	}

	protected boolean cancelMemberOrGoodsRebate(int index)
	{
		if (SellType.ISCARD(saletype) || SellType.ISCOUPON(saletype))
			return false;

		return super.cancelMemberOrGoodsRebate(index);
	}

	public boolean allowEditGoods()
	{
		if ((SellType.ISCARD(saletype) || SellType.ISCOUPON(saletype)) && SellType.ISBACK(saletype))
		{
			new MessageBox("不允许修改单据");
			return false;
		}

		// 商品券
		if (isgoodscoupon)
		{
			new MessageBox("不允许修改单据");
			return false;
		}

		// 团购时不允许修改商品
		if (saletype.equals(SellType.GROUPBUY_SALE) && !Groupbuy_Change())
		{
			new MessageBox("团购不允许修改交易信息");
			return false;
		}

		// 会员卡必须在商品输完后刷,那么刷卡以后不能修改商品
		if (GlobalInfo.sysPara.customvsgoods == 'B' && checkMemberSale())
		{
			new MessageBox("已刷VIP卡,不能再修改商品\n\n请付款或取消VIP卡后再输入");
			return false;
		}

		if (isPreTakeStatus())
		{
			new MessageBox("预售提货状态下不允许修改商品状态");
			return false;
		}

		if ((SellType.PREPARE_BACK.equals(this.saletype)))
		{
			new MessageBox("预售退货状态下不允许修改商品状态");
			return false;
		}

		// 已经积分换购了的商品不允许进行修改,只能删除
		if (saleEvent.table.getSelectionIndex() >= 0 && goodsSpare.size() > saleEvent.table.getSelectionIndex())
		{
			SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(saleEvent.table.getSelectionIndex());
			if (info != null && info.char2 == 'Y')
			{
				new MessageBox("当前商品是已进行积分换购,不允许修改\n\n请删除后重新输入");
				return false;
			}
		}

		return true;
	}

	public boolean deleteGoods(int index)
	{
		if (SellType.ISPREPARETAKE(saletype))
		{
			new MessageBox("定金取货不允许删除商品");
			return false;
		}

		if (super.deleteGoods(index))
		{
			initSaleData();
			return true;
		}
		return false;
	}
}
