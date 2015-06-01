package custom.localize.Hbgy;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Struct.CmPopGoodsDef;
import com.efuture.javaPos.Struct.JfSaleRuleDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;

import custom.localize.Bstd.Bstd_DataService;
import custom.localize.Bstd.Bstd_SaleBS;

public class Hbgy_SaleBS0JfExchange extends Bstd_SaleBS
{
	// 查找商品是否存在换购规则
	public void findJfExchangeGoods(int index)
	{

		if (hhflag == 'Y')
		{
			new MessageBox("换货状态不允许使用积分换购");
			return;
		}
		// 无会员卡不进行积分换购
		if (curCustomer == null)
		{
			new MessageBox("没有刷会员卡不允许积分换购");
			return;
		}

		// 无0509付款方式,不能进行积分换购
		PayModeDef paymode = DataService.getDefault().searchPayMode("0509");
		if (paymode == null)
		{
			new MessageBox("没有[0509]积分换购付款方式");
			return;
		}

		// 查找积分换购商品规则
		JfSaleRuleDef jfrd = new JfSaleRuleDef();
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.get(index);

		if (saleGoodsDef.mjzke > 0)
		{
			new MessageBox("该商品已享受过积分换购,不可多次换购");
			return;
		}

		if (!((Bstd_DataService) DataService.getDefault()).getJfExchangeGoods(jfrd, saleGoodsDef.code, curCustomer.code, curCustomer.type))
			return;
		// 加入日志

		if (!checkCust())
			return;

		if ((saleGoodsDef.hjje - saleGoodsDef.hjzk) <= jfrd.money * saleGoodsDef.sl)
		{
			new MessageBox("当前商品销售金额小于等于兑换金额\n不能进行换购");

			return;
		}

		if (curCustomer.valuememo < jfrd.jf)
		{
			if (GlobalInfo.sysPara.autojfexchange == 'Y')
				return;

			new MessageBox("当前会员卡的积分小于换购积分\n不能进行换购");

			return;
		}

		// 弹出提示框
		String message = "积分加上";
		MessageBox me = new MessageBox("您目前可用" + jfrd.jf * saleGoodsDef.sl + message + ManipulatePrecision.doubleToString(jfrd.money * saleGoodsDef.sl) + "元\n换购该商品\n是否要进行换购?", null, true);

		if (me.verify() != GlobalVar.Key1)
			return;

		if (curCustomer.valuememo - (jfrd.jf * saleGoodsDef.sl) < 0)
		{
			new MessageBox("商品数量过大,所需积分已超过会员当前总积分【" + curCustomer.valuememo + "】\n\n商品数量修改无效");
			return;
		}

		if (ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - saleGoodsDef.hjzk - jfrd.money * saleGoodsDef.sl, 2, 1) < 0)
		{
			new MessageBox("当前换购折扣过大\n不能进行换购");
			return;
		}

		clearZZK(saleGoodsDef);
		getZZK(saleGoodsDef);

		saleGoodsDef.mjzke = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - saleGoodsDef.hjzk - jfrd.money * saleGoodsDef.sl, 2, 1);
		saleGoodsDef.num4 = jfrd.jf * saleGoodsDef.sl;
		saleGoodsDef.yhdjbh = "w";
		curCustomer.valuememo = curCustomer.valuememo - jfrd.jf * saleGoodsDef.sl;

		getZZK(saleGoodsDef);
		calcHeadYsje();

		SaleGoodsDef sgd = (SaleGoodsDef) saleGoodsDef.clone();
		sgd.name = "(" + ManipulatePrecision.doubleToString(jfrd.money * saleGoodsDef.sl) + "元 + 积分" + saleGoodsDef.num4 + "换购)" + sgd.name;

		saleEvent.table.modifyRow(rowInfo(sgd), index);
		saleEvent.setTotalInfo();
		saleEvent.setCurGoodsBigInfo();
	}

	public boolean calcGoodsCMPOPRebate(int index, CmPopGoodsDef cmp, int cmpindex)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		if (saleGoodsDef.yhdjbh == "w")
			return true;

		return super.calcGoodsCMPOPRebate(index, cmp, cmpindex);
	}

	protected boolean doneDeleteGoods(int index, SaleGoodsDef old_goods)
	{
		try
		{
			SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(index);
			if (sgd.yhdjbh.equals("w") && sgd.yhzke > 0)
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

	public boolean SaleCollectAccountPayJf()
	{
		int totaljf = 0;
		String jfinfo = "";
		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);

			if (sgd.yhdjbh.equals("w") && sgd.mjzke > 0)
			{
				sgd.yhzke += sgd.mjzke;
				totaljf += sgd.num4;
				jfinfo = jfinfo + (sgd.num4 + "|" + sgd.code + "|") + ",";
			}
			saleHead.num8 = totaljf;
		}

		if (totaljf <= 0)
			return true;

		PayModeDef paymode = DataService.getDefault().searchPayMode("0509");
		if (paymode == null)
		{
			new MessageBox("发送积分换购失败\n没有[0509]积分换购付款方式");
			return false;
		}

		Hbgy_PaymentCustJfSale pay = new Hbgy_PaymentCustJfSale(paymode, this);
		pay.createJfExchangeSalePay(0, totaljf, jfinfo, -1);

		if (!pay.collectAccountPay())
			return false;

		return true;

	}
}
