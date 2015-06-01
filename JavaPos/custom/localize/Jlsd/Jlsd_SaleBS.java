package custom.localize.Jlsd;


import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.SaleGoodsDef;

import custom.localize.Bhcm.Bhcm_SaleBS;

public class Jlsd_SaleBS extends  Bhcm_SaleBS {
	
	// 
	public void execCustomKey9(boolean keydownonsale)
	{
		if (!SellType.ISSALE(saleHead.djlb) || saleGoods == null | saleGoods.size() < 1)	return;
		int index = saleEvent.table.getSelectionIndex();
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		new Jlsd_ShoppingBagForm().open(saleGoodsDef);
	}
	
	
}
