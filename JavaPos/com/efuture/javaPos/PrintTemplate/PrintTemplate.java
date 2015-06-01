package com.efuture.javaPos.PrintTemplate;

import java.io.BufferedReader;
import java.lang.reflect.Field;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ExpressionDeal;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.SaleHeadDef;


public class PrintTemplate
{
    protected String curLoc = null;
    
    protected String[] Title = {"General","PageHead","PageBottom","Header","Detail","Total","Pay","Bottom","Memo"};
    protected Vector PageHead = null; //分页打印 页头  for yebk 2014-11-7 
    protected Vector PageBottom = null; // 分页打印页尾
    protected Vector Header = null;
    protected Vector Detail = null;
    protected Vector Total = null;
    protected Vector Pay = null;
    protected Vector Bottom = null;
    protected Vector Memo = null;
    
    public int Width;
    
    protected int PagePrint = 0;
    protected int AreaPrint = 0;
    protected int PageHeadPrint = 0;//多页时是否从第二页开始打印页头
    protected int PageBottomPrint = 0;//多页时是否每页打印页尾
    public boolean PrintEnd = false; //多页打印时标记打印是否已开始打印 Bottom 内容，已开始打印Bottom内容，则不打印每页页尾内容
    
    protected int Area_PageHead  = 0;
    protected int Area_PageBottom  = 0;
    protected int Area_Header    = 0;                   
    protected int Area_Detail    = 0;                          
    protected int Area_Total     = 0;                           
    protected int Area_Pay       = 0;                           
    protected int Area_Bottom    = 0;                            
    protected int Area_PageFeet  = 0;
    
    // 用于计算临时打印项
    protected SaleHeadDef salehead_temp; //对应salehead
    protected Vector salegoods_temp;	// 对应salegoods
    protected Vector salepay_temp;    // 对应salepay
    protected Vector originalsalegoods_temp; // 对应org_salepay
    protected Vector originalsalepay_temp; //对应org_salegoods
    
    protected int printstrack = -1; // 输出栈


    protected void InitTemplate()
    {
    	PageHead = new Vector();
    	PageBottom = new Vector();
    	Header = new Vector();
    	Detail = new Vector();
    	Total  = new Vector();
    	Pay	   = new Vector();
    	Bottom = new Vector();
    	Memo = new Vector();
    }
    
    protected String extendCase(PrintTemplateItem item, int index)
    {
    	return null;
    }
    
    protected boolean ReadTemplateFile(String[] title, String pathName)
    {
        BufferedReader br = null;
        String line = null;

        try
        {
            br = CommonMethod.readFile(pathName);

            while ((line = br.readLine()) != null)
            {
                if (line.trim().length() <= 0)
                {
                    continue;
                }

                if (line.trim().charAt(0) == ';') //判断是否为备注
                {
                    continue;
                }

                //判断标记
                if ((line.trim().charAt(0) == '[') &&
                        (line.trim().charAt(line.trim().length() - 1) == ']'))
                {
                    String line1 = line.trim()
                                       .substring(1, line.trim().length() - 1);

                    for (int i = 0; i < title.length; i++)
                    {
                        if (line1.equals(title[i]))
                        {
                            curLoc = title[i];
                            i      = title.length;
                        }
                    }
                }
                else // 添加 ITEM
                {
                    if (curLoc == null) //未开启 '添加' 标志  或当前要添加的类型不明
                    {
                        continue;
                    }

                    int num = line.indexOf("=");

                    if ((num < 0) || (num >= (line.length() - 1))) //没有 '=' 或 在最后一格有等号
                    {
                        continue;
                    }

                    String code = line.substring(0, num);
                    String syntax = line.substring(num + 1);
                    String[] row = syntax.split(",");

                    // 添加模版项
                    PrintTemplateItem item = new PrintTemplateItem();

                    try
                    {
                        item.code   = code.trim();
                        item.rowno  = Integer.parseInt(row[0].trim());
                        if (row.length >= 2) item.colno  = Integer.parseInt(row[1].trim());
                        if (row.length >= 3) item.length = Integer.parseInt(row[2].trim());
                        if (row.length >= 4) item.alignment = Integer.parseInt(row[3].trim());
                        if (row.length >= 5) item.text = row[4];
                    }
                    catch (Exception er)
                    {
                        er.printStackTrace();

                        continue;
                    }

                    // 将已构建的ITEM添加到对应的集合
                    addTemplateeItem(item, curLoc);
                }
            }

            br.close();
            
            return true;
        }
        catch (Exception er)
        {
            er.printStackTrace();

            return false;
        }
    }

    protected boolean addTemplateeItem(PrintTemplateItem item, String curLoc)
    {
		if (curLoc.equalsIgnoreCase("General"))
		{   
			if (item.code.equalsIgnoreCase("Width")) Width = item.rowno;
			else if (item.code.equalsIgnoreCase("PagePrint")) PagePrint = item.rowno;
			else if (item.code.equalsIgnoreCase("AreaPrint")) AreaPrint = item.rowno;
			else if (item.code.equalsIgnoreCase("PageHeadPrint")) PageHeadPrint = item.rowno;
			else if (item.code.equalsIgnoreCase("PageBottomPrint")) PageBottomPrint = item.rowno;
			else if (item.code.equalsIgnoreCase("Area_PageHead")) Area_PageHead = item.rowno;
			else if (item.code.equalsIgnoreCase("Area_PageBottom")) Area_PageBottom = item.rowno;
			else if (item.code.equalsIgnoreCase("Area_Header")) Area_Header = item.rowno;
			else if (item.code.equalsIgnoreCase("Area_Detail")) Area_Detail = item.rowno;
			else if (item.code.equalsIgnoreCase("Area_Total")) Area_Total = item.rowno;
			else if (item.code.equalsIgnoreCase("Area_Pay")) Area_Pay = item.rowno;
			else if (item.code.equalsIgnoreCase("Area_Bottom")) Area_Bottom = item.rowno;
			else if (item.code.equalsIgnoreCase("Area_PageFeet")) Area_PageFeet = item.rowno;
			
			return true;
		}
		else if (curLoc.equalsIgnoreCase("PageHead"))
		{
			PageHead.add(item);
			
			return true;
		}
		else if (curLoc.equalsIgnoreCase("PageBottom"))
		{
			PageBottom.add(item);
			
			return true;
		}
		else if (curLoc.equalsIgnoreCase("Header"))
		{
			Header.add(item);
			
			return true;
		}
		else if (curLoc.equalsIgnoreCase("Detail"))
		{
			Detail.add(item);
			
			return true;
		}
		else if (curLoc.equalsIgnoreCase("Total"))
		{
			Total.add(item);
			
			return true;
		}
		else if (curLoc.equalsIgnoreCase("Pay"))
		{
			Pay.add(item);
			
			return true;
		}
		else if (curLoc.equalsIgnoreCase("Bottom"))
		{
			Bottom.add(item);
			
			return true;
		}
		else if (curLoc.equalsIgnoreCase("Memo"))
		{
			Memo.add(item);
			
			return true;
		}

		return false;
    }
    
    protected String getItemDataString(PrintTemplateItem item, int index)
	{
        String line = null;

        line = extendCase(item,index);
        if (line == null)
        {
	        switch (Integer.parseInt(item.code))
	        {
	        	default:
	        		line = extendCase(item,index);
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
	
    protected Vector getCollectDataString(Vector mode, int index,int maxLength)
    {
        try
        {
            if (mode != null && mode.size() >= 0 && maxLength > 0)
            {
                Vector v = new Vector();

                for (int i = 0; i < mode.size(); i++)
                {
                    PrintTemplateItem item = (PrintTemplateItem) mode.elementAt(i);
                    
                    if (item.rowno > v.size())
                    {
                        String newLine = "";
                        String addLine = getItemDataString(item, index);
                        if (addLine != null)
                        {
	                        v.add(Convert.appendStringSize(newLine, addLine,
	                                                       item.colno, item.length,
	                                                       maxLength,item.alignment));
                        }
                        else
                        {
                        	v.add(null);
                        }
                    }
                    else
                    {
                        String oldLine = (String) v.elementAt(item.rowno - 1);
                        String addLine = getItemDataString(item, index);
                        if (addLine != null)
                        {
                        	if (oldLine == null) oldLine = "";
                        	
	                        v.add(item.rowno - 1,
	                              Convert.appendStringSize(oldLine, addLine,
	                                                       item.colno, item.length,
	                                                       maxLength,item.alignment));
	                        v.removeElementAt(item.rowno);
                        }
                    }
                }

                return v;
            }

            return null;
        }
        catch (Exception er)
        {
            er.printStackTrace();

            return null;
        }
    }
    
	protected void setPrintArea(String area)
	{   
		int startRow,endRow;
		
		// 非套打，打印区域为整页
		if (AreaPrint != 1)
		{
			startRow = Area_PageHead + 1;
			endRow 	 = Area_Bottom;
		}
		else if (area.equalsIgnoreCase("Header"))
	    {
	    	startRow = Area_PageHead + 1;
	    	endRow 	 = Area_Header ;
	    }
	    else if (area.equalsIgnoreCase("Detail"))
	    {
	    	startRow = Area_Header + 1;
	    	endRow 	 = Area_Detail ;
	    }
	    else if (area.equalsIgnoreCase("Total"))
	    {
	    	startRow = Area_Detail + 1;
	    	endRow 	 = Area_Total ;
	    }
	    else if (area.equalsIgnoreCase("Pay"))
	    {
	    	startRow = Area_Total + 1;
	    	endRow 	 = Area_Pay ;
	    }
	    else if (area.equalsIgnoreCase("Bottom"))
	    {
	    	startRow = Area_Pay + 1;
	    	endRow 	 = Area_Bottom;
	    }/*
	    else if (area.equalsIgnoreCase("Memo"))
	    {
	    	startRow = Area_Pay + 1;
	    	endRow 	 = Area_Header;
	    }*/
	    else
	    {
			startRow = Area_PageHead + 1;
			endRow 	 = Area_Bottom;
	    }
		
		// 设置打印区域
		printArea(startRow,endRow);
	}
	
	protected void printVector(Vector v)
	{
	    if (v == null) return;
	    
        for (int i = 0; i < v.size(); i++)
        {
        	if (v.elementAt(i) == null) continue;
        	
        	printLine((Convert.rightTrim((String)v.elementAt(i))) + "\n");
        }
	}
	
	protected void printStart()
	{
		int RepPrintTrack = printstrack;
		if (RepPrintTrack == -1)
		{
			RepPrintTrack = ConfigClass.RepPrintTrack;
		}
		
		switch(RepPrintTrack)
		{
			case 1:
				Printer.getDefault().startPrint_Normal();
				break;
			case 2:
				Printer.getDefault().startPrint_Journal();
				break;
			case 3:
				Printer.getDefault().startPrint_Slip();
				break;
			default:
				Printer.getDefault().startPrint_Normal();
				break;
		}
	}
	
	protected void printLine(String s)
	{
		int RepPrintTrack = printstrack;
		if (RepPrintTrack == -1)
		{
			RepPrintTrack = ConfigClass.RepPrintTrack;
		}
		
		switch(RepPrintTrack)
		{
			case 1:
				Printer.getDefault().printLine_Normal(s);
				break;
			case 2:
				Printer.getDefault().printLine_Journal(s);
				break;
			case 3:
				Printer.getDefault().printLine_Slip(s);
				break;
			default:
				Printer.getDefault().printLine_Normal(s);
				break;
		}
	}	
	
	protected void printArea(int startRow,int endRow)
	{
		int RepPrintTrack = printstrack;
		if (RepPrintTrack == -1)
		{
			RepPrintTrack = ConfigClass.RepPrintTrack;
		}
		
		switch(RepPrintTrack)
		{
			case 1:
				Printer.getDefault().setPrintArea_Normal(startRow,endRow);
				break;
			case 2:
				Printer.getDefault().setPrintArea_Journal(startRow,endRow);
				break;
			case 3:
				Printer.getDefault().setPrintArea_Slip(startRow,endRow);
				break;
			default:
				Printer.getDefault().setPrintArea_Normal(startRow,endRow);
				break;	
		}
	}
	
	public void printCutPaper()
	{
		int RepPrintTrack = printstrack;
		if (RepPrintTrack == -1)
		{
			RepPrintTrack = ConfigClass.RepPrintTrack;
		}
		
		switch(RepPrintTrack)
		{
			case 1:
				Printer.getDefault().cutPaper_Normal();
				break;
			case 2:
				Printer.getDefault().cutPaper_Journal();
				break;
			case 3:
				Printer.getDefault().cutPaper_Slip();
				break;
			default:
				Printer.getDefault().cutPaper_Normal();
				break;
		}
	}
	
	public void printSetPage()
	{
		int RepPrintTrack = printstrack;
		if (RepPrintTrack == -1)
		{
			RepPrintTrack = ConfigClass.RepPrintTrack;
		}
		
		switch(RepPrintTrack)
		{
			case 1:
				// 设置是否分页打印
				if (PagePrint != 1)
				{
					Printer.getDefault().setPagePrint_Normal(false,1);
				}
				else
				{
					Printer.getDefault().setPagePrint_Normal(true,Area_PageFeet);
				}
				break;
			case 2:
				// 第二栈总是不分页的
				Printer.getDefault().setPagePrint_Journal(false,1);
/*				
				// 设置是否分页打印
				if (PagePrint != 1)
				{
					Printer.getDefault().setPagePrint_Journal(false,1);
				}
				else
				{
					Printer.getDefault().setPagePrint_Journal(true,Area_PageFeet);
				}
*/				
				break;
			case 3:
				// 第三栈总是不分页的
				Printer.getDefault().setPagePrint_Slip(false,1);
/*				
				// 设置是否分页打印
				if (PagePrint != 1)
				{
					Printer.getDefault().setPagePrint_Slip(false,1);
				}
				else
				{
					Printer.getDefault().setPagePrint_Slip(true,Area_PageFeet);
				}
*/				
				break;				
			default:
				// 设置是否分页打印
				if (PagePrint != 1)
				{
					Printer.getDefault().setPagePrint_Normal(false,1);
				}
				else
				{
					Printer.getDefault().setPagePrint_Normal(true,Area_PageFeet);
				}
				break;
		}
	}
	
	public void printBill()
	{
		// 设置打印方式
		printSetPage();
		
		// 打印头部区域
		printHeader();
		
        // 打印明细区域
		printDetail();
		
		// 打印汇总区域
		printTotal();
		
        // 打印付款区域
		printPay();
		
        // 打印尾部区域
		printBottom();

        // 切纸
        printCutPaper();
	}

	public void printPageHeader()
	{
		// 是否启用打印页头
		if (PageHeadPrint != 1)
			return;

		// 分页且套打时，不打印
		if (PagePrint == 1 && AreaPrint == 1)
			return;
		
		// 设置打印区域
		setPrintArea("PageHead");
		
		// 打印
		printVector(getCollectDataString(PageHead,-1,Width));
	}
	
	public void printPageBottom()
	{
		// 是否启用打印页尾
		if (PageBottomPrint != 1)
			return;

		// 分页且套打时，不打印
		if (PagePrint == 1 && AreaPrint == 1)
			return;
		
		// 设置打印区域
		setPrintArea("PageBottom");
		
		// 打印
		printVector(getCollectDataString(PageBottom,-1,Width));
	}
	
	public void printHeader()
	{
		// 设置打印区域
		setPrintArea("Header");
		
		// 打印
		printVector(getCollectDataString(Header,-1,Width));
	}
	
	public void printDetail()
	{
		// 设置打印区域
		setPrintArea("Detail");
		
		//
    	printVector(getCollectDataString(Detail,-1,Width));
	}

	public void printTotal()
	{
		// 设置打印区域
		setPrintArea("Total");
		
		//
        printVector(getCollectDataString(Total,-1,Width));
	}
	
	public void printPay()
	{
		// 设置打印区域
		setPrintArea("Pay");
		
		//
    	printVector(getCollectDataString(Pay,-1,Width));
	}
	
	public void printBottom()
	{
		// 设置打印区域
		setPrintArea("Bottom");
		
        printVector(getCollectDataString(Bottom,-1,Width));	


	}
	
	public String calString(String expressStr,int index)
	{
		String express = expressStr.substring(expressStr.indexOf("calc|")+5);
		return getExpressValue(express,index);
	}
	
	public static Object findObjectValue(Object obj,String objname,int index)
	{
		try
		{
			// 递归调用查找成员
			if (objname.indexOf(".") > 0)
			{
				Object curObj = obj;
				String[] ref = objname.split("\\.");
				for (int i = 0 ; i < ref.length; i++)
				{
					curObj = findObjectValue(curObj,ref[i],index);
					if (curObj == null) break;
				}				
				return curObj;
			}
			
			// 系统参数对象
			if (objname.equals("syspara"))
			{
				return GlobalInfo.sysPara;
			}
			
			Class c1 = obj.getClass();
			Field field1 = null;
			int num = -1;
			
			if (objname.indexOf("#") >= 0)
			{
				num = Convert.toInt(objname.substring(objname.indexOf("#")+1))-1;
				objname = objname.substring(0, objname.indexOf("#"));
			}
			
			//循环查找变量。getDeclaredField只能取到自身类的变量
			while (c1 != null)
			{
				try
				{
					field1 = c1.getDeclaredField(objname);
				}
				catch (NoSuchFieldException e)
				{
				}
				
				if (field1 == null) 
				{
					c1 = c1.getSuperclass();
				}
				else
				{
					break;
				}
				if (c1.getName().indexOf("Object") >= 0) break;
			}

			if (field1 == null) return null;
			
			Object curObj = field1.get(obj);
			
			//查找是否为VECTOR
			if (curObj != null && curObj.getClass().getName().indexOf("Vector") >= 0)
			{
				try
				{
					Vector vc = (Vector)curObj;
					if (num >= 0)
					{
						curObj = vc.elementAt(num);
					}
					else if (index >= 0)
					{
						curObj = vc.elementAt(index);
					}
					else
					{
						return null;
						
					}
				}catch(Exception er)
				{
					er.printStackTrace();
					curObj = null;
				}
			}
			
			return curObj;
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String getObjectValue(String objectExpress,int index)
	{
		if (objectExpress == null) return null;
		if (objectExpress.charAt(0) == ':') objectExpress = objectExpress.substring(1);
		
		// salehead.djlbs 为查询单据的正负号， 
		boolean sign = false;		
		if (objectExpress.indexOf(".djlbs") >=0)
		{
			objectExpress = objectExpress.substring(0,objectExpress.indexOf("djlbs"))+"djlb";
			sign = true;
		}
		
		String[] ref = objectExpress.split("\\.");
		Object curObj = this;
		for (int i = 0 ; i < ref.length; i++)
		{
			curObj = findObjectValue(curObj,ref[i],index);
			
			if (curObj == null) return null;
		}
		
		if (sign && curObj.toString().trim().length() > 0)
		{
			return String.valueOf(SellType.SELLSIGN(curObj.toString().trim()));
		}
		
		return curObj.toString();
		/**
		if ( (objectExpress.indexOf("salehead.")) != -1)
		{
			return CommonMethod.getObjectPara(salehead_temp, obj[1]);
		}
		else if ( (objectExpress.indexOf("salegoods.")) != -1)
		{
			return CommonMethod.getObjectPara(salegoods_temp.elementAt(index), obj[1]);
		}
		else if ( (objectExpress.indexOf("salepay.")) != -1)
		{
			return CommonMethod.getObjectPara(salepay_temp.elementAt(index), obj[1]);
		}
		else if ( (objectExpress.indexOf("org_salegoods.")) != -1)
		{
			return CommonMethod.getObjectPara(originalsalegoods_temp.elementAt(index), obj[1]);
		}
		else if ( (objectExpress.indexOf("org_salepay.")) != -1)
		{
			return CommonMethod.getObjectPara(originalsalepay_temp.elementAt(index), obj[1]);
		}
		*/
	}
	
	public String getExpressValue(String express,int index)
	{
		String strTemp = null;
		
		while (express.indexOf(":")!=-1)
		{ 
			strTemp=express.substring(express.indexOf(":"),express.length()); 
			
			int end = -1;
			for (int ii=1;ii<strTemp.length();ii++) 
			{
				if (!((strTemp.charAt(ii) >= '0' && strTemp.charAt(ii) <= '9') || (strTemp.charAt(ii) >= 'a' && strTemp.charAt(ii) <= 'z') || strTemp.charAt(ii) == '.' || strTemp.charAt(ii) == '#'))
				{
					end = ii;
					break;
				}
			}
			if (end >= 0) strTemp = strTemp.substring(0,end);

			String expObj = getObjectValue(strTemp,index);
			if (expObj == null ) return null;
			express=express.replaceAll(strTemp,expObj); 
		} 
		return ExpressionDeal.SpiltExpression(express);
	}
	
	
	
	public int getPagePrint()
	{
		return PagePrint;
	}
	
	public int getPageBottom()
	{
		return Area_PageBottom;
	}
	
	public int getDetail()
	{
		return Area_Detail;
	}
	
	public int getTotal()
	{
		return Area_Total;
	}
	
	public int getPageFeet()
	{
		return Area_PageFeet;
	}
}
