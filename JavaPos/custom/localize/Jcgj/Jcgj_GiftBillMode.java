package custom.localize.Jcgj;

import com.efuture.javaPos.PrintTemplate.GiftBillMode;

public class Jcgj_GiftBillMode extends GiftBillMode
{
	public void PrintGiftBill()
	{
    	printStart();
    	printVector(getCollectDataString(Total,-1,Width));
	}
}
