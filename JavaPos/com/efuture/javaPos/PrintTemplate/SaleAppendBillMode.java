package com.efuture.javaPos.PrintTemplate;

import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Struct.SaleAppendDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

public class SaleAppendBillMode extends SaleBillMode
{
	protected final static int SAM_Head_str1 = 102;
	protected final static int SAM_Head_str2 = 103;
	protected final static int SAM_Head_str3 = 104;
	protected final static int SAM_Head_str4 = 105;
	protected final static int SAM_Head_str5 = 106;
	protected final static int SAM_Head_str6 = 107;
	protected final static int SAM_Head_str7 = 108;
	protected final static int SAM_Head_str8 = 109;
	protected final static int SAM_Head_str9 = 110;
	protected final static int SAM_Head_str10 = 111;
	protected final static int SAM_Head_str11 = 112;
	protected final static int SAM_Head_str12 = 113;
	protected final static int SAM_Head_str13 = 114;
	protected final static int SAM_Head_str14 = 115;
	protected final static int SAM_Head_str15 = 116;
	protected final static int SAM_Head_str16 = 117;
	protected final static int SAM_Head_str17 = 118;
	protected final static int SAM_Head_str18 = 119;
	protected final static int SAM_Head_str19 = 120;
	protected final static int SAM_Head_str20 = 121;
	
	protected final static int SAM_Goods_str1 = 122;
	protected final static int SAM_Goods_str2 = 123;
	protected final static int SAM_Goods_str3 = 124;
	protected final static int SAM_Goods_str4 = 125;
	protected final static int SAM_Goods_str5 = 126;
	protected final static int SAM_Goods_str6 = 127;
	protected final static int SAM_Goods_str7 = 128;
	protected final static int SAM_Goods_str8 = 129;
	protected final static int SAM_Goods_str9 = 130;
	protected final static int SAM_Goods_str10 = 131;
	protected final static int SAM_Goods_str11 = 132;
	protected final static int SAM_Goods_str12 = 133;
	protected final static int SAM_Goods_str13 = 134;
	protected final static int SAM_Goods_str14 = 135;
	protected final static int SAM_Goods_str15 = 136;
	protected final static int SAM_Goods_str16 = 137;
	protected final static int SAM_Goods_str17 = 138;
	protected final static int SAM_Goods_str18 = 139;
	protected final static int SAM_Goods_str19 = 140;
	protected final static int SAM_Goods_str20 = 141;
	
	// 如果字段是code-value方式存储的化,以下模版项只打印code项
	protected final static int SAM_Head_str1code = 142;
	protected final static int SAM_Head_str2code = 143;
	protected final static int SAM_Head_str3code = 144;
	protected final static int SAM_Head_str4code = 145;
	protected final static int SAM_Head_str5code = 146;
	protected final static int SAM_Head_str6code = 147;
	protected final static int SAM_Head_str7code = 148;
	protected final static int SAM_Head_str8code = 149;
	protected final static int SAM_Head_str9code = 150;
	protected final static int SAM_Head_str10code = 151;
	protected final static int SAM_Head_str11code = 152;
	protected final static int SAM_Head_str12code = 153;
	protected final static int SAM_Head_str13code = 154;
	protected final static int SAM_Head_str14code = 155;
	protected final static int SAM_Head_str15code = 156;
	protected final static int SAM_Head_str16code = 157;
	protected final static int SAM_Head_str17code = 158;
	protected final static int SAM_Head_str18code = 159;
	protected final static int SAM_Head_str19code = 160;
	protected final static int SAM_Head_str20code = 161;
	
	protected final static int SAM_Goods_str1code = 162;
	protected final static int SAM_Goods_str2code = 163;
	protected final static int SAM_Goods_str3code = 164;
	protected final static int SAM_Goods_str4code = 165;
	protected final static int SAM_Goods_str5code = 166;
	protected final static int SAM_Goods_str6code = 167;
	protected final static int SAM_Goods_str7code = 168;
	protected final static int SAM_Goods_str8code = 169;
	protected final static int SAM_Goods_str9code = 170;
	protected final static int SAM_Goods_str10code = 171;
	protected final static int SAM_Goods_str11code = 172;
	protected final static int SAM_Goods_str12code = 173;
	protected final static int SAM_Goods_str13code = 174;
	protected final static int SAM_Goods_str14code = 175;
	protected final static int SAM_Goods_str15code = 176;
	protected final static int SAM_Goods_str16code = 177;
	protected final static int SAM_Goods_str17code = 178;
	protected final static int SAM_Goods_str18code = 179;
	protected final static int SAM_Goods_str19code = 180;
	protected final static int SAM_Goods_str20code = 181;
	
	// 如果字段是code-value方式存储的化,以下模版项只打印value项
	protected final static int SAM_Head_str1value = 182;
	protected final static int SAM_Head_str2value = 183;
	protected final static int SAM_Head_str3value = 184;
	protected final static int SAM_Head_str4value = 185;
	protected final static int SAM_Head_str5value = 186;
	protected final static int SAM_Head_str6value = 187;
	protected final static int SAM_Head_str7value = 188;
	protected final static int SAM_Head_str8value = 189;
	protected final static int SAM_Head_str9value = 190;
	protected final static int SAM_Head_str10value = 191;
	protected final static int SAM_Head_str11value = 192;
	protected final static int SAM_Head_str12value = 193;
	protected final static int SAM_Head_str13value = 194;
	protected final static int SAM_Head_str14value = 195;
	protected final static int SAM_Head_str15value = 196;
	protected final static int SAM_Head_str16value = 197;
	protected final static int SAM_Head_str17value = 198;
	protected final static int SAM_Head_str18value = 199;
	protected final static int SAM_Head_str19value = 200;
	protected final static int SAM_Head_str20value = 201;
	
	protected final static int SAM_Goods_str1value = 202;
	protected final static int SAM_Goods_str2value = 203;
	protected final static int SAM_Goods_str3value = 204;
	protected final static int SAM_Goods_str4value = 205;
	protected final static int SAM_Goods_str5value = 206;
	protected final static int SAM_Goods_str6value = 207;
	protected final static int SAM_Goods_str7value = 208;
	protected final static int SAM_Goods_str8value = 209;
	protected final static int SAM_Goods_str9value = 210;
	protected final static int SAM_Goods_str10value = 211;
	protected final static int SAM_Goods_str11value = 212;
	protected final static int SAM_Goods_str12value = 213;
	protected final static int SAM_Goods_str13value = 214;
	protected final static int SAM_Goods_str14value = 215;
	protected final static int SAM_Goods_str15value = 216;
	protected final static int SAM_Goods_str16value = 217;
	protected final static int SAM_Goods_str17value = 218;
	protected final static int SAM_Goods_str18value = 219;
	protected final static int SAM_Goods_str19value = 220;
	protected final static int SAM_Goods_str20value = 221;
	
	protected Vector saleappend;
	protected Vector originalsaleappend;
	protected SaleAppendDef saleappendhead;  
	
	protected static SaleAppendBillMode saleAppendBillMode = null;
	
	public static SaleBillMode getDefault()
	{
		if (SaleAppendBillMode.saleAppendBillMode == null)
		{
			SaleAppendBillMode.saleAppendBillMode = CustomLocalize.getDefault().createSaleAppendBillMode();
		}

		return SaleAppendBillMode.saleAppendBillMode;
	}
	
	public boolean ReadTemplateFile()
    {
		if (!CommonMethod.isFileExist(GlobalVar.ConfigPath + "//SaleAppendBillMode.ini")) return true;
			
        super.InitTemplate();
        
        return super.ReadTemplateFile(Title,GlobalVar.ConfigPath + "//SaleAppendBillMode.ini");
    }
	
	public void setTemplateObject(SaleHeadDef h, Vector s, Vector p, Vector sa )
	{
		super.setTemplateObject(h, s, p);
		
		for (int i = 0;i < sa.size();i++)
		{
			SaleAppendDef sad = (SaleAppendDef)sa.elementAt(i);
			
			if (sad.rowno < 0) 
			{
				saleappendhead = sad;
				continue;
			}
			
			int j = 0;
			for (j = 0;j < s.size();j++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef)s.elementAt(i);
				if (sgd.rowno == sad.rowno)
				{
					saleappend.add(sgd);
					continue;
				}
			}
			
			if (j >= s.size())
			{
				saleappend.add(null);
			}
		}
		
		originalsaleappend = sa;
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
	
	protected String extendCase(PrintTemplateItem item, int index)
    {
        String line = null;
        
        try
        {           	
        	if ((Integer.parseInt(item.code) >= SAM_Head_str1 && Integer.parseInt(item.code) <= SAM_Head_str20) ||
        		(Integer.parseInt(item.code) >= SAM_Head_str1code && Integer.parseInt(item.code) <= SAM_Head_str20code)||
        		(Integer.parseInt(item.code) >= SAM_Head_str1value && Integer.parseInt(item.code) <= SAM_Head_str20value)
        		)
        		
        	{
        		if (this.saleappendhead == null) return "";
        	}
        	
        	if ((Integer.parseInt(item.code) >= SAM_Goods_str1 && Integer.parseInt(item.code) <= SAM_Goods_str20)||
        		(Integer.parseInt(item.code) >= SAM_Goods_str1code && Integer.parseInt(item.code) <= SAM_Goods_str20code)||
        		(Integer.parseInt(item.code) >= SAM_Goods_str1value && Integer.parseInt(item.code) <= SAM_Goods_str20value)
        		)
        	{
        		if (this.saleappend.elementAt(index) == null) return "";
        	}
        		
        	int itemindex = Integer.parseInt(item.code);
        	
        	switch (itemindex)
            {
        	
    			case SAM_Head_str1:
    			case SAM_Head_str1code:
    			case SAM_Head_str1value:
	    			line = saleappendhead.str1;
	    			break;
    			case SAM_Head_str2:
    			case SAM_Head_str2code:
    			case SAM_Head_str2value:
	    			line = saleappendhead.str2;
	    			break;
    			case SAM_Head_str3:
    			case SAM_Head_str3code:
    			case SAM_Head_str3value:
	    			line = saleappendhead.str3;
	    			break;
    			case SAM_Head_str4:
    			case SAM_Head_str4code:
    			case SAM_Head_str4value:
	    			line = saleappendhead.str4;
	    			break;
    			case SAM_Head_str5:
    			case SAM_Head_str5code:
    			case SAM_Head_str5value:
	    			line = saleappendhead.str5;
	    			break;
    			case SAM_Head_str6:
    			case SAM_Head_str6code:
    			case SAM_Head_str6value:
	    			line = saleappendhead.str6;
	    			break;
    			case SAM_Head_str7:
    			case SAM_Head_str7code:
    			case SAM_Head_str7value:
	    			line = saleappendhead.str7;
	    			break;
    			case SAM_Head_str8:
    			case SAM_Head_str8code:
    			case SAM_Head_str8value:
	    			line = saleappendhead.str8;
	    			break;
    			case SAM_Head_str9:
    			case SAM_Head_str9code:
    			case SAM_Head_str9value:
	    			line = saleappendhead.str9;
	    			break;
    			case SAM_Head_str10:
    			case SAM_Head_str10code:
    			case SAM_Head_str10value:
	    			line = saleappendhead.str10;
	    			break;
    			case SAM_Head_str11:
    			case SAM_Head_str11code:
    			case SAM_Head_str11value:
	    			line = saleappendhead.str11;
	    			break;
    			case SAM_Head_str12:
    			case SAM_Head_str12code:
    			case SAM_Head_str12value:
	    			line = saleappendhead.str12;
	    			break;
    			case SAM_Head_str13:
    			case SAM_Head_str13code:
    			case SAM_Head_str13value:
	    			line = saleappendhead.str13;
	    			break;
    			case SAM_Head_str14:
    			case SAM_Head_str14code:
    			case SAM_Head_str14value:
	    			line = saleappendhead.str14;
	    			break;
    			case SAM_Head_str15:
    			case SAM_Head_str15code:
    			case SAM_Head_str15value:
	    			line = saleappendhead.str15;
	    			break;
    			case SAM_Head_str16:
    			case SAM_Head_str16code:
    			case SAM_Head_str16value:
	    			line = saleappendhead.str16;
	    			break;
    			case SAM_Head_str17:
    			case SAM_Head_str17code:
    			case SAM_Head_str17value:
	    			line = saleappendhead.str17;
	    			break;
    			case SAM_Head_str18:
    			case SAM_Head_str18code:
    			case SAM_Head_str18value:
	    			line = saleappendhead.str18;
	    			break;
    			case SAM_Head_str19:
    			case SAM_Head_str19code:
    			case SAM_Head_str19value:
	    			line = saleappendhead.str19;
	    			break;
    			case SAM_Head_str20:
    			case SAM_Head_str20code:
    			case SAM_Head_str20value:
	    			line = saleappendhead.str20;
	    			break;			
        		case SAM_Goods_str1:
        		case SAM_Goods_str1code:
        		case SAM_Goods_str1value:
        			line = ((SaleAppendDef)saleappend.get(index)).str1;
        			break;
        		case SAM_Goods_str2:
        		case SAM_Goods_str2code:
        		case SAM_Goods_str2value:
        			line = ((SaleAppendDef)saleappend.get(index)).str2;
        			break;
        		case SAM_Goods_str3:
        		case SAM_Goods_str3code:
        		case SAM_Goods_str3value:
        			line = ((SaleAppendDef)saleappend.get(index)).str3;
        			break;
        		case SAM_Goods_str4:
        		case SAM_Goods_str4code:
        		case SAM_Goods_str4value:
        			line = ((SaleAppendDef)saleappend.get(index)).str4;
        			break;
        		case SAM_Goods_str5:
        		case SAM_Goods_str5code:
        		case SAM_Goods_str5value:
        			line = ((SaleAppendDef)saleappend.get(index)).str5;
        			break;
            	case SAM_Goods_str6:
            	case SAM_Goods_str6code:
            	case SAM_Goods_str6value:
            		line = ((SaleAppendDef)saleappend.get(index)).str6;
                	break;
            	case SAM_Goods_str7:
            	case SAM_Goods_str7code:
            	case SAM_Goods_str7value:
            		line = ((SaleAppendDef)saleappend.get(index)).str7;
            		break;
            	case SAM_Goods_str8:
            	case SAM_Goods_str8code:
            	case SAM_Goods_str8value:
            		line = ((SaleAppendDef)saleappend.get(index)).str8;
            		break;
            	case SAM_Goods_str9:
            	case SAM_Goods_str9code:
            	case SAM_Goods_str9value:
            		line = ((SaleAppendDef)saleappend.get(index)).str9;
            		break;
            	case SAM_Goods_str10:
            		line = ((SaleAppendDef)saleappend.get(index)).str10;
            		break;
            	case SAM_Goods_str11:
            	case SAM_Goods_str10code:
            	case SAM_Goods_str10value:
            		line = ((SaleAppendDef)saleappend.get(index)).str11;
            		break;
            	case SAM_Goods_str12:
            	case SAM_Goods_str12code:
            	case SAM_Goods_str12value:
            		line = ((SaleAppendDef)saleappend.get(index)).str12;
            		break;
            	case SAM_Goods_str13:
            	case SAM_Goods_str13code:
            	case SAM_Goods_str13value:
            		line = ((SaleAppendDef)saleappend.get(index)).str13;
            		break;
            	case SAM_Goods_str14:
            	case SAM_Goods_str14code:
            	case SAM_Goods_str14value:
            		line = ((SaleAppendDef)saleappend.get(index)).str14;
            		break;
            	case SAM_Goods_str15:
            	case SAM_Goods_str15code:
            	case SAM_Goods_str15value:
            		line = ((SaleAppendDef)saleappend.get(index)).str15;
            		break;
            	case SAM_Goods_str16:
            	case SAM_Goods_str16code:
            	case SAM_Goods_str16value:
            		line = ((SaleAppendDef)saleappend.get(index)).str16;
            		break;
            	case SAM_Goods_str17:
            	case SAM_Goods_str17code:
            	case SAM_Goods_str17value:
            		line = ((SaleAppendDef)saleappend.get(index)).str17;
            		break;
            	case SAM_Goods_str18:
            	case SAM_Goods_str18code:
            	case SAM_Goods_str18value:
            		line = ((SaleAppendDef)saleappend.get(index)).str18;
            		break;
            	case SAM_Goods_str19:
            	case SAM_Goods_str19code:
            	case SAM_Goods_str19value:
            		line = ((SaleAppendDef)saleappend.get(index)).str19;
            		break;
            	case SAM_Goods_str20:
            	case SAM_Goods_str20code:
            	case SAM_Goods_str20value:
            		line = ((SaleAppendDef)saleappend.get(index)).str20;
            		break;
            }
        	
        	if (line != null)
        	{
        		int itemindex1 = Integer.parseInt(item.code);
        		
        		String[] lines = line.split("-");
        		
        		// 只打印code
        		if ((itemindex1 >= SAM_Head_str1code && itemindex1 >= SAM_Head_str20code) ||
        			(itemindex1 >= SAM_Goods_str1code && itemindex1 >= SAM_Goods_str20code)
        			)
        		{
        			line = "";
        			
        			if (lines.length > 0) line = lines[0];
        		}
        		// 只打印value
        		else if ((itemindex1 >= SAM_Head_str1value && itemindex1 >= SAM_Head_str20value) ||
            			(itemindex1 >= SAM_Goods_str1value && itemindex1 >= SAM_Goods_str20value)
            			)
        		{
        			if (lines.length > 1)
    				{
        				line = "";
        				
        				for (int i = 1;i < lines.length;i++)
        				{
        					line = line + lines[i] + "-";
        				}
        				
        				if (line.length() > 0) line = line + line.substring(0,line.length() - 1);
    				}
        		}
        	}
        	
        	return line;
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
         	return null;
        }
    }
	
	protected void printStart()
	{
		int n = Integer.parseInt(String.valueOf(GlobalInfo.sysPara.saprintyyytrack));
		switch(n)
		{
			case 1:
				// 向小票栈输出时应该按小票的输出栈执行
				super.printStart();
				break;
			case 2:
				Printer.getDefault().startPrint_Journal();
				break;
			case 3:
				Printer.getDefault().startPrint_Slip();
				break;
			default:
				Printer.getDefault().startPrint_Slip();
				break;
		}
	}
	
	protected void printLine(String s)
	{
		int n = Integer.parseInt(String.valueOf(GlobalInfo.sysPara.saprintyyytrack));
		switch(n)
		{
			case 1:
				// 向小票栈输出时应该按小票的输出栈执行
				super.printLine(s);
				break;
			case 2:
				Printer.getDefault().printLine_Journal(s);
				break;
			case 3:
				Printer.getDefault().printLine_Slip(s);
				break;
			default:
				Printer.getDefault().printLine_Slip(s);
				break;
		}
	}	
	
	protected void printArea(int startRow,int endRow)
	{
		int n = Integer.parseInt(String.valueOf(GlobalInfo.sysPara.saprintyyytrack));
		switch(n)
		{
			case 1:
				// 向小票栈输出时应该按小票的输出栈执行
				super.printArea(startRow,endRow);
				break;
			case 2:
				Printer.getDefault().setPrintArea_Journal(startRow,endRow);
				break;
			case 3:
				Printer.getDefault().setPrintArea_Slip(startRow,endRow);
				break;
			default:
				Printer.getDefault().setPrintArea_Slip(startRow,endRow);
				break;	
		}
	}
	
	public void printCutPaper()
	{
		int n = Integer.parseInt(String.valueOf(GlobalInfo.sysPara.saprintyyytrack));
		switch(n)
		{
			case 1:
				// 向小票栈输出时应该按小票的输出栈执行
				super.printCutPaper();
				break;
			case 2:
				Printer.getDefault().cutPaper_Journal();
				break;
			case 3:
				Printer.getDefault().cutPaper_Slip();
				break;
			default:
				Printer.getDefault().cutPaper_Slip();
				break;
		}
	}
	
	public void printSetPage()
	{
		int n = Integer.parseInt(String.valueOf(GlobalInfo.sysPara.saprintyyytrack));
		switch(n)
		{
			case 1:
				// 向小票栈输出时应该按小票的输出栈执行
				super.printSetPage();
				break;
			case 2:
				// 第二栈总是不分页的
				Printer.getDefault().setPagePrint_Journal(false,1);
				break;
			case 3:
				// 第三栈总是不分页的
				Printer.getDefault().setPagePrint_Slip(false,1);
				break;				
			default:
				// 第三栈总是不分页的
				Printer.getDefault().setPagePrint_Slip(false,1);
				break;
		}
	}
}
