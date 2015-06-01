package custom.localize.Dxyc;


import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.SellType;

import custom.localize.Bstd.Bstd_SaleBS;

public class Dxyc_SaleBS extends Bstd_SaleBS
{

	
	public boolean writeHangGrant(){
//		 盘点和买卷交易不实时打印
		if (!SellType.ISCHECKINPUT(saletype))
		{
				for (int i = 0 ; i < saleGoods.size(); i++)
				{
					realTimePrintGoods(null,i);
				}
				Dxyc_SaleBillMode.gdorqx =1;
	}
		return super.writeHangGrant();
	}
	
	public boolean clearSell(int index){
//		 盘点和买卷交易不实时打印
		if (!SellType.ISCHECKINPUT(saletype))
		{
				for (int i = 0 ; i < saleGoods.size(); i++)
				{
					realTimePrintGoods(null,i);
				}
				Dxyc_SaleBillMode.gdorqx =2;
	}
		return super.clearSell(index);
	}

	public void backSell(){
//		 盘点和买卷交易不实时打印
		if (!SellType.ISCHECKINPUT(saletype))
		{
				for (int i = 0 ; i < saleGoods.size(); i++)
				{
					realTimePrintGoods(null,i);
				}

					if(saleGoods != null){
//						SaleBillMode.getDefault().printRealTimeCancel();
						Printer.getDefault().cutPaper_Journal();
					}
				Dxyc_SaleBillMode.gdorqx =0;
	}
		super.backSell();
	}
	
}
