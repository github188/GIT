package custom.localize.Jlsd;


import com.efuture.commonKit.Convert;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.SaleGoodsDef;

import custom.localize.Bhcm.Bhcm_SaleBillMode;



public class Jlsd_SaleBillMode extends Bhcm_SaleBillMode
{
	
	
	protected final static int SBM_ShoppingBag = 202;//购物袋


	protected String getItemDataString(PrintTemplateItem item, int index)
	{
		String line = null;

		line = extendCase(item, index);

		String text = item.text;

		if (line == null)
		{
			switch (Integer.parseInt(item.code))
			{
				
				case SBM_ShoppingBag: // 购物袋
					line = ((SaleGoodsDef) salegoods.elementAt(index)).str4;
					if(line != null){
					line = line.replace("D","大袋");
					line = line.replace("Z","中袋");
					line = line.replace("X","小袋");
					}else{
						line = "";
					}
					break;
					
				default:
					return super.getItemDataString(item, index);
			}
		}
		
		if ((line != null) && line.equals("&!"))
		{
			line = null;
		}

		// if (line != null && Integer.parseInt(item.code) != 0 && item.text !=
		// null && !item.text.trim().equals(""))
		if ((line != null) && (Integer.parseInt(item.code) != 0) && (text != null) && !text.trim().equals(""))
		{
			// line = item.text + line;
			int maxline = item.length - Convert.countLength(text);
			
			line = text + Convert.appendStringSize("", line, 0, maxline, maxline, item.alignment);
		}

		return line;
	}
	}
