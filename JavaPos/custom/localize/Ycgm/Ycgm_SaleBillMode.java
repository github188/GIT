package custom.localize.Ycgm;

import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;

import custom.localize.Cmls.Cmls_SaleBillMode;

public class Ycgm_SaleBillMode extends Cmls_SaleBillMode
{

	protected final static int SBM_GmJd = 202;
	
	protected String extendCase(PrintTemplateItem item, int index) 
	{
		String line = null;
		switch (Integer.parseInt(item.code))
		{
			case SBM_GmJd:
				
				line = salehead.str8;//((SalePayDef) salepay.elementAt(index)).kye +"";
				break;
		}
		return line;
	}
}
