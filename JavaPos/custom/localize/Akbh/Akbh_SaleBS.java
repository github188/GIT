package custom.localize.Akbh;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Struct.SaleGoodsDef;

import custom.localize.Cmls.Cmls_SaleBS;

public class Akbh_SaleBS extends Cmls_SaleBS
{
	public void execCustomKey2(boolean keydownonsale)
	{
		int index = saleEvent.table.getSelectionIndex();
		if (index < 0) return ;
		SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(index);
		
		if (new MessageBox("确定要将此商品成交金额改为0么?",null,true).verify() == GlobalVar.Key1)
		{
			sgd.lszre = 0;
			sgd.lszke = sgd.hjje - getZZK(sgd);
			getZZK(sgd);
			// 重算小票应收
			calcHeadYsje();
	//		 刷新商品列表
			saleEvent.updateTable(getSaleGoodsDisplay());
			// 显示汇总
			saleEvent.setTotalInfo();
			// 显示商品大字信息
			saleEvent.setCurGoodsBigInfo();
		}
	}
}
