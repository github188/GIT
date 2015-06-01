package custom.localize.Jdhx;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Jdhx.Jdhx_WaterFee.FeeHeadDef;

public class Jdhx_DisplaySaleTicketBS extends DisplaySaleTicketBS
{

	public void printSaleTicket(SaleHeadDef vsalehead, Vector vsalegoods, Vector vsalepay, boolean isRed)
	{
		try
		{
			if (SellType.isJF(vsalehead.djlb))
			{
				Jdhx_WaterFee.getDefault().head = new FeeHeadDef();
			    Jdhx_WaterFee.getDefault().head.yhh = vsalehead.str3 ;//vsalehead.str4.substring(0,5);
			    Jdhx_WaterFee.getDefault().head.xm = Convert.newSubString(vsalehead.str4, 18, 58);;
			    Jdhx_WaterFee.getDefault().head.jylb = vsalehead.str4.substring(4,6);
			    Jdhx_WaterFee.getDefault().head.qfzje = Convert.toDouble(vsalehead.str4.substring(100,110));
			    Jdhx_WaterFee.getDefault().head.qfbs = Convert.toInt(vsalehead.str4.substring(98,100));
				Jdhx_WaterFee.getDefault().writeWaterFeeBill(Jdhx_WaterFee.getDefault().head, vsalehead, vsalegoods, vsalepay);
				Jdhx_WaterFee.getDefault().printBill();
				return;
			}
			
			super.printSaleTicket(vsalehead, vsalegoods, vsalepay, isRed);	
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return ;
	}
}
