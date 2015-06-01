package custom.localize.Xdmg;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.SaleGoodsDef;

import custom.localize.Cmls.Cmls_SaleBillMode;

public class Xdmg_SaleBillMode extends Cmls_SaleBillMode
{
	protected final static int SBM_HtCode = 301;
	protected String extendCase(PrintTemplateItem item, int index) 
	{
		String line = null;
		switch (Integer.parseInt(item.code))
		{
			case SBM_BxJf:
                if (salehead.num4 == 0)
                {
                    line = "&!";
                }
                else
                {
                    line = ManipulatePrecision.doubleToString(salehead.num4);
                }
				break;
				
			case SBM_HtCode:
				String ContracCode = ((SaleGoodsDef) salegoods.elementAt(0)).str6;
				if(ContracCode == null ||ContracCode.trim().equals("")) line = "";
				else line = ContracCode;
				break;
				
		}
		return line;
	}
}
