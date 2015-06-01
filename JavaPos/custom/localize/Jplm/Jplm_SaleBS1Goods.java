package custom.localize.Jplm;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;

import custom.localize.Shhl.Shhl_NetService;

public class Jplm_SaleBS1Goods extends Jplm_SaleBS0CalcPop
{

	public boolean findGoods(String code, String yyyh, String gz, String memo)
	{
		String[] cutpriceinfo = null;

		if (!GlobalInfo.sysPara.stampprefix.equals("") && code.startsWith(GlobalInfo.sysPara.stampprefix))
		{
			if (saleGoods.size() == 0)
			{
				new MessageBox("请录入该折扣码对应的商品!");
				return false;
			}

			cutpriceinfo = new String[3];
			// 查找削价码对应的商品
			if (!((Shhl_NetService) NetService.getDefault()).findStampGoods(code, cutpriceinfo) || cutpriceinfo[0] == null || cutpriceinfo[1] == null)
			{
				new MessageBox("校验折扣码有效性失败!");
				return false;
			}
		}

		if (cutpriceinfo != null)
		{
			GoodsDef gds = (GoodsDef) goodsAssistant.get(goodsAssistant.size() - 1);

			if (!gds.code.equals(cutpriceinfo[0]))
			{
				new MessageBox("该折扣码无法对商品[" + gds.barcode + "]进行打折!");
				return false;
			}

			gds.poptype = '2';

			if (Convert.toInt(cutpriceinfo[1]) > 1)
				gds.poplsjzkl = ManipulatePrecision.doubleConvert(Convert.toDouble(cutpriceinfo[1]) / 100, 2, 1);
			else
				gds.poplsjzkl = ManipulatePrecision.doubleConvert(Convert.toDouble(cutpriceinfo[1]), 2, 1);

			SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(goodsAssistant.size() - 1);
			sgd.yhdjbh = cutpriceinfo[2]; // 记录优惠单据编号

			calcAllRebate(goodsAssistant.size() - 1, false);

			getZZK(sgd);
			calcHeadYsje();

			refreshSaleForm();
			return true;
		}

		return super.findGoods(code, yyyh, gz, memo);
	}

}
