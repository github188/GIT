package com.efuture.javaPos.PrintTemplate;

import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Struct.SaleHeadDef;


public class CheckGoodsMode extends PrintTemplate
{
	protected static CheckGoodsMode checkGoodsMode = null;
	protected boolean isLoad =  false;
	
	protected SaleHeadDef salehead;
	protected Vector salegoods;
	
	protected final static int SBM_salefphm = 58;
	
	public static CheckGoodsMode getDefault()
    {
        if (CheckGoodsMode.checkGoodsMode == null)
        {
        	CheckGoodsMode.checkGoodsMode = CustomLocalize.getDefault().createCheckGoodsMode();
        }

        return CheckGoodsMode.checkGoodsMode;
    }
	
	public boolean ReadTemplateFile()
    {
		if (!CommonMethod.isFileExist(GlobalVar.ConfigPath + "//CheckGoodsMode.ini")) return true;
			
        super.InitTemplate();
        
        isLoad = super.ReadTemplateFile(Title,GlobalVar.ConfigPath + "//CheckGoodsMode.ini");
        
        return isLoad;
    }
	
	public void setTemplateObject(SaleHeadDef h, Vector s, Vector p)
	{
		salehead = h;
		salegoods = convertGoodsDetail(s);
		
		SaleBillMode.getDefault().setTemplateObject(h, s, p);
	}
	
	protected Vector convertGoodsDetail(Vector s)
	{
		return s;
	}
	
	public String getItemDataString(PrintTemplateItem item, int index)
    {
		String line = null;
		
		try
        {
			line = extendCase(item, index);
			
			if (line == null)
            {
				switch (Integer.parseInt(item.code))
				{
					case SBM_salefphm: // 盘点单号
						line = salehead.yfphm;
					break;	
					default:
						line = SaleBillMode.getDefault().getItemDataString(item, index);
				}
				
				
            }
			
			return line;
        }
		catch (Exception ex)
        {
        	ex.printStackTrace();
        	return null;
        }
    }
	
	public void setLoad(boolean isload)
	{
		this.isLoad = isload;
	}
	
	public boolean isLoad()
	{
		return isLoad;
	}
	
	public SaleHeadDef getSalehead()
	{
		return salehead;
	}

	public Vector getSalegoods()
	{
		return salegoods;
	}
	
	public void printBill()
    {
		// 设置打印方式
        printSetPage();

        // 打印头部区域
        printHeader();

        // 打印明细区域
        printDetail();
        
        // 打印尾部区域
        printBottom();
        
        // 打印付款区域
		printPay();
		
        // 打印汇总区域
        printTotal();

        // 切纸
        printCutPaper();
    }
	
	public void printDetail()
	{
		// 设置打印区域
		setPrintArea("Detail");

		// 循环打印商品明细
		for (int i = 0; i < salegoods.size(); i++)
		{
			printVector(getCollectDataString(Detail, i, Width));
		}
	}
}
