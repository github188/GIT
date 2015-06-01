package custom.localize.Nnmk;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.PrintTemplate.GiftBillMode;

public class Nnmk_GiftBillMode extends GiftBillMode
{
	protected void printVector(Vector v)
	{
	    if (v == null) return;
	    
        for (int i = 0; i < v.size(); i++)
        {
        	if (v.elementAt(i) == null) continue;
        	
        	printLine((Convert.rightTrim((String)v.elementAt(i))) + "\n");
        	System.out.println(i+"          "+(Convert.rightTrim((String)v.elementAt(i))));
        }
	}
	
	protected void printStart()
	{
		if (gift.type.equals("90") || gift.type.equals("88") || gift.type.equals("3"))
			Printer.getDefault().startPrint_Normal();
		else
			Printer.getDefault().startPrint_Journal();
	}
	
	
    public void PrintGiftBill()
    {
    	printStart();
    	if (gift.type.equals("89"))
    	{
    		for (int i = 0 ; i <  2; i++)
    		{
    			
    			if (i == 0) printLine(Convert.appendStringSize("", "收银员联",0, Width,Width,2));
    			if (i == 1) printLine(Convert.appendStringSize("", "顾客联",0, Width,Width,2));
    			printVector(getCollectDataString(Total,-1,Width));
    		}
    	}
    	else
    	{
    		//super.PrintGiftBill();
    		printVector(getCollectDataString(Total,-1,Width));
    	}
    	
    	
    	
    	
    	// 检查有没有附加信息
    	String name = GlobalVar.ConfigPath+"//GiftAppendInfo.ini";
    	if (new File(name).exists())
    	{
    		String line = null;
    		boolean start = false;
    		BufferedReader br = CommonMethod.readFile(name);
    		try
			{
				while((line = br.readLine()) != null)
				{
					if (line.trim().length() <= 0) continue;
					
					if (line.trim().charAt(0) == ';') continue;
					
					if (line.trim().charAt(0)=='[' && line.trim().indexOf(gift.info) >=0)
					{
						start = true;
						continue;
					}
					
					if (start && line.trim().charAt(0) == '[')
					{
						start = false;
						break;
					}
					
					if (start)
					{
						printLine(line);
						System.out.println("append   "+line);
					}
				}
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
	protected void printLine(String s)
	{
		if (gift.type.equals("90") || gift.type.equals("88") || gift.type.equals("3"))
			Printer.getDefault().printLine_Normal(s);
		else
			Printer.getDefault().printLine_Journal(s);
	}
	
	public void printCutPaper()
	{
		if (gift.type.equals("90") || gift.type.equals("88") || gift.type.equals("3"))
			Printer.getDefault().cutPaper_Normal();
		else
			Printer.getDefault().cutPaper_Journal();
	}

}
