package com.efuture.javaPos.PrintTemplate;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Device.LineDisplay;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;


public class DisplayMode extends PrintTemplate
{
	protected static DisplayMode displayMode = null;
	
    private SaleHeadDef salehead;
    private Vector salegoods;
    protected final int DM_text = 0;
    protected final int DM_uid  = 1;
    protected final int DM_jg   = 2;
    protected final int DM_hjje = 3;
    protected final int DM_dj   = 4;
    protected final int DM_cjje = 5;
    protected final int DM_yfje = 6;
    protected final int DM_sjfk = 7;
    protected final int DM_zl   = 8;
    protected final int DM_ysje = 9;
    protected final int DM_goodsname = 10;
    
    public static DisplayMode getDefault()
    {
        if (DisplayMode.displayMode == null)
        {
        	DisplayMode.displayMode = CustomLocalize.getDefault().createDisplayMode();
        }

        return DisplayMode.displayMode;
    }
    
    public boolean ReadTemplateFile()
    {
        super.InitTemplate();
        
        return super.ReadTemplateFile(Title, GlobalVar.ConfigPath+"//DisplayMode.ini");
    }
    
    public void setTemplateObject(SaleHeadDef h,Vector s)
    {
    	salehead = h;
    	salegoods = s;
    }
    
	public String getItemDataString(PrintTemplateItem item, int index)
	{
        String line = null;

        line = extendCase(item,index);
        if (line == null)
        {
	        switch (Integer.parseInt(item.code))
	        {
	            case DM_text: //文本
					if (item.text == null)
					{
						line = "";
					}
					else
					{
						if (item.text.trim().indexOf("calc|") == 0)
						{
							line = super.calString(item.text, index);
						}
						else
						{
							line = item.text;
						}
					}
					break;
	
	            case DM_uid: //品名规格
	                line = ((SaleGoodsDef) salegoods.elementAt(index)).uid;
	
	                break;
	
	            case DM_jg: //售价
	                line = ManipulatePrecision.doubleToString(((SaleGoodsDef) salegoods.elementAt(index)).jg);
	
	                break;
	
	            case DM_hjje: //售价金额
	                line = ManipulatePrecision.doubleToString(((SaleGoodsDef) salegoods.elementAt(index)).hjje);
	
	                break;
	
	            case DM_dj: //成交单价
	                line = ManipulatePrecision.doubleToString(ManipulatePrecision.div((((SaleGoodsDef) salegoods.elementAt(index)).hjje -
	                                                              ((SaleGoodsDef) salegoods.elementAt(index)).hjzk),
	                                                              ((SaleGoodsDef) salegoods.elementAt(index)).sl));
	
	                break;
	
	            case DM_cjje: //成交金额
	                line = ManipulatePrecision.doubleToString(((SaleGoodsDef) salegoods.elementAt(index)).hjje -
	                                      ((SaleGoodsDef) salegoods.elementAt(index)).hjzk);
	
	                break;
	
	            case DM_yfje: //应付金额
	                line = ManipulatePrecision.doubleToString(salehead.ysje + salehead.sswr_sysy);
	
	                break;
	
	            case DM_sjfk: //实收金额
	                line = ManipulatePrecision.doubleToString(salehead.sjfk);
	
	                break;
	
	            case DM_zl: //找零金额
	                line = ManipulatePrecision.doubleToString(salehead.zl);
	
	                break;
	
	            case DM_ysje: //应收金额
	                line = ManipulatePrecision.doubleToString(salehead.ysje);
	
	                break;    
	            case DM_goodsname: //商品名称
	            	line = ((SaleGoodsDef) salegoods.elementAt(index)).name;
	            	break;
	            default:
	            	line = extendCase(item,index);
	            	break;
	        }
        }
        
        if (line != null && Integer.parseInt(item.code) != 0 && item.text != null && !item.text.trim().equals(""))
        {
            //line = item.text + line;
        	int maxline = item.length - Convert.countLength(item.text);
        	line = item.text + Convert.appendStringSize("",line,0,maxline,maxline,item.alignment);
        }

        return line;
	}
	
    public void lineDisplayWelcome()
    {
    	dislayVector(getCollectDataString(Header,-1,Width));
    }

    public void lineDisplayGoods()
    {
        if (salegoods.size() > 0)
        {
        	dislayVector(getCollectDataString(Detail,salegoods.size() - 1,Width));
        }
        else
        {
        	lineDisplayWelcome();
        }
    }
    
    public void lineDisplayTotal()
    {
    	dislayVector(getCollectDataString(Total,-1,Width));
    }
    
    public void lineDisplayPay()
    {
    	dislayVector(getCollectDataString(Pay,-1,Width));
    }

    public void lineDisplayChange()
    {
    	dislayVector(getCollectDataString(Bottom,-1,Width));
    }
    
	protected void dislayVector(Vector v)
	{
	    if (v == null) return;
	    
	    LineDisplay.getDefault().clearText();
	    
        for (int i = 0; i < v.size(); i++)
        {
        	LineDisplay.getDefault().displayAt(i, 0, (String) v.elementAt(i));
        }
	}	
}
