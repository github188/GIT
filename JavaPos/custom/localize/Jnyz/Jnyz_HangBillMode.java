package custom.localize.Jnyz;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.PrintTemplate.HangBillMode;

public class Jnyz_HangBillMode extends HangBillMode
{

	public void printBill(int num,double ysje)
	{
		//	
//		printSetPage();
		
		//
		printLine("\n\n");
		printLine("挂单收条\n\n");
		printLine("挂 单 号:  "+ num +"\n");
		printLine("挂单金额:  "+ManipulatePrecision.doubleToString(ysje) + " 元" +"\n");
		printLine("挂单时间:  "+ManipulateDateTime.getCurrentDateTime()+"\n");    	
		printLine("收银机号:  "+ConfigClass.CashRegisterCode+"\n");
		printLine("收银员号:  "+GlobalInfo.posLogin.gh+"\n");
		
		printLine("\n");
		printLine("\n");
		
//		if (GlobalInfo.sysPara.isPrintGd.trim().charAt(0) == 'A')
//		{
//			// 打印头部区域
//			SaleBillMode.getDefault().printHeader();		
//			// 打印挂单明细		
//		    SaleBillMode.getDefault().printDetail();
//		}

		//
		Printer.getDefault().cutPaper_Journal();
	}
	
	protected void printLine(String s)
	{
		/*if (printstrack != -1)
		{
			super.printLine(s);
		}
		else
		{
			if ((SellType.ISBACK(salehead.djlb) || SellType.ISHC(salehead.djlb)) && (GlobalInfo.sysPara.printInBill != 'Y'))
			{
				super.printLine(s);
			}
			else
			{*/
		//写死打在第二打印机上
				Printer.getDefault().printLine_Journal(s);
			//}
//		}
	}
	
}
