package custom.localize.Bjcx;

import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.PrintTemplate.HangBillMode;

public class Bjcx_HangBillMode extends HangBillMode
{
	public void printCutPaper()
	{
		/*if (printstrack != -1)
		{
			super.printCutPaper();
		}
		else
		{
			if ((SellType.ISBACK(salehead.djlb) || SellType.ISHC(salehead.djlb)) && (GlobalInfo.sysPara.printInBill != 'Y'))
			{
				super.printCutPaper();
			}
			else
			{
				Printer.getDefault().cutPaper_Normal();
			//}
		}*/
		Printer.getDefault().cutPaper_Normal();
	}
}
