package custom.localize.Zmjc;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.PrintTemplate.HangBillMode;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

public class Zmjc_HangBillMode extends HangBillMode
{

	public void printBill(int num,double ysje)
	{
		//	
		printSetPage();
		
		//
		printLine("\n\n");
		printLine(Language.apply("挂单收条\n\n"));
		printLine(Language.apply("挂 单 号:  ")+ num +"\n");
		printLine(Language.apply("挂单金额:  ")+ManipulatePrecision.doubleToString(ysje) + " 元" +"\n");
		printLine(Language.apply("挂单时间:  ")+ManipulateDateTime.getCurrentDateTime()+"\n");    	
		printLine(Language.apply("收银机号:  ")+ConfigClass.CashRegisterCode+"\n");
		printLine(Language.apply("收银员号:  ")+GlobalInfo.posLogin.gh+"\n");
		
		printLine("\n");//中免空行不够
		printLine("\n");
		printLine("\n");
		printLine("\n");
		printLine("\n");
		printLine("\n");
		
		if (GlobalInfo.sysPara.isPrintGd.trim().charAt(0) == 'A')
		{
			// 打印头部区域
			SaleBillMode.getDefault().printHeader();		
			// 打印挂单明细		
		    SaleBillMode.getDefault().printDetail();
		}

		//
		printCutPaper();
	}
	
}
