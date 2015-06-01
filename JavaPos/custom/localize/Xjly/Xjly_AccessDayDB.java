package custom.localize.Xjly;

import java.util.Vector;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Struct.SaleHeadDef;

public class Xjly_AccessDayDB extends AccessDayDB
{
	public boolean writeSale(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		saleHead.rqsj = ManipulateDateTime.getCurrentDate() + " " + ManipulateDateTime.getCurrentTime();
		return super.writeSale(saleHead, saleGoods, salePayment);
	}
}
