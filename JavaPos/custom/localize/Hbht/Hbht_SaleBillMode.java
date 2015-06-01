package custom.localize.Hbht;

import java.util.Vector;

import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Cmls.Cmls_SaleBillMode;

public class Hbht_SaleBillMode extends Cmls_SaleBillMode
{

	public void setTemplateObject(SaleHeadDef h, Vector s, Vector p)
	{
		super.setTemplateObject(h, s, p);
		
		//if (SellType.ISSALE(h.djlb))
			Hbht_LadingBillMode.getDefault().setTemplateObject(h, s, p);
	}

	public void printAppendBill()
	{
		super.printAppendBill();

		Hbht_LadingBillMode.getDefault().printBill();
	}

}
