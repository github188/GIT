package custom.localize.Tygc;

import com.efuture.javaPos.PrintTemplate.GiftBillMode;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;

public class Tygc_GiftBillMode extends GiftBillMode
{
	protected final int GBM_barCode = 50; //条形码

	 protected String extendCase(PrintTemplateItem item, int index)
	 {
		String line = null;
		if(Integer.parseInt(item.code)==GBM_barCode)
		{
			char[] a = { 0x1D, 0x48, 0x01 };
			//char[] b = { 0x1D, 0x6B, 67 ,13 };
			char[] b = { 0x1D, 0x6B, 0x43 ,0x0D };
			//char[] b = { 0x1D, 0x6B, 0x01 };
			line =  String.valueOf(a)+String.valueOf(b) + gift.code;
			//line =  String.valueOf(a)+String.valueOf(b) + "2000000521060";
			
		}
		return line;
	 }
	 public void PrintGiftBill()
		{
		printStart();
		printVector(getCollectDataString(Total, -1, Width));
			//printCutPaper();
		}
}
