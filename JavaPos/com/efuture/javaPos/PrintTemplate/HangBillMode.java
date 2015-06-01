package com.efuture.javaPos.PrintTemplate;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;

public class HangBillMode extends PrintTemplate
{
	protected static HangBillMode hangBillMode = null;
	
    public static HangBillMode getDefault()
    {
        if (HangBillMode.hangBillMode == null)
        {
        	HangBillMode.hangBillMode = CustomLocalize.getDefault().createHangBillMode();
        }

        return HangBillMode.hangBillMode;
    }
    
    public boolean ReadTemplateFile()
    {
    	return true;
    }
    
	public void printBill(int num,double ysje)
	{
		//	
		printSetPage();
		
		//
		printLine("\n\n");
		printLine(Language.apply("挂单收条\n\n"));
		printLine(Language.apply("挂 单 号") +":  "+ num +"\n");
		printLine(Language.apply("挂单金额") +":  "+ManipulatePrecision.doubleToString(ysje) + Language.apply(" 元") +"\n");
		printLine(Language.apply("挂单时间") +":  "+ManipulateDateTime.getCurrentDateTime()+"\n");    	
		printLine(Language.apply("收银机号") +":  "+ConfigClass.CashRegisterCode+"\n");
		printLine(Language.apply("收银员号") +":  "+GlobalInfo.posLogin.gh+"\n");
		
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
	
	protected void printLine(String s)
	{
		if (printstrack != -1)
		{
			super.printLine(s);
		}
		else
		{
			/*if ((SellType.ISBACK(salehead.djlb) || SellType.ISHC(salehead.djlb)) && (GlobalInfo.sysPara.printInBill != 'Y'))
			{
				super.printLine(s);
			}
			else
			{*/
				Printer.getDefault().printLine_Normal(s);
			//}
		}
	}
	
	public void printCutPaper()
	{
		if (printstrack != -1)
		{
			super.printCutPaper();
		}
		else
		{
			/*if ((SellType.ISBACK(salehead.djlb) || SellType.ISHC(salehead.djlb)) && (GlobalInfo.sysPara.printInBill != 'Y'))
			{
				super.printCutPaper();
			}
			else
			{*/
				Printer.getDefault().cutPaper_Normal();
			//}
		}
	}
}
