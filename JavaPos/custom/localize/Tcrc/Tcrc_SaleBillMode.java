package custom.localize.Tcrc;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bhls.Bhls_SaleBillMode;

public class Tcrc_SaleBillMode extends Bhls_SaleBillMode {
	protected final static int SBM_ValidDate = 202;
	
    public void printGift()
    {
        boolean first = false;
        for (int i = 0; i < salegoods.size(); i++)
        {
            SaleGoodsDef saleGoodsDef = (SaleGoodsDef) salegoods.elementAt(i);

            if (saleGoodsDef.flag == '1' || saleGoodsDef.flag == '5')
            {
                if (!first)
                {
                    printLine(Convert.appendStringSize("", "\n赠品栏", 0, Width, Width, 2));
                }

                first = true;
                printLine("商品编码: " + saleGoodsDef.code);
                printLine("商品柜组: " + saleGoodsDef.gz);
                printLine("促销单号: " + saleGoodsDef.zsdjbh);
                String line = Convert.appendStringSize("", "赠送价值:", 0, 9, Width);
                line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(saleGoodsDef.lsj*SellType.SELLSIGN(salehead.djlb)), 10, 10, Width);
                line = Convert.appendStringSize(line, "赠送数量:", 21, 9, Width);
                line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(saleGoodsDef.sl*SellType.SELLSIGN(salehead.djlb), 2, 1), 30, 8, Width);
                printLine(line);
                
                printLine("\n");
            }
        }
        
        if (first)
        {
        	printLine("购物金额："+ManipulatePrecision.doubleToString(salehead.ysje*SellType.SELLSIGN(salehead.djlb)));
        }
        
    }
    
    protected String extendCase(PrintTemplateItem item, int index)
    {
    	String line = null;
		switch (Integer.parseInt(item.code))
		{
		case SBM_payno: // 付款方式帐号
				
				line = ((SalePayDef) salepay.elementAt(index)).payno;
				
				if (DataService.getDefault().searchPayMode(((SalePayDef) salepay.elementAt(index)).paycode).type == '3')
				{
					if (line.length() > 10)
					{
						String head = line.substring(0,6);
						String bottom = line.substring(line.length() - 4);
						line = head +"*******"+bottom;
					}
				}

				if ((line == null) || (line.length() <= 0))
				{
					line = null;
				}

				break;
    	case SBM_Aqje: // A券金额

			if ((salehead.memo != null) && (salehead.memo.split(",").length > 1))
			{
				String[] row = salehead.memo.split(",");
				double aje = Convert.toDouble(row[0]);

				if (aje > 0)
				{
					line = ManipulatePrecision.doubleToString(aje);
					
				}
			}

			break;

		case SBM_Bqje: // B券金额

			if ((salehead.memo != null) && (salehead.memo.split(",").length > 1))
			{
				String[] row = salehead.memo.split(",");
				double bje = Convert.toDouble(row[1]);

				if (bje > 0)
				{
					line = ManipulatePrecision.doubleToString(bje);
					
				}
			}

			break;
		case SBM_ValidDate: // AB 券的有效期
			if ((salehead.memo != null) && (salehead.memo.split(",").length > 2))
			{
				String[] row = salehead.memo.split(",");
				if (row[2].trim().length() > 0)
				{
					line = row[2];
				}
				else
				{
					line = "&!";
				}
			}
		}
		
		return line;
    }
    
    public void printSetPage()
	{
		if (printstrack != -1)
		{
			super.printSetPage();
		}
		else
		{
			if ((SellType.ISBACK(salehead.djlb) || SellType.ISHC(salehead.djlb)) && (GlobalInfo.sysPara.printInBill != 'Y'))
			{
				super.printSetPage();
			}
			else
			{
				// 设置是否分页打印
				if (PagePrint != 1)
				{
					Printer.getDefault().setPagePrint_Journal(false, 1);
				}
				else
				{
					Printer.getDefault().setPagePrint_Journal(true, Area_PageFeet);
				}
			}
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
			if ((SellType.ISBACK(salehead.djlb) || SellType.ISHC(salehead.djlb)) && (GlobalInfo.sysPara.printInBill != 'Y'))
			{
				super.printCutPaper();
			}
			else
			{
				Printer.getDefault().cutPaper_Journal();
			}
		}
	}
    
    protected void printArea(int startRow, int endRow)
	{
		if (printstrack != -1)
		{
			super.printArea(startRow, endRow);
		}
		else
		{
			if ((SellType.ISBACK(salehead.djlb) || SellType.ISHC(salehead.djlb)) && (GlobalInfo.sysPara.printInBill != 'Y'))
			{
				super.printArea(startRow, endRow);
			}
			else
			{
				Printer.getDefault().setPrintArea_Journal(startRow, endRow);
			}
		}
	}
    protected void printLine(String s)
	{
		if (printstrack != -1)
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
			{
				Printer.getDefault().printLine_Journal(s);
			}
		}
	}
    public boolean checkIsPrint(String template)
	{
    	if(salehead.printnum > 0 && template.equals("SalePrintMode_fp.ini"))
			return false;
		return true;
	}
    
    public boolean checkPrintNum(String template)
	{
    	if(template.equals("SalePrintMode.ini")){
    		return true;
    	}
		return false;
	}
    
}
