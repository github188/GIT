package custom.localize.Zmsy;

import com.efuture.DeBugTools.PosLog;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;

import custom.localize.Zmjc.Zmjc_DisplaySaleTicketBS;

public class Zmsy_DisplaySaleTicketBS extends Zmjc_DisplaySaleTicketBS
{

	//从菜单重印小票时，要提示输入提货单号
	public void printSaleTicket_Msg(String strSyjh, long lngFphm, String djlb)
    {
		try
		{
			if (SellType.ISBACK(djlb)) return;
			
			Zmsy_SaleBS saleBS = (Zmsy_SaleBS)GlobalInfo.saleform.sale.saleBS;
			saleBS.inputTHD(strSyjh, lngFphm, djlb);
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		
    }
}
