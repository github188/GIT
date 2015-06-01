package com.efuture.javaPos.PrintTemplate;

import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ExpressionDeal;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class YyySaleBillMode extends SaleBillMode
{	
	public class GroupDef
	{
		//分组条件 1,2,3
		public String key1 = "";
		public String key2 = "";
		public String key3 = "";
		public String yyyh = "";
		public String gz = "";
		public Vector row_set = new Vector();
		public GroupSummaryDef gsd = new GroupSummaryDef();
	}
	
	public class GroupSummaryDef
	{
		public double hjje = 0;
		public double hjzk = 0;
		public double hjsl = 0;
	}

	protected final static int YSB_group_key1 = 102;
	
	protected final static int YSB_group_key2 = 103;
	
	protected final static int YSB_group_key3 = 104;
	
	protected final static int YSB_group_yyyh = 105;
	
	protected final static int YSB_group_gz = 106;
	
	protected final static int YSB_group_hjje = 107;

	protected final static int YSB_group_hjzk = 108;

	protected final static int YSB_group_hjsl = 109;
	
	protected final static int YSB_group_hjys = 110;
	
	protected final static int YSB_group_hjysdx = 111;
	
	protected Vector groupset =  new Vector(); 
	
	protected GroupDef curgroup = null;
	
	protected String message = "";
	
	private boolean isLoad =  false;
	
	protected static YyySaleBillMode yyySaleBillMode = null;

	public static SaleBillMode getDefault()
	{
		if (YyySaleBillMode.yyySaleBillMode == null)
		{
			YyySaleBillMode.yyySaleBillMode = CustomLocalize.getDefault().createYyySaleBillMode();
		}

		return YyySaleBillMode.yyySaleBillMode;
	}

	public boolean ReadTemplateFile()
    {
		if (!CommonMethod.isFileExist(GlobalVar.ConfigPath + "//YyySalePrintMode.ini")) return true;
			
        super.InitTemplate();
        
        isLoad = super.ReadTemplateFile(Title,GlobalVar.ConfigPath + "//YyySalePrintMode.ini");
        return isLoad;
    }

	public void setTemplateObject(SaleHeadDef h, Vector s, Vector p)
	{
		salehead = h;
		salegoods = new Vector();
		salepay = convertPayDetail(p);

		originalsalegoods = s;
		originalsalepay = p;
		
		group();
	}

	// 按营业员柜组分组
	public void groupByYyyGz()
	{
		//yyyh = group.key_set.get(0);gz = group.key_set.get(1); 
		
		groupset.clear();
		message = "";
		
		SaleGoodsDef sgd = null;
		GroupDef group = null;
		
		for (int i = 0; i < originalsalegoods.size(); i++)
		{
			sgd = (SaleGoodsDef) originalsalegoods.elementAt(i);
			
			// 查找是否相同商品,按营业员柜组分组
			int j = 0;
			for (j = 0; j < groupset.size(); j++)
			{
				group = (GroupDef) groupset.elementAt(j);

				if (group.key1.equals(sgd.yyyh) && group.key2.equals(sgd.gz))
				{
					group.row_set.add(String.valueOf(i));

					break;
				}
			}

			if (j >= groupset.size())
			{
				group = new GroupDef();
				group.key1 = sgd.yyyh;
				group.key2 = sgd.gz;
				group.yyyh = sgd.yyyh;
				group.gz = sgd.gz;
				group.row_set.add(String.valueOf(i));
				groupset.add(group);
			}
		}
		
//		message = "请将营业员([key1])的销售单放入打印机\n\n按‘回车’键后开始打印\n按‘退出’键则跳过打印";
		message = Language.apply("请将营业员([key1])的销售单放入打印机\n\n按‘回车’键后开始打印\n按‘退出’键则跳过打印");
	}
	
	// 按商品分组
	public void groupByGoods()
	{
		//goodscode = group.key_set.get(0)
		
		groupset.clear();
		message = "";
		
		SaleGoodsDef sgd = null;
		GroupDef group = null;
		
		for (int i = 0; i < originalsalegoods.size(); i++)
		{
			sgd = (SaleGoodsDef) originalsalegoods.elementAt(i);
			
			group = new GroupDef();
			group.key1 = sgd.code;
			group.yyyh = sgd.yyyh;
			group.gz = sgd.gz;
			group.row_set.add(String.valueOf(i));
			group.key2 = String.valueOf(sgd.jg);
			group.key3 = String.valueOf(sgd.sl);
			groupset.add(group);
		
		}
		
//		message = "请将商品([key1])的销售单放入打印机\n商品数量：[key3]\n商品单价：[key2]\n按‘回车’键后开始打印\n按‘退出’键则跳过打印";
		message = Language.apply("请将商品([key1])的销售单放入打印机\n商品数量：[key3]\n商品单价：[key2]\n按‘回车’键后开始打印\n按‘退出’键则跳过打印");
	}
	
	// 按柜组
	public void groupByGz()
	{
		//yyyh = group.key_set.get(0);gz = group.key_set.get(1); 
		
		groupset.clear();
		message = "";
		
		SaleGoodsDef sgd = null;
		GroupDef group = null;
		
		for (int i = 0; i < originalsalegoods.size(); i++)
		{
			sgd = (SaleGoodsDef) originalsalegoods.elementAt(i);
			
			// 查找是否相同商品,按营业员柜组分组
			int j = 0;
			for (j = 0; j < groupset.size(); j++)
			{
				group = (GroupDef) groupset.elementAt(j);

				if (group.key1.equals(sgd.gz))
				{
					group.row_set.add(String.valueOf(i));

					break;
				}
			}

			if (j >= groupset.size())
			{
				group = new GroupDef();
				group.key1 = sgd.gz;
				group.yyyh = sgd.yyyh;
				group.gz = sgd.gz;
				group.row_set.add(String.valueOf(i));
				groupset.add(group);
			}
		}
		
//		message = "请将柜组([key1])的销售单放入打印机\n\n按‘回车’键后开始打印\n按‘退出’键则跳过打印";
		message = Language.apply("请将柜组([key1])的销售单放入打印机\n\n按‘回车’键后开始打印\n按‘退出’键则跳过打印");
	}
	
	//	 按营业员
	public void groupByYyy()
	{
		//yyyh = group.key_set.get(0);gz = group.key_set.get(1); 
		
		groupset.clear();
		message = "";
		
		SaleGoodsDef sgd = null;
		GroupDef group = null;
		
		for (int i = 0; i < originalsalegoods.size(); i++)
		{
			sgd = (SaleGoodsDef) originalsalegoods.elementAt(i);
			
			// 查找是否相同商品,按营业员柜组分组
			int j = 0;
			for (j = 0; j < groupset.size(); j++)
			{
				group = (GroupDef) groupset.elementAt(j);

				if (group.key1.equals(sgd.yyyh))
				{
					group.row_set.add(String.valueOf(i));

					break;
				}
			}

			if (j >= groupset.size())
			{
				group = new GroupDef();
				group.key1 = sgd.yyyh;
				group.yyyh = sgd.yyyh;
				group.gz = sgd.gz;
				group.row_set.add(String.valueOf(i));
				groupset.add(group);
			}
		}
		
//		message = "请将营业员([key1])的销售单放入打印机\n\n按‘回车’键后开始打印\n按‘退出’键则跳过打印";
		message = Language.apply("请将营业员([key1])的销售单放入打印机\n\n按‘回车’键后开始打印\n按‘退出’键则跳过打印");
	}
	
	public void group()
	{
		 
		//	打印营业员联分组方式,1-营业员+柜组,2-单品,3-柜组,4-营业员
		if (GlobalInfo.sysPara.printyyygrouptype == '1')
		{
			this.groupByYyyGz();
		}
		else if (GlobalInfo.sysPara.printyyygrouptype == '2')
		{
			this.groupByGoods();
		}
		else if (GlobalInfo.sysPara.printyyygrouptype == '3')
		{
			this.groupByGz();
		}
		else if (GlobalInfo.sysPara.printyyygrouptype == '4')
		{
			this.groupByYyy();
		}
		else if (GlobalInfo.sysPara.printyyygrouptype == '5')
		{
			this.groupByFph();
		}
		
		groupsummary();
	}
	
	public void groupByFph()
	{
		//yyyh = group.key_set.get(0);gz = group.key_set.get(1); 
		groupset.clear();
		message = "";
		
		SaleGoodsDef sgd = null;
		GroupDef group = null;
		
		for (int i = 0; i < originalsalegoods.size(); i++)
		{
			sgd = (SaleGoodsDef) originalsalegoods.elementAt(i);
			
			int j = 0;
			for (j = 0; j < groupset.size(); j++)
			{
				group = (GroupDef) groupset.elementAt(j);

				if (group.key1 != null && group.key1.equals(sgd.fph))
				{
					group.row_set.add(String.valueOf(i));

					break;
				}
			}

			if (j >= groupset.size())
			{
				group = new GroupDef();
				group.key1 = sgd.fph;
				group.yyyh = sgd.yyyh;
				group.gz = sgd.gz;
				group.row_set.add(String.valueOf(i));
				groupset.add(group);
			}
		}
		
//		message = "请将发票号([key1])的销售单放入打印机\n\n按‘回车’键后开始打印\n按‘退出’键则跳过打印";
		message = Language.apply("请将发票号([key1])的销售单放入打印机\n\n按‘回车’键后开始打印\n按‘退出’键则跳过打印");
	}
	
	public void groupsummary()
	{		
		for (int i = 0;i < groupset.size();i++)
		{
			GroupDef group = (GroupDef) groupset.elementAt(i);
			
			GroupSummaryDef gsd = group.gsd;
			
			for (int j = 0;j < group.row_set.size();j++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef)originalsalegoods.elementAt(Integer.parseInt((String) group.row_set.elementAt(j)));
				
				gsd.hjje += sgd.hjje;
				gsd.hjzk += sgd.hjzk;
				gsd.hjsl += sgd.sl;
			}
		}
	}
	
	public void printBill()
	{
		// GlobalInfo.sysPara.fdprintyyy = (N-不打营业员联但打印小票联/Y-打印营业员联也打印小票/A-打印营业员联但不打印小票)
    	// 超市小票 或者 系统参数定义不打印分单,则不打印营业员小票
		if (
			(GlobalInfo.syjDef.issryyy == 'N') || 
		    (GlobalInfo.syjDef.issryyy == 'A' && ((SaleGoodsDef)originalsalegoods.elementAt(0)).yyyh.equals(Language.apply("超市"))) ||
		    (GlobalInfo.sysPara.fdprintyyy == 'N')
			)
    	{
    		return;
    	}
		
		// 按分组进行分单打印
		for (int i = 0;i < groupset.size();i ++)
		{
			// 设置当前分组信息
			curgroup = (GroupDef)groupset.elementAt(i);
			
			// 设置当前分组的salegoods
			salegoods.clear();
			for (int j = 0;j < curgroup.row_set.size();j++)
			{
				int index = Convert.toInt(curgroup.row_set.get(j));
				SaleGoodsDef sgd = (SaleGoodsDef)originalsalegoods.get(index);
				salegoods.add(sgd);
			}
			
			// 从第3栈打印才进行提示
			if (!message.equals("") && GlobalInfo.sysPara.fdprintyyytrack == '3')
			{
				String str = "";
				/*str = message.replace("[key1]", curgroup.key1);
				str = str.replace("[key2]", curgroup.key2);
				str = str.replace("[key3]", curgroup.key3);
				str = str.replace("[yyyh]", curgroup.yyyh);
				str = str.replace("[gz]", curgroup.gz);*/
				
				str = ExpressionDeal.replace(message,"[key1]",curgroup.key1);
				str = ExpressionDeal.replace(str,"[key2]", curgroup.key2);
				str = ExpressionDeal.replace(str,"[key3]", curgroup.key3);
				str = ExpressionDeal.replace(str,"[yyyh]", curgroup.yyyh);
				str = ExpressionDeal.replace(str,"[gz]", curgroup.gz);
				
				if (new MessageBox(str).verify() == GlobalVar.Exit)
				{
					continue;
				}
			}
			
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
	}
	
	public void setLoad(boolean isload)
	{
		this.isLoad = isload;
	}
	
	public boolean isLoad()
	{
		return isLoad;
	}

	protected String extendCase(PrintTemplateItem item, int index)
    {
        String line = null;
        
        try
        {        	
        	switch (Integer.parseInt(item.code))
            {
        		case YSB_group_key1:
        			line = this.curgroup.key1;
        			break;
        		case YSB_group_key2:
        			line = this.curgroup.key2;
        			break;
        		case YSB_group_key3:
        			line = this.curgroup.key3;
        			break;
        		case YSB_group_yyyh:
        			line = this.curgroup.yyyh;
        			break;
        		case YSB_group_gz:
        			line = this.curgroup.gz;
        			break;
            	case YSB_group_hjje:
            		line = ManipulatePrecision.doubleToString(this.curgroup.gsd.hjje);
                	break;
            	case YSB_group_hjzk:
            		line = ManipulatePrecision.doubleToString(this.curgroup.gsd.hjzk);
            		break;
            	case YSB_group_hjsl:
            		line = ManipulatePrecision.doubleToString(this.curgroup.gsd.hjsl);
            		break;
            	case YSB_group_hjys:
            		line = ManipulatePrecision.doubleToString(this.curgroup.gsd.hjje - this.curgroup.gsd.hjzk);
            		break;
            	case YSB_group_hjysdx://应收金额大写
            		line = ManipulatePrecision.getFloatConverChinese(this.curgroup.gsd.hjje - this.curgroup.gsd.hjzk);
            		break;
            }
        	
        	return line;
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
         	return null;
        }
    }
	
	public void printDetail()
	{
		// 设置打印区域
		setPrintArea("Detail");

		// 循环打印商品明细
		for (int i = 0; i < salegoods.size(); i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) salegoods.elementAt(i);

			// 赠品商品不打印
			if (sgd.flag == '1')
			{
				continue;
			}

			printVector(getCollectDataString(Detail, i, Width));
		}
	}

	public void printPay()
	{
		// 设置打印区域
		setPrintArea("Pay");

		// 循环打印付款明细
		for (int i = 0; i < salepay.size(); i++)
		{
			SalePayDef spd = (SalePayDef) salepay.elementAt(i);

			// 找零付款不打印
			if (spd.flag == '2')
			{
				continue;
			}

			printVector(getCollectDataString(Pay, i, Width));
		}
	}

	protected void printStart()
	{
		int n = Integer.parseInt(String.valueOf(GlobalInfo.sysPara.fdprintyyytrack));
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
		int n = Integer.parseInt(String.valueOf(GlobalInfo.sysPara.fdprintyyytrack));
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
		int n = Integer.parseInt(String.valueOf(GlobalInfo.sysPara.fdprintyyytrack));
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
		int n = Integer.parseInt(String.valueOf(GlobalInfo.sysPara.fdprintyyytrack));
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
		int n = Integer.parseInt(String.valueOf(GlobalInfo.sysPara.fdprintyyytrack));
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
