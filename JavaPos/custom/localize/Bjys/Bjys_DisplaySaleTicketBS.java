package custom.localize.Bjys;

import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Struct.SaleGoodsDef;

public class Bjys_DisplaySaleTicketBS extends DisplaySaleTicketBS
{
	public String getInputBarCode(SaleGoodsDef sgd)
    {
    	String vbarcode = sgd.barcode;
    	   	
    	
    	return vbarcode;
    }
}
