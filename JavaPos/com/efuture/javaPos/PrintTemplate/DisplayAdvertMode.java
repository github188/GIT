package com.efuture.javaPos.PrintTemplate;

import java.util.Vector;

import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Struct.SaleHeadDef;


public class DisplayAdvertMode extends PrintTemplate
{
	protected static DisplayAdvertMode displayMode = null;
	
    public static DisplayAdvertMode getDefault()
    {
        if (DisplayAdvertMode.displayMode == null)
        {
        	DisplayAdvertMode.displayMode = CustomLocalize.getDefault().createDisplayAdvertMode();
        }

        return DisplayAdvertMode.displayMode;
    }
    
    public boolean ReadTemplateFile()
    {
        super.InitTemplate();
        
        return super.ReadTemplateFile(Title, GlobalVar.ConfigPath+"//DisplayAdvertMode.ini");
    }
    
    public void setTemplateObject(SaleHeadDef h,Vector s)
    {
    }
    
	public String getItemDataString(PrintTemplateItem item, int index)
	{
		return SaleBillMode.getDefault().getItemDataString(item, index);
	}
	
    public String lineDisplayWelcome()
    {
    	return dislayVector(getCollectDataString(Header,-1,Width));
    }

    public String lineDisplayGoods(int index)
    {
    	return dislayVector(getCollectDataString(Detail,index,Width));
    }
    
    public String lineDisplayTotal()
    {
    	return dislayVector(getCollectDataString(Total,-1,Width));
    }
    
    public String lineDisplayPay()
    {
    	return dislayVector(getCollectDataString(Pay,-1,Width));
    }

    public String lineDisplayChange()
    {
    	return dislayVector(getCollectDataString(Bottom,-1,Width));
    }
    
	protected String dislayVector(Vector v)
	{
	    if (v == null) return "";
	    
	    String line = ""; 
        for (int i = 0; i < v.size(); i++)
        {
        	if (v.elementAt(i) == null) continue;
        	
        	line += ((String)v.elementAt(i)).trim() + "\n";
        }

        // 去掉尾部换行
        if (line.length() > 0 && line.charAt(line.length()-1) == '\n') line = line.substring(0,line.length()-1);
        return line;
	}	
}
