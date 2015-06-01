package custom.localize.Bjcx;

import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.PrintTemplate.GiftBillMode;

public class Bjcx_GiftBillMode extends GiftBillMode
{
	
	 protected void printLine(String s)
		{
		 Printer.getDefault().printLine_Normal(s);
		}
	 
	 public void printCutPaper()
		{
		 Printer.getDefault().cutPaper_Normal();
		}
	 	
}
